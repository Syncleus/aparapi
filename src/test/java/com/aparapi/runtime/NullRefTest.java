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

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.KernelManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assume.assumeTrue;

public class NullRefTest {
    private static OpenCLDevice openCLDevice = null;

    private class CLKernelManager extends KernelManager {
        @Override
        protected List<Device.TYPE> getPreferredDeviceTypes() {
            return Arrays.asList(Device.TYPE.ACC, Device.TYPE.GPU, Device.TYPE.CPU);
        }
    }

    @Before
    public void setUpBeforeClass() throws Exception {
        KernelManager.setKernelManager(new CLKernelManager());
        Device device = KernelManager.instance().bestDevice();
        assumeTrue(device != null && device instanceof OpenCLDevice);
        openCLDevice = (OpenCLDevice) device;
    }

    @After
    public void classTeardown() {
        Util.resetKernelManager();
    }

    @Test
    public void test() {
        doTest();
    }

    private void doTest() {
        final Kernel kernel = new NullRefKernel();
        kernel.execute(Range.create(openCLDevice, 1, 1));
    }

    private class NullRefKernel extends Kernel {
        private final int[] nullArray = null;

        @Override
        public void run() {
            if(nullArray == null) {
                noop();
            }
        }

        private void noop() {
            //no op
        }
    }
}
