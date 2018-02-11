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

public class MathFallThruTest extends com.aparapi.codegen.CodeGenJUnitBase {
    private static final String[] expectedOpenCL = {
    "#pragma OPENCL EXTENSION cl_khr_fp64 : enable\n" +
"\n" +
" typedef struct This_s{\n" +
" __global long *longout;\n" +
" __global int *intout;\n" +
" int passid;\n" +
" }This;\n" +
" int get_pass_id(This *this){\n" +
" return this->passid;\n" +
" }\n" +
"\n" +
" __kernel void run(\n" +
" __global long *longout,\n" +
" __global int *intout,\n" +
" int passid\n" +
" ){\n" +
" This thisStruct;\n" +
" This* this=&thisStruct;\n" +
" this->longout = longout;\n" +
" this->intout = intout;\n" +
" this->passid = passid;\n" +
" {\n" +
" float f1 = 1.0f;\n" +
" double d1 = 1.0;\n" +
" this->longout[0]  = round((ceil(cos(exp(floor(log(pow(d1, d1)))))) + tan(sqrt(sin(rint(acos(asin(atan(atan2(d1, d1))))))))));\n" +
" this->longout[1]  = popcount(this->longout[0]);\n" +
" this->longout[2]  = clz(this->longout[0]);\n" +
" this->intout[0]  = round((ceil(cos(exp(floor(log(pow(f1, f1)))))) + tan(sqrt(sin(rint(acos(asin(atan(atan2(f1, f1))))))))));\n" +
" this->intout[1]  = popcount(this->intout[0]);\n" +
" this->intout[2]  = clz(this->intout[0]);\n" +
" char pass = 0;\n" +
" return;\n" +
" }\n" +
" }\n" +
"\n" +
" "};
    private static final Class<? extends com.aparapi.internal.exception.AparapiException> expectedException = null;

    @Test
    public void MathFallThruTest() {
        test(com.aparapi.codegen.test.MathFallThru.class, expectedException, expectedOpenCL);
    }

    @Test
    public void MathFallThruTestWorksWithCaching() {
        test(com.aparapi.codegen.test.MathFallThru.class, expectedException, expectedOpenCL);
    }
}
