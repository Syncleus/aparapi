/**
 * Copyright (c) 2016 - 2017 Syncleus, Inc.
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

import com.aparapi.Range;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RangeSizeTest {

    @Test
    public void test384x384() {
        Range range = Range.create2D(384, 384);
        System.out.println("local[0] " + range.getLocalSize(0));
        System.out.println("local[1] " + range.getLocalSize(1));
        System.out.println("workGroupSize " + range.getWorkGroupSize());
        assertTrue("Range > max work size", range.getLocalSize(0) * range.getLocalSize(1) <= range.getWorkGroupSize());
    }

    @Test
    public void test384x320() {
        Range range = Range.create2D(384, 320);
        System.out.println("local[0] " + range.getLocalSize(0));
        System.out.println("local[1] " + range.getLocalSize(1));
        System.out.println("workGroupSize " + range.getWorkGroupSize());
        assertTrue("Range > max work size", range.getLocalSize(0) * range.getLocalSize(1) <= range.getWorkGroupSize());
    }

}
