package com.amd.aparapi.sample.extension;

import java.util.Arrays;

import com.amd.aparapi.Device;
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
            @GlobalWriteOnly("binResult") int[] binResult);
   }

   public static void main(String[] args) {
      final int WIDTH = 1024*4;
      final int HEIGHT = 1024*4;
      final int BIN_SIZE = 256;
      final int GROUP_SIZE = 128;
      final int SUB_HISTOGRAM_COUNT = ((WIDTH * HEIGHT) / (GROUP_SIZE * BIN_SIZE));

  

      byte[] data = new byte[WIDTH * HEIGHT];
      for (int i = 0; i < WIDTH * HEIGHT; i++) {
         data[i] = (byte) (Math.random() * BIN_SIZE/2);
      }
      byte[] sharedArray = new byte[GROUP_SIZE * BIN_SIZE];
      int[] binResult = new int[SUB_HISTOGRAM_COUNT * BIN_SIZE];
      int[] histo = new int[BIN_SIZE];
      int[] refHisto = new int[BIN_SIZE];
      
      
   
      Range range = Range.create((WIDTH * HEIGHT)/BIN_SIZE, GROUP_SIZE);

      Device device = Device.best();

      if (device instanceof OpenCLDevice) {
         OpenCLDevice openclDevice = (OpenCLDevice) device;

         HistogramKernel histogram = openclDevice.create(HistogramKernel.class);
       //  histogram.histogram256(range, data, sharedArray, binResult);

        // Arrays.fill(binResult, 0);
         
         long start= System.currentTimeMillis();
         histogram.histogram256(range, data, sharedArray, binResult);
         // Calculate final histogram bin 
         for(int i = 0; i < SUB_HISTOGRAM_COUNT; ++i)
             for(int j = 0; j < BIN_SIZE; ++j)
                 histo[j] += binResult[i * BIN_SIZE + j];
        
         System.out.println("opencl "+(System.currentTimeMillis()-start));
         
         start = System.currentTimeMillis();
         for (int i=0; i<WIDTH * HEIGHT; i++){
            refHisto[data[i]]++;
         }
         System.out.println("java "+(System.currentTimeMillis()-start));
         for (int i = 0; i < 128; i++) {
            System.out.println(i + " " + histo[i]+" "+refHisto[i]);
         }

      }
   }
}
