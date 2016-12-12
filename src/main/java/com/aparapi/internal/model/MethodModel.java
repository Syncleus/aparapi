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
package com.aparapi.internal.model;

import com.aparapi.Config;
import com.aparapi.Kernel;
import com.aparapi.internal.exception.AparapiException;
import com.aparapi.internal.exception.ClassParseException;
import com.aparapi.internal.instruction.*;
import com.aparapi.internal.instruction.InstructionPattern.InstructionMatch;
import com.aparapi.internal.instruction.InstructionSet.*;
import com.aparapi.internal.model.ClassModel.*;
import com.aparapi.internal.model.ClassModel.ConstantPool.FieldEntry;
import com.aparapi.internal.model.ClassModel.ConstantPool.MethodReferenceEntry;
import com.aparapi.internal.model.ClassModel.ConstantPool.MethodReferenceEntry.Arg;
import com.aparapi.internal.reader.ByteReader;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MethodModel{

   private static final Logger logger = Logger.getLogger(Config.getLoggerName());

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

   private boolean methodIsPrivateMemoryGetter = false;

   // Only setters can use putfield
   private boolean usesPutfield;

   private FieldEntry accessorVariableFieldEntry;

   private boolean noCL = false;

   public boolean isGetter() {
      return methodIsGetter;
   }

   public boolean isSetter() {
      return methodIsSetter;
   }

   public boolean methodUsesPutfield() {
      return usesPutfield;
   }

   public boolean isNoCL() {
      return noCL;
   }

   public boolean isPrivateMemoryGetter() {
      return methodIsPrivateMemoryGetter;
   }

   public ClassModelMethod getMethod() {
      return method;
   }

   public FieldEntry getAccessorVariableFieldEntry() {
      return accessorVariableFieldEntry;
   }

   private final Set<MethodModel> calledMethods = new HashSet<>();

   public Set<MethodModel> getCalledMethods() {
      return calledMethods;
   }

   public void checkForRecursion(Set<MethodModel> transitiveCalledMethods) throws AparapiException {

      if (transitiveCalledMethods.contains(this)) {
         throw new ClassParseException(ClassParseException.TYPE.RECURSION, getName());
      }

      // Add myself
      transitiveCalledMethods.add(this);

      // For each callee, send him a copy of the call chain up to this method
       for (MethodModel next : getCalledMethods()) {
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
   public void setRequiredPragmas(Instruction instruction) {
      final boolean setDouble = instruction.getByteCode().usesDouble();
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

   public boolean requiresDoublePragma() {
      return usesDoubles;
   }

   public boolean requiresByteAddressableStorePragma() {
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
   public Map<Integer, Instruction> createListOfInstructions() throws ClassParseException {
      final Map<Integer, Instruction> pcMap = new LinkedHashMap<>();
      final byte[] code = method.getCode();

      // We create a byteReader for reading the bytes from the code array
      final ByteReader codeReader = new ByteReader(code);
      while (codeReader.hasMore()) {
         // Create an instruction from code reader's current position
         final int pc = codeReader.getOffset();
         final Instruction instruction = InstructionSet.ByteCode.create(this, codeReader);

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
               final MethodCall methodCall = (MethodCall) instruction;

               final MethodReferenceEntry methodReferenceEntry = methodCall.getConstantPoolMethodEntry();
               if (!Kernel.isMappedMethod(methodReferenceEntry)) { // we will allow trusted methods to violate this rule
                  for (final Arg arg : methodReferenceEntry.getArgs()) {
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
    */
   public void buildBranchGraphs(Map<Integer, Instruction> pcMap) {
      for (Instruction instruction = pcHead; instruction != null; instruction = instruction.getNextPC()) {
         if (instruction.isBranch()) {
            final Branch branch = instruction.asBranch();
            final Instruction targetInstruction = pcMap.get(branch.getAbsolute());
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
   public void deoptimizeReverseBranches() {

      for (Instruction instruction = pcHead; instruction != null; instruction = instruction.getNextPC()) {
         if (instruction.isBranch()) {
            final Branch branch = instruction.asBranch();
            if (branch.isReverse()) {
               final Instruction target = branch.getTarget();
               final LinkedList<Branch> list = target.getReverseUnconditionalBranches();
               if ((list != null) && (list.size() > 0) && (list.get(list.size() - 1) != branch)) {
                  final Branch unconditional = list.get(list.size() - 1).asBranch();
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
   public void txFormDups(ExpressionList _expressionList, final Instruction _instruction) throws ClassParseException {
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

         final Instruction clone = e;
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
         final Instruction clone1 = new CloneInstruction(this, e);
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
         final Instruction clone1 = new CloneInstruction(this, e);

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
               final Instruction childTail = expressionList.getTail();
               final Instruction childHead = expressionList.createList(cursor);

               instruction.setChildren(childHead, childTail);
            }
            // add this instruction to the tail of roots
            expressionList.add(instruction);
         }
      }
   }

   final InstructionTransformer[] transformers = new InstructionTransformer[] {

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

                  final Instruction accessRaw = i;
                  final Instruction assignRaw = i.getNextExpr();
                  final AccessInstanceField access = (AccessInstanceField) i.getReal();
                  final AssignToInstanceField assign = (AssignToInstanceField) i.getNextExpr().getReal();
                  if (access.getConstantPoolFieldIndex() == assign.getConstantPoolFieldIndex()) {
                     Instruction child = ((Instruction) assign).getFirstChild().getNextExpr();

                     if (child instanceof CastOperator) {
                        child = child.getFirstChild();
                     }
                     if (child instanceof I_IADD) {
                        final I_IADD add = (I_IADD) child;
                        final Instruction lhs = add.getLhs();
                        final Instruction rhs = add.getRhs();
                        if (lhs instanceof AccessInstanceField) {
                           if (rhs instanceof I_ICONST_1) {
                              final IncrementInstruction inc = new IncrementInstruction(MethodModel.this, (Instruction) access,
                                    true, false);
                              _expressionList.replaceInclusive(accessRaw, assignRaw, inc);
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

                  final Instruction topAddRaw = i;
                  final Instruction assignRaw = i.getNextExpr();
                  final I_IADD topAdd = (I_IADD) i.getReal();
                  final AssignToInstanceField assign = (AssignToInstanceField) i.getNextExpr().getReal();
                  final Instruction topLhs = topAdd.getLhs().getReal();
                  final Instruction topRhs = topAdd.getRhs().getReal();
                  if (topLhs instanceof AccessInstanceField) {
                     final AccessInstanceField topLhsAccess = (AccessInstanceField) topLhs;
                     if (topRhs instanceof I_ICONST_1) {
                        if (topLhsAccess.getConstantPoolFieldIndex() == assign.getConstantPoolFieldIndex()) {
                           final Instruction child = ((Instruction) assign).getFirstChild().getNextExpr();
                           final Instruction valueToAssign = assign.getValueToAssign();
                           if (valueToAssign instanceof I_IADD) {

                              final I_IADD add = (I_IADD) child;
                              final Instruction lhs = add.getLhs();
                              final Instruction rhs = add.getRhs();
                              if (lhs instanceof AccessInstanceField) {
                                 if (rhs instanceof I_ICONST_1) {

                                    final IncrementInstruction inc = new IncrementInstruction(MethodModel.this,
                                          (Instruction) topLhsAccess, true, true);
                                    _expressionList.replaceInclusive(topAddRaw, assignRaw, inc);

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

                  final AccessLocalVariable access = (AccessLocalVariable) i;
                  final AssignToLocalVariable assign = (AssignToLocalVariable) i.getNextExpr();
                  if (access.getLocalVariableTableIndex() == assign.getLocalVariableTableIndex()) {
                     final IncrementInstruction inc = new IncrementInstruction(MethodModel.this, (Instruction) access, true, false);
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

                  final AccessLocalVariable access = (AccessLocalVariable) i;
                  final AssignToLocalVariable assign = (AssignToLocalVariable) i.getNextExpr();
                  if (access.getLocalVariableTableIndex() == assign.getLocalVariableTableIndex()) {
                     final IncrementInstruction inc = new IncrementInstruction(MethodModel.this, (Instruction) access, false, false);
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

                  final AssignToLocalVariable assign = (AssignToLocalVariable) i;
                  final AccessLocalVariable access = (AccessLocalVariable) i.getNextExpr();
                  if (access.getLocalVariableTableIndex() == assign.getLocalVariableTableIndex()) {
                     final IncrementInstruction inc = new IncrementInstruction(MethodModel.this, (Instruction) access, true, true);
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

                  final AccessLocalVariable access = (AccessLocalVariable) i;
                  if (access.getLocalVariableTableIndex() != 0) { // we don;t want to trap on 'this' references ;) 
                     final AssignToLocalVariable assign = (AssignToLocalVariable) i.getNextExpr();
                     if (access.getLocalVariableTableIndex() != assign.getLocalVariableTableIndex()) {
                        final InlineAssignInstruction inlineAssign = new InlineAssignInstruction(MethodModel.this, assign,
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

                  final I_IINC iinc = (I_IINC) i;
                  final AccessLocalVariable access = (AccessLocalVariable) i.getNextExpr();
                  if (iinc.getLocalVariableTableIndex() == access.getLocalVariableTableIndex()) {

                     final IncrementInstruction inc = new IncrementInstruction(MethodModel.this, (Instruction) access,
                           iinc.isInc(), true);
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

                  final AccessLocalVariable access = (AccessLocalVariable) i;
                  final I_IINC iinc = (I_IINC) i.getNextExpr();

                  if (iinc.getLocalVariableTableIndex() == access.getLocalVariableTableIndex()) {

                     final IncrementInstruction inc = new IncrementInstruction(MethodModel.this, (Instruction) access,
                           iinc.isInc(), false);
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

                  final CastOperator cast = (CastOperator) i;

                  final AssignToLocalVariable assign = (AssignToLocalVariable) i.getNextExpr();

                  final InlineAssignInstruction inlineAssign = new InlineAssignInstruction(MethodModel.this, assign, cast);
                  _expressionList.replaceInclusive(cast, (Instruction) assign, inlineAssign);
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

                  final Instruction addRaw = i;
                  final Instruction assignArrayRaw = i.getNextExpr();
                  //   I_IADD add = (I_IADD) addRaw.getReal();
                  final AssignToArrayElement assignArray = (AssignToArrayElement) assignArrayRaw.getReal();
                  final FieldArrayElementIncrement inlineAssign = new FieldArrayElementIncrement(MethodModel.this, assignArray,
                        true, true);
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

                  final Instruction subRaw = i;
                  final Instruction assignArrayRaw = i.getNextExpr();
                  //   I_IADD add = (I_IADD) addRaw.getReal();
                  final AssignToArrayElement assignArray = (AssignToArrayElement) assignArrayRaw.getReal();
                  final FieldArrayElementIncrement inlineAssign = new FieldArrayElementIncrement(MethodModel.this, assignArray,
                        false, true);
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
                  final Instruction accessArrayRaw = i;
                  final Instruction assignArrayRaw = i.getNextExpr();
                  final AccessArrayElement accessArray = (AccessArrayElement) accessArrayRaw.getReal();
                  final AssignToArrayElement assignArray = (AssignToArrayElement) assignArrayRaw.getReal();
                  final AccessField accessField1 = (AccessField) accessArray.getArrayRef().getReal();
                  final AccessField accessField2 = (AccessField) assignArray.getArrayRef().getReal();
                  if (accessField1.getConstantPoolFieldIndex() == accessField2.getConstantPoolFieldIndex()) {
                     // we accessing the same field at least
                     //AccessLocalVariable accessLocalVariable1 = (AccessLocalVariable) accessArray.getArrayIndex().getReal();
                     //AccessLocalVariable accessLocalVariable2 = (AccessLocalVariable) assignArray.getArrayIndex().getReal();
                     //  if (accessLocalVariable1.getLocalVariableTableIndex() == accessLocalVariable2.getLocalVariableTableIndex()) {
                     // and both arrays are referencing the array element using the same variable
                     final FieldArrayElementIncrement inlineAssign = new FieldArrayElementIncrement(MethodModel.this, assignArray,
                           true, false);
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

                  final Instruction accessArrayRaw = i;
                  final Instruction assignArrayRaw = i.getNextExpr();
                  final AccessArrayElement accessArray = (AccessArrayElement) accessArrayRaw.getReal();
                  final AssignToArrayElement assignArray = (AssignToArrayElement) assignArrayRaw.getReal();
                  final AccessField accessField1 = (AccessField) accessArray.getArrayRef().getReal();
                  final AccessField accessField2 = (AccessField) assignArray.getArrayRef().getReal();
                  if (accessField1.getConstantPoolFieldIndex() == accessField2.getConstantPoolFieldIndex()) {
                     // we accessing the same field at least
                     final AccessLocalVariable accessLocalVariable1 = (AccessLocalVariable) accessArray.getArrayIndex().getReal();
                     final AccessLocalVariable accessLocalVariable2 = (AccessLocalVariable) assignArray.getArrayIndex().getReal();
                     if (accessLocalVariable1.getLocalVariableTableIndex() == accessLocalVariable2.getLocalVariableTableIndex()) {
                        // and both arrays are referencing the array element using the same variable
                        final FieldArrayElementIncrement inlineAssign = new FieldArrayElementIncrement(MethodModel.this,
                              assignArray, false, false);
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

                  final Instruction invoke = i;

                  final AssignToLocalVariable assign = (AssignToLocalVariable) i.getNextExpr();

                  final InlineAssignInstruction inlineAssign = new InlineAssignInstruction(MethodModel.this, assign, invoke);
                  _expressionList.replaceInclusive(invoke, (Instruction) assign, inlineAssign);
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

                  final Instruction constant = i;

                  final AssignToLocalVariable assign = (AssignToLocalVariable) i.getNextExpr();

                  final InlineAssignInstruction inlineAssign = new InlineAssignInstruction(MethodModel.this, assign, constant);
                  _expressionList.replaceInclusive(constant, (Instruction) assign, inlineAssign);
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

                  final Instruction invoke = i;

                  final AssignToArrayElement assign = (AssignToArrayElement) i.getNextExpr();

                  final FieldArrayElementAssign inlineAssign = new FieldArrayElementAssign(MethodModel.this, assign, invoke);
                  _expressionList.replaceInclusive(invoke, assign, inlineAssign);
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

                  final Instruction invoke = i;

                  final AssignToArrayElement assign = (AssignToArrayElement) i.getNextExpr();

                  final FieldArrayElementAssign inlineAssign = new FieldArrayElementAssign(MethodModel.this, assign, invoke);
                  _expressionList.replaceInclusive(invoke, assign, inlineAssign);

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
         final Instruction assignFirst = _operandStart.getNextExpr();
         Instruction assign = assignFirst;
         int count = 0;
         while ((assign != null) && (assign instanceof AssignToLocalVariable)) {
            assign = assign.getNextExpr();
            count++;
         }
         if (assign == null) {
            final Instruction newOne = new MultiAssignInstruction(this, _operandStart, assignFirst, assign);
            _expressionList.replaceInclusive(_operandStart, assign, newOne);
            txformed = true;
         }
      }

      if (!txformed) {
         boolean again = false;
         for (Instruction i = _operandStart; i != null; i = again ? i : i.getNextExpr()) {
            again = false;

            for (final InstructionTransformer txformer : transformers) {
               final Instruction newI = txformer.transform(_expressionList, i);
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
      final String methodName = getMethod().getName();
      String rawVarNameCandidate = null;
      boolean mightBeGetter = true;

      if (methodName.startsWith("get")) {
         rawVarNameCandidate = methodName.substring(3);
      } else if (methodName.startsWith("is")) {
         rawVarNameCandidate = methodName.substring(2);
      } else {
         mightBeGetter = false;
      }

      // Getters should have 3 bcs: aload_0, getfield, ?return
      if (mightBeGetter) {
         boolean possiblySimpleGetImplementation = pcMap.size() == 3;
         if ((rawVarNameCandidate != null) && (isNoCL() || possiblySimpleGetImplementation)) {
            final String firstLetter = rawVarNameCandidate.substring(0, 1).toLowerCase();
            final String varNameCandidateCamelCased = rawVarNameCandidate.replaceFirst(rawVarNameCandidate.substring(0, 1), firstLetter);
            String accessedFieldName;

            if (!isNoCL()) {

               Instruction instruction = expressionList.getHead();

               if ((instruction instanceof Return) && (expressionList.getHead() == expressionList.getTail())) {
                  instruction = instruction.getPrevPC();
                  if (instruction instanceof AccessInstanceField) {
                     final FieldEntry field = ((AccessInstanceField) instruction).getConstantPoolFieldEntry();
                     accessedFieldName = field.getNameAndTypeEntry().getNameUTF8Entry().getUTF8();
                     if (accessedFieldName.equals(varNameCandidateCamelCased)) {

                        // Verify field type matches return type
                        final String fieldType = field.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8();
                        final String returnType = getMethod().getDescriptor().substring(2);
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
                              setAccessorVariableFieldEntry(field);
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
               FieldEntry fieldEntry = getMethod().getOwnerClassModel().getConstantPool().getFieldEntry(varNameCandidateCamelCased);
               setAccessorVariableFieldEntry(fieldEntry);
               if (getAccessorVariableFieldEntry() == null) {
                  throw new ClassParseException(ClassParseException.TYPE.BADGETTERNAMEMISMATCH, methodName);
               }
               methodIsGetter = true;
               if (method.getClassModel().getPrivateMemorySize(fieldEntry.getNameAndTypeEntry().getNameUTF8Entry().getUTF8()) != null)
               {
                  methodIsPrivateMemoryGetter = true;
               }
            }
         } else {
            throw new ClassParseException(ClassParseException.TYPE.BADGETTERNAMENOTFOUND, methodName);
         }
      }
   }

   private void setAccessorVariableFieldEntry(FieldEntry field) {
      accessorVariableFieldEntry = field;
   }

   /**
    * Determine if this method is a setter and record the accessed field if so
    */
   void checkForSetter(Map<Integer, Instruction> pcMap) throws ClassParseException {
      final String methodName = getMethod().getName();
      if (methodName.startsWith("set")) {
         final String rawVarNameCandidate = methodName.substring(3);
         final String firstLetter = rawVarNameCandidate.substring(0, 1).toLowerCase();
         final String varNameCandidateCamelCased = rawVarNameCandidate.replaceFirst(rawVarNameCandidate.substring(0, 1),
               firstLetter);
         String accessedFieldName = null;
         final Instruction instruction = expressionList.getHead();

         // setters should be aload_0, ?load_1, putfield, return
         if ((instruction instanceof AssignToInstanceField) && (expressionList.getTail() instanceof Return) && (pcMap.size() == 4)) {
            final Instruction prev = instruction.getPrevPC();
            if (prev instanceof AccessLocalVariable) {
               final FieldEntry field = ((AssignToInstanceField) instruction).getConstantPoolFieldEntry();
               accessedFieldName = field.getNameAndTypeEntry().getNameUTF8Entry().getUTF8();
               if (accessedFieldName.equals(varNameCandidateCamelCased)) {

                  // Verify field type matches setter arg type
                  final String fieldType = field.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8();
                  final String setterArgType = getMethod().getDescriptor().substring(1, 2);

                  //System.out.println( "### field type = " + fieldType );
                  //System.out.println( "### setter type = " + setterArgType );
                  assert fieldType.length() == 1 : " can only use basic type getters";

                  if (fieldType.equals(setterArgType)) {
                     if (logger.isLoggable(Level.FINE)) {
                        logger.fine("Found " + methodName + " as a setter for " + varNameCandidateCamelCased.toLowerCase()
                              + " of type " + fieldType);
                     }

                     methodIsSetter = true;
                     setAccessorVariableFieldEntry(field);

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

      class Var implements LocalVariableInfo{

         int startPc = 0;

         int endPc = 0;

         String name = null;

         boolean arg;

         String descriptor = "";

         int slotIndex;

         Var(StoreSpec _storeSpec, int _slotIndex, int _startPc, boolean _arg) {
            slotIndex = _slotIndex;
            arg = _arg;
            startPc = _startPc;
            if (_storeSpec.equals(StoreSpec.A)) {
               name = "arr_" + _slotIndex;
               descriptor = "/* arg */";
            } else {
               name = _storeSpec.toString().toLowerCase() + "_" + _slotIndex;
               descriptor = _storeSpec.toString();
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

         @Override public boolean isArray() {
            return name.startsWith("arr");
         }

         @Override public int getStart() {
            return startPc;
         }

         @Override public int getEnd() {
            return endPc;
         }

         @Override public int getLength() {
            return endPc - startPc;
         }

         @Override public String getVariableName() {
            return (name);
         }

         @Override public String getVariableDescriptor() {
            return (descriptor);
         }

         @Override public int getVariableIndex() {
            return (slotIndex);
         }
      }

      final List<LocalVariableInfo> list = new ArrayList<>();

      public FakeLocalVariableTableEntry(Map<Integer, Instruction> _pcMap, ClassModelMethod _method) {
         int numberOfSlots = _method.getCodeEntry().getMaxLocals();

         MethodDescription description = ClassModel.getMethodDescription(_method.getDescriptor());
         String[] args = description.getArgs();

         int thisOffset = _method.isStatic() ? 0 : 1;

         Var[] vars = new Var[numberOfSlots + thisOffset];
         StoreSpec[] argsAsStoreSpecs = new StoreSpec[args.length + thisOffset];
         if (thisOffset == 1) {
            argsAsStoreSpecs[0] = StoreSpec.O;
            vars[0] = new Var(argsAsStoreSpecs[0], 0, 0, true);
            list.add(vars[0]);

         }
         for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("[")) {
               argsAsStoreSpecs[i + thisOffset] = StoreSpec.A;
            } else {
               argsAsStoreSpecs[i + thisOffset] = StoreSpec.valueOf(args[i].substring(0, 1));
            }
            vars[i + thisOffset] = new Var(argsAsStoreSpecs[i + thisOffset], i + thisOffset, 0, true);
            list.add(vars[i + thisOffset]);
         }
         for (int i = args.length + thisOffset; i < numberOfSlots + thisOffset; i++) {
            vars[i] = new Var();
         }

         int pc = 0;
         Instruction instruction = null;
         for (Entry<Integer, Instruction> entry : _pcMap.entrySet()) {

            pc = entry.getKey();
            instruction = entry.getValue();
            StoreSpec storeSpec = instruction.getByteCode().getStore();

            if (storeSpec != StoreSpec.NONE) {
               int slotIndex = ((InstructionSet.LocalVariableTableIndexAccessor) instruction).getLocalVariableTableIndex();
               Var prevVar = vars[slotIndex];
               Var var = new Var(storeSpec, slotIndex, pc + instruction.getLength(), false); // will get collected pretty soon if this is not the same as the previous in this slot
               if (!prevVar.equals(var)) {
                  prevVar.endPc = pc;
                  vars[slotIndex] = var;
                  list.add(vars[slotIndex]);
               }
            }
         }
         for (int i = 0; i < numberOfSlots + thisOffset; i++) {
            vars[i].endPc = pc + instruction.getLength();
         }

         Collections.sort(list, new Comparator<LocalVariableInfo>(){
            @Override public int compare(LocalVariableInfo o1, LocalVariableInfo o2) {
               return o1.getStart() - o2.getStart();
            }
         });

         if (Config.enableShowFakeLocalVariableTable) {
            System.out.println("FakeLocalVariableTable:");
            System.out.println(" Start  Length  Slot    Name   Signature");
            for (LocalVariableInfo lvi : list) {
               Var var = (Var) lvi;
               System.out.println(String.format(" %5d   %5d  %4d  %8s     %s", var.startPc, var.getLength(), var.slotIndex,
                     var.name, var.descriptor));
            }
         }
      }

      @Override public LocalVariableInfo getVariable(int _pc, int _index) {
         LocalVariableInfo returnValue = null;
         //  System.out.println("pc = " + _pc + " index = " + _index);
         for (LocalVariableInfo localVariableInfo : list) {
            // System.out.println("   start=" + localVariableInfo.getStart() + " length=" + localVariableInfo.getLength()
            // + " varidx=" + localVariableInfo.getVariableIndex());
            if (_pc >= localVariableInfo.getStart() - 1 && _pc <= (localVariableInfo.getStart() + localVariableInfo.getLength())
                  && _index == localVariableInfo.getVariableIndex()) {
               returnValue = localVariableInfo;
               break;
            }
         }
         return (returnValue);
      }

      @Override public Iterator<LocalVariableInfo> iterator() {
         return list.iterator();
      }

   }

   private void init(ClassModelMethod _method) throws AparapiException {
      try {
         method = _method;
         expressionList = new ExpressionList(this);
         ClassModel owner = _method.getOwnerClassModel();
         if (owner.getNoCLMethods().contains(method.getName())) {
             noCL = true;
         }

         // check if we have any exception handlers
         final int exceptionsSize = method.getCodeEntry().getExceptionPoolEntries().size();
         if (exceptionsSize > 0) {
            if (logger.isLoggable(Level.FINE)) {
               logger.fine("exception size for " + method + " = " + exceptionsSize);
            }
            throw new ClassParseException(ClassParseException.TYPE.EXCEPTION);
         }

         // check if we have any local variables which are arrays.  This is an attempt to avoid aliasing field arrays

         // We are going to make 4 passes.

         // Pass #1 create a linked list of instructions from head to tail
         final Map<Integer, Instruction> pcMap = createListOfInstructions();

         LocalVariableTableEntry<LocalVariableInfo> localVariableTableEntry = method.getLocalVariableTableEntry();
         if (localVariableTableEntry == null) {
            localVariableTableEntry = new FakeLocalVariableTableEntry(pcMap, method);
            method.setLocalVariableTableEntry(localVariableTableEntry);
            logger.warning("Method "
                  + method.getName()
                  + method.getDescriptor()
                  + " does not contain a LocalVariableTable entry (source not compiled with -g) aparapi will attempt to create a synthetic table based on bytecode. This is experimental!!");
         }

         // pass #2 build branch graph
         buildBranchGraphs(pcMap);

         // pass #3 build branch graph
         deoptimizeReverseBranches();

         // pass #4

         foldExpressions();

         // Accessor conversion only works on member object arrays
         if (isNoCL() || (entrypoint != null) && (_method.getClassModel() != entrypoint.getClassModel())) {
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
      } catch (final Throwable _t) {
         if (_t instanceof ClassParseException) {
            _t.printStackTrace();
            throw (ClassParseException) _t;
         }
         throw new ClassParseException(_t);

      }
   }

   public LocalVariableTableEntry<LocalVariableInfo> getLocalVariableTableEntry() {
      return (method.getLocalVariableTableEntry());
   }

   public ConstantPool getConstantPool() {
      return (method.getConstantPool());
   }

   public LocalVariableInfo getLocalVariable(int _pc, int _index) {
      return (method.getLocalVariable(_pc, _index));
   }

   public String getSimpleName() {
      return (method.getName());
   }

   /*
    * @return the fully qualified name such as "com_amd_javalabs_opencl_demo_PaternityTest$SimpleKernel__actuallyDoIt"
    */
   public String getName() {
      return (method.getClassModel().getMethod(method.getName(), method.getDescriptor()).getClassModel().getClassWeAreModelling()
            .getName().replace('.', '_')
            + "__" + method.getName());
   }

   public String getReturnType() {
      final String returnType = method.getDescriptorUTF8Entry().getUTF8();
      final int index = returnType.indexOf(")");
      return (returnType.substring(index + 1));
   }

   public List<MethodCall> getMethodCalls() {
      final List<MethodCall> methodCalls = new ArrayList<>();

      for (Instruction i = getPCHead(); i != null; i = i.getNextPC()) {
         if (i instanceof MethodCall) {
            final MethodCall methodCall = (MethodCall) i;
            methodCalls.add(methodCall);
         }
      }
      return (methodCalls);
   }

   public Instruction getPCHead() {
      return (pcHead);
   }

   public Instruction getExprHead() {
      return (expressionList.getHead());
   }

   @Override public String toString() {
      return "MethodModel of " + method;
   }
}
