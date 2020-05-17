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
package com.amd.aparapi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import com.amd.aparapi.Diff.DiffResult;

public class SwingDiff{
   JFrame frame;

   public SwingDiff(DiffResult result) {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

         frame = new JFrame("SwingDiff");

         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         JPanel panel = new JPanel(){
            @Override public void paint(Graphics g) {
               super.paint(g);
               g.drawRect(10, 10, 100, 100);
            }
         };
         panel.setLayout(new BorderLayout());

         StyleContext sc = new StyleContext();

         // Create and add the style
         final Style rootStyle = sc.addStyle("Root", null);
         rootStyle.addAttribute(StyleConstants.Foreground, Color.black);
         rootStyle.addAttribute(StyleConstants.FontSize, new Integer(12));
         rootStyle.addAttribute(StyleConstants.FontFamily, "serif");
         rootStyle.addAttribute(StyleConstants.Bold, new Boolean(false));
         final Style heading1Style = sc.addStyle("Heading1", rootStyle);
         heading1Style.addAttribute(StyleConstants.Foreground, Color.blue);

         final Style heading2Style = sc.addStyle("Heading2", rootStyle);
         heading2Style.addAttribute(StyleConstants.Foreground, Color.red);
         heading2Style.addAttribute(StyleConstants.Background, Color.green);

         final DefaultStyledDocument lhsdoc = new DefaultStyledDocument(sc);
         JTextPane lhs = new JTextPane(lhsdoc);

         lhsdoc.insertString(0, arrayToString(result.getLhs()), null);

         // Finally, apply the style to the heading

         lhsdoc.setParagraphAttributes(4, 1, heading2Style, false);
         lhsdoc.setParagraphAttributes(20, 5, heading1Style, false);

         lhs.setPreferredSize(new Dimension(800, 800));
         final DefaultStyledDocument rhsdoc = new DefaultStyledDocument(sc);
         JTextPane rhs = new JTextPane(rhsdoc);
         rhsdoc.insertString(0, arrayToString(result.getRhs()), null);

         rhsdoc.setParagraphAttributes(4, 1, heading2Style, false);
         rhsdoc.setParagraphAttributes(20, 5, heading1Style, false);
         rhs.setPreferredSize(new Dimension(800, 800));
         panel.add(new JScrollPane(lhs), BorderLayout.WEST);
         panel.add(new JScrollPane(rhs), BorderLayout.EAST);

         // frame.setBackground(background);
         frame.getContentPane().add(panel);
         frame.pack();
         frame.setVisible(true);
      } catch (ClassNotFoundException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (InstantiationException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (UnsupportedLookAndFeelException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (BadLocationException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

   }

   public static void main(String[] args) {
      String[] lhs = getFileContents("expected.c");
      String[] rhs = getFileContents("actual.c");

      DiffResult result = Diff.diff(lhs, rhs);
      System.out.println(result);

      SwingDiff swingDiff = new SwingDiff(result);
   }

   private static String arrayToString(String[] array) {
      StringBuilder stringBuilder = new StringBuilder();
      for (String line : array) {
         stringBuilder.append(line).append("\n");
      }
      return (stringBuilder.toString().trim());
   }

   private static String[] getFileContents(String string) {
      String[] content = null;
      try {
         BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(string)));
         List<String> lines = new ArrayList<String>();
         for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            lines.add(line);
         }
         reader.close();
         content = lines.toArray(new String[0]);
      } catch (FileNotFoundException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return (content);

   }

   private static String getFileContent(String string) {
      String content = null;
      try {
         BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(string)));
         StringBuilder sb = new StringBuilder();
         for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            sb.append(line).append("\n");
         }
         reader.close();
         content = sb.toString();
      } catch (FileNotFoundException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return (content);

   }

}
