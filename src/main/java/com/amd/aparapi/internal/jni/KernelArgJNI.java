package com.amd.aparapi.internal.jni;

import com.amd.aparapi.internal.annotation.*;

import java.lang.reflect.*;

/**
 * This class is intended to be used as a 'proxy' or 'facade' object for Java code to interact with JNI
 */
public abstract class KernelArgJNI{

   /**
    * The type of this KernelArg. Created by or-ing appropriate flags
    * 
    * @see KernelRunnerJNI#ARG_BOOLEAN
    * @see KernelRunnerJNI#ARG_BYTE
    * @see KernelRunnerJNI#ARG_CHAR
    * @see KernelRunnerJNI#ARG_FLOAT
    * @see KernelRunnerJNI#ARG_INT
    * @see KernelRunnerJNI#ARG_DOUBLE
    * @see KernelRunnerJNI#ARG_LONG
    * @see KernelRunnerJNI#ARG_SHORT
    * @see KernelRunnerJNI#ARG_ARRAY
    * @see KernelRunnerJNI#ARG_PRIMITIVE
    * @see KernelRunnerJNI#ARG_READ
    * @see KernelRunnerJNI#ARG_WRITE
    * @see KernelRunnerJNI#ARG_LOCAL
    * @see KernelRunnerJNI#ARG_GLOBAL
    * @see KernelRunnerJNI#ARG_CONSTANT
    * @see KernelRunnerJNI#ARG_ARRAYLENGTH
    * @see KernelRunnerJNI#ARG_EXPLICIT
    * @see KernelRunnerJNI#ARG_EXPLICIT_WRITE
    * @see KernelRunnerJNI#ARG_OBJ_ARRAY_STRUCT
    */
   @UsedByJNICode protected int type;

   /**
    * Name of the field
    */
   @UsedByJNICode protected String name;

   /**
    * If this field represents a Java array then the instance will be captured here
    */
   @UsedByJNICode protected Object javaArray;

   /**
    * If this field represents an aparapi buffer then the instance will be captured here
    */
   @UsedByJNICode protected Object javaBuffer;

   /**
    * If this is an array or a buffer then the size (in bytes) is held here
    */
   @UsedByJNICode protected int sizeInBytes;

   /**
    * If this is an array buffer then the number of elements is stored here
    */
   @UsedByJNICode protected int numElements;

   
   /**
    * If this is an multidimensional array then the number of dimensions is stored here
    */
   @UsedByJNICode protected int numDims;


   /**
    * If this is an multidimensional array then the dimensions are stored here
    */
   @UsedByJNICode protected int[] dims;

   /**
    * If this is an array buffer then the number of elements is stored here.
    * 
    * At present only set for AparapiLocalBuffer objs, JNI multiplies this by localSize
    */
   //  @Annotations.Unused @UsedByJNICode protected int bytesPerLocalWidth;

   /**
    * Only set for array objs, not used on JNI
    */
   @UsedByJNICode protected Object array;

   @UsedByJNICode protected Object buffer;

   /**
    * Field in Kernel class corresponding to this arg
    */
   @UsedByJNICode protected Field field;
}
