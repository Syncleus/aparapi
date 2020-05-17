package com.amd.aparapi.test;

public class ArbitraryScopeSimple{

   public void run(){
      int value = 10;
      {
         int count = 10;
         float f = 10f;
         value = (int) (count * f);
      }
      @SuppressWarnings("unused") int result = 0;
      int count = 0;
      result = value + count;

   }
}
/**{OpenCL{
 typedef struct This_s{
 int passid;
 }This;

 int get_pass_id(This *this){
 return this->passid;
 }

 __kernel void run(
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->passid = passid;
 {
 int i_1 = 10;
 int i_2 = 10;
 float f_3 = 10.0f;
 i_1 = (int)((float)i_2 * f_3);
 i_2 = 0;
 int i_3 = 0;
 i_2 = i_1 + i_3;
 return;
 }
 }
 }OpenCL}**/
