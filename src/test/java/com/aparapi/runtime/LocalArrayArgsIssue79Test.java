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

import static org.junit.Assert.assertArrayEquals;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.KernelManager;
import static org.junit.Assume.*;
import org.junit.BeforeClass;
import org.junit.Test;

public class LocalArrayArgsIssue79Test {
    static OpenCLDevice openCLDevice = null;
    private static final int SIZE = 32;
    private int[] targetArray;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Device device = KernelManager.instance().bestDevice();
        assumeTrue (device != null && device instanceof OpenCLDevice);
        openCLDevice = (OpenCLDevice) device;
    }

    @Test
    public void test() {
        final LocalArrayArgsKernel kernel = new LocalArrayArgsKernel();
        final Range range = openCLDevice.createRange(SIZE, SIZE);
        targetArray = new int[SIZE];
        kernel.setExplicit(false);
        kernel.setArray(targetArray);
        kernel.execute(range);
        validate();
    }
    
    @Test
    public void testExplicit() {
        final LocalArrayArgsKernel kernel = new LocalArrayArgsKernel();
        final Range range = openCLDevice.createRange(SIZE, SIZE);
        targetArray = new int[SIZE];
        kernel.setExplicit(true);
        kernel.setArray(targetArray);
        kernel.put(targetArray);
        kernel.execute(range);
        kernel.get(targetArray);
        validate();
    }

    void validate() {
        int[] expected = new int[SIZE];
        for (int threadId = 0; threadId < SIZE; threadId++) {
            for (int i = 0; i < SIZE; i++) {
                expected[threadId] += i + threadId;
            }
        	expected[threadId] *= threadId;
        }
        
        assertArrayEquals("destArray", expected, targetArray);
    }

    public static class LocalArrayArgsKernel extends Kernel {
        private int[] destArray;
        
        @Local
        private int[] myArray = new int[SIZE];

        public LocalArrayArgsKernel() {
        }
        
        @NoCL
        public void setArray(int[] target) {
            destArray = target;
        }

        private void doComputation1(@Local int[] arr, int id) {
            for (int i = 0; i < SIZE; i++) {
                arr[id] += i + id;
            }
        }

        private void doComputation2(int[] arr_$local$, int id) {
            arr_$local$[id] *= id;
        }

        @Override
        public void run() {
            int id = getLocalId();
            
            myArray[id] = destArray[id];

            doComputation1(myArray, id);
            doComputation2(myArray, id);
            
            destArray[id] = myArray[id];            
        }
    }
}
