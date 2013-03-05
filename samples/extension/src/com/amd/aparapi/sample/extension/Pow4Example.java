package com.amd.aparapi.sample.extension;

import com.amd.aparapi.Range;
import com.amd.aparapi.device.Device;
import com.amd.aparapi.device.OpenCLDevice;
import com.amd.aparapi.opencl.OpenCL;
import com.amd.aparapi.opencl.OpenCL.Resource;

public class Pow4Example{

   @Resource("com/amd/aparapi/sample/extension/squarer.cl") interface Squarer extends OpenCL<Squarer>{

      public Squarer square(//
            Range _range,//
            @GlobalReadWrite("in") float[] in,//
            @GlobalReadWrite("out") float[] out);
   }

   public static void main(String[] args) {

      final int size = 32;
      final float[] in = new float[size];
      for (int i = 0; i < size; i++) {
         in[i] = i;
      }
      final float[] squares = new float[size];
      final Range range = Range.create(size);

      final Device device = Device.best();

      if (device instanceof OpenCLDevice) {
         final OpenCLDevice openclDevice = (OpenCLDevice) device;

         final Squarer squarer = openclDevice.bind(Squarer.class);
         squarer.square(range, in, squares);

         for (int i = 0; i < size; i++) {
            System.out.println(in[i] + " " + squares[i]);
         }

         squarer.square(range, squares, in);

         for (int i = 0; i < size; i++) {
            System.out.println(i + " " + squares[i] + " " + in[i]);
         }
      }
   }

}
