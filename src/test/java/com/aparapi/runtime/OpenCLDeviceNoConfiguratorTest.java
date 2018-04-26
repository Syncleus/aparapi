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
import static org.junit.Assert.assertNotEquals;
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
 * Tests for feature OpenCLDeviceConfigurator set 2/2 
 * 
 * @author CoreRasurae
 */
public class OpenCLDeviceNoConfiguratorTest {
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

    @Test
    public void noConfiguratorTest() throws Exception {
    	setUp();
    	assertTrue("Device isShareMempory() should return true", openCLDevice.isSharedMemory());
		assertNotEquals("Device name should not be \"Configured\"", "Configured", openCLDevice.getName());
    	List<OpenCLPlatform> platforms = OpenCLPlatform.getUncachedOpenCLPlatforms();
    	for (OpenCLPlatform platform : platforms) {
    		for (OpenCLDevice device : platform.getOpenCLDevices()) {
    			assertTrue("Device isSharedMempory() should return true", device.isSharedMemory());
    			assertNotEquals("Device name should not be \"Configured\"", "Configured", device.getName());
    		}
    	}
    }
    
    @Test
    public void protectionAgainstRecursiveConfiguresTest() {
    	OpenCLDevice dev = new OpenCLDevice(null, 101L, Device.TYPE.CPU);
    	final AtomicInteger callCounter = new AtomicInteger(0);
    	IOpenCLDeviceConfigurator configurator = new IOpenCLDeviceConfigurator() {
			@Override
			public void configure(OpenCLDevice device) {
				callCounter.incrementAndGet();
				device.configure();
			}
    	};
    	OpenCLDevice.setConfigurator(configurator);
    	dev.configure();
    	
    	assertEquals("Number of confgure() calls should be one", 1, callCounter.get());
    }
}
