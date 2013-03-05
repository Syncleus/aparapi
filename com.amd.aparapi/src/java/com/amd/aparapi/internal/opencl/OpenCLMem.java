package com.amd.aparapi.internal.opencl;

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
