package com.amd.aparapi.test;

public class PostIncByte{

   byte incByte(byte a){
      return a++;
   }

   public void run(){
      byte startValue = (byte) 3;
      byte result = incByte(startValue++);
   }
}


/**{OpenCL{
 typedef struct This_s{
 int passid;
 }This;

 int get_pass_id(This *this){
 return this->passid;
 }

 char com_amd_aparapi_test_PostIncByte__incByte(This *this, char i_1){
 return(i_1++);
 }
 __kernel void run(
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->passid = passid;
 {
 int i_1 = 3;
 int i_2 = com_amd_aparapi_test_PostIncByte__incByte(this, i_1++);
 return;
 }
 }
 }OpenCL}**/