package com.amd.aparapi.test;

public class LongCompares{
   public void run(){

      boolean pass = false;
      long l1 = 1L;
      long l2 = 1L;
      if(l1 > l2){
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
 long l_2 = 1L;
 long l_4 = 1L;
 if ((l_2 - l_4)>0){
 i_1 = 1;
 }
 return;
 }
 }
 }OpenCL}**/