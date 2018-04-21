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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
			//Empty method intended
		}		
	}
		
	/**
	 * This test validates that all threads can start a profiling process after another thread
	 * that has already started profiling.
	 * @throws InterruptedException 
	 * @throws Exception 
	 */
	@Test
	public void testAllThreadCanStartPass() throws IllegalStateException, InterruptedException {
		final int javaThreads = ProfilingEvent.values().length;
		final KernelProfile kernelProfile = new KernelProfile(SimpleKernel.class);
		final KernelDeviceProfile kernelDeviceProfile = new KernelDeviceProfile(kernelProfile, SimpleKernel.class, JavaDevice.THREAD_POOL);
		final AtomicInteger receivedReports = new AtomicInteger(0);
		final ConcurrentSkipListSet<Long> onEventAccepted = new ConcurrentSkipListSet<Long>();
		final AtomicInteger index = new AtomicInteger(0);
		final long[] threadIds = new long[javaThreads + 1];
		
		kernelProfile.setReportObserver(new IProfileReportObserver() {
			@Override
			public void receiveReport(Class<? extends Kernel> kernelClass, Device device, WeakReference<ProfileReport> profileInfo) {
				receivedReports.incrementAndGet();
				onEventAccepted.add(profileInfo.get().getThreadId());
			}
		});
		
		//Ensure that the first thread as started profiling, before testing the others
		kernelDeviceProfile.onEvent(ProfilingEvent.START);
		
		List<ProfilingEvent> events = Arrays.asList(ProfilingEvent.values());
		
		ExecutorService executorService = Executors.newFixedThreadPool(javaThreads);
		try {
			events.forEach(evt -> {
				final int idx = index.getAndIncrement();
				executorService.submit(() -> {
					threadIds[idx] = Thread.currentThread().getId();
					kernelDeviceProfile.onEvent(ProfilingEvent.START);
					kernelDeviceProfile.onEvent(ProfilingEvent.EXECUTED);
				});
			});
		} finally {
			executorService.shutdown();
			if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
				executorService.shutdownNow();
				throw new IllegalStateException("ExecutorService terminated abnormaly");
			}
		}
		
		threadIds[index.get()] = Thread.currentThread().getId();
		for (int i = 0; i < javaThreads; i++) {
			assertTrue("Report wasn't received for thread with index " + i, onEventAccepted.contains(threadIds[i]));
		}
		assertFalse("Report was received for main thread", onEventAccepted.contains(threadIds[javaThreads]));
		assertEquals("Reports from all threads should have been received", javaThreads, receivedReports.get());
		
		//Only after this event should the main thread have received a report
		kernelDeviceProfile.onEvent(ProfilingEvent.EXECUTED);
		
		assertTrue("Report wasn't received for main thread", onEventAccepted.contains(threadIds[javaThreads]));
		assertEquals("Reports from all threads should have been received", javaThreads + 1, receivedReports.get());
	}
	
	@Test
	public void testGetProfilingEventsNames() {
		String[] stages = ProfilingEvent.getStagesNames();
		int i = 0;
		for (String stage : stages) {
			assertNotNull("Stage is null at index " + i, stage);
			assertFalse("Stage name is empty at index " + i, stage.isEmpty());
			ProfilingEvent event = ProfilingEvent.valueOf(stage);
			assertTrue("Stage name does not translate to an event", event != null);
			assertEquals("Stage name does match correct order", i, event.ordinal());
			i++;
		}
	}
	
	@Test
	public void testProfileReportClone() {
		final int reportId = 101;
		final int threadId = 192;
		final long[] values = new long[ProfilingEvent.values().length];
		for (int i = 0; i < values.length; i++) {
			values[i] = 900 + i;
		}
		
		ProfileReport report = new ProfileReport(threadId, SimpleKernel.class, JavaDevice.THREAD_POOL);
		report.setProfileReport(reportId, values);
		
		ProfileReport clonedReport = report.clone();
		assertNotEquals("Object references shouldn't be the same", report, clonedReport);
		assertEquals("Report Id doesn't match", reportId, clonedReport.getReportId());
		assertEquals("Class doesn't match", SimpleKernel.class, clonedReport.getKernelClass());
		assertEquals("Device doesn't match", JavaDevice.THREAD_POOL, clonedReport.getDevice());
		
		for (int i = 0; i < values.length; i++) {
			assertEquals("Values don't match for index " + i, report.getElapsedTime(i), clonedReport.getElapsedTime(i), 1e-10);
		}
		
		long[] valuesB = new long[ProfilingEvent.values().length];
		for (int i = 0; i < valuesB.length; i++) {
			valuesB[i] = 100 + i*100;
		}
		report.setProfileReport(reportId + 1, valuesB);
		
		for (int i = 1; i < values.length; i++) {
			assertNotEquals("Values match after new assingment for index " + i, report.getElapsedTime(i), clonedReport.getElapsedTime(i), 1e-10);
		}
		
	}
}
