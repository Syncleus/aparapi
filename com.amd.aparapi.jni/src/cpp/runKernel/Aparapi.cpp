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

#define APARAPI_SOURCE

//this is a workaround for windows machines since <windows.h> defines min/max that break code.
#define NOMINMAX

#include "Aparapi.h"
#include "Config.h"
#include "ProfileInfo.h"
#include "ArrayBuffer.h"
#include "CLHelper.h"
#include "List.h"
#include <algorithm>


//compiler dependant code
/**
 * calls either clEnqueueMarker or clEnqueueMarkerWithWaitList depending on the version of OpenCL installed.
 * conveiniece function so we don't have to have #ifdefs all over the code
 */
int enqueueMarker(cl_command_queue commandQueue, cl_event* firstEvent) {
#ifdef CL_VERSION_1_2
   return clEnqueueMarkerWithWaitList(commandQueue, 0, NULL, firstEvent);
#else
   // this was deprecated in 1.1
   return clEnqueueMarker(commandQueue, firstEvent);
#endif
}

/**
 * calls either GetCurrentProcessId or getpid depending on if we're on WIN32 or any other system
 * conveiniece function so we don't have to have #ifdefs all over the code
 */
jint getProcess() {
#if defined (_WIN32)
   return GetCurrentProcessId();
#else
   return (jint)getpid();
#endif
}


JNI_JAVA(jint, KernelRunnerJNI, disposeJNI)
   (JNIEnv *jenv, jobject jobj, jlong jniContextHandle) {
      if (config== NULL){
         config = new Config(jenv);
      }
      cl_int status = CL_SUCCESS;
      JNIContext* jniContext = JNIContext::getJNIContext(jniContextHandle);
      if (jniContext != NULL){
         jniContext->dispose(jenv, config);
         delete jniContext;
         jniContext = NULL;
      }
      return(status);
   }

/*
void idump(const char *str, void *ptr, int size){
   int * iptr = (int *)ptr;
   for (unsigned i=0; i<size/sizeof(int); i++){
      fprintf(stderr, "%s%4d %d\n", str, i, iptr[i]);
   }
}

void fdump(const char *str, void *ptr, int size){
   float * fptr = (float *)ptr;
   for (unsigned i=0; i<size/sizeof(float); i++){
      fprintf(stderr, "%s%4d %6.2f\n", str, i, fptr[i]);
   }
}
*/


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
        	(unsigned long)(arg->arrayBuffer->write.queued - currSampleBaseTime)/1000,
        	(unsigned long)(arg->arrayBuffer->write.submit - currSampleBaseTime)/1000,
        	(unsigned long)(arg->arrayBuffer->write.start - currSampleBaseTime)/1000,
        	(unsigned long)(arg->arrayBuffer->write.end - currSampleBaseTime)/1000);
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
            (unsigned long)(jniContext->exec[pass].queued - currSampleBaseTime)/1000,
            (unsigned long)(jniContext->exec[pass].submit - currSampleBaseTime)/1000,
            (unsigned long)(jniContext->exec[pass].start - currSampleBaseTime)/1000,
            (unsigned long)(jniContext->exec[pass].end - currSampleBaseTime)/1000);
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
            	(unsigned long)(arg->arrayBuffer->read.queued - currSampleBaseTime)/1000,
            	(unsigned long)(arg->arrayBuffer->read.submit - currSampleBaseTime)/1000,
            	(unsigned long)(arg->arrayBuffer->read.start - currSampleBaseTime)/1000,
            	(unsigned long)(arg->arrayBuffer->read.end - currSampleBaseTime)/1000);
         }
      }
   }
   fprintf(jniContext->profileFile, "\n");
   return(0);
}

// Should failed profiling abort the run and return early?
cl_int profile(ProfileInfo *profileInfo, cl_event *event, jint type, char* name, cl_ulong profileBaseTime ) {

   cl_int status = CL_SUCCESS;

   try {
      status = clGetEventProfilingInfo(*event, CL_PROFILING_COMMAND_QUEUED, sizeof(profileInfo->queued), &(profileInfo->queued), NULL);
      if(status != CL_SUCCESS) throw CLException(status, "clGetEventProfiliningInfo() QUEUED");

      status = clGetEventProfilingInfo(*event, CL_PROFILING_COMMAND_SUBMIT, sizeof(profileInfo->submit), &(profileInfo->submit), NULL);
      if(status != CL_SUCCESS) throw CLException(status, "clGetEventProfiliningInfo() SUBMIT");

      status = clGetEventProfilingInfo(*event, CL_PROFILING_COMMAND_START, sizeof(profileInfo->start), &(profileInfo->start), NULL);
      if(status != CL_SUCCESS) throw CLException(status, "clGetEventProfiliningInfo() START");

      status = clGetEventProfilingInfo(*event, CL_PROFILING_COMMAND_END, sizeof(profileInfo->end), &(profileInfo->end), NULL);
      if(status != CL_SUCCESS) throw CLException(status, "clGetEventProfiliningInfo() END");

   } catch(CLException& cle) {
     cle.printError();
     return cle.status();
   }

   profileInfo->queued -= profileBaseTime;
   profileInfo->submit -= profileBaseTime;
   profileInfo->start -= profileBaseTime;
   profileInfo->end -= profileBaseTime;
   profileInfo->type = type;
   profileInfo->name = name;
   profileInfo->valid = true;

   return status;
}


/**
 * Step through all non-primitive (arrays) args
 * and determine if the field has changed
 * The field may have been re-assigned by the Java code to NULL or another instance. 
 * If we detect a change then we discard the previous cl_mem buffer,
 * the caller will detect that the buffers are null and will create new cl_mem buffers. 
 * @param jenv the java environment
 * @param jobj the object we might be updating
 * @param jniContext the context we're working in
 *
 * @throws CLException
 */
jint updateNonPrimitiveReferences(JNIEnv *jenv, jobject jobj, JNIContext* jniContext) {
   cl_int status = CL_SUCCESS;
   if (jniContext != NULL){
      for (jint i = 0; i < jniContext->argc; i++){ 
         KernelArg *arg = jniContext->args[i];

         // make sure that the JNI arg reflects the latest type info from the instance.
         // For example if the buffer is tagged as explicit and needs to be pushed
         arg->syncType(jenv);

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
                  if(status != CL_SUCCESS) throw CLException(status, "clReleaseMemObject()");
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

/**
 * if we are profiling events the test a first event, and report profiling info.
 *
 * @param jniContest the context holding the information we got form Java
 *
 * @throws CLException
 */
void profileFirstRun(JNIContext* jniContext) {
   cl_event firstEvent;
   int status = CL_SUCCESS;

   status = enqueueMarker(jniContext->commandQueue, &firstEvent);
   if (status != CL_SUCCESS) throw CLException(status, "clEnqueueMarker endOfTxfers");

   status = clWaitForEvents(1, &firstEvent);
   if (status != CL_SUCCESS) throw CLException(status,"clWaitForEvents");

   status = clGetEventProfilingInfo(firstEvent, CL_PROFILING_COMMAND_QUEUED, sizeof(jniContext->profileBaseTime), &(jniContext->profileBaseTime), NULL);
   if (status != CL_SUCCESS) throw CLException(status, "clGetEventProfilingInfo#1");

   clReleaseEvent(firstEvent);
   if (status != CL_SUCCESS) throw CLException(status, "clReleaseEvent() read event");

   if (config->isVerbose()) {
      fprintf(stderr, "profileBaseTime %lu \n", (unsigned long)jniContext->profileBaseTime);
   }
}

/**
 * create a new variable in OpenCL for the object, and sets the kernel arguement.
 * currently the only objects supported are arrays.
 *
 * @param jenv the java environment
 * @param jniContext the context we got from java
 * @param arg the argument we're passing to opencl
 * @param argPos out: the position of arg in the opencl argument list
 * @param argIdx the position of arg in the argument array
 *
 * @throws CLException
 */
void updateObject(JNIEnv* jenv, JNIContext* jniContext, KernelArg* arg, int& argPos, int argIdx) {

   cl_int status = CL_SUCCESS;
   // if either this is the first run or user changed input array
   // or gc moved something, then we create buffers/args
   cl_uint mask = CL_MEM_USE_HOST_PTR;
   if (arg->isReadByKernel() && arg->isMutableByKernel()) mask |= CL_MEM_READ_WRITE;
   else if (arg->isReadByKernel() && !arg->isMutableByKernel()) mask |= CL_MEM_READ_ONLY;
   else if (arg->isMutableByKernel()) mask |= CL_MEM_WRITE_ONLY;
   arg->arrayBuffer->memMask = mask;

   if (config->isVerbose()) {
      strcpy(arg->arrayBuffer->memSpec,"CL_MEM_USE_HOST_PTR");
      if (mask & CL_MEM_READ_WRITE) strcat(arg->arrayBuffer->memSpec,"|CL_MEM_READ_WRITE");
      if (mask & CL_MEM_READ_ONLY) strcat(arg->arrayBuffer->memSpec,"|CL_MEM_READ_ONLY");
      if (mask & CL_MEM_WRITE_ONLY) strcat(arg->arrayBuffer->memSpec,"|CL_MEM_WRITE_ONLY");

      fprintf(stderr, "%s %d clCreateBuffer(context, %s, size=%08lx bytes, address=%08lx, &status)\n", arg->name, 
            argIdx, arg->arrayBuffer->memSpec, (unsigned long)arg->arrayBuffer->lengthInBytes, (unsigned long)arg->arrayBuffer->addr);
   }

   arg->arrayBuffer->mem = clCreateBuffer(jniContext->context, arg->arrayBuffer->memMask, 
         arg->arrayBuffer->lengthInBytes, arg->arrayBuffer->addr, &status);

   if(status != CL_SUCCESS) throw CLException(status,"clCreateBuffer");

   if (config->isTrackingOpenCLResources()){
      memList.add(arg->arrayBuffer->mem, __LINE__, __FILE__);
   }

   status = clSetKernelArg(jniContext->kernel, argPos, sizeof(cl_mem), (void *)&(arg->arrayBuffer->mem));
   if(status != CL_SUCCESS) throw CLException(status,"clSetKernelArg (array)");

   // Add the array length if needed
   if (arg->usesArrayLength()) {
      arg->syncJavaArrayLength(jenv);

      status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jint), &(arg->arrayBuffer->length));
      if(status != CL_SUCCESS) throw CLException(status,"clSetKernelArg (array length)");

      if (config->isVerbose()){
         fprintf(stderr, "runKernel arg %d %s, length = %d\n", argIdx, arg->name, arg->arrayBuffer->length);
      }
      argPos++;
   }
}

/**
 * manages the memory of KernelArgs that are object.  i.e. handels pinning, and moved objects.
 * currently the only objects supported are arrays.
 *
 * @param jenv the java environment
 * @param jniContext the context we got from java
 * @param arg the argument we're processing
 * @param argPos out: the position of arg in the opencl argument list
 * @param argIdx the position of arg in the argument array
 *
 * @throws CLException
 */
void processObject(JNIEnv* jenv, JNIContext* jniContext, KernelArg* arg, int& argPos, int argIdx) {

   cl_int status = CL_SUCCESS;

   if (config->isProfilingEnabled()){
      arg->arrayBuffer->read.valid = false;
      arg->arrayBuffer->write.valid = false;
   }

   // pin the arrays so that GC does not move them during the call

   // get the C memory address for the region being transferred
   // this uses different JNI calls for arrays vs. directBufs
   void * prevAddr =  arg->arrayBuffer->addr;
   arg->pin(jenv);

   if (config->isVerbose()) {
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
      if (arg->arrayBuffer->mem != 0 && objectMoved) {
         // we need to release the old buffer 
         if (config->isTrackingOpenCLResources()) {
            memList.remove((cl_mem)arg->arrayBuffer->mem, __LINE__, __FILE__);
         }
         status = clReleaseMemObject((cl_mem)arg->arrayBuffer->mem);
         //fprintf(stdout, "dispose arg %d %0lx\n", i, arg->arrayBuffer->mem);

         //this needs to be reported, but we can still keep going
         CLException::checkCLError(status, "clReleaseMemObject()");

         arg->arrayBuffer->mem = (cl_mem)0;
      }

      updateObject(jenv, jniContext, arg, argPos, argIdx);

   } else {
      // Keep the arg position in sync if no updates were required
      if (arg->usesArrayLength()){
         argPos++;
      }
   }

}


/**
 * keeps track of write events for KernelArgs.
 *
 * @param jenv the java envrionment
 * @param jniContext the context we got from java
 * @param arg the KernelArg to create a write event for
 * @param argIdx the position of arg in the argument array
 * @param writeEventCount out: the number of write events we've created so far
 *
 * @throws CLException
 */
void updateWriteEvents(JNIEnv* jenv, JNIContext* jniContext, KernelArg* arg, int argIdx, int& writeEventCount) {

   cl_int status = CL_SUCCESS;

   // we only enqueue a write if we know the kernel actually reads the buffer 
   // or if there is an explicit write pending
   // the default behavior for Constant buffers is also that there is no write enqueued unless explicit

   if (config->isProfilingEnabled()) {
      jniContext->writeEventArgs[writeEventCount] = argIdx;
   }

   status = clEnqueueWriteBuffer(jniContext->commandQueue, arg->arrayBuffer->mem, CL_FALSE, 0, 
         arg->arrayBuffer->lengthInBytes, arg->arrayBuffer->addr, 0, NULL, &(jniContext->writeEvents[writeEventCount]));
   if(status != CL_SUCCESS) throw CLException(status,"clEnqueueWriteBuffer");

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

/**
 * sets the opencl kernel arguement for local args.
 *
 * @param jenv the java envrionment
 * @param jniContext the context we got from java
 * @param arg the KernelArg to create a write event for
 * @param argPos out: the position of arg in the opencl argument list
 * @param argIdx the position of arg in the argument array
 *
 * @throws CLException
 */
void processLocal(JNIEnv* jenv, JNIContext* jniContext, KernelArg* arg, int& argPos, int argIdx) {

   cl_int status = CL_SUCCESS;
   // what if local buffer size has changed?  We need a check for resize here.
   if (jniContext->firstRun) {
      status = arg->setLocalBufferArg(jenv, argIdx, argPos, config->isVerbose());
      if(status != CL_SUCCESS) throw CLException(status,"clSetKernelArg() (local)");

      // Add the array length if needed
      if (arg->usesArrayLength()) {
         arg->syncJavaArrayLength(jenv);

         status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jint), &(arg->arrayBuffer->length));

         if (config->isVerbose()){
            fprintf(stderr, "runKernel arg %d %s, javaArrayLength = %d\n", argIdx, arg->name, arg->arrayBuffer->length);
         }

         if(status != CL_SUCCESS) throw CLException(status,"clSetKernelArg (array length)");

         argPos++;
      }
   } else {
      // Keep the arg position in sync if no updates were required
      if (arg->usesArrayLength()) {
         argPos++;
      }
   }
}

/**
 * processes all of the arguments for the OpenCL Kernel that we got from the JNIContext
 *
 * @param jenv the java environment
 * @param jniContext the context with the arguements
 * @param writeEventCount out: the number of arguements that could be written to
 * @param argPos out: the absolute position of the last argument
 *
 * @throws CLException
 */
int processArgs(JNIEnv* jenv, JNIContext* jniContext, int& argPos, int& writeEventCount) {

   cl_int status = CL_SUCCESS;

   // argPos is used to keep track of the kernel arg position, it can 
   // differ from "argIdx" due to insertion of javaArrayLength args which are not
   // fields read from the kernel object.
   for (int argIdx = 0; argIdx < jniContext->argc; argIdx++, argPos++) {

      KernelArg *arg = jniContext->args[argIdx];

      // make sure that the JNI arg reflects the latest type info from the instance.
      // For example if the buffer is tagged as explicit and needs to be pushed
      arg->syncType(jenv);

      if (config->isVerbose()){
         fprintf(stderr, "got type for arg %d, %s, type=%08x\n", argIdx, arg->name, arg->type);
      }

      if (!arg->isPrimitive() && !arg->isLocal()) {
          processObject(jenv, jniContext, arg, argPos, argIdx);

          if (arg->needToEnqueueWrite() && !arg->isConstant()) {
              if (config->isVerbose()) {
                  fprintf(stderr, "%swriting %s%sbuffer argIndex=%d argPos=%d %s\n",  
                        (arg->isExplicit() ? "explicitly " : ""), 
                        (arg->isConstant() ? "constant " : ""), 
                        (arg->isLocal() ? "local " : ""), 
                        argIdx,
                        argPos,
                        arg->name);
              }
              updateWriteEvents(jenv, jniContext, arg, argIdx, writeEventCount);
          }
      } else if (arg->isLocal()) {
          processLocal(jenv, jniContext, arg, argPos, argIdx);
      } else {  // primitive arguments
         status = arg->setPrimitiveArg(jenv, argIdx, argPos, config->isVerbose());
         if(status != CL_SUCCESS) throw CLException(status,"clSetKernelArg()");
      }

   }  // for each arg
   return status;
}

/**
 * enqueus the current kernel to run on opencl
 *
 * @param jniContext the context with the arguements
 * @param range the range that the kernel is running over
 * @param passes the number of passes for the kernel
 * @param argPos the number of arguments we passed to the kernel
 * @param writeEventCount the number of arguement that will be updated
 *
 * @throws CLException
 */
void enqueueKernel(JNIContext* jniContext, Range& range, int passes, int argPos, int writeEventCount){
   // We will need to revisit the execution of multiple devices.  
   // POssibly cloning the range per device and mutating each to handle a unique subrange (of global) and
   // maybe even pushing the offset into the range class.

   //   size_t globalSize_0AsSizeT = (range.globalDims[0] /jniContext->deviceIdc);
   //   size_t localSize_0AsSizeT = range.localDims[0];

   // To support multiple passes we add a 'secret' final arg called 'passid' and just schedule multiple enqueuendrange kernels.  Each of which having a separate value of passid


   // delete the last set
   if (jniContext->exec) {
      delete jniContext->exec;
      jniContext->exec = NULL;
   } 
   jniContext->passes = passes;
   jniContext->exec = new ProfileInfo[passes];

   cl_int status = CL_SUCCESS;
   for (int passid=0; passid < passes; passid++) {

      //size_t offset = 1; // (size_t)((range.globalDims[0]/jniContext->deviceIdc)*dev);
      status = clSetKernelArg(jniContext->kernel, argPos, sizeof(passid), &(passid));
      if (status != CL_SUCCESS) throw CLException(status, "clSetKernelArg() (passid)");

      // wait for this event count
      int writeCount = 0;
      // list of events to wait for
      cl_event* writeEvents = NULL;



      // -----------
      // fix for Mac OSX CPU driver (and possibly others) 
      // which fail to give correct maximum work group info
      // while using clGetDeviceInfo
      // see: http://www.openwall.com/lists/john-dev/2012/04/10/4
      cl_uint max_group_size[3];
      status = clGetKernelWorkGroupInfo(jniContext->kernel,
                                        (cl_device_id)jniContext->deviceId,
                                        CL_KERNEL_WORK_GROUP_SIZE,
                                        sizeof(max_group_size),
                                        &max_group_size, NULL);
      
      if (status != CL_SUCCESS) {
         CLException(status, "clGetKernelWorkGroupInfo()").printError();
      } else {
         range.localDims[0] = std::min((cl_uint)range.localDims[0], max_group_size[0]);
      }
      // ------ end fix

        
      // two options here due to passid
      // there may be 1 or more passes
      // enqueue depends on write enqueues 
      // we don't block but and we populate the executeEvents
      if (passid == 0) {

         int writeCount = writeEventCount;
         if(writeEventCount > 0) {
            cl_event* writeEvents = jniContext->writeEvents;
         }

      // we are in some passid > 0 pass 
      // maybe middle or last!
      // we don't depend on write enqueues
      // we block and do supply executeEvents 
      } else {
         //fprintf(stderr, "setting passid to %d of %d not first not last\n", passid, passes);

         status = clWaitForEvents(1, &jniContext->executeEvents[0]);
         if (status != CL_SUCCESS) throw CLException(status, "clWaitForEvents() execute event");

         if (config->isTrackingOpenCLResources()) {
            executeEventList.remove(jniContext->executeEvents[0],__LINE__, __FILE__);
         }

         status = clReleaseEvent(jniContext->executeEvents[0]);
         if (status != CL_SUCCESS) throw CLException(status, "clReleaseEvent() read event");

         // We must capture any profile info for passid-1  so we must wait for the last execution to complete
         if (passid == 1 && config->isProfilingEnabled()) {

            // Now we can profile info for passid-1 
            status = profile(&jniContext->exec[passid-1], &jniContext->executeEvents[0], 1, NULL, jniContext->profileBaseTime);
            if (status != CL_SUCCESS) throw CLException(status,"");
         }

      }

      status = clEnqueueNDRangeKernel(
            jniContext->commandQueue,
            jniContext->kernel,
            range.dims,
            range.offsets,
            range.globalDims,
            range.localDims,
            writeCount,
            writeEvents,
            &jniContext->executeEvents[0]);

      if (status != CL_SUCCESS) {

         for(int i = 0; i<range.dims;i++) {
            fprintf(stderr, "after clEnqueueNDRangeKernel, globalSize[%d] = %d, localSize[%d] = %d\n",
                  i, (int)range.globalDims[i], i, (int)range.localDims[i]);
         }
         throw CLException(status, "clEnqueueNDRangeKernel()");
      }

      if(config->isTrackingOpenCLResources()){
         executeEventList.add(jniContext->executeEvents[0],__LINE__, __FILE__);
      }
    
   }
}


/**
 * set readEvents and readArgEvents
 * readEvents[] will be populated with the event's that we will wait on below.  
 * readArgEvents[] will map the readEvent to the arg that originated it
 * So if we had
 *     arg[0]  read_write array
 *     arg[1]  read array
 *     arg[2]  write array
 *     arg[3]  primitive
 *     arg[4]  read array
 * At the end of the call
 *     readCount=3
 *     readEvent[0] = new read event for arg0
 *     readArgEvent[0] = 0
 *     readEvent[1] = new read event for arg1
 *     readArgEvent[1] = 1
 *     readEvent[2] = new read event for arg4
 *     readArgEvent[2] = 4
 *
 * @param jniContext the context we got from Java
 *
 * @return number of reads. 
 * It will never be > jniContext->argc which is the size of readEvents[] and readEventArgs[]
 *
 * @throws CLException
 */
int getReadEvents(JNIContext* jniContext) {

   int readEventCount = 0; 

   cl_int status = CL_SUCCESS;
   for (int i=0; i< jniContext->argc; i++) {
      KernelArg *arg = jniContext->args[i];

      if (arg->needToEnqueueRead()){
         if (arg->isConstant()){
            fprintf(stderr, "reading %s\n", arg->name);
         }
         if (config->isProfilingEnabled()) {
            jniContext->readEventArgs[readEventCount] = i;
         }
         if (config->isVerbose()){
            fprintf(stderr, "reading buffer %d %s\n", i, arg->name);
         }

         status = clEnqueueReadBuffer(jniContext->commandQueue, arg->arrayBuffer->mem, CL_FALSE, 0, 
               arg->arrayBuffer->lengthInBytes,arg->arrayBuffer->addr , 1, jniContext->executeEvents, &(jniContext->readEvents[readEventCount]));
         if (status != CL_SUCCESS) throw CLException(status, "clEnqueueReadBuffer()");

         if (config->isTrackingOpenCLResources()){
            readEventList.add(jniContext->readEvents[readEventCount],__LINE__, __FILE__);
         }
         readEventCount++;
      }
   }
   return readEventCount;
}

/**
 * wait for and release all the read events
 *
 * @param jniContext the context we got from Java
 * @param readEventCount the number of read events to wait for
 * @param passes the number of passes for the kernel
 *
 * @throws CLException
 */
void waitForReadEvents(JNIContext* jniContext, int readEventCount, int passes) {

   // don't change the order here
   // We wait for the reads which each depend on the execution, which depends on the writes ;)
   // So after the reads have completed, we can release the execute and writes.
   
   cl_int status = CL_SUCCESS;

   if (readEventCount > 0){

      status = clWaitForEvents(readEventCount, jniContext->readEvents);
      if (status != CL_SUCCESS) throw CLException(status, "clWaitForEvents() read events");

      for (int i=0; i < readEventCount; i++){

         if (config->isProfilingEnabled()) {

            status = profile(&jniContext->args[jniContext->readEventArgs[i]]->arrayBuffer->read, &jniContext->readEvents[i], 0,jniContext->args[jniContext->readEventArgs[i]]->name, jniContext->profileBaseTime);
            if (status != CL_SUCCESS) throw CLException(status, "");
         }
         status = clReleaseEvent(jniContext->readEvents[i]);
         if (status != CL_SUCCESS) throw CLException(status, "clReleaseEvent() read event");

         if (config->isTrackingOpenCLResources()){
            readEventList.remove(jniContext->readEvents[i],__LINE__, __FILE__);
         }
      }
   } else {
      // if readEventCount == 0 then we don't need any reads so we just wait for the executions to complete
      status = clWaitForEvents(1, jniContext->executeEvents);
      if (status != CL_SUCCESS) throw CLException(status, "clWaitForEvents() execute event");
   }

   if (config->isTrackingOpenCLResources()){
      executeEventList.remove(jniContext->executeEvents[0],__LINE__, __FILE__);
   }
   if (config->isProfilingEnabled()) {
      status = profile(&jniContext->exec[passes-1], &jniContext->executeEvents[0], 1, NULL, jniContext->profileBaseTime); // multi gpu ?
      if (status != CL_SUCCESS) throw CLException(status, "");
   }

}

/**
 * check to make sure opencl exited correctly and update java memory.
 *
 * @param jenv the java environment
 * @param jniContext the context we got from Java
 * @param writeEventCount the number of write events to wait for
 *
 * @throws CLException
 */
void checkEvents(JNIEnv* jenv, JNIContext* jniContext, int writeEventCount) {
   // extract the execution status from the executeEvent
   cl_int status;
   cl_int executeStatus;

   status = clGetEventInfo(jniContext->executeEvents[0], CL_EVENT_COMMAND_EXECUTION_STATUS, sizeof(cl_int), &executeStatus, NULL);
   if (status != CL_SUCCESS) throw CLException(status, "clGetEventInfo() execute event");
   if (executeStatus != CL_SUCCESS) throw CLException(executeStatus, "Execution status of execute event");

   status = clReleaseEvent(jniContext->executeEvents[0]);
   if (status != CL_SUCCESS) throw CLException(status, "clReleaseEvent() read event");

   for (int i = 0; i < writeEventCount; i++) {

      if (config->isProfilingEnabled()) {
         profile(&jniContext->args[jniContext->writeEventArgs[i]]->arrayBuffer->write, &jniContext->writeEvents[i], 2, jniContext->args[jniContext->writeEventArgs[i]]->name, jniContext->profileBaseTime);
      }

      status = clReleaseEvent(jniContext->writeEvents[i]);
      if (status != CL_SUCCESS) throw CLException(status, "clReleaseEvent() write event");

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
}

JNI_JAVA(jint, KernelRunnerJNI, runKernelJNI)
   (JNIEnv *jenv, jobject jobj, jlong jniContextHandle, jobject _range, jboolean needSync, jint passes) {
      if (config == NULL){
         config = new Config(jenv);
      }

      Range range(jenv, _range);

      cl_int status = CL_SUCCESS;
      JNIContext* jniContext = JNIContext::getJNIContext(jniContextHandle);


      if (jniContext->firstRun && config->isProfilingEnabled()){
         try {
            profileFirstRun(jniContext);
         } catch(CLException& cle) {
            cle.printError();
            return 0L;
         }
      }


      int argPos = 0;
      // Need to capture array refs
      if (jniContext->firstRun || needSync) {
         try {
            updateNonPrimitiveReferences(jenv, jobj, jniContext);
         } catch (CLException& cle) {
             cle.printError();
         }
         if (config->isVerbose()){
            fprintf(stderr, "back from updateNonPrimitiveReferences\n");
         }
      }


      try {
         int writeEventCount = 0;
         processArgs(jenv, jniContext, argPos, writeEventCount);
         enqueueKernel(jniContext, range, passes, argPos, writeEventCount);
         int readEventCount = getReadEvents(jniContext);
         waitForReadEvents(jniContext, readEventCount, passes);
         checkEvents(jenv, jniContext, writeEventCount);
      }
      catch(CLException& cle) {
         cle.printError();
         jniContext->unpinAll(jenv);
         return cle.status();
      }




      //fprintf(stderr, "About to return %d from exec\n", status);
      return(status);
   }


// we return the JNIContext from here 
JNI_JAVA(jlong, KernelRunnerJNI, initJNI)
   (JNIEnv *jenv, jobject jobj, jobject kernelObject, jobject openCLDeviceObject, jint flags) {
      if (openCLDeviceObject == NULL){
         fprintf(stderr, "no device object!\n");
      }
      if (config == NULL){
         config = new Config(jenv);
      }
      cl_int status = CL_SUCCESS;
      JNIContext* jniContext = new JNIContext(jenv, kernelObject, openCLDeviceObject, flags);

      if (jniContext->isValid()) {

         return((jlong)jniContext);
      } else {
         return(0L);
      }
   }


void writeProfile(JNIEnv* jenv, JNIContext* jniContext) {
   // compute profile filename
   // indicate cpu or gpu
   // timestamp
   // kernel name

   jclass classMethodAccess = jenv->FindClass("java/lang/Class"); 
   jmethodID getNameID = jenv->GetMethodID(classMethodAccess,"getName","()Ljava/lang/String;");
   jstring className = (jstring)jenv->CallObjectMethod(jniContext->kernelClass, getNameID);
   const char *classNameChars = jenv->GetStringUTFChars(className, NULL);

   const size_t TIME_STR_LEN = 200;

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
   jint pid = getProcess();

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

JNI_JAVA(jlong, KernelRunnerJNI, buildProgramJNI)
   (JNIEnv *jenv, jobject jobj, jlong jniContextHandle, jstring source) {
      JNIContext* jniContext = JNIContext::getJNIContext(jniContextHandle);
      if (jniContext == NULL){
         return 0;
      }

      try {
         cl_int status = CL_SUCCESS;

         jniContext->program = CLHelper::compile(jenv, jniContext->context,  1, &jniContext->deviceId, source, NULL, &status);

         if(status == CL_BUILD_PROGRAM_FAILURE) throw CLException(status, "");

         jniContext->kernel = clCreateKernel(jniContext->program, "run", &status);
         if(status != CL_SUCCESS) throw CLException(status,"clCreateKernel()");

         cl_command_queue_properties queue_props = 0;
         if (config->isProfilingEnabled()) {
            queue_props |= CL_QUEUE_PROFILING_ENABLE;
         }

         jniContext->commandQueue = clCreateCommandQueue(jniContext->context, (cl_device_id)jniContext->deviceId,
               queue_props,
               &status);
         if(status != CL_SUCCESS) throw CLException(status,"clCreateCommandQueue()");

         commandQueueList.add(jniContext->commandQueue, __LINE__, __FILE__);

         if (config->isProfilingCSVEnabled()) {
            writeProfile(jenv, jniContext);
         }
      } catch(CLException& cle) {
         cle.printError();
         return 0;
      }
      

      return((jlong)jniContext);
   }


// this is called once when the arg list is first determined for this kernel
JNI_JAVA(jint, KernelRunnerJNI, setArgsJNI)
   (JNIEnv *jenv, jobject jobj, jlong jniContextHandle, jobjectArray argArray, jint argc) {
      if (config == NULL) {
         config = new Config(jenv);
      }
      JNIContext* jniContext = JNIContext::getJNIContext(jniContextHandle);
      cl_int status = CL_SUCCESS;
      if (jniContext != NULL){      
         jniContext->argc = argc;
         jniContext->args = new KernelArg*[jniContext->argc];
         jniContext->firstRun = true;

         // Step through the array of KernelArg's to capture the type data for the Kernel's data members.
         for (jint i = 0; i < jniContext->argc; i++){ 
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



JNI_JAVA(jstring, KernelRunnerJNI, getExtensionsJNI)
   (JNIEnv *jenv, jobject jobj, jlong jniContextHandle) {
      if (config == NULL){
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

/**
 * find the arguement in our list of KernelArgs that matches the array the user asked for
 *
 * @param jenv the java environment
 * @param jniContext the context we're working in
 * @param buffer the array we're looking for
 *
 * @return the KernelArg representing the array
 */
KernelArg* getArgForBuffer(JNIEnv* jenv, JNIContext* jniContext, jobject buffer) {
   KernelArg *returnArg = NULL;

   if (jniContext != NULL){
      for (jint i = 0; returnArg == NULL && i < jniContext->argc; i++){ 
         KernelArg *arg = jniContext->args[i];
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
      if (returnArg == NULL){
         if (config->isVerbose()){
            fprintf(stderr, "attempt to get arg for buffer that does not appear to be referenced from kernel\n");
         }
      }
   }
   return returnArg;
}

// Called as a result of Kernel.get(someArray)
JNI_JAVA(jint, KernelRunnerJNI, getJNI)
   (JNIEnv *jenv, jobject jobj, jlong jniContextHandle, jobject buffer) {
      if (config == NULL){
         config = new Config(jenv);
      }
      cl_int status = CL_SUCCESS;
      JNIContext* jniContext = JNIContext::getJNIContext(jniContextHandle);
      if (jniContext != NULL){
         KernelArg *arg = getArgForBuffer(jenv, jniContext, buffer);
         if (arg != NULL){
            if (config->isVerbose()){
               fprintf(stderr, "explicitly reading buffer %s\n", arg->name);
            }
            arg->pin(jenv);

            try {
               status = clEnqueueReadBuffer(jniContext->commandQueue, arg->arrayBuffer->mem, 
                                            CL_FALSE, 0, 
                                            arg->arrayBuffer->lengthInBytes,
                                            arg->arrayBuffer->addr , 0, NULL, 
                                            &jniContext->readEvents[0]);
               if (config->isVerbose()){
                  fprintf(stderr, "explicitly read %s ptr=%lx len=%d\n", 
                          arg->name, (unsigned long)arg->arrayBuffer->addr, 
                          arg->arrayBuffer->lengthInBytes );
               }
               if (status != CL_SUCCESS) throw CLException(status, "clEnqueueReadBuffer()");

               status = clWaitForEvents(1, jniContext->readEvents);
               if (status != CL_SUCCESS) throw CLException(status, "clWaitForEvents");

               if (config->isProfilingEnabled()) {
                  status = profile(&arg->arrayBuffer->read, &jniContext->readEvents[0], 0,
                                   arg->name, jniContext->profileBaseTime);
                  if (status != CL_SUCCESS) throw CLException(status, "profile "); 
               }

               status = clReleaseEvent(jniContext->readEvents[0]);
               if (status != CL_SUCCESS) throw CLException(status, "clReleaseEvent() read event");

               // since this is an explicit buffer get, 
               // we expect the buffer to have changed so we commit
               arg->unpin(jenv); // was unpinCommit

            //something went wrong print the error and exit
            } catch(CLException& cle) {
               cle.printError();
               return status;
            }

         }else{
            if (config->isVerbose()){
               fprintf(stderr, "attempt to request to get a buffer that does not appear to be referenced from kernel\n");
            }
         }
      }
      return 0;
   }

JNI_JAVA(jobject, KernelRunnerJNI, getProfileInfoJNI)
   (JNIEnv *jenv, jobject jobj, jlong jniContextHandle) {
      if (config == NULL){
         config = new Config(jenv);
      }
      cl_int status = CL_SUCCESS;
      JNIContext* jniContext = JNIContext::getJNIContext(jniContextHandle);
      jobject returnList = NULL;
      if (jniContext != NULL){
         returnList = JNIHelper::createInstance(jenv, ArrayListClass, VoidReturn );
         if (config->isProfilingEnabled()){

            for (jint i = 0; i < jniContext->argc; i++){ 
               KernelArg *arg = jniContext->args[i];
               if (arg->isArray()){
                  if (arg->isMutableByKernel() && arg->arrayBuffer->write.valid){
                     jobject writeProfileInfo = arg->arrayBuffer->write.createProfileInfoInstance(jenv);
                     JNIHelper::callVoid(jenv, returnList, "add", ArgsBooleanReturn(ObjectClassArg), writeProfileInfo);
                  }
               }
            }

            for (jint pass = 0; pass < jniContext->passes; pass++){
               jobject executeProfileInfo = jniContext->exec[pass].createProfileInfoInstance(jenv);
               JNIHelper::callVoid(jenv, returnList, "add", ArgsBooleanReturn(ObjectClassArg), executeProfileInfo);
            }

            for (jint i = 0; i < jniContext->argc; i++){ 
               KernelArg *arg = jniContext->args[i];
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

