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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ConvMatrix3x3Editor{
   Component component;

   float[] default3x3;

   float[] none3x3 = new float[] {
         0,
         0,
         0,
         0,
         1,
         0,
         0,
         0,
         0
   };

   float[] blur3x3 = new float[] {
         .1f,
         .1f,
         .1f,
         .1f,
         .1f,
         .1f,
         .1f,
         .1f,
         .1f
   };

   JSpinner[] spinners = new JSpinner[9];

   protected void updated(float[] _convMatrix3x3) {

   };

   void set(float[] _to, float[] _from) {
      for (int i = 0; i < 9; i++) {
         _to[i] = _from[i];
         spinners[i].setValue((Double) (double) _to[i]);

      }
      updated(_to);
   }

   ConvMatrix3x3Editor(final float[] _convMatrix3x3) {
      default3x3 = Arrays.copyOf(_convMatrix3x3, _convMatrix3x3.length);
      JPanel leftPanel = new JPanel();
      JPanel controlPanel = new JPanel();
      BoxLayout layout = new BoxLayout(controlPanel, BoxLayout.Y_AXIS);
      controlPanel.setLayout(layout);
      component = leftPanel;
      JPanel grid3x3Panel = new JPanel();
      controlPanel.add(grid3x3Panel);
      grid3x3Panel.setLayout(new GridLayout(3, 3));
      for (int i = 0; i < 9; i++) {
         final int index = i;
         SpinnerModel model = new SpinnerNumberModel(_convMatrix3x3[index], -50f, 50f, 1f);
         JSpinner spinner = new JSpinner(model);
         spinners[i] = spinner;
         spinner.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent ce) {
               JSpinner source = (JSpinner) ce.getSource();
               double value = ((Double) source.getValue());
               _convMatrix3x3[index] = (float) value;
               updated(_convMatrix3x3);
            }
         });
         grid3x3Panel.add(spinner);
      }
      String[] options = new String[] {
            "DEFAULT",
            "NONE",
            "BLUR"
      };
      JComboBox combo = new JComboBox(options);
      combo.addActionListener(new ActionListener(){

         @Override public void actionPerformed(ActionEvent e) {
            JComboBox cb = (JComboBox) e.getSource();
            String value = (String) cb.getSelectedItem();
            if (value.equals("DEFAULT")) {
               set(_convMatrix3x3, default3x3);
            } else if (value.equals("NONE")) {
               set(_convMatrix3x3, none3x3);
            } else if (value.equals("BLUR")) {
               set(_convMatrix3x3, blur3x3);
            }
         }

      });
      controlPanel.add(combo);

      leftPanel.add(controlPanel, BorderLayout.NORTH);
   }
}
