package com.amd.aparapi.test;

public class PostIncLocalTwice{

   public void run() {
      @SuppressWarnings("unused") boolean pass = false;
      int i = 0;
      if (i++ + i++ == 1)
         pass = true;

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
      int i = 0;
      if ((i++ + i++)==1){
         pass = 1;
      }
      return;
   }
}
}OpenCL}**/
