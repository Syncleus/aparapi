package com.amd.aparapi.test;

public class Break{
   public void run() {
      @SuppressWarnings("unused") boolean pass = false;
      for (int i = 0; i < 10; i++) {
         if (i == 5) {
            break;
         }
         pass = true;
      }
   }
}
/**{Throws{ClassParseException}Throws}**/
