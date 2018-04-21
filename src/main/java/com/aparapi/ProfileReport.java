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
package com.aparapi;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.aparapi.device.Device;
import com.aparapi.internal.kernel.ProfilingEvent;

public final class ProfileReport {
	private static final double MILLION = 1000000d;
	private final long id;
	private final Class<? extends Kernel> kernelClass;
	private final long threadId;
	private final Device device;
	private final long currentTimes[];
	private final String[] stagesNames;

	/**
	 * Creates a new ProfileReport bean with profiling information for a given kernel that ran on the given device,
	 * for the given thread.
	 * @param reportId the unique identifier for this report (the identifier is unique within the <kernel,device> tuple) 
	 * @param clazz the kernel class that this report pertains to
	 * @param _device the device where the kernel ran
	 * @param _threadId the id of the thread that called the kernel.execute(...)
	 * @param _currentTimes the profiling data
	 */
	public ProfileReport(final long reportId, final Class<? extends Kernel> clazz, final Device _device, final long _threadId,
			final long[] _currentTimes) {
		id = reportId;
		kernelClass = clazz;
		threadId = _threadId;
		device = _device;
		currentTimes = Arrays.copyOf(_currentTimes, _currentTimes.length);
		stagesNames = ProfilingEvent.getStagesNames();
	}
	
	/**
	 * Retrieves the current report unique identifier.<br/>
	 * <b>Note: </b>The identifier is monotonically incremented at each new report for the current
	 * <kernel, device> tuple.
	 * @return the report id
	 */
	public long getReportId() {
		return id;
	}
	
	/**
	 * Retrieves the thread id of the thread that executed the kernel, producing this profile report. 
	 * @return the thread id
	 */
	public long getThreadId() {
		return threadId;
	}

	/**
	 * Retrieves the class of the kernel to which this profile report pertains to
	 * @return the Aparapi kernel class
	 */
	public Class<? extends Kernel> getKernelClass() {
		return kernelClass;
	}
	
	/**
	 * Retrieves the Aparapi device where the kernel was executed, producing this profile report.
	 * @return the Aparapi device
	 */
	public Device getDevice() {
		return device;
	}
	
	/**
	 * Get the names of the stages for which data was collected.
	 * @return the list with the stages names
	 */
	public List<String> getStageNames() {
		return Collections.unmodifiableList(Arrays.asList(stagesNames));
	}
	
	/**
	 * The number of stages available with report data.
	 * @return the number of stages
	 */
	public int getNumberOfStages() {
		return stagesNames.length;
	}
	
	/**
	 * Get the name of a given stage 
	 * @param stage the index of the stage
	 * @return the stage name
	 */
	public String getStageName(int stage) {
		return stagesNames[stage];
	}
	
    /** Elapsed time for a single event only, i.e. since the previous stage rather than from the start. */
    public double getElapsedTime(int stage) {
       if (stage == ProfilingEvent.START.ordinal()) {
          return 0;
       }
       return (currentTimes[stage] - currentTimes[stage - 1]) / MILLION;
    }

    /** Elapsed time for all events {@code from} through {@code to}.*/
    public double getElapsedTime(int from, int to) {
       return (currentTimes[to] - currentTimes[from]) / MILLION;
    }
    
    /**
     * Determine the execution time of the Kernel.execute(range) call from this report.
     * 
     * @return The time spent executing the kernel (ms)
     */
    public double getExecutionTime() {
       return getElapsedTime(ProfilingEvent.START.ordinal(), ProfilingEvent.EXECUTED.ordinal());
    }

    /**
     * Determine the time taken to convert bytecode to OpenCL for first Kernel.execute(range) call.
     * 
     * @return The time spent preparing the kernel for execution using GPU (ms)
     */
    public double getConversionTime() {
       return getElapsedTime(ProfilingEvent.START.ordinal(), ProfilingEvent.PREPARE_EXECUTE.ordinal());
    }
}
