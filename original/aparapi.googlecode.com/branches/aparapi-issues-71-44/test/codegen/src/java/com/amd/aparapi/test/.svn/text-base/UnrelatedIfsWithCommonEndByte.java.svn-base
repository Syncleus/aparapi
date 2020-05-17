package com.amd.aparapi.test;

public class UnrelatedIfsWithCommonEndByte{
   int width = 1024;

   float scale = 1f;

   int maxIterations = 10;

   public void run() {
      boolean a1 = true;
      boolean a2 = true;
      boolean b = false;
      boolean c = true;
      boolean outer = true;
      @SuppressWarnings("unused") boolean result = false;
      if (outer) {
         if (a1 && !a2) {
            // result = true;
            if (b) {
               result = true;
            }
            //result = false;
            if (c) {
               result = true;
            }
            //  result = false;
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
      char a1 = 1;
      char a2 = 1;
      char b = 0;
      char c = 1;
      char outer = 1;
      char result = 0;
      if (outer!=0 && a1!=0 && a2==0){
         if (b!=0){
            result = 1;
         }
         if (c!=0){
            result = 1;
         }
      }
      return;
   }
}
}OpenCL}**/
