package com.amd.aparapi.test;

public class WhileFloatCompound{

   public float randomFunc() {

      return (1.0f);
   }

   public void run() {
      float v1 = 1f, v2 = 0f, s = 1f;

      while (s < 1 && s > 0) {
         v1 = randomFunc();
         v2 = randomFunc();
         s = v1 * v1 + v2 * v2;
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
float com_amd_aparapi_test_WhileFloatCompound__randomFunc(This *this){
   return(1.0f);
}
__kernel void run(
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->passid = passid;
   {
      float v1 = 1.0f;
      float v2 = 0.0f;
      float s = 1.0f;
      for (; s<1.0f && s>0.0f; s = (v1 * v1) + (v2 * v2)){
         v1 = com_amd_aparapi_test_WhileFloatCompound__randomFunc(this);
         v2 = com_amd_aparapi_test_WhileFloatCompound__randomFunc(this);
      }
      return;
   }
 }
}OpenCL}**/

