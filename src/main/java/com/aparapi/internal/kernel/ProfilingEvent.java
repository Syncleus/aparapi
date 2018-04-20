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
package com.aparapi.internal.kernel;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Barney on 02/09/2015.
 */
public enum ProfilingEvent {
   START, CLASS_MODEL_BUILT, INIT_JNI, OPENCL_GENERATED, OPENCL_COMPILED, PREPARE_EXECUTE, EXECUTED;

	
   static final AtomicReference<String[]> stagesNames = new AtomicReference<String[]>(null);
   public static String[] getStagesNames() {
	  String[] result = null;
	  result = stagesNames.get();
	  if (result == null) {
		  final String[] names = new String[values().length];
		  for (int i = 0; i < values().length; i++) {
			  names[i] = values()[i].name();
		  }
		  if (stagesNames.compareAndSet(null, names)) {
			  result = names;
		  } else {
			  result = stagesNames.get();
		  }
	  }
	  return result;
   }
}
