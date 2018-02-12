/**
 * Copyright (c) 2016 - 2017 Syncleus, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
laws, including but not limited to the U.S. Export Administration Regulations ("EAR"), (15 C.F.R. Sections 730 through
774), and E.U. Council Regulation (EC) No 1334/2000 of 22 June 2000.  Further, pursuant to Section 740.6 of the EAR,
you hereby certify that, except pursuant to a license granted by the United States Department of Commerce Bureau of 
Industry and Security or as otherwise permitted pursuant to a License Exception under the U.S. Export Administration 
Regulations ("EAR"), you will not (1) export, re-export or release to a national of a country in Country Groups D:1,
E:1 or E:2 any restricted technology, software, or source code you receive hereunder, or (2) export to Country Groups
D:1, E:1 or E:2 the direct product of such technology or software, if such foreign produced direct product is subject
to national security controls as identified on the Commerce Control List (currently found in Supplement 1 to Part 774
of EAR).  For the most current Country Group listings, or for additional information about the EAR or your obligations
under those regulations, please refer to the U.S. Bureau of Industry and Security's website at http://www.bis.doc.gov/. 

*/
package com.aparapi.codegen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Source{
   enum STATE {
      NONE,
      JAVA,
      OPENCL,
      DOC
   };

   static final String OpenCLStart = "/**{OpenCL{";

   static final String OpenCLEnd = "}OpenCL}**/";

   static final String ThrowsStart = "/**{Throws{";

   static final String ThrowsEnd = "}Throws}**/";

   static final String DocStart = "/**";

   static final String DocEnd = "*/";

   Class<?> clazz;

   File file;

   Source.STATE state = STATE.NONE;

   List<String> all = new ArrayList<>();

   List<List<String>> opencl = new ArrayList<>();

   List<String> doc = new ArrayList<>();

   List<String> java = new ArrayList<>();

   List<String> exceptions = new ArrayList<>();

   public Source(Class<?> _clazz, File _rootDir) {
      clazz = _clazz;
      String srcName = clazz.getPackage().getName().replace(".", "/") + '/' + clazz + ".java";
      file = new File(_rootDir, srcName);
      try {
         BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

         state = STATE.JAVA;
         List<String> openclSection = null;
         for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            all.add(line);
            String trimmedLine = line.trim();
            switch (state) {
               case JAVA:
                  if (trimmedLine.equals(OpenCLStart)) {
                     state = STATE.OPENCL;
                     openclSection = new ArrayList<>();
                     opencl.add(openclSection);

                  } else if (trimmedLine.startsWith(ThrowsStart) && trimmedLine.endsWith(ThrowsEnd)) {
                     exceptions.add(trimmedLine.substring(ThrowsStart.length(), trimmedLine.length() - ThrowsEnd.length()));
                  } else if (trimmedLine.equals(DocStart)) {
                     state = STATE.DOC;
                  } else {
                     java.add(line);
                  }
                  break;
               case OPENCL:
                  if (trimmedLine.equals(OpenCLEnd)) {
                     state = STATE.JAVA;
                  } else {
                     openclSection.add(line);
                  }
                  break;
               case DOC:
                  if (trimmedLine.equals(DocEnd)) {
                     state = STATE.JAVA;
                  } else {
                     doc.add(line);
                  }
                  break;

            }
         }
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

   }

   private static String listToString(List<String> list) {
      StringBuilder sb = new StringBuilder();
       for (int i = 0, listSize = list.size(); i < listSize; i++) {
           String line = list.get(i);
           sb.append(line);
           if (i < listSize-1)
               sb.append('\n');
       }
      return sb.toString();
   }

   public String getOpenCLString(int _index) {
      return (listToString(opencl.get(_index)));
   }

   public List<List<String>> getOpenCL() {
      return (opencl);
   }

   public String getJavaString() {
      return (listToString(java));
   }

   public List<String> getJava() {
      return (java);
   }

   public File getFile() {
      return (file);
   }

   public String getExceptionsString() {
      return (listToString(exceptions));
   }

   public List<String> getExceptions() {
      return (exceptions);
   }

   public String getDocString() {
      return (listToString(doc));
   }

   public List<String> getDoc() {
      return (doc);
   }

   public int getOpenCLSectionCount() {
      return (opencl.size());
   }
}
