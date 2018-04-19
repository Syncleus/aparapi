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

public class ObjectArrayMemberGetterSetterTest extends com.aparapi.codegen.CodeGenJUnitBase {

    private static final String[] expectedOpenCL = {
        "typedef struct com_aparapi_codegen_test_DummyOOA_s{\n"
        + "   long  longField;\n"
        + "   float  floatField;\n"
        + "   int  mem;\n"
        + "   char  boolField;\n"
        + "   char _pad_17;\n"
        + "   char _pad_18;\n"
        + "   char _pad_19;\n"
        + "   char _pad_20;\n"
        + "   char _pad_21;\n"
        + "   char _pad_22;\n"
        + "   char _pad_23;\n"
        + "   \n"
        + "} com_aparapi_codegen_test_DummyOOA;\n"
        + "\n"
        + "typedef struct com_aparapi_codegen_test_TheOtherOne_s{\n"
        + "   int  mem;\n"
        + "   \n"
        + "} com_aparapi_codegen_test_TheOtherOne;\n"
        + "typedef struct This_s{\n"
        + "   int something;\n"
        + "   __global com_aparapi_codegen_test_DummyOOA *dummy;\n"
        + "   __global com_aparapi_codegen_test_TheOtherOne *other;\n"
        + "   __global int *out;\n"
        + "   int passid;\n"
        + "}This;\n"
        + "int get_pass_id(This *this){\n"
        + "   return this->passid;\n"
        + "}\n"
        + "void com_aparapi_codegen_test_DummyOOA__setBoolField(__global com_aparapi_codegen_test_DummyOOA *this, char x){\n"
        + "   this->boolField=x;\n"
        + "   return;\n"
        + "}\n"
        + "char com_aparapi_codegen_test_DummyOOA__isBoolField(__global com_aparapi_codegen_test_DummyOOA *this){\n"
        + "   return this->boolField;\n"
        + "}\n"
        + "char com_aparapi_codegen_test_DummyOOA__getBoolField(__global com_aparapi_codegen_test_DummyOOA *this){\n"
        + "   return this->boolField;\n"
        + "}\n"
        + "void com_aparapi_codegen_test_DummyOOA__setFloatField(__global com_aparapi_codegen_test_DummyOOA *this, float x){\n"
        + "   this->floatField=x;\n"
        + "   return;\n"
        + "}\n"
        + "float com_aparapi_codegen_test_DummyOOA__getFloatField(__global com_aparapi_codegen_test_DummyOOA *this){\n"
        + "   return this->floatField;\n"
        + "}\n"
        + "void com_aparapi_codegen_test_DummyOOA__setLongField(__global com_aparapi_codegen_test_DummyOOA *this, long x){\n"
        + "   this->longField=x;\n"
        + "   return;\n"
        + "}\n"
        + "long com_aparapi_codegen_test_DummyOOA__getLongField(__global com_aparapi_codegen_test_DummyOOA *this){\n"
        + "   return this->longField;\n"
        + "}\n"
        + "void com_aparapi_codegen_test_TheOtherOne__setMem(__global com_aparapi_codegen_test_TheOtherOne *this, int x){\n"
        + "   this->mem=x;\n"
        + "   return;\n"
        + "}\n"
        + "int com_aparapi_codegen_test_ObjectArrayMemberGetterSetter__getSomething(This *this){\n"
        + "   return(this->something);\n"
        + "}\n"
        + "int com_aparapi_codegen_test_TheOtherOne__getMem(__global com_aparapi_codegen_test_TheOtherOne *this){\n"
        + "   return this->mem;\n"
        + "}\n"
        + "void com_aparapi_codegen_test_DummyOOA__setMem(__global com_aparapi_codegen_test_DummyOOA *this, int x){\n"
        + "   this->mem=x;\n"
        + "   return;\n"
        + "}\n"
        + "int com_aparapi_codegen_test_DummyOOA__getMem(__global com_aparapi_codegen_test_DummyOOA *this){\n"
        + "   return this->mem;\n"
        + "}\n"
        + "__kernel void run(\n"
        + "   int something, \n"
        + "   __global com_aparapi_codegen_test_DummyOOA *dummy, \n"
        + "   __global com_aparapi_codegen_test_TheOtherOne *other, \n"
        + "   __global int *out, \n"
        + "   int passid\n"
        + "){\n"
        + "   This thisStruct;\n"
        + "   This* this=&thisStruct;\n"
        + "   this->something = something;\n"
        + "   this->dummy = dummy;\n"
        + "   this->other = other;\n"
        + "   this->out = out;\n"
        + "   this->passid = passid;\n"
        + "   {\n"
        + "      int myId = get_global_id(0);\n"
        + "      int tmp = com_aparapi_codegen_test_DummyOOA__getMem( &(this->dummy[myId]));\n"
        + "      com_aparapi_codegen_test_DummyOOA__setMem( &(this->dummy[myId]), (com_aparapi_codegen_test_DummyOOA__getMem( &(this->dummy[myId])) + 2));\n"
        + "      com_aparapi_codegen_test_DummyOOA__setMem( &(this->dummy[myId]), (com_aparapi_codegen_test_TheOtherOne__getMem( &(this->other[myId])) + com_aparapi_codegen_test_ObjectArrayMemberGetterSetter__getSomething(this)));\n"
        + "      com_aparapi_codegen_test_TheOtherOne__setMem( &(this->other[myId]), (com_aparapi_codegen_test_TheOtherOne__getMem( &(this->other[myId])) + com_aparapi_codegen_test_ObjectArrayMemberGetterSetter__getSomething(this)));\n"
        + "      com_aparapi_codegen_test_DummyOOA__setLongField( &(this->dummy[myId]), (com_aparapi_codegen_test_DummyOOA__getLongField( &(this->dummy[myId])) + 2L));\n"
        + "      com_aparapi_codegen_test_DummyOOA__setFloatField( &(this->dummy[myId]), (com_aparapi_codegen_test_DummyOOA__getFloatField( &(this->dummy[myId])) + 2.0f));\n"
        + "      com_aparapi_codegen_test_DummyOOA__setBoolField( &(this->dummy[myId]), (com_aparapi_codegen_test_DummyOOA__getBoolField( &(this->dummy[myId])) | com_aparapi_codegen_test_DummyOOA__isBoolField( &(this->dummy[myId]))));\n"
        + "      this->out[myId]  = com_aparapi_codegen_test_ObjectArrayMemberGetterSetter__getSomething(this);\n"
        + "      return;\n"
        + "   }\n"
        + "}"};
    private static final Class<? extends com.aparapi.internal.exception.AparapiException> expectedException = null;

    @Test
    public void ObjectArrayMemberGetterSetterTest() {
        test(com.aparapi.codegen.test.ObjectArrayMemberGetterSetter.class, expectedException, expectedOpenCL);
    }

    @Test
    public void ObjectArrayMemberGetterSetterTestWorksWithCaching() {
        test(com.aparapi.codegen.test.ObjectArrayMemberGetterSetter.class, expectedException, expectedOpenCL);
    }
}
