package com.amd.aparapi.test;

public class IfBooleanAndAndAnd{
   public void run(){
      boolean a = true, b = true, c = true, d = true;
      boolean pass = false;

      if(a && b && c && d){
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
 int i_1 = 1;
 int i_2 = 1;
 int i_3 = 1;
 int i_4 = 1;
 int i_5 = 0;
 if (i_1!=0 && i_2!=0 && i_3!=0 && i_4!=0){
 i_5 = 1;
 }
 return;
 }
 }
 }OpenCL}**/