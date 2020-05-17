package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

interface IFoo{
   public int bar(int n);
}

public class ImplementsInterface extends Kernel implements IFoo{
   int out[] = new int[1];

   int ival = 3;

   public int bar(int n) {
      return n + ival;
   }

   public void run() {
      out[0] = bar(1);
      @SuppressWarnings("unused") boolean pass = false;
   }
}
/**{OpenCL{
typedef struct This_s{
   int ival;
   __global int *out;   
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
   }

int com_amd_aparapi_test_ImplementsInterface__bar(This *this, int n){
   return((n + this->ival));
}
__kernel void run(
   int ival, 
   __global int *out,
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->ival = ival;
   this->out = out;
   this->passid = passid;
   {
      this->out[0]  = com_amd_aparapi_test_ImplementsInterface__bar(this, 1);
      char pass = 0;
      return;
   }
}
}OpenCL}**/
