package com.amd.aparapi.test;

public class CharAsParameter{

   public char doIt(char x) {
      return x;
   }

   public void run() {
      byte b = 0x1;

      doIt('A');

      doIt((char) b);
   }
}

/**{OpenCL{
      typedef struct This_s{
         int passid;
      }This;
      int get_pass_id(This *this){
         return this->passid;
      }
      unsigned short com_amd_aparapi_test_CharAsParameter__doIt(This *this, unsigned short x){
         return(x);
      }
      __kernel void run(
         int passid
      ){
         This thisStruct;
         This* this=&thisStruct;
         this->passid = passid;
         {
            char b = 1;
            com_amd_aparapi_test_CharAsParameter__doIt(this, 65);
            com_amd_aparapi_test_CharAsParameter__doIt(this, (unsigned short )b);
            return;
         }
      }
}OpenCL}**/
