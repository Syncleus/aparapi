package com.amd.aparapi.test;

public class FirstAssignInExpression2{

   public void run() {
      int value = 1;
      int assignMe;
      int result = 0;
      if (value == value) {
         result = assignMe = value;
      } else {
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
      int value = 1;
      int result=0;
      int assignMe=0;
      if (true){
         result = assignMe = value;
      }else{
         assignMe =1;
         result=2;
      }
      result++;
      return;
   }
}
}OpenCL}**/
