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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.aparapi.IProfileReportObserver;
import com.aparapi.Kernel;
import com.aparapi.ProfileReport;
import com.aparapi.device.Device;
import com.aparapi.device.JavaDevice;
import com.aparapi.internal.kernel.KernelDeviceProfile;
import com.aparapi.internal.kernel.KernelProfile;
import com.aparapi.internal.kernel.ProfilingEvent;

/**
 * This class provides unit tests to help in validation of thread-safe ProfileReports. 
 * 
 * @author CoreRasurae
 */
public class ProfileReportUnitTest {

	private class SimpleKernel extends Kernel {

		@Override
		public void run() {
			//This method is intended to be empty
		}		
	}
		
	/**
	 * This test validates that no thread can start a profiling process at any stage after another thread
	 * that has already started profiling.
	 * @throws Exception 
	 */
	@Test
	public void testNoThreadCanStartPass() throws Exception {
		final int javaThreads = ProfilingEvent.values().length;
		final int runs = 100;
		final KernelProfile kernelProfile = new KernelProfile(SimpleKernel.class);
		final KernelDeviceProfile kernelDeviceProfile = new KernelDeviceProfile(kernelProfile, SimpleKernel.class, JavaDevice.THREAD_POOL);
		final AtomicBoolean receivedReport = new AtomicBoolean(false);
		final AtomicBoolean[] onEventAccepted = new AtomicBoolean[javaThreads];
		final AtomicInteger idx = new AtomicInteger(0);
		for (int i = 0; i < javaThreads; i++) {
			onEventAccepted[i] = new AtomicBoolean(false);
		}
		
		kernelProfile.setReportObserver(new IProfileReportObserver() {
			@Override
			public void receiveReport(Class<? extends Kernel> kernelClass, Device device, ProfileReport profileInfo) {
				receivedReport.set(true);
			}
		});
		
		
		//This is the only thread that should start an event in this test
		assertTrue(kernelDeviceProfile.onEvent(ProfilingEvent.START));
		
		
		List<ProfilingEvent> events = Arrays.asList(ProfilingEvent.values());
		
		ExecutorService executorService = Executors.newFixedThreadPool(javaThreads);
		try {
			events.forEach(evt -> executorService.submit(() -> {
				for (int i = 0; i < runs; i++) {
					if (kernelDeviceProfile.onEvent(evt)) {
						onEventAccepted[idx.getAndIncrement()].set(true);
					}
				}
			}));
		} finally {
			executorService.shutdown();
			if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
				executorService.shutdownNow();
				throw new Exception("ExecutorService terminated abnormaly");
			}
		}
		
		for (int i = 0; i < javaThreads; i++) {
			assertFalse("Event was accepted for thread with index " + i, onEventAccepted[i].get());
		}
		assertFalse("No report should have been received", receivedReport.get());
	}
	

	/**
	 * This test validates that only a single thread can start, after being acknowledged, and that report is received.
	 * @throws Exception
	 */
	@Test
	public void testNoThreadStartsBeforeAckPass() throws Exception {
		final int javaThreads = ProfilingEvent.values().length + 1;
		final int runs = 100;
		final KernelProfile kernelProfile = new KernelProfile(SimpleKernel.class);
		final KernelDeviceProfile kernelDeviceProfile = new KernelDeviceProfile(kernelProfile, SimpleKernel.class, JavaDevice.THREAD_POOL);
		final AtomicBoolean receivedReport = new AtomicBoolean(false);
		final AtomicBoolean[] onEventAccepted = new AtomicBoolean[javaThreads];
		final AtomicInteger index = new AtomicInteger(0);
		for (int i = 0; i < javaThreads; i++) {
			onEventAccepted[i] = new AtomicBoolean(false);
		}
		
		kernelProfile.setReportObserver(new IProfileReportObserver() {
			@Override
			public void receiveReport(Class<? extends Kernel> kernelClass, Device device, ProfileReport profileInfo) {
				receivedReport.set(true);
				for (int i = 0; i < javaThreads; i++) {
					assertFalse("Event was accepted earlier for thread with index " + i, onEventAccepted[i].get());
				}				
			}
		});
		
		
		//This is the only thread that should start an event in this test
		assertTrue(kernelDeviceProfile.onEvent(ProfilingEvent.START));
		assertTrue(kernelDeviceProfile.onEvent(ProfilingEvent.EXECUTED));

		List<ProfilingEvent> events = new ArrayList<>(ProfilingEvent.values().length + 1);
		events.addAll(Arrays.asList(ProfilingEvent.values()));
		events.add(ProfilingEvent.START);
		
		ExecutorService executorService = Executors.newFixedThreadPool(javaThreads);
		try {
			events.forEach(evt -> { 
				final int idx = index.getAndIncrement(); 
				executorService.submit(() -> {
					for (int i = 0; i < runs; i++) {
						if (kernelDeviceProfile.onEvent(evt)) {
							if (!onEventAccepted[idx].get()) {
								onEventAccepted[idx].set(true);
							}
						}
					}
				});
			});
		} finally {
			executorService.shutdown();
			if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
				executorService.shutdownNow();
				throw new Exception("ExecutorService terminated abnormaly");
			}
		}
		
		boolean accepted = false;
		for (int i = 0; i < javaThreads; i++) {
			if (i == 0) {
				accepted = onEventAccepted[i].get();
			} else if (i == ProfilingEvent.values().length-1) {
				if (accepted == false) {
					assertTrue("Event should have been accepted", onEventAccepted[i].get());
				} else {
					assertFalse("Event shouldn't have been accepted", onEventAccepted[i].get());
				}
			} else {
				assertFalse("Event was accepted on wrong event stage for thread with index " + i, onEventAccepted[i].get());
			}
		}
		assertTrue("Event should have been accepted by another thread", accepted);
		assertTrue("Report should have been received", receivedReport.get());
	}

	/**
	 * This test validates that another thread cannot start a profile, before the current being acknowledged, when
	 * no observer is registered.
	 * @throws Exception
	 */
	@Test
	public void testAcknowledgeAllowsOtherThreadToRunPass() throws Exception {
		final int javaThreads = ProfilingEvent.values().length + 1;
		final int runs = 100;
		final KernelProfile kernelProfile = new KernelProfile(SimpleKernel.class);
		final KernelDeviceProfile kernelDeviceProfile = new KernelDeviceProfile(kernelProfile, SimpleKernel.class, JavaDevice.THREAD_POOL);
		final AtomicBoolean[] onEventAccepted = new AtomicBoolean[javaThreads];
		final AtomicInteger index = new AtomicInteger(0);
		for (int i = 0; i < javaThreads; i++) {
			onEventAccepted[i] = new AtomicBoolean(false);
		}
				
		//This is the only thread that should start an event in this test
		assertTrue(kernelDeviceProfile.onEvent(ProfilingEvent.START));
		assertTrue(kernelDeviceProfile.onEvent(ProfilingEvent.EXECUTED));

		List<ProfilingEvent> events = new ArrayList<>(ProfilingEvent.values().length + 1);
		events.addAll(Arrays.asList(ProfilingEvent.values()));
		events.add(ProfilingEvent.START);
		
		final ExecutorService executorService = Executors.newFixedThreadPool(javaThreads);
		try {
			events.forEach(evt -> { 
				final int idx = index.getAndIncrement(); 
				executorService.submit(() -> {
					for (int i = 0; i < runs; i++) {
						if (kernelDeviceProfile.onEvent(evt)) {
							onEventAccepted[idx].set(true);
						}
					}
				});
			});
		} finally {
			executorService.shutdown();
			if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
				executorService.shutdownNow();
				throw new Exception("ExecutorService terminated abnormaly");
			}
		}

		for (int i = 0; i < javaThreads; i++) {
			assertFalse("Event was accepted for thread with index " + i, onEventAccepted[i].get());
		}

		ProfileReport report = kernelDeviceProfile.getLastReport();
		assertNotNull("Profile report shouldn't be null", report);
		assertEquals("Thread Id doesn't match the expected", Thread.currentThread().getId(), report.getThreadId());
		assertEquals("Device doesn't match", report.getDevice(), JavaDevice.THREAD_POOL);
		assertEquals("Class doesn't match", report.getKernelClass(), SimpleKernel.class);
		
		kernelDeviceProfile.acknowledgeLastReport();
		
		index.set(0);
		final ExecutorService executorServiceB = Executors.newFixedThreadPool(javaThreads);
		try {
			events.forEach(evt -> { 
				final int idx = index.getAndIncrement(); 
				executorServiceB.submit(() -> {
					for (int i = 0; i < runs; i++) {
						if (kernelDeviceProfile.onEvent(evt)) {
							if (!onEventAccepted[idx].get()) {
								onEventAccepted[idx].set(true);
							}
						}
					}
				});
			});
		} finally {
			executorServiceB.shutdown();
			if (!executorServiceB.awaitTermination(1, TimeUnit.MINUTES)) {
				executorServiceB.shutdownNow();
				throw new Exception("ExecutorService terminated abnormaly");
			}
		}
		
		boolean accepted = false;
		for (int i = 0; i < javaThreads; i++) {
			if (i == 0) {
				accepted = onEventAccepted[i].get();
			} else if (i == ProfilingEvent.values().length-1) {
				if (accepted == false) {
					assertTrue("Event should have been accepted", onEventAccepted[i].get());
				} else {
					assertFalse("Event shouldn't have been accepted", onEventAccepted[i].get());
				}
			} else {
				assertFalse("Event was accepted on wrong event stage for thread with index " + i, onEventAccepted[i].get());
			}
		}
		assertTrue("Event should have been accepted by another thread", accepted);
	}
}
