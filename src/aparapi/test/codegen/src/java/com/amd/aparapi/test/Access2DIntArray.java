package com.amd.aparapi.test;

public class Access2DIntArray{
   int[][] ints = new int[1024][];

   public void run() {
      int value = ints[0][0];
   }
}
/**{Throws{ClassParseException}Throws}**/
