package com.amd.aparapi.sample.configuration;

import com.amd.aparapi.*;
import com.amd.aparapi.internal.kernel.*;

/**
 * Tests device selection when circumventing the {@link com.amd.aparapi.internal.kernel.KernelManager} by using the legacy mechanism
 * (setExecutionMode, etc.).
 */
public class LegacyConfigurationDemo {

   @SuppressWarnings("deprecation")
   public static void main(String[] ignored) {
      System.setProperty("com.amd.aparapi.executionMode", "GPU,CPU,SEQ");
      System.setProperty("com.amd.aparapi.dumpProfilesOnExit", "true");

      KernelWithAlternateFallbackAlgorithm kernel = new KernelWithAlternateFallbackAlgorithm();
      kernel.setExecutionMode(Kernel.EXECUTION_MODE.GPU);
      int globalRange = 1;
      kernel.execute(globalRange);

      StringBuilder report = new StringBuilder("\n");
      KernelManager.instance().reportDeviceUsage(report, true);
      System.out.println(report);
   }
}
