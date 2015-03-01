package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class MathAbs extends Kernel{
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
