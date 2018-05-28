package com.aparapi.internal.opencl;

import com.aparapi.device.OpenCLDevice;
import org.junit.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;

public class OpenCLKernelTest {

    @Test
    public void shouldCreateOpenCLKernel() {
        OpenCLProgram program = new OpenCLProgram(mock(OpenCLDevice.class), "test");

        OpenCLKernel kernel = OpenCLKernel.createKernel(program, "test", new ArrayList<>());
    }
}
