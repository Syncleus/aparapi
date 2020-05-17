package com.amd.aparapi.test;

public class MultipleAssign{

   public void run() {
      @SuppressWarnings("unused") int a = 0;
      @SuppressWarnings("unused") int b = 0;
      @SuppressWarnings("unused") int c = 0;
      a = b = c = 4;

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
      int a = 0;
      int b = 0;
      int c = 0;
      a = b = c = 4;
      return;
   }
}
}OpenCL}**/
