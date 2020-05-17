package com.amd.aparapi.test;

public class ReturnPostIncInt{

   int returnPostIncInt(int value) {

      return value++;
   }

   public void run() {
      returnPostIncInt(3);
   }
}
/**{OpenCL{
typedef struct This_s{
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
}
int com_amd_aparapi_test_ReturnPostIncInt__returnPostIncInt(This *this, int value){
   return(value++);
}
__kernel void run(
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->passid = passid;
   {
      com_amd_aparapi_test_ReturnPostIncInt__returnPostIncInt(this, 3);
      return;
   }
}
}OpenCL}**/
