#!/bin/bash
unset JAVA_HOME
export LANG=C
export ALT_BOOTDIR=/usr/lib/jvm/java-7-openjdk-amd64
export ALLOW_DOWNLOADS=true
export CORES=$(grep "cpu cores" /proc/cpuinfo | sort -u | sed "s/^.*: *//")
export HOTSPOT_BUILD_JOBS=${CORES}
export PARALLEL_COMPILE_JOBS=${CORES}

export CDT_GCC_SOURCE_DIRS=\
  build/linux-amd64/hotspot/outputdir/linux_amd64_compiler2/generated/generated/adfiles\
  build/linux-amd64/hotspot/outputdir/linux_amd64_compiler2/generated/generated/jvmtifiles\
  hotspot/src/cpu/x86/vm\
  hotspot/src/os_cpu/linux_x86/vm\
  hotspot/src/os/linux/vm\
  hotspot/src/os/posix/vm\
  hotspot/src/share/vm/adlc\
  hotspot/src/share/vm/asm\
  hotspot/src/share/vm/c1\
  hotspot/src/share/vm/ci\
  hotspot/src/share/vm/classfile\
  hotspot/src/share/vm/code\
  hotspot/src/share/vm/compiler\
  hotspot/src/share/vm/gc_implementation/concurrentMarkSweep\
  hotspot/src/share/vm/gc_implementation/g1\
  hotspot/src/share/vm/gc_implementation/parallelScavenge\
  hotspot/src/share/vm/gc_implementation/parNew\
  hotspot/src/share/vm/gc_implementation/shared\
  hotspot/src/share/vm/gc_interface\
  hotspot/src/share/vm/interpreter\
  hotspot/src/share/vm/libadt\
  hotspot/src/share/vm/memory\
  hotspot/src/share/vm/oops\
  hotspot/src/share/vm/opto\
  hotspot/src/share/vm/prims\
  hotspot/src/share/vm/prims/wbtestmethods\
  hotspot/src/share/vm/runtime\
  hotspot/src/share/vm/services\
  hotspot/src/share/vm/utilities

export CDT_GCC_INCLUDE_DIRS=\
  build/linux-amd64/hotspot/outputdir/linux_amd64_compiler2/generated/generated/adfiles\
  hotspot/src/cpu/x86/vm\
  hotspot/src/os_cpu/linux_x86/vm\
  hotspot/src/os/linux/vm\
  hotspot/src/os/posix/vm\
  hotspot/src/share/vm\

export CDT_GCC_OPTS=\
  -DAMD64 \
  -DARCH=\"amd64\" \
  -DASSERT \
  -DCOMPILER1 \
  -DCOMPILER2
  -DFASTDEBUG
  -DFULL_VERSION=\"25.0-b05\"
  -DGAMMA
  -DHOTSPOT_BUILD_TARGET=\"fastdebug\"
  -DHOTSPOT_BUILD_USER=\"gfrost\"
  -DHOTSPOT_LIB_ARCH=\"amd64\"
  -DHOTSPOT_RELEASE_VERSION=\"25.0-b05\"
  -DHOTSPOT_VM_DISTRO=\"OpenJDK\"
  -DINCLUDE_TRACE
  -DJDK_MAJOR_VERSION=\"1\"
  -DJDK_MINOR_VERSION=\"8\"
  -DJRE_RELEASE_VERSION=\"1.8.0-internal-fastdebug-gfrost_2012_12_04_14_11-b00\"
  -DLAUNCHER_TYPE=\"gamma\"
  -DLINK_INTO_LIBJVM
  -DLINUX
  -DTARGET_ARCH_MODEL_x86_64
  -DTARGET_ARCH_x86
  -DTARGET_COMPILER_gcc
  -DTARGET_OS_ARCH_MODEL_linux_x86_64
  -DTARGET_OS_ARCH_linux_x86
  -DTARGET_OS_FAMILY_linux
  -DVM_LITTLE_ENDIAN
  -D_FILE_OFFSET_BITS=64
  -D_GNU_SOURCE
  -D_LP64
  -D_LP64=1
  -D_REENTRANT
  -Damd64
