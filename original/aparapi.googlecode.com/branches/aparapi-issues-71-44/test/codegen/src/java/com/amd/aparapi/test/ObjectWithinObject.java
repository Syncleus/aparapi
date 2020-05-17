package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class ObjectWithinObject extends Kernel{

   final static class DummyOOA{
      int mem;

      float floatField;

      DummyOOA next;

   };

   final int size = 8;

   DummyOOA dummy[] = new DummyOOA[size];

   public void run() {
      int myId = getGlobalId();
      dummy[myId].mem = dummy[myId].next.mem + 4;
   }
}

/**{Throws{ClassParseException}Throws}**/
