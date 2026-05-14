/**
 * Copyright (c) 2016 - 2018 Syncleus, Inc.
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
package com.aparapi.codegen.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.Assert;
import org.junit.Test;

import com.aparapi.Kernel;
import com.aparapi.internal.model.ClassModel;
import com.aparapi.internal.model.Entrypoint;
import com.aparapi.internal.writer.KernelWriter;

public class MonteCarloNoDebugLocalVariableTest {
   private static final Pattern DECLARED_CAST_LONG_LOCAL = Pattern.compile("(?m)^\\s+long l_\\d+ = \\(long\\)i_\\d+;");
   private static final Pattern UNDECLARED_CAST_LONG_LOCAL = Pattern.compile("(?m)^\\s+l_\\d+ = \\(long\\)i_\\d+;");
   private static final String CLASS_NAME = "MonteCarloNoDebugGenerated";

   @Test
   public void declaresSyntheticLongLocalForCastAssignment() throws Exception {
      Class<?> kernelClass = compileNoDebugKernel();
      ClassModel classModel = ClassModel.createClassModel(kernelClass);
      Kernel kernel = (Kernel) kernelClass.getConstructor().newInstance();
      Entrypoint entrypoint = classModel.getEntrypoint("run", kernel);
      String actual = KernelWriter.writeToString(entrypoint);

      assertTrue(actual, DECLARED_CAST_LONG_LOCAL.matcher(actual).find());
      assertFalse(actual, UNDECLARED_CAST_LONG_LOCAL.matcher(actual).find());
   }

   private Class<?> compileNoDebugKernel() throws Exception {
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      Assert.assertNotNull("JDK compiler is required for this regression test", compiler);

      File sourceDir = new File("target/no-debug-codegen-test/source");
      File classesDir = new File("target/no-debug-codegen-test/classes");
      assertTrue(sourceDir.mkdirs() || sourceDir.isDirectory());
      assertTrue(classesDir.mkdirs() || classesDir.isDirectory());

      File sourceFile = new File(sourceDir, CLASS_NAME + ".java");
      try (FileWriter writer = new FileWriter(sourceFile)) {
         writer.write(kernelSource());
      }

      String[] args = new String[] {
            "-g:none",
            "-source", "8",
            "-target", "8",
            "-classpath", System.getProperty("java.class.path"),
            "-d", classesDir.getPath(),
            sourceFile.getPath()
      };
      int exitCode = compiler.run(null, null, null, args);
      assertTrue("javac failed: " + Arrays.toString(args), exitCode == 0);

      URL[] urls = new URL[] {classesDir.toURI().toURL()};
      URLClassLoader loader = new URLClassLoader(urls, getClass().getClassLoader());
      return Class.forName(CLASS_NAME, true, loader);
   }

   private String kernelSource() {
      return "import com.aparapi.Kernel;\n"
            + "public class " + CLASS_NAME + " extends Kernel {\n"
            + "  private final float[] result = new float[16];\n"
            + "  @Override public void run() {\n"
            + "    int idx = getGlobalId();\n"
            + "    int iter = 25000;\n"
            + "    long seed = (long) idx;\n"
            + "    float sum = 0.0f;\n"
            + "    for (int j = 0; j < iter; ++j) {\n"
            + "      seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);\n"
            + "      seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);\n"
            + "      float x = ((float) (seed & 0x0FFFFFFFL)) / 268435455f;\n"
            + "      seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);\n"
            + "      seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);\n"
            + "      float y = ((float) (seed & 0x0FFFFFFFL)) / 268435455f;\n"
            + "      float dist = (float) Math.sqrt(x * x + y * y);\n"
            + "      if (dist <= 1.0f) { sum += 1.0f; }\n"
            + "    }\n"
            + "    sum *= 4.0f;\n"
            + "    result[idx] = sum / (float) iter;\n"
            + "  }\n"
            + "}\n";
   }
}
