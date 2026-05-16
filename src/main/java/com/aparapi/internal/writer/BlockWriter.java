/**
 * Copyright (c) 2016 - 2018 Syncleus, Inc.
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

import com.aparapi.*;
import com.aparapi.internal.exception.*;
import com.aparapi.internal.instruction.*;
import com.aparapi.internal.instruction.BranchSet.LogicalExpressionNode;
import com.aparapi.internal.instruction.InstructionSet.AccessInstanceField;
import com.aparapi.internal.instruction.BranchSet.*;
import com.aparapi.internal.instruction.InstructionSet.*;
import com.aparapi.internal.model.ClassModel.ConstantPool.*;
import com.aparapi.internal.model.ClassModel.*;
import com.aparapi.internal.model.*;
import com.aparapi.internal.model.ClassModel.ConstantPool.NameAndTypeEntry;

import java.util.*;

/**
 * Base abstract class for converting <code>Aparapi</code> IR to text.<br/>
 *
 *
 * @author gfrost
 *
 */

public abstract class BlockWriter{

   public final static String arrayLengthMangleSuffix = "__javaArrayLength";

   public final static String arrayDimMangleSuffix = "__javaArrayDimension";

   public abstract void write(String _string);

   public void writeln(String _string) {
      write(_string);
      newLine();
   }

   public int indent = 0;

   private final Stack<Set<LocalVariableKey>> localDeclarationScopes = new Stack<Set<LocalVariableKey>>();

   private static final class LocalVariableKey{
      private final int index;

      private final String name;

      private final String descriptor;

      private LocalVariableKey(LocalVariableInfo localVariableInfo) {
         index = localVariableInfo.getVariableIndex();
         name = localVariableInfo.getVariableName();
         descriptor = localVariableInfo.getVariableDescriptor();
      }

      @Override public boolean equals(Object other) {
         if (this == other) {
            return true;
         }
         if (!(other instanceof LocalVariableKey)) {
            return false;
         }
         LocalVariableKey otherKey = (LocalVariableKey) other;
         return (index == otherKey.index) && name.equals(otherKey.name) && descriptor.equals(otherKey.descriptor);
      }

      @Override public int hashCode() {
         int result = index;
         result = (31 * result) + name.hashCode();
         result = (31 * result) + descriptor.hashCode();
         return (result);
      }
   }

   public void in() {
      indent++;
   }

   public void out() {
      indent--;
   }

   public void newLine() {
      write("\n");
      for (int i = 0; i < indent; i++) {
         write("   ");
      }
   }

   public void writeConditionalBranch16(ConditionalBranch16 _branch16, boolean _invert) throws CodeGenException {

      if (_branch16 instanceof If) {
         final If iff = (If) _branch16;

         writeInstruction(iff.getLhs());
         write(_branch16.getOperator().getText(_invert));
         writeInstruction(iff.getRhs());
      } else if (_branch16 instanceof I_IFNULL) {
         final I_IFNULL iff = (I_IFNULL) _branch16;
         writeInstruction(iff.getFirstChild());

         if (_invert) {
            write(" != NULL");
         } else {
            write(" == NULL");
         }

      } else if (_branch16 instanceof I_IFNONNULL) {
         final I_IFNONNULL iff = (I_IFNONNULL) _branch16;
         writeInstruction(iff.getFirstChild());

         if (_invert) {
            write(" == NULL");
         } else {
            write(" != NULL");
         }
      } else if (_branch16 instanceof IfUnary) {
         final IfUnary branch16 = (IfUnary) _branch16;
         final Instruction comparison = branch16.getUnary();
         final ByteCode comparisonByteCode = comparison.getByteCode();
         final String comparisonOperator = _branch16.getOperator().getText(_invert);

         switch (comparisonByteCode) {
            case FCMPG:
            case DCMPG:
            case FCMPL:
            case DCMPL:
               if (Config.verboseComparitor) {
                  write("/* bytecode=" + comparisonByteCode.getName() + " invert=" + _invert + "*/");
               }
               writeInstruction(comparison.getFirstChild());
               write(comparisonOperator);
               writeInstruction(comparison.getLastChild());
               break;
            default:
               if (Config.verboseComparitor) {
                  write("/* default bytecode=" + comparisonByteCode.getName() + " invert=" + _invert + "*/");
               }
               writeInstruction(comparison);
               write(comparisonOperator);
               write("0");
         }
      }
   }

   public void writeComposite(CompositeInstruction instruction) throws CodeGenException {
      if (instruction instanceof CompositeArbitraryScopeInstruction) {
         newLine();

         writeBlock(instruction.getFirstChild(), null);
      } else if (instruction instanceof CompositeIfInstruction) {
         newLine();
         write("if (");
         final Instruction blockStart = writeConditional(instruction.getBranchSet());

         write(")");
         writeBlock(blockStart, null);
      } else if (instruction instanceof CompositeIfElseInstruction) {
         final Instruction blockStart = instruction.getBranchSet().getLast().getNextExpr();
         final Instruction elseGoto = getElseGoto(blockStart);
         writeHoistedLocalDeclarations(blockStart, elseGoto, elseGoto.getNextExpr(), null);

         newLine();
         write("if (");
         writeConditional(instruction.getBranchSet());
         write(")");
         writeBlock(blockStart, elseGoto);
         write(" else ");
         writeBlock(elseGoto.getNextExpr(), null);
      } else if (instruction instanceof CompositeForSunInstruction) {
         newLine();
         write("for (");
         Instruction topBranch = instruction.getFirstChild();
         if (topBranch instanceof AssignToLocalVariable) {
            writeInstruction(topBranch);
            topBranch = topBranch.getNextExpr();
         }
         write("; ");
         final BranchSet branchSet = instruction.getBranchSet();
         final Instruction blockStart = writeConditional(branchSet);

         final Instruction lastGoto = instruction.getLastChild();

         if (branchSet.getFallThrough() == lastGoto) {
            // empty body no delta!
            write(";){}");
         } else {
            final Instruction delta = lastGoto.getPrevExpr();
            write("; ");
            if (!(delta instanceof CompositeInstruction)) {
               writeInstruction(delta);
               write(")");
               writeBlock(blockStart, delta);
            } else {
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

      } else if (instruction instanceof CompositeWhileInstruction) {
         newLine();
         write("while (");
         final BranchSet branchSet = instruction.getBranchSet();
         final Instruction blockStart = writeConditional(branchSet);
         write(")");
         final Instruction lastGoto = instruction.getLastChild();
         writeBlock(blockStart, lastGoto);

      } else if (instruction instanceof CompositeEmptyLoopInstruction) {
         newLine();
         write("for (");
         Instruction topBranch = instruction.getFirstChild();
         if (topBranch instanceof AssignToLocalVariable) {
            writeInstruction(topBranch);
            topBranch = topBranch.getNextExpr();
         }
         write("; ");
         writeConditional(instruction.getBranchSet());
         write(";){}");

      } else if (instruction instanceof CompositeForEclipseInstruction) {
         newLine();
         write("for (");
         Instruction topGoto = instruction.getFirstChild();
         if (topGoto instanceof AssignToLocalVariable) {
            writeInstruction(topGoto);
            topGoto = topGoto.getNextExpr();
         }
         write("; ");
         Instruction last = instruction.getLastChild();
         while (last.getPrevExpr().isBranch()) {
            last = last.getPrevExpr();
         }
         writeConditional(instruction.getBranchSet(), true);
         write("; ");
         final Instruction delta = last.getPrevExpr();
         if (!(delta instanceof CompositeInstruction)) {
            writeInstruction(delta);
            write(")");
            writeBlock(topGoto.getNextExpr(), delta);
         } else {
            write("){");
            in();
            writeSequence(topGoto.getNextExpr(), delta);

            newLine();
            writeSequence(delta, delta.getNextExpr());
            out();
            newLine();
            write("}");

         }

      } else if (instruction instanceof CompositeDoWhileInstruction) {
         newLine();
         write("do");
         Instruction blockStart = instruction.getFirstChild();
         Instruction blockEnd = instruction.getLastChild();
         writeBlock(blockStart, blockEnd);
         write("while(");
         writeConditional(((CompositeInstruction) instruction).getBranchSet(), true);
         write(");");
         newLine();
      }
   }

   public void writeSequence(Instruction _first, Instruction _last) throws CodeGenException {

      for (Instruction instruction = _first; instruction != _last; instruction = instruction.getNextExpr()) {
         if (instruction instanceof CompositeInstruction) {
            writeComposite((CompositeInstruction) instruction);
         } else if (!instruction.getByteCode().equals(ByteCode.NONE)) {
            writeHoistedExpressionLocalDeclarations(instruction);
            newLine();
            writeInstruction(instruction);
            write(";");

         }
      }

   }

   private Instruction getElseGoto(Instruction blockStart) {
      Instruction elseGoto = blockStart;
      while (!(elseGoto.isBranch() && elseGoto.asBranch().isUnconditional())) {
         elseGoto = elseGoto.getNextExpr();
      }
      return (elseGoto);
   }

   private void writeHoistedLocalDeclarations(Instruction thenFirst, Instruction thenLast, Instruction elseFirst, Instruction elseLast) {
      final LinkedHashMap<LocalVariableKey, LocalVariableInfo> thenDeclarations = collectBranchLocalDeclarations(thenFirst, thenLast);
      final LinkedHashMap<LocalVariableKey, LocalVariableInfo> elseDeclarations = collectBranchLocalDeclarations(elseFirst, elseLast);

      // Javac can split one source local into branch-local LVT entries; OpenCL needs one outer declaration.
      for (Map.Entry<LocalVariableKey, LocalVariableInfo> entry : thenDeclarations.entrySet()) {
         final LocalVariableKey key = entry.getKey();
         if (elseDeclarations.containsKey(key) && !isLocalVariableDeclared(key)) {
            final LocalVariableInfo localVariableInfo = entry.getValue();
            currentLocalDeclarationScope().add(key);
            newLine();
            write(convertType(localVariableInfo.getVariableDescriptor(), true, false));
            write(localVariableInfo.getVariableName());
            write(" = 0;");
         }
      }
   }

   private void writeHoistedExpressionLocalDeclarations(Instruction instruction) {
      final LinkedHashMap<LocalVariableKey, LocalVariableInfo> declarations = new LinkedHashMap<LocalVariableKey, LocalVariableInfo>();
      collectNestedExpressionLocalDeclarations(instruction, declarations);
      writeHoistedLocalDeclarations(declarations);
   }

   private void writeHoistedLocalDeclarations(LinkedHashMap<LocalVariableKey, LocalVariableInfo> declarations) {
      for (Map.Entry<LocalVariableKey, LocalVariableInfo> entry : declarations.entrySet()) {
         final LocalVariableKey key = entry.getKey();
         if (!isLocalVariableDeclared(key)) {
            final LocalVariableInfo localVariableInfo = entry.getValue();
            currentLocalDeclarationScope().add(key);
            newLine();
            write(convertType(localVariableInfo.getVariableDescriptor(), true, false));
            write(localVariableInfo.getVariableName());
            write(" = 0;");
         }
      }
   }

   private void collectNestedExpressionLocalDeclarations(Instruction instruction,
         LinkedHashMap<LocalVariableKey, LocalVariableInfo> declarations) {
      if (instruction.getByteCode().equals(ByteCode.MULTI_ASSIGN) || instruction.getByteCode().equals(ByteCode.INLINE_ASSIGN)) {
         collectLocalDeclarations(instruction, declarations);
      }
      for (Instruction child = instruction.getFirstChild(); child != null; child = child.getNextExpr()) {
         collectLocalDeclarations(child, declarations);
      }
   }

   private LinkedHashMap<LocalVariableKey, LocalVariableInfo> collectBranchLocalDeclarations(Instruction first, Instruction last) {
      final LinkedHashMap<LocalVariableKey, LocalVariableInfo> declarations = new LinkedHashMap<LocalVariableKey, LocalVariableInfo>();
      for (Instruction instruction = first; instruction != last; instruction = instruction.getNextExpr()) {
         collectLocalDeclarations(instruction, declarations);
      }
      return (declarations);
   }

   private void collectLocalDeclarations(Instruction instruction, LinkedHashMap<LocalVariableKey, LocalVariableInfo> declarations) {
      if (instruction instanceof AssignToLocalVariable) {
         collectLocalDeclaration((AssignToLocalVariable) instruction, declarations);
      }
      if (instruction.getByteCode().equals(ByteCode.MULTI_ASSIGN)) {
         final MultiAssignInstruction multiAssignInstruction = (MultiAssignInstruction) instruction;
         for (Instruction from = multiAssignInstruction.getFrom(); from != multiAssignInstruction.getTo(); from = from.getNextExpr()) {
            if (from instanceof AssignToLocalVariable) {
               collectLocalDeclaration((AssignToLocalVariable) from, declarations);
            }
         }
      } else if (instruction.getByteCode().equals(ByteCode.INLINE_ASSIGN)) {
         final InlineAssignInstruction inlineAssignInstruction = (InlineAssignInstruction) instruction;
         collectLocalDeclaration(inlineAssignInstruction.getAssignToLocalVariable(), declarations);
      }
      for (Instruction child = instruction.getFirstChild(); child != null; child = child.getNextExpr()) {
         collectLocalDeclarations(child, declarations);
      }
   }

   private void collectLocalDeclaration(AssignToLocalVariable assignToLocalVariable,
         LinkedHashMap<LocalVariableKey, LocalVariableInfo> declarations) {
      final LocalVariableInfo localVariableInfo = assignToLocalVariable.getLocalVariableInfo();
      if (assignToLocalVariable.isDeclaration() && isHoistableLocalVariable(localVariableInfo)) {
         declarations.put(new LocalVariableKey(localVariableInfo), localVariableInfo);
      }
   }

   private boolean isHoistableLocalVariable(LocalVariableInfo localVariableInfo) {
      if (localVariableInfo == null) {
         return (false);
      }
      final String descriptor = localVariableInfo.getVariableDescriptor();
      return (descriptor.length() == 1) && ("ZBCSIJFD".indexOf(descriptor.charAt(0)) >= 0);
   }

   private Set<LocalVariableKey> currentLocalDeclarationScope() {
      if (localDeclarationScopes.empty()) {
         localDeclarationScopes.push(new HashSet<LocalVariableKey>());
      }
      return (localDeclarationScopes.peek());
   }

   private boolean isLocalVariableDeclared(LocalVariableKey key) {
      for (int i = localDeclarationScopes.size() - 1; i >= 0; i--) {
         if (localDeclarationScopes.get(i).contains(key)) {
            return (true);
         }
      }
      return (false);
   }

   private boolean shouldDeclareLocalVariable(AssignToLocalVariable assignToLocalVariable) {
      if (!assignToLocalVariable.isDeclaration()) {
         return (false);
      }
      final LocalVariableInfo localVariableInfo = assignToLocalVariable.getLocalVariableInfo();
      return (localVariableInfo == null) || !isLocalVariableDeclared(new LocalVariableKey(localVariableInfo));
   }

   protected void writeGetterBlock(FieldEntry accessorVariableFieldEntry) {
      write("{");
      in();
      newLine();
      write("return this->");
      write(accessorVariableFieldEntry.getNameAndTypeEntry().getNameUTF8Entry().getUTF8());
      write(";");
      out();
      newLine();

      write("}");
   }

   public void writeBlock(Instruction _first, Instruction _last) throws CodeGenException {
      write("{");
      in();
      localDeclarationScopes.push(new HashSet<LocalVariableKey>());
      try {
         writeSequence(_first, _last);
      } finally {
         localDeclarationScopes.pop();
      }
      out();
      newLine();

      write("}");
   }

   public Instruction writeConditional(BranchSet _branchSet) throws CodeGenException {
      return (writeConditional(_branchSet, false));
   }

   public Instruction writeConditional(BranchSet _branchSet, boolean _invert) throws CodeGenException {

      final LogicalExpressionNode logicalExpression = _branchSet.getLogicalExpression();
      write(_invert ? logicalExpression : logicalExpression.cloneInverted());
      return (_branchSet.getLast().getNextExpr());
   }

   public void write(LogicalExpressionNode _node) throws CodeGenException {
      if (_node instanceof SimpleLogicalExpressionNode) {
         final SimpleLogicalExpressionNode sn = (SimpleLogicalExpressionNode) _node;

         writeConditionalBranch16((ConditionalBranch16) sn.getBranch(), sn.isInvert());
      } else {
         final CompoundLogicalExpressionNode ln = (CompoundLogicalExpressionNode) _node;
         boolean needParenthesis = false;
         final CompoundLogicalExpressionNode parent = (CompoundLogicalExpressionNode) ln.getParent();
         if (parent != null) {
            if (!ln.isAnd() && parent.isAnd()) {
               needParenthesis = true;
            }
         }
         if (needParenthesis) {

            write("(");
         }
         write(ln.getLhs());
         write(ln.isAnd() ? " && " : " || ");
         write(ln.getRhs());
         if (needParenthesis) {

            write(")");
         }
      }
   }

   public String convertType(String _typeDesc, boolean useClassModel, boolean isLocal) {
      return (_typeDesc);
   }

   public String convertCast(String _cast) {
      // Strip parens off cast
      //System.out.println("cast = " + _cast);
      final String raw = convertType(_cast.substring(1, _cast.length() - 1), false, false);
      return ("(" + raw + ")");
   }

   public void writeInstruction(Instruction _instruction) throws CodeGenException {
      if (_instruction instanceof CompositeIfElseInstruction) {
         boolean needParenthesis = isNeedParenthesis(_instruction);
         if(needParenthesis){
             write("(");
         }
         write("(");
         final Instruction lhs = writeConditional(((CompositeInstruction) _instruction).getBranchSet());
         write(")?");
         writeInstruction(lhs);
         write(":");
         writeInstruction(lhs.getNextExpr().getNextExpr());
         if(needParenthesis){
             write(")");
         }
      } else if (_instruction instanceof CompositeInstruction) {
          writeComposite((CompositeInstruction) _instruction);

      } else if (_instruction instanceof I_NEWARRAY) {
          if (_instruction.getParentExpr() instanceof Return) {
              throw new CodeGenException("'newarray' is not allowed after 'return'");
          }

          for (Instruction operand = _instruction.getFirstChild(); operand != null; operand = operand.getNextExpr()) {
              write("[");
              writeInstruction(operand);
              write("]");
          }

      } else if (_instruction instanceof AssignToLocalVariable) {
         final AssignToLocalVariable assignToLocalVariable = (AssignToLocalVariable) _instruction;

         final LocalVariableInfo localVariableInfo = assignToLocalVariable.getLocalVariableInfo();

         if ((_instruction.getFirstChild() instanceof I_NEWARRAY)) {
             if (localVariableInfo == null) {
                 throw new CodeGenException("outOfScope" + _instruction.getThisPC() + " = ");
             } else {
                 final String descriptor = localVariableInfo.getVariableDescriptor();
                 write(convertType(descriptor, true, true));
                 write(localVariableInfo.getVariableName());
             }
         } else {
             if (shouldDeclareLocalVariable(assignToLocalVariable)) {
                 final String descriptor = localVariableInfo.getVariableDescriptor();
                 // Arrays always map to __global arrays
                 if (descriptor.startsWith("[")) {
                     write(" __global ");
                 }
                 write(convertType(descriptor, true, false));
             }
             if (localVariableInfo == null) {
                 throw new CodeGenException("outOfScope" + _instruction.getThisPC() + " = ");
             } else {
                 write(localVariableInfo.getVariableName() + " = ");
             }
         }

         for (Instruction operand = _instruction.getFirstChild(); operand != null; operand = operand.getNextExpr()) {
            writeInstruction(operand);
         }

      } else if (_instruction instanceof AssignToArrayElement) {
         final AssignToArrayElement arrayAssignmentInstruction = (AssignToArrayElement) _instruction;
         writeInstruction(arrayAssignmentInstruction.getArrayRef());
         write("[");
         writeInstruction(arrayAssignmentInstruction.getArrayIndex());
         write("]");
         write(" ");
         write(" = ");
         writeInstruction(arrayAssignmentInstruction.getValue());
      } else if (_instruction instanceof AccessArrayElement) {

         //we're getting an element from an array
         //if the array is a primitive then we just return the value
         //so the generated code looks like
         //arrayName[arrayIndex];
         //but if the array is an object, or multidimensional array, then we want to return
         //a pointer to our index our position in the array.  The code will look like
         //&(arrayName[arrayIndex * this->arrayNameLen_dimension]
         //
         final AccessArrayElement arrayLoadInstruction = (AccessArrayElement) _instruction;

         //object array, get address
         boolean isMultiDimensional = arrayLoadInstruction instanceof I_AALOAD && isMultiDimensionalArray(arrayLoadInstruction);
         if (isMultiDimensional) {
            write("(&");
         }
         writeInstruction(arrayLoadInstruction.getArrayRef());
         write("[");
         writeInstruction(arrayLoadInstruction.getArrayIndex());

         //object array, find the size of each object in the array
         //for 2D arrays, this size is the size of a row.
         if (isMultiDimensional) {
            int dim = 0;
            Instruction load = arrayLoadInstruction.getArrayRef();
            while (load instanceof I_AALOAD) {
               load = load.getFirstChild();
               dim++;
            }

            NameAndTypeEntry nameAndTypeEntry = ((AccessField) load).getConstantPoolFieldEntry().getNameAndTypeEntry();
            if (isMultiDimensionalArray(nameAndTypeEntry)) {
               String arrayName = nameAndTypeEntry.getNameUTF8Entry().getUTF8();
               write(" * this->" + arrayName + arrayDimMangleSuffix + dim);
            }
         }

         write("]");

         //object array, close parentheses
         if (isMultiDimensional) {
            write(")");
         }
      } else if (_instruction instanceof AccessField) {
         final AccessField accessField = (AccessField) _instruction;
         if (accessField instanceof AccessInstanceField) {
            Instruction accessInstanceField = ((AccessInstanceField) accessField).getInstance();
            if (accessInstanceField instanceof CloneInstruction) {
               accessInstanceField = ((CloneInstruction) accessInstanceField).getReal();
            }
            if (!(accessInstanceField instanceof I_ALOAD_0)) {
               writeInstruction(accessInstanceField);
               write(".");
            } else {
               writeThisRef();
            }
         }
         write(accessField.getConstantPoolFieldEntry().getNameAndTypeEntry().getNameUTF8Entry().getUTF8());

      } else if (_instruction instanceof I_ARRAYLENGTH) {

         //getting the length of an array.
         //if this is a primitive array, then this is trivial
         //if we're getting an object array, then we need to find what dimension
         //we're looking at
         int dim = 0;
         Instruction load = _instruction.getFirstChild();
         while (load instanceof I_AALOAD) {
            load = load.getFirstChild();
            dim++;
         }
         NameAndTypeEntry nameAndTypeEntry = ((AccessInstanceField) load).getConstantPoolFieldEntry().getNameAndTypeEntry();
         final String arrayName = nameAndTypeEntry.getNameUTF8Entry().getUTF8();
         String dimSuffix = isMultiDimensionalArray(nameAndTypeEntry) ? Integer.toString(dim) : "";
         write("this->" + arrayName + arrayLengthMangleSuffix + dimSuffix);
      } else if (_instruction instanceof AssignToField) {
         final AssignToField assignedField = (AssignToField) _instruction;

         if (assignedField instanceof AssignToInstanceField) {
            final Instruction accessInstanceField = ((AssignToInstanceField) assignedField).getInstance().getReal();

            if (!(accessInstanceField instanceof I_ALOAD_0)) {
               writeInstruction(accessInstanceField);
               write(".");
            } else {
               writeThisRef();
            }
         }
         write(assignedField.getConstantPoolFieldEntry().getNameAndTypeEntry().getNameUTF8Entry().getUTF8());
         write("=");
         writeInstruction(assignedField.getValueToAssign());
      } else if (_instruction instanceof Constant<?>) {
         final Constant<?> constantInstruction = (Constant<?>) _instruction;
         final Object value = constantInstruction.getValue();

         if (value instanceof Float) {

            final Float f = (Float) value;
            if (f.isNaN()) {
               write("NAN");
            } else if (f.isInfinite()) {
               if (f < 0) {
                  write("-");
               }
               write("INFINITY");
            } else {
               write(value.toString());
               write("f");
            }
         } else if (value instanceof Double) {

            final Double d = (Double) value;
            if (d.isNaN()) {
               write("NAN");
            } else if (d.isInfinite()) {
               if (d < 0) {
                  write("-");
               }
               write("INFINITY");
            } else {
               write(value.toString());
            }
         } else {
            write(value.toString());
            if (value instanceof Long) {
               write("L");
            }
         }

      } else if (_instruction instanceof AccessLocalVariable) {
         final AccessLocalVariable localVariableLoadInstruction = (AccessLocalVariable) _instruction;
         final LocalVariableInfo localVariable = localVariableLoadInstruction.getLocalVariableInfo();
         write(localVariable.getVariableName());
      } else if (_instruction instanceof I_IINC) {
         final I_IINC location = (I_IINC) _instruction;
         final LocalVariableInfo localVariable = location.getLocalVariableInfo();
         final int adjust = location.getAdjust();

         write(localVariable.getVariableName());
         if (adjust == 1) {
            write("++");
         } else if (adjust == -1) {
            write("--");
         } else if (adjust > 1) {
            write("+=" + adjust);
         } else if (adjust < -1) {
            write("-=" + (-adjust));
         }
      } else if (_instruction instanceof BinaryOperator) {
         final BinaryOperator binaryInstruction = (BinaryOperator) _instruction;
         final Instruction parent = binaryInstruction.getParentExpr();
         boolean needsParenthesis = isNeedParenthesis(binaryInstruction);

         if (needsParenthesis) {
            write("(");
         }

         writeInstruction(binaryInstruction.getLhs());

         write(" " + binaryInstruction.getOperator().getText() + " ");
         writeInstruction(binaryInstruction.getRhs());

         if (needsParenthesis) {
            write(")");
         }

      } else if (_instruction instanceof CastOperator) {
         final CastOperator castInstruction = (CastOperator) _instruction;
         //  write("(");
         write(convertCast(castInstruction.getOperator().getText()));

         writeInstruction(castInstruction.getUnary());
         //    write(")");
      } else if (_instruction instanceof UnaryOperator) {
         final UnaryOperator unaryInstruction = (UnaryOperator) _instruction;
         //   write("(");
         write(unaryInstruction.getOperator().getText());

         writeInstruction(unaryInstruction.getUnary());
         //   write(")");
      } else if (_instruction instanceof Return) {

         final Return ret = (Return) _instruction;
         write("return");
         if (ret.getStackConsumeCount() > 0) {
            write("(");
            writeInstruction(ret.getFirstChild());
            write(")");
         }

      } else if (_instruction instanceof MethodCall) {
         final MethodCall methodCall = (MethodCall) _instruction;

         final MethodEntry methodEntry = methodCall.getConstantPoolMethodEntry();

         writeMethod(methodCall, methodEntry);
      } else if (_instruction.getByteCode().equals(ByteCode.CLONE)) {
         final CloneInstruction cloneInstruction = (CloneInstruction) _instruction;
         writeInstruction(cloneInstruction.getReal());
      } else if (_instruction.getByteCode().equals(ByteCode.INCREMENT)) {
         final IncrementInstruction incrementInstruction = (IncrementInstruction) _instruction;

         if (incrementInstruction.isPre()) {
            if (incrementInstruction.isInc()) {
               write("++");
            } else {
               write("--");
            }
         }

         writeInstruction(incrementInstruction.getFieldOrVariableReference());
         if (!incrementInstruction.isPre()) {
            if (incrementInstruction.isInc()) {
               write("++");
            } else {
               write("--");
            }
         }
      } else if (_instruction.getByteCode().equals(ByteCode.MULTI_ASSIGN)) {
         final MultiAssignInstruction multiAssignInstruction = (MultiAssignInstruction) _instruction;
         AssignToLocalVariable from = (AssignToLocalVariable) multiAssignInstruction.getFrom();
         final AssignToLocalVariable last = (AssignToLocalVariable) multiAssignInstruction.getTo();
         final Instruction common = multiAssignInstruction.getCommon();
         final Stack<AssignToLocalVariable> stack = new Stack<AssignToLocalVariable>();

         while (from != last) {
            stack.push(from);
            from = (AssignToLocalVariable) ((Instruction) from).getNextExpr();
         }

         for (AssignToLocalVariable alv = stack.pop(); alv != null; alv = stack.size() > 0 ? stack.pop() : null) {

            final LocalVariableInfo localVariableInfo = alv.getLocalVariableInfo();
            if (shouldDeclareLocalVariable(alv)) {
               write(convertType(localVariableInfo.getVariableDescriptor(), true, false));
            }
            if (localVariableInfo == null) {
               throw new CodeGenException("outOfScope" + _instruction.getThisPC() + " = ");
            } else {
               write(localVariableInfo.getVariableName() + " = ");
            }

         }
         writeInstruction(common);
      } else if (_instruction.getByteCode().equals(ByteCode.INLINE_ASSIGN)) {
         final InlineAssignInstruction inlineAssignInstruction = (InlineAssignInstruction) _instruction;
         final AssignToLocalVariable assignToLocalVariable = inlineAssignInstruction.getAssignToLocalVariable();

         final LocalVariableInfo localVariableInfo = assignToLocalVariable.getLocalVariableInfo();
         if (shouldDeclareLocalVariable(assignToLocalVariable)) {
            // this is bad! we need a general way to hoist up a required declaration
            throw new CodeGenException("/* we can't declare this " + convertType(localVariableInfo.getVariableDescriptor(), true, false)
                  + " here */");
         }
         write(localVariableInfo.getVariableName());
         write("=");
         writeInstruction(inlineAssignInstruction.getRhs());
      } else if (_instruction.getByteCode().equals(ByteCode.FIELD_ARRAY_ELEMENT_ASSIGN)) {
         final FieldArrayElementAssign inlineAssignInstruction = (FieldArrayElementAssign) _instruction;
         final AssignToArrayElement arrayAssignmentInstruction = inlineAssignInstruction.getAssignToArrayElement();

         writeInstruction(arrayAssignmentInstruction.getArrayRef());
         write("[");
         writeInstruction(arrayAssignmentInstruction.getArrayIndex());
         write("]");
         write(" ");
         write(" = ");

         writeInstruction(inlineAssignInstruction.getRhs());
      } else if (_instruction.getByteCode().equals(ByteCode.FIELD_ARRAY_ELEMENT_INCREMENT)) {

         final FieldArrayElementIncrement fieldArrayElementIncrement = (FieldArrayElementIncrement) _instruction;
         final AssignToArrayElement arrayAssignmentInstruction = fieldArrayElementIncrement.getAssignToArrayElement();
         if (fieldArrayElementIncrement.isPre()) {
            if (fieldArrayElementIncrement.isInc()) {
               write("++");
            } else {
               write("--");
            }
         }
         writeInstruction(arrayAssignmentInstruction.getArrayRef());

         write("[");
         writeInstruction(arrayAssignmentInstruction.getArrayIndex());
         write("]");
         if (!fieldArrayElementIncrement.isPre()) {
            if (fieldArrayElementIncrement.isInc()) {
               write("++");
            } else {
               write("--");
            }
         }

      } else if (_instruction.getByteCode().equals(ByteCode.NONE)) {
         // we are done
      } else if (_instruction instanceof Branch) {
          if(_instruction instanceof ConditionalBranch16)
            writeConditionalBranch16((ConditionalBranch16) _instruction, true);
          else
            throw new CodeGenException(String.format("%s -> %04d", _instruction.getByteCode().toString().toLowerCase(), ((Branch) _instruction).getTarget().getThisPC()));
      } else if (_instruction instanceof I_POP) {
         //POP discarded void call return?
         writeInstruction(_instruction.getFirstChild());
      } else {
         throw new CodeGenException(String.format("%s", _instruction.getByteCode().toString().toLowerCase()));
      }

   }

   private boolean isNeedParenthesis(Instruction instruction){
        final Instruction parent = instruction.getParentExpr();
        boolean needsParenthesis = true;

        if (parent instanceof AssignToLocalVariable) {
           needsParenthesis = false;
        } else if (parent instanceof AssignToField) {
           needsParenthesis = false;
        } else if (parent instanceof AssignToArrayElement) {
           needsParenthesis = false;
        } else {
           /**
                       if (parent instanceof BinaryOperator) {
                          BinaryOperator parentBinaryOperator = (BinaryOperator) parent;
                          if (parentBinaryOperator.getOperator().ordinal() > binaryInstruction.getOperator().ordinal()) {
                             needsParenthesis = false;
                          }
                       }
           **/
        }
        return needsParenthesis;
   }

   private boolean isMultiDimensionalArray(NameAndTypeEntry nameAndTypeEntry) {
      return nameAndTypeEntry.getDescriptorUTF8Entry().getUTF8().startsWith("[[");
   }

   private boolean isObjectArray(NameAndTypeEntry nameAndTypeEntry) {
      return nameAndTypeEntry.getDescriptorUTF8Entry().getUTF8().startsWith("[L");
   }

   private boolean isMultiDimensionalArray(final AccessArrayElement arrayLoadInstruction) {
      AccessField accessInstanceField = getUltimateInstanceFieldAccess(arrayLoadInstruction);
       if (accessInstanceField != null) {
           return isMultiDimensionalArray(accessInstanceField.getConstantPoolFieldEntry().getNameAndTypeEntry());
       } else {
           //Arrays can be accessed through local variables instead of instance fields, thus, AccessField instruction
           //can be null.
           AccessLocalVariable accessLocalVariable = getUltimateInstanceLocalVarAccess(arrayLoadInstruction);
           //Directly check for multi-dimensional array...
           return accessLocalVariable.getLocalVariableInfo().getVariableDescriptor().startsWith("[[");
       }      
   }

   private boolean isObjectArray(final AccessArrayElement arrayLoadInstruction) {
      AccessField accessInstanceField = getUltimateInstanceFieldAccess(arrayLoadInstruction);
      return isObjectArray(accessInstanceField.getConstantPoolFieldEntry().getNameAndTypeEntry());
   }

   private AccessField getUltimateInstanceFieldAccess(final AccessArrayElement arrayLoadInstruction) {
      Instruction load = arrayLoadInstruction.getArrayRef();
      while (load instanceof I_AALOAD) {
         load = load.getFirstChild();
      }
      
      if (load instanceof I_ALOAD ||
          load instanceof I_ALOAD_0 || load instanceof I_ALOAD_1 || load instanceof I_ALOAD_2 || load instanceof I_ALOAD_3)  {
          //It is not a Field Access, it is either a constant index local variable (0..3), or is a variable indexed local variable (>3)
          return null;
      }

      return (AccessField) load;
   }

   private AccessLocalVariable getUltimateInstanceLocalVarAccess(AccessArrayElement arrayLoadInstruction) {
       Instruction load = arrayLoadInstruction.getArrayRef();
       while (load instanceof I_AALOAD) {
          load = load.getFirstChild();
       }

       return (AccessLocalVariable)load;
   }   
   
   public void writeMethod(MethodCall _methodCall, MethodEntry _methodEntry) throws CodeGenException {
      boolean noCL = _methodEntry.getOwnerClassModel().getNoCLMethods()
            .contains(_methodEntry.getNameAndTypeEntry().getNameUTF8Entry().getUTF8());
      if (noCL) {
         return;
      }

      if (_methodCall instanceof VirtualMethodCall) {
         final Instruction instanceInstruction = ((VirtualMethodCall) _methodCall).getInstanceReference();
         if (!(instanceInstruction instanceof I_ALOAD_0)) {
            writeInstruction(instanceInstruction);
            write(".");
         } else {
            writeThisRef();
         }
      }
      final int argc = _methodEntry.getStackConsumeCount();
      write(_methodEntry.getNameAndTypeEntry().getNameUTF8Entry().getUTF8());
      write("(");

      for (int arg = 0; arg < argc; arg++) {
         if (arg != 0) {
            write(", ");
         }
         writeInstruction(_methodCall.getArg(arg));
      }
      write(")");

   }

   public void writeThisRef() {
      write("this.");
   }

   public void writeMethodBody(MethodModel _methodModel) throws CodeGenException {
      if (_methodModel.isGetter() && !_methodModel.isNoCL()) {
         FieldEntry accessorVariableFieldEntry = _methodModel.getAccessorVariableFieldEntry();
         writeGetterBlock(accessorVariableFieldEntry);
      } else {
         writeBlock(_methodModel.getExprHead(), null);
      }
   }

   public abstract void write(Entrypoint entryPoint) throws CodeGenException;
}
