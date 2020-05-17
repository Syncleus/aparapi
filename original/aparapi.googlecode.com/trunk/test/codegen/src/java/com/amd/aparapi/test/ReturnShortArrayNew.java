package com.amd.aparapi.test;

public class ReturnShortArrayNew{

   short[] returnShortArrayNew() {
      return new short[1024];
   }

   public void run() {
      returnShortArrayNew();
   }
}
/**{Throws{ClassParseException}Throws}**/
