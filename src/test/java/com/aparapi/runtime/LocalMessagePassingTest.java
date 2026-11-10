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
import com.aparapi.device.JavaDevice;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LocalMessagePassingTest {
    public static class RingKernel extends Kernel {
        @Local final int[] mailbox;
        final int[] input;
        final int[] output;
        final int rounds;

        public RingKernel(int size, int localSize, int rounds) {
            mailbox = new int[localSize];
            input = new int[size];
            output = new int[size];
            this.rounds = rounds;
            for (int i = 0; i < size; i++) {
                input[i] = Integer.MIN_VALUE + i;
            }
        }

        @Override public void run() {
            int rank = getLocalId();
            int value = input[getGlobalId()];
            for (int round = 0; round < rounds; round++) {
                localSend(mailbox, (rank + 1) % getLocalSize(), value);
                value = localReceive(mailbox, rank);
            }
            output[getGlobalId()] = value;
        }
    }

    @Test(timeout = 30000) public void ringReusesMailboxAcrossRoundsAndGroups() {
        checkRing(24, 4, 101);
    }

    @Test(timeout = 30000) public void singleWorkItemCanSendToItself() {
        checkRing(3, 1, 5);
    }

    private void checkRing(int size, int localSize, int rounds) {
        RingKernel kernel = new RingKernel(size, localSize, rounds);
        try {
            kernel.setExecutionMode(Kernel.EXECUTION_MODE.JTP);
            kernel.setExplicit(true);
            kernel.put(kernel.input);
            kernel.execute(Range.create(JavaDevice.THREAD_POOL, size, localSize));
            kernel.get(kernel.output);
            assertEquals(Kernel.EXECUTION_MODE.JTP, kernel.getExecutionMode());
            for (int i = 0; i < size; i++) {
                int groupStart = i / localSize * localSize;
                int source = (i % localSize - rounds % localSize + localSize) % localSize;
                assertEquals("work-item " + i, kernel.input[groupStart + source], kernel.output[i]);
            }
        } finally {
            kernel.dispose();
        }
    }

    public static class BroadcastKernel extends Kernel {
        @Local final int[] mailbox = new int[1];
        final int[] output = new int[16];

        @Override public void run() {
            if (getLocalId() == 0) {
                localSend(mailbox, 0, 123 + getGroupId());
            }
            output[getGlobalId()] = localReceive(mailbox, 0);
        }
    }

    @Test(timeout = 30000) public void oneSenderCanBroadcastToGroup() {
        BroadcastKernel kernel = new BroadcastKernel();
        try {
            kernel.setExecutionMode(Kernel.EXECUTION_MODE.JTP);
            kernel.execute(Range.create(JavaDevice.THREAD_POOL, 16, 4));
            for (int i = 0; i < 16; i++) {
                assertEquals(123 + i / 4, kernel.output[i]);
            }
        } finally {
            kernel.dispose();
        }
    }
}
