package com.amd.aparapi.test;

public class ReturnPreIncInt{

   int returnPreIncInt(int value){
      return ++value;
   }

   public void run(){
      returnPreIncInt(3);
   }
}


/**{OpenCL{
 typedef struct This_s{
 int passid;
 }This;

 int get_pass_id(This *this){
 return this->passid;
 }

 int com_amd_aparapi_test_ReturnPreIncInt__returnPreIncInt(This *this, int i_1){
 i_1++;
 return(i_1);
 }
 __kernel void run(
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->passid = passid;
 {
 com_amd_aparapi_test_ReturnPreIncInt__returnPreIncInt(this, 3);
 return;
 }
 }
 }OpenCL}**/