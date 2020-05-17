package com.amd.aparapi.test;

public class ForAsFirst{

   public void run(){

      for(int i = 0; i < 1; i++){

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
 for (; i_1<1; i_1++){
 }
 return;
 }
 }
 }OpenCL}**/
