package com.amd.aparapi.sample.extension;

import java.util.Arrays;

import com.amd.aparapi.Device;
import com.amd.aparapi.OpenCL;
import com.amd.aparapi.OpenCLDevice;
import com.amd.aparapi.Range;

public class FFTExample{
   @OpenCL.Resource("com/amd/aparapi/sample/extension/fft.cl") interface FFT extends OpenCL<FFT>{

      public FFT forward(//
            Range _range,//
            @GlobalReadWrite("real") float[] real,//
            @GlobalReadWrite("imaginary") float[] imaginary//
      );
   }

   static void fft(float[] x, float[] y) {
      short dir = 1;
      long m = 10;
      int n, i, i1, j, k, i2, l, l1, l2;
      double c1, c2, tx, ty, t1, t2, u1, u2, z;

      /* Calculate the number of points */
      n = 1;
      for (i = 0; i < m; i++)
         n *= 2;

      /* Do the bit reversal */
      i2 = n >> 1;
      j = 0;
      for (i = 0; i < n - 1; i++) {
         if (i < j) {
            tx = x[i];
            ty = y[i];
            x[i] = x[j];
            y[i] = y[j];
            x[j] = (float) tx;
            y[j] = (float) ty;
         }
         k = i2;
         while (k <= j) {
            j -= k;
            k >>= 1;
         }
         j += k;
      }

      /* Compute the FFT */
      c1 = -1.0;
      c2 = 0.0;
      l2 = 1;
      for (l = 0; l < m; l++) {
         l1 = l2;
         l2 <<= 1;
         u1 = 1.0;
         u2 = 0.0;
         for (j = 0; j < l1; j++) {
            for (i = j; i < n; i += l2) {
               i1 = i + l1;
               t1 = u1 * x[i1] - u2 * y[i1];
               t2 = u1 * y[i1] + u2 * x[i1];
               x[i1] = (float) (x[i] - t1);
               y[i1] = (float) (y[i] - t2);
               x[i] += (float) t1;
               y[i] += (float) t2;
            }
            z = u1 * c1 - u2 * c2;
            u2 = u1 * c2 + u2 * c1;
            u1 = z;
         }
         c2 = Math.sqrt((1.0 - c1) / 2.0);
         if (dir == 1)
            c2 = -c2;
         c1 = Math.sqrt((1.0 + c1) / 2.0);
      }

      /* Scaling for forward transform */
      /*if (dir == 1) {
         for (i=0;i<n;i++) {
            x[i] /= n;
            y[i] /= n;
         }
      }*/

   }

   public static void main(String[] args) {
      final int LEN = 1024;
      float initial[] = new float[LEN];
      float real[] = new float[LEN];
      float imaginary[] = new float[LEN];
      float referenceReal[] = Arrays.copyOf(real, real.length);
      float referenceImaginary[] = Arrays.copyOf(imaginary, imaginary.length);
      OpenCLDevice device = (OpenCLDevice) Device.best();
      FFT fft = device.bind(FFT.class);
      for (int i = 0; i < LEN; i++) {
         initial[i] = real[i] = referenceReal[i] = (float) (Math.random() * 256);
         imaginary[i] = referenceImaginary[0] = 0f;
      }

      Range range = device.createRange(64);
      System.out.println("range=" + range);

      StopWatch timer = new StopWatch();
      timer.start();
      fft.forward(range, real, imaginary);
      timer.print("opencl");

      timer.start();
      fft(referenceReal, referenceImaginary);
      timer.print("java");
      for (int i = 0; i < LEN; i++) {
         if (Math.abs(real[i] - referenceReal[i]) > 0.01) {
            System.out.printf("%d %5.2f %5.2f %5.2f\n", i, initial[i], real[i], referenceReal[i]);
         }
      }

   }
}
