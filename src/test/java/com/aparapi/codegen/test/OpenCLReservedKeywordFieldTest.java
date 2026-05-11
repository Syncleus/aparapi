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

import org.junit.Test;

public class OpenCLReservedKeywordFieldTest extends com.aparapi.codegen.CodeGenJUnitBase {
    private static final String[] expectedOpenCL = {"typedef struct This_s{\n" +
        "   __local float *local__java;\n" +
        "   __global float *result;\n" +
        "   int passid;\n" +
        "}This;\n" +
        "int get_pass_id(This *this){\n" +
        "   return this->passid;\n" +
        "}\n" +
        "__kernel void run(\n" +
        "   __local float *local__java,\n" +
        "   __global float *result,\n" +
        "   int passid\n" +
        "){\n" +
        "   This thisStruct;\n" +
        "   This* this=&thisStruct;\n" +
        "   this->local__java = local__java;\n" +
        "   this->result = result;\n" +
        "   this->passid = passid;\n" +
        "   {\n" +
        "      int row = get_global_id(0);\n" +
        "      this->local__java[row]  = (float)row;\n" +
        "      barrier(CLK_LOCAL_MEM_FENCE);\n" +
        "      this->result[0]  = this->local__java[row];\n" +
        "      return;\n" +
        "   }\n" +
        "}\n"};
    private static final Class<? extends com.aparapi.internal.exception.AparapiException> expectedException = null;

    @Test
    public void OpenCLReservedKeywordFieldTest() {
        test(com.aparapi.codegen.test.OpenCLReservedKeywordField.class, expectedException, expectedOpenCL);
    }

    @Test
    public void OpenCLReservedKeywordFieldTestWorksWithCaching() {
        test(com.aparapi.codegen.test.OpenCLReservedKeywordField.class, expectedException, expectedOpenCL);
    }
}
