package gov.pnnl.aparapi.sample.mdarray;

import com.amd.aparapi.Kernel;

class MDArray {

   static int N = 1 << 10;

   static int M = 1 << 5;

   public static void main(String[] args) {
      System.out.println("boolean 1D");
      Zrun1D();
      System.out.println("byte 1D");
      Brun1D();
      System.out.println("short 1D");
      Srun1D();
      System.out.println("int 1D");
      Irun1D();
      System.out.println("long 1D");
      Lrun1D();
      System.out.println("float 1D");
      Frun1D();
      System.out.println("double 1D");
      Drun1D();
      System.out.println("boolean 2D");
      Zrun2D();
      System.out.println("byte 2D");
      Brun2D();
      System.out.println("short 2D");
      Srun2D();
      System.out.println("int 2D");
      Irun2D();
      System.out.println("long 2D");
      Lrun2D();
      System.out.println("float 2D");
      Frun2D();
      System.out.println("double 2D");
      Drun2D();
      System.out.println("boolean 3D");
      Zrun3D();
      System.out.println("byte 3D");
      Brun3D();
      System.out.println("short 3D");
      Srun3D();
      System.out.println("int 3D");
      Irun3D();
      System.out.println("long 3D");
      Lrun3D();
      System.out.println("float 3D");
      Frun3D();
      System.out.println("double 3D");
      Drun3D();
   }

   private static boolean[] matMull(boolean[] A, boolean[] B, int N) {
      final boolean[] C = new boolean[N * N];
      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            for (int k = 0; k < N; k++) {
               C[(i * N) + j] ^= A[(i * N) + k] & B[(k * N) + j];
            }
         }
      }
      return C;
   }

   private static byte[] matMull(byte[] A, byte[] B, int N) {
      final byte[] C = new byte[N * N];
      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            for (int k = 0; k < N; k++) {
               C[(i * N) + j] += (byte) (A[(i * N) + k] * B[(k * N) + j]);
            }
         }
      }
      return C;
   }

   private static short[] matMull(short[] A, short[] B, int N) {
      final short[] C = new short[N * N];
      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            for (int k = 0; k < N; k++) {
               C[(i * N) + j] += (short) (A[(i * N) + k] * B[(k * N) + j]);
            }
         }
      }
      return C;
   }

   private static int[] matMull(int[] A, int[] B, int N) {
      final int[] C = new int[N * N];
      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            for (int k = 0; k < N; k++) {
               C[(i * N) + j] += A[(i * N) + k] * B[(k * N) + j];
            }
         }
      }
      return C;
   }

   private static long[] matMull(long[] A, long[] B, int N) {
      final long[] C = new long[N * N];
      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            for (int k = 0; k < N; k++) {
               C[(i * N) + j] += A[(i * N) + k] * B[(k * N) + j];
            }
         }
      }
      return C;
   }

   private static float[] matMull(float[] A, float[] B, int N) {
      final float[] C = new float[N * N];
      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            for (int k = 0; k < N; k++) {
               C[(i * N) + j] += A[(i * N) + k] * B[(k * N) + j];
            }
         }
      }
      return C;
   }

   private static double[] matMull(double[] A, double[] B, int N) {
      final double[] C = new double[N * N];
      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            for (int k = 0; k < N; k++) {
               C[(i * N) + j] += A[(i * N) + k] * B[(k * N) + j];
            }
         }
      }
      return C;
   }

   private static boolean[][] matMull(boolean[][] A, boolean[][] B, int N) {
      final boolean[][] C = new boolean[N][N];
      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            for (int k = 0; k < N; k++) {
               C[i][j] ^= A[i][k] & B[k][j];
            }
         }
      }
      return C;
   }

   private static byte[][] matMull(byte[][] A, byte[][] B, int N) {
      final byte[][] C = new byte[N][N];
      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            for (int k = 0; k < N; k++) {
               C[i][j] += (byte) (A[i][k] * B[k][j]);
            }
         }
      }
      return C;
   }

   private static short[][] matMull(short[][] A, short[][] B, int N) {
      final short[][] C = new short[N][N];
      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            for (int k = 0; k < N; k++) {
               C[i][j] += (short) (A[i][k] * B[k][j]);
            }
         }
      }
      return C;
   }

   private static int[][] matMull(int[][] A, int[][] B, int N) {
      final int[][] C = new int[N][N];
      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            for (int k = 0; k < N; k++) {
               C[i][j] += A[i][k] * B[k][j];
            }
         }
      }
      return C;
   }

   private static long[][] matMull(long[][] A, long[][] B, int N) {
      final long[][] C = new long[N][N];
      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            for (int k = 0; k < N; k++) {
               C[i][j] += A[i][k] * B[k][j];
            }
         }
      }
      return C;
   }

   private static float[][] matMull(float[][] A, float[][] B, int N) {
      final float[][] C = new float[N][N];
      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            for (int k = 0; k < N; k++) {
               C[i][j] += A[i][k] * B[k][j];
            }
         }
      }
      return C;
   }

   private static double[][] matMull(double[][] A, double[][] B, int N) {
      final double[][] C = new double[N][N];
      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            for (int k = 0; k < N; k++) {
               C[i][j] += A[i][k] * B[k][j];
            }
         }
      }
      return C;
   }

   private static boolean[][][] matMull(boolean[][][] A, boolean[][][] B, int N) {
      final boolean[][][] C = new boolean[N][N][N];
      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            for (int k = 0; k < N; k++) {
               for (int l = 0; l < N; l++) {
                  C[i][j][k] ^= A[i][j][l] & B[l][j][k];
               }
            }
         }
      }
      return C;
   }

   private static byte[][][] matMull(byte[][][] A, byte[][][] B, int N) {
      final byte[][][] C = new byte[N][N][N];
      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            for (int k = 0; k < N; k++) {
               for (int l = 0; l < N; l++) {
                  C[i][j][k] += (byte) (A[i][j][l] * B[l][j][k]);
               }
            }
         }
      }
      return C;
   }

   private static short[][][] matMull(short[][][] A, short[][][] B, int N) {
      final short[][][] C = new short[N][N][N];
      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            for (int k = 0; k < N; k++) {
               for (int l = 0; l < N; l++) {
                  C[i][j][k] += (short) (A[i][j][l] * B[l][j][k]);
               }
            }
         }
      }
      return C;
   }

   private static int[][][] matMull(int[][][] A, int[][][] B, int N) {
      final int[][][] C = new int[N][N][N];
      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            for (int k = 0; k < N; k++) {
               for (int l = 0; l < N; l++) {
                  C[i][j][k] += A[i][j][l] * B[l][j][k];
               }
            }
         }
      }
      return C;
   }

   private static long[][][] matMull(long[][][] A, long[][][] B, int N) {
      final long[][][] C = new long[N][N][N];
      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            for (int k = 0; k < N; k++) {
               for (int l = 0; l < N; l++) {
                  C[i][j][k] += A[i][j][l] * B[l][j][k];
               }
            }
         }
      }
      return C;
   }

   private static float[][][] matMull(float[][][] A, float[][][] B, int N) {
      final float[][][] C = new float[N][N][N];
      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            for (int k = 0; k < N; k++) {
               for (int l = 0; l < N; l++) {
                  C[i][j][k] += A[i][j][l] * B[l][j][k];
               }
            }
         }
      }
      return C;
   }

   private static double[][][] matMull(double[][][] A, double[][][] B, int N) {
      final double[][][] C = new double[N][N][N];
      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            for (int k = 0; k < N; k++) {
               for (int l = 0; l < N; l++) {
                  C[i][j][k] += A[i][j][l] * B[l][j][k];
               }
            }
         }
      }
      return C;
   }

   private static boolean checkResults(boolean[] cpu, boolean[] gpu) {
      for (int i = 0; i < cpu.length; i++) {
         if (cpu[i] != gpu[i]) {
            return false;
         }
      }
      return true;
   }

   private static boolean checkResults(byte[] cpu, byte[] gpu) {
      for (int i = 0; i < cpu.length; i++) {
         if (cpu[i] != gpu[i]) {
            return false;
         }
      }
      return true;
   }

   private static boolean checkResults(short[] cpu, short[] gpu) {
      for (int i = 0; i < cpu.length; i++) {
         if (cpu[i] != gpu[i]) {
            return false;
         }
      }
      return true;
   }

   private static boolean checkResults(int[] cpu, int[] gpu) {
      for (int i = 0; i < cpu.length; i++) {
         if (cpu[i] != gpu[i]) {
            return false;
         }
      }
      return true;
   }

   private static boolean checkResults(long[] cpu, long[] gpu) {
      for (int i = 0; i < cpu.length; i++) {
         if (cpu[i] != gpu[i]) {
            return false;
         }
      }
      return true;
   }

   private static boolean checkResults(float[] cpu, float[] gpu) {
      for (int i = 0; i < cpu.length; i++) {
         if (cpu[i] != gpu[i]) {
            return false;
         }
      }
      return true;
   }

   private static boolean checkResults(double[] cpu, double[] gpu) {
      for (int i = 0; i < cpu.length; i++) {
         if (cpu[i] != gpu[i]) {
            return false;
         }
      }
      return true;
   }

   private static boolean checkResults(boolean[][] cpu, boolean[][] gpu) {
      for (int i = 0; i < cpu.length; i++) {
         for (int j = 0; j < cpu[i].length; j++) {
            if (cpu[i][j] != gpu[i][j]) {
               return false;
            }
         }
      }
      return true;
   }

   private static boolean checkResults(byte[][] cpu, byte[][] gpu) {
      for (int i = 0; i < cpu.length; i++) {
         for (int j = 0; j < cpu[i].length; j++) {
            if (cpu[i][j] != gpu[i][j]) {
               return false;
            }
         }
      }
      return true;
   }

   private static boolean checkResults(short[][] cpu, short[][] gpu) {
      for (int i = 0; i < cpu.length; i++) {
         for (int j = 0; j < cpu[i].length; j++) {
            if (cpu[i][j] != gpu[i][j]) {
               return false;
            }
         }
      }
      return true;
   }

   private static boolean checkResults(int[][] cpu, int[][] gpu) {
      for (int i = 0; i < cpu.length; i++) {
         for (int j = 0; j < cpu[i].length; j++) {
            if (cpu[i][j] != gpu[i][j]) {
               return false;
            }
         }
      }
      return true;
   }

   private static boolean checkResults(long[][] cpu, long[][] gpu) {
      for (int i = 0; i < cpu.length; i++) {
         for (int j = 0; j < cpu[i].length; j++) {
            if (cpu[i][j] != gpu[i][j]) {
               return false;
            }
         }
      }
      return true;
   }

   private static boolean checkResults(float[][] cpu, float[][] gpu) {
      for (int i = 0; i < cpu.length; i++) {
         for (int j = 0; j < cpu[i].length; j++) {
            if (cpu[i][j] != gpu[i][j]) {
               return false;
            }
         }
      }
      return true;
   }

   private static boolean checkResults(double[][] cpu, double[][] gpu) {
      for (int i = 0; i < cpu.length; i++) {
         for (int j = 0; j < cpu[i].length; j++) {
            if (cpu[i][j] != gpu[i][j]) {
               return false;
            }
         }
      }
      return true;
   }

   private static boolean checkResults(boolean[][][] cpu, boolean[][][] gpu) {
      for (int i = 0; i < cpu.length; i++) {
         for (int j = 0; j < cpu[i].length; j++) {
            for (int k = 0; k < cpu[i][j].length; k++) {
               if (cpu[i][j][k] != gpu[i][j][k]) {
                  return false;
               }
            }
         }
      }
      return true;
   }

   private static boolean checkResults(byte[][][] cpu, byte[][][] gpu) {
      for (int i = 0; i < cpu.length; i++) {
         for (int j = 0; j < cpu[i].length; j++) {
            for (int k = 0; k < cpu[i][j].length; k++) {
               if (cpu[i][j][k] != gpu[i][j][k]) {
                  return false;
               }
            }
         }
      }
      return true;
   }

   private static boolean checkResults(short[][][] cpu, short[][][] gpu) {
      for (int i = 0; i < cpu.length; i++) {
         for (int j = 0; j < cpu[i].length; j++) {
            for (int k = 0; k < cpu[i][j].length; k++) {
               if (cpu[i][j][k] != gpu[i][j][k]) {
                  return false;
               }
            }
         }
      }
      return true;
   }

   private static boolean checkResults(int[][][] cpu, int[][][] gpu) {
      for (int i = 0; i < cpu.length; i++) {
         for (int j = 0; j < cpu[i].length; j++) {
            for (int k = 0; k < cpu[i][j].length; k++) {
               if (cpu[i][j][k] != gpu[i][j][k]) {
                  return false;
               }
            }
         }
      }
      return true;
   }

   private static boolean checkResults(long[][][] cpu, long[][][] gpu) {
      for (int i = 0; i < cpu.length; i++) {
         for (int j = 0; j < cpu[i].length; j++) {
            for (int k = 0; k < cpu[i][j].length; k++) {
               if (cpu[i][j][k] != gpu[i][j][k]) {
                  return false;
               }
            }
         }
      }
      return true;
   }

   private static boolean checkResults(float[][][] cpu, float[][][] gpu) {
      for (int i = 0; i < cpu.length; i++) {
         for (int j = 0; j < cpu[i].length; j++) {
            for (int k = 0; k < cpu[i][j].length; k++) {
               if (cpu[i][j][k] != gpu[i][j][k]) {
                  return false;
               }
            }
         }
      }
      return true;
   }

   private static boolean checkResults(double[][][] cpu, double[][][] gpu) {
      for (int i = 0; i < cpu.length; i++) {
         for (int j = 0; j < cpu[i].length; j++) {
            for (int k = 0; k < cpu[i][j].length; k++) {
               if (cpu[i][j][k] != gpu[i][j][k]) {
                  return false;
               }
            }
         }
      }
      return true;
   }

   public static void Zrun1D() {
      final boolean[] A = new boolean[N * N];
      final boolean[] B = new boolean[N * N];
      final boolean[] gpu = new boolean[N * N];
      boolean[] cpu = new boolean[N * N];

      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            A[(i * N) + j] = ((i % 2) == 0) ^ ((j % 2) == 0);
            B[(i * N) + j] = ((i % 2) == 0) & ((j % 2) == 0);
            cpu[(i * N) + j] = false;
            gpu[(i * N) + j] = false;
         }
      }

      long gs = System.currentTimeMillis();
      final Kernel kernel = new ZMatMul1D(A, B, gpu, N);
      kernel.execute(N * N);
      gs = System.currentTimeMillis() - gs;

      long cs = System.currentTimeMillis();
      cpu = matMull(A, B, N);
      cs = System.currentTimeMillis() - cs;

      System.out.println("gpu time: " + gs + "\ncpu time: " + cs);
      System.out.print("valid? ");

      if (checkResults(cpu, gpu)) {
         System.out.println("yes");
      } else {
         System.out.println("no");
      }
   }

   public static void Brun1D() {
      final byte[] A = new byte[N * N];
      final byte[] B = new byte[N * N];
      final byte[] gpu = new byte[N * N];
      byte[] cpu = new byte[N * N];

      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            A[(i * N) + j] = (byte) (i + j);
            B[(i * N) + j] = (byte) (i - j);
            cpu[(i * N) + j] = (byte) 0;
            gpu[(i * N) + j] = (byte) 0;
         }
      }

      long gs = System.currentTimeMillis();
      final Kernel kernel = new BMatMul1D(A, B, gpu, N);
      kernel.execute(N * N);
      gs = System.currentTimeMillis() - gs;

      long cs = System.currentTimeMillis();
      cpu = matMull(A, B, N);
      cs = System.currentTimeMillis() - cs;

      System.out.println("gpu time: " + gs + "\ncpu time: " + cs);
      System.out.print("valid? ");

      if (checkResults(cpu, gpu)) {
         System.out.println("yes");
      } else {
         System.out.println("no");
      }
   }

   public static void Srun1D() {
      final short[] A = new short[N * N];
      final short[] B = new short[N * N];
      final short[] gpu = new short[N * N];
      short[] cpu = new short[N * N];

      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            A[(i * N) + j] = (short) (i + j);
            B[(i * N) + j] = (short) (i - j);
            cpu[(i * N) + j] = (short) 0;
            gpu[(i * N) + j] = (short) 0;
         }
      }

      long gs = System.currentTimeMillis();
      final Kernel kernel = new SMatMul1D(A, B, gpu, N);
      kernel.execute(N * N);
      gs = System.currentTimeMillis() - gs;

      long cs = System.currentTimeMillis();
      cpu = matMull(A, B, N);
      cs = System.currentTimeMillis() - cs;

      System.out.println("gpu time: " + gs + "\ncpu time: " + cs);
      System.out.print("valid? ");

      if (checkResults(cpu, gpu)) {
         System.out.println("yes");
      } else {
         System.out.println("no");
      }
   }

   public static void Irun1D() {
      final int[] A = new int[N * N];
      final int[] B = new int[N * N];
      final int[] gpu = new int[N * N];
      int[] cpu = new int[N * N];

      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            A[(i * N) + j] = i + j;
            B[(i * N) + j] = i - j;
            cpu[(i * N) + j] = 0;
            gpu[(i * N) + j] = 0;
         }
      }

      long gs = System.currentTimeMillis();
      final Kernel kernel = new IMatMul1D(A, B, gpu, N);
      kernel.execute(N * N);
      gs = System.currentTimeMillis() - gs;

      long cs = System.currentTimeMillis();
      cpu = matMull(A, B, N);
      cs = System.currentTimeMillis() - cs;

      System.out.println("gpu time: " + gs + "\ncpu time: " + cs);
      System.out.print("valid? ");

      if (checkResults(cpu, gpu)) {
         System.out.println("yes");
      } else {
         System.out.println("no");
      }
   }

   public static void Lrun1D() {
      final long[] A = new long[N * N];
      final long[] B = new long[N * N];
      final long[] gpu = new long[N * N];
      long[] cpu = new long[N * N];

      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            A[(i * N) + j] = i + j;
            B[(i * N) + j] = i - j;
            cpu[(i * N) + j] = 0l;
            gpu[(i * N) + j] = 0l;
         }
      }

      long gs = System.currentTimeMillis();
      final Kernel kernel = new LMatMul1D(A, B, gpu, N);
      kernel.execute(N * N);
      gs = System.currentTimeMillis() - gs;

      long cs = System.currentTimeMillis();
      cpu = matMull(A, B, N);
      cs = System.currentTimeMillis() - cs;

      System.out.println("gpu time: " + gs + "\ncpu time: " + cs);
      System.out.print("valid? ");

      if (checkResults(cpu, gpu)) {
         System.out.println("yes");
      } else {
         System.out.println("no");
      }
   }

   public static void Frun1D() {
      final float[] A = new float[N * N];
      final float[] B = new float[N * N];
      final float[] gpu = new float[N * N];
      float[] cpu = new float[N * N];

      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            A[(i * N) + j] = i + j;
            B[(i * N) + j] = i - j;
            cpu[(i * N) + j] = 0.0f;
            gpu[(i * N) + j] = 0.0f;
         }
      }

      long gs = System.currentTimeMillis();
      final Kernel kernel = new FMatMul1D(A, B, gpu, N);
      kernel.execute(N * N);
      gs = System.currentTimeMillis() - gs;

      long cs = System.currentTimeMillis();
      cpu = matMull(A, B, N);
      cs = System.currentTimeMillis() - cs;

      System.out.println("gpu time: " + gs + "\ncpu time: " + cs);
      System.out.print("valid? ");

      if (checkResults(cpu, gpu)) {
         System.out.println("yes");
      } else {
         System.out.println("no");
      }
   }

   public static void Drun1D() {
      final double[] A = new double[N * N];
      final double[] B = new double[N * N];
      final double[] gpu = new double[N * N];
      double[] cpu = new double[N * N];

      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            A[(i * N) + j] = i + j;
            B[(i * N) + j] = i - j;
            cpu[(i * N) + j] = 0.0;
            gpu[(i * N) + j] = 0.0;
         }
      }

      long gs = System.currentTimeMillis();
      final Kernel kernel = new DMatMul1D(A, B, gpu, N);
      kernel.execute(N * N);
      gs = System.currentTimeMillis() - gs;

      long cs = System.currentTimeMillis();
      cpu = matMull(A, B, N);
      cs = System.currentTimeMillis() - cs;

      System.out.println("gpu time: " + gs + "\ncpu time: " + cs);
      System.out.print("valid? ");

      if (checkResults(cpu, gpu)) {
         System.out.println("yes");
      } else {
         System.out.println("no");
      }
   }

   public static void Zrun2D() {
      final boolean[][] A = new boolean[N][N];
      final boolean[][] B = new boolean[N][N];
      final boolean[][] gpu = new boolean[N][N];
      boolean[][] cpu = new boolean[N][N];

      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            A[i][j] = ((i % 2) == 0) ^ ((j % 2) == 0);
            B[i][j] = ((i % 2) == 0) & ((j % 2) == 0);
            cpu[i][j] = false;
            gpu[i][j] = false;
         }
      }

      long gs = System.currentTimeMillis();
      final Kernel kernel = new ZMatMul2D(A, B, gpu, N);
      kernel.execute(N * N);
      gs = System.currentTimeMillis() - gs;

      long cs = System.currentTimeMillis();
      cpu = matMull(A, B, N);
      cs = System.currentTimeMillis() - cs;

      System.out.println("gpu time: " + gs + "\ncpu time: " + cs);
      System.out.print("valid? ");

      if (checkResults(cpu, gpu)) {
         System.out.println("yes");
      } else {
         System.out.println("no");
      }
   }

   public static void Brun2D() {
      final byte[][] A = new byte[N][N];
      final byte[][] B = new byte[N][N];
      final byte[][] gpu = new byte[N][N];
      byte[][] cpu = new byte[N][N];

      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            A[i][j] = (byte) (i + j);
            B[i][j] = (byte) (i - j);
            cpu[i][j] = (byte) 0;
            gpu[i][j] = (byte) 0;
         }
      }

      long gs = System.currentTimeMillis();
      final Kernel kernel = new BMatMul2D(A, B, gpu, N);
      kernel.execute(N * N);
      gs = System.currentTimeMillis() - gs;

      long cs = System.currentTimeMillis();
      cpu = matMull(A, B, N);
      cs = System.currentTimeMillis() - cs;

      System.out.println("gpu time: " + gs + "\ncpu time: " + cs);
      System.out.print("valid? ");

      if (checkResults(cpu, gpu)) {
         System.out.println("yes");
      } else {
         System.out.println("no");
      }
   }

   public static void Srun2D() {
      final short[][] A = new short[N][N];
      final short[][] B = new short[N][N];
      final short[][] gpu = new short[N][N];
      short[][] cpu = new short[N][N];

      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            A[i][j] = (short) (i + j);
            B[i][j] = (short) (i - j);
            cpu[i][j] = (short) 0;
            gpu[i][j] = (short) 0;
         }
      }

      long gs = System.currentTimeMillis();
      final Kernel kernel = new SMatMul2D(A, B, gpu, N);
      kernel.execute(N * N);
      gs = System.currentTimeMillis() - gs;

      long cs = System.currentTimeMillis();
      cpu = matMull(A, B, N);
      cs = System.currentTimeMillis() - cs;

      System.out.println("gpu time: " + gs + "\ncpu time: " + cs);
      System.out.print("valid? ");

      if (checkResults(cpu, gpu)) {
         System.out.println("yes");
      } else {
         System.out.println("no");
      }
   }

   public static void Irun2D() {
      final int[][] A = new int[N][N];
      final int[][] B = new int[N][N];
      final int[][] gpu = new int[N][N];
      int[][] cpu = new int[N][N];

      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            A[i][j] = i + j;
            B[i][j] = i - j;
            cpu[i][j] = 0;
            gpu[i][j] = 0;
         }
      }

      long gs = System.currentTimeMillis();
      final Kernel kernel = new IMatMul2D(A, B, gpu, N);
      kernel.execute(N * N);
      gs = System.currentTimeMillis() - gs;

      long cs = System.currentTimeMillis();
      cpu = matMull(A, B, N);
      cs = System.currentTimeMillis() - cs;

      System.out.println("gpu time: " + gs + "\ncpu time: " + cs);
      System.out.print("valid? ");

      if (checkResults(cpu, gpu)) {
         System.out.println("yes");
      } else {
         System.out.println("no");
      }
   }

   public static void Lrun2D() {
      final long[][] A = new long[N][N];
      final long[][] B = new long[N][N];
      final long[][] gpu = new long[N][N];
      long[][] cpu = new long[N][N];

      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            A[i][j] = i + j;
            B[i][j] = i - j;
            cpu[i][j] = 0l;
            gpu[i][j] = 0l;
         }
      }

      long gs = System.currentTimeMillis();
      final Kernel kernel = new LMatMul2D(A, B, gpu, N);
      kernel.execute(N * N);
      gs = System.currentTimeMillis() - gs;

      long cs = System.currentTimeMillis();
      cpu = matMull(A, B, N);
      cs = System.currentTimeMillis() - cs;

      System.out.println("gpu time: " + gs + "\ncpu time: " + cs);
      System.out.print("valid? ");

      if (checkResults(cpu, gpu)) {
         System.out.println("yes");
      } else {
         System.out.println("no");
      }
   }

   public static void Frun2D() {
      final float[][] A = new float[N][N];
      final float[][] B = new float[N][N];
      final float[][] gpu = new float[N][N];
      float[][] cpu = new float[N][N];

      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            A[i][j] = i + j;
            B[i][j] = i - j;
            cpu[i][j] = 0.0f;
            gpu[i][j] = 0.0f;
         }
      }

      long gs = System.currentTimeMillis();
      final Kernel kernel = new FMatMul2D(A, B, gpu, N);
      kernel.execute(N * N);
      gs = System.currentTimeMillis() - gs;

      long cs = System.currentTimeMillis();
      cpu = matMull(A, B, N);
      cs = System.currentTimeMillis() - cs;

      System.out.println("gpu time: " + gs + "\ncpu time: " + cs);
      System.out.print("valid? ");

      if (checkResults(cpu, gpu)) {
         System.out.println("yes");
      } else {
         System.out.println("no");
      }
   }

   public static void Drun2D() {
      final double[][] A = new double[N][N];
      final double[][] B = new double[N][N];
      final double[][] gpu = new double[N][N];
      double[][] cpu = new double[N][N];

      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            A[i][j] = i + j;
            B[i][j] = i - j;
            cpu[i][j] = 0.0;
            gpu[i][j] = 0.0;
         }
      }

      long gs = System.currentTimeMillis();
      final Kernel kernel = new DMatMul2D(A, B, gpu, N);
      kernel.execute(N * N);
      gs = System.currentTimeMillis() - gs;

      long cs = System.currentTimeMillis();
      cpu = matMull(A, B, N);
      cs = System.currentTimeMillis() - cs;

      System.out.println("gpu time: " + gs + "\ncpu time: " + cs);
      System.out.print("valid? ");

      if (checkResults(cpu, gpu)) {
         System.out.println("yes");
      } else {
         System.out.println("no");
      }
   }

   public static void Zrun3D() {
      final boolean[][][] A = new boolean[M][M][M];
      final boolean[][][] B = new boolean[M][M][M];
      final boolean[][][] gpu = new boolean[M][M][M];
      boolean[][][] cpu = new boolean[M][M][M];

      for (int i = 0; i < M; i++) {
         for (int j = 0; j < M; j++) {
            for (int k = 0; k < M; k++) {
               A[i][j][k] = ((i % 2) == 0) ^ (((j % 2) == 0) & ((k % 2) == 0));
               B[i][j][k] = (((i % 2) == 0) & ((j % 2) == 0)) ^ ((k % 2) == 0);
               ;
               cpu[i][j][k] = false;
               gpu[i][j][k] = false;
            }
         }
      }

      long gs = System.currentTimeMillis();
      final Kernel kernel = new ZMatMul3D(A, B, gpu, M);
      kernel.execute(M * M * M);
      gs = System.currentTimeMillis() - gs;

      long cs = System.currentTimeMillis();
      cpu = matMull(A, B, M);
      cs = System.currentTimeMillis() - cs;

      System.out.println("gpu time: " + gs + "\ncpu time: " + cs);
      System.out.print("valid? ");

      if (checkResults(cpu, gpu)) {
         System.out.println("yes");
      } else {
         System.out.println("no");
      }
   }

   public static void Brun3D() {
      final byte[][][] A = new byte[M][M][M];
      final byte[][][] B = new byte[M][M][M];
      final byte[][][] gpu = new byte[M][M][M];
      byte[][][] cpu = new byte[M][M][M];

      for (int i = 0; i < M; i++) {
         for (int j = 0; j < M; j++) {
            for (int k = 0; k < M; k++) {
               A[i][j][k] = (byte) (i + j + k);
               B[i][j][k] = (byte) ((i - j) + k);
               cpu[i][j][k] = (byte) 0;
               gpu[i][j][k] = (byte) 0;
            }
         }
      }

      long gs = System.currentTimeMillis();
      final Kernel kernel = new BMatMul3D(A, B, gpu, M);
      kernel.execute(M * M * M);
      gs = System.currentTimeMillis() - gs;

      long cs = System.currentTimeMillis();
      cpu = matMull(A, B, M);
      cs = System.currentTimeMillis() - cs;

      System.out.println("gpu time: " + gs + "\ncpu time: " + cs);
      System.out.print("valid? ");

      if (checkResults(cpu, gpu)) {
         System.out.println("yes");
      } else {
         System.out.println("no");
      }
   }

   public static void Srun3D() {
      final short[][][] A = new short[M][M][M];
      final short[][][] B = new short[M][M][M];
      final short[][][] gpu = new short[M][M][M];
      short[][][] cpu = new short[M][M][M];

      for (int i = 0; i < M; i++) {
         for (int j = 0; j < M; j++) {
            for (int k = 0; k < M; k++) {
               A[i][j][k] = (short) (i + j + k);
               B[i][j][k] = (short) ((i - j) + k);
               cpu[i][j][k] = (short) 0;
               gpu[i][j][k] = (short) 0;
            }
         }
      }

      long gs = System.currentTimeMillis();
      final Kernel kernel = new SMatMul3D(A, B, gpu, M);
      kernel.execute(M * M * M);
      gs = System.currentTimeMillis() - gs;

      long cs = System.currentTimeMillis();
      cpu = matMull(A, B, M);
      cs = System.currentTimeMillis() - cs;

      System.out.println("gpu time: " + gs + "\ncpu time: " + cs);
      System.out.print("valid? ");

      if (checkResults(cpu, gpu)) {
         System.out.println("yes");
      } else {
         System.out.println("no");
      }
   }

   public static void Irun3D() {
      final int[][][] A = new int[M][M][M];
      final int[][][] B = new int[M][M][M];
      final int[][][] gpu = new int[M][M][M];
      int[][][] cpu = new int[M][M][M];

      for (int i = 0; i < M; i++) {
         for (int j = 0; j < M; j++) {
            for (int k = 0; k < M; k++) {
               A[i][j][k] = i + j + k;
               B[i][j][k] = (i - j) + k;
               cpu[i][j][k] = 0;
               gpu[i][j][k] = 0;
            }
         }
      }

      long gs = System.currentTimeMillis();
      final Kernel kernel = new IMatMul3D(A, B, gpu, M);
      kernel.execute(M * M * M);
      gs = System.currentTimeMillis() - gs;

      long cs = System.currentTimeMillis();
      cpu = matMull(A, B, M);
      cs = System.currentTimeMillis() - cs;

      System.out.println("gpu time: " + gs + "\ncpu time: " + cs);
      System.out.print("valid? ");

      if (checkResults(cpu, gpu)) {
         System.out.println("yes");
      } else {
         System.out.println("no");
      }
   }

   public static void Lrun3D() {
      final long[][][] A = new long[M][M][M];
      final long[][][] B = new long[M][M][M];
      final long[][][] gpu = new long[M][M][M];
      long[][][] cpu = new long[M][M][M];

      for (int i = 0; i < M; i++) {
         for (int j = 0; j < M; j++) {
            for (int k = 0; k < M; k++) {
               A[i][j][k] = i + j + k;
               B[i][j][k] = (i - j) + k;
               cpu[i][j][k] = 0l;
               gpu[i][j][k] = 0l;
            }
         }
      }

      long gs = System.currentTimeMillis();
      final Kernel kernel = new LMatMul3D(A, B, gpu, M);
      kernel.execute(M * M * M);
      gs = System.currentTimeMillis() - gs;

      long cs = System.currentTimeMillis();
      cpu = matMull(A, B, M);
      cs = System.currentTimeMillis() - cs;

      System.out.println("gpu time: " + gs + "\ncpu time: " + cs);
      System.out.print("valid? ");

      if (checkResults(cpu, gpu)) {
         System.out.println("yes");
      } else {
         System.out.println("no");
      }
   }

   public static void Frun3D() {
      final float[][][] A = new float[M][M][M];
      final float[][][] B = new float[M][M][M];
      final float[][][] gpu = new float[M][M][M];
      float[][][] cpu = new float[M][M][M];

      for (int i = 0; i < M; i++) {
         for (int j = 0; j < M; j++) {
            for (int k = 0; k < M; k++) {
               A[i][j][k] = i + j + k;
               B[i][j][k] = (i - j) + k;
               cpu[i][j][k] = 0.0f;
               gpu[i][j][k] = 0.0f;
            }
         }
      }

      long gs = System.currentTimeMillis();
      final Kernel kernel = new FMatMul3D(A, B, gpu, M);
      kernel.execute(M * M * M);
      gs = System.currentTimeMillis() - gs;

      long cs = System.currentTimeMillis();
      cpu = matMull(A, B, M);
      cs = System.currentTimeMillis() - cs;

      System.out.println("gpu time: " + gs + "\ncpu time: " + cs);
      System.out.print("valid? ");

      if (checkResults(cpu, gpu)) {
         System.out.println("yes");
      } else {
         System.out.println("no");
      }
   }

   public static void Drun3D() {
      final double[][][] A = new double[M][M][M];
      final double[][][] B = new double[M][M][M];
      final double[][][] gpu = new double[M][M][M];
      double[][][] cpu = new double[M][M][M];

      for (int i = 0; i < M; i++) {
         for (int j = 0; j < M; j++) {
            for (int k = 0; k < M; k++) {
               A[i][j][k] = i + j + k;
               B[i][j][k] = (i - j) + k;
               cpu[i][j][k] = 0.0;
               gpu[i][j][k] = 0.0;
            }
         }
      }

      long gs = System.currentTimeMillis();
      final Kernel kernel = new DMatMul3D(A, B, gpu, M);
      kernel.execute(M * M * M);
      gs = System.currentTimeMillis() - gs;

      long cs = System.currentTimeMillis();
      cpu = matMull(A, B, M);
      cs = System.currentTimeMillis() - cs;

      System.out.println("gpu time: " + gs + "\ncpu time: " + cs);
      System.out.print("valid? ");

      if (checkResults(cpu, gpu)) {
         System.out.println("yes");
      } else {
         System.out.println("no");
      }
   }
}
