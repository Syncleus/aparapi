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

public class Atomic32Pragma extends Kernel {

    final int[] values = new int[10];

    @Override
    public void run() {
        atomicAdd(values, 1, 1);
    }
}

/**{OpenCL{
 #pragma OPENCL EXTENSION cl_khr_global_int32_base_atomics : enable
 #pragma OPENCL EXTENSION cl_khr_global_int32_extended_atomics : enable
 #pragma OPENCL EXTENSION cl_khr_local_int32_base_atomics : enable
 #pragma OPENCL EXTENSION cl_khr_local_int32_extended_atomics : enable
 int atomicAdd(__global int *_arr, int _index, int _delta){
 return atomic_add(&_arr[_index], _delta);
 }
 typedef struct This_s{
 __global int *values;
 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }

 __kernel void run(
 __global int *values,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->values = values;
 this->passid = passid;
 {
 atomicAdd(this->values, 1, 1);
 return;
 }
 }
 }OpenCL}**/
