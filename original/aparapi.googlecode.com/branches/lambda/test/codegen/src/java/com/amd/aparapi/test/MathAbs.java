package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class MathAbs extends Kernel{
   public void run(){
      double d = -1.0;
      float f = -1.0f;
      int i = -1;
      long n = -1;
      @SuppressWarnings("unused") boolean pass = true;
      if((abs(d) != 1) || (abs(f) != 1) || (abs(i) != 1) || (abs(n) != 1)){
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
 int i_4 = -1;
 long l_5 = -1L;
 int i_7 = 1;
 if (abs(d_1)!=1.0 || abs(f_3)!=1.0f || abs(i_4)!=1 || (abs(l_5) - 1L)!=0){
 i_7 = 0;
 }
 return;
 }
 }
 }OpenCL}**/