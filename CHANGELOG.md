# Aparapi Changelog

## v2.0.1
* Add support for getKernelMaxWorkGroupSize(), getKernelCompileWorkGroupSize(), getKernelPreferredWorkGroupSizeMultiple(), getKernelMinimumPrivateMemSizeInUsePerWorkItem() and getKernelLocalMemSizeInUse()
* Fixed Barriers give inconsistent results on NVIDIA backend.
* New Kernel.compile(...) methods for forcing pre-compilation of a kernel without executing it
* Fixed NPE bug for Kernel.getProfileReportCurrentThread(device) and similar methods 
* Fixed bug where ClassModel would throw an error when loaded if boot strap methods were 0.
* Aparapi can now run on any OpenCL version rather than failing on untested versions it produces a warning.
* Updated the following dependency versions:
** com.aparapi: aparapi-jni 1.4.2 -> 1.4.3
** org.apache.bcel:bcel 6.4.1 -< 6.5.0
** org.scala-lang:scala-library 2.13.1 -> 2.13.4
** com.syncleus:syncleus 7 -> 8 


## v2.0.0

* Fixed Potential JVM crash when using multi-dimensional arrays (> 1D)
* Fixed bug causing wrong Aparapi-native library to be loaded.
* Minimum Java JDK compatible is now 1.8 or higher, used to be 1.7
* Updated the following dependency versions:
** com.aparapi: aparapi-jni 1.4.1 -> 1.4.2
** org.scala-lang: scala-library 2.12.6 -> 2.13.1
** net.alchim31.maven: scala-maven-plugin 3.4.1 -> 4.3.0
** org.apache.bcel: bcel 6.2 -> 6.4.1

## 1.10.0

* If statements with empty blocks and comparisons outside of if or while statements now compile and run on the GPU.
* Fix kernel management inconsistencies regarding preferred devices management
* Fix Java execution mode with barriers to not deadlock when a thread dies or is interrupted (InterruptedException)
* Fix Java execution mode to fail-fast when Kernel execution fails
* Java execution mode now provides detailed backtraces of failed Kernel threads including passId, groupIds, globalIds and localIds
* Internal translation of bytecode is now facilitated by the BCEL library
* Scala support has been added (see unit tests).
* Fix arrays of AtomicInteger stored on local variables no longer fail with type cast exception while generating OpenCL (support for I_ALOAD_0,1,2,3 bytecode instructions)

## 1.9.0

* Fixed local arrays handling 1D and ND, to cope with arrays resizing across kernel executions
* Significant speed-up on discrete GPUs with dedicated memory - OpenCLDevice.setSharedMemory(false)
* Now supports efficient execution on discrete GPU and other devices with dedicated memory
* Support for OpenCLDevice configurator/configure API

## 1.8.0

* Updated KernelManager to facilitate class extensions having constructors with non static parameters
* Enable kernel profiling and execution simultaneously on multiple devices (multiple threads calling same kernel class on multiple devices)
* Fixed JVM crash when multi-dimensional arrays were used in Local memory (2D and 3D local arrays are now supported)
* Fixed bug where signed integer constants were being interpreted as unsigned values during Codegen.

## 1.7.0

* Fully support barrier() - localBarrier(),  globalBarrier() and localGlobalBarrier() on OpenCL 1.2 and later.
* Improved exception handling, stack traces no longer double print and Error and other throwables are never caught.
* Fix issue causing SEVERE log messages on Aparapi kernel profiling under multithreading.
* Provide new interfaces for thread safe kernel profiling (mutiple threads calling same kernel class on same device).
* Fixed occasional deadlock in JTP execution mode.
* Significant speedup when running in JTP execution mode.

## 1.6.0

* Added support for Local arguments in kernel functions
* Added full support for atomic operations of arrays of integers on OpenCL 1.2 and later.
* Parent pom no longer points to a snapshot.

## 1.5.0

* Support for OpenCL 2.1 added.
* Support inline array creation in the kernel, which is implemented in the GPU in private memory.
* Updated parent pom to v6.
* createProgram had the wrong signature producing a unsatisfied link exception that is now fixed.
* Build now requires version 3.5.0 of maven due to changes in surefire plugin.
* Added the functions popcount and clz

## 1.4.1

* Fixed NullPointerException when using KernelManger from the KernelManagers class
* Now requires maven 3.0.4 or later.
* Bumped parent pom version to 4.
* Removed explicit versions from several plugins in the pom as these are defined in the parent.

## 1.4.0

* Updated nexus staging plugin: 1.6.7 -> 1.6.8
* Added Fused Multiply Add support.
* Fixed a bug whereby the library failed to load if OpenCL implementation isnt present, system now loads and falls back to native Java.
* Fixed several mistakes and typos in the various examples.

## 1.3.4

* Updated to aparapi-jni 1.1.2 thus fixing `UnsatisfiedLinkError` which occured only on Windows.

## 1.3.3

* Added several missing math functions that are part of the OpenCL standard: acospi, asinpi, atanpi, atan2pi, cbrt,
  cosh, cospi, exp2, exp10, expm1, log2, log10, log1p, mad, nextafter, sinh, sinpi, tanh, tanpi.
* Fixed "`CXXABI_1.3.8' not found " error encountered on some older systems.
* Fixed a bug where calling createProgram resulted in an exception.
* Changed aparapi JNI load notification into a proper logger message instead of direct to system.out.

## 1.3.2

* Added Windows 64bit support.
* Improved exception message when a native library can't be loaded.

## 1.3.1

* Added full support for OSX.

## 1.3.0

* No longer needs Aparapi JNI library to be installed manually, this is loaded dynmically now.

## 1.2.0

* Kernels of the same class are now eligible to be run on different devices.
* Added method to set execution mode without any fallback.
* Added opencl device method for name.
* Fixed a memory leak.
* Added method for uncached platform retrieval.

## 1.1.2

* Fixed a bug where OpenCL kernels were getting compiled twice.

## 1.1.1

* Fixed a bug where forward references existed when they shouldnt.
* Fixed some bugs where improper directory structure were referenced produced exceptions.

## 1.1.0

* Changed group id and package to com.aparapi
