package com.amd.aparapi.test;

public class MultipleAssign{

   public void run(){
      int a = 0;
      int b = 0;
      int c = 0;
      a = b = c = 4;

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
 int i_3 = 0;
 i_1 = i_2 = i_3 = 4;
 return;
 }
 }
 }OpenCL}**/