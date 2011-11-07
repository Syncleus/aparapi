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

/**
 * We throw <code>ClassParseException</code>s (derived from <code>AparapiException</code>) if we encounter any Aparapi unfriendly 
 * constructs.  This allows us to <strong>fail fast</strong>.
 * 
 * @see com.amd.aparapi.AparapiException
 *
 * @author gfrost
 *
 */
@SuppressWarnings("serial") public class ClassParseException extends AparapiException{
   public static enum TYPE {
      NONE("none"), //
      ARRAY_RETURN("We don't support areturn instructions"), //
      PUTFIELD("We don't support putstatic instructions"), //
      INVOKESTATIC("We don't support invokestatic instructions"), //
      INVOKEINTERFACE("We don't support invokeinterface instructions"), //
      GETSTATIC("We don't support getstatic instructions"), //
      ATHROW("We don't support athrow instructions"), //
      SYNCHRONIZE("We don't support monitorenter or monitorexit instructions"), //
      NEW("We don't support new instructions"), //
      CHARARRAY("We don't support java char array accesses"), //
      ARRAYALIAS("We don't support copying refs in kernels"), //
      ACCESSTOCHARFIELD("We don't support java char type"), //
      SWITCH("We don't support lookupswitch or tableswitch instructions"), //
      METHODARRAYARG("We don't support passing arrays as method args"), //
      RECURSION("We don't support recursion"), //
      UNSUPPORTEDBYTECODE("This bytecode is not supported"), //
      OPERANDCONSUMERPRODUCERMISSMATCH("Detected an non-reducable operand consumer/producer mismatch"), //
      BADGETTERTYPEMISMATCH("Getter return type does not match field var type"), //
      BADGETTERNAMEMISMATCH("Getter name does not match fiels name"), //
      BADGETTERNAMENOTFOUND("Getter not found"), //
      BADSETTERTYPEMISMATCH("Setter arg type does not match field var type"), // 
      EXCEPTION("We don't support catch blocks"), //
      ARRAYLOCALVARIABLE("Found an array local variable which assumes that we will alias a field array"), //
      CHARLOCALVARIABLE("Found a char local variable"), //
      CONFUSINGBRANCHESPOSSIBLYCONTINUE("we don't support continue"), //
      CONFUSINGBRANCHESPOSSIBLYBREAK("we don't support break"), //
      OBJECTFIELDREFERENCE("Using java objects inside kernels is not supported"), //
      OBJECTARRAYFIELDREFERENCE("Object array elements cannot contain"), //
      OVERRIDENFIELD("Found overidden field"), //
      LOCALARRAYLENGTHACCESS("Found array length access on local array. Might be a result of using ForEach()"), //
      ACCESSEDOBJECTNONFINAL("Kernel array object member class must be final."), //
      ACCESSEDOBJECTFIELDNAMECONFLICT("Conflicting fields found in class hierarchy"), //
      ACCESSEDOBJECTONLYSUPPORTSSIMPLEPUTFIELD("We don't support putfield instructions beyond simple setters"), //
      ACCESSEDOBJECTSETTERARRAY("Passing array arguments to Intrinsics in expression form is not supported"), //
      MULTIDIMENSIONARRAYASSIGN("Can't assign to two dimension array"), //
      MULTIDIMENSIONARRAYACCESS("Can't access through a two dimensional array");
      private String description;

      TYPE(String _description) {
         description = _description;
      }

      public String getDescription() {
         return (description);
      }
   };

   Instruction instruction;

   TYPE type;

   ClassParseException(TYPE _type) {
      super(_type.getDescription());
      type = _type;
      instruction = null;
   }

   ClassParseException(Instruction _instruction, TYPE _type) {
      super("@" + _instruction.getThisPC() + " " + _instruction.getByteCode() + " " + _type.getDescription());
      type = _type;
      instruction = _instruction;
   }

   ClassParseException(TYPE _type, String _methodName) {
      super("@" + _methodName + " " + _type.getDescription());
      type = _type;
      instruction = null;
   }

   public Instruction getInstruction() {
      return (instruction);
   }

   public TYPE getType() {
      return (type);
   }

   ClassParseException(Throwable _t) {
      super(_t);
   }
}
