/**
 * Copyright (c) 2016 - 2018 Syncleus, Inc.
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

import java.lang.ref.WeakReference;
import java.text.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.*;

/**
 * Created by Barney on 02/09/2015.
 */
public class KernelDeviceProfile {

   private static Logger logger = Logger.getLogger(Config.getLoggerName());
   private static final int NUM_EVENTS = ProfilingEvent.values().length;
   private static final double MILLION = 1000 * 1000;
   private static final int TABLE_COLUMN_HEADER_WIDTH = 21;
   private static final int TABLE_COLUMN_COUNT_WIDTH = 8;
   private static final int TABLE_COLUMN_WIDTH;
   private static String tableHeader = null;
   
   private final KernelProfile parentKernelProfile;
   private final Class<? extends Kernel> kernel;
   private final Device device;
   private final DecimalFormat format;
   private final AtomicLong invocationCountGlobal = new AtomicLong(0);
   private final AtomicReference<Accumulator> lastAccumulator = new AtomicReference<Accumulator>(null);

   private final GlobalAccumulator globalAcc = new GlobalAccumulator();

   private final Map<Thread,Accumulator> accs = Collections.synchronizedMap(
           new WeakHashMap<Thread,Accumulator>(Runtime.getRuntime().availableProcessors()*2, 0.95f)
   );
   
   static {
      assert ProfilingEvent.START.ordinal() == 0 : "ProfilingEvent.START.ordinal() != 0";
      int max = 0;
      for (ProfilingEvent event : ProfilingEvent.values()) {
         max = Math.max(max, event.name().length());
      }
      TABLE_COLUMN_WIDTH = max + 1;
   }

   private class GlobalAccumulator {
	   private final AtomicLongArray accumulatedTimes = new AtomicLongArray(NUM_EVENTS);
	   private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	   
	   private void accumulateTimes(final long[] currentTimes) {
		   //Read lock is only exclusive to write lock, thus many threads can update
		   //the accumulated times simultaneously.
		   lock.readLock().lock();
		   try {
			   for (int i = 1; i < currentTimes.length; ++i) {
				   long elapsed = currentTimes[i] - currentTimes[i - 1];
	
				   accumulatedTimes.addAndGet(i, elapsed);
			   }
		   } finally {
			   lock.readLock().unlock();
		   }
	   }
	   
	   private void consultAccumulatedTimes(final long[] accumulatedTimesHolder) {
		  //Write lock is exclusive to all other locks, so only one thread can retrieve
		  //the accumulated times at a given moment.
		  lock.writeLock().lock();
		  try {
			  for (int i = 0; i < NUM_EVENTS; i++) {
				  accumulatedTimesHolder[i] = accumulatedTimes.get(i);
			  }	  
		  } finally {
			  lock.writeLock().unlock();
		  }	  
	   }
   }
   
   private class Accumulator {
	   private final long threadId;
	   private final long[] currentTimes = new long[NUM_EVENTS];
	   private final long[] accumulatedTimes = new long[NUM_EVENTS];
	   private final ProfileReport report;
	   private final WeakReference<ProfileReport> reportRef; 
	   private ProfilingEvent lastEvent = null;
	   private int invocationCount = 0;	   

	   private Accumulator(long _threadId) {
		   threadId = _threadId;
		   report = new ProfileReport(threadId, kernel, device);
		   reportRef = new WeakReference<>(report);
	   }
	   
	   private void parseStartEventHelper(final ProfilingEvent event) {
	      if (event == ProfilingEvent.START) {		 
	          if (lastEvent != null) {
	             logger.log(Level.SEVERE, "ProfilingEvent.START encountered without ProfilingEvent.EXECUTED");
	          } else if (lastEvent == ProfilingEvent.START) {
	             logger.log(Level.SEVERE, "Duplicate event ProfilingEvent.START");
	          }
	          Arrays.fill(currentTimes, 0L);
	          ++invocationCount;
	          invocationCountGlobal.incrementAndGet();
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
	          
	          globalAcc.accumulateTimes(currentTimes);
	          lastAccumulator.set(this);
	       }
	   }
	   
	   private void onEvent(final ProfilingEvent event) {
		  parseStartEventHelper(event);
			
	      lastEvent = event;
	      if (event == ProfilingEvent.EXECUTED) {
	    	 updateProfileReport(report, invocationCount, currentTimes);
	    	 IProfileReportObserver observer = parentKernelProfile.getReportObserver();
	         lastEvent = null;
	    	 if (observer != null) {
	    		 observer.receiveReport(kernel, device, reportRef); 
	    	 }
	      }		   
	   }
   }

   private Accumulator getAccForThreadPutIfAbsent() {
       Thread t = Thread.currentThread();
       Accumulator a = accs.get(t);
       if (a == null) {
    	   a = new Accumulator(t.getId());
    	   accs.put(t, a);
       }
       return a;
   }
   
   private Accumulator getAccForThread() {
	   Thread t = Thread.currentThread();
	   return accs.get(t);
   }
      
   public KernelDeviceProfile(KernelProfile parentProfile, Class<? extends Kernel> kernel, Device device) {
	  this.parentKernelProfile = parentProfile;
      this.kernel = kernel;
      this.device = device;
      this.format = (DecimalFormat) DecimalFormat.getNumberInstance();
      format.setMinimumFractionDigits(3);
      format.setMaximumFractionDigits(3);
   }

   public void onEvent(ProfilingEvent event) {
	   getAccForThreadPutIfAbsent().onEvent(event);
   }

   private ProfileReport updateProfileReport(final ProfileReport report, long invocationCount, long[] currentTimes) {
	   report.setProfileReport(invocationCount, currentTimes);
	   
	   return report;
   }

   /** 
    * Elapsed time for a single event only and for the current thread, i.e. since the previous stage rather than from the start.
    * 
    *  
    */
   public double getElapsedTimeCurrentThread(int stage) {
	   if (stage == ProfilingEvent.START.ordinal()) {
           return 0;    
	   }
	   
	   Accumulator acc = getAccForThread();

	   return acc == null ? Double.NaN : (acc.currentTimes[stage] - acc.currentTimes[stage - 1]) / MILLION;
   }
   
   /** Elapsed time for all events {@code from} through {@code to} for the current thread.*/
   public double getElapsedTimeCurrentThread(int from, int to) {
	   Accumulator acc = getAccForThread();

	   return acc == null ? Double.NaN : (acc.currentTimes[to] - acc.currentTimes[from]) / MILLION;
   }
   
   /**
    * Retrieves the most recent complete report available for the current thread calling this method.<br/>
    * <b>Note1: <b>If the profile report is intended to be kept in memory, the object should be cloned with
    * {@link com.aparapi.ProfileReport#clone()}<br/>
    * <b>Note2: <b/>If the thread didn't execute this KernelDeviceProfile instance respective kernel and device, it
    * will return null.
    * @return <ul><li>the profiling report for the current most recent execution</li>
    *             <li>null, if no profiling report is available for such thread</li></ul>
    */
   public WeakReference<ProfileReport> getReportCurrentThread() {
	   Accumulator acc = getAccForThread();

	   return acc == null ? null : acc.reportRef;
   }

   /**
    * Retrieves the most recent complete report available for the last thread that executed this KernelDeviceProfile
    * instance respective kernel and device.<br/>
    * <b>Note1: <b>If the profile report is intended to be kept in memory, the object should be cloned with
    * {@link com.aparapi.ProfileReport#clone()}<br/>
    * 
    * @return <ul><li>the profiling report for the current most recent execution</li>
    *             <li>null, if no profiling report is available yet</li></ul>
    */
   public WeakReference<ProfileReport> getReportLastThread() {
	   Accumulator acc = lastAccumulator.get();

	   return acc == null ? null : acc.reportRef;
   }
   
   /** 
    * Elapsed time for a single event only, i.e. since the previous stage rather than from the start, summed over all executions, 
    * for the current thread, if it has executed the kernel on the device assigned to this KernelDeviceProfile instance. 
    *
    * @param stage the event stage
    */
   public double getCumulativeElapsedTimeCurrrentThread(ProfilingEvent stage) {
	   Accumulator acc = getAccForThread();

	   return acc == null ? Double.NaN : acc.accumulatedTimes[stage.ordinal()] / MILLION;
   }
   
   /**
    *  Elapsed time of entire execution, summed over all executions, for the current thread,
    *  if it has executed the kernel on the device assigned to this KernelDeviceProfile instance.
    */
   public double getCumulativeElapsedTimeAllCurrentThread() {
	  double sum = 0;
	  
	  Accumulator acc = getAccForThread();
	  if (acc == null) {
		  return sum;
	  }

      for (int i = 1; i <= ProfilingEvent.EXECUTED.ordinal(); ++i) {
         sum += acc.accumulatedTimes[i];
      }
      
      return sum;
   }

   /** 
    *  Elapsed time for a single event only and for the last thread that finished executing a kernel,
    *  i.e. single event only - since the previous stage rather than from the start. 
    *  @param stage the event stage
    */
   public double getElapsedTimeLastThread(int stage) {
	   if (stage == ProfilingEvent.START.ordinal()) {
           return 0;    
	   }
	   
	   Accumulator acc = lastAccumulator.get();

	   return acc == null ? Double.NaN : (acc.currentTimes[stage] - acc.currentTimes[stage - 1]) / MILLION;
   }
   
   /** 
    * Elapsed time for all events {@code from} through {@code to} for the last thread that executed this KernelDeviceProfile
    * instance respective kernel and device.
    * 
    * @param from the first event to consider that defines the elapsed period start
    * @param to the last event to consider for elapsed period
    */
   public double getElapsedTimeLastThread(int from, int to) {
	   Accumulator acc = lastAccumulator.get();
	   
	   return acc == null ? Double.NaN : (acc.currentTimes[to] - acc.currentTimes[from]) / MILLION;
   }
      
   /** 
    * Elapsed time for a single event only, i.e. since the previous stage rather than from the start, summed over all executions,
    * for the last thread that executed this KernelDeviceProfile instance respective kernel and device.
    * 
    * @param stage the event stage
    */
   public double getCumulativeElapsedTimeGlobal(ProfilingEvent stage) {
	   final long[] accumulatedTimesHolder = new long[NUM_EVENTS];
	   globalAcc.consultAccumulatedTimes(accumulatedTimesHolder);

	   return accumulatedTimesHolder[stage.ordinal()] / MILLION;
   }
   
   /** 
    * Elapsed time of entire execution, summed over all executions, for all the threads,
    * that executed the kernel on this device.
    */
   public double getCumulativeElapsedTimeAllGlobal() {
	  final long[] accumulatedTimesHolder = new long[NUM_EVENTS];
	  globalAcc.consultAccumulatedTimes(accumulatedTimesHolder);

      double sum = 0;
      for (int i = 1; i <= ProfilingEvent.EXECUTED.ordinal(); ++i) {
         sum += accumulatedTimesHolder[i];
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
	  //At the end of execution profile data may no longer be available due to the weak references,
	  //thus it is best to use the last report
	  StringBuilder builder = new StringBuilder(150);
	  Accumulator acc = lastAccumulator.get();
	  if (acc == null) {
		  appendRowHeaders(builder, device.getShortDescription(), String.valueOf(invocationCountGlobal.get()));
		  builder.append("No thread available");
		  return builder.toString();
	  }
	 
      double total = 0;
      appendRowHeaders(builder, device.getShortDescription(), String.valueOf(invocationCountGlobal.get()));
      for (int i = 1; i < NUM_EVENTS; ++i) {
         ProfilingEvent stage = ProfilingEvent.values()[i];
         double time = getElapsedTimeLastThread(stage.ordinal());
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
      double count = mean ? invocationCountGlobal.get() : 1;
      StringBuilder builder = new StringBuilder(150);
      appendRowHeaders(builder, device.getShortDescription(), String.valueOf(invocationCountGlobal.get()));
      for (int i = 1; i < NUM_EVENTS; ++i) {
         ProfilingEvent stage = ProfilingEvent.values()[i];
         double time = getCumulativeElapsedTimeGlobal(stage);
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
