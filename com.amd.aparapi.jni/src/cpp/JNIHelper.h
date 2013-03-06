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
   and Security�s website at http://www.bis.doc.gov/. 
   */

#ifndef JNIHELPER_H
#define JNIHELPER_H

#include <jni.h>
#include <string>
#include "CLException.h"
#define JavaLangPackage(name) "java/lang/" name
#define JavaUtilPackage(name) "java/util/" name
#define AparapiPackage(name) "com/amd/aparapi/" name
#define AparapiDevicePackage(name) "com/amd/aparapi/device/" name
#define AparapiOpenCLPackage(name) "com/amd/aparapi/internal/opencl/" name
#define AparapiUtilPackage(name) "com/amd/aparapi/internal/util/" name

#define ProfileInfoClass AparapiPackage("ProfileInfo")
#define OpenCLKernelClass AparapiOpenCLPackage("OpenCLKernel")
#define OpenCLPlatformClass AparapiOpenCLPackage("OpenCLPlatform")
#define OpenCLDeviceClass AparapiDevicePackage("OpenCLDevice")
#define OpenCLProgramClass AparapiOpenCLPackage("OpenCLProgram")
#define OpenCLArgDescriptorClass AparapiOpenCLPackage("OpenCLArgDescriptor")
#define OpenCLMemClass AparapiOpenCLPackage("OpenCLMem")
#define StringClass JavaLangPackage("String")
#define ObjectClass JavaLangPackage("Object")
#define ListClass JavaUtilPackage("List")
#define ArrayListClass JavaUtilPackage("ArrayList")
#define DeviceTypeClass AparapiDevicePackage("Device$TYPE")

#define ARG(name) "L" name ";"

#define ProfileInfoClassArg ARG(ProfileInfoClass)
#define OpenCLKernelClassArg ARG(OpenCLKernelClass)
#define OpenCLPlatformClassArg ARG(OpenCLPlatformClass)
#define OpenCLDeviceClassArg ARG(OpenCLDeviceClass)
#define OpenCLProgramClassArg ARG(OpenCLProgramClass)
#define OpenCLArgDescriptorClassArg ARG(OpenCLArgDescriptorClass)
#define OpenCLMemClassArg ARG(OpenCLMemClass)
#define StringClassArg ARG(StringClass)
#define ObjectClassArg ARG(ObjectClass)
#define ListClassArg ARG(ListClass)
#define ArrayListClassArg ARG(ArrayListClass)
#define DeviceTypeClassArg ARG(DeviceTypeClass)
#define LongArg "J"
#define IntArg "I"
#define ArrayArg(name) "[" ARG(name)

#define Args(name) "(" name ")"

#define ArgsVoidReturn(name) Args(name)"V"
#define ArgsBooleanReturn(name) Args(name)"Z"

#define VoidReturn  ArgsVoidReturn("")

#define JNI_JAVA(type, className, methodName) JNIEXPORT type JNICALL Java_com_amd_aparapi_internal_jni_##className##_##methodName

class JNIHelper {

      static void setField(JNIEnv* jenv, jobject instance, jfieldID fieldId, jint* value) {
         jenv->SetIntField(instance, fieldId, *value);
      }
      static void setField(JNIEnv* jenv, jobject instance, jfieldID fieldId, jfloat* value) {
         jenv->SetFloatField(instance, fieldId, *value);
      }
      static void setField(JNIEnv* jenv, jobject instance, jfieldID fieldId, jdouble* value) {
         jenv->SetDoubleField(instance, fieldId, *value);
      }
      static void setField(JNIEnv* jenv, jobject instance, jfieldID fieldId, jshort* value) {
         jenv->SetShortField(instance, fieldId, *value);
      }
      static void setField(JNIEnv* jenv, jobject instance, jfieldID fieldId, jlong* value) {
         jenv->SetLongField(instance, fieldId, *value);
      }
      static void setField(JNIEnv* jenv, jobject instance, jfieldID fieldId, jobject* value) {
         jenv->SetObjectField(instance, fieldId, *value);
      }
      static void setField(JNIEnv* jenv, jobject instance, jfieldID fieldId, jboolean* value) {
         jenv->SetBooleanField(instance, fieldId, *value);
      }

      static void getField(JNIEnv* jenv, jobject instance, jfieldID fieldId, jint* value) {
         *value = jenv->GetIntField(instance, fieldId);
      }
      static void getField(JNIEnv* jenv, jobject instance, jfieldID fieldId, jfloat* value) {
         *value = jenv->GetFloatField(instance, fieldId);
      }
      static void getField(JNIEnv* jenv, jobject instance, jfieldID fieldId, jdouble* value) {
         *value = jenv->GetDoubleField(instance, fieldId);
      }
      static void getField(JNIEnv* jenv, jobject instance, jfieldID fieldId, jshort* value) {
         *value = jenv->GetShortField(instance, fieldId);
      }
      static void getField(JNIEnv* jenv, jobject instance, jfieldID fieldId, jlong* value) {
         *value = jenv->GetLongField(instance, fieldId);
      }
      static void getField(JNIEnv* jenv, jobject instance, jfieldID fieldId, jobject* value) {
         *value = jenv->GetObjectField(instance, fieldId);
      }
      static void getField(JNIEnv* jenv, jobject instance, jfieldID fieldId, jboolean* value) {
         *value = jenv->GetBooleanField(instance, fieldId);
      }

      static const char* getSignature(jint value) {
         return "I";
      }
      static const char* getSignature(jfloat value) {
         return "F";
      }
      static const char* getSignature(jdouble value) {
         return "D";
      }
      static const char* getSignature(jshort value) {
         return "H";
      }
      static const char* getSignature(jlong value) {
         return "J";
      }
      static const char* getSignature(jboolean value) {
         return "Z";
      }


   public:
      static void callVoid(JNIEnv *jenv, jobject instance, const char *methodName, const char *methodSignature, ...);
      static jlong callLong(JNIEnv *jenv, jobject instance, const char *methodName, const char *methodSignature, ...);
      static jobject callObject(JNIEnv *jenv, jobject instance, const char *methodName, const char *methodSignature, ...);

      static jobject createInstance(JNIEnv *jenv, const char *className, const char *signature, ... );

      static jobject getStaticFieldObject(JNIEnv *jenv, const char *className, const char *fieldName, const char *signature);

      static const char *getType(jint value) {
         return "int";
      }
      static const char *getType(jfloat value) {
         return "float";
      }
      static const char *getType(jdouble value) {
         return "double";
      }
      static const char *getType(jshort value) {
         return "short";
      }
      static const char *getType(jlong value) {
         return "long";
      }
      static const char *getType(jobject value) {
         return "object";
      }
      static const char *getType(jboolean value) {
         return "boolean";
      }

      //these have to go here, because they're templated

      template<typename jT>
      static void setInstanceField(JNIEnv* jenv, jobject instance, const char *fieldName, jT value) {
          return setInstanceField<jT>(jenv, instance, fieldName, getSignature(value), value);
      }

      template<typename jT>
      static void setInstanceField(JNIEnv* jenv, jobject instance, const char *fieldName, const char* signature, jT value) {
         try {
            jclass theClass = jenv->GetObjectClass(instance);
            if (theClass == NULL ||  jenv->ExceptionCheck())
               throw "bummer! getting class from instance\n";
            jfieldID fieldId = jenv->GetFieldID(theClass,fieldName,signature);
            if (fieldId == NULL || jenv->ExceptionCheck())
               throw std::string("bummer getting ") + getType(value) + " field '" + fieldName + "'\n";
            setField(jenv, instance, fieldId, &value);
            if (jenv->ExceptionCheck())
               throw std::string("bummer setting ") + getType(value) + " field '" + fieldName + "'\n";
         } catch(std::string& se) {
            jenv->ExceptionDescribe(); 
            jenv->ExceptionClear();
            fprintf(stderr,"%s",se.c_str());
         }
      }

      template<typename jT>
      static jT getInstanceField(JNIEnv *jenv, jobject instance, const char *fieldName) {
          return getInstanceField<jT>(jenv, instance, fieldName, getSignature((jT)0));
      }

      template<typename jT>
      static jT getInstanceField(JNIEnv *jenv, jobject instance, const char *fieldName, const char *signature) {
         jT value = (jT)0;
         try {
            jclass theClass = jenv->GetObjectClass(instance);
            if (theClass == NULL ||  jenv->ExceptionCheck()) 
               throw "bummer! getting class from instance\n";
            jfieldID fieldId = jenv->GetFieldID(theClass,fieldName, signature);
            if (fieldId == NULL || jenv->ExceptionCheck())
               throw std::string("bummer getting ") + getType(value) + "field '" + fieldName + "' \n";
            getField(jenv, instance, fieldId, &value);
            if (jenv->ExceptionCheck())
               throw std::string("bummer getting ") + getType(value) + "field '" + fieldName + "' \n";
         } catch(std::string& se) {
            jenv->ExceptionDescribe(); 
            jenv->ExceptionClear();
            fprintf(stderr,"%s",se.c_str());
            return NULL;
         }
         return(value);
      }


      static jfieldID GetFieldID(JNIEnv* jenv, jclass c, const char* name, const char* type) {
         jfieldID field = jenv->GetFieldID(c, name, type);
         if(field == 0) {
            fprintf(stderr, "!!!!!!! no such field as %s: failed !!!!!!!\n", name);\
         }
         return field;
      }

};

#endif // JNIHELPER_H

