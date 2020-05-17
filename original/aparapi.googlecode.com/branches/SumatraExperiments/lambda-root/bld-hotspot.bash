#!/bin/bash
. ./setvars.bash
pushd lambda
# if we want to build all in hotspot uncomment next line
# touch $(find . -name "*.cpp" -o -name "*.c" -o -name "*.h" -o -name "*.hpp")
make DEBUG_NAME=fastdebug NO_DOCS=true NO_IMAGES=true DEV_ONLY=true hotspot
popd

