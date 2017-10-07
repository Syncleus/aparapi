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

public class CallSuperTest extends com.aparapi.codegen.CodeGenJUnitBase {
    private static final String[] expectedOpenCL = {
        "typedef struct This_s{\n" +
" __global int *out;\n" +
" int passid;\n" +
" }This;\n" +
" int get_pass_id(This *this){\n" +
" return this->passid;\n" +
" }\n" +
"\n" +
" int com_aparapi_codegen_test_CallSuperBase__foo(This *this, int n){\n" +
" return((n * 2));\n" +
" }\n" +
" int com_aparapi_codegen_test_CallSuper__foo(This *this, int n){\n" +
" return((1 + com_aparapi_codegen_test_CallSuperBase__foo(this, n)));\n" +
" }\n" +
" __kernel void run(\n" +
" __global int *out,\n" +
" int passid\n" +
" ){\n" +
" This thisStruct;\n" +
" This* this=&thisStruct;\n" +
" this->out = out;\n" +
" this->passid = passid;\n" +
" {\n" +
" this->out[0]  = com_aparapi_codegen_test_CallSuper__foo(this, 2);\n" +
" return;\n" +
" }\n" +
" }\n" +
" "};
    private static final Class<? extends com.aparapi.internal.exception.AparapiException> expectedException = null;

    @Test
    public void CallSuperTest() {
        test(com.aparapi.codegen.test.CallSuper.class, expectedException, expectedOpenCL);
    }

    @Test
    public void CallSuperTestWorksWithCaching() {
        test(com.aparapi.codegen.test.CallSuper.class, expectedException, expectedOpenCL);
    }
}
