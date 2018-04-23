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
   private final AtomicReference<LinkedList<Device>> preferredDevices = new AtomicReference<>(null);
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

   synchronized void setPreferredDevices(LinkedHashSet<Device> _preferredDevices) {
      if (preferredDevices.get() != null) {
         preferredDevices.get().clear();
         preferredDevices.get().addAll(_preferredDevices);
      }
      else {
         preferredDevices.set(new LinkedList<>(_preferredDevices));
      }
      failedDevices.clear();
   }

   public Device getPreferredDevice(Kernel kernel) {
      List<Device> localPreferredDevices = getPreferredDevices(kernel);
      return localPreferredDevices.isEmpty() ? null : localPreferredDevices.get(0);
   }

   synchronized void markPreferredDeviceFailed() {
      if (preferredDevices.get().size() > 0) {
         failedDevices.add(preferredDevices.get().remove(0));
      }
   }

   private void maybeSetUpDefaultPreferredDevices() {
	   preferredDevices.compareAndSet(null, new LinkedList<>(manager.getDefaultPreferences().getPreferredDevices(null)));
   }

   public List<Device> getFailedDevices() {
      return new ArrayList<>(failedDevices);
   }
}
