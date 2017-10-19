/**
 * Copyright (c) 2016 - 2017 Syncleus, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aparapi.runtime;

import com.aparapi.Kernel;
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class UseStaticArrayTest {
    private static final Logger LOGGER = Logger.getLogger(UseStaticArrayTest.class);

    @Test
    public void test() {
        UseStaticArrayKernel k = new UseStaticArrayKernel();
        k.test();
    }

    protected static class UseStaticArrayKernel extends Kernel {
        private static final Logger LOGGER = Logger.getLogger(UseStaticArrayKernel.class);

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
//      for (int i = 0; i < size; i++) {
//         assertTrue("results == fooBar", results[i] == values[i]);
//      }
        }
    }
}
