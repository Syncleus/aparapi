package com.amd.aparapi.test;

public class AccessIntArray{
   int[] ints = new int[1024];

   public void run(){
      for(int i = 0; i < 1024; i++){
         ints[i] = 1;
      }
   }
}
/**{OpenCL{
 typedef struct This_s{
 __global int* ints;
 int passid;
 }This;

 int get_pass_id(This *this){
 return this->passid;
 }

 __kernel void run(
 __global int* ints,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->ints = ints;
 this->passid = passid;
 {
 int i_1 = 0;
 for (; i_1<1024; i_1++){
 this->ints[i_1]  = 1;
 }
 return;
 }
 }
 }OpenCL}**/
