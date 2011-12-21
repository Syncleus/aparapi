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

package com.amd.aparapi.sample.convolution;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.amd.aparapi.Kernel;

/**
 * An example Aparapi application which demonstrates image manipulation via convolution filter
 *    http://processing.org/learning/pixels/
 *    http://docs.gimp.org/en/plug-in-convmatrix.html
 * 
 * @author Gary Frost
 */
public class Main{


   final static class ConvolutionFilter{
      private float[] weights;

      private int offset;

      ConvolutionFilter(float _nw, float _n, float ne, float _w, float _o, float _e, float _sw, float _s, float _se, int _offset) {
         weights = new float[] {
               _nw,
               _w,
               ne,
               _w,
               _o,
               _e,
               _sw,
               _s,
               _se
         };
         offset = _offset;
      }

   }

   private static final ConvolutionFilter NONE = new ConvolutionFilter(0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0);

   private static final ConvolutionFilter BLUR = new ConvolutionFilter(1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 0);

   private static final ConvolutionFilter EMBOSS = new ConvolutionFilter(-2f, -1f, 0f, -1f, 1f, 1f, 0f, 1f, 2f, 0);

   public static class ConvolutionKernel extends Kernel{

      private final float[] filter = new float[10];

      private final int[] inputData;

      private final int[] outputData;

      private final int width;

      private final int height;

      private int offset;

      public ConvolutionKernel(int _width, int _height, BufferedImage _inputImage, BufferedImage _outputImage) {
         inputData = ((DataBufferInt) _inputImage.getRaster().getDataBuffer()).getData();
         outputData = ((DataBufferInt) _outputImage.getRaster().getDataBuffer()).getData();
         width = _width;
         height = _height;
         setExplicit(true);
      }

      public void run() {

         int x = getGlobalId() % width;
         int y = getGlobalId() / width;

         if (x > 1 && x < (width - 1) && y > 1 && y < (height - 1)) {

            int result = 0;
            // We handle each color separately using rgbshift as an 8 bit mask for red, green, blue
            for (int rgbShift = 0; rgbShift < 24; rgbShift += 8) { // 0,8,16
               int channelAccum = 0;
               float accum = 0;
               int count = 0;
               for (int dx = -1; dx < 2; dx++) { // west to east
                  for (int dy = -1; dy < 2; dy++) { // north to south
                     int rgb = (inputData[((y + dy) * width) + (x + dx)]);
                     int channelValue = ((rgb >> rgbShift) & 0xff);
                     accum += filter[count];
                     channelAccum += channelValue * filter[count++];

                  }
               }
               channelAccum /= accum;
               channelAccum += offset;
               channelAccum = max(0, min(channelAccum, 0xff));
               result |= (channelAccum << rgbShift);
            }
            outputData[y * width + x] = result;
         }
      }

      public void apply(ConvolutionFilter _filter) {
         System.arraycopy(_filter.weights, 0, filter, 0, _filter.weights.length);
         offset = _filter.offset;
         put(filter);
         execute(width * height);
         get(outputData);
      }
   }

   public static void main(String[] _args) throws IOException, InterruptedException {

      JFrame frame = new JFrame("Convolution");

      BufferedImage testCard = ImageIO.read(new File("hang.jpg"));

      int imageHeight = testCard.getHeight();

      int imageWidth = testCard.getWidth();

      int padWidth = 64 - (imageWidth % 64);

      int padHeight = 64 - (imageHeight % 64);

      final int width = imageWidth + padWidth; // now multiple of 64

      final int height = imageHeight + padHeight; // now multiple of 64

      final BufferedImage inputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

      inputImage.getGraphics().drawImage(testCard, padWidth / 2, padHeight / 2, null);
      final BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      outputImage.getGraphics().drawImage(testCard, padWidth / 2, padHeight / 2, null);
      final ConvolutionKernel lifeKernel = new ConvolutionKernel(width, height, inputImage, outputImage);

      // Create a component for viewing the offscreen image
      @SuppressWarnings("serial") JComponent viewer = new JComponent(){
         @Override public void paintComponent(Graphics g) {
            g.drawImage(outputImage, 0, 0, width, height, 0, 0, width, height, this);
         }
      };

      // Set the default size and add to the frames content pane
      viewer.setPreferredSize(new Dimension(width, height));
      frame.getContentPane().add(viewer);

      // Swing housekeeping
      frame.pack();
      frame.setVisible(true);
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

      ConvolutionFilter[] filters = new ConvolutionFilter[] {
            NONE,
            BLUR,
            EMBOSS
      };
      long start = System.nanoTime();
      for (int i = 0; i < 10; i++) {
      
         for (ConvolutionFilter filter : filters) {

            lifeKernel.apply(filter); // Work is performed here

            viewer.repaint(); 
         }
      }
      System.out.println((System.nanoTime() - start) / 1000000);

   }
}
