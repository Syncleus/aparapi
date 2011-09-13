package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class CharAsParameter extends Kernel{

   public char doIt(char x) {
      return x;
   }

   @Override public void run() {
      byte b = 0x1;

      doIt('A');

      doIt((char) b);

   }
}

/**{Throws{ClassParseException}Throws}**/
