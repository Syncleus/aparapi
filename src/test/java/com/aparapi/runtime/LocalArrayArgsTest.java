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
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.KernelManager;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

import org.junit.Before;
import org.junit.Test;

public class LocalArrayArgsTest {
    private static OpenCLDevice openCLDevice = null;
    private static final int SIZE = 32;
    private int[] targetArray;

    @Before
    public void setUpBeforeClass() throws Exception {
        Device device = KernelManager.instance().bestDevice();
        assumeTrue (device != null && device instanceof OpenCLDevice);
        openCLDevice = (OpenCLDevice) device;
    }

    @Test
    public void test() {
        final LocalArrayArgsKernel kernel = new LocalArrayArgsKernel();
        try {
	        final Range range = openCLDevice.createRange(SIZE, SIZE);
	        targetArray = new int[SIZE];
	        kernel.setExplicit(false);
	        kernel.setArray(targetArray);
	        kernel.execute(range);
	        assertTrue(validate());
        } finally {
        	kernel.dispose();
        }
    }
    
    @Test
    public void testExplicit() {
        final LocalArrayArgsKernel kernel = new LocalArrayArgsKernel();
        try {
	        final Range range = openCLDevice.createRange(SIZE, SIZE);
	        targetArray = new int[SIZE];
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

    private boolean validate() {
        int[] expected = new int[SIZE];
        for (int threadId = 0; threadId < SIZE; threadId++) {
            for (int i = 0; i < SIZE; i++) {
                expected[threadId] += i + threadId;
            }
        	expected[threadId] *= threadId;
        }
        
        assertArrayEquals("targetArray", expected, targetArray);
        
        return true;
    }

    public static class LocalArrayArgsKernel extends Kernel {
        private int[] resultArray;
        
        @Local
        private int[] myArray = new int[SIZE];
        
        @PrivateMemorySpace(SIZE)
        private int[] other_$private$ = new int[SIZE];

        @NoCL
        public void setArray(int[] target) {
            resultArray = target;
        }

        private void doInitialCopy(@Local int[] target, int[] source, int id) {
        	target[id] = source[id];
        }
        
        private void doComputation1(@Local int[] arr, int id) {
            for (int i = 0; i < SIZE; i++) {
                arr[id] += i + id;
            }
        }

        private void doComputation2(int[] arr_$local$, int id) {
            arr_$local$[id] *= id;
        }
        
        private void doComputation3(int[] arr_$local$, int[] arr_$private$, int id) {
        	arr_$private$[id] = arr_$local$[id];
        }

        @Override
        public void run() {
            int id = getLocalId();                
            
            
            doInitialCopy(myArray, resultArray, id);
            doComputation1(myArray, id);
            doComputation2(myArray, id);
            doComputation3(myArray, other_$private$, id);
            
            resultArray[id] = myArray[id];            
        }
    }
}
