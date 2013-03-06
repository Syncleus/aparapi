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

#include "Common.h"
#define JNI_SOURCE
#include "JNIHelper.h"

void JNIHelper::callVoid(JNIEnv *jenv, jobject instance, const char *methodName, const char *methodSignature, ...){
   try {
      jclass theClass = jenv->GetObjectClass(instance);
      if (theClass == NULL ||  jenv->ExceptionCheck())
         throw std::string("bummer! getting class from instance");

      jmethodID methodId= jenv->GetMethodID(theClass,methodName,methodSignature);
      if (methodId == NULL || jenv->ExceptionCheck())
         throw std::string("bummer getting method '") + methodName + "', '" + methodSignature + "' from instance";

      va_list argp;
      va_start(argp, methodSignature);
      jenv->CallVoidMethodV(instance, methodId, argp);
      va_end(argp);

      if (jenv->ExceptionCheck())
         throw std::string("bummer calling '") + methodName + "' '" + methodSignature;

   } catch(std::string& s) {
      jenv->ExceptionDescribe(); /* write to console */
      jenv->ExceptionClear();
      fprintf(stderr, "%s\n", s.c_str());
   }
}

jobject JNIHelper::callObject(JNIEnv *jenv, jobject instance, const char *methodName, const char *methodSignature, ...){
   jobject value = NULL;
   try {
      jclass theClass = jenv->GetObjectClass(instance);
      if (theClass == NULL ||  jenv->ExceptionCheck())
         throw std::string("bummer! getting class from instance");

      jmethodID methodId= jenv->GetMethodID(theClass,methodName,methodSignature);
      if (methodId == NULL || jenv->ExceptionCheck())
         throw std::string("bummer getting method '") + methodName + "', '" + methodSignature + "' from instance";

      va_list argp;
      va_start(argp, methodSignature);
      jobject value = jenv->CallObjectMethodV(instance, methodId, argp);
      va_end(argp);

      if (jenv->ExceptionCheck())
         throw std::string("bummer calling '") + methodName + "' '" + methodSignature;

   } catch(std::string& s) {
      jenv->ExceptionDescribe(); /* write to console */
      jenv->ExceptionClear();
      fprintf(stderr, "%s\n", s.c_str());
      return 0L;
   }

   return value;
}

jlong JNIHelper::callLong(JNIEnv *jenv, jobject instance, const char *methodName, const char *methodSignature, ...){
   jlong value = 0L;
   try {
      jclass theClass = jenv->GetObjectClass(instance);
      if (theClass == NULL ||  jenv->ExceptionCheck())
         throw std::string("bummer! getting class from instance");

      jmethodID methodId = jenv->GetMethodID(theClass,methodName,methodSignature);
      if (methodId == NULL || jenv->ExceptionCheck())
         throw std::string("bummer getting method '") + methodName + "', '" + methodSignature + "' from instance";

      va_list argp;
      va_start(argp, methodSignature);
      jlong value = jenv->CallLongMethodV(instance, methodId, argp);
      va_end(argp);

      if (jenv->ExceptionCheck())
         throw std::string("bummer calling '") + methodName + "' '" + methodSignature;

   } catch(std::string& s) {
      jenv->ExceptionDescribe(); /* write to console */
      jenv->ExceptionClear();
      fprintf(stderr, "%s\n", s.c_str());
      return 0L;
   }
   return value;
}

jobject JNIHelper::getStaticFieldObject(JNIEnv *jenv, const char *className, const char *fieldName, const char *signature){
   jobject value = NULL;
   try {
      jclass theClass = jenv->FindClass(className);
      if (theClass == NULL ||  jenv->ExceptionCheck())
         throw std::string("bummer! getting '") + className;

      jfieldID fieldId = jenv->GetStaticFieldID(theClass,fieldName,signature);
      if (fieldId == NULL || jenv->ExceptionCheck())
         throw std::string("bummer getting static field '") + fieldName + "' from '" + className + "' with signature! '" + signature;

      value = jenv->GetStaticObjectField(theClass, fieldId);
      if (value == NULL || jenv->ExceptionCheck())
         throw std::string("bummer getting static field  value '") + fieldName + "' from '" + className + "' with signature! '" + signature;

   } catch(std::string& s) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "%s\n", s.c_str());
      return(NULL);
   }

   return(value);
}

jobject JNIHelper::createInstance(JNIEnv *jenv, const char* className, const char *signature, ... ){
   jobject instance;
   try {
      jclass theClass = jenv->FindClass(className);
      if (theClass == NULL || jenv->ExceptionCheck())
         throw std::string("bummer! getting '") + className;

      jmethodID constructor = jenv->GetMethodID(theClass,"<init>",signature);
      if (constructor == NULL || jenv->ExceptionCheck())
         throw std::string("bummer getting constructor from '") + className + "' with signature! '" + signature;

      va_list argp;
      va_start(argp, signature);
      instance = jenv->NewObjectV(theClass, constructor, argp);
      va_end(argp);

      if (instance == NULL || jenv->ExceptionCheck())
         throw std::string("bummer invoking constructor from '") + className + "' with signature! '" + signature;

   } catch(std::string& s) {
      jenv->ExceptionDescribe();
      jenv->ExceptionClear();
      fprintf(stderr, "%s\n", s.c_str());
      return(NULL);
   }
   return(instance);
} 
