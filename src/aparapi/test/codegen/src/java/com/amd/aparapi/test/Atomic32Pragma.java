package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class Atomic32Pragma extends Kernel{

   final int[] values = new int[10];

   @Override public void run() {
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
