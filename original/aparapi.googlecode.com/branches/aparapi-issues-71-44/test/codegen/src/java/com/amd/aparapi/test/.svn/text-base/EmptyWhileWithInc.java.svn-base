package com.amd.aparapi.test;

public class EmptyWhileWithInc{
   public void run() {
      int x = 0;
      while (x++ < 10) {
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
      for (int x = 0; x++<10;){}
      return;
   }
}
}OpenCL}**/
