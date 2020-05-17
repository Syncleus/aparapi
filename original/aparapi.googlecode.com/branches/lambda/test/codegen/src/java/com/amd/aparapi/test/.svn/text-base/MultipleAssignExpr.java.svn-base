package com.amd.aparapi.test;

public class MultipleAssignExpr{

   int sum(int lhs, int rhs){
      return (lhs + rhs);
   }

   public void run(){
      int a = 0;
      int b = 0;
      int c = 0;
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

 int com_amd_aparapi_test_MultipleAssignExpr__sum(This *this, int i_1, int i_2){
 return((i_1 + i_2));
 }
 __kernel void run(
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->passid = passid;
 {
 int i_1 = 0;
 int i_2 = 0;
 int i_3 = 0;
 i_1 = i_2 = i_3 = com_amd_aparapi_test_MultipleAssignExpr__sum(this, 1, 2);
 return;
 }
 }
 }OpenCL}**/