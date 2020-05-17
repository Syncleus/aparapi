package com.amd.aparapi.test;

public class ReturnByteArrayNew{

   byte[] returnByteArrayNew() {
      return new byte[1024];
   }

   public void run() {
      returnByteArrayNew();
   }
}
/**{Throws{ClassParseException}Throws}**/
