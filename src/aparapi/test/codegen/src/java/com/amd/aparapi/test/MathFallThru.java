package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class MathFallThru extends Kernel{

   public void run() {
      float f1 = 1.0f;
      double d1 = 1.0;
      longout[0] = round(ceil(cos(exp(floor(log(pow(d1, d1)))))) + tan(sqrt(sin(rint(acos(asin(atan(atan2(d1, d1)))))))));
      intout[0] = round(ceil(cos(exp(floor(log(pow(f1, f1)))))) + tan(sqrt(sin(rint(acos(asin(atan(atan2(f1, f1)))))))));
      @SuppressWarnings("unused") boolean pass = false;
   }

   long longout[] = new long[1];

   int intout[] = new int[1];
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
      this->intout[0]  = round((ceil(cos(exp(floor(log(pow(f1, f1)))))) + tan(sqrt(sin(rint(acos(asin(atan(atan2(f1, f1))))))))));
      char pass = 0;
      return;
   }
}

}OpenCL}**/
