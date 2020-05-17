package com.amd.aparapi.samples;
// http://cr.openjdk.java.net/~briangoetz/lambda/lambda-translation.html
public class WheresMyLambdaBytecode{
   interface SAM{
      void run();
   }

   public static void run(SAM sam){
      sam.run();
   }

   public static void main(String[] _args) {
      for (int i=0; i<10000; i++){
         run(()->{ System.out.println("here I am"); });
      }
   }

}
