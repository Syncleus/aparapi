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

public class ObjectArrayMemberCallTest extends com.aparapi.codegen.CodeGenJUnitBase {

    private static final String[] expectedOpenCL = {
        "typedef struct com_aparapi_codegen_test_ObjectArrayMemberCall$DummyOOA_s{\n"
        + "   int  mem;\n"
        + "   \n"
        + "} com_aparapi_codegen_test_ObjectArrayMemberCall$DummyOOA;\n"
        + "typedef struct This_s{\n"
        + "   __global com_aparapi_codegen_test_ObjectArrayMemberCall$DummyOOA *dummy;\n"
        + "   int passid;\n"
        + "}This;\n"
        + "int get_pass_id(This *this){\n"
        + "   return this->passid;\n"
        + "}\n"
        + "int com_aparapi_codegen_test_ObjectArrayMemberCall$DummyOOA__getMem(__global com_aparapi_codegen_test_ObjectArrayMemberCall$DummyOOA *this){\n"
        + "   return this->mem;\n"
        + "}\n"
        + "int com_aparapi_codegen_test_ObjectArrayMemberCall$DummyOOA__addEmUp(__global com_aparapi_codegen_test_ObjectArrayMemberCall$DummyOOA *this, int x, int y){\n"
        + "   return((x + y));\n"
        + "}\n"
        + "int com_aparapi_codegen_test_ObjectArrayMemberCall$DummyOOA__addEmUpPlusOne(__global com_aparapi_codegen_test_ObjectArrayMemberCall$DummyOOA *this, int x, int y){\n"
        + "   return(((com_aparapi_codegen_test_ObjectArrayMemberCall$DummyOOA__addEmUp(this, x, y) + 1) + com_aparapi_codegen_test_ObjectArrayMemberCall$DummyOOA__getMem(this)));\n"
        + "}\n"
        + "int com_aparapi_codegen_test_ObjectArrayMemberCall$DummyOOA__addToMem(__global com_aparapi_codegen_test_ObjectArrayMemberCall$DummyOOA *this, int x){\n"
        + "   return((x + this->mem));\n"
        + "}\n"
        + "__kernel void run(\n"
        + "   __global com_aparapi_codegen_test_ObjectArrayMemberCall$DummyOOA *dummy, \n"
        + "   int passid\n"
        + "){\n"
        + "   This thisStruct;\n"
        + "   This* this=&thisStruct;\n"
        + "   this->dummy = dummy;\n"
        + "   this->passid = passid;\n"
        + "   {\n"
        + "      int myId = get_global_id(0);\n"
        + "      this->dummy[myId].mem=com_aparapi_codegen_test_ObjectArrayMemberCall$DummyOOA__addEmUp( &(this->dummy[myId]), this->dummy[myId].mem, 2);\n"
        + "      int tmp = com_aparapi_codegen_test_ObjectArrayMemberCall$DummyOOA__addToMem( &(this->dummy[myId]), 2);\n"
        + "      int tmp2 = com_aparapi_codegen_test_ObjectArrayMemberCall$DummyOOA__addEmUpPlusOne( &(this->dummy[myId]), 2, tmp);\n"
        + "      return;\n"
        + "   }\n"
        + "}"};
    private static final Class<? extends com.aparapi.internal.exception.AparapiException> expectedException = null;

    @Test
    public void ObjectArrayMemberCallTest() {
        test(com.aparapi.codegen.test.ObjectArrayMemberCall.class, expectedException, expectedOpenCL);
    }

    @Test
    public void ObjectArrayMemberCallTestWorksWithCaching() {
        test(com.aparapi.codegen.test.ObjectArrayMemberCall.class, expectedException, expectedOpenCL);
    }
}
