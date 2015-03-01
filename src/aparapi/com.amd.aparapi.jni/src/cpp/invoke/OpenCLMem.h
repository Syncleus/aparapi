#ifndef OPEN_CL_MEM_H
#define OPEN_CL_MEM_H

#include "JNIHelper.h"
#include "Common.h"

class OpenCLMem {
   public:
      static jobject create(JNIEnv *jenv) {
         return(JNIHelper::createInstance(jenv, OpenCLMemClassArg, VoidReturn));
      }

      static jsize getArraySizeInBytes(JNIEnv *jenv, jarray array, jlong argBits){
         jsize arrayLen = jenv->GetArrayLength(array);
         jsize sizeInBytes = getPrimitiveSizeInBytes(jenv, argBits) * arrayLen;
         return(sizeInBytes);
      }


      static jlong getBits(JNIEnv *jenv, jobject memInstance){
         return(JNIHelper::getInstanceField<jlong>(jenv, memInstance, "bits"));
      }
      static void setBits(JNIEnv *jenv, jobject memInstance, jlong bits){
         JNIHelper::setInstanceField<jlong>(jenv, memInstance, "bits", bits);
      }
      static void* getAddress(JNIEnv *jenv, jobject memInstance){
         return((void*)JNIHelper::getInstanceField<jlong>(jenv, memInstance, "address"));
      }
      static void setAddress(JNIEnv *jenv, jobject memInstance, void *address){
         JNIHelper::setInstanceField<jlong>(jenv, memInstance, "address", (jlong)address);
      }
      static void setInstance(JNIEnv *jenv, jobject memInstance, jobject instance){
         JNIHelper::setInstanceField<jobject>(jenv, memInstance, "instance", ObjectClassArg, instance);
      }
      static void setSizeInBytes(JNIEnv *jenv, jobject memInstance, jint sizeInBytes){
         JNIHelper::setInstanceField<jint>(jenv, memInstance, "sizeInBytes", sizeInBytes);
      }
      static size_t getSizeInBytes(JNIEnv *jenv, jobject memInstance){
         return((size_t)JNIHelper::getInstanceField<jint>(jenv, memInstance, "sizeInBytes"));
      }
      static jobject getInstance(JNIEnv *jenv, jobject memInstance){
         return(JNIHelper::getInstanceField<jobject>(jenv, memInstance, "instance", ObjectClassArg));
      }

      static cl_mem getMem(JNIEnv *jenv, jobject memInstance){
         cl_mem mem = (cl_mem)JNIHelper::getInstanceField<jlong>(jenv, memInstance, "memId");
         return(mem);
      }

      static void setMem(JNIEnv *jenv, jobject memInstance, cl_mem mem){
         JNIHelper::setInstanceField<jlong>(jenv, memInstance, "memId", (jlong)mem);
      }

      static void describe(JNIEnv *jenv, jobject memInstance){
         jlong memBits = OpenCLMem::getBits(jenv, memInstance);
         OpenCLMem::describeBits(jenv, memBits);
      }

      static jobject create(JNIEnv *jenv, cl_context context,  jlong argBits, jarray array);
      static cl_uint bitsToOpenCLMask(jlong argBits );
      static jsize getPrimitiveSizeInBytes(JNIEnv *jenv, jlong argBits);
      static void *pin(JNIEnv *jenv, jarray array, jlong *memBits);
      static void describeBits(JNIEnv *jenv, jlong bits);
      static void unpin(JNIEnv *jenv, jarray array, void *ptr, jlong *memBits);
};

#endif // OPEN_CL_MEM_H
