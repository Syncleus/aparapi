package gov.pnnl.aparapi.sample.mdarray;
import com.amd.aparapi.Kernel;

class SMatMul1D extends Kernel{
   short[] A;

   short[] B;

   short[] C;

   int N;

   public SMatMul1D(short[] A, short[] B, short[] C, int N) {
      this.A = A;
      this.B = B;
      this.C = C;
      this.N = N;
   }

   @Override public void run() {
      int id = getGlobalId();
      int i = id / N;
      int j = id % N;
      for (int k = 0; k < N; k++) {
         C[i * N + j] += (short) (A[i * N + k] * B[k * N + j]);
      }
   }
}
