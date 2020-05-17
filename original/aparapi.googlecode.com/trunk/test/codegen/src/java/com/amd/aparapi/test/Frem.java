package com.amd.aparapi.test;

public class Frem{
   public void run() {
      out[0] = m % n;
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
      this->out[0]  = this->m % this->n;
      return;
   }
}
}OpenCL}**/
