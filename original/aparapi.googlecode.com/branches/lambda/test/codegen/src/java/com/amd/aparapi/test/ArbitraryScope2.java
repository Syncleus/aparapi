package com.amd.aparapi.test;

public class ArbitraryScope2{
   int width = 1024;

   float scale = 1f;

   int maxIterations = 10;

   public void run(){
      int tid = 0;

      int i = tid % width;
      int j = tid / width;

      float x0 = ((i * scale) - ((scale / 2) * width)) / width;
      float y0 = ((j * scale) - ((scale / 2) * width)) / width;

      float x = x0;
      float y = y0;

      float x2 = x * x;
      float y2 = y * y;

      {
         int count = 0;
         count++;
      }

      float scaleSquare = scale * scale;

      int count = 0;

      for(int iter = 0; iter < maxIterations; ++iter){
         if(x2 + y2 <= scaleSquare){
            y = 2 * x * y + y0;
            x = x2 - y2 + x0;

            x2 = x * x;
            y2 = y * y;
            count++;
         }else{
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
 int i_1 = 0;
 int i_2 = i_1 % this->width;
 int i_3 = i_1 / this->width;
 float f_4 = (((float)i_2 * this->scale) - ((this->scale / 2.0f) * (float)this->width)) / (float)this->width;
 float f_5 = (((float)i_3 * this->scale) - ((this->scale / 2.0f) * (float)this->width)) / (float)this->width;
 float f_6 = f_4;
 float f_7 = f_5;
 float f_8 = f_6 * f_6;
 float f_9 = f_7 * f_7;
 int i_10 = 0;
 i_10++;
 float f_10 = this->scale * this->scale;
 int i_11 = 0;
 int i_12 = 0;
 for (; i_12<this->maxIterations; i_12++){
 if ((f_8 + f_9)<=f_10){
 f_7 = ((2.0f * f_6) * f_7) + f_5;
 f_6 = (f_8 - f_9) + f_4;
 f_8 = f_6 * f_6;
 f_9 = f_7 * f_7;
 i_11++;
 } else {
 i_11--;
 }
 }
 i_12 = (256 * i_11) / this->maxIterations;
 return;
 }
 }
 }OpenCL}**/
