package com.amd.aparapi.sample.extension;

public class StopWatch{
   long start = 0L;

   public void start() {
      start = System.nanoTime();
   }

   public void print(String _str) {
      long end = (System.nanoTime() - start) / 1000000;
      System.out.println(_str + " " + end);
   }

}
