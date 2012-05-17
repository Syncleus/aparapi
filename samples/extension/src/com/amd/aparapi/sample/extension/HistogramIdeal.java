package com.amd.aparapi.sample.extension;

import java.util.Arrays;

import com.amd.aparapi.Device;
import com.amd.aparapi.Kernel;
import com.amd.aparapi.OpenCL;
import com.amd.aparapi.OpenCLDevice;
import com.amd.aparapi.Range;

public class HistogramIdeal{

   @OpenCL.Resource("com/amd/aparapi/sample/extension/HistogramKernel.cl") interface HistogramKernel extends
         OpenCL<HistogramKernel>{

      public HistogramKernel histogram256(//
            Range _range,//
            @GlobalReadOnly("data") byte[] data,//
            @Local("sharedArray") byte[] sharedArray,//
            @GlobalWriteOnly("binResult") int[] binResult,//
            @Arg("binSize") int binSize);

      public HistogramKernel bin256(//
            Range _range,//        
            @GlobalWriteOnly("histo") int[] histo,//
            @GlobalReadOnly("binResult") int[] binResult,//
            @Arg("subHistogramSize") int subHistogramSize);
   }

   public static void main(String[] args) {
      final int WIDTH = 1024 * 16;
      final int HEIGHT = 1024 * 8;
      final int BIN_SIZE = 128;
      final int GROUP_SIZE = 128;
      final int SUB_HISTOGRAM_COUNT = ((WIDTH * HEIGHT) / (GROUP_SIZE * BIN_SIZE));

      byte[] data = new byte[WIDTH * HEIGHT];
      for (int i = 0; i < WIDTH * HEIGHT; i++) {
         data[i] = (byte) (Math.random() * BIN_SIZE / 2);
      }
      byte[] sharedArray = new byte[GROUP_SIZE * BIN_SIZE];
      final int[] binResult = new int[SUB_HISTOGRAM_COUNT * BIN_SIZE];
      System.out.println("binResult size=" + binResult.length);
      final int[] histo = new int[BIN_SIZE];
      int[] refHisto = new int[BIN_SIZE];
      Device device = Device.best();
    
      if (device != null) {
         
         System.out.println(((OpenCLDevice)device).getPlatform().getName());
         Range rangeBinSize = device.createRange(BIN_SIZE);

         Range range = Range.create((WIDTH * HEIGHT) / BIN_SIZE, GROUP_SIZE);

         if (device instanceof OpenCLDevice) {
            OpenCLDevice openclDevice = (OpenCLDevice) device;

            HistogramKernel histogram = openclDevice.create(HistogramKernel.class);
            long start = System.nanoTime();
            histogram.begin()//
                  .put(data)//
                  .histogram256(range, data, sharedArray, binResult, BIN_SIZE)//
                  // by leaving binResult on the GPU we can save two 1Mb transfers
                  .bin256(rangeBinSize, histo, binResult, SUB_HISTOGRAM_COUNT)//
                  .get(histo)//
                  .end();
            System.out.println("opencl " + ((System.nanoTime() - start) / 1000000));
            start = System.nanoTime();
            for (int i = 0; i < WIDTH * HEIGHT; i++) {
               refHisto[data[i]]++;
            }
            System.out.println("java " + ((System.nanoTime() - start) / 1000000));
            for (int i = 0; i < 128; i++) {
               if (refHisto[i] != histo[i]) {
                  System.out.println(i + " " + histo[i] + " " + refHisto[i]);
               }
            }

         }
      }else{
         System.out.println("no GPU device");
      }
   }
}
