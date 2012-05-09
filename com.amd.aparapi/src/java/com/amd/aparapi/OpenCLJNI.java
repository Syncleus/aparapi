package com.amd.aparapi;

import java.util.List;

public class OpenCLJNI{

   static {
      String arch = System.getProperty("os.arch");
      //System.out.println("arch = "+arch);

      String libName = null;
      try {

         if (arch.equals("amd64") || arch.equals("x86_64")) {
            libName = "aparapi_x86_64";
         } else if (arch.equals("x86") || arch.equals("i386")) {
            libName = "aparapi_x86";
         }
         if (libName != null) {
            System.out.println("loading " + libName);
            Runtime.getRuntime().loadLibrary(libName);
         } else {
            System.out.println("Expected property os.arch to contain amd64 or x86 but found " + arch
                  + " don't know which library to load.");
         }
      } catch (UnsatisfiedLinkError e) {
         System.out
               .println("Check your environment. Failed to load aparapi native library "
                     + libName
                     + " or possibly failed to locate opencl native library (opencl.dll/opencl.so). Ensure that both are in your PATH (windows) or in LD_LIBRARY_PATH (linux).");

      }

      Runtime.getRuntime().loadLibrary(libName);
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

   native public List<OpenCLPlatform> getPlatforms();

   native public OpenCLProgram createProgram(OpenCLDevice context, String openCLSource);

   native public OpenCLKernel createKernel(OpenCLProgram program, String kernelName, List<OpenCLArg> args);

   native public void invoke(OpenCLKernel openCLKernel, Object[] args);

   native public void remap(OpenCLProgram program, OpenCLMem mem, long address);

   native public void getMem(OpenCLProgram program, OpenCLMem mem);

}
