package com.amd.aparapi.test;

public class AssignAndPassAsParameterSimple{

   void actuallyDoIt(int a) {

   }

   public void run() {
      @SuppressWarnings("unused") int z;
      actuallyDoIt(z = 1);
   }
}
/**{Throws{ClassParseException}Throws}**/
