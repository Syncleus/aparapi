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

public class NullRefTest {
    @Ignore("Known bug, runs on CPU but not GPU.")
    @Test
    public void test() {
        new NullRefTest().doTest();
    }

    private void doTest() {
        final Kernel kernel = new NullRefKernel();
        kernel.execute(1);
    }

    private class NullRefKernel extends Kernel {
        private final int[] nullArray = null;

        @Override
        public void run() {
            if(nullArray == null) {
                noop();
            }
        }

        private void noop() {
            //no op
        }
    }
}
