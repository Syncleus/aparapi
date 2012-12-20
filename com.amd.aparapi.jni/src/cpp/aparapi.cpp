/*
   Copyright (c) 2010-2011, Advanced Micro Devices, Inc.
   All rights reserved.

   Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
   following conditions are met:

   Redistributions of source code must retain the above copyright notice, this list of conditions and the following
   disclaimer. 

   Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution. 

   Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products
   derived from this software without specific prior written permission. 

   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
   INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
   DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
   SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
   SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
   WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
   OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

   If you use the software (in whole or in part), you shall adhere to all applicable U.S., European, and other export
   laws, including but not limited to the U.S. Export Administration Regulations ("EAR"), (15 C.F.R. Sections 730 
   through 774), and E.U. Council Regulation (EC) No 1334/2000 of 22 June 2000.  Further, pursuant to Section 740.6 of
   the EAR, you hereby certify that, except pursuant to a license granted by the United States Department of Commerce
   Bureau of Industry and Security or as otherwise permitted pursuant to a License Exception under the U.S. Export 
   Administration Regulations ("EAR"), you will not (1) export, re-export or release to a national of a country in 
   Country Groups D:1, E:1 or E:2 any restricted technology, software, or source code you receive hereunder, or (2) 
   export to Country Groups D:1, E:1 or E:2 the direct product of such technology or software, if such foreign produced
   direct product is subject to national security controls as identified on the Commerce Control List (currently 
   found in Supplement 1 to Part 774 of EAR).  For the most current Country Group listings, or for additional 
   information about the EAR or your obligations under those regulations, please refer to the U.S. Bureau of Industry
   and Security?s website at http://www.bis.doc.gov/. 
   */
#include "common.h"
#include "config.h"
#include "profileInfo.h"
#include "arrayBuffer.h"
#include "clHelper.h"
#define APARAPI_SOURCE
#include "aparapi.h"
#include "com_amd_aparapi_KernelRunner.h"
#include "opencljni.h"

class Range{
   public:
      static jclass rangeClazz;
      static jfieldID globalSize_0_FieldID;
      static jfieldID globalSize_1_FieldID;
      static jfieldID globalSize_2_FieldID;
      static jfieldID localSize_0_FieldID;
      static jfieldID localSize_1_FieldID;
      static jfieldID localSize_2_FieldID;
      static jfieldID dimsFieldID;
      static jfieldID localIsDerivedFieldID; 
      jobject range;
      cl_int dims;
      size_t *offsets;
      size_t *globalDims;
      size_t *localDims;
      jboolean localIsDerived;
      Range(JNIEnv *jenv, jobject range):
         range(range),
         dims(0),
         offsets(NULL),
         globalDims(NULL),
         localDims(NULL){
            if (rangeClazz ==NULL){
               jclass rangeClazz = jenv->GetObjectClass(range); 
               globalSize_0_FieldID = jenv->GetFieldID(rangeClazz, "globalSize_0", "I"); ASSERT_FIELD(globalSize_0_);
               globalSize_1_FieldID = jenv->GetFieldID(rangeClazz, "globalSize_1", "I"); ASSERT_FIELD(globalSize_1_);
               globalSize_2_FieldID = jenv->GetFieldID(rangeClazz, "globalSize_2", "I"); ASSERT_FIELD(globalSize_2_);
               localSize_0_FieldID = jenv->GetFieldID(rangeClazz, "localSize_0", "I"); ASSERT_FIELD(localSize_0_);
               localSize_1_FieldID = jenv->GetFieldID(rangeClazz, "localSize_1", "I"); ASSERT_FIELD(localSize_1_);
               localSize_2_FieldID = jenv->GetFieldID(rangeClazz, "localSize_2", "I"); ASSERT_FIELD(localSize_2_);
               dimsFieldID = jenv->GetFieldID(rangeClazz, "dims", "I"); ASSERT_FIELD(dims);
               localIsDerivedFieldID = jenv->GetFieldID(rangeClazz, "localIsDerived", "Z"); ASSERT_FIELD(localIsDerived);
            }
            dims = jenv->GetIntField(range, dimsFieldID);
            localIsDerived = jenv->GetBooleanField(range, localIsDerivedFieldID);
            if (dims >0){
               //fprintf(stderr, "native range dims == %d\n", dims);
               offsets = new size_t[dims];
               globalDims = new size_t[dims];
               localDims = new size_t[dims];
               offsets[0]= 0;
               localDims[0]= jenv->GetIntField(range, localSize_0_FieldID);
               //fprintf(stderr, "native range localSize_0 == %d\n", localDims[0]);
               globalDims[0]= jenv->GetIntField(range, globalSize_0_FieldID);
               //fprintf(stderr, "native range globalSize_0 == %d\n", globalDims[0]);
               if (dims >1){
                  offsets[1]= 0;
                  localDims[1]= jenv->GetIntField(range, localSize_1_FieldID);
                  //fprintf(stderr, "native range localSize_1 == %d\n", localDims[1]);
                  globalDims[1]= jenv->GetIntField(range, globalSize_1_FieldID);
                  //fprintf(stderr, "native range globalSize_1 == %d\n", globalDims[1]);
                  if (dims >2){
                     offsets[2]= 0;
                     localDims[2]= jenv->GetIntField(range, localSize_2_FieldID);
                     //fprintf(stderr, "native range localSize_2 == %d\n", localDims[2]);
                     globalDims[2]= jenv->GetIntField(range, globalSize_2_FieldID);
                     //fprintf(stderr, "native range globalSize_2 == %d\n", globalDims[2]);
                  }
               }

            }
         }
      ~Range(){
         if (offsets!= NULL){
            delete offsets;
         }
         if (globalDims!= NULL){
            delete globalDims;
         }
         if (localDims!= NULL){
            delete localDims;
         }
      }
};
jclass Range::rangeClazz = (jclass)0;
jfieldID  Range::globalSize_0_FieldID=0;
jfieldID  Range::globalSize_1_FieldID=0;
jfieldID  Range::globalSize_2_FieldID=0;
jfieldID  Range::localSize_0_FieldID=0;
jfieldID  Range::localSize_1_FieldID=0;
jfieldID  Range::localSize_2_FieldID=0;
jfieldID  Range::dimsFieldID=0;
jfieldID  Range::localIsDerivedFieldID=0; 

class JNIContext ; // forward reference

class KernelArg{
   private:
      static jclass argClazz;
      static jfieldID nameFieldID;
      static jfieldID typeFieldID; 
      static jfieldID sizeInBytesFieldID;
      static jfieldID numElementsFieldID;
   public:
      static jfieldID javaArrayFieldID; 
   public:
      JNIContext *jniContext;  
      jobject argObj;    // the Java KernelRunner.KernelArg object that we are mirroring.
      jobject javaArg;   // global reference to the corresponding java KernelArg object we grabbed our own global reference so that the object won't be collected until we dispose!
      char *name;        // used for debugging printfs
      jint type;         // a bit mask determining the type of this arg

      ArrayBuffer *arrayBuffer;

      KernelArg(JNIEnv *jenv, JNIContext *jniContext, jobject argObj); // Uses JNIContext so cant inline here see below

      ~KernelArg(){
      }

      void unpinAbort(JNIEnv *jenv){
         arrayBuffer->unpinAbort(jenv);
      }
      void unpinCommit(JNIEnv *jenv){
         arrayBuffer->unpinCommit(jenv);
      }
      void unpin(JNIEnv *jenv){
         //if  (value.ref.isPinned == JNI_FALSE){		 
         //     fprintf(stdout, "why are we unpinning buffer %s! isPinned = JNI_TRUE\n", name);
         //}
         if (isMutableByKernel()){
            // we only need to commit if the buffer has been written to
            // we use mode=0 in that case (rather than JNI_COMMIT) because that frees any copy buffer if it exists
            // in most cases this array will have been pinned so this will not be an issue
            unpinCommit(jenv);
         }else {
            // fast path for a read_only buffer
            unpinAbort(jenv);
         }
      }
      void pin(JNIEnv *jenv){
         arrayBuffer->pin(jenv);
      }

      int isArray(){
         return(type&com_amd_aparapi_KernelRunner_ARG_ARRAY);
      }
      int isReadByKernel(){
         return(type&com_amd_aparapi_KernelRunner_ARG_READ);
      }
      int isMutableByKernel(){
         return(type&com_amd_aparapi_KernelRunner_ARG_WRITE);
      }
      int isExplicit(){
         return(type&com_amd_aparapi_KernelRunner_ARG_EXPLICIT);
      }
      int usesArrayLength(){
         return(type&com_amd_aparapi_KernelRunner_ARG_ARRAYLENGTH);
      }
      int isExplicitWrite(){
         return(type&com_amd_aparapi_KernelRunner_ARG_EXPLICIT_WRITE);
      }
      int isImplicit(){
         return(!isExplicit());
      }
      int isPrimitive(){
         return(type&com_amd_aparapi_KernelRunner_ARG_PRIMITIVE);
      }
      int isGlobal(){
         return(type&com_amd_aparapi_KernelRunner_ARG_GLOBAL);
      }
      int isFloat(){
         return(type&com_amd_aparapi_KernelRunner_ARG_FLOAT);
      }
      int isLong(){
         return (type&com_amd_aparapi_KernelRunner_ARG_LONG);
      }
      int isInt(){
         return (type&com_amd_aparapi_KernelRunner_ARG_INT);
      }
      int isDouble(){
         return (type&com_amd_aparapi_KernelRunner_ARG_DOUBLE);
      }
      int isBoolean(){
         return (type&com_amd_aparapi_KernelRunner_ARG_BOOLEAN);
      }
      int isByte(){
         return (type&com_amd_aparapi_KernelRunner_ARG_BYTE);
      }
      int isShort(){
         return (type&com_amd_aparapi_KernelRunner_ARG_SHORT);
      }
      int isLocal(){
         return (type&com_amd_aparapi_KernelRunner_ARG_LOCAL);
      }
      int isStatic(){
         return (type&com_amd_aparapi_KernelRunner_ARG_STATIC);
      }
      int isConstant(){
         return (type&com_amd_aparapi_KernelRunner_ARG_CONSTANT);
      }
      int isAparapiBuf(){
         return (type&com_amd_aparapi_KernelRunner_ARG_APARAPI_BUF);
      }
      int isBackedByArray(){
         return ( (isArray() && (isGlobal() || isConstant())));
      }
      int needToEnqueueRead(){
         return(((isArray() && isGlobal()) || ((isAparapiBuf()&&isGlobal()))) && (isImplicit()&&isMutableByKernel()));
      }
      int needToEnqueueWrite(){
         return ((isImplicit()&&isReadByKernel())||(isExplicit()&&isExplicitWrite()));
      }
      void syncType(JNIEnv* jenv){
         type = jenv->GetIntField(javaArg, typeFieldID);
      }
      void syncSizeInBytes(JNIEnv* jenv){
         arrayBuffer->lengthInBytes = jenv->GetIntField(javaArg, sizeInBytesFieldID);
      }
      void syncJavaArrayLength(JNIEnv* jenv){
         arrayBuffer->length = jenv->GetIntField(javaArg, numElementsFieldID);
      }
      void clearExplicitBufferBit(JNIEnv* jenv){
         type &= ~com_amd_aparapi_KernelRunner_ARG_EXPLICIT_WRITE;
         jenv->SetIntField(javaArg, typeFieldID,type );
      }

      void syncValue(JNIEnv *jenv); // Uses JNIContext so can't inline here we below.  
      cl_int setLocalBufferArg(JNIEnv *jenv, int argIdx, int argPos); // Uses JNIContext so can't inline here we below.  
      cl_int setPrimitiveArg(JNIEnv *jenv, int argIdx, int argPos ); // Uses JNIContext so can't inline here we below.  
};

jclass KernelArg::argClazz=(jclass)0;
jfieldID KernelArg::nameFieldID=0;
jfieldID KernelArg::typeFieldID=0; 
jfieldID KernelArg::javaArrayFieldID=0; 
jfieldID KernelArg::sizeInBytesFieldID=0;
jfieldID KernelArg::numElementsFieldID=0; 

class JNIContext{
   private: 
      jint flags;
      jboolean valid;
   public:
      jobject kernelObject;
      jobject openCLDeviceObject;
      jclass kernelClass;
      cl_device_id deviceId;
      cl_int deviceType;
      cl_context context;
      cl_command_queue commandQueue;
      cl_program program;
      cl_kernel kernel;
      jint argc;
      KernelArg** args;
      cl_event* executeEvents;
      cl_event* readEvents;
      cl_ulong profileBaseTime;
      jint* readEventArgs;
      cl_event* writeEvents;
      jint* writeEventArgs;
      jboolean firstRun;
      jint passes;
      ProfileInfo *exec;
      FILE* profileFile;

      static JNIContext* getJNIContext(jlong jniContextHandle){
         return((JNIContext*)jniContextHandle);
      }

      JNIContext(JNIEnv *jenv, jobject _kernelObject, jobject _openCLDeviceObject, jint _flags): 
         kernelObject(jenv->NewGlobalRef(_kernelObject)),
         kernelClass((jclass)jenv->NewGlobalRef(jenv->GetObjectClass(_kernelObject))), 
         openCLDeviceObject(jenv->NewGlobalRef(_openCLDeviceObject)),
         flags(_flags),
         profileBaseTime(0),
         passes(0),
         exec(NULL),
         deviceType(((flags&com_amd_aparapi_KernelRunner_JNI_FLAG_USE_GPU)==com_amd_aparapi_KernelRunner_JNI_FLAG_USE_GPU)?CL_DEVICE_TYPE_GPU:CL_DEVICE_TYPE_CPU),
         profileFile(NULL), 
         valid(JNI_FALSE){
            cl_int status = CL_SUCCESS;
            jobject platformInstance = OpenCLDevice::getPlatformInstance(jenv, openCLDeviceObject);
            cl_platform_id platformId = OpenCLPlatform::getPlatformId(jenv, platformInstance);
            deviceId = OpenCLDevice::getDeviceId(jenv, openCLDeviceObject);
            cl_device_type returnedDeviceType;
            clGetDeviceInfo(deviceId, CL_DEVICE_TYPE,  sizeof(returnedDeviceType), &returnedDeviceType, NULL);
            //fprintf(stderr, "device[%d] CL_DEVICE_TYPE = %x\n", deviceId, returnedDeviceType);


            cl_context_properties cps[3] = { CL_CONTEXT_PLATFORM, (cl_context_properties)platformId, 0 };
            cl_context_properties* cprops = (NULL == platformId) ? NULL : cps;
            context = clCreateContextFromType( cprops, returnedDeviceType, NULL, NULL, &status); 
            ASSERT_CL_NO_RETURN("clCreateContextFromType()");
            if (status == CL_SUCCESS){
               valid = JNI_TRUE;
            }
         }

      jboolean isValid(){
         return(valid);
      }
      jboolean isUsingGPU(){
         return((flags&com_amd_aparapi_KernelRunner_JNI_FLAG_USE_GPU)==com_amd_aparapi_KernelRunner_JNI_FLAG_USE_GPU?JNI_TRUE:JNI_FALSE);
      }
      ~JNIContext(){
      }

      void dispose(JNIEnv *jenv){
         //fprintf(stdout, "dispose()\n");
         cl_int status = CL_SUCCESS;
         jenv->DeleteGlobalRef(kernelObject);
         jenv->DeleteGlobalRef(kernelClass);
         if (context != 0){
            status = clReleaseContext(context);
            //fprintf(stdout, "dispose context %0lx\n", context);
            ASSERT_CL_NO_RETURN("clReleaseContext()");
            context = (cl_context)0;
         }
         if (commandQueue != 0){
            if (config->isTrackingOpenCLResources()){
               commandQueueList.remove((cl_command_queue)commandQueue, __LINE__, __FILE__);
            }
            status = clReleaseCommandQueue((cl_command_queue)commandQueue);
            //fprintf(stdout, "dispose commandQueue %0lx\n", commandQueue);
            ASSERT_CL_NO_RETURN("clReleaseCommandQueue()");
            commandQueue = (cl_command_queue)0;
         }
         if (program != 0){
            status = clReleaseProgram((cl_program)program);
            //fprintf(stdout, "dispose program %0lx\n", program);
            ASSERT_CL_NO_RETURN("clReleaseProgram()");
            program = (cl_program)0;
         }
         if (kernel != 0){
            status = clReleaseKernel((cl_kernel)kernel);
            //fprintf(stdout, "dispose kernel %0lx\n", kernel);
            ASSERT_CL_NO_RETURN("clReleaseKernel()");
            kernel = (cl_kernel)0;
         }
         if (argc> 0){
            for (int i=0; i< argc; i++){
               KernelArg *arg = args[i];
               if (!arg->isPrimitive()){
                  if (arg->arrayBuffer != NULL){
                     if (arg->arrayBuffer->mem != 0){
                        if (config->isTrackingOpenCLResources()){
                           memList.remove((cl_mem)arg->arrayBuffer->mem, __LINE__, __FILE__);
                        }
                        status = clReleaseMemObject((cl_mem)arg->arrayBuffer->mem);
                        //fprintf(stdout, "dispose arg %d %0lx\n", i, arg->arrayBuffer->mem);
                        ASSERT_CL_NO_RETURN("clReleaseMemObject()");
                        arg->arrayBuffer->mem = (cl_mem)0;
                     }
                     if (arg->arrayBuffer->javaArray != NULL)  {
                        jenv->DeleteWeakGlobalRef((jweak) arg->arrayBuffer->javaArray);
                     }
                     delete arg->arrayBuffer;
                     arg->arrayBuffer = NULL;
                  }
               }
               if (arg->name != NULL){
                  free(arg->name); arg->name = NULL;
               }
               if (arg->javaArg != NULL ) {
                  jenv->DeleteGlobalRef((jobject) arg->javaArg);
               }
               delete arg; arg=args[i]=NULL;
            }
            delete[] args; args=NULL;

            // do we need to call clReleaseEvent on any of these that are still retained....
            delete []readEvents; readEvents =NULL;
            delete []writeEvents; writeEvents = NULL;
            delete []executeEvents; executeEvents = NULL;

            if (config->isProfilingEnabled()) {
               if (config->isProfilingCSVEnabled()) {
                  if (profileFile != NULL && profileFile != stderr) {
                     fclose(profileFile);
                  }
               }
               delete[] readEventArgs; readEventArgs=0;
               delete[] writeEventArgs; writeEventArgs=0;
            } 
         }
         if (config->isTrackingOpenCLResources()){
            fprintf(stderr, "after dispose{ \n");
            commandQueueList.report(stderr);
            memList.report(stderr); 
            readEventList.report(stderr); 
            executeEventList.report(stderr); 
            writeEventList.report(stderr); 
            fprintf(stderr, "}\n");
         }
      }

      /*
         Release JNI critical pinned arrays before returning to java code
         */
      void unpinAll(JNIEnv* jenv) {
         for (int i=0; i< argc; i++){
            KernelArg *arg = args[i];
            if (arg->isBackedByArray()) {
               arg->unpin(jenv);
            }
         }
      }


};

KernelArg::KernelArg(JNIEnv *jenv, JNIContext *jniContext, jobject argObj):
   jniContext(jniContext),
   argObj(argObj){
      javaArg = jenv->NewGlobalRef(argObj);   // save a global ref to the java Arg Object
      if (argClazz == 0){
         jclass c = jenv->GetObjectClass(argObj); 
         nameFieldID = jenv->GetFieldID(c, "name", "Ljava/lang/String;"); ASSERT_FIELD(name);
         typeFieldID = jenv->GetFieldID(c, "type", "I"); ASSERT_FIELD(type);
         javaArrayFieldID = jenv->GetFieldID(c, "javaArray", "Ljava/lang/Object;"); ASSERT_FIELD(javaArray);
         sizeInBytesFieldID = jenv->GetFieldID(c, "sizeInBytes", "I"); ASSERT_FIELD(sizeInBytes);
         numElementsFieldID = jenv->GetFieldID(c, "numElements", "I"); ASSERT_FIELD(numElements);
         argClazz  = c;
      }
      type = jenv->GetIntField(argObj, typeFieldID);
      jstring nameString  = (jstring)jenv->GetObjectField(argObj, nameFieldID);
      const char *nameChars = jenv->GetStringUTFChars(nameString, NULL);
#ifdef _WIN32
      name=_strdup(nameChars);
#else
      name=strdup(nameChars);
#endif
      jenv->ReleaseStringUTFChars(nameString, nameChars);
      if (isArray()){
         arrayBuffer= new ArrayBuffer();
      }
   }

cl_int KernelArg::setLocalBufferArg(JNIEnv *jenv, int argIdx, int argPos){
   if (config->isVerbose()){
       fprintf(stderr, "ISLOCAL, clSetKernelArg(jniContext->kernel, %d, %d, NULL);\n", argIdx, (int) arrayBuffer->lengthInBytes);
   }
   return(clSetKernelArg(jniContext->kernel, argPos, (int)arrayBuffer->lengthInBytes, NULL));
}
cl_int KernelArg::setPrimitiveArg(JNIEnv *jenv, int argIdx, int argPos){
   cl_int status = CL_SUCCESS;
   if (isFloat()){
      if (isStatic()){
         jfieldID fieldID = jenv->GetStaticFieldID(jniContext->kernelClass, name, "F");
         jfloat f = jenv->GetStaticFloatField(jniContext->kernelClass, fieldID);
         if (config->isVerbose()){
            fprintf(stderr, "clSetKernelArg static primitive float '%s' index=%d pos=%d value=%f\n",
                 name, argIdx, argPos, f); 
         }
         status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jfloat), &f);
      }else{
         jfieldID fieldID = jenv->GetFieldID(jniContext->kernelClass, name, "F");
         jfloat f = jenv->GetFloatField(jniContext->kernelObject, fieldID);
         if (config->isVerbose()){
            fprintf(stderr, "clSetKernelArg primitive float '%s' index=%d pos=%d value=%f\n",
                 name, argIdx, argPos, f); 
         }
         status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jfloat), &f);
      }
   }else if (isInt()){
      if (isStatic()){
         jfieldID fieldID = jenv->GetStaticFieldID(jniContext->kernelClass, name, "I");
         jint i = jenv->GetStaticIntField(jniContext->kernelClass, fieldID);
         if (config->isVerbose()){
            fprintf(stderr, "clSetKernelArg static primitive int '%s' index=%d pos=%d value=%d\n",
                 name, argIdx, argPos, i); 
         }
         status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jint), &i);
      }else{
         jfieldID fieldID = jenv->GetFieldID(jniContext->kernelClass, name, "I");
         jint i = jenv->GetIntField(jniContext->kernelObject, fieldID);
         if (config->isVerbose()){
            fprintf(stderr, "clSetKernelArg primitive int '%s' index=%d pos=%d value=%d\n",
                 name, argIdx, argPos, i); 
         }
         status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jint), &i);
      }
   }else if (isBoolean()){
      if (isStatic()){
         jfieldID fieldID = jenv->GetStaticFieldID(jniContext->kernelClass, name, "Z");
         jboolean z = jenv->GetStaticBooleanField(jniContext->kernelClass, fieldID);
         if (config->isVerbose()){
            fprintf(stderr, "clSetKernelArg static primitive boolean '%s' index=%d pos=%d value=%d\n",
                 name, argIdx, argPos, z); 
         }
         status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jboolean), &z);
      }else{
         jfieldID fieldID = jenv->GetFieldID(jniContext->kernelClass, name, "Z");
         jboolean z = jenv->GetBooleanField(jniContext->kernelObject, fieldID);
         if (config->isVerbose()){
            fprintf(stderr, "clSetKernelArg primitive boolean '%s' index=%d pos=%d value=%d\n",
                 name, argIdx, argPos, z); 
         }
         status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jboolean), &z);
      }
   }else if (isByte()){
      if (isStatic()){
         jfieldID fieldID = jenv->GetStaticFieldID(jniContext->kernelClass, name, "B");
         jbyte b = jenv->GetStaticByteField(jniContext->kernelClass, fieldID);
         if (config->isVerbose()){
            fprintf(stderr, "clSetKernelArg static primitive byte '%s' index=%d pos=%d value=%d\n",
                 name, argIdx, argPos, b); 
         }
         status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jbyte), &b);
      }else{
         jfieldID fieldID = jenv->GetFieldID(jniContext->kernelClass, name, "B");
         jbyte b = jenv->GetByteField(jniContext->kernelObject, fieldID);
         if (config->isVerbose()){
            fprintf(stderr, "clSetKernelArg primitive byte '%s' index=%d pos=%d value=%d\n",
                 name, argIdx, argPos, b); 
         }
         status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jbyte), &b);
      }
   }else if (isLong()){
      if (isStatic()){
         jfieldID fieldID = jenv->GetStaticFieldID(jniContext->kernelClass, name, "J");
         jlong j = jenv->GetStaticLongField(jniContext->kernelClass, fieldID);
         if (config->isVerbose()){
            fprintf(stderr, "clSetKernelArg static primitive long '%s' index=%d pos=%d value=%ld\n",
                 name, argIdx, argPos, j); 
         }
         status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jlong), &j);
      }else{
         jfieldID fieldID = jenv->GetFieldID(jniContext->kernelClass, name, "J");
         jlong j = jenv->GetLongField(jniContext->kernelObject, fieldID);
         if (config->isVerbose()){
            fprintf(stderr, "clSetKernelArg primitive long '%s' index=%d pos=%d value=%ld\n",
                 name, argIdx, argPos, j); 
         }
         status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jlong), &j);
      }
   }else if (isDouble()){
      if (isStatic()){
         jfieldID fieldID = jenv->GetStaticFieldID(jniContext->kernelClass, name, "D");
         jdouble d  = jenv->GetStaticDoubleField(jniContext->kernelClass, fieldID);
         if (config->isVerbose()){
            fprintf(stderr, "clSetKernelArg static primitive long '%s' index=%d pos=%d value=%lf\n",
                 name, argIdx, argPos, d); 
         }
         status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jdouble), &d);
      }else{
         jfieldID fieldID = jenv->GetFieldID(jniContext->kernelClass, name, "D");
         jdouble d = jenv->GetDoubleField(jniContext->kernelObject, fieldID);
         if (config->isVerbose()){
            fprintf(stderr, "clSetKernelArg primitive long '%s' index=%d pos=%d value=%lf\n",
                 name, argIdx, argPos, d); 
         }
         status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jdouble), &d);
      }
   }
   return status;
}

JNI_JAVA(jint, KernelRunner, disposeJNI)
   (JNIEnv *jenv, jobject jobj, jlong jniContextHandle) {
      if (config== NULL){
         config = new Config(jenv);
      }
      cl_int status = CL_SUCCESS;
      JNIContext* jniContext = JNIContext::getJNIContext(jniContextHandle);
      if (jniContext != NULL){
         jniContext->dispose(jenv);
         delete jniContext;
         jniContext = NULL;
      }
      return(status);
   }

void idump(char *str, void *ptr, int size){
   int * iptr = (int *)ptr;
   for (unsigned i=0; i<size/sizeof(int); i++){
      fprintf(stderr, "%s%4d %d\n", str, i, iptr[i]);
   }
}

void fdump(char *str, void *ptr, int size){
   float * fptr = (float *)ptr;
   for (unsigned i=0; i<size/sizeof(float); i++){
      fprintf(stderr, "%s%4d %6.2f\n", str, i, fptr[i]);
   }
}


jint writeProfileInfo(JNIContext* jniContext){
   cl_ulong currSampleBaseTime = -1;
   int pos = 1;

   if (jniContext->firstRun) {
      fprintf(jniContext->profileFile, "# PROFILE Name, queued, submit, start, end (microseconds)\n");
   }       

   // A read by a user kernel means the OpenCL layer wrote to the kernel and vice versa
   for (int i=0; i< jniContext->argc; i++){
      KernelArg *arg=jniContext->args[i];
      if (arg->isBackedByArray() && arg->isReadByKernel()){

         // Initialize the base time for this sample
         if (currSampleBaseTime == -1) {
            currSampleBaseTime = arg->arrayBuffer->write.queued;
         } 
         fprintf(jniContext->profileFile, "%d write %s,", pos++, arg->name);

         fprintf(jniContext->profileFile, "%lu,%lu,%lu,%lu,",  
               (arg->arrayBuffer->write.queued - currSampleBaseTime)/1000, 
               (arg->arrayBuffer->write.submit - currSampleBaseTime)/1000, 
               (arg->arrayBuffer->write.start - currSampleBaseTime)/1000, 
               (arg->arrayBuffer->write.end - currSampleBaseTime)/1000);
      }
   }

   for (jint pass=0; pass<jniContext->passes; pass++){

      // Initialize the base time for this sample if necessary
      if (currSampleBaseTime == -1) {
         currSampleBaseTime = jniContext->exec[pass].queued;
      } 

      // exec 
      fprintf(jniContext->profileFile, "%d exec[%d],", pos++, pass);

      fprintf(jniContext->profileFile, "%lu,%lu,%lu,%lu,",  
            (jniContext->exec[pass].queued - currSampleBaseTime)/1000, 
            (jniContext->exec[pass].submit - currSampleBaseTime)/1000, 
            (jniContext->exec[pass].start - currSampleBaseTime)/1000, 
            (jniContext->exec[pass].end - currSampleBaseTime)/1000);
   }

   // 
   if ( jniContext->argc == 0 ) {
      fprintf(jniContext->profileFile, "\n");
   } else { 
      for (int i=0; i< jniContext->argc; i++){
         KernelArg *arg=jniContext->args[i];
         if (arg->isBackedByArray() && arg->isMutableByKernel()){

            // Initialize the base time for this sample
            if (currSampleBaseTime == -1) {
               currSampleBaseTime = arg->arrayBuffer->read.queued;
            }

            fprintf(jniContext->profileFile, "%d read %s,", pos++, arg->name);

            fprintf(jniContext->profileFile, "%lu,%lu,%lu,%lu,",  
                  (arg->arrayBuffer->read.queued - currSampleBaseTime)/1000, 
                  (arg->arrayBuffer->read.submit - currSampleBaseTime)/1000, 
                  (arg->arrayBuffer->read.start - currSampleBaseTime)/1000, 
                  (arg->arrayBuffer->read.end - currSampleBaseTime)/1000);
         }
      }
   }
   fprintf(jniContext->profileFile, "\n");
   return(0);
}

// Should failed profiling abort the run and return early?
cl_int profile(ProfileInfo *profileInfo, cl_event *event, jint type, char* name, cl_ulong profileBaseTime ){
   cl_int status = CL_SUCCESS;
   status = clGetEventProfilingInfo(*event, CL_PROFILING_COMMAND_QUEUED, sizeof(profileInfo->queued), &(profileInfo->queued), NULL);
   ASSERT_CL( "clGetEventProfiliningInfo() QUEUED");
   status = clGetEventProfilingInfo(*event, CL_PROFILING_COMMAND_SUBMIT, sizeof(profileInfo->submit), &(profileInfo->submit), NULL);
   ASSERT_CL( "clGetEventProfiliningInfo() SUBMIT");
   status = clGetEventProfilingInfo(*event, CL_PROFILING_COMMAND_START, sizeof(profileInfo->start), &(profileInfo->start), NULL);
   ASSERT_CL( "clGetEventProfiliningInfo() START");
   status = clGetEventProfilingInfo(*event, CL_PROFILING_COMMAND_END, sizeof(profileInfo->end), &(profileInfo->end), NULL);
   ASSERT_CL( "clGetEventProfiliningInfo() END");

   profileInfo->queued -= profileBaseTime;
   profileInfo->submit -= profileBaseTime;
   profileInfo->start -= profileBaseTime;
   profileInfo->end -= profileBaseTime;
   profileInfo->type = type;
   profileInfo->name = name;
   profileInfo->valid = true;
   return status;
}


//Step through all non-primitive (array of primitive or array object references) and determine if the field has changed
//The field may have been re-assigned by the Java code to NULL or another instance. 
//If we detect a change then we discard the previous cl_mem buffer, the caller will detect that the buffers are null and will create new cl_mem buffers. 
jint updateNonPrimitiveReferences(JNIEnv *jenv, jobject jobj, JNIContext* jniContext) {
   cl_int status = CL_SUCCESS;
   if (jniContext != NULL){
      for (jint i=0; i<jniContext->argc; i++){ 
         KernelArg *arg=jniContext->args[i];
         arg->syncType(jenv); // make sure that the JNI arg reflects the latest type info from the instance.  For example if the buffer is tagged as explicit and needs to be pushed

         if (config->isVerbose()){
            fprintf(stderr, "got type for %s: %08x\n", arg->name, arg->type);
         }
         if (!arg->isPrimitive()) {
            // Following used for all primitive arrays, object arrays and nio Buffers
            jarray newRef = (jarray)jenv->GetObjectField(arg->javaArg, KernelArg::javaArrayFieldID);
            if (config->isVerbose()){
               fprintf(stderr, "testing for Resync javaArray %s: old=%p, new=%p\n", arg->name, arg->arrayBuffer->javaArray, newRef);         
            }

            if (!jenv->IsSameObject(newRef, arg->arrayBuffer->javaArray)) {
               if (config->isVerbose()){
                  fprintf(stderr, "Resync javaArray for %s: %p  %p\n", arg->name, newRef, arg->arrayBuffer->javaArray);         
               }
               // Free previous ref if any
               if (arg->arrayBuffer->javaArray != NULL) {
                  jenv->DeleteWeakGlobalRef((jweak) arg->arrayBuffer->javaArray);
                  if (config->isVerbose()){
                     fprintf(stderr, "DeleteWeakGlobalRef for %s: %p\n", arg->name, arg->arrayBuffer->javaArray);         
                  }
               }

               // need to free opencl buffers, run will reallocate later
               if (arg->arrayBuffer->mem != 0) {
                  //fprintf(stderr, "-->releaseMemObject[%d]\n", i);
                  if (config->isTrackingOpenCLResources()){
                     memList.remove(arg->arrayBuffer->mem,__LINE__, __FILE__);
                  }
                  status = clReleaseMemObject((cl_mem)arg->arrayBuffer->mem);
                  //fprintf(stderr, "<--releaseMemObject[%d]\n", i);
                  ASSERT_CL("clReleaseMemObject()");
                  arg->arrayBuffer->mem = (cl_mem)0;
               }

               arg->arrayBuffer->addr = NULL;

               // Capture new array ref from the kernel arg object

               if (newRef != NULL) {
                  arg->arrayBuffer->javaArray = (jarray)jenv->NewWeakGlobalRef((jarray)newRef);
                  if (config->isVerbose()){
                     fprintf(stderr, "NewWeakGlobalRef for %s, set to %p\n", arg->name,
                           arg->arrayBuffer->javaArray);         
                  }
               } else {
                  arg->arrayBuffer->javaArray = NULL;
               }

               // Save the lengthInBytes which was set on the java side
               arg->syncSizeInBytes(jenv);

               if (config->isVerbose()){
                  fprintf(stderr, "updateNonPrimitiveReferences, args[%d].lengthInBytes=%d\n", i, arg->arrayBuffer->lengthInBytes);
               }
            } // object has changed
         }
      } // for each arg
   } // if jniContext != NULL
   return(status);
}



JNI_JAVA(jint, KernelRunner, runKernelJNI)
   (JNIEnv *jenv, jobject jobj, jlong jniContextHandle, jobject _range, jboolean needSync, jint passes) {
      if (config== NULL){
         config = new Config(jenv);
      }

      Range range(jenv, _range);

      cl_int status = CL_SUCCESS;
      JNIContext* jniContext = JNIContext::getJNIContext(jniContextHandle);


      if (jniContext->firstRun && config->isProfilingEnabled()){
         cl_event firstEvent;
         status = clEnqueueMarker(jniContext->commandQueue, &firstEvent);
         if (status != CL_SUCCESS) {
            PRINT_CL_ERR(status, "clEnqueueMarker endOfTxfers");
            return 0L;
         }
         status = clWaitForEvents(1, &firstEvent);
         if (status != CL_SUCCESS) {
            PRINT_CL_ERR(status, "clWaitForEvents");
            return 0L;
         }
         status = clGetEventProfilingInfo(firstEvent, CL_PROFILING_COMMAND_QUEUED, sizeof(jniContext->profileBaseTime), &(jniContext->profileBaseTime), NULL);
         if (status != CL_SUCCESS) {
            PRINT_CL_ERR(status, "clGetEventProfilingInfo#1");
            return 0L;
         }
         clReleaseEvent(firstEvent);
         if (status != CL_SUCCESS) {
            PRINT_CL_ERR(status, "clReleaseEvent() read event");
            return 0L;
         }
         if (config->isVerbose()){
            fprintf(stderr, "profileBaseTime %lu \n", jniContext->profileBaseTime);
         }
      }


      // Need to capture array refs
      if (jniContext->firstRun || needSync) {
         updateNonPrimitiveReferences(jenv, jobj, jniContext );
         if (config->isVerbose()){
            fprintf(stderr, "back from updateNonPrimitiveReferences\n");
         }
      }

      int writeEventCount = 0;

      // argPos is used to keep track of the kernel arg position, it can 
      // differ from "argIdx" due to insertion of javaArrayLength args which are not
      // fields read from the kernel object.

      int argPos=0;
      for (int argIdx=0; argIdx< jniContext->argc; argIdx++, argPos++){
         KernelArg *arg = jniContext->args[argIdx];
         arg->syncType(jenv); // make sure that the JNI arg reflects the latest type info from the instance.  For example if the buffer is tagged as explicit and needs to be pushed

         if (config->isVerbose()){
            fprintf(stderr, "got type for arg %d, %s, type=%08x\n", argIdx, arg->name, arg->type);
         }
         if (!arg->isPrimitive() && !arg->isLocal()) {
            if (config->isProfilingEnabled()){
               arg->arrayBuffer->read.valid = false;
               arg->arrayBuffer->write.valid = false;
            }
            // pin the arrays so that GC does not move them during the call

            // get the C memory address for the region being transferred
            // this uses different JNI calls for arrays vs. directBufs
            void * prevAddr =  arg->arrayBuffer->addr;
            arg->pin(jenv);

            if (config->isVerbose()){
               fprintf(stderr, "runKernel: arrayOrBuf ref %p, oldAddr=%p, newAddr=%p, ref.mem=%p\n",
                     arg->arrayBuffer->javaArray, 
                     prevAddr,
                     arg->arrayBuffer->addr,
                     arg->arrayBuffer->mem);
               fprintf(stderr, "at memory addr %p, contents: ", arg->arrayBuffer->addr);
               unsigned char *pb = (unsigned char *) arg->arrayBuffer->addr;
               for (int k=0; k<8; k++) {
                  fprintf(stderr, "%02x ", pb[k]);
               }
               fprintf(stderr, "\n" );
            }
            // record whether object moved 
            // if we see that isCopy was returned by getPrimitiveArrayCritical, treat that as a move
            bool objectMoved = (arg->arrayBuffer->addr != prevAddr) || arg->arrayBuffer->isCopy;

            if (config->isVerbose()){
               if (arg->isExplicit() && arg->isExplicitWrite()){
                  fprintf(stderr, "explicit write of %s\n",  arg->name);
               }
            }

            if (jniContext->firstRun || (arg->arrayBuffer->mem == 0) || objectMoved ){

               if (arg->arrayBuffer->mem != 0 && objectMoved){
                  // we need to release the old buffer 
                  if (config->isTrackingOpenCLResources()){
                     memList.remove((cl_mem)arg->arrayBuffer->mem, __LINE__, __FILE__);
                  }
                  status = clReleaseMemObject((cl_mem)arg->arrayBuffer->mem);
                  //fprintf(stdout, "dispose arg %d %0lx\n", i, arg->arrayBuffer->mem);
                  ASSERT_CL_NO_RETURN("clReleaseMemObject()");
                  arg->arrayBuffer->mem = (cl_mem)0;
               }
               // if either this is the first run or user changed input array
               // or gc moved something, then we create buffers/args
               cl_uint mask = CL_MEM_USE_HOST_PTR;
               if (arg->isReadByKernel() && arg->isMutableByKernel()) mask |= CL_MEM_READ_WRITE;
               else if (arg->isReadByKernel() && !arg->isMutableByKernel()) mask |= CL_MEM_READ_ONLY;
               else if (arg->isMutableByKernel()) mask |= CL_MEM_WRITE_ONLY;
               arg->arrayBuffer->memMask = mask;
               if (config->isVerbose()){
                  strcpy(arg->arrayBuffer->memSpec,"CL_MEM_USE_HOST_PTR");
                  if (mask & CL_MEM_READ_WRITE) strcat(arg->arrayBuffer->memSpec,"|CL_MEM_READ_WRITE");
                  if (mask & CL_MEM_READ_ONLY) strcat(arg->arrayBuffer->memSpec,"|CL_MEM_READ_ONLY");
                  if (mask & CL_MEM_WRITE_ONLY) strcat(arg->arrayBuffer->memSpec,"|CL_MEM_WRITE_ONLY");

                  fprintf(stderr, "%s %d clCreateBuffer(context, %s, size=%08x bytes, address=%08x, &status)\n", arg->name, 
                        argIdx, arg->arrayBuffer->memSpec, arg->arrayBuffer->lengthInBytes, arg->arrayBuffer->addr);
               }
               arg->arrayBuffer->mem = clCreateBuffer(jniContext->context, arg->arrayBuffer->memMask, 
                     arg->arrayBuffer->lengthInBytes, arg->arrayBuffer->addr, &status);

               if (status != CL_SUCCESS) {
                  PRINT_CL_ERR(status, "clCreateBuffer");
                  jniContext->unpinAll(jenv);
                  return status;
               }
               if (config->isTrackingOpenCLResources()){
                  memList.add(arg->arrayBuffer->mem, __LINE__, __FILE__);
               }

               status = clSetKernelArg(jniContext->kernel, argPos, sizeof(cl_mem), (void *)&(arg->arrayBuffer->mem));                  
               if (status != CL_SUCCESS) {
                  PRINT_CL_ERR(status, "clSetKernelArg (array)");
                  jniContext->unpinAll(jenv);
                  return status;
               }

               // Add the array length if needed
               if (arg->usesArrayLength()){
                  arg->syncJavaArrayLength(jenv);

                  status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jint), &(arg->arrayBuffer->length));

                  if (config->isVerbose()){
                     fprintf(stderr, "runKernel arg %d %s, length = %d\n", argIdx, arg->name, arg->arrayBuffer->length);
                  }
                  if (status != CL_SUCCESS) {
                     PRINT_CL_ERR(status, "clSetKernelArg (array length)");
                     jniContext->unpinAll(jenv);
                     return status;
                  }
                  argPos++;
               }
            } else {
               // Keep the arg position in sync if no updates were required
               if (arg->usesArrayLength()){
                  argPos++;
               }
            }

            // we only enqueue a write if we know the kernel actually reads the buffer or if there is an explicit write pending
            // the default behavior for Constant buffers is also that there is no write enqueued unless explicit

            if (arg->needToEnqueueWrite() && !arg->isConstant()){
               if (config->isVerbose()){
                  fprintf(stderr, "%swriting %s%sbuffer argIndex=%d argPos=%d %s\n",  
                        (arg->isExplicit() ? "explicitly " : ""), 
                        (arg->isConstant() ? "constant " : ""), 
                        (arg->isLocal() ? "local " : ""), 
                        argIdx,
                        argPos,
                        arg->name);
               }
               if (config->isProfilingEnabled()) {
                  jniContext->writeEventArgs[writeEventCount]=argIdx;
               }

               status = clEnqueueWriteBuffer(jniContext->commandQueue, arg->arrayBuffer->mem, CL_FALSE, 0, 
                     arg->arrayBuffer->lengthInBytes, arg->arrayBuffer->addr, 0, NULL, &(jniContext->writeEvents[writeEventCount]));
               if (status != CL_SUCCESS) {
                  PRINT_CL_ERR(status, "clEnqueueWriteBuffer");
                  jniContext->unpinAll(jenv);
                  return status;
               }
               if (config->isTrackingOpenCLResources()){
                  writeEventList.add(jniContext->writeEvents[writeEventCount],__LINE__, __FILE__);
               }
               writeEventCount++;
               if (arg->isExplicit() && arg->isExplicitWrite()){
                  if (config->isVerbose()){
                     fprintf(stderr, "clearing explicit buffer bit %d %s\n", argIdx, arg->name);
                  }
                  arg->clearExplicitBufferBit(jenv);
               }
            }
         } else if (arg->isLocal()){
            if (jniContext->firstRun){ // what if local buffer size has changed?  We need a check for resize here.
               status = arg->setLocalBufferArg(jenv, argIdx, argPos);
               if (status != CL_SUCCESS) {
                  PRINT_CL_ERR(status, "clSetKernelArg() (local)");
                  jniContext->unpinAll(jenv);
                  return status;
               }

                // Add the array length if needed
               if (arg->usesArrayLength()){
                  arg->syncJavaArrayLength(jenv);

                  status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jint), &(arg->arrayBuffer->length));

                  if (config->isVerbose()){
                     fprintf(stderr, "runKernel arg %d %s, javaArrayLength = %d\n", argIdx, arg->name, arg->arrayBuffer->length);
                  }
                  if (status != CL_SUCCESS) {
                     PRINT_CL_ERR(status, "clSetKernelArg (array length)");
                     jniContext->unpinAll(jenv);
                     return status;
                  }
                  argPos++;
               }
            } else {
               // Keep the arg position in sync if no updates were required
               if (arg->usesArrayLength()){
                  argPos++;
               }
            }
         }else{  // primitive arguments
            status = arg->setPrimitiveArg(jenv, argIdx, argPos);
            if (status != CL_SUCCESS) {
               PRINT_CL_ERR(status, "clSetKernelArg()");
               jniContext->unpinAll(jenv);
               return status;
            }
         }
      }  // for each arg



      // We will need to revisit the execution of multiple devices.  
      // POssibly cloning the range per device and mutating each to handle a unique subrange (of global) and
      // maybe even pushing the offset into the range class.

      //   size_t globalSize_0AsSizeT = (range.globalDims[0] /jniContext->deviceIdc);
      //   size_t localSize_0AsSizeT = range.localDims[0];

      // To support multiple passes we add a 'secret' final arg called 'passid' and just schedule multiple enqueuendrange kernels.  Each of which having a separate value of passid
      //
      //
      if (jniContext->exec){      // delete the last set
         delete jniContext->exec;
         jniContext->exec = NULL;
      } 
      jniContext->passes = passes;
      jniContext->exec = new ProfileInfo[passes];

      for (int passid=0; passid<passes; passid++){
         //size_t offset = 1; // (size_t)((range.globalDims[0]/jniContext->deviceIdc)*dev);
         status = clSetKernelArg(jniContext->kernel, argPos, sizeof(passid), &(passid));
         if (status != CL_SUCCESS) {
            PRINT_CL_ERR(status, "clSetKernelArg() (passid)");
            jniContext->unpinAll(jenv);
            return status;
         }

		// -----------
		// fix for Mac OSX CPU driver (and possibly others) which fail to give correct maximum work group info
		// while using clGetDeviceInfo
		// see: http://www.openwall.com/lists/john-dev/2012/04/10/4
		cl_uint max_group_size[3];
        status = clGetKernelWorkGroupInfo(jniContext->kernel, (cl_device_id)jniContext->deviceId, CL_KERNEL_WORK_GROUP_SIZE, sizeof(max_group_size), &max_group_size, NULL);
        
		if (status != CL_SUCCESS) {
			PRINT_CL_ERR(status, "clGetKernelWorkGroupInfo()");
		} else {
			range.localDims[0] = range.localDims[0] > max_group_size[0] ? max_group_size[0] : range.localDims[0];
		}
		// ------ end fix

         // two options here due to passid
         if (passid == 0){
            //fprintf(stderr, "setting passid to %d of %d first and last\n", passid, passes);
            // there may be 1 or more passes
            // enqueue depends on write enqueues 
            // we don't block but and we populate the executeEvents
            status = clEnqueueNDRangeKernel(
                  jniContext->commandQueue,
                  jniContext->kernel,
                  range.dims,
                  range.offsets, range.globalDims,
                  range.localDims,
                  writeEventCount,
                  writeEventCount?jniContext->writeEvents:NULL,
                  &jniContext->executeEvents[0]);
         }else{
            // we are in some passid >0 pass 
            // maybe middle or last!
            // we don't depend on write enqueues
            // we block and do supply executeEvents 
            //fprintf(stderr, "setting passid to %d of %d not first not last\n", passid, passes);
            //

            status = clWaitForEvents(1, &jniContext->executeEvents[0]);
            if (status != CL_SUCCESS) {
               PRINT_CL_ERR(status, "clWaitForEvents() execute event");
               jniContext->unpinAll(jenv);
               return status;
            }
            if (config->isTrackingOpenCLResources()){
               executeEventList.remove(jniContext->executeEvents[0],__LINE__, __FILE__);
            }
            status = clReleaseEvent(jniContext->executeEvents[0]);
            if (status != CL_SUCCESS) {
               PRINT_CL_ERR(status, "clReleaseEvent() read event");
               jniContext->unpinAll(jenv);
               return status;
            }


            // We must capture any profile info for passid-1  so we must wait for the last execution to complete
            if (passid == 1 && config->isProfilingEnabled()) {
               // Now we can profile info for passid-1 
               status = profile(&jniContext->exec[passid-1], &jniContext->executeEvents[0], 1, NULL, jniContext->profileBaseTime);
               if (status != CL_SUCCESS) {
                  jniContext->unpinAll(jenv);
                  return status;
               }
            }
            status = clEnqueueNDRangeKernel(
                  jniContext->commandQueue, 
                  jniContext->kernel,
                  range.dims,
                  range.offsets,
                  range.globalDims,
                  range.localDims,
                  0,    // wait for this event count
                  NULL, // list of events to wait for
                  &jniContext->executeEvents[0]);
         }


         if (status != CL_SUCCESS) {
            PRINT_CL_ERR(status, "clEnqueueNDRangeKernel()");
            for(int i = 0; i<range.dims;i++) {
               fprintf(stderr, "after clEnqueueNDRangeKernel, globalSize[%d] = %d, localSize[%d] = %d\n",
                     i, (int)range.globalDims[i], i, (int)range.localDims[i]);
            }
            jniContext->unpinAll(jenv);
            return status;
         }
         if(config->isTrackingOpenCLResources()){
            executeEventList.add(jniContext->executeEvents[0],__LINE__, __FILE__);
         }
       
      }

      // We will use readEventCount to track the number of reads. It will never be > jniContext->argc which is the size of readEvents[] and readEventArgs[]
      // readEvents[] will be populated with the event's that we will wait on below.  
      // readArgEvents[] will map the readEvent to the arg that originated it
      // So if we had
      //    arg[0]  read_write array
      //    arg[1]  read array
      //    arg[2]  write array
      //    arg[3]  primitive
      //    arg[4]  read array
      // At the end of the next loop 
      //    readCount=3
      //    readEvent[0] = new read event for arg0
      //    readArgEvent[0] = 0
      //    readEvent[1] = new read event for arg1
      //    readArgEvent[1] = 1
      //    readEvent[2] = new read event for arg4
      //    readArgEvent[2] = 4

      int readEventCount = 0; 

      for (int i=0; i< jniContext->argc; i++){
         KernelArg *arg = jniContext->args[i];

         if (arg->needToEnqueueRead()){
            if (arg->isConstant()){
               fprintf(stderr, "reading %s\n", arg->name);
            }
            if (config->isProfilingEnabled()) {
               jniContext->readEventArgs[readEventCount]=i;
            }
            if (config->isVerbose()){
               fprintf(stderr, "reading buffer %d %s\n", i, arg->name);
            }

            status = clEnqueueReadBuffer(jniContext->commandQueue, arg->arrayBuffer->mem, CL_FALSE, 0, 
                  arg->arrayBuffer->lengthInBytes,arg->arrayBuffer->addr , 1, jniContext->executeEvents, &(jniContext->readEvents[readEventCount]));
            if (status != CL_SUCCESS) {
               PRINT_CL_ERR(status, "clEnqueueReadBuffer()");
               jniContext->unpinAll(jenv);
               return status;
            }
            if (config->isTrackingOpenCLResources()){
               readEventList.add(jniContext->readEvents[readEventCount],__LINE__, __FILE__);
            }
            readEventCount++;
         }
      }

      // don't change the order here
      // We wait for the reads which each depend on the execution, which depends on the writes ;)
      // So after the reads have completed, we can release the execute and writes.

      if (readEventCount >0){
         status = clWaitForEvents(readEventCount, jniContext->readEvents);
         if (status != CL_SUCCESS) {
            PRINT_CL_ERR(status, "clWaitForEvents() read events");
            jniContext->unpinAll(jenv);
            return status;
         }

         for (int i=0; i< readEventCount; i++){
            if (config->isProfilingEnabled()) {
               status = profile(&jniContext->args[jniContext->readEventArgs[i]]->arrayBuffer->read, &jniContext->readEvents[i], 0,jniContext->args[jniContext->readEventArgs[i]]->name, jniContext->profileBaseTime);
               if (status != CL_SUCCESS) {
                  jniContext->unpinAll(jenv);
                  return status;
               }
            }
            status = clReleaseEvent(jniContext->readEvents[i]);
            if (status != CL_SUCCESS) {
               PRINT_CL_ERR(status, "clReleaseEvent() read event");
               jniContext->unpinAll(jenv);
               return status;
            }
            if (config->isTrackingOpenCLResources()){
               readEventList.remove(jniContext->readEvents[i],__LINE__, __FILE__);
            }
         }
      } else {
         // if readEventCount == 0 then we don't need any reads so we just wait for the executions to complete
         status = clWaitForEvents(1, jniContext->executeEvents);
         if (status != CL_SUCCESS) {
            PRINT_CL_ERR(status, "clWaitForEvents() execute event");
            jniContext->unpinAll(jenv);
            return status;
         }
      }
      if (config->isTrackingOpenCLResources()){
         executeEventList.remove(jniContext->executeEvents[0],__LINE__, __FILE__);
      }
      if (config->isProfilingEnabled()) {
         status = profile(&jniContext->exec[passes-1], &jniContext->executeEvents[0], 1, NULL, jniContext->profileBaseTime); // multi gpu ?
         if (status != CL_SUCCESS) {
            jniContext->unpinAll(jenv);
            return status;
         }
      }
      // extract the execution status from the executeEvent
      cl_int executeStatus;
      status = clGetEventInfo(jniContext->executeEvents[0], CL_EVENT_COMMAND_EXECUTION_STATUS, sizeof(cl_int), &executeStatus, NULL);
      if (status != CL_SUCCESS) {
         PRINT_CL_ERR(status, "clGetEventInfo() execute event");
         jniContext->unpinAll(jenv);
         return status;
      }
      if (executeStatus != CL_SUCCESS) {
         // it should definitely not be negative, but since we did a wait above, it had better be CL_COMPLETE==CL_SUCCESS
         PRINT_CL_ERR(executeStatus, "Execution status of execute event");
         jniContext->unpinAll(jenv);
         return executeStatus;
      }
      status = clReleaseEvent(jniContext->executeEvents[0]);
      if (status != CL_SUCCESS) {
         PRINT_CL_ERR(status, "clReleaseEvent() read event");
         jniContext->unpinAll(jenv);
         return status;
      }

      for (int i=0; i< writeEventCount; i++){
         if (config->isProfilingEnabled()) {
            profile(&jniContext->args[jniContext->writeEventArgs[i]]->arrayBuffer->write, &jniContext->writeEvents[i], 2, jniContext->args[jniContext->writeEventArgs[i]]->name, jniContext->profileBaseTime);
         }
         status = clReleaseEvent(jniContext->writeEvents[i]);
         if (status != CL_SUCCESS) {
            PRINT_CL_ERR(status, "clReleaseEvent() write event");
            jniContext->unpinAll(jenv);
            return status;
         }
         if (config->isTrackingOpenCLResources()){
            writeEventList.remove(jniContext->writeEvents[i],__LINE__, __FILE__);
         }
      }

      jniContext->unpinAll(jenv);

      if (config->isProfilingCSVEnabled()) {
         writeProfileInfo(jniContext);
      }
      if (config->isTrackingOpenCLResources()){
         fprintf(stderr, "following execution of kernel{\n");
         commandQueueList.report(stderr);
         memList.report(stderr); 
         readEventList.report(stderr); 
         executeEventList.report(stderr); 
         writeEventList.report(stderr); 
         fprintf(stderr, "}\n");
      }

      jniContext->firstRun = false;

      //fprintf(stderr, "About to return %d from exec\n", status);
      return(status);
   }


// we return the JNIContext from here 
JNI_JAVA(jlong, KernelRunner, initJNI)
   (JNIEnv *jenv, jclass clazz, jobject kernelObject, jobject openCLDeviceObject, jint flags) {
      if (config== NULL){
         config = new Config(jenv);
      }
      cl_int status = CL_SUCCESS;
      JNIContext* jniContext = new JNIContext(jenv, kernelObject, openCLDeviceObject, flags);

      if (jniContext->isValid()){

         return((jlong)jniContext);
      }else{
         return(0L);
      }
   }


JNI_JAVA(jlong, KernelRunner, buildProgramJNI)
   (JNIEnv *jenv, jobject jobj, jlong jniContextHandle, jstring source) {
      JNIContext* jniContext = JNIContext::getJNIContext(jniContextHandle);
      if (jniContext == NULL){
         return 0;
      }

      cl_int status = CL_SUCCESS;

      jniContext->program = CLHelper::compile(jenv, jniContext->context,  1, &jniContext->deviceId, source, NULL, &status);

      if(status == CL_BUILD_PROGRAM_FAILURE) {
         return(0);
      }

      jniContext->kernel = clCreateKernel(jniContext->program, "run", &status);
      ASSERT_CL("clCreateKernel()");

      cl_command_queue_properties queue_props = 0;
      if (config->isProfilingEnabled()) {
         queue_props |= CL_QUEUE_PROFILING_ENABLE;
      }

      jniContext->commandQueue= clCreateCommandQueue(jniContext->context, (cl_device_id)jniContext->deviceId,
            queue_props,
            &status);
      ASSERT_CL("clCreateCommandQueue()");

      commandQueueList.add(jniContext->commandQueue, __LINE__, __FILE__);

      if (config->isProfilingCSVEnabled()) {
         // compute profile filename
#if defined (_WIN32)
         jint pid = GetCurrentProcessId();
#else
         pid_t pid = getpid();
#endif
         // indicate cpu or gpu
         // timestamp
         // kernel name

         jclass classMethodAccess = jenv->FindClass("java/lang/Class"); 
         jmethodID getNameID=jenv->GetMethodID(classMethodAccess,"getName","()Ljava/lang/String;");
         jstring className = (jstring)jenv->CallObjectMethod(jniContext->kernelClass, getNameID);
         const char *classNameChars = jenv->GetStringUTFChars(className, NULL);

#define TIME_STR_LEN 200

         char timeStr[TIME_STR_LEN];
         struct tm *tmp;
         time_t t = time(NULL);
         tmp = localtime(&t);
         if (tmp == NULL) {
            perror("localtime");
         }
         //strftime(timeStr, TIME_STR_LEN, "%F.%H%M%S", tmp);  %F seemed to cause a core dump
         strftime(timeStr, TIME_STR_LEN, "%H%M%S", tmp);

         char* fnameStr = new char[strlen(classNameChars) + strlen(timeStr) + 128];

         //sprintf(fnameStr, "%s.%s.%d.%llx\n", classNameChars, timeStr, pid, jniContext);
         sprintf(fnameStr, "aparapiprof.%s.%d.%016lx", timeStr, pid, (unsigned long)jniContext);
         jenv->ReleaseStringUTFChars(className, classNameChars);

         FILE* profileFile = fopen(fnameStr, "w");
         if (profileFile != NULL) {
            jniContext->profileFile = profileFile;
         } else {
            jniContext->profileFile = stderr;
            fprintf(stderr, "Could not open profile data file %s, reverting to stderr\n", fnameStr);
         }
         delete []fnameStr;
      }

      return((jlong)jniContext);
   }


// this is called once when the arg list is first determined for this kernel
JNI_JAVA(jint, KernelRunner, setArgsJNI)
   (JNIEnv *jenv, jobject jobj, jlong jniContextHandle, jobjectArray argArray, jint argc) {
      if (config== NULL){
         config = new Config(jenv);
      }
      JNIContext* jniContext = JNIContext::getJNIContext(jniContextHandle);
      cl_int status = CL_SUCCESS;
      if (jniContext != NULL){      
         jniContext->argc = argc;
         jniContext->args = new KernelArg*[jniContext->argc];
         jniContext->firstRun = true;

         // Step through the array of KernelArg's to capture the type data for the Kernel's data members.
         for (jint i=0; i<jniContext->argc; i++){ 
            jobject argObj = jenv->GetObjectArrayElement(argArray, i);
            KernelArg* arg = jniContext->args[i] = new KernelArg(jenv, jniContext, argObj);
            if (config->isVerbose()){
               if (arg->isExplicit()){
                  fprintf(stderr, "%s is explicit!\n", arg->name);
               }
            }

            if (config->isVerbose()){
               fprintf(stderr, "in setArgs arg %d %s type %08x\n", i, arg->name, arg->type);
               if (arg->isLocal()){
                  fprintf(stderr, "in setArgs arg %d %s is local\n", i, arg->name);
               }else if (arg->isConstant()){
                  fprintf(stderr, "in setArgs arg %d %s is constant\n", i, arg->name);
               }else{
                  fprintf(stderr, "in setArgs arg %d %s is *not* local\n", i, arg->name);
               }
            }

            //If an error occurred, return early so we report the first problem, not the last
            if (jenv->ExceptionCheck() == JNI_TRUE) {
               jniContext->argc = -1;
               delete[] jniContext->args;
               jniContext->args = NULL;
               jniContext->firstRun = true;
               return (status);
            }

         }
         // we will need an executeEvent buffer for all devices
         jniContext->executeEvents = new cl_event[1];

         // We will need *at most* jniContext->argc read/write events
         jniContext->readEvents = new cl_event[jniContext->argc];
         if (config->isProfilingEnabled()) {
            jniContext->readEventArgs = new jint[jniContext->argc];
         }
         jniContext->writeEvents = new cl_event[jniContext->argc];
         if (config->isProfilingEnabled()) {
            jniContext->writeEventArgs = new jint[jniContext->argc];
         }
      }
      return(status);
   }



JNI_JAVA(jstring, KernelRunner, getExtensionsJNI)
   (JNIEnv *jenv, jobject jobj, jlong jniContextHandle) {
      if (config== NULL){
         config = new Config(jenv);
      }
      jstring jextensions = NULL;
      JNIContext* jniContext = JNIContext::getJNIContext(jniContextHandle);
      if (jniContext != NULL){
         cl_int status = CL_SUCCESS;
         jextensions = CLHelper::getExtensions(jenv, jniContext->deviceId, &status);
      }
      return jextensions;
   }

KernelArg* getArgForBuffer(JNIEnv* jenv, JNIContext* jniContext, jobject buffer) {
   cl_int status = CL_SUCCESS;
   KernelArg *returnArg= NULL;

   if (jniContext != NULL){
      for (jint i=0; returnArg == NULL && i<jniContext->argc; i++){ 
         KernelArg *arg= jniContext->args[i];
         if (arg->isArray()){
            jboolean isSame = jenv->IsSameObject(buffer, arg->arrayBuffer->javaArray);
            if (isSame){
               if (config->isVerbose()){
                  fprintf(stderr, "matched arg '%s'\n", arg->name);
               }
               returnArg = arg;
            }else{
               if (config->isVerbose()){
                  fprintf(stderr, "unmatched arg '%s'\n", arg->name);
               }
            }
         }
      }
      if (returnArg==NULL){
         if (config->isVerbose()){
            fprintf(stderr, "attempt to get arg for buffer that does not appear to be referenced from kernel\n");
         }
      }
   }
   return returnArg;
}

// Called as a result of Kernel.get(someArray)
JNI_JAVA(jint, KernelRunner, getJNI)
   (JNIEnv *jenv, jobject jobj, jlong jniContextHandle, jobject buffer) {
      if (config== NULL){
         config = new Config(jenv);
      }
      cl_int status = CL_SUCCESS;
      JNIContext* jniContext = JNIContext::getJNIContext(jniContextHandle);
      if (jniContext != NULL){
         KernelArg *arg= getArgForBuffer(jenv, jniContext, buffer);
         if (arg != NULL){
            if (config->isVerbose()){
               fprintf(stderr, "explicitly reading buffer %s\n", arg->name);
            }
            arg->pin(jenv);

            status = clEnqueueReadBuffer(jniContext->commandQueue, arg->arrayBuffer->mem, CL_FALSE, 0, 
                  arg->arrayBuffer->lengthInBytes,arg->arrayBuffer->addr , 0, NULL, &jniContext->readEvents[0]);
            if (config->isVerbose()){
               fprintf(stderr, "explicitly read %s ptr=%lx len=%d\n", arg->name, arg->arrayBuffer->addr,arg->arrayBuffer->lengthInBytes );
            }
            if (status != CL_SUCCESS) {
               PRINT_CL_ERR(status, "clEnqueueReadBuffer()");
               return status;
            }
            status = clWaitForEvents(1, jniContext->readEvents);
            if (status != CL_SUCCESS) {
               PRINT_CL_ERR(status, "clWaitForEvents");
               return status;
            }
            if (config->isProfilingEnabled()){
               status = profile(&arg->arrayBuffer->read, &jniContext->readEvents[0], 0,arg->name, jniContext->profileBaseTime);
               if (status != CL_SUCCESS) {
                  PRINT_CL_ERR(status, "profile ");
                  return status;
               }
            }

            clReleaseEvent(jniContext->readEvents[0]);
            if (status != CL_SUCCESS) {
               PRINT_CL_ERR(status, "clReleaseEvent() read event");
               return status;
            }
            // since this is an explicit buffer get, we expect the buffer to have changed so we commit
            arg->unpin(jenv); // was unpinCommit

         }else{
            if (config->isVerbose()){
               fprintf(stderr, "attempt to request to get a buffer that does not appear to be referenced from kernel\n");
            }
         }
      }
      return 0;
   }

JNI_JAVA(jobject, KernelRunner, getProfileInfoJNI)
   (JNIEnv *jenv, jobject jobj, jlong jniContextHandle) {
      if (config== NULL){
         config = new Config(jenv);
      }
      cl_int status = CL_SUCCESS;
      JNIContext* jniContext = JNIContext::getJNIContext(jniContextHandle);
      jobject returnList = NULL;
      if (jniContext != NULL){
         returnList = JNIHelper::createInstance(jenv, ArrayListClass, VoidReturn );
         if (config->isProfilingEnabled()){

            for (jint i=0; i<jniContext->argc; i++){ 
               KernelArg *arg= jniContext->args[i];
               if (arg->isArray()){
                  if (arg->isMutableByKernel() && arg->arrayBuffer->write.valid){
                     jobject writeProfileInfo = arg->arrayBuffer->write.createProfileInfoInstance(jenv);
                     JNIHelper::callVoid(jenv, returnList, "add", ArgsBooleanReturn(ObjectClassArg), writeProfileInfo);
                  }
               }
            }

            for (jint pass=0; pass<jniContext->passes; pass++){
               jobject executeProfileInfo = jniContext->exec[pass].createProfileInfoInstance(jenv);
               JNIHelper::callVoid(jenv, returnList, "add", ArgsBooleanReturn(ObjectClassArg), executeProfileInfo);
            }

            for (jint i=0; i<jniContext->argc; i++){ 
               KernelArg *arg= jniContext->args[i];
               if (arg->isArray()){
                  if (arg->isReadByKernel() && arg->arrayBuffer->read.valid){
                     jobject readProfileInfo = arg->arrayBuffer->read.createProfileInfoInstance(jenv);
                     JNIHelper::callVoid(jenv, returnList, "add", ArgsBooleanReturn(ObjectClassArg), readProfileInfo);
                  }
               }
            }
         }
      }
      return returnList;
   }
