package com.aparapi.device;

import com.aparapi.internal.opencl.OpenCLPlatform;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class OpenCLDeviceTest {

    @Before
    public void resetConfigurator() {
        OpenCLDevice.setConfigurator(null);
    }

    @Test
    public void shouldSetShortDescriptionBasedOnPlatformNameWithBrackets() {
        OpenCLDevice sut = Utils.createDevice(Utils.createPlatform("Intel (R)"), Device.TYPE.CPU);
        assertEquals("Intel<CPU>", sut.getShortDescription());
    }

    @Test
    public void shouldSetShortDescriptionBasedOnPlatformNameWithSpaces() {
        OpenCLDevice sut = Utils.createDevice(Utils.createPlatform("Intel Core"), Device.TYPE.CPU);
        assertEquals("Intel<CPU>", sut.getShortDescription());
    }


    /**
     * once device description is calculated,
     * it should be set to object field and should not be calculated again
     */
    @Test
    public void shouldSetShortDescriptionToDeviceProperty() {
        OpenCLPlatform platform = Utils.createPlatform("Intel");
        OpenCLDevice sut = Utils.createDevice(platform, Device.TYPE.CPU);
        sut.getShortDescription();
        sut.getShortDescription();
        verify(platform, times(1)).getName();
    }


}
