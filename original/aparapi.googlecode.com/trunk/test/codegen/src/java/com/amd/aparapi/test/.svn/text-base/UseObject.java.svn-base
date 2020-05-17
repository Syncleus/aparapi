package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class UseObject extends Kernel{
   class Dummy{
      public int n;
   };

   Dummy dummy = new Dummy();

   public void run() {
      out[0] = dummy.n;
      out[1] = plainInt;
   }

   int out[] = new int[2];

   int plainInt = -1;

}
/**{Throws{ClassParseException}Throws}**/
