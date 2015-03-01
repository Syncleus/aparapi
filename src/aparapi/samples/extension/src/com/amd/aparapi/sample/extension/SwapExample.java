package com.amd.aparapi.sample.extension;

import com.amd.aparapi.Range;
import com.amd.aparapi.device.Device;
import com.amd.aparapi.device.OpenCLDevice;
import com.amd.aparapi.opencl.OpenCL;

public class SwapExample{

   interface Swapper extends OpenCL<Swapper>{
      @Kernel("{\n"//
            + "  const size_t id = get_global_id(0);\n"//
            + "  float temp=lhs[id];" + "  lhs[id] = rhs[id];\n"//
            + "  rhs[id] = temp;\n"//
            + "}\n")//
      public Swapper swap(//
            Range _range,//
            @GlobalReadWrite("lhs") float[] lhs,//
            @GlobalReadWrite("rhs") float[] rhs);
   }

   public static void main(String[] args) {

      final int size = 32;
      final float[] lhs = new float[size];
      for (int i = 0; i < size; i++) {
         lhs[i] = i;
      }
      final float[] rhs = new float[size];
      final Range range = Range.create(size);

      final Device device = Device.best();

      if (device instanceof OpenCLDevice) {
         final OpenCLDevice openclDevice = (OpenCLDevice) device;

         final Swapper swapper = openclDevice.bind(Swapper.class);
         for (int i = 0; i < size; i++) {
            System.out.println(lhs[i] + " " + rhs[i]);
         }

         swapper.swap(range, lhs, rhs);

         for (int i = 0; i < size; i++) {
            System.out.println(lhs[i] + " " + rhs[i]);
         }

         swapper.swap(range, lhs, rhs);

         for (int i = 0; i < size; i++) {
            System.out.println(lhs[i] + " " + rhs[i]);
         }

         swapper.swap(range, rhs, lhs);

         for (int i = 0; i < size; i++) {
            System.out.println(lhs[i] + " " + rhs[i]);
         }

      }
   }

}
