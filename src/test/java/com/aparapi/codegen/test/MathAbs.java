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

public class MathAbs extends Kernel {
    public void run() {
        double d = -1.0;
        float f = -1.0f;
        int i = -1;
        long n = -1;
        @SuppressWarnings("unused") boolean pass = true;
        if ((abs(d) != 1) || (abs(f) != 1) || (abs(i) != 1) || (abs(n) != 1))
            pass = false;
    }
}
/**{OpenCL{
 #pragma OPENCL EXTENSION cl_khr_fp64 : enable

 typedef struct This_s{

 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }

 __kernel void run(
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->passid = passid;
 {
 double d = -1.0;
 float f = -1.0f;
 int i = -1;
 long n = -1L;
 char pass = 1;
 if (fabs(d)!=1.0 || fabs(f)!=1.0f || abs(i)!=1 || (abs(n) - 1L)!=0){
 pass = 0;
 }
 return;
 }
 }
 }OpenCL}**/
