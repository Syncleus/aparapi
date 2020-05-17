package com.amd.aparapi.test;

public class ConstantAssignInExpression{

   void func(int _arg) {
      // nada
   }

   public void run() {
      @SuppressWarnings("unused") int result = 1;
      func(result = 0);

   }
}
/**{OpenCL{
typedef struct This_s{
   
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
   }

void com_amd_aparapi_test_ConstantAssignInExpression__func(This *this, int _arg){
   return;
}
__kernel void run(
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->passid = passid;
   {
      int result = 1;
      com_amd_aparapi_test_ConstantAssignInExpression__func(this, result=0);
      return;
   }
}
}OpenCL}**/
