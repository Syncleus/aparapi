package com.amd.aparapi.test;

public class ClassHasStaticFieldAccess{
   int[] ints = new int[1024];

   static int foo = 6;

   public void run() {
      for (int i = 0; i < 1024; i++) {
         if (i % 2 == 0) {
            ints[i] = foo;
         }
      }
   }
}
/**{Throws{ClassParseException}Throws}**/
