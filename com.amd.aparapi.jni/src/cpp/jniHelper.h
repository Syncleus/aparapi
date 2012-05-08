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

#ifndef JNIHELPER_H
#define JNIHELPER_H

#include <jni.h>

class JNIHelper{
   public:
      static void callVoid(JNIEnv *jenv, jobject instance, char *methodName, char *methodSignature, ...);
      static jlong callLong(JNIEnv *jenv, jobject instance, char *methodName, char *methodSignature, ...);
      static jobject callObject(JNIEnv *jenv, jobject instance, char *methodName, char *methodSignature, ...);

      static jobject JNIHelper::createInstance(JNIEnv *jenv, char *className, char *signature, ... );

      static jobject getStaticFieldObject(JNIEnv *jenv, char *className, char *fieldName, char *signature);

      static jint getInstanceFieldInt(JNIEnv *jenv, jobject instance, char *fieldName);
      static jfloat getInstanceFieldFloat(JNIEnv *jenv, jobject instance, char *fieldName);
      static jdouble getInstanceFieldDouble(JNIEnv *jenv, jobject instance, char *fieldName);
      static jshort getInstanceFieldShort(JNIEnv *jenv, jobject instance, char *fieldName);
      static jlong getInstanceFieldLong(JNIEnv *jenv, jobject instance, char *fieldName);
      static jboolean getInstanceFieldBoolean(JNIEnv *jenv, jobject instance, char *fieldName);
      static jobject getInstanceFieldObject(JNIEnv *jenv, jobject instance, char *fieldName, char *signature);

      static void setInstanceFieldInt(JNIEnv* jenv, jobject instance, char *fieldName, jint value);
      static void setInstanceFieldLong(JNIEnv* jenv, jobject instance, char *fieldName, jlong value);
      static void setInstanceFieldBoolean(JNIEnv* jenv, jobject instance, char *fieldName, jboolean value);
      static void setInstanceFieldObject(JNIEnv* jenv, jobject instance, char *fieldName, char *signature, jobject value);
};

#define JNIExceptionChecker(){\
   fprintf(stderr, "line %d\n", __LINE__);\
   if ((jenv)->ExceptionOccurred()) {\
      (jenv)->ExceptionDescribe(); /* write to console */\
      (jenv)->ExceptionClear();\
   }\
}

#define CHECK_NO_RETURN(condition, msg) if(condition){\
   fprintf(stderr, "!!!!!!! %s failed !!!!!!!\n", msg);\
}

#define CHECK_RETURN(condition, msg, val) if(condition){\
   fprintf(stderr, "!!!!!!! %s failed !!!!!!!\n", msg);\
   return val;\
}

#define CHECK(condition, msg) CHECK_RETURN(condition, msg, 0)

#define ASSERT_CL_NO_RETURN(msg) if (status != CL_SUCCESS){\
   fprintf(stderr, "!!!!!!! %s failed: %s\n", msg, CLHelper::errString(status));\
}

#define ASSERT_CL_RETURN(msg, val) if (status != CL_SUCCESS){\
   ASSERT_CL_NO_RETURN(msg)\
   return val;\
}

#define ASSERT_CL(msg) ASSERT_CL_RETURN(msg, 0)

#define PRINT_CL_ERR(status, msg) fprintf(stderr, "!!!!!!! %s failed %s\n", msg, CLHelper::errString(status));

#define ASSERT_FIELD(id) CHECK_NO_RETURN(id##FieldID == 0, "No such field as " #id)

#endif // JNIHELPER_H

