package com.amd.aparapi.internal.kernel;

import com.amd.aparapi.*;
import com.amd.aparapi.device.*;

import java.util.*;
import java.util.logging.*;

/**
 * Collects profiling information per kernel class per device. Not thread safe, it is necessary for client code to correctly synchronize on
 * objects of this class.
 */
public class KernelProfile {

   private static Logger logger = Logger.getLogger(Config.getLoggerName());
   private final Class<? extends Kernel> kernelClass;
   private LinkedHashMap<Device, KernelDeviceProfile> deviceProfiles = new LinkedHashMap<>();
   private Device currentDevice;
   private Device lastDevice;
   private KernelDeviceProfile currentDeviceProfile;

   public KernelProfile(Class<? extends Kernel> _kernelClass) {
      kernelClass = _kernelClass;
   }

   public double getLastExecutionTime() {
      KernelDeviceProfile lastDeviceProfile = getLastDeviceProfile();
      return lastDeviceProfile == null ? Double.NaN : lastDeviceProfile.getLastElapsedTime(ProfilingEvent.START, ProfilingEvent.EXECUTED);
   }

   public double getLastConversionTime() {
      KernelDeviceProfile lastDeviceProfile = getLastDeviceProfile();
      return lastDeviceProfile == null ? Double.NaN : lastDeviceProfile.getLastElapsedTime(ProfilingEvent.START, ProfilingEvent.EXECUTED);   }

   public double getAccumulatedTotalTime() {
      KernelDeviceProfile lastDeviceProfile = getLastDeviceProfile();
      if (lastDeviceProfile == null) {
         return Double.NaN;
      }
      else {
         return lastDeviceProfile.getCumulativeElapsedTimeAll();
      }
   }

   private KernelDeviceProfile getLastDeviceProfile() {
      return null;
   }

   void onStart(Device device) {
      currentDevice = device;
      synchronized (deviceProfiles) {
         currentDeviceProfile = deviceProfiles.get(device);
         if (currentDeviceProfile == null) {
            currentDeviceProfile = new KernelDeviceProfile(kernelClass, device);
            deviceProfiles.put(device, currentDeviceProfile);
         }
      }
      currentDeviceProfile.onEvent(ProfilingEvent.START);
   }

   void onEvent(ProfilingEvent event) {
      switch (event) {
         case CLASS_MODEL_BUILT: // fallthrough
         case OPENCL_GENERATED: // fallthrough
         case OPENCL_COMPILED: // fallthrough
         case PREPARE_EXECUTE: // fallthrough
         case EXECUTED: // fallthrough
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
}
