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

package com.amd.aparapi.sample.mandel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.ProfileInfo;
import com.amd.aparapi.Range;

/**
 * An example Aparapi application which displays a view of the Mandelbrot set and lets the user zoom in to a particular point. 
 * 
 * When the user clicks on the view, this example application will zoom in to the clicked point and zoom out there after.
 * On GPU, additional computing units will offer a better viewing experience. On the other hand on CPU, this example 
 * application might suffer with sub-optimal frame refresh rate as compared to GPU. 
 *  
 * @author gfrost
 *
 */

public class Main{

   /**
    * An Aparapi Kernel implementation for creating a scaled view of the mandelbrot set.
    *  
    * @author gfrost
    *
    */

   public static class MandelKernel extends Kernel{

      /** RGB buffer used to store the Mandelbrot image. This buffer holds (width * height) RGB values. */
      final private int rgb[];

      /** Mandelbrot image width. */
      final private int width;

      /** Mandelbrot image height. */
      final private int height;

      /** Maximum iterations for Mandelbrot. */
      final private int maxIterations = 64;

      /** Palette which maps iteration values to RGB values. */
      @Constant final private int pallette[] = new int[maxIterations + 1];

      /** Mutable values of scale, offsetx and offsety so that we can modify the zoom level and position of a view. */
      private float scale = .0f;

      private float offsetx = .0f;

      private float offsety = .0f;

      /**
       * Initialize the Kernel.
       *  
       * @param _width Mandelbrot image width
       * @param _height Mandelbrot image height
       * @param _rgb Mandelbrot image RGB buffer
       * @param _pallette Mandelbrot image palette
       */
      public MandelKernel(int _width, int _height, int[] _rgb) {
         //Initialize palette values
         for (int i = 0; i < maxIterations; i++) {
            float h = i / (float) maxIterations;
            float b = 1.0f - h * h;
            pallette[i] = Color.HSBtoRGB(h, 1f, b);
         }

         width = _width;
         height = _height;
         rgb = _rgb;

      }

      @Override public void run() {

         /** Determine which RGB value we are going to process (0..RGB.length). */
         int gid = getGlobalId();

         /** Translate the gid into an x an y value. */
         float x = (((gid % width * scale) - ((scale / 2) * width)) / width) + offsetx;

         float y = (((gid / width * scale) - ((scale / 2) * height)) / height) + offsety;

         int count = 0;

         float zx = x;
         float zy = y;
         float new_zx = 0f;

         // Iterate until the algorithm converges or until maxIterations are reached.
         while (count < maxIterations && zx * zx + zy * zy < 8) {
            new_zx = zx * zx - zy * zy + x;
            zy = 2 * zx * zy + y;
            zx = new_zx;
            count++;
         }

         // Pull the value out of the palette for this iteration count.
         rgb[gid] = pallette[count];
      }

      public void setScaleAndOffset(float _scale, float _offsetx, float _offsety) {
         offsetx = _offsetx;
         offsety = _offsety;
         scale = _scale;
      }

   }

   /** User selected zoom-in point on the Mandelbrot view. */
   public static volatile Point to = null;

   @SuppressWarnings("serial") public static void main(String[] _args) {

      JFrame frame = new JFrame("MandelBrot");

      /** Width of Mandelbrot view. */
      final int width = 768;

      /** Height of Mandelbrot view. */
      final int height = 768;

      /** Mandelbrot image height. */
      final Range range = Range.create(width * height);

      /** Image for Mandelbrot view. */
      final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      final BufferedImage offscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      // Draw Mandelbrot image
      JComponent viewer = new JComponent(){
         @Override public void paintComponent(Graphics g) {

            g.drawImage(image, 0, 0, width, height, this);
         }
      };

      // Set the size of JComponent which displays Mandelbrot image
      viewer.setPreferredSize(new Dimension(width, height));

      final Object doorBell = new Object();

      // Mouse listener which reads the user clicked zoom-in point on the Mandelbrot view 
      viewer.addMouseListener(new MouseAdapter(){
         @Override public void mouseClicked(MouseEvent e) {
            to = e.getPoint();
            synchronized (doorBell) {
               doorBell.notify();
            }
         }
      });

      // Swing housework to create the frame
      frame.getContentPane().add(viewer);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);

      // Extract the underlying RGB buffer from the image.
      // Pass this to the kernel so it operates directly on the RGB buffer of the image
      final int[] rgb = ((DataBufferInt) offscreen.getRaster().getDataBuffer()).getData();
      final int[] imageRgb = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
      // Create a Kernel passing the size, RGB buffer and the palette.
      final MandelKernel kernel = new MandelKernel(width, height, rgb);

      float defaultScale = 3f;

      // Set the default scale and offset, execute the kernel and force a repaint of the viewer.
      kernel.setScaleAndOffset(defaultScale, -1f, 0f);
      kernel.execute(range);

      System.arraycopy(rgb, 0, imageRgb, 0, rgb.length);
      viewer.repaint();

      // Report target execution mode: GPU or JTP (Java Thread Pool).
      System.out.println("Execution mode=" + kernel.getExecutionMode());

      // Window listener to dispose Kernel resources on user exit.
      frame.addWindowListener(new WindowAdapter(){
         public void windowClosing(WindowEvent _windowEvent) {
            kernel.dispose();
            System.exit(0);
         }
      });

      // Wait until the user selects a zoom-in point on the Mandelbrot view.
      while (true) {

         // Wait for the user to click somewhere
         while (to == null) {
            synchronized (doorBell) {
               try {
                  doorBell.wait();
               } catch (InterruptedException ie) {
                  ie.getStackTrace();
               }
            }
         }

         float x = -1f;
         float y = 0f;
         float scale = defaultScale;
         float tox = (float) (to.x - width / 2) / width * scale;
         float toy = (float) (to.y - height / 2) / height * scale;

         // This is how many frames we will display as we zoom in and out.
         int frames = 128;
         long startMillis = System.currentTimeMillis();
         for (int sign = -1; sign < 2; sign += 2) {
            for (int i = 0; i < frames - 4; i++) {
               scale = scale + sign * defaultScale / frames;
               x = x - sign * (tox / frames);
               y = y - sign * (toy / frames);

               // Set the scale and offset, execute the kernel and force a repaint of the viewer.
               kernel.setScaleAndOffset(scale, x, y);
               kernel.execute(range);
               List<ProfileInfo> profileInfo = kernel.getProfileInfo();
               if (profileInfo != null && profileInfo.size() > 0) {
                  for (ProfileInfo p : profileInfo) {
                     System.out.print(" " + p.getType() + " " + p.getLabel() + " " + (p.getStart() / 1000) + " .. "
                           + (p.getEnd() / 1000) + " " + (p.getEnd() - p.getStart()) / 1000 + "us");
                  }
                  System.out.println();
               }

               System.arraycopy(rgb, 0, imageRgb, 0, rgb.length);
               viewer.repaint();
            }
         }

         long elapsedMillis = System.currentTimeMillis() - startMillis;
         System.out.println("FPS = " + frames * 1000 / elapsedMillis);

         // Reset zoom-in point.
         to = null;

      }

   }

}
