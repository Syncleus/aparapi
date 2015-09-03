package com.amd.aparapi.sample.configuration;

import com.amd.aparapi.*;

/**
 * Kernel which will always fail to run on an OpenCLDevice but has an alternative fallback algorithm.
 */
public class KernelWithAlternateFallbackAlgorithm extends Kernel {
   @Override
   public void run() {
      // deliberately, will fail to generate OpenCL as println is unsupported
      System.out.println("Running in Java (regular algorithm)");
   }

   @Override
   public boolean hasFallbackAlgorithm() {
      return true;
   }

   @Override
   public void executeFallbackAlgorithm(Range _range, int _passes) {
      System.out.println("Running in Java (alternate non-parallel algorithm)");
   }
}
