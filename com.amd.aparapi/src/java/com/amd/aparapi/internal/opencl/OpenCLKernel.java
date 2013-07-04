package com.amd.aparapi.internal.opencl;

import java.util.List;

import com.amd.aparapi.internal.jni.OpenCLJNI;

public class OpenCLKernel extends OpenCLJNI{

   private OpenCLArgDescriptor[] args = null;

   private OpenCLProgram program = null;

   private String kernelName = null;

   private long kernelId = 0;

   /**
    * This constructor is specifically for JNI usage
    * 
    * @param kernel
    * @param programInstance
    * @param name
    * @param _args
    */
   public OpenCLKernel(long kernel, OpenCLProgram programInstance, String name, OpenCLArgDescriptor[] _args) {
      kernelId = kernel;
      program = programInstance;
      kernelName = name;
      args = _args;
   }

   private OpenCLKernel() {
   }

   /**
    * This method is used to create a new Kernel from JNI
    * 
    * @param _program
    * @param _kernelName
    * @param _args
    * @return
    */
   public static OpenCLKernel createKernel(OpenCLProgram _program, String _kernelName, List<OpenCLArgDescriptor> _args) {
      final OpenCLArgDescriptor[] argArray = _args.toArray(new OpenCLArgDescriptor[0]);
      final OpenCLKernel oclk = new OpenCLKernel().createKernelJNI(_program, _kernelName, argArray);
      for (final OpenCLArgDescriptor arg : argArray) {
         arg.kernel = oclk;
      }
      return oclk;
   }

   public String getName() {
      return kernelName;
   }

   public void invoke(Object[] _args) {
      invoke(this, _args);
   }

   public void dispose(){
       dispose(this);
   }
}
