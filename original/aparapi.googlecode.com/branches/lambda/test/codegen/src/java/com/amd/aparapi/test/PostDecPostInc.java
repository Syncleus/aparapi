package com.amd.aparapi.test;

public class PostDecPostInc{

   public void run(){
      int i = 0;
      int result = 0;
      result = i-- + i++;

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
 i_2 = i_1-- + i_1++;
 return;
 }
 }
 }OpenCL}**/