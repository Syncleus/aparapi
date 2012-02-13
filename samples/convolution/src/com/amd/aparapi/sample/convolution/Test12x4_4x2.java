package com.amd.aparapi.sample.convolution;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

public class Test12x4_4x2{
   public static void main(String[] _args) {
      // globalThreadId, threadId, globalX, globalY, localX, localY
      final int[][] test = new int[][] {
            {
                  0, //globalThreadId
                  0,//threadId
                  0,//globalX
                  0,//globalY
                  0,//localX
                  0
            //localY
            },
            {
                  1,//globalThreadId
                  1,//threadId
                  1,//globalX
                  0,//globalY
                  1,//localX
                  0
            //localY
            },
            {
                  2,//globalThreadId
                  2,//threadId
                  2,//globalX
                  0,//globalY
                  2,//localX
                  0
            //localY
            },
            {
                  3,//globalThreadId
                  3,//threadId
                  3,//globalX
                  0,//globalY
                  3,//localX
                  0
            //localY
            },
            {
                  4,//globalThreadId
                  4,//threadId
                  0,//globalX
                  1,//globalY
                  0,//localX
                  1
            //localY
            },
            {
                  5,//globalThreadId
                  5,//threadId
                  1,//globalX
                  1,//globalY
                  1,//localX
                  1
            //localY
            },
            {
                  6,//globalThreadId
                  6,//threadId
                  2,//globalX
                  1,//globalY
                  2,//localX
                  1
            //localY
            },
            {
                  7,//globalThreadId
                  7,//threadId
                  3,//globalX
                  1,//globalY
                  3,//localX
                  1
            //localY
            },
            {
                  8,//globalThreadId
                  0,//threadId
                  4,//globalX
                  0,//globalY
                  0,//localX
                  0
            //localY
            },
            {
                  9,//globalThreadId
                  1,//threadId
                  5,//globalX
                  0,//globalY
                  1,//localX
                  0
            //localY
            },
            {
                  10,//globalThreadId
                  2,//threadId
                  6,//globalX
                  0,//globalY
                  2,//localX
                  0
            //localY
            },
            {
                  11,//globalThreadId
                  3,//threadId
                  7,//globalX
                  0,//globalY
                  3,//localX
                  0
            //localY
            },
            {
                  12,//globalThreadId
                  4,//threadId
                  4,//globalX
                  1,//globalY
                  0,//localX
                  1
            //localY
            },
            {
                  13,//globalThreadId
                  5,//threadId
                  5,//globalX
                  1,//globalY
                  1,//localX
                  1
            //localY
            },
            {
                  14,//globalThreadId
                  6,//threadId
                  6,//globalX
                  1,//globalY
                  2,//localX
                  1
            //localY
            },
            {
                  15,//globalThreadId
                  7,//threadId
                  7,//globalX
                  1,//globalY
                  3,//localX
                  1
            //localY
            },
            {
                  16,//globalThreadId
                  0,//threadId
                  8,//globalX
                  0,//globalY
                  0,//localX
                  0
            //localY
            },
            {
                  17,//globalThreadId
                  1,//threadId
                  9,//globalX
                  0,//globalY
                  1,//localX
                  0
            //localY
            },
            {
                  18,//globalThreadId
                  2,//threadId
                  10,//globalX
                  0,//globalY
                  2,//localX
                  0
            //localY
            },
            {
                  19,//globalThreadId
                  3,//threadId
                  11,//globalX
                  0,//globalY
                  3,//localX
                  0
            //localY
            },

            {
                  20,//globalThreadId
                  4,//threadId
                  8,//globalX
                  1,//globalY
                  0,//localX
                  1
            //localY
            },
            {
                  21,//globalThreadId
                  5,//threadId
                  9,//globalX
                  1,//globalY
                  1,//localX
                  1
            //localY
            },
            {
                  22,//globalThreadId
                  6,//threadId
                  10,//globalX
                  1,
                  2,//localX
                  1
            //localY
            },
            {
                  23,//globalThreadId
                  7,//threadId
                  11,//globalX
                  1,//globalY
                  3,//localX
                  1
            //localY
            },
            {
                  24,//globalThreadId
                  0,//threadId
                  0,//globalX
                  2,//globalY
                  0,//localX
                  0
            //localY
            },
            {
                  25,//globalThreadId
                  1,//threadId
                  1,//globalX
                  2,//globalY
                  1,//localX
                  0
            //localY
            },
            {
                  26,//globalThreadId
                  2,//threadId
                  2,//globalX
                  2,//globalY
                  2,//localX
                  0
            //localY
            },
            {
                  27,//globalThreadId
                  3,//threadId
                  3,//globalX
                  2,//globalY
                  3,//localX
                  0
            //localY
            },
            {
                  28,//globalThreadId
                  4,//threadId
                  0,//globalX
                  3,//globalY
                  0,//localX
                  1
            //localY
            },
            {
                  29,//globalThreadId
                  5,//threadId
                  1,//globalX
                  3,//globalY
                  1,//localX
                  1
            //localY
            },
            {
                  30,//globalThreadId
                  6,//threadId
                  2,//globalX
                  3,//globalY
                  2,//localX
                  1
            //localY
            },
            {
                  31,//globalThreadId
                  7,//threadId
                  3,//globalX
                  3,//globalY
                  3,//localX
                  1
            //localY
            },
            {
                  32,//globalThreadId
                  0,//threadId
                  4,//globalX
                  2,//globalY
                  0,//localX
                  0
            //localY
            },
            {
                  33,//globalThreadId
                  1,//threadId
                  5,//globalX
                  2,//globalY
                  1,//localX
                  0
            //localY
            },
            {
                  34,//globalThreadId
                  2,//threadId
                  6,//globalX
                  2,//globalY
                  2,//localX
                  0
            //localY
            },
            {
                  35,//globalThreadId
                  3,//threadId
                  7,//globalX
                  2,//globalY
                  3,//localX
                  0
            //localY
            },
            {
                  36,//globalThreadId
                  4,//threadId
                  4,//globalX
                  3,//globalY
                  0,//localX
                  1
            //localY
            },
            {
                  37,//globalThreadId
                  5,//threadId
                  5,//globalX
                  3,//globalY
                  1,//localX
                  1
            //localY
            },
            {
                  38,//globalThreadId
                  6,//threadId
                  6,//globalX
                  3,//globalY
                  2,//localX
                  1
            //localY
            },
            {
                  39,//globalThreadId
                  7,//threadId
                  7,//globalX
                  3,//globalY
                  3,//localX
                  1
            //localY
            },
            {
                  40,//globalThreadId
                  0,//threadId
                  8,//globalX
                  2,//globalY
                  0,//localX
                  0
            //localY
            },
            {
                  41,//globalThreadId
                  1,//threadId
                  9,//globalX
                  2,//globalY
                  1,//localX
                  0
            //localY
            },
            {
                  42,//globalThreadId
                  2,//threadId
                  10,//globalX
                  2,//globalY
                  2,//localX
                  0
            //localY
            },
            {
                  43,//globalThreadId
                  3,//threadId
                  11,//globalX
                  2,//globalY
                  3,//localX
                  0
            //localY
            },

            {
                  44,//globalThreadId
                  4,//threadId
                  8,//globalX
                  3,//globalY
                  0,//localX
                  1
            //localY
            },
            {
                  45,//globalThreadId
                  5,//threadId
                  9,//globalX
                  3,//globalY
                  1,//localX
                  1
            //localY
            },
            {
                  46,//globalThreadId
                  6,//threadId
                  10,//globalX
                  3,//globalY
                  2,//localX
                  1
            //localY
            },
            {
                  47,//globalThreadId
                  7,//threadId
                  11,//globalX
                  3,//globalY
                  3,//localX
                  1
            //localY
            },
      };
      Kernel kernel = new Kernel(){

         @Override public void run() {
            int x = getGlobalId(0);
            int y = getGlobalId(1);
            int lx = getLocalId(0);
            int ly = getLocalId(1);
            int w = getGlobalSize(0);
            int h = getGlobalSize(1);
            int globalThreadId = getGlobalId(1) * getGlobalSize(0) + getGlobalId(0);
            int threadId = getLocalId(1) * getLocalSize(0) + getLocalId(0);
            synchronized (test) {
               boolean show = false;
               if (globalThreadId != test[globalThreadId][0]) {
                  System.out.println("bad globalThreadId");
                  show = true;
               }
               if (threadId != test[globalThreadId][1]) {
                  System.out.println("bad threadId");
                  show = true;
               }
               if (x != test[globalThreadId][2]) {
                  System.out.println("bad globalx");
                  show = true;
               }
               if (y != test[globalThreadId][3]) {
                  System.out.println("bad globaly");
                  show = true;
               }
               if (lx != test[globalThreadId][4]) {
                  System.out.println("bad localx");
                  show = true;
               }
               if (ly != test[globalThreadId][5]) {
                  System.out.println("bad localy");
                  show = true;
               }
               if (show) {
                  System.out.println("derived =>" + globalThreadId + " " + threadId + " " + x + "," + y + " " + lx + "," + ly + " "
                        + w + "," + h);
                  System.out.println("data    =>" + test[globalThreadId][0] + " " + test[globalThreadId][1] + " "
                        + test[globalThreadId][2] + "," + test[globalThreadId][3] + " " + test[globalThreadId][4] + ","
                        + test[globalThreadId][5] + " " + w + "," + h);
               }
            }
         }

      };
      kernel.execute(Range.create2D(12, 4, 4, 2));

   }
}
