package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class CallObjectStatic extends Kernel{
   static class Dummy{
      static public int foo() {
         return 42;
      }
   };

   public void run() {
      out[0] = Dummy.foo();
   }

   int out[] = new int[2];
}

/**{Throws{ClassParseException}Throws}**/
