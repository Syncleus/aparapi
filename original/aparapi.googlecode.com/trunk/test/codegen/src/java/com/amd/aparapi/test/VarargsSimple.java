package com.amd.aparapi.test;

public class VarargsSimple{
   public static int max(int... values) {
      if (values.length == 0) {
         return 0;
      }

      int max = Integer.MIN_VALUE;
      for (int i = 0; i < values.length; i++) {
         if (values[i] > max)
            max = i;
      }
      return max;
   }

   public void run() {
      out[0] = max(1, 4, 5, 9, 3);
   }

   int out[] = new int[1];

}
/**{Throws{ClassParseException}Throws}**/
