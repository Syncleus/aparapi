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

public class AparapiDetector4 extends Detector{

   class DetectorKernel extends Kernel{

      private int width;

      private int[] weightedGrayImage;

      private int[] weightedGrayImageSquared;

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

      static final private int SCALE_INTS = ScaleInfo.SCALE_INTS;

      final private int[] feature_r1r2r3LnRn;

      final private int[] rect_x1y1x2y2;

      final private float[] rect_w;

      final private float[] feature_LvRvThres;

      private int scaleIds;

      final int cascadeWidth;

      final int cascadeHeight;

      private short[] scale_ValueWidthIJ;

      private int stageId = 0;

      private int[] scaleIdCountEvenOdd = new int[2];

      private int[] scaleIdsOdd;

      private int[] scaleIdsEven;

      public DetectorKernel(HaarCascade _haarCascade) {
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
         int gid = getGlobalId(0);
         int even = (stageId & 1); // 1 for odd 0 for even

         if (gid < scaleIdCountEvenOdd[even]) { // so that gid can be rounded up to next multiple of groupsize.
            int scaleId = (even == 0 ? scaleIdsEven[gid] : scaleIdsOdd[gid]);
            short scale = (short) scale_ValueWidthIJ[scaleId * SCALE_INTS + 0];
            short i = (short) scale_ValueWidthIJ[scaleId * SCALE_INTS + 2];
            short j = (short) scale_ValueWidthIJ[scaleId * SCALE_INTS + 3];

            short w = (short) (scale * cascadeWidth);
            short h = (short) (scale * cascadeHeight);
            float inv_area = 1f / (w * h);
            float sum = 0;
            for (int treeId = stage_startEnd[stageId * STAGE_INTS + 0]; treeId <= stage_startEnd[stageId * STAGE_INTS + 1]; treeId++) {
               int featureId = tree_startEnd[treeId * TREE_INTS + 0];
               float thresh = 0f;

               for (boolean done = false; !done;) {

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

            if (sum > stage_thresh[stageId * STAGE_FLOATS + 0]) {
               if (even == 0) {
                  scaleIdsOdd[atomicAdd(scaleIdCountEvenOdd, 1, 1)] = scaleId;
               } else {
                  scaleIdsEven[atomicAdd(scaleIdCountEvenOdd, 0, 1)] = scaleId;
               }
            }

         }

      }

   }

   DetectorKernel kernel;

   private Device device;

   public AparapiDetector4(HaarCascade haarCascade, float baseScale, float scaleInc, float increment, boolean doCannyPruning) {
      super(haarCascade, baseScale, scaleInc, increment, doCannyPruning);
      device = Device.best();
      kernel = new DetectorKernel(haarCascade);
      kernel.setExplicit(true);
      // kernel.setExecutionMode(Kernel.EXECUTION_MODE.JTP);
   }

   ScaleInfo scaleInfo = null;

   Range range = null;

   int[] defaultIds = null;

   @Override List<Rectangle> getFeatures(final int width, final int height, float maxScale, final int[] weightedGrayImage,
         final int[] weightedGrayImageSquared, final int[] cannyIntegral) {

      final List<Rectangle> features = new ArrayList<Rectangle>();
      if (scaleInfo == null) {
         scaleInfo = new ScaleInfo(width, height, maxScale);
         kernel.scaleIds = scaleInfo.scaleIds;
         defaultIds = new int[kernel.scaleIds];
         for (int i = 0; i < kernel.scaleIds; i++) {
            defaultIds[i] = i;
         }
         kernel.scaleIdsEven = new int[kernel.scaleIds];
         kernel.scaleIdsOdd = new int[kernel.scaleIds];

         // System.out.println(range);
         kernel.width = width;

         // System.out.println("scaledIds = " + kernel.scaleIds);
         kernel.scale_ValueWidthIJ = scaleInfo.scale_ValueWidthIJ;
      }
      kernel.weightedGrayImage = weightedGrayImage;
      kernel.weightedGrayImageSquared = weightedGrayImageSquared;
      int count = kernel.scaleIds;

      System.arraycopy(defaultIds, 0, kernel.scaleIdsEven, 0, kernel.scaleIds);
      kernel.put(kernel.scaleIdsEven);
      boolean even = true;
      // kernel.put(kernel.weightedGrayImage);
      // kernel.put(kernel.weightedGrayImageSquared);
      for (kernel.stageId = 0; count > 0 && kernel.stageId < haarCascade.stage_ids; kernel.stageId++) {
         // System.out.println("#1 pass count for stage " + kernel.stageId + " is " + count);
         //even = (kernel.stageId & 1) == 0;
         if (even) {
            kernel.scaleIdCountEvenOdd[0] = count;
            kernel.scaleIdCountEvenOdd[1] = 0;
         } else {
            kernel.scaleIdCountEvenOdd[1] = count;
            kernel.scaleIdCountEvenOdd[0] = 0;

         }
         kernel.put(kernel.scaleIdCountEvenOdd);

         // kernel.put(kernel.weightedGrayImage);
         //   kernel.put(kernel.weightedGrayImageSquared);
         // long start = System.nanoTime();
         range = device.createRange(count + ((device.getMaxWorkItemSize()[0]) - (count % device.getMaxWorkItemSize()[0])));

         // range = device.createRange(count);
         //  long end = System.nanoTime();
         //   System.out.println("scale "+((end-start)/1000));
         kernel.execute(range);
         kernel.get(kernel.scaleIdCountEvenOdd);
         if (even) {
            count = kernel.scaleIdCountEvenOdd[1];
         } else {
            count = kernel.scaleIdCountEvenOdd[0];
         }
         //  List<ProfileInfo> profileInfoList = kernel.getProfileInfo();
         // for (ProfileInfo profileInfo : profileInfoList) {
         //    System.out.println(profileInfo);
         //  }
         even = !even;
      }
      if (count > 0) {
         int passes[] = null;
         if (!even) {
            kernel.get(kernel.scaleIdsOdd);
            kernel.get(kernel.scaleIdCountEvenOdd);
            passes = kernel.scaleIdsOdd;
            count = kernel.scaleIdCountEvenOdd[1];
         } else {
            kernel.get(kernel.scaleIdsEven);
            kernel.get(kernel.scaleIdCountEvenOdd);
            passes = kernel.scaleIdsEven;
            count = kernel.scaleIdCountEvenOdd[0];
         }

         for (int i = 0; i < count; i++) {
            int scaleId = passes[i];
            int x = kernel.scale_ValueWidthIJ[scaleId * kernel.SCALE_INTS + 2];
            int y = kernel.scale_ValueWidthIJ[scaleId * kernel.SCALE_INTS + 3];
            int scaledFeatureWidth = kernel.scale_ValueWidthIJ[scaleId * kernel.SCALE_INTS + 1];
            features.add(new Rectangle(x, y, scaledFeatureWidth, scaledFeatureWidth));
         }

      }
      return (features);
   }

}
