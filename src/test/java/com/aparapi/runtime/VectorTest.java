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

import static org.junit.Assert.assertEquals;

import com.aparapi.Kernel;
import com.aparapi.opencl.vector.Float2;
import java.util.Arrays;
import org.junit.Test;


public class VectorTest {
    @Test
    public void test() {
        VectorKernel b = new VectorKernel();
        b.test();
    }

    public static class VectorKernel extends Kernel {
        private static final int SIZE = 32;

        final Float2[] target = new Float2[SIZE];

        VectorKernel() {
            for (int i = 0; i < SIZE; ++i) {
                target[i] = Float2.create();
            }
        }

        @Override
        public void run() {
            int id = getGlobalId();

            final Float2 v = Float2.create(id, id * 2);
            target[id] = v;
        }

        void validate() {
            for (int i = 0; i < SIZE; i++) {

                Float2 v = target[i];

                assertEquals(i, v.x, 0.01f);
                assertEquals(i * 2, v.y, 0.01f);
            }
        }

        public void test() {
            execute(SIZE);
            validate();
        }
    }
}
