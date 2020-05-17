package com.amd.aparapi.test;

public class TernaryAndOr{
   float random() {
      return (.1f);
   }

   public void run() {

      @SuppressWarnings("unused") int count = random() == 0.f && (random() > .8f) || (random() < .2f) ? +1 : -1;
   }
}
/**{OpenCL{
typedef struct This_s{
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
}
float com_amd_aparapi_test_TernaryAndOr__random(This *this){
   return(0.1f);
}
__kernel void run(
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->passid = passid;
   {
      int count = (com_amd_aparapi_test_TernaryAndOr__random(this)==0.0f && com_amd_aparapi_test_TernaryAndOr__random(this)>0.8f || com_amd_aparapi_test_TernaryAndOr__random(this)<0.2f)?1:-1;
      return;
   }
}
}OpenCL}**/
