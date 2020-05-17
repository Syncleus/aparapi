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
#define CONFIG_SOURCE
#include "config.h"

jboolean Config::getBoolean(JNIEnv *jenv, char *fieldName){
   jfieldID fieldID = jenv->GetStaticFieldID(configClass, fieldName, "Z");
   return(jenv->GetStaticBooleanField(configClass, fieldID));
}

Config::Config(JNIEnv *jenv) : indent(0){
   enableVerboseJNI = false;
   configClass = jenv->FindClass("com/amd/aparapi/Config");
   if (configClass == NULL ||  jenv->ExceptionCheck()) {
      jenv->ExceptionDescribe(); 
      jenv->ExceptionClear();
      fprintf(stderr, "bummer! getting Config from instance\n");
   }else{
      enableVerboseJNI = getBoolean(jenv, (char*)"enableVerboseJNI");
      enableVerboseJNIOpenCLResourceTracking = getBoolean(jenv, (char*)"enableVerboseJNIOpenCLResourceTracking");
      enableProfiling = getBoolean(jenv, (char*)"enableProfiling");
      enableProfilingCSV = getBoolean(jenv, (char*)"enableProfilingCSV");
   }

   //fprintf(stderr, "Config::enableVerboseJNI=%s\n",enableVerboseJNI?"true":"false");
   //fprintf(stderr, "Config::enableVerboseJNIOpenCLResourceTracking=%s\n",enableVerboseJNIOpenCLResourceTracking?"true":"false");
   //fprintf(stderr, "Config::enableProfiling=%s\n",enableProfiling?"true":"false");
   //fprintf(stderr, "Config::enableProfilingCSV=%s\n",enableProfilingCSV?"true":"false");
}

jboolean Config::isVerbose(){
   return enableVerboseJNI;
}

jboolean Config::isProfilingCSVEnabled(){
   return enableProfilingCSV;
}
jboolean Config::isTrackingOpenCLResources(){
   return enableVerboseJNIOpenCLResourceTracking;
}
jboolean Config::isProfilingEnabled(){
   return enableProfiling;
}
void Config::f(char *_fmt, ...){
      va_list args;
      va_start (args, _fmt);
      vfprintf (stderr, _fmt, args);
      va_end (args);
}
void Config::indentf(char *_fmt, ...){
   if (isVerbose()){
      for (int i=0; i< indent; i++){
         fprintf(stderr, "    ");
      }
      va_list args;
      va_start (args, _fmt);
      vfprintf (stderr, _fmt, args);
      va_end (args);
   }
}

void Config::in(char *_name){
   if (isVerbose()){
      indentf("-> %s\n", _name);
      indent++;
   }
}
void Config::out(char *_name){
   if (isVerbose()){
      indent--;
      indentf("<- %s\n", _name);
   }
}
