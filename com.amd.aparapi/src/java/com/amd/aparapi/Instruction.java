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

import java.util.LinkedList;

import com.amd.aparapi.InstructionSet.Branch;
import com.amd.aparapi.InstructionSet.ByteCode;
import com.amd.aparapi.InstructionSet.CompositeInstruction;
import com.amd.aparapi.InstructionSet.ConditionalBranch;

/**
 * Initially represents a single Java bytecode instruction.
 * 
 * Instructions for each bytecode are created when the bytecode is first scanned.  
 * 
 * Each Instruction will contain a pc (program counter) offset from the beginning of the sequence of bytecode and the length will be determined by the information gleaned from InstructionSet.BYTECODE.
 * 
 * 
 * @author gfrost
 *
 */
abstract class Instruction{

   protected MethodModel method;

   private ByteCode byteCode;

   private int length;

   protected int pc;

   abstract String getDescription();

   private Instruction nextPC = null;

   private Instruction prevPC = null;

   private Instruction nextExpr = null;

   private Instruction prevExpr = null;

   private Instruction parentExpr = null;

   private LinkedList<ConditionalBranch> forwardConditionalBranchTargets;

   private LinkedList<ConditionalBranch> reverseConditionalBranchTargets;

   private LinkedList<Branch> forwardUnconditionalBranchTargets;

   private LinkedList<Branch> reverseUnconditionalBranchTargets;

   private Instruction firstChild = null;

   private Instruction lastChild = null;

   protected void setChildren(Instruction _firstChild, Instruction _lastChild) {

      if (_firstChild == null || _lastChild == null) {
         throw new IllegalStateException("null children added");
      }
      firstChild = _firstChild;
      lastChild = _lastChild;

      for (Instruction i = firstChild; i != lastChild; i = i.getNextExpr()) {
         if (i == null) {
            throw new IllegalStateException("child list broken ");
         }
         i.setParentExpr(this);
      }
      lastChild.setParentExpr(this);
   }

   Instruction getPrevExpr() {
      return (prevExpr);

   }

   Instruction getNextExpr() {
      return (nextExpr);
   }

   void setNextPC(Instruction _nextByPC) {
      nextPC = _nextByPC;
   }

   void setPrevPC(Instruction _prevByPC) {
      prevPC = _prevByPC;
   }

   void setPrevExpr(Instruction _prevExpr) {
      prevExpr = _prevExpr;
   }

   void setNextExpr(Instruction _nextExpr) {
      nextExpr = _nextExpr;
   }

   Instruction toInstruction() {
      return (this);
   }

   final int getLength() {
      return (length);
   }

   final void setLength(int _length) {
      length = _length;
   }

   final ByteCode getByteCode() {
      return (byteCode);
   }

   int getThisPC() {
      return (pc);
   }

   int getStartPC() {
      return (getFirstChild() == null ? pc : getFirstChild().getStartPC());
   }

   protected Instruction(MethodModel _method, ByteCode _byteCode, int _pc) {
      method = _method;
      pc = _pc;
      byteCode = _byteCode;
   }

   protected Instruction(MethodModel _method, ByteCode _byteCode, ByteReader _byteReader, boolean _wide) {
      this(_method, _byteCode, _wide? _byteReader.getOffset() - 2:_byteReader.getOffset()-1);
   }

   // This works for most cases (except calls whose operand count depends upon the signature) so all call instructions therefore override this method
   int getStackConsumeCount() {
      return (byteCode.getPop().getStackAdjust());
   }

   int getStackProduceCount() {
      return (byteCode.getPush().getStackAdjust());
   }

   int getStackDelta() {
      return (getStackProduceCount() - getStackConsumeCount());
   }

   @Override public String toString() {
      return (String.format("%d %s", pc, byteCode.getName()));
   }

   boolean isBranch() {
      return (this instanceof Branch);
   }

   int compareTo(Instruction _other) {
      return (pc - _other.pc);
   }

   boolean isAfter(Instruction _other) {
      return (compareTo(_other) > 0);
   }

   boolean isAfterOrEqual(Instruction _other) {
      return (compareTo(_other) >= 0);
   }

   boolean isBefore(Instruction _other) {
      return (compareTo(_other) < 0);
   }

   boolean isBeforeOrEqual(Instruction _other) {
      return (compareTo(_other) <= 0);
   }

   Instruction getFirstChild() {
      return (firstChild);
   }

   Instruction getLastChild() {
      return (lastChild);
   }

   Instruction getStartInstruction() {
      return (getFirstChild() == null ? this : getFirstChild().getStartInstruction());

   }

   MethodModel getMethod() {
      return (method);
   }

   Instruction getNextPC() {
      return (nextPC);
   }

   Instruction getPrevPC() {
      return (prevPC);
   }

   void setParentExpr(Instruction _parentExpr) {
      parentExpr = _parentExpr;
   }

   Instruction getParentExpr() {
      return (parentExpr);
   }

   Instruction getRootExpr() {
      return (parentExpr == null ? this : parentExpr.getRootExpr());
   }

   boolean isReverseConditionalBranchTarget() {
      return (reverseConditionalBranchTargets != null && reverseConditionalBranchTargets.size() > 0);
   }

   boolean isForwardConditionalBranchTarget() {
      return (forwardConditionalBranchTargets != null && forwardConditionalBranchTargets.size() > 0);
   }

   boolean isReverseUnconditionalBranchTarget() {
      return (reverseUnconditionalBranchTargets != null && reverseUnconditionalBranchTargets.size() > 0);
   }

   boolean isForwardUnconditionalBranchTarget() {
      return (forwardUnconditionalBranchTargets != null && forwardUnconditionalBranchTargets.size() > 0);
   }

   boolean isReverseBranchTarget() {
      return (isReverseConditionalBranchTarget() || isReverseUnconditionalBranchTarget());
   }

   boolean isConditionalBranchTarget() {
      return (isReverseConditionalBranchTarget() || isForwardConditionalBranchTarget());
   }

   boolean isUnconditionalBranchTarget() {
      return (isReverseUnconditionalBranchTarget() || isForwardUnconditionalBranchTarget());
   }

   boolean isForwardBranchTarget() {
      return (isForwardConditionalBranchTarget() || isForwardUnconditionalBranchTarget());
   }

   boolean isBranchTarget() {
      return (isForwardBranchTarget() || isReverseBranchTarget());
   }

   boolean producesStack() {
      return (this instanceof CompositeInstruction || (getStackProduceCount() > 0));
   }

   Instruction getReal() {
      return (this);
   }

   Branch asBranch() {
      return ((Branch) this);
   }

   boolean consumesStack() {
      return (getStackConsumeCount() > 0);
   }

   void addBranchTarget(Branch _branch) {

      if (_branch.isReverse()) {
         if (_branch.isConditional()) {
            if (reverseConditionalBranchTargets == null) {
               reverseConditionalBranchTargets = new LinkedList<ConditionalBranch>();
            }
            reverseConditionalBranchTargets.add((ConditionalBranch) _branch);
         } else {
            if (reverseUnconditionalBranchTargets == null) {
               reverseUnconditionalBranchTargets = new LinkedList<Branch>();
            }
            reverseUnconditionalBranchTargets.add(_branch);
         }
      } else {
         if (_branch.isConditional()) {
            if (forwardConditionalBranchTargets == null) {
               forwardConditionalBranchTargets = new LinkedList<ConditionalBranch>();
            }
            forwardConditionalBranchTargets.add((ConditionalBranch) _branch);
         } else {
            if (forwardUnconditionalBranchTargets == null) {
               forwardUnconditionalBranchTargets = new LinkedList<Branch>();
            }
            forwardUnconditionalBranchTargets.add(_branch);
         }
      }

   }

   void removeBranchTarget(Branch _branch) {
      if (_branch.isReverse()) {
         if (_branch.isConditional()) {
            if (reverseConditionalBranchTargets != null) {
               reverseConditionalBranchTargets.remove(_branch);
               if (reverseConditionalBranchTargets.size() == 0) {
                  reverseConditionalBranchTargets = null;
               }
            }
         } else {
            if (reverseUnconditionalBranchTargets != null) {
               reverseUnconditionalBranchTargets.remove(_branch);
               if (reverseUnconditionalBranchTargets.size() == 0) {
                  reverseUnconditionalBranchTargets = null;
               }
            }
         }
      } else {
         if (_branch.isConditional()) {
            if (forwardConditionalBranchTargets != null) {
               forwardConditionalBranchTargets.remove(_branch);
               if (forwardConditionalBranchTargets.size() == 0) {
                  forwardConditionalBranchTargets = null;
               }
            }
         } else {
            if (forwardUnconditionalBranchTargets != null) {
               forwardUnconditionalBranchTargets.remove(_branch);
               if (forwardUnconditionalBranchTargets.size() == 0) {
                  forwardUnconditionalBranchTargets = null;
               }
            }
         }
      }
   }

   LinkedList<Branch> getForwardUnconditionalBranches() {
      return (forwardUnconditionalBranchTargets);
   }

   LinkedList<ConditionalBranch> getForwardConditionalBranches() {
      return (forwardConditionalBranchTargets);
   }

   LinkedList<Branch> getReverseUnconditionalBranches() {
      return (reverseUnconditionalBranchTargets);
   }

   LinkedList<ConditionalBranch> getReverseConditionalBranches() {
      return (reverseConditionalBranchTargets);
   }

   boolean isForwardBranch() {
      return (isBranch() && asBranch().isForward());
   }

   boolean sameAs(Instruction _other) {
      return (equals(_other));
   }

}
