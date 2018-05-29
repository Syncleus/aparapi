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

import com.aparapi.Kernel;
import com.aparapi.codegen.test.CallObject;
import com.aparapi.device.Device;

import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;

class Utils {

    static KernelDeviceProfile createKernelDeviceProfile() {
        Device device = mock(Device.class);
        KernelProfile kernelProfile = mock(KernelProfile.class);
        return new KernelDeviceProfile(kernelProfile, Kernel.class, device);
    }

    static Kernel createKernel() {
        return new CallObject();
    }

    static KernelRunner createKernelRunner() {
        Kernel kernel = mock(Kernel.class);
        return new KernelRunner(kernel);
    }

    static KernelArg createKernelArg(Field field, int type) {
        KernelArg arg = new KernelArg();
        arg.setField(field);
        arg.setType(type);
        return arg;
    }

    static Object getFieldValue(KernelRunner kernelRunner, String fieldName) throws Exception {
        Field field = KernelRunner.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(kernelRunner);
    }

    static <T> void setFieldValue(KernelRunner kernelRunner, String fieldName, T fieldValue) throws Exception {
        Field field = KernelRunner.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(kernelRunner, fieldValue);
    }
}
