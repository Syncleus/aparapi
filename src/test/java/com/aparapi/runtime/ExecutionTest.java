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
package com.aparapi.runtime;

import com.aparapi.Execution;
import com.aparapi.Kernel;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ExecutionTest {

   @Test
   public void executeAsyncReturnsBeforeKernelCompletesAndWaitsForResults() {
      final int size = 8;
      final int[] values = new int[size];
      final AtomicBoolean releaseKernel = new AtomicBoolean(false);
      final AtomicInteger started = new AtomicInteger(0);
      Kernel kernel = new Kernel() {
         @Override
         public void run() {
            started.incrementAndGet();
            while (!releaseKernel.get()) {
               Thread.yield();
            }
            values[getGlobalId()] = getGlobalId() + 1;
         }
      };
      kernel.setExecutionMode(Kernel.EXECUTION_MODE.SEQ);

      Execution execution = kernel.executeAsync(size);
      while (started.get() == 0) {
         Thread.yield();
      }

      assertFalse(execution.isFinished());
      releaseKernel.set(true);
      execution.waitUntilFinished();

      assertTrue(execution.isFinished());
      assertFalse(execution.hasFailed());
      assertArrayEquals(new int[] {1, 2, 3, 4, 5, 6, 7, 8}, values);
   }

   @Test
   public void waitUntilFinishedRethrowsRuntimeExceptionFromKernel() {
      Kernel kernel = new Kernel() {
         @Override
         public void run() {
            throw new IllegalStateException("expected failure");
         }
      };
      kernel.setExecutionMode(Kernel.EXECUTION_MODE.SEQ);

      Execution execution = kernel.executeAsync(1);

      try {
         execution.waitUntilFinished();
         fail("Expected waitUntilFinished to rethrow the kernel failure");
      } catch (IllegalStateException e) {
         assertTrue(e.getMessage().contains("expected failure"));
      }
      assertTrue(execution.isFinished());
      assertTrue(execution.hasFailed());
   }
}
