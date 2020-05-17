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

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class UpdateJUnits{
   public static class Editor{
      final static SimpleAttributeSet del = new SimpleAttributeSet();
      final static SimpleAttributeSet ins = new SimpleAttributeSet();
      final static SimpleAttributeSet equal = new SimpleAttributeSet();

      static{
         del.addAttribute(StyleConstants.CharacterConstants.Background, Color.RED.brighter().brighter());
         ins.addAttribute(StyleConstants.CharacterConstants.Background, Color.GREEN.brighter().brighter());
      }

      volatile boolean changing = false;

      StyledDocument document = new DefaultStyledDocument();
      JTextPane textPane = new JTextPane(document);

      JScrollPane scrollPane = new JScrollPane(textPane);
      JPanel panel = new JPanel();

      Editor(String name, int _width, int _height, boolean _editable){
         panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
         panel.add(new JLabel(name));
         textPane.setEditable(_editable);
         scrollPane.setPreferredSize(new Dimension(_width, _height));
         panel.add(scrollPane);
      }

      Editor startChange(){
         changing = true;
         return (this);
      }

      Editor endChange(){
         changing = false;
         return (this);
      }


      Editor clear(){
         try{
            document.remove(0, document.getLength());
         }catch(BadLocationException e){
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
         }
         return (this);
      }

      Editor add(String _string, SimpleAttributeSet _attr){
         try{
            document.insertString(document.getLength(), _string, _attr);
         }catch(BadLocationException e){
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
         }
         return (this);
      }

      Editor del(String _string){
         add(_string, del);
         return (this);
      }

      Editor ins(String _string){
         add(_string, ins);
         return (this);
      }

      Editor equal(String _string){
         add(_string, equal);
         return (this);
      }


      String getText(){
         String text = null;
         try{
            text = document.getText(0, document.getLength());
         }catch(BadLocationException e){
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
         }
         return (text);
      }


      JComponent getComponent(){
         return (panel);
      }


   }

   public static Source source = null;
   public static Editor javaEditor = new Editor("Java", 300, 300, true);
   public static Editor expectedOpenCLEditor = new Editor("Expected OpenCL", 600, 600, true);
   public static Editor actualOpenCLEditor = new Editor("Actual OpenCL", 600, 600, false);
   public static DiffMatchPatch dmp = new DiffMatchPatch();


   static void save(Source source){
      StringBuilder sb = new StringBuilder();
      sb.append(javaEditor.getText());
      sb.append("\n");
      if(source.hasThrows()){
         sb.append(Source.ThrowsStart).append("\n").append(source.getThrows()).append("\n").append(Source.ThrowsEnd);
      }
      if(source.hasMode()){
         sb.append(Source.ModeStart).append("\n").append(source.getMode()).append("\n").append(Source.ModeEnd);
      }
      if(source.hasHSAIL()){
         sb.append(Source.HSAILStart).append("\n").append(source.getHSAIL()).append("\n").append(Source.HSAILEnd);
      }
      if(source.hasOpenCL()){
         sb.append(Source.OpenCLStart).append("\n").append(expectedOpenCLEditor.getText()).append("\n").append(Source.OpenCLEnd);
      }

      try{
         Writer w = new FileWriter(source.file);
         w.append(sb.toString());
         w.close();
      }catch(IOException ioe){

      }
   }

   static void colorise(String _expected, String _actual){
      LinkedList<DiffMatchPatch.Diff> res = dmp.diff_main(_expected, _actual);
      expectedOpenCLEditor.startChange().clear();
      actualOpenCLEditor.startChange().clear();
      for(DiffMatchPatch.Diff aDiff : res){
         String text = aDiff.text;
         switch(aDiff.operation){
            case INSERT:
               actualOpenCLEditor.ins(text);
               break;
            case DELETE:
               expectedOpenCLEditor.del(text);
               break;
            case EQUAL:
               actualOpenCLEditor.equal(text);
               expectedOpenCLEditor.equal(text);
               break;
         }
      }
      expectedOpenCLEditor.endChange();
      actualOpenCLEditor.endChange();
   }

   public static void main(String[] args) throws ClassNotFoundException, FileNotFoundException, IOException{

      try{
         for(UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
            if("Nimbus".equals(info.getName())){
               UIManager.setLookAndFeel(info.getClassName());
               break;
            }
         }
      }catch(Exception e){
         // If Nimbus is not available, you can set the GUI to another look and feel.
      }
      File rootDir = new File(System.getProperty("root", "."));

      final String rootPackageName = CreateJUnitTests.class.getPackage().getName();
      final String testPackageName = rootPackageName + ".test";
      final File sourceDir = new File(rootDir, "src/java");
      File testDir = new File(sourceDir, testPackageName.replace(".", "/"));
      List<String> classNames = new ArrayList<String>();

      for(File sourceFile : testDir.listFiles(new FilenameFilter(){
         @Override
         public boolean accept(File dir, String name){
            return (name.endsWith(".java"));
         }
      })){
         String fileName = sourceFile.getName();
         String className = fileName.substring(0, fileName.length() - ".java".length());
         classNames.add(className);
      }


      Collections.sort(classNames);

      JFrame frame = new JFrame("");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

      JList list = new JList(classNames.toArray(new String[0]));


      expectedOpenCLEditor.document.addDocumentListener(new DocumentListener(){
         void update(){

            final String lhs = expectedOpenCLEditor.getText();
            final String rhs = actualOpenCLEditor.getText();
            if(!expectedOpenCLEditor.changing && !actualOpenCLEditor.changing && lhs != null && rhs != null){
               SwingUtilities.invokeLater(new Runnable(){
                  @Override
                  public void run(){
                     int caret = expectedOpenCLEditor.textPane.getCaretPosition();
                     colorise(lhs, rhs);
                     expectedOpenCLEditor.textPane.setCaretPosition(Math.min(expectedOpenCLEditor.document.getLength(), caret));
                  }

               });
            }
         }

         @Override
         public void insertUpdate(DocumentEvent e){
            update();
         }

         @Override
         public void removeUpdate(DocumentEvent e){
            update();
         }

         @Override
         public void changedUpdate(DocumentEvent e){
            update();
         }
      });

      JPanel left = new JPanel();
      left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
      left.add(javaEditor.getComponent());
      left.add(new JScrollPane(list));
      panel.add(left);
      panel.add(expectedOpenCLEditor.getComponent());
      panel.add(actualOpenCLEditor.getComponent());

      JToolBar toolBar = new JToolBar("Still draggable");
      JButton saveButton = new JButton("Save");
      toolBar.add(saveButton);
      saveButton.addActionListener(new ActionListener(){
         @Override
         public void actionPerformed(ActionEvent e){
            if(source != null){
               save(source);
            }
         }
      });

      JButton acceptButton = new JButton("Accept");
      acceptButton.addActionListener(new ActionListener(){
         @Override
         public void actionPerformed(ActionEvent e){
            final String rhs = actualOpenCLEditor.getText();
            expectedOpenCLEditor.clear().equal(rhs);
         }
      });
      toolBar.add(acceptButton);
      frame.add(toolBar, BorderLayout.NORTH);
      frame.add(panel, BorderLayout.CENTER);

      list.addListSelectionListener(new ListSelectionListener(){
         @Override
         public void valueChanged(ListSelectionEvent e){
            if(!e.getValueIsAdjusting()){
               String className = (String) ((JList) (e.getSource())).getSelectedValue();
               try{
                  Class clazz = Class.forName(testPackageName + "." + className);
                  source = new Source(clazz, sourceDir);
                  javaEditor.clear().equal(source.getJava().toString());
                  if(source.hasOpenCL()){
                     String expectedOpenCL = source.getOpenCL().toString();
                     String actualOpenCL = null;
                     try{
                        // ClassModel.flush(); // should not need this
                        ClassModel classModel = ClassModel.getClassModel(clazz);
                        try{
                           Entrypoint entrypoint = classModel.getKernelEntrypoint();
                           actualOpenCL = OpenCLKernelWriter.writeToString(entrypoint);
                           colorise(expectedOpenCL, actualOpenCL);


                        }catch(AparapiException ex){
                           System.out.println("failed to created OpenCL");
                           actualOpenCLEditor.startChange().clear().equal(" NO actual OpenCL!\n").endChange();
                           ex.printStackTrace();
                        }
                     }catch(ClassParseException ex){
                        System.out.println("failed to array_load class ");
                        actualOpenCLEditor.startChange().clear().equal(" NO actual OpenCL!\n").endChange();
                        ex.printStackTrace();
                     }
                  }else{
                     expectedOpenCLEditor.startChange().clear().equal(" NO expected OpenCL!\n").endChange();
                     actualOpenCLEditor.startChange().clear().equal(" NO actual OpenCL!\n").endChange();
                  }

               }catch(ClassNotFoundException nfe){
                  javaEditor.startChange().clear().equal(" Can't array_load class!\n").endChange();
                  expectedOpenCLEditor.startChange().clear().equal(" Can't array_load class!\n").endChange();
                  actualOpenCLEditor.startChange().clear().equal(" Can't array_load class!\n").endChange();
               }
            }
         }
      });
      frame.setVisible(true);
      frame.pack();
   }
}
