package com.amd.aparapi.test;

public class FloatParams{

   int addEmUp(float y, float z) {
      return ((int) y + (int) z);
   }

   public void run() {

      int y = 2;

      float x = 0f;

      addEmUp((x = (float) y), x);

   }
}
/**{OpenCL{
typedef struct This_s{
   
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
   }

int com_amd_aparapi_test_FloatParams__addEmUp(This *this, float y, float z){
   return(((int)y + (int)z));
}
__kernel void run(
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->passid = passid;
   {
      int y = 2;
      float x = 0.0f;
      com_amd_aparapi_test_FloatParams__addEmUp(this, x=(float)y, x);
      return;
   }
}
}OpenCL}**/
