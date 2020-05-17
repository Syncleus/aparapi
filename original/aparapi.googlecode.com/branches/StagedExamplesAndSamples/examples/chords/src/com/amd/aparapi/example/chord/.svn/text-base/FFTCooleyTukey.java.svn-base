package com.amd.aparapi.example.chord;

import com.amd.aparapi.Kernel;

/***************************************************************
 * We based our CooleyTuky implementations off of this code. 
 * 
 * fft.c
 * Douglas L. Jones 
 * University of Illinois at Urbana-Champaign 
 * January 19, 1992 
 * http://cnx.rice.edu/content/m12016/latest/
 * 
 *   fft: in-place radix-2 DIT DFT of a complex input 
 * 
 *   input: 
 *       n: length of FFT: must be a power of two 
 *       m: n = 2**m 
 *   input/output 
 *       x: float array of length n with real part of data 
 *       y: float array of length n with imag part of data 
 * 
 *   Permission to copy and use this program is granted 
 *   as long as this header is included. 
 *   
 * Also look at 
 *   http://www.dspguide.com/ch12/3.htm
 ****************************************************************/
public abstract class FFTCooleyTukey extends Kernel{
   int n;

   int m;

   final int[] mapFrom;

   final int[] mapTo;

   int mapLen;

   final float[] imag;

   final float[] real;

   final float c[];

   final float s[];

   public FFTCooleyTukey(float[] _real, float[] _imag) {
      real = _real;
      imag = _imag;
      n = real.length;
      mapFrom = new int[n];
      mapTo = new int[n];

      m = (int) (Math.log(n) / Math.log(2)); // 1<<m == n
      for (int bits = 1; bits < n; bits++) {
         int reverseBits = Integer.reverse(bits) >>> (32 - m); // xxxxx(1010) -> xxxxx(0101) where m == 4 
         if (bits < reverseBits) {
            mapFrom[mapLen] = bits;
            mapTo[mapLen] = reverseBits;
            mapLen++;
         }
      }
      c = new float[n];
      s = new float[n];
      for (int i = 0; i < n; i++) {
         c[i] = (float) Math.cos(2 * Math.PI * i / n);
         s[i] = (float) Math.sin(-2 * Math.PI * i / n);
      }
   }

   public void inv() {

      for (int k = 0; k < n; k++) {
         imag[k] = -imag[k];
      }
      fwd();
      for (int k = 0; k < n; k++) {
         real[k] = real[k] / n;
         imag[k] = -imag[k] / n;
      }

   }

   void swap(int i) {
      float temp = imag[mapFrom[i]];
      imag[mapFrom[i]] = imag[mapTo[i]];
      imag[mapTo[i]] = temp;
      temp = real[mapFrom[i]];
      real[mapFrom[i]] = real[mapTo[i]];
      real[mapTo[i]] = temp;
   }

   abstract void fwd();

}
