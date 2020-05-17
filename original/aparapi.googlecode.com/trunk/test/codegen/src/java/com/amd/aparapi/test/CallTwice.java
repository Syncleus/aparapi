package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class CallTwice extends Kernel{

   public int getOne() {
      return (1);
   }

   @Override public void run() {
      out[0] = getOne() + getOne();
   }

   int out[] = new int[1];
}

/**{OpenCL{
typedef struct This_s{
   __global int *out;
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
}
int com_amd_aparapi_test_CallTwice__getOne(This *this){
   return(1);
}
__kernel void run(
   __global int *out, 
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->out = out;
   this->passid = passid;
   {
      this->out[0]  = com_amd_aparapi_test_CallTwice__getOne(this) + com_amd_aparapi_test_CallTwice__getOne(this);
      return;
   }
}
}OpenCL}**/
