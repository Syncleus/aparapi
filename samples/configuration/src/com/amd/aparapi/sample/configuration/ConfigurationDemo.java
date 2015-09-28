package com.amd.aparapi.sample.configuration;

import com.amd.aparapi.*;
import com.amd.aparapi.internal.kernel.*;

import java.util.*;

/**
 * Tests device selection via {@link com.amd.aparapi.internal.kernel.KernelManager}.
 */
public class ConfigurationDemo {
   public static void main(String[] ignored) {
      StringBuilder report;

      List<Integer> tests = Arrays.asList(0, 1, 2, 3);
      int reps = 1;
      for (int rep = 0; rep < reps; ++rep) {
         runTests(rep == 0, tests);

         if (rep % 100 == 99 || rep == 0 || rep == reps - 1) {
            report = new StringBuilder("rep = " + rep + "\n");
            KernelManager.instance().reportDeviceUsage(report, true);
            System.out.println(report);
         }
      }
   }

   private static void runTests(boolean verbose, List<Integer> testIndicesToRun) {
      final int globalSize = 1;
      Kernel kernel;
      if (testIndicesToRun.contains(0)) {
         if (verbose) {
            System.out.println();
            System.out.println("Testing default KernelPreferences with kernel which cannot be run in OpenCL, with fallback algorithm");
            System.out.println();
         }
         kernel = new KernelWithAlternateFallbackAlgorithm();
         kernel.execute(globalSize);
         kernel.dispose();
      }

      if (testIndicesToRun.contains(1)) {
         if (verbose) {
            System.out.println();
            System.out.println("Testing default KernelPreferences with kernel which cannot be run in OpenCL, without fallback algorithm");
            System.out.println();
         }
         kernel = new KernelWithoutAlternateFallbackAlgorithm();
         kernel.execute(globalSize);
         kernel.dispose();
      }

      if (testIndicesToRun.contains(2)) {
         if (verbose) {
            System.out.println();
            System.out.println("Retesting previous case, should jump straight to regular java implementation without warnings");
            System.out.println();
         }
         kernel = new KernelWithoutAlternateFallbackAlgorithm();
         kernel.execute(globalSize);
         kernel.dispose();
      }

      if (testIndicesToRun.contains(3)) {
         if (verbose) {
            System.out.println();
            System.out.println("Testing default KernelPreferences with kernel which should be run in OpenCL");
            System.out.println();
         }
         KernelOkayInOpenCL clKernel = new KernelOkayInOpenCL();
         kernel = clKernel;
         kernel.execute(clKernel.inChars.length);
         String result = new String(clKernel.outChars);
         if (verbose) {
            System.out.println("kernel output: " + result);
         }
         kernel.dispose();
      }
   }
}
