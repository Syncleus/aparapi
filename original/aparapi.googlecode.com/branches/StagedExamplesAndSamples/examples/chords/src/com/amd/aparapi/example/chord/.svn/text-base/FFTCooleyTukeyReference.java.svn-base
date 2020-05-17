package com.amd.aparapi.example.chord;

public class FFTCooleyTukeyReference extends FFTCooleyTukey{

   public FFTCooleyTukeyReference(float[] _real, float[] _imag) {
      super(_real, _imag);
   }

   @Override public void fwd() {
      // Swap entries based on bit reverse mappings
      for (int i = 0; i < mapLen; i++) {
         swap(i);
      }

      // FFT
      for (int i = 0; i < m; i++) {
         final int n1 = 1 << i;
         int a = 0;
         for (int j = 0; j < n1; j++) {
            for (int k = j; k < n; k = k + (1 << (i + 1))) {
               float t1 = c[a] * imag[k + n1] - s[a] * real[k + n1];
               float t2 = s[a] * imag[k + n1] + c[a] * real[k + n1];
               imag[k + n1] = imag[k] - t1;
               real[k + n1] = real[k] - t2;
               imag[k] = imag[k] + t1;
               real[k] = real[k] + t2;
            }
            a += 1 << (m - i - 1);
         }

      }
   }

   @Override public void run() {
      // don't run me
   }

}
