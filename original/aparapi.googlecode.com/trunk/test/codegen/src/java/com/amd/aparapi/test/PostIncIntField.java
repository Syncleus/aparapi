package com.amd.aparapi.test;

public class PostIncIntField{

   int _y = 2;

   int incInt(int a) {
      return a++;
   }

   public void run() {

      incInt(_y++);

   }
}
/**{Throws{ClassParseException}Throws}**/
