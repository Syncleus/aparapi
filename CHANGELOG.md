# Aparapi Changelog

## 1.7.0
* Fully support OpenCL 1.2 barrier() - localBarrier(),  globalBarrier() and localGlobalBarrier()
* Improved exception handling, stack traces no longer double print and Error and other throwables are never caught.
* Fix issue #62 - SEVERE log messages on Aparapi kernel profiling under multithreading 
* Provide new interfaces for thread safe kernel profiling (mutiple threads calling same kernel class on same device)
* Fix issue #101 - Possible deadlock in JTP execution mode

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
