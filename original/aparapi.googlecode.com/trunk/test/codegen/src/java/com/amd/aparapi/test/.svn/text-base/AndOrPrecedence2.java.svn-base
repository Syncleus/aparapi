package com.amd.aparapi.test;

public class AndOrPrecedence2{
   public void run() {
      boolean a = false;
      boolean b = false;
      boolean d = false;
      @SuppressWarnings("unused") boolean pass = false;

      if (a && !(b && d)) {
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
      char a = 0;
      char b = 0;
      char d = 0;
      char pass = 0;
      if (a!=0 && (b==0 || d==0)){
         pass = 1;
      }
      return;
   }
}
}OpenCL}**/
