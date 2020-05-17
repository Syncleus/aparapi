package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class MathMin extends Kernel{
   public void run(){
      double d1 = -1.0, d2 = 1.0;
      float f1 = -1.0f, f2 = 1.0f;
      int i1 = -1, i2 = 1;
      long n1 = -1, n2 = 1;
      boolean pass = true;
      if((min(d1, d2) != 1) || (min(f1, f2) != 1) || (min(i1, i2) != 1) || (min(n1, n2) != 1)){
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
 double d_3 = 1.0;
 float f_5 = -1.0f;
 float f_6 = 1.0f;
 int i_7 = -1;
 int i_8 = 1;
 long l_9 = -1L;
 long l_11 = 1L;
 int i_13 = 1;
 if (min(d_1, d_3)!=1.0 || min(f_5, f_6)!=1.0f || min(i_7, i_8)!=1 || (min(l_9, l_11) - 1L)!=0){
 i_13 = 0;
 }
 return;
 }
 }
 }OpenCL}**/