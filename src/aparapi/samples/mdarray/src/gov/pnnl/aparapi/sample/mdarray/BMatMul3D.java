package gov.pnnl.aparapi.sample.mdarray;
import com.amd.aparapi.Kernel;

class BMatMul3D extends Kernel{
   byte[][][] A;

   byte[][][] B;

   byte[][][] C;

   int N;

   public BMatMul3D(byte[][][] A, byte[][][] B, byte[][][] C, int N) {
      this.A = A;
      this.B = B;
      this.C = C;
      this.N = N;
   }

   @Override public void run() {
      int id = getGlobalId();
      int i = id / (N * N);
      int j = (id / N) % N;
      int k = id % N;
      int a0 = A.length;
      int a1 = A[0].length;
      int a2 = A[0][0].length;
      for (int l = 0; l < N; l++) {
         C[i][j][k] += (byte) (A[i][j][l] * B[l][j][k]);
      }
   }
}
