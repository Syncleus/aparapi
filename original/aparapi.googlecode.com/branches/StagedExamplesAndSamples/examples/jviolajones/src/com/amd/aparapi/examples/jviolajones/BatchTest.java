package com.amd.aparapi.examples.jviolajones;

/**
This project is based on the open source jviolajones project created by Simon
Houllier and is used with his permission. Simon's jviolajones project offers 
a pure Java implementation of the Viola-Jones algorithm.

http://en.wikipedia.org/wiki/Viola%E2%80%93Jones_object_detection_framework

The original Java source code for jviolajones can be found here
http://code.google.com/p/jviolajones/ and is subject to the
gnu lesser public license  http://www.gnu.org/licenses/lgpl.html

Many thanks to Simon for his excellent project and for permission to use it 
as the basis of an Aparapi example.
**/

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class BatchTest extends JFrame{

   private List<BufferedImage> images;

   BatchTest(List<BufferedImage> _images) {
      super("BatchTester");
      images = _images;

      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      JPanel panel = new JPanel();
      for (BufferedImage i : images) {
         JLabel label = new JLabel();
         label.setIcon(new ImageIcon(i));
         panel.add(label);
      }
      getContentPane().add(new JScrollPane(panel), BorderLayout.CENTER);
      pack();
      setVisible(true);

   }

   public static void main(String[] _args) throws IOException {
      if (_args.length == 1) {
         HaarCascade haarCascade = HaarCascade.create("haarcascade_frontalface_alt2.xml");

        Detector detector = new AparapiDetector4(haarCascade, 1f, 2f, 0.1f, false);
        // Detector detector = new MultiThreadedDetector(haarCascade, 1f, 2f, 0.1f, false);
         List<BufferedImage> rawImages = new ArrayList<BufferedImage>();
         for (File f : new File(_args[0]).listFiles()) {
       
            rawImages.add(ImageIO.read(f));
         }
         
         List<BufferedImage> images = new ArrayList<BufferedImage>();
         
         StopWatch timer = new StopWatch("All");
       
         for (BufferedImage image:rawImages){
            List<Rectangle> rects = detector.getFeatures(image);
            Graphics2D gc = image.createGraphics();
            for (Rectangle r: rects){
                gc.draw(r);
            }
            images.add(image);
         }
         timer.stop();
         
         new BatchTest(images);
      }

   }

}
