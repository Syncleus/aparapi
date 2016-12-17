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

public class ObjectArrayMemberAccess extends Kernel {

    final int size = 64;

    ;
    int out[] = new int[2];

    int something;

    DummyOOA dummy[] = null;

    public ObjectArrayMemberAccess() {
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
        dummy[myId].mem = dummy[myId].mem + 2;
        dummy[myId].floatField = dummy[myId].floatField + (float) 2.0;
    }

    final static class DummyOOA {
        int mem;

        float floatField;

        public DummyOOA() {
            mem = -3;
            floatField = -3;
        }

        public int getMem() {
            return mem;
        }

        public void setMem(int x) {
            mem = x;
        }

        public float getFloatField() {
            return floatField;
        }

        public void setFloatField(float x) {
            floatField = x;
        }

    }
}

/**{OpenCL{
 typedef struct com_amd_aparapi_test_ObjectArrayMemberAccess$DummyOOA_s{
 int  mem;
 float  floatField;

 } com_amd_aparapi_test_ObjectArrayMemberAccess$DummyOOA;

 typedef struct This_s{
 __global com_amd_aparapi_test_ObjectArrayMemberAccess$DummyOOA *dummy;
 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }

 __kernel void run(
 __global com_amd_aparapi_test_ObjectArrayMemberAccess$DummyOOA *dummy,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->dummy = dummy;
 this->passid = passid;
 {
 int myId = get_global_id(0);
 this->dummy[myId].mem=this->dummy[myId].mem + 2;
 this->dummy[myId].floatField=this->dummy[myId].floatField + 2.0f;
 return;
 }
 }
 }OpenCL}**/
