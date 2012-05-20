package com.amd.aparapi.sample.extension;

import com.amd.aparapi.Device;
import com.amd.aparapi.Kernel;
import com.amd.aparapi.OpenCL;
import com.amd.aparapi.OpenCLDevice;
import com.amd.aparapi.Range;

public class Histogram{

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
      Device device = Device.firstGPU();
      Kernel k = new Kernel(){

         @Override public void run() {
            int j = getGlobalId(0);
            for (int i = 0; i < SUB_HISTOGRAM_COUNT; ++i)
               histo[j] += binResult[i * BIN_SIZE + j];
         }

      };
      Range range2 = device.createRange(BIN_SIZE);
      k.execute(range2);

      Range range = Range.create((WIDTH * HEIGHT) / BIN_SIZE, GROUP_SIZE);

      if (device instanceof OpenCLDevice) {
         OpenCLDevice openclDevice = (OpenCLDevice) device;

         HistogramKernel histogram = openclDevice.bind(HistogramKernel.class);
       
         StopWatch timer = new StopWatch();
         timer.start();

         histogram.histogram256(range, data, sharedArray, binResult, BIN_SIZE);
         boolean java = false;
         boolean aparapiKernel = false;
         if (java) {
            // Calculate final histogram bin 
            for (int j = 0; j < BIN_SIZE; ++j)
               for (int i = 0; i < SUB_HISTOGRAM_COUNT; ++i)
                  histo[j] += binResult[i * BIN_SIZE + j];
         } else if (aparapiKernel) {
            k.execute(range2);
         } else {
            histogram.bin256(range2, histo, binResult, SUB_HISTOGRAM_COUNT);
         }
         timer.print("opencl");
         timer.start();
         for (int i = 0; i < WIDTH * HEIGHT; i++) {
            refHisto[data[i]]++;
         }
         timer.print("java");
         for (int i = 0; i < 128; i++) {
            if (refHisto[i] != histo[i]) {
               System.out.println(i + " " + histo[i] + " " + refHisto[i]);
            }
         }
      }
   }
}
