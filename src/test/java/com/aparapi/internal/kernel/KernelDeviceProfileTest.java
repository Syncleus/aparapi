package com.aparapi.internal.kernel;

import com.aparapi.Kernel;
import com.aparapi.device.Device;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static com.aparapi.internal.kernel.ProfilingEvent.*;
import static com.aparapi.internal.kernel.Utils.createKernelDeviceProfile;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;

public class KernelDeviceProfileTest {

    @Test
    public void shouldReturnZeroForElapsedTimeForStageStart() {
        KernelDeviceProfile sut = createKernelDeviceProfile();
        assertEquals(0d, sut.getElapsedTimeCurrentThread(START.ordinal()));
    }

    @Test
    public void shouldReturnNonZeroForElapsedTimeIfProfilingEventWasCalled() {
        KernelDeviceProfile sut = createKernelDeviceProfile();
        sut.onEvent(PREPARE_EXECUTE);
        assertTrue(sut.getElapsedTimeCurrentThread(PREPARE_EXECUTE.ordinal()) > 0);
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
        double prepareExecuteTime = sut.getElapsedTimeCurrentThread(PREPARE_EXECUTE.ordinal());
        sut.onEvent(ProfilingEvent.EXECUTED);
        double executedTime = sut.getElapsedTimeCurrentThread(EXECUTED.ordinal());
        assertTrue(sut.getCumulativeElapsedTimeAllCurrentThread() >= prepareExecuteTime + executedTime);
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
        assertTrue(sut.getElapsedTimeLastThread(INIT_JNI.ordinal()) > 0);
    }

    @Test
    public void shouldReturnDifferenceBetweenElapsedTimesForLastThreadByIndex() {
        KernelDeviceProfile sut = createKernelDeviceProfile();
        sut.onEvent(ProfilingEvent.START);
        sut.onEvent(ProfilingEvent.CLASS_MODEL_BUILT);
        sut.onEvent(ProfilingEvent.INIT_JNI);
        sut.onEvent(EXECUTED);
        assertTrue(sut.getElapsedTimeLastThread(INIT_JNI.ordinal(), EXECUTED.ordinal()) > 0);
    }
}
