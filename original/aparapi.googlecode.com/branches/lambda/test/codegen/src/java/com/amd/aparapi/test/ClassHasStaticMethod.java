package com.amd.aparapi.test;

public class ClassHasStaticMethod{
   int[] ints = new int[1024];

   static int getIntAndReturnIt(int a){
      return (int) (((int) 1) - a);
   }

   public void run(){
      int foo = 1;
      for(int i = 0; i < 1024; i++){
         if(i % 2 == 0){
            ints[i] = foo;
         }else{
            ints[i] = getIntAndReturnIt(foo);
            ;
         }
      }
   }
}
/**{OpenCL{
 typedef struct This_s{
 __global int* ints;
 int passid;
 }This;

 int get_pass_id(This *this){
 return this->passid;
 }

 int com_amd_aparapi_test_ClassHasStaticMethod__getIntAndReturnIt(int i_0){
 return((1 - i_0));
 }
 __kernel void run(
 __global int* ints,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->ints = ints;
 this->passid = passid;
 {
 int i_1 = 1;
 int i_2 = 0;
 for (; i_2<1024; i_2++){
 if ((i_2 % 2)==0){
 this->ints[i_2]  = i_1;
 } else {
 this->ints[i_2]  = com_amd_aparapi_test_ClassHasStaticMethod__getIntAndReturnIt(i_1);
 }
 }
 return;
 }
 }
 }OpenCL}**/

