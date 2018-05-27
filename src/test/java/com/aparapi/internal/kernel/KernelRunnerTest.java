package com.aparapi.internal.kernel;

import com.aparapi.Kernel;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class KernelRunnerTest {

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
    public void shouldCleanUpArrays1() throws Exception {
        Kernel kernel = mock(Kernel.class);
        KernelRunner sut = new KernelRunner(kernel);
        KernelArg arg = mock(KernelArg.class);
        when(arg.getType()).thenReturn(128);
        KernelArg[] args = new KernelArg[]{arg};
        Field argsField = KernelRunner.class.getDeclaredField("args");
        argsField.setAccessible(true);
        argsField.set(sut, args);
        when(kernel.isRunningCL()).thenReturn(true);
        sut.cleanUpArrays();
        verify(kernel).execute(0);
    }
}
