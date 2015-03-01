package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class IEEERemainderFloat extends Kernel{
   public void run() {
      out[0] = IEEEremainder(m, n);
   }

   float out[] = new float[10];

   float m;

   float n;
}

/**{OpenCL{
typedef struct This_s{
   __global float *out;
   float m;
   float n;
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
}
__kernel void run(
   __global float *out, 
   float m, 
   float n, 
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
