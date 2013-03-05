package com.amd.aparapi.internal.jni;

import com.amd.aparapi.internal.annotation.UsedByJNICode;

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
