package com.aparapi.device;

import com.aparapi.ProfileInfo;
import com.aparapi.internal.opencl.OpenCLPlatform;
import com.aparapi.opencl.OpenCL;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class OpenCLDeviceTest {

    public static final String TEST = "test";

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

    @Test(expected = IllegalStateException.class)
    public void shouldThrowAnExceptionIfAnnotationValueIsNull() {
        OpenCLDevice sut = Utils.createDevice(Utils.createPlatform("Intel (R)"), Device.TYPE.CPU);
        sut.getArgs(Utils.methodByName("charAt", String.class));
    }
}
