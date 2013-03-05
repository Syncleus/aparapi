package com.amd.aparapi.test;

public class ClassHasStaticMethodSimple{

   static void staticMethod() {

   }

   public void run() {
      staticMethod();

   }
}

/**{OpenCL{
typedef struct This_s{
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
}
void com_amd_aparapi_test_ClassHasStaticMethodSimple__staticMethod(){
   return;
}
__kernel void run(
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->passid = passid;
   {
      com_amd_aparapi_test_ClassHasStaticMethodSimple__staticMethod();
      return;
   }
}
}OpenCL}**/

