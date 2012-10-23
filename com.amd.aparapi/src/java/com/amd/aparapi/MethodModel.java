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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amd.aparapi.ClassModel.ClassModelMethod;
import com.amd.aparapi.ClassModel.ConstantPool;
import com.amd.aparapi.ClassModel.ConstantPool.FieldEntry;
import com.amd.aparapi.ClassModel.ConstantPool.MethodReferenceEntry;
import com.amd.aparapi.ClassModel.ConstantPool.MethodReferenceEntry.Arg;
import com.amd.aparapi.ClassModel.LocalVariableInfo;
import com.amd.aparapi.ClassModel.LocalVariableTableEntry;
import com.amd.aparapi.ClassModel.MethodDescription;
import com.amd.aparapi.InstructionPattern.InstructionMatch;
import com.amd.aparapi.InstructionSet.AccessArrayElement;
import com.amd.aparapi.InstructionSet.AccessField;
import com.amd.aparapi.InstructionSet.AccessInstanceField;
import com.amd.aparapi.InstructionSet.AccessLocalVariable;
import com.amd.aparapi.InstructionSet.AssignToArrayElement;
import com.amd.aparapi.InstructionSet.AssignToInstanceField;
import com.amd.aparapi.InstructionSet.AssignToLocalVariable;
import com.amd.aparapi.InstructionSet.Branch;
import com.amd.aparapi.InstructionSet.CastOperator;
import com.amd.aparapi.InstructionSet.CloneInstruction;
import com.amd.aparapi.InstructionSet.DUP;
import com.amd.aparapi.InstructionSet.FieldArrayElementAssign;
import com.amd.aparapi.InstructionSet.FieldArrayElementIncrement;
import com.amd.aparapi.InstructionSet.I_AASTORE;
import com.amd.aparapi.InstructionSet.I_ARETURN;
import com.amd.aparapi.InstructionSet.I_ATHROW;
import com.amd.aparapi.InstructionSet.I_BASTORE;
import com.amd.aparapi.InstructionSet.I_CASTORE;
import com.amd.aparapi.InstructionSet.I_DUP;
import com.amd.aparapi.InstructionSet.I_DUP2;
import com.amd.aparapi.InstructionSet.I_DUP_X1;
import com.amd.aparapi.InstructionSet.I_DUP_X2;
import com.amd.aparapi.InstructionSet.I_GETSTATIC;
import com.amd.aparapi.InstructionSet.I_IADD;
import com.amd.aparapi.InstructionSet.I_ICONST_1;
import com.amd.aparapi.InstructionSet.I_IINC;
import com.amd.aparapi.InstructionSet.I_INVOKEINTERFACE;
import com.amd.aparapi.InstructionSet.I_LOOKUPSWITCH;
import com.amd.aparapi.InstructionSet.I_MONITORENTER;
import com.amd.aparapi.InstructionSet.I_MONITOREXIT;
import com.amd.aparapi.InstructionSet.I_PUTFIELD;
import com.amd.aparapi.InstructionSet.I_PUTSTATIC;
import com.amd.aparapi.InstructionSet.I_TABLESWITCH;
import com.amd.aparapi.InstructionSet.IncrementInstruction;
import com.amd.aparapi.InstructionSet.InlineAssignInstruction;
import com.amd.aparapi.InstructionSet.LoadSpec;
import com.amd.aparapi.InstructionSet.MethodCall;
import com.amd.aparapi.InstructionSet.MultiAssignInstruction;
import com.amd.aparapi.InstructionSet.New;
import com.amd.aparapi.InstructionSet.Return;
import com.amd.aparapi.InstructionSet.StoreSpec;

class MethodModel{
   static Logger logger = Logger.getLogger(Config.getLoggerName());

   private ExpressionList expressionList;

   private ClassModelMethod method;

   /**
      True is an indication to use the fp64 pragma
   */
   private boolean usesDoubles;

   /**
      True is an indication to use the byte addressable store pragma
   */
   private boolean usesByteWrites;

   private boolean methodIsGetter;

   private boolean methodIsSetter;

   // Only setters can use putfield
   private boolean usesPutfield;

   private FieldEntry accessorVariableFieldEntry;

   boolean isGetter() {
      return methodIsGetter;
   }

   boolean isSetter() {
      return methodIsSetter;
   }

   boolean methodUsesPutfield() {
      return usesPutfield;
   }

   ClassModelMethod getMethod() {
      return method;
   }

   FieldEntry getAccessorVariableFieldEntry() {
      return accessorVariableFieldEntry;
   }

   private Set<MethodModel> calledMethods = new HashSet<MethodModel>();

   Set<MethodModel> getCalledMethods() {
      return calledMethods;
   }

   void checkForRecursion(Set<MethodModel> transitiveCalledMethods) throws AparapiException {

      if (transitiveCalledMethods.contains(this)) {
         throw new ClassParseException(ClassParseException.TYPE.RECURSION, getName());
      }

      // Add myself
      transitiveCalledMethods.add(this);

      // For each callee, send him a copy of the call chain up to this method
      Iterator<MethodModel> cmi = getCalledMethods().iterator();
      while (cmi.hasNext()) {
         MethodModel next = cmi.next();
         next.checkForRecursion(transitiveCalledMethods);
      }

      // Done examining this call path, remove myself
      transitiveCalledMethods.remove(this);
   }

   /**
    * After we have folded the top level instructions this root list will contain a list of all of the 'root' instructions (stores/loops/conditionals) 
    * We are going to build a linked list.  Here we track the head and tail
    */
   private Instruction pcTail = null;

   private Instruction pcHead = null;

   /**
    * Look at each instruction for use of long/double or byte writes which
    * require pragmas to be used in the OpenCL source
    * 
    */
   void setRequiredPragmas(Instruction instruction) {
      boolean setDouble = instruction.getByteCode().usesDouble();
      if (setDouble) {
         usesDoubles = true;
         if (logger.isLoggable(Level.FINE)) {
            logger.fine("Found D on =" + instruction + " in " + getName());
         }
      }
      if ((instruction instanceof I_BASTORE) || (instruction instanceof I_CASTORE /* || instruction instanceof I_SASTORE */)) {
         usesByteWrites = true;
         if (usesByteWrites && logger.isLoggable(Level.FINE)) {
            logger.fine("Found Byte Addressable Store on =" + instruction + " in " + getName());
         }
      }
   }

   boolean requiresDoublePragma() {
      return usesDoubles;
   }

   boolean requiresByteAddressableStorePragma() {
      return usesByteWrites;
   }

   /**
    * Create a linked list of instructions (from pcHead to pcTail).
    * 
    * Returns a map of int (pc) to Instruction which to allow us to quickly get from a bytecode offset to the appropriate instruction. 
    * 
    * Note that not all int values from 0 to code.length values will map to a valid instruction, if pcMap.get(n) == null then this implies
    * that 'n' is not the start of an instruction
    * 
    * So either pcMap.get(i)== null or pcMap.get(i).getThisPC()==i
    * 
    * @return Map<Integer, Instruction> the returned pc to Instruction map
    */
   Map<Integer, Instruction> createListOfInstructions() throws ClassParseException {
      Map<Integer, Instruction> pcMap = new HashMap<Integer, Instruction>();
      byte[] code = method.getCode();

      // We create a byteReader for reading the bytes from the code array
      ByteReader codeReader = new ByteReader(code);
      while (codeReader.hasMore()) {
         // Create an instruction from code reader's current position
         int pc = codeReader.getOffset();
         Instruction instruction = InstructionSet.ByteCode.create(this, codeReader);

         if ((!Config.enablePUTFIELD) && (instruction instanceof I_PUTFIELD)) {
            // Special case putfield handling to allow object setter processing
            // and bail later if necessary
            //throw new ClassParseException("We don't support putfield instructions");
            usesPutfield = true;
         }

         if ((!Config.enableARETURN) && (instruction instanceof I_ARETURN)) {
            throw new ClassParseException(instruction, ClassParseException.TYPE.ARRAY_RETURN);
         }

         if ((!Config.enablePUTSTATIC) && (instruction instanceof I_PUTSTATIC)) {
            throw new ClassParseException(instruction, ClassParseException.TYPE.PUTFIELD);
         }

         if ((!Config.enableINVOKEINTERFACE) && (instruction instanceof I_INVOKEINTERFACE)) {
            throw new ClassParseException(instruction, ClassParseException.TYPE.INVOKEINTERFACE);
         }

         if ((!Config.enableGETSTATIC) && (instruction instanceof I_GETSTATIC)) {
            throw new ClassParseException(instruction, ClassParseException.TYPE.GETSTATIC);
         }

         if ((!Config.enableATHROW) && (instruction instanceof I_ATHROW)) {
            throw new ClassParseException(instruction, ClassParseException.TYPE.ATHROW);
         }

         if ((!Config.enableMONITOR) && ((instruction instanceof I_MONITORENTER) || (instruction instanceof I_MONITOREXIT))) {
            throw new ClassParseException(instruction, ClassParseException.TYPE.SYNCHRONIZE);
         }

         if ((!Config.enableNEW) && (instruction instanceof New)) {
            throw new ClassParseException(instruction, ClassParseException.TYPE.NEW);
         }

         if (instruction instanceof I_AASTORE) {
            throw new ClassParseException(instruction, ClassParseException.TYPE.ARRAYALIAS);
         }

         if ((!Config.enableSWITCH) && ((instruction instanceof I_LOOKUPSWITCH) || (instruction instanceof I_TABLESWITCH))) {
            throw new ClassParseException(instruction, ClassParseException.TYPE.SWITCH);
         }

         if (!Config.enableMETHODARRAYPASSING) {
            if (instruction instanceof MethodCall) {
               MethodCall methodCall = (MethodCall) instruction;

               MethodReferenceEntry methodReferenceEntry = methodCall.getConstantPoolMethodEntry();
               if (!Kernel.isMappedMethod(methodReferenceEntry)) { // we will allow trusted methods to violate this rule
                  for (Arg arg : methodReferenceEntry.getArgs()) {
                     if (arg.isArray()) {
                        throw new ClassParseException(instruction, ClassParseException.TYPE.METHODARRAYARG);

                     }
                  }
               }
            }
         }

         setRequiredPragmas(instruction);

         pcMap.put(pc, instruction);

         // list maintenance, make this the pcHead if pcHead is null
         if (pcHead == null) {
            pcHead = instruction;
         }

         // extend the list of instructions here we make the new instruction point to previous tail
         instruction.setPrevPC(pcTail);
         // if tail exists (not the first instruction in the list) link it to the new instruction
         if (pcTail != null) {
            pcTail.setNextPC(instruction);
         }
         // now move the tail along
         pcTail = instruction;

      }
      return (pcMap);
   }

   /**
    * Here we connect the branch nodes to the instruction that they branch to.
    * <p>
    * Each branch node contains a 'target' field indended to reference the node that the branch targets. Each instruction also contain four seperate lists of branch nodes that reference it.
    * These lists hold forwardConditional, forwardUnconditional, reverseConditional and revereseUnconditional branches that reference it.
    * <p>
    * So assuming that we had a branch node at pc offset 100 which represented 'goto 200'. 
    * <p>
    * Following this call the branch node at pc offset 100 will have a 'target' field which actually references the instruction at pc offset 200, and the instruction at pc offset 200 will 
    * have the branch node (at 100) added to it's forwardUnconditional list.
    * 
    * @see InstructionSet.Branch#getTarget()
    * @see Instruction#getForwardConditionalTargets()
    * @see Instruction#getForwardUnconditionalTargets()
    * @see Instruction#getReverseConditionalTargets()
    * @see Instruction#getReverseUnconditionalTargets()
    */
   void buildBranchGraphs(Map<Integer, Instruction> pcMap) {

      for (Instruction instruction = pcHead; instruction != null; instruction = instruction.getNextPC()) {
         if (instruction.isBranch()) {
            Branch branch = instruction.asBranch();
            Instruction targetInstruction = pcMap.get(branch.getAbsolute());
            branch.setTarget(targetInstruction);
         }
      }

   }

   /**
    * Javac optimizes some branches to avoid goto->goto, branch->goto etc.  
    * 
    * This method specifically deals with reverse branches which are the result of such optimisations. 
    * 
    * <code><pre>
    * 
    * </pre></code>
    * 
    * 
    * 
    */
   void deoptimizeReverseBranches() {

      for (Instruction instruction = pcHead; instruction != null; instruction = instruction.getNextPC()) {
         if (instruction.isBranch()) {
            Branch branch = instruction.asBranch();
            if (branch.isReverse()) {
               Instruction target = branch.getTarget();
               LinkedList<Branch> list = target.getReverseUnconditionalBranches();
               if ((list != null) && (list.size() > 0) && (list.get(list.size() - 1) != branch)) {
                  Branch unconditional = list.get(list.size() - 1).asBranch();
                  branch.retarget(unconditional);

               }
            }
         }
      }
   }

   /**
    * DUP family of instructions break our stack unwind model (whereby we treat instructions like the oeprands they create/consume).
    * 
    * <p>
    * Here we replace DUP style instructions with a 'mock' instruction which 'clones' the effect of the instruction.  This would be invalid to execute but is useful 
    * to replace the DUP with a 'pattern' which it simulates.  This allows us to later apply transforms to represent the original code. 
    * 
    * <p>
    * An example might be the bytecode for the following sequence.
    * <code><pre>
    *    results[10]++; 
         return
    * </pre></code>
    * 
    * Which results in the following bytecode
    * <code><pre>
      0:   aload_0       // reference through 'this' to get 
      1:   getfield      // field 'results' which is an array of int
      4:   bipush  10    // push the array index
      6:   dup2          // dreaded dup2 we'll come back here
      7:   iaload        // ignore for the moment.
      8:   iconst_1
      9:   iadd
      10:  iastore
      11:  return
    * </pre></code>
    * 
    * First we need to know what the stack will look like before the dup2 is encountered.
    * Using our folding technique we represent the first two instructions inside ()
    * 
    * <pre><code>
           getfield (aload_0     // result in the array field reference on stack
           bipush  10            // the array index
           dup2                  // dreaded dup2 we'll come back here
    * </code></pre>
    * 
    * The <code>dup2</code> essentially copies the top two elements on the stack.  So we emulate this by replacing the dup2 with clones of the instructions which would reinstate the same stack state. 
    * <p>
    * So after the <code>dup2</code> transform we end up with:- 
    * <pre><code>
          getfield (aload_0)     // result in the array field reference on stack
          bipush  10             // the array index
          {getfield (aload_0)}   // result in the array field reference on stack
          {bipush 10}            // the array index
    * </code></pre>
    * 
    * So carrying on lets look at the <code>iaload</code> which consumes two operands (the index and the array field reference) and creates one (the result of an array access)
    * 
    * <pre><code>
          getfield (aload_0)     // result in the array field reference on stack
          bipush  10             // the array index
          {getfield (aload_0)}   // result in the array field reference on stack
          {bipush  10}           // the array index
          iaload 
    * </code></pre>
    *
    * So we now have 
    * 
    * <pre><code>
          getfield (aload_0)                        // result in the array field reference on stack
          bipush  10                                // the array index
          iaload ({getfield(aload_0), {bipush 10})  // results in the array element on the stack
          iconst
          iadd
    * </code></pre>
    * 
    * And if you are following along the <code>iadd</code> will fold the previous two stack entries essentially pushing the result of  
    * <code>results[10]+1<code> on the stack.
    *   
    * <pre><code>
          getfield (aload_0)                                        // result in the array field reference on stack
          bipush  10                                                // the array index
          iadd (iaload ({getfield(aload_0), {bipush 10}, iconst_1)  // push of results[10]+1 
    * </code></pre>
    * Then the final <code>istore</code> instruction which consumes 3 stack operands (the field array reference, the index and the value to assign).
    * 
    * <p>
    * Which results in 
    * <pre><code> 
          istore (getfield (aload_0), bipush 10,  iadd (iaload ({getfield(aload_0), {bipush 10}, iconst_1)) // results[10] = results[10+1]
    * </code></pre> 
    * 
    * Where <code>results[10] = results[10+1]<code> is the long-hand form of the <code>results[10]++</code>
    * and will be transformed by one of the 'inc' transforms to the more familiar form a little later. 
    * 
    * @param _expressionList
    * @param _instruction
    * @throws ClassParseException
    */
   void txFormDups(ExpressionList _expressionList, final Instruction _instruction) throws ClassParseException {
      if (_instruction instanceof I_DUP) {
         Instruction e = _expressionList.getTail();
         while (!e.producesStack()) {
            e = e.getPrevExpr();
         }
         _expressionList.add(new CloneInstruction(this, e));
         System.out.println("clone of " + e);
      } else if (_instruction instanceof I_DUP2) {

         Instruction e = _expressionList.getTail();
         while (!e.producesStack()) {
            e = e.getPrevPC();
         }
         Instruction clone = e;
         e = e.getPrevExpr();
         while (!e.producesStack()) {
            e = e.getPrevExpr();
         }
         _expressionList.add(new CloneInstruction(this, e));
         _expressionList.add(new CloneInstruction(this, clone));
      } else if (_instruction instanceof I_DUP_X1) {

         Instruction e = _expressionList.getTail();

         while (!e.producesStack()) {
            e = e.getPrevExpr();
         }
         Instruction clone1 = new CloneInstruction(this, e);
         e = e.getPrevExpr();
         while (!e.producesStack()) {
            e = e.getPrevExpr();
         }
         _expressionList.insertBetween(e.getPrevExpr(), e, clone1);

      } else if (_instruction instanceof I_DUP_X2) {

         // dup_x2 duplicates top operand and jams a copy in 3 down from the top
         // ...word3, word2, word1 => ...word1, word3, word2, word1

         Instruction e = _expressionList.getTail();

         if (logger.isLoggable(Level.FINE)) {
            logger.fine("Found DUP_X2 prev=" + e.getPrevExpr() + " e=" + e + " curr=" + _instruction);
         }

         // Get the previous instr to write to stack "word1" 
         while (!e.producesStack()) {
            if (logger.isLoggable(Level.FINE)) {
               logger.fine("DUP_X2 skipping to find write: e=" + e);
            }
            e = e.getPrevExpr();
         }

         // Clone it, this will replace the dup action
         Instruction clone1 = new CloneInstruction(this, e);

         if (logger.isLoggable(Level.FINE)) {
            logger.fine("DUP_X2 cloning: clone1=" + clone1);
         }

         // Skip over 2 earlier writes to stack and capture 3rd one 
         e = e.getPrevExpr();

         for (int i = 0; i < 2;) {
            if (logger.isLoggable(Level.FINE)) {
               logger.fine("DUP_X2 skipping to find insert: e=" + e);
            }
            if (e.producesStack()) {
               i++;
            }
            if (i < 2) {
               e = e.getPrevExpr();
            }
         }

         if (logger.isLoggable(Level.FINE)) {
            logger.fine("DUP_X2 insert: prev=" + e.getPrevExpr() + " e=" + e + " clone=" + clone1);
         }

         // Add our clone in between those two writes
         _expressionList.insertBetween(e.getPrevExpr(), e, clone1);

      } else if (_instruction instanceof DUP) {

         throw new ClassParseException(_instruction, ClassParseException.TYPE.UNSUPPORTEDBYTECODE);
      }

   }

   /**
    *  Try to fold the instructions into higher level structures. 
    * At the end we have a folded instruction tree with 'roots' containing the 
    * top level branches (stores mostly)
    * @throws ClassParseException
    */

   void foldExpressions() throws ClassParseException {

      // we also populate a second list of expressions held between headTail.head and headTail.tail

      for (Instruction instruction = pcHead; instruction != null; instruction = instruction.getNextPC()) {

         // Here we are going to extract loop/if/structure from the list that we have collected so far in the roots list 
         // We are looking for a new instruction which is the target of a forward branch (this is why we collected forward branch counts) we only enter this loop
         // however if roots list is not empty and it's tail is not a forward branch. 

         expressionList.foldComposite(instruction);

         // If we find a DUP then we need to txform the DUP into a set of clones on the xpressionlist
         if (instruction instanceof DUP) {
            txFormDups(expressionList, instruction);
         } else {
            if (instruction.consumesStack()) {
               // If instruction consumes n operands, then walk back until we find n roots on the xpressionlist that produce stack. 
               // we will user this cursor to track our progress
               Instruction cursor = expressionList.getTail();

               // set this flag if we pass something that does not produce stack
               boolean foundNonStackProducer = false;

               // operandStart will points to the beginning of the list of consumed operands
               Instruction operandStart = null;

               // back up from root tail past each instruction expecting to create a consumed operand for this instruction
               for (int i = 0; i < instruction.getStackConsumeCount();) {
                  if (!cursor.producesStack()) {
                     foundNonStackProducer = true; // we spotted an instruction that does not consume stack. So we need to analyze this
                  } else {
                     i++;
                  }
                  operandStart = cursor;
                  cursor = cursor.getPrevExpr();
               }

               // if we found something that did not consume stack we probably have an expression with a side effect 

               if (foundNonStackProducer) {
                  // Something like
                  //     a = b++;
                  //     foo(i++);
                  //     return(a++);
                  // so we need to check for common transformations
                  applyTransformations(expressionList, instruction, operandStart);
               }

               // cut the tail off and give it to instruction
               Instruction childTail = expressionList.getTail();
               Instruction childHead = expressionList.createList(cursor);

               instruction.setChildren(childHead, childTail);
            }
            // add this instruction to the tail of roots
            expressionList.add(instruction);
         }
      }
   }

   InstructionTransformer[] transformers = new InstructionTransformer[] {

         new InstructionTransformer("long hand post increment of field"){

            /**
             * 
             * <pre><code>
             *                 A                                     A
             *                 |                                     |
             *         +1, 0  getfield<f>                            |
             *                 |              / getfield<f>         Increment(fieldref<f>++)
             *          0, 0  putfield<f> - iadd                     |
             *                 |              \ i_const_1            |
             *                 B                                     B
             *                 
             *                 A                                     A
             *                 |                                     |
             *         +1, 0  getfield<f>                            |
             *                 |                      / getfield<f>  Increment(fieldRef<f>++)
             *          0, 0  putfield<f> - i2<t> iadd               |
             *                 |                      \ i_const_1    |
             *                 B                                     B
             * </code></pre>
             */

            @Override public Instruction transform(final ExpressionList _expressionList, final Instruction i) {
               InstructionMatch result = null;

               if (Config.enablePUTFIELD
                     && (result = InstructionPattern.accessInstanceField.matches(i, InstructionPattern.assignToInstanceField)).ok) {

                  Instruction accessRaw = i;
                  Instruction assignRaw = i.getNextExpr();
                  AccessInstanceField access = (AccessInstanceField) i.getReal();
                  AssignToInstanceField assign = (AssignToInstanceField) i.getNextExpr().getReal();
                  if (access.getConstantPoolFieldIndex() == assign.getConstantPoolFieldIndex()) {
                     Instruction child = ((Instruction) assign).getFirstChild().getNextExpr();

                     if (child instanceof CastOperator) {
                        child = child.getFirstChild();
                     }
                     if (child instanceof I_IADD) {
                        I_IADD add = (I_IADD) child;
                        Instruction lhs = add.getLhs();
                        Instruction rhs = add.getRhs();
                        if (lhs instanceof AccessInstanceField) {
                           if (rhs instanceof I_ICONST_1) {
                              IncrementInstruction inc = new IncrementInstruction(MethodModel.this, (Instruction) access, true,
                                    false);
                              _expressionList.replaceInclusive((Instruction) accessRaw, (Instruction) assignRaw, inc);
                              return (inc);
                           }
                        }
                     }
                  }
               }
               return (null);
            }
         },
         new InstructionTransformer("long hand pre increment of field"){
            /**
             * <pre>
             *                 A                                     A
             *                 |                                     |
             *                 |  / getfield<f>                      |
             *         +1, -2 iadd                                   |
             *                 |  \ i_const_1                       Increment(++fieldref<f>)
             *                 |                / getfield<f>        |
             *         +0, -1 putfield<f> -- iadd                    |
             *                 |                \ i_const_1          |
             *                 B                                     B
             * </pre>
             */

            @Override public Instruction transform(final ExpressionList _expressionList, final Instruction i) {
               InstructionMatch result = null;
               if (Config.enablePUTFIELD
                     && (result = InstructionPattern.fieldPlusOne.matches(i, InstructionPattern.assignToInstanceField)).ok) {

                  Instruction topAddRaw = i;
                  Instruction assignRaw = i.getNextExpr();
                  I_IADD topAdd = (I_IADD) i.getReal();
                  AssignToInstanceField assign = (AssignToInstanceField) i.getNextExpr().getReal();
                  Instruction topLhs = topAdd.getLhs().getReal();
                  Instruction topRhs = topAdd.getRhs().getReal();
                  if (topLhs instanceof AccessInstanceField) {
                     AccessInstanceField topLhsAccess = (AccessInstanceField) topLhs;
                     if (topRhs instanceof I_ICONST_1) {
                        if (topLhsAccess.getConstantPoolFieldIndex() == assign.getConstantPoolFieldIndex()) {
                           Instruction child = ((Instruction) assign).getFirstChild().getNextExpr();
                           Instruction valueToAssign = assign.getValueToAssign();
                           if (valueToAssign instanceof I_IADD) {

                              I_IADD add = (I_IADD) child;
                              Instruction lhs = add.getLhs();
                              Instruction rhs = add.getRhs();
                              if (lhs instanceof AccessInstanceField) {
                                 if (rhs instanceof I_ICONST_1) {

                                    IncrementInstruction inc = new IncrementInstruction(MethodModel.this,
                                          (Instruction) topLhsAccess, true, true);
                                    _expressionList.replaceInclusive((Instruction) topAddRaw, (Instruction) assignRaw, inc);

                                    return (inc);
                                 }
                              }
                           }
                        }

                     }
                  }

               }
               return (null);
            }
         },
         new InstructionTransformer("long hand post increment of local variable"){
            /**
             * <pre>
             *                 A                                     A
             *                 |                                     |
             *         +1, 0  iload&ltn&gt                               |
             *                 |              / iload&ltn&gt            Increment(varref&ltn&gt++)
             *          0, 0  istore&ltn&gt - iadd                       |
             *                 |              \ i_const_1            |
             *                 B                                     B
             *                 
             *                 A                                     A
             *                 |                                     |
             *         +1, 0  &ltt&gtload&ltn&gt                             |
             *                 |                      / iload&ltn&gt    Increment( varref&ltn&gt++)
             *          0, 0  &ltt&gtstore&ltn&gt - i2&ltt&gt iadd               |
             *                 |                      \ i_const_1    |
             *                 B                                     B
             * </pre>
             */
            @Override public Instruction transform(ExpressionList _expressionList, Instruction i) {
               // looking for a post increment on a local variable
               InstructionMatch result = null;
               if ((result = InstructionPattern.accessLocalVariable.matches(i, InstructionPattern.longHandIncLocalVariable)).ok) {

                  AccessLocalVariable access = (AccessLocalVariable) i;
                  AssignToLocalVariable assign = (AssignToLocalVariable) i.getNextExpr();
                  if (access.getLocalVariableTableIndex() == assign.getLocalVariableTableIndex()) {
                     IncrementInstruction inc = new IncrementInstruction(MethodModel.this, (Instruction) access, true, false);
                     _expressionList.replaceInclusive((Instruction) access, (Instruction) assign, inc);
                     return (inc);
                  }
               }
               return (null);
            }

         },
         new InstructionTransformer("long hand post decrement of local variable"){
            /**
             * <pre>
             *                 A                                     A
             *                 |                                     |
             *         +1, 0  iload<n>                               |
             *                 |              / iload<n>            Decrement(varref<n>--)
             *          0, 0  istore<n> - isub                       |
             *                 |              \ i_const_1            |
             *                 B                                     B
             *                 
             *                 A                                     A
             *                 |                                     |
             *         +1, 0  <t>load<n>                             |
             *                 |                      / iload<n>    Decrement( varref<n>--)
             *          0, 0  <t>store<n> - i2<t> isub               |
             *                 |                      \ i_const_1    |
             *                 B                                     B
             * </pre>
             */
            @Override public Instruction transform(ExpressionList _expressionList, Instruction i) {

               InstructionMatch result = null;
               if ((result = InstructionPattern.accessLocalVariable.matches(i, InstructionPattern.longHandDecLocalVariable)).ok) {

                  AccessLocalVariable access = (AccessLocalVariable) i;
                  AssignToLocalVariable assign = (AssignToLocalVariable) i.getNextExpr();
                  if (access.getLocalVariableTableIndex() == assign.getLocalVariableTableIndex()) {
                     IncrementInstruction inc = new IncrementInstruction(MethodModel.this, (Instruction) access, false, false);
                     _expressionList.replaceInclusive((Instruction) access, (Instruction) assign, inc);
                     return (inc);
                  }
               }
               return (null);
            }

         },
         new InstructionTransformer("long hand pre increment of local variable"){
            /**
             * <pre>
             *                 A                                     A
             *                 |              / iload<n>             |
             *          0, 0  istore<n> - iadd                       |
             *                 |              \ i_const_1            Increment(++varref<n>)
             *         +1, 0  iload<n>                               |
             *                 |                                     |           
             *                 B                                     B
             *                 
             *                 A                                     A
             *                 |                      / iload<n>     |
             *          0, 0  <t>store<n> - i2<t> iadd               |
             *                 |                      \ i_const_1    Increment( ++varref<n>)
             *         +1, 0  <t>load<n>                             |
             *                 |                                     |
             *                 B                                     B
             * </pre>
             */
            @Override public Instruction transform(ExpressionList _expressionList, Instruction i) {

               InstructionMatch result = null;
               // pre increment local variable
               if ((result = InstructionPattern.longHandIncLocalVariable.matches(i, InstructionPattern.accessLocalVariable)).ok) {

                  AssignToLocalVariable assign = (AssignToLocalVariable) i;
                  AccessLocalVariable access = (AccessLocalVariable) i.getNextExpr();
                  if (access.getLocalVariableTableIndex() == assign.getLocalVariableTableIndex()) {
                     IncrementInstruction inc = new IncrementInstruction(MethodModel.this, (Instruction) access, true, true);
                     _expressionList.replaceInclusive((Instruction) assign, (Instruction) access, inc);
                     return (inc);
                  }

               }

               return (null);
            }

         },
         new InstructionTransformer("inline assign - say for methiod call or logical expression - "){
            /**
             * <pre>
             *                 A                                     A
             *                 |                                     |
             *          0, 0  iload<n>                               |
             *                 |       / iload<n>               InlineAssign(istore<?>, iload<n>)
             *         +1, 0  istore<?>                              |
             *                 |                                     |           
             *                 B                                     B
             * </pre>
             */
            @Override public Instruction transform(ExpressionList _expressionList, Instruction i) {

               InstructionMatch result = null;

               if ((result = InstructionPattern.accessLocalVariable.matches(i, InstructionPattern.assignToLocalVariable)).ok) {

                  AccessLocalVariable access = (AccessLocalVariable) i;
                  if (access.getLocalVariableTableIndex() != 0) { // we don;t want to trap on 'this' references ;) 
                     AssignToLocalVariable assign = (AssignToLocalVariable) i.getNextExpr();
                     if (access.getLocalVariableTableIndex() != assign.getLocalVariableTableIndex()) {
                        InlineAssignInstruction inlineAssign = new InlineAssignInstruction(MethodModel.this, assign,
                              (Instruction) access);
                        _expressionList.replaceInclusive((Instruction) access, (Instruction) assign, inlineAssign);
                        return (inlineAssign);
                     }
                  }
               }

               return (null);
            }

         },
         new InstructionTransformer("pre increment of local variable"){

            @Override public Instruction transform(ExpressionList _expressionList, Instruction i) {

               InstructionMatch result = null;
               if ((result = InstructionPattern.inc.matches(i, InstructionPattern.accessLocalVariable)).ok) {

                  I_IINC iinc = (I_IINC) i;
                  AccessLocalVariable access = (AccessLocalVariable) i.getNextExpr();
                  if (iinc.getLocalVariableTableIndex() == access.getLocalVariableTableIndex()) {

                     IncrementInstruction inc = new IncrementInstruction(MethodModel.this, (Instruction) access, iinc.isInc(), true);
                     _expressionList.replaceInclusive(iinc, (Instruction) access, inc);
                     return (inc);
                  }
               }
               return (null);
            }

         },
         new InstructionTransformer("post increment of local variable"){

            @Override public Instruction transform(ExpressionList _expressionList, Instruction i) {

               InstructionMatch result = null;

               if ((result = InstructionPattern.accessLocalVariable.matches(i, InstructionPattern.inc)).ok) {

                  AccessLocalVariable access = (AccessLocalVariable) i;
                  I_IINC iinc = (I_IINC) i.getNextExpr();

                  if (iinc.getLocalVariableTableIndex() == access.getLocalVariableTableIndex()) {

                     IncrementInstruction inc = new IncrementInstruction(MethodModel.this, (Instruction) access, iinc.isInc(),
                           false);
                     _expressionList.replaceInclusive((Instruction) access, iinc, inc);
                     return (inc);
                  }
               }
               return (null);
            }

         },
         new InstructionTransformer("inline assign of local variable (with cast)"){
            /**
             * <pre>
             *                 A                                     A
             *                 |      /exp                           |
             *          0, 0  cast<n>                                |
             *                 |       / iload<n>               InlineAssign(istore<?>, cast)
             *         +1, 0  istore<?>                              |
             *                 |                                     |           
             *                 B                                     B
             * </pre>
             */
            @Override public Instruction transform(ExpressionList _expressionList, Instruction i) {

               InstructionMatch result = null;
               if ((result = InstructionPattern.cast.matches(i, InstructionPattern.assignToLocalVariable)).ok) {

                  CastOperator cast = (CastOperator) i;

                  AssignToLocalVariable assign = (AssignToLocalVariable) i.getNextExpr();

                  InlineAssignInstruction inlineAssign = new InlineAssignInstruction(MethodModel.this, assign, (Instruction) cast);
                  _expressionList.replaceInclusive((Instruction) cast, (Instruction) assign, inlineAssign);
                  return (inlineAssign);

               }
               return (null);
            }

         },
         new InstructionTransformer("field array element pre increment with nested index (local variable) pre increment"){
            /**
             * <pre>
             *                 A                                     A
             *                 |            / getfield - aload       |
             *                 |    / iaload                         |
             *                 |   /        \ i_aload1               |
             *                iadd                                   |                            
             *                 |   \ iconst 1                        |
             *                 |                                     |
             *                 |                                  FieldArrayElementIncrement(pre)
             *                 |    / getfield - aload               |
             *                iastore -  iload                       |                            
             *                 |    \ [fieldArrayElementPlusOne]     |
             *                 |                                     |       
             *                 B                                     B
             * </pre>
             */
            @Override public Instruction transform(ExpressionList _expressionList, Instruction i) {

               InstructionMatch result = null;
               if ((result = InstructionPattern.fieldArrayElementPlusOne.matches(i,
                     InstructionPattern.longHandFieldArrayElementIncrement)).ok) {

                  Instruction addRaw = i;
                  Instruction assignArrayRaw = i.getNextExpr();
                  //   I_IADD add = (I_IADD) addRaw.getReal();
                  AssignToArrayElement assignArray = (AssignToArrayElement) assignArrayRaw.getReal();
                  FieldArrayElementIncrement inlineAssign = new FieldArrayElementIncrement(MethodModel.this, assignArray, true,
                        true);
                  _expressionList.replaceInclusive(addRaw, assignArrayRaw, inlineAssign);
                  return (inlineAssign);

               }

               return (null);
            }

         },
         new InstructionTransformer("field array element pre decrement with nested index (local variable) pre decrement"){
            /**
             * <pre>
             *                 A                                     A
             *                 |            / getfield - aload       |
             *                 |    / iaload                         |
             *                 |   /        \ i_aload1               |
             *                isub                                   |                            
             *                 |   \ iconst 1                        |
             *                 |                                     |
             *                 |                                  FieldArrayElementIncrement(pre)
             *                 |    / getfield - aload               |
             *                iastore -  iload                       |                            
             *                 |    \ [fieldArrayElementMinusOne]    |
             *                 |                                     |       
             *                 B                                     B
             * </pre>
             */
            @Override public Instruction transform(ExpressionList _expressionList, Instruction i) {

               InstructionMatch result = null;
               if ((result = InstructionPattern.fieldArrayElementMinusOne.matches(i,
                     InstructionPattern.longHandFieldArrayElementDecrement)).ok) {

                  Instruction subRaw = i;
                  Instruction assignArrayRaw = i.getNextExpr();
                  //   I_IADD add = (I_IADD) addRaw.getReal();
                  AssignToArrayElement assignArray = (AssignToArrayElement) assignArrayRaw.getReal();
                  FieldArrayElementIncrement inlineAssign = new FieldArrayElementIncrement(MethodModel.this, assignArray, false,
                        true);
                  _expressionList.replaceInclusive(subRaw, assignArrayRaw, inlineAssign);
                  return (inlineAssign);

               }
               return (null);
            }

         },
         new InstructionTransformer("field array element post inccrement with nested index (local variable) "){

            @Override public Instruction transform(ExpressionList _expressionList, Instruction i) {

               InstructionMatch result = null;
               if ((result = InstructionPattern.fieldArrayElementAccess.matches(i,
                     InstructionPattern.longHandFieldArrayElementIncrement)).ok) {
                  /**
                   * <pre>
                   *                 A                                     A              
                   *                 |     / getfield<f> - aload           |
                   *                iaload                                 |
                   *                 |     \ i_load                        |                    
                   *                 |                                 FieldArrayElementIncrement(post)
                   *                 |    / getfield - aload               |
                   *                iastore -  iload                       |                            
                   *                 |    \ [fieldArrayElementPlusOne]     |
                   *                 |                                     |           
                   *                 B                                     B
                   *                 
                   *  
                   * </pre>
                   */
                  Instruction accessArrayRaw = i;
                  Instruction assignArrayRaw = i.getNextExpr();
                  AccessArrayElement accessArray = (AccessArrayElement) accessArrayRaw.getReal();
                  AssignToArrayElement assignArray = (AssignToArrayElement) assignArrayRaw.getReal();
                  AccessField accessField1 = (AccessField) accessArray.getArrayRef().getReal();
                  AccessField accessField2 = (AccessField) assignArray.getArrayRef().getReal();
                  if (accessField1.getConstantPoolFieldIndex() == accessField2.getConstantPoolFieldIndex()) {
                     // we accessing the same field at least
                     //AccessLocalVariable accessLocalVariable1 = (AccessLocalVariable) accessArray.getArrayIndex().getReal();
                     //AccessLocalVariable accessLocalVariable2 = (AccessLocalVariable) assignArray.getArrayIndex().getReal();
                     //  if (accessLocalVariable1.getLocalVariableTableIndex() == accessLocalVariable2.getLocalVariableTableIndex()) {
                     // and both arrays are referencing the array element using the same variable
                     FieldArrayElementIncrement inlineAssign = new FieldArrayElementIncrement(MethodModel.this, assignArray, true,
                           false);
                     _expressionList.replaceInclusive(accessArrayRaw, assignArrayRaw, inlineAssign);
                     return (inlineAssign);
                     // }
                  }

               }

               return (null);
            }

         },
         new InstructionTransformer("field array element post decrement with nested index (local variable) "){
            /**
             * <pre>
             *                 A                                     A              
             *                 |     / getfield<f> - aload           |
             *                iaload                                 |
             *                 |     \ i_load                        |                    
             *                 |                                 FieldArrayElementIncrement(post)
             *                 |    / getfield - aload               |
             *                iastore -  iload                       |                            
             *                 |    \ [fieldArrayElementMinusOne]    |
             *                 |                                     |           
             *                 B                                     B
             *                 
             *  
             * </pre>
             */
            @Override public Instruction transform(ExpressionList _expressionList, Instruction i) {

               InstructionMatch result = null;
               if ((result = InstructionPattern.fieldArrayElementAccess.matches(i,
                     InstructionPattern.longHandFieldArrayElementDecrement)).ok) {

                  Instruction accessArrayRaw = i;
                  Instruction assignArrayRaw = i.getNextExpr();
                  AccessArrayElement accessArray = (AccessArrayElement) accessArrayRaw.getReal();
                  AssignToArrayElement assignArray = (AssignToArrayElement) assignArrayRaw.getReal();
                  AccessField accessField1 = (AccessField) accessArray.getArrayRef().getReal();
                  AccessField accessField2 = (AccessField) assignArray.getArrayRef().getReal();
                  if (accessField1.getConstantPoolFieldIndex() == accessField2.getConstantPoolFieldIndex()) {
                     // we accessing the same field at least
                     AccessLocalVariable accessLocalVariable1 = (AccessLocalVariable) accessArray.getArrayIndex().getReal();
                     AccessLocalVariable accessLocalVariable2 = (AccessLocalVariable) assignArray.getArrayIndex().getReal();
                     if (accessLocalVariable1.getLocalVariableTableIndex() == accessLocalVariable2.getLocalVariableTableIndex()) {
                        // and both arrays are referencing the array element using the same variable
                        FieldArrayElementIncrement inlineAssign = new FieldArrayElementIncrement(MethodModel.this, assignArray,
                              false, false);
                        _expressionList.replaceInclusive(accessArrayRaw, assignArrayRaw, inlineAssign);
                        return (inlineAssign);
                     }
                  }

               }
               return (null);
            }

         },
         new InstructionTransformer("inline assign (for method call or logical expression)"){
            /**
             * <pre>
             *                 A                                     A
             *                 |                                     |
             *          0, 0  invoke<n>                              |
             *                 |       / invoke()               InlineAssign(istore<?>, invoke)
             *         +1, 0  istore<?>                              |
             *                 |                                     |           
             *                 B                                     B
             * </pre>
             */

            @Override public Instruction transform(ExpressionList _expressionList, Instruction i) {

               InstructionMatch result = null;
               if ((result = InstructionPattern.methodCall.matches(i, InstructionPattern.assignToLocalVariable)).ok) {

                  Instruction invoke = i;

                  AssignToLocalVariable assign = (AssignToLocalVariable) i.getNextExpr();

                  InlineAssignInstruction inlineAssign = new InlineAssignInstruction(MethodModel.this, assign, (Instruction) invoke);
                  _expressionList.replaceInclusive((Instruction) invoke, (Instruction) assign, inlineAssign);
                  return (inlineAssign);

               }
               return (null);
            }

         },
         new InstructionTransformer("incline assign from constant (method call or logical expression)"){
            /**
             * <pre>
             *                 A                                     A
             *                 |                                     |
             *          0, 0  invoke<n>                              |
             *                 |       / invoke()               InlineAssign(istore<?>, invoke)
             *         +1, 0  istore<?>                              |
             *                 |                                     |           
             *                 B                                     B
             * </pre>
             */

            @Override public Instruction transform(ExpressionList _expressionList, Instruction i) {

               InstructionMatch result = null;

               if ((result = InstructionPattern.constant.matches(i, InstructionPattern.assignToLocalVariable)).ok) {

                  Instruction constant = i;

                  AssignToLocalVariable assign = (AssignToLocalVariable) i.getNextExpr();

                  InlineAssignInstruction inlineAssign = new InlineAssignInstruction(MethodModel.this, assign,
                        (Instruction) constant);
                  _expressionList.replaceInclusive((Instruction) constant, (Instruction) assign, inlineAssign);
                  return (inlineAssign);

               }

               return (null);
            }

         },
         new InstructionTransformer("inline array assignment as part of a method call"){
            /**
             * <pre>
             *                 A                                     A
             *                 |                                     |
             *          0, 0  invoke<n>                              |
             *                 |       / invoke()               InlineAssign(istore<?>, invoke)
             *         +1, 0  iastore<?>                              |
             *                 |                                     |           
             *                 B                                     B
             * </pre>
             */
            @Override public Instruction transform(ExpressionList _expressionList, Instruction i) {

               InstructionMatch result = null;
               if ((result = InstructionPattern.methodCall.matches(i, InstructionPattern.assignToArrayElement)).ok) {

                  Instruction invoke = i;

                  AssignToArrayElement assign = (AssignToArrayElement) i.getNextExpr();

                  FieldArrayElementAssign inlineAssign = new FieldArrayElementAssign(MethodModel.this, assign, (Instruction) invoke);
                  _expressionList.replaceInclusive((Instruction) invoke, (Instruction) assign, inlineAssign);
                  return (inlineAssign);

               }

               return (null);
            }

         },
         new InstructionTransformer("inline array element increment as as part of a method call "){
            /**
             * <pre>
             *                 A                                     A
             *                 |                                     |
             *          0, 0  invoke<n>                              |
             *                 |       / invoke()               InlineAssign(istore<?>, invoke)
             *         +1, 0  iastore<?>                              |
             *                 |                                     |           
             *                 B                                     B
             * </pre>
             */
            @Override public Instruction transform(ExpressionList _expressionList, Instruction i) {

               InstructionMatch result = null;
               if ((result = InstructionPattern.assignToArrayElement.matches(i,
                     InstructionPattern.longHandFieldArrayElementIncrement)).ok) {

                  Instruction invoke = i;

                  AssignToArrayElement assign = (AssignToArrayElement) i.getNextExpr();

                  FieldArrayElementAssign inlineAssign = new FieldArrayElementAssign(MethodModel.this, assign, (Instruction) invoke);
                  _expressionList.replaceInclusive((Instruction) invoke, (Instruction) assign, inlineAssign);

                  return (inlineAssign);

               }

               return (null);
            }

         }

   };

   void applyTransformations(ExpressionList _expressionList, final Instruction _instruction, final Instruction _operandStart)
         throws ClassParseException {

      if (logger.isLoggable(Level.FINE)) {

         System.out.println("We are looking at " + _instruction + " which wants to consume " + _instruction.getStackConsumeCount()
               + " operands");
      }
      boolean txformed = false;

      /**
       * Here we look for multi-assigns
       * i.e 
       * 
       * a=b=c=<exp>;
       */
      if ((_instruction instanceof AssignToLocalVariable) && _operandStart.producesStack()
            && (_operandStart.getNextExpr() instanceof AssignToLocalVariable)) {
         Instruction assignFirst = _operandStart.getNextExpr();
         Instruction assign = assignFirst;
         int count = 0;
         while ((assign != null) && (assign instanceof AssignToLocalVariable)) {
            assign = assign.getNextExpr();
            count++;
         }
         if (assign == null) {
            Instruction newOne = new MultiAssignInstruction(this, _operandStart, assignFirst, assign);
            _expressionList.replaceInclusive(_operandStart, assign, newOne);
            txformed = true;
         }
      }

      if (!txformed) {
         boolean again = false;
         for (Instruction i = _operandStart; i != null; i = again ? i : i.getNextExpr()) {
            again = false;

            for (InstructionTransformer txformer : transformers) {
               Instruction newI = txformer.transform(_expressionList, i);
               if (newI != null) {
                  i = newI;
                  again = true;
                  txformed = true;
                  break;
               }
            }

         }

      }

      if (txformed) {
         if (logger.isLoggable(Level.FINE)) {

            System.out.println("We are looking at " + _instruction + " which wants to consume "
                  + _instruction.getStackConsumeCount() + " operands");
         }
      } else {
         throw new ClassParseException(_instruction, ClassParseException.TYPE.OPERANDCONSUMERPRODUCERMISSMATCH);
      }

   }

   /**
    * Determine if this method is a getter and record the accessed field if so
    */
   void checkForGetter(Map<Integer, Instruction> pcMap) throws ClassParseException {
      String methodName = getMethod().getName();
      String rawVarNameCandidate = null;
      boolean mightBeSetter = true;

      if (methodName.startsWith("get")) {
         rawVarNameCandidate = methodName.substring(3);
      } else if (methodName.startsWith("is")) {
         rawVarNameCandidate = methodName.substring(2);
      } else {
         mightBeSetter = false;
      }

      // Getters should have 3 bcs: aload_0, getfield, ?return
      if (mightBeSetter) {
         if ((rawVarNameCandidate != null) && (pcMap.size() == 3)) {
            String firstLetter = rawVarNameCandidate.substring(0, 1).toLowerCase();
            String varNameCandidateCamelCased = rawVarNameCandidate.replaceFirst(rawVarNameCandidate.substring(0, 1), firstLetter);
            String accessedFieldName = null;
            Instruction instruction = expressionList.getHead();

            if ((instruction instanceof Return) && (expressionList.getHead() == expressionList.getTail())) {
               instruction = instruction.getPrevPC();
               if (instruction instanceof AccessInstanceField) {
                  FieldEntry field = ((AccessInstanceField) instruction).getConstantPoolFieldEntry();
                  accessedFieldName = field.getNameAndTypeEntry().getNameUTF8Entry().getUTF8();
                  if (accessedFieldName.equals(varNameCandidateCamelCased)) {

                     // Verify field type matches return type
                     String fieldType = field.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8();
                     String returnType = getMethod().getDescriptor().substring(2);
                     //System.out.println( "### field type = " + fieldType );
                     //System.out.println( "### method args = " + returnType );
                     assert (fieldType.length() == 1) && (returnType.length() == 1) : " can only use basic type getters";

                     // Allow isFoo style for boolean fields
                     if ((methodName.startsWith("is") && fieldType.equals("Z")) || (methodName.startsWith("get"))) {
                        if (fieldType.equals(returnType)) {
                           if (logger.isLoggable(Level.FINE)) {
                              logger.fine("Found " + methodName + " as a getter for " + varNameCandidateCamelCased.toLowerCase());
                           }

                           methodIsGetter = true;
                           accessorVariableFieldEntry = field;
                           assert methodIsSetter == false : " cannot be both";
                        } else {
                           throw new ClassParseException(ClassParseException.TYPE.BADGETTERTYPEMISMATCH, methodName);

                        }
                     }
                  } else {
                     throw new ClassParseException(ClassParseException.TYPE.BADGETTERNAMEMISMATCH, methodName);
                  }
               }
            } else {
               throw new ClassParseException(ClassParseException.TYPE.BADGETTERNAMENOTFOUND, methodName);
            }
         } else {
            throw new ClassParseException(ClassParseException.TYPE.BADGETTERNAMENOTFOUND, methodName);
         }
      }
   }

   /**
    * Determine if this method is a setter and record the accessed field if so
    */
   void checkForSetter(Map<Integer, Instruction> pcMap) throws ClassParseException {
      String methodName = getMethod().getName();
      if (methodName.startsWith("set")) {
         String rawVarNameCandidate = methodName.substring(3);
         String firstLetter = rawVarNameCandidate.substring(0, 1).toLowerCase();
         String varNameCandidateCamelCased = rawVarNameCandidate.replaceFirst(rawVarNameCandidate.substring(0, 1), firstLetter);
         String accessedFieldName = null;
         Instruction instruction = expressionList.getHead();

         // setters should be aload_0, ?load_1, putfield, return
         if ((instruction instanceof AssignToInstanceField) && (expressionList.getTail() instanceof Return) && (pcMap.size() == 4)) {
            Instruction prev = instruction.getPrevPC();
            if (prev instanceof AccessLocalVariable) {
               FieldEntry field = ((AssignToInstanceField) instruction).getConstantPoolFieldEntry();
               accessedFieldName = field.getNameAndTypeEntry().getNameUTF8Entry().getUTF8();
               if (accessedFieldName.equals(varNameCandidateCamelCased)) {

                  // Verify field type matches setter arg type
                  String fieldType = field.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8();
                  String setterArgType = getMethod().getDescriptor().substring(1, 2);

                  //System.out.println( "### field type = " + fieldType );
                  //System.out.println( "### setter type = " + setterArgType );
                  assert fieldType.length() == 1 : " can only use basic type getters";

                  if (fieldType.equals(setterArgType)) {
                     if (logger.isLoggable(Level.FINE)) {
                        logger.fine("Found " + methodName + " as a setter for " + varNameCandidateCamelCased.toLowerCase()
                              + " of type " + fieldType);
                     }

                     methodIsSetter = true;
                     accessorVariableFieldEntry = field;

                     // Setters use putfield which will miss the normal store check
                     if (fieldType.equals("B") || fieldType.equals("Z")) {
                        usesByteWrites = true;
                     }

                     assert methodIsGetter == false : " cannot be both";
                  } else {
                     throw new ClassParseException(ClassParseException.TYPE.BADSETTERTYPEMISMATCH, methodName);
                  }
               } else {
                  throw new ClassParseException(ClassParseException.TYPE.BADSETTERTYPEMISMATCH, methodName);
               }
            }
         }
      }
   }

   // The entrypoint is used to make checks on object accessors
   Entrypoint entrypoint = null;

   MethodModel(ClassModelMethod _method, Entrypoint _entrypoint) throws AparapiException {
      entrypoint = _entrypoint;
      init(_method);
   }

   MethodModel(ClassModelMethod _method) throws AparapiException {
      init(_method);
   }

   public static class FakeLocalVariableTableEntry implements LocalVariableTableEntry<LocalVariableInfo>{

      public static class FakeLocalVariableInfo implements LocalVariableInfo{

         int start;

         int end;

         String name;

         String descriptor;

         boolean isArray;

         int variableIndex;

         public FakeLocalVariableInfo(int _variableIndex, int _start, int _end, String _variableName, String _variableDescriptor,
               boolean _isArray) {
            variableIndex = _variableIndex;
            start = _start;
            end = _end;
            name = _variableName;
            descriptor = _variableDescriptor;
            isArray = _isArray;
         }

         @Override public int getEnd() {
            return (end);
         }

         @Override public int getStart() {
            return (start);
         }

         @Override public String getVariableDescriptor() {
            return (descriptor);
         }

         @Override public int getVariableIndex() {
            throw new IllegalStateException();

         }

         @Override public String getVariableName() {

            return (name);
         }

         @Override public boolean isArray() {

            return (isArray);
         }

      }

      List<LocalVariableInfo> list = new ArrayList<LocalVariableInfo>();

      /*

      public class SlotTable{
         Slot[] slots;

         int slotSize;

         Map.Entry<Integer, Instruction> pcMapEntries[];

         StoreSpec[] argsAsStoreSlots;

         public SlotTable(StoreSpec[] _argsAsStoreSlots, int _numberOfSlots, Map<Integer, Instruction> _pcMap) {
            slotSize = _pcMap.size();
            pcMapEntries = new Map.Entry[slotSize];
            int count = 0;
            for (Map.Entry<Integer, Instruction> entry : _pcMap.entrySet()) {
               pcMapEntries[count++] = entry;
            }

            argsAsStoreSlots = _argsAsStoreSlots;
            slots = new Slot[_numberOfSlots];
            for (int i = 0; i < _numberOfSlots; i++) {
               slots[i] = new Slot(i < argsAsStoreSlots.length ? argsAsStoreSlots[i] : StoreSpec.NONE, slotSize);
            }
         }

         public class Slot{

            public Slot(StoreSpec _defaultStoreSpec, int _size) {
               defaultStoreSpec = _defaultStoreSpec;
               entries = new LinkedHashMap<Integer, Entry>();
               for (Map.Entry<Integer, Instruction> pcMapEntry : pcMapEntries) {
                  entries.put(pcMapEntry.getKey(), new Entry(pcMapEntry.getKey()));
               }
            }

            StoreSpec defaultStoreSpec;

            int number;

            public class Entry{
               int slotNumber;

               public Entry(int _slotNumber) {
                  slotNumber = _slotNumber;

               }

               LoadSpec loadSpec = LoadSpec.NONE;

               StoreSpec storeSpec = StoreSpec.NONE;

               public String toString() {
                  if (loadSpec == LoadSpec.NONE && storeSpec == StoreSpec.NONE) {
                     return ("  ");
                  } else if (loadSpec != LoadSpec.NONE) {
                     return ("L" + loadSpec);
                  } else {
                     return ("S" + storeSpec);
                  }
               }
            }

            Map<Integer, Entry> entries;

            public Entry getEntry(int _pc) {
               return entries.get(_pc);
            }

            public void setLoad(int _pc, LoadSpec _loadSpec) {
               entries.get(_pc).loadSpec = _loadSpec;

            }

            public void setStore(int _pc, StoreSpec _storeSpec) {
               entries.get(_pc).storeSpec = _storeSpec;

            }

            public StoreSpec getDefaultStoreSpec() {
               return (defaultStoreSpec);
            }

            public LocalVariableInfo createLocalVariableInfo(int _index) {
               StoreSpec storeSpec = defaultStoreSpec;
               int variableIndex = 0;
               int start = 0;
               int end = 0;
               String variableName = null;
               String variableDescription = null;

               String state = storeSpec == StoreSpec.NONE ? "NONE" : "STARTED";

               int count = 0;
               for (Map.Entry<Integer, Entry> entry : entries.entrySet()) {

                  if (entry.getValue().storeSpec != StoreSpec.NONE) {
                     if (state.equals("NONE")) {
                        storeSpec = entry.getValue().storeSpec;
                        state = "STARTED";
                        start = end = entry.getKey();
                     } else if (storeSpec != entry.getValue().storeSpec) {
                        storeSpec = entry.getValue().storeSpec;
                        state = "STARTED";
                        start = end = entry.getKey();
                     }

                  }
                  count++;
               }
               return ((LocalVariableInfo) new FakeLocalVariableInfo(variableIndex, start, end, variableName, variableDescription,
                     false));
            }
         }

         public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Slot slot : slots) {
               StoreSpec storeSpec = slot.getDefaultStoreSpec();
               if (storeSpec != StoreSpec.NONE) {
                  sb.append("S" + storeSpec);
               } else {
                  sb.append("  ");
               }
               sb.append("|");
            }
            sb.append("\n");

            for (Slot slot : slots) {
               sb.append("--|");
            }
            sb.append("\n");
            for (Map.Entry<Integer, Instruction> pcMapEntry : pcMapEntries) {

               for (Slot slot : slots) {
                  sb.append(slot.getEntry(pcMapEntry.getKey()) + "|");
               }
               sb.append(" " + pcMapEntry.getValue());
               sb.append("\n");
            }

            return (sb.toString());

         }

         public void setLoad(int slot, int pc, LoadSpec _loadSpec) {
            slots[slot].setLoad(pc, _loadSpec);

         }

         public void setStore(int slot, int pc, StoreSpec _storeSpec) {
            slots[slot].setStore(pc, _storeSpec);

         }
      }
      

      SlotTable slotTable;
      */

      class Var{

         int startPc = 0;

         int endPc = 0;

         String name = null;

         // String descriptor;
         Var(StoreSpec _storeSpec, int _slotIndex, int _startPc) {
            startPc = _startPc;
            if (_storeSpec.equals(StoreSpec.A)) {
               name = "arr_" + _slotIndex;
            } else {
               name = _storeSpec.toString().toLowerCase() + "_" + _slotIndex;
            }
         }

         Var() {
            name = "NONE";
         }

         @Override public boolean equals(Object object) {
            return (object instanceof Var && ((object == this) || ((Var) object).name.equals(name)));
         }

         public String toString() {
            return (name + "[" + startPc + "-" + endPc + "]");
         }
      }

      public FakeLocalVariableTableEntry(Map<Integer, Instruction> _pcMap, ClassModelMethod _method) {
         int numberOfSlots = _method.getCodeEntry().getMaxLocals();
         Var[] vars = new Var[numberOfSlots];
         MethodDescription description = ClassModel.getMethodDescription(_method.getDescriptor());
         String[] args = description.getArgs();
         StoreSpec[] argsAsStoreSpecs = new StoreSpec[args.length];
         for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("[")) {
               argsAsStoreSpecs[i] = StoreSpec.A;
            } else {
               argsAsStoreSpecs[i] = StoreSpec.valueOf(args[i].substring(0, 1));
            }
            vars[i] = new Var(argsAsStoreSpecs[i], i, 0);
         }
         for (int i = args.length; i < numberOfSlots; i++) {
            vars[i] = new Var();
         }

         //  System.out.println("slots= " + numberOfSlots);
         // slotTable = new SlotTable(argsAsStoreSpecs, numberOfSlots, _pcMap);
         // System.out.println(slotTable);

         for (Entry<Integer, Instruction> entry : _pcMap.entrySet()) {
            int pc = entry.getKey();
            Instruction instruction = entry.getValue();
            LoadSpec loadSpec = instruction.getByteCode().getLoad();
            StoreSpec storeSpec = instruction.getByteCode().getStore();

            int slotIndex = -1;

            if (loadSpec != LoadSpec.NONE) {
               slotIndex = ((InstructionSet.LocalVariableTableIndexAccessor) instruction).getLocalVariableTableIndex();
               //  slotTable.setLoad(slotIndex, pc,
               //    loadSpec);
               if (vars[slotIndex].endPc < pc) {
                  vars[slotIndex].endPc = pc;
               }

            }
            if (storeSpec != StoreSpec.NONE) {

               slotIndex = ((InstructionSet.LocalVariableTableIndexAccessor) instruction).getLocalVariableTableIndex();
               // slotTable.setStore(slotIndex, pc,
               //    storeSpec);
               Var var = new Var(storeSpec, slotIndex, pc);
               if (!vars[slotIndex].equals(var)) {
                  vars[slotIndex] = var;
               }

            }
            for (int i = 0; i < numberOfSlots; i++) {
               System.out.print(vars[i] + "|");
            }

            System.out.println(" Instruction " + entry.getValue() + " " + slotIndex);
         }
         //  System.out.println(slotTable);

      }

      @Override public LocalVariableInfo getVariable(int _pc, int _index) {
         return (null);//slotTable.slots[_index].createLocalVariableInfo(_index));
      }

      @Override public Iterator<LocalVariableInfo> iterator() {
         return list.iterator();
      }

   }

   private void init(ClassModelMethod _method) throws AparapiException {
      try {
         method = _method;
         expressionList = new ExpressionList(this);

         // check if we have any exception handlers
         int exceptionsSize = method.getCodeEntry().getExceptionPoolEntries().size();
         if (exceptionsSize > 0) {
            if (logger.isLoggable(Level.FINE)) {
               logger.fine("exception size for " + method + " = " + exceptionsSize);
            }
            throw new ClassParseException(ClassParseException.TYPE.EXCEPTION);
         }

         // check if we have any local variables which are arrays.  This is an attempt to avoid aliasing field arrays

         // We are going to make 4 passes.

         // Pass #1 create a linked list of instructions from head to tail
         Map<Integer, Instruction> pcMap = createListOfInstructions();

         LocalVariableTableEntry<LocalVariableInfo> localVariableTableEntry = method.getLocalVariableTableEntry();
         if (Config.enableAllowMissingLocalVariableTable && localVariableTableEntry == null) {
            logger.warning("class does not contain a LocalVariableTable - but enableAllowMissingLocalVariableTable is set so we are ignoring");
         } else {
            if (localVariableTableEntry == null) {
               //System.out.println("create local variable table");

               localVariableTableEntry = new FakeLocalVariableTableEntry(pcMap, method);

               method.setLocalVariableTableEntry(localVariableTableEntry);

               //throw new ClassParseException(ClassParseException.TYPE.MISSINGLOCALVARIABLETABLE);
            }
            for (LocalVariableInfo localVariableInfo : localVariableTableEntry) {
               // TODO: What was the thinking here?
               final boolean DISALLOWARRAYLOCALVAR = false;
               if (DISALLOWARRAYLOCALVAR && localVariableInfo.isArray()) {
                  throw new ClassParseException(ClassParseException.TYPE.ARRAYLOCALVARIABLE);
               }
            }
         }

         // pass #2 build branch graph
         buildBranchGraphs(pcMap);

         // pass #3 build branch graph
         deoptimizeReverseBranches();

         // pass #4

         foldExpressions();

         // Attempt to detect accesses through multi-dimension arrays. 
         // This was issue 10 in open source release http://code.google.com/p/aparapi/issues/detail?id=10
         for (Entry<Integer, Instruction> instructionEntry : pcMap.entrySet()) {
            Instruction instruction = instructionEntry.getValue();
            if (instruction instanceof AccessArrayElement) {
               AccessArrayElement accessArrayElement = (AccessArrayElement) instruction;
               Instruction accessed = accessArrayElement.getArrayRef();
               // System.out.println("accessed "+accessed);
               if (accessed instanceof AccessArrayElement) {
                  throw new ClassParseException(ClassParseException.TYPE.MULTIDIMENSIONARRAYACCESS);
               }

            }
            if (instruction instanceof AssignToArrayElement) {
               AssignToArrayElement assignToArrayElement = (AssignToArrayElement) instruction;
               Instruction assigned = assignToArrayElement.getArrayRef();

               // System.out.println("assigned "+assigned);
               if (assigned instanceof AccessArrayElement) {
                  throw new ClassParseException(ClassParseException.TYPE.MULTIDIMENSIONARRAYASSIGN);
               }

            }
         }
         // Accessor conversion only works on member object arrays
         if ((entrypoint != null) && (_method.getClassModel() != entrypoint.getClassModel())) {
            if (logger.isLoggable(Level.FINE)) {
               logger.fine("Considering accessor call: " + getName());
            }
            checkForGetter(pcMap);
            checkForSetter(pcMap);
         }

         // In order to allow inline access of object member fields, postpone this check
         //if ((!Config.enablePUTFIELD) && usesPutfield && !isSetter()) {
         //   throw new ClassParseException("We don't support putfield instructions beyond simple setters");
         //}

         if (logger.isLoggable(Level.FINE)) {
            logger.fine("end \n" + expressionList.dumpDiagram(null));
         }
         if (Config.instructionListener != null) {
            Config.instructionListener.showAndTell("end", expressionList.getHead(), null);
         }
      } catch (Throwable _t) {
         if (_t instanceof ClassParseException) {
            _t.printStackTrace();
            throw (ClassParseException) _t;
         }
         throw new ClassParseException(_t);

      }
   }

   LocalVariableTableEntry getLocalVariableTableEntry() {
      return (method.getLocalVariableTableEntry());
   }

   ConstantPool getConstantPool() {
      return (method.getConstantPool());
   }

   LocalVariableInfo getLocalVariable(int _pc, int _index) {
      return (method.getLocalVariable(_pc, _index));
   }

   String getSimpleName() {
      return (method.getName());
   }

   /*
    * @return the fully qualified name such as "com_amd_javalabs_opencl_demo_PaternityTest$SimpleKernel__actuallyDoIt"
    */
   String getName() {
      return (method.getClassModel().getMethod(method.getName(), method.getDescriptor()).getClassModel().getClassWeAreModelling()
            .getName().replace('.', '_')
            + "__" + method.getName());
   }

   String getReturnType() {
      String returnType = method.getDescriptorUTF8Entry().getUTF8();
      int index = returnType.indexOf(")");
      return (returnType.substring(index + 1));
   }

   List<MethodCall> getMethodCalls() {
      List<MethodCall> methodCalls = new ArrayList<MethodCall>();

      for (Instruction i = getPCHead(); i != null; i = i.getNextPC()) {
         if (i instanceof MethodCall) {
            MethodCall methodCall = (MethodCall) i;
            methodCalls.add(methodCall);
         }
      }
      return (methodCalls);
   }

   Instruction getPCHead() {
      return (pcHead);
   }

   Instruction getExprHead() {
      return (expressionList.getHead());
   }

}
