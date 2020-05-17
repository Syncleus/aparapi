package com.amd.aparapi.test;

public class IfElseIfElseIfElse{
   public void run(){
      boolean a = true;
      boolean b = true;
      boolean c = true;
      boolean result = false;

      if(a){
      }else if(b){
         result = true;
      }else if(c){
         result = true;
      }else{
         result = true;
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
 int i_3 = 1;
 int i_4 = 0;
 if (i_1!=0){
 } else {
 if (i_2!=0){
 i_4 = 1;
 } else {
 if (i_3!=0){
 i_4 = 1;
 } else {
 i_4 = 1;
 }
 }
 }
 return;
 }
 }
 }OpenCL}**/