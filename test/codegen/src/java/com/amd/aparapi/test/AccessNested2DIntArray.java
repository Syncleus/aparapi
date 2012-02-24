package com.amd.aparapi.test;

public class AccessNested2DIntArray{
   int[] indices = new int[1024];

   int[][] ints = new int[1024][];

   public void run() {
      int value = ints[indices[0]][0];
   }

}
/**{Throws{ClassParseException}Throws}**/
