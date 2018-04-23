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

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.*;

/**
 * Collects profiling information per kernel class per device.
 */
public class KernelProfile {

   public static final double MILLION = 1000000d;
   private static Logger logger = Logger.getLogger(Config.getLoggerName());
   private final Class<? extends Kernel> kernelClass;
   private ConcurrentSkipListMap<Device, KernelDeviceProfile> deviceProfiles = new ConcurrentSkipListMap<>();
   private final AtomicReference<Device> currentDevice = new AtomicReference<Device>(null);
   private IProfileReportObserver observer;
   
   public KernelProfile(Class<? extends Kernel> _kernelClass) {
      kernelClass = _kernelClass;
   }

   public double getLastExecutionTime() {
      KernelDeviceProfile lastDeviceProfile = getLastDeviceProfile();
      return lastDeviceProfile == null ? Double.NaN : lastDeviceProfile.getElapsedTimeLastThread(ProfilingEvent.START.ordinal(), ProfilingEvent.EXECUTED.ordinal());
   }

   public double getLastConversionTime() {
      KernelDeviceProfile lastDeviceProfile = getLastDeviceProfile();
      return lastDeviceProfile == null ? Double.NaN : lastDeviceProfile.getElapsedTimeLastThread(ProfilingEvent.START.ordinal(), ProfilingEvent.PREPARE_EXECUTE.ordinal());
   }

   public double getAccumulatedTotalTime() {
      KernelDeviceProfile lastDeviceProfile = getLastDeviceProfile();
      if (lastDeviceProfile == null) {
         return Double.NaN;
      }
      else {
         return lastDeviceProfile.getCumulativeElapsedTimeAllGlobal() / MILLION;
      }
   }

   /**
    * Retrieves the last device profile that was updated by the last thread that made 
    * a profiling information update, when executing this kernel on the specified device.
    * @return the device profile 
    */
   public KernelDeviceProfile getLastDeviceProfile() {
      return deviceProfiles.get(currentDevice.get());
   }

   /**
    * Starts a profiling information gathering sequence for the current thread invoking this method
    * regarding the specified execution device.
    * @param device
    */
   void onStart(Device device) {
	  KernelDeviceProfile currentDeviceProfile = deviceProfiles.get(device);
      if (currentDeviceProfile == null) {    	 
         currentDeviceProfile = new KernelDeviceProfile(this, kernelClass, device);
         KernelDeviceProfile existingProfile = deviceProfiles.putIfAbsent(device, currentDeviceProfile);
         if (existingProfile != null) {
        	 currentDeviceProfile = existingProfile;
         }
      }
      
      currentDeviceProfile.onEvent(ProfilingEvent.START);
      currentDevice.set(device);
   }

   /**
    * Updates the profiling information for the current thread invoking this method regarding
    * the specified execution device.
    * 
    * @param device the device where the kernel is/was executed
    * @param event the event for which the profiling information is being updated
    */
   void onEvent(Device device, ProfilingEvent event) {
	  if (event == null) {
		  logger.log(Level.WARNING, "Discarding profiling event " + event + " for null device, for Kernel class: " + kernelClass.getName());
		  return;
	  }
	  final KernelDeviceProfile deviceProfile = deviceProfiles.get(device);
      switch (event) {
         case CLASS_MODEL_BUILT: // fallthrough
         case OPENCL_GENERATED:  // fallthrough
         case INIT_JNI:          // fallthrough
         case OPENCL_COMPILED:   // fallthrough
         case PREPARE_EXECUTE:   // fallthrough
         case EXECUTED:          // fallthrough
         {
            if (deviceProfile == null) {
               logger.log(Level.SEVERE, "Error in KernelProfile, no currentDevice (synchronization error?");
            }
            deviceProfile.onEvent(event);
            break;
         }
         case START:
            throw new IllegalArgumentException("must use onStart(Device) to start profiling");
         default:
            throw new IllegalArgumentException("Unhandled event " + event);
      }
   }

   public Collection<Device> getDevices() {
      return deviceProfiles.keySet();
   }

   public Collection<KernelDeviceProfile> getDeviceProfiles() {
      return deviceProfiles.values();
   }

   public KernelDeviceProfile getDeviceProfile(Device device) {
      return deviceProfiles.get(device);
   }

   public void setReportObserver(IProfileReportObserver _observer) {
	  observer = _observer;
   }
   
   public IProfileReportObserver getReportObserver() {
	   return observer;
   }
}
