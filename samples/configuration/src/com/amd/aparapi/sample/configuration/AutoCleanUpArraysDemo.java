package com.amd.aparapi.sample.configuration;

import com.amd.aparapi.sample.mandel.*;

public class AutoCleanUpArraysDemo {
   public static void main(String[] ignored) {

      System.setProperty("com.amd.aparapi.dumpProfileOnExecution", "true");

      int size = 1024;
      int[] rgbs = new int[size * size];
      Main.MandelKernel kernel = new Main.MandelKernel(size, size, rgbs);
      kernel.setAutoCleanUpArrays(true);
      kernel.execute(size * size);
      System.out.println("length = " + kernel.getRgbs().length);
      kernel.resetImage(size, size, rgbs);
      kernel.execute(size * size);
      System.out.println("length = " + kernel.getRgbs().length);
   }
}
