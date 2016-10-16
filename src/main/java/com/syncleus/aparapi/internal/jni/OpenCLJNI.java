package com.syncleus.aparapi.internal.jni;

import com.syncleus.aparapi.ProfileInfo;
import java.util.List;

import com.syncleus.aparapi.device.OpenCLDevice;
import com.syncleus.aparapi.internal.opencl.OpenCLArgDescriptor;
import com.syncleus.aparapi.internal.opencl.OpenCLKernel;
import com.syncleus.aparapi.internal.opencl.OpenCLMem;
import com.syncleus.aparapi.internal.opencl.OpenCLPlatform;
import com.syncleus.aparapi.internal.opencl.OpenCLProgram;

/**
 * This class is intended to be used as a 'proxy' or 'facade' object for Java code to interact with JNI
 */
public abstract class OpenCLJNI{

   protected native List<OpenCLPlatform> getPlatforms();

   protected native OpenCLProgram createProgram(OpenCLDevice context, String openCLSource);

   protected native OpenCLKernel createKernelJNI(OpenCLProgram program, String kernelName, OpenCLArgDescriptor[] args);

   protected native void invoke(OpenCLKernel openCLKernel, Object[] args);

   protected native void disposeKernel(OpenCLKernel openCLKernel);

   protected native void disposeProgram(OpenCLProgram openCLProgram);

   protected native List<ProfileInfo> getProfileInfo(OpenCLProgram openCLProgram);

   protected native void remap(OpenCLProgram program, OpenCLMem mem, long address);

   protected native byte[] getBytes(String className);

   protected native void getMem(OpenCLProgram program, OpenCLMem mem);
}
