package com.amd.aparapi.test;

public class ReturnByteArrayVar{

   byte[] returnByteArrayVar() {
      byte[] bytes = new byte[1024];
      return bytes;
   }

   public void run() {

      returnByteArrayVar();
   }
}
/**{Throws{ClassParseException}Throws}**/
