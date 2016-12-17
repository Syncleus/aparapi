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
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CallStaticFromAnonymousKernelTest {

    static final int size = 256;

    // This method is a static target in the anonymous
    // kernel's containing class
    public static int fooBar(int i) {
        return i + 20;
    }

    public static void main(String args[]) {
        CallStaticFromAnonymousKernelTest k = new CallStaticFromAnonymousKernelTest();
        k.test();
    }

    @Test
    public void test() {
        final int[] values = new int[size];
        final int[] results = new int[size];
        for (int i = 0; i < size; i++) {
            values[i] = i;
            results[i] = 0;
        }
        Kernel kernel = new Kernel() {

            // Verify codegen for resolving static call from run's callees
            public int doodoo(int i) {
                return AnotherClass.foo(i);
            }

            @Override
            public void run() {
                int gid = getGlobalId();
                // Call a static in the containing class and call a kernel method
                // that calls a static in another class
                results[gid] = CallStaticFromAnonymousKernelTest.fooBar(values[gid]) + doodoo(gid);
            }
        };
        kernel.execute(size);

        for (int i = 0; i < size; i++) {
            assertTrue("results == fooBar", results[i] == (fooBar(values[i]) + AnotherClass.foo(i)));
        }
    }

    static class AnotherClass {
        static public int foo(int i) {
            return i + 42;
        }
    }
}
