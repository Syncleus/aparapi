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

import com.amd.aparapi.ClassModel.AttributePool.RuntimeAnnotationsEntry;
import com.amd.aparapi.ClassModel.AttributePool.RuntimeAnnotationsEntry.AnnotationInfo;
import com.amd.aparapi.ClassModel.ClassModelField;
import com.amd.aparapi.ClassModel.ConstantPool.FieldEntry;
import com.amd.aparapi.ClassModel.ConstantPool.MethodEntry;
import com.amd.aparapi.ClassModel.AttributePool.LocalVariableTableEntry.LocalVariableInfo;
import com.amd.aparapi.ClassModel.AttributePool.LocalVariableTableEntry.ArgLocalVariableInfo;
import com.amd.aparapi.ClassModel.AttributePool.LocalVariableTableEntry;
import com.amd.aparapi.InstructionSet.*;

import java.util.*;

public abstract class OpenCLKernelWriter{
   public final static String __local = "__local";

   public final static String __global = "__global";

   public final static String __constant = "__constant";

   public final static String LOCAL_ANNOTATION_NAME = TypeHelper.dotClassNameToSignature(Kernel.Local.class.getName(), 0);

   public final static String CONSTANT_ANNOTATION_NAME = TypeHelper.dotClassNameToSignature(Kernel.Constant.class.getName(), 0);


   Entrypoint entryPoint = null;

   final static Map<String, String> javaToCLIdentifierMap = new HashMap<String, String>();

   {

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


   static String getOpenCLName(TypeHelper.JavaType _javaType){
      String openCLName = null;
      if(_javaType.isArray()){
         String stars = "**********".substring(0, _javaType.getArrayDimensions());
         if(_javaType.getArrayElementType().equals(PrimitiveType.ref)){
            openCLName = _javaType.getMangledClassName() + stars;
         }else{
            openCLName = _javaType.getArrayElementType().getOpenCLTypeName() + stars;
         }
      }else if(_javaType.isPrimitive()){
         openCLName = _javaType.getPrimitiveType().getOpenCLTypeName();
      }else if(_javaType.isObject()){
         openCLName = _javaType.getObjectClassName();
      }
      return (openCLName);
   }

   static class ListBuilder{
      StringBuilder sb = new StringBuilder();
      String separator;
      boolean first = true;

      ListBuilder(String _separator){
         separator = _separator;
      }

      void append(String _string){
         if(first){
            first = false;
         }else{
            sb.append(separator);
         }
         sb.append(_string);
      }

      @Override public String toString(){
         return (sb.toString());
      }
   }

   protected void writeMethod(MethodCall _methodCall, MethodEntry _methodEntry) throws CodeGenException{

      // System.out.println("_methodEntry = " + _methodEntry);
      // special case for buffers

      TypeHelper.JavaMethodArgsAndReturnType argsAndReturnType = _methodEntry.getArgsAndReturnType();

      int argc = argsAndReturnType.getArgs().length;

      String methodName = _methodEntry.getNameAndTypeEntry().getName();
      String methodSignature = _methodEntry.getNameAndTypeEntry().getDescriptor();

      String barrierAndGetterMappings = javaToCLIdentifierMap.get(methodName + methodSignature);

      if(barrierAndGetterMappings != null){
         // this is one of the OpenCL barrier or size getter methods
         // write the mapping and exit
         if(argc > 0){
            write(barrierAndGetterMappings);
            write("(");
            for(int arg = 0; arg < argc; arg++){
               if((arg != 0)){
                  write(", ");
               }
               writeInstruction(_methodCall.getArg(arg));
            }
            write(")");
         }else{
            write(barrierAndGetterMappings);
         }
      }else{
         String intrinsicMapping = Kernel.getMappedMethodName(_methodEntry);
         // System.out.println("getMappedMethodName for " + methodName + " returned " + mapping);
         boolean isIntrinsic = false;

         if(intrinsicMapping == null){
            assert entryPoint != null : "entryPoint should not be null";
            boolean isSpecial = _methodCall instanceof I_INVOKESPECIAL;
            boolean isMapped = Kernel.isMappedMethod(_methodEntry);
            MethodModel m = entryPoint.getCallTarget(_methodEntry, isSpecial);

            if(m != null){
               write(m.getMangledName());
            }else{
               // Must be a library call like rsqrt
               assert isMapped : _methodEntry + " should be mapped method!";

               write(methodName);
               isIntrinsic = true;
            }
         }else{
            write(intrinsicMapping);
         }

         write("(");

         if((intrinsicMapping == null) && (_methodCall instanceof VirtualMethodCall) && (!isIntrinsic)){

            Instruction i = ((VirtualMethodCall) _methodCall).getInstanceReference();

            if(i instanceof I_ALOAD_0){
               // For I_ALOAD_0, it must be either a call in the lambda class or
               // a call to the iteration object.
               String classNameInDotForm = _methodEntry.getClassEntry().getDotClassName();
               if(classNameInDotForm.equals(entryPoint.getClassModel().getDotClassName())){
                  write("this");
               }else{
                  // It must be the iteration object
                  // Insert the syntax to access the iteration object from the source array
                  write(" &(this->elements[elements_array_index])");
               }
            }else if(i instanceof AccessArrayElement){
               AccessArrayElement arrayAccess = (AccessArrayElement) ((VirtualMethodCall) _methodCall).getInstanceReference();
               Instruction refAccess = arrayAccess.getArrayRef();
               if(refAccess.isFieldAccessor()){
                  // Calls to objects in arrays that are fields
                  write(" &(this->" + refAccess.asFieldAccessor().getFieldName());
               }else if(refAccess.isLocalVariableAccessor()){
                  // This case is to handle lambda argument object array refs
                  LocalVariableTableIndexAccessor localVariableLoadInstruction = refAccess.asLocalVariableAccessor();
                  LocalVariableInfo localVariable = localVariableLoadInstruction.getLocalVariableInfo();
                  write(" &(this->" + localVariable.getVariableName());
               }
               write("[");
               writeInstruction(arrayAccess.getArrayIndex());
               write("])");
            }else{
               // Assume it is a call in an object lambda on the iteration object.
               // Insert the syntax to access the iteration object from the source array
               write(" &(this->elements[elements_array_index])");
            }
         }
         for(int arg = 0; arg < argc; arg++){
            if(((intrinsicMapping == null) && (_methodCall instanceof VirtualMethodCall) && (!isIntrinsic)) || (arg != 0)){
               write(", ");
            }
            writeInstruction(_methodCall.getArg(arg));
         }
         write(")");
      }
   }

   void writePragma(String _name, boolean _enable){
      write("#pragma OPENCL EXTENSION " + _name + " : " + (_enable ? "en" : "dis") + "able");
      newLine();
   }


   static class OpenCLTextRenderer extends TextRenderer<OpenCLTextRenderer>{
      OpenCLTextRenderer __globalSpace(){
         return (append(__global));
      }

      OpenCLTextRenderer type(TypeHelper.JavaType _type){
         return (append(getOpenCLName(_type)));
      }

      OpenCLTextRenderer mangled(TypeHelper.JavaType _type){
         return (append(_type.getMangledClassName()));
      }
   }


   void write(Entrypoint _entryPoint) throws CodeGenException{
      List<String> thisStruct = new ArrayList<String>();
      List<String> argLines = new ArrayList<String>();
      List<String> assigns = new ArrayList<String>();

      entryPoint = _entryPoint;

      // Add code to collect lambda formal arguments
      // The local variables are the java args to the method
      {
         MethodModel mm = entryPoint.getMethodModel();
         int argsCount = 1;
         LocalVariableTableEntry lvt = mm.getLocalVariableTableEntry();
         for(ArgLocalVariableInfo alvi : lvt.getArgs()){
            StringBuilder thisStructLine = new StringBuilder();
            StringBuilder argLine = new StringBuilder();
            StringBuilder assignLine = new StringBuilder();
            if(((alvi.getSlot() != 0) || mm.getMethod().isStatic())){ // full scope but skip this
               // String descriptor = ;

               // For object stream lambdas, the lvi is the object prefix, but in
               // the kernel we will need something like:
               //
               // __global com_amd_aparapi_examples_oopnbody_Body *elements,
               // int   elements_array_index;
               //
               // where elements_array_index is the get_global_id index into the elements array


               //   JavaType type = TypeHelper.getJavaType(lvi.getVariableDescriptor());
               // String classModelType = prefix.getSignature();
               String output;
               boolean isObjectLambda = false;
               if(alvi.getRealType().isArray()){
                  int dim = alvi.getRealType().getArrayDimensions();
                  // This is a local array captured from the caller method and
                  // passed in from the Block/Consumer
                  if(alvi.getRealType().isArrayOfObjects(dim)){    // an array of objects
                     output = __global + " " + alvi.getRealType().getMangledClassName();
                  }else{
                     output = __global + " " + getOpenCLName(alvi.getRealType());
                  }
               }else if(alvi.getRealType().isPrimitive()){
                  output = getOpenCLName(alvi.getRealType());
               }else{
                  // This must be the iteration object
                  // Turn Lcom/amd/javalabs/opencl/demo/DummyOOA; into com_amd_javalabs_opencl_demo_DummyOOA for example
                  final String sourceArrayName = "elements";
                  output = __global + " " + alvi.getRealType().getMangledClassName();
                  isObjectLambda = true;

                  // Insert the source object array and integer index here
                  // in case of object stream lambda
                  //if (isObjectLambda == true && argsCount == 1) {

                  String elementsDeclaration = output + " *" + sourceArrayName;

                  // Add array to args
                  argLine.append(elementsDeclaration);
                  argLines.add(argLine.toString());

                  // Add array to this struct
                  thisStructLine.append(elementsDeclaration);
                  thisStruct.add(thisStructLine.toString());

                  // Add index to this struct and args
                  final String objSourceIndex = "elements_array_index";
                  final String objSourceIndexDecl = "int " + objSourceIndex;
                  thisStruct.add(objSourceIndexDecl);
                  argLines.add(objSourceIndexDecl);

                  // Add array to assigns
                  assignLine.append("this->").append(sourceArrayName).append(" = ").append(sourceArrayName);
                  assigns.add(assignLine.toString());

                  // Add get_global_id to assigns
                  StringBuilder assignGid = new StringBuilder();
                  assignGid.append(objSourceIndex).append(" = get_global_id(0)");
                  assigns.add(assignGid.toString());

               }

               if(!isObjectLambda){
                  if(alvi.isArray()){
                     // It will be a pointer ref to an array that was a captured arg
                     argLine.append(output);
                     thisStructLine.append(output);
                  }else{
                     argLine.append(getOpenCLName(alvi.getRealType()));
                     thisStructLine.append(getOpenCLName(alvi.getRealType()));
                  }
                  argLine.append(" ");
                  thisStructLine.append(" ");

                  // Note in the case of int lambdas, the last lambda java method
                  // arg is an int which acts as the opencl gid
                  // Its value is not used and it is assigned with get_global_id(0)
                  if(argsCount == (entryPoint.getLambdaActualParamsCount() + 1) &&
                        (alvi != null) && alvi.getVariableDescriptor().equals("I")){
                     StringBuilder assignGid = new StringBuilder();
                     assignGid.append(alvi.getVariableName()).append(" = get_global_id(0)");
                     assigns.add(assignGid.toString());
                  }

                  assignLine.append("this->").append(alvi.getVariableName()).append(" = ").append(alvi.getVariableName());

                  if(alvi.isArray()){
                     argLine.append(alvi.getVariableName());
                     thisStructLine.append(alvi.getVariableName());
                  }else{
                     argLine.append(alvi.getVariableName());
                     thisStructLine.append(alvi.getVariableName());
                  }

                  assigns.add(assignLine.toString());
                  argLines.add(argLine.toString());
                  thisStruct.add(thisStructLine.toString());
               }

               argsCount++;
            }
         }
      }

      for(ClassModelField field : _entryPoint.getReferencedClassModelFields()){
         // Field field = _entryPoint.getClassModel().getField(f.getHSAName());
         StringBuilder thisStructLine = new StringBuilder();
         StringBuilder argLine = new StringBuilder();
         StringBuilder assignLine = new StringBuilder();

         TypeHelper.JavaType fieldType = field.getType();


         // check the suffix
         String qualifier = field.getName().endsWith(Kernel.LOCAL_SUFFIX) ? __local
               : (field.getName().endsWith(Kernel.CONSTANT_SUFFIX) ? __constant : __global);
         RuntimeAnnotationsEntry visibleAnnotations = field.fieldAttributePool.getRuntimeVisibleAnnotationsEntry();

         if(visibleAnnotations != null){
            for(AnnotationInfo ai : visibleAnnotations){
               String typeDescriptor = ai.getTypeDescriptor();
               if(typeDescriptor.equals(LOCAL_ANNOTATION_NAME)){
                  qualifier = __local;
               }else if(typeDescriptor.equals(CONSTANT_ANNOTATION_NAME)){
                  qualifier = __constant;
               }
            }
         }


         if(fieldType.isArray()){
            argLine.append(qualifier + " ");
            thisStructLine.append(qualifier + " ");
         }

         // If it is a converted array of objects, emit the struct param
         String className = null;
         if(fieldType.isArray()){
            argLine.append(getOpenCLName(fieldType));
            thisStructLine.append(getOpenCLName(fieldType));
         }else if(fieldType.isObject()){
            // Turn Lcom/amd/javalabs/opencl/demo/DummyOOA; into com_amd_javalabs_opencl_demo_DummyOOA for example
            className = fieldType.getMangledClassName();
            argLine.append(className);
            thisStructLine.append(className);
         }else{
            argLine.append(getOpenCLName(fieldType));
            thisStructLine.append(getOpenCLName(fieldType));
         }

         argLine.append(" ");
         thisStructLine.append(" ");
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
         if(fieldType.isArray() && _entryPoint.getArrayFieldArrayLengthUsed().contains(field.getName())){
            StringBuilder lenStructLine = new StringBuilder();
            StringBuilder lenArgLine = new StringBuilder();
            StringBuilder lenAssignLine = new StringBuilder();

            lenStructLine.append("int " + field.getName() + arrayLengthMangleSuffix);

            lenAssignLine.append("this->");
            lenAssignLine.append(field.getName() + arrayLengthMangleSuffix);
            lenAssignLine.append(" = ");
            lenAssignLine.append(field.getName() + arrayLengthMangleSuffix);

            lenArgLine.append("int " + field.getName() + arrayLengthMangleSuffix);

            assigns.add(lenAssignLine.toString());
            argLines.add(lenArgLine.toString());
            thisStruct.add(lenStructLine.toString());
         }
      }

      boolean usesAtomics = false;
      if(Config.enableAtomic32 || _entryPoint.requiresAtomic32Pragma()){
         usesAtomics = true;
         writePragma("cl_khr_global_int32_base_atomics", true);
         writePragma("cl_khr_global_int32_extended_atomics", true);
         writePragma("cl_khr_local_int32_base_atomics", true);
         writePragma("cl_khr_local_int32_extended_atomics", true);
      }
      if(Config.enableAtomic64 || _entryPoint.requiresAtomic64Pragma()){
         usesAtomics = true;
         writePragma("cl_khr_int64_base_atomics", true);
         writePragma("cl_khr_int64_extended_atomics", true);
      }
      if(usesAtomics){
         writeInLn("int atomicAdd(__global int *_arr, int _index, int _delta){");
         {
            writeOutLn("return atomic_add(&_arr[_index], _delta);");
         }
         writeLn("}");
      }

      if(Config.enableDoubles || _entryPoint.requiresDoublePragma()){
         writePragma("cl_khr_fp64", true);
         newLine();
      }

      // Emit structs for oop transformation accessors
      for(ClassModel cm : _entryPoint.getObjectArrayFieldsClasses().values()){
         ArrayList<FieldEntry> fieldSet = cm.getStructMembers();
         if(fieldSet.size() > 0){
            String mangledClassName = cm.getMangledClassName();

            lnWriteInLn("typedef struct " + mangledClassName + "_s{");
            int totalSize = 0;
            int alignTo = 0;

            Iterator<FieldEntry> it = fieldSet.iterator();
            while(it.hasNext()){
               FieldEntry field = it.next();
               String fieldName = field.getNameAndTypeEntry().getName();
               TypeHelper.JavaType fieldType = field.getType();

               if(fieldType.getPrimitiveType().getJavaBytes() > alignTo){
                  alignTo = fieldType.getPrimitiveType().getJavaBytes();
               }
               totalSize += fieldType.getPrimitiveType().getJavaBytes();
               writeLn(fieldType.getPrimitiveType().getOpenCLTypeName() + " " + fieldName + ";");
            }

            // compute total size for OpenCL buffer
            int totalStructSize = 0;
            if(totalSize % alignTo == 0){
               totalStructSize = totalSize;
            }else{
               // Pad up if necessary
               totalStructSize = ((totalSize / alignTo) + 1) * alignTo;
            }
            if(totalStructSize > alignTo){
               while(totalSize < totalStructSize){
                  // structBuffer.put((byte)-1);
                  writeLn("char _pad_" + totalSize + ";");
                  totalSize++;
               }
            }
            out();
            writeLn("} " + mangledClassName + ";");
            newLine();
         }
      }

      write("typedef struct This_s{");
      in();
      newLine();
      for(String line : thisStruct){
         writeLn(line + ";");
      }
      writeOutLn("int passid;");
      writeLn("}This;");

      lnWriteInLn("int get_pass_id(This *this){");
      {
         writeOutLn("return this->passid;");
      }
      writeLn("}");
      newLine();


      for(MethodModel mm : _entryPoint.getCalledMethods()){
         // write declaration :)

         TypeHelper.JavaType returnType = mm.getMethod().getArgsAndReturnType().getReturnType();
         // Arrays always map to __global arrays
         if(returnType.isArray()){
            write(" __global ");
         }
         write(getOpenCLName(returnType) + " ");

         write(mm.getMangledName() + "(");

         if(!mm.getMethod().isStatic()){
            if((mm.getMethod().getClassModel() == _entryPoint.getClassModel())){
               //  || mm.getMethod().getClassModel().isSuperClass(_entryPoint.getClassModel())){     Why this? GRF
               write("This *this");
            }else{
               // Call to an object member or superclass of member
               for(ClassModel c : _entryPoint.getObjectArrayFieldsClasses().values()){
                  if(mm.getMethod().getClassModel() == c){
                     write("__global " + mm.getMethod().getClassModel().getMangledClassName()
                           + " *this");
                     break;
                  }else if(mm.getMethod().getClassModel().isSuperClass(c)){
                     write("__global " + c.getMangledClassName() + " *this");
                     break;
                  }
               }
            }
         }

         boolean alreadyHasFirstArg = !mm.getMethod().isStatic();
         LocalVariableTableEntry lvte = mm.getLocalVariableTableEntry();
         for(LocalVariableTableEntry.ArgLocalVariableInfo alvi : lvte.getArgs()){
            if((alvi.getSlot() != 0) || mm.getMethod().isStatic()){ // full scope but skip this

               if(alreadyHasFirstArg){
                  write(", ");
               }

               // Arrays always map to __global arrays
               if(alvi.getRealType().isArray()){
                  write(" __global ");
               }

               write(getOpenCLName(alvi.getRealType()) + " ");
               write(alvi.getVariableName());
               alreadyHasFirstArg = true;
            }
         }
         write(")");
         writeMethodBody(mm);
         newLine();
      }
      if(_entryPoint.isKernel()){
         writeIn("__kernel void " + _entryPoint.getMethodModel().getSimpleName() + "(");
      }else{
         writeIn("__kernel void run(");
      }
      boolean first = true;
      for(String line : argLines){

         if(first){
            first = false;
         }else{
            write(", ");
         }

         newLine();
         write(line);
      }

      if(first){
         first = false;
      }else{
         write(", ");
      }

      lnWriteOutLn("int passid");
      writeInLn("){");
      writeLn("This thisStruct;");
      writeLn("This* this=&thisStruct;");
      for(String line : assigns){
         writeLn(line + ";");
      }
      writeLn("this->passid = passid;");

      writeMethodBody(_entryPoint.getMethodModel());
      //  out();
      outWrite("}");

      // out();

   }


   protected void writeThisRef(){
      write("this->");
   }

   // Emit the this-> syntax when accessing locals that are lambda arguments

   protected void writeAccessLocalVariable(Instruction _instruction){
      AccessLocalVariable localVariableLoadInstruction = (AccessLocalVariable) _instruction;
      LocalVariableInfo localVariable = localVariableLoadInstruction.getLocalVariableInfo();
      if((localVariable.getStart() == 0) && (_instruction.getMethod() == entryPoint.getMethodModel().getMethod())){
         // This is a method parameter captured value into the lambda
         writeThisRef();
      }
      write(localVariable.getVariableName());
   }

   void writeInstruction(Instruction _instruction) throws CodeGenException{
      if((_instruction instanceof I_IUSHR) || (_instruction instanceof I_LUSHR)){
         BinaryOperator binaryInstruction = (BinaryOperator) _instruction;
         Instruction parent = binaryInstruction.getParentExpr();
         boolean needsParenthesis = true;

         if(parent instanceof AssignToLocalVariable){
            needsParenthesis = false;
         }else if(parent instanceof AssignToField){
            needsParenthesis = false;
         }else if(parent instanceof AssignToArrayElement){
            needsParenthesis = false;
         }
         if(needsParenthesis){
            write("(");
         }

         if(binaryInstruction instanceof I_IUSHR){
            write("((unsigned int)");
         }else{
            write("((unsigned long)");
         }
         writeInstruction(binaryInstruction.getLhs());
         write(")");
         write(" >> ");
         writeInstruction(binaryInstruction.getRhs());

         if(needsParenthesis){
            write(")");
         }
      }else if(_instruction instanceof CompositeIfElseInstruction){
         write("(");
         Instruction lhs = writeConditional(((CompositeInstruction) _instruction).getBranchSet(), false);
         write(")?");
         writeInstruction(lhs);
         write(":");
         writeInstruction(lhs.getNextExpr().getNextExpr());
      }else if(_instruction instanceof CompositeInstruction){
         writeComposite((CompositeInstruction) _instruction);

      }else if(_instruction instanceof AssignToLocalVariable){
         AssignToLocalVariable assignToLocalVariable = (AssignToLocalVariable) _instruction;

         LocalVariableInfo localVariableInfo = assignToLocalVariable.getLocalVariableInfo();
         if(assignToLocalVariable.isDeclaration()){
            //String descriptor = localVariableInfo.getVariableDescriptor();
            TypeSpec typeSpec = localVariableInfo.getTypeSpec();


            write(typeSpec.getPrimitiveType().getOpenCLTypeName() + " ");

         }
         if(localVariableInfo == null){
            throw new CodeGenException("outOfScope" + _instruction.getThisPC() + " = ");
         }else{
            write(localVariableInfo.getVariableName() + " = ");
         }

         for(Instruction operand = _instruction.getFirstChild(); operand != null; operand = operand.getNextExpr()){
            writeInstruction(operand);
         }

      }else if(_instruction instanceof AssignToArrayElement){
         AssignToArrayElement arrayAssignmentInstruction = (AssignToArrayElement) _instruction;
         writeInstruction(arrayAssignmentInstruction.getArrayRef());
         write("[");
         writeInstruction(arrayAssignmentInstruction.getArrayIndex());
         write("]");
         write(" ");
         write(" = ");
         writeInstruction(arrayAssignmentInstruction.getValue());
      }else if(_instruction instanceof AccessArrayElement){
         AccessArrayElement arrayLoadInstruction = (AccessArrayElement) _instruction;
         writeInstruction(arrayLoadInstruction.getArrayRef());
         write("[");
         writeInstruction(arrayLoadInstruction.getArrayIndex());
         write("]");
      }else if(_instruction instanceof AccessField){
         AccessField accessField = (AccessField) _instruction;
         if(accessField instanceof AccessInstanceField){
            Instruction accessInstanceField = ((AccessInstanceField) accessField).getInstance();
            if(accessInstanceField instanceof CloneInstruction){
               accessInstanceField = ((CloneInstruction) accessInstanceField).getReal();
            }
            if(!(accessInstanceField instanceof I_ALOAD_0)){
               writeInstruction(accessInstanceField);
               write(".");
            }else{
               writeThisRef();
            }
         }else{
            // It is a static field but we still pass it via "this"
            writeThisRef();
         }
         write(accessField.getConstantPoolFieldEntry().getNameAndTypeEntry().getName());

      }else if(_instruction instanceof I_ARRAYLENGTH){
         AccessField child = (AccessField) _instruction.getFirstChild();
         String arrayName = child.getConstantPoolFieldEntry().getNameAndTypeEntry().getName();
         write("this->" + arrayName + arrayLengthMangleSuffix);
      }else if(_instruction instanceof AssignToField){
         AssignToField assignedField = (AssignToField) _instruction;

         if(assignedField instanceof AssignToInstanceField){
            Instruction accessInstanceField = ((AssignToInstanceField) assignedField).getInstance().getReal();

            if(!(accessInstanceField instanceof I_ALOAD_0)){
               writeInstruction(accessInstanceField);
               write(".");
            }else{
               writeThisRef();
            }
         }
         write(assignedField.getConstantPoolFieldEntry().getNameAndTypeEntry().getName());
         write("=");
         writeInstruction(assignedField.getValueToAssign());
      }else if(_instruction instanceof Constant<?>){
         Constant<?> constantInstruction = (Constant<?>) _instruction;
         Object value = constantInstruction.getValue();

         if(value instanceof Float){

            Float f = (Float) value;
            if(f.isNaN()){
               write("NAN");
            }else if(f.isInfinite()){
               if(f < 0){
                  write("-");
               }
               write("INFINITY");
            }else{
               write(value.toString());
               write("f");
            }
         }else if(value instanceof Double){

            Double d = (Double) value;
            if(d.isNaN()){
               write("NAN");
            }else if(d.isInfinite()){
               if(d < 0){
                  write("-");
               }
               write("INFINITY");
            }else{
               write(value.toString());
            }
         }else{
            write(value.toString());
            if(value instanceof Long){
               write("L");
            }
         }

      }else if(_instruction instanceof AccessLocalVariable){
         writeAccessLocalVariable(_instruction);
      }else if(_instruction instanceof I_IINC){
         I_IINC location = (I_IINC) _instruction;
         LocalVariableInfo localVariable = location.getLocalVariableInfo();
         int adjust = location.getAdjust();

         write(localVariable.getVariableName());
         if(adjust == 1){
            write("++");
         }else if(adjust == -1){
            write("--");
         }else if(adjust > 1){
            write("+=" + adjust);
         }else if(adjust < -1){
            write("-=" + (-adjust));
         }
      }else if(_instruction instanceof BinaryOperator){
         BinaryOperator binaryInstruction = (BinaryOperator) _instruction;
         Instruction parent = binaryInstruction.getParentExpr();
         boolean needsParenthesis = true;

         if(parent instanceof AssignToLocalVariable){
            needsParenthesis = false;
         }else if(parent instanceof AssignToField){
            needsParenthesis = false;
         }else if(parent instanceof AssignToArrayElement){
            needsParenthesis = false;
         }else{
            /**
             if (parent instanceof BinaryOperator) {
             BinaryOperator parentBinaryOperator = (BinaryOperator) parent;
             if (parentBinaryOperator.getOperator().ordinal() > binaryInstruction.getOperator().ordinal()) {
             needsParenthesis = false;
             }
             }
             **/
         }

         if(needsParenthesis){
            write("(");
         }

         writeInstruction(binaryInstruction.getLhs());

         write(" " + binaryInstruction.getOperator().getText() + " ");
         writeInstruction(binaryInstruction.getRhs());

         if(needsParenthesis){
            write(")");
         }

      }else if(_instruction instanceof CastOperator){
         CastOperator castInstruction = (CastOperator) _instruction;
         //  write("(");


         write(castInstruction.getOperator().getText());


         writeInstruction(castInstruction.getUnary());
         //    write(")");
      }else if(_instruction instanceof UnaryOperator){
         UnaryOperator unaryInstruction = (UnaryOperator) _instruction;
         //   write("(");
         write(unaryInstruction.getOperator().getText());

         writeInstruction(unaryInstruction.getUnary());
         //   write(")");
      }else if(_instruction instanceof Return){

         Return ret = (Return) _instruction;
         write("return");
         if(ret.getStackConsumeCount() > 0){
            write("(");
            writeInstruction(ret.getFirstChild());
            write(")");
         }

      }else if(_instruction instanceof MethodCall){
         MethodCall methodCall = (MethodCall) _instruction;

         MethodEntry methodEntry = methodCall.getConstantPoolMethodEntry();

         writeMethod(methodCall, methodEntry);
      }else if(_instruction.getByteCode().equals(ByteCode.CLONE)){
         CloneInstruction cloneInstruction = (CloneInstruction) _instruction;
         writeInstruction(cloneInstruction.getReal());
      }else if(_instruction.getByteCode().equals(ByteCode.INCREMENT)){
         IncrementInstruction incrementInstruction = (IncrementInstruction) _instruction;

         if(incrementInstruction.isPre()){
            if(incrementInstruction.isInc()){
               write("++");
            }else{
               write("--");
            }
         }

         writeInstruction(incrementInstruction.getFieldOrVariableReference());
         if(!incrementInstruction.isPre()){
            if(incrementInstruction.isInc()){
               write("++");
            }else{
               write("--");
            }
         }
      }else if(_instruction.getByteCode().equals(ByteCode.MULTI_ASSIGN)){
         MultiAssignInstruction multiAssignInstruction = (MultiAssignInstruction) _instruction;
         AssignToLocalVariable from = (AssignToLocalVariable) multiAssignInstruction.getFrom();
         AssignToLocalVariable last = (AssignToLocalVariable) multiAssignInstruction.getTo();
         Instruction common = multiAssignInstruction.getCommon();
         Stack<AssignToLocalVariable> stack = new Stack<AssignToLocalVariable>();

         while(from != last){
            stack.push(from);
            from = (AssignToLocalVariable) ((Instruction) from).getNextExpr();
         }

         for(AssignToLocalVariable alv = stack.pop(); alv != null; alv = stack.size() > 0 ? stack.pop() : null){

            LocalVariableInfo localVariableInfo = alv.getLocalVariableInfo();
            if(alv.isDeclaration()){

               TypeSpec typeSpec = localVariableInfo.getTypeSpec();


               write(typeSpec.getPrimitiveType().getOpenCLTypeName());
            }
            if(localVariableInfo == null){
               throw new CodeGenException("outOfScope" + _instruction.getThisPC() + " = ");
            }else{
               write(localVariableInfo.getVariableName() + " = ");
            }

         }
         writeInstruction(common);
      }else if(_instruction.getByteCode().equals(ByteCode.INLINE_ASSIGN)){
         InlineAssignInstruction inlineAssignInstruction = (InlineAssignInstruction) _instruction;
         AssignToLocalVariable assignToLocalVariable = inlineAssignInstruction.getAssignToLocalVariable();

         LocalVariableInfo localVariableInfo = assignToLocalVariable.getLocalVariableInfo();
         if(assignToLocalVariable.isDeclaration()){
            // this is bad! we need a general way to hoist up a required declaration
            throw new CodeGenException("/* we can't declare this " + localVariableInfo.getTypeSpec().getPrimitiveType().getOpenCLTypeName()
                  + " here */");
         }
         write(localVariableInfo.getVariableName());
         write("=");
         writeInstruction(inlineAssignInstruction.getRhs());
      }else if(_instruction.getByteCode().equals(ByteCode.FIELD_ARRAY_ELEMENT_ASSIGN)){
         FieldArrayElementAssign inlineAssignInstruction = (FieldArrayElementAssign) _instruction;
         AssignToArrayElement arrayAssignmentInstruction = inlineAssignInstruction.getAssignToArrayElement();

         writeInstruction(arrayAssignmentInstruction.getArrayRef());
         write("[");
         writeInstruction(arrayAssignmentInstruction.getArrayIndex());
         write("]");
         write(" ");
         write(" = ");

         writeInstruction(inlineAssignInstruction.getRhs());
      }else if(_instruction.getByteCode().equals(ByteCode.FIELD_ARRAY_ELEMENT_INCREMENT)){

         FieldArrayElementIncrement fieldArrayElementIncrement = (FieldArrayElementIncrement) _instruction;
         AssignToArrayElement arrayAssignmentInstruction = fieldArrayElementIncrement.getAssignToArrayElement();
         if(fieldArrayElementIncrement.isPre()){
            if(fieldArrayElementIncrement.isInc()){
               write("++");
            }else{
               write("--");
            }
         }
         writeInstruction(arrayAssignmentInstruction.getArrayRef());

         write("[");
         writeInstruction(arrayAssignmentInstruction.getArrayIndex());
         write("]");
         if(!fieldArrayElementIncrement.isPre()){
            if(fieldArrayElementIncrement.isInc()){
               write("++");
            }else{
               write("--");
            }
         }

      }else if(_instruction.getByteCode().equals(ByteCode.NONE)){
         // we are done
      }else if(_instruction instanceof Branch){
         throw new CodeGenException(String.format("%s -> %04d", _instruction.getByteCode().toString().toLowerCase(),
               ((Branch) _instruction).getTarget().getThisPC()));
      }else if(_instruction instanceof I_POP){
         //POP discarded void call return?
         writeInstruction(_instruction.getFirstChild());
      }else if(_instruction instanceof I_NEWARRAY){
         throw new CodeGenException(String.format("can't create a new array inside a kernel!"));
      }else{
         throw new CodeGenException(String.format("%s", _instruction.getByteCode().toString().toLowerCase()));
      }


   }

   public static String writeToString(Entrypoint _entrypoint) throws CodeGenException{
      final StringBuilder openCLStringBuilder = new StringBuilder();
      OpenCLKernelWriter openCLWriter = new OpenCLKernelWriter(){
         @Override void write(String _string){
            openCLStringBuilder.append(_string);
         }
      };
      try{
         openCLWriter.write(_entrypoint);
      }catch(CodeGenException codeGenException){
         throw codeGenException;
      }catch(Throwable t){
         throw new CodeGenException(t);
      }
      return (openCLStringBuilder.toString());
   }


   final static String arrayLengthMangleSuffix = "__javaArrayLength";

   abstract void write(String _string);

   protected void writeLn(String _string){
      write(_string);
      newLine();
   }

   protected void writeIn(String _string){
      write(_string);
      in();
   }

   protected void lnWriteInLn(String _string){
      newLine();
      writeInLn(_string);
   }

   protected void writeInLn(String _string){
      writeIn(_string);
      newLine();
   }

   protected void lnWriteOutLn(String _string){
      newLine();
      writeOutLn(_string);
   }

   protected void writeOutLn(String _string){
      write(_string);
      out();
      newLine();
   }

   protected void outWrite(String _string){
      out();
      newLine();
      write(_string);
   }

   private int indent = 0;

   protected void in(){
      indent++;
   }

   protected void out(){
      indent--;
   }

   protected void newLine(){
      write("\n");
      for(int i = 0; i < indent; i++){
         write("   ");
      }
   }

   protected void writeConditionalBranch16(ConditionalBranch16 _branch16, boolean _invert) throws CodeGenException{

      if(_branch16 instanceof If){
         If iff = (If) _branch16;

         writeInstruction(iff.getLhs());
         write(_branch16.getOperator().getText(_invert));
         writeInstruction(iff.getRhs());
      }else if(_branch16 instanceof I_IFNULL){
         I_IFNULL iff = (I_IFNULL) _branch16;
         writeInstruction(iff.getFirstChild());

         if(_invert){
            write(" != NULL");
         }else{
            write(" == NULL");
         }

      }else if(_branch16 instanceof I_IFNONNULL){
         I_IFNONNULL iff = (I_IFNONNULL) _branch16;
         writeInstruction(iff.getFirstChild());

         if(_invert){
            write(" == NULL");
         }else{
            write(" != NULL");
         }
      }else if(_branch16 instanceof IfUnary){
         IfUnary branch16 = (IfUnary) _branch16;
         Instruction comparison = branch16.getUnary();
         ByteCode comparisonByteCode = comparison.getByteCode();
         String comparisonOperator = _branch16.getOperator().getText(_invert);

         switch(comparisonByteCode){
            case FCMPG:
            case DCMPG:
            case FCMPL:
            case DCMPL:
               if(Config.verboseComparitor){
                  write("/* bytecode=" + comparisonByteCode.getName() + " invert=" + _invert + "*/");
               }
               writeInstruction(comparison.getFirstChild());
               write(comparisonOperator);
               writeInstruction(comparison.getLastChild());
               break;
            default:
               if(Config.verboseComparitor){
                  write("/* default bytecode=" + comparisonByteCode.getName() + " invert=" + _invert + "*/");
               }
               writeInstruction(comparison);
               write(comparisonOperator);
               write("0");
         }

      }
   }

   protected void writeComposite(CompositeInstruction instruction) throws CodeGenException{
      if(instruction instanceof CompositeArbitraryScopeInstruction){
         newLine();

         writeBlock(instruction.getFirstChild(), null);
      }else if(instruction instanceof CompositeIfInstruction){
         newLine();
         write("if (");
         Instruction blockStart = writeConditional(((CompositeInstruction) instruction).getBranchSet(), false);

         write(")");
         writeBlock(blockStart, null);
      }else if(instruction instanceof CompositeIfElseInstruction){
         newLine();
         write("if (");
         Instruction blockStart = writeConditional(((CompositeInstruction) instruction).getBranchSet(), false);
         write(")");
         Instruction elseGoto = blockStart;
         while(!(elseGoto.isBranch() && elseGoto.asBranch().isUnconditional())){
            elseGoto = elseGoto.getNextExpr();
         }
         writeBlock(blockStart, elseGoto);
         write(" else ");
         writeBlock(elseGoto.getNextExpr(), null);
      }else if(instruction instanceof CompositeForSunInstruction){
         newLine();
         write("for (");
         Instruction topBranch = instruction.getFirstChild();
         if(topBranch instanceof AssignToLocalVariable){
            writeInstruction(topBranch);
            topBranch = topBranch.getNextExpr();
         }
         write("; ");
         BranchSet branchSet = ((CompositeInstruction) instruction).getBranchSet();
         Instruction blockStart = writeConditional(branchSet, false);

         Instruction lastGoto = instruction.getLastChild();

         if(branchSet.getFallThrough() == lastGoto){
            // empty body no delta!
            write(";){}");
         }else{
            Instruction delta = lastGoto.getPrevExpr();
            write("; ");
            if(!(delta instanceof CompositeInstruction)){
               writeInstruction(delta);
               write(")");
               writeBlock(blockStart, delta);
            }else{
               write("){");
               in();
               writeSequence(blockStart, delta);

               newLine();
               writeSequence(delta, delta.getNextExpr());
               out();
               newLine();
               write("}");

            }
         }

      }else if(instruction instanceof CompositeWhileInstruction){
         newLine();
         write("while (");
         BranchSet branchSet = ((CompositeInstruction) instruction).getBranchSet();
         Instruction blockStart = writeConditional(branchSet, false);
         write(")");
         Instruction lastGoto = instruction.getLastChild();
         writeBlock(blockStart, lastGoto);

      }else if(instruction instanceof CompositeEmptyLoopInstruction){
         newLine();
         write("for (");
         Instruction topBranch = instruction.getFirstChild();
         if(topBranch instanceof AssignToLocalVariable){
            writeInstruction(topBranch);
            topBranch = topBranch.getNextExpr();
         }
         write("; ");
         writeConditional(((CompositeInstruction) instruction).getBranchSet(), false);
         write(";){}");

      }else if(instruction instanceof CompositeForEclipseInstruction){
         newLine();
         write("for (");
         Instruction topGoto = instruction.getFirstChild();
         if(topGoto instanceof AssignToLocalVariable){
            writeInstruction(topGoto);
            topGoto = topGoto.getNextExpr();
         }
         write("; ");
         Instruction last = instruction.getLastChild();
         while(last.getPrevExpr().isBranch()){
            last = last.getPrevExpr();
         }
         writeConditional(((CompositeInstruction) instruction).getBranchSet(), true);
         write("; ");
         Instruction delta = last.getPrevExpr();
         if(!(delta instanceof CompositeInstruction)){
            writeInstruction(delta);
            write(")");
            writeBlock(topGoto.getNextExpr(), delta);
         }else{
            write("){");
            in();
            writeSequence(topGoto.getNextExpr(), delta);

            newLine();
            writeSequence(delta, delta.getNextExpr());
            out();
            newLine();
            write("}");

         }
      }else if(instruction instanceof CompositeDoWhileInstruction){
         newLine();
         write("do");
         Instruction blockStart = instruction.getFirstChild();
         Instruction blockEnd = instruction.getLastChild();
         writeBlock(blockStart, blockEnd);
         write("while(");
         writeConditional(((CompositeInstruction) instruction).getBranchSet(), false);
         write(");");
         newLine();

      }
   }

   protected void writeSequence(Instruction _first, Instruction _last) throws CodeGenException{

      for(Instruction instruction = _first; instruction != _last; instruction = instruction.getNextExpr()){
         if(instruction instanceof CompositeInstruction){
            writeComposite((CompositeInstruction) instruction);
         }else if(!instruction.getByteCode().equals(ByteCode.NONE)){
            newLine();
            writeInstruction(instruction);
            write(";");

         }
      }

   }

   protected void writeBlock(Instruction _first, Instruction _last) throws CodeGenException{
      writeIn("{");
      writeSequence(_first, _last);
      outWrite("}");
   }


   protected Instruction writeConditional(BranchSet _branchSet, boolean _invert) throws CodeGenException{

      BranchSet.LogicalExpressionNode logicalExpression = _branchSet.getLogicalExpression();
      write(logicalExpression, !_invert, null);
      return (_branchSet.getLast().getNextExpr());
   }


   protected void write(BranchSet.LogicalExpressionNode _node, boolean _invert, BranchSet.CompoundLogicalExpressionNode _parent) throws CodeGenException{

      if(_node instanceof BranchSet.SimpleLogicalExpressionNode){
         BranchSet.SimpleLogicalExpressionNode sn = (BranchSet.SimpleLogicalExpressionNode) _node;

         writeConditionalBranch16((ConditionalBranch16) sn.getBranch(), !sn.isInverted());

      }else{
         BranchSet.CompoundLogicalExpressionNode ln = (BranchSet.CompoundLogicalExpressionNode) _node;

         boolean paren = false;
         if(_invert){
            paren = (_parent != null) && _parent.isOr() && ln.isAnd();
         }else{
            paren = (_parent != null) && _parent.isAnd() && ln.isOr();
         }

         if(paren){
            write("(");
         }
         write(ln.getLhs(), _invert, ln);
         if(_invert){
            write(ln.isAnd() ? " || " : " && ");
         }else{
            write(ln.isAnd() ? " && " : " || ");
         }
         write(ln.getRhs(), _invert, ln);

         if(paren){
            write(")");
         }
      }
   }

   protected void writeMethodBody(MethodModel _methodModel) throws CodeGenException{
      writeBlock(_methodModel.getExprHead(), null);
   }

}
