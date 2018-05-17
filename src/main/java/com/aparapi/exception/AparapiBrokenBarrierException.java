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
package com.aparapi.exception;

/**
 * Exception is thrown when an Aparapi kernel is executing in Java mode, 
 * and a barrier cannot be completed due to threads that die during its execution.
 * 
 * Aparapi ensures execution won't deadlock due to such an event, resulting in 
 * fail-fast code, and allowing easy perception of the failure cause.
 * 
 * One such case is when an Aparapi kernel accesses outside a valid array position.
 *    
 * 
 * @author CoreRasurae
 */
public class AparapiBrokenBarrierException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1472616497910700885L;

	public AparapiBrokenBarrierException(String message) {
		super(message);
	}

	public AparapiBrokenBarrierException(Throwable cause) {
		super(cause);
	}

	public AparapiBrokenBarrierException(String message, Throwable cause) {
		super(message, cause);
	}

	public AparapiBrokenBarrierException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
