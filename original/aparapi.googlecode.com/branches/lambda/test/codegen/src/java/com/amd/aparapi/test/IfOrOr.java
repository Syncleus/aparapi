package com.amd.aparapi.test;

public class IfOrOr{
   public void run(){
      int testValue = 10;
      boolean pass = false;

      if(testValue >= 0 || testValue < 100 || testValue == 20){
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
 int i_1 = 10;
 int i_2 = 0;
 if (i_1>=0 || i_1<100 || i_1==20){
 i_2 = 1;
 }
 return;
 }
 }
 }OpenCL}**/