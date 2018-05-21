package com.aparapi.device;

import com.aparapi.internal.opencl.OpenCLPlatform;

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
}
