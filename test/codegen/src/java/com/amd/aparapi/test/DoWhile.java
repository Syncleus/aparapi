package com.amd.aparapi.test;

public class DoWhile{
   public void run() {
      @SuppressWarnings("unused") boolean pass = false;
      int i = 0;
      do{
         pass = true;
         i++;
      }while (i < 10);
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
      char pass = 0;
      int i = 0;
      do{
         pass = 1;
         i++;
      }while (i<10);
      return;
   }
}
}OpenCL}**/
