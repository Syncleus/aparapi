package com.amd.aparapi.test;

public class StaticFieldStore{
   int[] ints = new int[1024];

   static int foo = 6;

   public void run() {
      foo = ints[0];
   }
}
/**{Throws{ClassParseException}Throws}**/
