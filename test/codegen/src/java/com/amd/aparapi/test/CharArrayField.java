package com.amd.aparapi.test;

public class CharArrayField {
   public void run() {
      out[0] = 0;
   }

   char out[] = new char[1];
}

/**{OpenCL{
    typedef struct This_s{
       __global unsigned short  *out;
       int passid;
    }This;
    int get_pass_id(This *this){
       return this->passid;
    }
    __kernel void run(
       __global unsigned short  *out, 
       int passid
    ){
       This thisStruct;
       This* this=&thisStruct;
       this->out = out;
       this->passid = passid;
       {
          this->out[0]  = 0;
          return;
       }
    }
}OpenCL}**/
