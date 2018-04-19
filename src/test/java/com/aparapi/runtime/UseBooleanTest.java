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

public class UseBooleanTest {
    @Ignore("Known bug not currently fixed on GPU, works on CPU")
    @Test
    public void test() {
        new UseBooleanTest().executeTest();
    }

    private void executeTest() {
        final Kernel kernel = new BooleanKernel();
        kernel.execute(1);
    }

    private class BooleanKernel extends Kernel {
        private boolean isInverse = true;

        @Override
        public void run() {
            if (isInverse) {
                noop();
            }
        }

        private void noop() {
            //no op
        }
    }
}
