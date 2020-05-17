#!/bin/bash
. ./setvars.bash
DEBUG_NAME=fastdebug NO_DOCS=true NO_IMAGES=true DEV_ONLY=true LANG=C ZIP_DEBUGINFO_FILES=0 HOTSPOT_BUILD_JOBS=4  ALLOW_DOWNLOADS=true  HOTSPOT_BUILD_JOBS=4 PARALLEL_COMPILE_JOBS=4  NO_DOCS=true NO_IMAGES=true DEV_ONLY=true ALT_BOOTDIR=/usr/lib/jvm/java-7-openjdk-amd64 make -C lambda  $*
