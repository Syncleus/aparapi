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

public class ArrayCreationTest extends com.aparapi.codegen.CodeGenJUnitBase {
    private static final String[] expectedOpenCL = {
        "typedef struct This_s{\n"
            + "   __global float *results;\n"
            + "   int results__javaArrayLength0;\n"
            + "   int results__javaArrayDimension0;\n"
            + "   int results__javaArrayLength1;\n"
            + "   int results__javaArrayDimension1;\n"
            + "   int y;\n"
            + "   int passid;\n"
            + "}This;\n"
            + "int get_pass_id(This *this){\n"
            + "   return this->passid;\n"
            + "}\n"
            + " __global float* com_aparapi_codegen_test_ArrayCreation__method(This *this,  __global float* a){\n"
            + "   a[0]  = a[0] + 1.0f;\n"
            + "   return(a);\n"
            + "}\n"
            + "__kernel void run(\n"
            + "   __global float *results, \n"
            + "   int results__javaArrayLength0, \n"
            + "   int results__javaArrayDimension0, \n"
            + "   int results__javaArrayLength1, \n"
            + "   int results__javaArrayDimension1, \n"
            + "   int y, \n"
            + "   int passid\n"
            + "){\n"
            + "   This thisStruct;\n"
            + "   This* this=&thisStruct;\n"
            + "   this->results = results;\n"
            + "   this->results__javaArrayLength0 = results__javaArrayLength0;\n"
            + "   this->results__javaArrayDimension0 = results__javaArrayDimension0;\n"
            + "   this->results__javaArrayLength1 = results__javaArrayLength1;\n"
            + "   this->results__javaArrayDimension1 = results__javaArrayDimension1;\n"
            + "   this->y = y;\n"
            + "   this->passid = passid;\n"
            + "   {\n"
            + "      float a[2];\n"
            + "      float b[16];\n"
            + "       __global float* d = com_aparapi_codegen_test_ArrayCreation__method(this, a);\n"
            + "      this->results[this->y]  = b;\n"
            + "      return;\n"
            + "   }\n"
            + "}\n"
    };
    private static final Class<? extends com.aparapi.internal.exception.AparapiException> expectedException = null;

    @Test
    public void ArrayCreationTest() {
        test(ArrayCreation.class, expectedException, expectedOpenCL);
    }

    @Test
    public void ArrayCreationTestWorksWithCaching() {
        test(ArrayCreation.class, expectedException, expectedOpenCL);
    }
}
