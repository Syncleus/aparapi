package com.amd.aparapi.internal.jni;

import java.lang.reflect.Field;

import com.amd.aparapi.internal.annotation.UsedByJNICode;

/**
 * This class is intended to be used as a 'proxy' or 'facade' object for Java code to interact with JNI
 */
public abstract class KernelArgJNI{

   /**
    * The type of this KernelArg. Created by or-ing appropriate flags
    * 
    * @see ARG_BOOLEAN
    * @see ARG_BYTE
    * @see ARG_CHAR
    * @see ARG_FLOAT
    * @see ARG_INT
    * @see ARG_DOUBLE
    * @see ARG_LONG
    * @see ARG_SHORT
    * @see ARG_ARRAY
    * @see ARG_PRIMITIVE
    * @see ARG_READ
    * @see ARG_WRITE
    * @see ARG_LOCAL
    * @see ARG_GLOBAL
    * @see ARG_CONSTANT
    * @see ARG_ARRAYLENGTH
    * @see ARG_APARAPI_BUF
    * @see ARG_EXPLICIT
    * @see ARG_EXPLICIT_WRITE
    * @see ARG_OBJ_ARRAY_STRUCT
    * @see ARG_APARAPI_BUF_HAS_ARRAY
    * @see ARG_APARAPI_BUF_IS_DIRECT
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
    * If this is an array or a buffer then the size (in bytes) is held here
    */
   @UsedByJNICode protected int sizeInBytes;

   /**
    * If this is an array buffer then the number of elements is stored here
    */
   @UsedByJNICode protected int numElements;

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

   /**
    * Field in Kernel class corresponding to this arg
    */
   @UsedByJNICode protected Field field;
}
