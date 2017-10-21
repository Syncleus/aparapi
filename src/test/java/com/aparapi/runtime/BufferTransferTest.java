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
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

public class BufferTransferTest {

    static OpenCLDevice openCLDevice = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        Device device = KernelManager.instance().bestDevice();
        assumeTrue (device != null && device instanceof OpenCLDevice);
        openCLDevice = (OpenCLDevice) device;
    }

    @Test
    public void inOutOnce() {

        final int SIZE = 1024;
        final InOutKernel kernel = new InOutKernel();
        final Range range = openCLDevice.createRange(SIZE);

        kernel.in = new int[SIZE];
        kernel.out = new int[SIZE];

        Util.fill(kernel.in, new Util.Filler() {
            public void fill(int[] array, int index) {
                array[index] = index;
            }
        });
        kernel.execute(range);

        assertTrue("in == out", Util.same(kernel.in, kernel.out));

    }

    @Test
    public void auto() {

        final int SIZE = 1024;
        final AddKernel kernel = new AddKernel();
        final Range range = openCLDevice.createRange(SIZE);

        kernel.values = new int[SIZE];
        kernel.result = new int[SIZE];
        Util.zero(kernel.result);
        Util.fill(kernel.values, new Util.Filler() {
            public void fill(int[] array, int index) {
                array[index] = index;
            }
        });

        int[] expectedResult = Arrays.copyOf(kernel.result, kernel.result.length);

        Util.apply(expectedResult, kernel.values, new Util.Operator() {

            @Override
            public void apply(int[] lhs, int[] rhs, int index) {
                lhs[index] = lhs[index] + rhs[index];

            }
        });
        kernel.execute(range);

        assertTrue("expectedResult == result", Util.same(expectedResult, kernel.result));

        kernel.execute(range);

        Util.apply(expectedResult, kernel.values, new Util.Operator() {

            @Override
            public void apply(int[] lhs, int[] rhs, int index) {
                lhs[index] = lhs[index] + rhs[index];

            }
        });
        assertTrue("expectedResult == result", Util.same(expectedResult, kernel.result));

        Util.zero(kernel.values);
        kernel.execute(range);
        assertTrue("expectedResult == result", Util.same(expectedResult, kernel.result));

    }

    @Test
    public void explicit() {

        final int SIZE = 1024;
        final AddKernel kernel = new AddKernel();
        kernel.setExplicit(true);
        final Range range = openCLDevice.createRange(SIZE);

        kernel.values = new int[SIZE];
        kernel.result = new int[SIZE];
        Util.zero(kernel.result);
        Util.fill(kernel.values, new Util.Filler() {
            public void fill(int[] array, int index) {
                array[index] = index;
            }
        });

        int[] expectedResult = Arrays.copyOf(kernel.result, kernel.result.length);

        Util.apply(expectedResult, kernel.values, new Util.Operator() {

            @Override
            public void apply(int[] lhs, int[] rhs, int index) {
                lhs[index] = lhs[index] + rhs[index];

            }
        });

        kernel.execute(range).get(kernel.result);

        assertTrue("after first explicit add expectedResult == result", Util.same(expectedResult, kernel.result));

        kernel.execute(range).get(kernel.result);

        Util.apply(expectedResult, kernel.values, new Util.Operator() {
            @Override
            public void apply(int[] lhs, int[] rhs, int index) {
                lhs[index] = lhs[index] + rhs[index];

            }
        });
        assertTrue("after second explicit add expectedResult == result", Util.same(expectedResult, kernel.result));

        Util.zero(kernel.values);

        kernel.put(kernel.values).execute(range).get(kernel.result);

        assertTrue("after zeroing values and third explici add expectedResult == result", Util.same(expectedResult, kernel.result));

        Util.zero(kernel.result);

        kernel.put(kernel.result).execute(range).get(kernel.result);

        Util.zero(expectedResult);

        assertTrue("after zeroing values and result and forth  explicit add expectedResult == result",
            Util.same(expectedResult, kernel.result));

    }

    @Test
    public void issue60Explicit() {

        TestKernel kernel = new TestKernel();
        kernel.setExplicit(true);
        kernel.step();

    }

    @Test
    public void issue60Auto() {
        TestKernel kernel = new TestKernel();
        kernel.step();

    }

    public static class InOutKernel extends Kernel {

        int[] in;

        int[] out;

        @Override
        public void run() {
            int gid = getGlobalId(0);
            in[gid] = out[gid];

        }

    }

    public static class AddKernel extends Kernel {

        int[] values;

        int[] result;

        @Override
        public void run() {
            int gid = getGlobalId(0);
            result[gid] = result[gid] + values[gid];

        }

    }

    private class TestKernel extends Kernel {
        int[] simStep = new int[1];

        int[] neuronOutputs = new int[3];

        int[] expected = new int[]{
            3,
            0,
            0,
            0,
            3,
            0,
            0,
            0,
            3,
            0,
            0,
            0,
            3,
            0,
            0,
            0
        };

        public void step() {
            int simSteps = 16;
            int[][] log = new int[neuronOutputs.length][simSteps];
            put(neuronOutputs);
            for (simStep[0] = 0; simStep[0] < simSteps; simStep[0]++) {
                put(simStep).execute(neuronOutputs.length).get(neuronOutputs);
                for (int n = 0; n < neuronOutputs.length; n++)
                    log[n][simStep[0]] = neuronOutputs[n];
            }
            System.out.println(getTargetDevice().getShortDescription() + (isExplicit() ? ", explicit" : ", auto"));

            for (int n = 0; n < neuronOutputs.length; n++)
                System.out.println(Arrays.toString(log[n]));

            assertTrue("log[2] == expected", Util.same(log[2], expected));
        }

        @Override
        public void run() {
            int neuronID = getGlobalId();
            neuronOutputs[neuronID] = (simStep[0] % (neuronID + 2) == 0) ? (neuronID + 1) : 0;
        }
    }

}
