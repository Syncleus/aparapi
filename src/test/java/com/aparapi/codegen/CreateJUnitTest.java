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

import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CreateJUnitTest {
    @Ignore("We will probably never use this, it just generates test. We probably want to do this by hand.")
    @Test
    public void test() throws ClassNotFoundException, FileNotFoundException, IOException {
        File rootDir = new File(System.getProperty("root", "."));

        String rootPackageName = CreateJUnitTest.class.getPackage().getName();
        String testPackageName = rootPackageName + ".test";
        File sourceDir = new File(rootDir, "src/test/java");
        System.out.println(sourceDir.getCanonicalPath());
        File testDir = new File(sourceDir, testPackageName.replace(".", "/"));
        System.out.println(testDir.getCanonicalPath());

        List<String> classNames = new ArrayList<String>();
        for (File sourceFile : testDir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
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

            final String testName = className + "Test";

            StringBuilder sb = new StringBuilder();
            sb.append("package com.codegen.test.junit.codegen;\n");
            sb.append("import org.junit.Test;\n");
            String doc = source.getDocString();
            if (doc.length() > 0) {
                sb.append("/**\n");
                sb.append(doc);
                sb.append("\n */\n");
            }
            sb.append("public class " + testName + " extends com.codegen.CodeGenJUnitBase{\n");
            appendExpectedOpenCL(source, sb);
            appendExpectedExceptions(source, sb);
            appendTest(testPackageName, testName, "", sb);
            appendTest(testPackageName, testName, "WorksWithCaching", sb);
            sb.append("}\n");
            //  System.out.println(sb.toString());

            File generatedFile = new File(codeGenDir, testName + ".java");
            PrintStream out = new PrintStream(generatedFile);
            out.append(sb.toString());
            out.close();

        }

    }

    private static void appendTest(String testPackageName, String className, String suffix, StringBuilder sb) {
        sb.append("   @Test public void " + className + suffix + "(){\n");
        sb.append("       test(" + testPackageName + "." + className + ".class, expectedException, expectedOpenCL);\n");
        sb.append("   }\n");
    }

    private static void appendExpectedExceptions(Source source, StringBuilder sb) {
        String exceptions = source.getExceptionsString();
        if (exceptions.length() > 0) {
            sb.append("   private static final Class<? extends com.codegen.internal.exception.AparapiException> expectedException = ");

            sb.append("com.codegen.internal.exception." + exceptions + ".class");
            sb.append(";\n");
        } else {
            sb.append("   private static final Class<? extends com.codegen.internal.exception.AparapiException> expectedException = null;\n");
        }
    }

    private static void appendExpectedOpenCL(Source source, StringBuilder sb) {
        if (source.getOpenCLSectionCount() > 0) {

            sb.append("  private static final  String[] expectedOpenCL = new String[]{\n");
            for (List<String> opencl : source.getOpenCL()) {
                sb.append("   \"\"\n");
                for (String line : opencl) {
                    sb.append("   +\"" + line + "\\n\"\n");
                }
                sb.append("   ,\n");
            }
            sb.append("   };\n");
        } else {
            sb.append("   private static final String[] expectedOpenCL = null;\n");
        }
    }
}
