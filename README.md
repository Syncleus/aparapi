![](http://aparapi.com/images/logo-text-adjacent.png)

[![License](http://img.shields.io/:license-apache-blue.svg?style=flat-square)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Semantic Versioning](https://img.shields.io/SemVer/2.0.0.png)](http://semver.org/spec/v2.0.0.html)
[![Javadocs](http://www.javadoc.io/badge/com.aparapi/aparapi.svg)](http://www.javadoc.io/doc/com.aparapi/aparapi)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.aparapi/aparapi/badge.png?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.aparapi/aparapi/)
[![Gitter](https://badges.gitter.im/Syncleus/aparapi.svg)](https://gitter.im/Syncleus/aparapi?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

[![Build Status](https://travis-ci.org/Syncleus/aparapi.svg?branch=master)](https://travis-ci.org/Syncleus/aparapi)
[![codecov](https://codecov.io/gh/Syncleus/aparapi/branch/master/graph/badge.svg)](https://codecov.io/gh/Syncleus/aparapi)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/b8c0efbe275f44369d9959b5ded14bfd)](https://www.codacy.com/app/freemo/aparapi?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Syncleus/aparapi&amp;utm_campaign=badger)

A framework for executing native Java code on the GPU.

**Licensed under the Apache Software License v2**

Aparapi allows developers to write native Java code capable of being executed directly on a graphics card GPU by converting Java byte code to an OpenCL kernel dynamically at runtime. Because it is backed by OpenCL Aparapi is compatible with all OpenCL compatible Graphics Cards.

A GPU has a unique architecture that causes them to behave differently than a CPU. One of the most noticeable differences is that while a typical CPU has less than a dozen cores a high end GPU may have hundreds of cores. This makes them uniquely suited for data-parallel computation that can result in speedups hundreds of times more than what is capable with your average CPU. This can mean the difference between needing a whole data center to house your application versus just one or two computers, potentially saving millions in server costs.

Aparapi was originally a project conceived and developed by AMD corporation. It was later abandoned by AMD and sat mostly idle for several years. Despite this there were some failed efforts by the community to keep the project alive, but without a clear community leader no new releases ever came. Eventually we came along and rescued the project. Finally after such a long wait the first Aparapi release in 5 years was published and the community continues to push forward with renewed excitement.

Below you will find two side-by-side comparisons for the nbody problem on a CPU vs a GPU. The simulation is being run on an inexpensive graphics card; you can even run it yourself from the [examples project](https://github.com/Syncleus/aparapi-examples). Its obvious the drastic performance gains that can be acheived with Aparapi.

| ![NBody GPU](http://aparapi.com/images/nbody_gpu.gif) | ![NBody CPU](http://aparapi.com/images/nbody_cpu.gif) |
|:---:|:---:|
| GPU Accelerated | CPU Multi-threaded (8 cores) |

## Support and Documentation

Aparapi Javadocs: [latest](http://www.javadoc.io/doc/com.aparapi/aparapi) - [1.6.0](http://www.javadoc.io/doc/com.aparapi/1.6.0) - [1.5.0](http://www.javadoc.io/doc/com.aparapi/aparapi/1.5.0) - [1.4.1](http://www.javadoc.io/doc/com.aparapi/aparapi/1.4.1) - [1.4.0](http://www.javadoc.io/doc/com.aparapi/aparapi/1.4.0) - [1.3.4](http://www.javadoc.io/doc/com.aparapi/aparapi/1.3.4) - [1.3.3](http://www.javadoc.io/doc/com.aparapi/aparapi/1.3.3) - [1.3.2](http://www.javadoc.io/doc/com.aparapi/aparapi/1.3.2) - [1.3.1](http://www.javadoc.io/doc/com.aparapi/aparapi/1.3.1) - [1.3.0](http://www.javadoc.io/doc/com.aparapi/aparapi/1.3.0) - [1.2.0](http://www.javadoc.io/doc/com.aparapi/aparapi/1.2.0) - [1.1.2](http://www.javadoc.io/doc/com.aparapi/aparapi/1.1.2) - [1.1.1](http://www.javadoc.io/doc/com.aparapi/aparapi/1.1.1) - [1.1.0](http://www.javadoc.io/doc/com.aparapi/aparapi/1.1.0) - [1.0.0](http://www.javadoc.io/doc/com.syncleus.aparapi/aparapi/1.0.0)

For detailed documentation see [Aparapi.com](http://Aparapi.com) or check out the [latest Javadocs](http://www.javadoc.io/doc/com.aparapi/aparapi).

For support please use [Gitter](https://gitter.im/Syncleus/aparapi) or the [official Aparapi mailing list](https://groups.google.com/d/forum/aparapi).

Please file bugs and feature requests on [Github](https://github.com/Syncleus/aparapi/issues).

Aparapi conforms to the [Semantic Versioning 2.0.0](http://semver.org/spec/v2.0.0.html) standard. That means the version of a release isnt arbitrary but rather describes how the library interfaces have changed. Read more about it at the [Semantic Versioning page](http://semver.org/spec/v2.0.0.html).

## Related Projects

This particular repository only represents the core Java library. There are several other related repositories worth taking a look at.

* [Aparapi Examples](https://github.com/Syncleus/aparapi-examples) - A collection of Java examples to showcase the Aparapi library and help developers get started.
* [Aparapi JNI](https://github.com/Syncleus/aparapi-jni) - A java JAR which embeds and loads the native components at runtime. This prevents the need to seperately install the Aparapi Native library.
* [Aparapi Native](https://github.com/Syncleus/aparapi-native) - The native library component. Without this the Java library can't talk to the graphics card. This is not a java project but rather a C/C++ project.
* [Aparapi Vagrant](https://github.com/Syncleus/aparapi-vagrant) - A vagrant environment for compiling aparapi native libraries for linux, both x86 an x64.

## Prerequisites

Aparapi will run as-is on the CPU, however in order to access the GPU it requires OpenCL to be installed on the local system. If OpenCL isnt found then the library will just fallback to CPU mode. Aparapi supports, and has been tested on, both OpenCL 1.2, OpenCL 2.0, and OpenCL 2.1.

**Aparapi runs on all operating systems and platforms, however GPU acceleration support is currently provided for the following platforms: Windows 64bit, Windows 32bit, Mac OSX 64bit, Linux 64bit, and Linux 32bit.**

Note: It is no longer required to manually install the [Aparapi JNI native interface](https://github.com/Syncleus/aparapi-native), this is now done automatically through maven as a dependency on Aparapi.

## Java Dependency

To include Aparapi in your project of choice include the following Maven dependency into your build.

```xml

<dependency>
    <groupId>com.aparapi</groupId>
    <artifactId>aparapi</artifactId>
    <version>1.6.0</version>
</dependency>
```

## Obtaining the Source

The official source repository for Aparapi is located in the Syncleus Github repository and can be cloned using the
following command.

```bash

git clone https://github.com/Syncleus/aparapi.git
```

## Getting Started

With Aparapi we can take a sequential loop such as this (which adds each element from inA and inB arrays and puts the result in result).

```java

final float inA[] = .... // get a float array of data from somewhere
final float inB[] = .... // get a float array of data from somewhere
assert (inA.length == inB.length);
final float result = new float[inA.length];

for (int i = 0; i < array.length; i++) {
    result[i] = inA[i] + inB[i];
}
```

And refactor the sequential loop to the following form:

```java

Kernel kernel = new Kernel() {
    @Override
    public void run() {
        int i = getGlobalId();
        result[i] = inA[i] + inB[i];
    }
};

Range range = Range.create(result.length);
kernel.execute(range);
```
