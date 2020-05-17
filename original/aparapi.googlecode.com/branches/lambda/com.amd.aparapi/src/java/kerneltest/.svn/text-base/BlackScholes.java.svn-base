package kerneltest;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

public class BlackScholes{

   public static class BlackScholesKernel extends Kernel{

      /*
      * For a description of the algorithm and the terms used, please see the
      * documentation for this sample.
      *
      * On invocation of kernel blackScholes, each work thread calculates call price
      * and put price values for given stock price, option strike price,
      * time to expiration date, risk free interest and volatility factor.
      */

      final float S_LOWER_LIMIT = 10.0f;

      final float S_UPPER_LIMIT = 100.0f;

      final float K_LOWER_LIMIT = 10.0f;

      final float K_UPPER_LIMIT = 100.0f;

      final float T_LOWER_LIMIT = 1.0f;

      final float T_UPPER_LIMIT = 10.0f;

      final float R_LOWER_LIMIT = 0.01f;

      final float R_UPPER_LIMIT = 0.05f;

      final float SIGMA_LOWER_LIMIT = 0.01f;

      final float SIGMA_UPPER_LIMIT = 0.10f;

      /**
       * @param X input value
       * @brief Abromowitz Stegun approxmimation for PHI (Cumulative Normal Distribution Function)
       */
      float phi(float X){
         final float c1 = 0.319381530f;
         final float c2 = -0.356563782f;
         final float c3 = 1.781477937f;
         final float c4 = -1.821255978f;
         final float c5 = 1.330274429f;

         final float temp4 = 0.2316419f;

         final float oneBySqrt2pi = 0.398942280f;

         float absX = abs(X);
         float t = 1f / (1f + temp4 * absX);

         float y = 1f - oneBySqrt2pi * exp(-X * X / 2f) * t * (c1 + t * (c2 + t * (c3 + t * (c4 + t * c5))));

         float result = (X < 0f) ? 1f - y : y;


         return result;
      }

      /*
      * @brief   Calculates the call and put prices by using Black Scholes model
      * @param   s       Array of random values of current option price
      * @param   sigma   Array of random values sigma
      * @param   k       Array of random values strike price
      * @param   t       Array of random values of expiration time
      * @param   r       Array of random values of risk free interest rate
      * @param   width   Width of call price or put price array
      * @param   call    Array of calculated call price values
      * @param   put     Array of calculated put price values
      */
      @Override
      public void run(){
         // float d1, d2;
         // float phiD1, phiD2;
         // float sigmaSqrtT;
         // float KexpMinusRT;

         final int gid = getGlobalId();
         final float two = 2.0f;
         final float inRand = randArray[gid];
         final float S = S_LOWER_LIMIT * inRand + S_UPPER_LIMIT * (1.0f - inRand);
         final float K = K_LOWER_LIMIT * inRand + K_UPPER_LIMIT * (1.0f - inRand);
         final float T = T_LOWER_LIMIT * inRand + T_UPPER_LIMIT * (1.0f - inRand);
         final float R = R_LOWER_LIMIT * inRand + R_UPPER_LIMIT * (1.0f - inRand);
         final float sigmaVal = SIGMA_LOWER_LIMIT * inRand + SIGMA_UPPER_LIMIT * (1.0f - inRand);

         final float sigmaSqrtT = sigmaVal * sqrt(T);

         final float d1 = (log(S / K) + (R + sigmaVal * sigmaVal / two) * T) / sigmaSqrtT;
         final float d2 = d1 - sigmaSqrtT;

         final float KexpMinusRT = K * exp(-R * T);

         float phiD1 = phi(d1);
         float phiD2 = phi(d2);

         call[gid] = S * phiD1 - KexpMinusRT * phiD2;

         phiD1 = phi(-d1);
         phiD2 = phi(-d2);

         put[gid] = KexpMinusRT * phiD2 - S * phiD1;
      }

      private float randArray[];

      private float put[];

      private float call[];

      public BlackScholesKernel(int size){
         randArray = new float[size];
         call = new float[size];
         put = new float[size];

         for(int i = 0; i < size; i++){
            randArray[i] = i * 1.0f / size;
         }
      }

      public void showArray(float ary[], String name, int count){
         String line;
         line = name + ": ";
         for(int i = 0; i < count; i++){
            if(i > 0){
               line += ", ";
            }
            line += ary[i];
         }
         System.out.println(line);
      }

      public void showResults(int count){
         showArray(call, "Call Prices", count);
         showArray(put, "Put  Prices", count);
      }
   }

   public static void main(String[] _args) throws ClassNotFoundException, InstantiationException, IllegalAccessException{

      int size = Integer.getInteger("size", 512);
      Range range = Range.create(size);
      int iterations = Integer.getInteger("iterations", 5);
      System.out.println("size =" + size);
      System.out.println("iterations =" + iterations);
      BlackScholesKernel kernel = new BlackScholesKernel(size);

      long totalExecTime = 0;
      long iterExecTime = 0;

      for(int i = 0; i < iterations; i++){
         iterExecTime = kernel.execute(size).getExecutionTime();
         totalExecTime += iterExecTime;
      }
      kernel.execute(range, iterations);
      System.out.println("Average execution time " + kernel.getAccumulatedExecutionTime() / iterations);
      kernel.showResults(10);

      kernel.dispose();
   }

}

