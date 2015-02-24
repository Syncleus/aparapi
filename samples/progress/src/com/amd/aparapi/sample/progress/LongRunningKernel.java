package com.amd.aparapi.sample.progress;

import com.amd.aparapi.Kernel;

/**
 * Kernel which performs very many meaningless calculations, used to demonstrate progress tracking and cancellation of multi-pass Kernels.
 */
public class LongRunningKernel extends Kernel {

   public static final int RANGE = 20000;
   private static final int REPETITIONS = 1 * 1000 * 1000;

   public final long[] data = new long[RANGE];

   @Override
   public void run() {
      int id = getGlobalId();
      if (id == 0) {
         report();
      }
      for (int rep = 0; rep < REPETITIONS; ++rep) {
         data[id] += (int) sqrt(1);
      }
   }

   @NoCL
   public void report() {
      int passId = getPassId();
      System.out.println("Java execution: passId = " + passId);
   }
}
