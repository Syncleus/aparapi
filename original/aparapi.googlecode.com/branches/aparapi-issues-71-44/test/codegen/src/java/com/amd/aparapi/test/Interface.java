package com.amd.aparapi.test;

public class Interface{

   public interface Operator{
      public double operate(double d);
   }

   public class SimpleAdder implements Operator{
      public double operate(double d) {
         return d + 1.0;
      }
   }

   public void run() {
      out[0] = sa.operate(0.0);
   }

   double out[] = new double[1];

   Operator sa = new SimpleAdder();
}
/**{Throws{ClassParseException}Throws}**/
