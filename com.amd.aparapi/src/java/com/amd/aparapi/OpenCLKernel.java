package com.amd.aparapi;

import java.util.List;

public class OpenCLKernel{
   private OpenCLArgDescriptor[] args;

   private long kernelId;

   private OpenCLProgram program;

   private String name;

   OpenCLKernel(long _kernelId, OpenCLProgram _program, String _name, List<OpenCLArgDescriptor> _args) {
      kernelId = _kernelId;
      program = _program;
      name = _name;
      args = _args.toArray(new OpenCLArgDescriptor[0]);
      for (OpenCLArgDescriptor arg : args) {
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
