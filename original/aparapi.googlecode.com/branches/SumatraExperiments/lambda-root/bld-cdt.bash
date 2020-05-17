#!/bin/bash
. ./setvars.bash

find lambda -name *.cpp -o -name *.c \
   | grep -v "/hotspot/src/cpu/sparc/" \
   | grep -v "/hotspot/src/cpu/zero/" \
   | grep -v "/hotspot/src/os_cpu/bsd_x86/" \
   | grep -v "/hotspot/src/os_cpu/bsd_zero/" \
   | grep -v "/hotspot/src/os_cpu/solaris_x86/" \
   | grep -v "/hotspot/src/os_cpu/windows_x86/" \
   | grep -v "/hotspot/src/os_cpu/linux_zero/" \
   | grep -v "/hotspot/src/os_cpu/linux_sparc/" \
   | grep -v "/hotspot/src/os_cpu/solaris_sparc/"  \
   | grep -v "/hotspot/src/os/windows/"  \
   | grep -v "/hotspot/src/os/solaris/"  \
   | grep -v "/hotspot/src/os/bsd/" \
   | grep -v "/jdk/src/" \
   | grep -v "/jdk/make/" \
   | grep -v "/jdk/test/" \
   | grep -v "/build/.*/democlasses/" \
   | sed "s:^:${PWD}/:" \
   | tee sFiles \
   | sed "s:/[^/]*$::" \
   | sort -u  > sDirs

find lambda -name *.h -o -name *.hpp \
   | grep -v "/hotspot/src/cpu/sparc/" \
   | grep -v "/hotspot/src/cpu/zero/" \
   | grep -v "/hotspot/src/os_cpu/bsd_x86/" \
   | grep -v "/hotspot/src/os_cpu/bsd_zero/" \
   | grep -v "/hotspot/src/os_cpu/solaris_x86/" \
   | grep -v "/hotspot/src/os_cpu/windows_x86/" \
   | grep -v "/hotspot/src/os_cpu/linux_zero/" \
   | grep -v "/hotspot/src/os_cpu/linux_sparc/" \
   | grep -v "/hotspot/src/os_cpu/solaris_sparc/"  \
   | grep -v "/hotspot/src/os/windows/"  \
   | grep -v "/hotspot/src/os/solaris/"  \
   | grep -v "/hotspot/src/os/bsd/" \
   | grep -v "/hotspot/agent/src/os/solaris/" \
   | grep -v "/hotspot/agent/src/os/bsd/" \
   | grep -v "/jdk/src/" \
   | grep -v "/jdk/make/" \
   | grep -v "/jdk/test/" \
   | grep -v "/build/.*/democlasses/" \
   | sed "s:[^/]*$::" \
   | sed "s:^:-I${PWD}/:" \
   | sort -u  > hDirs

touch $(cat sFiles)
pushd lambda
make DEBUG_NAME=fastdebug NO_DOCS=true NO_IMAGES=true DEV_ONLY=true hotspot 2>/dev/null \
   | tr " " "\n"\
   | grep "^-D" \
   | sort -u  > ../hOpts
popd
