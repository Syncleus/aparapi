package com.amd.aparapi.test;

public class PreIncByteField{

   byte z = (byte) 3;

   byte incByte(byte _a) {
      return ++_a;
   }

   public void run() {

      z = incByte(++z);

   }
}
/**{Throws{ClassParseException}Throws}**/
