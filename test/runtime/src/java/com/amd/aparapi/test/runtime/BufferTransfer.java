package com.amd.aparapi.test.runtime;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

public class BufferTransfer{
  
   
   interface Filler{
      void fill(int[] array, int index);
   }
   interface Comparer{
      boolean same(int[] lhs, int[]rhs, int index);
   }
   void fill(int[] array, Filler _filler ){
       for (int i=0; i<array.length; i++ ){
          _filler.fill(array, i);
       }
   }
   boolean same(int[] lhs, int[] rhs,  Comparer _comparer ){
      boolean same = lhs != null && rhs!=null && lhs.length==rhs.length;
      for (int i=0; same && i<lhs.length; i++ ){
         same = _comparer.same(lhs, rhs, i);
      }
      return(same);
  }
   void zero(int[] array){
      Arrays.fill(array, 0);
   }
   
   boolean same(int[] lhs, int[] rhs){
     return(same(lhs, rhs, new Comparer(){

      @Override public boolean same(int[] lhs, int[] rhs, int index) {
        
         return lhs[index]==rhs[index];
      }}));
   }
  
   
   public static class BufferTransferKernel extends Kernel{
      int[] inSmall;
      int[] inLarge;
      int[] inOutSmall;
      int[] inOutLarge;
      int[] outSmall;
      int[] outLarge;
      int pass;
      @Override public void run() {
         int gid = getGlobalId(0);
         outSmall[gid] = inSmall[gid];
         outLarge[gid*1024*16+pass] = inSmall[gid];
         
      }
      
   }
   
   @BeforeClass public static void setUpBeforeClass() throws Exception {
      System.out.println("setUpBeforeClass");
   }
   
   
   @AfterClass public static void tearDownAfterClass() throws Exception {
      System.out.println("tearDownAfterClass");
   }

   @Before public void setUp() throws Exception {
      System.out.println("setup");
   }

   @After public void tearDown() throws Exception {
      System.out.println("tearDown");
   }

  

   @Test public void once() {
      
     final int SMALL_SIZE=2048;
      final  int LARGE_SIZE=2048*1024*16;
      final  BufferTransferKernel bufferTransferKernel = new BufferTransferKernel();
      final  Range range = Range.create(LARGE_SIZE);
      final  Range smallRange = Range.create(SMALL_SIZE);
      
      bufferTransferKernel.inSmall = new int[SMALL_SIZE];
      bufferTransferKernel.inLarge = new int[LARGE_SIZE];
      bufferTransferKernel.inOutSmall = new int[SMALL_SIZE];
      bufferTransferKernel.inOutLarge = new int[LARGE_SIZE];
      bufferTransferKernel.outSmall = new int[SMALL_SIZE];
      bufferTransferKernel.outLarge = new int[LARGE_SIZE];
      
      zero(bufferTransferKernel.inSmall);
      zero(bufferTransferKernel.inOutSmall);
      zero(bufferTransferKernel.outSmall);
      zero(bufferTransferKernel.inLarge);
      zero(bufferTransferKernel.inOutLarge);
      zero(bufferTransferKernel.outLarge);
      
     // bufferTransferKernel.setExecutionMode(Kernel.EXECUTION_MODE.SEQ);
      fill(bufferTransferKernel.inSmall, new Filler(){
         public void fill(int[] array, int index) {
             array[index]=index;
         }});
      bufferTransferKernel.execute(smallRange);
      
      assertTrue("inSmall == outSmall", same(bufferTransferKernel.inSmall, bufferTransferKernel.outSmall));
      
      
      
      
   }

}
