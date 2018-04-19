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
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class AccessGetterSetterTest {

    @Test
    public void test() {
        Issue102Kernel b = new Issue102Kernel();
        b.test();
    }

    protected static class Issue102Kernel extends Kernel {
        private static final int SIZE = 32;

        private static BugDataObject[] objects = new BugDataObject[SIZE];
        int[] target = new int[SIZE];

        public Issue102Kernel() {
            for (int i = 0; i < SIZE; ++i) {
                objects[i] = new BugDataObject();
                target[i] = 99;
            }
        }

        @Override
        public void run() {
            int id = getGlobalId();
            target[id] = objects[id].getValue();
        }

        void validate() {
            for (int i = 0; i < SIZE; i++) {
                assertTrue("target == objects", target[i] == objects[i].getValue());
            }
        }

        public void test() {
            execute(SIZE);
            validate();
        }

        static final class BugDataObject {
            int value = 7;

            public int getValue() {
                return value;
            }

            public void setValue(int value) {
                this.value = value;
            }
        }
    }
}
