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
        assertEquals(JavaDevice.THREAD_POOL.getShortDescription(), "Java Thread Pool");
        assertEquals(JavaDevice.THREAD_POOL.getDeviceId(), -3);
        assertEquals(JavaDevice.THREAD_POOL.getType(), Device.TYPE.JTP);
        assertEquals(JavaDevice.THREAD_POOL.toString(), "Java Thread Pool");

    }

    @Test
    public void shouldReturnCorrectPropertiesForAlternativeAlgorithm() {
        assertEquals(JavaDevice.ALTERNATIVE_ALGORITHM.getShortDescription(), "Java Alternative Algorithm");
        assertEquals(JavaDevice.ALTERNATIVE_ALGORITHM.getDeviceId(), -2);
        assertEquals(JavaDevice.ALTERNATIVE_ALGORITHM.getType(), Device.TYPE.ALT);
        assertEquals(JavaDevice.ALTERNATIVE_ALGORITHM.toString(), "Java Alternative Algorithm");

    }

    @Test
    public void shouldReturnCorrectPropertiesForSequential() {
        assertEquals(JavaDevice.SEQUENTIAL.getShortDescription(), "Java Sequential");
        assertEquals(JavaDevice.SEQUENTIAL.getDeviceId(), -1);
        assertEquals(JavaDevice.SEQUENTIAL.getType(), Device.TYPE.SEQ);
        assertEquals(JavaDevice.SEQUENTIAL.toString(), "Java Sequential");

    }
}
