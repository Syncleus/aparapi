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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Test;

import com.aparapi.device.Device;
import com.aparapi.device.IOpenCLDeviceConfigurator;
import com.aparapi.device.JavaDevice;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.KernelManager;
import com.aparapi.internal.kernel.KernelPreferences;
import com.aparapi.internal.opencl.OpenCLPlatform;

/**
 * Tests for feature OpenCLDeviceConfigurator set 1/2 
 * 
 * @author CoreRasurae
 */
public class OpenCLDeviceConfiguratorTest {
    private static OpenCLDevice openCLDevice = null;
    private final AtomicInteger callCounter = new AtomicInteger(0);
    
	public static List<OpenCLDevice> listDevices(OpenCLDevice.TYPE type) {
		final ArrayList<OpenCLDevice> results = new ArrayList<>();

		for (final OpenCLPlatform p : OpenCLPlatform.getUncachedOpenCLPlatforms()) {
			for (final OpenCLDevice device : p.getOpenCLDevices()) {
				if (type == null || device.getType() == type) {
					results.add(device);
				}
			}
		}

		return results;
	}

	private class UncachedCLKernelManager extends KernelManager {
		private KernelPreferences defaultPreferences;
		
		@Override
		protected void setup() {
			callCounter.set(0);
			defaultPreferences = createDefaultPreferences();
		}
		
		@Override
		public KernelPreferences getDefaultPreferences() {
			return defaultPreferences;
		}
	
		private List<OpenCLDevice> filter(OpenCLDevice.TYPE type, List<OpenCLDevice> devices) {
			final ArrayList<OpenCLDevice> results = new ArrayList<>();

			for (final OpenCLDevice device : devices) {
				if (type == null || device.getType() == type) {
					results.add(device);
				}
			}

			return results;
		}
		
		@Override
		protected LinkedHashSet<Device> createDefaultPreferredDevices() {
			LinkedHashSet<Device> devices = new LinkedHashSet<>();

			List<OpenCLDevice> all = listDevices(null);
			
			List<OpenCLDevice> accelerators = filter(Device.TYPE.ACC, all);
			List<OpenCLDevice> gpus = filter(Device.TYPE.GPU, all);
			List<OpenCLDevice> cpus = filter(Device.TYPE.CPU, all);

			Collections.sort(accelerators, getDefaultAcceleratorComparator());
			Collections.sort(gpus, getDefaultGPUComparator());

			List<Device.TYPE> preferredDeviceTypes = getPreferredDeviceTypes();

			for (Device.TYPE type : preferredDeviceTypes) {
				switch (type) {
				case UNKNOWN:
					throw new AssertionError("UNKNOWN device type not supported");
				case GPU:
					devices.addAll(gpus);
					break;
				case CPU:
					devices.addAll(cpus);
					break;
				case JTP:
					devices.add(JavaDevice.THREAD_POOL);
					break;
				case SEQ:
					devices.add(JavaDevice.SEQUENTIAL);
					break;
				case ACC:
					devices.addAll(accelerators);
					break;
				case ALT:
					devices.add(JavaDevice.ALTERNATIVE_ALGORITHM);
					break;
				default:
				}
			}
			
			return devices;
		}
		
		@Override
		protected List<Device.TYPE> getPreferredDeviceTypes() {
			return Arrays.asList(Device.TYPE.ACC, Device.TYPE.GPU, Device.TYPE.CPU);
		}
	}
        
    public void setUp() throws Exception {
    	KernelManager.setKernelManager(new UncachedCLKernelManager());
        Device device = KernelManager.instance().bestDevice();
        if (device == null || !(device instanceof OpenCLDevice)) {
        	System.out.println("!!!No OpenCLDevice available for running the integration test");
        }
        assumeTrue (device != null && device instanceof OpenCLDevice);
        openCLDevice = (OpenCLDevice) device;
    }

    @After
    public void teardDown() {
    	Util.resetKernelManager();
    }
    
    
    public void setUpWithConfigurator(IOpenCLDeviceConfigurator configurator) throws Exception {
    	OpenCLDevice.setConfigurator(configurator);
    	setUp();
    }
    
    @Test
    public void configuratorCallbackTest() throws Exception {
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
    
    @Test(expected=IllegalArgumentException.class)
    public void exceptionConfiguratorTestFail() {
    	final AtomicBoolean called = new AtomicBoolean(false);
    	OpenCLDevice dev = new OpenCLDevice(null, 101L, Device.TYPE.CPU);
    	IOpenCLDeviceConfigurator configurator = new IOpenCLDeviceConfigurator() {
			@Override
			public void configure(OpenCLDevice device) {
				called.set(true);
				throw new IllegalArgumentException("This exception is part of the test, shouldn't cause test to fail");
			}
    	};
    	OpenCLDevice.setConfigurator(configurator);
    	dev.configure();
    	assertTrue("Configurator should have benn called", called.get());
    }
 }
