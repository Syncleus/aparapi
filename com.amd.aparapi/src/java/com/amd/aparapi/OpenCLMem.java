package com.amd.aparapi;

public class OpenCLMem{

   public final static int MEM_DIRTY_BIT = 1 << 0x00F;

   public final static int MEM_COPY_BIT = 1 << 0x010;

   public final static int MEM_ENQUEUED_BIT = 1 << 0x011;

   long bits; // dirty, copy, enqueued

   int sizeInBytes;

   long memId;

   long address;

   Object instance;

   OpenCLProgram program;
}
