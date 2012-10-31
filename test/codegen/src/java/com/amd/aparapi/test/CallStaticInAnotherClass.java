package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;


class AnotherClass{
   static public int foo() {
      return 42;
   }
};

public class CallStaticInAnotherClass extends Kernel{

   public int doodoo() {
      return AnotherClass.foo();
   }

   public void run() {
      out[0] = AnotherClass.foo() + doodoo();
   }

   int out[] = new int[2];
}

/**{OpenCL{
typedef struct This_s{
   __global int *out;
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
}
int com_amd_aparapi_test_AnotherClass__foo(){
   return(42);
}
int com_amd_aparapi_test_CallStaticInAnotherClass__doodoo(This *this){
   return(com_amd_aparapi_test_AnotherClass__foo());
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
      this->out[0]  = com_amd_aparapi_test_AnotherClass__foo() + com_amd_aparapi_test_CallStaticInAnotherClass__doodoo(this);
      return;
   }
}

}OpenCL}**/
