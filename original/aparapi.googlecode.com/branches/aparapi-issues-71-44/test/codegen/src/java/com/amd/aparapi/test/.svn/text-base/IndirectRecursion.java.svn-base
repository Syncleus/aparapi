package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class IndirectRecursion extends Kernel{

   public void run() {
      intout[0] = foo(10);
      @SuppressWarnings("unused") boolean pass = false;
   }

   int foo(int n) {
      if (n > 0) {
         return bar(n);
      }
      return -1;
   }

   int bar(int n) {
      return foo(--n);
   }

   int intout[] = new int[1];

}
/**{Throws{ClassParseException}Throws}**/
