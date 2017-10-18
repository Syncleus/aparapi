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
import java.util.Arrays;
import org.junit.Test;


public class ArrayTest {
    @Test
    public void test() {
        VectorKernel b = new VectorKernel();
        b.test();
    }

    public static class VectorKernel extends Kernel {
        private static final int SIZE = 32;

        static int[][] target = new int[SIZE][SIZE];

        public VectorKernel() {
            for (int i = 0; i < SIZE; ++i) {
                int[] ints = new int[SIZE];
                Arrays.fill(ints, 99);

                target[i] = ints;
            }
        }

        private static void fillArray(int[] ints_$private$, int id) {
            for (int i = 0; i < SIZE; i++) {
                ints_$private$[i] = i + id;
            }
        }

        @Override
        public void run() {
            int id = getGlobalId();

            int[] ints = new int[SIZE];

            fillArray(ints, id);

            for (int i = 0; i < SIZE; i++) {
                target[id][i] = ints[i];
            }
        }

        void validate() {
            for (int j = 0; j < SIZE; j++) {

                int[] expected = new int[SIZE];
                for (int i = 0; i < SIZE; i++) {
                    expected[i] = i + j;
                }

                assertArrayEquals("target["+j+"]", expected, target[j]);
            }
        }

        public void test() {
            execute(SIZE);
            validate();
        }
    }
}
