package com.amd.aparapi.test;

public class Ex{
   public void run() {
      int total = 0;
      for (int i = 0; i < 100; i++) {
         if (i % 10 == 0 && i % 4 == 0) {
            total++;
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
      int total = 0;
      for (int i = 0; i<100; i++){
         if ((i % 10)==0 && (i % 4)==0){
            total++;
         }
      }
      return;
   }
}
}OpenCL}**/
