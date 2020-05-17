package com.amd.aparapi.test;

public class AccessDoubleArray{
   double[] doubles = new double[1024];

   public void run(){

      for(int i = 0; i < 1024; i++){
         doubles[i] = 1.0;
      }
   }
}
/**{OpenCL{
 typedef struct This_s{
 __global double* doubles;
 int passid;
 }This;

 int get_pass_id(This *this){
 return this->passid;
 }

 __kernel void run(
 __global double* doubles,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->doubles = doubles;
 this->passid = passid;
 {
 int i_1 = 0;
 for (; i_1<1024; i_1++){
 this->doubles[i_1]  = 1.0;
 }
 return;
 }
 }
 }OpenCL}**/

/**{HSAIL{
 This is junk
 }HSAIL}**/
