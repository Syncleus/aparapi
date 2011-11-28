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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.amd.aparapi.ClassModel.AttributePool.LocalVariableTableEntry;
import com.amd.aparapi.ClassModel.AttributePool.LocalVariableTableEntry.LocalVariableInfo;
import com.amd.aparapi.ClassModel.AttributePool.RuntimeAnnotationsEntry;
import com.amd.aparapi.ClassModel.ClassModelField;
import com.amd.aparapi.ClassModel.ConstantPool.FieldEntry;
import com.amd.aparapi.ClassModel.ConstantPool.MethodEntry;
import com.amd.aparapi.InstructionSet.AccessArrayElement;
import com.amd.aparapi.InstructionSet.AssignToArrayElement;
import com.amd.aparapi.InstructionSet.AssignToField;
import com.amd.aparapi.InstructionSet.AssignToLocalVariable;
import com.amd.aparapi.InstructionSet.BinaryOperator;
import com.amd.aparapi.InstructionSet.I_ALOAD_0;
import com.amd.aparapi.InstructionSet.I_GETFIELD;
import com.amd.aparapi.InstructionSet.I_INVOKESPECIAL;
import com.amd.aparapi.InstructionSet.I_IUSHR;
import com.amd.aparapi.InstructionSet.I_LUSHR;
import com.amd.aparapi.InstructionSet.MethodCall;
import com.amd.aparapi.InstructionSet.VirtualMethodCall;

abstract class KernelWriter extends BlockWriter{

   final String cvtBooleanToChar = "char ";

   final String cvtBooleanArrayToCharStar = "char* ";

   final String cvtByteToChar = "char ";

   final String cvtByteArrayToCharStar = "char* ";

   final String cvtCharToShort = "unsigned short ";

   final String cvtCharArrayToShortStar = "unsigned short* ";

   final String cvtIntArrayToIntStar = "int* ";

   final String cvtFloatArrayToFloatStar = "float* ";

   final String cvtDoubleArrayToDoubleStar = "double* ";

   final String cvtLongArrayToLongStar = "long* ";

   final String cvtShortArrayToShortStar = "short* ";

   // private static Logger logger = Logger.getLogger(Config.getLoggerName());

   Entrypoint entryPoint = null;

   final static Map<String, String> javaToCLIdentifierMap = new HashMap<String, String>();
   {
      javaToCLIdentifierMap.put("getGlobalId()I", "get_global_id(0)");
      javaToCLIdentifierMap.put("getGlobalSize()I", "get_global_size(0)");
      javaToCLIdentifierMap.put("getLocalId()I", "get_local_id(0)");
      javaToCLIdentifierMap.put("getLocalSize()I", "get_local_size(0)");
      javaToCLIdentifierMap.put("getNumGroups()I", "get_num_groups(0)");
      javaToCLIdentifierMap.put("getGroupId()I", "get_group_id(0)");
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
   @Override protected String convertType(String _typeDesc, boolean useClassModel) {
      if (_typeDesc.equals("Z") || _typeDesc.equals("boolean")) {
         return (cvtBooleanToChar);
      } else if (_typeDesc.equals("[Z") || _typeDesc.equals("boolean[]")) {
         return (cvtBooleanArrayToCharStar);
      } else if (_typeDesc.equals("B") || _typeDesc.equals("byte")) {
         return (cvtByteToChar);
      } else if (_typeDesc.equals("[B") || _typeDesc.equals("byte[]")) {
         return (cvtByteArrayToCharStar);
      } else if (_typeDesc.equals("C") || _typeDesc.equals("char")) {
         return (cvtCharToShort);
      } else if (_typeDesc.equals("[C") || _typeDesc.equals("char[]")) {
         return (cvtCharArrayToShortStar);
      } else if (_typeDesc.equals("[I") || _typeDesc.equals("int[]")) {
         return (cvtIntArrayToIntStar);
      } else if (_typeDesc.equals("[F") || _typeDesc.equals("float[]")) {
         return (cvtFloatArrayToFloatStar);
      } else if (_typeDesc.equals("[D") || _typeDesc.equals("double[]")) {
         return (cvtDoubleArrayToDoubleStar);
      } else if (_typeDesc.equals("[J") || _typeDesc.equals("long[]")) {
         return (cvtLongArrayToLongStar);
      } else if (_typeDesc.equals("[S") || _typeDesc.equals("short[]")) {
         return (cvtShortArrayToShortStar);
      }
      // if we get this far, we haven't matched anything yet
      if (useClassModel) {
         return (ClassModel.convert(_typeDesc, "", true));
      } else {
         return _typeDesc;
      }
   }

   @Override protected void writeMethod(MethodCall _methodCall, MethodEntry _methodEntry) throws CodeGenException {

      // System.out.println("_methodEntry = " + _methodEntry);
      // special case for buffers

      int argc = _methodEntry.getStackConsumeCount();

      String methodName = _methodEntry.getNameAndTypeEntry().getNameUTF8Entry().getUTF8();
      String methodSignature = _methodEntry.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8();

      String barrierAndGetterMappings = javaToCLIdentifierMap.get(methodName + methodSignature);

      if (barrierAndGetterMappings != null) {
         // this is one of the OpenCL barrier or size getter methods
         // write the mapping and exit
         write(barrierAndGetterMappings);
      } else {

         String intrinsicMapping = Kernel.getMappedMethodName(_methodEntry);
         // System.out.println("getMappedMethodName for " + methodName + " returned " + mapping);
         boolean isIntrinsic = false;

         if (intrinsicMapping == null) {
            assert entryPoint != null : "entryPoint should not be null";
            boolean isSpecial = _methodCall instanceof I_INVOKESPECIAL;
            MethodModel m = entryPoint.getCallTarget(_methodEntry, isSpecial);

            if (m != null) {
               write(m.getName());
            } else {
               // Must be a library call like rsqrt
               write(methodName);
               isIntrinsic = true;
            }
         } else {
            write(intrinsicMapping);
         }

         write("(");

         if ((intrinsicMapping == null) && (_methodCall instanceof VirtualMethodCall) && (!isIntrinsic)) {

            Instruction i = ((VirtualMethodCall) _methodCall).getInstanceReference();

            if (i instanceof I_ALOAD_0) {
               write("this");
            } else if (i instanceof AccessArrayElement) {
               AccessArrayElement arrayAccess = (AccessArrayElement) ((VirtualMethodCall) _methodCall).getInstanceReference();
               Instruction refAccess = arrayAccess.getArrayRef();
               assert refAccess instanceof I_GETFIELD : "ref should come from getfield";
               String fieldName = ((I_GETFIELD) refAccess).getConstantPoolFieldEntry().getNameAndTypeEntry().getNameUTF8Entry()
                     .getUTF8();
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

   void writePragma(String _name, boolean _enable) {
      write("#pragma OPENCL EXTENSION " + _name + " : " + (_enable ? "en" : "dis") + "able");
      newLine();
   }

   @Override void write(Entrypoint _entryPoint) throws CodeGenException {
      List<String> thisStruct = new ArrayList<String>();
      List<String> argLines = new ArrayList<String>();
      List<String> assigns = new ArrayList<String>();

      // hack
      // for (java.lang.reflect.Field f:_entryPoint.getTheClass().getDeclaredFields()){

      entryPoint = _entryPoint;

      for (ClassModelField field : _entryPoint.getReferencedClassModelFields()) {
         // Field field = _entryPoint.getClassModel().getField(f.getName());
         StringBuilder thisStructLine = new StringBuilder();
         StringBuilder argLine = new StringBuilder();
         StringBuilder assignLine = new StringBuilder();

         String signature = field.getDescriptor();

         boolean isPointer = false;
         RuntimeAnnotationsEntry visibleAnnotations = field.fieldAttributePool.getRuntimeVisibleAnnotationsEntry();

         String type = "__global";
         if (visibleAnnotations != null) {
            // for (AnnotationInfo ai : visibleAnnotations) {
            // String typeDescriptor = ai.getTypeDescriptor();
            // }
         }

         if (signature.startsWith("[")) {
            argLine.append(type + " ");
            thisStructLine.append(type + " ");
            isPointer = true;
            signature = signature.substring(1);
         }

         // If it is a converted array of objects, emit the struct param
         String className = null;
         if (signature.startsWith("L")) {
            // Turn Lcom/amd/javalabs/opencl/demo/DummyOOA; into com_amd_javalabs_opencl_demo_DummyOOA for example
            className = (signature.substring(1, signature.length() - 1)).replace("/", "_");
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
            thisStructLine.append("*");
         }
         assignLine.append("this->");
         assignLine.append(field.getName());
         assignLine.append(" = ");
         assignLine.append(field.getName());
         argLine.append(field.getName());
         thisStructLine.append(field.getName());
         assigns.add(assignLine.toString());
         argLines.add(argLine.toString());
         thisStruct.add(thisStructLine.toString());

         // Add int field into "this" struct for supporting java arraylength op
         // named like foo__javaArrayLength
         if (isPointer && _entryPoint.getArrayFieldArrayLengthUsed().contains(field.getName())) {
            StringBuilder lenStructLine = new StringBuilder();
            StringBuilder lenArgLine = new StringBuilder();
            StringBuilder lenAssignLine = new StringBuilder();

            lenStructLine.append("int " + field.getName() + BlockWriter.arrayLengthMangleSuffix);

            lenAssignLine.append("this->");
            lenAssignLine.append(field.getName() + BlockWriter.arrayLengthMangleSuffix);
            lenAssignLine.append(" = ");
            lenAssignLine.append(field.getName() + BlockWriter.arrayLengthMangleSuffix);

            lenArgLine.append("int " + field.getName() + BlockWriter.arrayLengthMangleSuffix);

            assigns.add(lenAssignLine.toString());
            argLines.add(lenArgLine.toString());
            thisStruct.add(lenStructLine.toString());
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
         writePragma("cl_amd_fp64", true);
         newLine();
      }

      // Emit structs for oop transformation accessors
      for (ClassModel cm : _entryPoint.getObjectArrayFieldsClasses().values()) {
         ArrayList<FieldEntry> fieldSet = cm.getStructMembers();
         if (fieldSet.size() > 0) {
            String mangledClassName = cm.getClassWeAreModelling().getName().replace(".", "_");
            newLine();
            write("typedef struct " + mangledClassName + "_s{");
            in();
            newLine();

            int totalSize = 0;
            int alignTo = 0;

            Iterator<FieldEntry> it = fieldSet.iterator();
            while (it.hasNext()) {
               FieldEntry field = it.next();
               String fType = field.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8();
               int fSize = InstructionSet.TypeSpec.valueOf(fType.equals("Z") ? "B" : fType).getSize();

               if (fSize > alignTo) {
                  alignTo = fSize;
               }
               totalSize += fSize;

               String cType = convertType(field.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8(), true);
               assert cType != null : "could not find type for " + field.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8();
               writeln(cType + " " + field.getNameAndTypeEntry().getNameUTF8Entry().getUTF8() + ";");
            }

            // compute total size for OpenCL buffer
            int totalStructSize = 0;
            if (totalSize % alignTo == 0) {
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
      for (String line : thisStruct) {
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

      for (MethodModel mm : _entryPoint.getCalledMethods()) {
         // write declaration :)

         String returnType = mm.getReturnType();
         // Arrays always map to __global arrays
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
               for (ClassModel c : _entryPoint.getObjectArrayFieldsClasses().values()) {
                  if (mm.getMethod().getClassModel() == c) {
                     write("__global " + mm.getMethod().getClassModel().getClassWeAreModelling().getName().replace(".", "_")
                           + " *this");
                     break;
                  } else if (mm.getMethod().getClassModel().isSuperClass(c.getClassWeAreModelling())) {
                     write("__global " + c.getClassWeAreModelling().getName().replace(".", "_") + " *this");
                     break;
                  }
               }
            }
         }

         boolean alreadyHasFirstArg = !mm.getMethod().isStatic();

         LocalVariableTableEntry lvte = mm.getLocalVariableTableEntry();
         for (LocalVariableInfo lvi : lvte) {
            if ((lvi.getStart() == 0) && ((lvi.getVariableIndex() != 0) || mm.getMethod().isStatic())) { // full scope but skip this
               String descriptor = lvi.getVariableDescriptor();
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
      for (String line : argLines) {

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
      for (String line : assigns) {
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

   @Override protected void writeThisRef() {

      write("this->");

   }

   @Override void writeInstruction(Instruction _instruction) throws CodeGenException {
      if ((_instruction instanceof I_IUSHR) || (_instruction instanceof I_LUSHR)) {
         BinaryOperator binaryInstruction = (BinaryOperator) _instruction;
         Instruction parent = binaryInstruction.getParentExpr();
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

   static String writeToString(Entrypoint _entrypoint) throws CodeGenException {
      final StringBuilder openCLStringBuilder = new StringBuilder();
      KernelWriter openCLWriter = new KernelWriter(){
         @Override void write(String _string) {
            openCLStringBuilder.append(_string);
         }
      };
      openCLWriter.write(_entrypoint);
      return (openCLStringBuilder.toString());
   }
}
