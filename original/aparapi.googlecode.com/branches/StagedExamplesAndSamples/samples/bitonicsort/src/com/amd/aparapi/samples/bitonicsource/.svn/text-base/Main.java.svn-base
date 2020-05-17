package com.amd.aparapi.samples.bitonicsort;

/*
 * @author Vivek Kumar Chaubey  - vivek.kumar.chaubey@gmail.com
 * 
 *  Progran : Bitonic Sort
 *  Date : 17th December 2011
 *  Keywords : Java, GPU , Aparapi
 */

import static java.lang.System.out;
import java.util.Random;
import com.amd.aparapi.Kernel;

public class Main extends Kernel {

   public static int array_size = 2<<20; // MUST BE A POWER OF 2.
   public static int max_value = Integer.MAX_VALUE/2;

   int[] theArray = new int[array_size]; // actual arrary to sort
   int stage=0; // View http://en.wikipedia.org/wiki/Bitonic_sorter to understand stages and passes
   int passOfStage=0; //pass
   int blockWidth=1; /// it is the half of a block which can be taken as independent in a pass.
   int stage_blockWidth; // max value of a blockwidth achieved in a stage. 

   public Main() {
      out.println("Initializing data...\n");
      Random random = new Random();
      for (int i = 0; i < array_size; i++) {
         theArray[i] = random.nextInt(max_value);
      }
   }

   @Override public void run() { 
      int threadId = getGlobalId();
      threadId = 2 * blockWidth * ((int) threadId / (blockWidth))
         + (threadId % (blockWidth));
      int temp=0;

      if ((threadId / (2 * stage_blockWidth)) % 2 == 0) {
         if (theArray[threadId] > theArray[threadId + blockWidth]) {
            temp = theArray[threadId];
            theArray[threadId] = theArray[threadId + blockWidth];
            theArray[threadId + blockWidth] = temp;
         }
      } else {
         if (theArray[threadId] < theArray[threadId + blockWidth]) {
            temp = theArray[threadId];
            theArray[threadId] = theArray[threadId + blockWidth];
            theArray[threadId + blockWidth] = temp;
         }
      }
   }

   public void sort() {

      int temp;
      assert Integer.highestOneBit(array_size)== Integer.lowestOneBit(array_size);
      int numStages = Integer.numberOfTrailingZeros(array_size);
      if (isExplicit()){
         put(theArray); // put theArray on the GPU
      } 

      long startTimeMs = System.currentTimeMillis();
      for (stage = 1; stage < numStages+1 ; stage++) { // looping stages
         stage_blockWidth = 1 << (stage - 1);
         for (passOfStage = 1; passOfStage < stage + 1; passOfStage++) { // looping passes, number of passes = numeric value of that stage
            blockWidth =  1 << (stage - passOfStage);
            execute(array_size / 2);

         }
      }
      if (isExplicit()){
         get(theArray); // pull theArray back from the GPU
      } 
      out.println("\n Time taken by kernel :"+(System.currentTimeMillis() - startTimeMs)+" milliseconds");

      boolean passed = true;

      for (int array_loop = 1; array_loop < array_size; array_loop++) {
         if (theArray[array_loop - 1] > theArray[array_loop]) {
            passed = false;
            out.println("fail! "+theArray[array_loop - 1]+" "+theArray[array_loop]);
         }

      }
      out.print("TEST "+(passed?"PASSED":"FAILED"));
   }

   public static void main(String[] args) {

      out.println("array_size "+ array_size);
      Main sort = new Main();
      //sort.setExecutionMode(Kernel.EXECUTION_MODE.CPU);
      sort.setExplicit(true); // toggle this to check the performance with/out explicit buffer management
      out.println("Execution mode=" + sort.getExecutionMode());
      sort.sort();
   }

}

