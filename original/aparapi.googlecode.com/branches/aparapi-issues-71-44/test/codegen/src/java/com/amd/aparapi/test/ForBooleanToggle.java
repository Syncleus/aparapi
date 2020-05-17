package com.amd.aparapi.test;

public class ForBooleanToggle{
   public void run() {
      boolean pass = false;
      for (int i = 0; i > 2 && i < 10; i++) {
         pass = !pass;
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
      for (int i = 0; i>2 && i<10; i++){
         pass = (pass==0)?1:0;
      }
      return;
   }
}
}OpenCL}**/

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
      for (int i = 0; i>2 && i<10; i++){
         pass = (pass!=0)?0:1;
      }
      return;
   }
}
}OpenCL}**/
