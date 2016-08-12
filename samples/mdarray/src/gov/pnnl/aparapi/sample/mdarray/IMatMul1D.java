package gov.pnnl.aparapi.sample.mdarray;
import com.amd.aparapi.Kernel;

class IMatMul1D extends Kernel{
   int[] A;

   int[] B;

   int[] C;

   int N;

   public IMatMul1D(int[] A, int[] B, int[] C, int N) {
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
         C[i * N + j] += A[i * N + k] * B[k * N + j];
      }
   }
}
