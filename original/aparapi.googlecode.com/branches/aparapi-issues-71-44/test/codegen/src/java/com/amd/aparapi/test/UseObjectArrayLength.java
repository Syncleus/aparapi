package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class UseObjectArrayLength extends Kernel{
   final class Dummy{
      public int n;
   };

   int out[] = new int[2];

   Dummy dummy[] = new Dummy[10];

   public void run() {
      out[0] = dummy.length;
   }
}
/**{OpenCL{
typedef struct This_s{
   __global int *out;
   __global com_amd_aparapi_test_UseObjectArrayLength$Dummy *dummy;
   int dummy__javaArrayLength;   
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
   }

__kernel void run(
   __global int *out,
   __global com_amd_aparapi_test_UseObjectArrayLength$Dummy *dummy, 
   int dummy__javaArrayLength,
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->out = out;
   this->dummy = dummy;
   this->dummy__javaArrayLength = dummy__javaArrayLength;
   this->passid = passid;
   {
      this->out[0]  = this->dummy__javaArrayLength;
      return;
   }
}
}OpenCL}**/
