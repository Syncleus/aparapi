package com.amd.aparapi.test;

public class PlayPen{
   public void run() {
      int testValue = 10;
      @SuppressWarnings("unused") boolean pass = false;

      if ((testValue % 2 == 0 || testValue <= 0 && (testValue >= 100) && testValue % 4 == 0)) {
         pass = true;
      }

      if ((testValue < 3 || testValue > 5) && (testValue < 2 || testValue > 2) || testValue > 5) {
         pass = true;
      }
      boolean a = false, b = false, c = false, d = false, e = false, f = false;
      if ((a || b && c && d) && e || f) {
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
      int testValue = 10;
      char pass = 0;
      if ((testValue % 2)==0 || testValue<=0 && testValue>=100 && (testValue % 4)==0){
         pass = 1;
      }
      if ((testValue<3 || testValue>5) && (testValue<2 || testValue>2) || testValue>5){
         pass = 1;
      }
      char a = 0;
      char b = 0;
      char c = 0;
      char d = 0;
      char e = 0;
      char f = 0;
      if ((a!=0 || b!=0 && c!=0 && d!=0) && e!=0 || f!=0){
         pass = 1;
      }
      return;
   }
}
}OpenCL}**/
