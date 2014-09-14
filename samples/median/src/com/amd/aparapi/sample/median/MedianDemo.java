package com.amd.aparapi.sample.median;

import com.amd.aparapi.Kernel;

import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

/**
 * Demonstrate use of __private namespaces and @NoCL annotations.
 */
public class MedianDemo {
   public final static BufferedImage testImage;

   static {
      try {
         testImage = ImageIO.read(new File("C:\\dev\\aparapi_live\\aparapi\\samples\\convolution\\testcard.jpg"));
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private static final boolean TEST_JTP = false;

   public static void main(String[] ignored) {
      System.setProperty("com.amd.aparapi.enableShowGeneratedOpenCL", "true");
      int[] argbs = testImage.getRGB(0, 0, testImage.getWidth(), testImage.getHeight(), null, 0, testImage.getWidth());
      MedianKernel7x7 kernel = new MedianKernel7x7();
      kernel._imageTypeOrdinal = MedianKernel7x7.RGB;
      kernel._sourceWidth = testImage.getWidth();
      kernel._sourceHeight = testImage.getHeight();
      kernel._sourcePixels = argbs;
      kernel._destPixels = new int[argbs.length];
      if (TEST_JTP) {
         kernel.setExecutionMode(Kernel.EXECUTION_MODE.JTP);
      }
      kernel.processImages(new MedianSettings(7));
      BufferedImage out = new BufferedImage(testImage.getWidth(), testImage.getHeight(), BufferedImage.TYPE_INT_RGB);
      out.setRGB(0, 0, testImage.getWidth(), testImage.getHeight(), kernel._destPixels, 0, testImage.getWidth());
      ImageIcon icon1 = new ImageIcon(testImage);
      JLabel label1 = new JLabel(icon1);
      ImageIcon icon2 = new ImageIcon(out);
      JLabel label2 = new JLabel(icon2);
      JFrame frame = new JFrame("Test Median");
      frame.setLayout(new FlowLayout());
      frame.getContentPane().add(label1);
      frame.getContentPane().add(label2);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setVisible(true);

      int reps = 20;
      for (int rep = 0; rep < reps; ++rep) {
         long start = System.nanoTime();
         kernel.processImages(new MedianSettings(7));
         long elapsed = System.nanoTime() - start;
         System.out.println("elapsed = " + elapsed / 1000000f + "ms");
      }
   }
}
