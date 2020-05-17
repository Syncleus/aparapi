package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class MathDegRad extends Kernel{
   public void run(){
      double d = -1.0;
      float f = -1.0f;
      boolean pass = true;
      if((toRadians(toDegrees(d)) != d) || (toRadians(toDegrees(f)) != f)){
         pass = false;
      }
   }
}

/**{OpenCL{
 typedef struct This_s{
 int passid;
 }This;

 int get_pass_id(This *this){
 return this->passid;
 }

 __kernel void run(
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->passid = passid;
 {
 double d_1 = -1.0;
 float f_3 = -1.0f;
 int i_4 = 1;
 if (toRadians(toDegrees(d_1))!=d_1 || toRadians(toDegrees(f_3))!=f_3){
 i_4 = 0;
 }
 return;
 }
 }
 }OpenCL}**/