package com.amd.aparapi.test;

public class MultiContinue{
   public void run() {
      @SuppressWarnings("unused") boolean pass = false;
      for (int i = 0; i < 10; i++) {
         if (i == 5) {
            continue;
         } else {
            if (i == 2) {
               continue;
            }
            if (i == 1) {
               continue;
            }
         }
         if (i == 10) {
            continue;
         }
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
      char pass = 0;
      for (int i = 0; i<10; i++){
         if (i==5){
         } else {
            if (i==2){
            } else {
               if (i==1){
               } else {
                  if (i==10){
                  } else {
                     pass = 1;
                  }
               }
            }
         }
      }
      return;
   }
}
}OpenCL}**/
