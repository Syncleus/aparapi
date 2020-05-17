package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class RightShifts extends Kernel{

   int iout[] = new int[10];

   int i1, i2;

   public void run() {
      iout[1] = i1 >> i2;
      iout[2] = i1 >>> i2;
   }
}
/**{OpenCL{
typedef struct This_s{
   __global int *iout;
   int i1;
   int i2;
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
   }

__kernel void run(
   __global int *iout, 
   int i1,
   int i2,
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->iout = iout;
   this->i1 = i1;
   this->i2 = i2;
   this->passid = passid;
   {
      this->iout[1]  = this->i1 >> this->i2;
      this->iout[2]  = ((unsigned int)this->i1) >> this->i2;
      return;
   }
}

}OpenCL}**/
