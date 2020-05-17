package com.amd.aparapi.test;

public class AndOrPrecedence2{
   public void run(){
      boolean a = false;
      boolean b = false;
      boolean d = false;
      @SuppressWarnings("unused") boolean pass = false;

      if(a && !(b && d)){
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
 int i_1 = 0;
 int i_2 = 0;
 int i_3 = 0;
 int i_4 = 0;
 if (i_1!=0 && (i_2==0 || i_3==0)){
 i_4 = 1;
 }
 return;
 }
 }
 }OpenCL}**/
