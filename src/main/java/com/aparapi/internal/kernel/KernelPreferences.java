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

import com.aparapi.Kernel;
import com.aparapi.device.Device;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class KernelPreferences {
    public final Class<? extends Kernel> kernelClass;
    private final KernelManager manager;
    private final List<Device> preferredDevices = new CopyOnWriteArrayList<>();
    private final LinkedHashSet<Device> failedDevices = new LinkedHashSet<>();

    public KernelPreferences(KernelManager manager, Class<? extends Kernel> kernelClass) {
        this.kernelClass = kernelClass;
        this.manager = manager;
    }

    public List<Device> getPreferredDevices(Kernel kernel) {

        List<Device> p = this.preferredDevices;

        if (p.isEmpty()) {
            p = setPreferredDevices(manager.getDefaultPreferences().getPreferredDevices(null));
        }

        if (kernel == null) {
            return Collections.unmodifiableList(p);
        } else {

            List<Device> localPreferredDevices = new ArrayList<>();
            for (int i = 0, pSize = p.size(); i < pSize; i++) {
                Device device = p.get(i);
                if (kernel.isAllowDevice(device)) {
                    localPreferredDevices.add(device);
                }
            }
            return Collections.unmodifiableList(localPreferredDevices);
        }
    }

    List<Device> setPreferredDevices(Collection<Device> _preferredDevices) {

        synchronized (preferredDevices) {
            preferredDevices.clear();
            preferredDevices.addAll(_preferredDevices);
            failedDevices.clear();
        }

        return preferredDevices;

      /*}
      else {
         preferredDevices = new LinkedList<>(_preferredDevices);
      }*/
    }

    public Device getPreferredDevice(Kernel kernel) {
        List<Device> localPreferredDevices = getPreferredDevices(kernel);
        return localPreferredDevices.isEmpty() ? null : localPreferredDevices.get(0);
    }

    synchronized void markPreferredDeviceFailed() {
        synchronized (preferredDevices) {
            if (!preferredDevices.isEmpty()) {
                failedDevices.add(preferredDevices.remove(0));
            }
        }
    }

    List<Device> getFailedDevices() {
        return new ArrayList<>(failedDevices);
    }
}
