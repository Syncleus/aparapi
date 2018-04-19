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

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.JavaDevice;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.KernelManager;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class BarrierSupportTest {
    private static OpenCLDevice openCLDevice = null;
    private static int SIZE;
    private int[] targetArray;

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
    public void setUpBefore() throws Exception {
    	KernelManager.setKernelManager(new CLKernelManager());
        Device device = KernelManager.instance().bestDevice();
        assumeTrue (device != null && device instanceof OpenCLDevice);
        openCLDevice = (OpenCLDevice) device;
        SIZE = openCLDevice.getMaxWorkGroupSize();
    }

    @Test
    public void testBarrier1() {
    	System.out.println("Executing on device: " + openCLDevice.getShortDescription() + " - " + openCLDevice.getName());
        final Barrrier1Kernel kernel = new Barrrier1Kernel(SIZE);
        try {
	        final Range range = openCLDevice.createRange(SIZE, SIZE);
	        targetArray = initInputArray();
	        kernel.setExplicit(false);
	        kernel.setArray(targetArray);
	        kernel.execute(range);
	        assertTrue(validate());
        } finally {
        	kernel.dispose();
        }
    }
    
    @Test
    public void testBarrier1Explicit() {
    	System.out.println("Executing on device: " + openCLDevice.getShortDescription() + " - " + openCLDevice.getName());
        final Barrrier1Kernel kernel = new Barrrier1Kernel(SIZE);
        try {
	        final Range range = openCLDevice.createRange(SIZE, SIZE);
	        targetArray = initInputArray();
	        kernel.setExplicit(true);
	        kernel.setArray(targetArray);
	        kernel.put(targetArray);
	        kernel.execute(range);
	        kernel.get(targetArray);
	        assertTrue(validate());
        } finally {
        	kernel.dispose();
        }
    }

    @Test
    public void testBarrier1JTP() {
    	SIZE = 256;
    	KernelManager.setKernelManager(new JTPKernelManager());
        Device device = KernelManager.instance().bestDevice();
        System.out.println("Executing on device: " + device.getShortDescription());
        assumeTrue (device != null && device instanceof JavaDevice);

        final Barrrier1Kernel kernel = new Barrrier1Kernel(SIZE);
        try {
	        final Range range = device.createRange(SIZE, SIZE);
	        targetArray = initInputArray();
	        kernel.setExplicit(false);
	        kernel.setArray(targetArray);
	        kernel.execute(range);
	        assertTrue(validate());
        } finally {
        	kernel.dispose();
        }
    }

    @Test
    public void testBarrier2() {
    	System.out.println("Executing on device: " + openCLDevice.getShortDescription() + " - " + openCLDevice.getName());
        final Barrrier2Kernel kernel = new Barrrier2Kernel(SIZE);
        try {
	        final Range range = openCLDevice.createRange(SIZE, SIZE);
	        targetArray = initInputArray();
	        kernel.setExplicit(false);
	        kernel.setArray(targetArray);
	        kernel.execute(range);
	        assertTrue(validate());
        } finally {
        	kernel.dispose();
        }
    }
    
    @Test
    public void testBarrier2Explicit() {
    	System.out.println("Executing on device: " + openCLDevice.getShortDescription() + " - " + openCLDevice.getName());
        final Barrrier2Kernel kernel = new Barrrier2Kernel(SIZE);
        try {
	        final Range range = openCLDevice.createRange(SIZE, SIZE);
	        targetArray = initInputArray();
	        kernel.setExplicit(true);
	        kernel.setArray(targetArray);
	        kernel.put(targetArray);
	        kernel.execute(range);
	        kernel.get(targetArray);
	        assertTrue(validate());
        } finally {
        	kernel.dispose();
        }
    }

    @Test
    public void testBarrier2JTP() {
    	SIZE = 256;
    	KernelManager.setKernelManager(new JTPKernelManager());
        Device device = KernelManager.instance().bestDevice();
        System.out.println("Executing on device: " + device.getShortDescription());
        assumeTrue (device != null && device instanceof JavaDevice);

        final Barrrier2Kernel kernel = new Barrrier2Kernel(SIZE);
        try {
	        final Range range = device.createRange(SIZE, SIZE);
	        targetArray = initInputArray();
	        kernel.setExplicit(false);
	        kernel.setArray(targetArray);
	        kernel.execute(range);
	        assertTrue(validate());
        } finally {
        	kernel.dispose();
        }
    }

    private int[] initInputArray() {
    	int[] inputArray = new int[SIZE];
    	for (int i = 0; i < SIZE; i++) {
    		inputArray[i] = i;
    	}
    	return inputArray;
    }
    
    private boolean validate() {
    	int[] inputArray = initInputArray();
        int[] expected = new int[SIZE];
        for (int threadId = 0; threadId < SIZE; threadId++) {
        	final int targetId = (SIZE - 1) - ((threadId + SIZE/2) % SIZE);
        	expected[targetId] += inputArray[threadId];
            for (int i = 0; i < SIZE; i++) {
            	expected[threadId] += i;
            }
        }

        int[] temp = expected;
    	expected = new int[SIZE];
        for (int threadId = 0; threadId < SIZE; threadId++) {
        	int targetId = ((threadId + SIZE/2) % SIZE);
        	expected[targetId] = temp[threadId];
        }

        for (int threadId = 0; threadId < SIZE; threadId++) {
	    	if (threadId < SIZE/2) {
	    		expected[threadId] += expected[(SIZE-1) - threadId];
	    	}
        }

        assertArrayEquals("targetArray", expected, targetArray);
        
        return true;
    }

    private static class Barrrier1Kernel extends Kernel {
    	private final int SIZE;
        private int[] resultArray;
        
        @Local
        private int[] myArray;
        
        private Barrrier1Kernel(int size) {
        	this.SIZE = size;
        	myArray = new int[size];
        }
        
        @NoCL
        public void setArray(int[] target) {
            resultArray = target;
        }

        private void doInitialCopy(@Local int[] target, int[] source, int id) {
        	int targetId = (SIZE - 1) - ((id + SIZE/2) % SIZE);
        	target[targetId] = source[id];
        }
        
        private void doComputation1(@Local int[] arr, int id) {
            for (int i = 0; i < SIZE; i++) {
                arr[id] += i;
            }
        }
        
        @Override
        public void run() {
            int id = getLocalId();                
                        
            doInitialCopy(myArray, resultArray, id);
            localBarrier();
            doComputation1(myArray, id);
            int targetId = ((id + SIZE/2) % SIZE);
            resultArray[targetId] = myArray[id];
            globalBarrier();
            if (id < SIZE/2) {
            	resultArray[id] += resultArray[(SIZE - 1) - id];
            }
        }
    }
    
    private static class Barrrier2Kernel extends Kernel {
    	private final int SIZE;
        private int[] resultArray;
        
        @Local
        private int[] myArray;
        
        private Barrrier2Kernel(int size) {
        	this.SIZE = size;
        	myArray = new int[size];
        }
        
        @NoCL
        public void setArray(int[] target) {
            resultArray = target;
        }

        private void doInitialCopy(@Local int[] target, int[] source, int id) {
        	int targetId = (SIZE - 1) - ((id + SIZE/2) % SIZE);
        	target[targetId] = source[id];
        }
        
        private void doComputation1(@Local int[] arr, int id) {
            for (int i = 0; i < SIZE; i++) {
                arr[id] += i;
            }
        }
        
        @Override
        public void run() {
            int id = getLocalId();                
                        
            doInitialCopy(myArray, resultArray, id);
            localGlobalBarrier();
            doComputation1(myArray, id);
            int targetId = ((id + SIZE/2) % SIZE);
            resultArray[targetId] = myArray[id];
            localGlobalBarrier();
            if (id < SIZE/2) {
            	resultArray[id] += resultArray[(SIZE - 1) - id];
            }
        }
    }

}
