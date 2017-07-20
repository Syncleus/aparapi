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

public class FusedMultiplyAddTest extends com.aparapi.codegen.CodeGenJUnitBase {
    private static final String[] expectedOpenCL = {"#pragma OPENCL EXTENSION cl_khr_fp64 : enable\n" +
        "typedef struct This_s{\n" +
        " int passid;\n" +
        "}This;\n" +
        "int get_pass_id(This *this){\n" +
        "   return this->passid;\n" +
        "}\n" +
        "__kernel void run(\n" +
        "   int passid\n" +
        "){\n" +
        "   This thisStruct;\n" +
        "   This* this=&thisStruct;\n" +
        "   this->passid = passid;\n" +
        "   {\n" +
        "      double d1 = 123.0;\n" +
        "      double d2 = 0.456;\n" +
        "      double d3 = 789.0;\n" +
        "      float f1 = 123.0f;\n" +
        "      float f2 = 0.456f;\n" +
        "      float f3 = 789.0f;\n" +
        "      char pass = 1;\n" +
        "      if (fma(d1, d2, d3)!=845.088 || fma(f1, f2, f3)!=845.088f){\n" +
        "         pass = 0;\n" +
        "      }\n" +
        "      return;\n" +
        "   }\n" +
        "}"};
    private static final Class<? extends com.aparapi.internal.exception.AparapiException> expectedException = null;

    @Test
    public void FusedMultiplyAddTest() {
        test(com.aparapi.codegen.test.FusedMultiplyAdd.class, expectedException, expectedOpenCL);
    }

    @Test
    public void FusedMultiplyAddTestWorksWithCaching() {
        test(com.aparapi.codegen.test.FusedMultiplyAdd.class, expectedException, expectedOpenCL);
    }
}
/**{OpenCL{
#pragma OPENCL EXTENSION cl_khr_fp64 : enable

typedef struct This_s{
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
}
__kernel void run(
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->passid = passid;
   {
      double d1 = 123.0;
      double d2 = 0.456;
      double d3 = 789.0;
      float f1 = 123.0f;
      float f2 = 0.456f;
      float f3 = 789.0f;
      char pass = 1;
      if (fma(d1, d2, d3)!=845.088 || fma(f1, f2, f3)!=845.088f){
         pass = 0;
      }
      return;
   }
}OpenCL}**/
