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
import static org.junit.Assume.assumeTrue;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.JavaDevice;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.KernelManager;

public class LocalAtomicVariableArrayTest {
    private static final int SIZE = 10;
    
    private static OpenCLDevice openCLDevice = null;

    private class CLKernelManager extends KernelManager {
        @Override
        protected List<Device.TYPE> getPreferredDeviceTypes() {
            return Arrays.asList(Device.TYPE.ACC, Device.TYPE.GPU, Device.TYPE.CPU);
        }
    }
    
    private class JTPKernelManager extends KernelManager {
        private JTPKernelManager() {
            LinkedHashSet<Device> preferredDevices = new LinkedHashSet<Device>(1);
            preferredDevices.add(JavaDevice.THREAD_POOL);
            setDefaultPreferredDevices(preferredDevices);
        }
        @Override
        protected List<Device.TYPE> getPreferredDeviceTypes() {
            return Arrays.asList(Device.TYPE.JTP);
        }
    }

    @Before
    public void setUpBeforeClass() throws Exception {
        KernelManager.setKernelManager(new CLKernelManager());
        Device device = KernelManager.instance().bestDevice();
        assumeTrue (device != null && device instanceof OpenCLDevice);
        openCLDevice = (OpenCLDevice) device;
    }

    @After
    public void classTeardown() {
        Util.resetKernelManager();
    }

    @Test
    public void openCLTest() {
        SimpleLocalVarKernel myKernel = new SimpleLocalVarKernel();
        Range range = openCLDevice.createRange(SIZE, SIZE);;
        try {
            myKernel.execute(range);
            assertEquals("Atomic increment doesn't match", SIZE, myKernel.atomics[1].get());
        } finally {
            myKernel.dispose();
        }
    }        
    
    public class SimpleLocalVarKernel extends Kernel {
        private AtomicInteger[] atomics = new AtomicInteger[SIZE];

        public SimpleLocalVarKernel() {
            for (int i = 0; i < atomics.length; i++) {
                atomics[i] = new AtomicInteger(0);
            }
        }

        @Override public void run() {
            int gid = getGlobalId();

            atomicUpdate(atomics, 1);
        }

        public int atomicUpdate(AtomicInteger[] arr, int index) {
            //Other logic could be included to avoid having to call atomicUpdate, just to update an atomic
            return atomicInc(arr[index]);    
        }               
    }
}
