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

import com.aparapi.Kernel;

public class RightShifts extends Kernel {

    int iout[] = new int[10];

    int i1, i2;

    public void run() {
        iout[1] = i1 >> i2;
        iout[2] = i1 >>> i2;
    }
}
/**{OpenCL{
 typedef struct This_s{
 __global int *iout;
 int i1;
 int i2;
 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }

 __kernel void run(
 __global int *iout,
 int i1,
 int i2,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->iout = iout;
 this->i1 = i1;
 this->i2 = i2;
 this->passid = passid;
 {
 this->iout[1]  = this->i1 >> this->i2;
 this->iout[2]  = ((unsigned int)this->i1) >> this->i2;
 return;
 }
 }

 }OpenCL}**/
