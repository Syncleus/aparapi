package com.amd.aparapi.test;

public class PostIncArrayItemFieldIndex{

   final static int START_SIZE = 128;

   public int[] values = new int[START_SIZE];

   public int[] results = new int[START_SIZE];

   public int a = 10;

   public void run() {
      values[a] = results[a]++;
   }
}
/**{OpenCL{
typedef struct This_s{
   __global int *values;
   int a;
   __global int *results;
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
   }

__kernel void run(
   __global int *values,
   int a,
   __global int *results,
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->values = values;
   this->a = a;
   this->results = results;
   this->passid = passid;
   {
      this->values[this->a]  = this->results[this->a]++;
      return;
   }
}
}OpenCL}**/
