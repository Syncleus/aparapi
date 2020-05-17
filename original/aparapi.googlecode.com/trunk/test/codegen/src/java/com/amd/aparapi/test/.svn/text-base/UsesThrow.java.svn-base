package com.amd.aparapi.test;

public class UsesThrow{
   int[] ints = new int[1024];

   int doIt(int a) throws Exception {
      if (a < 0) {
         throw new Exception("Zoinks!");
      }
      return (int) (((int) 1) - a);
   }

   public void run() {
      @SuppressWarnings("unused") int foo = 1;
      try {
         for (int i = 0; i < 1024; i++) {
            if (i % 2 == 0) {
               ints[i] = doIt(i);
            }
         }
      } catch (Exception e) {
         // nothing
      }
   }
}
/**{Throws{ClassParseException}Throws}**/
