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
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class KernelAsyncExecutionTest {

    @Test(timeout = 5000)
    public void executeAsyncRangeRunsInBackgroundAndCanBeWaitedOn() throws Exception {
        final int[] output = new int[1];
        final CountDownLatch enteredKernel = new CountDownLatch(1);
        final CountDownLatch releaseKernel = new CountDownLatch(1);
        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                enteredKernel.countDown();
                try {
                    releaseKernel.await(2, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
                output[getGlobalId()] = getGlobalId() + 1;
            }
        };
        kernel.setExecutionMode(Kernel.EXECUTION_MODE.JTP);

        Execution execution = kernel.executeAsync(Range.create(1));

        Assert.assertTrue(enteredKernel.await(2, TimeUnit.SECONDS));
        Assert.assertFalse(execution.isFinished());
        releaseKernel.countDown();
        Assert.assertSame(kernel, execution.waitUntilFinished());
        Assert.assertTrue(execution.isFinished());
        Assert.assertEquals(1, output[0]);
    }
}
