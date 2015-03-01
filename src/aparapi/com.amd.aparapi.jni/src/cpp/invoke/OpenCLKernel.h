#ifndef OPEN_CL_KERNEL_H
#define OPEN_CL_KERNEL_H

#include "Common.h"
#include "JNIHelper.h"

class OpenCLKernel{
   public:

      static jobject create(JNIEnv *jenv, cl_kernel kernel, jobject programInstance, jstring name, jobjectArray args){
         return(JNIHelper::createInstance(jenv, OpenCLKernelClass, 
                     ArgsVoidReturn( LongArg OpenCLProgramClassArg StringClassArg ArrayArg(OpenCLArgDescriptorClass)), 
                     (jlong)kernel, programInstance, name, args));
      }

      static cl_kernel getKernel(JNIEnv *jenv, jobject kernelInstance){
          cl_kernel k = (cl_kernel) JNIHelper::getInstanceField<jlong>(jenv, kernelInstance, "kernelId");
         return((cl_kernel) JNIHelper::getInstanceField<jlong>(jenv, kernelInstance, "kernelId"));
      }

      static jobject getProgramInstance(JNIEnv *jenv, jobject kernelInstance){
         return(JNIHelper::getInstanceField<jobject>(jenv, kernelInstance, "program", OpenCLProgramClassArg));
      }

      static jobjectArray getArgsArray(JNIEnv *jenv, jobject kernelInstance){
         return(reinterpret_cast<jobjectArray> 
                     (JNIHelper::getInstanceField<jobject>(jenv, kernelInstance, "args", 
                        ArrayArg(OpenCLArgDescriptorClass) )));
      }
};

#endif //OPEN_CL_KERNEL_H
