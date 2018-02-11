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

    /** test all sizes to ensure that all the possible divisors (even or not)
     *  for the available # of threads/cores are tested */
    @Test public void test_n() {
        for (int size = 2; size < 33; size++)
            new VectorKernel(size).test();
    }

    @Test public void test_1() {
        new VectorKernel(1).test();
    }
    @Test public void test_2() {
        new VectorKernel(2).test();
    }

    public static class VectorKernel extends Kernel {
        private final int SIZE;

        final int[][] target;

        public VectorKernel(int size) {
            this.SIZE = size;
            this.target = new int[SIZE][SIZE];
            for (int i = 0; i < SIZE; ++i) {
                int[] ints = new int[SIZE];
                Arrays.fill(ints, 99);

                target[i] = ints;
            }
        }

        private void fillArray(int[] ints_$private$, int id) {
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
            int[] expected = new int[SIZE];
            for (int j = 0; j < SIZE; j++) {

                for (int i = 0; i < SIZE; i++) {
                    expected[i] = i + j;
                }

                assertArrayEquals("size=" + SIZE + "\ttarget["+j+ ']', expected, target[j]);
            }
        }

        public void test() {
            execute(SIZE);
            validate();
        }
    }
}
