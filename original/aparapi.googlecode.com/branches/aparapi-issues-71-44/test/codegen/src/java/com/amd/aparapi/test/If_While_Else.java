package com.amd.aparapi.test;

public class If_While_Else{
   public void run() {
      boolean a = true;

      if (a) {
         while (a) {
            a = false;
         }
      } else {
         a = true;

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
      char a = 1;
      if (a!=0){
         for (; a!=0; a = 0){
         }
      } else {
         a = 1;
      }
      return;
   }
}
}OpenCL}**/
