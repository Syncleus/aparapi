package com.amd.aparapi.test;

public class UnrelatedIfElsesWithCommonEndByte{
   /*
   1:   istore_1   (0:iconst_1)
   3:   istore_2   (2:iconst_0)
   5:   istore_3   (4:iconst_1)
   7:   istore  4  (6:iconst_0)
   10:  ifeq    39 (9:iload_1)        ?
   14:  ifeq    23 (13:iload_2)       | ?
   18:  istore  4  (17:iconst_1)      | |
   20:  goto    26                    | | +
   24:  istore  4  (23:  iconst_0)    | v |
   27:  ifeq    36 (26:  iload_3)     |   v ?
   31:  istore  4  (30:  iconst_1)    |     |
   33:  goto    39                    |     | +
   37:  istore  4  (36:  iconst_0)    |     v |
   39:  return                        v       v
   */
   int width = 1024;

   float scale = 1f;

   int maxIterations = 10;

   public void run() {
      boolean a = true;
      boolean b = false;
      boolean c = true;
      @SuppressWarnings("unused") boolean result = false;

      if (a) {
         if (b) {
            result = true;
         } else {
            result = false;
         }

         if (c) {
            result = true;
         } else {
            result = false;
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
      char a = 1;
      char b = 0;
      char c = 1;
      char result = 0;
      if (a!=0){
         if (b!=0){
            result = 1;
         } else {
            result = 0;
         }
         if (c!=0){
            result = 1;
         } else {
            result = 0;
         }
      }
      return;
   }
}
}OpenCL}**/
