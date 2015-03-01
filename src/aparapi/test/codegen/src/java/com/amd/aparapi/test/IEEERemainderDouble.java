package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class IEEERemainderDouble extends Kernel{
   public void run() {
      out[0] = IEEEremainder(m, n);
   }

   double out[] = new double[10];

   double m;

   double n;
}

/**{OpenCL{
#pragma OPENCL EXTENSION cl_khr_fp64 : enable

typedef struct This_s{
   __global double *out;
   double m;
   double n;
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
}
__kernel void run(
   __global double *out, 
   double m, 
   double n, 
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->out = out;
   this->m = m;
   this->n = n;
   this->passid = passid;
   {
      this->out[0]  = remainder(this->m, this->n);
      return;
   }
}

}OpenCL}**/
