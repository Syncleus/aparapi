# Setting up Linus HSA machine for APARAPI

## Introduction
Now that HSA hardware is generally available I figured it was time to describe how to setup a HSA enabled Linux platform so that it can run Aparapi.

Here is [a nice introduction to HSA](http://developer.amd.com/resources/heterogeneous-computing/what-is-heterogeneous-system-architecture-hsa/)

But for Aparapi users the main advantage is that we are no longer limited to the GPU memory for running GPU tasks. Also because the CPU and the GPU can both see the same memory (the Java heap) Aparapi code can now access Java objects directly. This removes a number of Aparapi constraints. So more of your code can now run on the GPU.

##Hardware Required
These instructions were based on my experience setting up a platform using the following hardware.

|Component  | Suggested                                                                                                   |
|-----------|-------------------------------------------------------------------------------------------------------------|
|APU        | AMD A10-7850K APU [http://www.amd.com/us/products/desktop/processors/a-series/Pages/a-series-apu.aspx](http://www.amd.com/us/products/desktop/processors/a-series/Pages/a-series-apu.aspx) |
| Motherboard | ASUS A88X-PRO or A88XM-A [http://www.asus.com/Motherboards/A88XPRO http://www.asus.com/Motherboards/A88XMA](http://www.asus.com/Motherboards/A88XPRO http://www.asus.com/Motherboards/A88XMA) |
|Memory | G.SKILL Ripjaws X Series 16GB (2 x 8GB) 240-Pin DDR3 SDRAM DDR3 2133 |

##Software Required
We also have some software dependencies.

|Component                                              | Suggested                                                                    |
|-------------------------------------------------------|------------------------------------------------------------------------------|
|Java 8 JDK                                             | http://www.oracle.com/technetwork/java/javase/downloads/ea-jsp-142245.html   |
| Ubuntu 13.10 64-bit edition                           | http://www.ubuntu.com/download                                               |
| Ubuntu 13.10 64-bit edition HSA enabled kernel image	| https://github.com/HSAFoundation/Linux-HSA-Drivers-And-Images-AMD            |
| OKRA HSA enabled runtime                              | https://github.com/HSAFoundation/Okra-Interface-to-HSA-Device                |

The hope is that the list of HW/SW support widens, but for early adopters this is the set of HW/SW we have been testing with.

##Setting up your System
Configure your BIOS to support IOMMU
Once you have built your AMD A10-7850K APU based system you should make sure that your system is configured to use IOMMU.

Remember HSA allows the GPU and CPU cores to share the same memory. IOMMU needs to be enabled for this.

##For the A88X-PRO board
For the recommended ASUS board above you will need to make sure that your BIOS is updated to version 0802. Here is a direct link to the 0802 version of the BIOS from ASUS's site as of 2/28/2014.

[http://dlcdnet.asus.com/pub/ASUS/mb/SocketFM2/A88X-PRO/A88X-PRO-ASUS-0802.zip](http://dlcdnet.asus.com/pub/ASUS/mb/SocketFM2/A88X-PRO/A88X-PRO-ASUS-0802.zip)

Once you have the latest BIOS you will need to enable IOMMU in the system BIOS. This is done using the "CPU Configuration" screen under "Advanced Mode" and then enabling IOMMU.

##For the A88XM-A
You will need the 1102 (or later) version of the BIOS

[http://dlcdnet.asus.com/pub/ASUS/mb/SocketFM2/A88XM-A/A88XM-A-ASUS-1102.zip](http://dlcdnet.asus.com/pub/ASUS/mb/SocketFM2/A88XM-A/A88XM-A-ASUS-1102.zip)

Once you have the latest BIOS you will need to enable IOMMU in the system BIOS. This is done using the "CPU Configuration" screen under "Advanced Mode" and then enabling IOMMU.

##Installing Ubuntu 13.10
Once you have your BIOS setup you need to install Ubuntu [http://www.ubuntu.com/download](http://www.ubuntu.com/download)

Installing HSA enabled kernel + driver
Until all of the HSA drivers and features are available in stock linux and have been pulled down into Ubuntu distro we will need a special HSA enabled kernel image.

A Ubuntu compatible kernel can be pulled from github

    $ cd ~ # I put all of this in my home dir
    $ sudo apt-get install git
    $ git clone https://github.com/HSAFoundation/Linux-HSA-Drivers-And-Images-AMD.git

Or you can pull the zip and unzip using curl if you don't have git

    $ cd ~ # I put all of this in my home dir
    $ curl -L https://github.com/HSAFoundation/Linux-HSA-Drivers-And-Images-AMD/archive/master.zip > drivers.zip
    $ unzip drivers.zip

This will create the following subdir on your machine

  Linux-HSA-Drivers-And-Images-AMD/
    LICENSE
    README.md
    ubuntu12.10-based-alpha1/
        xorg.conf
        linux-image-3.13.0-kfd+_3.13.0-kfd+-2_amd64.deb


From here we can install our new image and setup the HSA KFD (the driver for HSA)and reboot to the new kernel.

    $ cd ~/Linux-HSA-Drivers-And-Images-AMD
    $ echo  "KERNEL==\"kfd\", MODE=\"0666\"" | sudo tee /etc/udev/rules.d/kfd.rules
    $ sudo dpkg -i ubuntu13.10-based-alpha1/linux-image-3.13.0-kfd+_3.13.0-kfd+-2_amd64.deb
    $ sudo cp ~/Linux-HSA-Drivers-And-Images-AMD/ubuntu13.10-based-alpha1/xorg.conf /etc/X11
    $ sudo reboot

##Installing OKRA RT
Now we need a runtime for executing HSAIL code. We share common infrastructure used by our sister OpenJDK project called Sumatra. Both Aparapi and Sumatra use OKRA to execute HSAIL code on a HSA enabled platform.

We can get the latest version using of OKRA (Offloadable Kernel Runtime API) from another HSA foundation repository.

    $ cd ~ # I put all of this in my home dir
    $ git clone https://github.com/HSAFoundation/Okra-Interface-to-HSA-Device.git

or if you prefer curl/unzip

    $ cd ~ # I put all of this in my home dir
    $ curl -L https://github.com/HSAFoundation/Okra-Interface-to-HSA-Device/archive/master.zip > okra.zip
    $ unzip okra.zip

This will create the following dir structure.

  Okra-Interface-to-HSA-Device/
    README.md
    okra/
       README
       dist/
          okra.jar
          bin/
             libamdhsacl64.so
             libnewhsacore64.so
             libokra_x86_64.so
          include/
             common.h
             okraContext.h

       samples/
          dist/
            Squares
            Squares.hsail
          runSquares.sh

OKRA offers a C API (for those that are so inclined ;) ) as well as a java jar file which contains JNI wrappers.

##Sanity check your HSA and OKRA install
So to sanity check your install you can run a small sample app (binary)

    $ cd ~/Okra-Interface-to-HSA-Device/okra/samples/
    $ sh runSquares.sh

If everything is OK this should run the C Squares test app.

Congratulations, you have executed your first HSA enabled app.

##Getting OpenCL headers and libraries
We need OpenCL headers and libraries to build Aparapi (remember we still support OpenCL).

My recommendation is to download AMD-APP-SDK-v2.9-lnx64.tgz from [http://developer.amd.com/tools-and-sdks/heterogeneous-computing/amd-accelerated-parallel-processing-app-sdk/downloads](http://developer.amd.com/tools-and-sdks/heterogeneous-computing/amd-accelerated-parallel-processing-app-sdk/downloads) and extract the libraries and headers.

Note that we have nested zipped jars in this archive.

    $ cd ~
    $ gunzip ~/Downloads/AMD-APP-SDK-v2.9-lnx64.tgz
    $ tar xvf ~/Downloads/AMD-APP-SDK-v2.9-lnx64.tar
    $ rm ~/default-install_lnx_64.pl ~/icd-registration.tgz ~/Install-AMD-APP.sh ~/ReadMe.txt
    $ gunzip ~/AMD-APP-SDK-v2.9-RC-lnx64.tgz
    $ tar xvf ~/AMD-APP-SDK-v2.9-RC-lnx64.tar
    $ rm ~/AMD-APP-SDK-v2.9-RC-lnx64.tar
    $ rm -rf AMD-APP-SDK-v2.9-RC-lnx64/samples

Note where AMD-APP-SDK-v2.9-RC-lnx64 is located, you need this in the following step.

##You will need Java 8
Download Java 8 JDK from [https://jdk8.java.net/download.html](https://jdk8.java.net/download.html) I chose to download the zipped tar and not install with RPM so I can control the location of the install.

    >$ cd ~
    $ gunzip /home/gfrost/Downloads/jdk-8-fcs-bin-b132-linux-x64-04_mar_2014.tar.gz
    $ tar xvf ~/Downloads/jdk-8-fcs-bin-b132-linux-x64-04_mar_2014.tar

I now have ~/jdk1.8.0 as my java 8 install dir.

Alternatively the following will pull from Oracles site using curl

    $ cd ~
    $ curl http://download.java.net/jdk8/archive/b132/binaries/jdk-8-fcs-bin-b132-linux-x64-04_mar_2014.tar.gz?q=download/jdk8/archive/b132/binaries/jdk-8-fcs-bin-b132-linux-x64-04_mar_2014.tar.gz > jdk-8-fcs-bin-b132-linux-x64-04_mar_2014.tar.gz
    $ gunzip jdk-8-fcs-bin-b132-linux-x64-04_mar_2014.tar.gz
    $ tar xvf jdk-8-fcs-bin-b132-linux-x64-04_mar_2014.tar

I now have ~/jdk1.8.0 as my java 8 install dir.

You will need ant
    $ sudo apt-get install ant

This takes a long time because in also installs a java7 jdk.

You will need g++
We use g++ to build the JNI side of Aparapi

    $ sudo apt-get install g++

##Pulling the HSA enabled Aparapi branch and building
Now we can pull the Aparapi lambda/HSA branch from SVN

    $ sudo apt-get install subversion
    $ svn checkout https://aparapi.googlecode.com/svn/branches/lambda aparapi-lambda

If you are familiar with Aparapi structure then this tree should not be that much of a surprise but there are a few subtle changes.

Specifically the build system has been changed to support OKRA, Aparapi JNI code is provided as a Java agent and the execution scripts all refer to ${APARAPI_HOME}/env.sh to setup a reasonable execution environment.

You will need to edit env.sh and make sure that APARAPI_HOME, OKRA_HOME, OCL_HOME and JAVA_HOME correctly.

Here are how I set my vars.

|environment variable	|value                                               |
|-----------------------|----------------------------------------------------|
|JAVA_HOME              |/home/${LOGNAME}/jdk1.8.0                           |
|OCL_HOME               |/home/${LOGNAME}/AMD-APP-SDK-v2.9-RC-lnx64          |
|APARAPI_HOME           |/home/${LOGNAME}/aparapi-lambda                     |
|OKRA_HOME              |/home/${LOGNAME}/Okra-Interface-to-HSA-Device/okra/ |

It is recommended (thanks notzed ;) ) that you test your env.sh using sh env.sh until it stops reporting errors. Once you have finished I recommend sourcing it into your current shell before building with ant.

    $ cd ~aparapi-lambda
    $ . env.sh
    $ ant

If you get any problems check the env.sh vars first.

If all is well you should be able to run some samples.

    $ cd ~/aparapi-lambda/samples/mandel
    $ sh hsailmandel.sh