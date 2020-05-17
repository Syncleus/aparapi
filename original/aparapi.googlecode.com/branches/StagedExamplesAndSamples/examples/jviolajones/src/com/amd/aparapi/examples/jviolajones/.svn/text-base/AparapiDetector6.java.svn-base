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

public class AparapiDetector6 extends Detector{

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

      private int[] scaleIdsEvenOdd;

      private int[] counts_$local$;

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
         int odd = ((stageId + 1) & 1); // 0 for odd 1 for even
         int lid = getLocalId(0);
         int lsz = getLocalSize(0);
         counts_$local$[lid * 2] = 0;
         counts_$local$[lid * 2 + 1] = 0;
         localBarrier();

         if (gid < scaleIdCountEvenOdd[even]) { // so that gid can be rounded up to next multiple of groupsize.
            int scaleId = scaleIdsEvenOdd[scaleIds * even + gid];
            short scale = scale_ValueWidthIJ[scaleId * SCALE_INTS + 0];
            short i = scale_ValueWidthIJ[scaleId * SCALE_INTS + 2];
            short j = scale_ValueWidthIJ[scaleId * SCALE_INTS + 3];

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
               counts_$local$[lid] = 1;
               counts_$local$[lid+lsz] = scaleId;
            }

         }

         localBarrier();
         if (lid == 0) {
            int count = 0;
            for (int wi = 0; wi < lsz; wi++) {
               count += counts_$local$[wi];
            }
            int base = atomicAdd(scaleIdCountEvenOdd, odd, count);
            count = 0;
            for (int wi = 0; wi < lsz; wi ++) {
               if (counts_$local$[wi] == 1) {
                  scaleIdsEvenOdd[scaleIds * odd + base + count] = counts_$local$[wi+lsz];
                  count++;
               }

            }
         }
      }
   }

   DetectorKernel kernel;

   private Device device;

   public AparapiDetector6(HaarCascade haarCascade, float baseScale, float scaleInc, float increment, boolean doCannyPruning) {
      super(haarCascade, baseScale, scaleInc, increment, doCannyPruning);
      device = Device.best();
      kernel = new DetectorKernel(haarCascade);
      kernel.setExplicit(true);
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
         kernel.scaleIdsEvenOdd = new int[kernel.scaleIds * 2];
         kernel.counts_$local$ = new int[device.getMaxWorkItemSize()[0] * 2];
         kernel.width = width;
         kernel.scale_ValueWidthIJ = scaleInfo.scale_ValueWidthIJ;
      }
      kernel.weightedGrayImage = weightedGrayImage;
      kernel.weightedGrayImageSquared = weightedGrayImageSquared;
      int count = kernel.scaleIds;

      System.arraycopy(defaultIds, 0, kernel.scaleIdsEvenOdd, 0, kernel.scaleIds);
      kernel.put(kernel.scaleIdsEvenOdd);
      int even = 0;
      int odd = 0;
      for (kernel.stageId = 0; count > 0 && kernel.stageId < haarCascade.stage_ids; kernel.stageId++) {
         // System.out.println("#1 pass count for stage " + kernel.stageId + " is " + count);
         even = (kernel.stageId & 1); // 1 for odd 0 for even
         odd = ((kernel.stageId + 1) & 1); // 0 for odd 1 for even
         kernel.scaleIdCountEvenOdd[even] = count;
         kernel.scaleIdCountEvenOdd[odd] = 0;
         kernel.put(kernel.scaleIdCountEvenOdd);
         range = device.createRange(count + ((device.getMaxWorkItemSize()[0]) - (count % device.getMaxWorkItemSize()[0])));
         kernel.execute(range);
         kernel.get(kernel.scaleIdCountEvenOdd);
         count = kernel.scaleIdCountEvenOdd[odd];

         // List<ProfileInfo> profileInfoList = kernel.getProfileInfo();
         //  for (ProfileInfo profileInfo : profileInfoList) {
         //   System.out.println(profileInfo);
         // }
      }
      if (count > 0) {
         kernel.get(kernel.scaleIdsEvenOdd);
         for (int i = 0; i < count; i++) {
            int scaleId = kernel.scaleIdsEvenOdd[odd * kernel.scaleIds + i];
            int x = kernel.scale_ValueWidthIJ[scaleId * kernel.SCALE_INTS + 2];
            int y = kernel.scale_ValueWidthIJ[scaleId * kernel.SCALE_INTS + 3];
            int scaledFeatureWidth = kernel.scale_ValueWidthIJ[scaleId * kernel.SCALE_INTS + 1];
            features.add(new Rectangle(x, y, scaledFeatureWidth, scaledFeatureWidth));
         }

      }
      return (features);
   }

}
