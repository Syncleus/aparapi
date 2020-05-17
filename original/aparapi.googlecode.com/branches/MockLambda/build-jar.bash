. ./setvars.bash

echo JAVA_HOME=${JAVA_HOME}

ls ${JAVA_HOME}

rm -r -f aparapi.jar classes 

mkdir classes

${JAVA_HOME}/bin/javac \
 -XDlambdaToMethod \
 -g \
 -d classes \
 -sourcepath src/java/ \
 src/java/com/amd/aparapi/Annotations.java \
 src/java/com/amd/aparapi/Aparapi.java \
 src/java/com/amd/aparapi/AparapiException.java \
 src/java/com/amd/aparapi/BlockWriter.java \
 src/java/com/amd/aparapi/BranchSet.java \
 src/java/com/amd/aparapi/ByteBuffer.java \
 src/java/com/amd/aparapi/ByteReader.java \
 src/java/com/amd/aparapi/ClassModel.java \
 src/java/com/amd/aparapi/ClassParseException.java \
 src/java/com/amd/aparapi/CodeGenException.java \
 src/java/com/amd/aparapi/Config.java \
 src/java/com/amd/aparapi/DeprecatedException.java \
 src/java/com/amd/aparapi/Device.java \
 src/java/com/amd/aparapi/Entrypoint.java \
 src/java/com/amd/aparapi/ExpressionList.java \
 src/java/com/amd/aparapi/Instruction.java \
 src/java/com/amd/aparapi/InstructionHelper.java \
 src/java/com/amd/aparapi/InstructionPattern.java \
 src/java/com/amd/aparapi/InstructionSet.java \
 src/java/com/amd/aparapi/InstructionTransformer.java \
 src/java/com/amd/aparapi/InstructionViewer.java \
 src/java/com/amd/aparapi/JavaDevice.java \
 src/java/com/amd/aparapi/Kernel.java \
 src/java/com/amd/aparapi/KernelRunner.java \
 src/java/com/amd/aparapi/KernelWriter.java \
 src/java/com/amd/aparapi/MethodModel.java \
 src/java/com/amd/aparapi/OpenCL.java \
 src/java/com/amd/aparapi/OpenCLAdapter.java \
 src/java/com/amd/aparapi/OpenCLArgDescriptor.java \
 src/java/com/amd/aparapi/OpenCLDevice.java \
 src/java/com/amd/aparapi/OpenCLJNI.java \
 src/java/com/amd/aparapi/OpenCLKernel.java \
 src/java/com/amd/aparapi/OpenCLMem.java \
 src/java/com/amd/aparapi/OpenCLPlatform.java \
 src/java/com/amd/aparapi/OpenCLProgram.java \
 src/java/com/amd/aparapi/ProfileInfo.java \
 src/java/com/amd/aparapi/Range.java \
 src/java/com/amd/aparapi/RangeException.java \
 src/java/com/amd/aparapi/UnsafeWrapper.java 

${JAVA_HOME}/bin/jar cf aparapi.jar -C classes com
