package com.amd.aparapi.test;

public class UnrelatedIfsWithCommonEndByte{
   int width = 1024;

   float scale = 1f;

   int maxIterations = 10;

   public void run(){
      boolean a1 = true;
      boolean a2 = true;
      boolean b = false;
      boolean c = true;
      boolean outer = true;
      boolean result = false;
      if(outer){
         if(a1 && !a2){
            // result = true;
            if(b){
               result = true;
            }
            //result = false;
            if(c){
               result = true;
            }
            //  result = false;
         }
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
 int i_1 = 1;
 int i_2 = 1;
 int i_3 = 0;
 int i_4 = 1;
 int i_5 = 1;
 int i_6 = 0;
 if (i_5!=0 && i_1!=0 && i_2==0){
 if (i_3!=0){
 i_6 = 1;
 }
 if (i_4!=0){
 i_6 = 1;
 }
 }
 return;
 }
 }
 }OpenCL}**/