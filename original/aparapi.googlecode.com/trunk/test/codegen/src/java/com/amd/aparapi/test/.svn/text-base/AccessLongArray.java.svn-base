package com.amd.aparapi.test;

public class AccessLongArray{
   long[] longs = new long[1024];

   public void run() {
      for (int i = 0; i < 1024; i++) {
         longs[i] = 1;
      }
   }
}
/**{OpenCL{
typedef struct This_s{
   __global long *longs;

   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
   }

__kernel void run(
   __global long *longs,
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->longs = longs;
   this->passid = passid;
   {
      for (int i = 0; i<1024; i++){
         this->longs[i]  = 1L;
      }
      return;
   }
}
}OpenCL}**/
