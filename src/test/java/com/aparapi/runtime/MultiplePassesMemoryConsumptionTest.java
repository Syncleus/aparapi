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
import org.junit.Assert;
import org.junit.Test;

public class MultiplePassesMemoryConsumptionTest {

    @Test
    public void test() {
        final int globalArray[] = new int[512];
        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                globalArray[getGlobalId()] = getGlobalId();
            }
        };
        long baseFree = Runtime.getRuntime().freeMemory();
        for (int loop = 0; loop < 100; loop++) {
            if( baseFree > Runtime.getRuntime().freeMemory())
                baseFree = Runtime.getRuntime().freeMemory();
            kernel.execute(Range.create(512, 64), 1);
            for (int i = 0; i < globalArray.length; ++i) {
                Assert.assertEquals("Wrong", i, globalArray[i]);
            }
        }
        long testFree = Runtime.getRuntime().freeMemory();
        for (int loop = 0; loop < 100; loop++) {
            if( testFree > Runtime.getRuntime().freeMemory())
                testFree = Runtime.getRuntime().freeMemory();
            kernel.execute(Range.create(512, 64), 2);
            for (int i = 0; i < globalArray.length; ++i) {
                Assert.assertEquals("Wrong", i, globalArray[i]);
            }
        }
        long extraMemory = baseFree - testFree;
        Assert.assertTrue("Too much memory consumed: " + extraMemory, extraMemory < 4000000);
    }

}
