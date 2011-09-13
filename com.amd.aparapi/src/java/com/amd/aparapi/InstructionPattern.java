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

import com.amd.aparapi.InstructionSet.AccessArrayElement;
import com.amd.aparapi.InstructionSet.AccessInstanceField;
import com.amd.aparapi.InstructionSet.AccessLocalVariable;
import com.amd.aparapi.InstructionSet.AssignToArrayElement;
import com.amd.aparapi.InstructionSet.AssignToInstanceField;
import com.amd.aparapi.InstructionSet.AssignToLocalVariable;
import com.amd.aparapi.InstructionSet.CastOperator;
import com.amd.aparapi.InstructionSet.Constant;
import com.amd.aparapi.InstructionSet.I_IADD;
import com.amd.aparapi.InstructionSet.I_ICONST_1;
import com.amd.aparapi.InstructionSet.I_IINC;
import com.amd.aparapi.InstructionSet.I_ISUB;
import com.amd.aparapi.InstructionSet.MethodCall;

class InstructionPattern{

   @SuppressWarnings("unused") private boolean compareSubTrees(Instruction _lhs, Instruction _rhs) {
      _lhs = _lhs.getReal();
      _rhs = _rhs.getReal();
      boolean same = _lhs.sameAs(_rhs);
      if (same) {
         Instruction lhsChild = _lhs.getFirstChild();
         Instruction rhsChild = _rhs.getFirstChild();
         while (same && lhsChild != null && rhsChild != null) {
            same = same && compareSubTrees(lhsChild, rhsChild);
            if (same) {
               rhsChild = rhsChild.getNextExpr();
               lhsChild = lhsChild.getNextExpr();
            }
         }
         same = same && lhsChild == rhsChild;

      }
      return (same);
   }

   static class InstructionMatch{
      final boolean ok;

      static final InstructionMatch TRUE = new InstructionMatch(true);

      static final InstructionMatch FALSE = new InstructionMatch(false);

      InstructionMatch(boolean _ok) {
         ok = _ok;
      }

      static InstructionMatch test(boolean _condition) {
         return (_condition ? TRUE : FALSE);
      }

   }

   static abstract class InstructionMatcher{

      private String description;

      abstract InstructionMatch matches(Instruction _instruction);

      InstructionMatch matches(Instruction _instruction, InstructionMatcher _instructionMatcher) {
         if (matches(_instruction.getReal()).ok) {
            if (_instruction.getNextExpr() != null) {
               return (_instructionMatcher.matches(_instruction.getNextExpr().getReal()));
            }
         }
         return (InstructionMatch.FALSE);
      }

      InstructionMatcher(String _description) {
         description = _description;
      }

      String getDescription() {
         return (description);
      }
   }

   class AssignableInstructionMatcher extends InstructionMatcher{
      private Class<?>[] classes;

      AssignableInstructionMatcher(Class<?>... _classes) {
         super("AssignableInstructionMatcher");
         classes = _classes;
      }

      @Override InstructionMatch matches(Instruction _instruction) {

         for (Class<?> c : classes) {
            if (c.isAssignableFrom(_instruction.getClass())) {
               return (InstructionMatch.TRUE);

            }
         }
         return (InstructionMatch.TRUE);
      }
   }

   static final InstructionMatcher assignToLocalVariable = new InstructionMatcher("Assign to local variable"){

      @Override InstructionMatch matches(Instruction _instruction) {
         return (InstructionMatch.test(_instruction instanceof AssignToLocalVariable));
      }

   };

   static final InstructionMatcher constant = new InstructionMatcher("Constant "){

      @Override InstructionMatch matches(Instruction _instruction) {
         return (InstructionMatch.test(_instruction instanceof Constant<?>));
      }

   };

   static final InstructionMatcher assignToArrayElement = new InstructionMatcher("Assign to array element"){

      @Override InstructionMatch matches(Instruction _instruction) {
         return (InstructionMatch.test(_instruction instanceof AssignToArrayElement));
      }

   };

   static final InstructionMatcher methodCall = new InstructionMatcher("Method Call"){

      @Override InstructionMatch matches(Instruction _instruction) {

         return (InstructionMatch.test(_instruction instanceof MethodCall));
      }

   };

   static final InstructionMatcher longHandIncLocalVariable = new InstructionMatcher("Long hand increment of local variable"){
      /**
       * <pre>
       *                                                   
       *                  / iload<n>         
       *  istore<n> - iadd                       
       *                  \ i_const_1   
       *                  
       *                          / iload<n>         
       *  istore<n> - (?2i) - iadd                       
       *                          \ i_const_1    
       *
       * </pre>
       */
      @Override InstructionMatch matches(Instruction _instruction) {
         if (_instruction instanceof AssignToLocalVariable) {
            AssignToLocalVariable assign = (AssignToLocalVariable) _instruction;
            Instruction child = ((Instruction) assign).getFirstChild();

            if (child instanceof CastOperator) {
               child = child.getFirstChild();
            }
            if (child instanceof I_IADD) {
               I_IADD add = (I_IADD) child;
               Instruction lhs = add.getLhs();
               Instruction rhs = add.getRhs();
               if (lhs instanceof AccessLocalVariable) {
                  AccessLocalVariable access = (AccessLocalVariable) lhs;
                  if (access.getLocalVariableTableIndex() == assign.getLocalVariableTableIndex()) {
                     if (rhs instanceof I_ICONST_1) {
                        return (InstructionMatch.TRUE);
                     }
                  }
               }
            }
         }
         return (InstructionMatch.FALSE);
      }

   };

   static final InstructionMatcher longHandDecLocalVariable = new InstructionMatcher("Long hand decrement of local variable"){
      /**
       * <pre>
       *                                                   
       *                  / iload<n>         
       *  istore<n> - isub                       
       *                  \ i_const_1   
       *                  
       *                          / iload<n>         
       *  istore<n> - (?2i) - isub                       
       *                          \ i_const_1    
       *
       * </pre>
       */
      @Override InstructionMatch matches(Instruction _instruction) {
         if (_instruction instanceof AssignToLocalVariable) {
            AssignToLocalVariable assign = (AssignToLocalVariable) _instruction;
            Instruction child = ((Instruction) assign).getFirstChild();

            if (child instanceof CastOperator) {
               child = child.getFirstChild();
            }
            if (child instanceof I_ISUB) {
               I_ISUB add = (I_ISUB) child;
               Instruction lhs = add.getLhs();
               Instruction rhs = add.getRhs();
               if (lhs instanceof AccessLocalVariable) {
                  AccessLocalVariable access = (AccessLocalVariable) lhs;
                  if (access.getLocalVariableTableIndex() == assign.getLocalVariableTableIndex()) {
                     if (rhs instanceof I_ICONST_1) {
                        return (InstructionMatch.TRUE);
                     }
                  }
               }
            }
         }
         return (InstructionMatch.FALSE);
      }

   };

   static final InstructionMatcher fieldPlusOne = new InstructionMatcher("Field Plus One"){
      /**
       * <pre>                                               
       *                   / getfield<f>       
       *         i2<t> iadd                          
       *                   \ i_const_1    
       *                         
       *              / getfield<f>  
       *         iadd              
       *              \ i_const_1    
       * </pre>
       */
      @Override InstructionMatch matches(Instruction _instruction) {

         if (_instruction instanceof CastOperator) {
            CastOperator topCastOperator = (CastOperator) _instruction;
            _instruction = topCastOperator.getFirstChild().getReal();
         }

         if (_instruction instanceof I_IADD) {
            I_IADD add = (I_IADD) _instruction;
            Instruction addLhs = add.getLhs().getReal();
            Instruction addRhs = add.getRhs().getReal();
            if (addLhs instanceof AccessInstanceField) {
               if (addRhs instanceof I_ICONST_1) {
                  return (InstructionMatch.TRUE);
               }
            }
         }
         return (InstructionMatch.FALSE);
      }

   };

   static final InstructionMatcher fieldMinusOne = new InstructionMatcher("Field minus 1"){
      /**
       * <pre>                                               
       *                   / getfield<f>       
       *         i2<t> isub                          
       *                   \ i_const_1    
       *                         
       *              / getfield<f>  
       *         isub              
       *              \ i_const_1    
       * </pre>
       */
      @Override InstructionMatch matches(Instruction _instruction) {

         if (_instruction instanceof CastOperator) {
            CastOperator topCastOperator = (CastOperator) _instruction;
            _instruction = topCastOperator.getFirstChild().getReal();
         }

         if (_instruction instanceof I_ISUB) {
            I_ISUB add = (I_ISUB) _instruction;
            Instruction addLhs = add.getLhs().getReal();
            Instruction addRhs = add.getRhs().getReal();
            if (addLhs instanceof AccessInstanceField) {
               if (addRhs instanceof I_ICONST_1) {
                  return (InstructionMatch.TRUE);
               }
            }
         }
         return (InstructionMatch.FALSE);
      }

   };

   static final InstructionMatcher fieldArrayElementAccess = new InstructionMatcher("Field array element access"){
      /**
       * 
       * <pre>                                                
       *                         
       *              / getfield<f>  
       *         iaload             
       *              \ i_load    
       * </pre>
       */
      @Override InstructionMatch matches(Instruction _instruction) {

         if (_instruction instanceof AccessArrayElement) {
            AccessArrayElement accessArrayElement = (AccessArrayElement) _instruction;
            Instruction addLhs = accessArrayElement.getArrayRef().getReal();
            // Instruction addRhs = accessArrayElement.getArrayIndex().getReal();
            if (addLhs instanceof AccessInstanceField) {

               return (InstructionMatch.TRUE);

            }
         }
         return (InstructionMatch.FALSE);
      }

   };

   static final InstructionMatcher fieldArrayElementPlusOne = new InstructionMatcher("field array element plus one"){
      /**
       * <pre>                                                
       *                                         [       / getfield - aload_0 ]
       *              / [fieldArrayElementAccess][ iaload                     ]
       *         iadd                            [       \ iload              ]
       *              \ iconst_1    
       * </pre>
       */
      @Override InstructionMatch matches(Instruction _instruction) {
         if (_instruction instanceof I_IADD) {
            I_IADD accessArrayElement = (I_IADD) _instruction;
            if (accessArrayElement.getLhs() != null) {
               Instruction addLhs = accessArrayElement.getLhs().getReal();
               if (fieldArrayElementAccess.matches(addLhs).ok) {
                  Instruction addRhs = accessArrayElement.getRhs().getReal();
                  if (addRhs instanceof I_ICONST_1) {
                     return (InstructionMatch.TRUE);
                  }
               }
            }
         }
         return (InstructionMatch.FALSE);
      }

   };

   static final InstructionMatcher fieldArrayElementMinusOne = new InstructionMatcher("field array element minus one"){
      /**
       * <pre>                                                
       *                                         [       / getfield - aload_0 ]
       *              / [fieldArrayElementAccess][ iaload                     ]
       *         isub                            [       \ iload              ]
       *              \ iconst_1    
       * </pre>
       */
      @Override InstructionMatch matches(Instruction _instruction) {
         if (_instruction instanceof I_ISUB) {
            I_ISUB accessArrayElement = (I_ISUB) _instruction;
            Instruction addLhs = accessArrayElement.getLhs().getReal();
            if (fieldArrayElementAccess.matches(addLhs).ok) {
               Instruction addRhs = accessArrayElement.getRhs().getReal();
               if (addRhs instanceof I_ICONST_1) {
                  return (InstructionMatch.TRUE);
               }
            }
         }
         return (InstructionMatch.FALSE);
      }

   };

   static final InstructionMatcher longHandFieldArrayElementIncrement = new InstructionMatcher(
         "long hand field array element increment"){
      /**
       * //iastore{9:getfield{8:aload_0} ,12:iload_1 ,17:iadd{14:iaload{*9:getfield{8:aload_0} ,*12:iload_1} ,16:iconst_1}}
       * <pre>                                                
       *                         
       *                  / getfield - aload  
       *         iastore -  iload                                                    
       *                  \ [fieldArrayElementPlusOne]     
       * </pre>
       */
      @Override InstructionMatch matches(Instruction _instruction) {

         if (_instruction instanceof AssignToArrayElement) {
            AssignToArrayElement accessArrayElement = (AssignToArrayElement) _instruction;
            Instruction arrayRef = accessArrayElement.getArrayRef().getReal();
            //  Instruction arrayIndex = accessArrayElement.getArrayIndex().getReal();
            Instruction value = accessArrayElement.getValue().getReal();
            // Instruction addRhs = accessArrayElement.getArrayIndex().getReal();
            if (arrayRef instanceof AccessInstanceField) {
               if (fieldArrayElementPlusOne.matches(value).ok) {
                  return (InstructionMatch.TRUE);
               }
            }
         }
         return (InstructionMatch.FALSE);
      }

   };

   static final InstructionMatcher longHandFieldArrayElementDecrement = new InstructionMatcher(
         "long hand field array element decrement"){
      /**
       * //iastore{9:getfield{8:aload_0} ,12:iload_1 ,17:iadd{14:iaload{*9:getfield{8:aload_0} ,*12:iload_1} ,16:iconst_1}}
       * <pre>                                                
       *                         
       *                  / getfield - aload  
       *         iastore -  iload                                                    
       *                  \ [fieldArrayElementPlusOne]     
       * </pre>
       */
      @Override InstructionMatch matches(Instruction _instruction) {

         if (_instruction instanceof AssignToArrayElement) {
            AssignToArrayElement accessArrayElement = (AssignToArrayElement) _instruction;
            Instruction arrayRef = accessArrayElement.getArrayRef().getReal();
            //  Instruction arrayIndex = accessArrayElement.getArrayIndex().getReal();
            Instruction value = accessArrayElement.getValue().getReal();
            // Instruction addRhs = accessArrayElement.getArrayIndex().getReal();
            if (arrayRef instanceof AccessInstanceField) {
               if (fieldArrayElementMinusOne.matches(value).ok) {
                  return (InstructionMatch.TRUE);
               }
            }
         }
         return (InstructionMatch.FALSE);
      }

   };

   static final InstructionMatcher accessLocalVariable = new InstructionMatcher("access to local variable"){

      @Override InstructionMatch matches(Instruction _instruction) {
         return (InstructionMatch.test(_instruction instanceof AccessLocalVariable));
      }

   };

   static final InstructionMatcher inc = new InstructionMatcher("inc"){

      @Override InstructionMatch matches(Instruction _instruction) {
         return (InstructionMatch.test(_instruction instanceof I_IINC));
      }

   };

   static final InstructionMatcher cast = new InstructionMatcher("cast"){

      @Override InstructionMatch matches(Instruction _instruction) {
         return (InstructionMatch.test(_instruction instanceof CastOperator));
      }

   };

   static final InstructionMatcher accessInstanceField = new InstructionMatcher("access instance field"){

      @Override InstructionMatch matches(Instruction _instruction) {
         return (InstructionMatch.test(_instruction instanceof AccessInstanceField));
      }

   };

   static final InstructionMatcher assignToInstanceField = new InstructionMatcher("assign to instance field"){

      @Override InstructionMatch matches(Instruction _instruction) {
         return (InstructionMatch.test(_instruction instanceof AssignToInstanceField));
      }

   };

   static final InstructionMatcher iadd = new InstructionMatcher("iadd"){

      @Override InstructionMatch matches(Instruction _instruction) {
         return (InstructionMatch.test(_instruction instanceof I_IADD));
      }

   };

}
