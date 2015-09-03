package com.amd.aparapi.sample.configuration;

/**
 * Created by Barney on 24/08/2015.
 */
public class KernelOkayInOpenCL extends com.amd.aparapi.Kernel {
   char[] inChars = "KernelOkayInOpenCL".toCharArray();
   char[] outChars = new char[inChars.length];

   @Override
   public void run() {
      int index = getGlobalId();
      oops();
      outChars[index] = inChars[index];
   }

   @NoCL
   private void oops() {
      System.out.println("Oops, running in kernel in Java");
   }
}
