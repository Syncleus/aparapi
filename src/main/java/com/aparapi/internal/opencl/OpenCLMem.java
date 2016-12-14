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
package com.aparapi.internal.opencl;

public class OpenCLMem{

   public final static int MEM_DIRTY_BIT = 1 << 0x00F;

   public final static int MEM_COPY_BIT = 1 << 0x010;

   public final static int MEM_ENQUEUED_BIT = 1 << 0x011;

   public long bits; // dirty, copy, enqueued

   public int sizeInBytes;

   public long memId;

   public long address;

   public Object instance;

   public OpenCLProgram program;
}
