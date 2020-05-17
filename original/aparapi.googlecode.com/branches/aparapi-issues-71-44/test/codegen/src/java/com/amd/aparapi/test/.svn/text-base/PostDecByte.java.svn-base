package com.amd.aparapi.test;

public class PostDecByte{

   /**
    * This is a nonsense test, but it should be emitted correctly to return the
    * original value of a
    */
   byte incByte(byte a) {
      return a++;
   }

   public void run() {
      byte startValue = (byte) 3;
      @SuppressWarnings("unused") byte result = incByte(startValue--);
   }
}
/**{OpenCL{
typedef struct This_s{
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
   }

char com_amd_aparapi_test_PostDecByte__incByte(This *this, char a){
   return(a++);
}
__kernel void run(
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->passid = passid;
   {
      char startValue = 3; 
      char result = com_amd_aparapi_test_PostDecByte__incByte(this, startValue--);
      return;
   }
}
}OpenCL}**/
