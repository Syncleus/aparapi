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
package com.aparapi.internal.kernel;

import com.aparapi.Kernel;
import com.aparapi.device.Device;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Field;
import java.util.Map;

import static com.aparapi.internal.kernel.ProfilingEvent.*;
import static com.aparapi.internal.kernel.Utils.createKernelDeviceProfile;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;

public class KernelDeviceProfileTest {
    private static final double MILLION = 1000 * 1000;

    @Test
    public void shouldReturnZeroForElapsedTimeForStageStart() {
        KernelDeviceProfile sut = createKernelDeviceProfile();
        assertEquals(0d, sut.getElapsedTimeCurrentThread(START.ordinal()));
    }

    @Test
    public void shouldReturnNonZeroForElapsedTimeIfProfilingEventWasCalled() {
        KernelDeviceProfile sut = createKernelDeviceProfile();
        sut.onEvent(PREPARE_EXECUTE);
        long[] currentTimes = getCurrentTimesArrayForCurrentThread(sut);
        long prepareExecuteTime = currentTimes[PREPARE_EXECUTE.ordinal()];
        assertTrue(prepareExecuteTime > 0);
        assertEquals(prepareExecuteTime / MILLION, sut.getElapsedTimeCurrentThread(PREPARE_EXECUTE.ordinal()));
    }

    @Test
    public void shouldReturnNaNForElapsedTimeIfProfilingEventWasNotCalled() {
        KernelDeviceProfile sut = createKernelDeviceProfile();
        assertEquals(Double.NaN, sut.getElapsedTimeCurrentThread(EXECUTED.ordinal()));
    }

    @Test
    public void shouldReturnZeroElapsedTimeIfNothingWasProfiled() {
        KernelDeviceProfile sut = createKernelDeviceProfile();
        assertEquals(0d, sut.getCumulativeElapsedTimeAllCurrentThread());
    }

    @Test
    public void shouldReturnZeroForProfiledEventsIfExecutedEventWasNotCalled() {
        KernelDeviceProfile sut = createKernelDeviceProfile();
        sut.onEvent(START);
        sut.onEvent(ProfilingEvent.PREPARE_EXECUTE);
        sut.onEvent(ProfilingEvent.CLASS_MODEL_BUILT);
        assertEquals(0d, sut.getCumulativeElapsedTimeAllCurrentThread());
    }

    @Test
    public void shouldReturnSumOfProfileTimesForProfiledEventsAfterExecutedEventIsProfiled() {
        KernelDeviceProfile sut = createKernelDeviceProfile();
        sut.onEvent(START);
        sut.onEvent(ProfilingEvent.PREPARE_EXECUTE);
        sut.onEvent(ProfilingEvent.EXECUTED);
        long[] accumulatedTimes = getAccumulatedTimesArrayForCurrentThread(sut);
        long prepareExecuteTime = accumulatedTimes[PREPARE_EXECUTE.ordinal()];
        long executedTime = accumulatedTimes[EXECUTED.ordinal()];
        assertTrue(prepareExecuteTime > 0);
        assertTrue(executedTime > 0);
        assertEquals((double) (prepareExecuteTime + executedTime), sut.getCumulativeElapsedTimeAllCurrentThread());
    }

    @Test
    public void shouldReturnZeroForElapsedTimeLastThreadForStageStart() {
        KernelDeviceProfile sut = createKernelDeviceProfile();
        assertEquals(0d, sut.getElapsedTimeLastThread(START.ordinal()));
    }

    @Test
    public void shouldReturnNanForElapsedTimeLastThreadIfNothingWasProfiled() {
        KernelDeviceProfile sut = createKernelDeviceProfile();
        assertEquals(Double.NaN, sut.getElapsedTimeLastThread(EXECUTED.ordinal()));
    }

    @Test
    public void shouldReturnDifferenceBetweenElapsedTimesForElapsedTimeLastThreadIfTheyWereProfiled() {
        KernelDeviceProfile sut = createKernelDeviceProfile();
        sut.onEvent(ProfilingEvent.START);
        sut.onEvent(ProfilingEvent.CLASS_MODEL_BUILT);
        sut.onEvent(ProfilingEvent.INIT_JNI);
        sut.onEvent(EXECUTED);
        long[] currentTimes = getCurrentTimesArrayForCurrentThread(sut);
        long jniExecuteTime = currentTimes[INIT_JNI.ordinal()];
        long classModelBuiltTime = currentTimes[CLASS_MODEL_BUILT.ordinal()];
        assertTrue(classModelBuiltTime > 0);
        assertTrue(jniExecuteTime > 0);
        assertEquals((jniExecuteTime - classModelBuiltTime) / MILLION, sut.getElapsedTimeLastThread(INIT_JNI.ordinal()));
    }

    @Test
    public void shouldReturnDifferenceBetweenElapsedTimesForLastThreadByIndex() {
        KernelDeviceProfile sut = createKernelDeviceProfile();
        sut.onEvent(ProfilingEvent.START);
        sut.onEvent(ProfilingEvent.CLASS_MODEL_BUILT);
        sut.onEvent(ProfilingEvent.INIT_JNI);
        sut.onEvent(EXECUTED);
        long[] currentTimes = getCurrentTimesArrayForCurrentThread(sut);
        long jniExecuteTime = currentTimes[INIT_JNI.ordinal()];
        long classModelBuiltTime = currentTimes[CLASS_MODEL_BUILT.ordinal()];
        assertTrue(classModelBuiltTime > 0);
        assertTrue(jniExecuteTime > 0);
        assertEquals((jniExecuteTime - classModelBuiltTime) / MILLION, sut.getElapsedTimeLastThread(INIT_JNI.ordinal(), EXECUTED.ordinal()));
        assertTrue(sut.getElapsedTimeLastThread(INIT_JNI.ordinal(), EXECUTED.ordinal()) > 0);
    }

    private static long[] getCurrentTimesArrayForCurrentThread(KernelDeviceProfile kernelDeviceProfile) {
        try {
            Map allAccumulators = getAccumulatorMap(kernelDeviceProfile);
            Object currentThreadAccumulator = allAccumulators.get(Thread.currentThread());
            return (long[]) getFieldAndMakeItAccessible(currentThreadAccumulator.getClass(), "currentTimes")
                .get(currentThreadAccumulator);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static long[] getAccumulatedTimesArrayForCurrentThread(KernelDeviceProfile kernelDeviceProfile) {
        try {
            Map allAccumulators = getAccumulatorMap(kernelDeviceProfile);
            Object currentThreadAccumulator = allAccumulators.get(Thread.currentThread());
            return (long[]) getFieldAndMakeItAccessible(currentThreadAccumulator.getClass(), "accumulatedTimes")
                .get(currentThreadAccumulator);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static Map getAccumulatorMap(KernelDeviceProfile kernelDeviceProfile) {
        try {
            return (Map) getFieldAndMakeItAccessible(KernelDeviceProfile.class, "accs").get(kernelDeviceProfile);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static Field getFieldAndMakeItAccessible(Class<?> clazz, String fieldName) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e.getMessage());
        }
        field.setAccessible(true);
        return field;
    }
}
