package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class NaN extends Kernel{
      @Override public void run() {
         double d = 1.0E-10;        
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
      double d = 1.0E-10;
      return;
   }
}
}OpenCL}**/
