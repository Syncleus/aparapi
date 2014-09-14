package com.amd.aparapi.sample.median;

public class MedianSettings {
   public final int windowWidth;
   public final int windowHeight;

   public MedianSettings(int windowSize) {
      this(windowSize, windowSize);
   }

   public MedianSettings(int windowWidth, int windowHeight) {
      this.windowWidth = windowWidth;
      this.windowHeight = windowHeight;
   }
}
