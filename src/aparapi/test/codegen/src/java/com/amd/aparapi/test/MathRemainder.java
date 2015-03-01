package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class MathRemainder extends Kernel{
   public void run() {
      double d1 = 7.0, d2 = 2.0;
      float f1 = 7.0f, f2 = 2.0f;
      @SuppressWarnings("unused") boolean pass = true;
      if ((IEEEremainder(d1, d2) != 1) || (IEEEremainder(f1, f2) != 1))
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
      double d1 = 7.0;
      double d2 = 2.0;
      float f1 = 7.0f;
      float f2 = 2.0f;
      char pass = 1;
      if (remainder(d1, d2)!=1.0 || remainder(f1, f2)!=1.0f){
         pass = 0;
      }
      return;
   }
}
}OpenCL}**/
