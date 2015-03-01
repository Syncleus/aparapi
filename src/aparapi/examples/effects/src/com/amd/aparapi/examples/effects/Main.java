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

package com.amd.aparapi.examples.effects;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

/**
 * An example Aparapi application which tracks the mouse and updates the color pallete of the window based on the distance from the mouse pointer. 
 * 
 * On GPU, additional computing units will offer a better viewing experience. On the other hand on CPU, this example 
 * application might suffer with sub-optimal frame refresh rate as compared to GPU. 
 *  
 * @author gfrost
 *
 */

public class Main{

   /**
    * An Aparapi Kernel implementation for tracking the mouse position and coloring each pixel of a window depending on proximity to the mouse position.
    *  
    * @author gfrost
    *
    */

   public static class MouseTrackKernel extends Kernel{

      /** RGB buffer used to store the screen image. This buffer holds (width * height) RGB values. */
      final private int rgb[];

      /** image width. */
      final private int width;

      /**  image height. */
      final private int height;

      /** Palette used for points */
      final private int pallette[];

      /** Maximum iterations we will check for. */
      final private int palletteSize;

      /** Mutable values of scale, offsetx and offsety so that we can modify the zoom level and position of a view. */

      final private float[] trailx;

      final private float[] traily;

      final private int trail;

      /**
       * Initialize the Kernel.
       *  
       * @param _width  image width
       * @param _height  image height
       * @param _rgb  image RGB buffer
       * @param _pallette  image palette
       * @param _trailx  float array holding x ordinates for mouse trail positions
       * @param _traily  float array holding y ordinates for mouse trail positions
       */
      public MouseTrackKernel(int _width, int _height, int[] _rgb, int[] _pallette, float[] _trailx, float[] _traily) {
         width = _width;
         height = _height;
         rgb = _rgb;
         pallette = _pallette;
         palletteSize = pallette.length - 1;
         trailx = _trailx;
         traily = _traily;
         trail = trailx.length;
      }

      @Override public void run() {

         /** Determine which RGB value we are going to process (0..RGB.length). */
         int gid = getGlobalId();

         /** Translate the gid into an x an y value. */
         float x = (gid % width);

         float y = (gid / height);

         float minRadius = 1024f;

         /** determine the minimum radius between this pixel position (x,y) and each of the trail positions _trailx[0..n],_traily[0..n] **/
         for (int i = 0; i < trail; i++) {
            float dx = x - trailx[i];
            float dy = y - traily[i];
            minRadius = min(sqrt(dx * dx + dy * dy), minRadius);
         }

         /** convert the radius length into an index into the pallete array **/
         int palletteIndex = min((int) minRadius, palletteSize);

         /** set the rgb value for this color **/
         rgb[gid] = pallette[palletteIndex];

      }

   }

   /** We track the latest mouse position here. */
   public static volatile Point mousePosition = null;

   @SuppressWarnings("serial") public static void main(String[] _args) {

      JFrame frame = new JFrame("MouseTracker");

      /** Width of Mandelbrot view. */
      final int width = 1024;

      /** Height of Mandelbrot view. */
      final int height = 1024;

      final Range range = Range.create2D(width, height);

      /** The size of the pallette of pixel colors we choose from. */
      final int palletteSize = 128;

      /** Palette which maps iteration values to RGB values. */
      final int pallette[] = new int[palletteSize + 1];

      /** Initialize palette values **/
      for (int i = 0; i < palletteSize; i++) {
         float h = i / (float) palletteSize;
         float b = 1.0f - h * h;
         pallette[i] = Color.HSBtoRGB(h, 1f, b);
      }

      /** We will keep a trail of 64 mouse positions **/
      final float trailx[] = new float[64];
      final float traily[] = new float[trailx.length];

      /** Image for view. */
      final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      final BufferedImage offscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

      /** Override the paint handler to just copy the image **/
      JComponent viewer = new JComponent(){
         @Override public void paintComponent(Graphics g) {

            g.drawImage(image, 0, 0, width, height, this);
         }
      };

      /** Set the size of JComponent which displays the image **/
      viewer.setPreferredSize(new Dimension(width, height));

      /** We use this to synchronize access from Swing display thread **/
      final Object doorBell = new Object();

      /** Mouse listener which collects the latest the mouse position from Mandelbrot view whenever the mouse is moved **/
      viewer.addMouseMotionListener(new MouseMotionAdapter(){

         @Override public void mouseMoved(MouseEvent e) {
            /** grab the mouse position **/
            mousePosition = e.getPoint();

            /** tell the waitin thread that we have a new position **/
            synchronized (doorBell) {
               doorBell.notify();
            }
         }
      });

      /** Swing housework to create the frame **/
      frame.getContentPane().add(viewer);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);

      /** Extract the underlying RGB buffer from the image. **/
      /** Pass this to the kernel so it operates directly on the RGB buffer of the image **/
      final int[] rgb = ((DataBufferInt) offscreen.getRaster().getDataBuffer()).getData();
      final int[] imageRgb = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

      /** Create a Kernel passing the size, RGB buffer, the palette and the trail positions **/
      final MouseTrackKernel kernel = new MouseTrackKernel(width, height, rgb, pallette, trailx, traily);

      /** initialize the trail positions to center of screen **/
      for (int i = 0; i < trailx.length; i++) {
         trailx[i] = (float) width / 2;
         traily[i] = (float) width / 2;
      }

      /** we use this to track the position where we insert latest mouse position, just a circular buffer **/

      int trailLastUpdatedPosition = 0;

      kernel.execute(range);
      System.arraycopy(rgb, 0, imageRgb, 0, rgb.length);
      viewer.repaint();

      /** Report target execution mode: GPU or JTP (Java Thread Pool). **/
      System.out.println("Execution mode=" + kernel.getExecutionMode());

      /** Window listener to dispose Kernel resources on user exit. **/
      frame.addWindowListener(new WindowAdapter(){
         public void windowClosing(WindowEvent _windowEvent) {
            kernel.dispose();
            System.exit(0);
         }
      });

      /** update loop**/
      while (true) {

         /** Wait for the user to move mouse **/
         while (mousePosition == null) {
            synchronized (doorBell) {
               try {
                  doorBell.wait();
               } catch (InterruptedException ie) {
                  ie.getStackTrace();
               }
            }
         }
         /** add the new x,y to the trail arrays and bump the poition **/
         trailx[trailLastUpdatedPosition % trailx.length] = (float) mousePosition.x;
         traily[trailLastUpdatedPosition % traily.length] = (float) mousePosition.y;
         trailLastUpdatedPosition++;

         /** execute the kernel which calculates new pixel values **/
         kernel.execute(range);

         /** copy the rgb values to the imageRgb buffer **/
         System.arraycopy(rgb, 0, imageRgb, 0, rgb.length);

         /** request a repaint **/
         viewer.repaint();
      }

   }

}
