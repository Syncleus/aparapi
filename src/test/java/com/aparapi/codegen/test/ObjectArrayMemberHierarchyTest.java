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

public class ObjectArrayMemberHierarchyTest extends com.aparapi.codegen.CodeGenJUnitBase {

    private static final String[] expectedOpenCL = {
        "typedef struct com_aparapi_codegen_test_ObjectArrayMemberHierarchy$DummyOOA_s{\n"
        + "   float  floatField;\n"
        + "   int  intField;\n"
        + "   \n"
        + "} com_aparapi_codegen_test_ObjectArrayMemberHierarchy$DummyOOA;\n"
        + "typedef struct This_s{\n"
        + "   int something;\n"
        + "   __global com_aparapi_codegen_test_ObjectArrayMemberHierarchy$DummyOOA *dummy;\n"
        + "   int passid;\n"
        + "}This;\n"
        + "int get_pass_id(This *this){\n"
        + "   return this->passid;\n"
        + "}\n"
        + "void com_aparapi_codegen_test_ObjectArrayMemberHierarchy$DummyOOA__setFloatField(__global com_aparapi_codegen_test_ObjectArrayMemberHierarchy$DummyOOA *this, float x){\n"
        + "   this->floatField=x;\n"
        + "   return;\n"
        + "}\n"
        + "int com_aparapi_codegen_test_ObjectArrayMemberHierarchy__getSomething(This *this){\n"
        + "   return(this->something);\n"
        + "}\n"
        + "int com_aparapi_codegen_test_ObjectArrayMemberHierarchy$DummyParent__getIntField(__global com_aparapi_codegen_test_ObjectArrayMemberHierarchy$DummyOOA *this){\n"
        + "   return this->intField;\n"
        + "}\n"
        + "__kernel void run(\n"
        + "   int something, \n"
        + "   __global com_aparapi_codegen_test_ObjectArrayMemberHierarchy$DummyOOA *dummy, \n"
        + "   int passid\n"
        + "){\n"
        + "   This thisStruct;\n"
        + "   This* this=&thisStruct;\n"
        + "   this->something = something;\n"
        + "   this->dummy = dummy;\n"
        + "   this->passid = passid;\n"
        + "   {\n"
        + "      int myId = get_global_id(0);\n"
        + "      this->dummy[myId].intField=(com_aparapi_codegen_test_ObjectArrayMemberHierarchy$DummyParent__getIntField( &(this->dummy[myId])) + 2) + com_aparapi_codegen_test_ObjectArrayMemberHierarchy__getSomething(this);\n"
        + "      com_aparapi_codegen_test_ObjectArrayMemberHierarchy$DummyOOA__setFloatField( &(this->dummy[myId]), (this->dummy[myId].floatField + 2.0f));\n"
        + "      return;\n"
        + "   }\n"
        + "}"};
    private static final Class<? extends com.aparapi.internal.exception.AparapiException> expectedException = null;

    @Test
    public void ObjectArrayMemberHierarchyTest() {
        test(com.aparapi.codegen.test.ObjectArrayMemberHierarchy.class, expectedException, expectedOpenCL);
    }

    @Test
    public void ObjectArrayMemberHierarchyTestWorksWithCaching() {
        test(com.aparapi.codegen.test.ObjectArrayMemberHierarchy.class, expectedException, expectedOpenCL);
    }
}
