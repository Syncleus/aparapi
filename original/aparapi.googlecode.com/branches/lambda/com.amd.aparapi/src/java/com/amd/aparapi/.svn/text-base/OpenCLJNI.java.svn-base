package com.amd.aparapi;

import java.util.List;
import java.util.logging.Logger;

public class OpenCLJNI{
   private static Logger logger = Logger.getLogger(Config.getLoggerName());

   static boolean openCLAvailable = true;
   static final OpenCLJNI jni = new OpenCLJNI();

   public static OpenCLJNI getJNI(){
      return (jni);
   }

   native public List<OpenCLPlatform> getPlatforms();

   native public OpenCLProgram createProgram(OpenCLDevice context, String openCLSource);

   native public OpenCLKernel createKernel(OpenCLProgram program, String kernelName, List<OpenCLArgDescriptor> args);

   native public void invoke(OpenCLKernel openCLKernel, Object[] args);

   native public void remap(OpenCLProgram program, OpenCLMem mem, long address);

   native public void getMem(OpenCLProgram program, OpenCLMem mem);

   native public byte[] getBytes(String className);

   public boolean isOpenCLAvailable(){
      return (openCLAvailable);
   }

}
