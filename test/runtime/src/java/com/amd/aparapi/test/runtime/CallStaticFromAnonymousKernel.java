package com.amd.aparapi.test.runtime;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.amd.aparapi.Kernel;

class AnotherClass{
   static public int foo(int i) {
      return i + 42;
   }
};

public class CallStaticFromAnonymousKernel {

   static final int size = 256;

   // This method is a static target in the anonymous
   // kernel's containing class
   public static int fooBar(int i) {
      return i + 20; 
   }   

   @Test public void test() {
      final int[] values = new int[size];
      final int[] results = new int[size];

      for(int i=0; i<size; i++) {
         values[i] = i;
         results[i] = 0;
      }

      Kernel kernel = new Kernel() {

         // Verify codegen for resolving static call from run's callees
         public int doodoo(int i) {
            return AnotherClass.foo(i);
         }

         @Override public void run() {
            int gid = getGlobalId();

            // Call a static in the containing class and call a kernel method 
            // that calls a static in another class
            results[gid] = CallStaticFromAnonymousKernel.fooBar(values[gid]) + doodoo(gid);
         }   
      };  
      kernel.execute(size);

      assertTrue("ran on GPU", kernel.getExecutionMode()==Kernel.EXECUTION_MODE.GPU);

      for(int i=0; i<size; i++) {
         assertTrue( "results == fooBar", results[i] == (fooBar(values[i]) + AnotherClass.foo(i)));
      }
   }

   public static void main(String args[]) {
      CallStaticFromAnonymousKernel k = new CallStaticFromAnonymousKernel();
      k.test();
   }
}
