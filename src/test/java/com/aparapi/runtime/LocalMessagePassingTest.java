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

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.JavaDevice;

public class LocalMessagePassingTest {

   @Test
   public void localExchangeAndBroadcastWorkOnJavaThreadPool() {
      final int[] values = {10, 20, 30, 40};

      Kernel kernel = new Kernel() {
         @Local int[] mailbox = new int[4];

         @Override public void run() {
            int localId = getLocalId();
            int value = values[getGlobalId()];
            int exchangeValue = localExchange(mailbox, (localId + 1) & 3, value);
            int broadcastValue = localBroadcast(mailbox, 0, value);
            values[getGlobalId()] = exchangeValue + broadcastValue;
         }
      };

      try {
         kernel.execute(Range.create(JavaDevice.THREAD_POOL, 4, 4));
      } finally {
         kernel.dispose();
      }

      assertArrayEquals(new int[] {30, 40, 50, 20}, values);
   }
}
