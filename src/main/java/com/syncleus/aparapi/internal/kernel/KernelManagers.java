package com.syncleus.aparapi.internal.kernel;

import com.syncleus.aparapi.device.*;

import java.util.*;

/**
 * KernelManager instances useful for debugging.
 */
public class KernelManagers {

   public static final KernelManager JTP_ONLY = new KernelManager() {

      private List<Device.TYPE> types = Collections.singletonList(Device.TYPE.JTP);

      @Override
      protected List<Device.TYPE> getPreferredDeviceTypes() {
         return types;
      }
   };

   public static final KernelManager SEQUENTIAL_ONLY = new KernelManager() {

      private final List<Device.TYPE> types = Collections.singletonList(Device.TYPE.SEQ);

      @Override
      protected List<Device.TYPE> getPreferredDeviceTypes() {
         return types;
      }
   };
}
