package com.amd.aparapi.sample.extension;

import com.amd.aparapi.Device;
import com.amd.aparapi.OpenCL;
import com.amd.aparapi.OpenCLDevice;
import com.amd.aparapi.Range;

public class SquareExample{

   interface Squarer extends OpenCL<Squarer>{
      @Kernel("{\n"//
            + "  const size_t id = get_global_id(0);\n"//
            + "  out[id] = in[id]*in[id];\n"//
            + "}\n")//
      public Squarer square(//
            Range _range,//
            @GlobalReadWrite("in") float[] in,//
            @GlobalReadWrite("out") float[] out);
   }

   @OpenCL.Resource("com/amd/aparapi/sample/extension/squarer.cl") interface SquarerWithResource extends
         OpenCL<SquarerWithResource>{

      public SquarerWithResource square(//
            Range _range,//
            @GlobalReadWrite("in") float[] in,//
            @GlobalReadWrite("out") float[] out);
   }

   @OpenCL.Source("\n"//
         + "__kernel void square (\n" //
         + "   __global float *in,\n"//
         + "   __global float *out\n" + "){\n"//
         + "   const size_t id = get_global_id(0);\n"//
         + "   out[id] = in[id]*in[id];\n"//
         + "}\n") interface SquarerWithSource extends OpenCL<SquarerWithSource>{

      public SquarerWithSource square(//
            Range _range,//
            @GlobalReadOnly("in") float[] in,//
            @GlobalWriteOnly("out") float[] out);
   }

   public static void main(String[] args) {

      int size = 32;
      int[] v = new int[size];
      float[] in = new float[size];
      for (int i = 0; i < size; i++) {
         in[i] = i;
      }
      float[] out = new float[size];
      Range range = Range.create(size);

      Device device = Device.best();

      if (device instanceof OpenCLDevice) {
         OpenCLDevice openclDevice = (OpenCLDevice) device;

         SquarerWithResource squarer = openclDevice.create(SquarerWithResource.class);
         squarer.square(range, in, out);

         for (int i = 0; i < size; i++) {
            System.out.println(in[i] + " " + out[i]);
         }
         
         squarer.square(range, out, in);

         for (int i = 0; i < size; i++) {
            System.out.println(in[i] + " " + out[i]);
         }
      }
   }

}
