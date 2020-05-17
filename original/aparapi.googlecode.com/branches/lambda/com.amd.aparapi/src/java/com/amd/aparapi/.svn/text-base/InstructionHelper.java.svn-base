package com.amd.aparapi;

import com.amd.aparapi.ClassModel.AttributePool.LocalVariableTableEntry.LocalVariableInfo;
import com.amd.aparapi.InstructionSet.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InstructionHelper{


   static class BranchVector{
      protected Instruction from;

      protected Instruction to;

      protected Instruction start;

      protected Instruction end;

      private boolean forward = false;

      BranchVector(Instruction _from, Instruction _to){
         from = _from;
         to = _to;
         if(from.getThisPC() > to.getThisPC()){
            start = _to;
            end = _from;
            forward = false;
         }else{
            start = _from;
            end = _to;
            forward = true;
         }

      }

      boolean overlaps(BranchVector _other){
         boolean overlap = (start.getThisPC() < _other.start.getThisPC() && end.getThisPC() > _other.start.getThisPC() && end
               .getThisPC() <= _other.end.getThisPC()) //
               || (_other.start.getThisPC() < start.getThisPC() && _other.start.getThisPC() > start.getThisPC() && _other.end
               .getThisPC() <= end.getThisPC());

         return (overlap);
      }

      Instruction getTo(){
         return (to);
      }

      Instruction getFrom(){
         return (from);
      }

      int getStartPC(){
         return (start.getThisPC());
      }

      int getEndPC(){
         return (end.getThisPC());
      }

      Instruction getStart(){
         return (start);
      }

      Instruction getEnd(){
         return (end);
      }

      @Override
      public boolean equals(Object other){
         return (other instanceof BranchVector && ((other == this) || (((BranchVector) other).from
               .equals(((BranchVector) other).to))));
      }

      @Override
      public int hashCode(){
         return (from.hashCode() * 31 + to.hashCode());

      }

      boolean isForward(){
         return (forward);
      }

      @Override
      public String toString(){
         if(isForward()){
            return ("forward from " + getStart() + " to " + getEnd());
         }
         return ("backward from " + getEnd() + " to " + getStart());
      }

      boolean isConditionalBranch(){
         return (getFrom().isBranch() && getFrom().asBranch().isConditional());
      }

      boolean isBackward(){
         return (!isForward());
      }

      static final String NONE = " ";

      static final String THROUGH = "|";

      static final String CONDITIONAL_START = "?";

      static final String UNCONDITIONAL_START = "-";

      static final String TOP_ARROW = "^";

      static final String BOTTOM_ARROW = "v";

      String render(int _pc){
         String returnString = NONE;

         if(isForward()){
            if(_pc == getStartPC()){
               returnString = isConditionalBranch() ? CONDITIONAL_START : UNCONDITIONAL_START;
            }else if(_pc > getStartPC() && _pc < getEndPC()){
               returnString = THROUGH;
            }else if(_pc == getEndPC()){
               returnString = BOTTOM_ARROW;
            }
         }else{
            if(_pc == getStartPC()){
               returnString = TOP_ARROW;
            }else if(_pc > getStartPC() && _pc < getEndPC()){
               returnString = THROUGH;
            }else if(_pc == getEndPC()){
               returnString = isConditionalBranch() ? CONDITIONAL_START : UNCONDITIONAL_START;
            }
         }
         return returnString;
      }

      String render(int _startPC, int _thisPC){
         String returnString = NONE;
         if(isForward()){
            if(_startPC == getStartPC()){
               returnString = isConditionalBranch() ? CONDITIONAL_START : UNCONDITIONAL_START;
            }else if(_thisPC > getStartPC() && _startPC < getEndPC()){
               returnString = THROUGH;
            }else if(_thisPC == getEndPC()){
               returnString = BOTTOM_ARROW;
            }
         }else{
            if(_startPC == getStartPC()){
               returnString = TOP_ARROW;
            }else if(_thisPC > getStartPC() && _startPC < getEndPC()){
               returnString = THROUGH;
            }else if(_thisPC == getEndPC()){
               returnString = isConditionalBranch() ? CONDITIONAL_START : UNCONDITIONAL_START;
            }
         }
         return returnString;
      }

   }

   static String getLabel(Instruction instruction, boolean showNumber, boolean verboseBytecodeLabels){

      ByteCode byteCode = instruction.getByteCode();
      final StringBuilder label = new StringBuilder();
      if(showNumber){
         label.append(String.format("%3d: ", instruction.getThisPC()));
      }


      String byteCodeName = byteCode.getName();

      if(!verboseBytecodeLabels){
         label.append(byteCodeName);
      }else{
         label.append(byteCodeName).append(" ");
         if(instruction instanceof ConditionalBranch16){
            ConditionalBranch16 conditionalBranch16 = (ConditionalBranch16) instruction;
            label.append(conditionalBranch16.getOperator().getText());
            label.append(" -> ");
            label.append(conditionalBranch16.getTarget().getThisPC());
         }else if(instruction instanceof Branch){
            Branch branch = (Branch) instruction;
            label.append(" -> ");
            label.append(branch.getTarget().getThisPC());
         }else if(instruction instanceof MethodCall){
            MethodCall methodCall = (MethodCall) instruction;
            label.append(methodCall.getConstantPoolMethodEntry().getNameAndTypeEntry().getNameUTF8Entry().getUTF8());
            label.append(" ");
            label.append(methodCall.getConstantPoolMethodEntry().getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8());
         }else if(instruction instanceof OperatorInstruction){
            OperatorInstruction operatorInstruction = (OperatorInstruction) instruction;
            label.append(operatorInstruction.getOperator().getText() + "(" + byteCodeName + ")");
         }else if(instruction instanceof FieldReference){
            FieldReference field = (FieldReference) instruction;
            label.append(field.getConstantPoolFieldEntry().getNameAndTypeEntry().getNameUTF8Entry().getUTF8());
            label.append(field.getConstantPoolFieldEntry().getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8());
         }else if(instruction instanceof Constant<?>){
            Constant<?> constant = (Constant<?>) instruction;
            Object value = constant.getValue();
            if(value != null){
               label.append(value);
            }else{
               if(instruction instanceof I_ACONST_NULL){
                  label.append("null");
               }else{
                  label.append(byteCodeName);
               }
            }
         }else if(instruction instanceof AssignToLocalVariable){

            AssignToLocalVariable assignToLocalVariable = (AssignToLocalVariable) instruction;
            LocalVariableInfo info = assignToLocalVariable.getLocalVariableInfo();

            if(assignToLocalVariable.isDeclaration()){


               label.append(TypeHelper.convert(info.getVariableDescriptor()));
            }

            label.append(info == null ? "?" : info.getVariableName());
            label.append("=");

         }else if(instruction instanceof LocalVariableTableIndexAccessor){
            LocalVariableTableIndexAccessor localVariableAccessor = (LocalVariableTableIndexAccessor) instruction;
            LocalVariableInfo info = localVariableAccessor.getLocalVariableInfo();
            label.append(info.getVariableName());

         }else if(instruction instanceof I_IINC){

            label.append(instruction.getByteCode());
            label.append(" " + ((I_IINC) instruction).getDelta());
            label.append(" " + ((I_IINC) instruction).getLocalVariableInfo().getVariableName());
         }else if(instruction instanceof CompositeInstruction){
            label.append("composite ");
            label.append(instruction.getByteCode());
         }
      }

      return (label.toString());
   }

   static private void appendFoldedInstruction(Table _sl, String _prefix, Instruction _instruction){
      _sl.data(_instruction.getThisPC());
      _sl.data(_prefix + InstructionHelper.getLabel(_instruction, false, false));
      int startPc = _instruction.getStartPC();
      int thisPc = _instruction.getThisPC();

      StringBuilder sb = new StringBuilder();
      for(BranchVector branchInfo : getBranches(_instruction.getMethod())){
         sb.append(branchInfo.render(startPc, thisPc));
      }

      _sl.data(sb.toString());
      for(Instruction child = _instruction.getFirstChild(); child != null; child = child.getNextExpr()){
         appendFoldedInstruction(_sl, _prefix + "   ", child);
      }
   }

   static void writeExpression(String _prefix, Instruction _instruction){
      System.out.println(_prefix + InstructionHelper.getLabel(_instruction, true, false));
   }

   public static String getFoldedView(MethodModel _methodModel){
      Table sl = new Table("%4d", " %s", " %s");
      sl.header("  pc", " expression", " branches");
      for(Instruction root = _methodModel.getExprHead(); root != null; root = root.getNextExpr()){
         appendFoldedInstruction(sl, "", root);
      }
      return (sl.toString());
   }

   static String createView(MethodModel _methodModel, String _msg, Instruction _head){
      Table table = new Table("[%2d-%2d] ", "%-60s", "%s");
      for(Instruction root = _head; root != null; root = root.getNextExpr()){

         String label = InstructionHelper.getLabel(root, true, false);
         StringBuilder sb = new StringBuilder();
         for(BranchVector branchInfo : getBranches(_methodModel.getMethod())){
            sb.append(branchInfo.render(root.getThisPC(), root.getStartPC()));
         }
         table.data(root.getStartPC(), root.getThisPC());
         table.data(label);
         table.data(sb);

      }
      return (_msg + "{\n" + table.toString() + "}\n");
   }

   static String createView(MethodModel _methodModel, String _msg, Instruction _head, Instruction _tail,
                            int _pcForwardBranchTargetCounts[]){
      Table table = new Table("[%2d-%2d] ", "%-40s", "%s", "%3d");

      for(Instruction root = _head; root != null; root = root.getNextExpr()){
         String label = InstructionHelper.getLabel(root, false, false);
         StringBuilder sb = new StringBuilder();
         for(BranchVector branchInfo : getBranches(_methodModel.getMethod())){
            sb.append(branchInfo.render(root.getThisPC(), root.getStartPC()));
         }
         table.data(root.getStartPC(), root.getThisPC());
         table.data(" " + label);
         table.data(sb);
         table.data(_pcForwardBranchTargetCounts[root.getStartPC()]);
      }
      String label = InstructionHelper.getLabel(_tail, false, false);
      StringBuilder sb = new StringBuilder();
      for(BranchVector branchInfo : getBranches(_methodModel.getMethod())){
         sb.append(branchInfo.render(_tail.getThisPC(), _tail.getStartPC()));
      }
      table.data(_tail.getStartPC(), _tail.getThisPC());
      table.data("[" + label + "]");
      table.data(sb);
      table.data(_pcForwardBranchTargetCounts[_tail.getStartPC()]);
      return (_msg + "{\n" + table.toString() + "}\n");
   }

   public static String getJavapView(ClassModel.ClassModelMethod _method){
      Table table = new Table("%4d", "%4d", " %s", " %s");
      table.header("stack ", "pc ", " mnemonic", " branches");
      int stack = 0;
      for(Instruction i : _method.getInstructionMap().values()){
         stack += i.getStackDelta();
         int pc = i.getThisPC();
         table.data(stack);
         table.data(pc);
         table.data(InstructionHelper.getLabel(i, false, true));
         StringBuilder sb = new StringBuilder();
         for(BranchVector branchInfo : getBranches(_method)){
            sb.append(branchInfo.render(pc));
         }
         table.data(sb);
      }
      return (table.toString());
   }

   public static String getJavapView(MethodModel _methodModel){
      return (getJavapView(_methodModel.getMethod()));

   }

   private static Comparator<BranchVector> branchInfoComparator = new Comparator<BranchVector>(){
      @Override
      public int compare(BranchVector left, BranchVector right){
         int value = left.getFrom().compareTo(right.getFrom());
         return (value);
      }

   };

   static List<BranchVector> getBranches(ClassModel.ClassModelMethod _method){
      List<BranchVector> branchVectors = new ArrayList<BranchVector>();

      for(Instruction instruction : _method.getInstructionMap().values()){
         if(instruction.isBranch()){
            Branch branch = (Branch) instruction;
            Instruction branchTarget = branch.getTarget();
            branchVectors.add(new BranchVector(branch, branchTarget));
         }
      }
      // Sort the branch vectors.  The natural order is essentially by from address. Note that this is not the same as start address.. back edges would be the exceptions
      Collections.sort(branchVectors, branchInfoComparator);

      return (branchVectors);
   }

   void edump(StringBuilder _sb, Instruction i, boolean clone){
      String label = InstructionHelper.getLabel(i, true, true);

      if(i instanceof CloneInstruction){
         edump(_sb, ((CloneInstruction) i).getReal(), true);
      }else{

         if(i.producesStack()){
            _sb.append("  ");
         }else{
            _sb.append("! ");
         }

         if(clone){
            _sb.append("*");
         }else{
            _sb.append(" ");
         }
         _sb.append(i.getThisPC() + ":" + label);
      }

   }

   void fdump(int _depth, Instruction i, boolean clone){
      String label = i.getByteCode().getName();// InstructionHelper.getLabel(i, false, false, false);

      if(i instanceof CloneInstruction){
         fdump(_depth, ((CloneInstruction) i).getReal(), true);
      }else{
         if(_depth == 0){
            if(i.producesStack()){
               System.out.print("  ");
            }else{
               System.out.print("! ");
            }
         }

         if(clone){
            System.out.print("*");
         }else if(_depth == 0){
            System.out.print(" ");
         }
         System.out.print(i.getThisPC() + ":" + label);
      }
      if(i.getFirstChild() != null){
         // int child=0;
         System.out.print("{");
         boolean comma = false;
         for(Instruction ii = i.getFirstChild(); ii != null; ii = ii.getNextExpr()){
            if(comma){
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

   void dump(String _indent, Instruction i, boolean clone){
      String label = InstructionHelper.getLabel(i, false, false);

      if(i instanceof CloneInstruction){
         dump(_indent, ((CloneInstruction) i).getReal(), true);
      }else{
         System.out.println(_indent + (clone ? "*" : " ") + label);
      }
      for(Instruction ii = i.getFirstChild(); ii != null; ii = ii.getNextExpr()){
         dump(_indent + "  ", ii, false);

      }
   }

}
