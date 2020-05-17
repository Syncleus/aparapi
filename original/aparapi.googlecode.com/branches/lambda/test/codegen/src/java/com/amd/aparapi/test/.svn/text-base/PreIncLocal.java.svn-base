package com.amd.aparapi.test;

public class PreIncLocal{

   public void run(){
      boolean pass = false;
      int i = 0;
      if(++i == 1){
         pass = true;
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
 i_2++;
 if (i_2==1){
 i_1 = 1;
 }
 return;
 }
 }
 }OpenCL}**/