package com.amd.aparapi.test;

public class Access2DIntArray{
   int[][] ints = new int[1024][];

   public void run() {
      for (int i = 0; i < 1024; i++) {
         ints[i][0] = 1;
      }
   }
}
/**{Throws{ClassParseException}Throws}**/
