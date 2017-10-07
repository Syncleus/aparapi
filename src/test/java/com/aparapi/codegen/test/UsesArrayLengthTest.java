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

public class UsesArrayLengthTest extends com.aparapi.codegen.CodeGenJUnitBase {

    private static final String[] expectedOpenCL = {
        "typedef struct This_s{\n"
        + "   __global char  *values;\n"
        + "   int values__javaArrayLength;\n"
        + "   __global char  *results;\n"
        + "   int results__javaArrayLength;\n"
        + "   __global char  *results2;\n"
        + "   int passid;\n"
        + "}This;\n"
        + "int get_pass_id(This *this){\n"
        + "   return this->passid;\n"
        + "}\n"
        + "char com_aparapi_codegen_test_UsesArrayLength__actuallyDoIt(This *this, int index){\n"
        + "   int x = 0;\n"
        + "   char y = this->values[(this->values__javaArrayLength - index)];\n"
        + "   x = index + this->results__javaArrayLength;\n"
        + "   return((((this->results__javaArrayLength - x)>0)?1:0));\n"
        + "}\n"
        + "__kernel void run(\n"
        + "   __global char  *values, \n"
        + "   int values__javaArrayLength, \n"
        + "   __global char  *results, \n"
        + "   int results__javaArrayLength, \n"
        + "   __global char  *results2, \n"
        + "   int passid\n"
        + "){\n"
        + "   This thisStruct;\n"
        + "   This* this=&thisStruct;\n"
        + "   this->values = values;\n"
        + "   this->values__javaArrayLength = values__javaArrayLength;\n"
        + "   this->results = results;\n"
        + "   this->results__javaArrayLength = results__javaArrayLength;\n"
        + "   this->results2 = results2;\n"
        + "   this->passid = passid;\n"
        + "   {\n"
        + "      int myId = 0;\n"
        + "      char x = (this->values__javaArrayLength>0)?1:0;\n"
        + "      this->results[myId]  = x & com_aparapi_codegen_test_UsesArrayLength__actuallyDoIt(this, this->values__javaArrayLength);\n"
        + "      this->results2[myId]  = (this->results[myId]==0)?1:0;\n"
        + "      return;\n"
        + "   }\n"
        + "}"};
    private static final Class<? extends com.aparapi.internal.exception.AparapiException> expectedException = null;

    @Test
    public void UsesArrayLengthTest() {
        test(com.aparapi.codegen.test.UsesArrayLength.class, expectedException, expectedOpenCL);
    }

    @Test
    public void UsesArrayLengthTestWorksWithCaching() {
        test(com.aparapi.codegen.test.UsesArrayLength.class, expectedException, expectedOpenCL);
    }
}
