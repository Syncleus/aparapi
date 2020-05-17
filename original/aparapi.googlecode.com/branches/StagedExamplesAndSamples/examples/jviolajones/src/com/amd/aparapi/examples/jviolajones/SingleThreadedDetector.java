package com.amd.aparapi.examples.jviolajones;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

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

public class SingleThreadedDetector extends Detector{

   public SingleThreadedDetector(HaarCascade haarCascade, float baseScale, float scaleInc, float increment, boolean doCannyPruning) {
      super(haarCascade, baseScale, scaleInc, increment, doCannyPruning);
      // TODO Auto-generated constructor stub
   }

   ScaleInfo scaleInfo = null;

   List<Rectangle> getFeaturesOld(final int width, final int height, float maxScale, final int[] weightedGrayImage,
         final int[] weightedGrayImageSquared, final int[] cannyIntegral) {
      System.out.println("old");

      final List<Rectangle> features = new ArrayList<Rectangle>();

      for (float scale = baseScale; scale < maxScale; scale *= scale_inc) {
         final int scaledFeatureStep = (int) (scale * haarCascade.cascadeWidth * increment);
         final int scaledFeatureWidth = (int) (scale * haarCascade.cascadeWidth);
         final float scale_f = scale;

         for (int i = 0; i < width - scaledFeatureWidth; i += scaledFeatureStep) {

            for (int j = 0; j < height - scaledFeatureWidth; j += scaledFeatureStep) {

               if (cannyIntegral != null) {
                  int edges_density = cannyIntegral[i + scaledFeatureWidth + (j + scaledFeatureWidth) * width]
                        + cannyIntegral[i + (j) * width] - cannyIntegral[i + (j + scaledFeatureWidth) * width]
                        - cannyIntegral[i + scaledFeatureWidth + (j) * width];
                  int d = edges_density / scaledFeatureWidth / scaledFeatureWidth;
                  if (d < 20 || d > 100)
                     continue;
               }

               Rectangle rectangle = haarCascade.getFeature(weightedGrayImage, weightedGrayImageSquared, width, height, i, j,
                     scale_f, scaledFeatureWidth);
               if (rectangle != null) {

                  features.add(rectangle);

               }
            }

         }

      }

      return (features);

   }

   List<Rectangle> getFeatures(final int width, final int height, float maxScale, final int[] weightedGrayImage,
         final int[] weightedGrayImageSquared, final int[] cannyIntegral) {
      final List<Rectangle> features = new ArrayList<Rectangle>();
      System.out.println("new");
      if (scaleInfo == null) {
         scaleInfo = new ScaleInfo(width, height, maxScale);
      }

      for (int scaleId = 0; scaleId < scaleInfo.scaleIds; scaleId++) {
         int i = scaleInfo.scale_ValueWidthIJ[scaleId * ScaleInfo.SCALE_INTS + 2];
         int j = scaleInfo.scale_ValueWidthIJ[scaleId * ScaleInfo.SCALE_INTS + 3];
         int scaledFeatureWidth = scaleInfo.scale_ValueWidthIJ[scaleId * ScaleInfo.SCALE_INTS + 1];
         int scale = scaleInfo.scale_ValueWidthIJ[ScaleInfo.SCALE_INTS * scaleId + 0];

         if (cannyIntegral != null) {
            int edges_density = cannyIntegral[i + scaledFeatureWidth + (j + scaledFeatureWidth) * width]
                  + cannyIntegral[i + (j) * width] - cannyIntegral[i + (j + scaledFeatureWidth) * width]
                  - cannyIntegral[i + scaledFeatureWidth + (j) * width];
            int d = edges_density / scaledFeatureWidth / scaledFeatureWidth;
            if (d < 20 || d > 100)
               continue;
         }

         Rectangle rectangle = haarCascade.getFeature(weightedGrayImage, weightedGrayImageSquared, width, height, i, j, scale,
               scaledFeatureWidth);
         if (rectangle != null) {

            features.add(rectangle);

         }

      }

      return (features);

   }

}
