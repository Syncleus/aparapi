package com.aparapi.runtime;

import static org.junit.Assume.assumeTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;
import org.junit.Before;
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
    
    @Before
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
     */
    @Test
    public void singleThreadedSingleKernelObserverOpenCLTest() {
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

    private class ReportObserverNoConcurrentCalls implements IProfileReportObserver {
    	private final ConcurrentSkipListSet<Long> expectedThreadsIds = new ConcurrentSkipListSet<>();
    	private final ConcurrentSkipListSet<Long> observedThreadsIds = new ConcurrentSkipListSet<>();
    	private final AtomicInteger atomicSimultaneousCalls = new AtomicInteger(0);
    	private final Device device;
    	private long receivedReportsCount = 0;
    	private double accumulatedElapsedTime = 0.0;
    	
    	private ReportObserverNoConcurrentCalls(Device _device) {
    		device = _device;
    	}
    	
    	private void addAcceptedThreadId(long threadId) {
    		expectedThreadsIds.add(threadId);
    	}
    	
    	private double getAccumulatedElapsedTime() {
    		return accumulatedElapsedTime;
    	}
    	
    	private long getReceivedReportsCount() {
    		return receivedReportsCount;
    	}
    	
		@Override
		public void receiveReport(Class<? extends Kernel> kernelClass, Device _device, ProfileReport profileInfo) {
			int currentSimultaneousCalls = atomicSimultaneousCalls.incrementAndGet();
			assertTrue("Observer was called concurrently", currentSimultaneousCalls == 1);
			accumulatedElapsedTime += profileInfo.getExecutionTime();
			assertEquals("Kernel class does not match", Basic1Kernel.class, kernelClass);
			assertEquals("Device does not match", device, _device);
			boolean isThreadAccepted = expectedThreadsIds.contains(profileInfo.getThreadId());
			assertTrue("Thread generating the report (" + profileInfo.getThreadId() + 
					") is not among the accepted ones: " + expectedThreadsIds.toString(), isThreadAccepted);
			++receivedReportsCount;
			assertEquals("Received report count doesn't match current ID - reports were lost", receivedReportsCount, profileInfo.getReportId());
			observedThreadsIds.add(profileInfo.getThreadId());
			atomicSimultaneousCalls.decrementAndGet();
		}
    }
    
    public boolean singleThreadedSingleKernelReportObserverTestHelper(Device device, int size) {
    	final int runs = 100;
    	final int inputArray[] = new int[size];
    	final Basic1Kernel kernel = new Basic1Kernel();
    	
    	int[] outputArray = null;
    	Range range = device.createRange(size, size);
    	
    	ReportObserverNoConcurrentCalls observer = new ReportObserverNoConcurrentCalls(device);
    	observer.addAcceptedThreadId(Thread.currentThread().getId());
    	kernel.registerProfileReportObserver(observer);
    	
    	long startOfExecution = System.currentTimeMillis();
    	try {
    		for (int i = 0; i < runs; i++) {
    			outputArray = Arrays.copyOf(inputArray, inputArray.length);
    			kernel.setInputOuputArray(outputArray);
    			kernel.execute(range);
    		}
    		
    		long runTime = System.currentTimeMillis() - startOfExecution;
    		assertEquals("Number of profiling reports doesn't match the expected", runs, observer.getReceivedReportsCount());
    		assertEquals("Aparapi Accumulated execution time doesn't match", kernel.getAccumulatedExecutionTime(device), observer.getAccumulatedElapsedTime(), 1e-10);
    		assertEquals("Test estimated accumulated time doesn't match within 100ms window", runTime, kernel.getAccumulatedExecutionTime(device), 100);
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
    	private long startOfExecution;
    	private long runTime;
    	private long threadId;
    	private int kernelCalls;
    	private int[] outputArray;
    }
    
    @SuppressWarnings("unchecked")
	public boolean multiThreadedSingleKernelReportObserverTestHelper(Device device, int size) throws InterruptedException, ExecutionException {
    	final int runs = 100;
    	final int javaThreads = 10;
    	final int inputArray[] = new int[size];
    	final AtomicInteger atomicResultId = new AtomicInteger(0);
    	final CyclicBarrier barrier = new CyclicBarrier(javaThreads);
    	ExecutorService executorService = Executors.newFixedThreadPool(javaThreads);
    	
    	final ReportObserverNoConcurrentCalls observer = new ReportObserverNoConcurrentCalls(device);
    	
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
						results[id].startOfExecution = System.currentTimeMillis();
						results[id].kernelCalls = 0;
						for (int i = 0; i < runs; i++) {
							results[id].outputArray = Arrays.copyOf(inputArray, inputArray.length);
							k.setInputOuputArray(results[id].outputArray);
		    				k.execute(Range.create(device, size, size));
		    				results[id].kernelCalls++;
		    				if (i == 0) {
		    					//Ensure that each thread sends at least one report
		    					boolean retry = true;
		    					while (retry) {
			    					try {
										barrier.await(10, TimeUnit.SECONDS);
										retry = false;
									} catch (InterruptedException e) {
										retry = true;
									} catch (BrokenBarrierException e) {
										throw new RuntimeException("Failed on barrier", e);
									} catch (TimeoutException e) {
										throw new RuntimeException("Failed on barrier", e);
									}
		    					}
		    				}
						}
						results[id].runTime = System.currentTimeMillis() - results[id].startOfExecution;					
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

    	assertTrue("Threads did not terminate correctly", terminatedOk);
    
    	int totalNumberOfCalls = 0;
    	double minExecutionTime = Double.MAX_VALUE;
    	double maxExecutionTime = 0;
    	for (int i = 0; i < javaThreads; i++) {
    		totalNumberOfCalls += results[i].kernelCalls;
    		maxExecutionTime = Math.max(maxExecutionTime, results[i].runTime);
    		minExecutionTime = Math.min(minExecutionTime, results[i].runTime);
    	}

    	//Sometimes on slower machines, it may happen that the observer doesn't receive as many reports as the number of kernel runs.
    	assertTrue("Number of total reports must be more than the iteration count", observer.getReceivedReportsCount() >= runs);
    	assertTrue("Number of reports is less than the total number of calls", observer.getReceivedReportsCount() <= totalNumberOfCalls);
    	assertTrue("Number of Java threads sending profile reports should be at least 1", observer.observedThreadsIds.size() >= 1);
    	if (device instanceof OpenCLDevice) {
    		//It is expected that the observer accumulated elapsed time is less than the estimated execution time, which includes additional test execution overhead.
    		//On JTP this difference can become bigger, so Java devices are excluded from this check.
	    	assertEquals("Report execution time doesn't match within 300ms of real min. kernel execution time", minExecutionTime, observer.getAccumulatedElapsedTime(), 300);
	    	assertEquals("Report execution time doesn't match within 300ms of real max. kernel execution time", maxExecutionTime, observer.getAccumulatedElapsedTime(), 300);
    	}
    	for (int i = 0; i < javaThreads; i++) {
    		assertEquals("Thread index " + i + " didn't make the expected number of kernel runs", runs, results[i].kernelCalls);
    		assertTrue("Thread index " + i + " kernel computation doesn't match the expected", validateBasic1Kernel(inputArray, results[i].outputArray));
    	}
    	
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
