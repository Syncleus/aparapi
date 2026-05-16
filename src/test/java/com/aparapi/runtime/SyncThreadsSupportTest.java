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
import com.aparapi.device.Device;
import com.aparapi.device.JavaDevice;
import com.aparapi.internal.kernel.KernelManager;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.junit.After;
import org.junit.Test;

public class SyncThreadsSupportTest {
    private static final int SIZE = 64;

    private static class JTPKernelManager extends KernelManager {
        private JTPKernelManager() {
            LinkedHashSet<Device> preferredDevices = new LinkedHashSet<Device>(1);
            preferredDevices.add(JavaDevice.THREAD_POOL);
            setDefaultPreferredDevices(preferredDevices);
        }

        @Override
        protected List<Device.TYPE> getPreferredDeviceTypes() {
            return Arrays.asList(Device.TYPE.JTP);
        }
    }

    @After
    public void tearDown() {
        Util.resetKernelManager();
    }

    @Test
    public void syncThreadsSynchronizesWorkItemsInJTPMode() {
        KernelManager.setKernelManager(new JTPKernelManager());
        Device device = KernelManager.instance().bestDevice();
        assertTrue(device instanceof JavaDevice);

        int[] target = new int[SIZE];
        SyncThreadsKernel kernel = new SyncThreadsKernel(target);
        try {
            Range range = device.createRange(SIZE, SIZE);
            kernel.execute(range);
            assertArrayEquals(expectedValues(), target);
        } finally {
            kernel.dispose();
        }
    }

    private static int[] expectedValues() {
        int[] expected = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            expected[i] = (i + 1) % SIZE;
        }
        return expected;
    }

    private static class SyncThreadsKernel extends Kernel {
        private final int[] target;

        @Local
        private final int[] scratch = new int[SIZE];

        private SyncThreadsKernel(int[] target) {
            this.target = target;
        }

        @Override
        public void run() {
            int id = getLocalId();
            scratch[id] = id;
            syncThreads();
            target[id] = scratch[(id + 1) % SIZE];
        }
    }
}
