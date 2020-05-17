package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

class OverriddenKernelFieldParent extends Kernel{
   int out[] = new int[1];

   int foo(int n) {
      out[0] = n + 1;
      return out[0];
   }

   public void run() {
      out[0] = foo(3);
   }
}

public class OverriddenKernelField extends OverriddenKernelFieldParent{
   public void run() {
      out[0] = foo(2);
   }

   int foo(int n) {
      return super.foo(n + 1);
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
int com_amd_aparapi_test_OverriddenKernelFieldParent__foo(This *this, int n){
   this->out[0]  = n + 1;
   return(this->out[0]);
}
int com_amd_aparapi_test_OverriddenKernelField__foo(This *this, int n){
   return(com_amd_aparapi_test_OverriddenKernelFieldParent__foo(this, (n + 1)));
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
      this->out[0]  = com_amd_aparapi_test_OverriddenKernelField__foo(this, 2);
      return;
   }
}
}OpenCL}**/
