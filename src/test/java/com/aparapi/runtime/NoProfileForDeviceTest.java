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

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.aparapi.Kernel;
import com.aparapi.ProfileReport;
import com.aparapi.device.Device;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.KernelManager;

/**
 * Provides integration tests to help in assuring that new APIs for ProfileReports are working,
 * when called for a device that never ran the specific kernel. 
 * 
 * @author CoreRasurae
 */
public class NoProfileForDeviceTest {

	private static OpenCLDevice openCLDevice;
	
	private class CLKernelManager extends KernelManager {
    	@Override
    	protected List<Device.TYPE> getPreferredDeviceTypes() {
    		return Arrays.asList(Device.TYPE.ACC, Device.TYPE.GPU, Device.TYPE.CPU);
    	}
    }
	
	@Before
    public void setUpBefore() throws Exception {
    	KernelManager.setKernelManager(new CLKernelManager());
        Device device = KernelManager.instance().bestDevice();
        if (device == null || !(device instanceof OpenCLDevice)) {
        	System.out.println("!!!No OpenCLDevice available for running the integration test - test will be skipped");
        }
        assumeTrue (device != null && device instanceof OpenCLDevice);
        openCLDevice = (OpenCLDevice) device;
    }

    @Test
    public void getProfileReportLastThreadTest() {
    	NeverCalledKernel k = new NeverCalledKernel();
    	WeakReference<ProfileReport> report = k.getProfileReportLastThread(openCLDevice);
    	assertTrue(report == null);
    }
    
    @Test
    public void getProfileReportCurrentThreadTest() {
    	NeverCalledKernel k = new NeverCalledKernel();
    	WeakReference<ProfileReport> report = k.getProfileReportCurrentThread(openCLDevice);
    	assertTrue(report == null);
    }

    private class NeverCalledKernel extends Kernel {

		@Override
		public void run() {
			//Intentionally empty
		}
    	
    }
}
