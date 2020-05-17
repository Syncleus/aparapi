package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class TwoForLoops extends Kernel{
   public void run() {
      for (int i = 0; i < size; i++) {
         a[i] = i;
      }

      int sum = 0;
      for (int i = 0; i < size; i++) {
         sum += a[i];
      }
   }

   final int size = 100;

   int a[] = new int[size];

}
/**{OpenCL{
typedef struct This_s{
   __global int *a;   
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
}

__kernel void run(
   __global int *a,
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->a = a;
   this->passid = passid;
   {
      for (int i = 0; i<100; i++){
         this->a[i]  = i;
      }
      int sum = 0;
      for (int i = 0; i<100; i++){
         sum = sum + this->a[i];
      }
      return;
   }
}
}OpenCL}**/
