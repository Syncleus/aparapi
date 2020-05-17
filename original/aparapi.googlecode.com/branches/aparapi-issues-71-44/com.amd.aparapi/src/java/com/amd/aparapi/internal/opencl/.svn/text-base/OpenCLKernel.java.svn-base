package com.amd.aparapi.internal.opencl;

import java.util.List;

import com.amd.aparapi.internal.jni.OpenCLJNI;

public class OpenCLKernel extends OpenCLJNI {

   private final List<OpenCLArgDescriptor> args;

   private final OpenCLProgram program;

   private final String kernelName;

   /**
    * Minimal constructor
    * 
    * @param _program
    * @param _kernelName
    * @param _args
    */
   public OpenCLKernel(OpenCLProgram _program, String _kernelName, List<OpenCLArgDescriptor> _args) {
      program = _program;
      kernelName = _kernelName;
      //      args = _args.toArray(new OpenCLArgDescriptor[0]);

      for (final OpenCLArgDescriptor arg : _args) {
         arg.kernel = this;
      }

      args = _args;
   }

   public OpenCLKernel createKernel() {
      return createKernel(program, kernelName, args);
   }

   public String getName() {
      return kernelName;
   }

   public void invoke(Object[] _args) {
      invoke(this, _args);
   }
}
