package com.amd.aparapi.example.chord;

import java.util.ArrayList;
import java.util.List;

public class FFTCooleyTukeyKernel extends FFTCooleyTukey{

   final float[] ca;

   final float[] sa;

   final int[] k;

   final int[] k_n1;

   final int len;

   public class Line{
      public Line(int _a, int _k, int _k_n1, int _n) {
         a = _a;
         k = _k;
         k_n1 = _k_n1;
         ca = c[a];
         sa = s[a];
         n = _n;
      }

      int k, k_n1, n, a;

      float ca, sa;

      String toBin(int value, int width) {
         StringBuilder b = new StringBuilder();

         for (int bit = 1 << (width - 1); bit != 0; bit >>= 1) {
            b.append(((bit & value) == bit) ? '1' : '0');
         }
         return (b.toString());
      }

      @Override public String toString() {
         return (String.format("%02x: (%02x,%02x)  %s %s %s", n, k, k_n1, toBin(n, 8), toBin(k, 8), toBin(k_n1, 8)));
      }
   }

   private List<Line> list;

   public FFTCooleyTukeyKernel(float[] _real, float[] _imag) {
      super(_real, _imag);

      int count = 0;
      len = (n / 2) * m;
      ca = new float[len + 1];
      sa = new float[len + 1];
      k = new int[len + 1];
      k_n1 = new int[len + 1];
      list = new ArrayList<Line>(); // FFT
      for (int i = 0; i < m; i++) {
         final int n1 = 1 << i;
         int a = 0;
         for (int j = 0; j < n1; j++) {
            for (int k = j; k < n; k = k + (1 << (i + 1))) {
               Line line = new Line(a, k, k + n1, count);
               ca[count] = line.ca;
               sa[count] = line.sa;
               this.k[count] = line.k;
               k_n1[count] = line.k_n1;
               list.add(line);
               count++;

            }
            a += 1 << (m - i - 1);
         }
      }
      setExplicit(true); // around 50% performance
   }

   @Override public void run() {
      int offset = getPassId() * (n / 2);
      int i = getGlobalId() + offset;

      if (offset == 0) { // we use this pass to perform the swap 
         swap(i);
      } else { // all other values of offset are used to calculate butterflies
         i = i - getGlobalSize();

         int k_n1_i = k_n1[i];
         int k_i = k[i];
         float sa_i = sa[i];
         float ca_i = ca[i];
         float imag_k_n1_i = imag[k_n1_i];
         float real_k_n1_i = real[k_n1_i];
         float imag_k_i = imag[k_i];
         float real_k_i = real[k_i];
         float t1 = ca_i * imag_k_n1_i - sa_i * real_k_n1_i;
         float t2 = sa_i * imag_k_n1_i + ca_i * real_k_n1_i;

         imag[k_n1_i] = imag_k_i - t1;
         real[k_n1_i] = real_k_i - t2;
         imag[k_i] = imag_k_i + t1;
         real[k_i] = real_k_i + t2;
      }
   }

   @Override public void fwd() {

      //  System.out.println("len = "+len);

      // we iterate m times (len/2)/
      if (isExplicit()) {
         put(real).put(imag);
      }

      // We loop m+1 times (m = number of bits)
      // the first pass will perform the tand each time the kernel processes n/2 entries from the map

      execute(n / 2, m + 1);

      //for (int i = 0; i < m + 1; i++) {
      // offset = i * (n / 2);
      //execute(n / 2);
      // System.out.println(this.getExecutionMode());
      // }
      if (isExplicit()) {
         get(real).get(imag);
      }

   }

}
