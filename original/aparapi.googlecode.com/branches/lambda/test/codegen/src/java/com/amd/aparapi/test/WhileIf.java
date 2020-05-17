package com.amd.aparapi.test;

public class WhileIf{
   public void run(){

      int a = 0;
      int b = 0;
      int c = 0;
      int d = 0;

      while(a == a){
         if(b == b){
            c = c;
         }
         //d = d; // remove this will work
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
 int i_1 = 0;
 int i_2 = 0;
 int i_3 = 0;
 int i_4 = 0;
 for (; i_1==i_1; ){

 if (i_2==i_2){
 i_3 = i_3;
 }
 }
 return;
 }
 }
 }OpenCL}**/