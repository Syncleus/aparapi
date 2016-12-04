# Aparapi

[![License](http://img.shields.io/:license-apache-blue.svg?style=flat-square)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.aparapi/aparapi/badge.png?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.aparapi/aparapi/)
[![Javadocs](http://www.javadoc.io/badge/com.aparapi/aparapi.svg)](http://www.javadoc.io/doc/com.aparapi/aparapi)
[![Gitter](https://badges.gitter.im/Syncleus/aparapi.svg)](https://gitter.im/Syncleus/aparapi?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

A framework for executing native Java code on the GPU.

**Licensed under the Apache Software License v2**

Aparapi allows developers to write native Java code capable of being executed directly on a graphics card GPU by converting Java byte code to an OpenCL kernel dynamically at runtime. Because it is backed by OpenCL Aparapi is compatible with all OpenCL compatible Graphics Cards.

A GPU has a unique architecture that causes them to behave differently than a CPU. One of the most noticeable differences is that while a typical CPU has less than a dozen cores a high end GPU may have hundreds of cores. This makes them uniquely suited for data-parallel computation that can result in speedups hundreds of times more than what is capable with your average CPU. This can mean the difference between needing a whole data center to house your application versus just one or two computers, potentially saving millions in server costs.

Aparapi was originally a project conceived and developed by AMD corporation. It was later abandoned by AMD and sat mostly-idle for several years. Despite this there were some failed efforts by the community to keep the project alive, but without a clear community leader no new releases ever came. Eventually we came along and rescued the project, and modernized the project. Finally after such a long wait the first Aparapi release in 5 years was published and the community continues to push forward with renewed excitement.

For detailed documentation see [Aparapi.com](http://Aparapi.com) or check out the [latest Javadocs](http://www.javadoc.io/doc/com.aparapi/aparapi).

For support please use [Gitter](https://gitter.im/Syncleus/aparapi) or the [official Aparapi mailing list](https://groups.google.com/d/forum/aparapi).

Please file bugs and feature requests on [Github](https://github.com/Syncleus/aparapi/issues).

## Dependency

To include Aparapi in your project of choice include the following Maven dependency into your build.

```xml

<dependency>
    <groupId>com.aparapi</groupId>
    <artifactId>aparapi</artifactId>
    <version>1.1.2</version>
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
    result[i] = intA[i] + inB[i];
}
```

And refactor the sequential loop to the following form:

```java

Kernel kernel = new Kernel() {
    @Override
    public void run() {
        int i = getGlobalId();
        result[i] = intA[i] + inB[i];
    }
};

Range range = Range.create(result.length);
kernel.execute(range);
```
