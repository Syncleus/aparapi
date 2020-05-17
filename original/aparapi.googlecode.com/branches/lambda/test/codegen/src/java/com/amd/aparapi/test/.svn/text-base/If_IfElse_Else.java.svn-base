package com.amd.aparapi.test;

public class If_IfElse_Else{
   public void run(){
      boolean a = true;
      boolean b = true;
      boolean result = false;

      if(a){
         if(b){
            result = true;
         }else{
            result = true;
         }
      }else{
         result = false;
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
 int i_2 = 1;
 int i_3 = 0;
 if (i_1!=0){
 if (i_2!=0){
 i_3 = 1;
 } else {
 i_3 = 1;
 }
 } else {
 i_3 = 0;
 }
 return;
 }
 }
 }OpenCL}**/