package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class ObjectArrayMemberBadSetter extends Kernel{

   final class DummyOOA{
      int mem;

      float floatField;

      float theOtherFloatField;

      public float getFloatField() {
         return floatField;
      }

      public void setFloatField(float x) {
         theOtherFloatField = x;
      }
   };

   DummyOOA dummy[] = null;

   final int size = 64;

   public ObjectArrayMemberBadSetter() {
      dummy = new DummyOOA[size];

      dummy[0] = new DummyOOA();
   }

   public void run() {
      int myId = getGlobalId();
      dummy[myId].setFloatField(dummy[myId].getFloatField() + (float) 2.0);
   }
}

/**{Throws{ClassParseException}Throws}**/
