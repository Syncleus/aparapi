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
package com.aparapi.internal.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
public class ReflectionTest {

    @Test
    public void shouldReturnSimpleNameForNonAnonymousClass() {
        assertEquals("String", Reflection.getSimpleName(String.class));
    }

    @Test
    public void shouldReturnSimpleNameForAnonymousClass() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

            }
        };
        assertTrue(Reflection.getSimpleName(runnable.getClass()).startsWith("ReflectionTest"));
    }
}
