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
package com.amd.aparapi.internal.instruction;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.amd.aparapi.internal.instruction.InstructionSet.Branch;
import com.amd.aparapi.internal.instruction.InstructionSet.ConditionalBranch;

/**
 * Deals with the issue of recognizing that a sequence of bytecode branch instructions actually represent a single if/while with a logical expression.
 * 
 * <p>
 * A logical expressions such as
 * <pre><code>
      if (i>= 0 && i%2 == 0 && i<100){}
 * </code></pre>
 * gets translated into a sequence of bytecode level branches and targets.  Which might look like the following. 
 * <pre><code>
   a: if ? e      +
   b: if ? d      |+
   c: if ? e      ||+
   d: if ? out    |v|+
   e: ...         v v|
      ...            |
 out: _instruction   v
 * </code></pre>
 * We need an algorithm for recognizing the underlying logical expression. 
 * <p>
 * Essentially, given a set of branches, get the longest sequential sequence including the input set which target each other or _target.
 *
 * Branches can legally branch to another in the valid set, or to the fall through of the last in the valid set or to _target
 *<p>
 * So an <pre>if(COND){IF_INSTRUCTIONS}else{ELSE_INSTUCTIONS}...</pre> will be  
<pre><code> 
       branch[?? branch]*, instructions*,goto,instruction*,target
</code></pre>
 * and <pre>if(COND){IF_INSTRUCTIONS}...</pre> will be :-
<code><pre>
       branch[?? branch]*,instruction*,target
</pre></code>
 *  The psuedo code code the algorithm looks like this:
<code><pre>
   int n=0;
   while (exp.length >1){
     if (exp[n].target == exp[n+1].target){          #rule 1
      replace exp[n] and exp[n+1] with a single expression representing 'exp[n] || exp[n+1]'
      n=0;
     }else if (exp[n].target == exp[n+1].next){      #rule 2
      replace exp[n] and exp[n+1] with a single expression representing '!(exp[n]) && exp[n+1]
      n=0;
     }else{                                          #rule 3
      n++;
     }
   }

   result = !exp[0];
</pre></code>
 * @author gfrost 
 */

public class BranchSet{
   /**
    * Base abstract class used to hold information used to construct node tree for logical expressions. 
    * 
    * @see SimpleLogicalExpressionNode
    * @see CompoundLogicalExpressionNode
    * 
    * @author gfrost
    *
    */
   public static abstract class LogicalExpressionNode{
      private LogicalExpressionNode next = null;

      private LogicalExpressionNode parent = null;

      public void setParent(LogicalExpressionNode _parent) {
         parent = _parent;
      }

      public abstract int getTarget();

      public abstract int getFallThrough();

      public abstract void invert();

      public LogicalExpressionNode getRoot() {
         if (parent != null) {
            return (parent);
         } else {
            return (this);
         }
      }

      public LogicalExpressionNode getNext() {
         return (next == null ? next : next.getRoot());
      }

      public void setNext(LogicalExpressionNode _next) {
         next = _next == null ? _next : _next.getRoot();
      }

      public LogicalExpressionNode getParent() {
         return (parent);
      }
   }

   /**
    * A node in the expression tree representing a simple logical expression.
    * 
    * For example <bold><code>(i&lt3)</code></bold> in the following would appear as a SimpleLogicalExpressionNode<br/>
    * <pre><code>
    * if (i<3){}
    * </code></pre>
    * 
    * @author gfrost
    *
    */
   public static class SimpleLogicalExpressionNode extends LogicalExpressionNode{
      private final ConditionalBranch branch;

      protected boolean invert = false;

      public SimpleLogicalExpressionNode(ConditionalBranch _branch) {
         branch = _branch;
      }

      @Override public int getTarget() {
         return (getBranch().getTarget().getThisPC());
      }

      @Override public void invert() {
         invert = !invert;
      }

      @Override public int getFallThrough() {
         return (getBranch().getNextPC().getThisPC());
      }

      public boolean isInvert() {
         return (invert);
      }

      public ConditionalBranch getBranch() {
         return branch;
      }
   }

   /**
    * A node in the expression tree representing a simple logical expression.
    * 
    * For example <bold><code>(i&lt3 || i&gt10)</code></bold> in the following would appear as a CompoundLogicalExpressionNode<br/>
    * <pre><code>
    * if (i<3 || i>10){}
    * </code></pre>
    * 
    * @author gfrost
    *
    */
   public static class CompoundLogicalExpressionNode extends LogicalExpressionNode{
      private final LogicalExpressionNode lhs;

      private final LogicalExpressionNode rhs;

      private boolean and;

      public CompoundLogicalExpressionNode(boolean _and, LogicalExpressionNode _lhs, LogicalExpressionNode _rhs) {
         lhs = _lhs;
         and = _and;
         rhs = _rhs;
         setNext(_rhs.getNext());
         if (and) {
            lhs.invert();
            // rhs.invert();
         }
         rhs.setParent(this);
         lhs.setParent(this);
      }

      @Override public int getTarget() {
         return (rhs.getTarget());
      }

      @Override public void invert() {
         and = !and;
         lhs.invert();
         rhs.invert();
      }

      public boolean isAnd() {
         return (and);
      }

      @Override public int getFallThrough() {
         return (rhs.getFallThrough());
      }

      public LogicalExpressionNode getLhs() {

         return lhs;
      }

      public LogicalExpressionNode getRhs() {

         return rhs;
      }
   }

   private final List<ConditionalBranch> set = new ArrayList<ConditionalBranch>();

   private final Instruction fallThrough;

   private final Instruction target;

   private final Branch last;

   private Branch first;

   private LogicalExpressionNode logicalExpressionNode = null;

   /**
    * We construct a branch set with the 'last' branch.  It is assumed that all nodes prior to <code>_branch</code> are folded.
    * 
    * This will walk backwards until it finds a non-branch or until it finds a branch that does not below to this set.
    * 
    * @param _branch
    */
   public BranchSet(Branch _branch) {
      target = _branch.getTarget();
      last = _branch;

      final Set<Branch> expandedSet = new LinkedHashSet<Branch>();
      final Instruction fallThroughRoot = last.getNextExpr();
      fallThrough = fallThroughRoot == null ? last.getNextPC() : fallThroughRoot.getStartInstruction();
      first = last;
      while ((first.getPrevExpr() != null) && first.getPrevExpr().isBranch() && first.getPrevExpr().asBranch().isConditional()) {
         final Instruction prevBranchTarget = first.getPrevExpr().asBranch().getTarget();
         final Instruction prevBranchTargetRoot = prevBranchTarget.getRootExpr();
         if ((prevBranchTarget == target) || (prevBranchTarget == fallThrough) || expandedSet.contains(prevBranchTargetRoot)) {
            expandedSet.add(first);
            first = first.getPrevExpr().asBranch();
         } else {
            break;
         }
      }
      for (Instruction i = first; i != fallThroughRoot; i = i.getNextExpr()) {
         set.add((ConditionalBranch) i.asBranch());
         ((ConditionalBranch) i.asBranch()).setBranchSet(this);
      }

      //   ConditionalBranch16 branches[] = set.toArray(new ConditionalBranch16[0]);

      LogicalExpressionNode end = null;
      for (final ConditionalBranch cb : set) {
         final SimpleLogicalExpressionNode sn = new SimpleLogicalExpressionNode(cb);
         if (logicalExpressionNode == null) {
            logicalExpressionNode = sn;
         } else {
            end.setNext(sn);
         }
         end = sn;
      }
      int count = 0;
      while (logicalExpressionNode.next != null) {
         if (++count > 20) {
            throw new IllegalStateException("Sanity check, we seem to have >20 iterations collapsing logical expression");
         }
         LogicalExpressionNode n = logicalExpressionNode;
         LogicalExpressionNode prev = null;
         int i = 0;

         while ((n != null) && (n.getNext() != null)) {
            if ((n.getTarget() == n.getNext().getTarget()) || (n.getTarget() == n.getNext().getFallThrough())) {
               LogicalExpressionNode newNode = null;
               if (n.getTarget() == n.getNext().getTarget()) {
                  // lhs(n) and rhs(n.next) are branching to the same location so we replace (lhs ?? rhs) with (lhs || rhs)
                  // System.out.println("exp["+i+"] exp["+(i+1)+"] replaced by (exp["+i+"] || exp["+(i+1)+"])");
                  newNode = new CompoundLogicalExpressionNode(false, n, n.getNext());
               } else if (n.getTarget() == n.getNext().getFallThrough()) {
                  // lhs(n) target and rhs(n.next) fallthrough are the same so we replace (lhs ?? rhs) with !(lhs && rhs)
                  // System.out.println("exp["+i+"] exp["+(i+1)+"] replaced by (!exp["+i+"] && exp["+(i+1)+"])");
                  newNode = new CompoundLogicalExpressionNode(true, n, n.getNext());
               }
               if (n == logicalExpressionNode) {
                  logicalExpressionNode = newNode;
               }
               if (prev != null) {
                  prev.setNext(newNode);
               }
               break;
            } else {
               prev = n;
               n = n.getNext();
               i++;
            }
         }
      }
   }

   public List<ConditionalBranch> getBranches() {
      return (set);
   }

   public Branch getFirst() {
      return (first);
   }

   public Branch getLast() {

      return (last);
   }

   public void unhook() {
      for (final Branch b : set) {
         b.unhook();
      }
   }

   public Instruction getTarget() {
      return (target);
   }

   public Instruction getFallThrough() {
      return (fallThrough);
   }

   public LogicalExpressionNode getLogicalExpression() {
      return (logicalExpressionNode);
   }
}
