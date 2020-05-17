package com.amd.aparapi.test;

public class ArbitraryScopeSimple{

   public void run() {
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
      int value = 10;
      {
         int count = 10;
         float f = 10.0f;
         value = (int)((float)count * f);
      }
      int result = 0;
      int count = 0;
      result = value + count;
      return;
   }
}

}OpenCL}**/
