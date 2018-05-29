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
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class OpenCLDeviceTest {

    public static final String TEST = "test";
    public static final String INTEL = "Intel";

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
        OpenCLPlatform platform = Utils.createPlatform(INTEL);
        OpenCLDevice sut = Utils.createDevice(platform, Device.TYPE.CPU);
        sut.getShortDescription();
        sut.getShortDescription();
        verify(platform).getName();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowAnExceptionIfAnnotationValueIsNull() {
        OpenCLDevice sut = Utils.createDevice(Utils.createPlatform("Intel (R)"), Device.TYPE.CPU);
        sut.getArgs(Utils.methodByName("charAt", String.class));
    }

    @Test
    public void shouldConfigureDevice() {
        OpenCLDevice sut = Utils.createDevice(Utils.createPlatform(INTEL), Device.TYPE.CPU);
        IOpenCLDeviceConfigurator testConfigurator = mock(IOpenCLDeviceConfigurator.class);
        OpenCLDevice.setConfigurator(testConfigurator);
        sut.configure();
        verify(testConfigurator).configure(sut);
    }

    @Test
    public void shouldSkipConfigurationIfConfiguratorIsNotSet() {
        OpenCLDevice sut = Utils.createDevice(Utils.createPlatform(INTEL), Device.TYPE.CPU);
        OpenCLDevice.setConfigurator(null);
        sut.configure();
    }
}
