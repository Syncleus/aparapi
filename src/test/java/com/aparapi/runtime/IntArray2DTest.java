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

import static org.junit.Assert.*;
import static org.junit.Assume.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IntArray2DTest {
    private static OpenCLDevice openCLDevice = null;
    private int[] targetArray;

    @Before
    public void setUpBeforeClass() throws Exception {
        Device device = KernelManager.instance().bestDevice();
        assumeTrue (device != null && device instanceof OpenCLDevice);
        openCLDevice = (OpenCLDevice) device;
    }

    @After
    public void classTeardown() {
        Util.resetKernelManager();
    }
    

    @Test
    public void test() {
        int size = 128;
        final int count = 3;
        final int[][] V = new int[count][size];
        final int[][] totals = new int[count][size];

        //lets fill in V randomly...
        for (int j = 0; j < count; j++) {
            for (int i = 0; i < size; i++) {
                //test number either 0, 1, or 2
                totals[j][i] = V[j][i] = (i + j) % 3;
            }
        }

        final Kernel kernel = new Kernel() {
                @Override
                public void run() {
                    int gid = getGlobalId();
                    for(int index = 0; index < count; index++) {
                        totals[index][gid] += gid + 3;
                    }
                }
            };
            
        final Range range = openCLDevice.createRange(size);
        try {
            kernel.execute(range);

            for (int index = 0; index < count; index++) {
                for (int gid = 0; gid < size; gid++) {
                    assertEquals("Testing for index: " + index + " and gid: " + gid, V[index][gid] + gid + 3, totals[index][gid]);
                }
            }
        } finally {
            kernel.dispose();
        }
    }
}
