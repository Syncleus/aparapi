package com.amd.aparapi.sample.extension;

import com.amd.aparapi.Device;
import com.amd.aparapi.OpenCL;
import com.amd.aparapi.OpenCLDevice;
import com.amd.aparapi.Range;

public class SwapExample{

   interface Swapper extends OpenCL<Swapper>{
      @Kernel("{\n"//
            + "  const size_t id = get_global_id(0);\n"//
            + "  float temp=lhs[id];"
            + "  lhs[id] = rhs[id];\n"//
            + "  rhs[id] = temp;\n"//
            + "}\n")//
      public Swapper swap(//
            Range _range,//
            @GlobalReadWrite("lhs") float[] lhs,//
            @GlobalReadWrite("rhs") float[] rhs);
   }

 

   public static void main(String[] args) {

      int size = 32;
      float[] lhs = new float[size];
      for (int i = 0; i < size; i++) {
         lhs[i] = i;
      }
      float[] rhs = new float[size];
      Range range = Range.create(size);

      Device device = Device.best();

      if (device instanceof OpenCLDevice) {
         OpenCLDevice openclDevice = (OpenCLDevice) device;

         Swapper swapper = openclDevice.create(Swapper.class);
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
