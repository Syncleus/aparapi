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

import static org.junit.Assert.assertEquals;

public class UnsafeWrapperTest {

    @Test
    public void shouldAtomicAdd() {
        int[] array = new int[]{1, 2};
        UnsafeWrapper.atomicAdd(array, 1, 3);
        assertEquals(array[1], 5);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldAtomicAddShouldThrowAnExceptionIfIndexIsLessThanZero() {
        int[] array = new int[]{1, 2};
        UnsafeWrapper.atomicAdd(array, -1, 3);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldAtomicAddShouldThrowAnExceptionIfIndexIsOutOfBounds() {
        int[] array = new int[]{};
        UnsafeWrapper.atomicAdd(array, 0, 3);
    }

    @Test
    public void shouldGetInt() {
//        assertEquals(5 , UnsafeWrapper.getInt(new IntHolder(), 0));
    }


//    @Test
//    public void shouldGetFloat(){
//        assertEquals(5 , UnsafeWrapper.getFloat(new Float(11), 0));
//    }


    private static class IntHolder {
        private int value = 10;
    }


}
