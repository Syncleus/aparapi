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

public class Atomic32PragmaTest extends com.aparapi.codegen.CodeGenJUnitBase {
    private static final String[] expectedOpenCL = {
    "#pragma OPENCL EXTENSION cl_khr_global_int32_base_atomics : enable\n" +
" #pragma OPENCL EXTENSION cl_khr_global_int32_extended_atomics : enable\n" +
" #pragma OPENCL EXTENSION cl_khr_local_int32_base_atomics : enable\n" +
" #pragma OPENCL EXTENSION cl_khr_local_int32_extended_atomics : enable\n" +
" #define atomicGet(p) (*p)\n" + 
" #define atomicSet(p, val) (*p=val)\n" +
" int atomicAdd(__global int *_arr, int _index, int _delta){\n" +
" return atomic_add(&_arr[_index], _delta);\n" +
" }\n" +
" typedef struct This_s{\n" +
" __global int *values;\n" +
" int passid;\n" +
" }This;\n" +
" int get_pass_id(This *this){\n" +
" return this->passid;\n" +
" }\n" +
"\n" +
" __kernel void run(\n" +
" __global int *values,\n" +
" int passid\n" +
" ){\n" +
" This thisStruct;\n" +
" This* this=&thisStruct;\n" +
" this->values = values;\n" +
" this->passid = passid;\n" +
" {\n" +
" atomicAdd(this->values, 1, 1);\n" +
" return;\n" +
" }\n" +
" }\n" +
" "};
    private static final Class<? extends com.aparapi.internal.exception.AparapiException> expectedException = null;

    @Test
    public void Atomic32PragmaTest() {
        test(com.aparapi.codegen.test.Atomic32Pragma.class, expectedException, expectedOpenCL);
    }

    @Test
    public void Atomic32PragmaTestWorksWithCaching() {
        test(com.aparapi.codegen.test.Atomic32Pragma.class, expectedException, expectedOpenCL);
    }
}
