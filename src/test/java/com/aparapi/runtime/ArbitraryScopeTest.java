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
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArbitraryScopeTest
{
    @Ignore("Known bug, ignoring test")
    @Test
    public void UnusedInArbitraryScopeTest()
    {
        Kernel kernel = new Kernel()
        {
            public void run() {
                int count = 0;
                {
                    @SuppressWarnings("unused") int value = count + 1;
                }
            }
        };
        kernel.execute(1);
    }

    @Ignore("Known bug, ignoring test")
    @Test
    public void UnusedInNormalScopeTest()
    {
        Kernel kernel = new Kernel() {
            int[] ints = new int[1024];

            public void run() {
                if (ints != null) {
                    int value = ints[0];
                }

            }
        };
        kernel.execute(1);
    }
}
