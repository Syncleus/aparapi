package com.amd.aparapi.test;

public class WhileFloatCompound{

   public float randomFunc(){

      return (1.0f);
   }

   public void run(){
      float v1 = 1f, v2 = 0f, s = 1f;

      while(s < 1 && s > 0){
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
 float f_1 = 1.0f;
 float f_2 = 0.0f;
 float f_3 = 1.0f;
 for (; f_3<1.0f && f_3>0.0f; f_3 = (f_1 * f_1) + (f_2 * f_2)){
 f_1 = com_amd_aparapi_test_WhileFloatCompound__randomFunc(this);
 f_2 = com_amd_aparapi_test_WhileFloatCompound__randomFunc(this);
 }
 return;
 }
 }
 }OpenCL}**/