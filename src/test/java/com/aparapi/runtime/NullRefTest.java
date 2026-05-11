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
import com.aparapi.internal.model.ClassModel;
import com.aparapi.internal.model.Entrypoint;
import com.aparapi.internal.writer.KernelWriter;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class NullRefTest {
    @Test
    public void test() {
        new NullRefTest().doTest();
    }

    @Test
    public void finalNullArrayIsInlinedInGeneratedOpenCL() throws Exception {
        NullRefKernel kernel = new NullRefKernel();
        ClassModel classModel = ClassModel.createClassModel(kernel.getClass());
        Entrypoint entrypoint = classModel.getEntrypoint("run", kernel);

        String openCL = KernelWriter.writeToString(entrypoint);
        assertTrue(entrypoint.canInlineNullArrayField("nullArray"));
        assertTrue(openCL.contains("if (NULL == NULL)"));
        assertTrue(!openCL.contains("nullArray"));
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
