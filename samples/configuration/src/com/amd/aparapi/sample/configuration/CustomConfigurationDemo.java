package com.amd.aparapi.sample.configuration;

import com.amd.aparapi.device.*;
import com.amd.aparapi.internal.kernel.*;

import java.util.*;

/**
 * Created by Barney on 31/08/2015.
 */
public class CustomConfigurationDemo {

   public static void main(String[] ignored) {
      System.setProperty("com.amd.aparapi.dumpProfilesOnExit", "true");
      KernelManager manager = new KernelManager() {
         @Override
         protected List<Device.TYPE> getPreferredDeviceTypes() {
            return Arrays.asList(Device.TYPE.CPU, Device.TYPE.ALT, Device.TYPE.JTP);
         }
      };
      KernelManager.setKernelManager(manager);

      System.out.println("\nTesting custom KernelPreferences with kernel, preferences choose CPU");
      KernelOkayInOpenCL kernel = new KernelOkayInOpenCL();
      kernel.execute(kernel.inChars.length);
      System.out.println(kernel.outChars);

      System.out.println("\nTesting custom KernelPreferences with kernel, preferences specify CPU but kernel vetos CPU");
      kernel = new KernelOkayInOpenCL() {
         @Override
         public boolean isAllowDevice(Device _device) {
            return _device.getType() != Device.TYPE.CPU;
         }
      };
      kernel.execute(kernel.inChars.length);
      System.out.println(kernel.outChars);

      StringBuilder report = new StringBuilder("\n");
      KernelManager.instance().reportDeviceUsage(report, true);
      System.out.println(report);
   }
}
