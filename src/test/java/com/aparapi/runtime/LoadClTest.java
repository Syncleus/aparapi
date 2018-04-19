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

import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.KernelManager;
import com.aparapi.opencl.OpenCL;
import com.aparapi.opencl.OpenCL.Resource;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class LoadClTest {

    @Test
    public void test() {
        final int size = 32;
        final float[] in = new float[size];

        for (int i = 0; i < size; i++) {
            in[i] = i;
        }

        final float[] squares = new float[size];
        final float[] quads = new float[size];
        final Range range = Range.create(size);

        final Device device = KernelManager.instance().bestDevice();

        if (device instanceof OpenCLDevice) {
            final OpenCLDevice openclDevice = (OpenCLDevice) device;

            final Squarer squarer = openclDevice.bind(Squarer.class);
            squarer.square(range, in, squares);

            for (int i = 0; i < size; i++) {
                assertTrue("in[" + i + "] * in[" + i + "] = in[" + i + "]^2", in[i] * in[i] == squares[i]);
            }

            squarer.square(range, squares, quads);

            for (int i = 0; i < size; i++) {
                assertTrue("in[" + i + "]^2 * in[" + i + "]^2 = in[" + i + "]^4", in[i] * in[i] * in[i] * in[i] == quads[i]);
            }
        }
    }

    @Resource("squarer.cl")
    interface Squarer extends OpenCL<Squarer> {
        public Squarer square(
                              Range _range,
                              @GlobalReadWrite("in") float[] in,//
                              @GlobalReadWrite("out") float[] out);
    }
}

