package com.amd.aparapi.test;

public class FirstAssignInExpression{

   void func(int _arg) {
      // nada
   }

   int y = 2;

   public void run() {
      int value = 1;
      @SuppressWarnings("unused") int result;
      func(result = value);

   }
}
/**{OpenCL{
typedef struct This_s{
   
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
   }

void func(This *this, int _arg){
   return;
}
__kernel void run(
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->passid = passid;
   {
      int result;
      func(this, result = 0);
      return;
   }
}
}OpenCL}**/
