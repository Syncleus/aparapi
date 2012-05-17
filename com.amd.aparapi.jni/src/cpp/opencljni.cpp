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
   and Security’s website at http://www.bis.doc.gov/. 
   */

#define OPENCLJNI_SOURCE
#include "opencljni.h"

#include "com_amd_aparapi_OpenCLJNI.h"

void OpenCLArgDescriptor::describeBits(JNIEnv *jenv, jlong bits){
   fprintf(stderr, " %lx ", bits);
   if (argisset(bits, READONLY)){
      fprintf(stderr, "readonly ");
   }
   if (argisset(bits, WRITEONLY)){
      fprintf(stderr, "writeonly ");
   }
   if (argisset(bits, READWRITE)){
      fprintf(stderr, "readwrite ");
   }
   if (argisset(bits, ARRAY)){
      fprintf(stderr, "array ");
   }
   if (argisset(bits, PRIMITIVE)){
      fprintf(stderr, "primitive ");
   }
   if (argisset(bits, FLOAT)){
      fprintf(stderr, "float ");
   }
   if (argisset(bits, BYTE)){
      fprintf(stderr, "byte ");
   }
   if (argisset(bits, SHORT)){
      fprintf(stderr, "short ");
   }
   if (argisset(bits, LONG)){
      fprintf(stderr, "long ");
   }
   if (argisset(bits, DOUBLE)){
      fprintf(stderr, "double ");
   }
   if (argisset(bits, INT)){
      fprintf(stderr, "int ");
   }
   if (argisset(bits, GLOBAL)){
      fprintf(stderr, "global ");
   }
   if (argisset(bits, LOCAL)){
      fprintf(stderr, "local ");
   }
   if (argisset(bits, ISARG)){
      fprintf(stderr, "isarg ");
   }
}

void OpenCLMem::describeBits(JNIEnv *jenv, jlong bits){
   fprintf(stderr, " %lx ", bits);
   if (memisset(bits, COPY)){
      fprintf(stderr, "copy ");
   }
   if (memisset(bits, DIRTY)){
      fprintf(stderr, "dirty ");
   }
   if (memisset(bits, ENQUEUED)){
      fprintf(stderr, "enqueued ");
   }
}


jobject OpenCLDevice::getPlatformInstance(JNIEnv *jenv, jobject deviceInstance){ 
   return(JNIHelper::getInstanceFieldObject(jenv, deviceInstance, "platform", OpenCLPlatformClassArg ));
} 
cl_device_id OpenCLDevice::getDeviceId(JNIEnv *jenv, jobject deviceInstance){
   return((cl_device_id)JNIHelper::getInstanceFieldLong(jenv, deviceInstance, "deviceId"));
}

cl_platform_id OpenCLPlatform::getPlatformId(JNIEnv *jenv, jobject platformInstance){
   return((cl_platform_id)JNIHelper::getInstanceFieldLong(jenv, platformInstance, "platformId"));
}

jobject OpenCLProgram::create(JNIEnv *jenv, cl_program program, cl_command_queue queue, cl_context context, jobject deviceInstance, jstring source, jstring log){
   return(JNIHelper::createInstance(jenv, OpenCLProgramClass, ArgsVoidReturn( LongArg LongArg LongArg OpenCLDeviceClassArg StringClassArg StringClassArg) , (jlong)program, (jlong)queue, (jlong)context, deviceInstance, source, log));
}

cl_context OpenCLProgram::getContext(JNIEnv *jenv, jobject programInstance){
   return((cl_context) JNIHelper::getInstanceFieldLong(jenv, programInstance, "contextId")); 
}
cl_program OpenCLProgram::getProgram(JNIEnv *jenv, jobject programInstance){
   return((cl_program) JNIHelper::getInstanceFieldLong(jenv, programInstance, "programId")); 
}
cl_command_queue OpenCLProgram::getCommandQueue(JNIEnv *jenv, jobject programInstance){
   return((cl_command_queue)JNIHelper::getInstanceFieldLong(jenv, programInstance, "queueId"));
}

jobject OpenCLKernel::create(JNIEnv *jenv, cl_kernel kernel, jobject programInstance, jstring name, jobject args){
   return(JNIHelper::createInstance(jenv, OpenCLKernelClass, ArgsVoidReturn( LongArg OpenCLProgramClassArg StringClassArg ListClassArg) , (jlong)kernel, programInstance, name, args));
}

cl_kernel OpenCLKernel::getKernel(JNIEnv *jenv, jobject kernelInstance){
   return((cl_kernel) JNIHelper::getInstanceFieldLong(jenv, kernelInstance, "kernelId"));
}

jobject OpenCLKernel::getProgramInstance(JNIEnv *jenv, jobject kernelInstance){
   return(JNIHelper::getInstanceFieldObject(jenv, kernelInstance, "program", OpenCLProgramClassArg));
}

jobjectArray OpenCLKernel::getArgsArray(JNIEnv *jenv, jobject kernelInstance){
   return(reinterpret_cast<jobjectArray> (JNIHelper::getInstanceFieldObject(jenv, kernelInstance, "args", ArrayArg(OpenCLArgDescriptorClass) )));
}

jobject OpenCLMem::create(JNIEnv *jenv){
   return(JNIHelper::createInstance(jenv, OpenCLMemClassArg, VoidReturn));
}
cl_uint OpenCLMem::bitsToOpenCLMask(jlong argBits ){
   cl_uint mask =  CL_MEM_USE_HOST_PTR;
   if (argisset(argBits, READONLY)){
      mask|= CL_MEM_READ_ONLY;
   }else if (argisset(argBits, READWRITE)){
      mask|= CL_MEM_READ_WRITE;
   } else if (argisset(argBits, WRITEONLY)){
      mask|= CL_MEM_WRITE_ONLY;
   }
   return(mask);
}

jsize OpenCLMem::getPrimitiveSizeInBytes(JNIEnv *jenv, jlong argBits){
   jsize sizeInBytes = 0;
   if (argisset(argBits, DOUBLE) || argisset(argBits, LONG)){
      sizeInBytes = 8;
   }else if (argisset(argBits, FLOAT) || argisset(argBits, INT)){
      sizeInBytes = 4;
   }else if (argisset(argBits, SHORT)){
      sizeInBytes = 2;
   }else if (argisset(argBits, BYTE)){
      sizeInBytes = 1;
   }
   return(sizeInBytes);
}

jsize OpenCLMem::getArraySizeInBytes(JNIEnv *jenv, jarray array, jlong argBits){
   jsize arrayLen = jenv->GetArrayLength(array);
   jsize sizeInBytes = getPrimitiveSizeInBytes(jenv, argBits) * arrayLen;
   fprintf(stderr, "size of new Mem arg is %d\n", sizeInBytes);
   return(sizeInBytes);
}

void *OpenCLMem::pin(JNIEnv *jenv, jarray array, jlong *memBits){
   jboolean isCopy;
   void *ptr = jenv->GetPrimitiveArrayCritical(array, &isCopy); 
   if (memBits != NULL){
      if(0){OpenCLMem::describeBits(jenv, *memBits);fprintf(stderr, " before  \n");}
      if (isCopy){
         memset(*memBits, COPY);
      }else{
         memreset(*memBits, COPY);
      }
      if(0){OpenCLMem::describeBits(jenv, *memBits);fprintf(stderr, " after \n");}
   }
   return(ptr);
}

void OpenCLMem::unpin(JNIEnv *jenv, jarray array, void *ptr, jlong *memBits){
   if (argisset(*memBits, WRITEONLY)){
      jenv->ReleasePrimitiveArrayCritical(array, ptr,JNI_ABORT);
   }else{
      jenv->ReleasePrimitiveArrayCritical(array, ptr, 0);
   }
}

jobject OpenCLMem::create(JNIEnv *jenv, cl_context context, jlong argBits, jarray array){
   jsize sizeInBytes = getArraySizeInBytes(jenv, array, argBits);

   jlong memBits = 0;

   cl_int status = CL_SUCCESS;
   void *ptr = OpenCLMem::pin(jenv, array, &memBits); 
   cl_mem mem=clCreateBuffer(context, bitsToOpenCLMask(argBits),  sizeInBytes, ptr, &status);
   if (status != CL_SUCCESS){
      fprintf(stderr, "buffer creation failed!\n");
   }

   jobject memInstance = OpenCLMem::create(jenv);
   fprintf(stderr, "created a new mem object!\n");
   OpenCLMem::setAddress(jenv, memInstance, ptr);
   OpenCLMem::setInstance(jenv, memInstance, array);
   OpenCLMem::setSizeInBytes(jenv, memInstance, sizeInBytes);
   OpenCLMem::setBits(jenv, memInstance, memBits);
   OpenCLMem::setMem(jenv, memInstance, mem);
   fprintf(stderr, "initiated mem object!\n");
   return(memInstance);
}
jlong OpenCLMem::getBits(JNIEnv *jenv, jobject memInstance){
   return(JNIHelper::getInstanceFieldLong(jenv, memInstance, "bits"));
}
void OpenCLMem::setBits(JNIEnv *jenv, jobject memInstance, jlong bits){
   JNIHelper::setInstanceFieldLong(jenv, memInstance, "bits", bits);
}
void *OpenCLMem::getAddress(JNIEnv *jenv, jobject memInstance){
   return((void*)JNIHelper::getInstanceFieldLong(jenv, memInstance, "address"));
}
void OpenCLMem::setAddress(JNIEnv *jenv, jobject memInstance, void *address){
   JNIHelper::setInstanceFieldLong(jenv, memInstance, "address", (jlong)address);
}
void OpenCLMem::setInstance(JNIEnv *jenv, jobject memInstance, jobject instance){
   JNIHelper::setInstanceFieldObject(jenv, memInstance, "instance", ObjectClassArg, instance);
}
void OpenCLMem::setSizeInBytes(JNIEnv *jenv, jobject memInstance, jint sizeInBytes){
   JNIHelper::setInstanceFieldInt(jenv, memInstance, "sizeInBytes", sizeInBytes);
}
size_t OpenCLMem::getSizeInBytes(JNIEnv *jenv, jobject memInstance){
   return((size_t)JNIHelper::getInstanceFieldInt(jenv, memInstance, "sizeInBytes"));
}
jobject OpenCLMem::getInstance(JNIEnv *jenv, jobject memInstance){
   return(JNIHelper::getInstanceFieldObject(jenv, memInstance, "instance", ObjectClassArg));
}

cl_mem OpenCLMem::getMem(JNIEnv *jenv, jobject memInstance){
   cl_mem mem = (cl_mem)JNIHelper::getInstanceFieldLong(jenv, memInstance, "memId");
   return(mem);
}

void OpenCLMem::setMem(JNIEnv *jenv, jobject memInstance, cl_mem mem){
      JNIHelper::setInstanceFieldLong(jenv, memInstance, "memId", (jlong)mem);
}

void OpenCLMem::describe(JNIEnv *jenv, jobject memInstance){
   jlong memBits = OpenCLMem::getBits(jenv, memInstance);
   OpenCLMem::describeBits(jenv, memBits);
   fprintf(stderr, "\n");
}


jlong OpenCLArgDescriptor::getBits(JNIEnv *jenv, jobject argInstance){
   return(JNIHelper::getInstanceFieldLong(jenv, argInstance, "bits"));
}
void OpenCLArgDescriptor::setBits(JNIEnv *jenv, jobject argInstance, jlong bits){
   JNIHelper::setInstanceFieldLong(jenv, argInstance, "bits", bits);
}
jobject OpenCLArgDescriptor::getMemInstance(JNIEnv *jenv, jobject argInstance){
   return(JNIHelper::getInstanceFieldObject(jenv, argInstance, "memVal", OpenCLMemClassArg));
}
void OpenCLArgDescriptor::setMemInstance(JNIEnv *jenv, jobject argInstance, jobject memInstance){
   JNIHelper::setInstanceFieldObject(jenv, argInstance, "memVal", OpenCLMemClassArg,memInstance);
}
void OpenCLArgDescriptor::describe(JNIEnv *jenv, jobject argDef, jint argIndex){
   jlong argBits = OpenCLArgDescriptor::getBits(jenv, argDef);
   fprintf(stderr, " %d ", argIndex);
   OpenCLArgDescriptor::describeBits(jenv, argBits);
   fprintf(stderr, "\n");
}
jint OpenCLRange::getDims(JNIEnv *jenv, jobject rangeInstance){
   return(JNIHelper::getInstanceFieldInt(jenv, rangeInstance, "dims"));
}

void OpenCLRange::fill(JNIEnv *jenv, jobject rangeInstance, jint dims, size_t* offsets, size_t* globalDims, size_t* localDims){
   if (dims >0){
      offsets[0]= 0;
      localDims[0]=JNIHelper::getInstanceFieldInt(jenv, rangeInstance, "localSize_0"); 
      globalDims[0]=JNIHelper::getInstanceFieldInt(jenv, rangeInstance, "globalSize_0"); 
      if (dims >1){
         offsets[1]= 0;
         localDims[1]=JNIHelper::getInstanceFieldInt(jenv, rangeInstance, "localSize_1"); 
         globalDims[1]=JNIHelper::getInstanceFieldInt(jenv, rangeInstance, "globalSize_1"); 
         if (dims >2){
            offsets[2]= 0;
            localDims[2]=JNIHelper::getInstanceFieldInt(jenv, rangeInstance, "localSize_2"); 
            globalDims[2]=JNIHelper::getInstanceFieldInt(jenv, rangeInstance, "globalSize_2"); 
         }
      }
   }
}

JNI_JAVA(jobject, OpenCLJNI, createProgram)
   (JNIEnv *jenv, jobject jobj, jobject deviceInstance, jstring source) {

      jobject platformInstance = OpenCLDevice::getPlatformInstance(jenv, deviceInstance);
      cl_platform_id platformId = OpenCLPlatform::getPlatformId(jenv, platformInstance);
      cl_device_id deviceId = OpenCLDevice::getDeviceId(jenv, deviceInstance);
      cl_int status = CL_SUCCESS;
      cl_device_type deviceType;
      clGetDeviceInfo(deviceId, CL_DEVICE_TYPE,  sizeof(deviceType), &deviceType, NULL);
      //fprintf(stderr, "device[%d] CL_DEVICE_TYPE = %x\n", deviceId, deviceType);


      cl_context_properties cps[3] = { CL_CONTEXT_PLATFORM, (cl_context_properties)platformId, 0 };
      cl_context_properties* cprops = (NULL == platformId) ? NULL : cps;
      cl_context context = clCreateContextFromType( cprops, deviceType, NULL, NULL, &status);


      jstring log=NULL;
      cl_program program = CLHelper::compile(jenv, context, 1, &deviceId, source, &log, &status);
      cl_command_queue queue = NULL;
      if(status == CL_SUCCESS) {
         cl_command_queue_properties queue_props = CL_QUEUE_PROFILING_ENABLE;
         queue = clCreateCommandQueue(context, deviceId, queue_props, &status);
      }else{
         fprintf(stderr, "queue creation seems to have failed\n");

      }
      jobject programInstance = OpenCLProgram::create(jenv, program, queue, context, deviceInstance, source, log);

      return(programInstance);
   }

JNI_JAVA(jobject, OpenCLJNI, createKernel)
   (JNIEnv *jenv, jobject jobj, jobject programInstance, jstring name, jobject args) {
      cl_context context = OpenCLProgram::getContext(jenv, programInstance);
      cl_program program = OpenCLProgram::getProgram(jenv, programInstance); 
      cl_int status = CL_SUCCESS;
      const char *nameChars = jenv->GetStringUTFChars(name, NULL);
      fprintf(stderr, "tring to extract kernel '%s'\n", nameChars);
      cl_kernel kernel = clCreateKernel(program, nameChars, &status);
      jenv->ReleaseStringUTFChars(name, nameChars);

      if (kernel == NULL){
         fprintf(stderr, "kernel is null!\n");
      }

      jobject kernelInstance = NULL;
      if (status == CL_SUCCESS){
         kernelInstance = OpenCLKernel::create(jenv, kernel, programInstance, name, args);
      }else{
         fprintf(stderr, "kernel creation seems to have failed\n");
      }
      return(kernelInstance);

   }



void putArg(JNIEnv *jenv, cl_context context, cl_kernel kernel, cl_command_queue commandQueue, cl_event *events, jint *eventc, jint argIndex, jobject argDef, jobject arg){
   if(1){
      fprintf(stderr, "putArg ");
      OpenCLArgDescriptor::describe(jenv, argDef, argIndex);
   }
   cl_int status = CL_SUCCESS;
   jlong argBits = OpenCLArgDescriptor::getBits(jenv, argDef);
   if (argisset(argBits, ARRAY) && argisset(argBits, GLOBAL)){ 
      fprintf(stderr, "GLOBAL\n");
      jobject memInstance = OpenCLArgDescriptor::getMemInstance(jenv, argDef);
      if (memInstance == NULL){
         // first call?
         memInstance = OpenCLMem::create(jenv, context, argBits, (jarray)arg);
         OpenCLArgDescriptor::setMemInstance(jenv, argDef, memInstance);
      }else{
         // check of argBits == memInstance.argBits
         // we need to pin it
         // jboolean isCopy;
         void *ptr  =  OpenCLMem::pin(jenv, (jarray)arg,&argBits); 
         void *oldPtr = OpenCLMem::getAddress(jenv, memInstance);
         if (ptr !=oldPtr){
            fprintf(stderr, "ptr moved from %lx to %lx\n", oldPtr, ptr);
            cl_mem mem = OpenCLMem::getMem(jenv, memInstance);
            status = clReleaseMemObject(mem); 
            memInstance = OpenCLMem::create(jenv, context, argBits, (jarray)arg);
            OpenCLArgDescriptor::setMemInstance(jenv, argDef, memInstance);
         }
         OpenCLArgDescriptor::setBits(jenv, argDef, argBits);
      }
      cl_mem mem = OpenCLMem::getMem(jenv, memInstance);
      cl_int status = CL_SUCCESS;
      if (argisset(argBits, READONLY)|argisset(argBits, READWRITE)){ // kernel reads this so enqueue a write
         void *ptr= OpenCLMem::getAddress(jenv, memInstance);
         size_t sizeInBytes= OpenCLMem::getSizeInBytes(jenv, memInstance);
         jlong memBits = OpenCLMem::getBits(jenv, memInstance);
         memset(memBits, ENQUEUED);
         OpenCLMem::setBits(jenv, memInstance, memBits);
         status = clEnqueueWriteBuffer(commandQueue, mem, CL_FALSE, 0, sizeInBytes, ptr, *eventc, (*eventc)==0?NULL:events, &events[*eventc]);
         if (status != CL_SUCCESS) {
            fprintf(stderr, "error enqueuing write %s!\n",  CLHelper::errString(status));
         }else{
            if(0)fprintf(stderr, "enqueued write eventc = %d!\n", *eventc);
         }
         (*eventc)++;
      }
      status = clSetKernelArg(kernel, argIndex, sizeof(cl_mem), (void *)&(mem));          
      if (status != CL_SUCCESS) {
         fprintf(stderr, "error setting arg %d %s!\n",  argIndex, CLHelper::errString(status));
      }else{
         if(0)fprintf(stderr, "set arg  = %d!\n", argIndex);
      }
   } else if (argisset(argBits, ARRAY) && argisset(argBits, LOCAL)){ 
      fprintf(stderr, "LOCAL\n");

      jsize sizeInBytes = OpenCLMem::getArraySizeInBytes(jenv, (jarray)arg, argBits);
      cl_int status = CL_SUCCESS;
      fprintf(stderr, "local is %d bytes\n", sizeInBytes);
      status = clSetKernelArg(kernel, argIndex, (size_t)sizeInBytes, (void *)NULL);          
      if (status != CL_SUCCESS) {
         fprintf(stderr, "error setting arg %d %s!\n",  argIndex, CLHelper::errString(status));
      }else{
         if(0)fprintf(stderr, "set arg  = %d!\n", argIndex);
      }
   }else if (argisset(argBits, PRIMITIVE)){
      fprintf(stderr, "PRIMITIVE\n");
      if (argisset(argBits, INT)){
         cl_int value = JNIHelper::getInstanceFieldInt(jenv, arg, "value");
         status = clSetKernelArg(kernel, argIndex, sizeof(value), (void *)&(value));          
         if (status != CL_SUCCESS) {
            fprintf(stderr, "error setting int arg %d %d %s!\n",  argIndex, value, CLHelper::errString(status));
         }else{
            if(0)fprintf(stderr, "set arg  = %d to %d!\n", argIndex, value);
         }
      }else if (argisset(argBits, FLOAT)){
         cl_float value = JNIHelper::getInstanceFieldFloat(jenv, arg, "value");
         status = clSetKernelArg(kernel, argIndex, sizeof(value), (void *)&(value));          
         if (status != CL_SUCCESS) {
            fprintf(stderr, "error setting int arg %d %f %s!\n",  argIndex, value, CLHelper::errString(status));
         }else{
            if(0)fprintf(stderr, "set arg  = %d to %f!\n", argIndex, value);
         }

      }else if (argisset(argBits, DOUBLE)){
         cl_double value = JNIHelper::getInstanceFieldDouble(jenv, arg, "value");
         status = clSetKernelArg(kernel, argIndex, sizeof(value), (void *)&(value));          
         if (status != CL_SUCCESS) {
            fprintf(stderr, "error setting double arg %d %lf %s!\n",  argIndex, value, CLHelper::errString(status));
         }else{
            if(0)fprintf(stderr, "set arg  = %d to %lf!\n", argIndex, value);
         }

      }
   }
}




void getArg(JNIEnv *jenv, cl_context context, cl_command_queue commandQueue, cl_event *events, jint *eventc, jint argIndex, jobject argDef, jobject arg){
   if (0){
      fprintf(stderr, "post ");
      OpenCLArgDescriptor::describe(jenv, argDef, argIndex);
   }
   jlong argBits = OpenCLArgDescriptor::getBits(jenv, argDef);
   if (argisset(argBits, ARRAY) && argisset(argBits, GLOBAL)){
      jobject memInstance = OpenCLArgDescriptor::getMemInstance(jenv, argDef);
      if (memInstance == NULL){
         fprintf(stderr, "mem instance not set\n");
      }else{
         if(0)fprintf(stderr, "retrieved mem instance\n");
      }
      void *ptr= OpenCLMem::getAddress(jenv, memInstance);
      if (argisset(argBits, WRITEONLY)|argisset(argBits, READWRITE)){

         cl_mem mem = OpenCLMem::getMem(jenv, memInstance);

         size_t sizeInBytes= OpenCLMem::getSizeInBytes(jenv, memInstance);
         if (0){
            fprintf(stderr, "about to enqueu read eventc = %d!\n", *eventc);
         }
         cl_int status = clEnqueueReadBuffer(commandQueue, mem, CL_FALSE, 0, sizeInBytes, ptr ,*eventc, (*eventc)==0?NULL:events, &events[*eventc]);
         if (status != CL_SUCCESS) {
            fprintf(stderr, "error enqueuing read %s!\n",  CLHelper::errString(status));
         }else{
            if (0){
               fprintf(stderr, "enqueued read eventc = %d!\n", *eventc);
            }
         }
         (*eventc)++;
      }

      jobject arrayInstance = OpenCLMem::getInstance(jenv, memInstance);
      jlong memBits = OpenCLMem::getBits(jenv, memInstance);
      OpenCLMem::unpin(jenv, (jarray)arrayInstance, ptr, &memBits);
      memreset(memBits, ENQUEUED); 
      memreset(memBits, COPY); 
      OpenCLMem::setBits(jenv, memInstance, memBits);
   }
}

JNI_JAVA(void, OpenCLJNI, invoke)
   (JNIEnv *jenv, jobject jobj, jobject kernelInstance, jobjectArray argArray) {
      cl_kernel kernel = OpenCLKernel::getKernel(jenv, kernelInstance);
      jobject programInstance = OpenCLKernel::getProgramInstance(jenv, kernelInstance);
      jobjectArray argDefsArray = OpenCLKernel::getArgsArray(jenv, kernelInstance);

      cl_context context =OpenCLProgram::getContext(jenv, programInstance);
      cl_command_queue commandQueue = OpenCLProgram::getCommandQueue(jenv, programInstance);


      // walk through the args creating buffers when needed 
      // we use the bitfields to determine which is which
      // note that argArray[0] is the range then 1,2,3 etc matches argDefsArray[0,1,2]
      jsize argc = jenv->GetArrayLength(argDefsArray);
      if (0) fprintf(stderr, "argc = %d\n", argc);
      jint reads=0;
      jint writes=0;
      for (jsize argIndex=0; argIndex<argc; argIndex++){
         jobject argDef = jenv->GetObjectArrayElement(argDefsArray, argIndex);
         jlong argBits = OpenCLArgDescriptor::getBits(jenv, argDef);
         if (argisset(argBits, READONLY)){
            reads++;
         }
         if (argisset(argBits, READWRITE)){
            reads++;
            writes++;
         }
         if (argisset(argBits, WRITEONLY)){
            writes++;
         }
      }

      if (0) fprintf(stderr, "reads=%d writes=%d\n", reads, writes);
      cl_event * events= new cl_event[reads+writes+1];

      jint eventc =0;

      for (jsize argIndex=0; argIndex<argc; argIndex++){
         jobject argDef = jenv->GetObjectArrayElement(argDefsArray, argIndex);
         jobject arg = jenv->GetObjectArrayElement(argArray, argIndex+1);
         putArg(jenv, context, kernel, commandQueue, events, &eventc, argIndex, argDef, arg);
      }

      jobject rangeInstance = jenv->GetObjectArrayElement(argArray, 0);
      jint dims = OpenCLRange::getDims(jenv, rangeInstance);

      size_t *offsets = new size_t[dims];
      size_t *globalDims = new size_t[dims];
      size_t *localDims = new size_t[dims];
      OpenCLRange::fill(jenv, rangeInstance, dims, offsets, globalDims, localDims);

      cl_int status = CL_SUCCESS;

      if(0) fprintf(stderr, "Exec %d\n", eventc);
      status = clEnqueueNDRangeKernel(
            commandQueue,
            kernel,
            dims,
            offsets,
            globalDims,
            localDims,
            eventc, // count Of events to wait for
            eventc==0?NULL:events, // address of events to wait for
            &events[eventc]);
      if (status != CL_SUCCESS) {
         fprintf(stderr, "error enqueuing execute %s !\n", CLHelper::errString(status));
      }else{
         if (0) fprintf(stderr, "success enqueuing execute eventc= %d !\n", eventc);
      }
      eventc++;

      for (jsize argIndex=0; argIndex<argc; argIndex++){
         jobject argDef = jenv->GetObjectArrayElement(argDefsArray, argIndex);
         jobject arg = jenv->GetObjectArrayElement(argArray, argIndex+1);
         getArg(jenv, context, commandQueue, events, &eventc, argIndex, argDef, arg);
      }
      status = clWaitForEvents(eventc, events);
      if (status != CL_SUCCESS) {
         fprintf(stderr, "error waiting for events !\n");
      }
   }

JNI_JAVA(jobject, OpenCLJNI, getPlatforms)
   (JNIEnv *jenv, jobject jobj) {
      jobject platformListInstance = JNIHelper::createInstance(jenv, ArrayListClass, VoidReturn);
      cl_int status = CL_SUCCESS;
      cl_uint platformc;

      status = clGetPlatformIDs(0, NULL, &platformc);
      //fprintf(stderr, "There are %d platforms\n", platformc);
      cl_platform_id* platformIds = new cl_platform_id[platformc];
      status = clGetPlatformIDs(platformc, platformIds, NULL);

      if (status == CL_SUCCESS){
         for (unsigned platformIdx = 0; platformIdx < platformc; ++platformIdx) {
            char platformVersionName[512];
            status = clGetPlatformInfo(platformIds[platformIdx], CL_PLATFORM_VERSION, sizeof(platformVersionName), platformVersionName, NULL);

            // fix this so OpenCL 1.3 or higher will not break!
            if (   !strncmp(platformVersionName, "OpenCL 1.2", 10)
                  || !strncmp(platformVersionName, "OpenCL 1.1", 10)
#ifdef __APPLE__
                  || !strncmp(platformVersionName, "OpenCL 1.0", 10)
#endif
               ) { 
               char platformVendorName[512];  
               char platformName[512];  
               status = clGetPlatformInfo(platformIds[platformIdx], CL_PLATFORM_VENDOR, sizeof(platformVendorName), platformVendorName, NULL);
               status = clGetPlatformInfo(platformIds[platformIdx], CL_PLATFORM_NAME, sizeof(platformName), platformName, NULL);
               //fprintf(stderr, "platform vendor    %d %s\n", platformIdx, platformVendorName); 
               //fprintf(stderr, "platform version %d %s\n", platformIdx, platformVersionName); 
               jobject platformInstance = JNIHelper::createInstance(jenv, OpenCLPlatformClass , ArgsVoidReturn(LongArg StringClassArg StringClassArg StringClassArg ), 
                     (jlong)platformIds[platformIdx],
                     jenv->NewStringUTF(platformVersionName), 
                     jenv->NewStringUTF(platformVendorName),
                     jenv->NewStringUTF(platformName)
                     );
               JNIHelper::callVoid(jenv, platformListInstance, "add", ArgsBooleanReturn(ObjectClassArg), platformInstance);

               cl_uint deviceIdc;
               cl_device_type requestedDeviceType =CL_DEVICE_TYPE_CPU |CL_DEVICE_TYPE_GPU ;
               status = clGetDeviceIDs(platformIds[platformIdx], requestedDeviceType, 0, NULL, &deviceIdc);
               if (status == CL_SUCCESS && deviceIdc >0 ){
                  cl_device_id* deviceIds = new cl_device_id[deviceIdc];
                  status = clGetDeviceIDs(platformIds[platformIdx], requestedDeviceType, deviceIdc, deviceIds, NULL);
                  if (status == CL_SUCCESS){
                     for (unsigned deviceIdx=0; deviceIdx<deviceIdc; deviceIdx++){

                        cl_device_type deviceType;
                        status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_TYPE,  sizeof(deviceType), &deviceType, NULL);
                        jobject deviceTypeEnumInstance = JNIHelper::getStaticFieldObject(jenv, DeviceTypeClass, "UNKNOWN", DeviceTypeClassArg);
                        //fprintf(stderr, "device[%d] CL_DEVICE_TYPE = ", deviceIdx);
                        if (deviceType & CL_DEVICE_TYPE_DEFAULT) {
                           deviceType &= ~CL_DEVICE_TYPE_DEFAULT;
                           //  fprintf(stderr, "Default ");
                        }
                        if (deviceType & CL_DEVICE_TYPE_CPU) {
                           deviceType &= ~CL_DEVICE_TYPE_CPU;
                           //fprintf(stderr, "CPU ");
                           deviceTypeEnumInstance = JNIHelper::getStaticFieldObject(jenv, DeviceTypeClass, "CPU", DeviceTypeClassArg);
                        }
                        if (deviceType & CL_DEVICE_TYPE_GPU) {
                           deviceType &= ~CL_DEVICE_TYPE_GPU;
                           //fprintf(stderr, "GPU ");
                           deviceTypeEnumInstance = JNIHelper::getStaticFieldObject(jenv, DeviceTypeClass, "GPU", DeviceTypeClassArg);
                        }
                        if (deviceType & CL_DEVICE_TYPE_ACCELERATOR) {
                           deviceType &= ~CL_DEVICE_TYPE_ACCELERATOR;
                           //fprintf(stderr, "Accelerator ");
                        }
                        //fprintf(stderr, "(0x%llx) ", deviceType);
                        //fprintf(stderr, "\n");


                        jobject deviceInstance = JNIHelper::createInstance(jenv, OpenCLDeviceClass, ArgsVoidReturn( OpenCLPlatformClassArg LongArg DeviceTypeClassArg  ),
                              platformInstance, 
                              (jlong)deviceIds[deviceIdx],
                              deviceTypeEnumInstance);
                        JNIHelper::callVoid(jenv, platformInstance, "add", ArgsVoidReturn( OpenCLDeviceClassArg ), deviceInstance);


                        cl_uint maxComputeUnits;
                        status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_MAX_COMPUTE_UNITS,  sizeof(maxComputeUnits), &maxComputeUnits, NULL);
                        //fprintf(stderr, "device[%d] CL_DEVICE_MAX_COMPUTE_UNITS = %u\n", deviceIdx, maxComputeUnits);
                        JNIHelper::callVoid(jenv, deviceInstance, "setMaxComputeUnits", ArgsVoidReturn(IntArg),  maxComputeUnits);



                        cl_uint maxWorkItemDimensions;
                        status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS,  sizeof(maxWorkItemDimensions), &maxWorkItemDimensions, NULL);
                        //fprintf(stderr, "device[%d] CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS = %u\n", deviceIdx, maxWorkItemDimensions);
                        JNIHelper::callVoid(jenv, deviceInstance, "setMaxWorkItemDimensions",  ArgsVoidReturn(IntArg),  maxWorkItemDimensions);

                        size_t *maxWorkItemSizes = new size_t[maxWorkItemDimensions];
                        status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_MAX_WORK_ITEM_SIZES,  sizeof(size_t)*maxWorkItemDimensions, maxWorkItemSizes, NULL);
                        for (unsigned dimIdx=0; dimIdx<maxWorkItemDimensions; dimIdx++){
                           //fprintf(stderr, "device[%d] dim[%d] = %d\n", deviceIdx, dimIdx, maxWorkItemSizes[dimIdx]);
                           JNIHelper::callVoid(jenv, deviceInstance, "setMaxWorkItemSize", ArgsVoidReturn(IntArg IntArg), dimIdx,maxWorkItemSizes[dimIdx]);
                        }

                        size_t maxWorkGroupSize;
                        status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_MAX_WORK_GROUP_SIZE,  sizeof(maxWorkGroupSize), &maxWorkGroupSize, NULL);
                        //fprintf(stderr, "device[%d] CL_DEVICE_MAX_GROUP_SIZE = %u\n", deviceIdx, maxWorkGroupSize);
                        JNIHelper::callVoid(jenv, deviceInstance, "setMaxWorkGroupSize",  ArgsVoidReturn(IntArg),  maxWorkGroupSize);

                        cl_ulong maxMemAllocSize;
                        status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_MAX_MEM_ALLOC_SIZE,  sizeof(maxMemAllocSize), &maxMemAllocSize, NULL);
                        //fprintf(stderr, "device[%d] CL_DEVICE_MAX_MEM_ALLOC_SIZE = %lu\n", deviceIdx, maxMemAllocSize);
                        JNIHelper::callVoid(jenv, deviceInstance, "setMaxMemAllocSize",  ArgsVoidReturn(LongArg),  maxMemAllocSize);

                        cl_ulong globalMemSize;
                        status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_GLOBAL_MEM_SIZE,  sizeof(globalMemSize), &globalMemSize, NULL);
                        //fprintf(stderr, "device[%d] CL_DEVICE_GLOBAL_MEM_SIZE = %lu\n", deviceIdx, globalMemSize);
                        JNIHelper::callVoid(jenv, deviceInstance, "setGlobalMemSize", ArgsVoidReturn(LongArg),  globalMemSize);



                        cl_ulong localMemSize;
                        status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_LOCAL_MEM_SIZE,  sizeof(localMemSize), &localMemSize, NULL);
                        //fprintf(stderr, "device[%d] CL_DEVICE_LOCAL_MEM_SIZE = %lu\n", deviceIdx, localMemSize);
                        JNIHelper::callVoid(jenv, deviceInstance, "setLocalMemSize", ArgsVoidReturn(LongArg),  localMemSize);
                     }

                  }
               }
            }

         }
      }
      return (platformListInstance);
   }

