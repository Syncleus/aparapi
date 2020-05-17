package com.amd.aparapi.test;

public class TernaryNested{
   public void run(){
      boolean a = false, b = false, c = false;
      int count = a ? b ? 1 : 2 : c ? 3 : 4;
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
 int i_1 = 0;
 int i_2 = 0;
 int i_3 = 0;
 int i_4 = (i_1!=0)?(i_2!=0)?1:2:(i_3!=0)?3:4;
 return;
 }
 }
 }OpenCL}**/