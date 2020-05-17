package com.amd.aparapi.test;

public class AndOrPrecedence{
   public void run() {
      boolean a = true;
      boolean b = false;
      boolean c = false;
      @SuppressWarnings("unused") boolean pass = false;

      if (a || b && c) {
         pass = true;
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
      char b = 0;
      char c = 0;
      char pass = 0;
      if (a!=0 || b!=0 && c!=0){
         pass = 1;
      }
      return;
   }
}
}OpenCL}**/
