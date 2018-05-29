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
package com.aparapi.device;

import com.aparapi.internal.opencl.OpenCLPlatform;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class Utils {

    static final int DEVICE_ID = 1;

    static OpenCLDevice createDevice(OpenCLPlatform platform, Device.TYPE type) {
        return new OpenCLDevice(platform, DEVICE_ID, type);
    }

    static OpenCLPlatform createPlatform(String name) {
        OpenCLPlatform platform = mock(OpenCLPlatform.class);
        when(platform.getName()).thenReturn(name);
        return platform;
    }

    static Method methodByName(String name, Class<?> clazz) {
        return Arrays.stream(clazz.getMethods())
            .filter(m -> m.getName().equals(name))
            .findFirst().orElseThrow(() -> new RuntimeException("method with name not found " + name));
    }
}
