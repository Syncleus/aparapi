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
package com.aparapi.internal.model;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class EntrypointFieldAccessTest {

   public static class StaticArrayKernel {
      static final int[] source = new int[8];

      static final int[] target = new int[8];

      public void run() {
         int id = 0;
         target[id] = source[id];
      }
   }

   public static class InstanceArrayKernel {
      final int[] source = new int[8];

      final int[] target = new int[8];

      public void run() {
         int id = 0;
         target[id] = source[id];
      }
   }

   @Test
   public void recordsStaticArrayAssignmentsAndAccesses() throws Exception {
      Entrypoint entrypoint = entrypointFor(StaticArrayKernel.class);

      assertTrue("static array assignment should be recorded",
            entrypoint.getArrayFieldAssignments().contains("target"));
      assertTrue("static array access should be recorded",
            entrypoint.getArrayFieldAccesses().contains("source"));
   }

   @Test
   public void recordsInstanceArrayAssignmentsAndAccesses() throws Exception {
      Entrypoint entrypoint = entrypointFor(InstanceArrayKernel.class);

      assertTrue("instance array assignment should be recorded",
            entrypoint.getArrayFieldAssignments().contains("target"));
      assertTrue("instance array access should be recorded",
            entrypoint.getArrayFieldAccesses().contains("source"));
   }

   private Entrypoint entrypointFor(Class<?> clazz) throws Exception {
      ClassModel classModel = ClassModel.createClassModel(clazz);
      return classModel.getEntrypoint("run", null);
   }
}
