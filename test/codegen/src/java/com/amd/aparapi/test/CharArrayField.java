package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class CharArrayField extends Kernel{
   @Override public void run() {
      out[0] = 0;
   }

   char out[] = new char[1];
}

/**{Throws{ClassParseException}Throws}**/
