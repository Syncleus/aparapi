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

public class CallTwice extends Kernel {

    int out[] = new int[1];

    public int getOne() {
        return (1);
    }

    @Override
    public void run() {
        out[0] = getOne() + getOne();
    }
}

/**{OpenCL{
 typedef struct This_s{
 __global int *out;
 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }
 int com_amd_aparapi_test_CallTwice__getOne(This *this){
 return(1);
 }
 __kernel void run(
 __global int *out,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->out = out;
 this->passid = passid;
 {
 this->out[0]  = com_amd_aparapi_test_CallTwice__getOne(this) + com_amd_aparapi_test_CallTwice__getOne(this);
 return;
 }
 }
 }OpenCL}**/
