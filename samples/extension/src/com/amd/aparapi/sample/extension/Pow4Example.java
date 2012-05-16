package com.amd.aparapi.sample.extension;

import com.amd.aparapi.Device;
import com.amd.aparapi.OpenCL;
import com.amd.aparapi.OpenCLDevice;
import com.amd.aparapi.Range;

public class Pow4Example{

  

   @OpenCL.Resource("com/amd/aparapi/sample/extension/squarer.cl") interface Squarer extends
         OpenCL<Squarer>{

      public Squarer square(//
            Range _range,//
            @GlobalReadWrite("in") float[] in,//
            @GlobalReadWrite("out") float[] out);
   }

 
   public static void main(String[] args) {

      int size = 32;
      float[] in = new float[size];
      for (int i = 0; i < size; i++) {
         in[i] = i;
      }
      float[] squares = new float[size];
      Range range = Range.create(size);

      Device device = Device.best();

      if (device instanceof OpenCLDevice) {
         OpenCLDevice openclDevice = (OpenCLDevice) device;

         Squarer squarer = openclDevice.create(Squarer.class);
         squarer.square(range, in, squares);

         for (int i = 0; i < size; i++) {
            System.out.println(in[i] + " " + squares[i]);
         }
         
         squarer.square(range, squares, in);

         for (int i = 0; i < size; i++) {
            System.out.println(i+" " +squares[i] +" "+ in[i] );
         }
      }
   }

}
