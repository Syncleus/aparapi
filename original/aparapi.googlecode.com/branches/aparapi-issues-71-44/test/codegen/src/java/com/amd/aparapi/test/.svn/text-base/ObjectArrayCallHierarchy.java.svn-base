package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class ObjectArrayCallHierarchy extends Kernel{

   final static int size = 16;

   static class DummyParent{
      int intField;

      int field2;

      public DummyParent() {
         intField = -3;
         field2 = -4;
      }

      public int getIntField() {
         return intField;
      }

      public void setIntField(int x) {
         intField = x;
      }

      public void call2() {
         setIntField(intField + field2);
      }

   };

   final static class DummyOOA extends DummyParent{
      int intField;

      public void funnyCall() {
         setIntField(intField + getIntField());
         call2();
      }

      public int funnyGet() {
         funnyCall();
         setIntField(intField + getIntField());
         return intField + getIntField();
      }
   };

   int something;

   DummyOOA dummy[] = null;

   public ObjectArrayCallHierarchy() {
      something = -1;
      dummy = new DummyOOA[size];
      dummy[0] = new DummyOOA();
   }

   public int bar(int x) {
      return -x;
   }

   public void run() {
      int myId = getGlobalId();
      dummy[myId].intField = bar(2) + dummy[myId].funnyGet();
   }
}

/**{Throws{ClassParseException}Throws}**/
