package com.amd.aparapi.test;

public class TernaryNested{
   public void run() {
      boolean a = false, b = false, c = false;
      @SuppressWarnings("unused") int count = a ? b ? 1 : 2 : c ? 3 : 4;
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
      char a = 0;
      char b = 0;
      char c = 0;
      int count = (a!=0)?(b!=0)?1:2:(c!=0)?3:4;
      return;
   }
}
}OpenCL}**/
