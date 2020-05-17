package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

abstract class CallSuperBase extends Kernel{
   int foo(int n) {
      return n * 2;
   }
}

public class CallSuper extends CallSuperBase{
   public void run() {
      out[0] = foo(2);
   }

   int foo(int n) {
      return 1 + super.foo(n);
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

int com_amd_aparapi_test_CallSuperBase__foo(This *this, int n){
   return((n * 2));
}
int com_amd_aparapi_test_CallSuper__foo(This *this, int n){
   return((1 + com_amd_aparapi_test_CallSuperBase__foo(this, n)));
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
      this->out[0]  = com_amd_aparapi_test_CallSuper__foo(this, 2);
      return;
   }
}
}OpenCL}**/
