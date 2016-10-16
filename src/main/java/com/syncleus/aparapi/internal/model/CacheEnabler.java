package com.syncleus.aparapi.internal.model;

import com.syncleus.aparapi.Kernel;

public class CacheEnabler{
   private static volatile boolean cachesEnabled = true;

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
