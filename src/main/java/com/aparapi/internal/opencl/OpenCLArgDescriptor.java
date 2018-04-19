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
/**
 * 
 */
package com.aparapi.internal.opencl;

public class OpenCLArgDescriptor{

   public final static int ARG_BYTE_BIT = 1 << 0x000;

   public final static int ARG_SHORT_BIT = 1 << 0x001;

   public final static int ARG_INT_BIT = 1 << 0x002;

   public final static int ARG_FLOAT_BIT = 1 << 0x003;

   public final static int ARG_LONG_BIT = 1 << 0x004;

   public final static int ARG_DOUBLE_BIT = 1 << 0x005;

   public final static int ARG_ARRAY_BIT = 1 << 0x006;

   public final static int ARG_PRIMITIVE_BIT = 1 << 0x007;

   public final static int ARG_GLOBAL_BIT = 1 << 0x008;

   public final static int ARG_LOCAL_BIT = 1 << 0x009;

   public final static int ARG_CONST_BIT = 1 << 0x00A;

   public final static int ARG_READONLY_BIT = 1 << 0x00B;

   public final static int ARG_WRITEONLY_BIT = 1 << 0x00C;

   public final static int ARG_READWRITE_BIT = 1 << 0x00D;

   public final static int ARG_ISARG_BIT = 1 << 0x00E;

   public OpenCLMem memVal;

   private final String name;

   public long bits;

   public OpenCLKernel kernel;



   /**
    * Full constructor
    * 
    * @param _name
    * @param _bits
    */
   public OpenCLArgDescriptor(String _name, long _bits) {
      name = _name;
      bits = _bits;
   }

   @Override public String toString() {
      final StringBuilder argBuilder = new StringBuilder();

      if ((bits & ARG_GLOBAL_BIT) == ARG_GLOBAL_BIT) {
         argBuilder.append("__global ");
      } else if ((bits & ARG_LOCAL_BIT) == ARG_LOCAL_BIT) {
         argBuilder.append("__local ");
      } else if ((bits & ARG_CONST_BIT) == ARG_CONST_BIT) {
         argBuilder.append("__constant ");
      } else if ((bits & ARG_ISARG_BIT) == ARG_ISARG_BIT) {
         // 
      } else {
         argBuilder.append("WHATISTHIS?");
      }

      if ((bits & ARG_FLOAT_BIT) == ARG_FLOAT_BIT) {
         argBuilder.append("float ");
      } else if ((bits & ARG_INT_BIT) == ARG_INT_BIT) {
         argBuilder.append("int ");
      } else if ((bits & ARG_SHORT_BIT) == ARG_SHORT_BIT) {
         argBuilder.append("short ");
      } else if ((bits & ARG_DOUBLE_BIT) == ARG_DOUBLE_BIT) {
         argBuilder.append("double ");
      } else if ((bits & ARG_LONG_BIT) == ARG_LONG_BIT) {
         argBuilder.append("long ");
      }

      if ((bits & ARG_ARRAY_BIT) == ARG_ARRAY_BIT) {
         argBuilder.append("*");
      }

      argBuilder.append(name);

      if ((bits & ARG_READONLY_BIT) == ARG_READONLY_BIT) {
         argBuilder.append(" /* readonly */");
      } else if ((bits & ARG_WRITEONLY_BIT) == ARG_WRITEONLY_BIT) {
         argBuilder.append(" /* writeonly */");
      } else if ((bits & ARG_READWRITE_BIT) == ARG_READWRITE_BIT) {
         argBuilder.append(" /* readwrite */");
      }

      return (argBuilder.toString());
   }
}
