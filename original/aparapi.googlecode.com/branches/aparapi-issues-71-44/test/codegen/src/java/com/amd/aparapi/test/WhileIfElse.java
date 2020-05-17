package com.amd.aparapi.test;

public class WhileIfElse{
   public void run() {

      int a = 0;
      int b = 0;
      int c = 0;
      int d = 0;

      while (a == a) {
         if (b == b) {
            c = c;
         } else {
            d = d;
         }
      }
   }

}
/**{OpenCL{
typedef struct This_s{
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
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
      int d = 0;
      for (; a==a; ){
         if (b==b){
            c = c;
         } else {
            d = d;
         }
      }
      return;
   }
}
}OpenCL}**/
