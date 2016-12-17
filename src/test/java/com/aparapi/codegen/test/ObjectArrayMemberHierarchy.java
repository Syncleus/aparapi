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

import com.aparapi.Kernel;

public class ObjectArrayMemberHierarchy extends Kernel {

    final static int size = 16;
    int something;

    ;
    DummyOOA dummy[] = null;

    ;

    public ObjectArrayMemberHierarchy() {
        something = -1;
        dummy = new DummyOOA[size];

        dummy[0] = new DummyOOA();
    }

    public int getSomething() {
        return something;
    }

    public int bar(int x) {
        return -x;
    }

    public void run() {
        int myId = getGlobalId();
        dummy[myId].intField = dummy[myId].getIntField() + 2 + getSomething();
        dummy[myId].setFloatField(dummy[myId].floatField + (float) 2.0);
    }

    static class DummyParent {
        int intField;

        public DummyParent() {
            intField = -3;
        }

        public int getIntField() {
            return intField;
        }

        public void setIntField(int x) {
            intField = x;
        }

        public int foo() {
            return 42 + getIntField();
        }

    }

    final static class DummyOOA extends DummyParent {
        float floatField;

        public float getFloatField() {
            return floatField;
        }

        public void setFloatField(float x) {
            floatField = x;
        }
    }
}

/**{OpenCL{
 typedef struct com_amd_aparapi_test_ObjectArrayMemberHierarchy$DummyOOA_s{
 float  floatField;
 int  intField;

 } com_amd_aparapi_test_ObjectArrayMemberHierarchy$DummyOOA;

 typedef struct This_s{
 int something;
 __global com_amd_aparapi_test_ObjectArrayMemberHierarchy$DummyOOA *dummy;
 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }

 void com_amd_aparapi_test_ObjectArrayMemberHierarchy$DummyOOA__setFloatField( __global com_amd_aparapi_test_ObjectArrayMemberHierarchy$DummyOOA *this, float x){
 this->floatField=x;
 return;
 }
 int com_amd_aparapi_test_ObjectArrayMemberHierarchy__getSomething(This *this){
 return(this->something);
 }
 int com_amd_aparapi_test_ObjectArrayMemberHierarchy$DummyParent__getIntField( __global com_amd_aparapi_test_ObjectArrayMemberHierarchy$DummyOOA *this){
 return(this->intField);
 }
 __kernel void run(
 int something,
 __global com_amd_aparapi_test_ObjectArrayMemberHierarchy$DummyOOA *dummy,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->something = something;
 this->dummy = dummy;
 this->passid = passid;
 {
 int myId = get_global_id(0);
 this->dummy[myId].intField=(com_amd_aparapi_test_ObjectArrayMemberHierarchy$DummyParent__getIntField( &(this->dummy[myId])) + 2) + com_amd_aparapi_test_ObjectArrayMemberHierarchy__getSomething(this);
 com_amd_aparapi_test_ObjectArrayMemberHierarchy$DummyOOA__setFloatField( &(this->dummy[myId]), (this->dummy[myId].floatField + 2.0f));
 return;
 }
 }
 }OpenCL}**/
