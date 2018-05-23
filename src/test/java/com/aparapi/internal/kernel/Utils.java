/**
 * Copyright (c) 2016 - 2018 Syncleus, Inc.
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

import com.aparapi.Kernel;
import com.aparapi.device.Device;

import static org.mockito.Mockito.mock;

public class Utils {

    public static KernelDeviceProfile createKernelDeviceProfile() {
        Device device = mock(Device.class);
        KernelProfile kernelProfile = mock(KernelProfile.class);
        return new KernelDeviceProfile(kernelProfile, Kernel.class, device);
    }
}
