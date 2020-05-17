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

public class BitonicSort extends Kernel {

        public static final int TRUE = 1;
        public static final int FALSE = 0;
        public static int array_size = 1048576*4; // MUST BE A POWER OF 2.
        //public static int array_size = 64;
        public static int max_value = 1000000;

        int[] theArray; // ACTUAL ARRAY
        int stage; // View http://en.wikipedia.org/wiki/Bitonic_sorter to understand stages and passes
        int passOfStage; //pass
        int blockWidth; /// it is the half of a block which can be taken as independent in a pass.
        int stage_blockWidth; // max value of a blockwidth achieved in a stage. 

        public BitonicSort() {

                out.println("Initializing data...\n");
                theArray = new int[array_size];
                Random random = new Random();
                for (int i = 0; i < theArray.length; i++) {
                        theArray[i] = random.nextInt(max_value);
                }

                stage = 0;
                passOfStage = 0;
                blockWidth = 1;
                
        }

        @Override
        public void run() { 

                int threadId = getGlobalId();
                int temp;
                threadId = 2 * blockWidth * ((int) threadId / (blockWidth))
                                + (threadId % (blockWidth));

                if (((int) (threadId / (2 * stage_blockWidth))) % 2 == 0) {
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

                int numStages = 0;
                int temp;
                long t0 = System.currentTimeMillis();
                for (temp = theArray.length; temp > 1; temp >>= 1)
                        ++numStages;

                for (stage = 1; stage < numStages + 1; stage++) { // looping stages

                        stage_blockWidth = (int) pow(2, (stage - 1));

                        for (passOfStage = 1; passOfStage < stage + 1; passOfStage++) { // looping passes, number of passes = numeric value of that stage
                                blockWidth = (int) pow(2, (stage - passOfStage));
                                execute(theArray.length / 2);

                        }
                }
                long t1 = System.currentTimeMillis();
                System.out.println("\n Time taken by kernel :"+(t1 - t0)+" milliseconds");
                
                for (int array_loop = 1; array_loop < theArray.length; array_loop++) {
                        if (theArray[array_loop - 1] > theArray[array_loop]) {

                                out.print("TEST FAILED");
                                return;
                        }

                }
                out.print("TEST PASSED");
        }

        public static void main(String[] args) {

                BitonicSort sort = new BitonicSort();
                sort.setExecutionMode(Kernel.EXECUTION_MODE.CPU);
                System.out.println("Execution mode=" + sort.getExecutionMode());
                sort.sort();
        }

}

/*
 * @author Vivek Kumar Chaubey  - vivek.kumar.chaubey@gmail.com
 * 
 *  Progran : Bitonic Sort
 *  Date : 17th December 2011
 *  Keywords : Java, GPU , Aparapi
 */

