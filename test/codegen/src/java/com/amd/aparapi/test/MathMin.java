package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class MathMin extends Kernel{
   public void run() {
      double d1 = -1.0, d2 = 1.0;
      float f1 = -1.0f, f2 = 1.0f;
      int i1 = -1, i2 = 1;
      long n1 = -1, n2 = 1;
      @SuppressWarnings("unused") boolean pass = true;
      if ((min(d1, d2) != 1) || (min(f1, f2) != 1) || (min(i1, i2) != 1) || (min(n1, n2) != 1))
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
      double d1 = -1.0;
      double d2 = 1.0;
      float f1 = -1.0f;
      float f2 = 1.0f;
      int i1 = -1;
      int i2 = 1;
      long n1 = -1L;
      long n2 = 1L;
      char pass = 1;
      if (fmin(d1, d2)!=1.0 || fmin(f1, f2)!=1.0f || min(i1, i2)!=1 || (min(n1, n2) - 1L)!=0){
         pass = 0;
      }
      return;
   }
}

}OpenCL}**/
