package com.amd.aparapi.test;

public class IfBooleanAndAndAnd{
   public void run() {
      boolean a = true, b = true, c = true, d = true;
      @SuppressWarnings("unused") boolean pass = false;

      if (a && b && c && d) {
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
      char b = 1;
      char c = 1;
      char d = 1;
      char pass = 0;
      if (a!=0 && b!=0 && c!=0 && d!=0){
         pass = 1;
      }
      return;
   }
}
}OpenCL}**/
