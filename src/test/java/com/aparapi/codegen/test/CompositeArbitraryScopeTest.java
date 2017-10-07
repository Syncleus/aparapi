/**
 * Copyright (c) 2016 - 2017 Syncleus, Inc.
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

public class CompositeArbitraryScopeTest extends com.aparapi.codegen.CodeGenJUnitBase {
    private static final String[] expectedOpenCL = {
        "typedef struct This_s{\n" +
"\n" +
" int passid;\n" +
" }This;\n" +
" int get_pass_id(This *this){\n" +
" return this->passid;\n" +
" }\n" +
"\n" +
" void com_aparapi_codegen_test_CompositeArbitraryScope__t5(This *this){\n" +
" int gid = get_global_id(0);\n" +
" int numRemaining = 1;\n" +
" int thisCount = 0;\n" +
" for (; numRemaining>0 && gid>0; numRemaining++){\n" +
" numRemaining++;\n" +
" thisCount = min(numRemaining, gid);\n" +
" numRemaining = numRemaining - thisCount;\n" +
" }\n" +
" gid = gid - thisCount;\n" +
" return;\n" +
" }\n" +
" void com_aparapi_codegen_test_CompositeArbitraryScope__t4(This *this){\n" +
" int gid = get_global_id(0);\n" +
" int numRemaining = 1;\n" +
" while (numRemaining>0 && gid>0){\n" +
" numRemaining++;\n" +
" {\n" +
" int thisCount = min(numRemaining, gid);\n" +
" numRemaining = numRemaining - thisCount;\n" +
" numRemaining++;\n" +
" gid--;\n" +
" }\n" +
" }\n" +
" return;\n" +
" }\n" +
" void com_aparapi_codegen_test_CompositeArbitraryScope__t3(This *this){\n" +
" int gid = get_global_id(0);\n" +
" int numRemaining = 1;\n" +
" while (numRemaining>0){\n" +
" numRemaining++;\n" +
" {\n" +
" int thisCount = min(numRemaining, gid);\n" +
" numRemaining = numRemaining - thisCount;\n" +
" numRemaining++;\n" +
" }\n" +
" }\n" +
" return;\n" +
" }\n" +
" void com_aparapi_codegen_test_CompositeArbitraryScope__t2(This *this){\n" +
" int gid = get_global_id(0);\n" +
" int numRemaining = 1;\n" +
" for (; numRemaining>0; numRemaining){\n" +
" {\n" +
" int thisCount = min(numRemaining, gid);\n" +
" numRemaining = numRemaining - thisCount;\n" +
" }\n" +
" }\n" +
" return;\n" +
" }\n" +
" void com_aparapi_codegen_test_CompositeArbitraryScope__t1(This *this){\n" +
" int gid = get_global_id(0);\n" +
" int numRemaining = 1;\n" +
" while (numRemaining>0){\n" +
" numRemaining++;\n" +
" {\n" +
" int thisCount = min(numRemaining, gid);\n" +
" numRemaining = numRemaining - thisCount;\n" +
" }\n" +
" }\n" +
" return;\n" +
" }\n" +
" __kernel void run(\n" +
" int passid\n" +
" ){\n" +
" This thisStruct;\n" +
" This* this=&thisStruct;\n" +
" this->passid = passid;\n" +
" {\n" +
" int gid = get_global_id(0);\n" +
" int numRemaining = 1;\n" +
" com_aparapi_codegen_test_CompositeArbitraryScope__t1(this);\n" +
" com_aparapi_codegen_test_CompositeArbitraryScope__t2(this);\n" +
" com_aparapi_codegen_test_CompositeArbitraryScope__t3(this);\n" +
" com_aparapi_codegen_test_CompositeArbitraryScope__t4(this);\n" +
" com_aparapi_codegen_test_CompositeArbitraryScope__t5(this);\n" +
" while (numRemaining>0){\n" +
" numRemaining++;\n" +
" {\n" +
" int thisCount = min(numRemaining, gid);\n" +
" numRemaining = numRemaining - thisCount;\n" +
" }\n" +
" }\n" +
" return;\n" +
" }\n" +
" }\n" +
" "};
    private static final Class<? extends com.aparapi.internal.exception.AparapiException> expectedException = null;

    @Test
    public void CompositeArbitraryScopeTest() {
        test(com.aparapi.codegen.test.CompositeArbitraryScope.class, expectedException, expectedOpenCL);
    }

    @Test
    public void CompositeArbitraryScopeTestWorksWithCaching() {
        test(com.aparapi.codegen.test.CompositeArbitraryScope.class, expectedException, expectedOpenCL);
    }
}
