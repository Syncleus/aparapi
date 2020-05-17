package com.amd.aparapi.test;

public class DoWhileOr{
   public void run(){
      @SuppressWarnings("unused") boolean pass = false;
      int i = 0;
      do{
         pass = true;
         i++;
      }while(i < 10 || i == 100);
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
 do{
 i_1 = 1;
 i_2++;
 }while(i_2<10 || i2==100);

 return;
 }
 }
 }OpenCL}**/
