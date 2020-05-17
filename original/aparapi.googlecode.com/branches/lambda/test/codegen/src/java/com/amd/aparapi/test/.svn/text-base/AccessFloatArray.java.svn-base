package com.amd.aparapi.test;

public class AccessFloatArray{
   float[] floats = new float[1024];

   public void run(){
      for(int i = 0; i < 1024; i++){
         floats[i] = 1f;
      }
   }
}
/**{OpenCL{
 typedef struct This_s{
 __global float* floats;
 int passid;
 }This;

 int get_pass_id(This *this){
 return this->passid;
 }

 __kernel void run(
 __global float* floats,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->floats = floats;
 this->passid = passid;
 {
 int i_1 = 0;
 for (; i_1<1024; i_1++){
 this->floats[i_1]  = 1.0f;
 }
 return;
 }
 }
 }OpenCL}**/
