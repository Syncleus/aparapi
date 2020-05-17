package com.amd.aparapi.test;

public class If_If_Else2{
   public void run() {
      boolean a = true;
      boolean b = true;
      @SuppressWarnings("unused") boolean result = false;

      if (a && b) {
         result = true;
      } else {
         result = false;
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
      char b = 1;
      char result = 0;
      if (a!=0 && b!=0){
         result = 1;
      } else {
         result = 0;
      }
      return;
   }
}
}OpenCL}**/
