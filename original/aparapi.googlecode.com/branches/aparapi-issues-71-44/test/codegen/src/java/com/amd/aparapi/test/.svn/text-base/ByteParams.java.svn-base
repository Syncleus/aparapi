package com.amd.aparapi.test;

public class ByteParams{

   // input for addEmUp2
   byte[] test = new byte[4];

   byte addEmUp2(byte x, byte y) {
      return (byte) ((byte) x + (byte) y);
   }

   public void run() {

      @SuppressWarnings("unused") byte bb = 0;
      byte cc = 7;

      addEmUp2((bb = cc), cc);
   }
}
/**{OpenCL{
typedef struct This_s{

   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
   }

char com_amd_aparapi_test_ByteParams__addEmUp2(This *this, char x, char y){
   return((char )(x + y));
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
      com_amd_aparapi_test_ByteParams__addEmUp2(this, bb = cc, cc);
      return;
   }
}
}OpenCL}**/
