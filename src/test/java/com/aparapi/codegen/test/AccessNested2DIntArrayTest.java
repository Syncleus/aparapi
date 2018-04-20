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

public class AccessNested2DIntArrayTest extends com.aparapi.codegen.CodeGenJUnitBase {
    private static final String[] expectedOpenCL = {"typedef struct This_s{\n" +
        "   __global int *ints;\n" +
        "   int ints__javaArrayLength0;\n" +
        "   int ints__javaArrayDimension0;\n" +
        "   int ints__javaArrayLength1;\n" +
        "   int ints__javaArrayDimension1;\n" +
        "   __global int *indices;\n" +
        "   int passid;\n" +
        "}This;\n" +
        "int get_pass_id(This *this){\n" +
        "   return this->passid;\n" +
        "}\n" +
        "__kernel void run(\n" +
        "   __global int *ints, \n" +
        "   int ints__javaArrayLength0, \n" +
        "   int ints__javaArrayDimension0, \n" +
        "   int ints__javaArrayLength1, \n" +
        "   int ints__javaArrayDimension1, \n" +
        "   __global int *indices, \n" +
        "   int passid\n" +
        "){\n" +
        "   This thisStruct;\n" +
        "   This* this=&thisStruct;\n" +
        "   this->ints = ints;\n" +
        "   this->ints__javaArrayLength0 = ints__javaArrayLength0;\n" +
        "   this->ints__javaArrayDimension0 = ints__javaArrayDimension0;\n" +
        "   this->ints__javaArrayLength1 = ints__javaArrayLength1;\n" +
        "   this->ints__javaArrayDimension1 = ints__javaArrayDimension1;\n" +
        "   this->indices = indices;\n" +
        "   this->passid = passid;\n" +
        "   {\n" +
        "      int value = (&this->ints[this->indices[0] * this->ints__javaArrayDimension0])[0];\n" +
        "      return;\n" +
        "   }\n" +
        "}\n" +
        "\n"};
    private static final Class<? extends com.aparapi.internal.exception.AparapiException> expectedException = null;

    @Test
    public void AccessNested2DIntArrayTest() {
        test(com.aparapi.codegen.test.AccessNested2DIntArray.class, expectedException, expectedOpenCL);
    }

    @Test
    public void AccessNested2DIntArrayTestWorksWithCaching() {
        test(com.aparapi.codegen.test.AccessNested2DIntArray.class, expectedException, expectedOpenCL);
    }
}
