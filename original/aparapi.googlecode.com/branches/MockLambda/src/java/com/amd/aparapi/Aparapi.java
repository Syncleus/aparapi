package com.amd.aparapi;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

public class Aparapi{
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

   static public void forEach(int[] intArray, KernelII kernel){
      final int width = intArray.length;
      final int threads = Runtime.getRuntime().availableProcessors();
      final CyclicBarrier barrier = new CyclicBarrier(threads+1);
      for (int t=0; t<threads; t++){
         final int finalt = t;
         new Thread(()->{
            for (int x=finalt*(width/threads); x<(finalt+1)*(width/threads); x++){
               kernel.run(x,intArray[x]);
            }
            wait(barrier);
         }).start();
      }
      wait(barrier);
   }

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
}

