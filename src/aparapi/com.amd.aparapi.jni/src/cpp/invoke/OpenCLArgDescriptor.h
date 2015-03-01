#ifndef OPEN_CL_ARG_DESCRIPTOR_H
#define OPEN_CL_ARG_DESCRIPTOR_H

#include "JNIHelper.h"

class OpenCLArgDescriptor{
   public:

      static jlong getBits(JNIEnv *jenv, jobject argInstance){
         return(JNIHelper::getInstanceField<jlong>(jenv, argInstance, "bits"));
      }
      static void setBits(JNIEnv *jenv, jobject argInstance, jlong bits){
         JNIHelper::setInstanceField<jlong>(jenv, argInstance, "bits", bits);
      }
      static jobject getMemInstance(JNIEnv *jenv, jobject argInstance){
         return(JNIHelper::getInstanceField<jobject>(jenv, argInstance, "memVal", OpenCLMemClassArg));
      }
      static void setMemInstance(JNIEnv *jenv, jobject argInstance, jobject memInstance){
         JNIHelper::setInstanceField<jobject>(jenv, argInstance, "memVal", OpenCLMemClassArg, memInstance);
      }
      static void describe(JNIEnv *jenv, jobject argDef, jint argIndex){
         jlong argBits = OpenCLArgDescriptor::getBits(jenv, argDef);
         fprintf(stderr, " %d ", argIndex);
         OpenCLArgDescriptor::describeBits(jenv, argBits);
         fprintf(stderr, "\n");
      }

      static void describeBits(JNIEnv *jenv, jlong bits);
};

#endif //OPEN_CL_ARG_DESCRIPTOR_H
