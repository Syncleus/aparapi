
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

@SuppressWarnings("serial") public abstract class ConvolutionViewer extends JFrame{

   private int height;

   private int width;

   private BufferedImage outputImage;

   private BufferedImage inputImage;

   private byte[] inBytes;

   private byte[] outBytes;

   private Graphics2D gc;

   private float[] convMatrix3x3;

   public ConvolutionViewer(File _file, float[] _convMatrix3x3) {

      JFrame frame = new JFrame("Convolution Viewer");

      convMatrix3x3 = _convMatrix3x3;
      try {
         inputImage = ImageIO.read(_file);

         // System.out.println(inputImage);

         height = inputImage.getHeight();

         width = inputImage.getWidth();

         outputImage = new BufferedImage(width, height, inputImage.getType());

         gc = outputImage.createGraphics();

         inBytes = ((DataBufferByte) inputImage.getRaster().getDataBuffer()).getData();
         outBytes = ((DataBufferByte) outputImage.getRaster().getDataBuffer()).getData();

         final JLabel imageLabel = new JLabel();
         imageLabel.setIcon(new ImageIcon(outputImage));

         ConvMatrix3x3Editor editor = new ConvMatrix3x3Editor(_convMatrix3x3){
            @Override protected void updated(float[] _convMatrix3x3) {
               convMatrix3x3 = _convMatrix3x3;
               long start = System.currentTimeMillis();

               applyConvolution(convMatrix3x3, inBytes, outBytes, width, height);
               long end = System.currentTimeMillis();
               gc.setColor(Color.BLACK);
               gc.fillRect(0, 0, 50, 40);
               gc.setColor(Color.YELLOW);
               gc.drawString("" + (end - start) + "ms", 10, 20);

               imageLabel.repaint();
            }
         };
         frame.getContentPane().add(editor.component, BorderLayout.WEST);

         frame.getContentPane().add(imageLabel, BorderLayout.CENTER);
         frame.pack();
         frame.setVisible(true);
         frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

         applyConvolution(convMatrix3x3, inBytes, outBytes, width, height);

         imageLabel.repaint();
      } catch (IOException e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }

   }

   abstract protected void applyConvolution(float[] convMatrix3x3, byte[] _inBytes, byte[] _outBytes, int _width, int _height);

}
