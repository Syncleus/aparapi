package com.amd.aparapi.test;

public class IfAndAnd{
   public void run() {
      int testValue = 10;
      @SuppressWarnings("unused") boolean pass = false;

      if (testValue >= 0 && testValue < 100 && testValue == 20) {
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
      int testValue = 10;
      char pass = 0;
      if (testValue>=0 && testValue<100 && testValue==20){
         pass = 1;
      }
      return;
   }
}
}OpenCL}**/
