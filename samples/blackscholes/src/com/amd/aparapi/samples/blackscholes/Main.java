/*
Copyright (c) 2010-2011, Advanced Micro Devices, Inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following
disclaimer. 

Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
disclaimer in the documentation and/or other materials provided with the distribution. 

Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products
derived from this software without specific prior written permission. 

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

If you use the software (in whole or in part), you shall adhere to all applicable U.S., European, and other export
laws, including but not limited to the U.S. Export Administration Regulations ("EAR"), (15 C.F.R. Sections 730 through
774), and E.U. Council Regulation (EC) No 1334/2000 of 22 June 2000.  Further, pursuant to Section 740.6 of the EAR,
you hereby certify that, except pursuant to a license granted by the United States Department of Commerce Bureau of 
Industry and Security or as otherwise permitted pursuant to a License Exception under the U.S. Export Administration 
Regulations ("EAR"), you will not (1) export, re-export or release to a national of a country in Country Groups D:1,
E:1 or E:2 any restricted technology, software, or source code you receive hereunder, or (2) export to Country Groups
D:1, E:1 or E:2 the direct product of such technology or software, if such foreign produced direct product is subject
to national security controls as identified on the Commerce Control List (currently found in Supplement 1 to Part 774
of EAR).  For the most current Country Group listings, or for additional information about the EAR or your obligations
under those regulations, please refer to the U.S. Bureau of Industry and Security's website at http://www.bis.doc.gov/. 

*/
package com.amd.aparapi.samples.blackscholes;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

public class Main{

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
      * @brief   Abromowitz Stegun approxmimation for PHI (Cumulative Normal Distribution Function)
      * @param   X input value
      * @param   phi pointer to store calculated CND of X
      */
      float phi(float X) {
         final float c1 = 0.319381530f;
         final float c2 = -0.356563782f;
         final float c3 = 1.781477937f;
         final float c4 = -1.821255978f;
         final float c5 = 1.330274429f;

         final float zero = 0.0f;
         final float one = 1.0f;
         final float two = 2.0f;
         final float temp4 = 0.2316419f;

         final float oneBySqrt2pi = 0.398942280f;

         float absX = abs(X);
         float t = one / (one + temp4 * absX);

         float y = one - oneBySqrt2pi * exp(-X * X / two) * t * (c1 + t * (c2 + t * (c3 + t * (c4 + t * c5))));

         float result = (X < zero) ? (one - y) : y;

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
      @Override public void run() {
         float d1, d2;
         float phiD1, phiD2;
         float sigmaSqrtT;
         float KexpMinusRT;

         int gid = getGlobalId();
         float two = 2.0f;
         float inRand = randArray[gid];
         float S = S_LOWER_LIMIT * inRand + S_UPPER_LIMIT * (1.0f - inRand);
         float K = K_LOWER_LIMIT * inRand + K_UPPER_LIMIT * (1.0f - inRand);
         float T = T_LOWER_LIMIT * inRand + T_UPPER_LIMIT * (1.0f - inRand);
         float R = R_LOWER_LIMIT * inRand + R_UPPER_LIMIT * (1.0f - inRand);
         float sigmaVal = SIGMA_LOWER_LIMIT * inRand + SIGMA_UPPER_LIMIT * (1.0f - inRand);

         sigmaSqrtT = sigmaVal * sqrt(T);

         d1 = (log(S / K) + (R + sigmaVal * sigmaVal / two) * T) / sigmaSqrtT;
         d2 = d1 - sigmaSqrtT;

         KexpMinusRT = K * exp(-R * T);

         phiD1 = phi(d1);
         phiD2 = phi(d2);

         call[gid] = S * phiD1 - KexpMinusRT * phiD2;

         phiD1 = phi(-d1);
         phiD2 = phi(-d2);

         put[gid] = KexpMinusRT * phiD2 - S * phiD1;
      }

      private float randArray[];

      private float put[];

      private float call[];

      public BlackScholesKernel(int size) {
         randArray = new float[size];
         call = new float[size];
         put = new float[size];

         for (int i = 0; i < size; i++) {
            randArray[i] = i * 1.0f / size;
         }
      }

      public void showArray(float ary[], String name, int count) {
         String line;
         line = name + ": ";
         for (int i = 0; i < count; i++) {
            if (i > 0)
               line += ", ";
            line += ary[i];
         }
         System.out.println(line);
      }

      public void showResults(int count) {
         showArray(call, "Call Prices", count);
         showArray(put, "Put  Prices", count);
      }
   }

   public static void main(String[] _args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

      int size = Integer.getInteger("size", 512);
      Range range = Range.create(size);
      int iterations = Integer.getInteger("iterations", 5);
      System.out.println("size =" + size);
      System.out.println("iterations =" + iterations);
      BlackScholesKernel kernel = new BlackScholesKernel(size);

      long totalExecTime = 0;
      long iterExecTime = 0;
      /*
      for (int i = 0; i < iterations; i++) {
         iterExecTime = kernel.execute(size).getExecutionTime();
         totalExecTime += iterExecTime;
      }*/
      kernel.execute(range, iterations);
      System.out.println("Average execution time " + kernel.getAccumulatedExecutionTime() / iterations);
      kernel.showResults(10);

      kernel.dispose();
   }

}
