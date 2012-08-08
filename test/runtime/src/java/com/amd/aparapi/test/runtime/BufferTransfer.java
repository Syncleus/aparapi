package com.amd.aparapi.test.runtime;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amd.aparapi.Device;
import com.amd.aparapi.Kernel;
import com.amd.aparapi.OpenCLDevice;
import com.amd.aparapi.Range;

public class BufferTransfer{

   interface Filler{
      void fill(int[] array, int index);
   }

   interface Comparer{
      boolean same(int[] lhs, int[] rhs, int index);
   }

   interface Operator{
      void apply(int[] lhs, int[] rhs, int index);
   }

   void fill(int[] array, Filler _filler) {
      for (int i = 0; i < array.length; i++) {
         _filler.fill(array, i);
      }
   }

   boolean same(int[] lhs, int[] rhs, Comparer _comparer) {
      boolean same = lhs != null && rhs != null && lhs.length == rhs.length;
      for (int i = 0; same && i < lhs.length; i++) {
         same = _comparer.same(lhs, rhs, i);
      }
      return (same);
   }

   void zero(int[] array) {
      Arrays.fill(array, 0);
   }

   boolean same(int[] lhs, int[] rhs) {
      return (same(lhs, rhs, new Comparer(){

         @Override public boolean same(int[] lhs, int[] rhs, int index) {

            return lhs[index] == rhs[index];
         }
      }));
   }

   void apply(int[] lhs, int[] rhs, Operator _operator) {
      for (int i = 0; i < lhs.length; i++) {
         _operator.apply(lhs, rhs, i);
      }
   }

  

   static OpenCLDevice openCLDevice = null;

   @BeforeClass public static void setUpBeforeClass() throws Exception {
      //System.out.println("setUpBeforeClass");
      Device device = Device.best();
      if (device == null || !(device instanceof OpenCLDevice)) {
         throw new IllegalStateException("no opencl device!");
      }
      openCLDevice = (OpenCLDevice) device;
   }

   @AfterClass public static void tearDownAfterClass() throws Exception {
      //System.out.println("tearDownAfterClass");
   }

   @Before public void setUp() throws Exception {
      //System.out.println("setup");

   }

   @After public void tearDown() throws Exception {
      //System.out.println("tearDown");
   }

   public static class InOutKernel extends Kernel{

      int[] in;

      int[] out;

      @Override public void run() {
         int gid = getGlobalId(0);
         in[gid] = out[gid];

      }

   }

   @Test public void inOutOnce() {

      final int SIZE = 1024;
      final InOutKernel kernel = new InOutKernel();
      final Range range = openCLDevice.createRange(SIZE);

      kernel.in = new int[SIZE];
      kernel.out = new int[SIZE];

      fill(kernel.in, new Filler(){
         public void fill(int[] array, int index) {
            array[index] = index;
         }
      });
      kernel.execute(range);

      assertTrue("in == out", same(kernel.in, kernel.out));

   }

   public static class AddKernel extends Kernel{

      int[] values;

      int[] result;

      @Override public void run() {
         int gid = getGlobalId(0);
         result[gid] = result[gid] + values[gid];

      }

   }

   @Test public void auto() {

      final int SIZE = 1024;
      final AddKernel kernel = new AddKernel();
      final Range range = openCLDevice.createRange(SIZE);

      kernel.values = new int[SIZE];
      kernel.result = new int[SIZE];
      zero(kernel.result);
      fill(kernel.values, new Filler(){
         public void fill(int[] array, int index) {
            array[index] = index;
         }
      });

      int[] expectedResult = Arrays.copyOf(kernel.result, kernel.result.length);

      apply(expectedResult, kernel.values, new Operator(){

         @Override public void apply(int[] lhs, int[] rhs, int index) {
            lhs[index] = lhs[index] + rhs[index];

         }
      });
      kernel.execute(range);

      assertTrue("expectedResult == result", same(expectedResult, kernel.result));

      kernel.execute(range);

      apply(expectedResult, kernel.values, new Operator(){

         @Override public void apply(int[] lhs, int[] rhs, int index) {
            lhs[index] = lhs[index] + rhs[index];

         }
      });
      assertTrue("expectedResult == result", same(expectedResult, kernel.result));

      zero(kernel.values);
      kernel.execute(range);
      assertTrue("expectedResult == result", same(expectedResult, kernel.result));

   }

   @Test public void explicit() {

      final int SIZE = 1024;
      final AddKernel kernel = new AddKernel();
      kernel.setExplicit(true);
      final Range range = openCLDevice.createRange(SIZE);

      kernel.values = new int[SIZE];
      kernel.result = new int[SIZE];
      zero(kernel.result);
      fill(kernel.values, new Filler(){
         public void fill(int[] array, int index) {
            array[index] = index;
         }
      });

      int[] expectedResult = Arrays.copyOf(kernel.result, kernel.result.length);

      apply(expectedResult, kernel.values, new Operator(){

         @Override public void apply(int[] lhs, int[] rhs, int index) {
            lhs[index] = lhs[index] + rhs[index];

         }
      });

      kernel.execute(range).get(kernel.result);

      assertTrue("after first explicit add expectedResult == result", same(expectedResult, kernel.result));

      kernel.execute(range).get(kernel.result);

      apply(expectedResult, kernel.values, new Operator(){
         @Override public void apply(int[] lhs, int[] rhs, int index) {
            lhs[index] = lhs[index] + rhs[index];

         }
      });
      assertTrue("after second explicit add expectedResult == result", same(expectedResult, kernel.result));

      zero(kernel.values);

      kernel.put(kernel.values).execute(range).get(kernel.result);

      assertTrue("after zeroing values and third explici add expectedResult == result", same(expectedResult, kernel.result));

      zero(kernel.result);

      kernel.put(kernel.result).execute(range).get(kernel.result);

      zero(expectedResult);

      assertTrue("after zeroing values and result and forth  explicit add expectedResult == result",
            same(expectedResult, kernel.result));

   }

   private class TestKernel extends Kernel{
      int[] simStep = new int[1];

      int[] neuronOutputs = new int[3];
      
      int[] expected = new int []{3, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0};

      public void step() {
         int simSteps = 16;
         int[][] log = new int[neuronOutputs.length][simSteps];
         put(neuronOutputs);
         for (simStep[0] = 0; simStep[0] < simSteps; simStep[0]++) {
            put(simStep).execute(neuronOutputs.length).get(neuronOutputs);
            for (int n = 0; n < neuronOutputs.length; n++)
               log[n][simStep[0]] = neuronOutputs[n];
         }
         System.out.println(getExecutionMode() + (isExplicit() ? ", explicit" : ", auto"));
     
         for (int n = 0; n < neuronOutputs.length; n++)
            System.out.println(Arrays.toString(log[n]));
      
         assertTrue("log[2] == expected", same(log[2], expected));
      }

      @Override public void run() {
         int neuronID = getGlobalId();
         neuronOutputs[neuronID] = (simStep[0] % (neuronID + 2) == 0) ? (neuronID + 1) : 0;
      }
   }

   @Test public void issue60Explicit() {

      TestKernel kernel = new TestKernel();
      kernel.setExplicit(true);
      kernel.step();

   }
   
   @Test public void issue60Auto() {
      TestKernel kernel = new TestKernel();
      kernel.step();

   }

}
