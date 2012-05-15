package com.amd.aparapi;

import java.util.List;
import java.util.logging.Logger;

public class OpenCLJNI{
   private static Logger logger = Logger.getLogger(Config.getLoggerName());

   static boolean openCLAvailable = false;
   static {
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

   static final OpenCLJNI jni = new OpenCLJNI();

   public static OpenCLJNI getJNI() {
      return (jni);
   }

   public final static int INT_BIT = 1 << 0;

   public final static int FLOAT_BIT = 1 << 1;

   public final static int DOUBLE_BIT = 1 << 2;

   public final static int SHORT_BIT = 1 << 3;
   

   public final static int ARRAY_BIT = 1 << 4;

   public final static int GLOBAL_BIT = 1 << 5;

   public final static int LOCAL_BIT = 1 << 6;

   public final static int CONST_BIT = 1 << 7;

   public final static int PRIMITIVE_BIT = 1 << 8;

   public final static int LONG_BIT = 1 << 9;

   public final static int READONLY_BIT = 1 << 10;

   public final static int WRITEONLY_BIT = 1 << 11;

   public final static int READWRITE_BIT = 1 << 12;

   public final static int MEM_DIRTY_BIT = 1 << 13;

   public final static int MEM_COPY_BIT = 1 << 14;

   public final static int MEM_ENQUEUED_BIT = 1 << 15;

   public final static int ARG_BIT = 1 << 16;

   public final static int BYTE_BIT = 1 << 17;
   
   native public List<OpenCLPlatform> getPlatforms();

   native public OpenCLProgram createProgram(OpenCLDevice context, String openCLSource);

   native public OpenCLKernel createKernel(OpenCLProgram program, String kernelName, List<OpenCLArg> args);

   native public void invoke(OpenCLKernel openCLKernel, Object[] args);

   native public void remap(OpenCLProgram program, OpenCLMem mem, long address);

   native public void getMem(OpenCLProgram program, OpenCLMem mem);
   
   public boolean isOpenCLAvailable(){
      return(openCLAvailable);
   }

}
