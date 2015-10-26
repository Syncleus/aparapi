package com.amd.aparapi.test.runtime;

import com.amd.aparapi.*;
import com.amd.aparapi.device.*;
import com.amd.aparapi.internal.kernel.*;
import com.amd.aparapi.opencl.*;
import com.amd.aparapi.opencl.OpenCL.*;
import org.junit.*;

import static org.junit.Assert.*;

public class LoadCL{

   @Resource("com/amd/aparapi/test/runtime/squarer.cl") interface Squarer extends OpenCL<Squarer>{
      public Squarer square(//
            Range _range,//
            @GlobalReadWrite("in") float[] in,//
            @GlobalReadWrite("out") float[] out);
   }

   @Test public void test() {
      final int size = 32;
      final float[] in = new float[size];

      for (int i = 0; i < size; i++) {
         in[i] = i;
      }

      final float[] squares = new float[size];
      final float[] quads = new float[size];
      final Range range = Range.create(size);

      final Device device = KernelManager.instance().bestDevice();

      if (device instanceof OpenCLDevice) {
         final OpenCLDevice openclDevice = (OpenCLDevice) device;

         final Squarer squarer = openclDevice.bind(Squarer.class);
         squarer.square(range, in, squares);

         for (int i = 0; i < size; i++) {
            assertTrue("in["+i+"] * in["+i+"] = in["+i+"]^2",in[i]*in[i] == squares[i]);
         }

         squarer.square(range, squares, quads);

         for (int i = 0; i < size; i++) {
            assertTrue("in["+i+"]^2 * in["+i+"]^2 = in["+i+"]^4", in[i]*in[i]*in[i]*in[i] == quads[i]);
         }
      }
   }
}

