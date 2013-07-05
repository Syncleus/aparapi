package com.amd.aparapi.sample.extension;

import com.amd.aparapi.Range;
import com.amd.aparapi.device.Device;
import com.amd.aparapi.device.OpenCLDevice;
import com.amd.aparapi.opencl.OpenCL;
import com.amd.aparapi.opencl.OpenCL.Resource;
import com.amd.aparapi.opencl.OpenCL.Source;

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

   @Resource("com/amd/aparapi/sample/extension/squarer.cl") interface SquarerWithResource extends OpenCL<SquarerWithResource>{
      public SquarerWithResource square(//
            Range _range,//
            @GlobalReadWrite("in") float[] in,//
            @GlobalReadWrite("out") float[] out);
   }

   @Source("\n"//
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
      final int size = 32;
      final float[] in = new float[size];

      for (int i = 0; i < size; i++) {
         in[i] = i;
      }

      final float[] squares = new float[size];
      final float[] quads = new float[size];
      final Range range = Range.create(size);

      final Device device = Device.best();

      if (device instanceof OpenCLDevice) {
         final OpenCLDevice openclDevice = (OpenCLDevice) device;

         for (int l=0; l<1000; l++){

         final SquarerWithResource squarer = openclDevice.bind(SquarerWithResource.class);
         squarer.square(range, in, squares);

         for (int i = 0; i < size; i++) {
            System.out.println(l+" "+in[i] + " " + squares[i]);
         }

         squarer.square(range, squares, quads);

         for (int i = 0; i < size; i++) {
            System.out.println(l+" "+ in[i] + " " + squares[i] + " " + quads[i]);
         }

         squarer.dispose();
         }
      }
   }
}
