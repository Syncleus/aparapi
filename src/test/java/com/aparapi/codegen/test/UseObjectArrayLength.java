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

public class UseObjectArrayLength extends Kernel {
    int out[] = new int[2];

    ;
    Dummy dummy[] = new Dummy[10];

    @Override
    public void run() {
        out[0] = dummy.length;
    }

    static final class Dummy {
        public int n;
    }
}
/**{OpenCL{
 typedef struct This_s{
 __global int *out;
 __global com_amd_aparapi_test_UseObjectArrayLength$Dummy *dummy;
 int dummy__javaArrayLength;
 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }

 __kernel void run(
 __global int *out,
 __global com_amd_aparapi_test_UseObjectArrayLength$Dummy *dummy,
 int dummy__javaArrayLength,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->out = out;
 this->dummy = dummy;
 this->dummy__javaArrayLength = dummy__javaArrayLength;
 this->passid = passid;
 {
 this->out[0]  = this->dummy__javaArrayLength;
 return;
 }
 }
 }OpenCL}**/
