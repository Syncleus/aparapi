package com.amd.aparapi.test;

public class ByteParamsSimple{

   void addEmUp2(byte x, byte y) {

   }

   public void run() {

      byte bb = 0;
      byte cc = 7;

      addEmUp2(bb, cc);
   }
}
/**{OpenCL{
typedef struct This_s{

   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
   }

void com_amd_aparapi_test_ByteParamsSimple__addEmUp2(This *this, char x, char y){
   return;
}
__kernel void run(
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->passid = passid;
   {
      char bb = 0;
      char cc = 7;
      com_amd_aparapi_test_ByteParamsSimple__addEmUp2(this, bb, cc);
      return;
   }
}
}OpenCL}**/
