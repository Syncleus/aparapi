package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class OverloadMethod extends Kernel{
   public void run() {
      out[0] = foo(2) + foo(2, 3);
   }

   int foo(int n) {
      return n + 1;
   }

   int foo(int a, int b) {
      return min(a, b);
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
int com_amd_aparapi_test_OverloadMethod__foo(This *this, int a, int b){
   return(min(a, b));
}
int com_amd_aparapi_test_OverloadMethod__foo(This *this, int n){
   return((n + 1));
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
      this->out[0]  = com_amd_aparapi_test_OverloadMethod__foo(this, 2) + com_amd_aparapi_test_OverloadMethod__foo(this, 2, 3);
      return;
   }
}
}OpenCL}**/
