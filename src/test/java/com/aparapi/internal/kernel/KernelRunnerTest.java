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
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.collections.Sets;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class KernelRunnerTest {

    private static final int[] INITIAL_ARRAY = {1};
    private static final int[] NEW_REF = {11};
    private static final int TOTAL_BUFFER_SIZE = 3;
    private static final int TOTAL_STRUCT_SIZE = 2;
    private static final int OBJ_ARRAY_SIZE = 1;
    private static final int EXPECTED_CURRENT_PASS_VALUE = 1;
    private static final int PASS_ID = 2;

    @Test
    public void shouldCleanUpArrays() throws Exception {
        Kernel kernel = mock(Kernel.class);
        KernelRunner sut = new KernelRunner(kernel);
        KernelArg arg = mock(KernelArg.class);
        KernelArg[] args = new KernelArg[]{arg};
        Field argsField = KernelRunner.class.getDeclaredField("args");
        argsField.setAccessible(true);
        argsField.set(sut, args);
        when(kernel.isRunningCL()).thenReturn(true);
        sut.cleanUpArrays();
        verify(kernel).execute(0);
    }


    @Test
    public void shouldCleanUpArraysShouldNotSetValueForFinalArrays() throws Exception {
        TestKernelWithFinalArray kernel = Mockito.spy(new TestKernelWithFinalArray(INITIAL_ARRAY));
        when(kernel.isRunningCL()).thenReturn(true);
        KernelRunner sut = new KernelRunner(kernel);
        Field field = TestKernelWithFinalArray.class.getDeclaredField("values");
        KernelArg kernelArg = Utils.createKernelArg(field, 128);
        setArgsArray(sut, kernelArg);
        sut.cleanUpArrays();
        verify(kernel).execute(0);
        assertArraysEqual(INITIAL_ARRAY, kernel);
    }

    @Test
    public void shouldCleanUpArraysShouldSetValueForNonFinalArrays() throws Exception {
        TestKernelWithNonFinalArray kernel = Mockito.spy(new TestKernelWithNonFinalArray(INITIAL_ARRAY));
        when(kernel.isRunningCL()).thenReturn(true);
        KernelRunner sut = new KernelRunner(kernel);
        Field field = TestKernelWithNonFinalArray.class.getDeclaredField("values");
        KernelArg kernelArg = Utils.createKernelArg(field, 128);
        setArgsArray(sut, kernelArg);
        sut.cleanUpArrays();
        verify(kernel).execute(0);
        assertArraysEqual((int[]) Array.newInstance(int.class, 1), kernel);
    }

    @Test
    public void shouldDisposeAndClearBinaryKeys() throws Exception {
        TestKernelWithNonFinalArray kernel = Mockito.spy(new TestKernelWithNonFinalArray(INITIAL_ARRAY));
        when(kernel.isRunningCL()).thenReturn(true);
        KernelRunner sut = new KernelRunner(kernel);
        Utils.setFieldValue(sut, "seenBinaryKeys", Sets.newSet("test"));
        sut.dispose();
        HashSet<String> keys = (HashSet<String>) Utils.getFieldValue(sut, "seenBinaryKeys");
        assertTrue(keys.isEmpty());
    }

    @Test
    public void shouldAllocateArrayBufferIfFirstTime() throws Exception {
        TestKernelWithNonFinalArray kernel = Mockito.spy(new TestKernelWithNonFinalArray(INITIAL_ARRAY));
        KernelRunner sut = new KernelRunner(kernel);
        Field field = TestKernelWithNonFinalArray.class.getDeclaredField("values");
        KernelArg kernelArg = Utils.createKernelArg(field, 128);
        assertTrue(sut.allocateArrayBufferIfFirstTimeOrArrayChanged(kernelArg, NEW_REF, OBJ_ARRAY_SIZE, TOTAL_STRUCT_SIZE, TOTAL_BUFFER_SIZE));
        ByteBuffer structBuffer = ByteBuffer.allocate(TOTAL_BUFFER_SIZE);
        assertEquals(structBuffer.order(ByteOrder.LITTLE_ENDIAN), kernelArg.getObjArrayByteBuffer());
        assertEquals(structBuffer.order(ByteOrder.LITTLE_ENDIAN).array().length, kernelArg.getObjArrayBuffer().length);
    }

    @Test
    public void shouldAllocateArrayBufferIfArrayWasChanged() throws Exception {
        TestKernelWithNonFinalArray kernel = Mockito.spy(new TestKernelWithNonFinalArray(INITIAL_ARRAY));
        KernelRunner sut = new KernelRunner(kernel);
        Field field = TestKernelWithNonFinalArray.class.getDeclaredField("values");
        KernelArg kernelArg = Utils.createKernelArg(field, 128);
        kernelArg.setObjArrayBuffer(ByteBuffer.allocate(TOTAL_BUFFER_SIZE).array());
        assertTrue(sut.allocateArrayBufferIfFirstTimeOrArrayChanged(kernelArg, NEW_REF, OBJ_ARRAY_SIZE, TOTAL_STRUCT_SIZE, TOTAL_BUFFER_SIZE));
        ByteBuffer structBuffer = ByteBuffer.allocate(TOTAL_BUFFER_SIZE);
        assertEquals(structBuffer.order(ByteOrder.LITTLE_ENDIAN), kernelArg.getObjArrayByteBuffer());
        assertEquals(structBuffer.order(ByteOrder.LITTLE_ENDIAN).array().length, kernelArg.getObjArrayBuffer().length);
    }

    @Test
    public void shouldNotAllocateArrayBufferIfArrayWasNotChanged() throws Exception {
        TestKernelWithNonFinalArray kernel = Mockito.spy(new TestKernelWithNonFinalArray(INITIAL_ARRAY));
        KernelRunner sut = new KernelRunner(kernel);
        Field field = TestKernelWithNonFinalArray.class.getDeclaredField("values");
        KernelArg kernelArg = Utils.createKernelArg(field, 128);
        kernelArg.setObjArrayBuffer(ByteBuffer.allocate(TOTAL_BUFFER_SIZE).array());
        assertTrue(sut.allocateArrayBufferIfFirstTimeOrArrayChanged(kernelArg, NEW_REF, OBJ_ARRAY_SIZE, TOTAL_STRUCT_SIZE, TOTAL_BUFFER_SIZE));
        assertFalse(sut.allocateArrayBufferIfFirstTimeOrArrayChanged(kernelArg, kernelArg.getArray(), OBJ_ARRAY_SIZE, TOTAL_STRUCT_SIZE, TOTAL_BUFFER_SIZE));
    }

    @Test
    public void shouldReturnZeroIfCancelStateWasNotSet() {
        KernelRunner sut = Utils.createKernelRunner();
        assertEquals(0, sut.getCancelState());
    }

    @Test
    public void shouldReturn1IfCancelStateWasSet() {
        KernelRunner sut = Utils.createKernelRunner();
        sut.cancelMultiPass();
        assertEquals(1, sut.getCancelState());
    }

    @Test
    public void shouldReturn0IfCancelStateWasCleared() throws Exception {
        KernelRunner sut = Utils.createKernelRunner();
        sut.cancelMultiPass();
        assertEquals(1, sut.getCancelState());
        Method clearCancelMultiPass = KernelRunner.class.getDeclaredMethod("clearCancelMultiPass");
        clearCancelMultiPass.setAccessible(true);
        clearCancelMultiPass.invoke(sut);
        assertEquals(0, sut.getCancelState());
    }

    @Test
    public void shouldReturnCompletedAsCurrentPassIfNotExecuting() {
        KernelRunner sut = Utils.createKernelRunner();
        assertEquals(-1, sut.getCurrentPass());
    }

    @Test
    public void shouldReturnCurrentPassRemoteIfKernelIsExecuting() throws Exception {
        TestKernelWithNonFinalArray kernel = Mockito.spy(new TestKernelWithNonFinalArray(INITIAL_ARRAY));
        KernelRunner sut = new KernelRunner(kernel);
        IntBuffer outBufferRemoteInt = ByteBuffer.allocateDirect(4).asIntBuffer();
        outBufferRemoteInt.put(EXPECTED_CURRENT_PASS_VALUE);
        Utils.setFieldValue(sut, "executing", true);
        Utils.setFieldValue(sut, "outBufferRemoteInt", outBufferRemoteInt);
        when(kernel.isRunningCL()).thenReturn(true);
        assertEquals(EXPECTED_CURRENT_PASS_VALUE, sut.getCurrentPass());
    }

    @Test
    public void shouldReturnCurrentPassLocalIfKernelIsExecuting() throws Exception {
        TestKernelWithNonFinalArray kernel = Mockito.spy(new TestKernelWithNonFinalArray(INITIAL_ARRAY));
        KernelRunner sut = new KernelRunner(kernel);
        Utils.setFieldValue(sut, "executing", true);
        Utils.setFieldValue(sut, "passId", PASS_ID);
        when(kernel.isRunningCL()).thenReturn(false);
        assertEquals(PASS_ID, sut.getCurrentPass());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldExecuteShouldThrowExceptionIfRangeIsNull() throws Exception {
        TestKernelWithNonFinalArray kernel = Mockito.spy(new TestKernelWithNonFinalArray(INITIAL_ARRAY));
        KernelRunner sut = new KernelRunner(kernel);
        sut.execute("", null, 1);
    }

    @Test
    public void shouldReturnNullForKernelProfileInfoIfNotExplicit() throws Exception {
        TestKernelWithNonFinalArray kernel = Mockito.spy(new TestKernelWithNonFinalArray(INITIAL_ARRAY));
        KernelRunner sut = new KernelRunner(kernel);
        when(kernel.isRunningCL()).thenReturn(true);
        assertEquals(null, sut.getProfileInfo());
    }

    @Test
    public void shouldReturnNullForKernelProfileInfoIfExplicitKernelNotCL() throws Exception {
        TestKernelWithNonFinalArray kernel = Mockito.spy(new TestKernelWithNonFinalArray(INITIAL_ARRAY));
        KernelRunner sut = new KernelRunner(kernel);
        Utils.setFieldValue(sut, "explicit", true);
        when(kernel.isRunningCL()).thenReturn(false);
        assertEquals(null, sut.getProfileInfo());
    }

    @Test
    public void shouldPutArrayIfExplicitAndKernelIsOpenCL() throws Exception {
        TestKernelWithNonFinalArray kernel = Mockito.spy(new TestKernelWithNonFinalArray(INITIAL_ARRAY));
        KernelRunner sut = new KernelRunner(kernel);
        Utils.setFieldValue(sut, "explicit", true);
        when(kernel.isRunningCL()).thenReturn(true);
        sut.put(INITIAL_ARRAY);
        Set actualAray = (Set) Utils.getFieldValue(sut, "puts");
        assertFalse(actualAray.isEmpty());
        assertEquals(actualAray.iterator().next(), INITIAL_ARRAY);
    }

    private static void assertArraysEqual(int[] expectedArray, TestKernelWithNonFinalArray updatedKernel) {
        assertTrue(Arrays.equals(expectedArray, updatedKernel.values));
    }

    private static void assertArraysEqual(int[] expectedArray, TestKernelWithFinalArray updatedKernel) {
        assertTrue(Arrays.equals(expectedArray, updatedKernel.values));
    }

    private static void setArgsArray(KernelRunner kernelRunner, KernelArg... args) throws Exception {
        Field argsField = KernelRunner.class.getDeclaredField("args");
        argsField.setAccessible(true);
        argsField.set(kernelRunner, args);
    }

    private static class TestKernelWithFinalArray extends Kernel {
        private final int[] values;

        private TestKernelWithFinalArray(int[] values) {
            this.values = values;
        }

        @Override
        public void run() {

        }
    }

    private static class TestKernelWithNonFinalArray extends Kernel {
        private int[] values;

        private TestKernelWithNonFinalArray(int[] values) {
            this.values = values;
        }

        @Override
        public void run() {

        }
    }
}
