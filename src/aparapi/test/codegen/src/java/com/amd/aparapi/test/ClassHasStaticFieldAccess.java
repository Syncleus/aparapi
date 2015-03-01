package com.amd.aparapi.test;

public class ClassHasStaticFieldAccess{
   int[] ints = new int[1024];

   static int foo = 6;

   public void run() {
      for (int i = 0; i < 1024; i++) {
         if (i % 2 == 0) {
            ints[i] = foo;
         }
      }
   }
}
/**{OpenCL{
typedef struct This_s{
   __global int *ints;
   int foo;
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
}
__kernel void run(
   __global int *ints, 
   int foo, 
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->ints = ints;
   this->foo = foo;
   this->passid = passid;
   {
      for (int i = 0; i<1024; i++){
         if ((i % 2)==0){
            this->ints[i]  = foo;
         }
      }
      return;
   }
}

}OpenCL}**/
