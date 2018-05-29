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
package com.aparapi.device;

import com.aparapi.Range;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DeviceTest {

    private static final int LOCAL_WIDTH = 11;
    private static final int GLOBAL_WIDTH = 11;
    private static final int GLOBAL_HEIGHT = 12;
    private static final int LOCAL_HEIGHT = 14;
    private static final int GLOBAL_DEPTH = 15;
    private static final int LOCAL_DEPTH = 17;
    private static final String TEST = "test";

    @Test
    public void shouldCreateRangeWithCurrentDevice() {
        Device sut = Utils.createDevice(Utils.createPlatform(TEST), Device.TYPE.ACC);
        assertEquals(Range.create(sut, GLOBAL_WIDTH, LOCAL_WIDTH), sut.createRange(GLOBAL_WIDTH, LOCAL_WIDTH));
    }

    @Test
    public void shouldCreateRange2DGlobalParamsWithCurrentDevice() {
        Device sut = Utils.createDevice(Utils.createPlatform(TEST), Device.TYPE.ACC);
        assertEquals(Range.create2D(sut, GLOBAL_WIDTH, GLOBAL_HEIGHT), sut.createRange2D(GLOBAL_WIDTH, GLOBAL_HEIGHT));
    }

    @Test
    public void shouldCreateRange2DGlobalAndLocalWithCurrentDevice() {
        Device sut = Utils.createDevice(Utils.createPlatform(TEST), Device.TYPE.ACC);
        assertEquals(Range.create2D(sut, GLOBAL_WIDTH, GLOBAL_HEIGHT, LOCAL_WIDTH, LOCAL_HEIGHT),
            sut.createRange2D(GLOBAL_WIDTH, GLOBAL_HEIGHT, LOCAL_WIDTH, LOCAL_HEIGHT));
    }

    @Test
    public void shouldCreateRange3DGlobalWithCurrentDevice() {
        Device sut = Utils.createDevice(Utils.createPlatform(TEST), Device.TYPE.ACC);
        assertEquals(Range.create3D(sut, GLOBAL_WIDTH, GLOBAL_HEIGHT, GLOBAL_DEPTH),
            sut.createRange3D(GLOBAL_WIDTH, GLOBAL_HEIGHT, GLOBAL_DEPTH));
    }

    @Test
    public void shouldCreateRange3DGlobalAndLocalWithCurrentDevice() {
        Device sut = Utils.createDevice(Utils.createPlatform(TEST), Device.TYPE.ACC);
        assertEquals(Range.create3D(sut, GLOBAL_WIDTH, GLOBAL_HEIGHT, GLOBAL_DEPTH, LOCAL_WIDTH, LOCAL_HEIGHT, LOCAL_DEPTH),
            sut.createRange3D(GLOBAL_WIDTH, GLOBAL_HEIGHT, GLOBAL_DEPTH, LOCAL_WIDTH, LOCAL_HEIGHT, LOCAL_DEPTH));
    }

    @Test
    public void shouldCompareDevicesAccordingToTheirRank() {
        Device least = Utils.createDevice(Utils.createPlatform(TEST), Device.TYPE.ACC);
        Device largest = Utils.createDevice(Utils.createPlatform(TEST), Device.TYPE.SEQ);
        assertEquals(-1, least.compareTo(largest));
        assertEquals(1, largest.compareTo(least));
        assertEquals(0, least.compareTo(least));
    }
}
