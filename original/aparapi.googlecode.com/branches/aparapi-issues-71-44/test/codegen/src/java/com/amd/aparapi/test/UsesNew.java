package com.amd.aparapi.test;

public class UsesNew{
   int[] ints = new int[1024];

   public void run() {
      @SuppressWarnings("unused") int foo = 1;

      ints = new int[128];
   }
}
/**{Throws{ClassParseException}Throws}**/
