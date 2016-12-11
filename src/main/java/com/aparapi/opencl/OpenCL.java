/**
 * Copyright (c) 2016 - 2017 Syncleus, Inc.
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
package com.aparapi.opencl;

import com.aparapi.ProfileInfo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

public interface OpenCL<T> {

   String CL_KHR_FP64 = "cl_khr_fp64";

   String CL_KHR_SELECT_FPROUNDING_MODE = "cl_khr_select_fprounding_mode";

   String CL_KHR_GLOBAL_INT32_BASE_ATOMICS = "cl_khr_global_int32_base_atomics";

   String CL_KHR_GLOBAL_INT32_EXTENDED_ATOMICS = "cl_khr_global_int32_extended_atomics";

   String CL_KHR_LOCAL_INT32_BASE_ATOMICS = "cl_khr_local_int32_base_atomics";

   String CL_KHR_LOCAL_INT32_EXTENDED_ATOMICS = "cl_khr_local_int32_extended_atomics";

   String CL_KHR_INT64_BASE_ATOMICS = "cl_khr_int64_base_atomics";

   String CL_KHR_INT64_EXTENDED_ATOMICS = "cl_khr_int64_extended_atomics";

   String CL_KHR_3D_IMAGE_WRITES = "cl_khr_3d_image_writes";

   String CL_KHR_BYTE_ADDRESSABLE_SUPPORT = "cl_khr_byte_addressable_store";

   String CL_KHR_FP16 = "cl_khr_fp16";

   String CL_KHR_GL_SHARING = "cl_khr_gl_sharing";

   @Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME)
   @interface Put {
   }

   @Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME)
   @interface Get {
   }

   @Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME)
   @interface Source {
      String value();
   }

   @Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME)
   @interface Resource {
      String value();
   }

   @Target(ElementType.METHOD) @Retention(RetentionPolicy.RUNTIME)
   @interface Kernel {
      String value();
   }

   @Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME)
   @interface Arg {
      String value();
   }

   @Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME)
   @interface GlobalReadWrite {
      String value();
   }

   @Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME)
   @interface GlobalReadOnly {
      String value();
   }

   @Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME)
   @interface GlobalWriteOnly {
      String value();
   }

   @Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME)
   @interface Local {
      String value();
   }

   @Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME)
   @interface Constant {
      String value();
   }

   T put(float[] array);

   T put(int[] array);

   T put(short[] array);

   T put(byte[] array);

   T put(char[] array);

   T put(boolean[] array);

   T put(double[] array);

   T get(float[] array);

   T get(int[] array);

   T get(short[] array);

   T get(char[] array);

   T get(boolean[] array);

   T get(double[] array);

   T get(byte[] array);

   T begin();

   T end();

   T dispose();

   List<ProfileInfo> getProfileInfo();
}
