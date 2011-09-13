package com.amd.aparapi.test;

public class ClassHasStaticMethod{
   int[] ints = new int[1024];

   static int getIntAndReturnIt(int a) {
      return (int) (((int) 1) - a);
   }

   public void run() {
      int foo = 1;
      for (int i = 0; i < 1024; i++) {
         if (i % 2 == 0) {
            ints[i] = foo;
         } else {
            ints[i] = getIntAndReturnIt(foo);
            ;
         }
      }
   }
}
/**{Throws{ClassParseException}Throws}**/
