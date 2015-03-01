package gov.pnnl.aparapi.sample.mdarray;
import com.amd.aparapi.Kernel;

class BMatMul2D extends Kernel{
   byte[][] A;

   byte[][] B;

   byte[][] C;

   int N;

   public BMatMul2D(byte[][] A, byte[][] B, byte[][] C, int N) {
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
         C[i][j] += (byte) (A[i][k] * B[k][j]);
      }
   }
}
