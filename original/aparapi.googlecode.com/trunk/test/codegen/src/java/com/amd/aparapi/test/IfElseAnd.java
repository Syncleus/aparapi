package com.amd.aparapi.test;

public class IfElseAnd{
   public void run() {
      int testValue = 10;
      @SuppressWarnings("unused") boolean pass = false;

      if (testValue >= 0 && testValue < 100) {
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
      int testValue = 10;
      char pass = 0;
      if (testValue>=0 && testValue<100){
         pass = 1;
      } else {
         pass = 0;
      }
      return;
   }
}
}OpenCL}**/
