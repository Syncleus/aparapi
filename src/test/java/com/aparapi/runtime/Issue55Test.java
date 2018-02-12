/**
 * Copyright (c) 2016 - 2017 Syncleus, Inc.
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.aparapi.Kernel;
import com.aparapi.device.Device;
import com.aparapi.internal.kernel.KernelManager;
import com.aparapi.internal.kernel.KernelManagers;

public class Issue55Test {
	private Kernel testKernel;

	@Before
	public void setUp() {
		testKernel = new Kernel() {

			/**
			 * This kernel does nothing.
			 */
			@Override
			public void run() {
				// this block was intentionally left empty, as the kernel should do nothing
			}
		};
	}

	@After
	public void tearDown() {
		testKernel.dispose();
	}

	@AfterClass
	public static void tearDownClass() {
		// reset the KernelManager after we are done, as some tests expect a openCL device
		KernelManager.setKernelManager(new KernelManager() {});
	}

	@Test
	public void testUseJtpOnly() {
		KernelManager.setKernelManager(KernelManagers.JTP_ONLY);

        Device device = KernelManager.instance().defaultPreferences.getPreferredDevice(testKernel);

		assertThat(device.getType(), is(Device.TYPE.JTP));
	}

	@Test
	public void testUseSequentialOnly() {
		KernelManager.setKernelManager(KernelManagers.SEQUENTIAL_ONLY);

        Device device = KernelManager.instance().defaultPreferences.getPreferredDevice(testKernel);

		assertThat(device.getType(), is(Device.TYPE.SEQ));
	}
}
