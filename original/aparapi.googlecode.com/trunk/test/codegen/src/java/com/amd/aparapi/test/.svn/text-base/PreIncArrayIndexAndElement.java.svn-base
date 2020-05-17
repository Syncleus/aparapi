package com.amd.aparapi.test;

public class PreIncArrayIndexAndElement{

   int array[] = new int[4];

   public void run() {
      int i = 0;
      ++array[++i];
   }
}
/**{OpenCL{
typedef struct This_s{
   __global int *array; 
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
   }

__kernel void run(
   __global int *array,
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->array = array;
   this->passid = passid;
   {
      int i = 0;
      this->array[++i]  = this->array[i] + 1;
      return;
   }
}
}OpenCL}**/
