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
import java.util.logging.*;

/**
 * Collects profiling information per kernel class per device. Not thread safe, it is necessary for client code to correctly synchronize on
 * objects of this class.
 */
public class KernelProfile {

   public static final double MILLION = 1000000d;
   private static Logger logger = Logger.getLogger(Config.getLoggerName());
   private final Class<? extends Kernel> kernelClass;
   private LinkedHashMap<Device, KernelDeviceProfile> deviceProfiles = new LinkedHashMap<>();
   private Device currentDevice;
   private Device lastDevice;
   private KernelDeviceProfile currentDeviceProfile;
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

   public KernelDeviceProfile getLastDeviceProfile() {
      return deviceProfiles.get(currentDevice);
   }

   void onStart(Device device) {
      synchronized (deviceProfiles) {
         currentDeviceProfile = deviceProfiles.get(device);
         if (currentDeviceProfile == null) {
            currentDeviceProfile = new KernelDeviceProfile(this, kernelClass, device);
            deviceProfiles.put(device, currentDeviceProfile);
         }
      }
      
      currentDeviceProfile.onEvent(ProfilingEvent.START);
      currentDevice = device;
   }

   void onEvent(ProfilingEvent event) {
      switch (event) {
         case CLASS_MODEL_BUILT: // fallthrough
         case OPENCL_GENERATED:  // fallthrough
         case INIT_JNI:          // fallthrough
         case OPENCL_COMPILED:   // fallthrough
         case PREPARE_EXECUTE:   // fallthrough
         case EXECUTED:          // fallthrough
         {
            if (currentDeviceProfile == null) {
               logger.log(Level.SEVERE, "Error in KernelProfile, no currentDevice (synchronization error?");
            }
            currentDeviceProfile.onEvent(event);
            break;
         }
         case START:
            throw new IllegalArgumentException("must use onStart(Device) to start profiling");
         default:
            throw new IllegalArgumentException("Unhandled event " + event);
      }
   }

   void onFinishedExecution() {
      reset();
   }

   private void reset() {
      lastDevice = currentDevice;
      currentDevice = null;
      currentDeviceProfile = null;
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
