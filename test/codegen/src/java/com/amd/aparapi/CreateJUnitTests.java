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
package com.amd.aparapi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class CreateJUnitTests{
   public static void main(String[] args) throws ClassNotFoundException, FileNotFoundException, IOException {
      File rootDir = new File(System.getProperty("root", "."));

      String rootPackageName = CreateJUnitTests.class.getPackage().getName();
      String testPackageName = rootPackageName + ".test";
      File sourceDir = new File(rootDir, "src/java");
      System.out.println(sourceDir.getCanonicalPath());
      File testDir = new File(sourceDir, testPackageName.replace(".", "/"));
      System.out.println(testDir.getCanonicalPath());

      List<String> classNames = new ArrayList<String>();
      for (File sourceFile : testDir.listFiles(new FilenameFilter(){

         @Override public boolean accept(File dir, String name) {
            return (name.endsWith(".java"));
         }
      })) {
         String fileName = sourceFile.getName();
         String className = fileName.substring(0, fileName.length() - ".java".length());
         classNames.add(className);
      }

      File genSourceDir = new File(rootDir, "src/genjava");
      File codeGenDir = new File(genSourceDir, rootPackageName.replace(".", "/") + "/test/junit/codegen/");
      codeGenDir.mkdirs();

      for (String className : classNames) {

         Source source = new Source(Class.forName(testPackageName + "." + className), sourceDir);

         StringBuilder sb = new StringBuilder();
         sb.append("package com.amd.aparapi.test.junit.codegen;\n");
         sb.append("import org.junit.Test;\n");
         String doc = source.getDocString();
         if (doc.length() > 0) {
            sb.append("/**\n");
            sb.append(doc);
            sb.append("\n */\n");
         }
         sb.append("public class " + className + " extends com.amd.aparapi.CodeGenJUnitBase{\n");
         sb.append("   @Test public void " + className + "(){\n");
         if (source.getOpenCLSectionCount() > 0) {

            sb.append("   String[] expectedOpenCL = new String[]{\n");
            for (List<String> opencl : source.getOpenCL()) {
               sb.append("   \"\"\n");
               for (String line : opencl) {
                  sb.append("   +\"" + line + "\\n\"\n");
               }
               sb.append("   ,\n");
            }
            sb.append("   };\n");
         } else {
            sb.append("   String[] expectedOpenCL = null;\n");
         }

         String exceptions = source.getExceptionsString();
         if (exceptions.length() > 0) {
            sb.append("   Class<? extends com.amd.aparapi.internal.exception.AparapiException> expectedException = ");

            sb.append("com.amd.aparapi.internal.exception." + exceptions + ".class");
            sb.append(";\n");
         } else {
            sb.append("   Class<? extends com.amd.aparapi.internal.exception.AparapiException> expectedException = null;\n");
         }
         sb.append("       test(" + testPackageName + "." + className + ".class, expectedException, expectedOpenCL);\n");
         sb.append("   }\n");
         sb.append("}\n");
         //  System.out.println(sb.toString());

         File generatedFile = new File(codeGenDir, className + ".java");
         PrintStream out = new PrintStream(generatedFile);
         out.append(sb.toString());
         out.close();

      }

   }
}
