package com.amd.aparapi.test;

public class WhileWithoutMutator{
   public void run() {
      int x = 0;
      while (x != 0) {
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
      int x = 0;
      for (; x!=0;){}
      return;
   }
 }
}OpenCL}**/
