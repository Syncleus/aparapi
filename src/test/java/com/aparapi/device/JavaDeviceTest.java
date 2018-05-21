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
