#HSAEnablementOfLambdaBranch
*Adding HSA Support to Aparapi lambda branch Updated Feb 28, 2014 by frost.g...@gmail.com*

* [How to setup a HSA enabled Linux Platform](SettingUpLinuxHSAMachineForAparapi.md)
* [How to setup a HSA simulator on a Linux Platform](UsingAparapiLambdaBranchWithHSASimulator.md)

Recently the HSA Foundation released their ‘Programmers Reference Manual’. This manual is for developers wishing to write code for upcoming HSA compatible devices, it describes the HSA Intermediate Language (HSAIL) along with its binary form (BRIG) and describes how code is expected to execute on a HSA enabled devices.

In many ways we can think of HSAIL as we do Java bytecode. It is a common intermediate form that can be optimized at runtime to execute across a variety of future heterogeneous platforms. HSAIL will greatly simplify the development of software taking advantage of both sequential and parallel compute solutions.

Now that the spec is out, we have started adding HSA support to the Aparapi lambda branch. We believe that HSA combined with the upcoming Java 8 feature lambda will be a natural way to express parallel algorithms which can be executed on the GPU via HSA.

A HSA+Lambda enabled Aparapi will remove many of Aparapi's constraints. HSA allows all of the CPU's memory to be accessed directly from code running on the GPU. This means

* We no longer need to move data from the host CPU to the GPU.
* We are no longer limited to the memory addressable from the GPU
* We can access multi-dim arrays efficiently
* We can access Java objects directly from the GPU.
These are all substantial benefits.

In the existing code (early prototype) we provide access to HSA as a specific device type.

So our ubiquitous 'squares' example will initially be written as:

    int in[] = ..//
    int out[] = .../
    Device.hsa().forEach(in.length, (i)->{
       out[i] = in[i]*in[i];
     });
You will obviously need a Java 8 compatible JDK ([https://jdk8.java.net/download.html](https://jdk8.java.net/download.html)) in your path.

We also recommend using IntelliJ which has preliminary support for Java 8 lambda features. You can download the community edition of IntelliJ from [http://www.jetbrains.com/idea/](http://www.jetbrains.com/idea/)