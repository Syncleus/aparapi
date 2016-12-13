# Aparapi Changelog

## 1.3.3

* Fixed a bug where calling createProgram resulted in an exception. 

## 1.3.2

* Added Windows 64bit support.
* Improved exception message when a native library can't be loaded.

## 1.3.1

* Added full support for OSX.

## 1.3.0

* No longer needs Aparapi JNI library to be installed manually, this is loaded dynmically now.

## 2.0.0

* work on config, mem override, mem align, sync with java-ucores
* Java for UCores is meant to be an accelerator 'friendly' Java version based on OpenJDK and obuildfactory. Accelerator users should consider using Java for UCores in conjunction with Aparapi UCores as it will automatically allocate memory on boundaries that can enhance PCIE transfer speeds.
* Added a a more flexible way of per-platform config through ConfigSettings.cpp
* Support for Altera OpenCL V15
* Support for platform select using Altera OpenCL V15 ICD
* Support for selection of binary/source flow for non-Altera platforms (usage examples below)
* Control dynamic binary/source/default flow through kernel (usage examples below)
* Device select for systems with multiple devices of the same type (usage examples below)
* New Configuration Settings file for better per platform settings control (ConfigSettings.h)
* Improved build system for Altera JNI
* Aparapi Range.getFactors bug fix – fixed a bug in the original Aparapi distribution.
* Altera ICD bug fix – only other accelerators were discovered

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