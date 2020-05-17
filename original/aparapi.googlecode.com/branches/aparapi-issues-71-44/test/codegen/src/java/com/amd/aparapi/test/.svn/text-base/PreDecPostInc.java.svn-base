package com.amd.aparapi.test;

public class PreDecPostInc{

   public void run() {
      int i = 0;
      @SuppressWarnings("unused") int result = 0;
      result = --i + i++;

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
      int i = 0;
      int result = 0;
      i--;
      result = i + i++;
      return;
   }
}
}OpenCL}**/
