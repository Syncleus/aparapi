package com.amd.aparapi.test;

public class ClassHasStaticMethod{
   int[] ints = new int[1024];

   static int getIntAndReturnIt(int a) {
      return (int) (((int) 1) - a);
   }

   public void run() {
      int foo = 1;
      for (int i = 0; i < 1024; i++) {
         if (i % 2 == 0) {
            ints[i] = foo;
         } else {
            ints[i] = getIntAndReturnIt(foo);
            ;
         }
      }
   }
}
/**{OpenCL{
typedef struct This_s{
__global int *ints;
int passid;
}This;
int get_pass_id(This *this){
return this->passid;
}
int com_amd_aparapi_test_ClassHasStaticMethod__getIntAndReturnIt(int a){
return((1 - a));
}
__kernel void run(
__global int *ints, 
int passid
){
This thisStruct;
This* this=&thisStruct;
this->ints = ints;
this->passid = passid;
{
int foo = 1;
for (int i = 0; i<1024; i++){
if ((i % 2)==0){
this->ints[i] = foo;
} else {
this->ints[i] = com_amd_aparapi_test_ClassHasStaticMethod__getIntAndReturnIt(foo);
}
}
return;
}
}
}OpenCL}**/

