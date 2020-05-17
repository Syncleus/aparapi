package com.amd.aparapi.test;

public class Sequence{
   public void run() {
      @SuppressWarnings("unused") boolean pass = false;

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
      char pass = 0;
      return;
   }
}
}OpenCL}**/
