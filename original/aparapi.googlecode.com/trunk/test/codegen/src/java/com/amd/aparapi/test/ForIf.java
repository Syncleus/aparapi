package com.amd.aparapi.test;

public class ForIf{
   public void run() {
      @SuppressWarnings("unused") boolean pass = false;
      for (int i = 0; i > 2; i++) {
         if (i == 3) {
            pass = true;
         }
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
      char pass = 0;
      for (int i = 0; i>2; i++){
         if (i==3){
            pass = 1;
         }
      }
      return;
   }
}
}OpenCL}**/
