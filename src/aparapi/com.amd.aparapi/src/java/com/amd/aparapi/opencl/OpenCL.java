package com.amd.aparapi.opencl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface OpenCL<T> {

   public static final String CL_KHR_FP64 = "cl_khr_fp64";

   public static final String CL_KHR_SELECT_FPROUNDING_MODE = "cl_khr_select_fprounding_mode";

   public static final String CL_KHR_GLOBAL_INT32_BASE_ATOMICS = "cl_khr_global_int32_base_atomics";

   public static final String CL_KHR_GLOBAL_INT32_EXTENDED_ATOMICS = "cl_khr_global_int32_extended_atomics";

   public static final String CL_KHR_LOCAL_INT32_BASE_ATOMICS = "cl_khr_local_int32_base_atomics";

   public static final String CL_KHR_LOCAL_INT32_EXTENDED_ATOMICS = "cl_khr_local_int32_extended_atomics";

   public static final String CL_KHR_INT64_BASE_ATOMICS = "cl_khr_int64_base_atomics";

   public static final String CL_KHR_INT64_EXTENDED_ATOMICS = "cl_khr_int64_extended_atomics";

   public static final String CL_KHR_3D_IMAGE_WRITES = "cl_khr_3d_image_writes";

   public static final String CL_KHR_BYTE_ADDRESSABLE_SUPPORT = "cl_khr_byte_addressable_store";

   public static final String CL_KHR_FP16 = "cl_khr_fp16";

   public static final String CL_KHR_GL_SHARING = "cl_khr_gl_sharing";

   @Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME) public @interface Put {
   }

   @Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME) public @interface Get {
   }

   @Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME) public @interface Source {
      String value();
   }

   @Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME) public @interface Resource {
      String value();
   }

   @Target(ElementType.METHOD) @Retention(RetentionPolicy.RUNTIME) public @interface Kernel {
      String value();
   }

   @Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME) public @interface Arg {
      String value();
   }

   @Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME) public @interface GlobalReadWrite {
      String value();
   }

   @Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME) public @interface GlobalReadOnly {
      String value();
   }

   @Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME) public @interface GlobalWriteOnly {
      String value();
   }

   @Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME) public @interface Local {
      String value();
   }

   @Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME) public @interface Constant {
      String value();
   }

   public T put(float[] array);

   public T put(int[] array);

   public T put(short[] array);

   public T put(byte[] array);

   public T put(char[] array);

   public T put(boolean[] array);

   public T put(double[] array);

   public T get(float[] array);

   public T get(int[] array);

   public T get(short[] array);

   public T get(char[] array);

   public T get(boolean[] array);

   public T get(double[] array);

   public T get(byte[] array);

   public T begin();

   public T end();
}
