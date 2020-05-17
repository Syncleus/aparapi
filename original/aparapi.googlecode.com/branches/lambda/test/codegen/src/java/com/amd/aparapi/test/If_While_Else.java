package com.amd.aparapi.test;

public class If_While_Else{
   public void run(){
      boolean a = true;

      if(a){
         while(a){
            a = false;
         }
      }else{
         a = true;

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

 __kernel void run(
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->passid = passid;
 {
 int i_1 = 1;
 if (i_1!=0){
 for (; i_1!=0; i_1 = 0){
 }
 } else {
 i_1 = 1;
 }
 return;
 }
 }
 }OpenCL}**/