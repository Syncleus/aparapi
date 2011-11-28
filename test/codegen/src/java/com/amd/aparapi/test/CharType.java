package com.amd.aparapi.test;

import com.amd.aparapi.Kernel;

public class CharType extends Kernel {
  @Override
  public void run() {
    final char c = Character.MAX_VALUE;
    this.out[0] = c;
  }

  int out[] = new int[1];
}

/**{OpenCL{
     typedef struct This_s{
        __global int *out;
        int passid;
     }This;
     int get_pass_id(This *this){
        return this->passid;
     }
     __kernel void run(
        __global int *out, 
        int passid
     ){
        This thisStruct;
        This* this=&thisStruct;
        this->out = out;
        this->passid = passid;
        {
           unsigned short c = 65535;
           this->out[0]  = 65535;
           return;
        }
     }
}OpenCL}**/
