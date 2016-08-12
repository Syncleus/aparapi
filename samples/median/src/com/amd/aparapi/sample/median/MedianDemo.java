package com.amd.aparapi.sample.median;

import com.amd.aparapi.internal.kernel.*;

import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

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

   public static void main(String[] ignored) {
      final int size = 5;
      System.setProperty("com.amd.aparapi.dumpProfilesOnExit", "true");
      boolean verbose = false;
      if (verbose)
      {
          System.setProperty("com.amd.aparapi.enableVerboseJNI", "true");
          System.setProperty("com.amd.aparapi.dumpFlags", "true");
          System.setProperty("com.amd.aparapi.enableShowGeneratedOpenCL", "true");
          System.setProperty("com.amd.aparapi.enableVerboseJNIOpenCLResourceTracking", "true");
          System.setProperty("com.amd.aparapi.enableExecutionModeReporting", "true");
      }

//      KernelManager.setKernelManager(new KernelManager(){
//         @Override
//         protected Comparator<OpenCLDevice> getDefaultGPUComparator() {
//            return new Comparator<OpenCLDevice>() {
//               @Override
//               public int compare(OpenCLDevice o1, OpenCLDevice o2) {
//                  return o2.getMaxComputeUnits() - o1.getMaxComputeUnits();
//               }
//            };
//         }
//      });

      System.out.println(KernelManager.instance().bestDevice());

      int[] argbs = testImage.getRGB(0, 0, testImage.getWidth(), testImage.getHeight(), null, 0, testImage.getWidth());
      MedianKernel7x7 kernel = createMedianKernel(argbs);

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

      StringBuilder builder = new StringBuilder();
      KernelManager.instance().reportDeviceUsage(builder, true);
      System.out.println(builder);

      int reps = 50;
      final boolean newKernel = false;
      for (int rep = 0; rep < reps; ++rep) {
         if (newKernel) {
            kernel.dispose();
            kernel = createMedianKernel(argbs);
         }
         long start = System.nanoTime();
         kernel.processImages(new MedianSettings(size));
         long elapsed = System.nanoTime() - start;
         System.out.println("elapsed = " + elapsed / 1000000f + "ms");
      }

      builder = new StringBuilder();
      KernelManager.instance().reportDeviceUsage(builder, true);
      System.out.println(builder);
   }

   private static MedianKernel7x7 createMedianKernel(int[] argbs) {
      MedianKernel7x7 kernel = new MedianKernel7x7();
      kernel._imageTypeOrdinal = MedianKernel7x7.RGB;
      kernel._sourceWidth = testImage.getWidth();
      kernel._sourceHeight = testImage.getHeight();
      kernel._sourcePixels = argbs;
      kernel._destPixels = new int[argbs.length];
      return kernel;
   }
}
