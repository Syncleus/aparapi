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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amd.aparapi.ClassModel.LocalVariableInfo;
import com.amd.aparapi.ClassModel.LocalVariableTableEntry;
import com.amd.aparapi.InstructionSet.AssignToLocalVariable;
import com.amd.aparapi.InstructionSet.Branch;
import com.amd.aparapi.InstructionSet.ByteCode;
import com.amd.aparapi.InstructionSet.CompositeArbitraryScopeInstruction;
import com.amd.aparapi.InstructionSet.CompositeInstruction;
import com.amd.aparapi.InstructionSet.ConditionalBranch;
import com.amd.aparapi.InstructionSet.FakeGoto;
import com.amd.aparapi.InstructionSet.Return;
import com.amd.aparapi.InstructionSet.UnconditionalBranch;

/**
 * Essentially a glorified linked list of Instructions plus some additional state to allow us to transform sequences.
 * 
 * ExpressionLists do have the notion of a parent which allows us to clone an existing parent, allow transformations 
 * and then possibly commit or abort the transformations at will. 
 * @author gfrost
 *
 */
class ExpressionList{

   private static Logger logger = Logger.getLogger(Config.getLoggerName());

   private MethodModel methodModel;

   private ExpressionList parent;

   private Instruction head;

   private Instruction tail;

   private Instruction instruction;

   private ExpressionList(MethodModel _methodModel, ExpressionList _parent, Instruction _instruction) {

      methodModel = _methodModel;
      parent = _parent;
      instruction = _instruction;
      if (parent != null) {
         head = parent.head;
         tail = parent.tail;
      }
      if (instruction != null) {
         tail = _instruction.getPrevExpr();
         tail.setNextExpr(null);
         _instruction.setPrevExpr(null);
      }
   }

   ExpressionList(MethodModel _methodModel) {
      this(_methodModel, null, null);
   }

   /**
    * Determine whether the sequence of instructions from _start to _extent is free of branches which extend beyond _extent. 
    * 
    * As a side effect, if we find a possible branch it is likely a break or continue so we mark the conditional as such.
    *
    * @param _start
    * @param _extent
    * @return
    */
   boolean doesNotContainContinueOrBreak(Instruction _start, Instruction _extent) {
      boolean ok = true;
      boolean breakOrContinue = false;
      for (Instruction i = _start; i != null; i = i.getNextExpr()) {
         if (i.isBranch()) {
            if (i.asBranch().isForwardUnconditional() && i.asBranch().getTarget().isAfter(_extent)) {
               breakOrContinue = true;
            } else {
               ok = false;
               break;
            }
         }
      }
      if (ok) {
         if (breakOrContinue) {
            for (Instruction i = _start; i != null; i = i.getNextExpr()) {
               if (i.isBranch() && i.asBranch().isForwardUnconditional() && i.asBranch().getTarget().isAfter(_extent)) {
                  i.asBranch().setBreakOrContinue(true);
               }
            }
         }
      }
      return (ok);
   }

   boolean doesNotContainCompositeOrBranch(Instruction _start, Instruction _exclusiveEnd) {
      boolean ok = true;
      for (Instruction i = _start; i != null && i != _exclusiveEnd; i = i.getNextExpr()) {
         if (!(i instanceof CompositeInstruction) && (i.isBranch())) {
            ok = false;
            break;
         }
      }
      return (ok);
   }

   void unwind() {
      if (parent != null) {
         if (instruction != null) {
            tail.setNextExpr(instruction);
            instruction.setPrevExpr(tail);
            parent.head = head;
         } else {
            parent.head = head;
            parent.tail = tail;
         }
      }
   }

   /**
    *  [1] [2] [3] [4]
    *  
    *  Note that passing null here essentially deletes the existing expression list and returns the expression
    *  
    * @param _newTail
    * @return
    */

   Instruction createList(final Instruction _newTail) {
      Instruction childExprHead = null;
      if (_newTail == null) {
         childExprHead = head;
         tail = head = null;
      } else {
         childExprHead = _newTail.getNextExpr();
         tail = _newTail;
         _newTail.setNextExpr(null);
         if (childExprHead != null) {
            childExprHead.setPrevExpr(null);
         }

      }
      return (childExprHead);
   }

   /**
    * Add this instruction to the end of the list. 
    * 
    * @param _instruction
    * @return The instruction we added
    */

   Instruction add(Instruction _instruction) {

      if (head == null) {
         head = _instruction;
      } else {
         _instruction.setPrevExpr(tail);
         tail.setNextExpr(_instruction);

      }
      tail = _instruction;
      MethodModel.logger.log(Level.FINE, "After PUSH of " + _instruction + " tail=" + tail);
      return (tail);
   }

   /**
    * Insert the given instruction (_newone) between the existing entries (_prev and _next). 
    * @param _prev
    * @param _next
    * @param _newOne
    */
   void insertBetween(Instruction _prev, Instruction _next, Instruction _newOne) {
      _newOne.setNextExpr(null);
      _newOne.setPrevExpr(null);
      if (_prev == null) {
         // this is the new head
         if (_next == null) {
            head = tail = _newOne;
         } else {
            _newOne.setNextExpr(head);
            head.setPrevExpr(_newOne);
            head = _newOne;
         }
      } else if (_next == null) {
         _newOne.setPrevExpr(tail);
         tail.setNextExpr(_newOne);
         tail = _newOne;
      } else {
         _newOne.setNextExpr(_prev.getNextExpr());
         _newOne.setPrevExpr(_next.getPrevExpr());
         _prev.setNextExpr(_newOne);
         _next.setPrevExpr(_newOne);
      }

   }

   /**
     * Inclusive replace between _head and _tail with _newOne. 
      * 
      * <pre>
      *    |      | --> |       | ---> ... ---> |       | ---> |      |
      *    | prev |     | _head |               | _tail |      | next |
      *    |      | <-- |       | <--- ... <----|       | <--- |      |
      * </pre>
      *  To 
      * <pre>
      *    |      | --> |         | ---> |      |
      *    | prev |     | _newOne |      | next |
      *    |      | <-- |         | <--- |      |
      * </pre>
      */

   void replaceInclusive(Instruction _head, Instruction _tail, Instruction _newOne) {
      _newOne.setNextExpr(null);
      _newOne.setPrevExpr(null);
      Instruction prevHead = _head.getPrevExpr();
      if (_tail == null) {
         // this is the new tail
         _newOne.setPrevExpr(prevHead);
         prevHead.setNextExpr(_newOne);
         tail = _newOne;
      } else {
         Instruction tailNext = _tail.getNextExpr();
         if (prevHead == null) {
            // this is the new head
            if (tailNext == null) {
               head = tail = _newOne;
            } else {
               _newOne.setNextExpr(head);
               head.setPrevExpr(_newOne);
               head = _newOne;
            }
         } else if (tailNext == null) {
            _newOne.setPrevExpr(prevHead);
            prevHead.setNextExpr(_newOne);
            tail = _newOne;
            _head.setPrevExpr(null);
         } else {
            _newOne.setNextExpr(tailNext);
            _newOne.setPrevExpr(prevHead);
            prevHead.setNextExpr(_newOne);
            tailNext.setPrevExpr(_newOne);

         }
         _tail.setNextExpr(null);
         _head.setPrevExpr(null);
      }

   }

   /**
    * Fold headTail.tail into valid composites
    * 
    * <pre>
    * if(??){then}... 
    *   ?? ?> [THEN] ...
    *       -------->
    *
    * if (??){THEN}else{ELSE}...
    * 
    *   ?? ?> [THEN] >> [ELSE] ...
    *       ------------>
    *                 -------->
    *               
    * sun for (INIT,??,DELTA){BODY} ...
    * 
    *    [INIT] ?? ?> [BODY] [DELTA] << ...
    *               ------------------>
    *            <-------------------
    *        
    * sun for (,??,DELTA){BODY} ...
    * 
    *     ?? ?> [BODY] [DELTA] << ...
    *         ------------------>
    *      <-------------------    
    *        
    * sun while (?){l} ...
    * 
    *    ?? ?> [BODY] << ...
    *        ----------->
    *     <------------
    *               
    * eclipse for (INIT,??,DELTA){BODY} ...
    *    [INIT] >> [BODY] [DELTA] ?? ?< ...
    *            ---------------->
    *              <-----------------
    *          
    * eclipse for (,??,DELTA){BODY} ...
    *    >> [BODY] [DELTA] ?? ?< ...
    *     --------------->
    *       <-----------------
    *      
    * eclipse while (??){BODY} ...
    *    >> [BODY] ?? ?< ...
    *     -------->
    *       <----------
    *
    * eclipe if (?1) { while (?2) {BODY} } else {ELSE} ...
    *    ?1 ?> >> [BODY] ?2 ?< >> [ELSE] ...
    *           --------->
    *              <---------
    *        --------------------->    
    *                           -------->   
    * 
    * sun for (,?1,DELTA){ if (?2) { THEN break; } BODY} ...
    * 
    *     ?1 ?> ?2 ?> [THEN] >> [BODY] [DELTA] << ...
    *               ----------->
    *         ---------------------------------->
    *                         ------------------>
    *     <------------------------------------ 
    *     
    * sun for (,?1,DELTA){ if (?2) { THEN continue; } BODY} ...
    * 
    *     ?1 ?> ?2 ?> THEN >> [BODY] [DELTA] << ...
    *               --------->
    *                       -------->
    *         -------------------------------->
    *     <----------------------------------     
    *           
    * Some exceptions based on sun javac optimizations
    * 
    * if (?1){ if (?2){THEN} }else{ ELSE } ...
    *   One might expect 
    *    ?1 ?> ?2 ?> [THEN] >> [ELSE] ...
    *        ----------------->
    *              -------->!         
    *                        ------------->
    *   However the conditional branch to the unconditional (!) is optimized away and instead the unconditional inverted and extended 
    *                   
    *    ?1 ?> ?2 ?> [THEN] >> [ELSE] ...
    *        ----------------->
    *              --------*--------->
    *              
    * sun if (?1) { while (?2) {l} } else {e} ...
    *   One might expect 
    *    ?1 ?> ?2 ?> [BODY] << >> [ELSE] ...
    *        ------------------->
    *              ----------->!
    *            <----------    
    *                           -------->
    *                    
    *   However as above the conditional branch to the unconditional (!) can be optimized away and the conditional inverted and extended 
    *    ?1 ?> ?2 ?> [BODY] << >> [ELSE] ...
    *        -------------------->
    *              -----------*--------->   
    *            <-----------  
    *              
    *   However we can also now remove the forward unconditional completely as it is unreachable
    *    ?1 ?> ?2 ?> [BODY] << [ELSE] ...
    *        ----------------->
    *              ------------------>   
    *            <-----------       
    *               
    * sun while(?1){if (?2) {THEN} else {ELSE} } ...
    *   One might expect 
    *    ?1 ?> ?2 ?> [BODY] >> [ELSE] << ...
    *         -------------------------->
    *           <---------------------
    *               ---------->    
    *                         ------->!
    *                    
    *   However the unconditional branch to the unconditional backbranch (!) can be optimized away and the unconditional wrapped back directly to the loop control head 
    *    ?1 ?> ?2 ?> [BODY] << [ELSE] << ...
    *         -------------------------->
    *           <---------------------
    *               ---------->    
    *           <-----------
                                        
    * </pre>
    * @param _instruction
    * @throws ClassParseException 
    */
   boolean foldComposite(final Instruction _instruction) throws ClassParseException {
      boolean handled = false;
      try {

         if (logger.isLoggable(Level.FINE)) {
            System.out.println("foldComposite: curr = " + _instruction);
            System.out.println(dumpDiagram(_instruction));
            // System.out.println(dumpDiagram(null, _instruction));
         }
         if (_instruction.isForwardBranchTarget() || (tail != null && tail.isBranch() && tail.asBranch().isReverseConditional())) {
            while (_instruction.isForwardBranchTarget()
                  || (tail != null && tail.isBranch() && tail.asBranch().isReverseConditional())) {
               if (logger.isLoggable(Level.FINE)) {
                  System.out.println(dumpDiagram(_instruction));

               }
               handled = false;

               if (tail != null && tail.isBranch() && tail.asBranch().isReverseConditional()) {
                  /**
                   * This looks like an eclipse style for/while loop or possibly a do{}while()
                   * <pre>
                   * eclipse for (INIT,??,DELTA){BODY} ...
                   *    [INIT] >> [BODY] [DELTA] ?? ?< ...
                   *            ---------------->
                   *              <-----------------
                   *          
                   * eclipse for (,??,DELTA){BODY} ...
                   *    >> [BODY] [DELTA] ?? ?< ...
                   *     --------------->
                   *       <-----------------
                   *      
                   * eclipse while (??){BODY} ...
                   *    >> [BODY] ?? ?< ...
                   *     -------->
                   *       <----------
                   * do {BODY} while(??)
                   *    [BODY] ?? ?< ...
                   *    <-----------
                   *    
                   * </pre>
                   **/
                  BranchSet branchSet = ((ConditionalBranch) tail.asBranch()).getOrCreateBranchSet();
                  Instruction loopTop = branchSet.getTarget().getRootExpr();
                  Instruction beginingOfBranch = branchSet.getFirst();

                  Instruction startOfBeginningOfBranch = beginingOfBranch.getStartInstruction();
                  // empty loops sometimes look like eclipse loops!
                  if (startOfBeginningOfBranch == loopTop) {

                     loopTop = loopTop.getPrevExpr();
                     if (loopTop instanceof AssignToLocalVariable) {
                        LocalVariableInfo localVariableInfo = ((AssignToLocalVariable) loopTop).getLocalVariableInfo();
                        if (localVariableInfo.getStart() == loopTop.getNextExpr().getStartPC()
                              && localVariableInfo.getEnd() == _instruction.getThisPC()) {
                           loopTop = loopTop.getPrevExpr(); // back up over the initialization
                        }
                     }
                     addAsComposites(ByteCode.COMPOSITE_EMPTY_LOOP, loopTop, branchSet);
                     handled = true;
                  } else {

                     if (loopTop.getPrevExpr() != null && loopTop.getPrevExpr().isBranch()
                           && loopTop.getPrevExpr().asBranch().isForwardUnconditional()) {
                        if (doesNotContainCompositeOrBranch(branchSet.getTarget().getRootExpr(), branchSet.getFirst().getPrevExpr())) {
                           branchSet.unhook();
                           loopTop.getPrevExpr().asBranch().unhook();
                           loopTop = loopTop.getPrevExpr();
                           // looptop == the unconditional?
                           loopTop = loopTop.getPrevExpr();
                           if (loopTop instanceof AssignToLocalVariable) {
                              LocalVariableInfo localVariableInfo = ((AssignToLocalVariable) loopTop).getLocalVariableInfo();
                              if (localVariableInfo.getStart() == loopTop.getNextExpr().getStartPC()
                                    && localVariableInfo.getEnd() == _instruction.getThisPC()) {
                                 loopTop = loopTop.getPrevExpr(); // back up over the initialization
                              }
                           }
                           addAsComposites(ByteCode.COMPOSITE_FOR_ECLIPSE, loopTop, branchSet);
                           handled = true;
                        }
                     }
                     if (!handled){
                        // do{}while()_ do not require any previous instruction
                       if (loopTop.getPrevExpr() ==null){
                           throw new IllegalStateException("might be a dowhile with no provious expression");
                         
                        }else if (!(loopTop.getPrevExpr().isBranch() && loopTop.getPrevExpr().asBranch().isForwardUnconditional())){
                           if (doesNotContainCompositeOrBranch(branchSet.getTarget().getRootExpr(), branchSet.getFirst().getPrevExpr())) {
                              loopTop = loopTop.getPrevExpr();
                               branchSet.unhook();
                              addAsComposites(ByteCode.COMPOSITE_DO_WHILE, loopTop, branchSet);
                              handled = true;
                           }
                        }else{
                           throw new IllegalStateException("might be mistaken for a do while!");
                        }
                    
                         
                     }
                  }
               }
               if (!handled && _instruction.isForwardConditionalBranchTarget() && tail.isBranch()
                     && tail.asBranch().isReverseUnconditional()) {

                  /**
                   * This is s sun style loop 
                   * <pre>       
                   * sun for (INIT,??,DELTA){BODY} ...
                   * 
                   *    [INIT] ?? ?> [BODY] [DELTA] << ...
                   *               ------------------>
                   *            <-------------------
                   *        
                   * sun for (,??,DELTA){BODY} ...
                   * 
                   *     ?? ?> [BODY] [DELTA] << ...
                   *         ------------------>
                   *      <-------------------    
                   *        
                   * sun while (?){l} ...
                   *  
                   *    ?? ?> [BODY] << ...
                   *         ----------->
                   *     <------------
                   *               
                   *</pre>
                   */
                  ConditionalBranch lastForwardConditional = _instruction.getForwardConditionalBranches().getLast();
                  BranchSet branchSet = lastForwardConditional.getOrCreateBranchSet();
                  Branch reverseGoto = tail.asBranch();
                  Instruction loopBackTarget = reverseGoto.getTarget();
                  if (loopBackTarget.getReverseUnconditionalBranches().size() > 1) {
                     throw new ClassParseException(ClassParseException.TYPE.CONFUSINGBRANCHESPOSSIBLYCONTINUE);
                  }
                  if (_instruction.isForwardUnconditionalBranchTarget()) {
                     /**
                      * Check if we have a break
                      * <pre>              
                      *    ?? ?> [BODY] ?1 ?> >> [BODY] << ...
                      *         -------------------------->
                      *                     ---->
                      *                        ----------->
                      *     <----------------------------
                      *               
                      *</pre>
                      */
                     Branch lastForwardUnconditional = _instruction.getForwardUnconditionalBranches().getLast();
                     if (lastForwardUnconditional != null && lastForwardUnconditional.isAfter(lastForwardConditional)) {
                        throw new ClassParseException(ClassParseException.TYPE.CONFUSINGBRANCHESPOSSIBLYBREAK);
                     }
                  }
                  if (loopBackTarget != branchSet.getFirst().getStartInstruction()) {
                     /**
                      * we may have a if(?1){while(?2){}}else{...} where the else goto has been optimized away. 
                      * <pre>
                      *   One might expect 
                      *    ?1 ?> ?2 ?> [BODY] << >> [ELSE] ...
                      *        ------------------->
                      *              ----------->!
                      *            <----------    
                      *                           -------->
                      *                    
                      *   However as above the conditional branch to the unconditional (!) can be optimized away and the conditional inverted and extended 
                      *    ?1 ?> ?2 ?> [BODY] << >> [ELSE] ...
                      *        -------------------->
                      *              -----------*--------->   
                      *            <-----------  
                      *              
                      *   However we can also now remove the forward unconditional completely as it is unreachable
                      *    ?1 ?> ?2 ?> [BODY] << [ELSE] ...
                      *        ----------------->
                      *              ------------------>   
                      *            <-----------       
                      *               
                      * </pre>
                      */

                     Instruction loopbackTargetRoot = loopBackTarget.getRootExpr();
                     if (loopbackTargetRoot.isBranch() && loopbackTargetRoot.asBranch().isConditional()) {
                        ConditionalBranch topOfRealLoop = (ConditionalBranch) loopbackTargetRoot.asBranch();
                        BranchSet extentBranchSet = topOfRealLoop.getBranchSet();
                        if (topOfRealLoop.getBranchSet() == null) {
                           extentBranchSet = topOfRealLoop.findEndOfConditionalBranchSet(_instruction.getNextPC())
                                 .getOrCreateBranchSet();
                        }
                        // We believe that this extendBranchSet is the real top of the while.
                        if (doesNotContainCompositeOrBranch(extentBranchSet.getLast().getNextExpr(), reverseGoto)) {

                           Instruction loopTop = topOfRealLoop.getPrevExpr();
                           if (loopTop instanceof AssignToLocalVariable) {
                              LocalVariableInfo localVariableInfo = ((AssignToLocalVariable) loopTop).getLocalVariableInfo();
                              if (localVariableInfo.getStart() == loopTop.getNextExpr().getStartPC()
                                    && localVariableInfo.getEnd() == _instruction.getThisPC()) {
                                 loopTop = loopTop.getPrevExpr(); // back up over the initialization
                              }
                           }
                           extentBranchSet.unhook();

                           addAsComposites(ByteCode.COMPOSITE_FOR_SUN, loopTop, extentBranchSet);
                           UnconditionalBranch fakeGoto = new FakeGoto(methodModel, extentBranchSet.getLast().getTarget());

                           add(fakeGoto);
                           extentBranchSet.getLast().getTarget().addBranchTarget(fakeGoto);

                           handled = true;
                        }
                     }
                  } else {
                     /**
                      * Just a normal sun style loop
                      */
                     if (doesNotContainCompositeOrBranch(branchSet.getLast().getNextExpr(), reverseGoto)) {
                        Instruction loopTop = reverseGoto.getTarget().getRootExpr().getPrevExpr();

                        if (logger.isLoggable(Level.FINEST)) {
                           Instruction next = branchSet.getFirst().getNextExpr();
                           System.out.println("### for/while candidate exprs: " + branchSet.getFirst());
                           while (next != null) {
                              System.out.println("### expr = " + next);
                              next = next.getNextExpr();
                           }
                        }

                        if (loopTop instanceof AssignToLocalVariable) {
                           LocalVariableInfo localVariableInfo = ((AssignToLocalVariable) loopTop).getLocalVariableInfo();
                           if (localVariableInfo.getStart() == loopTop.getNextExpr().getStartPC()
                                 && localVariableInfo.getEnd() == _instruction.getThisPC()) {
                              loopTop = loopTop.getPrevExpr(); // back up over the initialization

                           }
                        }
                        branchSet.unhook();

                        // If there is an inner scope, it is likely that the loop counter var
                        // is modified using an inner scope variable so use while rather than for
                        if (reverseGoto.getPrevExpr() instanceof CompositeArbitraryScopeInstruction) {
                           addAsComposites(ByteCode.COMPOSITE_WHILE, loopTop, branchSet);
                        } else {
                           addAsComposites(ByteCode.COMPOSITE_FOR_SUN, loopTop, branchSet);
                        }
                        handled = true;
                     }

                  }
               }
               if (!handled && !tail.isForwardBranch() && _instruction.isForwardConditionalBranchTarget()) {
                  /**
                   * This an if(exp) 
                   *<pre>             *
                   * if(??){then}... 
                   *   ?? ?> [THEN] ...
                   *       -------->
                   *
                   *</pre>
                   */
                  ConditionalBranch lastForwardConditional = _instruction.getForwardConditionalBranches().getLast();
                  BranchSet branchSet = lastForwardConditional.getOrCreateBranchSet();
                  if (doesNotContainContinueOrBreak(branchSet.getLast().getNextExpr(), _instruction)) {
                     branchSet.unhook();
                     addAsComposites(ByteCode.COMPOSITE_IF, branchSet.getFirst().getPrevExpr(), branchSet);
                     handled = true;
                  }
               }
               if (!handled && !tail.isForwardBranch() && _instruction.isForwardUnconditionalBranchTarget()) {

                  LinkedList<Branch> forwardUnconditionalBranches = _instruction.getForwardUnconditionalBranches();

                  Branch lastForwardUnconditional = forwardUnconditionalBranches.getLast();
                  Instruction afterGoto = lastForwardUnconditional.getNextExpr();
                  if (afterGoto.getStartInstruction().isForwardConditionalBranchTarget()) {
                     LinkedList<ConditionalBranch> forwardConditionalBranches = afterGoto.getStartInstruction()
                           .getForwardConditionalBranches();
                     ConditionalBranch lastForwardConditional = forwardConditionalBranches.getLast();
                     BranchSet branchSet = lastForwardConditional.getOrCreateBranchSet();

                     if (doesNotContainCompositeOrBranch(branchSet.getLast().getNextExpr(), lastForwardUnconditional)) {
                        if (doesNotContainContinueOrBreak(afterGoto.getNextExpr(), _instruction)) {
                           branchSet.unhook();
                           lastForwardUnconditional.unhook();
                           addAsComposites(ByteCode.COMPOSITE_IF_ELSE, branchSet.getFirst().getPrevExpr(), branchSet);
                           handled = true;
                        }
                     } else {
                        //then not clean.   
                        ExpressionList newHeadTail = new ExpressionList(methodModel, this, lastForwardUnconditional);
                        handled = newHeadTail.foldComposite(lastForwardUnconditional.getStartInstruction());
                        newHeadTail.unwind();
                        // handled = foldCompositeRecurse(lastForwardUnconditional);
                        if (!handled && forwardUnconditionalBranches.size() > 1) {
                           //  BI  AI      AE      BE
                           //  ?>  ?>  ..  >>  ..  >>   C   S  
                           //  ?---------------------->22    
                           //      ?---------->18            
                           //              +-------------->31
                           //                      +------>31
                           // Javac sometimes performs the above optimization.  Basically the GOTO for the inner IFELSE(AI,AE) instead of targeting the GOTO
                           // from the outer IFELSE(B1,BE) so instead of AE->BE->... we have AE-->...
                           //
                           // So given more than one target we retreat up the list of unconditionals until we find a clean one treating the previously visited GOTO 
                           // as a possible end

                           for (int i = forwardUnconditionalBranches.size(); i > 1; i--) {
                              Branch thisGoto = forwardUnconditionalBranches.get(i - 1);
                              Branch elseGoto = forwardUnconditionalBranches.get(i - 2);
                              Instruction afterElseGoto = elseGoto.getNextExpr();
                              if (afterElseGoto.getStartInstruction().isConditionalBranchTarget()) {
                                 BranchSet elseBranchSet = afterElseGoto.getStartInstruction().getForwardConditionalBranches()
                                       .getLast().getOrCreateBranchSet();
                                 if (doesNotContainCompositeOrBranch(elseBranchSet.getLast().getNextExpr(), elseGoto)) {
                                    if (doesNotContainCompositeOrBranch(afterElseGoto.getNextExpr(), thisGoto)) {
                                       if (logger.isLoggable(Level.FINE)) {
                                          System.out.println(dumpDiagram(_instruction));
                                       }
                                       elseBranchSet.unhook();
                                       elseGoto.unhook();
                                       if (logger.isLoggable(Level.FINE)) {
                                          System.out.println(dumpDiagram(_instruction));

                                       }

                                       CompositeInstruction composite = CompositeInstruction.create(ByteCode.COMPOSITE_IF_ELSE,
                                             methodModel, elseBranchSet.getFirst(), thisGoto, elseBranchSet);
                                       replaceInclusive(elseBranchSet.getFirst(), thisGoto.getPrevExpr(), composite);

                                       handled = true;

                                       break;
                                    }
                                 }
                              }

                           }

                        }
                     }

                  }

               }
               if (!handled && !tail.isForwardBranch() && _instruction.isForwardConditionalBranchTarget()
                     && _instruction.isForwardUnconditionalBranchTarget()) {
                  // here we have multiple composites ending at the same point

                  Branch lastForwardUnconditional = _instruction.getForwardUnconditionalBranches().getLast();
                  ConditionalBranch lastForwardConditional = _instruction.getStartInstruction().getForwardConditionalBranches()
                        .getLast();
                  // we will clip the tail and see if recursing helps

                  if (lastForwardConditional.getTarget().isAfter(lastForwardUnconditional)) {

                     lastForwardConditional.retarget(lastForwardUnconditional);

                     ExpressionList newHeadTail = new ExpressionList(methodModel, this, lastForwardUnconditional);
                     handled = newHeadTail.foldComposite(lastForwardUnconditional.getStartInstruction());
                     newHeadTail.unwind();

                  }

               }
               if (!handled) {
                  break;
               }
            }

         } else {

            // might be end of arbitrary scope
            LocalVariableTableEntry<LocalVariableInfo> localVariableTable = methodModel.getMethod().getLocalVariableTableEntry();
            int startPc = Short.MAX_VALUE;

            for (LocalVariableInfo localVariableInfo : localVariableTable) {

               if (localVariableInfo.getEnd() == _instruction.getThisPC()) {
                  logger.fine(localVariableInfo.getVariableName() + "  scope  " + localVariableInfo.getStart() + " ,"
                        + localVariableInfo.getEnd());
                  if (localVariableInfo.getStart() < startPc) {
                     startPc = localVariableInfo.getStart();
                  }
               }

            }

            if (startPc < Short.MAX_VALUE) {
               logger.fine("Scope block from " + startPc + " to  " + (tail.getThisPC() + tail.getLength()));
               for (Instruction i = head; i != null; i = i.getNextPC()) {
                  if (i.getThisPC() == startPc) {
                     Instruction startInstruction = i.getRootExpr().getPrevExpr();
                     logger.fine("Start = " + startInstruction);

                     addAsComposites(ByteCode.COMPOSITE_ARBITRARY_SCOPE, startInstruction.getPrevExpr(), null);
                     handled = true;
                     break;
                  }
               }

            }

         }

         if (Config.instructionListener != null) {
            Config.instructionListener.showAndTell("after folding", head, _instruction);
         }

      } catch (ClassParseException _classParseException) {
         throw new ClassParseException(_classParseException);
      } catch (Throwable t) {
         throw new ClassParseException(t);

      }
      return (handled);
   }

   private void addAsComposites(ByteCode _byteCode, Instruction _start, BranchSet _branchSet) {
      Instruction childTail = tail;
      Instruction childHead = createList(_start);
      CompositeInstruction composite = CompositeInstruction.create(_byteCode, methodModel, childHead, childTail, _branchSet);
      add(composite);
   }

   /**
    * Aids debugging.  Creates a diagrammatic form of the roots (+ tail instruction) so that we can analyze control flow. 
    * <pre>
    * I I I C C I U I U[I]I
    *       |---------->1
    *         |---->1
    *             |------>2
    *                 |-->2
    * </pre>
    * @param _cursor The instruction we are looking at
    * @param _instruction The instruction we are considering adding (may be null)
    * @return
    */
   String dumpDiagram(Instruction _instruction) {
      StringBuilder sb = new StringBuilder();
      List<Instruction> list = new ArrayList<Instruction>();

      for (Instruction i = head; i != null; i = i.getNextExpr()) {
         list.add(i);
      }

      for (Instruction i = _instruction; i != null; i = i.getNextPC()) {
         list.add(i);
      }
      Instruction[] array = list.toArray(new Instruction[0]);
      boolean lastWasCursor = false;

      List<Branch> branches = new ArrayList<Branch>();
      for (Instruction i : list) {
         sb.append(String.format(" %3d", i.getStartPC()));
      }
      sb.append("\n");
      for (Instruction i : list) {
         sb.append(String.format(" %3d", i.getThisPC()));
      }
      sb.append("\n");
      for (Instruction i : list) {

         if (i == _instruction) {
            sb.append(" [");
            lastWasCursor = true;
         } else {
            if (lastWasCursor) {
               sb.append("] ");
               lastWasCursor = false;
            } else {
               sb.append("  ");
            }
         }
         if (i.isBranch() && i.asBranch().isConditional()) {
            branches.add(i.asBranch());

            if (i.asBranch().isForward()) {
               sb.append("?>");

            } else {
               sb.append("?<");
            }
         } else if (i.isBranch() && i.asBranch().isUnconditional()) {
            branches.add(i.asBranch());
            if (i.asBranch().isForward()) {
               sb.append(">>");
            } else {
               sb.append("<<");
            }
         } else if (i instanceof CompositeInstruction) {
            sb.append(" C");
         } else if (i instanceof Return) {

            sb.append(" R");
            // } else if (i instanceof AssignToLocalVariable) {
            //    sb.append(" S");
         } else {
            sb.append("..");
         }
      }
      if (lastWasCursor) {
         sb.append("] ");
      } else {
         sb.append("  ");
      }
      for (Branch b : branches) {
         sb.append("\n   ");
         if (b.isForward()) {
            for (int i = 0; i < array.length; i++) {
               if (array[i].getStartPC() < b.getStartPC() || array[i].getThisPC() > b.getTarget().getThisPC()) {
                  sb.append("    ");
               } else {
                  if (b.isConditional()) {
                     sb.append("?-");
                  } else {
                     sb.append("+-");
                  }
                  i++;
                  while (i < array.length && array[i].getStartPC() < b.getTarget().getThisPC()) {
                     sb.append("----");
                     i++;
                  }
                  sb.append("->");
                  sb.append(b.getTarget().getThisPC());

               }
            }
         } else {
            for (int i = 0; i < array.length; i++) {
               if (array[i].getStartPC() < b.getTarget().getThisPC() || array[i].getThisPC() > b.getThisPC()) {
                  sb.append("    ");
               } else {
                  sb.append("<-");
                  i++;
                  while (i < array.length && array[i].getStartPC() < b.getThisPC()) {
                     sb.append("----");
                     i++;
                  }
                  if (b.isConditional()) {
                     sb.append("-?");
                  } else {
                     sb.append("-+");
                  }
               }
            }
         }
      }

      return (sb.toString());
   }

   Instruction getTail() {
      return (tail);
   }

   Instruction getHead() {
      return (head);
   }
}
