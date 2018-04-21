/**
 * Copyright (c) 2016 - 2018 Syncleus, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aparapi.runtime;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.aparapi.IProfileReportObserver;
import com.aparapi.Kernel;
import com.aparapi.ProfileReport;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.JavaDevice;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.KernelManager;

/**
 * Provides integration tests to help in assuring that new APIs for ProfileReports are working,
 * in single threaded and multi-threaded environments. 
 * 
 * @author CoreRasurae
 */
public class ProfileReportNewAPITest {

	private static OpenCLDevice openCLDevice;
	
	@Rule 
	public TestName name = new TestName();

	
    private class CLKernelManager extends KernelManager {
    	@Override
    	protected List<Device.TYPE> getPreferredDeviceTypes() {
    		return Arrays.asList(Device.TYPE.ACC, Device.TYPE.GPU, Device.TYPE.CPU);
    	}
    }
    
    private class JTPKernelManager extends KernelManager {
    	private JTPKernelManager() {
    		LinkedHashSet<Device> preferredDevices = new LinkedHashSet<Device>(1);
    		preferredDevices.add(JavaDevice.THREAD_POOL);
    		setDefaultPreferredDevices(preferredDevices);
    	}
    	@Override
    	protected List<Device.TYPE> getPreferredDeviceTypes() {
    		return Arrays.asList(Device.TYPE.JTP);
    	}
    }
    
    public void setUpBefore() throws Exception {
    	KernelManager.setKernelManager(new CLKernelManager());
        Device device = KernelManager.instance().bestDevice();
        if (device == null || !(device instanceof OpenCLDevice)) {
        	System.out.println("!!!No OpenCLDevice available for running the integration test - test will be skipped");
        }
        assumeTrue (device != null && device instanceof OpenCLDevice);
        openCLDevice = (OpenCLDevice) device;
    }

    /**
     * Tests the ProfileReport observer interface in a single threaded, single kernel environment running on 
     * an OpenCL device.
     * @throws Exception 
     */
    @Test
    public void singleThreadedSingleKernelObserverOpenCLTest() throws Exception {
    	setUpBefore();
    	System.out.println("Test " + name.getMethodName() + " - Executing on device: " + openCLDevice.getShortDescription() + " - " + openCLDevice.getName());
    	assertTrue(singleThreadedSingleKernelReportObserverTestHelper(openCLDevice, 128));
    }

    /**
     * Tests the ProfileReport observer interface in a single threaded, single kernel environment running on 
     * Java Thread Pool.
     */
    @Test
    public void singleThreadedSingleKernelObserverJTPTest() {
    	KernelManager.setKernelManager(new JTPKernelManager());
    	Device device = KernelManager.instance().bestDevice();
    	assertTrue(singleThreadedSingleKernelReportObserverTestHelper(device, 16));
    }

    private class ThreadTestState {
    	private double accumulatedElapsedTime = 0.0;
    	private long receivedReportsCount = 0;
    }
    
    private class ReportObserver implements IProfileReportObserver {
    	private final ConcurrentSkipListSet<Long> expectedThreadsIds = new ConcurrentSkipListSet<>();
    	private final ConcurrentSkipListMap<Long, ThreadTestState> observedThreadsIds = new ConcurrentSkipListMap<>();
    	private final Device device;
    	private final int threads;
    	private final int runs;
    	private final boolean[] receivedReportIds;
    	
    	private ReportObserver(Device _device, int _threads, int _runs) {
    		device = _device;
    		threads = _threads;
    		runs = _runs;
    	
    		receivedReportIds = new boolean[threads * runs];
    	}
    	
    	private void addAcceptedThreadId(long threadId) {
    		expectedThreadsIds.add(threadId);
    	}
    	
    	private ConcurrentSkipListMap<Long, ThreadTestState> getObservedThreadsIds() {
    		return observedThreadsIds;
    	}
    	    	
		@Override
		public void receiveReport(Class<? extends Kernel> kernelClass, Device _device, WeakReference<ProfileReport> profileInfoRef) {
			ProfileReport profileInfo = profileInfoRef.get();
			assertEquals("Kernel class does not match", Basic1Kernel.class, kernelClass);
			assertEquals("Device does not match", device, _device);
			boolean isThreadAccepted = expectedThreadsIds.contains(profileInfo.getThreadId());
			assertTrue("Thread generating the report (" + profileInfo.getThreadId() + 
					") is not among the accepted ones: " + expectedThreadsIds.toString(), isThreadAccepted);
			Long threadId = profileInfo.getThreadId();
			ThreadTestState state = observedThreadsIds.computeIfAbsent(threadId, k -> new ThreadTestState());
			state.accumulatedElapsedTime += profileInfo.getExecutionTime();
			state.receivedReportsCount++;
			receivedReportIds[(int)profileInfo.getReportId() - 1] = true;
		}
    }
    
    public boolean singleThreadedSingleKernelReportObserverTestHelper(Device device, int size) {
    	final int runs = 100;
    	final int inputArray[] = new int[size];
    	final Basic1Kernel kernel = new Basic1Kernel();
    	
    	int[] outputArray = null;
    	Range range = device.createRange(size, size);
    	
    	ReportObserver observer = new ReportObserver(device, 1, runs);
    	observer.addAcceptedThreadId(Thread.currentThread().getId());
    	kernel.registerProfileReportObserver(observer);

		for (int i = 0; i < runs; i++) {
			assertFalse("Report with id " + i + " shouldn't have been received yet", observer.receivedReportIds[i]);
		}
    	
    	long startOfExecution = System.currentTimeMillis();
    	try {
    		for (int i = 0; i < runs; i++) {
    			outputArray = Arrays.copyOf(inputArray, inputArray.length);
    			kernel.setInputOuputArray(outputArray);
    			kernel.execute(range);
    		}
    		long runTime = System.currentTimeMillis() - startOfExecution;
    		ConcurrentSkipListMap<Long, ThreadTestState> results = observer.getObservedThreadsIds();
    		ThreadTestState state = results.get(Thread.currentThread().getId());
    		assertNotNull("Reports should have been received for thread", state);

    		assertEquals("Number of profiling reports doesn't match the expected", runs, state.receivedReportsCount);
    		assertEquals("Aparapi Accumulated execution time doesn't match", kernel.getAccumulatedExecutionTimeAllThreads(device), state.accumulatedElapsedTime, 1e-10);
    		assertEquals("Test estimated accumulated time doesn't match within 200ms window", runTime, kernel.getAccumulatedExecutionTimeAllThreads(device), 200);
    		for (int i = 0; i < runs; i++) {
    			assertTrue("Report with id " + i + " wasn't received", observer.receivedReportIds[i]);
    		}
    		assertTrue(validateBasic1Kernel(inputArray, outputArray));
    	} finally {
    		kernel.dispose();
    	}
    	
    	return true;
    }

    /**
     * Tests the ProfileReport observer interface in a multi threaded, single kernel environment running on 
     * an OpenCL device.
     */
    @Test
    public void multiThreadedSingleKernelObserverOpenCLTest() throws Exception {
    	setUpBefore();
    	System.out.println("Test " + name.getMethodName() + " - Executing on device: " + openCLDevice.getShortDescription() + " - " + openCLDevice.getName());
    	assertTrue(multiThreadedSingleKernelReportObserverTestHelper(openCLDevice, 128));
    }

    /**
     * Tests the ProfileReport observer interface in a multi threaded, single kernel environment running on 
     * Java Thread Pool.
     */
    @Test
    public void multiThreadedSingleKernelObserverJTPTest() throws Exception  {
    	KernelManager.setKernelManager(new JTPKernelManager());
    	Device device = KernelManager.instance().bestDevice();
    	assertTrue(multiThreadedSingleKernelReportObserverTestHelper(device, 16));
    }

    private class ThreadResults {
    	private long runTime;
    	private long threadId;
    	private int kernelCalls;
    	private double accumulatedExecutionTime;
    	private int[] outputArray;
    }
    
    @SuppressWarnings("unchecked")
    public boolean multiThreadedSingleKernelReportObserverTestRunner(final ExecutorService executorService, 
    		final List<Basic1Kernel> kernels, final ThreadResults[] results, int[] inputArray, int runs, int javaThreads,
    		final Device device, final ReportObserver observer, int size) throws InterruptedException, ExecutionException {
    	final AtomicInteger atomicResultId = new AtomicInteger(0);
      	boolean terminatedOk = false;
    	try {
    		List<Future<Runnable>> futures = new ArrayList<>(javaThreads); 
    		for (Basic1Kernel k : kernels) {
				futures.add((Future<Runnable>)executorService.submit(new Runnable() {
					@Override
					public void run() {
						int id = atomicResultId.getAndIncrement();
						results[id].threadId = Thread.currentThread().getId();
						observer.addAcceptedThreadId(results[id].threadId);
						long startOfExecution = System.currentTimeMillis();
						results[id].kernelCalls = 0;
						for (int i = 0; i < runs; i++) {
							results[id].outputArray = Arrays.copyOf(inputArray, inputArray.length);
							k.setInputOuputArray(results[id].outputArray);
		    				k.execute(Range.create(device, size, size));
		    				results[id].kernelCalls++;
						}
						results[id].runTime = System.currentTimeMillis() - startOfExecution;
						results[id].accumulatedExecutionTime = k.getAccumulatedExecutionTimeCurrentThread(device);
					}
				}));
    		}
    		for (Future<Runnable> future : futures) {
    			future.get();
    		}
    	} finally {
    		executorService.shutdown();
    		try {
    			terminatedOk = executorService.awaitTermination(1, TimeUnit.MINUTES);
    		} catch (InterruptedException ex) {
    			//For the purposes of the test this suffices
    			terminatedOk = false;
    		}
            if (!terminatedOk) {
            	executorService.shutdownNow();
            }
    	}
    	
    	return terminatedOk;
    }
    
	public boolean multiThreadedSingleKernelReportObserverTestHelper(Device device, int size) throws InterruptedException, ExecutionException {
    	final int runs = 100;
    	final int javaThreads = 10;
    	final int inputArray[] = new int[size];
    	ExecutorService executorService = Executors.newFixedThreadPool(javaThreads);
    	
    	final ReportObserver observer = new ReportObserver(device, javaThreads, runs);

		for (int i = 0; i < runs; i++) {
			assertFalse("Report with id " + i + " shouldn't have been received yet", observer.receivedReportIds[i]);
		}
    	
    	final List<Basic1Kernel> kernels = new ArrayList<Basic1Kernel>(javaThreads);
    	for (int i = 0; i < javaThreads; i++) {
        	final Basic1Kernel kernel = new Basic1Kernel();
        	kernel.registerProfileReportObserver(observer);
        	kernels.add(kernel);
    	}
    	
    	final ThreadResults[] results = new ThreadResults[javaThreads];
    	for (int i = 0; i < results.length; i++) {
    		results[i] = new ThreadResults();
    	}
    	
  
    	boolean terminatedOk = multiThreadedSingleKernelReportObserverTestRunner(executorService, kernels, results, 
    			inputArray, runs, javaThreads, device, observer, size);
    	
    	assertTrue("Threads did not terminate correctly", terminatedOk);
    
    	double allThreadsAccumulatedTime = 0;
    	ConcurrentSkipListMap<Long, ThreadTestState> states = observer.getObservedThreadsIds();
    	assertEquals("Number of Java threads sending profile reports should match the number of JavaThreads", javaThreads, states.values().size());
    	for (int i = 0; i < javaThreads; i++) {
    		ThreadTestState state = states.get(results[i].threadId);
    		assertNotNull("Report should have been received for thread with index " + i, state);
    		assertEquals("Number of total iteration should match number of runs for thread with index " + i, runs, results[i].kernelCalls);
        	assertEquals("Number of received reports should match total number of calls for thread with index " + i, runs, state.receivedReportsCount);
        	assertEquals("Overall elapsed time received in reports doesn't match KernelDeviceProfile.Accumulator for threa with index " + i,
        			results[i].accumulatedExecutionTime, state.accumulatedElapsedTime, 1e-10);
        	allThreadsAccumulatedTime += state.accumulatedElapsedTime;
        	assertTrue("Thread index " + i + " kernel computation doesn't match the expected", validateBasic1Kernel(inputArray, results[i].outputArray));
        	assertEquals("Runtime is not within 600ms of the kernel estimated", results[i].runTime, state.accumulatedElapsedTime, 600);
    	}
    	
    	assertEquals("Overall kernel execution time doesn't match", 
    			kernels.get(0).getAccumulatedExecutionTimeAllThreads(device), allThreadsAccumulatedTime, 1e10);
    	
    	return true;
    }
    
    private boolean validateBasic1Kernel(final int[] inputArray, final int[] resultArray) {
    	int[] expecteds = Arrays.copyOf(inputArray, inputArray.length);
    	for (int threadId = 0; threadId < inputArray.length; threadId++) {
    		expecteds[threadId] += threadId;
    	}
    	
    	assertArrayEquals(expecteds, resultArray);
    	
    	return true;
    }

    private class Basic1Kernel extends Kernel {
    	protected int[] workArray;
    	
    	@NoCL
    	public void setInputOuputArray(int[] array) {
    		workArray = array;
    	}

		@NoCL
    	public int getId() {
    		return 1;
    	}
    	
		@Override
		public void run() {
			int id = getLocalId();
			
			workArray[id]+=id;
		}
    }    
}
