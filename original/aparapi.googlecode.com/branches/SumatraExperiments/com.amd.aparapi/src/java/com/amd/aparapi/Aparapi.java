package com.amd.aparapi;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
//import java.lang.invoke.InnerClassLambdaMetafactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.BrokenBarrierException;
import java.util.function.IntBlock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.List;

import com.amd.aparapi.ClassModel.ConstantPool.MethodEntry;
import com.amd.aparapi.InstructionSet.AccessField;
import com.amd.aparapi.InstructionSet.MethodCall;
import com.amd.aparapi.InstructionSet.VirtualMethodCall;

public class Aparapi{
   
   private static Logger logger = Logger.getLogger(Config.getLoggerName());

   public interface KernelI {
      void run(int x);
   }
   public interface KernelII {
      void run(int x, int y);
   }
   public interface KernelIII {
      void run(int x, int y, int id);
   }

   public interface KernelSAM {
      void run();
   }

   static void wait(CyclicBarrier barrier){
      try {
         barrier.await(); 
      } catch (InterruptedException ex) { 
      } catch (BrokenBarrierException ex) { 
      }
   }

/*
   static public void forEach(int width, int height, KernelII kernel){
      final int threads = Runtime.getRuntime().availableProcessors();
      final CyclicBarrier barrier = new CyclicBarrier(threads+1);
      for (int t=0; t<threads; t++){
         final int finalt = t;
         new Thread(()->{
            for (int x=finalt*(width/threads); x<(finalt+1)*(width/threads); x++){
               for (int y=0; y<height; y++){
                  kernel.run(x,y);
               }
            }
            wait(barrier);
         }).start();
      }
      wait(barrier);

   }
   static public void forEach(int width, KernelI kernel){
      final int threads = Runtime.getRuntime().availableProcessors();
      final CyclicBarrier barrier = new CyclicBarrier(threads+1);
      for (int t=0; t<threads; t++){
         final int finalt = t;
         new Thread(()->{
            for (int x=finalt*(width/threads); x<(finalt+1)*(width/threads); x++){
               kernel.run(x);
            }
            wait(barrier);
         }).start();
      }
      wait(barrier);
   }
*/
   
  
   static public void forEachJava(int jobSize, IntBlock block) {

      // Single threaded solution
      //for (int i=0; i<jobSize; i++) {
      //   block.accept(i);
      //}
      
      
    final int width = jobSize;
    final int threads = Runtime.getRuntime().availableProcessors();
    final CyclicBarrier barrier = new CyclicBarrier(threads+1);
    for (int t=0; t<threads; t++){
       final int finalt = t;
       new Thread(()->{
          for (int x=finalt*(width/threads); x<(finalt+1)*(width/threads); x++){
             block.accept(x);
          }   
          wait(barrier);
       }).start();
    }   
    wait(barrier);
      
   }
   
   
   static final ConcurrentHashMap<Class, KernelRunner> kernels = new ConcurrentHashMap<Class, KernelRunner>();
   static final ConcurrentHashMap<Class, Boolean> haveGoodKernel = new ConcurrentHashMap<Class, Boolean>();
   
   
   static public void forEach(int jobSize, IntBlock block) {
      
      // Note it is a new Block object each time
      
      KernelRunner kernelRunner = kernels.get(block.getClass());
      Boolean haveKernel = haveGoodKernel.get(block.getClass());
      
      try {

         if ((kernelRunner == null) && (haveKernel == null)) {
            kernelRunner = new KernelRunner(block);
         }

         if ((kernelRunner != null) && (kernelRunner.getRunnable() == true)) {
            boolean success = kernelRunner.execute(block, Range.create(jobSize), 1);
            if (success == true) {
               kernels.put(block.getClass(), kernelRunner);
               haveGoodKernel.put(block.getClass(), true);
            }
            kernelRunner.setRunnable(success);

         } else {
            forEachJava(jobSize, block);
         }

         return;
         
      } catch (AparapiException e) {
         System.err.println(e);
         e.printStackTrace();
         
         if (logger.isLoggable(Level.FINE)) {
            logger.fine("Kernel failed, try to revert to java.");
         }

         haveGoodKernel.put(block.getClass(), false);
         
         if (kernelRunner != null) {
            kernelRunner.setRunnable(false);
         }
      }
      
      if (logger.isLoggable(Level.FINE)) {
         logger.fine("Running java.");
      }
      
      forEachJava(jobSize, block);
   }
/*
   static public void forEach(int[][] intArray, KernelIII kernel){
      final int width = intArray.length;
      final int threads = Runtime.getRuntime().availableProcessors();
      final CyclicBarrier barrier = new CyclicBarrier(threads+1);
      for (int t=0; t<threads; t++){
         final int finalt = t;
         new Thread(()->{
            for (int x=finalt*(width/threads); x<(finalt+1)*(width/threads); x++){
               int[] arr = intArray[x];
               int arrLen = arr.length;
               for (int y=0; y<arrLen; y++){
                  kernel.run(x,y, arr[y]);
               }
            }
            wait(barrier);
         }).start();
      }
      wait(barrier);
   }
*/   
}

