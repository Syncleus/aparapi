package com.amd.aparapi.test;

public class PostIncArrayItem{

   final static int START_SIZE = 128;

   public int[] values = new int[START_SIZE];

   public int[] results = new int[START_SIZE];

   void actuallyDoIt(int a) {

   }

   public void run() {
      int a = 10;
      values[a] = results[a]++;
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
      int a = 10;
      this->values[a]  = this->results[a]++;
      return;
   }
}
}OpenCL}**/
