package com.amd.aparapi.test;

public class PreIncByte{

   byte preIncByte(byte a){
      return ++a;
   }

   public void run(){
      byte initValue = 0;
      byte result = preIncByte(++initValue);
   }
}


/**{OpenCL{
 typedef struct This_s{
 int passid;
 }This;

 int get_pass_id(This *this){
 return this->passid;
 }

 char com_amd_aparapi_test_PreIncByte__preIncByte(This *this, char i_1){
 i_1 = (char)(i_1 + 1);
 return(i_1);
 }
 __kernel void run(
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->passid = passid;
 {
 int i_1 = 0;
 int i_2 = com_amd_aparapi_test_PreIncByte__preIncByte(this, ++i_1);
 return;
 }
 }
 }OpenCL}**/