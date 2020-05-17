package com.amd.aparapi.test;

public class FirstAssignInExpression2{

   public void run(){
      int value = 1;
      int assignMe;
      int result = 0;
      if(value == value){
         result = assignMe = value;
      }else{
         assignMe = 1;
         result = 2;
      }
      result++;
      assignMe++;

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
 int i_3 = 0;
 if (i_1==i_1){
 i_3 = inti_2 = i_1;
 } else {
 i_2 = 1;
 i_3 = 2;
 }
 i_3++;
 i_2++;
 return;
 }
 }
 }OpenCL}**/
