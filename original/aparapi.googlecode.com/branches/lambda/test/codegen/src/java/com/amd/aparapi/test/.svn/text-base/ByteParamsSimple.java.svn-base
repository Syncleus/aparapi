package com.amd.aparapi.test;

public class ByteParamsSimple{

   void addEmUp2(byte x, byte y){

   }

   public void run(){

      byte bb = 0;
      byte cc = 7;

      addEmUp2(bb, cc);
   }
}

/**{OpenCL{
 typedef struct This_s{
 int passid;
 }This;

 int get_pass_id(This *this){
 return this->passid;
 }

 void com_amd_aparapi_test_ByteParamsSimple__addEmUp2(This *this, char i_1, char i_2){
 return;
 }
 __kernel void run(
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->passid = passid;
 {
 int i_1 = 0;
 int i_2 = 7;
 com_amd_aparapi_test_ByteParamsSimple__addEmUp2(this, i_1, i_2);
 return;
 }
 }
 }OpenCL}**/