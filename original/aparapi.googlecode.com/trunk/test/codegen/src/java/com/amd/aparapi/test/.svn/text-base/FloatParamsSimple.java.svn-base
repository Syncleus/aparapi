package com.amd.aparapi.test;

public class FloatParamsSimple{

   void floatParams(float y) {

   }

   public void run() {

      floatParams(0f);

   }
}
/**{OpenCL{
typedef struct This_s{

   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
   }

void com_amd_aparapi_test_FloatParamsSimple__floatParams(This *this, float y){
   return;
}
__kernel void run(
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->passid = passid;
   {
      com_amd_aparapi_test_FloatParamsSimple__floatParams(this, 0.0f);
      return;
   }
}
}OpenCL}**/
