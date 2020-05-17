package com.amd.aparapi.test;

public class WhileEmptyLoop{
   public void run() {
      int x = 10;
      while (x-- != 0) {
      }
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
      for (int x = 10; x--!=0;){}
      return;
   }
}
}OpenCL}**/
