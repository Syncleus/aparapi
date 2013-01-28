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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.amd.aparapi.ClassModel.ConstantPool;
import com.amd.aparapi.ClassModel.ConstantPool.Entry;
import com.amd.aparapi.ClassModel.ConstantPool.FieldEntry;
import com.amd.aparapi.ClassModel.ConstantPool.MethodEntry;
import com.amd.aparapi.ClassModel.LocalVariableInfo;
import com.amd.aparapi.ClassModel.LocalVariableTableEntry;

class InstructionSet{

   static enum LoadSpec {
      NONE, //
      F, // Float
      D, // Double
      I, // Integer
      L, // Long
      A, // Array
      O, // Object
   }

   static enum StoreSpec {
      NONE, //
      F, // Float
      D, // Double
      I, // Integer
      L, // Long
      A, // Array
      O, // Object
   }

   static enum TypeSpec {
      NONE("none", "none", 0, 0), //
      Z("Z", "boolean", 4, 1), // Note 'Z' is the java code for 'boolean' type
      C("C", "char", 2, 1), //
      F("F", "float", 4, 1), //
      D("D", "double", 8, 2), //
      B("B", "byte", 1, 1), //
      S("S", "short", 2, 1), //
      I("I", "int", 4, 1), //
      L("L", "long", 8, 1), // 
      J("J", "long", 8, 1), // Note J is the java code for 'long' type
      A("A", "array", 4, 1), //
      O("O", "object", 4, 1),
      N("N", "null", 4, 1),
      IorForS("IorForS", "int, float or String depending on constant pool entry", 4, 1),
      LorD("LorD", "long or float depending upon the constant pool entry", 8, 2),
      RA("RA", "return address", 4, 1),
      UNKNOWN("UNKNOWN", "unknown", -1, -1),
      ARGS("ARGS", "args to method call", -1, -1);

      private String longName;

      private String shortName;

      private int size;

      private int slots;

      TypeSpec(String _shortName, String _longName, int _size, int _slots) {
         shortName = _shortName;
         longName = _longName;
         size = _size;
         slots = _slots;
      }

      int getSize() {
         return (size);
      }

      int getSlots() {
         return (slots);
      }

      String getLongName() {
         return (longName);
      }

      String getShortName() {
         return (shortName);
      }

   }

   /**
    * Represents an Operator
    * 
    * @author gfrost
    *
    */

   static enum Operator {
      NONE,
      LogicalOr(true, "||"), //
      LogicalAnd(true, "&&", LogicalOr), //
      Equal(true, "=="), //
      NotEqual(true, "!=", Equal), //
      LessThan(true, "<"), //
      GreaterThanOrEqual(true, ">=", LessThan), //
      GreaterThan(true, ">"), //
      LessThanOrEqual(true, "<=", GreaterThan), //
      EqualNULL(true, "NULL=="),
      NotEqualNULL(true, "NULL!=", EqualNULL), //

      BitwiseOr(true, "|"), //
      BitwiseAnd(true, "&"), //
      BitwiseXor(true, "^"),

      LeftShift(true, "<<"), //
      ArithmeticRightShift(true, ">>>"), //
      LogicalRightShift(true, ">>"), //  was >>> but this caused issues in opencl 

      Add(true, "+"), //
      Sub(true, "-"), //

      Div(true, "/"), //
      Rem(true, "%"), //
      Mul(true, "*"), //

      Neg(false, "-"), //
      Pos(false, "+"), //

      I2FCast(false, "(float)"),
      I2LCast(false, "(long)"), //
      I2DCast(false, "(double)"), //
      L2ICast(false, "(int)"), //
      L2FCast(false, "(float)"), //
      L2DCast(false, "(double)"), //
      F2ICast(false, "(int)"), //
      F2LCast(false, "(long)"), //
      F2DCast(false, "(double)"), //
      D2ICast(false, "(int)"), //
      D2LCast(false, "(long)"), //
      D2FCast(false, "(float)"), //
      I2BCast(false, "(byte)"), //
      I2CCast(false, "(char)"), //
      I2SCast(false, "(short)");

      private String text;

      private boolean binary;

      private Operator compliment;

      Operator(boolean _binary, String _text) {

         text = _text;
         binary = _binary;
      }

      Operator(boolean _binary, String _text, Operator _c) {
         this(_binary, _text);
         compliment = _c;
         compliment.compliment = this;
      }

      Operator() {
         this(false, null);
      }

      String getText() {
         return text;
      }

      Operator getCompliment() {
         return (compliment);
      }

      String getText(boolean _invert) {
         return (_invert ? compliment.getText() : getText());
      }

      boolean isBinary() {
         return (binary);

      }

      boolean isUnary() {
         return (!this.equals(Operator.NONE) && !isBinary());

      }
   }

   static enum PushSpec {
      NONE, //
      UNKNOWN, //
      I(TypeSpec.I), //
      II(TypeSpec.I, TypeSpec.I), //
      III(TypeSpec.I, TypeSpec.I, TypeSpec.I), //
      IIII(TypeSpec.I, TypeSpec.I, TypeSpec.I, TypeSpec.I), //
      IIIII(TypeSpec.I, TypeSpec.I, TypeSpec.I, TypeSpec.I, TypeSpec.I), //
      IIIIII(TypeSpec.I, TypeSpec.I, TypeSpec.I, TypeSpec.I, TypeSpec.I, TypeSpec.I), //
      L(TypeSpec.L), //
      F(TypeSpec.F), //
      D(TypeSpec.D), //
      O(TypeSpec.O), //
      A(TypeSpec.A), //
      N(TypeSpec.N), //
      IorForS(TypeSpec.IorForS), //
      LorD(TypeSpec.LorD), //
      RA(TypeSpec.RA);

      PushSpec(TypeSpec... _types) {
         types = _types;
      }

      private TypeSpec[] types;

      int getStackAdjust() {
         return (types.length);
      }
   }

   static enum PopSpec {
      NONE, //
      UNKNOWN(TypeSpec.UNKNOWN), //
      I(TypeSpec.I), //
      II(TypeSpec.I, TypeSpec.I), //
      III(TypeSpec.I, TypeSpec.I, TypeSpec.I), //
      IIII(TypeSpec.I, TypeSpec.I, TypeSpec.I, TypeSpec.I), //
      IIIII(TypeSpec.I, TypeSpec.I, TypeSpec.I, TypeSpec.I, TypeSpec.I), //
      L(TypeSpec.L), //
      LL(TypeSpec.L, TypeSpec.L), //
      F(TypeSpec.F), //
      FF(TypeSpec.F, TypeSpec.F), //
      D(TypeSpec.D), //
      DD(TypeSpec.D, TypeSpec.D), //
      O(TypeSpec.O), //
      OO(TypeSpec.O, TypeSpec.O), //
      A(TypeSpec.A), //
      AI(TypeSpec.A, TypeSpec.I), //
      AII(TypeSpec.A, TypeSpec.I, TypeSpec.I), //
      AIF(TypeSpec.A, TypeSpec.I, TypeSpec.F), //
      AID(TypeSpec.A, TypeSpec.I, TypeSpec.D), //
      AIL(TypeSpec.A, TypeSpec.I, TypeSpec.L), //
      AIC(TypeSpec.A, TypeSpec.I, TypeSpec.C), //
      AIS(TypeSpec.A, TypeSpec.I, TypeSpec.S), //
      AIB(TypeSpec.A, TypeSpec.I, TypeSpec.B), //
      AIO(TypeSpec.A, TypeSpec.I, TypeSpec.O), //
      LI(TypeSpec.L, TypeSpec.I), //
      OUNKNOWN(TypeSpec.O, TypeSpec.UNKNOWN), //
      ARGS(TypeSpec.ARGS), //
      OARGS(TypeSpec.O, TypeSpec.ARGS), //
      ;

      PopSpec(TypeSpec... _types) {
         types = _types;
      }

      private TypeSpec[] types;

      int getStackAdjust() {
         return (types.length);
      }

   }

   static enum ImmediateSpec {
      NONE("NONE"), //
      UNKNOWN("UNKNOWN"), //
      Bconst("byte constant value", TypeSpec.B), //
      Sconst("short constant value", TypeSpec.S), //
      Bcpci("byte constant pool constant index", TypeSpec.B), //
      Scpci("short constant pool constant index", TypeSpec.S), //
      Icpci("int constant pool index", TypeSpec.I), //
      Blvti("byte local variable table index", TypeSpec.B),
      Spc("short pc", TypeSpec.S),
      Ipc("int pc", TypeSpec.I),
      Scpfi("short constant pool field index", TypeSpec.S),
      Scpmi("short constant pool method index", TypeSpec.S),
      ScpmiBB("short constant pool method index, byte count, byte (always zero)", TypeSpec.S, TypeSpec.B, TypeSpec.B),
      ScpciBdim("short constant pool class index, byte dimensions", TypeSpec.S, TypeSpec.B),
      BlvtiBconst("byte local variable table index, byte constant value", TypeSpec.B, TypeSpec.B);

      private String name;

      ImmediateSpec(String _name, TypeSpec... _types) {

         name = _name;
         types = _types;
      }

      private TypeSpec[] types;

      String getName() {
         return (name);
      }

      TypeSpec[] getTypes() {
         return (types);
      }

   }

   static enum ByteCode {
      // name, operation type, immediateOperands, pop operands, push operands
      NOP(null, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.NONE, PopSpec.NONE, PushSpec.NONE, Operator.NONE), //
      ACONST_NULL(I_ACONST_NULL.class, PushSpec.N), //
      ICONST_M1(I_ICONST_M1.class, PushSpec.I), //
      ICONST_0(I_ICONST_0.class, PushSpec.I), // 
      ICONST_1(I_ICONST_1.class, PushSpec.I), // 
      ICONST_2(I_ICONST_2.class, PushSpec.I), // 
      ICONST_3(I_ICONST_3.class, PushSpec.I), // 
      ICONST_4(I_ICONST_4.class, PushSpec.I), // 
      ICONST_5(I_ICONST_5.class, PushSpec.I), // 
      LCONST_0(I_LCONST_0.class, PushSpec.L), // 
      LCONST_1(I_LCONST_1.class, PushSpec.L), // 
      FCONST_0(I_FCONST_0.class, PushSpec.F), //
      FCONST_1(I_FCONST_1.class, PushSpec.F), //
      FCONST_2(I_FCONST_2.class, PushSpec.F), //
      DCONST_0(I_DCONST_0.class, PushSpec.D), //
      DCONST_1(I_DCONST_1.class, PushSpec.D), //
      BIPUSH(I_BIPUSH.class, ImmediateSpec.Bconst, PushSpec.I), //
      SIPUSH(I_SIPUSH.class, ImmediateSpec.Sconst, PushSpec.I), //
      LDC(I_LDC.class, ImmediateSpec.Bcpci, PushSpec.IorForS), //
      LDC_W(I_LDC_W.class, ImmediateSpec.Scpci, PushSpec.IorForS), //
      LDC2_W(I_LDC2_W.class, ImmediateSpec.Scpci, PushSpec.LorD), //
      ILOAD(I_ILOAD.class, LoadSpec.I, ImmediateSpec.Blvti, PushSpec.I), //
      LLOAD(I_LLOAD.class, LoadSpec.L, ImmediateSpec.Blvti, PushSpec.L), //
      FLOAD(I_FLOAD.class, LoadSpec.F, ImmediateSpec.Blvti, PushSpec.F), //
      DLOAD(I_DLOAD.class, LoadSpec.F, ImmediateSpec.Blvti, PushSpec.D), //
      ALOAD(I_ALOAD.class, LoadSpec.A, ImmediateSpec.Blvti, PushSpec.O), //
      ILOAD_0(I_ILOAD_0.class, LoadSpec.I, PushSpec.I), //
      ILOAD_1(I_ILOAD_1.class, LoadSpec.I, PushSpec.I), //
      ILOAD_2(I_ILOAD_2.class, LoadSpec.I, PushSpec.I), //
      ILOAD_3(I_ILOAD_3.class, LoadSpec.I, PushSpec.I), //
      LLOAD_0(I_LLOAD_0.class, LoadSpec.L, PushSpec.L), //
      LLOAD_1(I_LLOAD_1.class, LoadSpec.L, PushSpec.L), //
      LLOAD_2(I_LLOAD_2.class, LoadSpec.L, PushSpec.L), //
      LLOAD_3(I_LLOAD_3.class, LoadSpec.L, PushSpec.L), //
      FLOAD_0(I_FLOAD_0.class, LoadSpec.F, PushSpec.F), //
      FLOAD_1(I_FLOAD_1.class, LoadSpec.F, PushSpec.F), //
      FLOAD_2(I_FLOAD_2.class, LoadSpec.F, PushSpec.F), //
      FLOAD_3(I_FLOAD_3.class, LoadSpec.F, PushSpec.F), //
      DLOAD_0(I_DLOAD_0.class, LoadSpec.D, PushSpec.D), //
      DLOAD_1(I_DLOAD_1.class, LoadSpec.D, PushSpec.D), //
      DLOAD_2(I_DLOAD_2.class, LoadSpec.D, PushSpec.D), //
      DLOAD_3(I_DLOAD_3.class, LoadSpec.D, PushSpec.D), //
      ALOAD_0(I_ALOAD_0.class, LoadSpec.A, PushSpec.O), //
      ALOAD_1(I_ALOAD_1.class, LoadSpec.A, PushSpec.O), //
      ALOAD_2(I_ALOAD_2.class, LoadSpec.A, PushSpec.O), //
      ALOAD_3(I_ALOAD_3.class, LoadSpec.A, PushSpec.O), //
      IALOAD(I_IALOAD.class, PopSpec.AI, PushSpec.I), //
      LALOAD(I_LALOAD.class, PopSpec.AI, PushSpec.L), //
      FALOAD(I_FALOAD.class, PopSpec.AI, PushSpec.F), //
      DALOAD(I_DALOAD.class, PopSpec.AI, PushSpec.D), //
      AALOAD(I_AALOAD.class, PopSpec.AI, PushSpec.A), //
      BALOAD(I_BALOAD.class, PopSpec.AI, PushSpec.I), //
      CALOAD(I_CALOAD.class, PopSpec.AI, PushSpec.I), //
      SALOAD(I_SALOAD.class, PopSpec.AI, PushSpec.I), //
      ISTORE(I_ISTORE.class, StoreSpec.I, ImmediateSpec.Blvti, PopSpec.I), //
      LSTORE(I_LSTORE.class, StoreSpec.L, ImmediateSpec.Blvti, PopSpec.L), //
      FSTORE(I_FSTORE.class, StoreSpec.F, ImmediateSpec.Blvti, PopSpec.F), //
      DSTORE(I_DSTORE.class, StoreSpec.D, ImmediateSpec.Blvti, PopSpec.D), //
      ASTORE(I_ASTORE.class, StoreSpec.A, ImmediateSpec.Blvti, PopSpec.O), //
      ISTORE_0(I_ISTORE_0.class, StoreSpec.I, PopSpec.I), //
      ISTORE_1(I_ISTORE_1.class, StoreSpec.I, PopSpec.I), //
      ISTORE_2(I_ISTORE_2.class, StoreSpec.I, PopSpec.I), //
      ISTORE_3(I_ISTORE_3.class, StoreSpec.I, PopSpec.I), //
      LSTORE_0(I_LSTORE_0.class, StoreSpec.L, PopSpec.L), //
      LSTORE_1(I_LSTORE_1.class, StoreSpec.L, PopSpec.L), //
      LSTORE_2(I_LSTORE_2.class, StoreSpec.L, PopSpec.L), //
      LSTORE_3(I_LSTORE_3.class, StoreSpec.L, PopSpec.L), //
      FSTORE_0(I_FSTORE_0.class, StoreSpec.F, PopSpec.F), //
      FSTORE_1(I_FSTORE_1.class, StoreSpec.F, PopSpec.F), //
      FSTORE_2(I_FSTORE_2.class, StoreSpec.F, PopSpec.F), //
      FSTORE_3(I_FSTORE_3.class, StoreSpec.F, PopSpec.F), //
      DSTORE_0(I_DSTORE_0.class, StoreSpec.D, PopSpec.D), //
      DSTORE_1(I_DSTORE_1.class, StoreSpec.D, PopSpec.D), //
      DSTORE_2(I_DSTORE_2.class, StoreSpec.D, PopSpec.D), //
      DSTORE_3(I_DSTORE_3.class, StoreSpec.D, PopSpec.D), //
      ASTORE_0(I_ASTORE_0.class, StoreSpec.A, PopSpec.O), //
      ASTORE_1(I_ASTORE_1.class, StoreSpec.A, PopSpec.O), //
      ASTORE_2(I_ASTORE_2.class, StoreSpec.A, PopSpec.O), //
      ASTORE_3(I_ASTORE_3.class, StoreSpec.A, PopSpec.O), //
      IASTORE(I_IASTORE.class, PopSpec.AII), //
      LASTORE(I_LASTORE.class, PopSpec.AIL), //
      FASTORE(I_FASTORE.class, PopSpec.AIF), //
      DASTORE(I_DASTORE.class, PopSpec.AID), //
      AASTORE(I_AASTORE.class, PopSpec.AIO), //
      BASTORE(I_BASTORE.class, PopSpec.AIB), //
      CASTORE(I_CASTORE.class, PopSpec.AIC), //
      SASTORE(I_SASTORE.class, PopSpec.AIS), //
      POP(I_POP.class, PopSpec.I), //
      POP2(I_POP2.class, PopSpec.II), //
      DUP(I_DUP.class, PopSpec.I, PushSpec.II), //
      DUP_X1(I_DUP_X1.class, PopSpec.II, PushSpec.III), //
      DUP_X2(I_DUP_X2.class, PopSpec.III, PushSpec.IIII), //
      DUP2(I_DUP2.class, PopSpec.II, PushSpec.IIII), //
      DUP2_X1(I_DUP2_X1.class, PopSpec.III, PushSpec.IIIII), //
      DUP2_X2(I_DUP2_X2.class, PopSpec.IIII, PushSpec.IIIIII), //
      SWAP(I_SWAP.class, PopSpec.II, PushSpec.II), // ..., value2, value1 => ..., value1,
      // value2
      IADD(I_IADD.class, PopSpec.II, PushSpec.I, Operator.Add), //
      LADD(I_LADD.class, PopSpec.LL, PushSpec.L, Operator.Add), //
      FADD(I_FADD.class, PopSpec.FF, PushSpec.F, Operator.Add), //
      DADD(I_DADD.class, PopSpec.DD, PushSpec.D, Operator.Add), //
      ISUB(I_ISUB.class, PopSpec.II, PushSpec.I, Operator.Sub), //
      LSUB(I_LSUB.class, PopSpec.LL, PushSpec.L, Operator.Sub), //
      FSUB(I_FSUB.class, PopSpec.FF, PushSpec.F, Operator.Sub), //
      DSUB(I_DSUB.class, PopSpec.DD, PushSpec.D, Operator.Sub), //
      IMUL(I_IMUL.class, PopSpec.II, PushSpec.I, Operator.Mul), //
      LMUL(I_LMUL.class, PopSpec.LL, PushSpec.L, Operator.Mul), //
      FMUL(I_FMUL.class, PopSpec.FF, PushSpec.F, Operator.Mul), //
      DMUL(I_DMUL.class, PopSpec.DD, PushSpec.D, Operator.Mul), //
      IDIV(I_IDIV.class, PopSpec.II, PushSpec.I, Operator.Div), //
      LDIV(I_LDIV.class, PopSpec.LL, PushSpec.L, Operator.Div), //
      FDIV(I_FDIV.class, PopSpec.FF, PushSpec.F, Operator.Div), //
      DDIV(I_DDIV.class, PopSpec.DD, PushSpec.D, Operator.Div), //
      IREM(I_IREM.class, PopSpec.II, PushSpec.I, Operator.Rem), //
      LREM(I_LREM.class, PopSpec.LL, PushSpec.L, Operator.Rem), //
      FREM(I_FREM.class, PopSpec.FF, PushSpec.F, Operator.Rem), //
      DREM(I_DREM.class, PopSpec.DD, PushSpec.D, Operator.Rem), //
      INEG(I_INEG.class, PopSpec.I, PushSpec.I, Operator.Neg), //
      LNEG(I_LNEG.class, PopSpec.L, PushSpec.L, Operator.Neg), //
      FNEG(I_FNEG.class, PopSpec.F, PushSpec.F, Operator.Neg), //
      DNEG(I_DNEG.class, PopSpec.D, PushSpec.D, Operator.Neg), //
      ISHL(I_ISHL.class, PopSpec.II, PushSpec.I, Operator.LeftShift), //
      LSHL(I_LSHL.class, PopSpec.LI, PushSpec.L, Operator.LeftShift), //
      ISHR(I_ISHR.class, PopSpec.II, PushSpec.I, Operator.LogicalRightShift), //
      LSHR(I_LSHR.class, PopSpec.LI, PushSpec.L, Operator.LogicalRightShift), //
      IUSHR(I_IUSHR.class, PopSpec.II, PushSpec.I, Operator.ArithmeticRightShift), //
      LUSHR(I_LUSHR.class, PopSpec.LI, PushSpec.L, Operator.ArithmeticRightShift), //
      IAND(I_IAND.class, PopSpec.II, PushSpec.I, Operator.BitwiseAnd), //
      LAND(I_LAND.class, PopSpec.LL, PushSpec.L, Operator.BitwiseAnd), //
      IOR(I_IOR.class, PopSpec.II, PushSpec.I, Operator.BitwiseOr), //
      LOR(I_LOR.class, PopSpec.LL, PushSpec.L, Operator.BitwiseOr), //
      IXOR(I_IXOR.class, PopSpec.II, PushSpec.I, Operator.BitwiseXor), //
      LXOR(I_LXOR.class, PopSpec.LL, PushSpec.L, Operator.BitwiseXor), //
      IINC(I_IINC.class,LoadSpec.I,StoreSpec.I, ImmediateSpec.BlvtiBconst), //
      I2L(I_I2L.class, PopSpec.I, PushSpec.L, Operator.I2LCast), //
      I2F(I_I2F.class, PopSpec.I, PushSpec.F, Operator.I2FCast), //
      I2D(I_I2D.class, PopSpec.I, PushSpec.D, Operator.I2DCast), //
      L2I(I_L2I.class, PopSpec.L, PushSpec.I, Operator.L2ICast), //
      L2F(I_L2F.class, PopSpec.L, PushSpec.F, Operator.L2FCast), //
      L2D(I_L2D.class, PopSpec.L, PushSpec.D, Operator.L2DCast), //
      F2I(I_F2I.class, PopSpec.F, PushSpec.I, Operator.F2ICast), //
      F2L(I_F2L.class, PopSpec.F, PushSpec.L, Operator.F2LCast), //
      F2D(I_F2D.class, PopSpec.F, PushSpec.D, Operator.F2DCast), //
      D2I(I_D2I.class, PopSpec.D, PushSpec.I, Operator.D2ICast), //
      D2L(I_D2L.class, PopSpec.D, PushSpec.L, Operator.D2LCast), //
      D2F(I_D2F.class, PopSpec.D, PushSpec.F, Operator.D2FCast), //
      I2B(I_I2B.class, PopSpec.I, PushSpec.I, Operator.I2BCast), //
      I2C(I_I2C.class, PopSpec.I, PushSpec.I, Operator.I2CCast), //
      I2S(I_I2S.class, PopSpec.I, PushSpec.I, Operator.I2SCast), //
      LCMP(I_LCMP.class, PopSpec.LL, PushSpec.I, Operator.Sub), //
      FCMPL(I_FCMPL.class, PopSpec.FF, PushSpec.I, Operator.LessThan), //
      FCMPG(I_FCMPG.class, PopSpec.FF, PushSpec.I, Operator.GreaterThan), //
      DCMPL(I_DCMPL.class, PopSpec.DD, PushSpec.I, Operator.LessThan), //
      DCMPG(I_DCMPG.class, PopSpec.DD, PushSpec.I, Operator.GreaterThan), //
      IFEQ(I_IFEQ.class, ImmediateSpec.Spc, PopSpec.I, Operator.Equal), //
      IFNE(I_IFNE.class, ImmediateSpec.Spc, PopSpec.I, Operator.NotEqual), //
      IFLT(I_IFLT.class, ImmediateSpec.Spc, PopSpec.I, Operator.LessThan), //
      IFGE(I_IFGE.class, ImmediateSpec.Spc, PopSpec.I, Operator.GreaterThanOrEqual), //
      IFGT(I_IFGT.class, ImmediateSpec.Spc, PopSpec.I, Operator.GreaterThan), //
      IFLE(I_IFLE.class, ImmediateSpec.Spc, PopSpec.I, Operator.LessThanOrEqual), //
      IF_ICMPEQ(I_IF_ICMPEQ.class, ImmediateSpec.Sconst, PopSpec.II, Operator.Equal), //
      IF_ICMPNE(I_IF_ICMPNE.class, ImmediateSpec.Spc, PopSpec.II, Operator.NotEqual), //
      IF_ICMPLT(I_IF_ICMPLT.class, ImmediateSpec.Spc, PopSpec.II, Operator.LessThan), //
      IF_ICMPGE(I_IF_ICMPGE.class, ImmediateSpec.Spc, PopSpec.II, Operator.GreaterThanOrEqual), //
      IF_ICMPGT(I_IF_ICMPGT.class, ImmediateSpec.Spc, PopSpec.II, Operator.GreaterThan), //
      IF_ICMPLE(I_IF_ICMPLE.class, ImmediateSpec.Spc, PopSpec.II, Operator.LessThanOrEqual), //
      IF_ACMPEQ(I_IF_ACMPEQ.class, ImmediateSpec.Spc, PopSpec.OO, Operator.Equal), //
      IF_ACMPNE(I_IF_ACMPNE.class, ImmediateSpec.Spc, PopSpec.OO, Operator.NotEqual), //
      GOTO(I_GOTO.class, ImmediateSpec.Spc), //
      JSR(I_JSR.class, ImmediateSpec.Spc, PushSpec.RA), //
      RET(I_RET.class, ImmediateSpec.Bconst), //
      TABLESWITCH(I_TABLESWITCH.class, ImmediateSpec.UNKNOWN, PopSpec.I), //
      LOOKUPSWITCH(I_LOOKUPSWITCH.class, ImmediateSpec.UNKNOWN, PopSpec.I), //
      IRETURN(I_IRETURN.class, PopSpec.I), //
      LRETURN(I_LRETURN.class, PopSpec.L), //
      FRETURN(I_FRETURN.class, PopSpec.F), //
      DRETURN(I_DRETURN.class, PopSpec.D), //
      ARETURN(I_ARETURN.class, PopSpec.O), //
      RETURN(I_RETURN.class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.NONE, PopSpec.NONE, PushSpec.NONE, Operator.NONE), //
      GETSTATIC(I_GETSTATIC.class, ImmediateSpec.Scpfi, PushSpec.UNKNOWN), //
      PUTSTATIC(I_PUTSTATIC.class, ImmediateSpec.Scpfi, PopSpec.UNKNOWN), //
      GETFIELD(I_GETFIELD.class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.Scpfi, PopSpec.O, PushSpec.UNKNOWN, Operator.NONE), //
      PUTFIELD(I_PUTFIELD.class, ImmediateSpec.Scpfi, PopSpec.OUNKNOWN), //
      INVOKEVIRTUAL(I_INVOKEVIRTUAL.class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.Scpmi, PopSpec.OARGS, PushSpec.UNKNOWN,
            Operator.NONE), //
      INVOKESPECIAL(I_INVOKESPECIAL.class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.Scpmi, PopSpec.OARGS, PushSpec.UNKNOWN,
            Operator.NONE), //
      INVOKESTATIC(I_INVOKESTATIC.class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.Scpmi, PopSpec.ARGS, PushSpec.UNKNOWN,
            Operator.NONE), //
      INVOKEINTERFACE(I_INVOKEINTERFACE.class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.ScpmiBB, PopSpec.OARGS,
            PushSpec.UNKNOWN, Operator.NONE), //
      INVOKEDYNAMIC(I_INVOKEDYNAMIC.class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.ScpmiBB, PopSpec.OARGS, PushSpec.UNKNOWN,
            Operator.NONE), //

      NEW(I_NEW.class, ImmediateSpec.Scpci, PushSpec.O), //
      NEWARRAY(I_NEWARRAY.class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.Bconst, PopSpec.I, PushSpec.A, Operator.NONE), //
      ANEWARRAY(I_ANEWARRAY.class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.Sconst, PopSpec.I, PushSpec.A, Operator.NONE), // 189
      ARRAYLENGTH(I_ARRAYLENGTH.class, PopSpec.A, PushSpec.I), // 190
      ATHROW(I_ATHROW.class, PopSpec.O, PushSpec.O), // 191
      CHECKCAST(I_CHECKCAST.class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.Scpci, PopSpec.O, PushSpec.O, Operator.NONE), // 192
      INSTANCEOF(I_INSTANCEOF.class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.Scpci, PopSpec.O, PushSpec.I, Operator.NONE), // 193
      MONITORENTER(I_MONITORENTER.class, PopSpec.O), // 194
      MONITOREXIT(I_MONITOREXIT.class, PopSpec.O), // 195
      WIDE(I_WIDE.class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.UNKNOWN, PopSpec.UNKNOWN, PushSpec.UNKNOWN, Operator.NONE), // 196
      MULTIANEWARRAY(I_MULTIANEWARRAY.class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.ScpciBdim, PopSpec.UNKNOWN, PushSpec.A,
            Operator.NONE), // 197
      IFNULL(I_IFNULL.class, ImmediateSpec.Spc, PopSpec.O, Operator.EqualNULL), // 198
      IFNONNULL(I_IFNONNULL.class, ImmediateSpec.Spc, PopSpec.O, Operator.NotEqualNULL), // 199
      GOTO_W(I_GOTO_W.class, ImmediateSpec.Ipc), // 200
      JSR_W(I_JSR_W.class, ImmediateSpec.Ipc, PushSpec.RA), // 201
      ILLEGAL_202, // BREAKPOINT("breakpoint"),
      ILLEGAL_203, // LDC_QUICK("ldc_quick"),
      ILLEGAL_204, // LDC_W_QUICK("ldc_w_quick"),
      ILLEGAL_205, // LDC2_W_QUICK("ldc2_w_quick"),
      ILLEGAL_206, // GETFIELD_QUICK("getfield_quick"),
      ILLEGAL_207, // PUTFIELD_QUICK("putfield_quick"),
      ILLEGAL_208, // GETFIELD2_QUICK("getfield2_quick"),
      ILLEGAL_209, // PUTFIELD2_QUICK("putfield2_quick"),
      ILLEGAL_210, // GETSTATIC_QUICK("getstatic_quick"),
      ILLEGAL_211, // PUTSTATIC_QUICK("putstatic_quick"),
      ILLEGAL_212, // GETSTATIC2_QUICK("getstatic2_quick"),
      ILLEGAL_213, // PUTSTATIC2_QUICK("putstatic2_quick"),
      ILLEGAL_214, // INVOKEVIRTUAL_QUICK("invokevirtual_quick"),
      ILLEGAL_215, // INVOKENONVIRTUAL_QUICK("invokenonvirtual_quick"),
      ILLEGAL_216, // INVOKESUPER_QUICK("invokesuper_quick"),
      ILLEGAL_217, // INVOKESTATIC_QUICK("invokestatic_quick"),
      ILLEGAL_218, // INVOKEINTERFACE_QUICK("invokeinterface_quick"),
      ILLEGAL_219, // INVOKEVIRTUALOBJECT_QUICK("invokevirtualobject_quick"),
      ILLEGAL_220, // 220
      ILLEGAL_221, // NEW_QUICK("new_quick"),
      ILLEGAL_222, // ANEWARRAY_QUICK("anewarray_quick"),
      ILLEGAL_223, // MULTIANEWARRAY_QUICK("multianewarray_quick"),
      ILLEGAL_224, // CHECKCAST_QUICK("checkcast_quick"),
      ILLEGAL_225, // INSTANCEOF_QUICK("instanceof_quick"),
      ILLEGAL_226, // INVOKEVIRTUAL_QUICK_W("invokevirtual_quick_w"),
      ILLEGAL_227, // GETFIELD_QUICK_W("getfield_quick_w"),
      ILLEGAL_228, // PUTFIELD_QUICK_W("putfield_quick_w"),
      ILLEGAL_229, // 
      ILLEGAL_230, // 
      ILLEGAL_231, // 
      ILLEGAL_232, // 
      ILLEGAL_233, // 
      ILLEGAL_234, // 
      ILLEGAL_235, // 
      ILLEGAL_236, // 
      ILLEGAL_237, // 
      ILLEGAL_238, // 
      ILLEGAL_239, //
      ILLEGAL_240, // 
      ILLEGAL_241, // 
      ILLEGAL_242, // 
      ILLEGAL_243, // 
      ILLEGAL_244, // 
      ILLEGAL_245, // 
      ILLEGAL_246, //
      ILLEGAL_247, //
      ILLEGAL_248, //
      ILLEGAL_249, //
      ILLEGAL_250, //
      ILLEGAL_251, //
      ILLEGAL_252, //
      ILLEGAL_253, //
      ILLEGAL_254, // IMPDEP1("impdep1"),
      ILLEGAL_255, // IMPDEP2("impdep2"),
      NONE, //
      COMPOSITE_IF, //
      COMPOSITE_IF_ELSE, //
      COMPOSITE_FOR_SUN, //
      COMPOSITE_FOR_ECLIPSE, // 
      COMPOSITE_ARBITRARY_SCOPE, //
      COMPOSITE_WHILE, //
      CLONE, //
      INCREMENT, //
      INLINE_ASSIGN, //
      MULTI_ASSIGN, //
      FAKEGOTO, //
      FIELD_ARRAY_ELEMENT_INCREMENT, //
      FIELD_ARRAY_ELEMENT_ASSIGN, //
      HEAD, //
      COMPOSITE_EMPTY_LOOP, //
      COMPOSITE_DO_WHILE;

      private Class<?> clazz;

      private ImmediateSpec immediate;

      private PushSpec push;

      private PopSpec pop;

      private Operator operator;

      private LoadSpec loadSpec;

      private StoreSpec storeSpec;

      private ByteCode(Class<?> _class, LoadSpec _loadSpec, StoreSpec _storeSpec, ImmediateSpec _immediate, PopSpec _pop,
            PushSpec _push, Operator _operator) {
         clazz = _class;
         immediate = _immediate;
         push = _push;
         pop = _pop;
         operator = _operator;
         loadSpec = _loadSpec;
         storeSpec = _storeSpec;
      }

      private ByteCode(Class<?> _class, ImmediateSpec _immediate) {
         this(_class, LoadSpec.NONE, StoreSpec.NONE, _immediate, PopSpec.NONE, PushSpec.NONE, Operator.NONE);
      }

      private ByteCode(Class<?> _class, PushSpec _push) {
         this(_class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.NONE, PopSpec.NONE, _push, Operator.NONE);
      }

      private ByteCode(Class<?> _class, StoreSpec _store, ImmediateSpec _immediate, PopSpec _pop) {
         this(_class, LoadSpec.NONE, _store, _immediate, _pop, PushSpec.NONE, Operator.NONE);
      }

      private ByteCode(Class<?> _class, StoreSpec _store, PopSpec _pop) {
         this(_class, LoadSpec.NONE, _store, ImmediateSpec.NONE, _pop, PushSpec.NONE, Operator.NONE);
      }

      private ByteCode(Class<?> _class, ImmediateSpec _immediate, PopSpec _pop) {
         this(_class, LoadSpec.NONE, StoreSpec.NONE, _immediate, _pop, PushSpec.NONE, Operator.NONE);
      }

      private ByteCode(Class<?> _class, ImmediateSpec _immediate, PopSpec _pop, Operator _operator) {
         this(_class, LoadSpec.NONE, StoreSpec.NONE, _immediate, _pop, PushSpec.NONE, _operator);
      }

      private ByteCode(Class<?> _class, LoadSpec _load, ImmediateSpec _immediate, PushSpec _push) {
         this(_class, _load, StoreSpec.NONE, _immediate, PopSpec.NONE, _push, Operator.NONE);
      }

      private ByteCode(Class<?> _class, LoadSpec _load, PushSpec _push) {
         this(_class, _load, StoreSpec.NONE, ImmediateSpec.NONE, PopSpec.NONE, _push, Operator.NONE);
      }

      private ByteCode(Class<?> _class, ImmediateSpec _immediate, PushSpec _push) {
         this(_class, LoadSpec.NONE, StoreSpec.NONE, _immediate, PopSpec.NONE, _push, Operator.NONE);
      }

      private ByteCode(Class<?> _class, PopSpec _pop, PushSpec _push) {
         this(_class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.NONE, _pop, _push, Operator.NONE);
      }

      private ByteCode(Class<?> _class, PopSpec _pop, PushSpec _push, Operator _operator) {
         this(_class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.NONE, _pop, _push, _operator);
      }

      private ByteCode(Class<?> _class, PopSpec _pop) {
         this(_class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.NONE, _pop, PushSpec.NONE, Operator.NONE);
      }

      private ByteCode() {
         this(null, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.NONE, PopSpec.NONE, PushSpec.NONE, Operator.NONE);
      }
      
      private ByteCode(Class<?> _class, LoadSpec _load, StoreSpec _store, ImmediateSpec _immediate) {
         this(_class, _load, _store, _immediate, PopSpec.NONE, PushSpec.NONE, Operator.NONE);
      }

      int getCode() {
         return (ordinal());
      }

      String getName() {
         return (name().toLowerCase());
      }

      ImmediateSpec getImmediate() {
         return (immediate);
      }

      static ByteCode get(int _idx) {
         return (values()[_idx]);
      }

      PushSpec getPush() {
         return (push);
      }

      PopSpec getPop() {
         return (pop);
      }

      // Note I am intentionally skipping PushSpec.LorD.
      boolean usesDouble() {
         PushSpec push = getPush();
         PopSpec pop = getPop();
         if ((push == PushSpec.D) || (pop == PopSpec.D) || (pop == PopSpec.DD) || (pop == PopSpec.AID)) {
            return true;
         }
         return false;
      }

      Instruction newInstruction(MethodModel _methodModel, ByteReader byteReader, boolean _isWide) {
         Instruction newInstruction = null;
         if (clazz != null) {

            try {

               Constructor<?> constructor = clazz.getDeclaredConstructor(MethodModel.class, ByteReader.class, boolean.class);
               newInstruction = (Instruction) constructor.newInstance(_methodModel, byteReader, _isWide);
               newInstruction.setLength(byteReader.getOffset() - newInstruction.getThisPC());
            } catch (SecurityException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            } catch (NoSuchMethodException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            } catch (IllegalArgumentException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            } catch (InstantiationException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            } catch (IllegalAccessException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            } catch (InvocationTargetException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }

         }
         return (newInstruction);
      }

      static Instruction create(MethodModel _methodModel, ByteReader _byteReader) {
         ByteCode byteCode = get(_byteReader.u1());
         boolean isWide = false;
         if (byteCode.equals(ByteCode.WIDE)) {
            // handle wide 
            //System.out.println("WIDE");
            isWide = true;
            byteCode = get(_byteReader.u1());
         }
         Instruction newInstruction = byteCode.newInstruction(_methodModel, _byteReader, isWide);

         return (newInstruction);

      }

      Operator getOperator() {
         return (operator);
      }

      public LoadSpec getLoad() {
         return (loadSpec);
      }

      public StoreSpec getStore() {
         return (storeSpec);
      }
   }

   static class CompositeInstruction extends Instruction{

      protected BranchSet branchSet;

      protected CompositeInstruction(MethodModel method, ByteCode _byteCode, Instruction _firstChild, Instruction _lastChild,
            BranchSet _branchSet) {
         super(method, _byteCode, -1);
         branchSet = _branchSet;
         setChildren(_firstChild, _lastChild);
      }

      @Override String getDescription() {
         return ("COMPOSITE! " + getByteCode());
      }

      @Override int getThisPC() {
         return (getLastChild().getThisPC());
      }

      @Override int getStartPC() {
         return (getFirstChild().getStartPC());
      }

      static CompositeInstruction create(ByteCode _byteCode, MethodModel _methodModel, Instruction _firstChild,
            Instruction _lastChild, BranchSet _branchSet) {
         CompositeInstruction compositeInstruction = null;
         switch (_byteCode) {
            case COMPOSITE_IF:
               compositeInstruction = new CompositeIfInstruction(_methodModel, _firstChild, _lastChild, _branchSet);
               break;
            case COMPOSITE_IF_ELSE:
               compositeInstruction = new CompositeIfElseInstruction(_methodModel, _firstChild, _lastChild, _branchSet);
               break;
            case COMPOSITE_FOR_SUN:
               compositeInstruction = new CompositeForSunInstruction(_methodModel, _firstChild, _lastChild, _branchSet);
               break;
            case COMPOSITE_WHILE:
               compositeInstruction = new CompositeWhileInstruction(_methodModel, _firstChild, _lastChild, _branchSet);
               break;
            case COMPOSITE_FOR_ECLIPSE:
               compositeInstruction = new CompositeForEclipseInstruction(_methodModel, _firstChild, _lastChild, _branchSet);
               break;
            case COMPOSITE_ARBITRARY_SCOPE:
               compositeInstruction = new CompositeArbitraryScopeInstruction(_methodModel, _firstChild, _lastChild, _branchSet);
               break;
            case COMPOSITE_EMPTY_LOOP:
               compositeInstruction = new CompositeEmptyLoopInstruction(_methodModel, _firstChild, _lastChild, _branchSet);
               break;
            case COMPOSITE_DO_WHILE:
               compositeInstruction = new CompositeDoWhileInstruction(_methodModel, _firstChild, _lastChild, _branchSet);
               break;
         }
         return (compositeInstruction);

      }

      BranchSet getBranchSet() {
         return (branchSet);
      }
   }

   static class CompositeIfInstruction extends CompositeInstruction{

      protected CompositeIfInstruction(MethodModel method, Instruction _firstChild, Instruction _lastChild, BranchSet _branchSet) {
         super(method, ByteCode.COMPOSITE_IF, _firstChild, _lastChild, _branchSet);

      }
   }

   static class CompositeIfElseInstruction extends CompositeInstruction{

      protected CompositeIfElseInstruction(MethodModel method, Instruction _firstChild, Instruction _lastChild, BranchSet _branchSet) {
         super(method, ByteCode.COMPOSITE_IF_ELSE, _firstChild, _lastChild, _branchSet);

      }

   }

   static class CompositeForSunInstruction extends CompositeInstruction{

      protected CompositeForSunInstruction(MethodModel method, Instruction _firstChild, Instruction _lastChild, BranchSet _branchSet) {
         super(method, ByteCode.COMPOSITE_FOR_SUN, _firstChild, _lastChild, _branchSet);

      }
   }

   static class CompositeWhileInstruction extends CompositeInstruction{

      protected CompositeWhileInstruction(MethodModel method, Instruction _firstChild, Instruction _lastChild, BranchSet _branchSet) {
         super(method, ByteCode.COMPOSITE_WHILE, _firstChild, _lastChild, _branchSet);

      }
   }

   static class CompositeEmptyLoopInstruction extends CompositeInstruction{

      protected CompositeEmptyLoopInstruction(MethodModel method, Instruction _firstChild, Instruction _lastChild,
            BranchSet _branchSet) {
         super(method, ByteCode.COMPOSITE_EMPTY_LOOP, _firstChild, _lastChild, _branchSet);

      }
   }

   static class CompositeDoWhileInstruction extends CompositeInstruction{

      protected CompositeDoWhileInstruction(MethodModel method, Instruction _firstChild, Instruction _lastChild,
            BranchSet _branchSet) {
         super(method, ByteCode.COMPOSITE_DO_WHILE, _firstChild, _lastChild, _branchSet);

      }
   }

   static class CompositeForEclipseInstruction extends CompositeInstruction{

      protected CompositeForEclipseInstruction(MethodModel method, Instruction _firstChild, Instruction _lastChild,
            BranchSet _branchSet) {
         super(method, ByteCode.COMPOSITE_FOR_ECLIPSE, _firstChild, _lastChild, _branchSet);

      }
   }

   static class CompositeArbitraryScopeInstruction extends CompositeInstruction{

      protected CompositeArbitraryScopeInstruction(MethodModel method, Instruction _firstChild, Instruction _lastChild,
            BranchSet _branchSet) {
         super(method, ByteCode.COMPOSITE_ARBITRARY_SCOPE, _firstChild, _lastChild, _branchSet);

      }
   }

   static abstract class OperatorInstruction extends Instruction{

      protected OperatorInstruction(MethodModel _methodPoolEntry, ByteCode code, ByteReader reader, boolean _wide) {
         super(_methodPoolEntry, code, reader, _wide);

      }

      public Operator getOperator() {
         return (getByteCode().getOperator());
      }

   }

   static abstract class BinaryOperator extends OperatorInstruction implements Binary{

      @Override public final Instruction getLhs() {
         return (getFirstChild());
      }

      @Override public final Instruction getRhs() {
         return (getLastChild());
      }

      protected BinaryOperator(MethodModel _methodPoolEntry, ByteCode code, ByteReader reader, boolean _wide) {
         super(_methodPoolEntry, code, reader, _wide);
      }

   }

   static abstract class UnaryOperator extends OperatorInstruction implements Unary{

      @Override public final Instruction getUnary() {
         return (getFirstChild());
      }

      protected UnaryOperator(MethodModel _methodPoolEntry, ByteCode code, ByteReader reader, boolean _wide) {
         super(_methodPoolEntry, code, reader, _wide);
      }

   }

   static abstract class CastOperator extends UnaryOperator{

      protected CastOperator(MethodModel _methodPoolEntry, ByteCode code, ByteReader reader, boolean _wide) {
         super(_methodPoolEntry, code, reader, _wide);
      }

   }

   static abstract class Branch extends Instruction{
      protected int offset;

      protected boolean breakOrContinue;

      protected Instruction target;

      int getAbsolute() {
         return (getThisPC() + getOffset());
      }

      private int getOffset() {
         return (offset);
      }

      Branch(MethodModel _methodPoolEntry, ByteCode _byteCode, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, _byteCode, _byteReader, _wide);

      }

      Branch(MethodModel _methodPoolEntry, ByteCode _byteCode, Instruction _target) {
         super(_methodPoolEntry, _byteCode, -1);
         setTarget(_target);

      }

      Instruction getTarget() {
         return (target);
      }

      void setTarget(Instruction _target) {
         target = _target;
         offset = target.getThisPC() - getThisPC();
         target.addBranchTarget(this);
      }

      boolean isConditional() {
         return (this instanceof ConditionalBranch);
      }

      boolean isUnconditional() {
         return (this instanceof UnconditionalBranch);
      }

      boolean isReverseConditional() {
         return (isConditional() && isReverse());
      }

      boolean isForwardConditional() {
         return (isConditional() && isForward());
      }

      boolean isReverseUnconditional() {
         return (isUnconditional() && isReverse());
      }

      boolean isForwardUnconditional() {
         return (isUnconditional() && isForward());
      }

      boolean isReverse() {
         return (offset < 0);
      }

      boolean isForward() {
         return (offset >= 0);
      }

      void unhook() {
         getTarget().removeBranchTarget(this);

      }

      void setBreakOrContinue(boolean b) {
         breakOrContinue = true;

      }

      boolean isBreakOrContinue() {
         return (breakOrContinue);

      }

      public void retarget(Instruction _newTarget) {
         //System.out.println("retargetting " + pc + " -> " + target.getThisPC() + " to " + _newTarget.getThisPC());
         unhook(); // removes this from the list of branchers to target
         setTarget(_newTarget);
         //System.out.println("retargetted " + pc + " -> " + target.getThisPC());
         //  _newTarget.addBranchTarget(this);
      }

   }

   static abstract class ConditionalBranch extends Branch{
      private BranchSet branchSet;

      ConditionalBranch(MethodModel _methodPoolEntry, ByteCode _byteCode, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, _byteCode, _byteReader, _wide);
      }

      void setBranchSet(BranchSet _branchSet) {
         branchSet = _branchSet;
      }

      BranchSet getOrCreateBranchSet() {
         if (branchSet == null) {
            branchSet = new BranchSet(this);
         }
         return branchSet;
      }

      BranchSet getBranchSet() {
         return branchSet;
      }

      // extent is a guess but we know that the target will be beyond extent, we are not interested in targets that fall before extent
      ConditionalBranch findEndOfConditionalBranchSet(Instruction _extent) {
         // bummer ;)
         // we need to find the actual branch set.  Be careful here we can only create a branch set when we *know* that a conditional is the last in the set. 
         // we don't know that here.  We have to scan forward to try to find it 
         ConditionalBranch i = this;
         Instruction theTarget = null;
         ConditionalBranch lastToTarget = null;

         if (getTarget().isAfter(_extent)) {
            // if this conditional is already pointing beyond extent then we know the target
            theTarget = getTarget();
            lastToTarget = this;
         }
         while (i.getNextExpr().isBranch() && i.getNextExpr().asBranch().isForwardConditional()) {
            Branch nextBranch = i.getNextExpr().asBranch();
            if (theTarget == null && nextBranch.getTarget().isAfter(_extent)) {
               theTarget = nextBranch.getTarget();
               lastToTarget = this;
            } else if (nextBranch.getTarget() == theTarget) {
               lastToTarget = this;
            }
            i = (ConditionalBranch) i.getNextExpr();

         }
         if (theTarget == null) {
            throw new IllegalStateException("unable to find end of while extent");
         }
         return (lastToTarget);
      }
   }

   static abstract class UnconditionalBranch extends Branch{

      UnconditionalBranch(MethodModel _methodPoolEntry, ByteCode _byteCode, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, _byteCode, _byteReader, _wide);
      }

      UnconditionalBranch(MethodModel _methodPoolEntry, ByteCode _byteCode, Instruction _target) {
         super(_methodPoolEntry, _byteCode, _target);
      }

   }

   static abstract class IfUnary extends ConditionalBranch16 implements Unary{

      IfUnary(MethodModel _methodPoolEntry, ByteCode _byteCode, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, _byteCode, _byteReader, _wide);

      }

      @Override public Instruction getUnary() {
         return (getFirstChild());
      }

   }

   static abstract class If extends ConditionalBranch16 implements Binary{

      If(MethodModel _methodPoolEntry, ByteCode _byteCode, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, _byteCode, _byteReader, _wide);

      }

      @Override public final Instruction getLhs() {
         return (getFirstChild());
      }

      @Override public final Instruction getRhs() {
         return (getLastChild());
      }

   }

   static abstract class ConditionalBranch16 extends ConditionalBranch implements HasOperator{

      ConditionalBranch16(MethodModel _methodPoolEntry, ByteCode _byteCode, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, _byteCode, _byteReader, _wide);
         offset = _byteReader.s2();

      }

      @Override public Operator getOperator() {
         return (getByteCode().getOperator());
      }
   }

   static abstract class UnconditionalBranch16 extends UnconditionalBranch{

      UnconditionalBranch16(MethodModel _methodPoolEntry, ByteCode _byteCode, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, _byteCode, _byteReader, _wide);
         offset = _byteReader.s2();

      }

   }

   static abstract class Branch32 extends UnconditionalBranch{
      Branch32(MethodModel _methodPoolEntry, ByteCode _byteCode, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, _byteCode, _byteReader, _wide);
         offset = _byteReader.s4();

      }

   }

   static abstract class ArrayAccess extends Instruction{

      protected ArrayAccess(MethodModel _methodPoolEntry, ByteCode _byteCode, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, _byteCode, _byteReader, _wide);

      }

      Instruction getArrayRef() {
         return (getFirstChild());
      }

      Instruction getArrayIndex() {
         return (getFirstChild().getNextExpr());
      }

   }

   static abstract class AccessArrayElement extends ArrayAccess{
      protected AccessArrayElement(MethodModel _methodPoolEntry, ByteCode _byteCode, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, _byteCode, _byteReader, _wide);
      }
   }

   static class I_AALOAD extends AccessArrayElement{
      I_AALOAD(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.AALOAD, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("push reference from arrayref and index");
      }

   }

   static abstract class AssignToArrayElement extends ArrayAccess{

      public Instruction getValue() {
         return (getFirstChild().getNextExpr().getNextExpr());
      }

      protected AssignToArrayElement(MethodModel _methodPoolEntry, ByteCode _byteCode, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, _byteCode, _byteReader, _wide);

      }

   }

   static class I_AASTORE extends AssignToArrayElement{
      I_AASTORE(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.AASTORE, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop reference into arrayref[index]");
      }

   }

   static class I_ACONST_NULL extends Instruction implements Constant<Object>{
      I_ACONST_NULL(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ACONST_NULL, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("push null");
      }

      @Override public Object getValue() {
         return null;
      }

   }

   static abstract class LocalVariableConstIndexAccessor extends IndexConst implements AccessLocalVariable{
      LocalVariableConstIndexAccessor(MethodModel methodPoolEntry, ByteCode byteCode, ByteReader byteReader, boolean _wide,
            int index) {
         super(methodPoolEntry, byteCode, byteReader, _wide, index);
      }

      @Override public final int getLocalVariableTableIndex() {
         return (index);
      }

      @Override public final LocalVariableInfo getLocalVariableInfo() {
         return (method.getLocalVariableTableEntry().getVariable(getThisPC() + getLength(), getLocalVariableTableIndex()));
      }
   }

   static abstract class LocalVariableConstIndexLoad extends LocalVariableConstIndexAccessor{
      LocalVariableConstIndexLoad(MethodModel methodPoolEntry, ByteCode byteCode, ByteReader byteReader, boolean _wide, int index) {
         super(methodPoolEntry, byteCode, byteReader, _wide, index);
      }

      @Override final String getDescription() {
         return ("push reference from local var index " + index);
      }

   }

   static abstract class LocalVariableConstIndexStore extends LocalVariableConstIndexAccessor implements AssignToLocalVariable{
      LocalVariableConstIndexStore(MethodModel methodPoolEntry, ByteCode byteCode, ByteReader byteReader, boolean _wide, int index) {
         super(methodPoolEntry, byteCode, byteReader, _wide, index);
      }

      @Override public boolean isDeclaration() {
         LocalVariableInfo lvi = method.getLocalVariableTableEntry().getVariable(getThisPC() + getLength(),
               getLocalVariableTableIndex());
         return (lvi.getStart() == getThisPC() + getLength());
      }

      @Override final String getDescription() {
         return ("pop reference into local var index " + index);
      }
   }

   static abstract class LocalVariableIndex08Accessor extends Index08 implements AccessLocalVariable{
      LocalVariableIndex08Accessor(MethodModel methodPoolEntry, ByteCode byteCode, ByteReader byteReader, boolean _wide) {
         super(methodPoolEntry, byteCode, byteReader, _wide);
      }

      @Override public final int getLocalVariableTableIndex() {
         return (index);
      }

      @Override public final LocalVariableInfo getLocalVariableInfo() {

         return (method.getLocalVariableTableEntry().getVariable(getThisPC() + getLength(), getLocalVariableTableIndex()));
      }
   }

   static abstract class LocalVariableIndex08Load extends LocalVariableIndex08Accessor{
      LocalVariableIndex08Load(MethodModel methodPoolEntry, ByteCode byteCode, ByteReader byteReader, boolean _wide) {
         super(methodPoolEntry, byteCode, byteReader, _wide);
      }

      @Override final String getDescription() {
         return ("push reference from local var index " + index);
      }

   }

   static abstract class LocalVariableIndex08Store extends LocalVariableIndex08Accessor implements AssignToLocalVariable{
      LocalVariableIndex08Store(MethodModel methodPoolEntry, ByteCode byteCode, ByteReader byteReader, boolean _wide) {
         super(methodPoolEntry, byteCode, byteReader, _wide);
      }

      @Override public boolean isDeclaration() {
         LocalVariableTableEntry localVariableTableEntry = method.getLocalVariableTableEntry();
         LocalVariableInfo localVarInfo = localVariableTableEntry.getVariable(getThisPC() + getLength(),
               getLocalVariableTableIndex());
         return (localVarInfo != null && localVarInfo.getStart() == getThisPC() + getLength());
      }

      @Override final String getDescription() {
         return ("pop reference into local var index " + index);
      }

   }

   static class I_ALOAD extends LocalVariableIndex08Load{
      I_ALOAD(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ALOAD, _byteReader, _wide);

      }

   }

   static class I_ALOAD_0 extends LocalVariableConstIndexLoad{
      I_ALOAD_0(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ALOAD_0, _byteReader, _wide, 0);
      }
   }

   static class I_ALOAD_1 extends LocalVariableConstIndexLoad{
      I_ALOAD_1(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ALOAD_1, _byteReader, _wide, 1);

      }

   }

   static class I_ALOAD_2 extends LocalVariableConstIndexLoad{
      I_ALOAD_2(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ALOAD_2, _byteReader, _wide, 2);
      }
   }

   static class I_ALOAD_3 extends LocalVariableConstIndexLoad{
      I_ALOAD_3(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ALOAD_3, _byteReader, _wide, 3);
      }
   }

   static class I_ANEWARRAY extends Index16 implements New{

      I_ANEWARRAY(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ANEWARRAY, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("new array of reference");
      }

   }

   static class I_ARETURN extends Return{
      I_ARETURN(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ARETURN, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("return popped reference");
      }

   }

   static class I_ARRAYLENGTH extends Instruction{
      I_ARRAYLENGTH(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ARRAYLENGTH, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop array push length");
      }
   }

   static class I_ASTORE extends LocalVariableIndex08Store{
      I_ASTORE(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ASTORE, _byteReader, _wide);

      }

   }

   static class I_ASTORE_0 extends LocalVariableConstIndexStore{
      I_ASTORE_0(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ASTORE_0, _byteReader, _wide, 0);

      }

   }

   static class I_ASTORE_1 extends LocalVariableConstIndexStore{
      I_ASTORE_1(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ASTORE_1, _byteReader, _wide, 1);

      }

   }

   static class I_ASTORE_2 extends LocalVariableConstIndexStore{
      I_ASTORE_2(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ASTORE_2, _byteReader, _wide, 2);

      }

   }

   static class I_ASTORE_3 extends LocalVariableConstIndexStore{
      I_ASTORE_3(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ASTORE_3, _byteReader, _wide, 3);

      }

   }

   static class I_ATHROW extends Instruction{
      I_ATHROW(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ATHROW, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop reference and throw");
      }

   }

   static class I_BALOAD extends AccessArrayElement{
      I_BALOAD(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.BALOAD, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("push byte/boolean from arrayref and index");
      }

   }

   static class I_BASTORE extends AssignToArrayElement{
      I_BASTORE(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.BASTORE, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop boolean/byte into arrayref[index]");
      }

   }

   static class I_BIPUSH extends ImmediateConstant<Integer>{

      I_BIPUSH(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.BIPUSH, _byteReader, _wide);
         value = _byteReader.u1();

      }

      @Override String getDescription() {
         return ("push (byte)");
      }

      @Override public Integer getValue() {
         int byteValue = super.getValue();
         if (byteValue > 127) {
            byteValue = -(256 - byteValue);
         }
         return (byteValue);
      }
   }

   static class I_CALOAD extends AccessArrayElement{
      I_CALOAD(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.CALOAD, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("push char from arrayref and index");
      }

   }

   static class I_CASTORE extends AssignToArrayElement{
      I_CASTORE(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.CASTORE, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop char into arrayref[index]");
      }
   }

   static class I_CHECKCAST extends Index16{
      I_CHECKCAST(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.CHECKCAST, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("peek reference check against the constant accessed 16 bit");
      }

   }

   static class I_D2F extends CastOperator{
      I_D2F(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.D2F, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop double push float");
      }

   }

   static class I_D2I extends CastOperator{
      I_D2I(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.D2I, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop double push int");
      }

   }

   static class I_D2L extends CastOperator{
      I_D2L(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.D2L, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop double push long");
      }

   }

   static class I_DADD extends BinaryOperator{
      I_DADD(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DADD, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("add top two doubles");
      }

   }

   static class I_DALOAD extends AccessArrayElement{
      I_DALOAD(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DALOAD, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("push double from arrayref and index");
      }

   }

   static class I_DASTORE extends AssignToArrayElement{
      I_DASTORE(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DASTORE, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop double into arrayref[index]");
      }

   }

   static class I_DCMPG extends Instruction{
      I_DCMPG(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DCMPG, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("push result of double comparison");
      }

   }

   static class I_DCMPL extends Instruction{
      I_DCMPL(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DCMPL, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("push result of double comparison");
      }

   }

   static abstract class BytecodeEncodedConstant<T> extends Instruction implements Constant<T>{
      private T value;

      BytecodeEncodedConstant(MethodModel _methodPoolEntry, ByteCode _byteCode, ByteReader _byteReader, boolean _wide, T _value) {
         super(_methodPoolEntry, _byteCode, _byteReader, _wide);
         value = _value;

      }

      @Override public T getValue() {
         return (value);
      }

   }

   static abstract class ImmediateConstant<T> extends Instruction implements Constant<T>{
      protected T value;

      ImmediateConstant(MethodModel _methodPoolEntry, ByteCode _byteCode, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, _byteCode, _byteReader, _wide);

      }

      @Override public T getValue() {
         return (value);
      }

   }

   static class I_DCONST_0 extends BytecodeEncodedConstant<Double>{
      I_DCONST_0(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DCONST_0, _byteReader, _wide, 0.0);

      }

      @Override String getDescription() {
         return ("push (double) 0.0");
      }

   }

   static class I_DCONST_1 extends BytecodeEncodedConstant<Double>{
      I_DCONST_1(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DCONST_1, _byteReader, _wide, 1.0);

      }

      @Override String getDescription() {
         return ("push (double) 1.0");
      }

   }

   static class I_DDIV extends BinaryOperator{
      I_DDIV(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DDIV, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("div top two doubles");
      }

   }

   static class I_DLOAD extends LocalVariableIndex08Load{
      I_DLOAD(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DLOAD, _byteReader, _wide);

      }

   }

   static class I_DLOAD_0 extends LocalVariableConstIndexLoad{
      I_DLOAD_0(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DLOAD_0, _byteReader, _wide, 0);

      }

   }

   static class I_DLOAD_1 extends LocalVariableConstIndexLoad{
      I_DLOAD_1(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DLOAD_1, _byteReader, _wide, 1);

      }

   }

   static class I_DLOAD_2 extends LocalVariableConstIndexLoad{
      I_DLOAD_2(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DLOAD_2, _byteReader, _wide, 2);

      }

   }

   static class I_DLOAD_3 extends LocalVariableConstIndexLoad{
      I_DLOAD_3(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DLOAD_3, _byteReader, _wide, 3);

      }

   }

   static class I_DMUL extends BinaryOperator{
      I_DMUL(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DMUL, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("mul top two doubles");
      }

   }

   static class I_DNEG extends UnaryOperator{
      I_DNEG(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DNEG, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("neg top double");
      }

   }

   static class I_DREM extends BinaryOperator{
      I_DREM(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DREM, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("rem top two doubles");
      }

   }

   static class I_DRETURN extends Return{
      I_DRETURN(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DRETURN, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("return popped double");
      }

   }

   static class I_DSTORE extends LocalVariableIndex08Store{
      I_DSTORE(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DSTORE, _byteReader, _wide);

      }

   }

   static class I_DSTORE_0 extends LocalVariableConstIndexStore{
      I_DSTORE_0(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DSTORE_0, _byteReader, _wide, 0);

      }

   }

   static class I_DSTORE_1 extends LocalVariableConstIndexStore{
      I_DSTORE_1(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DSTORE_1, _byteReader, _wide, 1);

      }

   }

   static class I_DSTORE_2 extends LocalVariableConstIndexStore{
      I_DSTORE_2(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DSTORE_2, _byteReader, _wide, 2);

      }

   }

   static class I_DSTORE_3 extends LocalVariableConstIndexStore{
      I_DSTORE_3(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DSTORE_3, _byteReader, _wide, 3);

      }

   }

   static class I_DSUB extends BinaryOperator{
      I_DSUB(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DSUB, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("sub top two doubles");
      }

   }

   static abstract class DUP extends Instruction{
      DUP(MethodModel _methodPoolEntry, ByteCode _byteCode, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, _byteCode, _byteReader, _wide);

      }
   }

   static class I_DUP extends DUP{
      I_DUP(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DUP, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("dup top item");
      }

   }

   static class I_DUP_X1 extends DUP{
      I_DUP_X1(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DUP_X1, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("dup top item 2 items down");
      }

   }

   static class I_DUP_X2 extends DUP{
      I_DUP_X2(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DUP_X2, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("dup top item 3 items down");
      }

   }

   static class I_DUP2 extends DUP{
      I_DUP2(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DUP2, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("dup top 2 items");
      }

   }

   static class I_DUP2_X1 extends DUP{
      I_DUP2_X1(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DUP2_X1, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("dup top 2 items 2 items down");
      }

   }

   static class I_DUP2_X2 extends DUP{
      I_DUP2_X2(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.DUP_X2, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("dup top 2 items 3 items down");
      }

   }

   static class I_F2D extends CastOperator{
      I_F2D(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.F2D, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop float push double");
      }

   }

   static class I_F2I extends CastOperator{
      I_F2I(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.F2I, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop float push int");
      }

   }

   static class I_F2L extends CastOperator{
      I_F2L(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.F2L, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop float push long");
      }

   }

   static class I_FADD extends BinaryOperator{
      I_FADD(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.FADD, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("add top two floats");
      }

   }

   static class I_FALOAD extends AccessArrayElement{
      I_FALOAD(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.FALOAD, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("push float from arrayref and index");
      }

   }

   static class I_FASTORE extends AssignToArrayElement{
      I_FASTORE(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.FASTORE, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop float into arrayref[index]");
      }

   }

   static class I_FCMPG extends BinaryOperator{
      I_FCMPG(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.FCMPG, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("push result of float comparison");
      }

   }

   static class I_FCMPL extends BinaryOperator{
      I_FCMPL(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.FCMPL, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("push result of float comparison");
      }

   }

   static class I_FCONST_0 extends BytecodeEncodedConstant<Float>{
      I_FCONST_0(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.FCONST_0, _byteReader, _wide, 0f);

      }

      @Override String getDescription() {
         return ("push (float) 0.0");
      }

   }

   static class I_FCONST_1 extends BytecodeEncodedConstant<Float>{
      I_FCONST_1(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.FCONST_1, _byteReader, _wide, 1f);

      }

      @Override String getDescription() {
         return ("push (float) 1.0");
      }

   }

   static class I_FCONST_2 extends BytecodeEncodedConstant<Float>{
      I_FCONST_2(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.FCONST_2, _byteReader, _wide, 2f);

      }

      @Override String getDescription() {
         return ("push (float) 2.0");
      }

   }

   static class I_FDIV extends BinaryOperator{
      I_FDIV(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.FDIV, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("div top two floats");
      }

   }

   static class I_FLOAD extends LocalVariableIndex08Load{
      I_FLOAD(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.FLOAD, _byteReader, _wide);

      }

   }

   static class I_FLOAD_0 extends LocalVariableConstIndexLoad{
      I_FLOAD_0(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.FLOAD_0, _byteReader, _wide, 0);

      }

   }

   static class I_FLOAD_1 extends LocalVariableConstIndexLoad{
      I_FLOAD_1(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.FLOAD_1, _byteReader, _wide, 1);

      }

   }

   static class I_FLOAD_2 extends LocalVariableConstIndexLoad{
      I_FLOAD_2(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.FLOAD_2, _byteReader, _wide, 2);

      }

   }

   static class I_FLOAD_3 extends LocalVariableConstIndexLoad{
      I_FLOAD_3(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.FLOAD_3, _byteReader, _wide, 3);

      }

   }

   static class I_FMUL extends BinaryOperator{
      I_FMUL(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.FMUL, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("mul top two floats");
      }

   }

   static class I_FNEG extends UnaryOperator{
      I_FNEG(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.FNEG, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("neg top float");
      }

   }

   static class I_FREM extends BinaryOperator{
      I_FREM(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.FREM, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("rem top two floats");
      }

   }

   static class I_FRETURN extends Return{
      I_FRETURN(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.FRETURN, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("return popped float");
      }

   }

   static class I_FSTORE extends LocalVariableIndex08Store{
      I_FSTORE(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.FSTORE, _byteReader, _wide);

      }

   }

   static class I_FSTORE_0 extends LocalVariableConstIndexStore{
      I_FSTORE_0(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.FSTORE_0, _byteReader, _wide, 0);

      }

   }

   static class I_FSTORE_1 extends LocalVariableConstIndexStore{
      I_FSTORE_1(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.FSTORE_1, _byteReader, _wide, 1);

      }

   }

   static class I_FSTORE_2 extends LocalVariableConstIndexStore{
      I_FSTORE_2(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.FSTORE_2, _byteReader, _wide, 2);

      }

   }

   static class I_FSTORE_3 extends LocalVariableConstIndexStore{
      I_FSTORE_3(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.FSTORE_3, _byteReader, _wide, 3);

      }

   }

   static class I_FSUB extends BinaryOperator{
      I_FSUB(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.FSUB, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("sub top two floats");
      }

   }

   static class I_GETFIELD extends Index16 implements AccessInstanceField{
      I_GETFIELD(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.GETFIELD, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("push value from field referenced by 16 bit constant index");
      }

      @Override public int getConstantPoolFieldIndex() {
         return (index);
      }

      @Override public FieldEntry getConstantPoolFieldEntry() {
         return (method.getConstantPool().getFieldEntry(getConstantPoolFieldIndex()));
      }

      @Override public Instruction getInstance() {
         return (getFirstChild());
      }

      @Override int getStackConsumeCount() {
         return (1);
      }

      @Override int getStackProduceCount() {
         return (1);
      }

   }

   static class I_GETSTATIC extends Index16 implements AccessField{
      I_GETSTATIC(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.GETSTATIC, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("push  static field value at 16 bit constant index");
      }

      @Override public int getConstantPoolFieldIndex() {
         return (index);
      }

      @Override public FieldEntry getConstantPoolFieldEntry() {
         return (method.getConstantPool().getFieldEntry(getConstantPoolFieldIndex()));
      }

      @Override int getStackConsumeCount() {
         return (0);
      }

      @Override int getStackProduceCount() {
         return (1);
      }
   }

   static class I_GOTO extends UnconditionalBranch16{
      I_GOTO(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.GOTO, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("branch ");
      }

   }

   static class I_GOTO_W extends Branch32{
      I_GOTO_W(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.GOTO_W, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("goto wide branch");
      }

   }

   static class I_I2B extends CastOperator{
      I_I2B(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.I2B, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop int push byte");
      }

   }

   static class I_I2C extends CastOperator{
      I_I2C(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.I2C, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop int push char");
      }

   }

   static class I_I2D extends CastOperator{
      I_I2D(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.I2D, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop int push double");
      }

   }

   static class I_I2F extends CastOperator{
      I_I2F(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.I2F, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop int push float");
      }

   }

   static class I_I2L extends CastOperator{
      I_I2L(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.I2L, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop int push long");
      }

   }

   static class I_I2S extends CastOperator{
      I_I2S(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.I2S, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop int push short");
      }

   }

   static class I_IADD extends BinaryOperator{
      I_IADD(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IADD, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("add top two ints");
      }

   }

   static class I_IALOAD extends AccessArrayElement{
      I_IALOAD(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IALOAD, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("push int from arrayref and index");
      }

   }

   static class I_IAND extends BinaryOperator{
      I_IAND(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IAND, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("and top two ints");
      }

   }

   static class I_IASTORE extends AssignToArrayElement{
      I_IASTORE(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IASTORE, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop int into arrayref[index]");
      }

   }

   static class I_ICONST_0 extends BytecodeEncodedConstant<Integer>{
      I_ICONST_0(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ICONST_0, _byteReader, _wide, 0);

      }

      @Override String getDescription() {
         return ("push (int) 0");
      }

   }

   static class I_ICONST_1 extends BytecodeEncodedConstant<Integer>{
      I_ICONST_1(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ICONST_1, _byteReader, _wide, 1);

      }

      String getDescription() {
         return ("push (int) 1");
      }

   }

   static class I_ICONST_2 extends BytecodeEncodedConstant<Integer>{
      I_ICONST_2(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ICONST_2, _byteReader, _wide, 2);

      }

      @Override String getDescription() {
         return ("push (int) 2");
      }

   }

   static class I_ICONST_3 extends BytecodeEncodedConstant<Integer>{
      I_ICONST_3(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ICONST_3, _byteReader, _wide, 3);

      }

      @Override String getDescription() {
         return ("push (int) 3");
      }

   }

   static class I_ICONST_4 extends BytecodeEncodedConstant<Integer>{
      I_ICONST_4(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ICONST_4, _byteReader, _wide, 4);

      }

      @Override String getDescription() {
         return ("push (int) 4");
      }

   }

   static class I_ICONST_5 extends BytecodeEncodedConstant<Integer>{
      I_ICONST_5(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ICONST_5, _byteReader, _wide, 5);

      }

      @Override String getDescription() {
         return ("push (int) 5");
      }

   }

   static class I_ICONST_M1 extends BytecodeEncodedConstant<Integer>{
      I_ICONST_M1(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ICONST_M1, _byteReader, _wide, -1);

      }

      @Override String getDescription() {
         return ("push (int)-1");
      }

   }

   static class I_IDIV extends BinaryOperator{
      I_IDIV(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IDIV, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("div top two ints");
      }

   }

   static class I_IF_ACMPEQ extends If{
      I_IF_ACMPEQ(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IF_ACMPEQ, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("branch if stack top references ==");
      }

   }

   static class I_IF_ACMPNE extends If{
      I_IF_ACMPNE(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IF_ACMPNE, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("branch if stack top references !=");
      }

   }

   static class I_IF_ICMPEQ extends If{
      I_IF_ICMPEQ(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IF_ICMPEQ, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("branch if stack top ints ==");
      }

   }

   static class I_IF_ICMPGE extends If{
      I_IF_ICMPGE(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IF_ICMPGE, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("branch if stack top ints >=");
      }

   }

   static class I_IF_ICMPGT extends If{
      I_IF_ICMPGT(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IF_ICMPGT, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("branch if stack top ints > ");
      }

   }

   static class I_IF_ICMPLE extends If{
      I_IF_ICMPLE(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IF_ICMPLE, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("branch if stack top ints <=");
      }

   }

   static class I_IF_ICMPLT extends If{
      I_IF_ICMPLT(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IF_ICMPLT, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("branch if stack top ints < ");
      }

   }

   static class I_IF_ICMPNE extends If{
      I_IF_ICMPNE(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IF_ICMPNE, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("branch if stack top ints !=");
      }

   }

   static class I_IFEQ extends IfUnary{
      I_IFEQ(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IFEQ, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("branch if stack top int == 0");
      }

   }

   static class I_IFGE extends IfUnary{
      I_IFGE(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IFGE, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("branch if stack top int >= 0");
      }

   }

   static class I_IFGT extends IfUnary{
      I_IFGT(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IFGT, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("branch if stack top int > 0");
      }

   }

   static class I_IFLE extends IfUnary{
      I_IFLE(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IFLE, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("branch if stack top int <= 0");
      }

   }

   static class I_IFLT extends IfUnary{
      I_IFLT(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IFLT, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("branch if stack top int < 0");
      }

   }

   static class I_IFNE extends IfUnary{
      I_IFNE(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IFNE, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("branch if stack top int != 0");
      }

   }

   static class I_IFNONNULL extends ConditionalBranch16{
      I_IFNONNULL(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IFNONNULL, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("branch if non null");
      }

   }

   static class I_IFNULL extends ConditionalBranch16{
      I_IFNULL(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IFNULL, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("branch if null");
      }

   }

   static class I_IINC extends Index08{
      private int delta;

      private boolean wide;

      I_IINC(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IINC, _byteReader, _wide);
         wide = _wide;
         if (wide) {
            delta = _byteReader.u2();
         } else {
            delta = _byteReader.u1();
         }

      }

      @Override String getDescription() {
         return ("inc var index 08 bit by byte");
      }

      LocalVariableInfo getLocalVariableInfo() {
         return (method.getLocalVariableTableEntry().getVariable(getThisPC(), getLocalVariableTableIndex()));
      }

      int getLocalVariableTableIndex() {
         return (index);
      }

      int getDelta() {
         return (delta);
      }

      boolean isInc() {
         return getAdjust() > 0;
      }

      int getAdjust() {
         int adjust = delta;
         if (wide) {
            if (adjust > 0x7fff) {
               adjust = -0x10000 + adjust;
            }
         } else {
            if (adjust > 0x7f) {
               adjust = -0x100 + adjust;
            }
         }
         return (adjust);
      }
   }

   static class I_ILOAD extends LocalVariableIndex08Load{
      I_ILOAD(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ILOAD, _byteReader, _wide);

      }

   }

   static class I_ILOAD_0 extends LocalVariableConstIndexLoad{
      I_ILOAD_0(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ILOAD_0, _byteReader, _wide, 0);

      }

   }

   static class I_ILOAD_1 extends LocalVariableConstIndexLoad{
      I_ILOAD_1(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ILOAD_1, _byteReader, _wide, 1);

      }

   }

   static class I_ILOAD_2 extends LocalVariableConstIndexLoad{
      I_ILOAD_2(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ILOAD_2, _byteReader, _wide, 2);

      }

   }

   static class I_ILOAD_3 extends LocalVariableConstIndexLoad{
      I_ILOAD_3(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ILOAD_3, _byteReader, _wide, 3);

      }

   }

   static class I_IMUL extends BinaryOperator{
      I_IMUL(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IMUL, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("mul top two ints");
      }

   }

   static class I_INEG extends UnaryOperator{
      I_INEG(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.INEG, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("neg top int");
      }

   }

   static class I_INSTANCEOF extends Index16{
      I_INSTANCEOF(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.INSTANCEOF, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop reference check against the constant accessed 16 bit push 1 if same");
      }

   }

   static class I_INVOKEINTERFACE extends Index16 implements InterfaceConstantPoolMethodIndexAccessor{
      private int args;

      I_INVOKEINTERFACE(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.INVOKEINTERFACE, _byteReader, _wide);
         args = _byteReader.u1();
         @SuppressWarnings("unused") int zeroByte = _byteReader.u1();

      }

      @Override public int getArgs() {
         return (args);
      }

      @Override String getDescription() {
         return ("pop args and call the interface method referenced by 16 bit constant index");
      }

      @Override public int getConstantPoolInterfaceMethodIndex() {
         return (index);
      }

      @Override public ConstantPool.InterfaceMethodEntry getConstantPoolInterfaceMethodEntry() {
         return (method.getConstantPool().getInterfaceMethodEntry(getConstantPoolInterfaceMethodIndex()));
      }

      @Override public Instruction getArg(int _arg) {
         Instruction child = getFirstChild();
         _arg++;
         while (_arg-- != 0) {
            child = child.getNextExpr();
         }
         return (child);
      }

      @Override public Instruction getInstanceReference() {
         return (getFirstChild());
      }

      @Override int getStackConsumeCount() {
         return (getConstantPoolInterfaceMethodEntry().getStackConsumeCount() + 1); // + 1 to account for instance 'this'

      }

      @Override int getStackProduceCount() {
         return (getConstantPoolInterfaceMethodEntry().getStackProduceCount()); // + 1 to account for instance 'this'
      }
   }

   static class I_INVOKEDYNAMIC extends Index16 implements InterfaceConstantPoolMethodIndexAccessor{
      private int args;

      I_INVOKEDYNAMIC(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.INVOKEDYNAMIC, _byteReader, _wide);
         args = _byteReader.u1();
         @SuppressWarnings("unused") int zeroByte = _byteReader.u1();

      }

      @Override public int getArgs() {
         return (args);
      }

      @Override String getDescription() {
         return ("pop args and call the invoke dynamic method referenced by 16 bit constant index");
      }

      @Override public int getConstantPoolInterfaceMethodIndex() {
         return (index);
      }

      @Override public ConstantPool.InterfaceMethodEntry getConstantPoolInterfaceMethodEntry() {
         return (method.getConstantPool().getInterfaceMethodEntry(getConstantPoolInterfaceMethodIndex()));
      }

      @Override public Instruction getArg(int _arg) {
         Instruction child = getFirstChild();
         _arg++;
         while (_arg-- != 0) {
            child = child.getNextExpr();
         }
         return (child);
      }

      @Override public Instruction getInstanceReference() {
         return (getFirstChild());
      }

      @Override int getStackConsumeCount() {
         return (getConstantPoolInterfaceMethodEntry().getStackConsumeCount() + 1); // + 1 to account for instance 'this'

      }

      @Override int getStackProduceCount() {
         return (getConstantPoolInterfaceMethodEntry().getStackProduceCount()); // + 1 to account for instance 'this'
      }
   }

   static class I_INVOKESPECIAL extends Index16 implements VirtualMethodCall{

      I_INVOKESPECIAL(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.INVOKESPECIAL, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop object reference and args and call the special method referenced by 16 bit constant index");
      }

      @Override public int getConstantPoolMethodIndex() {
         return (index);
      }

      @Override public ConstantPool.MethodEntry getConstantPoolMethodEntry() {
         return (method.getConstantPool().getMethodEntry(getConstantPoolMethodIndex()));
      }

      @Override public Instruction getArg(int _arg) {
         Instruction child = getFirstChild();
         _arg++;
         while (_arg-- != 0) {
            child = child.getNextExpr();
         }
         return (child);
      }

      @Override public Instruction getInstanceReference() {
         return (getFirstChild());
      }

      @Override public int getStackConsumeCount() {
         return (getConstantPoolMethodEntry().getStackConsumeCount() + 1); // + 1 to account for instance 'this'

      }

      @Override int getStackProduceCount() {
         return (getConstantPoolMethodEntry().getStackProduceCount());
      }
   }

   static class I_INVOKESTATIC extends Index16 implements MethodCall{
      I_INVOKESTATIC(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.INVOKESTATIC, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop args and call the  static method referenced by 16 bit constant index");
      }

      @Override public int getConstantPoolMethodIndex() {
         return (index);
      }

      @Override public ConstantPool.MethodEntry getConstantPoolMethodEntry() {
         return (method.getConstantPool().getMethodEntry(getConstantPoolMethodIndex()));
      }

      @Override public Instruction getArg(int _arg) {
         Instruction child = getFirstChild();

         while (_arg-- != 0) {
            child = child.getNextExpr();
         }
         return (child);
      }

      @Override int getStackConsumeCount() {
         return (getConstantPoolMethodEntry().getStackConsumeCount());

      }

      @Override int getStackProduceCount() {
         return (getConstantPoolMethodEntry().getStackProduceCount());
      }
   }

   static class I_INVOKEVIRTUAL extends Index16 implements VirtualMethodCall{
      I_INVOKEVIRTUAL(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.INVOKEVIRTUAL, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop object reference and args and call the method referenced by 16 bit constant index");
      }

      @Override public int getConstantPoolMethodIndex() {
         return (index);
      }

      @Override public ConstantPool.MethodEntry getConstantPoolMethodEntry() {
         return (method.getConstantPool().getMethodEntry(getConstantPoolMethodIndex()));
      }

      @Override public Instruction getArg(int _arg) {
         Instruction child = getFirstChild();
         _arg++;
         while (_arg-- != 0) {
            child = child.getNextExpr();
         }
         return (child);
      }

      @Override public Instruction getInstanceReference() {
         return (getFirstChild());
      }

      @Override int getStackConsumeCount() {
         return (getConstantPoolMethodEntry().getStackConsumeCount() + 1); // + 1 to account for instance 'this'
      }

      @Override int getStackProduceCount() {
         return (getConstantPoolMethodEntry().getStackProduceCount());
      }
   }

   static class I_IOR extends BinaryOperator{
      I_IOR(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IOR, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("or top two ints");
      }

   }

   static class I_IREM extends BinaryOperator{
      I_IREM(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IREM, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("rem top two ints");
      }

   }

   static class I_IRETURN extends Return{
      I_IRETURN(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IRETURN, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("return popped int");
      }

   }

   static class I_ISHL extends BinaryOperator{
      I_ISHL(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ISHL, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("shift left top int");
      }

   }

   static class I_ISHR extends BinaryOperator{
      I_ISHR(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ISHR, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("shift right top int");
      }

   }

   static class I_ISTORE extends LocalVariableIndex08Store{
      I_ISTORE(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ISTORE, _byteReader, _wide);

      }

   }

   static class I_ISTORE_0 extends LocalVariableConstIndexStore{
      I_ISTORE_0(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ISTORE_0, _byteReader, _wide, 0);

      }

   }

   static class I_ISTORE_1 extends LocalVariableConstIndexStore{
      I_ISTORE_1(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ISTORE_1, _byteReader, _wide, 1);

      }

   }

   static class I_ISTORE_2 extends LocalVariableConstIndexStore{
      I_ISTORE_2(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ISTORE_2, _byteReader, _wide, 2);

      }

   }

   static class I_ISTORE_3 extends LocalVariableConstIndexStore{
      I_ISTORE_3(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ISTORE_3, _byteReader, _wide, 3);

      }

   }

   static class I_ISUB extends BinaryOperator{
      I_ISUB(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.ISUB, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("sub top two ints");
      }

   }

   static class I_IUSHR extends BinaryOperator{
      I_IUSHR(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IUSHR, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("shift right top int unsigned");
      }

   }

   static class I_IXOR extends BinaryOperator{
      I_IXOR(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.IXOR, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("xor top two ints");
      }

   }

   static class I_JSR extends UnconditionalBranch16{
      I_JSR(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.JSR, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("jump to subroutine ");
      }

   }

   static class I_JSR_W extends Branch32{
      I_JSR_W(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.JSR_W, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("subroutine");
      }

   }

   static class I_L2D extends CastOperator{
      I_L2D(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.L2D, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop long push double");
      }

   }

   static class I_L2F extends CastOperator{
      I_L2F(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.L2F, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop long push float");
      }

   }

   static class I_L2I extends CastOperator{
      I_L2I(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.L2I, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop long push int");
      }

   }

   static class I_LADD extends BinaryOperator{
      I_LADD(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LADD, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("add top two longs");
      }

   }

   static class I_LALOAD extends AccessArrayElement{
      I_LALOAD(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LALOAD, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("push long from arrayref and index");
      }

   }

   static class I_LAND extends BinaryOperator{
      I_LAND(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LAND, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("and top two longs");
      }

   }

   static class I_LASTORE extends AssignToArrayElement{
      I_LASTORE(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LASTORE, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop long into arrayref[index]");
      }

   }

   static class I_LCMP extends BinaryOperator{
      I_LCMP(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LCMP, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("push result of long comparison");
      }

   }

   static class I_LCONST_0 extends BytecodeEncodedConstant<Long>{
      I_LCONST_0(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LCONST_0, _byteReader, _wide, 0L);

      }

      @Override String getDescription() {
         return ("push (long) 0");
      }

   }

   static class I_LCONST_1 extends BytecodeEncodedConstant<Long>{
      I_LCONST_1(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LCONST_1, _byteReader, _wide, 1L);

      }

      @Override String getDescription() {
         return ("push (long) 1");
      }

   }

   static class I_LDC extends Index08 implements ConstantPoolEntryConstant{
      I_LDC(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LDC, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("push constant at 08 bit index");
      }

      @Override public Object getValue() {
         return (method.getConstantPool().getConstantEntry(getConstantPoolIndex()));

      }

      @Override public int getConstantPoolIndex() {
         return (index);
      }

      @Override public Entry getConstantPoolEntry() {
         return (method.getConstantPool().get(getConstantPoolIndex()));
      }

   }

   static class I_LDC_W extends Index16 implements ConstantPoolEntryConstant{
      I_LDC_W(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LDC_W, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("push constant at 16 bit index");
      }

      @Override public int getConstantPoolIndex() {
         return (index);
      }

      @Override public Object getValue() {
         return (method.getConstantPool().getConstantEntry(getConstantPoolIndex()));

      }

      @Override public Entry getConstantPoolEntry() {
         return (method.getConstantPool().get(getConstantPoolIndex()));
      }

   }

   static class I_LDC2_W extends Index16 implements ConstantPoolEntryConstant{
      I_LDC2_W(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LDC2_W, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("push long/double constant at 16 bit index");
      }

      @Override public int getConstantPoolIndex() {
         return (index);
      }

      @Override public Entry getConstantPoolEntry() {
         return (method.getConstantPool().get(getConstantPoolIndex()));
      }

      @Override public Object getValue() {
         return (method.getConstantPool().getConstantEntry(getConstantPoolIndex()));

      }
   }

   static class I_LDIV extends BinaryOperator{
      I_LDIV(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LDIV, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("div top two longs");
      }

   }

   static class I_LLOAD extends LocalVariableIndex08Load{
      I_LLOAD(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LLOAD, _byteReader, _wide);

      }

   }

   static class I_LLOAD_0 extends LocalVariableConstIndexLoad{
      I_LLOAD_0(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LLOAD_0, _byteReader, _wide, 0);

      }

   }

   static class I_LLOAD_1 extends LocalVariableConstIndexLoad{
      I_LLOAD_1(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LLOAD_1, _byteReader, _wide, 1);

      }

   }

   static class I_LLOAD_2 extends LocalVariableConstIndexLoad{
      I_LLOAD_2(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LLOAD_2, _byteReader, _wide, 2);

      }

   }

   static class I_LLOAD_3 extends LocalVariableConstIndexLoad{
      I_LLOAD_3(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LLOAD_3, _byteReader, _wide, 3);

      }

   }

   static class I_LMUL extends BinaryOperator{
      I_LMUL(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LMUL, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("mul top two longs");
      }

   }

   static class I_LNEG extends UnaryOperator{
      I_LNEG(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LNEG, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("neg top long");
      }

   }

   static class I_LOOKUPSWITCH extends Switch{
      private int[] matches;

      private int npairs;

      I_LOOKUPSWITCH(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LOOKUPSWITCH, _byteReader, _wide);
         int operandStart = _byteReader.getOffset();
         int padLength = ((operandStart % 4) == 0) ? 0 : 4 - (operandStart % 4);
         _byteReader.bytes(padLength);
         offset = _byteReader.u4();
         npairs = _byteReader.u4();
         offsets = new int[npairs];
         matches = new int[npairs];
         for (int i = 0; i < npairs; i++) {
            matches[i] = _byteReader.u4();
            offsets[i] = _byteReader.u4();
         }

      }

      @Override String getDescription() {
         return ("help!");
      }

      int[] getMatches() {
         return (matches);
      }

      int getNpairs() {
         return (npairs);
      }

   }

   static class I_LOR extends BinaryOperator{
      I_LOR(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LOR, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("or top two longs");
      }

   }

   static class I_LREM extends BinaryOperator{
      I_LREM(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LREM, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("rem top two longs");
      }

   }

   static class I_LRETURN extends Return{
      I_LRETURN(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LRETURN, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("return popped long");
      }

   }

   static class I_LSHL extends BinaryOperator{
      I_LSHL(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LSHL, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("shift left top long");
      }

   }

   static class I_LSHR extends BinaryOperator{
      I_LSHR(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LSHR, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("shift right top long");
      }

   }

   static class I_LSTORE extends LocalVariableIndex08Store{
      I_LSTORE(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LSTORE, _byteReader, _wide);

      }

   }

   static class I_LSTORE_0 extends LocalVariableConstIndexStore{
      I_LSTORE_0(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LSTORE_0, _byteReader, _wide, 0);

      }

   }

   static class I_LSTORE_1 extends LocalVariableConstIndexStore{
      I_LSTORE_1(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LSTORE_1, _byteReader, _wide, 1);

      }

   }

   static class I_LSTORE_2 extends LocalVariableConstIndexStore{
      I_LSTORE_2(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LSTORE_2, _byteReader, _wide, 2);

      }

   }

   static class I_LSTORE_3 extends LocalVariableConstIndexStore{
      I_LSTORE_3(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LSTORE_3, _byteReader, _wide, 3);

      }

   }

   static class I_LSUB extends BinaryOperator{
      I_LSUB(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LSUB, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("sub top two longs");
      }

   }

   static class I_LUSHR extends BinaryOperator{
      I_LUSHR(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LUSHR, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("shift right top long unsigned");
      }

   }

   static class I_LXOR extends BinaryOperator{
      I_LXOR(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.LXOR, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("xor top two longs");
      }

   }

   static class I_MONITORENTER extends Instruction{
      I_MONITORENTER(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.MONITORENTER, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop reference and inc monitor");
      }

   }

   static class I_MONITOREXIT extends Instruction{
      I_MONITOREXIT(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.MONITOREXIT, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop reference and dec monitor");
      }

   }

   static class I_MULTIANEWARRAY extends Index16 implements New{
      private int dimensions;

      I_MULTIANEWARRAY(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.MULTIANEWARRAY, _byteReader, _wide);
         dimensions = _byteReader.u1();

      }

      @Override String getDescription() {
         return ("create a multi dimension array of refernce types ");
      }

      int getDimensions() {
         return (dimensions);
      }

   }

   static class I_NEW extends Index16 implements New{
      I_NEW(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.NEW, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("new");
      }

   }

   static class I_NEWARRAY extends Instruction implements New{
      private int type;

      I_NEWARRAY(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.NEWARRAY, _byteReader, _wide);
         type = _byteReader.u1();

      }

      @Override String getDescription() {
         return ("new array simple type");
      }

      int getType() {
         return (type);
      }

   }

   static class I_NOP extends Instruction{
      I_NOP(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.NOP, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("no op");
      }

   }

   static class I_POP extends Instruction{
      I_POP(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.POP, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop one item");
      }

   }

   static class I_POP2 extends Instruction{
      I_POP2(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.POP2, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop 2 items");
      }

   }

   static class I_PUTFIELD extends Index16 implements AssignToInstanceField{
      I_PUTFIELD(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.PUTFIELD, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop stack value into field referenced by 16 bit constant index");
      }

      @Override public int getConstantPoolFieldIndex() {
         return (index);
      }

      @Override public FieldEntry getConstantPoolFieldEntry() {
         return (method.getConstantPool().getFieldEntry(getConstantPoolFieldIndex()));
      }

      @Override int getStackConsumeCount() {
         return (2);
      }

      @Override int getStackProduceCount() {
         return (0);
      }

      @Override public Instruction getInstance() {
         return (getFirstChild());
      }

      @Override public Instruction getValueToAssign() {
         return (getLastChild());
      }

   }

   static class I_PUTSTATIC extends Index16 implements AssignToField{
      I_PUTSTATIC(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.PUTSTATIC, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop stack value into 16 bit constant index as field");
      }

      @Override public int getConstantPoolFieldIndex() {
         return (index);
      }

      @Override public FieldEntry getConstantPoolFieldEntry() {
         return (method.getConstantPool().getFieldEntry(getConstantPoolFieldIndex()));
      }

      @Override int getStackConsumeCount() {
         return (1);
      }

      @Override int getStackProduceCount() {
         return (0);
      }

      @Override public Instruction getValueToAssign() {
         return (getLastChild());
      }
   }

   static class I_RET extends Index08 implements AssignToLocalVariable{
      I_RET(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.RET, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("return to pc in local var index 08 bit");
      }

      @Override public LocalVariableInfo getLocalVariableInfo() {
         return (method.getLocalVariableTableEntry().getVariable(getThisPC() + getLength(), getLocalVariableTableIndex()));
      }

      @Override public boolean isDeclaration() {
         return (method.getLocalVariableTableEntry().getVariable(getThisPC() + getLength(), getLocalVariableTableIndex())
               .getStart() == getThisPC() + getLength());
      }

      @Override public int getLocalVariableTableIndex() {
         return (index);
      }

      // @Override  Instruction getValue() {
      //    return (getFirstChild());
      //}
   }

   static class I_RETURN extends Return{
      I_RETURN(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.RETURN, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("return void");
      }

   }

   static class I_SALOAD extends AccessArrayElement{
      I_SALOAD(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.SALOAD, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("push short from arrayref and index");
      }

   }

   static class I_SASTORE extends AssignToArrayElement{
      I_SASTORE(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.SASTORE, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("pop short into arrayref[index]");
      }

   }

   static class I_SIPUSH extends ImmediateConstant<Integer>{
      I_SIPUSH(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.SIPUSH, _byteReader, _wide);
         value = _byteReader.u2();

      }

      @Override String getDescription() {
         return ("push (short)");
      }
   }

   static class I_SWAP extends Instruction{
      I_SWAP(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.SWAP, _byteReader, _wide);

      }

      @Override String getDescription() {
         return ("swap top 2 items");
      }

   }

   static class I_TABLESWITCH extends Switch{
      private int high;

      private int low;

      I_TABLESWITCH(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.TABLESWITCH, _byteReader, _wide);
         int operandStart = _byteReader.getOffset();
         int padLength = ((operandStart % 4) == 0) ? 0 : 4 - (operandStart % 4);
         _byteReader.bytes(padLength);
         offset = _byteReader.u4();
         low = _byteReader.u4();
         high = _byteReader.u4();
         offsets = new int[high - low + 1];
         for (int i = low; i <= high; i++) {
            offsets[i - low] = _byteReader.u4();
         }

      }

      @Override String getDescription() {
         return ("help!");
      }

      int getHigh() {
         return (high);
      }

      int getLow() {
         return (low);
      }

   }

   static class I_WIDE extends Instruction{
      private boolean iinc;

      private int increment;

      private int index;

      private int wideopcode;

      I_WIDE(MethodModel _methodPoolEntry, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, ByteCode.WIDE, _byteReader, _wide);
         wideopcode = _byteReader.u1();
         index = _byteReader.u2();
         if (((wideopcode >= 0x15 && wideopcode <= 0x19) || (wideopcode >= 0x36 && wideopcode <= 0x3a) || (wideopcode == 0xa9))) {
            iinc = false;
         } else {
            increment = _byteReader.u2();
            iinc = true;
         }

      }

      @Override String getDescription() {
         return ("help");
      }

      int getIncrement() {
         return (increment);
      }

      int getIndex() {
         return (index);
      }

      int getWideopcode() {
         return (wideopcode);
      }

      boolean isiinc() {
         return (iinc);
      }

   }

   static class I_END extends Instruction{

      protected I_END(MethodModel method, int _pc) {
         super(method, ByteCode.NONE, _pc);
      }

      @Override String getDescription() {
         return ("END");
      }
   }

   static abstract class Index extends Instruction{
      protected int index;

      Index(MethodModel _methodPoolEntry, ByteCode _byteCode, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, _byteCode, _byteReader, _wide);

      }

   }

   static abstract class IndexConst extends Index{

      IndexConst(MethodModel _methodPoolEntry, ByteCode _byteCode, ByteReader _byteReader, boolean _wide, int _index) {
         super(_methodPoolEntry, _byteCode, _byteReader, _wide);
         index = _index;

      }

   }

   static abstract class Index08 extends Index{
      Index08(MethodModel _methodPoolEntry, ByteCode _byteCode, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, _byteCode, _byteReader, _wide);
         if (_wide) {
            index = _byteReader.u2();
         } else {
            index = _byteReader.u1();
         }

      }
   }

   static abstract class Index16 extends Index{
      Index16(MethodModel _methodPoolEntry, ByteCode _byteCode, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, _byteCode, _byteReader, _wide);
         index = _byteReader.u2();

      }

   }

   static abstract class Return extends Instruction{

      Return(MethodModel _methodPoolEntry, ByteCode _byteCode, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, _byteCode, _byteReader, _wide);

      }

   }

   static abstract class Switch extends Branch{

      Switch(MethodModel _methodPoolEntry, ByteCode _code, ByteReader _byteReader, boolean _wide) {
         super(_methodPoolEntry, _code, _byteReader, _wide);
      }

      protected int[] offsets;

      protected Instruction[] targets;

      Instruction getTarget(int _index) {
         return (targets[_index]);
      }

      void setTarget(int _index, Instruction _instruction) {
         targets[_index] = _instruction;
      }

      int getAbsolute(int _index) {
         return (getThisPC() + offsets[_index]);
      }

      int getOffset(int _index) {
         return (offsets[_index]);
      }

      int[] getOffsets() {
         return (offsets);
      }

      int getSize() {
         return (offsets.length);
      }

   }

   interface MethodCall{
      int getConstantPoolMethodIndex();

      MethodEntry getConstantPoolMethodEntry();

      Instruction getArg(int _arg);

   }

   interface VirtualMethodCall extends MethodCall{

      Instruction getInstanceReference();
   }

   interface InterfaceConstantPoolMethodIndexAccessor{
      int getConstantPoolInterfaceMethodIndex();

      ConstantPool.InterfaceMethodEntry getConstantPoolInterfaceMethodEntry();

      Instruction getInstanceReference();

      int getArgs();

      Instruction getArg(int _arg);
   }

   static interface New{
   }

   interface FieldReference{
      int getConstantPoolFieldIndex();

      FieldEntry getConstantPoolFieldEntry();
   }

   interface AccessField extends FieldReference{

   }

   interface AssignToField extends FieldReference{
      Instruction getValueToAssign();
   }

   interface AccessInstanceField extends AccessField{
      Instruction getInstance();
   }

   interface AssignToInstanceField extends AssignToField{
      Instruction getInstance();
   }

   interface LocalVariableTableIndexAccessor{
      int getLocalVariableTableIndex();

      LocalVariableInfo getLocalVariableInfo();
   }

   interface AccessLocalVariable extends LocalVariableTableIndexAccessor{

   }

   interface AssignToLocalVariable extends LocalVariableTableIndexAccessor{
      boolean isDeclaration();
   }

   interface Constant<T> {
      T getValue();
   }

   @SuppressWarnings("unchecked") interface ConstantPoolEntryConstant extends Constant{
      int getConstantPoolIndex();

      ConstantPool.Entry getConstantPoolEntry();
   };

   interface HasOperator{
      Operator getOperator();
   }

   interface Binary extends HasOperator{
      Instruction getLhs();

      Instruction getRhs();
   }

   interface Unary extends HasOperator{
      Instruction getUnary();
   }

   static class CloneInstruction extends Instruction{
      private Instruction cloning;

      CloneInstruction(MethodModel method, Instruction _cloning) {
         super(method, ByteCode.CLONE, -1);
         cloning = _cloning;
      }

      @Override String getDescription() {
         return ("CLONE! " + getByteCode());
      }

      @Override int getStackConsumeCount() {
         return (cloning.getStackConsumeCount());
      }

      @Override int getStackProduceCount() {
         return (cloning.getStackProduceCount());
      }

      @Override Instruction getReal() {
         return (cloning);
      }
   }

   static class IncrementInstruction extends Instruction{
      private Instruction fieldOrVariable;

      private boolean isInc;

      private boolean isPre;

      Instruction getFieldOrVariableReference() {
         return fieldOrVariable;
      }

      boolean isPre() {
         return isPre;
      }

      IncrementInstruction(MethodModel method, Instruction _fieldOrVariable, boolean _isInc, boolean _isPre) {
         super(method, ByteCode.INCREMENT, -1);

         fieldOrVariable = _fieldOrVariable;
         isPre = _isPre;
         isInc = _isInc;
      }

      @Override String getDescription() {
         return ("INCREMENT Local Variable! " + getByteCode());
      }

      boolean isInc() {
         return (isInc);
      }

      @Override Instruction getStartInstruction() {
         return (fieldOrVariable.getStartInstruction());
      }
   }

   static class InlineAssignInstruction extends Instruction{
      AssignToLocalVariable assignToLocalVariable;

      Instruction rhs;

      InlineAssignInstruction(MethodModel method, AssignToLocalVariable _assignToLocalVariable, Instruction _rhs) {
         super(method, ByteCode.INLINE_ASSIGN, -1);
         assignToLocalVariable = _assignToLocalVariable;
         rhs = _rhs;

      }

      @Override String getDescription() {
         return ("INLINE ASSIGN! " + getByteCode());
      }

      AssignToLocalVariable getAssignToLocalVariable() {
         return (assignToLocalVariable);
      }

      Instruction getRhs() {
         return (rhs);
      }
   }

   static class FieldArrayElementAssign extends Instruction{
      AssignToArrayElement assignToArrayElement;

      Instruction rhs;

      FieldArrayElementAssign(MethodModel method, AssignToArrayElement _assignToArrayElement, Instruction _rhs) {
         super(method, ByteCode.FIELD_ARRAY_ELEMENT_ASSIGN, -1);
         assignToArrayElement = _assignToArrayElement;
         rhs = _rhs;

      }

      @Override String getDescription() {
         return ("FIELD ARRAY ELEMENT INCREMENT! " + getByteCode());
      }

      AssignToArrayElement getAssignToArrayElement() {
         return (assignToArrayElement);
      }

      Instruction getRhs() {
         return (rhs);
      }

   }

   static class FieldArrayElementIncrement extends Instruction{
      AssignToArrayElement assignToArrayElement;

      boolean isPre;

      boolean isInc;

      FieldArrayElementIncrement(MethodModel method, AssignToArrayElement _assignToArrayElement, boolean _isInc, boolean _isPre) {
         super(method, ByteCode.FIELD_ARRAY_ELEMENT_INCREMENT, -1);
         assignToArrayElement = _assignToArrayElement;
         isPre = _isPre;
         isInc = _isInc;

      }

      @Override String getDescription() {
         return ("FIELD ARRAY ELEMENT INCREMENT! " + getByteCode());
      }

      AssignToArrayElement getAssignToArrayElement() {
         return (assignToArrayElement);
      }

      boolean isPre() {
         return (isPre);
      }

      boolean isInc() {
         return (isInc);
      }
   }

   static class MultiAssignInstruction extends Instruction{
      Instruction from, to, common;

      MultiAssignInstruction(MethodModel method, Instruction _common, Instruction _from, Instruction _to) {
         super(method, ByteCode.MULTI_ASSIGN, -1);
         common = _common;
         from = _from;
         to = _to;
      }

      @Override String getDescription() {
         return ("MULTIASSIGN! " + getByteCode());
      }

      Instruction getTo() {
         return (to);
      }

      Instruction getFrom() {
         return (from);
      }

      Instruction getCommon() {
         return (common);
      }
   }

   static class FakeGoto extends UnconditionalBranch{

      FakeGoto(MethodModel _methodPoolEntry, Instruction _target) {
         super(_methodPoolEntry, ByteCode.FAKEGOTO, _target);
      }

      @Override String getDescription() {

         return "FAKE goto";
      }

   }
}
