/**
 * Copyright (c) 2016 - 2018 Syncleus, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aparapi.device;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JavaDeviceTest {

    @Test
    public void shouldReturnCorrectPropertiesForThreadPoolDevice() {
        assertEquals("Java Thread Pool", JavaDevice.THREAD_POOL.getShortDescription());
        assertEquals(-3, JavaDevice.THREAD_POOL.getDeviceId());
        assertEquals(Device.TYPE.JTP, JavaDevice.THREAD_POOL.getType());
        assertEquals("Java Thread Pool", JavaDevice.THREAD_POOL.toString());

    }

    @Test
    public void shouldReturnCorrectPropertiesForAlternativeAlgorithm() {
        assertEquals("Java Alternative Algorithm", JavaDevice.ALTERNATIVE_ALGORITHM.getShortDescription());
        assertEquals(-2, JavaDevice.ALTERNATIVE_ALGORITHM.getDeviceId());
        assertEquals(Device.TYPE.ALT, JavaDevice.ALTERNATIVE_ALGORITHM.getType());
        assertEquals("Java Alternative Algorithm", JavaDevice.ALTERNATIVE_ALGORITHM.toString());

    }

    @Test
    public void shouldReturnCorrectPropertiesForSequential() {
        assertEquals("Java Sequential", JavaDevice.SEQUENTIAL.getShortDescription());
        assertEquals(-1, JavaDevice.SEQUENTIAL.getDeviceId());
        assertEquals(Device.TYPE.SEQ, JavaDevice.SEQUENTIAL.getType());
        assertEquals("Java Sequential", JavaDevice.SEQUENTIAL.toString());

    }
}
