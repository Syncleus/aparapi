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

public class MathFallThru extends Kernel {

    long longout[] = new long[3];
    int intout[] = new int[3];

    public void run() {
        float f1 = 1.0f;
        double d1 = 1.0;
        longout[0] = round(ceil(cos(exp(floor(log(pow(d1, d1)))))) + tan(sqrt(sin(rint(acos(asin(atan(atan2(d1, d1)))))))));
        longout[1] = popcount(longout[0]);
        longout[2] = clz(longout[0]);
        intout[0] = round(ceil(cos(exp(floor(log(pow(f1, f1)))))) + tan(sqrt(sin(rint(acos(asin(atan(atan2(f1, f1)))))))));
        intout[1] = popcount(intout[0]);
        intout[2] = clz(intout[0]);
        @SuppressWarnings("unused") boolean pass = false;
    }
}
/**{OpenCL{
 #pragma OPENCL EXTENSION cl_khr_fp64 : enable

 typedef struct This_s{
 __global long *longout;
 __global int *intout;
 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }

 __kernel void run(
 __global long *longout,
 __global int *intout,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->longout = longout;
 this->intout = intout;
 this->passid = passid;
 {
 float f1 = 1.0f;
 double d1 = 1.0;
 this->longout[0]  = round((ceil(cos(exp(floor(log(pow(d1, d1)))))) + tan(sqrt(sin(rint(acos(asin(atan(atan2(d1, d1))))))))));
 this->longout[1]  = popcount(this->longout[0]);
 this->longout[2]  = clz(this->longout[0]);
 this->intout[0]  = round((ceil(cos(exp(floor(log(pow(f1, f1)))))) + tan(sqrt(sin(rint(acos(asin(atan(atan2(f1, f1))))))))));
 this->intout[1]  = popcount(this->intout[0]);
 this->intout[2]  = clz(this->intout[0]);
 char pass = 0;
 return;
 }
 }

 }OpenCL}**/
