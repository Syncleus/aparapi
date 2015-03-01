package com.amd.aparapi.test;

public class Assign2DIntArray{
   int[][] ints = new int[1024][];

   public void run() {
      ints[0][0] = 1;
   }
}
/**{Throws{ClassParseException}Throws}**/
