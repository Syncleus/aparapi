package com.amd.aparapi.test;

public class WideInc{

   public void run() {
      int value = 0;
      value += 128;
   }
}

/**{OpenCL{
typedef struct This_s{  
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
   }
__kernel void run(
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->passid = passid;
   {
      int value = 0;
      value+=128;
      return;
   }
}
}OpenCL}**/
