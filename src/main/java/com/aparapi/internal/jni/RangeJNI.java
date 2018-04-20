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
package com.aparapi.internal.jni;

import com.aparapi.internal.annotation.UsedByJNICode;

/**
 * This class is intended to be used as a 'proxy' or 'facade' object for Java code to interact with JNI
 */
public abstract class RangeJNI{

   @UsedByJNICode protected int globalSize_0 = 1;

   @UsedByJNICode protected int localSize_0 = 1;

   @UsedByJNICode protected int globalSize_1 = 1;

   @UsedByJNICode protected int localSize_1 = 1;

   @UsedByJNICode protected int globalSize_2 = 1;

   @UsedByJNICode protected int localSize_2 = 1;

   @UsedByJNICode protected int dims;

   @UsedByJNICode protected boolean valid = true;

   @UsedByJNICode protected boolean localIsDerived = false;
}
