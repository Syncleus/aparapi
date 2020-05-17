package com.amd.aparapi.test;

public class PreIncByte{

   byte preIncByte(byte a) {
      return ++a;
   }

   public void run() {
      byte initValue = 0;
      @SuppressWarnings("unused") byte result = preIncByte(++initValue);
   }
}
/**{OpenCL{
typedef struct This_s{
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
}
char com_amd_aparapi_test_PreIncByte__preIncByte(This *this, char a){
   a = (char )(a + 1);
   return(a);
}
__kernel void run(
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->passid = passid;
   {
      char initValue = 0;
      char result = com_amd_aparapi_test_PreIncByte__preIncByte(this, ++initValue);
      return;
   }
}
}OpenCL}**/

/**{OpenCL{
typedef struct This_s{
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
}
char com_amd_aparapi_test_PreIncByte__preIncByte(This *this, char a){
   return(a=(char )(a + 1));
}
__kernel void run(
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->passid = passid;
   {
      char initValue = 0;
      char result = com_amd_aparapi_test_PreIncByte__preIncByte(this, initValue=(char )(initValue + 1));
      return;
   }
}
}OpenCL}**/
