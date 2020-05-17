package com.amd.aparapi.example.histogram;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

public class Main{

   public static void main(String args[]) throws IOException {
      final FileInputStream fis = new FileInputStream("C:\\\\Users\\gfrost\\Memory.DMP");
      // allocate a channel to read that file
      FileChannel fc = fis.getChannel();
      int SIZE = 128 * 1024 * 1024;
      final byte[] bytes = new byte[SIZE];

      ByteBuffer buffer = ByteBuffer.wrap(bytes);
      int bytesRead = fc.read(buffer);
      final int histogram[] = new int[256];

      long start = System.currentTimeMillis();
      for (byte b : bytes) {
         histogram[b & 0xff]++;
      }
      System.out.println("time " + (System.currentTimeMillis() - start));

      int[] histogramSequentialJavaReference = Arrays.copyOf(histogram, histogram.length);
      Arrays.fill(histogram, 0);
      if (false) {
         Range range1 = Range.create(bytes.length);
         System.out.println("Range 1 =" + range1);

         Kernel kernel1 = new Kernel(){

            @Override public void run() {

               this.atomicAdd(histogram, 0xff & bytes[getGlobalId(0)], 1);
            }

         };
         Arrays.fill(histogram, 0);
         kernel1.execute(range1);
         System.out.println("time " + (kernel1.getExecutionTime() - kernel1.getConversionTime()));
      }
      if (false) {
         final int len = bytes.length;
         final int chunks = 32;
         final int chunkSize = len / chunks;
         final Range range = Range.create(chunks);
         final int histogramSize = histogram.length;

         System.out.println("Range  =" + range);

         Kernel kernel = new Kernel(){
            @Local int[] localHistogram = new int[chunks * histogramSize]; // must be less than 16k apparently

            @Override public void run() {
               int chunk = getGlobalId(0);
               int start = chunk * chunkSize;
               int end = start + chunkSize;
               int localHistogramOffset = chunk * histogramSize;
               // This work item populates a histogram_$local$[localHistogramOffset]...histogram_$local$[localHistogramOffset+getLocalSize(0)] 
               for (int i = start; i < end; i++) {
                  int value = (0xff & bytes[i]);
                  localHistogram[localHistogramOffset + value]++;
               }
               this.localBarrier(); // all chunks sync here so local histogram has 'chunk' sub histograms.

               // we need to merge localHistogram[chunk1*histogramSize+0], localHistogram[chunk2*histogramSize+0 ... into histogram[0]  
               // each workitem handles histogram/chunks merges
               /* int merges = histogramSize/chunks;
                for (int index=0; index<merges; index++){
                   int offset = chunk*merges+index;
                   int total=0;
                   for (int i=0; i<chunks; i++){
                      total+=localHistogram[i*histogramSize+offset];  
                   }
                   histogram[offset]=total;
                }*/
            }

         };
         Arrays.fill(histogram, 0);
         kernel.execute(range);
         System.out.println("time " + (kernel.getExecutionTime() - kernel.getConversionTime()));
      }
      if (true) {
         final int chunkSize = 32;
         final int histogramSize = 256;
         Range range = Range.create(bytes.length / chunkSize, histogramSize);
         System.out.println("Range  =" + range);

         Kernel kernel = new Kernel(){
            @Local int[] localHistogram = new int[chunkSize * histogramSize];

            @Override public void run() {
               int chunk = getGlobalId(0);
               int chunkId = chunk % chunkSize;
               int start = chunk * chunkSize;
               int end = start + chunkSize;

               int localHistogramOffset = chunkId * histogramSize;
               // This work item populates a histogram_$local$[localHistogramOffset]...histogram_$local$[localHistogramOffset+getLocalSize(0)] 
               for (int i = start; i < end; i++) {
                  int value = (0xff & bytes[i]);
                  localHistogram[localHistogramOffset + value]++;
               }
               this.localBarrier(); // all chunks sync here so each groupId has contributed chunkSize updates to their own local histo
               int merges = histogramSize / chunkSize;
               for (int index = 0; index < merges; index++) {
                  int offset = chunk * merges + index;
                  int total = 0;
                  for (int i = 0; i < chunkSize; i++) {
                     total += localHistogram[i * histogramSize + offset];
                  }
                  this.atomicAdd(histogram, offset, total);

               }
            }

         };
         Arrays.fill(histogram, 0);
         kernel.execute(range);
         System.out.println("time " + (kernel.getExecutionTime() - kernel.getConversionTime()));
      }

      for (int i = 0; i < histogram.length; i++) {
         if (histogram[i] != histogramSequentialJavaReference[i]) {
            String label = null;

            switch (i) {
               case 0x9:
                  label = "ht";
                  break;
               case 0xa:
                  label = "nl";
                  break;
               case 0xd:
                  label = "cr";
                  break;
               default:
                  label = new String(new byte[] {
                     (byte) (i > 127 ? -255 + i : i)
                  });
            }

            System.out.printf("%3d %2x '%2s' %3d != %3d\n", i, i, label, histogramSequentialJavaReference[i], histogram[i]);
         }
      }
   }
}
