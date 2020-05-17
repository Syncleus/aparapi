package com.amd.aparapi.test;

public class PostIncInt{

   int foo(int a){
      return a;
   }

   public void run(){
      int y = 2;
      foo(y++);
   }
}


/**{OpenCL{
 typedef struct This_s{
 int passid;
 }This;

 int get_pass_id(This *this){
 return this->passid;
 }

 int com_amd_aparapi_test_PostIncInt__foo(This *this, int i_1){
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
 com_amd_aparapi_test_PostIncInt__foo(this, i_1++);
 return;
 }
 }
 }OpenCL}**/