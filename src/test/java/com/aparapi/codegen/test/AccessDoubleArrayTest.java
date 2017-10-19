/**
 * Copyright (c) 2016 - 2017 Syncleus, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aparapi.codegen.test;

import org.apache.log4j.Logger;
import org.junit.Test;

public class AccessDoubleArrayTest extends com.aparapi.codegen.CodeGenJUnitBase {
    private static final Logger LOGGER = Logger.getLogger(AccessDoubleArrayTest.class);
    private static final String[] expectedOpenCL = {"#pragma OPENCL EXTENSION cl_khr_fp64 : enable\n" +
        "\n" +
        "typedef struct This_s{\n" +
        "   __global double *doubles;\n" +
        "   int passid;\n" +
        "}This;\n" +
        "int get_pass_id(This *this){\n" +
        "   return this->passid;\n" +
        "}\n" +
        "__kernel void run(\n" +
        "   __global double *doubles, \n" +
        "   int passid\n" +
        "){\n" +
        "   This thisStruct;\n" +
        "   This* this=&thisStruct;\n" +
        "   this->doubles = doubles;\n" +
        "   this->passid = passid;\n" +
        "   {\n" +
        "      for (int i = 0; i<1024; i++){\n" +
        "         this->doubles[i]  = 1.0;\n" +
        "      }\n" +
        "      return;\n" +
        "   }\n" +
        "}\n" +
        "\n"};
    private static final Class<? extends com.aparapi.internal.exception.AparapiException> expectedException = null;

    @Test
    public void AccessDoubleArrayTest() {
        test(com.aparapi.codegen.test.AccessDoubleArray.class, expectedException, expectedOpenCL);
    }

    @Test
    public void AccessDoubleArrayTestWorksWithCaching() {
        test(com.aparapi.codegen.test.AccessDoubleArray.class, expectedException, expectedOpenCL);
    }
}
