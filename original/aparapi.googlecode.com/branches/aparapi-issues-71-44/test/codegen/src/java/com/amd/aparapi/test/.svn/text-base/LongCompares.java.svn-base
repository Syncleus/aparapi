package com.amd.aparapi.test;

public class LongCompares{
   public void run() {

      @SuppressWarnings("unused") boolean pass = false;
      long l1 = 1L;
      long l2 = 1L;
      if (l1 > l2) {
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
      long l1 = 1L;
      long l2 = 1L;
      if ((l1 - l2)>0){
         pass = 1;
      }
      return;
   }
}
}OpenCL}**/
