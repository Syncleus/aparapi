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

import com.aparapi.Kernel;
import com.aparapi.internal.model.CacheEnabler;
import com.aparapi.internal.model.ClassModel;
import com.aparapi.internal.model.Entrypoint;
import com.aparapi.internal.writer.KernelWriter;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import org.junit.Test;

public class Issue10NoDebugMonteCarloTest {

   @Test
   public void noDebugMonteCarloLongLocalIsDeclared() throws Exception {
      Path root = Files.createTempDirectory("aparapi-issue10-");
      Path sourceRoot = root.resolve("src");
      Path classesRoot = root.resolve("classes");
      Path source = sourceRoot.resolve("issue10").resolve("MonteCarloNoDebug.java");
      Files.createDirectories(source.getParent());
      Files.createDirectories(classesRoot);
      Files.write(source, sourceText().getBytes(StandardCharsets.UTF_8));

      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      assertTrue("JDK compiler is required for this regression test", compiler != null);
      int exit = compiler.run(null, null, null,
            "-g:none",
            "-classpath", System.getProperty("java.class.path"),
            "-d", classesRoot.toString(),
            source.toString());
      assertTrue("dynamic javac failed: " + exit, exit == 0);

      try (URLClassLoader loader = new URLClassLoader(new URL[] { classesRoot.toUri().toURL() },
            Thread.currentThread().getContextClassLoader())) {
         Class<?> kernelClass = Class.forName("issue10.MonteCarloNoDebug", true, loader);
         CacheEnabler.setCachesEnabled(false);
         ClassModel classModel = ClassModel.createClassModel(kernelClass);
         Object kernelInstance = kernelClass.getConstructor().newInstance();
         Entrypoint entrypoint = classModel.getEntrypoint("run", kernelInstance instanceof Kernel ? (Kernel) kernelInstance : null);
         String opencl = KernelWriter.writeToString(entrypoint);

         assertTrue(opencl, opencl.contains("long l_3 = (long)i_1;"));
         assertFalse(opencl, opencl.contains("\n       l_3 = (long)i_1;"));
      }
   }

   private static String sourceText() {
      return "package issue10;\n"
            + "import com.aparapi.Kernel;\n"
            + "public class MonteCarloNoDebug extends Kernel {\n"
            + "  private int size;\n"
            + "  private float[] result;\n"
            + "  public MonteCarloNoDebug() { this.size = 4; this.result = new float[size]; }\n"
            + "  @Override public void run() {\n"
            + "    int idx = getGlobalId();\n"
            + "    int iter = 25000;\n"
            + "    long seed = idx;\n"
            + "    float sum = 0.0f;\n"
            + "    for (int j = 0; j < iter; ++j) {\n"
            + "      seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);\n"
            + "      seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);\n"
            + "      float x = ((float)(seed & 0x0FFFFFFF)) / 268435455f;\n"
            + "      seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);\n"
            + "      seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);\n"
            + "      float y = ((float)(seed & 0x0FFFFFFF)) / 268435455f;\n"
            + "      float dist = (float)Math.sqrt(x * x + y * y);\n"
            + "      if (dist <= 1.0f) sum += 1.0f;\n"
            + "    }\n"
            + "    sum *= 4;\n"
            + "    result[idx] = sum / (float)iter;\n"
            + "  }\n"
            + "}\n";
   }
}
