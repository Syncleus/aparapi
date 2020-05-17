package com.amd.aparapi.test;

public class PreDecArrayItem{

   final static int START_SIZE = 128;

   public int[] values = new int[START_SIZE];

   public int[] results = new int[START_SIZE];

   public void run() {
      int y = 2;
      values[y] = --results[y];
   }
}
/**{OpenCL{
typedef struct This_s{
   __global int *values;
   __global int *results;
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
   }

__kernel void run(
   __global int *values, 
   __global int *results,
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->values = values;
   this->results = results;
   this->passid = passid;
   {
      int y = 2;
      this->values[y]  = --this->results[y];
      return;
   }
}

}OpenCL}**/
