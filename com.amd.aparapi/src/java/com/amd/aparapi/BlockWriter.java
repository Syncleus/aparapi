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

import java.util.Stack;

import com.amd.aparapi.BranchSet.CompoundLogicalExpressionNode;
import com.amd.aparapi.BranchSet.LogicalExpressionNode;
import com.amd.aparapi.BranchSet.SimpleLogicalExpressionNode;
import com.amd.aparapi.ClassModel.ConstantPool.MethodEntry;
import com.amd.aparapi.ClassModel.LocalVariableInfo;
import com.amd.aparapi.InstructionSet.AccessArrayElement;
import com.amd.aparapi.InstructionSet.AccessField;
import com.amd.aparapi.InstructionSet.AccessInstanceField;
import com.amd.aparapi.InstructionSet.AccessLocalVariable;
import com.amd.aparapi.InstructionSet.AssignToArrayElement;
import com.amd.aparapi.InstructionSet.AssignToField;
import com.amd.aparapi.InstructionSet.AssignToInstanceField;
import com.amd.aparapi.InstructionSet.AssignToLocalVariable;
import com.amd.aparapi.InstructionSet.BinaryOperator;
import com.amd.aparapi.InstructionSet.Branch;
import com.amd.aparapi.InstructionSet.ByteCode;
import com.amd.aparapi.InstructionSet.CastOperator;
import com.amd.aparapi.InstructionSet.CloneInstruction;
import com.amd.aparapi.InstructionSet.CompositeArbitraryScopeInstruction;
import com.amd.aparapi.InstructionSet.CompositeEmptyLoopInstruction;
import com.amd.aparapi.InstructionSet.CompositeForEclipseInstruction;
import com.amd.aparapi.InstructionSet.CompositeDoWhileInstruction;
import com.amd.aparapi.InstructionSet.CompositeForSunInstruction;
import com.amd.aparapi.InstructionSet.CompositeIfElseInstruction;
import com.amd.aparapi.InstructionSet.CompositeIfInstruction;
import com.amd.aparapi.InstructionSet.CompositeInstruction;
import com.amd.aparapi.InstructionSet.CompositeWhileInstruction;
import com.amd.aparapi.InstructionSet.ConditionalBranch16;
import com.amd.aparapi.InstructionSet.Constant;
import com.amd.aparapi.InstructionSet.FieldArrayElementAssign;
import com.amd.aparapi.InstructionSet.FieldArrayElementIncrement;
import com.amd.aparapi.InstructionSet.I_ALOAD_0;
import com.amd.aparapi.InstructionSet.I_ARRAYLENGTH;
import com.amd.aparapi.InstructionSet.I_IFNONNULL;
import com.amd.aparapi.InstructionSet.I_IFNULL;
import com.amd.aparapi.InstructionSet.I_IINC;
import com.amd.aparapi.InstructionSet.I_POP;
import com.amd.aparapi.InstructionSet.If;
import com.amd.aparapi.InstructionSet.IfUnary;
import com.amd.aparapi.InstructionSet.IncrementInstruction;
import com.amd.aparapi.InstructionSet.InlineAssignInstruction;
import com.amd.aparapi.InstructionSet.MethodCall;
import com.amd.aparapi.InstructionSet.MultiAssignInstruction;
import com.amd.aparapi.InstructionSet.Return;
import com.amd.aparapi.InstructionSet.UnaryOperator;
import com.amd.aparapi.InstructionSet.VirtualMethodCall;

/**
 * Base abstract class for converting <code>Aparapi</code> IR to text.<br/>
 * 
 *   
 * @author gfrost
 *
 */

abstract class BlockWriter{

   final static String arrayLengthMangleSuffix = "__javaArrayLength";

   abstract void write(String _string);

   protected void writeln(String _string) {
      write(_string);
      newLine();
   }

   private int indent = 0;

   protected void in() {
      indent++;
   }

   protected void out() {
      indent--;
   }

   protected void newLine() {
      write("\n");
      for (int i = 0; i < indent; i++) {
         write("   ");
      }
   }

   protected void writeConditionalBranch16(ConditionalBranch16 _branch16, boolean _invert) throws CodeGenException {

      if (_branch16 instanceof If) {
         If iff = (If) _branch16;

         writeInstruction(iff.getLhs());
         write(_branch16.getOperator().getText(_invert));
         writeInstruction(iff.getRhs());
      } else if (_branch16 instanceof I_IFNULL) {
         I_IFNULL iff = (I_IFNULL) _branch16;
         writeInstruction(iff.getFirstChild());

         if (_invert) {
            write(" != NULL");
         } else {
            write(" == NULL");
         }

      } else if (_branch16 instanceof I_IFNONNULL) {
         I_IFNONNULL iff = (I_IFNONNULL) _branch16;
         writeInstruction(iff.getFirstChild());

         if (_invert) {
            write(" == NULL");
         } else {
            write(" != NULL");
         }
      } else if (_branch16 instanceof IfUnary) {
         IfUnary branch16 = (IfUnary) _branch16;
         Instruction comparison = branch16.getUnary();
         ByteCode comparisonByteCode = comparison.getByteCode();
         String comparisonOperator = _branch16.getOperator().getText(_invert);

         switch (comparisonByteCode) {
            case FCMPG:
            case DCMPG:
            case FCMPL:
            case DCMPL:
               if (Config.verboseComparitor)
                  write("/* bytecode=" + comparisonByteCode.getName() + " invert=" + _invert + "*/");
               writeInstruction(comparison.getFirstChild());
               write(comparisonOperator);
               writeInstruction(comparison.getLastChild());
               break;
            default:
               if (Config.verboseComparitor)
                  write("/* default bytecode=" + comparisonByteCode.getName() + " invert=" + _invert + "*/");
               writeInstruction(comparison);
               write(comparisonOperator);
               write("0");
         }

      }
   }

   protected void writeComposite(CompositeInstruction instruction) throws CodeGenException {
      if (instruction instanceof CompositeArbitraryScopeInstruction) {
         newLine();

         writeBlock(instruction.getFirstChild(), null);
      } else if (instruction instanceof CompositeIfInstruction) {
         newLine();
         write("if (");
         Instruction blockStart = writeConditional(((CompositeInstruction) instruction).getBranchSet());

         write(")");
         writeBlock(blockStart, null);
      } else if (instruction instanceof CompositeIfElseInstruction) {
         newLine();
         write("if (");
         Instruction blockStart = writeConditional(((CompositeInstruction) instruction).getBranchSet());
         write(")");
         Instruction elseGoto = blockStart;
         while (!(elseGoto.isBranch() && elseGoto.asBranch().isUnconditional())) {
            elseGoto = elseGoto.getNextExpr();
         }
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
         BranchSet branchSet = ((CompositeInstruction) instruction).getBranchSet();
         Instruction blockStart = writeConditional(branchSet);

         Instruction lastGoto = instruction.getLastChild();

         if (branchSet.getFallThrough() == lastGoto) {
            // empty body no delta!
            write(";){}");
         } else {
            Instruction delta = lastGoto.getPrevExpr();
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
         BranchSet branchSet = ((CompositeInstruction) instruction).getBranchSet();
         Instruction blockStart = writeConditional(branchSet);
         write(")");
         Instruction lastGoto = instruction.getLastChild();
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
         writeConditional(((CompositeInstruction) instruction).getBranchSet());
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
         writeConditional(((CompositeInstruction) instruction).getBranchSet(), true);
         write("; ");
         Instruction delta = last.getPrevExpr();
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

   protected void writeSequence(Instruction _first, Instruction _last) throws CodeGenException {

      for (Instruction instruction = _first; instruction != _last; instruction = instruction.getNextExpr()) {
         if (instruction instanceof CompositeInstruction) {
            writeComposite((CompositeInstruction) instruction);
         } else if (!instruction.getByteCode().equals(ByteCode.NONE)) {
            newLine();
            writeInstruction(instruction);
            write(";");

         }
      }

   }

   protected void writeBlock(Instruction _first, Instruction _last) throws CodeGenException {
      write("{");
      in();
      writeSequence(_first, _last);
      out();
      newLine();

      write("}");
   }

   protected Instruction writeConditional(BranchSet _branchSet) throws CodeGenException {
      return (writeConditional(_branchSet, false));
   }

   protected Instruction writeConditional(BranchSet _branchSet, boolean _invert) throws CodeGenException {

      LogicalExpressionNode logicalExpression = _branchSet.getLogicalExpression();
      if (!_invert) {
         logicalExpression.invert();
      }
      write(logicalExpression);
      return (_branchSet.getLast().getNextExpr());
   }

   protected void write(LogicalExpressionNode _node) throws CodeGenException {
      if (_node instanceof SimpleLogicalExpressionNode) {
         SimpleLogicalExpressionNode sn = (SimpleLogicalExpressionNode) _node;

         writeConditionalBranch16((ConditionalBranch16) sn.getBranch(), sn.isInvert());
      } else {
         CompoundLogicalExpressionNode ln = (CompoundLogicalExpressionNode) _node;
         boolean needParenthesis = false;
         CompoundLogicalExpressionNode parent = (CompoundLogicalExpressionNode) ln.getParent();
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

   protected String convertType(String _typeDesc, boolean useClassModel) {
      return (_typeDesc);
   }

   protected String convertCast(String _cast) {
      // Strip parens off cast
      //System.out.println("cast = " + _cast);
      String raw = convertType(_cast.substring(1, _cast.length() - 1), false);
      return ("(" + raw + ")");
   }

   void writeInstruction(Instruction _instruction) throws CodeGenException {
      if (_instruction instanceof CompositeIfElseInstruction) {
         write("(");
         Instruction lhs = writeConditional(((CompositeInstruction) _instruction).getBranchSet());
         write(")?");
         writeInstruction(lhs);
         write(":");
         writeInstruction(lhs.getNextExpr().getNextExpr());
      } else if (_instruction instanceof CompositeInstruction) {
         writeComposite((CompositeInstruction) _instruction);

      } else if (_instruction instanceof AssignToLocalVariable) {
         AssignToLocalVariable assignToLocalVariable = (AssignToLocalVariable) _instruction;

         LocalVariableInfo localVariableInfo = assignToLocalVariable.getLocalVariableInfo();
         if (assignToLocalVariable.isDeclaration()) {
            String descriptor = localVariableInfo.getVariableDescriptor();
            // Arrays always map to __global arrays
            if (descriptor.startsWith("[")) {
               write(" __global ");
            }
            write(convertType(descriptor, true));
         }
         if (localVariableInfo == null) {
            throw new CodeGenException("outOfScope" + _instruction.getThisPC() + " = ");
         } else {
            write(localVariableInfo.getVariableName() + " = ");
         }

         for (Instruction operand = _instruction.getFirstChild(); operand != null; operand = operand.getNextExpr()) {
            writeInstruction(operand);
         }

      } else if (_instruction instanceof AssignToArrayElement) {
         AssignToArrayElement arrayAssignmentInstruction = (AssignToArrayElement) _instruction;
         writeInstruction(arrayAssignmentInstruction.getArrayRef());
         write("[");
         writeInstruction(arrayAssignmentInstruction.getArrayIndex());
         write("]");
         write(" ");
         write(" = ");
         writeInstruction(arrayAssignmentInstruction.getValue());
      } else if (_instruction instanceof AccessArrayElement) {
         AccessArrayElement arrayLoadInstruction = (AccessArrayElement) _instruction;
         writeInstruction(arrayLoadInstruction.getArrayRef());
         write("[");
         writeInstruction(arrayLoadInstruction.getArrayIndex());
         write("]");
      } else if (_instruction instanceof AccessField) {
         AccessField accessField = (AccessField) _instruction;
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
         AccessInstanceField child = (AccessInstanceField) _instruction.getFirstChild();
         String arrayName = child.getConstantPoolFieldEntry().getNameAndTypeEntry().getNameUTF8Entry().getUTF8();
         write("this->" + arrayName + arrayLengthMangleSuffix);
      } else if (_instruction instanceof AssignToField) {
         AssignToField assignedField = (AssignToField) _instruction;

         if (assignedField instanceof AssignToInstanceField) {
            Instruction accessInstanceField = ((AssignToInstanceField) assignedField).getInstance().getReal();

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
         Constant<?> constantInstruction = (Constant<?>) _instruction;
         Object value = constantInstruction.getValue();

         if (value instanceof Float) {

            Float f = (Float) value;
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

            Double d = (Double) value;
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
         AccessLocalVariable localVariableLoadInstruction = (AccessLocalVariable) _instruction;
         LocalVariableInfo localVariable = localVariableLoadInstruction.getLocalVariableInfo();
         write(localVariable.getVariableName());
      } else if (_instruction instanceof I_IINC) {
         I_IINC location = (I_IINC) _instruction;
         LocalVariableInfo localVariable = location.getLocalVariableInfo();
         int adjust = location.getAdjust();

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
         BinaryOperator binaryInstruction = (BinaryOperator) _instruction;
         Instruction parent = binaryInstruction.getParentExpr();
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
         CastOperator castInstruction = (CastOperator) _instruction;
         //  write("(");
         write(convertCast(castInstruction.getOperator().getText()));

         writeInstruction(castInstruction.getUnary());
         //    write(")");
      } else if (_instruction instanceof UnaryOperator) {
         UnaryOperator unaryInstruction = (UnaryOperator) _instruction;
         //   write("(");
         write(unaryInstruction.getOperator().getText());

         writeInstruction(unaryInstruction.getUnary());
         //   write(")");
      } else if (_instruction instanceof Return) {

         Return ret = (Return) _instruction;
         write("return");
         if (ret.getStackConsumeCount() > 0) {
            write("(");
            writeInstruction(ret.getFirstChild());
            write(")");
         }

      } else if (_instruction instanceof MethodCall) {
         MethodCall methodCall = (MethodCall) _instruction;

         MethodEntry methodEntry = methodCall.getConstantPoolMethodEntry();

         writeMethod(methodCall, methodEntry);
      } else if (_instruction.getByteCode().equals(ByteCode.CLONE)) {
         CloneInstruction cloneInstruction = (CloneInstruction) _instruction;
         writeInstruction(cloneInstruction.getReal());
      } else if (_instruction.getByteCode().equals(ByteCode.INCREMENT)) {
         IncrementInstruction incrementInstruction = (IncrementInstruction) _instruction;

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
         MultiAssignInstruction multiAssignInstruction = (MultiAssignInstruction) _instruction;
         AssignToLocalVariable from = (AssignToLocalVariable) multiAssignInstruction.getFrom();
         AssignToLocalVariable last = (AssignToLocalVariable) multiAssignInstruction.getTo();
         Instruction common = multiAssignInstruction.getCommon();
         Stack<AssignToLocalVariable> stack = new Stack<AssignToLocalVariable>();

         while (from != last) {
            stack.push(from);
            from = (AssignToLocalVariable) ((Instruction) from).getNextExpr();
         }

         for (AssignToLocalVariable alv = stack.pop(); alv != null; alv = stack.size() > 0 ? stack.pop() : null) {

            LocalVariableInfo localVariableInfo = alv.getLocalVariableInfo();
            if (alv.isDeclaration()) {
               write(convertType(localVariableInfo.getVariableDescriptor(), true));
            }
            if (localVariableInfo == null) {
               throw new CodeGenException("outOfScope" + _instruction.getThisPC() + " = ");
            } else {
               write(localVariableInfo.getVariableName() + " = ");
            }

         }
         writeInstruction(common);
      } else if (_instruction.getByteCode().equals(ByteCode.INLINE_ASSIGN)) {
         InlineAssignInstruction inlineAssignInstruction = (InlineAssignInstruction) _instruction;
         AssignToLocalVariable assignToLocalVariable = inlineAssignInstruction.getAssignToLocalVariable();

         LocalVariableInfo localVariableInfo = assignToLocalVariable.getLocalVariableInfo();
         if (assignToLocalVariable.isDeclaration()) {
            // this is bad! we need a general way to hoist up a required declaration
            throw new CodeGenException("/* we can't declare this " + convertType(localVariableInfo.getVariableDescriptor(), true)
                  + " here */");
         }
         write(localVariableInfo.getVariableName());
         write("=");
         writeInstruction(inlineAssignInstruction.getRhs());
      } else if (_instruction.getByteCode().equals(ByteCode.FIELD_ARRAY_ELEMENT_ASSIGN)) {
         FieldArrayElementAssign inlineAssignInstruction = (FieldArrayElementAssign) _instruction;
         AssignToArrayElement arrayAssignmentInstruction = inlineAssignInstruction.getAssignToArrayElement();

         writeInstruction(arrayAssignmentInstruction.getArrayRef());
         write("[");
         writeInstruction(arrayAssignmentInstruction.getArrayIndex());
         write("]");
         write(" ");
         write(" = ");

         writeInstruction(inlineAssignInstruction.getRhs());
      } else if (_instruction.getByteCode().equals(ByteCode.FIELD_ARRAY_ELEMENT_INCREMENT)) {

         FieldArrayElementIncrement fieldArrayElementIncrement = (FieldArrayElementIncrement) _instruction;
         AssignToArrayElement arrayAssignmentInstruction = fieldArrayElementIncrement.getAssignToArrayElement();
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
         throw new CodeGenException(String.format("%s -> %04d", _instruction.getByteCode().toString().toLowerCase(),
               ((Branch) _instruction).getTarget().getThisPC()));
      } else if (_instruction instanceof I_POP) {
         //POP discarded void call return?
         writeInstruction(_instruction.getFirstChild());
      } else {
         throw new CodeGenException(String.format("%s", _instruction.getByteCode().toString().toLowerCase()));
      }

   }

   protected void writeMethod(MethodCall _methodCall, MethodEntry _methodEntry) throws CodeGenException {

      if (_methodCall instanceof VirtualMethodCall) {
         Instruction instanceInstruction = ((VirtualMethodCall) _methodCall).getInstanceReference();
         if (!(instanceInstruction instanceof I_ALOAD_0)) {
            writeInstruction(instanceInstruction);
            write(".");
         } else {
            writeThisRef();
         }
      }
      int argc = _methodEntry.getStackConsumeCount();
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

   protected void writeThisRef() {
      write("this.");
   }

   protected void writeMethodBody(MethodModel _methodModel) throws CodeGenException {
      writeBlock(_methodModel.getExprHead(), null);
   }

   abstract void write(Entrypoint entryPoint) throws CodeGenException;

}
