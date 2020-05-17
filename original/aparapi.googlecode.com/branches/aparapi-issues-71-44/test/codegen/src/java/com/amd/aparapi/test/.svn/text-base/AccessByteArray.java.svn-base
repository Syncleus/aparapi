package com.amd.aparapi.test;

/**
 * 
 * Test whether we can assign a byte array element.
 * @author gfrost
 *
 */
public class AccessByteArray{
   byte[] bytes = new byte[1024];

   public void run() {

      for (int i = 0; i < 1024; i++) {

         bytes[i] = (byte) 1;

      }

   }
}
/**{OpenCL{
 typedef struct This_s{
   __global char  *bytes;
   
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
   }

__kernel void run(
   __global char  *bytes,
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->bytes = bytes;
   this->passid = passid;
   {
      for (int i = 0; i<1024; i++){
         this->bytes[i]  = 1;
      }
      return;
   }
}
}OpenCL}**/
