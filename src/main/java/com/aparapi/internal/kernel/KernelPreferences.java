/**
 * Copyright (c) 2016 - 2017 Syncleus, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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

public class KernelPreferences {
    private final Class<? extends Kernel> kernelClass;
    private final KernelManager manager;
    private final LinkedList<Device> preferredDevices = new LinkedList();
    protected final LinkedHashSet<Device> failedDevices = new LinkedHashSet<>();

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
        synchronized (preferredDevices) {
            if (kernel == null)
                return Collections.unmodifiableList(preferredDevices);
            else
                copy = new ArrayList(preferredDevices);
        }
        List<Device> localPreferredDevices = new ArrayList<>();
        for (Device device : copy) {
            if (kernel.isAllowDevice(device))
                localPreferredDevices.add(device);
        }
        return Collections.unmodifiableList(localPreferredDevices);
    }

    void setPreferredDevices(LinkedHashSet<Device> _preferredDevices) {
        synchronized (preferredDevices) {
            preferredDevices.clear();
            preferredDevices.addAll(_preferredDevices);
            failedDevices.clear();
        }
    }

    public Device getPreferredDevice(Kernel kernel) {
        List<Device> localPreferredDevices = getPreferredDevices(kernel);
        return localPreferredDevices.isEmpty() ? null : localPreferredDevices.get(0);
    }

    void markPreferredDeviceFailed() {
        synchronized (preferredDevices) {
            if (!preferredDevices.isEmpty()) {
                failedDevices.add(preferredDevices.removeFirst());
            }
        }
    }

    private void maybeSetUpDefaultPreferredDevices() {

        synchronized (preferredDevices) {
            if (preferredDevices.isEmpty()) {
                preferredDevices.addAll(manager.defaultPreferences.getPreferredDevices(null));
            }
        }

    }

//   public List<Device> getFailedDevices() {
//      return new ArrayList<>(failedDevices);
//   }
}
