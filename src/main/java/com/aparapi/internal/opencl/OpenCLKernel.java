/**
 * Copyright (c) 2016 - 2017 Syncleus, Inc.
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
package com.aparapi.internal.opencl;

import java.util.List;

import com.aparapi.internal.jni.OpenCLJNI;

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
       disposeKernel(this);
   }


}
