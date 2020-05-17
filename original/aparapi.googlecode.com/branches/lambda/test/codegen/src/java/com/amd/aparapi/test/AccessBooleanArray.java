package com.amd.aparapi.test;

public class AccessBooleanArray{
   boolean[] ba = new boolean[1024];

   public void run(){
      for(int i = 0; i < 1024; i++){
         if(i % 2 == 0){
            ba[i] = true;
         }else{
            ba[i] = false;
         }
      }
   }
}
/**{OpenCL{
 typedef struct This_s{
 __global char* ba;
 int passid;
 }This;

 int get_pass_id(This *this){
 return this->passid;
 }

 __kernel void run(
 __global char* ba,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->ba = ba;
 this->passid = passid;
 {
 int i_1 = 0;
 for (; i_1<1024; i_1++){
 if ((i_1 % 2)==0){
 this->ba[i_1]  = 1;
 } else {
 this->ba[i_1]  = 0;
 }
 }
 return;
 }
 }
 }OpenCL}**/
