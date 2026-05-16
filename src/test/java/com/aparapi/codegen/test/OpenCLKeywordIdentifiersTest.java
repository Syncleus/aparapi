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

public class OpenCLKeywordIdentifiersTest extends com.aparapi.codegen.CodeGenJUnitBase {
    private static final String[] expectedOpenCL = {"typedef struct This_s{\n" +
        "   int global__javaReserved;\n" +
        "   __global int *constant__javaReserved;\n" +
        "   int passid;\n" +
        "}This;\n" +
        "int get_pass_id(This *this){\n" +
        "   return this->passid;\n" +
        "}\n" +
        "int com_aparapi_codegen_test_OpenCLKeywordIdentifiers__helper(This *this, int kernel__javaReserved){\n" +
        "   int local__javaReserved = kernel__javaReserved + this->global__javaReserved;\n" +
        "   int read_only__javaReserved = local__javaReserved + 1;\n" +
        "   return(read_only__javaReserved);\n" +
        "}\n" +
        "__kernel void run(\n" +
        "   int global__javaReserved, \n" +
        "   __global int *constant__javaReserved, \n" +
        "   int passid\n" +
        "){\n" +
        "   This thisStruct;\n" +
        "   This* this=&thisStruct;\n" +
        "   this->global__javaReserved = global__javaReserved;\n" +
        "   this->constant__javaReserved = constant__javaReserved;\n" +
        "   this->passid = passid;\n" +
        "   {\n" +
        "      this->constant__javaReserved[0]  = com_aparapi_codegen_test_OpenCLKeywordIdentifiers__helper(this, this->global__javaReserved);\n" +
        "      return;\n" +
        "   }\n" +
        "}\n" +
        "\n"};
    private static final Class<? extends com.aparapi.internal.exception.AparapiException> expectedException = null;

    @Test
    public void OpenCLKeywordIdentifiersTest() {
        test(com.aparapi.codegen.test.OpenCLKeywordIdentifiers.class, expectedException, expectedOpenCL);
    }

    @Test
    public void OpenCLKeywordIdentifiersTestWorksWithCaching() {
        test(com.aparapi.codegen.test.OpenCLKeywordIdentifiers.class, expectedException, expectedOpenCL);
    }
}
