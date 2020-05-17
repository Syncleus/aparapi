package com.amd.aparapi.test;

public class Ternary{

   float random() {
      return (.1f);
   }

   public void run() {
      @SuppressWarnings("unused") int count = (random() > .5f) ? +1 : -1;
      @SuppressWarnings("unused") int foo = 3;
   }

}
/**{OpenCL{
typedef struct This_s{
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
}
float com_amd_aparapi_test_Ternary__random(This *this){
   return(0.1f);
}
__kernel void run(
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->passid = passid;
   {
      int count = (com_amd_aparapi_test_Ternary__random(this)>0.5f)?1:-1;
      int foo = 3;
      return;
   }
}
}OpenCL}**/
