package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class CallGetPassId extends Kernel{
   public void run() {
      int thePassId = getPassId();
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
      int thePassId = get_pass_id(this);
      return;
   }
}

}OpenCL}**/
