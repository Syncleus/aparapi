package com.amd.aparapi;

import java.util.List;

public class OpenCLKernel{
   private OpenCLArg[] args;

   private long kernelId;

   private OpenCLProgram program;

   private String name;

   OpenCLKernel(long _kernelId, OpenCLProgram _program, String _name, List<OpenCLArg> _args) {
      kernelId = _kernelId;
      program = _program;
      name = _name;
      args = _args.toArray(new OpenCLArg[0]);
      for (OpenCLArg arg : args) {
         arg.kernel = this;
      }
   }

   public String getName() {
      return name;
   }

   public void invoke(Object[] _args) {
      OpenCLJNI.getJNI().invoke(this, _args);

   }

}
