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

import java.lang.reflect.Field;

import static org.junit.Assert.assertTrue;

public class PrivateSuperclassFieldTest {

    @Test
    public void privateSuperclassFieldsAreIncludedInThisStruct() throws Exception {
        final ClassModel classModel = ClassModel.createClassModel(ChildKernel.class);
        final Entrypoint entrypoint = classModel.getEntrypoint("run", new ChildKernel());
        final String opencl = KernelWriter.writeToString(entrypoint);

        assertTrue(hasReferencedField(entrypoint, ParentKernel.class, "values"));
        assertTrue(hasReferencedField(entrypoint, ParentKernel.class, "scale"));
        assertTrue(opencl, opencl.contains("__global int *values;"));
        assertTrue(opencl, opencl.contains("int scale;"));
    }

    private static boolean hasReferencedField(Entrypoint entrypoint, Class<?> declaringClass, String fieldName) {
        for (final Field field : entrypoint.getReferencedFields()) {
            if (field.getDeclaringClass().equals(declaringClass) && field.getName().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }

    public abstract static class ParentKernel extends Kernel {
        private int[] values = new int[1];
        private int scale = 7;

        protected void updateValue(int id) {
            values[id] = scale;
        }
    }

    public static class ChildKernel extends ParentKernel {
        @Override
        public void run() {
            updateValue(getGlobalId());
        }
    }
}
