package com.amd.aparapi.test;

public class PlayPen{
   public void run(){
      int testValue = 10;
      boolean pass = false;

      if((testValue % 2 == 0 || testValue <= 0 && (testValue >= 100) && testValue % 4 == 0)){
         pass = true;
      }

      if((testValue < 3 || testValue > 5) && (testValue < 2 || testValue > 2) || testValue > 5){
         pass = true;
      }
      boolean a = false, b = false, c = false, d = false, e = false, f = false;
      if((a || b && c && d) && e || f){
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
 int i_1 = 10;
 int i_2 = 0;
 if ((i_1 % 2)==0 || i_1<=0 && i_1>=100 && (i_1 % 4)==0){
 i_2 = 1;
 }
 if ((i_1<3 || i_1>5) && (i_1<2 || i_1>2) || i_1>5){
 i_2 = 1;
 }
 int i_3 = 0;
 int i_4 = 0;
 int i_5 = 0;
 int i_6 = 0;
 int i_7 = 0;
 int i_8 = 0;
 if ((i_3!=0 || i_4!=0 && i_5!=0 && i_6!=0) && i_7!=0 || i_8!=0){
 i_2 = 1;
 }
 return;
 }
 }
 }OpenCL}**/