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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Collects profiling information per kernel class per device. Not thread safe, it is necessary for client code to correctly synchronize on
 * objects of this class.
 */
public class KernelProfile {

   private static final double MILLION = 1000000d;
   //private static Logger logger = Logger.getLogger(Config.getLoggerName());
   private final Class<? extends Kernel> kernelClass;
   public final Map<Device, KernelDeviceProfile> deviceProfiles = new ConcurrentHashMap<>();

   public KernelProfile(Class<? extends Kernel> _kernelClass) {
      kernelClass = _kernelClass;
   }

   public double getTotalExecutionTime() {
      double sum = 0;
      for (KernelDeviceProfile p : deviceProfiles.values())
         sum += p.getLastElapsedTime(ProfilingEvent.START, ProfilingEvent.EXECUTED);
      return sum / MILLION;
   }

   public double getTotalConversionTime() {
      double sum = 0;
      for (KernelDeviceProfile p : deviceProfiles.values())
         sum += p.getLastElapsedTime(ProfilingEvent.START, ProfilingEvent.PREPARE_EXECUTE);
      return sum / MILLION;
   }

   public double getTotalTime() {
      double sum = 0;
      for (KernelDeviceProfile p : deviceProfiles.values())
         sum += p.getCumulativeElapsedTimeAll();
      return sum / MILLION;
   }



   KernelDeviceProfile start(Device device) {
      KernelDeviceProfile currentDeviceProfile;
      //synchronized (deviceProfiles) {
       currentDeviceProfile = deviceProfiles.computeIfAbsent(device, d -> new KernelDeviceProfile(kernelClass, d));
       //}
      currentDeviceProfile.on(ProfilingEvent.START);
      return currentDeviceProfile;
   }

   public KernelDeviceProfile profiler(Device device) {
//      switch (event) {
//         case CLASS_MODEL_BUILT: // fallthrough
//         case OPENCL_GENERATED:  // fallthrough
//         case INIT_JNI:          // fallthrough
//         case OPENCL_COMPILED:   // fallthrough
//         case PREPARE_EXECUTE:   // fallthrough
//         case EXECUTED:          // fallthrough
//         {
            return deviceProfiles.get(device);
//         }
//         case START:
//            throw new IllegalArgumentException("must use onStart(Device) to start profiling");
//         default:
//            throw new IllegalArgumentException("Unhandled event " + event);
//      }
   }

   public Collection<Device> getDevices() {
      return deviceProfiles.keySet();
   }

   public Collection<KernelDeviceProfile> getDeviceProfiles() {
      return deviceProfiles.values();
   }

}
