package com.amd.aparapi.test;

public class Loops{
   public void run(){
      int sum = 0;

      for(int i = 0; i < 100; i++){
         sum = sum + ++i;
      }

      for(int i = 0; i < 100; i++){
         sum = sum + i++;
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
 int i_1 = 0;
 int i_2 = 0;
 for (; i_2<100; i_2++){
 i_1 = i_1 + ++i_2;
 }
 i_2 = 0;
 for (; i_2<100; i_2++){
 i_1 = i_1 + i_2++;
 }
 return;
 }
 }
 }OpenCL}**/