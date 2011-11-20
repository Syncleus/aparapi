package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class CallObjectStatic extends Kernel{
   static class Dummy{
      static public int foo() {
         return 42;
      }
   };

   public void run() {
      out[0] = Dummy.foo();
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
__kernel void run(
__global int *out, 
int passid
){
This thisStruct;
This* this=&thisStruct;
this->out = out;
this->passid = passid;
{
this->out[0] = foo();
return;
}
}
}OpenCL}**/
