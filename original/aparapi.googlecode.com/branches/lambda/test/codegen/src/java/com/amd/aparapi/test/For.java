package com.amd.aparapi.test;

public class For{
   public void run(){
      @SuppressWarnings("unused") boolean pass = false;
      for(int i = 0; i < 10; i++){
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
 for (; i_2<10; i_2++){
 i_1 = 1;
 }
 return;
 }
 }
 }OpenCL}**/
