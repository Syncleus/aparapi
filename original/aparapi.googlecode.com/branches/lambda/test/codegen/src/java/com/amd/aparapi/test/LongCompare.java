package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class LongCompare extends Kernel{
   public void run(){
      long n1 = 1;
      long n2 = 2;
      boolean pass = false;
      if(n2 > n1){
         pass = true;
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
 long l_1 = 1L;
 long l_3 = 2L;
 int i_5 = 0;
 if ((l_3 - l_1)>0){
 i_5 = 1;
 }
 return;
 }
 }
 }OpenCL}**/