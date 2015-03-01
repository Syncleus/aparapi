package gov.pnnl.aparapi.sample.mdarray;
import com.amd.aparapi.Kernel;

class SMatMul2D extends Kernel{
   short[][] A;

   short[][] B;

   short[][] C;

   int N;

   public SMatMul2D(short[][] A, short[][] B, short[][] C, int N) {
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
         C[i][j] += (short) (A[i][k] * B[k][j]);
      }
   }
}
