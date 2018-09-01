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
    public void simpleConstIndexOpenCLTest() {
        SimpleConstIndexLocalVarKernel myKernel = new SimpleConstIndexLocalVarKernel();
        Range range = openCLDevice.createRange(SIZE, SIZE);
        try {
            myKernel.execute(range);
            assertEquals("Atomic increment doesn't match, index 1", SIZE, myKernel.atomics[1].get());
            assertEquals("Atomic increment doesn't match, index 2", SIZE, myKernel.atomics[2].get());
            assertEquals("Atomic increment doesn't match, index 3", SIZE, myKernel.atomics[3].get());            
        } finally {
            myKernel.dispose();
        }
    }        

    @Test
    public void simpleVarIndexOpenCLTest() {
        SimpleVarIndexLocalVarKernel myKernel = new SimpleVarIndexLocalVarKernel();
        Range range = openCLDevice.createRange(SIZE, SIZE);
        try {
            myKernel.execute(range);
            assertEquals("Atomic increment doesn't match", SIZE, myKernel.atomics[4].get());
        } finally {
            myKernel.dispose();
        }
    }        

    public class SimpleConstIndexLocalVarKernel extends Kernel {
        private AtomicInteger[] atomics = new AtomicInteger[SIZE];

        public SimpleConstIndexLocalVarKernel() {
            for (int i = 0; i < atomics.length; i++) {
                atomics[i] = new AtomicInteger(0);
            }
        }

        @Override 
        public void run() {
            atomicUpdate1(atomics, 1);
            atomicUpdate2(2, atomics);
            atomicUpdate3(3, 0, atomics);
        }

        public int atomicUpdate1(AtomicInteger[] arr, int index) {
            //Exercises I_ALOAD_1
            return atomicInc(arr[index]);    
        }

        public int atomicUpdate2(int index, AtomicInteger[] arr) {
            //Exercises I_ALOAD_2
            return atomicInc(arr[index]);    
        }
        
        public int atomicUpdate3(int index, int indexB, AtomicInteger[] arr) {
            //Exercises I_ALOAD_3
            return atomicInc(arr[index+indexB]);    
        }     
    }
    
    public class SimpleVarIndexLocalVarKernel extends Kernel {
        private AtomicInteger[] atomics = new AtomicInteger[SIZE];

        public SimpleVarIndexLocalVarKernel() {
            for (int i = 0; i < atomics.length; i++) {
                atomics[i] = new AtomicInteger(0);
            }
        }

        @Override 
        public void run() {
            atomicUpdate4(4, 0, 0, atomics);
        }

        public int atomicUpdate4(int index, int indexB, int indexC, AtomicInteger[] arr) {
            //Exercises I_ALOAD - when index is greater than 3
            return atomicInc(arr[index+indexB+indexC]);
        }   
    }
}
