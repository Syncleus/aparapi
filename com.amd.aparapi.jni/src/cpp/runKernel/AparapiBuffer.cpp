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
#define APARAPIBUFFER_SOURCE
#include "AparapiBuffer.h"
#include "KernelArg.h"

AparapiBuffer::AparapiBuffer():
   javaObject((jobject) 0),
   numDims(0),
   dims(NULL),
   lengthInBytes(0),
   mem((cl_mem) 0),
   data(NULL),
   memMask((cl_uint)0) {
   }

AparapiBuffer::AparapiBuffer(void* _data, cl_uint* _lens, cl_uint _numDims, long _lengthInBytes, jobject _javaObject) :
   data(_data),
   lens(_lens),
   numDims(_numDims),
   lengthInBytes(_lengthInBytes),
   javaObject(_javaObject),
   mem((cl_mem) 0),
   memMask((cl_uint)0)
{
   dims = new cl_uint[_numDims];
   for(int i = 0; i < _numDims; i++) {
      dims[i] = 1;
      for(int j = i+1; j < _numDims; j++) {
         dims[i] *= lens[j];
      }
   }
}

jobject AparapiBuffer::getJavaObject(JNIEnv* env, KernelArg* arg) {
   return JNIHelper::getInstanceField<jobject>(env, arg->javaArg, "javaBuffer", ObjectClassArg);
}


AparapiBuffer* AparapiBuffer::flatten(JNIEnv* env, jobject arg, int type) {
   int numDims = JNIHelper::getInstanceField<jint>(env, arg, "numDims", IntArg);
   if(numDims == 2 && isBoolean(type)) {
      return AparapiBuffer::flattenBoolean2D(env,arg);
   } else if(numDims == 2 && isByte(type)) {
      return AparapiBuffer::flattenByte2D(env,arg);
   } else if(numDims == 2 && isShort(type)) {
      return AparapiBuffer::flattenShort2D(env,arg);
   } else if(numDims == 2 && isInt(type)) {
      return AparapiBuffer::flattenInt2D(env,arg);
   } else if(numDims == 2 && isLong(type)) {
      return AparapiBuffer::flattenLong2D(env,arg);
   } else if(numDims == 2 && isFloat(type)) {
      return AparapiBuffer::flattenFloat2D(env,arg);
   } else if(numDims == 2 && isDouble(type)) {
      return AparapiBuffer::flattenDouble2D(env,arg);
   } else if(numDims == 3 && isBoolean(type)) {
      return AparapiBuffer::flattenBoolean3D(env,arg);
   } else if(numDims == 3 && isByte(type)) {
      return AparapiBuffer::flattenByte3D(env,arg);
   } else if(numDims == 3 && isShort(type)) {
      return AparapiBuffer::flattenShort3D(env,arg);
   } else if(numDims == 3 && isInt(type)) {
      return AparapiBuffer::flattenInt3D(env,arg);
   } else if(numDims == 3 && isLong(type)) {
      return AparapiBuffer::flattenLong3D(env,arg);
   } else if(numDims == 3 && isFloat(type)) {
      return AparapiBuffer::flattenFloat3D(env,arg);
   } else if(numDims == 3 && isDouble(type)) {
      return AparapiBuffer::flattenDouble3D(env,arg);
   }
   return new AparapiBuffer();
}


AparapiBuffer* AparapiBuffer::flattenBoolean2D(JNIEnv* env, jobject arg) {

   jobject javaBuffer = JNIHelper::getInstanceField<jobject>(env, arg, "javaBuffer", ObjectClassArg);
   cl_uint* dims = new cl_uint[2];
   dims[0] = env->GetArrayLength((jobjectArray)javaBuffer);
   dims[1] = env->GetArrayLength((jbooleanArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, 0));
   int totalSize = dims[0] * dims[1];
   long bitSize = totalSize * sizeof(jboolean);

   jboolean* array = new jboolean[totalSize];
   /*
   jbooleanArray* jArray = new jbooleanArray[dims[0]];
   jboolean** elems = new jboolean*[dims[0]];

   for(int i = 0; i < (int)dims[0]; i++) {
      jArray[i] = (jbooleanArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      elems[i] = env->GetBooleanArrayElements(jArray[i],0);
   }

   #pragma omp parallel for
   for(int i = 0; i < (int)dims[0]; i++) {
      for(int j = 0; j < (int)dims[1]; j++) {
         array[i*dims[1] + j] = elems[i][j];
      }
   }

   for(int i = 0; i < (int)dims[0]; i++) {
      env->ReleaseBooleanArrayElements(jArray[i], elems[i], 0);
   }
   delete[] jArray;
   delete[] elems;
   */

   for(int i = 0; i < (int)dims[0]; i++) {
      jbooleanArray jArray = (jbooleanArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      jboolean* elems = env->GetBooleanArrayElements(jArray,0);

      for(int j = 0; j < (int)dims[1]; j++) {
         array[i*dims[1] + j] = elems[j];
      }
      env->ReleaseBooleanArrayElements(jArray, elems, 0);
   }
  
   return new AparapiBuffer((void*)array, (cl_uint*)dims, 2, bitSize, javaBuffer);
}

AparapiBuffer* AparapiBuffer::flattenByte2D(JNIEnv* env, jobject arg) {

   jobject javaBuffer = JNIHelper::getInstanceField<jobject>(env, arg, "javaBuffer", ObjectClassArg);
   cl_uint* dims = new cl_uint[2];
   dims[0] = env->GetArrayLength((jobjectArray)javaBuffer);
   dims[1] = env->GetArrayLength((jbyteArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, 0));
   int totalSize = dims[0] * dims[1];
   long bitSize = totalSize * sizeof(jbyte);

   jbyte* array = new jbyte[totalSize];
   /*
   jbyteArray* jArray = new jbyteArray[dims[0]];
   jbyte** elems = new jbyte*[dims[0]];

   for(int i = 0; i < (int)dims[0]; i++) {
      jArray[i] = (jbyteArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      elems[i] = env->GetByteArrayElements(jArray[i],0);
   }

   #pragma omp parallel for
   for(int i = 0; i < (int)dims[0]; i++) {
      for(int j = 0; j < (int)dims[1]; j++) {
         array[i*dims[1] + j] = elems[i][j];
      }
   }

   for(int i = 0; i < (int)dims[0]; i++) {
      env->ReleaseByteArrayElements(jArray[i], elems[i], 0);
   }
   delete[] jArray;
   delete[] elems;
   */

   for(int i = 0; i < (int)dims[0]; i++) {
      jbyteArray jArray = (jbyteArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      jbyte* elems = env->GetByteArrayElements(jArray,0);

      for(int j = 0; j < (int)dims[1]; j++) {
         array[i*dims[1] + j] = elems[j];
      }
      env->ReleaseByteArrayElements(jArray, elems, 0);
   }
  
   return new AparapiBuffer((void*)array, (cl_uint*)dims, 2, bitSize, javaBuffer);
}

AparapiBuffer* AparapiBuffer::flattenShort2D(JNIEnv* env, jobject arg) {

   jobject javaBuffer = JNIHelper::getInstanceField<jobject>(env, arg, "javaBuffer", ObjectClassArg);
   cl_uint* dims = new cl_uint[2];
   dims[0] = env->GetArrayLength((jobjectArray)javaBuffer);
   dims[1] = env->GetArrayLength((jshortArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, 0));
   int totalSize = dims[0] * dims[1];
   long bitSize = totalSize * sizeof(jshort);

   jshort* array = new jshort[totalSize];
   /*
   jshortArray* jArray = new jshortArray[dims[0]];
   jshort** elems = new jshort*[dims[0]];

   for(int i = 0; i < (int)dims[0]; i++) {
      jArray[i] = (jshortArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      elems[i] = env->GetShortArrayElements(jArray[i],0);
   }

   #pragma omp parallel for
   for(int i = 0; i < (int)dims[0]; i++) {
      for(int j = 0; j < (int)dims[1]; j++) {
         array[i*dims[1] + j] = elems[i][j];
      }
   }

   for(int i = 0; i < (int)dims[0]; i++) {
      env->ReleaseShortArrayElements(jArray[i], elems[i], 0);
   }
   delete[] jArray;
   delete[] elems;
   */

   for(int i = 0; i < (int)dims[0]; i++) {
      jshortArray jArray = (jshortArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      jshort* elems = env->GetShortArrayElements(jArray,0);

      for(int j = 0; j < (int)dims[1]; j++) {
         array[i*dims[1] + j] = elems[j];
      }
      env->ReleaseShortArrayElements(jArray, elems, 0);
   }
  
   return new AparapiBuffer((void*)array, (cl_uint*)dims, 2, bitSize, javaBuffer);
}

AparapiBuffer* AparapiBuffer::flattenInt2D(JNIEnv* env, jobject arg) {

   jobject javaBuffer = JNIHelper::getInstanceField<jobject>(env, arg, "javaBuffer", ObjectClassArg);
   cl_uint* dims = new cl_uint[2];
   dims[0] = env->GetArrayLength((jobjectArray)javaBuffer);
   dims[1] = env->GetArrayLength((jintArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, 0));
   int totalSize = dims[0] * dims[1];
   long bitSize = totalSize * sizeof(jint);

   jint* array = new jint[totalSize];
   /*
   jintArray* jArray = new jintArray[dims[0]];
   jint** elems = new jint*[dims[0]];

   for(int i = 0; i < (int)dims[0]; i++) {
      jArray[i] = (jintArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      elems[i] = env->GetIntArrayElements(jArray[i],0);
   }

   #pragma omp parallel for
   for(int i = 0; i < (int)dims[0]; i++) {
      for(int j = 0; j < (int)dims[1]; j++) {
         array[i*dims[1] + j] = elems[i][j];
      }
   }

   for(int i = 0; i < (int)dims[0]; i++) {
      env->ReleaseIntArrayElements(jArray[i], elems[i], 0);
   }
   delete[] jArray;
   delete[] elems;
   */

   for(int i = 0; i < (int)dims[0]; i++) {
      jintArray jArray = (jintArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      jint* elems = env->GetIntArrayElements(jArray,0);

      for(int j = 0; j < (int)dims[1]; j++) {
         array[i*dims[1] + j] = elems[j];
      }
      env->ReleaseIntArrayElements(jArray, elems, 0);
   }
  
   return new AparapiBuffer((void*)array, (cl_uint*)dims, 2, bitSize, javaBuffer);
}

AparapiBuffer* AparapiBuffer::flattenLong2D(JNIEnv* env, jobject arg) {

   jobject javaBuffer = JNIHelper::getInstanceField<jobject>(env, arg, "javaBuffer", ObjectClassArg);
   cl_uint* dims = new cl_uint[2];
   dims[0] = env->GetArrayLength((jobjectArray)javaBuffer);
   dims[1] = env->GetArrayLength((jlongArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, 0));
   int totalSize = dims[0] * dims[1];
   long bitSize = totalSize * sizeof(jlong);

   jlong* array = new jlong[totalSize];
   /*
   jlongArray* jArray = new jlongArray[dims[0]];
   jlong** elems = new jlong*[dims[0]];

   for(int i = 0; i < (int)dims[0]; i++) {
      jArray[i] = (jlongArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      elems[i] = env->GetLongArrayElements(jArray[i],0);
   }

   #pragma omp parallel for
   for(int i = 0; i < (int)dims[0]; i++) {
      for(int j = 0; j < (int)dims[1]; j++) {
         array[i*dims[1] + j] = elems[i][j];
      }
   }

   for(int i = 0; i < (int)dims[0]; i++) {
      env->ReleaseLongArrayElements(jArray[i], elems[i], 0);
   }
   delete[] jArray;
   delete[] elems;
   */

   for(int i = 0; i < (int)dims[0]; i++) {
      jlongArray jArray = (jlongArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      jlong* elems = env->GetLongArrayElements(jArray,0);

      for(int j = 0; j < (int)dims[1]; j++) {
         array[i*dims[1] + j] = elems[j];
      }
      env->ReleaseLongArrayElements(jArray, elems, 0);
   }
  
   return new AparapiBuffer((void*)array, (cl_uint*)dims, 2, bitSize, javaBuffer);
}

AparapiBuffer* AparapiBuffer::flattenFloat2D(JNIEnv* env, jobject arg) {

   jobject javaBuffer = JNIHelper::getInstanceField<jobject>(env, arg, "javaBuffer", ObjectClassArg);
   cl_uint* dims = new cl_uint[2];
   dims[0] = env->GetArrayLength((jobjectArray)javaBuffer);
   dims[1] = env->GetArrayLength((jfloatArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, 0));
   int totalSize = dims[0] * dims[1];
   long bitSize = totalSize * sizeof(jfloat);

   jfloat* array = new jfloat[totalSize];
   /*
   jfloatArray* jArray = new jfloatArray[dims[0]];
   jfloat** elems = new jfloat*[dims[0]];

   for(int i = 0; i < (int)dims[0]; i++) {
      jArray[i] = (jfloatArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      elems[i] = env->GetFloatArrayElements(jArray[i],0);
   }

   #pragma omp parallel for
   for(int i = 0; i < (int)dims[0]; i++) {
      for(int j = 0; j < (int)dims[1]; j++) {
         array[i*dims[1] + j] = elems[i][j];
      }
   }

   for(int i = 0; i < (int)dims[0]; i++) {
      env->ReleaseFloatArrayElements(jArray[i], elems[i], 0);
   }
   delete[] jArray;
   delete[] elems;
   */

   for(int i = 0; i < (int)dims[0]; i++) {
      jfloatArray jArray = (jfloatArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      jfloat* elems = env->GetFloatArrayElements(jArray,0);

      for(int j = 0; j < (int)dims[1]; j++) {
         array[i*dims[1] + j] = elems[j];
      }
      env->ReleaseFloatArrayElements(jArray, elems, 0);
   }
  
   return new AparapiBuffer((void*)array, (cl_uint*)dims, 2, bitSize, javaBuffer);
}

AparapiBuffer* AparapiBuffer::flattenDouble2D(JNIEnv* env, jobject arg) {

   jobject javaBuffer = JNIHelper::getInstanceField<jobject>(env, arg, "javaBuffer", ObjectClassArg);
   cl_uint* dims = new cl_uint[2];
   dims[0] = env->GetArrayLength((jobjectArray)javaBuffer);
   dims[1] = env->GetArrayLength((jdoubleArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, 0));
   int totalSize = dims[0] * dims[1];
   long bitSize = totalSize * sizeof(jdouble);

   jdouble* array = new jdouble[totalSize];
   /*
   jdoubleArray* jArray = new jdoubleArray[dims[0]];
   jdouble** elems = new jdouble*[dims[0]];

   for(int i = 0; i < (int)dims[0]; i++) {
      jArray[i] = (jdoubleArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      elems[i] = env->GetDoubleArrayElements(jArray[i],0);
   }

   #pragma omp parallel for
   for(int i = 0; i < (int)dims[0]; i++) {
      for(int j = 0; j < (int)dims[1]; j++) {
         array[i*dims[1] + j] = elems[i][j];
      }
   }

   for(int i = 0; i < (int)dims[0]; i++) {
      env->ReleaseDoubleArrayElements(jArray[i], elems[i], 0);
   }
   delete[] jArray;
   delete[] elems;
   */

   for(int i = 0; i < (int)dims[0]; i++) {
      jdoubleArray jArray = (jdoubleArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      jdouble* elems = env->GetDoubleArrayElements(jArray,0);

      for(int j = 0; j < (int)dims[1]; j++) {
         array[i*dims[1] + j] = elems[j];
      }
      env->ReleaseDoubleArrayElements(jArray, elems, 0);
   }
  
   return new AparapiBuffer((void*)array, (cl_uint*)dims, 2, bitSize, javaBuffer);
}


AparapiBuffer* AparapiBuffer::flattenBoolean3D(JNIEnv* env, jobject arg) {

   jobject javaBuffer = JNIHelper::getInstanceField<jobject>(env, arg, "javaBuffer", ObjectClassArg);
   cl_uint* dims = new cl_uint[3];
   jobjectArray j0 = (jobjectArray)javaBuffer;
   jobjectArray j1 = (jobjectArray)env->GetObjectArrayElement(j0, 0);
   jbooleanArray j2 = (jbooleanArray)env->GetObjectArrayElement(j1, 0);
   dims[0] = env->GetArrayLength(j0);
   dims[1] = env->GetArrayLength(j1);
   dims[2] = env->GetArrayLength(j2);

   int totalSize = dims[0] * dims[1] * dims[2];
   long bitSize = totalSize * sizeof(jboolean);

   jboolean* array = new jboolean[totalSize];
   /*
   jbooleanArray** jArray = new jbooleanArray*[dims[0]];
   jboolean*** elems = new jboolean**[dims[0]];

   for(int i = 0; i < (int)dims[0]; i++) {
      jArray[i] = new jbooleanArray[dims[1]];
      elems[i] = new jboolean*[dims[1]];
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      for(int j = 0; j < (int)dims[1]; j++) {
         jArray[i][j] = (jbooleanArray)env->GetObjectArrayElement(jrow, j);
         elems[i][j] = env->GetBooleanArrayElements(jArray[i][j],0);
      }
   }

   #pragma omp parallel for
   for(int i = 0; i < (int)dims[0]; i++) {
      for(int j = 0; j < (int)dims[1]; j++) {
         for(int k = 0; k < (int)dims[2]; k++) {
            array[i*dims[1]*dims[2] + j*dims[2] + k] = elems[i][j][k];
         }
      }
   }

   for(int i = 0; i < (int)dims[0]; i++) {
      for(int j = 0; j < (int)dims[1]; j++) {
         env->ReleaseBooleanArrayElements(jArray[i][j], elems[i][j], 0);
      }
      delete[] jArray[i];
      delete[] elems[i];
   }
   delete[] jArray;
   delete[] elems;
   */

   for(int i = 0; i < (int)dims[0]; i++) {
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      for(int j = 0; j < (int)dims[1]; j++) {
         jbooleanArray jArray = (jbooleanArray)env->GetObjectArrayElement(jrow, j);
         jboolean* elems = env->GetBooleanArrayElements(jArray,0);
         for(int k = 0; k < (int)dims[2]; k++) {
            array[i*dims[1]*dims[2] + j*dims[2] + k] = elems[k];
         }
         env->ReleaseBooleanArrayElements(jArray, elems, 0);
      }
   }
  
   return new AparapiBuffer((void*)array, (cl_uint*)dims, 3, bitSize, javaBuffer);
}

AparapiBuffer* AparapiBuffer::flattenByte3D(JNIEnv* env, jobject arg) {

   jobject javaBuffer = JNIHelper::getInstanceField<jobject>(env, arg, "javaBuffer", ObjectClassArg);
   cl_uint* dims = new cl_uint[3];
   jobjectArray j0 = (jobjectArray)javaBuffer;
   jobjectArray j1 = (jobjectArray)env->GetObjectArrayElement(j0, 0);
   jbyteArray j2 = (jbyteArray)env->GetObjectArrayElement(j1, 0);
   dims[0] = env->GetArrayLength(j0);
   dims[1] = env->GetArrayLength(j1);
   dims[2] = env->GetArrayLength(j2);

   int totalSize = dims[0] * dims[1] * dims[2];
   long bitSize = totalSize * sizeof(jbyte);

   jbyte* array = new jbyte[totalSize];
   /*
   jbyteArray** jArray = new jbyteArray*[dims[0]];
   jbyte*** elems = new jbyte**[dims[0]];

   for(int i = 0; i < (int)dims[0]; i++) {
      jArray[i] = new jbyteArray[dims[1]];
      elems[i] = new jbyte*[dims[1]];
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      for(int j = 0; j < (int)dims[1]; j++) {
         jArray[i][j] = (jbyteArray)env->GetObjectArrayElement(jrow, j);
         elems[i][j] = env->GetByteArrayElements(jArray[i][j],0);
      }
   }

   #pragma omp parallel for
   for(int i = 0; i < (int)dims[0]; i++) {
      for(int j = 0; j < (int)dims[1]; j++) {
         for(int k = 0; k < (int)dims[2]; k++) {
            array[i*dims[1]*dims[2] + j*dims[2] + k] = elems[i][j][k];
         }
      }
   }

   for(int i = 0; i < (int)dims[0]; i++) {
      for(int j = 0; j < (int)dims[1]; j++) {
         env->ReleaseByteArrayElements(jArray[i][j], elems[i][j], 0);
      }
      delete[] jArray[i];
      delete[] elems[i];
   }
   delete[] jArray;
   delete[] elems;
   */

   for(int i = 0; i < (int)dims[0]; i++) {
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      for(int j = 0; j < (int)dims[1]; j++) {
         jbyteArray jArray = (jbyteArray)env->GetObjectArrayElement(jrow, j);
         jbyte* elems = env->GetByteArrayElements(jArray,0);
         for(int k = 0; k < (int)dims[2]; k++) {
            array[i*dims[1]*dims[2] + j*dims[2] + k] = elems[k];
         }
         env->ReleaseByteArrayElements(jArray, elems, 0);
      }
   }
  
   return new AparapiBuffer((void*)array, (cl_uint*)dims, 3, bitSize, javaBuffer);
}

AparapiBuffer* AparapiBuffer::flattenShort3D(JNIEnv* env, jobject arg) {

   jobject javaBuffer = JNIHelper::getInstanceField<jobject>(env, arg, "javaBuffer", ObjectClassArg);
   cl_uint* dims = new cl_uint[3];
   jobjectArray j0 = (jobjectArray)javaBuffer;
   jobjectArray j1 = (jobjectArray)env->GetObjectArrayElement(j0, 0);
   jshortArray j2 = (jshortArray)env->GetObjectArrayElement(j1, 0);
   dims[0] = env->GetArrayLength(j0);
   dims[1] = env->GetArrayLength(j1);
   dims[2] = env->GetArrayLength(j2);

   int totalSize = dims[0] * dims[1] * dims[2];
   long bitSize = totalSize * sizeof(jshort);

   jshort* array = new jshort[totalSize];
   /*
   jshortArray** jArray = new jshortArray*[dims[0]];
   jshort*** elems = new jshort**[dims[0]];

   for(int i = 0; i < (int)dims[0]; i++) {
      jArray[i] = new jshortArray[dims[1]];
      elems[i] = new jshort*[dims[1]];
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      for(int j = 0; j < (int)dims[1]; j++) {
         jArray[i][j] = (jshortArray)env->GetObjectArrayElement(jrow, j);
         elems[i][j] = env->GetShortArrayElements(jArray[i][j],0);
      }
   }

   #pragma omp parallel for
   for(int i = 0; i < (int)dims[0]; i++) {
      for(int j = 0; j < (int)dims[1]; j++) {
         for(int k = 0; k < (int)dims[2]; k++) {
            array[i*dims[1]*dims[2] + j*dims[2] + k] = elems[i][j][k];
         }
      }
   }

   for(int i = 0; i < (int)dims[0]; i++) {
      for(int j = 0; j < (int)dims[1]; j++) {
         env->ReleaseShortArrayElements(jArray[i][j], elems[i][j], 0);
      }
      delete[] jArray[i];
      delete[] elems[i];
   }
   delete[] jArray;
   delete[] elems;
   */

   for(int i = 0; i < (int)dims[0]; i++) {
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      for(int j = 0; j < (int)dims[1]; j++) {
         jshortArray jArray = (jshortArray)env->GetObjectArrayElement(jrow, j);
         jshort* elems = env->GetShortArrayElements(jArray,0);
         for(int k = 0; k < (int)dims[2]; k++) {
            array[i*dims[1]*dims[2] + j*dims[2] + k] = elems[k];
         }
         env->ReleaseShortArrayElements(jArray, elems, 0);
      }
   }
  
   return new AparapiBuffer((void*)array, (cl_uint*)dims, 3, bitSize, javaBuffer);
}

AparapiBuffer* AparapiBuffer::flattenInt3D(JNIEnv* env, jobject arg) {

   jobject javaBuffer = JNIHelper::getInstanceField<jobject>(env, arg, "javaBuffer", ObjectClassArg);
   cl_uint* dims = new cl_uint[3];
   jobjectArray j0 = (jobjectArray)javaBuffer;
   jobjectArray j1 = (jobjectArray)env->GetObjectArrayElement(j0, 0);
   jintArray j2 = (jintArray)env->GetObjectArrayElement(j1, 0);
   dims[0] = env->GetArrayLength(j0);
   dims[1] = env->GetArrayLength(j1);
   dims[2] = env->GetArrayLength(j2);

   int totalSize = dims[0] * dims[1] * dims[2];
   long bitSize = totalSize * sizeof(jint);

   jint* array = new jint[totalSize];
   /*
   jintArray** jArray = new jintArray*[dims[0]];
   jint*** elems = new jint**[dims[0]];

   for(int i = 0; i < (int)dims[0]; i++) {
      jArray[i] = new jintArray[dims[1]];
      elems[i] = new jint*[dims[1]];
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      for(int j = 0; j < (int)dims[1]; j++) {
         jArray[i][j] = (jintArray)env->GetObjectArrayElement(jrow, j);
         elems[i][j] = env->GetIntArrayElements(jArray[i][j],0);
      }
   }

   #pragma omp parallel for
   for(int i = 0; i < (int)dims[0]; i++) {
      for(int j = 0; j < (int)dims[1]; j++) {
         for(int k = 0; k < (int)dims[2]; k++) {
            array[i*dims[1]*dims[2] + j*dims[2] + k] = elems[i][j][k];
         }
      }
   }

   for(int i = 0; i < (int)dims[0]; i++) {
      for(int j = 0; j < (int)dims[1]; j++) {
         env->ReleaseIntArrayElements(jArray[i][j], elems[i][j], 0);
      }
      delete[] jArray[i];
      delete[] elems[i];
   }
   delete[] jArray;
   delete[] elems;
   */

   for(int i = 0; i < (int)dims[0]; i++) {
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      for(int j = 0; j < (int)dims[1]; j++) {
         jintArray jArray = (jintArray)env->GetObjectArrayElement(jrow, j);
         jint* elems = env->GetIntArrayElements(jArray,0);
         for(int k = 0; k < (int)dims[2]; k++) {
            array[i*dims[1]*dims[2] + j*dims[2] + k] = elems[k];
         }
         env->ReleaseIntArrayElements(jArray, elems, 0);
      }
   }
  
   return new AparapiBuffer((void*)array, (cl_uint*)dims, 3, bitSize, javaBuffer);
}

AparapiBuffer* AparapiBuffer::flattenLong3D(JNIEnv* env, jobject arg) {

   jobject javaBuffer = JNIHelper::getInstanceField<jobject>(env, arg, "javaBuffer", ObjectClassArg);
   cl_uint* dims = new cl_uint[3];
   jobjectArray j0 = (jobjectArray)javaBuffer;
   jobjectArray j1 = (jobjectArray)env->GetObjectArrayElement(j0, 0);
   jlongArray j2 = (jlongArray)env->GetObjectArrayElement(j1, 0);
   dims[0] = env->GetArrayLength(j0);
   dims[1] = env->GetArrayLength(j1);
   dims[2] = env->GetArrayLength(j2);

   int totalSize = dims[0] * dims[1] * dims[2];
   jlong bitSize = totalSize * sizeof(jlong);

   jlong* array = new jlong[totalSize];
   /*
   jlongArray** jArray = new jlongArray*[dims[0]];
   jlong*** elems = new jlong**[dims[0]];

   for(int i = 0; i < (int)dims[0]; i++) {
      jArray[i] = new jlongArray[dims[1]];
      elems[i] = new jlong*[dims[1]];
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      for(int j = 0; j < (int)dims[1]; j++) {
         jArray[i][j] = (jlongArray)env->GetObjectArrayElement(jrow, j);
         elems[i][j] = env->GetLongArrayElements(jArray[i][j],0);
      }
   }

   #pragma omp parallel for
   for(int i = 0; i < (int)dims[0]; i++) {
      for(int j = 0; j < (int)dims[1]; j++) {
         for(int k = 0; k < (int)dims[2]; k++) {
            array[i*dims[1]*dims[2] + j*dims[2] + k] = elems[i][j][k];
         }
      }
   }

   for(int i = 0; i < (int)dims[0]; i++) {
      for(int j = 0; j < (int)dims[1]; j++) {
         env->ReleaseLongArrayElements(jArray[i][j], elems[i][j], 0);
      }
      delete[] jArray[i];
      delete[] elems[i];
   }
   delete[] jArray;
   delete[] elems;
   */

   for(int i = 0; i < (int)dims[0]; i++) {
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      for(int j = 0; j < (int)dims[1]; j++) {
         jlongArray jArray = (jlongArray)env->GetObjectArrayElement(jrow, j);
         jlong* elems = env->GetLongArrayElements(jArray,0);
         for(int k = 0; k < (int)dims[2]; k++) {
            array[i*dims[1]*dims[2] + j*dims[2] + k] = elems[k];
         }
         env->ReleaseLongArrayElements(jArray, elems, 0);
      }
   }
  
   return new AparapiBuffer((void*)array, (cl_uint*)dims, 3, bitSize, javaBuffer);
}

AparapiBuffer* AparapiBuffer::flattenFloat3D(JNIEnv* env, jobject arg) {

   jobject javaBuffer = JNIHelper::getInstanceField<jobject>(env, arg, "javaBuffer", ObjectClassArg);
   cl_uint* dims = new cl_uint[3];
   jobjectArray j0 = (jobjectArray)javaBuffer;
   jobjectArray j1 = (jobjectArray)env->GetObjectArrayElement(j0, 0);
   jfloatArray j2 = (jfloatArray)env->GetObjectArrayElement(j1, 0);
   dims[0] = env->GetArrayLength(j0);
   dims[1] = env->GetArrayLength(j1);
   dims[2] = env->GetArrayLength(j2);

   int totalSize = dims[0] * dims[1] * dims[2];
   long bitSize = totalSize * sizeof(jfloat);

   jfloat* array = new jfloat[totalSize];
   /*
   jfloatArray** jArray = new jfloatArray*[dims[0]];
   jfloat*** elems = new jfloat**[dims[0]];

   for(int i = 0; i < (int)dims[0]; i++) {
      jArray[i] = new jfloatArray[dims[1]];
      elems[i] = new jfloat*[dims[1]];
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      for(int j = 0; j < (int)dims[1]; j++) {
         jArray[i][j] = (jfloatArray)env->GetObjectArrayElement(jrow, j);
         elems[i][j] = env->GetFloatArrayElements(jArray[i][j],0);
      }
   }

   #pragma omp parallel for
   for(int i = 0; i < (int)dims[0]; i++) {
      for(int j = 0; j < (int)dims[1]; j++) {
         for(int k = 0; k < (int)dims[2]; k++) {
            array[i*dims[1]*dims[2] + j*dims[2] + k] = elems[i][j][k];
         }
      }
   }

   for(int i = 0; i < (int)dims[0]; i++) {
      for(int j = 0; j < (int)dims[1]; j++) {
         env->ReleaseFloatArrayElements(jArray[i][j], elems[i][j], 0);
      }
      delete[] jArray[i];
      delete[] elems[i];
   }
   delete[] jArray;
   delete[] elems;
   */

   for(int i = 0; i < (int)dims[0]; i++) {
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      for(int j = 0; j < (int)dims[1]; j++) {
         jfloatArray jArray = (jfloatArray)env->GetObjectArrayElement(jrow, j);
         jfloat* elems = env->GetFloatArrayElements(jArray,0);
         for(int k = 0; k < (int)dims[2]; k++) {
            array[i*dims[1]*dims[2] + j*dims[2] + k] = elems[k];
         }
         env->ReleaseFloatArrayElements(jArray, elems, 0);
      }
   }
  
   return new AparapiBuffer((void*)array, (cl_uint*)dims, 3, bitSize, javaBuffer);
}

AparapiBuffer* AparapiBuffer::flattenDouble3D(JNIEnv* env, jobject arg) {

   jobject javaBuffer = JNIHelper::getInstanceField<jobject>(env, arg, "javaBuffer", ObjectClassArg);
   cl_uint* dims = new cl_uint[3];
   jobjectArray j0 = (jobjectArray)javaBuffer;
   jobjectArray j1 = (jobjectArray)env->GetObjectArrayElement(j0, 0);
   jdoubleArray j2 = (jdoubleArray)env->GetObjectArrayElement(j1, 0);
   dims[0] = env->GetArrayLength(j0);
   dims[1] = env->GetArrayLength(j1);
   dims[2] = env->GetArrayLength(j2);

   int totalSize = dims[0] * dims[1] * dims[2];
   long bitSize = totalSize * sizeof(jdouble);

   jdouble* array = new jdouble[totalSize];
   /*
   jdoubleArray** jArray = new jdoubleArray*[dims[0]];
   jdouble*** elems = new jdouble**[dims[0]];

   for(int i = 0; i < (int)dims[0]; i++) {
      jArray[i] = new jdoubleArray[dims[1]];
      elems[i] = new jdouble*[dims[1]];
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      for(int j = 0; j < (int)dims[1]; j++) {
         jArray[i][j] = (jdoubleArray)env->GetObjectArrayElement(jrow, j);
         elems[i][j] = env->GetDoubleArrayElements(jArray[i][j],0);
      }
   }

   #pragma omp parallel for
   for(int i = 0; i < (int)dims[0]; i++) {
      for(int j = 0; j < (int)dims[1]; j++) {
         for(int k = 0; k < (int)dims[2]; k++) {
            array[i*dims[1]*dims[2] + j*dims[2] + k] = elems[i][j][k];
         }
      }
   }

   for(int i = 0; i < (int)dims[0]; i++) {
      for(int j = 0; j < (int)dims[1]; j++) {
         env->ReleaseDoubleArrayElements(jArray[i][j], elems[i][j], 0);
      }
      delete[] jArray[i];
      delete[] elems[i];
   }
   delete[] jArray;
   delete[] elems;
   */

   for(int i = 0; i < (int)dims[0]; i++) {
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement((jobjectArray)javaBuffer, i);
      for(int j = 0; j < (int)dims[1]; j++) {
         jdoubleArray jArray = (jdoubleArray)env->GetObjectArrayElement(jrow, j);
         jdouble* elems = env->GetDoubleArrayElements(jArray,0);
         for(int k = 0; k < (int)dims[2]; k++) {
            array[i*dims[1]*dims[2] + j*dims[1] + k] = elems[k];
         }
         env->ReleaseDoubleArrayElements(jArray, elems, 0);
      }
   }
  
   return new AparapiBuffer((void*)array, (cl_uint*)dims, 3, bitSize, javaBuffer);
}



void AparapiBuffer::inflate(JNIEnv* env, KernelArg* arg) {
   javaObject = JNIHelper::getInstanceField<jobject>(env, arg->javaArg, "javaBuffer", ObjectClassArg);
   if(numDims == 2 && arg->isBoolean()) {
      AparapiBuffer::inflateBoolean2D(env, arg);
   } else if(numDims == 2 && arg->isByte()) {
      AparapiBuffer::inflateByte2D(env, arg);
   } else if(numDims == 2 && arg->isShort()) {
      AparapiBuffer::inflateShort2D(env, arg);
   } else if(numDims == 2 && arg->isInt()) {
      AparapiBuffer::inflateInt2D(env, arg);
   } else if(numDims == 2 && arg->isLong()) {
      AparapiBuffer::inflateLong2D(env, arg);
   } else if(numDims == 2 && arg->isFloat()) {
      AparapiBuffer::inflateFloat2D(env, arg);
   } else if(numDims == 2 && arg->isDouble()) {
      AparapiBuffer::inflateDouble2D(env, arg);
   } else if(numDims == 3 && arg->isBoolean()) {
      AparapiBuffer::inflateBoolean3D(env, arg);
   } else if(numDims == 3 && arg->isByte()) {
      AparapiBuffer::inflateByte3D(env, arg);
   } else if(numDims == 3 && arg->isShort()) {
      AparapiBuffer::inflateShort3D(env, arg);
   } else if(numDims == 3 && arg->isInt()) {
      AparapiBuffer::inflateInt3D(env, arg);
   } else if(numDims == 3 && arg->isLong()) {
      AparapiBuffer::inflateLong3D(env, arg);
   } else if(numDims == 3 && arg->isFloat()) {
      AparapiBuffer::inflateFloat3D(env, arg);
   } else if(numDims == 3 && arg->isDouble()) {
      AparapiBuffer::inflateDouble3D(env, arg);
   } else {
       return;
   }

   deleteBuffer(arg);
}


void AparapiBuffer::inflateBoolean2D(JNIEnv *env, KernelArg* arg) {
   
   jobjectArray buffer = (jobjectArray)javaObject;
   jboolean* array = (jboolean*)data;
   /*
   jbooleanArray* jArray = new jbooleanArray[lens[0]];
   jboolean** body =  new jboolean*[lens[0]];

   for(int i = 0; i < lens[0]; i++) {
      jArray[i] = (jbooleanArray)env->GetObjectArrayElement(buffer, i);
      body[i] = env->GetBooleanArrayElements(jArray[i],0);
   }

   #pragma omp parallel for
   for(int i = 0; i < lens[0]; i++) {
      for(int j = 0; j < lens[1]; j++) {
         body[i][j] = array[i*dims[0] + j];
      }
   }

   for(int i = 0; i < lens[0]; i++) {
      env->ReleaseBooleanArrayElements(jArray[i], body[i], 0);
   }
   delete[] jArray;
   delete[] body;
   */

   for(int i = 0; i < lens[0]; i++) {
      jbooleanArray jArray = (jbooleanArray)env->GetObjectArrayElement(buffer, i);
      jboolean* body = env->GetBooleanArrayElements(jArray,0);
      for(int j = 0; j < lens[1]; j++) {
         body[j] = array[i*dims[0] + j];
      }
      env->ReleaseBooleanArrayElements(jArray, body, 0);
   }
}

void AparapiBuffer::inflateByte2D(JNIEnv *env, KernelArg* arg) {
   
   jobjectArray buffer = (jobjectArray)javaObject;
   jbyte* array = (jbyte*)data;
   /*
   jbyteArray* jArray = new jbyteArray[lens[0]];
   jbyte** body =  new jbyte*[lens[0]];

   for(int i = 0; i < lens[0]; i++) {
      jArray[i] = (jbyteArray)env->GetObjectArrayElement(buffer, i);
      body[i] = env->GetByteArrayElements(jArray[i],0);
   }

   #pragma omp parallel for
   for(int i = 0; i < lens[0]; i++) {
      for(int j = 0; j < lens[1]; j++) {
         body[i][j] = array[i*dims[0] + j];
      }
   }

   for(int i = 0; i < lens[0]; i++) {
      env->ReleaseByteArrayElements(jArray[i], body[i], 0);
   }
   delete[] jArray;
   delete[] body;
   */

   for(int i = 0; i < lens[0]; i++) {
      jbyteArray jArray = (jbyteArray)env->GetObjectArrayElement(buffer, i);
      jbyte* body = env->GetByteArrayElements(jArray,0);
      for(int j = 0; j < lens[1]; j++) {
         body[j] = array[i*dims[0] + j];
      }
      env->ReleaseByteArrayElements(jArray, body, 0);
   }
}

void AparapiBuffer::inflateShort2D(JNIEnv *env, KernelArg* arg) {
   
   jobjectArray buffer = (jobjectArray)javaObject;
   jshort* array = (jshort*)data;
   /*
   jshortArray* jArray = new jshortArray[lens[0]];
   jshort** body =  new jshort*[lens[0]];

   for(int i = 0; i < lens[0]; i++) {
      jArray[i] = (jshortArray)env->GetObjectArrayElement(buffer, i);
      body[i] = env->GetShortArrayElements(jArray[i],0);
   }

   #pragma omp parallel for
   for(int i = 0; i < lens[0]; i++) {
      for(int j = 0; j < lens[1]; j++) {
         body[i][j] = array[i*dims[0] + j];
      }
   }

   for(int i = 0; i < lens[0]; i++) {
      env->ReleaseShortArrayElements(jArray[i], body[i], 0);
   }
   delete[] jArray;
   delete[] body;
   */

   for(int i = 0; i < lens[0]; i++) {
      jshortArray jArray = (jshortArray)env->GetObjectArrayElement(buffer, i);
      jshort* body = env->GetShortArrayElements(jArray,0);
      for(int j = 0; j < lens[1]; j++) {
         body[j] = array[i*dims[0] + j];
      }
      env->ReleaseShortArrayElements(jArray, body, 0);
   }
}

void AparapiBuffer::inflateInt2D(JNIEnv *env, KernelArg* arg) {
   
   jobjectArray buffer = (jobjectArray)javaObject;
   jint* array = (jint*)data;
   /*
   jintArray* jArray = new jintArray[lens[0]];
   jint** body =  new jint*[lens[0]];

   for(int i = 0; i < lens[0]; i++) {
      jArray[i] = (jintArray)env->GetObjectArrayElement(buffer, i);
      body[i] = env->GetIntArrayElements(jArray[i],0);
   }

   #pragma omp parallel for
   for(int i = 0; i < lens[0]; i++) {
      for(int j = 0; j < lens[1]; j++) {
         body[i][j] = array[i*dims[0] + j];
      }
   }

   for(int i = 0; i < lens[0]; i++) {
      env->ReleaseIntArrayElements(jArray[i], body[i], 0);
   }
   delete[] jArray;
   delete[] body;
   */

   for(int i = 0; i < lens[0]; i++) {
      jintArray jArray = (jintArray)env->GetObjectArrayElement(buffer, i);
      jint* body = env->GetIntArrayElements(jArray,0);
      for(int j = 0; j < lens[1]; j++) {
         body[j] = array[i*dims[0] + j];
      }
      env->ReleaseIntArrayElements(jArray, body, 0);
   }
}

void AparapiBuffer::inflateLong2D(JNIEnv *env, KernelArg* arg) {
   
   jobjectArray buffer = (jobjectArray)javaObject;
   jlong* array = (jlong*)data;
   /*
   jlongArray* jArray = new jlongArray[lens[0]];
   jlong** body =  new jlong*[lens[0]];

   for(int i = 0; i < lens[0]; i++) {
      jArray[i] = (jlongArray)env->GetObjectArrayElement(buffer, i);
      body[i] = env->GetLongArrayElements(jArray[i],0);
   }

   #pragma omp parallel for
   for(int i = 0; i < lens[0]; i++) {
      for(int j = 0; j < lens[1]; j++) {
         body[i][j] = array[i*dims[0] + j];
      }
   }

   for(int i = 0; i < lens[0]; i++) {
      env->ReleaseLongArrayElements(jArray[i], body[i], 0);
   }
   delete[] jArray;
   delete[] body;
   */

   for(int i = 0; i < lens[0]; i++) {
      jlongArray jArray = (jlongArray)env->GetObjectArrayElement(buffer, i);
      jlong* body = env->GetLongArrayElements(jArray,0);
      for(int j = 0; j < lens[1]; j++) {
         body[j] = array[i*dims[0] + j];
      }
      env->ReleaseLongArrayElements(jArray, body, 0);
   }
}

void AparapiBuffer::inflateFloat2D(JNIEnv *env, KernelArg* arg) {
   
   jobjectArray buffer = (jobjectArray)javaObject;
   jfloat* array = (jfloat*)data;
   /*
   jfloatArray* jArray = new jfloatArray[lens[0]];
   jfloat** body =  new jfloat*[lens[0]];

   for(int i = 0; i < lens[0]; i++) {
      jArray[i] = (jfloatArray)env->GetObjectArrayElement(buffer, i);
      body[i] = env->GetFloatArrayElements(jArray[i],0);
   }

   #pragma omp parallel for
   for(int i = 0; i < lens[0]; i++) {
      for(int j = 0; j < lens[1]; j++) {
         body[i][j] = array[i*dims[0] + j];
      }
   }

   for(int i = 0; i < lens[0]; i++) {
      env->ReleaseFloatArrayElements(jArray[i], body[i], 0);
   }
   delete[] jArray;
   delete[] body;
   */

   for(int i = 0; i < lens[0]; i++) {
      jfloatArray jArray = (jfloatArray)env->GetObjectArrayElement(buffer, i);
      jfloat* body = env->GetFloatArrayElements(jArray,0);
      for(int j = 0; j < lens[1]; j++) {
         body[j] = array[i*dims[0] + j];
      }
      env->ReleaseFloatArrayElements(jArray, body, 0);
   }
}

void AparapiBuffer::inflateDouble2D(JNIEnv *env, KernelArg* arg) {
   
   jobjectArray buffer = (jobjectArray)javaObject;
   jdouble* array = (jdouble*)data;
   /*
   jdoubleArray* jArray = new jdoubleArray[lens[0]];
   jdouble** body =  new jdouble*[lens[0]];

   for(int i = 0; i < lens[0]; i++) {
      jArray[i] = (jdoubleArray)env->GetObjectArrayElement(buffer, i);
      body[i] = env->GetDoubleArrayElements(jArray[i],0);
   }

   #pragma omp parallel for
   for(int i = 0; i < lens[0]; i++) {
      for(int j = 0; j < lens[1]; j++) {
         body[i][j] = array[i*dims[0] + j];
      }
   }

   for(int i = 0; i < lens[0]; i++) {
      env->ReleaseDoubleArrayElements(jArray[i], body[i], 0);
   }
   delete[] jArray;
   delete[] body;
   */

   for(int i = 0; i < lens[0]; i++) {
      jdoubleArray jArray = (jdoubleArray)env->GetObjectArrayElement(buffer, i);
      jdouble* body = env->GetDoubleArrayElements(jArray,0);
      for(int j = 0; j < lens[1]; j++) {
         body[j] = array[i*dims[0] + j];
      }
      env->ReleaseDoubleArrayElements(jArray, body, 0);
   }
}


void AparapiBuffer::inflateBoolean3D(JNIEnv *env, KernelArg* arg) {
   
   jobjectArray buffer = (jobjectArray)javaObject;
   jboolean* array = (jboolean*)data;
   /*
   jbooleanArray** jArray = new jbooleanArray*[lens[0]];
   jboolean*** body =  new jboolean**[lens[0]];

   for(int i = 0; i < lens[0]; i++) {
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement(buffer, i);
      jArray[i] = new jbooleanArray[lens[0]];
      body[i] =  new jboolean*[lens[0]];
      for(int j = 0; j < lens[1]; j++) {
         jArray[i][j] = (jbooleanArray)env->GetObjectArrayElement(jrow, j);
         body[i][j] = env->GetBooleanArrayElements(jArray[i][j],0);
      }
   }

   #pragma omp parallel for
   for(int i = 0; i < lens[0]; i++) {
      for(int j = 0; j < lens[1]; j++) {
         for(int k = 0; k < lens[2]; k++) {
            body[i][j][k] = array[i*dims[0] + j*dims[1] + k];
         }
      }
   }

   for(int i = 0; i < lens[0]; i++) {
      for(int j = 0; j < lens[1]; j++) {
         env->ReleaseBooleanArrayElements(jArray[i][j], body[i][j], 0);
      }
      delete[] jArray[i];
      delete[] body[i];
   }
   delete[] jArray;
   delete[] body;
   */

   for(int i = 0; i < lens[0]; i++) {
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement(buffer, i);
      for(int j = 0; j < lens[1]; j++) {
         jbooleanArray jArray = (jbooleanArray)env->GetObjectArrayElement(jrow, j);
         jboolean* body = env->GetBooleanArrayElements(jArray,0);
         for(int k = 0; k < lens[2]; k++) {
            body[k] = array[i*dims[0] + j*dims[1] + k];
         }
         env->ReleaseBooleanArrayElements(jArray, body, 0);
      }
   }
}

void AparapiBuffer::inflateByte3D(JNIEnv *env, KernelArg* arg) {
   
   jobjectArray buffer = (jobjectArray)javaObject;
   jbyte* array = (jbyte*)data;
   /*
   jbyteArray** jArray = new jbyteArray*[lens[0]];
   jbyte*** body =  new jbyte**[lens[0]];

   for(int i = 0; i < lens[0]; i++) {
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement(buffer, i);
      jArray[i] = new jbyteArray[lens[0]];
      body[i] =  new jbyte*[lens[0]];
      for(int j = 0; j < lens[1]; j++) {
         jArray[i][j] = (jbyteArray)env->GetObjectArrayElement(jrow, j);
         body[i][j] = env->GetByteArrayElements(jArray[i][j],0);
      }
   }

   #pragma omp parallel for
   for(int i = 0; i < lens[0]; i++) {
      for(int j = 0; j < lens[1]; j++) {
         for(int k = 0; k < lens[2]; k++) {
            body[i][j][k] = array[i*dims[0] + j*dims[1] + k];
         }
      }
   }

   for(int i = 0; i < lens[0]; i++) {
      for(int j = 0; j < lens[1]; j++) {
         env->ReleaseByteArrayElements(jArray[i][j], body[i][j], 0);
      }
      delete[] jArray[i];
      delete[] body[i];
   }
   delete[] jArray;
   delete[] body;
   */

   for(int i = 0; i < lens[0]; i++) {
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement(buffer, i);
      for(int j = 0; j < lens[1]; j++) {
         jbyteArray jArray = (jbyteArray)env->GetObjectArrayElement(jrow, j);
         jbyte* body = env->GetByteArrayElements(jArray,0);
         for(int k = 0; k < lens[2]; k++) {
            body[k] = array[i*dims[0] + j*dims[1] + k];
         }
         env->ReleaseByteArrayElements(jArray, body, 0);
      }
   }
}

void AparapiBuffer::inflateShort3D(JNIEnv *env, KernelArg* arg) {
   
   jobjectArray buffer = (jobjectArray)javaObject;
   jshort* array = (jshort*)data;
   /*
   jshortArray** jArray = new jshortArray*[lens[0]];
   jshort*** body =  new jshort**[lens[0]];

   for(int i = 0; i < lens[0]; i++) {
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement(buffer, i);
      jArray[i] = new jshortArray[lens[0]];
      body[i] =  new jshort*[lens[0]];
      for(int j = 0; j < lens[1]; j++) {
         jArray[i][j] = (jshortArray)env->GetObjectArrayElement(jrow, j);
         body[i][j] = env->GetShortArrayElements(jArray[i][j],0);
      }
   }

   #pragma omp parallel for
   for(int i = 0; i < lens[0]; i++) {
      for(int j = 0; j < lens[1]; j++) {
         for(int k = 0; k < lens[2]; k++) {
            body[i][j][k] = array[i*dims[0] + j*dims[1] + k];
         }
      }
   }

   for(int i = 0; i < lens[0]; i++) {
      for(int j = 0; j < lens[1]; j++) {
         env->ReleaseShortArrayElements(jArray[i][j], body[i][j], 0);
      }
      delete[] jArray[i];
      delete[] body[i];
   }
   delete[] jArray;
   delete[] body;
   */

   for(int i = 0; i < lens[0]; i++) {
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement(buffer, i);
      for(int j = 0; j < lens[1]; j++) {
         jshortArray jArray = (jshortArray)env->GetObjectArrayElement(jrow, j);
         jshort* body = env->GetShortArrayElements(jArray,0);
         for(int k = 0; k < lens[2]; k++) {
            body[k] = array[i*dims[0] + j*dims[1] + k];
         }
         env->ReleaseShortArrayElements(jArray, body, 0);
      }
   }
}

void AparapiBuffer::inflateInt3D(JNIEnv *env, KernelArg* arg) {
   
   jobjectArray buffer = (jobjectArray)javaObject;
   jint* array = (jint*)data;
   /*
   jintArray** jArray = new jintArray*[lens[0]];
   jint*** body =  new jint**[lens[0]];

   for(int i = 0; i < lens[0]; i++) {
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement(buffer, i);
      jArray[i] = new jintArray[lens[0]];
      body[i] =  new jint*[lens[0]];
      for(int j = 0; j < lens[1]; j++) {
         jArray[i][j] = (jintArray)env->GetObjectArrayElement(jrow, j);
         body[i][j] = env->GetIntArrayElements(jArray[i][j],0);
      }
   }

   #pragma omp parallel for
   for(int i = 0; i < lens[0]; i++) {
      for(int j = 0; j < lens[1]; j++) {
         for(int k = 0; k < lens[2]; k++) {
            body[i][j][k] = array[i*dims[0] + j*dims[1] + k];
         }
      }
   }

   for(int i = 0; i < lens[0]; i++) {
      for(int j = 0; j < lens[1]; j++) {
         env->ReleaseIntArrayElements(jArray[i][j], body[i][j], 0);
      }
      delete[] jArray[i];
      delete[] body[i];
   }
   delete[] jArray;
   delete[] body;
   */

   for(int i = 0; i < lens[0]; i++) {
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement(buffer, i);
      for(int j = 0; j < lens[1]; j++) {
         jintArray jArray = (jintArray)env->GetObjectArrayElement(jrow, j);
         jint* body = env->GetIntArrayElements(jArray,0);
         for(int k = 0; k < lens[2]; k++) {
            body[k] = array[i*dims[0] + j*dims[1] + k];
         }
         env->ReleaseIntArrayElements(jArray, body, 0);
      }
   }
}

void AparapiBuffer::inflateLong3D(JNIEnv *env, KernelArg* arg) {
   
   jobjectArray buffer = (jobjectArray)javaObject;
   jlong* array = (jlong*)data;
   /*
   jlongArray** jArray = new jlongArray*[lens[0]];
   jlong*** body =  new jlong**[lens[0]];

   for(int i = 0; i < lens[0]; i++) {
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement(buffer, i);
      jArray[i] = new jlongArray[lens[0]];
      body[i] =  new jlong*[lens[0]];
      for(int j = 0; j < lens[1]; j++) {
         jArray[i][j] = (jlongArray)env->GetObjectArrayElement(jrow, j);
         body[i][j] = env->GetLongArrayElements(jArray[i][j],0);
      }
   }

   #pragma omp parallel for
   for(int i = 0; i < lens[0]; i++) {
      for(int j = 0; j < lens[1]; j++) {
         for(int k = 0; k < lens[2]; k++) {
            body[i][j][k] = array[i*dims[0] + j*dims[1] + k];
         }
      }
   }

   for(int i = 0; i < lens[0]; i++) {
      for(int j = 0; j < lens[1]; j++) {
         env->ReleaseLongArrayElements(jArray[i][j], body[i][j], 0);
      }
      delete[] jArray[i];
      delete[] body[i];
   }
   delete[] jArray;
   delete[] body;
   */

   for(int i = 0; i < lens[0]; i++) {
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement(buffer, i);
      for(int j = 0; j < lens[1]; j++) {
         jlongArray jArray = (jlongArray)env->GetObjectArrayElement(jrow, j);
         jlong* body = env->GetLongArrayElements(jArray,0);
         for(int k = 0; k < lens[2]; k++) {
            body[k] = array[i*dims[0] + j*dims[1] + k];
         }
         env->ReleaseLongArrayElements(jArray, body, 0);
      }
   }
}

void AparapiBuffer::inflateFloat3D(JNIEnv *env, KernelArg* arg) {
   
   jobjectArray buffer = (jobjectArray)javaObject;
   jfloat* array = (jfloat*)data;
   /*
   jfloatArray** jArray = new jfloatArray*[lens[0]];
   jfloat*** body =  new jfloat**[lens[0]];

   for(int i = 0; i < lens[0]; i++) {
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement(buffer, i);
      jArray[i] = new jfloatArray[lens[0]];
      body[i] =  new jfloat*[lens[0]];
      for(int j = 0; j < lens[1]; j++) {
         jArray[i][j] = (jfloatArray)env->GetObjectArrayElement(jrow, j);
         body[i][j] = env->GetFloatArrayElements(jArray[i][j],0);
      }
   }

   #pragma omp parallel for
   for(int i = 0; i < lens[0]; i++) {
      for(int j = 0; j < lens[1]; j++) {
         for(int k = 0; k < lens[2]; k++) {
            body[i][j][k] = array[i*dims[0] + j*dims[1] + k];
         }
      }
   }

   for(int i = 0; i < lens[0]; i++) {
      for(int j = 0; j < lens[1]; j++) {
         env->ReleaseFloatArrayElements(jArray[i][j], body[i][j], 0);
      }
      delete[] jArray[i];
      delete[] body[i];
   }
   delete[] jArray;
   delete[] body;
   */

   for(int i = 0; i < lens[0]; i++) {
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement(buffer, i);
      for(int j = 0; j < lens[1]; j++) {
         jfloatArray jArray = (jfloatArray)env->GetObjectArrayElement(jrow, j);
         jfloat* body = env->GetFloatArrayElements(jArray,0);
         for(int k = 0; k < lens[2]; k++) {
            body[k] = array[i*dims[0] + j*dims[1] + k];
         }
         env->ReleaseFloatArrayElements(jArray, body, 0);
      }
   }
}

void AparapiBuffer::inflateDouble3D(JNIEnv *env, KernelArg* arg) {
   
   jobjectArray buffer = (jobjectArray)javaObject;
   jdouble* array = (jdouble*)data;
   /*
   jdoubleArray** jArray = new jdoubleArray*[lens[0]];
   jdouble*** body =  new jdouble**[lens[0]];

   for(int i = 0; i < lens[0]; i++) {
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement(buffer, i);
      jArray[i] = new jdoubleArray[lens[0]];
      body[i] =  new jdouble*[lens[0]];
      for(int j = 0; j < lens[1]; j++) {
         jArray[i][j] = (jdoubleArray)env->GetObjectArrayElement(jrow, j);
         body[i][j] = env->GetDoubleArrayElements(jArray[i][j],0);
      }
   }

   #pragma omp parallel for
   for(int i = 0; i < lens[0]; i++) {
      for(int j = 0; j < lens[1]; j++) {
         for(int k = 0; k < lens[2]; k++) {
            body[i][j][k] = array[i*dims[0] + j*dims[1] + k];
         }
      }
   }

   for(int i = 0; i < lens[0]; i++) {
      for(int j = 0; j < lens[1]; j++) {
         env->ReleaseDoubleArrayElements(jArray[i][j], body[i][j], 0);
      }
      delete[] jArray[i];
      delete[] body[i];
   }
   delete[] jArray;
   delete[] body;
   */

   for(int i = 0; i < lens[0]; i++) {
      jobjectArray jrow = (jobjectArray)env->GetObjectArrayElement(buffer, i);
      for(int j = 0; j < lens[1]; j++) {
         jdoubleArray jArray = (jdoubleArray)env->GetObjectArrayElement(jrow, j);
         jdouble* body = env->GetDoubleArrayElements(jArray,0);
         for(int k = 0; k < lens[2]; k++) {
            body[k] = array[i*dims[0] + j*dims[1] + k];
         }
         env->ReleaseDoubleArrayElements(jArray, body, 0);
      }
   }
}

void AparapiBuffer::deleteBuffer(KernelArg* arg)
{
      delete[] dims;
      delete[] lens;
   if(arg->isBoolean()) {
      delete[] (jboolean*)data;
   } else if(arg->isByte()) {
      delete[] (jbyte*)data;
   } else if(arg->isShort()) {
      delete[] (jshort*)data;
   } else if(arg->isInt()) {
      delete[] (jint*)data;
   } else if(arg->isLong()) {
      delete[] (jlong*)data;
   } else if(arg->isFloat()) {
      delete[] (jfloat*)data;
   } else if(arg->isDouble()) {
      delete[] (jdouble*)data;
   }
}
