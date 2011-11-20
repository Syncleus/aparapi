package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class StaticMethodCall extends Kernel{
   public static int add(int i, int j) {
      return i+j;
   }
	
   public void run() {
      out[0] = add(1,2);
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
int com_amd_aparapi_test_StaticMethodCall__add(int i, int j){
   return((i + j));
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
      this->out[0]  = com_amd_aparapi_test_StaticMethodCall__add(1, 2);
      return;
   }
}

}OpenCL}**/
