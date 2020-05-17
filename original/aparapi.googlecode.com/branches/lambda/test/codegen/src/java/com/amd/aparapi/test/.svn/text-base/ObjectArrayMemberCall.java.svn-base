package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class ObjectArrayMemberCall extends Kernel{

   final static class DummyOOA{
      int mem;

      public DummyOOA(){
         mem = -3;
      }

      public int getMem(){
         return mem;
      }

      public void setMem(int x){
         mem = x;
      }

      public int addEmUp(int x, int y){
         return x + y;
      }

      public int addToMem(int x){
         return x + mem;
      }

      public int addEmUpPlusOne(int x, int y){
         return addEmUp(x, y) + 1 + getMem();
      }
   }

   ;

   int out[] = new int[2];

   int something;

   DummyOOA dummy[] = null;

   final int size = 64;

   public ObjectArrayMemberCall(){
      something = -1;
      dummy = new DummyOOA[size];

      dummy[0] = new DummyOOA();
   }

   public int getSomething(){
      return something;
   }

   public int bar(int x){
      return -x;
   }

   public void run(){
      int myId = getGlobalId();
      dummy[myId].mem = dummy[myId].addEmUp(dummy[myId].mem, 2);
      int tmp = dummy[myId].addToMem(2);
      int tmp2 = dummy[myId].addEmUpPlusOne(2, tmp);
   }
}


/**{OpenCL{
 typedef struct com_amd_aparapi_test_ObjectArrayMemberCall$DummyOOA_s{
 int mem;
 } com_amd_aparapi_test_ObjectArrayMemberCall$DummyOOA;

 typedef struct This_s{
 __global com_amd_aparapi_test_ObjectArrayMemberCall$DummyOOA* dummy;
 int passid;
 }This;

 int get_pass_id(This *this){
 return this->passid;
 }

 int com_amd_aparapi_test_ObjectArrayMemberCall$DummyOOA__getMem(__global com_amd_aparapi_test_ObjectArrayMemberCall$DummyOOA *this){
 return(this->mem);
 }
 int com_amd_aparapi_test_ObjectArrayMemberCall$DummyOOA__addEmUp(__global com_amd_aparapi_test_ObjectArrayMemberCall$DummyOOA *this, int i_1, int i_2){
 return((i_1 + i_2));
 }
 int com_amd_aparapi_test_ObjectArrayMemberCall$DummyOOA__addEmUpPlusOne(__global com_amd_aparapi_test_ObjectArrayMemberCall$DummyOOA *this, int i_1, int i_2){
 return(((com_amd_aparapi_test_ObjectArrayMemberCall$DummyOOA__addEmUp( &(this->elements[elements_array_index]), i_1, i_2) + 1) + com_amd_aparapi_test_ObjectArrayMemberCall$DummyOOA__getMem( &(this->elements[elements_array_index]))));
 }
 int com_amd_aparapi_test_ObjectArrayMemberCall$DummyOOA__addToMem(__global com_amd_aparapi_test_ObjectArrayMemberCall$DummyOOA *this, int i_1){
 return((i_1 + this->mem));
 }
 __kernel void run(
 __global com_amd_aparapi_test_ObjectArrayMemberCall$DummyOOA* dummy,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->dummy = dummy;
 this->passid = passid;
 {
 int i_1 = get_global_id(0);
 this->dummy[i_1].mem=com_amd_aparapi_test_ObjectArrayMemberCall$DummyOOA__addEmUp( &(this->dummy[i_1]), this->dummy[i_1].mem, 2);
 int i_2 = com_amd_aparapi_test_ObjectArrayMemberCall$DummyOOA__addToMem( &(this->dummy[i_1]), 2);
 int i_3 = com_amd_aparapi_test_ObjectArrayMemberCall$DummyOOA__addEmUpPlusOne( &(this->dummy[i_1]), 2, i_2);
 return;
 }
 }
 }OpenCL}**/