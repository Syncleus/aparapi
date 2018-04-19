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
import com.aparapi.device.Device;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class StaticArrayAssignmentTest {
    @Test
    @Ignore("Solution not implemented yet, will pass on cpu but not gpu")
    public void test() {
        UseStaticArrayKernel k = new UseStaticArrayKernel();
        k.test();
    }

    protected static class UseStaticArrayKernel extends Kernel {

        static final int size = 256;

        static final int[] values = new int[size];

        static final int[] results = new int[size];

        @Override
        public void run() {
            int gid = getGlobalId();
            results[gid] = values[gid];
        }

        @Test
        public void test() {

            for (int i = 0; i < size; i++) {
                values[i] = i;
                results[i] = 0;
            }

            execute(size);

            assertArrayEquals("results == fooBar", results, values);
        }
    }
}
