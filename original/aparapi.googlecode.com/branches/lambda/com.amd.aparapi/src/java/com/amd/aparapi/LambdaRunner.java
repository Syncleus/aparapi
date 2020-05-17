/*
Copyright (c) 2010-2011, Advanced Micro Devices, Inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following
disclaimer. 

Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
disclaimer in the documentation and/or other materials provided with the distribution. 

Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products
derived from this software without specific prior written permission. 

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

If you use the software (in whole or in part), you shall adhere to all applicable U.S., European, and other export
laws, including but not limited to the U.S. Export Administration Regulations ("EAR"), (15 C.F.R. Sections 730 through
774), and E.U. Council Regulation (EC) No 1334/2000 of 22 June 2000.  Further, pursuant to Section 740.6 of the EAR,
you hereby certify that, except pursuant to a license granted by the United States Department of Commerce Bureau of 
Industry and Security or as otherwise permitted pursuant to a License Exception under the U.S. Export Administration 
Regulations ("EAR"), you will not (1) export, re-export or release to a national of a country in Country Groups D:1,
E:1 or E:2 any restricted technology, software, or source code you receive hereunder, or (2) export to Country Groups
D:1, E:1 or E:2 the direct product of such technology or software, if such foreign produced direct product is subject
to national security controls as identified on the Commerce Control List (currently found in Supplement 1 to Part 774
of EAR).  For the most current Country Group listings, or for additional information about the EAR or your obligations
under those regulations, please refer to the U.S. Bureau of Industry and Security's website at http://www.bis.doc.gov/. 

*/
package com.amd.aparapi;

import com.amd.aparapi.ClassModel.ConstantPool.MethodEntry;
import com.amd.aparapi.InstructionSet.MethodCall;
import com.amd.aparapi.InstructionSet.TypeSpec;
import com.amd.aparapi.InstructionSet.VirtualMethodCall;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.logging.Level;
import java.util.stream.Stream;

//import com.amd.aparapi.Kernel.EXECUTION_MODE;

/**
 * The class is responsible for executing <code>Kernel</code> implementations. <br/>
 * <p/>
 * The <code>KernelRunner</code> is the real workhorse for Aparapi.  Each <code>Kernel</code> instance creates a single
 * <code>KernelRunner</code> to encapsulate state and to help coordinate interactions between the <code>Kernel</code>
 * and it's execution logic.<br/>
 * <p/>
 * The <code>KernelRunner</code> is created <i>lazily</i> as a result of calling <code>Kernel.execute()</code>. OREF this
 * time the <code>ExecutionMode</code> is consulted to determine the default requested mode.  This will dictate how
 * the <code>KernelRunner</code> will attempt to execute the <code>Kernel</code>
 *
 * @author gfrost
 * @see com.amd.aparapi.Kernel#execute(int _globalSize)
 */
class LambdaRunner extends OpenCLRunner{

   private LambdaKernelCall lambdaKernelCall;

   private long jniContextHandle = 0;

   private Entrypoint entryPoint;

   private int argc;

   /**
    * Create a LambdaRunner for a specific Kernel instance.
    *
    * @param _intConsumer
    */

   LambdaRunner(IntConsumer _intConsumer) throws AparapiException{
      try{
         lambdaKernelCall = new LambdaKernelCall(_intConsumer);
         if(logger.isLoggable(Level.INFO)){
            logger.info("New lambda call is = " + lambdaKernelCall);
         }
      }catch(ClassNotFoundException c){
         throw new AparapiException(c);
      }
   }


   LambdaRunner(Consumer block, Stream source) throws AparapiException{
      try{
         lambdaKernelCall = new LambdaKernelCall(block);
         objectLambdaSourceArray = (Object[]) lambdaKernelCall.setupStreamSource(source);
         if(logger.isLoggable(Level.INFO)){
            logger.info("New lambda call is = " + lambdaKernelCall);
         }
      }catch(ClassNotFoundException c){
         throw new AparapiException(c);
      }
   }

   /**
    * <code>Kernel.dispose()</code> delegates to <code>KernelRunner.dispose()</code> which delegates to <code>disposeJNI()</code> to actually close JNI data structures.<br/>
    *
    * @see LambdaRunner#disposeJNI(long)
    */
   void dispose(){

      // Might need to revisit this for Superbowl

      //      if (kernel.getExecutionMode().isOpenCL()) {
      //         disposeJNI(jniContextHandle);
      //      }
   }


   private native int updateLambdaBlockJNI(long _jniContextHandle, Object newHolder, int argc);


   private KernelArg[] args = null;

   private boolean usesOopConversion = false;

   /**
    * @param arg
    * @return
    * @throws AparapiException
    */
   private boolean prepareOopConversionBuffer(KernelArg arg) throws AparapiException{
      usesOopConversion = true;
      Class<?> arrayClass = null;
      ClassModel c = null;
      boolean didReallocate = false;

      if(lambdaKernelCall.isObjectLambda()){
         // Use the actual prefix of the object stream source array
         arrayClass = objectLambdaSourceArray.getClass();
      }else{
         arrayClass = arg.field.getType();
      }

      if(arg.objArrayElementModel == null){
         String arrayClassInDotForm = TypeHelper.signatureToDotClassName(arrayClass.getName(), 1);


         if(logger.isLoggable(Level.FINE)){
            logger.fine("looking for prefix = " + arrayClassInDotForm);
         }

         // get ClassModel of obj array from entrypt.objectArrayFieldsClasses
         c = entryPoint.getObjectArrayFieldsClasses().get(arrayClassInDotForm);
         arg.objArrayElementModel = c;
      }else{
         c = arg.objArrayElementModel;
      }
      assert c != null : "should find class for elements " + arrayClass.getName();

      int arrayBaseOffset = UnsafeWrapper.arrayBaseOffset(arrayClass);
      int arrayScale = UnsafeWrapper.arrayIndexScale(arrayClass);

      if(logger.isLoggable(Level.FINEST)){
         logger.finest("Syncing obj array prefix = " + arrayClass + " cvtd= " + c.getDotClassName()
               + "arrayBaseOffset=" + arrayBaseOffset + " arrayScale=" + arrayScale);
      }

      int objArraySize = 0;
      Object newRef = null;
      try{
         newRef = arg.field.get(arg.fieldHolder);
         objArraySize = Array.getLength(newRef);
      }catch(IllegalAccessException e){
         throw new AparapiException(e);
      }

      assert (newRef != null) && (objArraySize != 0) : "no data";

      int totalStructSize = c.getTotalStructSize();
      int totalBufferSize = objArraySize * totalStructSize;

      // allocate ByteBuffer if first time or array changed
      if((arg.objArrayBuffer == null) || (newRef != arg.array)){
         ByteBuffer structBuffer = ByteBuffer.allocate(totalBufferSize);
         arg.objArrayByteBuffer = structBuffer.order(ByteOrder.LITTLE_ENDIAN);
         arg.objArrayBuffer = arg.objArrayByteBuffer.array();
         didReallocate = true;
         if(logger.isLoggable(Level.FINEST)){
            logger.finest("objArraySize = " + objArraySize + " totalStructSize= " + totalStructSize + " totalBufferSize="
                  + totalBufferSize);
         }
      }else{
         arg.objArrayByteBuffer.clear();
      }

      // copy the fields that the JNI uses
      arg.javaArray = arg.objArrayBuffer;
      arg.numElements = objArraySize;
      arg.sizeInBytes = totalBufferSize;

      for(int j = 0; j < objArraySize; j++){
         int sizeWritten = 0;

         Object object = UnsafeWrapper.getObject(newRef, arrayBaseOffset + arrayScale * j);
         for(int i = 0; i < c.getStructMemberTypes().size(); i++){
            TypeSpec t = c.getStructMemberTypes().get(i);
            long offset = c.getStructMemberOffsets().get(i);

            if(logger.isLoggable(Level.FINEST)){
               logger.finest("name = " + c.getStructMembers().get(i).getNameAndTypeEntry().getNameUTF8Entry().getUTF8() + " t= "
                     + t);
            }

            switch(t){
               case I:{
                  int x = UnsafeWrapper.getInt(object, offset);
                  arg.objArrayByteBuffer.putInt(x);
                  sizeWritten += t.getPrimitiveType().getJavaBytes();
                  break;
               }
               case F:{
                  float x = UnsafeWrapper.getFloat(object, offset);
                  arg.objArrayByteBuffer.putFloat(x);
                  sizeWritten += t.getPrimitiveType().getJavaBytes();
                  break;
               }
               case L:{
                  long x = UnsafeWrapper.getLong(object, offset);
                  arg.objArrayByteBuffer.putLong(x);
                  sizeWritten += t.getPrimitiveType().getJavaBytes();
                  break;
               }
               case Z:{
                  boolean x = UnsafeWrapper.getBoolean(object, offset);
                  arg.objArrayByteBuffer.put(x == true ? (byte) 1 : (byte) 0);
                  // Booleans converted to 1 byte C chars for opencl
                  sizeWritten += t.getPrimitiveType().getJavaBytes();
                  break;
               }
               case B:{
                  byte x = UnsafeWrapper.getByte(object, offset);
                  arg.objArrayByteBuffer.put(x);
                  sizeWritten += t.getPrimitiveType().getJavaBytes();
                  break;
               }
               case D:{
                  throw new AparapiException("Double not implemented yet");
               }
               default:
                  assert true == false : "typespec did not match anything";
                  throw new AparapiException("Unhandled prefix in buffer conversion");
            }
         }

         // add padding here if needed
         if(logger.isLoggable(Level.FINEST)){
            logger.finest("sizeWritten = " + sizeWritten + " totalStructSize= " + totalStructSize);
         }

         assert sizeWritten <= totalStructSize : "wrote too much into buffer";

         while(sizeWritten < totalStructSize){
            if(logger.isLoggable(Level.FINEST)){
               logger.finest(arg.name + " struct pad byte = " + sizeWritten + " totalStructSize= " + totalStructSize);
            }
            arg.objArrayByteBuffer.put((byte) -1);
            sizeWritten++;
         }
      }

      assert arg.objArrayByteBuffer.arrayOffset() == 0 : "should be zero";

      return didReallocate;
   }

   private void extractOopConversionBuffer(KernelArg arg) throws AparapiException{
      Class<?> arrayClass = null;

      if(lambdaKernelCall.isObjectLambda()){
         // Use the actual prefix of the object stream source array
         arrayClass = objectLambdaSourceArray.getClass();
      }else{
         arrayClass = arg.field.getType();
      }

      ClassModel c = arg.objArrayElementModel;
      assert c != null : "should find class for elements: " + arrayClass.getName();
      assert arg.array != null : "array is null";

      int arrayBaseOffset = UnsafeWrapper.arrayBaseOffset(arrayClass);
      int arrayScale = UnsafeWrapper.arrayIndexScale(arrayClass);
      if(logger.isLoggable(Level.FINEST)){
         logger.finest("Syncing field:" + arg.name + ", bb=" + arg.objArrayByteBuffer + ", prefix = " + arrayClass);
      }

      int objArraySize = 0;
      try{
         objArraySize = Array.getLength(arg.field.get(arg.fieldHolder));
      }catch(IllegalAccessException e){
         throw new AparapiException(e);
      }

      assert objArraySize > 0 : "should be > 0";

      int totalStructSize = c.getTotalStructSize();

      arg.objArrayByteBuffer.rewind();

      for(int j = 0; j < objArraySize; j++){
         int sizeWritten = 0;
         Object object = UnsafeWrapper.getObject(arg.array, arrayBaseOffset + arrayScale * j);
         for(int i = 0; i < c.getStructMemberTypes().size(); i++){
            TypeSpec t = c.getStructMemberTypes().get(i);
            long offset = c.getStructMemberOffsets().get(i);
            switch(t){
               case I:{
                  // read int value from buffer and array_store into obj in the array
                  int x = arg.objArrayByteBuffer.getInt();
                  if(logger.isLoggable(Level.FINEST)){
                     logger.finest("fType = " + t.getPrimitiveType().getJavaTypeName() + " x= " + x);
                  }
                  UnsafeWrapper.putInt(object, offset, x);
                  sizeWritten += t.getPrimitiveType().getJavaBytes();
                  break;
               }
               case F:{
                  float x = arg.objArrayByteBuffer.getFloat();
                  if(logger.isLoggable(Level.FINEST)){
                     logger.finest("fType = " + t.getPrimitiveType().getJavaTypeName() + " x= " + x);
                  }
                  UnsafeWrapper.putFloat(object, offset, x);
                  sizeWritten += t.getPrimitiveType().getJavaBytes();
                  break;
               }
               case L:{
                  long x = arg.objArrayByteBuffer.getLong();
                  if(logger.isLoggable(Level.FINEST)){
                     logger.finest("fType = " + t.getPrimitiveType().getJavaTypeName() + " x= " + x);
                  }
                  UnsafeWrapper.putLong(object, offset, x);
                  sizeWritten += t.getPrimitiveType().getJavaBytes();
                  break;
               }
               case Z:{
                  byte x = arg.objArrayByteBuffer.get();
                  if(logger.isLoggable(Level.FINEST)){
                     logger.finest("fType = " + t.getPrimitiveType().getJavaTypeName() + " x= " + x);
                  }
                  UnsafeWrapper.putBoolean(object, offset, (x == 1 ? true : false));
                  // Booleans converted to 1 byte C chars for open cl
                  sizeWritten += t.getPrimitiveType().getJavaBytes();
                  break;
               }
               case B:{
                  byte x = arg.objArrayByteBuffer.get();
                  if(logger.isLoggable(Level.FINEST)){
                     logger.finest("fType = " + t.getPrimitiveType().getJavaTypeName() + " x= " + x);
                  }
                  UnsafeWrapper.putByte(object, offset, x);
                  sizeWritten += t.getPrimitiveType().getJavaBytes();
                  break;
               }
               case D:{
                  throw new AparapiException("Double not implemented yet");
               }
               default:
                  assert true == false : "typespec did not match anything";
                  throw new AparapiException("Unhandled prefix in buffer conversion");
            }
         }

         // add padding here if needed
         if(logger.isLoggable(Level.FINEST)){
            logger.finest("sizeWritten = " + sizeWritten + " totalStructSize= " + totalStructSize);
         }

         assert sizeWritten <= totalStructSize : "wrote too much into buffer";

         while(sizeWritten < totalStructSize){
            // skip over pad bytes
            arg.objArrayByteBuffer.get();
            sizeWritten++;
         }
      }
   }

   private void restoreObjects() throws AparapiException{
      for(int i = 0; i < argc; i++){
         KernelArg arg = args[i];
         if((arg.type & ARG_OBJ_ARRAY_STRUCT) != 0){
            extractOopConversionBuffer(arg);
         }
      }
   }

   private boolean updateKernelArrayRefs(Object lambdaObject) throws AparapiException{
      boolean needsSync = false;

      for(int i = 0; i < argc; i++){
         KernelArg arg = args[i];
         try{
            if((arg.type & ARG_ARRAY) != 0){
               Object newArrayRef;
               newArrayRef = arg.field.get(arg.fieldHolder);

               if(newArrayRef == null){
                  throw new IllegalStateException("Cannot send null refs to kernel, reverting to java");
               }

               if((arg.type & ARG_OBJ_ARRAY_STRUCT) != 0){
                  prepareOopConversionBuffer(arg);
               }else{
                  // set up JNI fields for normal arrays
                  arg.javaArray = newArrayRef;
                  arg.numElements = Array.getLength(newArrayRef);
                  arg.sizeInBytes = arg.numElements * arg.primitiveSize;

                  //                  if (((args[i].prefix & ARG_EXPLICIT) != 0) && puts.contains(newArrayRef)) {
                  //                     args[i].prefix |= ARG_EXPLICIT_WRITE;
                  //                     // System.out.println("detected an explicit write " + args[i].name);
                  //                     puts.remove(newArrayRef);
                  //                  }
               }
               if(newArrayRef != arg.array){
                  needsSync = true;
                  if(logger.isLoggable(Level.FINE)){
                     logger.fine("saw newArrayRef for " + arg.name + " = " + newArrayRef + ", newArrayLen = "
                           + Array.getLength(newArrayRef));
                  }
               }
               arg.array = newArrayRef;
            }
         }catch(IllegalArgumentException e){
            e.printStackTrace();
         }catch(IllegalAccessException e){
            e.printStackTrace();
         }
      }
      return needsSync;
   }


   //   /**
   //    * There is a new Block for each invocation of the lambda
   //    * @param callerBlock
   //    */
   //   private void updateCallerBlockParams(Object callerBlock) {
   //      currentCallerBlock = callerBlock;
   //      for(int i=0; i<argc; i++) {
   //         if (args[i].fieldHolder instanceof IntConsumer) {
   //            if (logger.isLoggable(Level.FINE)) {
   //               logger.fine("Updated Block for " + args[i].name + " old: " + args[i].fieldHolder + " new: " + callerBlock);
   //            }
   //            args[i].fieldHolder = callerBlock;
   //         }
   //      }
   //   }

   private LambdaRunner executeOpenCL(Object lambdaObject, Object callerBlock, final Range _range, final int _passes) throws AparapiException{
      assert args != null : "args should not be null";

      // Read the array refs after kernel may have changed them
      // We need to do this as input to computing the localSize
      boolean needSync = updateKernelArrayRefs(lambdaObject);
      if(needSync && logger.isLoggable(Level.FINE)){
         logger.fine("Need to resync arrays on " + lambdaObject.getClass().getName());
      }

      // The arguments are extracted from the block. It is a new block object
      // each time. The captured refs could be arrays in the lambda this object.
      // This could probably be improved so it is not continually updating
      // the same arrays refs from the lambda this object.
      updateLambdaBlockJNI(jniContextHandle, callerBlock, lambdaKernelCall.getLambdaCapturedFields().length);

      // native side will reallocate array buffers if necessary
      if(runJNI(jniContextHandle, _range, needSync, _passes) != 0){
         logger.warning("### CL exec seems to have failed. Trying to revert to Java ###");
         throw new AparapiException("CL exec seems to have failed. Trying to revert to Java");
      }

      if(usesOopConversion == true){
         restoreObjects();
      }

      if(logger.isLoggable(Level.FINEST)){
         logger.finest("executeOpenCL completed. " + _range);
      }
      return this;
   }

   /**
    * This is simply used to pass the iteration variable to the kernel
    */
   final int iterationVariable = 0;
   Object[] objectLambdaSourceArray;


   KernelArg prepareOneArg(Field field, Object holder){
      KernelArg currArg = new KernelArg();

      currArg.fieldHolder = holder;
      currArg.name = field.getName();
      currArg.field = field;
      if((field.getModifiers() & Modifier.STATIC) == Modifier.STATIC){
         currArg.type |= ARG_STATIC;
      }

      Class<?> type = field.getType();
      if(type.isArray()){
         currArg.type |= ARG_GLOBAL;
         currArg.array = null; // will get updated in updateKernelArrayRefs
         currArg.type |= ARG_ARRAY;

         // For Aparapi/Sumatra demos treat all arrays as read/write
         currArg.type |= (ARG_WRITE | ARG_READ);

         currArg.type |= type.isAssignableFrom(float[].class) ? ARG_FLOAT : 0;

         currArg.type |= type.isAssignableFrom(int[].class) ? ARG_INT : 0;

         currArg.type |= type.isAssignableFrom(boolean[].class) ? ARG_BOOLEAN : 0;

         currArg.type |= type.isAssignableFrom(byte[].class) ? ARG_BYTE : 0;

         currArg.type |= type.isAssignableFrom(char[].class) ? ARG_CHAR : 0;

         currArg.type |= type.isAssignableFrom(double[].class) ? ARG_DOUBLE : 0;

         currArg.type |= type.isAssignableFrom(long[].class) ? ARG_LONG : 0;

         currArg.type |= type.isAssignableFrom(short[].class) ? ARG_SHORT : 0;

         // arrays whose length is used will have an int arg holding
         // the length as a kernel param
         if(entryPoint.getArrayFieldArrayLengthUsed().contains(currArg.name)){
            currArg.type |= ARG_ARRAYLENGTH;
         }

         if(type.getName().startsWith("[L")){
            currArg.type |= (ARG_OBJ_ARRAY_STRUCT | ARG_WRITE | ARG_READ);
            if(logger.isLoggable(Level.FINE)){
               logger.fine("tagging " + currArg.name + " as (ARG_OBJ_ARRAY_STRUCT | ARG_WRITE | ARG_READ)");
            }
         }
      }else if(type.isAssignableFrom(float.class)){
         currArg.type |= ARG_PRIMITIVE;
         currArg.type |= ARG_FLOAT;
      }else if(type.isAssignableFrom(int.class)){
         currArg.type |= ARG_PRIMITIVE;
         currArg.type |= ARG_INT;
      }else if(type.isAssignableFrom(double.class)){
         currArg.type |= ARG_PRIMITIVE;
         currArg.type |= ARG_DOUBLE;
      }else if(type.isAssignableFrom(long.class)){
         currArg.type |= ARG_PRIMITIVE;
         currArg.type |= ARG_LONG;
      }else if(type.isAssignableFrom(boolean.class)){
         currArg.type |= ARG_PRIMITIVE;
         currArg.type |= ARG_BOOLEAN;
      }else if(type.isAssignableFrom(byte.class)){
         currArg.type |= ARG_PRIMITIVE;
         currArg.type |= ARG_BYTE;
      }else if(type.isAssignableFrom(char.class)){
         currArg.type |= ARG_PRIMITIVE;
         currArg.type |= ARG_CHAR;
      }else if(type.isAssignableFrom(short.class)){
         currArg.type |= ARG_PRIMITIVE;
         currArg.type |= ARG_SHORT;
      }

      currArg.primitiveSize = ((currArg.type & ARG_FLOAT) != 0 ? 4 : (currArg.type & ARG_INT) != 0 ? 4
            : (currArg.type & ARG_BYTE) != 0 ? 1 : (currArg.type & ARG_CHAR) != 0 ? 2
            : (currArg.type & ARG_BOOLEAN) != 0 ? 1 : (currArg.type & ARG_SHORT) != 0 ? 2
            : (currArg.type & ARG_LONG) != 0 ? 8 : (currArg.type & ARG_DOUBLE) != 0 ? 8 : 0);

      if(logger.isLoggable(Level.FINE)){
         logger.fine("prepareOneArg : " + currArg.name + ", prefix=" + Integer.toHexString(currArg.type)
               + ", primitiveSize=" + currArg.primitiveSize);
      }

      return currArg;
   }


   KernelArg[] prepareLambdaArgs(LambdaKernelCall call, Object callerBlock){
      Object lambdaObject = call.getLambdaKernelThis();
      Field[] callerCapturedFields = call.getLambdaCapturedFields();
      List<KernelArg> argsList = new ArrayList<KernelArg>();

      // Add fields in this order:
      // 1. captured args from block, including local arrays in caller method
      // 1b. Source array if this is an object stream
      // 2. iteration variable,
      // 3. field references from lambda's object
      try{

         for(Field field : callerCapturedFields){
            field.setAccessible(true);
            argsList.add(prepareOneArg(field, callerBlock));
         }

         if(call.isObjectLambda()){
            argsList.add(prepareOneArg(this.getClass().getDeclaredField("objectLambdaSourceArray"), this));
         }

         argsList.add(prepareOneArg(this.getClass().getDeclaredField("iterationVariable"), this));

         for(Field field : entryPoint.getReferencedFields()){
            field.setAccessible(true);
            argsList.add(prepareOneArg(field, lambdaObject));
         }

      }catch(Exception e){
         e.printStackTrace();
         return null;
      }
      return argsList.toArray(new KernelArg[0]);
   }

   // Hope for the best!
   boolean runnable = true;

   public void setRunnable(boolean b){
      runnable = b;
   }

   public boolean getRunnable(){
      return runnable;
   }


   synchronized boolean execute(Object callerBlock, final Range _range, final int _passes) throws AparapiException{

      long executeStartTime = System.currentTimeMillis();

      if(_range == null){
         throw new IllegalStateException("range can't be null");
      }

      if(entryPoint == null){
         assert lambdaKernelCall != null : "Should not be null";

         Class lambdaClass = lambdaKernelCall.getLambdaKernelClass();
         ClassModel classModel = ClassModel.getClassModel(lambdaClass);

         entryPoint = classModel.getLambdaEntrypoint(lambdaKernelCall.getLambdaMethodName(),
               lambdaKernelCall.getLambdaMethodSignature(), lambdaKernelCall.getLambdaKernelThis());

         if((entryPoint != null) && !entryPoint.shouldFallback()){
            synchronized(KernelRunner.class){ // This seems to be needed because of a race condition uncovered with issue #68 http://code.google.com/p/aparapi/issues/detail?id=68
               OpenCLDevice openCLDevice = null;
               int jniFlags = 0;
               // We used to treat as before by getting first GPU device
               // now we get the best GPU
               openCLDevice = (OpenCLDevice) ((Config.executionMode != null && Config.executionMode.equals("CPU")) ? OpenCLDevice.firstCPU() : OpenCLDevice.best());
               assert openCLDevice != null : "Device should not be null";
               jniFlags |= JNI_FLAG_USE_GPU; // this flag might be redundant now.
               jniFlags |= JNI_FLAG_LAMBDA_KERNEL;
               jniContextHandle = initJNI(lambdaKernelCall.isStatic() == true ?
                     lambdaKernelCall.getLambdaKernelClass() : lambdaKernelCall.getLambdaKernelThis(),
                     openCLDevice, jniFlags);
            }

            if(jniContextHandle == 0){
               throw new AparapiException("Can't create JNI context");
            }

            String extensions = getExtensionsJNI(jniContextHandle);
            capabilitiesSet = new HashSet<String>();

            StringTokenizer strTok = new StringTokenizer(extensions);
            while(strTok.hasMoreTokens()){
               capabilitiesSet.add(strTok.nextToken());
            }

            if(logger.isLoggable(Level.FINE)){
               logger.fine("Capabilities initialized to :" + capabilitiesSet.toString());
            }

            if(entryPoint.requiresDoublePragma() && !hasFP64Support()){
               throw new AparapiException("FP64 required but not supported");
            }

            if(entryPoint.requiresByteAddressableStorePragma() && !hasByteAddressableStoreSupport()){
               throw new AparapiException("Byte addressable stores required but not supported");
            }

            boolean all32AtomicsAvailable = hasGlobalInt32BaseAtomicsSupport() && hasGlobalInt32ExtendedAtomicsSupport()
                  && hasLocalInt32BaseAtomicsSupport() && hasLocalInt32ExtendedAtomicsSupport();

            if(entryPoint.requiresAtomic32Pragma() && !all32AtomicsAvailable){
               throw new AparapiException("32 bit Atomics required but not supported");
            }

            int paramCount = lambdaKernelCall.getLambdaCapturedFields().length;
            entryPoint.setLambdaActualParamsCount(paramCount);

            String openCL = null;
            openCL = OpenCLKernelWriter.writeToString(entryPoint);

            if(Config.enableShowGeneratedOpenCL){
               System.out.println(openCL);
            }

            if(logger.isLoggable(Level.INFO)){
               logger.info(openCL);
            }

            // Send the string to OpenCL to compile it
            if(buildProgramJNI(jniContextHandle, openCL) == 0){
               throw new AparapiException("OpenCL compile failed");
            }

            args = prepareLambdaArgs(lambdaKernelCall, callerBlock);

            argc = args.length;

            setArgsJNI(jniContextHandle, args, argc);

            conversionTime = System.currentTimeMillis() - executeStartTime;

            executeOpenCL(lambdaKernelCall.isStatic() == true ?
                  lambdaKernelCall.getLambdaKernelClass() : lambdaKernelCall.getLambdaKernelThis(), callerBlock, _range, _passes);

            if(logger.isLoggable(Level.INFO)){
               logger.info("First run done. ");
            }

         }else{
            throw new AparapiException("failed to locate entrypoint");
         }

      }else{
         executeOpenCL(lambdaKernelCall.isStatic() == true ?
               lambdaKernelCall.getLambdaKernelClass() : lambdaKernelCall.getLambdaKernelThis(), callerBlock, _range, _passes);
      }

      executionTime = System.currentTimeMillis() - executeStartTime;
      accumulatedExecutionTime += executionTime;

      return true;
   }

   synchronized boolean execute(Object callerBlock, final int _passes) throws AparapiException{
      execute(callerBlock, Range.create(objectLambdaSourceArray.length), _passes);
      return true;
   }

   private Set<Object> puts = new HashSet<Object>();

   private native int getJNI(long _jniContextHandle, Object _array);


}
