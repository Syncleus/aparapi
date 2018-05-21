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
