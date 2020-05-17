package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

final class DummyOOA{
   int mem;

   float floatField;

   long longField;

   boolean boolField;

   byte byteField;

   public DummyOOA() {
      mem = 8;
   }

   public boolean isBoolField() {
      return boolField;
   }

   public boolean getBoolField() {
      return boolField;
   }

   public void setBoolField(boolean x) {
      //boolField = x & true;
      boolField = x;
   }

   public int getMem() {
      return mem;
   }

   public void setMem(int x) {
      mem = x;
   }

   public float getFloatField() {
      return floatField;
   }

   public void setFloatField(float x) {
      floatField = x;
   }

   public long getLongField() {
      return longField;
   }

   public void setLongField(long x) {
      longField = x;
   }
};

final class TheOtherOne{
   int mem;

   public TheOtherOne() {
      mem = 8;
   }

   public int getMem() {
      return mem;
   }

   public void setMem(int x) {
      mem = x;
   }
};

public class ObjectArrayMemberGetterSetter extends Kernel{

   int out[] = new int[2];

   int something;

   DummyOOA dummy[] = null;

   TheOtherOne other[] = null;

   final int size = 64;

   public ObjectArrayMemberGetterSetter() {
      something = -1;
      dummy = new DummyOOA[size];
      other = new TheOtherOne[size];

      dummy[0] = new DummyOOA();
      other[0] = new TheOtherOne();
   }

   public int getSomething() {
      return something;
   }

   public int bar(int x) {
      return -x;
   }

   public void run() {
      int myId = getGlobalId();

      int tmp = dummy[myId].getMem();

      dummy[myId].setMem(dummy[myId].getMem() + 2);

      dummy[myId].setMem(other[myId].getMem() + getSomething());

      other[myId].setMem(other[myId].getMem() + getSomething());

      dummy[myId].setLongField(dummy[myId].getLongField() + 2);

      dummy[myId].setFloatField(dummy[myId].getFloatField() + (float) 2.0);

      dummy[myId].setBoolField(dummy[myId].getBoolField() | dummy[myId].isBoolField());

      out[myId] = getSomething();
   }
}
/**{OpenCL{
typedef struct com_amd_aparapi_test_TheOtherOne_s{
   int  mem;
   
} com_amd_aparapi_test_TheOtherOne;

typedef struct com_amd_aparapi_test_DummyOOA_s{
   long  longField;
   float  floatField;
   int  mem;
   char  boolField;
   char _pad_17;
   char _pad_18;
   char _pad_19;
   char _pad_20;
   char _pad_21;
   char _pad_22;
   char _pad_23;
   
} com_amd_aparapi_test_DummyOOA;

typedef struct This_s{
   int something;
   __global com_amd_aparapi_test_DummyOOA *dummy;
   __global com_amd_aparapi_test_TheOtherOne *other;
   __global int *out;
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
   }

void com_amd_aparapi_test_DummyOOA__setBoolField( __global com_amd_aparapi_test_DummyOOA *this, char x){
   this->boolField=x;
   return;
}
char com_amd_aparapi_test_DummyOOA__isBoolField( __global com_amd_aparapi_test_DummyOOA *this){
   return(this->boolField);
}
char com_amd_aparapi_test_DummyOOA__getBoolField( __global com_amd_aparapi_test_DummyOOA *this){
   return(this->boolField);
}
void com_amd_aparapi_test_DummyOOA__setFloatField( __global com_amd_aparapi_test_DummyOOA *this, float x){
   this->floatField=x;
   return;
}
float com_amd_aparapi_test_DummyOOA__getFloatField( __global com_amd_aparapi_test_DummyOOA *this){
   return(this->floatField);
}
void com_amd_aparapi_test_DummyOOA__setLongField( __global com_amd_aparapi_test_DummyOOA *this, long x){
   this->longField=x;
   return;
}
long com_amd_aparapi_test_DummyOOA__getLongField( __global com_amd_aparapi_test_DummyOOA *this){
   return(this->longField);
}
void com_amd_aparapi_test_TheOtherOne__setMem( __global com_amd_aparapi_test_TheOtherOne *this, int x){
   this->mem=x;
   return;
}
int com_amd_aparapi_test_ObjectArrayMemberGetterSetter__getSomething(This *this){
   return(this->something);
}
int com_amd_aparapi_test_TheOtherOne__getMem( __global com_amd_aparapi_test_TheOtherOne *this){
   return(this->mem);
}
void com_amd_aparapi_test_DummyOOA__setMem( __global com_amd_aparapi_test_DummyOOA *this, int x){
   this->mem=x;
   return;
}
int com_amd_aparapi_test_DummyOOA__getMem( __global com_amd_aparapi_test_DummyOOA *this){
   return(this->mem);
}
__kernel void run(
   int something, 
   __global com_amd_aparapi_test_DummyOOA *dummy, 
   __global com_amd_aparapi_test_TheOtherOne *other, 
   __global int *out,
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->something = something;
   this->dummy = dummy;
   this->other = other;
   this->out = out;
   this->passid = passid;
   {
      int myId = get_global_id(0);
      int tmp = com_amd_aparapi_test_DummyOOA__getMem( &(this->dummy[myId]));
      com_amd_aparapi_test_DummyOOA__setMem( &(this->dummy[myId]), (com_amd_aparapi_test_DummyOOA__getMem( &(this->dummy[myId])) + 2));
      com_amd_aparapi_test_DummyOOA__setMem( &(this->dummy[myId]), (com_amd_aparapi_test_TheOtherOne__getMem( &(this->other[myId])) + com_amd_aparapi_test_ObjectArrayMemberGetterSetter__getSomething(this)));
      com_amd_aparapi_test_TheOtherOne__setMem( &(this->other[myId]), (com_amd_aparapi_test_TheOtherOne__getMem( &(this->other[myId])) + com_amd_aparapi_test_ObjectArrayMemberGetterSetter__getSomething(this)));
      com_amd_aparapi_test_DummyOOA__setLongField( &(this->dummy[myId]), (com_amd_aparapi_test_DummyOOA__getLongField( &(this->dummy[myId])) + 2L));
      com_amd_aparapi_test_DummyOOA__setFloatField( &(this->dummy[myId]), (com_amd_aparapi_test_DummyOOA__getFloatField( &(this->dummy[myId])) + 2.0f));
      com_amd_aparapi_test_DummyOOA__setBoolField( &(this->dummy[myId]), (com_amd_aparapi_test_DummyOOA__getBoolField( &(this->dummy[myId])) | com_amd_aparapi_test_DummyOOA__isBoolField( &(this->dummy[myId]))));
      this->out[myId]  = com_amd_aparapi_test_ObjectArrayMemberGetterSetter__getSomething(this);
      return;
   }
}
}OpenCL}**/
