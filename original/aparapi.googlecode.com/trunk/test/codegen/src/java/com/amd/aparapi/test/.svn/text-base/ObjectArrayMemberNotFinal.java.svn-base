package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

class DummyOOANF{
   int mem;

   float floatField;

   long longField;

   boolean boolField;

   byte byteField;

   public DummyOOANF() {
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

public class ObjectArrayMemberNotFinal extends Kernel{

   int out[] = new int[2];

   int something;

   DummyOOANF dummy[] = null;

   final int size = 64;

   public ObjectArrayMemberNotFinal() {
      something = -1;
      dummy = new DummyOOANF[size];

      dummy[0] = new DummyOOANF();
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
   }
}
/**{Throws{ClassParseException}Throws}**/
