package com.amd.aparapi.internal.jni;

import java.util.List;

import com.amd.aparapi.device.OpenCLDevice;
import com.amd.aparapi.internal.opencl.OpenCLArgDescriptor;
import com.amd.aparapi.internal.opencl.OpenCLKernel;
import com.amd.aparapi.internal.opencl.OpenCLMem;
import com.amd.aparapi.internal.opencl.OpenCLPlatform;
import com.amd.aparapi.internal.opencl.OpenCLProgram;

/**
 * This class is intended to be used as a 'proxy' or 'facade' object for Java code to interact with JNI
 */
public abstract class OpenCLJNI{

   protected native List<OpenCLPlatform> getPlatforms();

   protected native OpenCLProgram createProgram(OpenCLDevice context, String openCLSource);

   protected native OpenCLKernel createKernelJNI(OpenCLProgram program, String kernelName, OpenCLArgDescriptor[] args);

   protected native void invoke(OpenCLKernel openCLKernel, Object[] args);

   protected native void dispose(OpenCLKernel openCLKernel);

   protected native void remap(OpenCLProgram program, OpenCLMem mem, long address);

   protected native byte[] getBytes(String className);

   protected native void getMem(OpenCLProgram program, OpenCLMem mem);
}
