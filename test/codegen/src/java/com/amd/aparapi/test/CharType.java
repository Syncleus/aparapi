package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class CharType extends Kernel{
   @Override public void run() {
      char c = Character.MAX_VALUE;
      out[0] = c;
   }

   int out[] = new int[1];
}

/**{Throws{ClassParseException}Throws}**/
