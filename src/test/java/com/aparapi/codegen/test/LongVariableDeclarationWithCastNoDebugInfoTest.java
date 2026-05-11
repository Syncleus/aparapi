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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.Test;

import com.aparapi.internal.model.ClassModel;
import com.aparapi.internal.model.Entrypoint;
import com.aparapi.internal.writer.KernelWriter;

public class LongVariableDeclarationWithCastNoDebugInfoTest {

   @Test
   public void declaresLongLocalInitializedFromCastWithoutDebugInfo() throws Exception {
      final String openCL = generateOpenCLFromFixtureWithoutDebugInfo();

      assertTrue(openCL, openCL.contains("long l_3=(long)i_1;") || openCL.contains("long l_3 = (long)i_1;"));
   }

   private String generateOpenCLFromFixtureWithoutDebugInfo() throws Exception {
      final Path sourceDir = Files.createTempDirectory("aparapi-no-debug-src");
      final Path classesDir = Files.createTempDirectory("aparapi-no-debug-classes");
      final Path packageDir = sourceDir.resolve("com/aparapi/codegen/generated");
      Files.createDirectories(packageDir);

      final Path sourceFile = packageDir.resolve("LongVariableDeclarationWithCastNoDebugInfo.java");
      final String source = ""
            + "package com.aparapi.codegen.generated;\n"
            + "public class LongVariableDeclarationWithCastNoDebugInfo {\n"
            + "   public void run() {\n"
            + "      int idx = 1;\n"
            + "      int iter = 25000;\n"
            + "      long seed = (long) idx;\n"
            + "      float sum = 0.0f;\n"
            + "      seed = seed + 1L;\n"
            + "      sum = sum + (float) seed + (float) iter;\n"
            + "   }\n"
            + "}\n";
      Files.write(sourceFile, source.getBytes(StandardCharsets.UTF_8));

      final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      assertNotNull("This test requires a JDK, not just a JRE", compiler);
      final int result = compiler.run(null, null, null, "-g:none", "-classpath", System.getProperty("java.class.path"),
            "-d", classesDir.toString(), sourceFile.toString());
      assertEquals(0, result);

      final URL[] urls = new URL[] { classesDir.toUri().toURL() };
      try (URLClassLoader classLoader = new URLClassLoader(urls, getClass().getClassLoader())) {
         final Class<?> fixture = Class.forName("com.aparapi.codegen.generated.LongVariableDeclarationWithCastNoDebugInfo",
               true, classLoader);
         final Object fixtureInstance = fixture.getConstructor((Class<?>[]) null).newInstance();
         final ClassModel classModel = ClassModel.createClassModel(fixture);
         final Entrypoint entrypoint = classModel.getEntrypoint("run", fixtureInstance);
         return KernelWriter.writeToString(entrypoint);
      }
   }
}
