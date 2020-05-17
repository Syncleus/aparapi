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

package com.amd.aparapi.sample.grid;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

public class Main{

   public static class EqualGroupKernel extends Kernel{
      final private int rgb[];

      int group;

      int[] numGroups = new int[] {
         0
      };

      public EqualGroupKernel(int[] _rgb) {
         rgb = _rgb;
      }

      @Override public void run() {
         int gid = (getGlobalSize() * getGlobalId(1)) + getGlobalId(0);
         numGroups[0] = getNumGroups();
         if ((getGroupId(1) * getNumGroups(0)) + getGroupId(0) == group) {
            rgb[gid] = 0xffffff;
         } else {
            rgb[gid] = 0x0;
         }
      }

      public void setGroup(int _group) {
         group = _group;
      }

      public int getGroupCount() {
         return (numGroups[0]);
      }

      public int incGroup() {
         group++;
         return (group);
      }
   }

   public static class LocalXAndYZeroKernel extends Kernel{
      final private int rgb[];

      public LocalXAndYZeroKernel(int[] _rgb) {
         rgb = _rgb;
      }

      @Override public void run() {
         int gid = (getGlobalSize(0) * getGlobalId(1)) + getGlobalId(0);

         if (getLocalId(0) == 0 && getLocalId(1) == 0) {
            rgb[gid] = 0xffffff;
         } else {
            rgb[gid] = 0x0;
         }
      }

   }

   public static class LocalXOrYZeroKernel extends Kernel{
      final private int rgb[];

      public LocalXOrYZeroKernel(int[] _rgb) {
         rgb = _rgb;
      }

      @Override public void run() {
         int gid = (getGlobalSize(0) * getGlobalId(1)) + getGlobalId(0);

         if (getLocalId(0) == 0 || getLocalId(1) == 0) {

            rgb[gid] = 0xffffff;
         } else {
            rgb[gid] = 0x0;
         }
      }

   }

   @SuppressWarnings("serial") public static void main(String[] _args) {

      JFrame frame = new JFrame("Grid");

      final Range range = Range.create2D(512, 512, 16, 16);

      final BufferedImage image = new BufferedImage(range.getGlobalSize(0), range.getGlobalSize(0), BufferedImage.TYPE_INT_RGB);
      final int[] imageRgb = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
      final EqualGroupKernel equalGroupKernel = new EqualGroupKernel(imageRgb);
      final LocalXAndYZeroKernel localXAndYZeroKernel = new LocalXAndYZeroKernel(imageRgb);
      final LocalXOrYZeroKernel localXOrYZeroKernel = new LocalXOrYZeroKernel(imageRgb);
      final JComponent viewer = new JComponent(){
         @Override public void paintComponent(Graphics g) {
            g.drawImage(image, 0, 0, range.getGlobalSize(0), range.getGlobalSize(1), this);
         }

         @Override public Dimension getPreferredSize() {
            return (new Dimension(range.getGlobalSize(0), range.getGlobalSize(1)));
         }
      };

      JPanel controls = new JPanel();

      JButton nextGroupButton = new JButton("Next Group");
      nextGroupButton.addActionListener(new ActionListener(){
         @Override public void actionPerformed(ActionEvent e) {
            equalGroupKernel.incGroup();
            equalGroupKernel.execute(range);
            System.out.println(" group " + equalGroupKernel.incGroup() + " of " + equalGroupKernel.getGroupCount());
            viewer.repaint();
         }
      });
      controls.add(nextGroupButton);

      JButton localXAndYZeroButton = new JButton("Local X and Y ==0 ");
      localXAndYZeroButton.addActionListener(new ActionListener(){
         @Override public void actionPerformed(ActionEvent e) {
            localXAndYZeroKernel.execute(range);
            viewer.repaint();
         }
      });
      controls.add(localXAndYZeroButton);

      JButton localXOrYZeroButton = new JButton("Local X or Y ==0 ");
      localXOrYZeroButton.addActionListener(new ActionListener(){
         @Override public void actionPerformed(ActionEvent e) {
            localXOrYZeroKernel.execute(range);
            viewer.repaint();
         }
      });
      controls.add(localXOrYZeroButton);

      // Window listener to dispose Kernel resources on user exit.
      frame.addWindowListener(new WindowAdapter(){
         public void windowClosing(WindowEvent _windowEvent) {
            equalGroupKernel.dispose();
            localXAndYZeroKernel.dispose();
            localXOrYZeroKernel.dispose();
            System.exit(0);
         }
      });
      frame.getContentPane().add(viewer, BorderLayout.CENTER);
      frame.getContentPane().add(controls, BorderLayout.SOUTH);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);

   }

}
