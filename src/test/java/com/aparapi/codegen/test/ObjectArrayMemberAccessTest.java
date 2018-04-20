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

public class ObjectArrayMemberAccessTest extends com.aparapi.codegen.CodeGenJUnitBase {
    private static final String[] expectedOpenCL = {
        "typedef struct com_aparapi_codegen_test_ObjectArrayMemberAccess$DummyOOA_s{\n" +
" int  mem;\n" +
" float  floatField;\n" +
"\n" +
" } com_aparapi_codegen_test_ObjectArrayMemberAccess$DummyOOA;\n" +
"\n" +
" typedef struct This_s{\n" +
" __global com_aparapi_codegen_test_ObjectArrayMemberAccess$DummyOOA *dummy;\n" +
" int passid;\n" +
" }This;\n" +
" int get_pass_id(This *this){\n" +
" return this->passid;\n" +
" }\n" +
"\n" +
" __kernel void run(\n" +
" __global com_aparapi_codegen_test_ObjectArrayMemberAccess$DummyOOA *dummy,\n" +
" int passid\n" +
" ){\n" +
" This thisStruct;\n" +
" This* this=&thisStruct;\n" +
" this->dummy = dummy;\n" +
" this->passid = passid;\n" +
" {\n" +
" int myId = get_global_id(0);\n" +
" this->dummy[myId].mem=this->dummy[myId].mem + 2;\n" +
" this->dummy[myId].floatField=this->dummy[myId].floatField + 2.0f;\n" +
" return;\n" +
" }\n" +
" }\n" +
" "};
    private static final Class<? extends com.aparapi.internal.exception.AparapiException> expectedException = null;

    @Test
    public void ObjectArrayMemberAccessTest() {
        test(com.aparapi.codegen.test.ObjectArrayMemberAccess.class, expectedException, expectedOpenCL);
    }

    @Test
    public void ObjectArrayMemberAccessTestWorksWithCaching() {
        test(com.aparapi.codegen.test.ObjectArrayMemberAccess.class, expectedException, expectedOpenCL);
    }
}
