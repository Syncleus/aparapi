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
import com.amd.aparapi.OpenCL;
import com.amd.aparapi.OpenCLDevice;
import com.amd.aparapi.Range;

public class AparapiDetector3 extends Detector{
   @OpenCL.Resource("detector/detector.cl") public static interface DetectorCL extends OpenCL<DetectorCL>{
      DetectorCL detect(@Arg("scaleIds") int scaleIds, //
            @GlobalReadOnly("scale_ValueWidthIJ") short scale_ValueWidthIJ[], //
            @Arg("cascadeWidth") int cascadeWidth, //
            @Arg("cascadeHeight") int cascadeHeight, //
            @GlobalReadOnly("stage_startEnd") int stage_startEnd[], //
            @GlobalReadOnly("tree_startEnd") int tree_startEnd[], //
            @GlobalReadOnly("weightedGrayImage") int weightedGrayImage[], //
            @Arg("width") int width,//
            @GlobalReadOnly("weightedGrayImageSquared") int weightedGrayImageSquared[], //
            @GlobalReadOnly("feature_r1r2r3LnRn") int feature_r1r2r3LnRn[], //
            @GlobalReadOnly("rect_x1y1x2y2") int rect_x1y1x2y2[], //
            @GlobalReadOnly("rect_w") float rect_w[],//
            @GlobalReadOnly("feature_LvRvThres") float feature_LvRvThres[], //
            @GlobalReadOnly("stage_thresh") float stage_thresh[], //
            @Arg("stage_ids") int stage_ids, //
            @GlobalReadOnly("found") int found[], //
            @GlobalReadOnly("found_rects") int found_rects[] //
      );
   }

   DetectorCL kernel;

   private Device device;

   public AparapiDetector3(HaarCascade haarCascade, float baseScale, float scaleInc, float increment, boolean doCannyPruning) {
      super(haarCascade, baseScale, scaleInc, increment, doCannyPruning);
      device = Device.best();
      kernel = ((OpenCLDevice) device).bind(DetectorCL.class);

   }

   ScaleInfo scaleInfo = null;

   int found[] = new int[1000];

   Range range = null;

   @Override List<Rectangle> getFeatures(final int width, final int height, float maxScale, final int[] weightedGrayImage,
         final int[] weightedGrayImageSquared, final int[] cannyIntegral) {

      final List<Rectangle> features = new ArrayList<Rectangle>();
      if (scaleInfo == null) {
         scaleInfo = new ScaleInfo(width, height, maxScale);

         range = device.createRange(scaleInfo.scaleIds
               + ((device.getMaxWorkItemSize()[0]) - (scaleInfo.scaleIds % device.getMaxWorkItemSize()[0])));
      }

      //      kernel.found[0] = 0;
      //
      //      kernel.weightedGrayImage = weightedGrayImage;
      //      kernel.weightedGrayImageSquared = weightedGrayImageSquared;
      //      kernel.put(kernel.found);
      //      // kernel.put(kernel.weightedGrayImage);
      //      //  kernel.put(kernel.weightedGrayImageSquared);
      //      kernel.execute(range);
      //      kernel.get(kernel.found);
      //      kernel.get(kernel.found_rects);
      //      //kernel.get(kernel.weightedGrayImage);
      //      // kernel.get(kernel.weightedGrayImageSquared);
      //      for (int i = 0; i < kernel.found[0]; i++) {
      //         features.add(new Rectangle(kernel.found_rects[i * DetectorKernel.RECT_FOUND_INTS + 0], kernel.found_rects[i
      //               * DetectorKernel.RECT_FOUND_INTS + 1], kernel.found_rects[i * DetectorKernel.RECT_FOUND_INTS + 2],
      //               kernel.found_rects[i * DetectorKernel.RECT_FOUND_INTS + 2]));
      //      }
      //      List<ProfileInfo> profileInfoList = kernel.getProfileInfo();
      //      for (ProfileInfo profileInfo : profileInfoList) {
      //         System.out.println(profileInfo);
      //      }

      return (features);
   }

}
