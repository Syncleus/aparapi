package com.amd.aparapi.internal.model;

import com.amd.aparapi.Kernel;

public class CacheEnabler{
   private static volatile boolean cachesEnabled;

   public static void setCachesEnabled(boolean cachesEnabled) {
      if (CacheEnabler.cachesEnabled != cachesEnabled) {
         Kernel.invalidateCaches();
         ClassModel.invalidateCaches();
      }

      CacheEnabler.cachesEnabled = cachesEnabled;
   }

   public static boolean areCachesEnabled() {
      return cachesEnabled;
   }
}
