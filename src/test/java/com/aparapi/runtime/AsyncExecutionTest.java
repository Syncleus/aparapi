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
import com.aparapi.Range;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AsyncExecutionTest {
   @Test
   @SuppressWarnings("deprecation")
   public void asyncExecuteRangeWaitsForCompletion() {
      final int[] values = new int[8];
      Kernel kernel = new Kernel() {
         @Override
         public void run() {
            int id = getGlobalId();
            values[id] = id * 2;
         }
      };
      kernel.setExecutionMode(Kernel.EXECUTION_MODE.SEQ);

      Execution execution = kernel.executeAsync(Range.create(values.length));
      assertSame(kernel, execution.getKernel());

      execution.waitUntilFinished();

      assertTrue(execution.isFinished());
      assertArrayEquals(new int[] { 0, 2, 4, 6, 8, 10, 12, 14 }, values);
   }

   @Test
   @SuppressWarnings("deprecation")
   public void asyncExecuteIntWithPassesCompletesAllPasses() {
      final int[] passes = new int[4];
      Kernel kernel = new Kernel() {
         @Override
         public void run() {
            passes[getGlobalId()]++;
         }
      };
      kernel.setExecutionMode(Kernel.EXECUTION_MODE.SEQ);

      Execution execution = kernel.executeAsync(passes.length, 3);
      execution.waitUntilFinished();

      assertArrayEquals(new int[] { 3, 3, 3, 3 }, passes);
   }

   @Test
   @SuppressWarnings("deprecation")
   public void asyncExecutionRethrowsFailureWhenWaitedOn() {
      Kernel kernel = new Kernel() {
         @Override
         public void run() {
            throw new IllegalStateException("boom");
         }
      };
      kernel.setExecutionMode(Kernel.EXECUTION_MODE.SEQ);

      Execution execution = kernel.executeAsync(1);
      try {
         execution.waitUntilFinished();
         fail("Expected async execution failure to be rethrown");
      } catch (IllegalStateException e) {
         assertEquals("boom", e.getMessage());
      }

      assertTrue(execution.isFinished());
      assertNotNull(execution.getFailure());
   }
}
