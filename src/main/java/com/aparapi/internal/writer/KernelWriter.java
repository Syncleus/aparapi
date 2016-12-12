/**
 * Copyright (c) 2016 - 2017 Syncleus, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
package com.aparapi.internal.writer;

import com.aparapi.Config;
import com.aparapi.Kernel;
import com.aparapi.internal.exception.ClassParseException;
import com.aparapi.internal.exception.CodeGenException;
import com.aparapi.internal.instruction.Instruction;
import com.aparapi.internal.instruction.InstructionSet.*;
import com.aparapi.internal.model.ClassModel;
import com.aparapi.internal.model.ClassModel.AttributePool.RuntimeAnnotationsEntry;
import com.aparapi.internal.model.ClassModel.AttributePool.RuntimeAnnotationsEntry.AnnotationInfo;
import com.aparapi.internal.model.ClassModel.ClassModelField;
import com.aparapi.internal.model.ClassModel.ConstantPool.FieldEntry;
import com.aparapi.internal.model.ClassModel.ConstantPool.MethodEntry;
import com.aparapi.internal.model.ClassModel.LocalVariableInfo;
import com.aparapi.internal.model.ClassModel.LocalVariableTableEntry;
import com.aparapi.internal.model.Entrypoint;
import com.aparapi.internal.model.MethodModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class KernelWriter extends BlockWriter{

   private final String cvtBooleanToChar = "char ";

   private final String cvtBooleanArrayToCharStar = "char* ";

   private final String cvtByteToChar = "char ";

   private final String cvtByteArrayToCharStar = "char* ";

   private final String cvtCharToShort = "unsigned short ";

   private final String cvtCharArrayToShortStar = "unsigned short* ";

   private final String cvtIntArrayToIntStar = "int* ";

   private final String cvtFloatArrayToFloatStar = "float* ";

   private final String cvtDoubleArrayToDoubleStar = "double* ";

   private final String cvtLongArrayToLongStar = "long* ";

   private final String cvtShortArrayToShortStar = "short* ";

   /** When declaring a __private struct pointer field, we always omit the "__private" qualifier. This is because the NVidia OpenCL compiler, at time of writing
    * erroneously complains about explicitly qualifying pointers with __private ("error: field may not be qualified with an address space").
    */
   private static final boolean IMPLICIT_PRIVATE_FIELDS = true;

   // private static Logger logger = Logger.getLogger(Config.getLoggerName());

   private Entrypoint entryPoint = null;

   public final static Map<String, String> javaToCLIdentifierMap = new HashMap<>();
   static {
      javaToCLIdentifierMap.put("getGlobalId()I", "get_global_id(0)");
      javaToCLIdentifierMap.put("getGlobalId(I)I", "get_global_id"); // no parenthesis if we are conveying args
      javaToCLIdentifierMap.put("getGlobalX()I", "get_global_id(0)");
      javaToCLIdentifierMap.put("getGlobalY()I", "get_global_id(1)");
      javaToCLIdentifierMap.put("getGlobalZ()I", "get_global_id(2)");

      javaToCLIdentifierMap.put("getGlobalSize()I", "get_global_size(0)");
      javaToCLIdentifierMap.put("getGlobalSize(I)I", "get_global_size"); // no parenthesis if we are conveying args
      javaToCLIdentifierMap.put("getGlobalWidth()I", "get_global_size(0)");
      javaToCLIdentifierMap.put("getGlobalHeight()I", "get_global_size(1)");
      javaToCLIdentifierMap.put("getGlobalDepth()I", "get_global_size(2)");

      javaToCLIdentifierMap.put("getLocalId()I", "get_local_id(0)");
      javaToCLIdentifierMap.put("getLocalId(I)I", "get_local_id"); // no parenthesis if we are conveying args
      javaToCLIdentifierMap.put("getLocalX()I", "get_local_id(0)");
      javaToCLIdentifierMap.put("getLocalY()I", "get_local_id(1)");
      javaToCLIdentifierMap.put("getLocalZ()I", "get_local_id(2)");

      javaToCLIdentifierMap.put("getLocalSize()I", "get_local_size(0)");
      javaToCLIdentifierMap.put("getLocalSize(I)I", "get_local_size"); // no parenthesis if we are conveying args
      javaToCLIdentifierMap.put("getLocalWidth()I", "get_local_size(0)");
      javaToCLIdentifierMap.put("getLocalHeight()I", "get_local_size(1)");
      javaToCLIdentifierMap.put("getLocalDepth()I", "get_local_size(2)");

      javaToCLIdentifierMap.put("getNumGroups()I", "get_num_groups(0)");
      javaToCLIdentifierMap.put("getNumGroups(I)I", "get_num_groups"); // no parenthesis if we are conveying args
      javaToCLIdentifierMap.put("getNumGroupsX()I", "get_num_groups(0)");
      javaToCLIdentifierMap.put("getNumGroupsY()I", "get_num_groups(1)");
      javaToCLIdentifierMap.put("getNumGroupsZ()I", "get_num_groups(2)");

      javaToCLIdentifierMap.put("getGroupId()I", "get_group_id(0)");
      javaToCLIdentifierMap.put("getGroupId(I)I", "get_group_id"); // no parenthesis if we are conveying args
      javaToCLIdentifierMap.put("getGroupX()I", "get_group_id(0)");
      javaToCLIdentifierMap.put("getGroupY()I", "get_group_id(1)");
      javaToCLIdentifierMap.put("getGroupZ()I", "get_group_id(2)");

      javaToCLIdentifierMap.put("getPassId()I", "get_pass_id(this)");

      javaToCLIdentifierMap.put("localBarrier()V", "barrier(CLK_LOCAL_MEM_FENCE)");

      javaToCLIdentifierMap.put("globalBarrier()V", "barrier(CLK_GLOBAL_MEM_FENCE)");
   }

   /**
    * These three convert functions are here to perform
    * any type conversion that may be required between
    * Java and OpenCL.
    * 
    * @param _typeDesc
    *          String in the Java JNI notation, [I, etc
    * @return Suitably converted string, "char*", etc
    */
   @Override public String convertType(String _typeDesc, boolean useClassModel) {
      switch (_typeDesc) {
         case "Z":
         case "boolean":
            return (cvtBooleanToChar);
         case "[Z":
         case "boolean[]":
            return (cvtBooleanArrayToCharStar);
         case "B":
         case "byte":
            return (cvtByteToChar);
         case "[B":
         case "byte[]":
            return (cvtByteArrayToCharStar);
         case "C":
         case "char":
            return (cvtCharToShort);
         case "[C":
         case "char[]":
            return (cvtCharArrayToShortStar);
         case "[I":
         case "int[]":
            return (cvtIntArrayToIntStar);
         case "[F":
         case "float[]":
            return (cvtFloatArrayToFloatStar);
         case "[D":
         case "double[]":
            return (cvtDoubleArrayToDoubleStar);
         case "[J":
         case "long[]":
            return (cvtLongArrayToLongStar);
         case "[S":
         case "short[]":
            return (cvtShortArrayToShortStar);
      }
      // if we get this far, we haven't matched anything yet
      if (useClassModel) {
         return (ClassModel.convert(_typeDesc, "", true));
      } else {
         return _typeDesc;
      }
   }

   @Override public void writeMethod(MethodCall _methodCall, MethodEntry _methodEntry) throws CodeGenException {
      final int argc = _methodEntry.getStackConsumeCount();

      final String methodName = _methodEntry.getNameAndTypeEntry().getNameUTF8Entry().getUTF8();
      final String methodSignature = _methodEntry.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8();

      final String barrierAndGetterMappings = javaToCLIdentifierMap.get(methodName + methodSignature);

      if (barrierAndGetterMappings != null) {
         // this is one of the OpenCL barrier or size getter methods
         // write the mapping and exit
         if (argc > 0) {
            write(barrierAndGetterMappings);
            write("(");
            for (int arg = 0; arg < argc; arg++) {
               if ((arg != 0)) {
                  write(", ");
               }
               writeInstruction(_methodCall.getArg(arg));
            }
            write(")");
         } else {
            write(barrierAndGetterMappings);
         }
      } else {
         final boolean isSpecial = _methodCall instanceof I_INVOKESPECIAL;
         MethodModel m = entryPoint.getCallTarget(_methodEntry, isSpecial);

         FieldEntry getterField = null;
         if (m != null && m.isGetter()) {
            getterField = m.getAccessorVariableFieldEntry();
         }
         if (getterField != null && isThis(_methodCall.getArg(0))) {
            String fieldName = getterField.getNameAndTypeEntry().getNameUTF8Entry().getUTF8();
            write("this->");
            write(fieldName);
            return;
         }
         boolean noCL = _methodEntry.getOwnerClassModel().getNoCLMethods()
               .contains(_methodEntry.getNameAndTypeEntry().getNameUTF8Entry().getUTF8());
         if (noCL) {
            return;
         }
         final String intrinsicMapping = Kernel.getMappedMethodName(_methodEntry);
         // System.out.println("getMappedMethodName for " + methodName + " returned " + mapping);
         boolean isIntrinsic = false;

         if (intrinsicMapping == null) {
            assert entryPoint != null : "entryPoint should not be null";
            boolean isMapped = Kernel.isMappedMethod(_methodEntry);

            if (m != null) {
               write(m.getName());
            } else {
               // Must be a library call like rsqrt
               assert isMapped : _methodEntry + " should be mapped method!";
               write(methodName);
               isIntrinsic = true;
            }
         } else {
            write(intrinsicMapping);
         }

         write("(");

         if ((intrinsicMapping == null) && (_methodCall instanceof VirtualMethodCall) && (!isIntrinsic)) {

            final Instruction i = ((VirtualMethodCall) _methodCall).getInstanceReference();

            if (i instanceof I_ALOAD_0) {
               write("this");
            } else if (i instanceof AccessArrayElement) {
               final AccessArrayElement arrayAccess = (AccessArrayElement) ((VirtualMethodCall) _methodCall).getInstanceReference();
               final Instruction refAccess = arrayAccess.getArrayRef();
               //assert refAccess instanceof I_GETFIELD : "ref should come from getfield";
               final String fieldName = ((AccessField) refAccess).getConstantPoolFieldEntry().getNameAndTypeEntry()
                     .getNameUTF8Entry().getUTF8();
               write(" &(this->" + fieldName);
               write("[");
               writeInstruction(arrayAccess.getArrayIndex());
               write("])");
            } else {
               assert false : "unhandled call from: " + i;
            }
         }
         for (int arg = 0; arg < argc; arg++) {
            if (((intrinsicMapping == null) && (_methodCall instanceof VirtualMethodCall) && (!isIntrinsic)) || (arg != 0)) {
               write(", ");
            }
            writeInstruction(_methodCall.getArg(arg));
         }
         write(")");
      }
   }

   private boolean isThis(Instruction instruction) {
      return instruction instanceof I_ALOAD_0;
   }

   public void writePragma(String _name, boolean _enable) {
      write("#pragma OPENCL EXTENSION " + _name + " : " + (_enable ? "en" : "dis") + "able");
      newLine();
   }

   public final static String __local = "__local";

   public final static String __global = "__global";

   public final static String __constant = "__constant";

   public final static String __private = "__private";

   public final static String LOCAL_ANNOTATION_NAME = "L" + com.aparapi.Kernel.Local.class.getName().replace('.', '/') + ";";

   public final static String CONSTANT_ANNOTATION_NAME = "L" + com.aparapi.Kernel.Constant.class.getName().replace('.', '/')
         + ";";

   @Override public void write(Entrypoint _entryPoint) throws CodeGenException {
      final List<String> thisStruct = new ArrayList<>();
      final List<String> argLines = new ArrayList<>();
      final List<String> assigns = new ArrayList<>();

      entryPoint = _entryPoint;

      for (final ClassModelField field : _entryPoint.getReferencedClassModelFields()) {
         // Field field = _entryPoint.getClassModel().getField(f.getName());
         final StringBuilder thisStructLine = new StringBuilder();
         final StringBuilder argLine = new StringBuilder();
         final StringBuilder assignLine = new StringBuilder();

         String signature = field.getDescriptor();

         boolean isPointer = false;

         int numDimensions = 0;

         // check the suffix

         String type = field.getName().endsWith(Kernel.LOCAL_SUFFIX) ? __local
               : (field.getName().endsWith(Kernel.CONSTANT_SUFFIX) ? __constant : __global);
         Integer privateMemorySize = null;
         try {
            privateMemorySize = _entryPoint.getClassModel().getPrivateMemorySize(field.getName());
         } catch (ClassParseException e) {
            throw new CodeGenException(e);
         }

         if (privateMemorySize != null) {
            type = __private;
         }
         final RuntimeAnnotationsEntry visibleAnnotations = field.getAttributePool().getRuntimeVisibleAnnotationsEntry();

         if (visibleAnnotations != null) {
            for (final AnnotationInfo ai : visibleAnnotations) {
               final String typeDescriptor = ai.getTypeDescriptor();
               if (typeDescriptor.equals(LOCAL_ANNOTATION_NAME)) {
                  type = __local;
               } else if (typeDescriptor.equals(CONSTANT_ANNOTATION_NAME)) {
                  type = __constant;
               }
            }
         }

         String argType = (__private.equals(type)) ? __constant : type;

         //if we have a an array we want to mark the object as a pointer
         //if we have a multiple dimensional array we want to remember the number of dimensions
         while (signature.startsWith("[")) {
            if (isPointer == false) {
               argLine.append(argType).append(" ");
               if (!(type.equals(__private) && IMPLICIT_PRIVATE_FIELDS)) {
                  thisStructLine.append(type).append(" ");
               }
            }
            isPointer = true;
            numDimensions++;
            signature = signature.substring(1);
         }

         // If it is a converted array of objects, emit the struct param
         String className = null;
         if (signature.startsWith("L")) {
            // Turn Lcom/aparapi/javalabs/opencl/demo/DummyOOA; into com_amd_javalabs_opencl_demo_DummyOOA for example
            className = (signature.substring(1, signature.length() - 1)).replace('/', '_');
            // if (logger.isLoggable(Level.FINE)) {
            // logger.fine("Examining object parameter: " + signature + " new: " + className);
            // }
            argLine.append(className);
            thisStructLine.append(className);
         } else {
            argLine.append(convertType(ClassModel.typeName(signature.charAt(0)), false));
            thisStructLine.append(convertType(ClassModel.typeName(signature.charAt(0)), false));
         }

         argLine.append(" ");
         thisStructLine.append(" ");

         if (isPointer) {
            argLine.append("*");
            if (privateMemorySize == null) {
               thisStructLine.append("*");
            }
         }

         if (privateMemorySize == null) {
            assignLine.append("this->");
            assignLine.append(field.getName());
            assignLine.append(" = ");
            assignLine.append(field.getName());
         }

         argLine.append(field.getName());
         thisStructLine.append(field.getName());
         if (privateMemorySize == null) {
            assigns.add(assignLine.toString());
         }
         argLines.add(argLine.toString());
         if (privateMemorySize != null) {
            thisStructLine.append("[").append(privateMemorySize).append("]");
         }
         thisStruct.add(thisStructLine.toString());

         // Add int field into "this" struct for supporting java arraylength op
         // named like foo__javaArrayLength
         if (isPointer && _entryPoint.getArrayFieldArrayLengthUsed().contains(field.getName()) || isPointer && numDimensions > 1) {

            for (int i = 0; i < numDimensions; i++) {
               final StringBuilder lenStructLine = new StringBuilder();
               final StringBuilder lenArgLine = new StringBuilder();
               final StringBuilder lenAssignLine = new StringBuilder();

               String suffix = numDimensions == 1 ? "" : Integer.toString(i);
               String lenName = field.getName() + BlockWriter.arrayLengthMangleSuffix + suffix;

               lenStructLine.append("int ").append(lenName);

               lenAssignLine.append("this->");
               lenAssignLine.append(lenName);
               lenAssignLine.append(" = ");
               lenAssignLine.append(lenName);

               lenArgLine.append("int ").append(lenName);

               assigns.add(lenAssignLine.toString());
               argLines.add(lenArgLine.toString());
               thisStruct.add(lenStructLine.toString());

               if (numDimensions > 1) {
                  final StringBuilder dimStructLine = new StringBuilder();
                  final StringBuilder dimArgLine = new StringBuilder();
                  final StringBuilder dimAssignLine = new StringBuilder();
                  String dimName = field.getName() + BlockWriter.arrayDimMangleSuffix + suffix;

                  dimStructLine.append("int ").append(dimName);

                  dimAssignLine.append("this->");
                  dimAssignLine.append(dimName);
                  dimAssignLine.append(" = ");
                  dimAssignLine.append(dimName);

                  dimArgLine.append("int ").append(dimName);

                  assigns.add(dimAssignLine.toString());
                  argLines.add(dimArgLine.toString());
                  thisStruct.add(dimStructLine.toString());
               }
            }
         }
      }

      if (Config.enableByteWrites || _entryPoint.requiresByteAddressableStorePragma()) {
         // Starting with OpenCL 1.1 (which is as far back as we support)
         // this feature is part of the core, so we no longer need this pragma
         if (false) {
            writePragma("cl_khr_byte_addressable_store", true);
            newLine();
         }
      }

      boolean usesAtomics = false;
      if (Config.enableAtomic32 || _entryPoint.requiresAtomic32Pragma()) {
         usesAtomics = true;
         writePragma("cl_khr_global_int32_base_atomics", true);
         writePragma("cl_khr_global_int32_extended_atomics", true);
         writePragma("cl_khr_local_int32_base_atomics", true);
         writePragma("cl_khr_local_int32_extended_atomics", true);
      }

      if (Config.enableAtomic64 || _entryPoint.requiresAtomic64Pragma()) {
         usesAtomics = true;
         writePragma("cl_khr_int64_base_atomics", true);
         writePragma("cl_khr_int64_extended_atomics", true);
      }

      if (usesAtomics) {
         write("int atomicAdd(__global int *_arr, int _index, int _delta){");
         in();
         {
            newLine();
            write("return atomic_add(&_arr[_index], _delta);");
            out();
            newLine();
         }
         write("}");

         newLine();
      }

      if (Config.enableDoubles || _entryPoint.requiresDoublePragma()) {
         writePragma("cl_khr_fp64", true);
         newLine();
      }

      // Emit structs for oop transformation accessors
      for (final ClassModel cm : _entryPoint.getObjectArrayFieldsClasses().values()) {
         final ArrayList<FieldEntry> fieldSet = cm.getStructMembers();
         if (fieldSet.size() > 0) {
            final String mangledClassName = cm.getClassWeAreModelling().getName().replace('.', '_');
            newLine();
            write("typedef struct " + mangledClassName + "_s{");
            in();
            newLine();

            int totalSize = 0;
            int alignTo = 0;

            for (FieldEntry field : fieldSet) {
               final String fType = field.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8();
               final int fSize = TypeSpec.valueOf(fType.equals("Z") ? "B" : fType).getSize();

               if (fSize > alignTo) {
                  alignTo = fSize;
               }
               totalSize += fSize;

               final String cType = convertType(field.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8(), true);
               assert cType != null : "could not find type for " + field.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8();
               writeln(cType + " " + field.getNameAndTypeEntry().getNameUTF8Entry().getUTF8() + ";");
            }

            // compute total size for OpenCL buffer
            int totalStructSize = 0;
            if ((totalSize % alignTo) == 0) {
               totalStructSize = totalSize;
            } else {
               // Pad up if necessary
               totalStructSize = ((totalSize / alignTo) + 1) * alignTo;
            }
            if (totalStructSize > alignTo) {
               while (totalSize < totalStructSize) {
                  // structBuffer.put((byte)-1);
                  writeln("char _pad_" + totalSize + ";");
                  totalSize++;
               }
            }

            out();
            newLine();
            write("} " + mangledClassName + ";");
            newLine();
         }
      }

      write("typedef struct This_s{");

      in();
      newLine();
      for (final String line : thisStruct) {
         write(line);
         writeln(";");
      }
      write("int passid");
      out();
      writeln(";");
      // out();
      // newLine();
      write("}This;");
      newLine();
      write("int get_pass_id(This *this){");
      in();
      {
         newLine();
         write("return this->passid;");
         out();
         newLine();
      }
      write("}");
      newLine();

      for (final MethodModel mm : _entryPoint.getCalledMethods()) {
         // write declaration :)
         if (mm.isPrivateMemoryGetter()) {
            continue;
         }

         final String returnType = mm.getReturnType();
         // Arrays always map to __private or__global arrays
         if (returnType.startsWith("[")) {
            write(" __global ");
         }
         write(convertType(returnType, true));

         write(mm.getName() + "(");

         if (!mm.getMethod().isStatic()) {
            if ((mm.getMethod().getClassModel() == _entryPoint.getClassModel())
                  || mm.getMethod().getClassModel().isSuperClass(_entryPoint.getClassModel().getClassWeAreModelling())) {
               write("This *this");
            } else {
               // Call to an object member or superclass of member
               for (final ClassModel c : _entryPoint.getObjectArrayFieldsClasses().values()) {
                  if (mm.getMethod().getClassModel() == c) {
                     write("__global " + mm.getMethod().getClassModel().getClassWeAreModelling().getName().replace('.', '_')
                           + " *this");
                     break;
                  } else if (mm.getMethod().getClassModel().isSuperClass(c.getClassWeAreModelling())) {
                     write("__global " + c.getClassWeAreModelling().getName().replace('.', '_') + " *this");
                     break;
                  }
               }
            }
         }

         boolean alreadyHasFirstArg = !mm.getMethod().isStatic();

         final LocalVariableTableEntry<LocalVariableInfo> lvte = mm.getLocalVariableTableEntry();
         for (final LocalVariableInfo lvi : lvte) {
            if ((lvi.getStart() == 0) && ((lvi.getVariableIndex() != 0) || mm.getMethod().isStatic())) { // full scope but skip this
               final String descriptor = lvi.getVariableDescriptor();
               if (alreadyHasFirstArg) {
                  write(", ");
               }

               // Arrays always map to __global arrays
               if (descriptor.startsWith("[")) {
                  write(" __global ");
               }

               write(convertType(descriptor, true));
               write(lvi.getVariableName());
               alreadyHasFirstArg = true;
            }
         }
         write(")");
         writeMethodBody(mm);
         newLine();
      }

      write("__kernel void " + _entryPoint.getMethodModel().getSimpleName() + "(");

      in();
      boolean first = true;
      for (final String line : argLines) {

         if (first) {
            first = false;
         } else {
            write(", ");
         }

         newLine();
         write(line);
      }

      if (first) {
         first = false;
      } else {
         write(", ");
      }
      newLine();
      write("int passid");
      out();
      newLine();
      write("){");
      in();
      newLine();
      writeln("This thisStruct;");
      writeln("This* this=&thisStruct;");
      for (final String line : assigns) {
         write(line);
         writeln(";");
      }
      write("this->passid = passid");
      writeln(";");

      writeMethodBody(_entryPoint.getMethodModel());
      out();
      newLine();
      writeln("}");
      out();
   }

   @Override public void writeThisRef() {
      write("this->");
   }

   @Override public void writeInstruction(Instruction _instruction) throws CodeGenException {
      if ((_instruction instanceof I_IUSHR) || (_instruction instanceof I_LUSHR)) {
         final BinaryOperator binaryInstruction = (BinaryOperator) _instruction;
         final Instruction parent = binaryInstruction.getParentExpr();
         boolean needsParenthesis = true;

         if (parent instanceof AssignToLocalVariable) {
            needsParenthesis = false;
         } else if (parent instanceof AssignToField) {
            needsParenthesis = false;
         } else if (parent instanceof AssignToArrayElement) {
            needsParenthesis = false;
         }
         if (needsParenthesis) {
            write("(");
         }

         if (binaryInstruction instanceof I_IUSHR) {
            write("((unsigned int)");
         } else {
            write("((unsigned long)");
         }
         writeInstruction(binaryInstruction.getLhs());
         write(")");
         write(" >> ");
         writeInstruction(binaryInstruction.getRhs());

         if (needsParenthesis) {
            write(")");
         }
      } else {
         super.writeInstruction(_instruction);
      }
   }

   public static String writeToString(Entrypoint _entrypoint) throws CodeGenException {
      final StringBuilder openCLStringBuilder = new StringBuilder();
      final KernelWriter openCLWriter = new KernelWriter(){
         @Override public void write(String _string) {
            openCLStringBuilder.append(_string);
         }
      };
      openCLWriter.write(_entrypoint);

      return (openCLStringBuilder.toString());
   }
}
