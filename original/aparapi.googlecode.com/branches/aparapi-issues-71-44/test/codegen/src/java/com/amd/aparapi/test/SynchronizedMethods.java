package com.amd.aparapi.test;

public class SynchronizedMethods{
   int[] ints = new int[1024];

   synchronized int doIt(int a) {
      return (int) (((int) 1) - a);
   }

   int doIt2(int a) {
      return (int) (((int) 1) - a);
   }

   public void run() {
      int foo = 1;
      for (int i = 0; i < 1024; i++) {
         if (i % 2 == 0) {
            ints[i] = doIt(i);
         } else {
            synchronized (this) {
               ints[i] = doIt2(foo);
            }
         }
      }
   }
}
/**{Throws{ClassParseException}Throws}**/
