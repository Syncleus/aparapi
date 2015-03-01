package gov.pnnl.aparapi.sample.mdarray;
import com.amd.aparapi.Kernel;

class DMatMul3D extends Kernel{
   double[][][] A;

   double[][][] B;

   double[][][] C;

   int N;

   public DMatMul3D(double[][][] A, double[][][] B, double[][][] C, int N) {
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
      for (int l = 0; l < N; l++) {
         C[i][j][k] += A[i][j][l] * B[l][j][k];
      }
   }
}
