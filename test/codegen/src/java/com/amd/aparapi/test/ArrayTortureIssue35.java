package com.amd.aparapi.test;

public class ArrayTortureIssue35{
   int[] a = new int[1];

   int[] b = new int[1];

   public void run() {
      a[b[0]++] = 1;
   }
}
/**{OpenCL{
typedef struct This_s{
   __global int *a;
   __global int *b;
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
   }

__kernel void run(
   __global int *a,
   __global int *b,
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->a = a;
   this->b = b;
   this->passid = passid;
   {
      this->a[this->b[0]++] = 1;
      return;
   }
}
}OpenCL}**/
