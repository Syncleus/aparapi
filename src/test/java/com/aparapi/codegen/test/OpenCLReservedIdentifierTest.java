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

public class OpenCLReservedIdentifierTest extends com.aparapi.codegen.CodeGenJUnitBase {

    private static final String[] expectedOpenCL = {
        "typedef struct This_s{\n" +
        " __global int *global__javaKeyword;\n" +
        " int passid;\n" +
        " }This;\n" +
        " int get_pass_id(This *this){\n" +
        " return this->passid;\n" +
        " }\n" +
        " int com_aparapi_codegen_test_OpenCLReservedIdentifier__add(This *this, int kernel__javaKeyword){\n" +
        " int local__javaKeyword = kernel__javaKeyword + 1;\n" +
        " return(local__javaKeyword);\n" +
        " }\n" +
        " __kernel void run(\n" +
        " __global int *global__javaKeyword, \n" +
        " int passid\n" +
        " ){\n" +
        " This thisStruct;\n" +
        " This* this=&thisStruct;\n" +
        " this->global__javaKeyword = global__javaKeyword;\n" +
        " this->passid = passid;\n" +
        " {\n" +
        " int event_t__javaKeyword = get_global_id(0);\n" +
        " int queue_t__javaKeyword = com_aparapi_codegen_test_OpenCLReservedIdentifier__add(this, event_t__javaKeyword);\n" +
        " this->global__javaKeyword[event_t__javaKeyword]  = queue_t__javaKeyword;\n" +
        " return;\n" +
        " }\n" +
        " }"
    };
    private static final Class<? extends com.aparapi.internal.exception.AparapiException> expectedException = null;

    @Test
    public void OpenCLReservedIdentifierTest() {
        test(com.aparapi.codegen.test.OpenCLReservedIdentifier.class, expectedException, expectedOpenCL);
    }

    @Test
    public void OpenCLReservedIdentifierTestWorksWithCaching() {
        test(com.aparapi.codegen.test.OpenCLReservedIdentifier.class, expectedException, expectedOpenCL);
    }
}
