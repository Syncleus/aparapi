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
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class StaticArrayAssignmentEntrypointTest {
    @Test
    public void staticArrayReadsAndWritesAreTracked() throws Exception {
        ClassModel classModel = ClassModel.createClassModel(StaticArrayAssignmentKernel.class);
        Entrypoint entrypoint = classModel.getEntrypoint("run", new StaticArrayAssignmentKernel());

        assertTrue(entrypoint.getArrayFieldAssignments().contains("target"));
        assertTrue(entrypoint.getArrayFieldAccesses().contains("source"));
    }

    public static class StaticArrayAssignmentKernel extends Kernel {
        static final int size = 32;

        static int[] source = new int[size];
        static int[] target = new int[size];

        @Override
        public void run() {
            int id = getGlobalId();
            target[id] = source[id];
        }
    }
}
