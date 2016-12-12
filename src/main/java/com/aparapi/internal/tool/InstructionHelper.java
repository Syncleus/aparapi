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
package com.aparapi.internal.tool;

import com.aparapi.internal.exception.CodeGenException;
import com.aparapi.internal.instruction.Instruction;
import com.aparapi.internal.instruction.InstructionSet.*;
import com.aparapi.internal.model.ClassModel;
import com.aparapi.internal.model.ClassModel.LocalVariableInfo;
import com.aparapi.internal.model.Entrypoint;
import com.aparapi.internal.model.MethodModel;
import com.aparapi.internal.writer.BlockWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InstructionHelper{

   public static class Table{

      final static String spaces = "                                                                                                                        ";

      private final List<Table.Col> cols = new ArrayList<>();

      private int size = 0;

      private int col = 0;

      public static class Col{
         private final List<String> text = new ArrayList<>();

         private int width;

         private String format = "%s";

         public Col(String _format) {
            format = _format;
         }

         public Col() {
            this("%s");
         }

         public void format(Object... args) {
            final String s = String.format(format, args);

            width = Math.max(s.length(), width);
            text.add(s);
         }

         public int size() {
            return (text.size());
         }

         public String pad(String _s, int _width) {
            final int length = _s.length();
            final int padWidth = _width - length;
            final String padded = _s + spaces.substring(0, padWidth);
            return (padded);

         }

         public String get(int _i) {

            return (pad(text.get(_i), width));
         }

         public void header(String _header) {
            text.add(_header);
            width = _header.length();
         }
      }

      public Table(String... _formats) {
         for (final String format : _formats) {
            cols.add(new Col(format));
         }
      }

      public void data(Object... args) {
         cols.get(col++).format(args);
         if (col == cols.size()) {
            col = 0;
            size++;
         }
      }

      @Override public String toString() {
         final StringBuilder sb = new StringBuilder();

         for (int i = 0; i < size; i++) {
            for (final Table.Col col : cols) {
               sb.append(col.get(i));
            }
            sb.append("\n");
         }

         return (sb.toString());
      }

      public void header(String... _headers) {
         for (int i = 0; i < _headers.length; i++) {
            cols.get(i).header(_headers[i]);
         }

         size++;
      }
   }

   public static class StringWriter extends BlockWriter{
      private StringBuilder sb = null;

      public StringWriter(StringBuilder _sb) {
         sb = _sb;
      }

      public StringWriter() {
         sb = new StringBuilder();
      }

      @Override public void write(String _string) {
         sb.append(_string);
      }

      @Override public String toString() {
         return (sb.toString().trim());
      }

      public void clear() {
         sb = new StringBuilder();
      }

      public static String write(MethodModel _methodModel) throws CodeGenException {
         final StringWriter sw = new StringWriter();
         sw.writeMethodBody(_methodModel);
         return (sw.toString());
      }

      @Override public void write(Entrypoint entryPoint) {
         // TODO Auto-generated method stub
      }

      @Override public void writeMethodBody(MethodModel _methodModel) throws CodeGenException {
         super.writeMethodBody(_methodModel);
      }
   }

   public static class BranchVector{
      protected final Instruction from;

      protected final Instruction to;

      protected final Instruction start;

      protected final Instruction end;

      private boolean forward = false;

      public BranchVector(Instruction _from, Instruction _to) {
         from = _from;
         to = _to;
         if (from.getThisPC() > to.getThisPC()) {
            start = _to;
            end = _from;
            forward = false;
         } else {
            start = _from;
            end = _to;
            forward = true;
         }

      }

      public boolean overlaps(BranchVector _other) {
         final boolean overlap = ((start.getThisPC() < _other.start.getThisPC()) && (end.getThisPC() > _other.start.getThisPC()) && (end
               .getThisPC() <= _other.end.getThisPC())) //
               || ((_other.start.getThisPC() < start.getThisPC()) && (_other.start.getThisPC() > start.getThisPC()) && (_other.end
                     .getThisPC() <= end.getThisPC()));

         return (overlap);
      }

      public Instruction getTo() {
         return (to);
      }

      public Instruction getFrom() {
         return (from);
      }

      public int getStartPC() {
         return (start.getThisPC());
      }

      public int getEndPC() {
         return (end.getThisPC());
      }

      public Instruction getStart() {
         return (start);
      }

      public Instruction getEnd() {
         return (end);
      }

      @Override public boolean equals(Object other) {
         return ((other instanceof BranchVector) && ((other == this) || (((BranchVector) other).from
               .equals(((BranchVector) other).to))));
      }

      @Override public int hashCode() {
         return ((from.hashCode() * 31) + to.hashCode());

      }

      public boolean isForward() {
         return (forward);
      }

      @Override public String toString() {
         if (isForward()) {
            return ("forward from " + getStart() + " to " + getEnd());
         }
         return ("backward from " + getEnd() + " to " + getStart());
      }

      public boolean isConditionalBranch() {
         return (getFrom().isBranch() && getFrom().asBranch().isConditional());
      }

      public boolean isBackward() {
         return (!isForward());
      }

      public static final String NONE = " ";

      public static final String THROUGH = "|";

      public static final String CONDITIONAL_START = "?";

      public static final String UNCONDITIONAL_START = "-";

      public static final String TOP_ARROW = "^";

      public static final String BOTTOM_ARROW = "v";

      public String render(int _pc) {
         String returnString = NONE;

         if (isForward()) {
            if (_pc == getStartPC()) {
               returnString = isConditionalBranch() ? CONDITIONAL_START : UNCONDITIONAL_START;
            } else if ((_pc > getStartPC()) && (_pc < getEndPC())) {
               returnString = THROUGH;
            } else if (_pc == getEndPC()) {
               returnString = BOTTOM_ARROW;
            }
         } else {
            if (_pc == getStartPC()) {
               returnString = TOP_ARROW;
            } else if ((_pc > getStartPC()) && (_pc < getEndPC())) {
               returnString = THROUGH;
            } else if (_pc == getEndPC()) {
               returnString = isConditionalBranch() ? CONDITIONAL_START : UNCONDITIONAL_START;
            }
         }
         return returnString;
      }

      public String render(int _startPC, int _thisPC) {
         String returnString = NONE;
         if (isForward()) {
            if (_startPC == getStartPC()) {
               returnString = isConditionalBranch() ? CONDITIONAL_START : UNCONDITIONAL_START;
            } else if ((_thisPC > getStartPC()) && (_startPC < getEndPC())) {
               returnString = THROUGH;
            } else if (_thisPC == getEndPC()) {
               returnString = BOTTOM_ARROW;
            }
         } else {
            if (_startPC == getStartPC()) {
               returnString = TOP_ARROW;
            } else if ((_thisPC > getStartPC()) && (_startPC < getEndPC())) {
               returnString = THROUGH;
            } else if (_thisPC == getEndPC()) {
               returnString = isConditionalBranch() ? CONDITIONAL_START : UNCONDITIONAL_START;
            }
         }

         return returnString;
      }
   }

   public static String getLabel(Instruction instruction, boolean showNumber, boolean showExpressions, boolean verboseBytecodeLabels) {

      final ByteCode byteCode = instruction.getByteCode();
      final StringBuilder label = new StringBuilder();
      if (showNumber) {
         label.append(String.format("%3d: ", instruction.getThisPC()));
      }

      if (!showExpressions) {
         final String byteCodeName = byteCode.getName();

         if (!verboseBytecodeLabels) {
            label.append(byteCodeName);
         } else {
            if (instruction instanceof ConditionalBranch16) {
               final ConditionalBranch16 conditionalBranch16 = (ConditionalBranch16) instruction;
               label.append(conditionalBranch16.getOperator().getText());
               label.append(" -> ");
               label.append(conditionalBranch16.getTarget().getThisPC());
            } else if (instruction instanceof Branch) {
               final Branch branch = (Branch) instruction;
               label.append(" -> ");
               label.append(branch.getTarget().getThisPC());
            } else if (instruction instanceof MethodCall) {
               final MethodCall methodCall = (MethodCall) instruction;
               label.append(methodCall.getConstantPoolMethodEntry().getNameAndTypeEntry().getNameUTF8Entry().getUTF8());
               label.append(" ");
               label.append(methodCall.getConstantPoolMethodEntry().getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8());
            } else if (instruction instanceof OperatorInstruction) {
               final OperatorInstruction operatorInstruction = (OperatorInstruction) instruction;
               label.append(operatorInstruction.getOperator().getText()).append("(").append(byteCodeName).append(")");
            } else if (instruction instanceof FieldReference) {
               final FieldReference field = (FieldReference) instruction;
               label.append(field.getConstantPoolFieldEntry().getNameAndTypeEntry().getNameUTF8Entry().getUTF8());
               label.append(field.getConstantPoolFieldEntry().getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8());
            } else if (instruction instanceof Constant<?>) {
               final Constant<?> constant = (Constant<?>) instruction;
               final Object value = constant.getValue();
               if (value != null) {
                  label.append(value);
               } else {
                  if (instruction instanceof I_ACONST_NULL) {
                     label.append("null");
                  } else {
                     label.append(byteCodeName);
                  }
               }
            } else if (instruction instanceof AssignToLocalVariable) {

               final AssignToLocalVariable assignToLocalVariable = (AssignToLocalVariable) instruction;
               final LocalVariableInfo info = assignToLocalVariable.getLocalVariableInfo();

               if (assignToLocalVariable.isDeclaration()) {
                  label.append(ClassModel.convert(info.getVariableDescriptor()));
               }

               label.append(info == null ? "?" : info.getVariableName());
               label.append("=");

            } else if (instruction instanceof LocalVariableTableIndexAccessor) {
               final LocalVariableTableIndexAccessor localVariableAccessor = (LocalVariableTableIndexAccessor) instruction;
               final LocalVariableInfo info = localVariableAccessor.getLocalVariableInfo();
               label.append(info.getVariableName());

            } else if (instruction instanceof I_IINC) {

               label.append(instruction.getByteCode());
               label.append(" ").append(((I_IINC) instruction).getDelta());
               label.append(" ").append(((I_IINC) instruction).getLocalVariableInfo().getVariableName());
            } else if (instruction instanceof CompositeInstruction) {
               label.append("composite ");
               label.append(instruction.getByteCode());
            } else {
               label.append(byteCodeName);
            }
         }
      } else {
         final StringWriter writer = new StringWriter(label);
         try {
            writer.writeInstruction(instruction);
         } catch (final CodeGenException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            writer.write("// exception " + e.getMessage());
         }
      }

      return (label.toString());
   }

   private static void appendFoldedInstruction(Table _sl, String _prefix, Instruction _instruction) {
      _sl.data(_instruction.getThisPC());
      _sl.data(_prefix + InstructionHelper.getLabel(_instruction, false, false, true));
      final int startPc = _instruction.getStartPC();
      final int thisPc = _instruction.getThisPC();

      final StringBuilder sb = new StringBuilder();
      for (final BranchVector branchInfo : getBranches(_instruction.getMethod())) {
         sb.append(branchInfo.render(startPc, thisPc));
      }

      _sl.data(sb.toString());
      for (Instruction child = _instruction.getFirstChild(); child != null; child = child.getNextExpr()) {
         appendFoldedInstruction(_sl, _prefix + "   ", child);
      }
   }

   static void writeExpression(String _prefix, Instruction _instruction) {
      System.out.println(_prefix + InstructionHelper.getLabel(_instruction, true, true, false));
   }

   static String getFoldedView(MethodModel _methodModel) {
      final Table sl = new Table("%4d", " %s", " %s");
      sl.header("  pc", " expression", " branches");
      for (Instruction root = _methodModel.getExprHead(); root != null; root = root.getNextExpr()) {
         appendFoldedInstruction(sl, "", root);
      }
      return (sl.toString());
   }

   static String createView(MethodModel _methodModel, String _msg, Instruction _head) {
      final Table table = new Table("[%2d-%2d] ", "%-60s", "%s");
      for (Instruction root = _head; root != null; root = root.getNextExpr()) {

         final String label = InstructionHelper.getLabel(root, false, true, false);
         final StringBuilder sb = new StringBuilder();
         for (final BranchVector branchInfo : getBranches(_methodModel)) {
            sb.append(branchInfo.render(root.getThisPC(), root.getStartPC()));
         }
         table.data(root.getStartPC(), root.getThisPC());
         table.data(label);
         table.data(sb);

      }
      return (_msg + "{\n" + table.toString() + "}\n");
   }

   static String createView(MethodModel _methodModel, String _msg, Instruction _head, Instruction _tail,
         int _pcForwardBranchTargetCounts[]) {
      final Table table = new Table("[%2d-%2d] ", "%-40s", "%s", "%3d");

      for (Instruction root = _head; root != null; root = root.getNextExpr()) {
         final String label = InstructionHelper.getLabel(root, false, false, false);
         final StringBuilder sb = new StringBuilder();
         for (final BranchVector branchInfo : getBranches(_methodModel)) {
            sb.append(branchInfo.render(root.getThisPC(), root.getStartPC()));
         }
         table.data(root.getStartPC(), root.getThisPC());
         table.data(" " + label);
         table.data(sb);
         table.data(_pcForwardBranchTargetCounts[root.getStartPC()]);
      }
      final String label = InstructionHelper.getLabel(_tail, false, false, false);
      final StringBuilder sb = new StringBuilder();
      for (final BranchVector branchInfo : getBranches(_methodModel)) {
         sb.append(branchInfo.render(_tail.getThisPC(), _tail.getStartPC()));
      }
      table.data(_tail.getStartPC(), _tail.getThisPC());
      table.data("[" + label + "]");
      table.data(sb);
      table.data(_pcForwardBranchTargetCounts[_tail.getStartPC()]);
      return (_msg + "{\n" + table.toString() + "}\n");
   }

   static String getJavapView(MethodModel _methodModel) {
      final Table table = new Table("%4d", "%4d", " %s", " %s");
      table.header("stack ", "pc ", " mnemonic", " branches");
      int stack = 0;
      for (Instruction i = _methodModel.getPCHead(); i != null; i = i.getNextPC()) {
         stack += i.getStackDelta();
         final int pc = i.getThisPC();
         table.data(stack);
         table.data(pc);
         table.data(InstructionHelper.getLabel(i, false, false, false));
         if (true) {
            final StringBuilder sb = new StringBuilder();
            for (final BranchVector branchInfo : getBranches(_methodModel)) {
               sb.append(branchInfo.render(pc));
            }
            table.data(sb);
         }

      }
      return (table.toString());
   }

   private static final Comparator<BranchVector> branchInfoComparator = new Comparator<BranchVector>(){
      @Override public int compare(BranchVector left, BranchVector right) {
         final int value = left.getFrom().compareTo(right.getFrom());
         return (value);
      }

   };

   static List<BranchVector> getBranches(MethodModel _methodModel) {
      final List<BranchVector> branchVectors = new ArrayList<>();

      for (Instruction instruction = _methodModel.getPCHead(); instruction != null; instruction = instruction.getNextPC()) {
         if (instruction.isBranch()) {
            final Branch branch = (Branch) instruction;
            final Instruction branchTarget = branch.getTarget();
            branchVectors.add(new BranchVector(branch, branchTarget));
         }
      }
      // Sort the branch vectors.  The natural order is essentially by from address. Note that this is not the same as start address.. back edges would be the exceptions
      Collections.sort(branchVectors, branchInfoComparator);

      return (branchVectors);
   }

   void edump(StringBuilder _sb, Instruction i, boolean clone) {
      final String label = InstructionHelper.getLabel(i, false, true, true);

      if (i instanceof CloneInstruction) {
         edump(_sb, i.getReal(), true);
      } else {

         if (i.producesStack()) {
            _sb.append("  ");
         } else {
            _sb.append("! ");
         }

         if (clone) {
            _sb.append("*");
         } else {
            _sb.append(" ");
         }
         _sb.append(i.getThisPC()).append(":").append(label);
      }

   }

   void fdump(int _depth, Instruction i, boolean clone) {
      final String label = i.getByteCode().getName();// InstructionHelper.getLabel(i, false, false, false);

      if (i instanceof CloneInstruction) {
         fdump(_depth, i.getReal(), true);
      } else {
         if (_depth == 0) {
            if (i.producesStack()) {
               System.out.print("  ");
            } else {
               System.out.print("! ");
            }
         }

         if (clone) {
            System.out.print("*");
         } else if (_depth == 0) {
            System.out.print(" ");
         }
         System.out.print(i.getThisPC() + ":" + label);
      }
      if (i.getFirstChild() != null) {
         // int child=0;
         System.out.print("{");
         boolean comma = false;
         for (Instruction ii = i.getFirstChild(); ii != null; ii = ii.getNextExpr()) {
            if (comma) {
               System.out.print(" ,");
            }
            // System.out.print("<"+child+">");
            fdump(_depth + 1, ii, false);
            comma = true;
            //  child++;
         }
         System.out.print("}");
      }
   }

   void dump(String _indent, Instruction i, boolean clone) {
      final String label = InstructionHelper.getLabel(i, true, false, false);

      if (i instanceof CloneInstruction) {
         dump(_indent, i.getReal(), true);
      } else {
         System.out.println(_indent + (clone ? "*" : " ") + label);
      }
      for (Instruction ii = i.getFirstChild(); ii != null; ii = ii.getNextExpr()) {
         dump(_indent + "  ", ii, false);

      }
   }

}
