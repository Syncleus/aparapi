package com.amd.aparapi.test;

public class IfElseNot__OrOr_And_{
   public void run(){
      int testValue = 10;
      @SuppressWarnings("unused") boolean pass = false;

      if(!((testValue % 2 == 0 || testValue <= 0 || testValue >= 100) && testValue % 4 == 0)){
         pass = true;
      }else{
         pass = false;
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
 if ((i_1 % 2)!=0 && i_1>0 && i_1<100 || (i_1 % 4)!=0){
 i_2 = 1;
 } else {
 i_2 = 0;
 }
 return;
 }
 }
 }OpenCL}**/