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

public class IEEERemainderFloat extends Kernel {
    float out[] = new float[10];
    float m;
    float n;

    @Override
    public void run() {
        out[0] = IEEEremainder(m, n);
    }
}

/**{OpenCL{
 typedef struct This_s{
 __global float *out;
 float m;
 float n;
 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }
 __kernel void run(
 __global float *out,
 float m,
 float n,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->out = out;
 this->m = m;
 this->n = n;
 this->passid = passid;
 {
 this->out[0]  = remainder(this->m, this->n);
 return;
 }
 }

 }OpenCL}**/
