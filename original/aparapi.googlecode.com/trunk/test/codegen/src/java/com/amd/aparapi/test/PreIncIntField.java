package com.amd.aparapi.test;

public class PreIncIntField{

   int y = 2;

   int preIncInt(int _a) {
      return ++_a;
   }

   public void run() {

      preIncInt(++y);

   }
}
/**{Throws{ClassParseException}Throws}**/
