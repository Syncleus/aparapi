package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class MathDegRad extends Kernel{
   public void run() {
      double d = -1.0;
      float f = -1.0f;
      @SuppressWarnings("unused") boolean pass = true;
      if ((toRadians(toDegrees(d)) != d) || (toRadians(toDegrees(f)) != f))
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
      char pass = 1;
      if (radians(degrees(d))!=d || radians(degrees(f))!=f){
         pass = 0;
      }
      return;
   }
}
}OpenCL}**/
