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

import org.junit.Test;

import com.aparapi.Kernel;
import com.aparapi.internal.model.ClassModel;
import com.aparapi.internal.model.Entrypoint;
import com.aparapi.internal.writer.KernelWriter;

public class ReservedOpenCLKeywordNamesTest {

   @Test
   public void manglesReservedFieldAndLocalVariableNames() throws Exception {
      ClassModel classModel = ClassModel.createClassModel(ReservedKeywordKernel.class);
      Entrypoint entrypoint = classModel.getEntrypoint("run", new ReservedKeywordKernel());
      String openCL = KernelWriter.writeToString(entrypoint);

      assertTrue(openCL.contains("__local float *aparapi_local"));
      assertTrue(openCL.contains("this->aparapi_local = aparapi_local;"));
      assertTrue(openCL.contains("int aparapi_kernel = "));
      assertTrue(openCL.contains("this->aparapi_local[aparapi_kernel]"));

      assertFalse(openCL.contains("__local float *local"));
      assertFalse(openCL.contains("this->local"));
      assertFalse(openCL.contains("int kernel = "));
   }

   public static class ReservedKeywordKernel extends Kernel {
      @Local final float[] local = new float[16];
      final int[] result = new int[1];

      @Override
      public void run() {
         int kernel = getGlobalId(0);
         local[kernel] = kernel;
         result[0] = kernel;
      }
   }
}
