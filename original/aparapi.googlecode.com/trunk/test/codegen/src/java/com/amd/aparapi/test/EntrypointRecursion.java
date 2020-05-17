package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class EntrypointRecursion extends Kernel{

   int[] values = new int[128];

   public void run() {
      int id = getGlobalId();

      values[id]++;

      if (values[id] < 20) {
         run();
      }
   }

}
/**{Throws{ClassParseException}Throws}**/
