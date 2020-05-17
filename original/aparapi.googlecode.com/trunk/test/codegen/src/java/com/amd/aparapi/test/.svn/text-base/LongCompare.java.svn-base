package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class LongCompare extends Kernel{
   public void run() {
      long n1 = 1;
      long n2 = 2;
      @SuppressWarnings("unused") boolean pass = false;
      if (n2 > n1)
         pass = true;
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
      long n1 = 1L;
      long n2 = 2L;
      char pass = 0;
      if ((n2 - n1)>0){
         pass = 1;
      }
      return;
   }
}

}OpenCL}**/
