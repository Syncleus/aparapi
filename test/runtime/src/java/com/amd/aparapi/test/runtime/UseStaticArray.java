package com.amd.aparapi.test.runtime;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.amd.aparapi.Kernel;


public class UseStaticArray extends Kernel {

   static final int size = 256;
   
   static final int[] values = new int[size];
   static final int[] results = new int[size];
   
   @Override public void run() {
      int gid = getGlobalId();
      results[gid] = values[gid];
   }   

   @Test public void test() {

      for(int i=0; i<size; i++) {
         values[i] = i;
         results[i] = 0;
      }

      execute(size);

      assertTrue("ran on GPU", getExecutionMode()==Kernel.EXECUTION_MODE.GPU);

      for(int i=0; i<size; i++) {
         assertTrue( "results == fooBar", results[i] == values[i] );
      }
   }

   public static void main(String args[]) {
      UseStaticArray k = new UseStaticArray();
      k.test();
   }
}
