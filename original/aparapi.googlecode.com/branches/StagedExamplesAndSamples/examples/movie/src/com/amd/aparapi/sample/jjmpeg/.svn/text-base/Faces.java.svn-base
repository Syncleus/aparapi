package com.amd.aparapi.sample.jjmpeg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import com.amd.aparapi.examples.jviolajones.Detector;
import com.amd.aparapi.examples.jviolajones.HaarCascade;
import com.amd.aparapi.examples.jviolajones.MultiThreadedDetector;
import com.amd.aparapi.examples.jviolajones.SingleThreadedDetector;
import com.amd.aparapi.examples.jviolajones.AparapiDetector4;
import com.amd.aparapi.examples.jviolajones.AparapiDetector5;
import com.amd.aparapi.examples.jviolajones.AparapiDetector6;

/**
 * Code based on Demo of JJVideoScanner class
 *
 * @author notzed
 */
public class Faces{

   public static void main(final String[] args) {

      HaarCascade haarCascade = HaarCascade.create("..\\jviolajones\\haarcascade_frontalface_alt2.xml");

      Detector detectorArg = new SingleThreadedDetector(haarCascade, 1f, 2f, 0.1f, false);
      if (args.length > 1 && args[1].equals("JTP")) {
         detectorArg = new MultiThreadedDetector(haarCascade, 1f, 2f, 0.1f, false);
      } else if (args.length > 1 && args[1].equals("GPU4")) {
         detectorArg = new AparapiDetector4(haarCascade, 1f, 2f, 0.1f, false);
      } else if (args.length > 1 && args[1].equals("GPU5")) {
         detectorArg = new AparapiDetector5(haarCascade, 1f, 2f, 0.1f, false);
      } else if (args.length > 1 && args[1].equals("GPU6")) {
         detectorArg = new AparapiDetector6(haarCascade, 1f, 2f, 0.1f, false);
      }

      final Detector detector = detectorArg;

      new JJMPEGPlayer("Faces", args[0], false){
         @Override protected void process(Graphics2D gc, BufferedImage image) {
            System.out.println(image);
            List<Rectangle> rects = detector.getFeatures(image);
            gc.setColor(Color.RED);
            for (Rectangle rect : rects) {
               gc.draw(rect);
            }
         }
      };

   }
}
