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

#define CLHELPER_SOURCE
#include "CLHelper.h"
#include "List.h"
#include <map>

void setMap(std::map<cl_int, const char*>& errorMap) {
   errorMap[CL_SUCCESS]                         = "success";
   errorMap[CL_DEVICE_NOT_FOUND]                = "device not found";
   errorMap[CL_DEVICE_NOT_AVAILABLE]            = "device not available";
   errorMap[CL_COMPILER_NOT_AVAILABLE]          = "compiler not available";
   errorMap[CL_MEM_OBJECT_ALLOCATION_FAILURE]   = "mem object allocation failure";
   errorMap[CL_OUT_OF_RESOURCES]                = "out of resources";
   errorMap[CL_OUT_OF_HOST_MEMORY]              = "out of host memory";
   errorMap[CL_PROFILING_INFO_NOT_AVAILABLE]    = "profiling not available";
   errorMap[CL_MEM_COPY_OVERLAP]                = "memcopy overlaps";
   errorMap[CL_IMAGE_FORMAT_MISMATCH]           = "image format mismatch";
   errorMap[CL_IMAGE_FORMAT_NOT_SUPPORTED]      = "image format not supported";
   errorMap[CL_BUILD_PROGRAM_FAILURE]           = "build program failed";
   errorMap[CL_MAP_FAILURE]                     = "map failed";
   errorMap[CL_INVALID_VALUE]                   = "invalid value";
   errorMap[CL_INVALID_DEVICE_TYPE]             = "invalid device type";
   errorMap[CL_INVALID_PLATFORM]                = "invlaid platform";
   errorMap[CL_INVALID_DEVICE]                  = "invalid device";
   errorMap[CL_INVALID_CONTEXT]                 = "invalid context";
   errorMap[CL_INVALID_QUEUE_PROPERTIES]        = "invalid queue properties";
   errorMap[CL_INVALID_COMMAND_QUEUE]           = "invalid command queue";
   errorMap[CL_INVALID_HOST_PTR]                = "invalid host ptr";
   errorMap[CL_INVALID_MEM_OBJECT]              = "invalid mem object";
   errorMap[CL_INVALID_IMAGE_FORMAT_DESCRIPTOR] = "invalid image format descriptor ";
   errorMap[CL_INVALID_IMAGE_SIZE]              = "invalid image size";
   errorMap[CL_INVALID_SAMPLER]                 = "invalid sampler";
   errorMap[CL_INVALID_BINARY]                  = "invalid binary";
   errorMap[CL_INVALID_BUILD_OPTIONS]           = "invalid build options";
   errorMap[CL_INVALID_PROGRAM]                 = "invalid program ";
   errorMap[CL_INVALID_PROGRAM_EXECUTABLE]      = "invalid program executable";
   errorMap[CL_INVALID_KERNEL_NAME]             = "invalid kernel name";
   errorMap[CL_INVALID_KERNEL_DEFINITION]       = "invalid definition";
   errorMap[CL_INVALID_KERNEL]                  = "invalid kernel";
   errorMap[CL_INVALID_ARG_INDEX]               = "invalid arg index";
   errorMap[CL_INVALID_ARG_VALUE]               = "invalid arg value";
   errorMap[CL_INVALID_ARG_SIZE]                = "invalid arg size";
   errorMap[CL_INVALID_KERNEL_ARGS]             = "invalid kernel args";
   errorMap[CL_INVALID_WORK_DIMENSION ]         = "invalid work dimension";
   errorMap[CL_INVALID_WORK_GROUP_SIZE]         = "invalid work group size";
   errorMap[CL_INVALID_WORK_ITEM_SIZE]          = "invalid work item size";
   errorMap[CL_INVALID_GLOBAL_OFFSET]           = "invalid global offset";
   errorMap[CL_INVALID_EVENT_WAIT_LIST]         = "invalid event wait list";
   errorMap[CL_INVALID_EVENT]                   = "invalid event";
   errorMap[CL_INVALID_OPERATION]               = "invalid operation";
   errorMap[CL_INVALID_GL_OBJECT]               = "invalid gl object";
   errorMap[CL_INVALID_BUFFER_SIZE]             = "invalid buffer size";
   errorMap[CL_INVALID_MIP_LEVEL]               = "invalid mip level";
   errorMap[CL_INVALID_GLOBAL_WORK_SIZE]        = "invalid global work size";
   errorMap[0]                                  = NULL;
}

const char *CLHelper::errString(cl_int status) {
   static bool mapSet = false;
   static std::map<cl_int,const char*> errorMap;
   if(!mapSet) {
      setMap(errorMap);
      mapSet = true;
   }
   if(errorMap.find(status) != errorMap.end()) {
      return errorMap[status];
   }

   //if we don't know what the error is
   static char unknown[25];
#ifdef _WIN32
   _snprintf(unknown, sizeof(unknown), "unknown error %d", status);
#else
   snprintf(unknown, sizeof(unknown), "unknown error %d", status);
#endif
   return (const char*)unknown;
}

void CLHelper::getBuildErr(JNIEnv *jenv, cl_device_id deviceId,  cl_program program, jstring *log){
   size_t buildLogSize = 0;
   clGetProgramBuildInfo(program, deviceId, CL_PROGRAM_BUILD_LOG, buildLogSize, NULL, &buildLogSize);
   char * buildLog = new char[buildLogSize];
   memset(buildLog, 0, buildLogSize);
   clGetProgramBuildInfo (program, deviceId, CL_PROGRAM_BUILD_LOG, buildLogSize, buildLog, NULL);
   fprintf(stderr, "clBuildProgram failed");
   fprintf(stderr, "\n************************************************\n");
   fprintf(stderr, "%s", buildLog);
   fprintf(stderr, "\n************************************************\n\n\n");
   if (log != NULL){
      *log =  jenv->NewStringUTF(buildLog); 
   }
   delete []buildLog;
}

cl_program CLHelper::compile(JNIEnv *jenv, cl_context context, size_t deviceCount, cl_device_id* deviceIds, jstring source, jstring* log, cl_int* status){
   const char *sourceChars = jenv->GetStringUTFChars(source, NULL);
   size_t sourceSize[] = { strlen(sourceChars) };
   cl_program program = clCreateProgramWithSource(context, 1, &sourceChars, sourceSize, status); 
   jenv->ReleaseStringUTFChars(source, sourceChars);
   *status = clBuildProgram(program, deviceCount, deviceIds, NULL, NULL, NULL);
   if(*status == CL_BUILD_PROGRAM_FAILURE) {
      getBuildErr(jenv, *deviceIds, program, log);
   }
   return(program);
}

jstring CLHelper::getExtensions(JNIEnv *jenv, cl_device_id deviceId, cl_int *status){
   jstring jextensions = NULL;
   size_t retvalsize = 0;
   *status = clGetDeviceInfo(deviceId, CL_DEVICE_EXTENSIONS, 0, NULL, &retvalsize);
   if (*status == CL_SUCCESS){
      char* extensions = new char[retvalsize];
      *status = clGetDeviceInfo(deviceId, CL_DEVICE_EXTENSIONS, retvalsize, extensions, NULL);
      if (*status == CL_SUCCESS){
         jextensions = jenv->NewStringUTF(extensions);
      }
      delete [] extensions;
   }
   return jextensions;
}


