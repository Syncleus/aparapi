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

#ifndef OPENCLJNI_H
#define OPENCLJNI_H

#include "common.h"
#include "jniHelper.h"
#include "clHelper.h"

#include "com_amd_aparapi_OpenCLArgDescriptor.h"
#include "com_amd_aparapi_OpenCLMem.h"

#define argisset(bits, token) (((bits) & com_amd_aparapi_OpenCLArgDescriptor_ARG_##token##_BIT) ==com_amd_aparapi_OpenCLArgDescriptor_ARG_##token##_BIT) 
#define argset(bits, token) (bits) |= com_amd_aparapi_OpenCLArgDescriptor_ARG_##token##_BIT 
#define argreset(bits, token) (bits) &= ~com_amd_aparapi_OpenCLArgDescriptor_ARG_##token##_BIT 

#define memisset(bits, token) (((bits) & com_amd_aparapi_OpenCLMem_MEM_##token##_BIT) ==com_amd_aparapi_OpenCLMem_MEM_##token##_BIT) 
#define memset(bits, token) (bits) |= com_amd_aparapi_OpenCLMem_MEM_##token##_BIT 
#define memreset(bits, token) (bits) &= ~com_amd_aparapi_OpenCLMem_MEM_##token##_BIT 

class OpenCLDevice{
   public:
      static jobject getPlatformInstance(JNIEnv *jenv, jobject deviceInstance);
      static cl_device_id getDeviceId(JNIEnv *jenv, jobject deviceInstance);
};
class OpenCLPlatform{
   public:
      static cl_platform_id getPlatformId(JNIEnv *jenv, jobject platformInstance);
};
class OpenCLProgram{
   public:
      static jobject create(JNIEnv *jenv, cl_program program, cl_command_queue queue, cl_context context, jobject deviceInstance, jstring source, jstring log);
      static cl_context getContext(JNIEnv *jenv, jobject programInstance);
      static cl_program getProgram(JNIEnv *jenv, jobject programInstance);
      static cl_command_queue getCommandQueue(JNIEnv *jenv, jobject programInstance);
};
class OpenCLKernel{
   public:
      static jobject create(JNIEnv *jenv, cl_kernel kernel, jobject programInstance, jstring name, jobject args);
      static cl_kernel getKernel(JNIEnv *jenv, jobject kernelInstance);
      static jobject getProgramInstance(JNIEnv *jenv, jobject kernelInstance);
      static jobjectArray getArgsArray(JNIEnv *jenv, jobject kernelInstance);
};

class OpenCLMem{
   public:
      static jobject create(JNIEnv *jenv);
      static cl_uint bitsToOpenCLMask(jlong argBits );
      static jsize getPrimitiveSizeInBytes(JNIEnv *jenv, jlong argBits);
      static jsize getArraySizeInBytes(JNIEnv *jenv, jarray array, jlong argBits);
      static void *pin(JNIEnv *jenv, jarray array, jlong *memBits);
      static void unpin(JNIEnv *jenv, jarray array, void *ptr, jlong *memBits);
      static jobject create(JNIEnv *jenv, cl_context context,  jlong argBits, jarray array);
      static jlong getBits(JNIEnv *jenv, jobject memInstance);
      static void setBits(JNIEnv *jenv, jobject memInstance, jlong bits);
      static void *getAddress(JNIEnv *jenv, jobject memInstance);
      static void setAddress(JNIEnv *jenv, jobject memInstance, void *address);
      static void setInstance(JNIEnv *jenv, jobject memInstance, jobject instance);
      static void setSizeInBytes(JNIEnv *jenv, jobject memInstance, jint sizeInBytes);
      static size_t getSizeInBytes(JNIEnv *jenv, jobject memInstance);
      static jobject getInstance(JNIEnv *jenv, jobject memInstance);
      static cl_mem getMem(JNIEnv *jenv, jobject memInstance);
      static void setMem(JNIEnv *jenv, jobject memInstance, cl_mem mem);
      static void describeBits(JNIEnv *jenv, jlong bits);
      static void describe(JNIEnv *jenv, jobject memInstance);
};

class OpenCLArgDescriptor{
   public:
      static jlong getBits(JNIEnv *jenv, jobject argInstance);
      static void setBits(JNIEnv *jenv, jobject argInstance, jlong bits);
      static jobject getMemInstance(JNIEnv *jenv, jobject argInstance);
      static void setMemInstance(JNIEnv *jenv, jobject argInstance, jobject memInstance);
      static void describeBits(JNIEnv *jenv, jlong bits);
      static void describe(JNIEnv *jenv, jobject argDef, jint argIndex);
};
class OpenCLRange{
   public:
      static jint getDims(JNIEnv *jenv, jobject rangeInstance);
      static void fill(JNIEnv *jenv, jobject rangeInstance, jint dims, size_t* offsets, size_t* globalDims, size_t* localDims);
};


#endif // OPENCLJNI_H
