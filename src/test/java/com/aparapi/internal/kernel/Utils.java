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
