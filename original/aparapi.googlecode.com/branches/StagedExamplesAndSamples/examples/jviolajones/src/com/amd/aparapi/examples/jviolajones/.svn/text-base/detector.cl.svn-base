#pragma OPENCL EXTENSION cl_khr_global_int32_base_atomics : enable
#pragma OPENCL EXTENSION cl_khr_global_int32_extended_atomics : enable
#pragma OPENCL EXTENSION cl_khr_local_int32_base_atomics : enable
#pragma OPENCL EXTENSION cl_khr_local_int32_extended_atomics : enable

__kernel void detect(
   int scaleIds, 
   __global short *scale_ValueWidthIJ, 
   int cascadeWidth, 
   int cascadeHeight, 
   __global int *stage_startEnd, 
   __global int *tree_startEnd, 
   __global int *weightedGrayImage, 
   int width, 
   __global int *weightedGrayImageSquared, 
   __global int *feature_r1r2r3LnRn, 
   __global int *rect_x1y1x2y2, 
   __global float *rect_w, 
   __global float *feature_LvRvThres, 
   __global float *stage_thresh, 
   int stage_ids, 
   __global int *found, 
   __global int *found_rects 
){
      int scaleId = get_global_id(0);
      if (scaleId<scaleIds){
         short i = scale_ValueWidthIJ[((scaleId * 4) + 2)];
         short j = scale_ValueWidthIJ[((scaleId * 4) + 3)];
         short scaledFeatureWidth = scale_ValueWidthIJ[((scaleId * 4) + 1)];
         short scale = scale_ValueWidthIJ[((scaleId * 4) + 0)];
         short w = (short)(scale * cascadeWidth);
         short h = (short)(scale * cascadeHeight);
         float inv_area = 1.0f / (float)(w * h);
         char pass = 1;
         for (short stageId = 0; pass!=0 && stageId<stage_ids; stageId = (short)(stageId + 1)){
            {
               float sum = 0.0f;
               for (int treeId = stage_startEnd[((stageId * 2) + 0)]; treeId<=stage_startEnd[((stageId * 2) + 1)]; treeId++){
                  {
                     int featureId = tree_startEnd[((treeId * 2) + 0)];
                     float thresh = 0.0f;
                     for (char done = 0; done==0; ){
                        int total_x = ((weightedGrayImage[((i + w) + ((j + h) * width))] + weightedGrayImage[(i + (j * width))]) - weightedGrayImage[(i + ((j + h) * width))]) - weightedGrayImage[((i + w) + (j * width))];
                        int total_x2 = ((weightedGrayImageSquared[((i + w) + ((j + h) * width))] + weightedGrayImageSquared[(i + (j * width))]) - weightedGrayImageSquared[(i + ((j + h) * width))]) - weightedGrayImageSquared[((i + w) + (j * width))];
                        float moy = (float)total_x * inv_area;
                        float vnorm = ((float)total_x2 * inv_area) - (moy * moy);
                        vnorm = (vnorm>1.0f)?sqrt(vnorm):1.0f;
                        int rect_sum = 0;
                        for (int r = 0; r<3; r++){
                           int rectId = feature_r1r2r3LnRn[((featureId * 5) + r)];
                           if (rectId!=-1){
                              int x1 = rect_x1y1x2y2[((rectId * 4) + 0)];
                              int y1 = rect_x1y1x2y2[((rectId * 4) + 1)];
                              int x2 = rect_x1y1x2y2[((rectId * 4) + 2)];
                              int y2 = rect_x1y1x2y2[((rectId * 4) + 3)];
                              float weight = rect_w[((rectId * 1) + 0)];
                              int rx1 = i + (scale * x1);
                              int rx2 = i + (scale * (x1 + y1));
                              int ry1 = j + (scale * x2);
                              int ry2 = j + (scale * (x2 + y2));
                              rect_sum = rect_sum + (int)((float)(((weightedGrayImage[(rx2 + (ry2 * width))] - weightedGrayImage[(rx1 + (ry2 * width))]) - weightedGrayImage[(rx2 + (ry1 * width))]) + weightedGrayImage[(rx1 + (ry1 * width))]) * weight);
                           }
                        }
                        float rect_sum2 = (float)rect_sum * inv_area;
                        
                        if (rect_sum2<(feature_LvRvThres[((featureId * 3) + 2)] * vnorm)){
                           int leftNodeId = feature_r1r2r3LnRn[((featureId * 5) + 3)];
                           if (leftNodeId==-1){
                              thresh = feature_LvRvThres[((featureId * 3) + 0)];
                              done = 1;
                           } else {
                              featureId = leftNodeId;
                           }
                        } else {
                           int rightNodeId = feature_r1r2r3LnRn[((featureId * 5) + 4)];
                           if (rightNodeId==-1){
                              thresh = feature_LvRvThres[((featureId * 3) + 1)];
                              done = 1;
                           } else {
                              featureId = rightNodeId;
                           }
                        }
                     }
                     sum = sum + thresh;
                  }
               }
               pass = (sum>stage_thresh[((stageId * 1) + 0)])?1:0;
            }
         }
         if (pass!=0){
            int value = atomic_add(found, 1);
            found_rects[(value * 3) + 0]  = i;
            found_rects[(value * 3) + 1]  = j;
            found_rects[(value * 3) + 2]  = scaledFeatureWidth;
         }
      }
      return;
}

