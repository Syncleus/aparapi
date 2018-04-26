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
package com.aparapi.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.aparapi.device.Device;
import com.aparapi.device.IOpenCLDeviceConfigurator;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.KernelManager;
import com.aparapi.internal.opencl.OpenCLPlatform;

/**
 * Tests for feature OpenCLDeviceConfigurator set 1/2 
 * 
 * @author CoreRasurae
 */
public class OpenCLDeviceConfiguratorTest {
    private static OpenCLDevice openCLDevice = null;

    private class CLKernelManager extends KernelManager {
    	@Override
    	protected List<Device.TYPE> getPreferredDeviceTypes() {
    		return Arrays.asList(Device.TYPE.ACC, Device.TYPE.GPU, Device.TYPE.CPU);
    	}
    }
        
    public void setUp() throws Exception {
    	KernelManager.setKernelManager(new CLKernelManager());
        Device device = KernelManager.instance().bestDevice();
        if (device == null || !(device instanceof OpenCLDevice)) {
        	System.out.println("!!!No OpenCLDevice available for running the integration test");
        }
        assumeTrue (device != null && device instanceof OpenCLDevice);
        openCLDevice = (OpenCLDevice) device;
    }

    public void setUpWithConfigurator(IOpenCLDeviceConfigurator configurator) throws Exception {
    	OpenCLDevice.setConfigurator(configurator);
    	setUp();
    }
    
    @Test
    public void configuratorCallbackTest() throws Exception {
    	final AtomicInteger callCounter = new AtomicInteger(0);
    	IOpenCLDeviceConfigurator configurator = new IOpenCLDeviceConfigurator() {
			@Override
			public void configure(OpenCLDevice device) {
				callCounter.incrementAndGet();
				device.setName("Configured");
				device.setSharedMemory(false);
			}
    	};
    	setUpWithConfigurator(configurator);
    	assertTrue("Number of configured devices should be > 0", callCounter.get() > 0);
    	int numberOfConfiguredDevices = callCounter.get();
    	
		assertFalse("Device isShareMempory() should return false", openCLDevice.isSharedMemory());
		assertEquals("Device name should be \"Configured\"", "Configured", openCLDevice.getName());   
    	
    	int numberOfDevices = 0;
    	List<OpenCLPlatform> platforms = OpenCLPlatform.getUncachedOpenCLPlatforms();
    	for (OpenCLPlatform platform : platforms) {
    		for (OpenCLDevice device : platform.getOpenCLDevices()) {
    			assertFalse("Device isShareMempory() should return false", device.isSharedMemory());
    			assertEquals("Device name should be \"Configured\"", "Configured", device.getName());
    			numberOfDevices++;
    		}
    	}

    	assertEquals("Number of configured devices should match numnber of devices", numberOfDevices, numberOfConfiguredDevices);
    	assertEquals("Number of calls doesn't match the expected", numberOfDevices*2, callCounter.get());
    }
 }
