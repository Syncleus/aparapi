package com.amd.aparapi;

import java.util.List;
import java.util.logging.Logger;

public class OpenCLJNI{
   private static Logger logger = Logger.getLogger(Config.getLoggerName());

   static boolean openCLAvailable = false;
   static {
      if (Config.useAgent) {
         logger.fine("Using agent!");
         openCLAvailable = true;
      } else {
         String arch = System.getProperty("os.arch");
         logger.fine("arch = " + arch);
         String aparapiLibraryName = null;

         if (arch.equals("amd64") || arch.equals("x86_64")) {
            aparapiLibraryName = "aparapi_x86_64";
         } else if (arch.equals("x86") || arch.equals("i386")) {
            aparapiLibraryName = "aparapi_x86";
         } else {
            logger.warning("Expected property os.arch to contain amd64, x86_64, x86 or i386 but instead found " + arch
                  + " as a result we don't know which aparapi to attempt to load.");
         }
         if (aparapiLibraryName != null) {
            logger.fine("attempting to load aparapi shared lib " + aparapiLibraryName);
            try {
               Runtime.getRuntime().loadLibrary(aparapiLibraryName);
               openCLAvailable = true;
            } catch (UnsatisfiedLinkError e) {
               System.out
                     .println("Check your environment. Failed to load aparapi native library "
                           + aparapiLibraryName
                           + " or possibly failed to locate opencl native library (opencl.dll/opencl.so). Ensure that both are in your PATH (windows) or in LD_LIBRARY_PATH (linux).");

            }
         }
      }
   }

   static final OpenCLJNI jni = new OpenCLJNI();

   public static OpenCLJNI getJNI() {
      return (jni);
   }

   native public List<OpenCLPlatform> getPlatforms();

   native public OpenCLProgram createProgram(OpenCLDevice context, String openCLSource);

   native public OpenCLKernel createKernel(OpenCLProgram program, String kernelName, List<OpenCLArgDescriptor> args);

   native public void invoke(OpenCLKernel openCLKernel, Object[] args);

   native public void remap(OpenCLProgram program, OpenCLMem mem, long address);

   native public void getMem(OpenCLProgram program, OpenCLMem mem);

   native public byte[] getBytes(String className);

   public boolean isOpenCLAvailable() {
      return (openCLAvailable);
   }

}
