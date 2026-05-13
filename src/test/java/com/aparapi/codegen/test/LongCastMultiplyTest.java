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

public class LongCastMultiplyTest extends com.aparapi.codegen.CodeGenJUnitBase {
    private static final String[] expectedOpenCL = {"inline ulong aparapi_umul64_lo(ulong a, ulong b){\n" +
        "   uint a0 = (uint)a;\n" +
        "   uint a1 = (uint)(a >> 32);\n" +
        "   uint b0 = (uint)b;\n" +
        "   uint b1 = (uint)(b >> 32);\n" +
        "   uint lo = a0 * b0;\n" +
        "   uint hi = mul_hi(a0, b0);\n" +
        "   uint cross = hi + (a0 * b1) + (a1 * b0);\n" +
        "   return (((ulong)cross) << 32) | (ulong)lo;\n" +
        "}\n" +
        "inline long aparapi_lmul(long a, long b){ return (long)aparapi_umul64_lo((ulong)a, (ulong)b); }\n" +
        "typedef struct This_s{\n" +
        "   __global long *values;\n" +
        "   int passid;\n" +
        "}This;\n" +
        "int get_pass_id(This *this){\n" +
        "   return this->passid;\n" +
        "}\n" +
        "long com_aparapi_codegen_test_LongCastMultiply__calculate(This *this, int value){\n" +
        "   return((aparapi_lmul((long)value, 100L)));\n" +
        "}\n" +
        "__kernel void run(\n" +
        "   __global long *values, \n" +
        "   int passid\n" +
        "){\n" +
        "   This thisStruct;\n" +
        "   This* this=&thisStruct;\n" +
        "   this->values = values;\n" +
        "   this->passid = passid;\n" +
        "   {\n" +
        "      this->values[0]  = com_aparapi_codegen_test_LongCastMultiply__calculate(this, (2147483647 + (int)get_global_id(0)));\n" +
        "      return;\n" +
        "   }\n" +
        "}\n" +
        "\n"};
    private static final Class<? extends com.aparapi.internal.exception.AparapiException> expectedException = null;

    @Test
    public void LongCastMultiplyTest() {
        test(com.aparapi.codegen.test.LongCastMultiply.class, expectedException, expectedOpenCL);
    }
}
