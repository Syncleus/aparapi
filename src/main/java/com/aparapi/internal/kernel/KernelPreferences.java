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
import java.util.concurrent.atomic.AtomicReference;

/**
 * Thread safe class holding the kernel preferences for a given kernel class.
 */
public class KernelPreferences {
   private final Class<? extends Kernel> kernelClass;
   private final KernelManager manager;
   private final AtomicReference<LinkedHashSet<Device>> preferredDevices = new AtomicReference<>(null);
   private final LinkedHashSet<Device> failedDevices = new LinkedHashSet<>();

   public KernelPreferences(KernelManager manager, Class<? extends Kernel> kernelClass) {
      this.kernelClass = kernelClass;
      this.manager = manager;
   }

   /** What Kernel subclass is this the preferences for? */
   public Class<? extends Kernel> getKernelClass() {
      return kernelClass;
   }

   public List<Device> getPreferredDevices(Kernel kernel) {
      maybeSetUpDefaultPreferredDevices();

      ArrayList<Device> copy;
      synchronized (this) {
         copy = new ArrayList<>(preferredDevices.get());
      }

      if (kernel == null) {
         return Collections.unmodifiableList(copy);
      }
      
      List<Device> localPreferredDevices = new ArrayList<>();
      for (Device device : copy) {
         if (kernel.isAllowDevice(device)) {
            localPreferredDevices.add(device);
         }
      }
      return Collections.unmodifiableList(localPreferredDevices);
   }
   
   /**
    * Validates if the specified devices is among the preferred devices for executing the kernel associated with the current
    * kernel preferences.
    * @param device the device to be tested
    * @return <ul><li>true, if specified device is among the preferred devices</li>
    *             <li>false, otherwise</li></ul>
    */
   public boolean isDeviceAmongPreferredDevices(Device device) {
	   maybeSetUpDefaultPreferredDevices();
	   
	   boolean result = false;
	   synchronized (this) {
		   result = preferredDevices.get().contains(device);
	   }
	   
	   return result;
   }

   synchronized void setPreferredDevices(LinkedHashSet<Device> _preferredDevices) {
      if (preferredDevices.get() != null) {
         preferredDevices.get().clear();
         preferredDevices.get().addAll(_preferredDevices);
      }
      else {
         preferredDevices.set(new LinkedHashSet<>(_preferredDevices));
      }
      failedDevices.clear();
   }

   public Device getPreferredDevice(Kernel kernel) {
      List<Device> localPreferredDevices = getPreferredDevices(kernel);
      return localPreferredDevices.isEmpty() ? null : localPreferredDevices.get(0);
   }

   synchronized void markPreferredDeviceFailed() {
	  LinkedHashSet<Device> devices = preferredDevices.get();
      if (devices.size() > 0) {
    	 Device device = devices.iterator().next();
    	 preferredDevices.get().remove(device);
         failedDevices.add(device);
      }
   }
   
   synchronized void markDeviceFailed(Device device) {
	   preferredDevices.get().remove(device);
       failedDevices.add(device);
   }


   private void maybeSetUpDefaultPreferredDevices() {
	   if (preferredDevices.get() == null) {
		   preferredDevices.compareAndSet(null, new LinkedHashSet<>(manager.getDefaultPreferences().getPreferredDevices(null)));
	   }
   }

   public synchronized List<Device> getFailedDevices() {
      return new ArrayList<>(failedDevices);
   }
}
