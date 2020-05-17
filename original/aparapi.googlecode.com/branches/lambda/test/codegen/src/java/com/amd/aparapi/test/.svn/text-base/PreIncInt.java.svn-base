package com.amd.aparapi.test;

public class PreIncInt{

   int preIncInt(int a){
      return a;
   }

   public void run(){
      int y = 2;
      preIncInt(++y);
   }
}


/**{OpenCL{
 typedef struct This_s{
 int passid;
 }This;

 int get_pass_id(This *this){
 return this->passid;
 }

 int com_amd_aparapi_test_PreIncInt__preIncInt(This *this, int i_1){
 return(i_1);
 }
 __kernel void run(
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->passid = passid;
 {
 int i_1 = 2;
 com_amd_aparapi_test_PreIncInt__preIncInt(this, ++i_1);
 return;
 }
 }
 }OpenCL}**/