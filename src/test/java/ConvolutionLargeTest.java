/**
 * Copyright (c) 2016 - 2017 Syncleus, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import com.aparapi.Kernel;
import com.aparapi.internal.model.CacheEnabler;
import com.aparapi.internal.model.Supplier;

public class ConvolutionLargeTest{

   private byte[] inBytes;

   private byte[] outBytes;

   private int width;

   private int height;

   private float _convMatrix3x3[];

   public ConvolutionLargeTest(String[] _args) throws IOException {
      //      final File _file = new File(_args.length == 1 ? _args[0] : "testcard.jpg");

      _convMatrix3x3 = new float[] {
            0f,
            -10f,
            0f,
            -10f,
            40f,
            -10f,
            0f,
            -10f,
            0f,
      };

      //      BufferedImage inputImage = ImageIO.read(_file);

      // System.out.println(inputImage);

      //      height = inputImage.getHeight();
      //
      //      width = inputImage.getWidth();
      //
      //      BufferedImage outputImage = new BufferedImage(width, height, inputImage.getType());
      //
      //      inBytes = ((DataBufferByte) inputImage.getRaster().getDataBuffer()).getData();
      //      outBytes = ((DataBufferByte) outputImage.getRaster().getDataBuffer()).getData();
   }

   private void prepareForSize(int pixels) {
      int side = (int) Math.sqrt(pixels);
      width = side;
      height = side;

      inBytes = new byte[width * height * 3];
      outBytes = new byte[width * height * 3];
   }

   public static void main(final String[] _args) throws IOException {
      new ConvolutionLargeTest(_args).go();
   }

   private void go() {
      boolean testWithoutCaches = true;
      int maxRounds = 5;
      for (int i = 1; i <= maxRounds; i++) {
         System.out.println("-----------------------------");
         int pixels = (1_000 * 1_000 * (1 << (i - 1))) & ~(1 << 10 - 1);
         System.out.println(MessageFormat.format("Round #{0}/{1} ({2} pixels)", i, maxRounds, pixels));
         prepareForSize(pixels);
         System.out.println("-----------------------------");
         System.out.println();
         testWithSupplier(new ImageConvolutionCreationContext(){
            private ImageConvolution convolution = new ImageConvolution();

            @Override public Supplier<ImageConvolution> getSupplier() {
               return new Supplier<ImageConvolution>(){
                  @Override public ImageConvolution get() {
                     return convolution;
                  }
               };
            }

            @Override public Consumer<ImageConvolution> getDisposer() {
               return new Consumer<ImageConvolution>(){
                  @Override public void accept(ImageConvolution k) {
                     // Do nothing
                  }
               };
            }

            @Override public void shutdown() {
               convolution.dispose();
            }

            @Override public String getName() {
               return "single kernel";
            }
         }, 10, testWithoutCaches);
         testWithSupplier(new ImageConvolutionCreationContext(){
            @Override public Supplier<ImageConvolution> getSupplier() {
               return new Supplier<ImageConvolution>(){
                  @Override public ImageConvolution get() {
                     return new ImageConvolution();
                  }
               };
            }

            @Override public Consumer<ImageConvolution> getDisposer() {
               return new Consumer<ImageConvolution>(){
                  @Override public void accept(ImageConvolution k) {
                     k.dispose();
                  }
               };
            }

            @Override public void shutdown() {
               // Do nothing
            }

            @Override public String getName() {
               return "multiple kernels";
            }
         }, 10, testWithoutCaches);
      }
   }

   private void testWithSupplier(ImageConvolutionCreationContext imageConvolutionCreationContext, int seconds,
         boolean testWithoutCaches) {
      System.out.println("Test context: " + imageConvolutionCreationContext.getName());
      CacheEnabler.setCachesEnabled(!testWithoutCaches);
      // Warmup
      doTest("Warmup (caches " + (testWithoutCaches ? "not " : "") + "enabled)", 2, imageConvolutionCreationContext);
      if (testWithoutCaches) {
         long timeWithoutCaches = doTest("Without caches", seconds, imageConvolutionCreationContext);
         CacheEnabler.setCachesEnabled(true);
         long timeWithCaches = doTest("With    caches", seconds, imageConvolutionCreationContext);
         System.out.println(MessageFormat.format("\tSpeedup: {0} %", 100d * (timeWithoutCaches - timeWithCaches)
               / timeWithoutCaches));
      } else {
         doTest("With    caches", seconds, imageConvolutionCreationContext);
      }
   }

   //   @FunctionalInterface
   private interface Consumer<K> {
      void accept(K k);
   }

   private interface ImageConvolutionCreationContext{
      Supplier<ImageConvolution> getSupplier();

      Consumer<ImageConvolution> getDisposer();

      void shutdown();

      String getName();

   }

   private long doTest(String name, int seconds, ImageConvolutionCreationContext imageConvolutionCreationContext) {
      long totalTime = 0;
      Supplier<ImageConvolution> imageConvolutionSupplier = imageConvolutionCreationContext.getSupplier();
      Consumer<ImageConvolution> disposer = imageConvolutionCreationContext.getDisposer();
      System.out.print("\tTesting " + name + "[" + imageConvolutionCreationContext.getName() + "] (" + seconds + " seconds) ");
      int calls = 0;
      long initialTime = System.nanoTime();
      long maxElapsedNs = TimeUnit.SECONDS.toNanos(seconds);
      for (;;) {
         long start = System.nanoTime();
         if (start - initialTime > maxElapsedNs)
            break;
         ImageConvolution imageConvolution = imageConvolutionSupplier.get();
         try {
            imageConvolution.applyConvolution(_convMatrix3x3, inBytes, outBytes, width, height);
         } finally {
            disposer.accept(imageConvolution);
         }

         long end = System.nanoTime();
         long roundTime = end - start;
         totalTime += roundTime;
         //         System.out.print("#" + i + " - " + roundTime + "ms ");
         //         System.out.print(roundTime + " ");
         System.out.print(".");
         calls++;
      }
      imageConvolutionCreationContext.shutdown();
      System.out.println();
      System.out.println(MessageFormat.format("\tFinished in {0} s ({1} ms/call, {2} calls)", totalTime / 1e9d,
            (totalTime / (calls * 1e6d)), calls));
      System.out.println();
      return totalTime / calls;
   }

   final static class ImageConvolution extends Kernel{

      private float convMatrix3x3[];

      private int width, height;

      private byte imageIn[], imageOut[];

      public void processPixel(int x, int y, int w, int h) {
         float accum = 0f;
         int count = 0;
         for (int dx = -3; dx < 6; dx += 3) {
            for (int dy = -1; dy < 2; dy += 1) {
               final int rgb = 0xff & imageIn[((y + dy) * w) + (x + dx)];

               accum += rgb * convMatrix3x3[count++];
            }
         }
         final byte value = (byte) (max(0, min((int) accum, 255)));
         imageOut[(y * w) + x] = value;

      }

      @Override public void run() {
         final int x = getGlobalId(0) % (width * 3);
         final int y = getGlobalId(0) / (width * 3);

         if ((x > 3) && (x < ((width * 3) - 3)) && (y > 1) && (y < (height - 1))) {
            processPixel(x, y, width * 3, height);
         }

      }

      public void applyConvolution(float[] _convMatrix3x3, byte[] _imageIn, byte[] _imageOut, int _width, int _height) {
         imageIn = _imageIn;
         imageOut = _imageOut;
         width = _width;
         height = _height;
         convMatrix3x3 = _convMatrix3x3;
         execute(3 * width * height);
      }
   }
}
