package com.amd.aparapi.test;

public class ForIfMandel{
   int width = 1024;

   float scale = 1f;

   int maxIterations = 10;

   public void run() {
      int tid = 0;

      int i = tid % width;
      int j = tid / width;

      float x0 = ((i * scale) - ((scale / 2) * width)) / width;
      float y0 = ((j * scale) - ((scale / 2) * width)) / width;

      float x = x0;
      float y = y0;

      float x2 = x * x;
      float y2 = y * y;

      float scaleSquare = scale * scale;

      int count = 0;

      for (int iter = 0; iter < maxIterations; ++iter) {
         if (x2 + y2 <= scaleSquare) {
            y = 2 * x * y + y0;
            x = x2 - y2 + x0;

            x2 = x * x;
            y2 = y * y;
            count++;
         } else {
            count--;
         }
      }
      @SuppressWarnings("unused") int value = (256 * count) / maxIterations;
   }
}
/**{OpenCL{
typedef struct This_s{
   int width;
   float scale;
   int maxIterations;  
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
   }

__kernel void run(
   int width, 
   float scale, 
   int maxIterations,
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->width = width;
   this->scale = scale;
   this->maxIterations = maxIterations;
   this->passid = passid;
   {
      int tid = 0;
      int i = tid % this->width;
      int j = tid / this->width;
      float x0 = (((float)i * this->scale) - ((this->scale / 2.0f) * (float)this->width)) / (float)this->width;
      float y0 = (((float)j * this->scale) - ((this->scale / 2.0f) * (float)this->width)) / (float)this->width;
      float x = x0;
      float y = y0;
      float x2 = x * x;
      float y2 = y * y;
      float scaleSquare = this->scale * this->scale;
      int count = 0;
      for (int iter = 0; iter<this->maxIterations; iter++){
         if ((x2 + y2)<=scaleSquare){
            y = ((2.0f * x) * y) + y0;
            x = (x2 - y2) + x0;
            x2 = x * x;
            y2 = y * y;
            count++;
         } else {
            count--;
         }
      }
      int value = (256 * count) / this->maxIterations;
      return;
   }
}
}OpenCL}**/
