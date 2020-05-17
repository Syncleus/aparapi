package com.amd.aparapi.test;

public class ContinueTorture{

   final static int START_SIZE = 128;

   public int[] values = new int[START_SIZE];

   public int[] results = new int[START_SIZE];

   int actuallyDoIt(int a) {
      return 1;
   }

   int actuallyDoIt2(int a) {
      return -1;
   }

   int myId = 34;

   public void run() {
      int idx = myId;
      while (--idx > 0) {

         if (myId == 0) {
            continue;
         }
         if (myId % 2 == 0) {
            results[myId] = actuallyDoIt(idx);
            continue;
         } else {
            results[myId] = actuallyDoIt2(idx);
            continue;
         }
      }
   }
}
//**{Throws{ClassParseException}Throws}**/