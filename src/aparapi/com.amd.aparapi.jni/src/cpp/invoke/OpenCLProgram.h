
#ifndef OPEN_CL_PROGRAM_H
#define OPEN_CL_PROGRAM_H

#include "JNIHelper.h"

class OpenCLProgram{
   public:
      static jobject create(JNIEnv *jenv, cl_program program, cl_command_queue queue, 
                            cl_context context, jobject deviceInstance, jstring source, 
                            jstring log) {
         return(JNIHelper::createInstance(jenv, OpenCLProgramClass, 
                     ArgsVoidReturn( LongArg LongArg LongArg 
                         OpenCLDeviceClassArg StringClassArg) , 
                     (jlong)program, (jlong)queue, (jlong)context, deviceInstance, source));
      }

      static cl_context getContext(JNIEnv *jenv, jobject programInstance){
         return((cl_context) JNIHelper::getInstanceField<jlong>(jenv, programInstance, "contextId")); 
      }
      static cl_program getProgram(JNIEnv *jenv, jobject programInstance){
         return((cl_program) JNIHelper::getInstanceField<jlong>(jenv, programInstance, "programId")); 
      }
      static cl_command_queue getCommandQueue(JNIEnv *jenv, jobject programInstance){
         return((cl_command_queue)JNIHelper::getInstanceField<jlong>(jenv, programInstance, "queueId"));
      }
};


#endif //OPEN_CL_PROGRAM_H
