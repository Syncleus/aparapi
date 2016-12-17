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
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ExplicitBoolean {

    private static void printArray(boolean[] a) {
        for (int i = 0; i < a.length; i++) {
            System.out.print((a[i] ? 1 : 0) + "\t");
        }
        System.out.println();
    }

    @Test
    public void test() {
        int size = 16;
        ExplicitBooleanTestKernel k1 = new ExplicitBooleanTestKernel(size);
        ExplicitBooleanTestKernel k2 = new ExplicitBooleanTestKernel(size);
        k2.input = k1.output;

        for (int i = 0; i < size; i++) {
            k1.input[i] = Math.random() > 0.5;
        }

        if (size <= 32)
            printArray(k1.input);

        k1.go();

        if (size <= 32)
            printArray(k1.output);

        assertTrue("k1.input == k1.output ", Util.same(k1.output, k1.output));

        k2.go();

        if (size <= 32)
            printArray(k2.output);

        assertTrue("k1.input == k2.input", Util.same(k1.output, k1.output));
        System.out.println(k1.getTargetDevice().getShortDescription());
    }

    class ExplicitBooleanTestKernel extends Kernel {
        public boolean[] input, output;
        int size; // Number of work items.
        int iterations; // Number of times to execute kernel.

        public ExplicitBooleanTestKernel(int _size) {
            size = _size;
            input = new boolean[size];
            output = new boolean[size];
            setExplicit(true);
            put(output);
        }

        public void go() {
            put(input);
            execute(size);
            get(output);
        }

        @Override
        public void run() {
            int id = getGlobalId();
            output[id] = input[id];
        }
    }

}
