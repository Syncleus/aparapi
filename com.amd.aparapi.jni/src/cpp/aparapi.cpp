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
#include "jniHelper.h"
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

class ProfileInfo{
   public:
      jboolean valid;
      jint type; //0 write, 1 execute, 2 read
      char *name;
      cl_ulong queued;
      cl_ulong submit;
      cl_ulong start;
      cl_ulong end;
};


class KernelArgRef{
   public:
      jobject javaArray;        // The java array or direct buffer that this arg is mapped to 
      bool  isArray;            // true if above is an array
      cl_uint javaArrayLength;  // the number of elements for arrays (used only when ARRAYLENGTH bit is set for this arg)
      cl_mem mem;               // the opencl buffer 
      void *addr;               // we use this temporarily whilst we pin the primitive array
      cl_uint memMask;          // the mask we used for createBuffer
      jboolean isCopy;
      jboolean isPinned;
      char memSpec[128];       // The string form of the mask we used for create buffer. for debugging
      ProfileInfo read;
      ProfileInfo write;
};
class JNIContext ; // forward reference

class KernelArg{
   private:
      static jclass argClazz;
      static jfieldID nameFieldID;
      static jfieldID typeFieldID; 
      static jfieldID isStaticFieldID; 
      static jfieldID sizeInBytesFieldID;
      static jfieldID numElementsFieldID; 
   public:
      static jfieldID javaArrayFieldID; 
      jobject argObj;
      char *name;        // used for debugging printfs
      jfieldID fieldID;  // The field that this arg represents in the kernel (java), used only for primitive updates
      jint type;         // a bit mask determining the type of this arg
      jboolean isStatic; // A flag indicating if the value is static
      jint sizeInBytes;  // bytes in the array or directBuf
      jobject javaArg;   // global reference to the corresponding java KernelArg object
      union{
         cl_char c;
         cl_double d;
         cl_float f;
         cl_int i;
         cl_long j;
         KernelArgRef ref;
      } value;

      KernelArg(JNIEnv *jenv, jobject argObj):
         argObj(argObj){
            javaArg = jenv->NewGlobalRef(argObj);   // save a global ref to the java Arg Object
            if (argClazz == 0){
               jclass c = jenv->GetObjectClass(argObj); 
               nameFieldID = jenv->GetFieldID(c, "name", "Ljava/lang/String;"); ASSERT_FIELD(name);
               typeFieldID = jenv->GetFieldID(c, "type", "I"); ASSERT_FIELD(type);
               isStaticFieldID = jenv->GetFieldID(c, "isStatic", "Z"); ASSERT_FIELD(isStatic);
               javaArrayFieldID = jenv->GetFieldID(c, "javaArray", "Ljava/lang/Object;"); ASSERT_FIELD(javaArray);
               sizeInBytesFieldID = jenv->GetFieldID(c, "sizeInBytes", "I"); ASSERT_FIELD(sizeInBytes);
               numElementsFieldID = jenv->GetFieldID(c, "numElements", "I"); ASSERT_FIELD(numElements);
            }
            type = jenv->GetIntField(argObj, typeFieldID);
            isStatic = jenv->GetBooleanField(argObj, isStaticFieldID);
            jstring nameString  = (jstring)jenv->GetObjectField(argObj, nameFieldID);
            const char *nameChars = jenv->GetStringUTFChars(nameString, NULL);
#ifdef _WIN32
            name=_strdup(nameChars);
#else
            name=strdup(nameChars);
#endif
            jenv->ReleaseStringUTFChars(nameString, nameChars);
         }

      ~KernelArg(){
      }

      void unpinAbort(JNIEnv *jenv){
         jenv->ReleasePrimitiveArrayCritical((jarray)value.ref.javaArray, value.ref.addr,JNI_ABORT);
      }
      void unpinCommit(JNIEnv *jenv){
         jenv->ReleasePrimitiveArrayCritical((jarray)value.ref.javaArray, value.ref.addr, 0);
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
         value.ref.isPinned = JNI_FALSE;
      }
      void pin(JNIEnv *jenv){
		 //if  (value.ref.isPinned == JNI_TRUE){			 
       //     fprintf(stdout, "why are we pinning buffer %s! isPinned = JNI_TRUE\n", name);
		// }
		 void *ptr = value.ref.addr;
         value.ref.addr = jenv->GetPrimitiveArrayCritical((jarray)value.ref.javaArray,&value.ref.isCopy);
		 //if (ptr != value.ref.addr){
       //     fprintf(stdout, "buffer %s has moved!\n", name);
		// }
       //  if (value.ref.isCopy){
       //     fprintf(stderr, "buffer %s is a copy!\n", name);
       //  }
         value.ref.isPinned = JNI_TRUE;
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
      int isConstant(){
         return (type&com_amd_aparapi_KernelRunner_ARG_CONSTANT);
      }
      int isAparapiBuf(){
         return (type&com_amd_aparapi_KernelRunner_ARG_APARAPI_BUF);
      }
      //int isAparapiBufHasArray(){
      //   return (type&com_amd_aparapi_KernelRunner_ARG_APARAPI_BUF_HAS_ARRAY);
      //}
      int isBackedByArray(){
         return ( (isArray() && (isGlobal() || isConstant())));
      }
      int needToEnqueueRead(){
         return(((isArray() && isGlobal()) || ((isAparapiBuf()&&isGlobal()))) && (isImplicit()&&isMutableByKernel()));
      }
      int needToEnqueueWrite(){
		//  if (isExplicitWrite()){
		//	  fprintf(stderr, "%s isExplicitWrite()\n", name);
		//  }
		 //  if (isExplicit()){
			//  fprintf(stderr, "%s isExplicit()\n", name);
		//  }
		//  fprintf(stderr, "%s neetToEnqueueWrite = %d\n", name, returnValue);
         return ((isImplicit()&&isReadByKernel())||(isExplicit()&&isExplicitWrite()));
      }
      void syncType(JNIEnv* jenv){
         type = jenv->GetIntField(javaArg, typeFieldID);
      }
      void syncSizeInBytes(JNIEnv* jenv){
         sizeInBytes = jenv->GetIntField(javaArg, sizeInBytesFieldID);
      }
      void syncJavaArrayLength(JNIEnv* jenv){
         value.ref.javaArrayLength = jenv->GetIntField(javaArg, numElementsFieldID);
      }
      void clearExplicitBufferBit(JNIEnv* jenv){
         type &= ~com_amd_aparapi_KernelRunner_ARG_EXPLICIT_WRITE;
         jenv->SetIntField(javaArg, typeFieldID,type );
      }
};
jclass KernelArg::argClazz=(jclass)0;
jfieldID KernelArg::nameFieldID=0;
jfieldID KernelArg::typeFieldID=0; 
jfieldID KernelArg::isStaticFieldID=0; 
jfieldID KernelArg::javaArrayFieldID=0; 
jfieldID KernelArg::sizeInBytesFieldID=0;
jfieldID KernelArg::numElementsFieldID=0; 

template <class T> class List; // forward

template <class T> class Handle{
   private:
       T value;
       int line;
       Handle<T> *next;
       friend class List<T>;
   public:
       Handle(T _value, int _line): value(_value), line(_line),next(NULL){
       }
};

template <class T> class List{
   private:
       char *name;
       Handle<T> *head;
       int count;
   public:
       List(char *_name): head(NULL), count(0), name(_name){
       }
       void add(T _value, int _line){
          Handle<T> *handle = new Handle<T>(_value, _line);
          handle->next = head; 
          head = handle;
          count++;
          //fprintf(stdout, "LINE %d added %s %0lx\n", _line, name, _value);
       }
       void remove(T _value, int _line){
          for (Handle<T> *ptr = head, *last=NULL; ptr != NULL; last=ptr, ptr = ptr->next){
             if (ptr->value == _value){
                if (last == NULL){ // head 
                   head = ptr->next;
                }else{ // !head
                   last->next = ptr->next;
                }
                delete ptr;
                count--;
                //fprintf(stdout, "LINE %d removed %s %0lx\n", _line, name, _value);
                return;
             }
          }
          fprintf(stderr, "LINE %d failed to find %s to remove %0lx\n", _line, name, _value);
       }
       void report(FILE *stream){
          if (head != NULL){
             fprintf(stream, "Resource report %d resources of type %s still in play ", count, name);
             for (Handle<T> *ptr = head; ptr != NULL; ptr = ptr->next){
                fprintf(stream, " %0lx(%d)", ptr->value, ptr->line);
             }
             fprintf(stream, "\n");
          }
       }
};

List<cl_command_queue> commandQueueList("cl_command_queue");
List<cl_mem> memList("cl_mem");
List<cl_event> readEventList("cl_event (read)");
List<cl_event> executeEventList("cl_event (exec)");
List<cl_event> writeEventList("cl_event (write)");

class JNIContext{
   private: 
      jint flags;
      jboolean valid;
      //cl_platform_id platform;
      //cl_platform_id* platforms;
      //cl_uint platformc;
   public:
      jobject kernelObject;
      jobject openCLDeviceObject;
      jclass kernelClass;
      //cl_uint deviceIdc;
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
      // these map to camelCase form of CL_DEVICE_XXX_XXX  For example CL_DEVICE_MAX_COMPUTE_UNITS == maxComputeUnits
     // cl_uint maxComputeUnits;
     // cl_uint maxWorkItemDimensions;
     // size_t *maxWorkItemSizes;
     // size_t maxWorkGroupSize;
     // cl_ulong globalMemSize;
     // cl_ulong localMemSize;

      static JNIContext* getJNIContext(jlong jniContextHandle){
         return((JNIContext*)jniContextHandle);
      }

      JNIContext(JNIEnv *jenv, jobject _kernelObject, jobject _openCLDeviceObject, jint _flags): 
         kernelObject(jenv->NewGlobalRef(_kernelObject)),
         kernelClass((jclass)jenv->NewGlobalRef(jenv->GetObjectClass(_kernelObject))), 
         openCLDeviceObject(jenv->NewGlobalRef(_openCLDeviceObject)),
         flags(_flags),
       //  platform(NULL),
         profileBaseTime(0),
         passes(0),
         exec(NULL),
         deviceType(((flags&com_amd_aparapi_KernelRunner_JNI_FLAG_USE_GPU)==com_amd_aparapi_KernelRunner_JNI_FLAG_USE_GPU)?CL_DEVICE_TYPE_GPU:CL_DEVICE_TYPE_CPU),
         profileFile(NULL), 
         valid(JNI_FALSE){
            cl_int status = CL_SUCCESS;
            // The device is passed to us.  So just extract the device Id, platform Id etc and create a context.
            //
            jobject platformInstance = OpenCLDevice::getPlatformInstance(jenv, openCLDeviceObject);
            cl_platform_id platformId = OpenCLPlatform::getPlatformId(jenv, platformInstance);
            deviceId = OpenCLDevice::getDeviceId(jenv, openCLDeviceObject);
            cl_device_type deviceType;
            clGetDeviceInfo(deviceId, CL_DEVICE_TYPE,  sizeof(deviceType), &deviceType, NULL);
      //fprintf(stderr, "device[%d] CL_DEVICE_TYPE = %x\n", deviceId, deviceType);


      cl_context_properties cps[3] = { CL_CONTEXT_PLATFORM, (cl_context_properties)platformId, 0 };
      cl_context_properties* cprops = (NULL == platformId) ? NULL : cps;
      context = clCreateContextFromType( cprops, deviceType, NULL, NULL, &status); 

            ASSERT_CL_NO_RETURN("clCreateContextFromType()");
            if (status == CL_SUCCESS){
               valid = JNI_TRUE;
            }
         }

jboolean isValid(){
   return(valid);
}
jboolean isProfilingCSVEnabled(){
   return((flags&com_amd_aparapi_KernelRunner_JNI_FLAG_ENABLE_PROFILING_CSV)==com_amd_aparapi_KernelRunner_JNI_FLAG_ENABLE_PROFILING_CSV?JNI_TRUE:JNI_FALSE);
}
jboolean isTrackingOpenCLResources(){
   return((flags&com_amd_aparapi_KernelRunner_JNI_FLAG_ENABLE_VERBOSE_JNI_OPENCL_RESOURCE_TRACKING)==com_amd_aparapi_KernelRunner_JNI_FLAG_ENABLE_VERBOSE_JNI_OPENCL_RESOURCE_TRACKING?JNI_TRUE:JNI_FALSE);
}
jboolean isProfilingEnabled(){
   return((flags&com_amd_aparapi_KernelRunner_JNI_FLAG_ENABLE_PROFILING)==com_amd_aparapi_KernelRunner_JNI_FLAG_ENABLE_PROFILING?JNI_TRUE:JNI_FALSE);
}
jboolean isUsingGPU(){
   return((flags&com_amd_aparapi_KernelRunner_JNI_FLAG_USE_GPU)==com_amd_aparapi_KernelRunner_JNI_FLAG_USE_GPU?JNI_TRUE:JNI_FALSE);
}
jboolean isVerbose(){
   return((flags&com_amd_aparapi_KernelRunner_JNI_FLAG_ENABLE_VERBOSE_JNI)==com_amd_aparapi_KernelRunner_JNI_FLAG_ENABLE_VERBOSE_JNI?JNI_TRUE:JNI_FALSE);
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
      if (isTrackingOpenCLResources()){
          commandQueueList.remove((cl_command_queue)commandQueue, __LINE__);
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
            if (arg->value.ref.mem != 0){
               if (isTrackingOpenCLResources()){
                  memList.remove((cl_mem)arg->value.ref.mem, __LINE__);
               }
               status = clReleaseMemObject((cl_mem)arg->value.ref.mem);
               //fprintf(stdout, "dispose arg %d %0lx\n", i, arg->value.ref.mem);
               ASSERT_CL_NO_RETURN("clReleaseMemObject()");
               arg->value.ref.mem = (cl_mem)0;
            }
            if (arg->value.ref.javaArray != NULL)  {
               jenv->DeleteWeakGlobalRef((jweak) arg->value.ref.javaArray);
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

      if (isProfilingEnabled()) {
         if (isProfilingCSVEnabled()) {
            if (profileFile != NULL && profileFile != stderr) {
               fclose(profileFile);
            }
         }
         delete[] readEventArgs; readEventArgs=0;
         delete[] writeEventArgs; writeEventArgs=0;
      } 
   }
   if (isTrackingOpenCLResources()){
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



JNI_JAVA(jint, KernelRunner, disposeJNI)
   (JNIEnv *jenv, jobject jobj, jlong jniContextHandle) {
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
            currSampleBaseTime = arg->value.ref.write.queued;
         } 
         fprintf(jniContext->profileFile, "%d write %s,", pos++, arg->name);

         fprintf(jniContext->profileFile, "%lu,%lu,%lu,%lu,",  
               (arg->value.ref.write.queued - currSampleBaseTime)/1000, 
               (arg->value.ref.write.submit - currSampleBaseTime)/1000, 
               (arg->value.ref.write.start - currSampleBaseTime)/1000, 
               (arg->value.ref.write.end - currSampleBaseTime)/1000);
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
               currSampleBaseTime = arg->value.ref.read.queued;
            }

            fprintf(jniContext->profileFile, "%d read %s,", pos++, arg->name);

            fprintf(jniContext->profileFile, "%lu,%lu,%lu,%lu,",  
                  (arg->value.ref.read.queued - currSampleBaseTime)/1000, 
                  (arg->value.ref.read.submit - currSampleBaseTime)/1000, 
                  (arg->value.ref.read.start - currSampleBaseTime)/1000, 
                  (arg->value.ref.read.end - currSampleBaseTime)/1000);
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

         if (jniContext->isVerbose()){
            fprintf(stderr, "got type for %s: %08x\n", arg->name, arg->type);
         }
         if (!arg->isPrimitive()) {
            // Following used for all primitive arrays, object arrays and nio Buffers
            jarray newRef = (jarray)jenv->GetObjectField(arg->javaArg, KernelArg::javaArrayFieldID);
            if (jniContext->isVerbose()){
               fprintf(stderr, "testing for Resync javaArray %s: old=%p, new=%p\n", arg->name, arg->value.ref.javaArray, newRef);         
            }

            if (!jenv->IsSameObject(newRef, arg->value.ref.javaArray)) {
               if (jniContext->isVerbose()){
                  fprintf(stderr, "Resync javaArray for %s: %p  %p\n", arg->name, newRef, arg->value.ref.javaArray);         
               }
               // Free previous ref if any
               if (arg->value.ref.javaArray != NULL) {
                  jenv->DeleteWeakGlobalRef((jweak) arg->value.ref.javaArray);
                  if (jniContext->isVerbose()){
                     fprintf(stderr, "DeleteWeakGlobalRef for %s: %p\n", arg->name, arg->value.ref.javaArray);         
                  }
               }

               // need to free opencl buffers, run will reallocate later
               if (arg->value.ref.mem != 0) {
                  //fprintf(stderr, "-->releaseMemObject[%d]\n", i);
                  status = clReleaseMemObject((cl_mem)arg->value.ref.mem);
                  //fprintf(stderr, "<--releaseMemObject[%d]\n", i);
                  ASSERT_CL("clReleaseMemObject()");
                  arg->value.ref.mem = (cl_mem)0;
               }

               arg->value.ref.mem = (cl_mem) 0;
               arg->value.ref.addr = NULL;

               // Capture new array ref from the kernel arg object

               if (newRef != NULL) {
                  arg->value.ref.javaArray = (jarray)jenv->NewWeakGlobalRef((jarray)newRef);
                  if (jniContext->isVerbose()){
                     fprintf(stderr, "NewWeakGlobalRef for %s, set to %p\n", arg->name,
                           arg->value.ref.javaArray);         
                  }
               } else {
                  arg->value.ref.javaArray = NULL;
               }
               arg->value.ref.isArray = true;

               // Save the sizeInBytes which was set on the java side
               arg->syncSizeInBytes(jenv);

               if (jniContext->isVerbose()){
                  fprintf(stderr, "updateNonPrimitiveReferences, args[%d].sizeInBytes=%d\n", i, arg->sizeInBytes);
               }
            } // object has changed
         }
      } // for each arg
   } // if jniContext != NULL
   return(status);
}



JNI_JAVA(jint, KernelRunner, runKernelJNI)
   (JNIEnv *jenv, jobject jobj, jlong jniContextHandle, jobject _range, jboolean needSync, jint passes) {

   Range range(jenv, _range);

   cl_int status = CL_SUCCESS;
   JNIContext* jniContext = JNIContext::getJNIContext(jniContextHandle);


    if (jniContext->firstRun && jniContext->isProfilingEnabled()){
         cl_event firstEvent;
#ifdef CL_VERSION_1_2
         status = clEnqueueMarkerWithWaitList(jniContext->commandQueue, 0, NULL, &firstEvent);
#else
         // this was deprecated in 1.1
         status = clEnqueueMarker(jniContext->commandQueue, &firstEvent);
#endif
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
		 if (jniContext->isVerbose()){
            fprintf(stderr, "profileBaseTime %lu \n", jniContext->profileBaseTime);
		 }
      }


   // Need to capture array refs
   if (jniContext->firstRun || needSync) {
      updateNonPrimitiveReferences(jenv, jobj, jniContext );
      if (jniContext->isVerbose()){
         fprintf(stderr, "back from updateNonPrimitiveReferences\n");
      }
   }

   int writeEventCount = 0;

   // kernelArgPos is used to keep track of the kernel arg position, it can 
   // differ from "i" due to insertion of javaArrayLength args which are not
   // fields read from the kernel object.
   int kernelArgPos = 0;

   for (int i=0; i< jniContext->argc; i++){
      KernelArg *arg = jniContext->args[i];
      arg->syncType(jenv); // make sure that the JNI arg reflects the latest type info from the instance.  For example if the buffer is tagged as explicit and needs to be pushed

      if (jniContext->isVerbose()){
         fprintf(stderr, "got type for arg %d, %s, type=%08x\n", i, arg->name, arg->type);
      }
      if (!arg->isPrimitive() && !arg->isLocal()) {
         if (jniContext->isProfilingEnabled()){
            arg->value.ref.read.valid = false;
            arg->value.ref.write.valid = false;
         }
         // pin the arrays so that GC does not move them during the call

         // get the C memory address for the region being transferred
         // this uses different JNI calls for arrays vs. directBufs
         void * prevAddr =  arg->value.ref.addr;
         if (arg->value.ref.isArray) {
            arg->pin(jenv);
         }

         if (jniContext->isVerbose()){
            fprintf(stderr, "runKernel: arrayOrBuf ref %p, oldAddr=%p, newAddr=%p, ref.mem=%p, isArray=%d\n",
                  arg->value.ref.javaArray, 
                  prevAddr,
                  arg->value.ref.addr,
                  arg->value.ref.mem,
                  arg->value.ref.isArray );
            fprintf(stderr, "at memory addr %p, contents: ", arg->value.ref.addr);
            unsigned char *pb = (unsigned char *) arg->value.ref.addr;
            for (int k=0; k<8; k++) {
               fprintf(stderr, "%02x ", pb[k]);
            }
            fprintf(stderr, "\n" );
         }
         // record whether object moved 
         // if we see that isCopy was returned by getPrimitiveArrayCritical, treat that as a move
         bool objectMoved = (arg->value.ref.addr != prevAddr) || arg->value.ref.isCopy;

         if (jniContext->isVerbose()){
            if (arg->isExplicit() && arg->isExplicitWrite()){
               fprintf(stderr, "explicit write of %s\n",  arg->name);
            }
         }

         if (jniContext->firstRun || (arg->value.ref.mem == 0) || objectMoved ){
            // if either this is the first run or user changed input array
            // or gc moved something, then we create buffers/args
            cl_uint mask = CL_MEM_USE_HOST_PTR;
            if (arg->isReadByKernel() && arg->isMutableByKernel()) mask |= CL_MEM_READ_WRITE;
            else if (arg->isReadByKernel() && !arg->isMutableByKernel()) mask |= CL_MEM_READ_ONLY;
            else if (arg->isMutableByKernel()) mask |= CL_MEM_WRITE_ONLY;
            arg->value.ref.memMask = mask;
            if (jniContext->isVerbose()){
               strcpy(arg->value.ref.memSpec,"CL_MEM_USE_HOST_PTR");
               if (mask & CL_MEM_READ_WRITE) strcat(arg->value.ref.memSpec,"|CL_MEM_READ_WRITE");
               if (mask & CL_MEM_READ_ONLY) strcat(arg->value.ref.memSpec,"|CL_MEM_READ_ONLY");
               if (mask & CL_MEM_WRITE_ONLY) strcat(arg->value.ref.memSpec,"|CL_MEM_WRITE_ONLY");

               fprintf(stderr, "%s %d clCreateBuffer(context, %s, size=%08x bytes, address=%08x, &status)\n", arg->name, 
                     i, arg->value.ref.memSpec, arg->sizeInBytes, arg->value.ref.addr);
            }
            arg->value.ref.mem = clCreateBuffer(jniContext->context, arg->value.ref.memMask, 
                  arg->sizeInBytes, arg->value.ref.addr, &status);

            if (status != CL_SUCCESS) {
               PRINT_CL_ERR(status, "clCreateBuffer");
               jniContext->unpinAll(jenv);
               return status;
            }
            if (jniContext->isTrackingOpenCLResources()){
               memList.add(arg->value.ref.mem, __LINE__);
            }

            status = clSetKernelArg(jniContext->kernel, kernelArgPos++, sizeof(cl_mem), (void *)&(arg->value.ref.mem));                  
            if (status != CL_SUCCESS) {
               PRINT_CL_ERR(status, "clSetKernelArg (array)");
               jniContext->unpinAll(jenv);
               return status;
            }

            // Add the array length if needed
            if (arg->usesArrayLength()){
               arg->syncJavaArrayLength(jenv);

               status = clSetKernelArg(jniContext->kernel, kernelArgPos++, sizeof(jint), &(arg->value.ref.javaArrayLength));

               if (jniContext->isVerbose()){
                  fprintf(stderr, "runKernel arg %d %s, javaArrayLength = %d\n", i, arg->name, arg->value.ref.javaArrayLength);
               }
               if (status != CL_SUCCESS) {
                  PRINT_CL_ERR(status, "clSetKernelArg (array length)");
                  jniContext->unpinAll(jenv);
                  return status;
               }
            }
         } else {
            // Keep the arg position in sync if no updates were required
            kernelArgPos++;
            if (arg->usesArrayLength()){
               kernelArgPos++;
            }
         }

         // we only enqueue a write if we know the kernel actually reads the buffer or if there is an explicit write pending
         // the default behavior for Constant buffers is also that there is no write enqueued unless explicit

         if (arg->needToEnqueueWrite() && !arg->isConstant()){
            if (jniContext->isVerbose()){
               if (arg->isExplicit() && arg->isExplicitWrite()){
                  fprintf(stderr, "writing explicit buffer %d %s\n", i, arg->name);
               }
            }
            if (jniContext->isVerbose()){
               fprintf(stderr, "%s writing buffer %d %s\n",  (arg->isExplicit() ? "explicitly" : ""), 
                     i, arg->name);
            }
            if (jniContext->isProfilingEnabled()) {
               jniContext->writeEventArgs[writeEventCount]=i;
            }

            if (arg->isConstant()){
               fprintf(stderr, "writing constant buffer %s\n", arg->name);
            }

            status = clEnqueueWriteBuffer(jniContext->commandQueue, arg->value.ref.mem, CL_FALSE, 0, 
                  arg->sizeInBytes, arg->value.ref.addr, 0, NULL, &(jniContext->writeEvents[writeEventCount]));
            if (status != CL_SUCCESS) {
               PRINT_CL_ERR(status, "clEnqueueWriteBuffer");
               jniContext->unpinAll(jenv);
               return status;
            }
            if (jniContext->isTrackingOpenCLResources()){
               writeEventList.add(jniContext->writeEvents[writeEventCount],__LINE__);
            }
            writeEventCount++;
            if (arg->isExplicit() && arg->isExplicitWrite()){
               if (jniContext->isVerbose()){
                  fprintf(stderr, "clearing explicit buffer bit %d %s\n", i, arg->name);
               }
               arg->clearExplicitBufferBit(jenv);
            }
         }
      } else if (arg->isLocal()){
         if (jniContext->firstRun){
            int bytes = arg->sizeInBytes;

            if (jniContext->isVerbose()){
               fprintf(stderr, "ISLOCAL, clSetKernelArg(jniContext->kernel, %d, %d, NULL);\n", i, bytes);
            }
            status = clSetKernelArg(jniContext->kernel, kernelArgPos++, bytes, NULL);
            if (status != CL_SUCCESS) {
               PRINT_CL_ERR(status, "clSetKernelArg() (local)");
               jniContext->unpinAll(jenv);
               return status;
            }
         } else {
            // Keep the arg position in sync if no updates were required
            kernelArgPos++;
            if (arg->usesArrayLength()){
               kernelArgPos++;
            }
         }
      }else{  // primitive arguments

         // we need to reflectively sync the value out of the kernel object
         if (arg->isFloat()){
            if (arg->isStatic){
               arg->value.f = jenv->GetStaticFloatField(jniContext->kernelClass, arg->fieldID);
            }else{
               arg->value.f = jenv->GetFloatField(jniContext->kernelObject, arg->fieldID);
            }
            //fprintf(stderr, "float arg %d\n", arg->value.f); 
         }else if (arg->isInt()){
            if (arg->isStatic){
               arg->value.i = jenv->GetStaticIntField(jniContext->kernelClass, arg->fieldID);
            }else{
               arg->value.i = jenv->GetIntField(jniContext->kernelObject, arg->fieldID);
            }
            //fprintf(stderr, "int arg %d\n", arg->value.i); 
         }else if (arg->isBoolean()){
            if (arg->isStatic){
               arg->value.c = jenv->GetStaticBooleanField(jniContext->kernelClass, arg->fieldID);
            }else{
               arg->value.c = jenv->GetBooleanField(jniContext->kernelObject, arg->fieldID);
            }
            //fprintf(stderr, "boolean arg %d\n", arg->value.c); 
         }else if (arg->isByte()){
            if (arg->isStatic){
               arg->value.c = jenv->GetStaticByteField(jniContext->kernelClass, arg->fieldID);
            }else{
               arg->value.c = jenv->GetByteField(jniContext->kernelObject, arg->fieldID);
            }
            //fprintf(stderr, "byte arg %d\n", arg->value.c); 
         }else if (arg->isLong()){
            if (arg->isStatic){
               arg->value.j = jenv->GetStaticLongField(jniContext->kernelClass, arg->fieldID);
            }else{
               arg->value.j = jenv->GetLongField(jniContext->kernelObject, arg->fieldID);
            }
            //fprintf(stderr, "long arg %d\n", arg->value.c); 
         }else if (arg->isDouble()){
            if (arg->isStatic){
               arg->value.d = jenv->GetStaticDoubleField(jniContext->kernelClass, arg->fieldID);
            }else{
               arg->value.d = jenv->GetDoubleField(jniContext->kernelObject, arg->fieldID);
            }
            //fprintf(stderr, "double arg %d\n", arg->value.c); 
         }

         if (jniContext->isVerbose()){
            fprintf(stderr, "clSetKernelArg %s: %d %d %d 0x%08x\n", arg->name, i, kernelArgPos, 
                  arg->sizeInBytes, arg->value);         
         }
         status = clSetKernelArg(jniContext->kernel, kernelArgPos++, arg->sizeInBytes, &(arg->value));
         if (status != CL_SUCCESS) {
            PRINT_CL_ERR(status, "clSetKernelArg() (value)");
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
   } 
   jniContext->passes = passes;
   jniContext->exec = new ProfileInfo[passes];

   for (int passid=0; passid<passes; passid++){
         //size_t offset = 1; // (size_t)((range.globalDims[0]/jniContext->deviceIdc)*dev);
         status = clSetKernelArg(jniContext->kernel, kernelArgPos, sizeof(passid), &(passid));
         if (status != CL_SUCCESS) {
            PRINT_CL_ERR(status, "clSetKernelArg() (passid)");
            jniContext->unpinAll(jenv);
            return status;
         }

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
            // We must capture any profile info for passid-1  so we must wait for the last execution to complete
            if (jniContext->isProfilingEnabled()) {
               if (passid ==1 ){
                  status = clWaitForEvents(1, &jniContext->executeEvents[0]);
                  if (status != CL_SUCCESS) {
                     PRINT_CL_ERR(status, "clWaitForEvents() execute event");
                     jniContext->unpinAll(jenv);
                     return status;
                  }
                //  execEventList.remove(jniContext->executeEvents[0],__LINE__);
               }
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
        // execEventList.add(jniContext->executeEvents[0],__LINE__);
      if (0){  // I dont think we need this
         if (passid < passes-1){
            // we need to wait for the executions to complete...
            status = clWaitForEvents(1,  jniContext->executeEvents);
            if (status != CL_SUCCESS) {
               PRINT_CL_ERR(status, "clWaitForEvents() execute events mid pass");
               jniContext->unpinAll(jenv);
               return status;
            }

            status = clReleaseEvent(jniContext->executeEvents[0]);
            if (status != CL_SUCCESS) {
               PRINT_CL_ERR(status, "clReleaseEvent() read event");
               jniContext->unpinAll(jenv);
               return status;
            }
         }
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
         if (jniContext->isProfilingEnabled()) {
            jniContext->readEventArgs[readEventCount]=i;
         }
         if (jniContext->isVerbose()){
            fprintf(stderr, "reading buffer %d %s\n", i, arg->name);
         }

         status = clEnqueueReadBuffer(jniContext->commandQueue, arg->value.ref.mem, CL_FALSE, 0, 
               arg->sizeInBytes,arg->value.ref.addr , 1, jniContext->executeEvents, &(jniContext->readEvents[readEventCount]));
         if (status != CL_SUCCESS) {
            PRINT_CL_ERR(status, "clEnqueueReadBuffer()");
            jniContext->unpinAll(jenv);
            return status;
         }
         if (jniContext->isTrackingOpenCLResources()){
            readEventList.add(jniContext->readEvents[readEventCount],__LINE__);
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
         if (jniContext->isProfilingEnabled()) {
            status = profile(&jniContext->args[jniContext->readEventArgs[i]]->value.ref.read, &jniContext->readEvents[i], 0,jniContext->args[jniContext->readEventArgs[i]]->name, jniContext->profileBaseTime);
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
         if (jniContext->isTrackingOpenCLResources()){
            readEventList.remove(jniContext->readEvents[i],__LINE__);
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

   if (jniContext->isProfilingEnabled()) {
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
      PRINT_CL_ERR(status, "clReleaseEvent() execute event");
      jniContext->unpinAll(jenv);
      return status;
   }

   for (int i=0; i< writeEventCount; i++){
      if (jniContext->isProfilingEnabled()) {
         profile(&jniContext->args[jniContext->writeEventArgs[i]]->value.ref.write, &jniContext->writeEvents[i], 2, jniContext->args[jniContext->writeEventArgs[i]]->name, jniContext->profileBaseTime);
      }
      status = clReleaseEvent(jniContext->writeEvents[i]);
      if (status != CL_SUCCESS) {
         PRINT_CL_ERR(status, "clReleaseEvent() write event");
         jniContext->unpinAll(jenv);
         return status;
      }
      if (jniContext->isTrackingOpenCLResources()){
         writeEventList.remove(jniContext->writeEvents[i],__LINE__);
      }
   }

   jniContext->unpinAll(jenv);

   if (jniContext->isProfilingCSVEnabled()) {
      writeProfileInfo(jniContext);
   }
   if (jniContext->isTrackingOpenCLResources()){
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
   fprintf(stdout, "init()\n");
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
   if (jniContext->isProfilingEnabled()) {
      queue_props |= CL_QUEUE_PROFILING_ENABLE;
   }

   jniContext->commandQueue= clCreateCommandQueue(jniContext->context, (cl_device_id)jniContext->deviceId,
       queue_props,
       &status);
   ASSERT_CL("clCreateCommandQueue()");

   commandQueueList.add(jniContext->commandQueue, __LINE__);

   if (jniContext->isProfilingCSVEnabled()) {
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
   JNIContext* jniContext = JNIContext::getJNIContext(jniContextHandle);
   cl_int status = CL_SUCCESS;
   if (jniContext != NULL){      
      jniContext->argc = argc;
      jniContext->args = new KernelArg*[jniContext->argc];
      jniContext->firstRun = true;

      // Step through the array of KernelArg's to capture the type data for the Kernel's data members.
      for (jint i=0; i<jniContext->argc; i++){ 


         jobject argObj = jenv->GetObjectArrayElement(argArray, i);
         KernelArg* arg = jniContext->args[i] = new KernelArg(jenv, argObj);
         if (jniContext->isVerbose()){
            if (arg->isExplicit()){
               fprintf(stderr, "%s is explicit!\n", arg->name);
            }
         }

         if (arg->isPrimitive()) {
            // for primitives, we cache the fieldID for that field in the kernel's arg object
            if (arg->isFloat()){
               if (arg->isStatic){
                  arg->fieldID = jenv->GetStaticFieldID(jniContext->kernelClass, arg->name, "F");
               }else{
                  arg->fieldID = jenv->GetFieldID(jniContext->kernelClass, arg->name, "F");
               }
               arg->sizeInBytes = sizeof(jfloat);
            }else if (arg->isInt()){
               if (arg->isStatic){
                  arg->fieldID = jenv->GetStaticFieldID(jniContext->kernelClass, arg->name, "I");
               }else{
                  arg->fieldID = jenv->GetFieldID(jniContext->kernelClass, arg->name, "I");
               }
               arg->sizeInBytes = sizeof(jint);
            }else if (arg->isByte()){
               if (arg->isStatic){
                  arg->fieldID = jenv->GetStaticFieldID(jniContext->kernelClass, arg->name, "B");
               }else{
                  arg->fieldID = jenv->GetFieldID(jniContext->kernelClass, arg->name, "B");
               }
               arg->sizeInBytes = sizeof(jbyte);
            }else if (arg->isBoolean()){
               if (arg->isStatic){
                  arg->fieldID = jenv->GetStaticFieldID(jniContext->kernelClass, arg->name, "Z");
               }else{
                  arg->fieldID = jenv->GetFieldID(jniContext->kernelClass, arg->name, "Z");
               }
               arg->sizeInBytes = sizeof(jboolean);
            }else if (arg->isLong()){
               if (arg->isStatic){
                  arg->fieldID = jenv->GetStaticFieldID(jniContext->kernelClass, arg->name, "J");
               }else{
                  arg->fieldID = jenv->GetFieldID(jniContext->kernelClass, arg->name, "J");
               }
               arg->sizeInBytes = sizeof(jlong);
            }else if (arg->isDouble()){
               if (arg->isStatic){
                  arg->fieldID = jenv->GetStaticFieldID(jniContext->kernelClass, arg->name, "D");
               }else{
                  arg->fieldID = jenv->GetFieldID(jniContext->kernelClass, arg->name, "D");
               }
               arg->sizeInBytes = sizeof(jdouble);
            }
         }else{ // we will use an array
            arg->value.ref.mem = (cl_mem) 0;
            arg->value.ref.javaArray = 0;
            arg->sizeInBytes = 0;
         }
         if (jniContext->isVerbose()){
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
      if (jniContext->isProfilingEnabled()) {
         jniContext->readEventArgs = new jint[jniContext->argc];
      }
      jniContext->writeEvents = new cl_event[jniContext->argc];
      if (jniContext->isProfilingEnabled()) {
         jniContext->writeEventArgs = new jint[jniContext->argc];
      }
   }
   return(status);
}



JNI_JAVA(jstring, KernelRunner, getExtensionsJNI)
    (JNIEnv *jenv, jobject jobj, jlong jniContextHandle) {
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
            jboolean isSame = jenv->IsSameObject(buffer, arg->value.ref.javaArray);
            if (isSame){
		       if (jniContext->isVerbose()){
                  fprintf(stderr, "matched arg '%s'\n", arg->name);
               }
               returnArg = arg;
            }else{
				 if (jniContext->isVerbose()){
                  fprintf(stderr, "unmatched arg '%s'\n", arg->name);
               }
			}
         }
      }
      if (returnArg==NULL){
         if (jniContext->isVerbose()){
            fprintf(stderr, "attempt to get arg for buffer that does not appear to be referenced from kernel\n");
         }
      }
   }
   return returnArg;
}

// Called as a result of Kernel.get(someArray)
JNI_JAVA(jint, KernelRunner, getJNI)
   (JNIEnv *jenv, jobject jobj, jlong jniContextHandle, jobject buffer) {
   cl_int status = CL_SUCCESS;
   JNIContext* jniContext = JNIContext::getJNIContext(jniContextHandle);
   if (jniContext != NULL){
      KernelArg *arg= getArgForBuffer(jenv, jniContext, buffer);
      if (arg != NULL){
         if (jniContext->isVerbose()){
           fprintf(stderr, "explicitly reading buffer %s\n", arg->name);
         }
         arg->pin(jenv);

         status = clEnqueueReadBuffer(jniContext->commandQueue, arg->value.ref.mem, CL_FALSE, 0, 
               arg->sizeInBytes,arg->value.ref.addr , 0, NULL, &jniContext->readEvents[0]);
		 if (jniContext->isVerbose()){
		    fprintf(stderr, "explicitly read %s ptr=%lx len=%d\n", arg->name, arg->value.ref.addr,arg->sizeInBytes );
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
         if (jniContext->isProfilingEnabled()){
            status = profile(&arg->value.ref.read, &jniContext->readEvents[0], 0,arg->name, jniContext->profileBaseTime);
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
         if (jniContext->isVerbose()){
            fprintf(stderr, "attempt to request to get a buffer that does not appear to be referenced from kernel\n");
         }
      }
   }
   return 0;
}


jobject createProfileInfo(JNIEnv *jenv, ProfileInfo &profileInfo){
   jobject profileInstance = JNIHelper::createInstance(jenv, ProfileInfoClass , ArgsVoidReturn(StringClassArg IntArg LongArg LongArg LongArg LongArg), 
         ((jstring)(profileInfo.name==NULL?NULL:jenv->NewStringUTF(profileInfo.name))),
         ((jint)profileInfo.type), 
         ((jlong)profileInfo.start),
         ((jlong)profileInfo.end),
         ((jlong)profileInfo.queued),
         ((jlong)profileInfo.submit));
   return(profileInstance);
}

JNI_JAVA(jobject, KernelRunner, getProfileInfoJNI)
   (JNIEnv *jenv, jobject jobj, jlong jniContextHandle) {
   cl_int status = CL_SUCCESS;
   JNIContext* jniContext = JNIContext::getJNIContext(jniContextHandle);
   jobject returnList = NULL;
   if (jniContext != NULL){
      returnList = JNIHelper::createInstance(jenv, ArrayListClass, VoidReturn );
      if (jniContext->isProfilingEnabled()){

         for (jint i=0; i<jniContext->argc; i++){ 
            KernelArg *arg= jniContext->args[i];
            if (arg->isArray()){
               if (arg->isMutableByKernel() && arg->value.ref.write.valid){
                  jobject writeProfileInfo = createProfileInfo(jenv,  arg->value.ref.write);
                  JNIHelper::callVoid(jenv, returnList, "add", ArgsBooleanReturn(ObjectClassArg), writeProfileInfo);
               }
            }
         }

         for (jint pass=0; pass<jniContext->passes; pass++){
            jobject executeProfileInfo = createProfileInfo(jenv, jniContext->exec[pass]);
            JNIHelper::callVoid(jenv, returnList, "add", ArgsBooleanReturn(ObjectClassArg), executeProfileInfo);
         }

         for (jint i=0; i<jniContext->argc; i++){ 
            KernelArg *arg= jniContext->args[i];
            if (arg->isArray()){
               if (arg->isReadByKernel() && arg->value.ref.read.valid){
                  jobject readProfileInfo = createProfileInfo(jenv,  arg->value.ref.read);
                  JNIHelper::callVoid(jenv, returnList, "add", ArgsBooleanReturn(ObjectClassArg), readProfileInfo);
               }
            }
         }
      }
   }
   return returnList;
}


/*
JNI_JAVA(jint, KernelRunner, getMaxComputeUnitsJNI)
   (JNIEnv *jenv, jobject jobj, jlong jniContextHandle) {
   cl_int status = CL_SUCCESS;
   JNIContext* jniContext = JNIContext::getJNIContext(jniContextHandle);
   if (jniContext != NULL){
      return(jniContext->maxComputeUnits);
   }else{
      return(0);
   }
}

JNI_JAVA(jint, KernelRunner, getMaxWorkItemDimensionsJNI)
   (JNIEnv *jenv, jobject jobj, jlong jniContextHandle) {
   cl_int status = CL_SUCCESS;
   JNIContext* jniContext = JNIContext::getJNIContext(jniContextHandle);
   if (jniContext != NULL){
      return(jniContext->maxWorkItemDimensions);
   }else{
      return(0);
   }
}

JNI_JAVA(jint, KernelRunner, getMaxWorkGroupSizeJNI)
   (JNIEnv *jenv, jobject jobj, jlong jniContextHandle) {
   cl_int status = CL_SUCCESS;
   JNIContext* jniContext = JNIContext::getJNIContext(jniContextHandle);
   if (jniContext != NULL){
      return(jniContext->maxWorkGroupSize);
   }else{
      return(0);
   }
}

JNI_JAVA(jint, KernelRunner, getMaxWorkItemSizeJNI)
   (JNIEnv *jenv, jobject jobj, jlong jniContextHandle, jint _index) {
   cl_int status = CL_SUCCESS;
   JNIContext* jniContext = JNIContext::getJNIContext(jniContextHandle);
   if (jniContext != NULL && _index >=0 && _index <= (int)(jniContext->maxWorkItemDimensions)){
      return(jniContext->maxWorkItemSizes[_index]);
   }else{
      return(0);
   }
}
*/
