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

The original Aparapi framework was built with GPUs/APUs in mind. We believe that heterogeneous computing has a lot more to offer than just combining CPUs and GPUs together. In the past few years we have started to see OpenCL support for FPGAs (Altera/Xilinx) and DSPs(TI) and more types computing devices will soon follow suite.

Combining several types of devices and architectures is at the heart of heterogeneous computing's power efficiency advantage. We believe that in order to optimize performance per watt and accommodate the needs of today's high level programming requirements we need to be able to work with all these types of devices using a single high level code base.

The framework has been tested and known to work on the following device types/OpenCL SDKs*:
1. CPUs (AMD/Intel)
2. GPUs (AMD/NVidia)
2. APUs (AMD)
3. FPGAs(Altera - Nallatech/Terasic boards)

Changes from original work
-----------------------------------
1. Support for FPGA devices (right now Altera OpenCL is supported)
2. Support for multiple platforms (platform selection)
3. Support for accelerators and OpenCL binary file format and flow
3. Built against OpenJDK (to allow more control/flexibility in future Java/OpenCL integration)
4. Misc small changes - Improve profiling usability etc.

For detailed documentation see [Aparapi.com](http://Aparapi.com) or check out the [latest Javadocs](http://www.javadoc.io/doc/com.aparapi/aparapi).

For support please use [Gitter](https://gitter.im/Syncleus/aparapi) or the [official Aparapi mailing list](https://groups.google.com/d/forum/aparapi).

Please file bugs and feature requests on [Github](https://github.com/Syncleus/aparapi/issues).

## Dependency

To include Aparapi in your project of choice include the following Maven dependency into your build.

```xml

<dependency>
    <groupId>com.aparapi</groupId>
    <artifactId>aparapi</artifactId>
    <version>1.2.0</version>
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

Project History 
---------------
To the best of my knowledge the original idea of trying to target FPGAs from Java using Aparapi was the brain child of Sai Rahul Chalamalasetti and Mitch Wright from HP Servers. 

I started writing the first implementation of the project (called APARAPI-FPGA's) while working as an intern at HP Servers in 2013.  It started as a proof of concept but showed great promise.

During that work I started envisioning a more general solution using Aparapi that will allow us to target any OpenCL device. i.e. not only GPUs or FPGAs.

The project culminated with two publications in Sept 2014 (FPL 2014 and FSP 2014) in which we described our findings and promised to release our initial work back to the open source community.

Since then we received a lot of interest and requests to release the source and we have been planning to release our implementation for some time now.

This project was left on the back burner for a long time, but a couple of months back I started to dedicate some time to rewrite the framework to what I originally envisioned it to be i.e. a general, programmer friendly, high level framework for heterogeneous devices. 

Two things were important to me working towards this public release:
1. I wanted to make sure it will be something that can be usable by regular Java programmers and not just release another difficult to understand research project that would require significant effort to make it work. 
2. I wanted to implement the more general idea (not just an FPGA frame work or GPU framework)

The initial release is still far from being programmer "Friendly", but it is a step forward and I hope it will encourage high level programmers to experiment with "exotic" heterogeneous architectures.

I welcome any one that wants to be involved in improving this framework.

If you use this work for academic purposes please reference the original FPL/FSP papers describing our initial Aprapi work(see below).

References
-----------------------------

The modified Aparapi framework that supports FPGAs was first introduced in the following papers:

Oren Segal, Sai Rahul Chalamalasetti, Mitch Wright and Martin Margala. “High Level Programming Framework for FPGAs in the Data Center”, Field Programmable Logic and Applications (FPL), 2014 24th International Conference on. IEEE, 2014.
http://ieeexplore.ieee.org/xpls/abs_all.jsp?arnumber=6927442

Segal, Oren, Martin Margala, Sai Rahul Chalamalasetti, and Mitch Wright. “High Level Programming for Heterogeneous Architectures.” arXiv preprint arXiv:1408.4964 (2014).
http://arxiv.org/abs/1408.4964

The original Aparapi project can be found at:
https://code.google.com/p/aparapi/

This work is part of the Heterogeneous computing research that is conducted by the Mora research group at UML:
http://mora.uml.edu/

Documentation
---------------
For information on how to setup the environment and run some tests please check the [project root]/docs folder:
https://gitlab.com/mora/aparapi-ucores/tree/master/docs

Wiki:
https://gitlab.com/mora/aparapi-ucores/wikis/home

Screenshots:
https://gitlab.com/mora/aparapi-ucores/wikis/Screenshots

Acknowledgments
---------------
1. AMD for doing such a great job with Aparapi without which this project would be an order of magnitude more complex.

2. HP Servers which believed back in 2012 that Java-OpenCL on FPGAs is something worth looking at.

3. Altera, Nallatech and Terassic for contributing FPGA hardware for us to develop and test on.
