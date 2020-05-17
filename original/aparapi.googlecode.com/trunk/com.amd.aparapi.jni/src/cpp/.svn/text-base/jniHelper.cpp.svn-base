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

#include "common.h"
#define JNI_SOURCE
#include "jniHelper.h"

void JNIHelper::callVoid(JNIEnv *jenv, jobject instance, char *methodName, char *methodSignature, ...){
   jclass theClass = jenv->GetObjectClass(instance);
   if (theClass == NULL ||  jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer! getting class from instance\n");
      return;
   }
   jmethodID methodId= jenv->GetMethodID(theClass,methodName,methodSignature);
   if (methodId == NULL || jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer getting method '%s','%s' from instance \n", methodName, methodSignature);
      return;
   }
   va_list argp;
   va_start(argp, methodSignature);
   jenv->CallVoidMethodV(instance, methodId, argp);
   if (jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); /* write to console */
      jenv->ExceptionClear();
      fprintf(stderr, "bummer  calling '%s %s'\n", methodName, methodSignature);
   }
   va_end(argp);
   return;
}

jobject JNIHelper::callObject(JNIEnv *jenv, jobject instance, char *methodName, char *methodSignature, ...){
   jclass theClass = jenv->GetObjectClass(instance);
   if (theClass == NULL ||  jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer! getting class from instance\n");
      return NULL;
   }
   jmethodID methodId= jenv->GetMethodID(theClass,methodName,methodSignature);
   if (methodId == NULL || jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer getting method '%s','%s' from instance \n", methodName, methodSignature);
      return NULL;
   }
   va_list argp;
   va_start(argp, methodSignature);
   jobject value = jenv->CallObjectMethodV(instance, methodId, argp);
   if (jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); /* write to console */
      jenv->ExceptionClear();
      fprintf(stderr, "bummer  calling '%s %s'\n", methodName, methodSignature);
      return NULL;
   }
   va_end(argp);
   return value;
}

jlong JNIHelper::callLong(JNIEnv *jenv, jobject instance, char *methodName, char *methodSignature, ...){
   jclass theClass = jenv->GetObjectClass(instance);
   if (theClass == NULL ||  jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer! getting class from instance\n");
      return 0L;
   }
   jmethodID methodId= jenv->GetMethodID(theClass,methodName,methodSignature);
   if (methodId == NULL || jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer getting method '%s','%s' from instance \n", methodName, methodSignature);
      return 0L;
   }
   va_list argp;
   va_start(argp, methodSignature);
   jlong value = jenv->CallLongMethodV(instance, methodId, argp);
   if (jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); /* write to console */
      jenv->ExceptionClear();
      fprintf(stderr, "bummer  calling '%s %s'\n", methodName, methodSignature);
      return 0L;
   }
   va_end(argp);
   return value;
}

void JNIHelper::setInstanceFieldInt(JNIEnv* jenv, jobject instance, char *fieldName, jint value){
   jclass theClass = jenv->GetObjectClass(instance);
   if (theClass == NULL ||  jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer! getting class from instance\n");
      return;
   }
   jfieldID fieldId= jenv->GetFieldID(theClass,fieldName,"I");
   if (fieldId == NULL || jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer getting int field '%s' \n", fieldName);
      return;
   }
   jenv->SetIntField(instance, fieldId, value);
   if (jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer setting int field  '%s' \n", fieldName);
      return;
   }
}

void JNIHelper::setInstanceFieldLong(JNIEnv* jenv, jobject instance, char *fieldName, jlong value){
   jclass theClass = jenv->GetObjectClass(instance);
   if (theClass == NULL ||  jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer! getting class from instance\n");
      return;
   }
   jfieldID fieldId= jenv->GetFieldID(theClass,fieldName,"J");
   if (fieldId == NULL || jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer getting long field '%s' \n", fieldName);
      return;
   }
   jenv->SetLongField(instance, fieldId, value);
   if (jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer setting long field  '%s' \n", fieldName);
      return;
   }
}
void JNIHelper::setInstanceFieldBoolean(JNIEnv* jenv, jobject instance, char *fieldName, jboolean value){
   jclass theClass = jenv->GetObjectClass(instance);
   if (theClass == NULL ||  jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer! getting class from instance\n");
      return;
   }
   jfieldID fieldId= jenv->GetFieldID(theClass,fieldName,"Z");
   if (fieldId == NULL || jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer getting boolean field id '%s' \n", fieldName);
      return;
   }
   jenv->SetBooleanField(instance, fieldId, value);
   if (jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer setting boolean field  '%s' \n", fieldName);
      return;
   }
}

void JNIHelper::setInstanceFieldObject(JNIEnv* jenv, jobject instance, char *fieldName, char *signature, jobject value){
   jclass theClass = jenv->GetObjectClass(instance);
   if (theClass == NULL ||  jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer! getting class from instance\n");
      return;
   }
   jfieldID fieldId= jenv->GetFieldID(theClass,fieldName, signature);
   if (fieldId == NULL || jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer getting %s object '%s' \n", signature, fieldName);
      return;
   }
   jenv->SetObjectField(instance, fieldId, value);
   if (jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer setting %s  object  '%s' \n", signature, fieldName);
      return;
   }
}

jobject JNIHelper::getStaticFieldObject(JNIEnv *jenv, char *className, char *fieldName, char *signature){
   jclass theClass = jenv->FindClass(className);
   if (theClass == NULL ||  jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe();
      jenv->ExceptionClear();
      fprintf(stderr, "bummer! getting '%s'\n", className);
      return(NULL);
   }
   jfieldID fieldId= jenv->GetStaticFieldID(theClass,fieldName,signature);
   if (fieldId == NULL || jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer getting static field '%s' from '%s' with signature! '%s' \n", fieldName, className, signature);
      return(NULL);
   }

   jobject value = jenv->GetStaticObjectField(theClass, fieldId);
   if (value == NULL || jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer getting static field value '%s' from '%s' with signature! '%s' \n", fieldName, className, signature);
      return(NULL);
   }

   return(value);
}
jobject JNIHelper::createInstance(JNIEnv *jenv, char* className, char *signature, ... ){
   jclass theClass = jenv->FindClass(className);
   if (theClass == NULL ||  jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe();
      jenv->ExceptionClear();
      fprintf(stderr, "bummer! getting '%s'\n", className);
      return(NULL);
   }

   jmethodID constructor= jenv->GetMethodID(theClass,"<init>",signature);
   if (constructor == NULL || jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer getting constructor from '%s' with signature! '%s' \n", className, signature);
      return(NULL);
   }
   va_list argp;
   va_start(argp, signature);
   jobject instance = jenv->NewObjectV(theClass, constructor, argp);
   if (instance == NULL || jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer invoking constructor from '%s' with signature! '%s' \n", className, signature);
   }
   va_end(argp);
   return(instance);
} 

jint JNIHelper::getInstanceFieldInt(JNIEnv *jenv, jobject instance, char *fieldName){
   jclass theClass = jenv->GetObjectClass(instance);
   if (theClass == NULL ||  jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer! getting class from instance\n");
      return 0;
   }
   jfieldID fieldId= jenv->GetFieldID(theClass,fieldName,"I");
   if (fieldId == NULL || jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer getting int field '%s' \n", fieldName);
      return 0;
   }
   jint value= jenv->GetIntField(instance, fieldId);
   if (jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer getting int field value '%s' \n", fieldName);
      return 0;
   }
   return(value);
}

jfloat JNIHelper::getInstanceFieldFloat(JNIEnv *jenv, jobject instance, char *fieldName){
   jclass theClass = jenv->GetObjectClass(instance);
   if (theClass == NULL ||  jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer! getting class from instance\n");
      return 0;
   }
   jfieldID fieldId= jenv->GetFieldID(theClass,fieldName,"F");
   if (fieldId == NULL || jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer getting int field '%s' \n", fieldName);
      return 0;
   }
   jfloat value= jenv->GetFloatField(instance, fieldId);
   if (jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer getting int field value '%s' \n", fieldName);
      return 0;
   }
   return(value);
}

jdouble JNIHelper::getInstanceFieldDouble(JNIEnv *jenv, jobject instance, char *fieldName){
   jclass theClass = jenv->GetObjectClass(instance);
   if (theClass == NULL ||  jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer! getting class from instance\n");
      return 0;
   }
   jfieldID fieldId= jenv->GetFieldID(theClass,fieldName,"D");
   if (fieldId == NULL || jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer getting int field '%s' \n", fieldName);
      return 0;
   }
   jdouble value= jenv->GetDoubleField(instance, fieldId);
   if (jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer getting int field value '%s' \n", fieldName);
      return 0;
   }
   return(value);
}
jshort JNIHelper::getInstanceFieldShort(JNIEnv *jenv, jobject instance, char *fieldName){
   jclass theClass = jenv->GetObjectClass(instance);
   if (theClass == NULL ||  jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer! getting class from instance\n");
      return 0;
   }
   jfieldID fieldId= jenv->GetFieldID(theClass,fieldName,"H");
   if (fieldId == NULL || jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer getting int field '%s' \n", fieldName);
      return 0;
   }
   jshort value= jenv->GetShortField(instance, fieldId);
   if (jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer getting int field value '%s' \n", fieldName);
      return 0;
   }
   return(value);
}

jlong JNIHelper::getInstanceFieldLong(JNIEnv *jenv, jobject instance, char *fieldName){
   jclass theClass = jenv->GetObjectClass(instance);
   if (theClass == NULL ||  jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer! getting class from instance\n");
      return 0;
   }
   jfieldID fieldId= jenv->GetFieldID(theClass,fieldName,"J");
   if (fieldId == NULL || jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer getting long field '%s' \n", fieldName);
      return 0;
   }
   jlong value= jenv->GetLongField(instance, fieldId);
   if (jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer getting long field value  '%s' \n", fieldName);
      return 0;
   }
   return(value);
}
jobject JNIHelper::getInstanceFieldObject(JNIEnv *jenv, jobject instance, char *fieldName, char *signature){
   jclass theClass = jenv->GetObjectClass(instance);
   if (theClass == NULL ||  jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer! getting class from instance\n");
      return NULL;
   }
   jfieldID fieldId= jenv->GetFieldID(theClass,fieldName, signature);
   if (fieldId == NULL || jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer getting object field '%s' \n", fieldName);
      return NULL;
   }
   jobject value= jenv->GetObjectField(instance, fieldId);
   if (jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer getting object field  '%s' \n", fieldName);
      return NULL;
   }
   return(value);
}
jboolean JNIHelper::getInstanceFieldBoolean(JNIEnv *jenv, jobject instance, char *fieldName){
   jclass theClass = jenv->GetObjectClass(instance);
   if (theClass == NULL ||  jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer! getting class from instance\n");
      return false;
   }
   jfieldID fieldId= jenv->GetFieldID(theClass,fieldName, "Z");
   if (fieldId == NULL || jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer getting boolean field '%s' \n", fieldName);
      return false;
   }
   jboolean value= jenv->GetBooleanField(instance, fieldId);
   if (jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer getting boolean field  '%s' \n", fieldName);
      return false;
   }
   return(value);
}
