package com.amd.aparapi.test;

public class PreDecArrayIndexAndElement{

   int array[] = new int[4];

   public void run(){
      int i = 0;
      --array[--i];
   }
}

/**{OpenCL{
 typedef struct This_s{
 __global int* array;
 int passid;
 }This;

 int get_pass_id(This *this){
 return this->passid;
 }

 __kernel void run(
 __global int* array,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->array = array;
 this->passid = passid;
 {
 int i_1 = 0;
 this->array[--i_1]  = this->array[i_1] - 1;
 return;
 }
 }
 }OpenCL}**/