package com.amd.aparapi.test;

public class IfElse_And_Or_And{
   public void run() {
      int x = 5;
      int y = 5;

      @SuppressWarnings("unused") boolean pass = false;

      if ((x >= 0 && x < 10) || (y >= 0 && y < 10)) {
         pass = true;
      } else {
         pass = false;
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
      int x = 5;
      int y = 5;
      char pass = 0;
      if (x>=0 && x<10 || y>=0 && y<10){
         pass = 1;
      } else {
         pass = 0;
      }
      return;
   }
}
}OpenCL}**/
