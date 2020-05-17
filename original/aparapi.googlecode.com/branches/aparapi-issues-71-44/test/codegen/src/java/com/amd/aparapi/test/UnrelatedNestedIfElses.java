package com.amd.aparapi.test;

public class UnrelatedNestedIfElses{

   int width = 1024;

   float scale = 1f;

   int maxIterations = 10;

   public void run() {
      boolean a = true;
      boolean b = false;
      boolean c = true;
      @SuppressWarnings("unused") boolean result = false;

      if (a) {
         if (b) {
            result = true;
         } else {
            result = false;
         }
      } else {
         if (c) {
            result = true;
         } else {
            result = false;
         }
      }

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
      char a = 1;
      char b = 0;
      char c = 1;
      char result = 0;
      if (a!=0){
         if (b!=0){
            result = 1;
         } else {
            result = 0;
         }
      }else{
         if (c!=0){
            result = 1;
         } else {
            result = 0;
         }
      }
      return;
   }
}
}OpenCL}**/
