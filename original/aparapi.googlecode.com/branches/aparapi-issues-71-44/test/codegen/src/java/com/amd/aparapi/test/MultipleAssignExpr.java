package com.amd.aparapi.test;

public class MultipleAssignExpr{

   int sum(int lhs, int rhs) {
      return (lhs + rhs);
   }

   public void run() {
      @SuppressWarnings("unused") int a = 0;
      @SuppressWarnings("unused") int b = 0;
      @SuppressWarnings("unused") int c = 0;
      a = b = c = sum(1, 2);

   }
}
/**{OpenCL{
typedef struct This_s{

   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
   }

int com_amd_aparapi_test_MultipleAssignExpr__sum(This *this, int lhs, int rhs){
   return((lhs + rhs));
}
__kernel void run(
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->passid = passid;
   {
      int a = 0;
      int b = 0;
      int c = 0;
      a = b = c = com_amd_aparapi_test_MultipleAssignExpr__sum(this, 1, 2);
      return;
   }
}
}OpenCL}**/
