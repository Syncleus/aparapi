package com.amd.aparapi.test;

public class NonNullCheck{
   int[] ints = new int[1024];

   public void run() {
      if (ints != null){
         int value = ints[0];
      }
    
   }
}
/**{OpenCL{
typedef struct This_s{
   __global int *ints;
    int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
}

__kernel void run(
   __global int *ints,
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->ints = ints;
   this->passid = passid;
   {
      if (this->ints != NULL){
         int value = this->ints[0];
      }
      return;
   }
}
}OpenCL}**/
