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

   final int[] values = new int[size];
   final int[] results = new int[size];
   
   public CallStaticFromAnonymousKernel() {
      for(int i=0; i<size; i++) {
         values[i] = i;
         results[i] = 0;
      }
   }

   public static int fooBar(int i) {
      return i + 20; 
   }   

   @Test public void test() {
      Kernel kernel = new Kernel() {

         public int doodoo(int i) {
            return AnotherClass.foo(i);
         }

         @Override public void run() {
            int gid = getGlobalId();
               results[gid] = fooBar(values[gid]) + doodoo(gid);
         }   
      };  
      kernel.execute(size);

      for(int i=0; i<size; i++) {
	 System.out.println(results[i] + " == " + fooBar(values[i]) + AnotherClass.foo(i));
         assertTrue( "results == fooBar", results[i] == (fooBar(values[i]) + AnotherClass.foo(i)));
      }

   }
}


