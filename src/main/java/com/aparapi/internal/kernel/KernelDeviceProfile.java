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

import com.aparapi.*;
import com.aparapi.device.*;

import java.text.*;
import java.util.*;
import java.util.logging.*;

/**
 * Created by Barney on 02/09/2015.
 */
public class KernelDeviceProfile {

   private static Logger logger = Logger.getLogger(Config.getLoggerName());
   private static final double MILLION = 1000 * 1000;
   private static final int TABLE_COLUMN_HEADER_WIDTH = 21;
   private static final int TABLE_COLUMN_COUNT_WIDTH = 8;
   private static final int TABLE_COLUMN_WIDTH;
   private static String tableHeader = null;
   private final Class<? extends Kernel> kernel;
   private final Device device;
   private long[] currentTimes = new long[ProfilingEvent.values().length];
   private long[] accumulatedTimes = new long[ProfilingEvent.values().length];
   private ProfilingEvent lastEvent = null;
   private final DecimalFormat format;
   private long invocationCount = 0;

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
      this.format = (DecimalFormat) DecimalFormat.getNumberInstance();
      format.setMinimumFractionDigits(3);
      format.setMaximumFractionDigits(3);
   }

   public void onEvent(ProfilingEvent event) {
      if (event == ProfilingEvent.START) {
         if (lastEvent != null) {
            logger.log(Level.SEVERE, "ProfilingEvent.START encountered without ProfilingEvent.EXECUTED");
         } else if (lastEvent == ProfilingEvent.START) {
            logger.log(Level.SEVERE, "Duplicate event ProfilingEvent.START");
         }
         Arrays.fill(currentTimes, 0L);
         ++invocationCount;
      } else {
         if (lastEvent == null) {
            if (event != ProfilingEvent.EXECUTED) {
               logger.log(Level.SEVERE, "ProfilingEvent.START was not invoked prior to ProfilingEvent." + event);
            }
         } else {
            for (int i = lastEvent.ordinal() + 1; i < event.ordinal(); ++i) {
               currentTimes[i] = currentTimes[i - 1];
            }
         }
      }
      currentTimes[event.ordinal()] = System.nanoTime();
      if (event == ProfilingEvent.EXECUTED) {
         for (int i = 1; i < currentTimes.length; ++i) {
            long elapsed = currentTimes[i] - currentTimes[i - 1];
            if (elapsed < 0) {
               logger.log(Level.SEVERE, "negative elapsed time for event " + event);
               break;
            }
            accumulatedTimes[i] += elapsed;
         }
      }
      lastEvent = event;
      if (event == ProfilingEvent.EXECUTED) {
         lastEvent = null;
      }
   }

   /** Elapsed time for a single event only, i.e. since the previous stage rather than from the start. */
   public double getLastElapsedTime(ProfilingEvent stage) {
      if (stage == ProfilingEvent.START) {
         return 0;
      }
      return (currentTimes[stage.ordinal()] - currentTimes[stage.ordinal() - 1]) / MILLION;
   }

   /** Elapsed time for all events {@code from} through {@code to}.*/
   public double getLastElapsedTime(ProfilingEvent from, ProfilingEvent to) {
      return (currentTimes[to.ordinal()] - currentTimes[from.ordinal()]) / MILLION;
   }

   /** Elapsed time for a single event only, i.e. since the previous stage rather than from the start, summed over all executions. */
   public double getCumulativeElapsedTime(ProfilingEvent stage) {
      return (accumulatedTimes[stage.ordinal()]) / MILLION;
   }

   /** Elapsed time of entire execution, summed over all executions. */
   public double getCumulativeElapsedTimeAll() {
      double sum = 0;
      for (int i = 1; i <= ProfilingEvent.EXECUTED.ordinal(); ++i) {
         sum += accumulatedTimes[i];
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
      double total = 0;
      StringBuilder builder = new StringBuilder(150);
      appendRowHeaders(builder, device.getShortDescription(), String.valueOf(invocationCount));
      for (int i = 1; i < currentTimes.length; ++i) {
         ProfilingEvent stage = ProfilingEvent.values()[i];
         double time = getLastElapsedTime(stage);
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
      double total = 0;
      double count = mean ? invocationCount : 1;
      StringBuilder builder = new StringBuilder(150);
      appendRowHeaders(builder, device.getShortDescription(), String.valueOf(invocationCount));
      for (int i = 1; i < currentTimes.length; ++i) {
         ProfilingEvent stage = ProfilingEvent.values()[i];
         double time = getCumulativeElapsedTime(stage);
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
      return "KernelDeviceProfile{" + kernel.toString() + ", " + device.getShortDescription() + "}";
   }
}
