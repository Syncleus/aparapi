package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

class CallRunSuperBase extends Kernel{
   @Override public void run() {
      out[0] = 2;
   }

   int out[] = new int[2];
}

public class CallRunSuper extends CallRunSuperBase{
   public void run() {
      super.run();
      out[1] = 3;
   }

}
/**{OpenCL{
typedef struct This_s{
   __global int *out;
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
   }

void com_amd_aparapi_test_CallRunSuperBase__run(This *this){
   this->out[0]  = 2;
   return;
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
      com_amd_aparapi_test_CallRunSuperBase__run(this);
      this->out[1]  = 3;
      return;
   }
}
}OpenCL}**/
