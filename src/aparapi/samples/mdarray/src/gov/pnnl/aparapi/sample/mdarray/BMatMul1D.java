package gov.pnnl.aparapi.sample.mdarray;
import com.amd.aparapi.Kernel;

class BMatMul1D extends Kernel{
   byte[] A;

   byte[] B;

   byte[] C;

   int N;

   public BMatMul1D(byte[] A, byte[] B, byte[] C, int N) {
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
         C[i * N + j] += (byte) (A[i * N + k] * B[k * N + j]);
      }
   }
}
