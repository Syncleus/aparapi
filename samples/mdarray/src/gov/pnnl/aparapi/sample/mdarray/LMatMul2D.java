package gov.pnnl.aparapi.sample.mdarray;
import com.amd.aparapi.Kernel;

class LMatMul2D extends Kernel{
   long[][] A;

   long[][] B;

   long[][] C;

   int N;

   public LMatMul2D(long[][] A, long[][] B, long[][] C, int N) {
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
         C[i][j] += A[i][k] * B[k][j];
      }
   }
}
