package com.amd.aparapi.samples.bitonicsort;

import java.util.Random;
import java.util.Arrays;
import com.amd.aparapi.Kernel;

public class Main2{
   private final static String packageName = Main2.class.getPackage().getName();

   public static final int TRUE = 1;

   public static final int FALSE = 0;

   public static class BitonicSortKernel extends Kernel{

      int[] array;

      int stage;

      int passOfStage;

      int direction = TRUE;

      @Override public void run() {
         int sortIncreasing = direction;
         int threadId = getGlobalId();

         int pairDistance = 1 << (stage - passOfStage);
         int blockWidth = 2 * pairDistance;

         int leftId = (threadId % pairDistance) + (threadId / pairDistance) * blockWidth;

         int rightId = leftId + pairDistance;

         int leftElement = array[leftId];
         int rightElement = array[rightId];

         int sameDirectionBlockWidth = 1 << stage;

         if ((threadId / sameDirectionBlockWidth) % 2 == 1) {
            sortIncreasing = 1 - sortIncreasing;
         }

         int greater = rightElement;
         int lesser = leftElement;

         if (leftElement > rightElement) {
            greater = leftElement;
            lesser = rightElement;
         }

         if (sortIncreasing != 0) {
            array[leftId] = lesser;
            array[rightId] = greater;
         } else {
            array[leftId] = greater;
            array[rightId] = lesser;
         }

      }

      public void sortAparapi(final int[] _array) {

         int passOfStage;
         int stage;
         array = _array;
         int numStages = Integer.numberOfTrailingZeros(Integer.highestOneBit(array.length));

         for (stage = 0; stage < numStages; ++stage) {
            for (passOfStage = 0; passOfStage < stage + 1; ++passOfStage) {
               execute(array.length / 2);
            }
         }

         if (isExplicit()) {
            get(array);
         }
      }

      public void sortRef(final int[] _array) {
         int globalSize = _array.length;
         array = _array;
         final int halfLength = globalSize / 2;

         for (int i = 2; i <= globalSize; i *= 2) {
            for (int j = i; j > 1; j /= 2) {
               boolean increasing = true;
               final int half_j = j / 2;

               for (int k = 0; k < globalSize; k += j) {
                  final int k_plus_half_j = k + half_j;

                  if (i < array.length) {
                     if ((k == i) || ((k % i) == 0) && (k != halfLength))
                        increasing = !increasing;
                  }

                  for (int l = k; l < k_plus_half_j; ++l) {
                     if (increasing) {
                        if (array[l] > array[l + half_j]) {
                           int temp = array[l];
                           array[l] = array[l + half_j];
                           array[l + half_j] = temp;
                        }
                     } else {
                        if (array[l + half_j] > array[l]) {
                           int temp = array[l + half_j];
                           array[l + half_j] = array[l];
                           array[l] = temp;
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public Main2() {
   }

   public static void main(String[] args) {

      //   Main sort = new Main();
      int size = Integer.getInteger(packageName + ".size", 1 << 16);
      boolean validate = true;// Boolean.getBoolean(packageName + ".validate");
      boolean explicit = true;//Boolean.getBoolean(packageName + ".explicit");

      int[] unsorted = new int[size];
      int[] aparapiSortResult = new int[size];

      int[] refResult = new int[size];
      Random random = new Random();
      for (int i = 0; i < size; i++) {
         unsorted[i] = random.nextInt(1<<10);
      }

      BitonicSortKernel kernel = new BitonicSortKernel();
      //  boolean needRef = validate | kernel.getExecutionMode() == Kernel.EXECUTION_MODE.NONE;
      if (!explicit && kernel.getExecutionMode() == Kernel.EXECUTION_MODE.NONE) {
         System.exit(0);
      }
      System.out.printf("%9d", size);

      if (kernel.getExecutionMode() == Kernel.EXECUTION_MODE.NONE) {
         System.out.print(",    " + kernel.getExecutionMode());
      } else {

         System.out.print(", " + kernel.getExecutionMode());
         System.out.print((explicit ? "_EXP" : "_AUT"));
      }
      if (kernel.getExecutionMode() != Kernel.EXECUTION_MODE.NONE) {
         int[] small = new int[256];
         for (int i = 0; i < 256; i++) {
            small[i] = Math.abs((int) (Math.random() * 255.0));
         }
         kernel.setExplicit(explicit);

         kernel.sortAparapi(small);

         System.arraycopy(unsorted, 0, aparapiSortResult, 0, size);
         System.arraycopy(unsorted, 0, refResult, 0, size);
         // System.arraycopy(unsorted, 0, defaultJavaSort, 0, size);

         long t0 = System.currentTimeMillis();
         kernel.sortAparapi(aparapiSortResult);
         long t1 = System.currentTimeMillis();
         System.out.printf(", %5d", (t1 - t0));
         if (validate) {
            //kernel.sortRef(refResult);
             Arrays.sort(refResult);
            long t2 = System.currentTimeMillis();
            // long t3 = System.currentTimeMillis();

            System.out.printf(", %5d\n", (t2 - t1));
            //System.out.println(", " + +(t3 - t2));

            if (validate) {
               for (int i = 0; i < Math.min(512, size); i++) {
                  int index = (int) (Math.random() * size);
                  if (aparapiSortResult[index] != refResult[index]) {
                     System.out.printf("mismatch [%4d] unsorted=%4d aparapiSorted=%4d sequentialJavaSorted=%4d\n", index,
                           unsorted[index], aparapiSortResult[index], refResult[index]);
                  }
               }
            }
         } else {
            System.out.println();
         }
      } else {
         System.arraycopy(unsorted, 0, refResult, 0, size);
         long t0 = System.currentTimeMillis();
         kernel.sortRef(refResult);
         long t1 = System.currentTimeMillis();
         System.out.printf(", %5d\n", (t1 - t0));
      }

   }

}
