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
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;


public class StaticVariableAssignmentTest {
    @Test
    @Ignore("Solution not implemented yet, will pass on cpu but not gpu")
    public void test() {
        Issue103Kernel b = new Issue103Kernel();
        b.test();
    }

    public static class Issue103Kernel extends Kernel {
        static final int size = 32;

        static int[] source = new int[size];
        static int[] target = new int[size];

        public Issue103Kernel() {
            for (int i = 0; i < size; ++i) {
                source[i] = 7;
                target[i] = 99;
            }
        }

        @Override
        public void run() {
            int id = getGlobalId();
            target[id] = source[id];
        }

        void validate() {
            assertArrayEquals("target == source", target, source);
//      for (int i = 0; i < size; i++) {
//         System.out.println(target[i] + " ... " + source[i]);
//         assertTrue("target == source", target[i] == source[i]);
//      }
        }

        public void test() {
            execute(size);
            validate();
        }
    }
}
