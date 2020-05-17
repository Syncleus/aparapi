package com.amd.aparapi.sample.jjmpeg;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import au.notzed.jjmpeg.io.JJMediaReader;
import au.notzed.jjmpeg.io.JJMediaReader.JJReaderVideo;

/**
 * Code based on Demo of JJVideoScanner class
 *
 * @author notzed
 */
public class JJMPEGPlayer{

   public JJMPEGPlayer(final String _title, final String _fileName, final boolean _useDestBuffer) {
      SwingUtilities.invokeLater(new Runnable(){
         final Object doorBell = new Object();

         public void run() {

            JFrame frame = new JFrame(_title);
            frame.getContentPane().setLayout(new BorderLayout());
            final JLabel label = new JLabel(){
               @Override public void paint(Graphics GC) {
                  super.paint(GC);
                  synchronized (doorBell) {
                     doorBell.notify();
                  }
               }
            };
            frame.getContentPane().add(label, BorderLayout.CENTER);

            try {
               final JJMediaReader reader = new JJMediaReader(_fileName);
               final JJReaderVideo vs = reader.openFirstVideoStream();
               final BufferedImage in = vs.createImage();
               final BufferedImage out = vs.createImage();
               if (_useDestBuffer) {
                  label.setIcon(new ImageIcon(out));
               } else {
                  label.setIcon(new ImageIcon(in));
               }
               new Thread(new Runnable(){
                  public void run() {
                     int frames = 0;
                     long start = System.currentTimeMillis();
                     try {
                        while (true) {
                           JJMediaReader.JJReaderStream rs = reader.readFrame();
                           if (rs != null) {
                              vs.getOutputFrame(in);
                              Graphics2D gc = in.createGraphics();
                              frames++;
                              long fps = (frames * 1000) / (System.currentTimeMillis() - start);
                              gc.drawString("" + fps, 20, 20);
                              if (_useDestBuffer) {
                                 process(gc, in, out);
                              } else {
                                 process(gc, in);
                              }

                              label.repaint();
                              synchronized (doorBell) {
                                 try {
                                    doorBell.wait();
                                 } catch (InterruptedException ie) {
                                    ie.getStackTrace();
                                 }
                              }
                           } else {
                              reader.dispose();
                              System.exit(1);
                           }
                           Thread.sleep(1);
                        }
                     } catch (Exception ex) {
                        ex.printStackTrace();
                        Logger.getLogger(JJMPEGPlayer.class.getName()).log(Level.SEVERE, null, ex);
                     }
                  }
               }).start();
               frame.pack();
               frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               frame.setVisible(true);
            } catch (Exception ex) {
               Logger.getLogger(JJMPEGPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }

         }
      });
   }

   protected void process(Graphics2D gc, BufferedImage image) {

   }

   protected void process(Graphics2D gc, BufferedImage in, BufferedImage _out) {

   }

}
