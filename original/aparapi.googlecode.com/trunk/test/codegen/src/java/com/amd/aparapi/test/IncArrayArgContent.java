package com.amd.aparapi.test;

public class IncArrayArgContent{

   int arr[] = new int[10];

   public void run() {

      incit(arr);
   }

   public void incit(int[] arr) {
      arr[0]++;

   }
}
/**{OpenCL{
typedef struct This_s{
   __global int *arr;
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
   }

void com_amd_aparapi_test_IncArrayArgContent__incit(This *this,  __global int* arr){
   arr[0]  = arr[0] + 1;
   return;
}
__kernel void run(
   __global int *arr,
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->arr = arr;
   this->passid = passid;
   {
      com_amd_aparapi_test_IncArrayArgContent__incit(this, this->arr);
      return;
   }
}
}OpenCL}**/
