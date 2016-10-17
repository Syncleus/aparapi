# Aparapi

![License](http://img.shields.io/:license-apache-blue.svg?style=flat-square)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Javadocs](http://www.javadoc.io/badge/com.syncleus.aparapi/aparapi.svg)](http://www.javadoc.io/doc/com.syncleus.aparapi/aparapi)
[![Gitter](https://badges.gitter.im/Syncleus/aparapi.svg)](https://gitter.im/Syncleus/aparapi?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Aparapi allows Java developers to take advantage of the compute power of GPU and APU devices by executing data parallel
code fragments on the GPU rather than being confined to the local CPU. It does this by converting Java bytecode to
OpenCL at runtime and executing on the GPU, if for any reason Aparapi can't execute on the GPU it will execute in a
Java thread pool.

We like to think that for the appropriate workload this extends Java's 'Write Once Run Anywhere' to include GPU devices.

## About the name

Aparapi is just a contraction of "A PARallel API"

However... "Apa rapi" in Indonesian (the language spoken on the island of Java) translates to "What a neat...". So
"Apa rapi Java Project" translates to "What a neat Java Project" How cool is that?