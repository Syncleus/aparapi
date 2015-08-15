PrivateMemorySpace
==================

## Introduction
The private memory space identifier (just "private" is also recognised) can be applied to struct fields in order to indicate that the data is not shared with/accessible to other kernel instances. Whilst this is the default for non-array data, it must be explicitly applied to array fields in order to make them private. Aparapi now supports arrays in the private memory space.

The private memory space is generally only suitable for smallish arrays, but is required for certain algorithms, e.g. for those which must mutate (for example, sort or partially sort) an exclusive copy of an array/subarray.

##Details
In Aparapi there are two mechanisms available to mark a Kernel class member as belonging to the private memory space when mapped to OpenCL code (matching the equivalent functionality for marking items as belonging to the local memory space). Either the field can be named with a suffix plus buffer size, for example

>   protected short[] myBuffer_$private$32 = new short[32];
or using the Annotation Kernel.PrivateMemorySpace, for example

>   protected @PrivateMemorySpace(32) short[] myBuffer = new short[32];
The latter should be used in preference to the former.

Note that OpenCL requires that the size of a private array be fixed at compile time for any kernel. Thus it is not possible for a single Kernel subclass to support private buffers of varying size. Unfortunately this may entail creating multiple subclasses with varying buffer sizes in order to most efficiently support varying private buffer sizes.

Of course, a single Kernel class can be created which has a private buffer large enough for all use cases, though this may be suboptimal if only a small fraction of the maximum buffer size is commonly required.

Because private buffers are unshared, they require much more of a GPU's memory than a local or global buffer of the same size, and should therefore be used sparingly and kept as small as possible, as overuse of large private arrays might cause GPU execution to fail on lower-end graphics cards.

However, private memory space is the fastest of all OpenCls memory spaces, so may in some limited cases might be used to increase execution speed even when the kernel does not need to modify the array and a shared (local or global) array would suffice - for example to provide a smallish lookup-table to replace an expensive function call.

Without modification, an Aparapi kernel which uses private buffers may fail to work when invoked in Java Threadpool (JTP) mode, because the buffer will be shared across multiple threads. However a simple mechanism exists which allows such buffers to be used safely in JTP execution mode.

The Kernel.NoCL annotation exists to allow specialised code to be executed when running in Java (or JTP) which is not invoked when running on the GPU. A NoCL method can be inserted at the begining of a Kernel's run() method which sets the private array to a value obtained from a static ThreadLocal<foo[]> where foo is the primitive type of the array in question. This will have no effect upon OpenCL execution, but will allow threadsafe execution when running in java.

In the project samples, there is a package com.amd.aparapi.sample.median which gives an example of a median image filter which uses a private array of pixel data to apply a distructive median algorithm to a "window" of local pixels. This sample also demonstrates how to use the ThreadLocal trick to allow correct behaviour when running in JTP execution mode.

[http://code.google.com/p/aparapi/source/browse/trunk/samples/median/src/com/amd/aparapi/sample/median/MedianDemo.java](http://code.google.com/p/aparapi/source/browse/trunk/samples/median/src/com/amd/aparapi/sample/median/MedianDemo.java)