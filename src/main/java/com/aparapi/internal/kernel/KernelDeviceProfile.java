/**
 * Copyright (c) 2016 - 2017 Syncleus, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aparapi.internal.kernel;

import com.aparapi.Config;
import com.aparapi.Kernel;
import com.aparapi.device.Device;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Barney on 02/09/2015.
 */
public class KernelDeviceProfile {

    private static final Logger logger = Logger.getLogger(Config.getLoggerName());
    private static final double MILLION = 1000 * 1000;
    private static final int TABLE_COLUMN_HEADER_WIDTH = 21;
    private static final int TABLE_COLUMN_COUNT_WIDTH = 8;
    private static final int TABLE_COLUMN_WIDTH;
    private static String tableHeader = null;

    private final Class<? extends Kernel> kernel;
    private final Device device;
    private final DecimalFormat format;

    private final static int NUM_EVENTS = ProfilingEvent.values().length;

    private class Accumulator {
        private final long[] currentTimes = new long[NUM_EVENTS];
        private final AtomicLongArray accumulatedTimes = new AtomicLongArray(NUM_EVENTS);
        private ProfilingEvent lastEvent = null;

        void onEvent(ProfilingEvent event) {

            //System.out.println(kernel + " " + Thread.currentThread() + " " + event);

            int eo = event.ordinal();
            long[] cur = this.currentTimes;
            if (event == ProfilingEvent.START) {

                if (lastEvent != null) {
                    logger.log(Level.SEVERE, "ProfilingEvent.START encountered without ProfilingEvent.EXECUTED, instead: " + lastEvent);
                }
                if (lastEvent == ProfilingEvent.START) {
                    logger.log(Level.SEVERE, "Duplicate event ProfilingEvent.START");
                }

                Arrays.fill(cur, 0L);

                invocationCount.incrementAndGet();

            } else {
                if (lastEvent != null) {
                    for (int i = lastEvent.ordinal() + 1; i < eo; ++i) {
                        cur[i] = cur[i - 1];
                    }
                } else {
                    if (event != ProfilingEvent.EXECUTED) {
                        logger.log(Level.SEVERE, "ProfilingEvent.START was not invoked prior to ProfilingEvent." + event);
                    }
                }
            }

            cur[eo] = System.nanoTime();

            if (event == ProfilingEvent.EXECUTED) {
                AtomicLongArray accumulatedTimes = this.accumulatedTimes;
                for (int i = 1; i < cur.length; ++i) {
                    long elapsed = cur[i] - cur[i - 1];
                    if (elapsed < 0) {
                        logger.log(Level.SEVERE, "negative elapsed time for event " + event);
                        break;
                    }
                    accumulatedTimes.addAndGet(i, elapsed);
                }
            }
            lastEvent = event;
            if (event == ProfilingEvent.EXECUTED) {
                lastEvent = null;
            }
        }
    }

    private final Accumulator combined = new Accumulator();

    private final Map<Thread,Accumulator> acc = Collections.synchronizedMap(
            new WeakHashMap<Thread,Accumulator>(Runtime.getRuntime().availableProcessors()*2, 0.95f)
    );

    private final AtomicLong invocationCount = new AtomicLong();

    static {
        assert ProfilingEvent.START.ordinal() == 0 : "ProfilingEvent.START.ordinal() != 0";
        int max = 0;
        for (ProfilingEvent event : ProfilingEvent.values()) {
            max = Math.max(max, event.name().length());
        }
        TABLE_COLUMN_WIDTH = max + 1;
    }

    public KernelDeviceProfile(Class<? extends Kernel> kernel, Device device) {
        this.kernel = kernel;
        this.device = device;
        this.format = (DecimalFormat) NumberFormat.getNumberInstance();
        format.setMinimumFractionDigits(3);
        format.setMaximumFractionDigits(3);
    }

    private Accumulator acc() {
        Thread t = Thread.currentThread();
        Accumulator a = acc.computeIfAbsent(t, k -> new Accumulator());
        return a;
    }

    private void commit() {
        synchronized (combined) {
            long[] c = combined.currentTimes;
            AtomicLongArray a = combined.accumulatedTimes;
            for (Accumulator x : this.acc.values()) {
                for (int i = 0; i < NUM_EVENTS; i++) {
                    long[] xc = x.currentTimes;
                    AtomicLongArray xa = x.accumulatedTimes;
                    c[i] = Math.max(c[i], xc[i]);
                    a.addAndGet( i, xa.getAndSet(i, 0)  );
                }
            }
        }
    }

    public void on(ProfilingEvent event) {
        acc().onEvent(event);
    }

    /** Elapsed time for a single event only, i.e. since the previous stage rather than from the start. */
    private double getLastElapsedTime(int stageOrdinal) {
        if (stageOrdinal == ProfilingEvent.START.ordinal()) {
            return 0;
        }
        long[] cc = combined.currentTimes;
        return (cc[stageOrdinal] - cc[stageOrdinal - 1]) / MILLION;
    }

    /** Elapsed time for all events {@code from} through {@code to}.*/
    public double getLastElapsedTime(ProfilingEvent from, ProfilingEvent to) {
        commit();
        return (combined.currentTimes[to.ordinal()] - combined.currentTimes[from.ordinal()]) / MILLION;
    }

    /** Elapsed time for a single event only, i.e. since the previous stage rather than from the start, summed over all executions. */
    private double getCumulativeElapsedTime(int stageOrdinal) {
        commit();
        return (combined.accumulatedTimes.get(stageOrdinal)) / MILLION;
    }

    /** Elapsed time of entire execution, summed over all executions. */
    public double getCumulativeElapsedTimeAll() {
        double sum = 0;
        commit();
        for (int i = 1; i <= ProfilingEvent.EXECUTED.ordinal(); ++i) {
            sum += combined.accumulatedTimes.get(i);
        }
        return sum;
    }

    public static synchronized String getTableHeader() {
        if (tableHeader == null) {
            int length = ProfilingEvent.values().length;
            StringBuilder builder = new StringBuilder(150);
            appendRowHeaders(builder, "Device", "Count");
            for (int i = 1; i < length; ++i) {
                ProfilingEvent stage = ProfilingEvent.values()[i];
                String heading = stage.name();
                appendCell(builder, heading);
            }
            builder.append("  ").append("Total");
            tableHeader = builder.toString();
        }
        return tableHeader;
    }

    public String getLastAsTableRow() {


        commit();

        StringBuilder builder = new StringBuilder(150);
        appendRowHeaders(builder, device.getShortDescription(), String.valueOf(invocationCount.get()));
        double total = 0;
        for (int i = 1; i < combined.currentTimes.length; ++i) {
            double time = getLastElapsedTime(i);
            total += time;
            String formatted = format.format(time);
            appendCell(builder, formatted);
        }
        builder.append("  ").append(format.format(total));
        return builder.toString();
    }

    public String getCumulativeAsTableRow() {
        return internalCumulativeAsTableRow(false);
    }

    public String getAverageAsTableRow() {
        return internalCumulativeAsTableRow(true);
    }

    private String internalCumulativeAsTableRow(boolean mean) {
        commit();

        double total = 0;
        double count = mean ? invocationCount.doubleValue() : 1;
        StringBuilder builder = new StringBuilder(150);
        appendRowHeaders(builder, device.getShortDescription(), String.valueOf(invocationCount.get()));
        for (int i = 1; i < combined.currentTimes.length; ++i) {
            double time = getCumulativeElapsedTime(i);
            if (mean) {
                time /= count;
            }
            total += time;
            String formatted = format.format(time);
            appendCell(builder, formatted);
        }
        builder.append("  ").append(format.format(total));
        return builder.toString();
    }

    private static void appendRowHeaders(StringBuilder builder, String device, String count) {
        if (device.length() > TABLE_COLUMN_HEADER_WIDTH - 1) {
            device = device.substring(0, TABLE_COLUMN_HEADER_WIDTH - 1);
        }
        builder.append(device);
        int padding = TABLE_COLUMN_HEADER_WIDTH - device.length();
        for (int i = 0; i < padding; ++i) {
            builder.append(' ');
        }

        builder.append(count);
        padding = TABLE_COLUMN_COUNT_WIDTH - count.length();
        for (int i = 0; i < padding; ++i) {
            builder.append(' ');
        }
    }

    private static void appendCell(StringBuilder builder, String cell) {
        int padding = TABLE_COLUMN_WIDTH - cell.length();
        for (int paddingIndex = 0; paddingIndex < padding; ++paddingIndex) {
            builder.append(' ');
        }
        builder.append(cell);
    }

    @Override
    public String toString() {
        return "KernelDeviceProfile{" + kernel.toString() + ", " + device.getShortDescription() + '}';
    }
}
