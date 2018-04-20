/**
 * Copyright (c) 2016 - 2018 Syncleus, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aparapi.internal.jni;

import com.aparapi.ProfileInfo;
import java.util.List;

import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.opencl.OpenCLArgDescriptor;
import com.aparapi.internal.opencl.OpenCLKernel;
import com.aparapi.internal.opencl.OpenCLMem;
import com.aparapi.internal.opencl.OpenCLPlatform;
import com.aparapi.internal.opencl.OpenCLProgram;

/**
 * This class is intended to be used as a 'proxy' or 'facade' object for Java code to interact with JNI
 */
public abstract class OpenCLJNI{

   protected native List<OpenCLPlatform> getPlatforms();

   public OpenCLProgram createProgram(OpenCLDevice context, String openCLSource)
   {
      return this.createProgram(context, openCLSource, "");
   }

   protected native OpenCLProgram createProgram(OpenCLDevice context, String openCLSource, String binaryKey);

   protected native OpenCLKernel createKernelJNI(OpenCLProgram program, String kernelName, OpenCLArgDescriptor[] args);

   protected native void invoke(OpenCLKernel openCLKernel, Object[] args);

   protected native void disposeKernel(OpenCLKernel openCLKernel);

   protected native void disposeProgram(OpenCLProgram openCLProgram);

   protected native List<ProfileInfo> getProfileInfo(OpenCLProgram openCLProgram);

   protected native void remap(OpenCLProgram program, OpenCLMem mem, long address);

   protected native byte[] getBytes(String className);

   protected native void getMem(OpenCLProgram program, OpenCLMem mem);
}
