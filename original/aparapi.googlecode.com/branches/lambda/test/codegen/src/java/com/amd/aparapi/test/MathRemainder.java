package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class MathRemainder extends Kernel{
   public void run(){
      double d1 = 7.0, d2 = 2.0;
      float f1 = 7.0f, f2 = 2.0f;
      boolean pass = true;
      if((IEEEremainder(d1, d2) != 1) || (IEEEremainder(f1, f2) != 1)){
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
 double d_1 = 7.0;
 double d_3 = 2.0;
 float f_5 = 7.0f;
 float f_6 = 2.0f;
 int i_7 = 1;
 if (IEEEremainder(d_1, d_3)!=1.0 || IEEEremainder(f_5, f_6)!=1.0f){
 i_7 = 0;
 }
 return;
 }
 }
 }OpenCL}**/