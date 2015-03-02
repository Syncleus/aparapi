Aparapi-Ucores - Aparapi for Unconvenional Cores
================================================
This project is a fork of AMD's Aparapi framework meant to allow using simple Java code to target any type of heterogeneous device that supports OpenCL.

Why we created Aparapi-Ucores
-----------------------------
The original Aparapi framework was built with GPUs/APUs in mind. We believe that heterogeneous computing has a lot more to offer than just combining CPUs and GPUs together. In the past few years we have started to see OpenCL support for FPGAs (Altera/Xilinx) and DSPs(TI) and more types computing devices will soon follow suite. 

Combining several types of devices and architectures is at the heart of heterogeneous computing's power efficiency advantage. We believe that in order to optimize performance per watt and accommodate the needs of today's high level programming requirements we need to be able to work with all these types of devices using a single high level code base. 

The framework has been tested and known to work on the following device types/OpenCL SDKs*:
1. CPUs (AMD/Intel)
2. GPUs (AMD/NVidia)
2. APUs (AMD)
3. FPGAs(Altera - Nallatech/Terasic boards)

* Tested operating systems - CentOS 6.3/4/5/6

Changes from original work
-----------------------------------
1. Support for FPGA devices (right now Altera OpenCL is supported)
2. Support for multiple platforms (platform selection)
3. Support for accelerators and OpenCL binary file format and flow
3. Built against OpenJDK (to allow more control/flexibility in future Java/OpenCL integration) 
4. Misc small changes - Improve profiling usability etc.

Project History 
---------------
As far as I know the original idea of trying to target FPGAs from Java using Aparapi was the brain child of two guys at HP Servers(Sai Rahul Chalamalasetti, Mitch Wright). 

I started writing the first implementation of the project (called APARAPI-FPGA's) while working as an intern at HP Servers in 2013.  It started as a proof of concept but showed great promise.

During that work I started envisioning a more general solution using Aparapi that will allow us to target any OpenCL device. i.e. not only GPUs or FPGAs.

The project culminated with two publications in Sept 2014 (FPL 2014 and FSP 2014) in which we described our findings and promised to release our initial work back to the open source community.

Since then we received a lot of interest and requests to release the source and we have been planning to release our implementation for some time now.

Due the crazy schedule of a PhD student I had to postpone work on this for a long time, but a couple of months back I decided to dedicate the time to rewrite the framework to what I envisioned it to be, a general high level framework for heterogeneous devices. 

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

Documentation
---------------
For information on how to setup the environment and run some tests please check the [project root]/docs folder

Acknowledgments
---------------
1. AMD for doing such a great job with Aparapi without which this project would be an order of magnitude more complex.

2. HP Servers which believed back in 2012 that OpenCL on FPGAs is something worth looking at.

3. Altera, Nallatech and Terassic for contributing FPGA hardware for us to develop and test on.
