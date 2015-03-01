package com.amd.aparapi.test.runtime;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

public class Issue69{

   public static void main(String[] args) {
      final int globalArray[] = new int[512];
      Kernel kernel = new Kernel(){
         @Override public void run() {
            globalArray[getGlobalId()] = getGlobalId();
         }
      };
      for (int loop = 0; loop < 100; loop++) {

         System.out.printf("%3d free = %10d\n", loop, Runtime.getRuntime().freeMemory());
         kernel.execute(Range.create(512, 64), 1);
         for (int i = 0; i < globalArray.length; ++i) {
            if (globalArray[i] != i)
               System.err.println("Wrong!");
         }
      }
      for (int loop = 0; loop < 100; loop++) {

         System.out.printf("%3d free = %10d\n", loop, Runtime.getRuntime().freeMemory());
         kernel.execute(Range.create(512, 64), 2);
         for (int i = 0; i < globalArray.length; ++i) {
            if (globalArray[i] != i)
               System.err.println("Wrong!");
         }
      }
   }

}
