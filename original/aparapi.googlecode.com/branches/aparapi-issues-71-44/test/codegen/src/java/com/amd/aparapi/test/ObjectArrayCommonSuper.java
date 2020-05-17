package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class ObjectArrayCommonSuper extends Kernel{

   final static int size = 16;

   static class DummyParent{
      int intField;

      public DummyParent() {
         intField = -3;
      }

      public int getIntField() {
         return intField;
      }
   };

   final static class DummyBrother extends DummyParent{
      int brosInt;

      public int getBrosInt() {
         return brosInt;
      }
   };

   final static class DummySister extends DummyParent{
      int sisInt;

      public int getSisInt() {
         return sisInt;
      }
   };

   DummyBrother db[] = new DummyBrother[size];

   DummySister ds[] = new DummySister[size];

   public ObjectArrayCommonSuper() {
      db[0] = new DummyBrother();
      ds[0] = new DummySister();
   }

   public void run() {
      int myId = getGlobalId();
      db[myId].intField = db[myId].getIntField() + db[myId].getBrosInt();
      ds[myId].intField = ds[myId].getIntField() + ds[myId].getSisInt();
   }
}

/**{Throws{ClassParseException}Throws}**/
