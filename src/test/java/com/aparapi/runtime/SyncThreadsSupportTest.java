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

import com.aparapi.Kernel;
import com.aparapi.Range;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class SyncThreadsSupportTest {
   @Test
   @SuppressWarnings("deprecation")
   public void syncThreadsWaitsForAllLocalWorkItems() {
      final int[] shared = new int[2];
      final int[] result = new int[2];
      Kernel kernel = new Kernel() {
         @Override
         public void run() {
            int id = getLocalId();
            shared[id] = id + 1;
            syncThreads();
            result[id] = shared[1 - id];
         }
      };
      kernel.setExecutionMode(Kernel.EXECUTION_MODE.JTP);

      kernel.execute(Range.create(2, 2));

      assertArrayEquals(new int[] { 2, 1 }, result);
   }
}
