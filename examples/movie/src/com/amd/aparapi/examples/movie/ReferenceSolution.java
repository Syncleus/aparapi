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
package com.amd.aparapi.examples.movie;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

public class ReferenceSolution{

   public static class Convolution extends Kernel{

      private byte[] inputData;

      private byte[] outputData;

      private int width;

      private int height;

      private Range range;

      float[] convMatrix3x3;

      public Convolution(BufferedImage _imageIn, BufferedImage _imageOut) {
         inputData = ((DataBufferByte) _imageIn.getRaster().getDataBuffer()).getData();
         outputData = ((DataBufferByte) _imageOut.getRaster().getDataBuffer()).getData();
         width = _imageIn.getWidth();
         height = _imageIn.getHeight();
         range = Range.create2D(width * 3, height);
         setExplicit(true);

      }

      public void processPixel(int x, int y, int w, int h) {
         float accum = 0;
         int count = 0;
         for (int dx = -3; dx < 6; dx += 3) {
            for (int dy = -1; dy < 2; dy += 1) {
               int rgb = 0xff & inputData[((y + dy) * w) + (x + dx)];
               accum += rgb * convMatrix3x3[count++];
            }
         }
         outputData[y * w + x] = (byte) Math.max(0, Math.min((int) accum, 255));
      }

      public void run() {
         int x = getGlobalId(0);
         int y = getGlobalId(1);
         int w = getGlobalSize(0);
         int h = getGlobalSize(1);
         if (x > 3 && x < (w - 3) && y > 1 && y < (h - 1)) {
            processPixel(x, y, w, h);
         } else {
            outputData[y * w + x] = inputData[(y * w) + x];
         }
      }

      public void apply(float[] _convMatrix3x3) {
         convMatrix3x3 = _convMatrix3x3;
         for (int x = 0; x < width * 3; x++) {
            for (int y = 0; y < height; y++) {
               if (x > 3 && x < (width * 3 - 3) && y > 1 && y < (height - 1)) {
                  processPixel(x, y, width * 3, height);
               }
            }
         }
      }

   }

   public static void main(final String[] _args) {
      String fileName = _args.length == 1 ? _args[0] : "Leo720p.wmv";

      float[] convMatrix3x3 = new float[] {
            0f,
            -10f,
            0f,
            -10f,
            41f,
            -10f,
            0f,
            -10f,
            0f
      };
      new JJMPEGPlayer("Aparapi - Solution", fileName, convMatrix3x3){
         Convolution kernel = null;

         @Override protected void processFrame(Graphics2D gc, float[] _convMatrix3x3, BufferedImage in, BufferedImage out) {
            if (kernel == null) {
               kernel = new Convolution(in, out);
            }
            kernel.apply(_convMatrix3x3);
         }
      };

   }
}
