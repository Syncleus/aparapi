package com.amd.aparapi.test;

public class BooleanToggle{
   public void run() {
      boolean pass = false;

      pass = !pass;

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
      pass = (pass==0)?1:0;
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
      pass = (pass!=0)?0:1;
      return;
   }
}
}OpenCL}**/
