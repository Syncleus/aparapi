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
package com.aparapi.codegen.test;

import static org.junit.Assert.assertTrue;

import com.aparapi.internal.model.ClassModel;
import com.aparapi.internal.model.Entrypoint;
import com.aparapi.internal.writer.KernelWriter;
import org.junit.Test;

public class AssignAndPassAsParameterSimpleTest extends com.aparapi.codegen.CodeGenJUnitBase {

    private String generateOpenCL() throws Exception {
        Class<?> testClass = com.aparapi.codegen.test.AssignAndPassAsParameterSimple.class;
        ClassModel classModel = ClassModel.createClassModel(testClass);
        Object kernelInstance = testClass.getConstructor((Class<?>[]) null).newInstance();
        Entrypoint entrypoint = classModel.getEntrypoint("run", null);
        return KernelWriter.writeToString(entrypoint);
    }

    @Test
    public void AssignAndPassAsParameterSimpleTest() throws Exception {
        String actual = generateOpenCL();
        assertTrue(actual.contains("int z;"));
        assertTrue(actual.contains("z=1"));
        assertTrue(actual.indexOf("int z;") < actual.indexOf("z=1"));
    }

    @Test
    public void AssignAndPassAsParameterSimpleTestWorksWithCaching() throws Exception {
        String actual = generateOpenCL();
        assertTrue(actual.contains("int z;"));
        assertTrue(actual.contains("z=1"));
    }
}
