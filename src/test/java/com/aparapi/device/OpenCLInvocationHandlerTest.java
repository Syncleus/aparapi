package com.aparapi.device;

import com.aparapi.internal.opencl.OpenCLKernel;
import com.aparapi.internal.opencl.OpenCLProgram;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class OpenCLInvocationHandlerTest {

    private static final String PROXY = "test";
    private static final Object[] ARGS = new Object[0];

    @Test
    public void shouldInvokeCorrespondingKernelForNonInterfaceMethods() throws Throwable {
        Method method = Object.class.getMethod("toString");
        OpenCLProgram program = mock(OpenCLProgram.class);
        Map<String, OpenCLKernel> kernelMap = new HashMap<>();
        OpenCLKernel kernel = mock(OpenCLKernel.class);
        kernelMap.put(method.getName(), kernel);
        InvocationHandler sut = new OpenCLDevice.OpenCLInvocationHandler(program, kernelMap);
        Object[] args = new Object[0];
        assertEquals(PROXY, sut.invoke(PROXY, method, args));
        verify(kernel).invoke(args);
    }

    @Test
    public void shouldDoNothingIfThereAreNoCorrespondingKernelForNonInterfaceMethods() throws Throwable {
        Method method = Object.class.getMethod("toString");
        OpenCLProgram program = mock(OpenCLProgram.class);
        InvocationHandler sut = new OpenCLDevice.OpenCLInvocationHandler(program, new HashMap<>());
        assertEquals(PROXY, sut.invoke(PROXY, method, ARGS));
    }

    @Test
    public void shouldDisposeAllKernelsForDispose() throws Throwable {
        // set up test variables
        Method method = ReservedInterfaceMethods.class.getMethod("dispose");
        OpenCLProgram program = mock(OpenCLProgram.class);
        Map<String, OpenCLKernel> kernelMap = new HashMap<>();
        OpenCLKernel disposeKernel = mock(OpenCLKernel.class);
        kernelMap.put(method.getName(), disposeKernel);
        OpenCLKernel putKernel = mock(OpenCLKernel.class);
        kernelMap.put("put", putKernel);
        InvocationHandler sut = new OpenCLDevice.OpenCLInvocationHandler(program, kernelMap);
        // test
        sut.invoke(PROXY, method, ARGS);
        //checks
        verify(disposeKernel).dispose();
        verify(putKernel).dispose();
        verify(program).dispose();
        assertTrue(kernelMap.isEmpty());
        //ensure that disposed has been set to true
        Field f = sut.getClass().getDeclaredField("disposed"); //NoSuchFieldException
        f.setAccessible(true);
        assertTrue((Boolean) f.get(sut));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowIllegalStateExceptionIfAlreadyDisposed() throws Throwable {
        // set up test variables
        Method method = ReservedInterfaceMethods.class.getMethod("dispose");
        OpenCLProgram program = mock(OpenCLProgram.class);
        Map<String, OpenCLKernel> kernelMap = new HashMap<>();
        OpenCLKernel disposeKernel = mock(OpenCLKernel.class);
        kernelMap.put(method.getName(), disposeKernel);
        InvocationHandler sut = new OpenCLDevice.OpenCLInvocationHandler(program, kernelMap);
        // test
        sut.invoke(PROXY, method, ARGS);
        sut.invoke(PROXY, method, ARGS);
    }


    static class ReservedInterfaceMethods {
        public void put() {
        }

        ;

        public void get() {
        }

        ;

        public void begin() {
        }

        ;

        public void end() {
        }

        ;

        public void dispose() {
        }

        ;

        public void getProfileInfo() {
        }

        ;
    }
}
