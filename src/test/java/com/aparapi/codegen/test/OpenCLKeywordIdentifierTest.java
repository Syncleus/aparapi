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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.aparapi.Kernel;
import com.aparapi.internal.model.ClassModel;
import com.aparapi.internal.model.Entrypoint;
import com.aparapi.internal.writer.KernelWriter;

public class OpenCLKeywordIdentifierTest {

    @Test
    public void OpenCLKeywordIdentifierTest() throws Exception {
        ClassModel classModel = ClassModel.createClassModel(OpenCLKeywordIdentifier.class);
        Object kernelInstance = OpenCLKeywordIdentifier.class.getConstructor((Class<?>[]) null).newInstance();
        Entrypoint entrypoint = classModel.getEntrypoint("run", kernelInstance instanceof Kernel ? kernelInstance : null);
        String openCL = KernelWriter.writeToString(entrypoint);

        assertTrue(openCL, openCL.contains("__global int *global_"));
        assertTrue(openCL, openCL.contains("this->global_ = global_"));
        assertTrue(openCL, openCL.contains("int kernel_"));
        assertTrue(openCL, openCL.contains("int local_ = kernel_ + 1"));
        assertTrue(openCL, openCL.contains("return(local_)"));

        assertFalse(openCL, openCL.contains("__global int *global,"));
        assertFalse(openCL, openCL.contains("this->global = global"));
        assertFalse(openCL, openCL.contains("int kernel)"));
        assertFalse(openCL, openCL.contains("int local = kernel + 1"));
    }
}
