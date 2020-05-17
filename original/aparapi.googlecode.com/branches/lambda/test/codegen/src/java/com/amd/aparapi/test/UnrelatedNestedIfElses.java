package com.amd.aparapi.test;

public class UnrelatedNestedIfElses{

   int width = 1024;

   float scale = 1f;

   int maxIterations = 10;

   public void run(){
      boolean a = true;
      boolean b = false;
      boolean c = true;
      boolean result = false;

      if(a){
         if(b){
            result = true;
         }else{
            result = false;
         }
      }else{
         if(c){
            result = true;
         }else{
            result = false;
         }
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
 int i_2 = 0;
 int i_3 = 1;
 int i_4 = 0;
 if (i_1!=0){
 if (i_2!=0){
 i_4 = 1;
 } else {
 i_4 = 0;
 }
 } else {
 if (i_3!=0){
 i_4 = 1;
 } else {
 i_4 = 0;
 }
 }
 return;
 }
 }
 }OpenCL}**/