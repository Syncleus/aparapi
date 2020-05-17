package com.amd.aparapi.examples.jviolajones;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

public class MultiThreadedDetector extends Detector{

   public MultiThreadedDetector(HaarCascade haarCascade, float baseScale, float scaleInc, float increment, boolean doCannyPruning) {
      super(haarCascade, baseScale, scaleInc, increment, doCannyPruning);
      // TODO Auto-generated constructor stub
   }

   @Override List<Rectangle> getFeatures(final int width, final int height, float maxScale, final int[] weightedGrayImage,
         final int[] weightedGrayImageSquared, final int[] cannyIntegral) {
      final List<Rectangle> features = new ArrayList<Rectangle>();
      ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
      for (float scale = baseScale; scale < maxScale; scale *= scale_inc) {
         final int scaledFeatureStep = (int) (scale * haarCascade.cascadeWidth * increment);
         final int scaledFeatureWidth = (int) (scale * haarCascade.cascadeWidth);
         final float scale_f = scale;

         for (int i = 0; i < width - scaledFeatureWidth; i += scaledFeatureStep) {
            final int i_f = i;
            threadPool.execute(new Runnable(){
               public void run() {
                  for (int j = 0; j < height - scaledFeatureWidth; j += scaledFeatureStep) {

                     if (cannyIntegral != null) {
                        int edges_density = cannyIntegral[i_f + scaledFeatureWidth + (j + scaledFeatureWidth) * width]
                              + cannyIntegral[i_f + (j) * width] - cannyIntegral[i_f + (j + scaledFeatureWidth) * width]
                              - cannyIntegral[i_f + scaledFeatureWidth + (j) * width];
                        int d = edges_density / scaledFeatureWidth / scaledFeatureWidth;
                        if (d < 20 || d > 100)
                           continue;
                     }

                     Rectangle rectangle = haarCascade.getFeature(weightedGrayImage, weightedGrayImageSquared, width, height, i_f,
                           j, scale_f, scaledFeatureWidth);
                     if (rectangle != null) {
                        synchronized (features) {
                           features.add(rectangle);
                        }
                     }
                  }
               }
            });
         }
      }
      threadPool.shutdown(); // we won't add anymore
      try {
         threadPool.awaitTermination(60, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return (features);

   }

}
