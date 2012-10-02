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

package com.amd.aparapi.examples.javaonedemo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.ProfileInfo;
import com.amd.aparapi.Range;

/**
 * An example Aparapi application which demonstrates Conways 'Game Of Life'.
 * 
 * Original code from Witold Bolt's site https://github.com/houp/aparapi/tree/master/samples/gameoflife.
 * 
 * Converted to use int buffer and some performance tweaks by Gary Frost
 * 
 * @author Wiltold Bolt
 * @author Gary Frost
 */
public class Life{

   /**
    * LifeKernel represents the data parallel algorithm describing by Conway's game of life.
    * 
    * http://en.wikipedia.org/wiki/Conway's_Game_of_Life
    * 
    * We examine the state of each pixel and its 8 neighbors and apply the following rules. 
    * 
    * if pixel is dead (off) and number of neighbors == 3 {
    *       pixel is turned on
    * } else if pixel is alive (on) and number of neighbors is neither 2 or 3
    *       pixel is turned off
    * }
    * 
    * We use an image buffer which is 2*width*height the size of screen and we use fromBase and toBase to track which half of the buffer is being mutated for each pass. We basically 
    * copy from getGlobalId()+fromBase to getGlobalId()+toBase;
    * 
    * 
    * Prior to each pass the values of fromBase and toBase are swapped.
    *
    */

   public static class LifeKernel extends Kernel{

      private static final int ALIVE = 0xffffff;

      private static final int DEAD = 0;

      private final int[] imageData;

      private final int width;

      private final int height;

      private final Range range;

      private int fromBase;

      private int toBase;

      public LifeKernel(int _width, int _height, BufferedImage _image, Kernel.EXECUTION_MODE _mode) {
         setExecutionMode(_mode);
         imageData = ((DataBufferInt) _image.getRaster().getDataBuffer()).getData();
         width = _width;
         height = _height;
         range = Range.create(width * height, 256);
         System.out.println("range = " + range);

         setExplicit(_mode.equals(Kernel.EXECUTION_MODE.GPU)); // This gives us a performance boost for GPU mode.

         fromBase = height * width;
         toBase = 0;
         Arrays.fill(imageData, LifeKernel.DEAD);
         /** draw a line across the image **/
         for (int i = width * (height / 2) + width / 10; i < width * (height / 2 + 1) - width / 10; i++) {
            imageData[toBase + i] = LifeKernel.ALIVE;
            imageData[fromBase + i] = LifeKernel.ALIVE;
         }
         if (isExplicit()) {
            put(imageData); // Because we are using explicit buffer management we must put the imageData array
         }

      }

      @Override public void run() {
         int gid = getGlobalId();
         int to = gid + toBase;
         int from = gid + fromBase;
         int x = gid % width;
         int y = gid / width;

         if ((x == 0 || x == width - 1 || y == 0 || y == height - 1)) {
            // This pixel is on the border of the view, just keep existing value
            imageData[to] = imageData[from];
         } else {
            // Count the number of neighbors.  We use (value&1x) to turn pixel value into either 0 or 1
            int neighbors = (imageData[from - 1] & 1) + // EAST
                  (imageData[from + 1] & 1) + // WEST
                  (imageData[from - width - 1] & 1) + // NORTHEAST                 
                  (imageData[from - width] & 1) + // NORTH
                  (imageData[from - width + 1] & 1) + // NORTHWEST
                  (imageData[from + width - 1] & 1) + // SOUTHEAST
                  (imageData[from + width] & 1) + // SOUTH
                  (imageData[from + width + 1] & 1); // SOUTHWEST

            // The game of life logic
            if (neighbors == 3 || (neighbors == 2 && imageData[from] == ALIVE)) {
               imageData[to] = ALIVE;
            } else {
               imageData[to] = DEAD;
            }

         }

      }

      public void nextGeneration() {
         // swap fromBase and toBase
         int swap = fromBase;
         fromBase = toBase;
         toBase = swap;

         execute(range);

      }

   }

   static boolean running = false;

   static LifeKernel lifeKernel = null;

   static double generationsPerSecondField = 0;

   public static void main(String[] _args) {

      JFrame frame = new JFrame("Game of Life");
      final int width = Integer.getInteger("width", 1024 + 512);

      final int height = Integer.getInteger("height", 768);

      // Buffer is twice the size as the screen.  We will alternate between mutating data from top to bottom
      // and bottom to top in alternate generation passses. The LifeKernel will track which pass is which
      final BufferedImage image = new BufferedImage(width, height * 2, BufferedImage.TYPE_INT_RGB);

      final LifeKernel lifeKernelJTP = new LifeKernel(width, height, image, Kernel.EXECUTION_MODE.JTP);

      final LifeKernel lifeKernelGPU = new LifeKernel(width, height, image, Kernel.EXECUTION_MODE.GPU);
      lifeKernel = lifeKernelJTP;

      final Font font = new Font("Garamond", Font.BOLD, 100);
      // Create a component for viewing the offsecreen image
      @SuppressWarnings("serial") JComponent viewer = new JComponent(){
         @Override public void paintComponent(Graphics g) {
            g.setFont(font);
            if (lifeKernel != null) {
               if (lifeKernel.isExplicit()) {
                  lifeKernel.get(lifeKernel.imageData); // We only pull the imageData when we intend to use it.
                  List<ProfileInfo> profileInfo = lifeKernel.getProfileInfo();
                  if (profileInfo != null) {
                     for (ProfileInfo p : profileInfo) {
                        System.out.print(" " + p.getType() + " " + p.getLabel() + " " + (p.getStart() / 1000) + " .. "
                              + (p.getEnd() / 1000) + " " + (p.getEnd() - p.getStart()) / 1000 + "us");
                     }
                     System.out.println();
                  }
               }
               // We copy one half of the offscreen buffer to the viewer, we copy the half that we just mutated.
               if (lifeKernel.fromBase == 0) {
                  g.drawImage(image, 0, 0, width, height, 0, 0, width, height, this);
               } else {
                  g.drawImage(image, 0, 0, width, height, 0, height, width, 2 * height, this);
               }
               g.drawString(String.format("%5.2f", generationsPerSecondField), 20, 100);

            }
         }
      };

      JPanel controlPanel = new JPanel(new FlowLayout());
      frame.getContentPane().add(controlPanel, BorderLayout.SOUTH);

      final JButton startButton = new JButton("Start");

      startButton.addActionListener(new ActionListener(){
         @Override public void actionPerformed(ActionEvent e) {
            running = true;
            startButton.setEnabled(false);
         }
      });
      controlPanel.add(startButton);

      final String[] choices = new String[] {
            // "Java Sequential",
            "Java Threads",
            "GPU OpenCL"
      };

      final JComboBox modeButton = new JComboBox(choices);

      modeButton.addItemListener(new ItemListener(){
         @Override public void itemStateChanged(ItemEvent e) {
            String item = (String) modeButton.getSelectedItem();

            // if (item.equals(choices[2])) {
            // modeButton = gpuMandelBrot;
            //   } else 
            if (item.equals(choices[0])) {
               lifeKernel = lifeKernelJTP;

               // modeButton = javaMandelBrot;
            } else if (item.equals(choices[1])) {
               lifeKernel = lifeKernelGPU;
               // modeButton = javaMandelBrotMultiThread;
            }
         }

      });
      controlPanel.add(modeButton);

      //  controlPanel.add(new JLabel(lifeKernel.getExecutionMode().toString()));

      //  controlPanel.add(new JLabel("  Generations/Second="));
      //  JLabel generationsPerSecond = new JLabel("0.00");
      //  controlPanel.add(generationsPerSecond);

      // Set the default size and add to the frames content pane
      viewer.setPreferredSize(new Dimension(width, height));
      frame.getContentPane().add(viewer);

      // Swing housekeeping
      frame.pack();
      frame.setVisible(true);
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

      long start = System.currentTimeMillis();
      long generations = 0;
      while (!running) {
         try {
            Thread.sleep(10);
            viewer.repaint();
         } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
         }
      }
      while (true) {

         lifeKernel.nextGeneration(); // Work is performed here
         viewer.repaint(); // Request a repaint of the viewer (causes paintComponent(Graphics) to be called later not synchronous
         generations++;
         long now = System.currentTimeMillis();
         if (now - start > 1000) {
            generationsPerSecondField = (generations * 1000.0) / (now - start);
            //  generationsPerSecond.setText(String.format("%5.2f", generationsPerSecondField));
            start = now;
            generations = 0;
         }
      }

   }
}
