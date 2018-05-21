package com.aparapi.device;

import com.aparapi.internal.opencl.OpenCLPlatform;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class OpenCLDeviceTest {

    private static final int DEVICE_ID = 1;

    @Before
    public void resetConfigurator() {
        OpenCLDevice.setConfigurator(null);
    }

    @Test
    public void shouldSetShortDescriptionBasedOnPlatformNameWithBrackets() {
        OpenCLDevice sut = createDevice(createPlatform("Intel (R)"), Device.TYPE.CPU);
        assertEquals("Intel<CPU>", sut.getShortDescription());
    }

    @Test
    public void shouldSetShortDescriptionBasedOnPlatformNameWithSpaces() {
        OpenCLDevice sut = createDevice(createPlatform("Intel Core"), Device.TYPE.CPU);
        assertEquals("Intel<CPU>", sut.getShortDescription());
    }


    /**
     * once device description is calculated,
     * it should be set to object field and should not be calculated again
     */
    @Test
    public void shouldSetShortDescriptionToDeviceProperty() {
        OpenCLPlatform platform = createPlatform("Intel");
        OpenCLDevice sut = createDevice(platform, Device.TYPE.CPU);
        sut.getShortDescription();
        sut.getShortDescription();
        verify(platform, times(1)).getName();
    }


    private static OpenCLDevice createDevice(OpenCLPlatform platform, Device.TYPE type) {
        return new OpenCLDevice(platform, DEVICE_ID, type);
    }

    private static OpenCLPlatform createPlatform(String name) {
        OpenCLPlatform platform = mock(OpenCLPlatform.class);
        when(platform.getName()).thenReturn(name);
        return platform;

    }


}
