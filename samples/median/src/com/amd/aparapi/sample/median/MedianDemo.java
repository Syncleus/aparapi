package com.amd.aparapi.sample.median;

import com.amd.aparapi.device.*;
import com.amd.aparapi.internal.kernel.*;

import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

/**
 * Demonstrate use of __private namespaces and @NoCL annotations.
 */
public class MedianDemo {
   public static BufferedImage testImage;

   static {
      try {
         File imageFile = new File("./samples/convolution/testcard.jpg").getCanonicalFile();
         if (imageFile.exists()) {
            testImage = ImageIO.read(imageFile);
         }
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private static final boolean TEST_JTP = true;

   public static void main(String[] ignored) {
      final int size = 5;
      System.setProperty("com.amd.aparapi.enableShowGeneratedOpenCL", "true");
      boolean verbose = true;
      if (verbose)
      {
          System.setProperty("com.amd.aparapi.enableVerboseJNI", "true");
          System.setProperty("com.amd.aparapi.dumpFlags", "true");
          System.setProperty("com.amd.aparapi.enableShowGeneratedOpenCL", "true");
          System.setProperty("com.amd.aparapi.enableVerboseJNIOpenCLResourceTracking", "true");
          System.setProperty("com.amd.aparapi.enableExecutionModeReporting", "true");
      }

      if (TEST_JTP) {
         LinkedHashSet<Device> devices = new LinkedHashSet<>(Collections.singleton(JavaDevice.THREAD_POOL));
         KernelManager.instance().setDefaultPreferredDevices(devices);
      }

      int[] argbs = testImage.getRGB(0, 0, testImage.getWidth(), testImage.getHeight(), null, 0, testImage.getWidth());
      MedianKernel7x7 kernel = new MedianKernel7x7();
      kernel._imageTypeOrdinal = MedianKernel7x7.RGB;
      kernel._sourceWidth = testImage.getWidth();
      kernel._sourceHeight = testImage.getHeight();
      kernel._sourcePixels = argbs;
      kernel._destPixels = new int[argbs.length];

      kernel.processImages(new MedianSettings(size));
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
         kernel.processImages(new MedianSettings(size));
         long elapsed = System.nanoTime() - start;
         System.out.println("elapsed = " + elapsed / 1000000f + "ms");
      }
   }
}
