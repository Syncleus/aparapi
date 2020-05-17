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

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.amd.aparapi.Device;
import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

public class AparapiDetector extends Detector{

   class DetectorKernel extends Kernel{

      private int width;

      private int scaledFeatureWidth;

      private int scaledFeatureStep;

      private float scale;

      private int[] weightedGrayImage;

      private int[] weightedGrayImageSquared;

      static final private int MAX_FOUND = 200;

      static final private int RECT_FOUND_INTS = 3;

      private int[] found_rects = new int[MAX_FOUND * RECT_FOUND_INTS];

      private int[] found = new int[1];

      final private int stage_ids;

      final private int[] tree_startEnd;

      final private int[] stage_startEnd;

      final private float[] stage_thresh;

      static final private int FEATURE_FLOATS = HaarCascade.FEATURE_FLOATS;

      static final private int FEATURE_INTS = HaarCascade.FEATURE_INTS;

      static final private int RECT_FLOATS = HaarCascade.RECT_FLOATS;

      static final private int RECT_INTS = HaarCascade.RECT_INTS;

      static final private int STAGE_FLOATS = HaarCascade.STAGE_FLOATS;

      static final private int STAGE_INTS = HaarCascade.STAGE_INTS;

      static final private int TREE_INTS = HaarCascade.TREE_INTS;

      final private int[] feature_r1r2r3LnRn;

      final private int[] rect_x1y1x2y2;

      final private float[] rect_w;

      final private float[] feature_LvRvThres;

      final int cascadeWidth;

      final int cascadeHeight;

      public DetectorKernel(HaarCascade _haarCascade) {
         stage_ids = _haarCascade.stage_ids;
         stage_startEnd = _haarCascade.stage_startEnd;
         stage_thresh = _haarCascade.stage_thresh;
         tree_startEnd = _haarCascade.tree_startEnd;
         feature_r1r2r3LnRn = _haarCascade.feature_r1r2r3LnRn;
         feature_LvRvThres = _haarCascade.feature_LvRvThres;
         rect_w = _haarCascade.rect_w;
         rect_x1y1x2y2 = _haarCascade.rect_x1y1x2y2;
         cascadeWidth = _haarCascade.cascadeWidth;
         cascadeHeight = _haarCascade.cascadeHeight;
      }

      @Override public void run() {
         int i = getGlobalId(0) * scaledFeatureStep;
         int j = getGlobalId(1) * scaledFeatureStep;

         boolean pass = true;
         for (int stageId = 0; pass == true && stageId < stage_ids; stageId++) {
            float sum = 0;
            for (int treeId = stage_startEnd[stageId * STAGE_INTS + 0]; treeId <= stage_startEnd[stageId * STAGE_INTS + 1]; treeId++) {
               int featureId = tree_startEnd[treeId * TREE_INTS + 0];
               float thresh = 0f;
               boolean done = false;
               while (!done) {

                  int w = (int) (scale * cascadeWidth);
                  int h = (int) (scale * cascadeHeight);
                  float inv_area = 1f / (w * h);
                  int total_x = weightedGrayImage[i + w + (j + h) * width] + weightedGrayImage[i + (j) * width]
                        - weightedGrayImage[i + (j + h) * width] - weightedGrayImage[i + w + (j) * width];
                  int total_x2 = weightedGrayImageSquared[i + w + (j + h) * width] + weightedGrayImageSquared[i + (j) * width]
                        - weightedGrayImageSquared[i + (j + h) * width] - weightedGrayImageSquared[i + w + (j) * width];
                  float moy = total_x * inv_area;
                  float vnorm = total_x2 * inv_area - moy * moy;
                  vnorm = (vnorm > 1) ? sqrt(vnorm) : 1;
                  int rect_sum = 0;
                  for (int r = 0; r < 3; r++) {
                     int rectId = feature_r1r2r3LnRn[featureId * FEATURE_INTS + r];
                     if (rectId != -1) {
                        int x1 = rect_x1y1x2y2[rectId * RECT_INTS + 0];
                        int y1 = rect_x1y1x2y2[rectId * RECT_INTS + 1];
                        int x2 = rect_x1y1x2y2[rectId * RECT_INTS + 2];
                        int y2 = rect_x1y1x2y2[rectId * RECT_INTS + 3];
                        float weight = rect_w[rectId * RECT_FLOATS + 0];
                        int rx1 = i + (int) (scale * x1);
                        int rx2 = i + (int) (scale * (x1 + y1));
                        int ry1 = j + (int) (scale * x2);
                        int ry2 = j + (int) (scale * (x2 + y2));
                        rect_sum += (int) ((weightedGrayImage[rx2 + (ry2) * width] - weightedGrayImage[rx1 + (ry2) * width]
                              - weightedGrayImage[rx2 + (ry1) * width] + weightedGrayImage[rx1 + (ry1) * width]) * weight);
                     }
                  }
                  float rect_sum2 = rect_sum * inv_area;

                  if (rect_sum2 < feature_LvRvThres[featureId * FEATURE_FLOATS + 2] * vnorm) {
                     int leftNodeId = feature_r1r2r3LnRn[featureId * FEATURE_INTS + 3];
                     if (leftNodeId == -1) {
                        thresh = feature_LvRvThres[featureId * FEATURE_FLOATS + 0];
                        done = true;
                     } else {
                        featureId = leftNodeId;
                     }
                  } else {
                     int rightNodeId = feature_r1r2r3LnRn[featureId * FEATURE_INTS + 4];
                     if (rightNodeId == -1) {
                        thresh = feature_LvRvThres[featureId * FEATURE_FLOATS + 1];
                        done = true;
                     } else {
                        featureId = rightNodeId;
                     }
                  }
               }

               sum += thresh;
            }

            if (sum <= stage_thresh[stageId * STAGE_FLOATS + 0]) {
               pass = false;
            }
         }
         if (pass) {
            int value = atomicAdd(found, 0, 1);

            found_rects[value * RECT_FOUND_INTS + 0] = i;
            found_rects[value * RECT_FOUND_INTS + 1] = j;
            found_rects[value * RECT_FOUND_INTS + 2] = scaledFeatureWidth;

         }

      }

      public void set(int _width, float _scale, int _scaledFeatureWidth, int _scaledFeatureStep, int[] _weightedGrayImage,
            int[] _weightedGreyImageSquared) {
         width = _width;
         scale = _scale;
         scaledFeatureWidth = _scaledFeatureWidth;
         scaledFeatureStep = _scaledFeatureStep;
         weightedGrayImage = _weightedGrayImage;
         weightedGrayImageSquared = _weightedGreyImageSquared;

      }

   }

   DetectorKernel kernel;

   private Device device;

   public AparapiDetector(HaarCascade haarCascade, float baseScale, float scaleInc, float increment, boolean doCannyPruning) {
      super(haarCascade, baseScale, scaleInc, increment, doCannyPruning);
      device = Device.best();
      kernel = new DetectorKernel(haarCascade);
      kernel.setExplicit(true);
      // kernel.setExecutionMode(Kernel.EXECUTION_MODE.JTP);
   }

   @Override List<Rectangle> getFeatures(final int width, final int height, float maxScale, final int[] weightedGrayImage,
         final int[] weightedGrayImageSquared, final int[] cannyIntegral) {

      final List<Rectangle> features = new ArrayList<Rectangle>();
      for (float scale = baseScale; scale < maxScale; scale *= scale_inc) {
         final int scaledFeatureStep = (int) (scale * haarCascade.cascadeWidth * increment);
         final int scaledFeatureWidth = (int) (scale * haarCascade.cascadeWidth);

         Range range = device.createRange2D((width - scaledFeatureWidth) / scaledFeatureStep, (height - scaledFeatureWidth)
               / scaledFeatureStep);

         // Range range = Range.create2D((width - scaledFeatureWidth) / scaledFeatureStep, (height - scaledFeatureWidth)
         // / scaledFeatureStep);
         //     System.out.println(range);
         kernel.found[0] = 0;
         kernel.put(kernel.found);
         kernel.set(width, scale, scaledFeatureWidth, scaledFeatureStep, weightedGrayImage, weightedGrayImageSquared);
         kernel.execute(range);
         // System.out.println(kernel.getExecutionMode() + " " + kernel.getConversionTime());
         //   System.out.println(kernel.getExecutionMode() + " " + kernel.getExecutionTime());
         kernel.get(kernel.found);
         kernel.get(kernel.found_rects);
         for (int i = 0; i < kernel.found[0]; i++) {
            features.add(new Rectangle(kernel.found_rects[i * DetectorKernel.RECT_FOUND_INTS + 0], kernel.found_rects[i
                  * DetectorKernel.RECT_FOUND_INTS + 1], kernel.found_rects[i * DetectorKernel.RECT_FOUND_INTS + 2],
                  kernel.found_rects[i * DetectorKernel.RECT_FOUND_INTS + 2]));
         }
      }

      return (features);
   }

}
