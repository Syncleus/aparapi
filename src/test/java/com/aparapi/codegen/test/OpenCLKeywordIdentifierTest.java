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

import com.aparapi.internal.model.ClassModel;
import com.aparapi.internal.model.Entrypoint;
import com.aparapi.internal.writer.KernelWriter;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OpenCLKeywordIdentifierTest {
    @Test
    public void OpenCLKeywordIdentifierTest() throws Exception {
        ClassModel classModel = ClassModel.createClassModel(OpenCLKeywordIdentifier.class);
        Entrypoint entrypoint = classModel.getEntrypoint("run", null);
        String actual = KernelWriter.writeToString(entrypoint);

        assertTrue(actual.contains("__global int *aparapi_constant"));
        assertTrue(actual.contains("this->aparapi_constant = aparapi_constant"));
        assertTrue(actual.contains("int aparapi_kernel = 0"));
        assertTrue(actual.contains("int aparapi_global = aparapi_kernel + 1"));
        assertTrue(actual.contains("this->aparapi_constant[aparapi_kernel]  = aparapi_global"));

        assertFalse(actual.contains("__global int *constant"));
        assertFalse(actual.contains("int kernel = 0"));
        assertFalse(actual.contains("int global ="));
    }

    @Test
    public void OpenCLKeywordIdentifierTestWorksWithCaching() throws Exception {
        OpenCLKeywordIdentifierTest();
    }
}
