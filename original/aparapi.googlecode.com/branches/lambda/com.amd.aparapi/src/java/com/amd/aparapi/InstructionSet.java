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

import com.amd.aparapi.ClassModel.ConstantPool;
import com.amd.aparapi.ClassModel.ConstantPool.Entry;
import com.amd.aparapi.ClassModel.ConstantPool.FieldEntry;
import com.amd.aparapi.ClassModel.ConstantPool.MethodEntry;

import com.amd.aparapi.ClassModel.AttributePool.LocalVariableTableEntry;
import com.amd.aparapi.ClassModel.AttributePool.LocalVariableTableEntry.LocalVariableInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

class InstructionSet{


   static enum TypeSpec{
      NONE(), //
      V(PrimitiveType.v), //     void
      Z(PrimitiveType.u1), // boolean
      C(PrimitiveType.u16), //   char
      F(PrimitiveType.f32), //  float
      D(PrimitiveType.f64), // double
      B(PrimitiveType.s8), //     byte
      S(PrimitiveType.s16), //  short
      I(PrimitiveType.s32), //    int
      L(PrimitiveType.s64), //   long
      OREF(PrimitiveType.ref), // ref (array or object) ref
      N(), // null
      IorForS(),   // int, float or String depending on constant pool entry
      LorD(),  //  long or double depending upon the constant pool entry
      RA(), // return address
      ANY(),// any primitive or reference type
      ARGS(),   // args to method call
      DIMS(); // dims for multiarraynew

      private PrimitiveType primitiveType;


      TypeSpec(PrimitiveType _primitiveType){
         primitiveType = _primitiveType;
      }

      TypeSpec(){
         this(PrimitiveType.none);
      }

      public PrimitiveType getPrimitiveType(){
         return primitiveType;
      }

   }

   static enum LoadSpec{
      NONE(TypeSpec.NONE), //
      F(TypeSpec.F), // Float
      D(TypeSpec.D), // Double
      I(TypeSpec.I), // Integer
      L(TypeSpec.L), // Long
      OREF(TypeSpec.OREF); // Array or Object
      TypeSpec typeSpec = TypeSpec.NONE;

      LoadSpec(TypeSpec _typeSpec){
         typeSpec = _typeSpec;
      }

      TypeSpec getTypeSpec(){
         return (typeSpec);
      }
   }

   static enum StoreSpec{
      NONE(TypeSpec.NONE), //
      F(TypeSpec.F), // Float
      D(TypeSpec.D), // Double
      I(TypeSpec.I), // Integer
      L(TypeSpec.L), // Long
      OREF(TypeSpec.OREF); // Array or Object
      TypeSpec typeSpec = TypeSpec.NONE;

      StoreSpec(TypeSpec _typeSpec){
         typeSpec = _typeSpec;
      }

      TypeSpec getTypeSpec(){
         return (typeSpec);
      }

      static StoreSpec valueOf(PrimitiveType _type){
         if(_type == TypeSpec.B.getPrimitiveType() || _type == TypeSpec.C.getPrimitiveType() ||
               _type == TypeSpec.S.getPrimitiveType() || _type == TypeSpec.Z.getPrimitiveType() ||
               _type == TypeSpec.I.getPrimitiveType()){
            return (I);
         }else if(_type == TypeSpec.F.getPrimitiveType()){
            return (F);
         }else if(_type == TypeSpec.D.getPrimitiveType()){
            return (I);
         }else if(_type == TypeSpec.L.getPrimitiveType()){
            return (L);
         }else if(_type == TypeSpec.OREF.getPrimitiveType()){
            return (OREF);
         }
         return (NONE);
      }
   }


   /**
    * Represents an Operator
    *
    * @author gfrost
    */

   static enum Operator{
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

      I2FCast(PrimitiveType.s32, PrimitiveType.f32), //
      I2LCast(PrimitiveType.s32, PrimitiveType.s64), //
      I2DCast(PrimitiveType.s32, PrimitiveType.f64), //
      I2BCast(PrimitiveType.s32, PrimitiveType.s8), //
      I2CCast(PrimitiveType.s32, PrimitiveType.u16), //
      I2SCast(PrimitiveType.s32, PrimitiveType.s16), //
      L2ICast(PrimitiveType.s64, PrimitiveType.s32), //
      L2FCast(PrimitiveType.s64, PrimitiveType.f32), //
      L2DCast(PrimitiveType.s64, PrimitiveType.f64), //
      F2ICast(PrimitiveType.f32, PrimitiveType.s32), //
      F2LCast(PrimitiveType.f32, PrimitiveType.s64), //
      F2DCast(PrimitiveType.f32, PrimitiveType.f64), //
      D2ICast(PrimitiveType.f64, PrimitiveType.s32), //
      D2LCast(PrimitiveType.f64, PrimitiveType.s64), //
      D2FCast(PrimitiveType.f64, PrimitiveType.f32); //


      private String text;

      private boolean binary;

      private Operator compliment;
      private PrimitiveType castFrom;
      private PrimitiveType castTo;


      Operator(PrimitiveType _castFrom, PrimitiveType _castTo){
         castTo = _castTo;
         castFrom = _castFrom;
         text = "(" + castTo.getOpenCLTypeName() + ")";
         binary = false;
      }

      Operator(boolean _binary, String _text){

         text = _text;
         binary = _binary;
      }

      Operator(boolean _binary, String _text, Operator _c){
         this(_binary, _text);
         compliment = _c;
         compliment.compliment = this;
      }

      Operator(){
         this(false, null);
      }

      String getText(){
         return text;
      }

      Operator getCompliment(){
         return (compliment);
      }

      String getText(boolean _invert){
         return (_invert ? compliment.getText() : getText());
      }

      boolean isBinary(){
         return (binary);

      }

      boolean isUnary(){
         return (!this.equals(Operator.NONE) && !isBinary());

      }
   }

   static enum PushSpec{
      NONE, //
      I(TypeSpec.I), //
      II(TypeSpec.I, TypeSpec.I), //
      III(TypeSpec.I, TypeSpec.I, TypeSpec.I), //
      IIII(TypeSpec.I, TypeSpec.I, TypeSpec.I, TypeSpec.I), //
      IIIII(TypeSpec.I, TypeSpec.I, TypeSpec.I, TypeSpec.I, TypeSpec.I), //
      IIIIII(TypeSpec.I, TypeSpec.I, TypeSpec.I, TypeSpec.I, TypeSpec.I, TypeSpec.I), //
      L(TypeSpec.L), //
      F(TypeSpec.F), //
      D(TypeSpec.D), //
      OREF(TypeSpec.OREF), //
      N(TypeSpec.N), //
      IorForS(TypeSpec.IorForS), //
      LorD(TypeSpec.LorD), //
      RA(TypeSpec.RA),//
      ANY(TypeSpec.ANY);//

      PushSpec(TypeSpec... _types){
         types = _types;
      }

      private TypeSpec[] types;

      int getStackAdjust(){
         return (types.length);
      }

      TypeSpec[] getTypes(){
         return (types);
      }
   }

   static enum PopSpec{
      NONE, //
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
      // O(TypeSpec.O), //
      AA(TypeSpec.OREF, TypeSpec.OREF), //
      A(TypeSpec.OREF), //
      AI(TypeSpec.OREF, TypeSpec.I), //
      AII(TypeSpec.OREF, TypeSpec.I, TypeSpec.I), //
      AIF(TypeSpec.OREF, TypeSpec.I, TypeSpec.F), //
      AID(TypeSpec.OREF, TypeSpec.I, TypeSpec.D), //
      AIL(TypeSpec.OREF, TypeSpec.I, TypeSpec.L), //
      AIC(TypeSpec.OREF, TypeSpec.I, TypeSpec.C), //
      AIS(TypeSpec.OREF, TypeSpec.I, TypeSpec.S), //
      AIB(TypeSpec.OREF, TypeSpec.I, TypeSpec.B), //
      AIA(TypeSpec.OREF, TypeSpec.I, TypeSpec.OREF), //
      LI(TypeSpec.L, TypeSpec.I), //
      OREF_ANY(TypeSpec.OREF, TypeSpec.ANY), //
      ARGS(TypeSpec.ARGS), //
      OREFARGS(TypeSpec.OREF, TypeSpec.ARGS), //
      DIMS(TypeSpec.DIMS),//
      ANY(TypeSpec.ANY),//
      ;

      PopSpec(TypeSpec... _types){
         types = _types;
      }

      private TypeSpec[] types;

      int getStackAdjust(){
         return (types.length);
      }

   }

   static enum ImmediateSpec{
      NONE("NONE"), //
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

      ImmediateSpec(String _name, TypeSpec... _types){

         name = _name;
         types = _types;
      }

      private TypeSpec[] types;

      String getName(){
         return (name);
      }

      TypeSpec[] getTypes(){
         return (types);
      }

   }

   static enum ByteCode{
      // name, operation prefix, immediateOperands, pop operands, push operands
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
      ALOAD(I_ALOAD.class, LoadSpec.OREF, ImmediateSpec.Blvti, PushSpec.OREF), //
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
      ALOAD_0(I_ALOAD_0.class, LoadSpec.OREF, PushSpec.OREF), //
      ALOAD_1(I_ALOAD_1.class, LoadSpec.OREF, PushSpec.OREF), //
      ALOAD_2(I_ALOAD_2.class, LoadSpec.OREF, PushSpec.OREF), //
      ALOAD_3(I_ALOAD_3.class, LoadSpec.OREF, PushSpec.OREF), //
      IALOAD(I_IALOAD.class, PopSpec.AI, PushSpec.I), //
      LALOAD(I_LALOAD.class, PopSpec.AI, PushSpec.L), //
      FALOAD(I_FALOAD.class, PopSpec.AI, PushSpec.F), //
      DALOAD(I_DALOAD.class, PopSpec.AI, PushSpec.D), //
      AALOAD(I_AALOAD.class, PopSpec.AI, PushSpec.OREF), //
      BALOAD(I_BALOAD.class, PopSpec.AI, PushSpec.I), //
      CALOAD(I_CALOAD.class, PopSpec.AI, PushSpec.I), //
      SALOAD(I_SALOAD.class, PopSpec.AI, PushSpec.I), //
      ISTORE(I_ISTORE.class, StoreSpec.I, ImmediateSpec.Blvti, PopSpec.I), //
      LSTORE(I_LSTORE.class, StoreSpec.L, ImmediateSpec.Blvti, PopSpec.L), //
      FSTORE(I_FSTORE.class, StoreSpec.F, ImmediateSpec.Blvti, PopSpec.F), //
      DSTORE(I_DSTORE.class, StoreSpec.D, ImmediateSpec.Blvti, PopSpec.D), //
      ASTORE(I_ASTORE.class, StoreSpec.OREF, ImmediateSpec.Blvti, PopSpec.A), //
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
      ASTORE_0(I_ASTORE_0.class, StoreSpec.OREF, PopSpec.A), //
      ASTORE_1(I_ASTORE_1.class, StoreSpec.OREF, PopSpec.A), //
      ASTORE_2(I_ASTORE_2.class, StoreSpec.OREF, PopSpec.A), //
      ASTORE_3(I_ASTORE_3.class, StoreSpec.OREF, PopSpec.A), //
      IASTORE(I_IASTORE.class, PopSpec.AII), //
      LASTORE(I_LASTORE.class, PopSpec.AIL), //
      FASTORE(I_FASTORE.class, PopSpec.AIF), //
      DASTORE(I_DASTORE.class, PopSpec.AID), //
      AASTORE(I_AASTORE.class, PopSpec.AIA), //
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
      IINC(I_IINC.class, LoadSpec.I, StoreSpec.I, ImmediateSpec.BlvtiBconst), //
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
      IF_ACMPEQ(I_IF_ACMPEQ.class, ImmediateSpec.Spc, PopSpec.AA, Operator.Equal), //
      IF_ACMPNE(I_IF_ACMPNE.class, ImmediateSpec.Spc, PopSpec.AA, Operator.NotEqual), //
      GOTO(I_GOTO.class, ImmediateSpec.Spc), //
      JSR(I_JSR.class, ImmediateSpec.Spc, PushSpec.RA), //
      RET(I_RET.class, ImmediateSpec.Bconst), //
      TABLESWITCH(I_TABLESWITCH.class, ImmediateSpec.NONE, PopSpec.I), //
      LOOKUPSWITCH(I_LOOKUPSWITCH.class, ImmediateSpec.NONE, PopSpec.I), //
      IRETURN(I_IRETURN.class, PopSpec.I), //
      LRETURN(I_LRETURN.class, PopSpec.L), //
      FRETURN(I_FRETURN.class, PopSpec.F), //
      DRETURN(I_DRETURN.class, PopSpec.D), //
      ARETURN(I_ARETURN.class, PopSpec.A), //
      RETURN(I_RETURN.class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.NONE, PopSpec.NONE, PushSpec.NONE, Operator.NONE), //
      GETSTATIC(I_GETSTATIC.class, ImmediateSpec.Scpfi, PushSpec.ANY), //
      PUTSTATIC(I_PUTSTATIC.class, ImmediateSpec.Scpfi, PopSpec.ANY), //
      GETFIELD(I_GETFIELD.class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.Scpfi, PopSpec.A, PushSpec.ANY, Operator.NONE), //
      PUTFIELD(I_PUTFIELD.class, ImmediateSpec.Scpfi, PopSpec.OREF_ANY), //
      INVOKEVIRTUAL(I_INVOKEVIRTUAL.class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.Scpmi, PopSpec.OREFARGS, PushSpec.ANY,
            Operator.NONE), //
      INVOKESPECIAL(I_INVOKESPECIAL.class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.Scpmi, PopSpec.OREFARGS, PushSpec.ANY,
            Operator.NONE), //
      INVOKESTATIC(I_INVOKESTATIC.class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.Scpmi, PopSpec.ARGS, PushSpec.ANY,
            Operator.NONE), //
      INVOKEINTERFACE(I_INVOKEINTERFACE.class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.ScpmiBB, PopSpec.OREFARGS,
            PushSpec.ANY, Operator.NONE), //
      INVOKEDYNAMIC(I_INVOKEDYNAMIC.class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.ScpmiBB, PopSpec.OREFARGS, PushSpec.ANY,
            Operator.NONE), //

      NEW(I_NEW.class, ImmediateSpec.Scpci, PushSpec.OREF), //
      NEWARRAY(I_NEWARRAY.class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.Bconst, PopSpec.I, PushSpec.OREF, Operator.NONE), //
      ANEWARRAY(I_ANEWARRAY.class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.Sconst, PopSpec.I, PushSpec.OREF, Operator.NONE), // 189
      ARRAYLENGTH(I_ARRAYLENGTH.class, PopSpec.A, PushSpec.I), // 190
      ATHROW(I_ATHROW.class, PopSpec.A, PushSpec.OREF), // 191
      CHECKCAST(I_CHECKCAST.class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.Scpci, PopSpec.A, PushSpec.OREF, Operator.NONE), // 192
      INSTANCEOF(I_INSTANCEOF.class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.Scpci, PopSpec.A, PushSpec.I, Operator.NONE), // 193
      MONITORENTER(I_MONITORENTER.class, PopSpec.A), // 194
      MONITOREXIT(I_MONITOREXIT.class, PopSpec.A), // 195
      WIDE(I_WIDE.class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.NONE, PopSpec.NONE, PushSpec.NONE, Operator.NONE), // 196
      MULTIANEWARRAY(I_MULTIANEWARRAY.class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.ScpciBdim, PopSpec.DIMS, PushSpec.OREF,
            Operator.NONE), // 197
      IFNULL(I_IFNULL.class, ImmediateSpec.Spc, PopSpec.A, Operator.EqualNULL), // 198
      IFNONNULL(I_IFNONNULL.class, ImmediateSpec.Spc, PopSpec.A, Operator.NotEqualNULL), // 199
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
                       PushSpec _push, Operator _operator){
         clazz = _class;
         immediate = _immediate;
         push = _push;
         pop = _pop;
         operator = _operator;
         loadSpec = _loadSpec;
         storeSpec = _storeSpec;
      }

      private ByteCode(Class<?> _class, ImmediateSpec _immediate){
         this(_class, LoadSpec.NONE, StoreSpec.NONE, _immediate, PopSpec.NONE, PushSpec.NONE, Operator.NONE);
      }

      private ByteCode(Class<?> _class, PushSpec _push){
         this(_class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.NONE, PopSpec.NONE, _push, Operator.NONE);
      }

      private ByteCode(Class<?> _class, StoreSpec _store, ImmediateSpec _immediate, PopSpec _pop){
         this(_class, LoadSpec.NONE, _store, _immediate, _pop, PushSpec.NONE, Operator.NONE);
      }

      private ByteCode(Class<?> _class, StoreSpec _store, PopSpec _pop){
         this(_class, LoadSpec.NONE, _store, ImmediateSpec.NONE, _pop, PushSpec.NONE, Operator.NONE);
      }

      private ByteCode(Class<?> _class, ImmediateSpec _immediate, PopSpec _pop){
         this(_class, LoadSpec.NONE, StoreSpec.NONE, _immediate, _pop, PushSpec.NONE, Operator.NONE);
      }

      private ByteCode(Class<?> _class, ImmediateSpec _immediate, PopSpec _pop, Operator _operator){
         this(_class, LoadSpec.NONE, StoreSpec.NONE, _immediate, _pop, PushSpec.NONE, _operator);
      }

      private ByteCode(Class<?> _class, LoadSpec _load, ImmediateSpec _immediate, PushSpec _push){
         this(_class, _load, StoreSpec.NONE, _immediate, PopSpec.NONE, _push, Operator.NONE);
      }

      private ByteCode(Class<?> _class, LoadSpec _load, PushSpec _push){
         this(_class, _load, StoreSpec.NONE, ImmediateSpec.NONE, PopSpec.NONE, _push, Operator.NONE);
      }

      private ByteCode(Class<?> _class, ImmediateSpec _immediate, PushSpec _push){
         this(_class, LoadSpec.NONE, StoreSpec.NONE, _immediate, PopSpec.NONE, _push, Operator.NONE);
      }

      private ByteCode(Class<?> _class, PopSpec _pop, PushSpec _push){
         this(_class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.NONE, _pop, _push, Operator.NONE);
      }

      private ByteCode(Class<?> _class, PopSpec _pop, PushSpec _push, Operator _operator){
         this(_class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.NONE, _pop, _push, _operator);
      }

      private ByteCode(Class<?> _class, PopSpec _pop){
         this(_class, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.NONE, _pop, PushSpec.NONE, Operator.NONE);
      }

      private ByteCode(){
         this(null, LoadSpec.NONE, StoreSpec.NONE, ImmediateSpec.NONE, PopSpec.NONE, PushSpec.NONE, Operator.NONE);
      }

      private ByteCode(Class<?> _class, LoadSpec _load, StoreSpec _store, ImmediateSpec _immediate){
         this(_class, _load, _store, _immediate, PopSpec.NONE, PushSpec.NONE, Operator.NONE);
      }

      int getCode(){
         return (ordinal());
      }

      String getName(){
         return (name().toLowerCase());
      }

      ImmediateSpec getImmediate(){
         return (immediate);
      }

      static ByteCode get(int _idx){
         return (values()[_idx]);
      }

      PushSpec getPush(){
         return (push);
      }

      PopSpec getPop(){
         return (pop);
      }

      // Note I am intentionally skipping PushSpec.LorD.
      boolean usesDouble(){
         PushSpec push = getPush();
         PopSpec pop = getPop();
         if((push == PushSpec.D) || (pop == PopSpec.D) || (pop == PopSpec.DD) || (pop == PopSpec.AID)){
            return true;
         }
         return false;
      }

      Instruction newInstruction(ClassModel.ClassModelMethod _classModelMethod, ByteReader byteReader, boolean _isWide){
         Instruction newInstruction = null;
         if(clazz != null){

            try{

               Constructor<?> constructor = clazz.getDeclaredConstructor(ClassModel.ClassModelMethod.class, ByteReader.class, boolean.class);
               newInstruction = (Instruction) constructor.newInstance(_classModelMethod, byteReader, _isWide);
               newInstruction.setLength(byteReader.getOffset() - newInstruction.getThisPC());
            }catch(SecurityException e){
               // TODO Auto-generated catch block
               e.printStackTrace();
            }catch(NoSuchMethodException e){
               // TODO Auto-generated catch block
               e.printStackTrace();
            }catch(IllegalArgumentException e){
               // TODO Auto-generated catch block
               e.printStackTrace();
            }catch(InstantiationException e){
               // TODO Auto-generated catch block
               e.printStackTrace();
            }catch(IllegalAccessException e){
               // TODO Auto-generated catch block
               e.printStackTrace();
            }catch(InvocationTargetException e){
               // TODO Auto-generated catch block
               e.printStackTrace();
            }

         }
         return (newInstruction);
      }

      static Instruction create(ClassModel.ClassModelMethod _classModelMethod, ByteReader _byteReader){
         ByteCode byteCode = get(_byteReader.u1());
         boolean isWide = false;
         if(byteCode.equals(ByteCode.WIDE)){
            // handle wide 
            //System.out.println("WIDE");
            isWide = true;
            byteCode = get(_byteReader.u1());
         }
         Instruction newInstruction = byteCode.newInstruction(_classModelMethod, _byteReader, isWide);

         return (newInstruction);

      }

      Operator getOperator(){
         return (operator);
      }

      public LoadSpec getLoad(){
         return (loadSpec);
      }

      public StoreSpec getStore(){
         return (storeSpec);
      }
   }

   static class CompositeInstruction extends Instruction{

      protected BranchSet branchSet;

      protected CompositeInstruction(ClassModel.ClassModelMethod method, ByteCode _byteCode, Instruction _firstChild, Instruction _lastChild,
                                     BranchSet _branchSet){
         super(method, _byteCode, -1);
         branchSet = _branchSet;
         setChildren(_firstChild, _lastChild);
      }

      @Override String getDescription(){
         return ("COMPOSITE! " + getByteCode());
      }

      @Override int getThisPC(){
         return (getLastChild().getThisPC());
      }

      @Override int getStartPC(){
         return (getFirstChild().getStartPC());
      }

      static CompositeInstruction create(ByteCode _byteCode, ClassModel.ClassModelMethod _methodModel, Instruction _firstChild,
                                         Instruction _lastChild, BranchSet _branchSet){
         CompositeInstruction compositeInstruction = null;
         switch(_byteCode){
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

      BranchSet getBranchSet(){
         return (branchSet);
      }
   }

   static class CompositeIfInstruction extends CompositeInstruction{

      protected CompositeIfInstruction(ClassModel.ClassModelMethod _method, Instruction _firstChild, Instruction _lastChild, BranchSet _branchSet){
         super(_method, ByteCode.COMPOSITE_IF, _firstChild, _lastChild, _branchSet);

      }
   }

   static class CompositeIfElseInstruction extends CompositeInstruction{

      protected CompositeIfElseInstruction(ClassModel.ClassModelMethod _method, Instruction _firstChild, Instruction _lastChild, BranchSet _branchSet){
         super(_method, ByteCode.COMPOSITE_IF_ELSE, _firstChild, _lastChild, _branchSet);

      }

   }

   static class CompositeForSunInstruction extends CompositeInstruction{

      protected CompositeForSunInstruction(ClassModel.ClassModelMethod _method, Instruction _firstChild, Instruction _lastChild, BranchSet _branchSet){
         super(_method, ByteCode.COMPOSITE_FOR_SUN, _firstChild, _lastChild, _branchSet);

      }
   }

   static class CompositeWhileInstruction extends CompositeInstruction{

      protected CompositeWhileInstruction(ClassModel.ClassModelMethod _method, Instruction _firstChild, Instruction _lastChild, BranchSet _branchSet){
         super(_method, ByteCode.COMPOSITE_WHILE, _firstChild, _lastChild, _branchSet);

      }
   }

   static class CompositeEmptyLoopInstruction extends CompositeInstruction{

      protected CompositeEmptyLoopInstruction(ClassModel.ClassModelMethod _method, Instruction _firstChild, Instruction _lastChild,
                                              BranchSet _branchSet){
         super(_method, ByteCode.COMPOSITE_EMPTY_LOOP, _firstChild, _lastChild, _branchSet);

      }
   }

   static class CompositeDoWhileInstruction extends CompositeInstruction{

      protected CompositeDoWhileInstruction(ClassModel.ClassModelMethod _method, Instruction _firstChild, Instruction _lastChild,
                                            BranchSet _branchSet){
         super(_method, ByteCode.COMPOSITE_DO_WHILE, _firstChild, _lastChild, _branchSet);

      }
   }

   static class CompositeForEclipseInstruction extends CompositeInstruction{

      protected CompositeForEclipseInstruction(ClassModel.ClassModelMethod _method, Instruction _firstChild, Instruction _lastChild,
                                               BranchSet _branchSet){
         super(_method, ByteCode.COMPOSITE_FOR_ECLIPSE, _firstChild, _lastChild, _branchSet);

      }
   }

   static class CompositeArbitraryScopeInstruction extends CompositeInstruction{

      protected CompositeArbitraryScopeInstruction(ClassModel.ClassModelMethod _method, Instruction _firstChild, Instruction _lastChild,
                                                   BranchSet _branchSet){
         super(_method, ByteCode.COMPOSITE_ARBITRARY_SCOPE, _firstChild, _lastChild, _branchSet);

      }
   }

   static abstract class OperatorInstruction extends Instruction{

      protected OperatorInstruction(ClassModel.ClassModelMethod _method, ByteCode code, ByteReader reader, boolean _wide){
         super(_method, code, reader, _wide);

      }

      public Operator getOperator(){
         return (getByteCode().getOperator());
      }

   }

   static abstract class BinaryOperator extends OperatorInstruction implements Binary{

      @Override
      public final Instruction getLhs(){
         return (getFirstChild());
      }

      @Override
      public final Instruction getRhs(){
         return (getLastChild());
      }

      protected BinaryOperator(ClassModel.ClassModelMethod _method, ByteCode code, ByteReader reader, boolean _wide){
         super(_method, code, reader, _wide);
      }

   }

   static abstract class UnaryOperator extends OperatorInstruction implements Unary{

      @Override
      public final Instruction getUnary(){
         return (getFirstChild());
      }

      protected UnaryOperator(ClassModel.ClassModelMethod _method, ByteCode code, ByteReader reader, boolean _wide){
         super(_method, code, reader, _wide);
      }

   }

   static abstract class CastOperator extends UnaryOperator{

      protected CastOperator(ClassModel.ClassModelMethod _method, ByteCode code, ByteReader reader, boolean _wide){
         super(_method, code, reader, _wide);
      }

   }

   static abstract class Branch extends Instruction{
      protected int offset;

      protected boolean breakOrContinue;

      protected Instruction target;

      int getAbsolute(){
         return (getThisPC() + getOffset());
      }

      private int getOffset(){
         return (offset);
      }

      Branch(ClassModel.ClassModelMethod _method, ByteCode _byteCode, ByteReader _byteReader, boolean _wide){
         super(_method, _byteCode, _byteReader, _wide);

      }

      Branch(ClassModel.ClassModelMethod _method, ByteCode _byteCode, Instruction _target){
         super(_method, _byteCode, -1);
         setTarget(_target);

      }

      Instruction getTarget(){
         return (target);
      }

      void setTarget(Instruction _target){
         target = _target;
         offset = target.getThisPC() - getThisPC();
         target.addBranchTarget(this);
      }

      boolean isConditional(){
         return (this instanceof ConditionalBranch);
      }

      boolean isUnconditional(){
         return (this instanceof UnconditionalBranch);
      }

      boolean isReverseConditional(){
         return (isConditional() && isReverse());
      }

      boolean isForwardConditional(){
         return (isConditional() && isForward());
      }

      boolean isReverseUnconditional(){
         return (isUnconditional() && isReverse());
      }

      boolean isForwardUnconditional(){
         return (isUnconditional() && isForward());
      }

      boolean isReverse(){
         return (offset < 0);
      }

      boolean isForward(){
         return (offset >= 0);
      }

      void unhook(){
         getTarget().removeBranchTarget(this);

      }

      void setBreakOrContinue(boolean b){
         breakOrContinue = true;

      }

      boolean isBreakOrContinue(){
         return (breakOrContinue);

      }

      public void retarget(Instruction _newTarget){
         //System.out.println("retargetting " + pc + " -> " + target.getThisPC() + " to " + _newTarget.getThisPC());
         unhook(); // removes this from the list of branchers to target
         setTarget(_newTarget);
         //System.out.println("retargetted " + pc + " -> " + target.getThisPC());
         //  _newTarget.addBranchTarget(this);
      }

   }

   static abstract class ConditionalBranch extends Branch{
      private BranchSet branchSet;

      ConditionalBranch(ClassModel.ClassModelMethod _method, ByteCode _byteCode, ByteReader _byteReader, boolean _wide){
         super(_method, _byteCode, _byteReader, _wide);
      }

      void setBranchSet(BranchSet _branchSet){
         branchSet = _branchSet;
      }

      BranchSet getOrCreateBranchSet(){
         if(branchSet == null){
            branchSet = new BranchSet(this);
         }
         return branchSet;
      }

      BranchSet getBranchSet(){
         return branchSet;
      }

      // extent is a guess but we know that the target will be beyond extent, we are not interested in targets that fall before extent
      ConditionalBranch findEndOfConditionalBranchSet(Instruction _extent){
         // bummer ;)
         // we need to find the actual branch set.  Be careful here we can only create a branch set when we *know* that a conditional is the last in the set. 
         // we don't know that here.  We have to scan forward to try to find it 
         ConditionalBranch i = this;
         Instruction theTarget = null;
         ConditionalBranch lastToTarget = null;

         if(getTarget().isAfter(_extent)){
            // if this conditional is already pointing beyond extent then we know the target
            theTarget = getTarget();
            lastToTarget = this;
         }
         while(i.getNextExpr().isBranch() && i.getNextExpr().asBranch().isForwardConditional()){
            Branch nextBranch = i.getNextExpr().asBranch();
            if(theTarget == null && nextBranch.getTarget().isAfter(_extent)){
               theTarget = nextBranch.getTarget();
               lastToTarget = this;
            }else if(nextBranch.getTarget() == theTarget){
               lastToTarget = this;
            }
            i = (ConditionalBranch) i.getNextExpr();

         }
         if(theTarget == null){
            throw new IllegalStateException("unable to find end of while extent");
         }
         return (lastToTarget);
      }
   }

   static abstract class UnconditionalBranch extends Branch{

      UnconditionalBranch(ClassModel.ClassModelMethod _method, ByteCode _byteCode, ByteReader _byteReader, boolean _wide){
         super(_method, _byteCode, _byteReader, _wide);
      }

      UnconditionalBranch(ClassModel.ClassModelMethod _method, ByteCode _byteCode, Instruction _target){
         super(_method, _byteCode, _target);
      }

   }

   static abstract class IfUnary extends ConditionalBranch16 implements Unary{

      IfUnary(ClassModel.ClassModelMethod _method, ByteCode _byteCode, ByteReader _byteReader, boolean _wide){
         super(_method, _byteCode, _byteReader, _wide);

      }

      @Override
      public Instruction getUnary(){
         return (getFirstChild());
      }

   }

   static abstract class If extends ConditionalBranch16 implements Binary{

      If(ClassModel.ClassModelMethod _method, ByteCode _byteCode, ByteReader _byteReader, boolean _wide){
         super(_method, _byteCode, _byteReader, _wide);

      }

      @Override
      public final Instruction getLhs(){
         return (getFirstChild());
      }

      @Override
      public final Instruction getRhs(){
         return (getLastChild());
      }

   }

   static abstract class ConditionalBranch16 extends ConditionalBranch implements HasOperator{

      ConditionalBranch16(ClassModel.ClassModelMethod _method, ByteCode _byteCode, ByteReader _byteReader, boolean _wide){
         super(_method, _byteCode, _byteReader, _wide);
         offset = _byteReader.s2();

      }

      @Override
      public Operator getOperator(){
         return (getByteCode().getOperator());
      }
   }

   static abstract class UnconditionalBranch16 extends UnconditionalBranch{

      UnconditionalBranch16(ClassModel.ClassModelMethod _method, ByteCode _byteCode, ByteReader _byteReader, boolean _wide){
         super(_method, _byteCode, _byteReader, _wide);
         offset = _byteReader.s2();

      }

   }

   static abstract class Branch32 extends UnconditionalBranch{
      Branch32(ClassModel.ClassModelMethod _method, ByteCode _byteCode, ByteReader _byteReader, boolean _wide){
         super(_method, _byteCode, _byteReader, _wide);
         offset = _byteReader.s4();

      }

   }

   static abstract class ArrayAccess extends Instruction{

      protected ArrayAccess(ClassModel.ClassModelMethod _method, ByteCode _byteCode, ByteReader _byteReader, boolean _wide){
         super(_method, _byteCode, _byteReader, _wide);

      }

      Instruction getArrayRef(){
         return (getFirstChild());
      }

      Instruction getArrayIndex(){
         return (getFirstChild().getNextExpr());
      }

   }

   static abstract class AccessArrayElement extends ArrayAccess{
      protected AccessArrayElement(ClassModel.ClassModelMethod _method, ByteCode _byteCode, ByteReader _byteReader, boolean _wide){
         super(_method, _byteCode, _byteReader, _wide);
      }
   }

   static class I_AALOAD extends AccessArrayElement{
      I_AALOAD(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.AALOAD, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("push reference from arrayref and index");
      }

   }

   static abstract class AssignToArrayElement extends ArrayAccess{

      public Instruction getValue(){
         return (getFirstChild().getNextExpr().getNextExpr());
      }

      protected AssignToArrayElement(ClassModel.ClassModelMethod _method, ByteCode _byteCode, ByteReader _byteReader, boolean _wide){
         super(_method, _byteCode, _byteReader, _wide);

      }

   }

   static class I_AASTORE extends AssignToArrayElement{
      I_AASTORE(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.AASTORE, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop reference into arrayref[index]");
      }

   }

   static class I_ACONST_NULL extends Instruction implements Constant<Object>{
      I_ACONST_NULL(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ACONST_NULL, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("push null");
      }

      @Override
      public Object getValue(){
         return null;
      }

   }

   static abstract class LocalVariableConstIndexAccessor extends IndexConst implements AccessLocalVariable{
      LocalVariableConstIndexAccessor(ClassModel.ClassModelMethod _method, ByteCode byteCode, ByteReader byteReader, boolean _wide,
                                      int index){
         super(_method, byteCode, byteReader, _wide, index);
      }

      @Override
      public final int getLocalVariableTableIndex(){
         return (index);
      }

      LocalVariableInfo localVariableInfo;

      @Override
      public final LocalVariableInfo getLocalVariableInfo(){

         return (localVariableInfo);
      }

      public void setLocalVariableInfo(LocalVariableInfo _localVariableInfo){
         localVariableInfo = _localVariableInfo;
      }
   }

   static abstract class LocalVariableConstIndexLoad extends LocalVariableConstIndexAccessor{
      LocalVariableConstIndexLoad(ClassModel.ClassModelMethod _method, ByteCode byteCode, ByteReader byteReader, boolean _wide, int index){
         super(_method, byteCode, byteReader, _wide, index);
      }

      @Override
      final String getDescription(){
         return ("push reference from local var index " + index);
      }

   }

   static abstract class LocalVariableConstIndexStore extends LocalVariableConstIndexAccessor implements AssignToLocalVariable{
      LocalVariableConstIndexStore(ClassModel.ClassModelMethod _method, ByteCode byteCode, ByteReader byteReader, boolean _wide, int index){
         super(_method, byteCode, byteReader, _wide, index);
      }

      @Override
      public boolean isDeclaration(){
         return (getLocalVariableInfo().getStart() == getThisPC() + getLength());
      }

      @Override
      final String getDescription(){
         return ("pop reference into local var index " + index);
      }
   }

   static abstract class LocalVariableIndex08Accessor extends Index08 implements AccessLocalVariable{


      LocalVariableIndex08Accessor(ClassModel.ClassModelMethod _method, ByteCode byteCode, ByteReader byteReader, boolean _wide){
         super(_method, byteCode, byteReader, _wide);
      }

      @Override
      public final int getLocalVariableTableIndex(){
         return (index);
      }

      LocalVariableInfo localVariableInfo;

      @Override
      public final LocalVariableInfo getLocalVariableInfo(){

         return (localVariableInfo);
      }

      public void setLocalVariableInfo(LocalVariableInfo _localVariableInfo){
         localVariableInfo = _localVariableInfo;
      }
   }

   static abstract class LocalVariableIndex08Load extends LocalVariableIndex08Accessor{
      LocalVariableIndex08Load(ClassModel.ClassModelMethod _method, ByteCode byteCode, ByteReader byteReader, boolean _wide){
         super(_method, byteCode, byteReader, _wide);
      }

      @Override
      final String getDescription(){
         return ("push reference from local var index " + index);
      }

   }

   static abstract class LocalVariableIndex08Store extends LocalVariableIndex08Accessor implements AssignToLocalVariable{
      LocalVariableIndex08Store(ClassModel.ClassModelMethod _method, ByteCode byteCode, ByteReader byteReader, boolean _wide){
         super(_method, byteCode, byteReader, _wide);
      }

      @Override
      public boolean isDeclaration(){
         return (getLocalVariableInfo().getStart() == getThisPC() + getLength());
      }

      @Override
      final String getDescription(){
         return ("pop reference into local var index " + index);
      }

   }

   static class I_ALOAD extends LocalVariableIndex08Load{
      I_ALOAD(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ALOAD, _byteReader, _wide);

      }

   }

   static class I_ALOAD_0 extends LocalVariableConstIndexLoad{
      I_ALOAD_0(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ALOAD_0, _byteReader, _wide, 0);
      }
   }

   static class I_ALOAD_1 extends LocalVariableConstIndexLoad{
      I_ALOAD_1(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ALOAD_1, _byteReader, _wide, 1);

      }

   }

   static class I_ALOAD_2 extends LocalVariableConstIndexLoad{
      I_ALOAD_2(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ALOAD_2, _byteReader, _wide, 2);
      }
   }

   static class I_ALOAD_3 extends LocalVariableConstIndexLoad{
      I_ALOAD_3(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ALOAD_3, _byteReader, _wide, 3);
      }
   }

   static class I_ANEWARRAY extends Index16 implements New{

      I_ANEWARRAY(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ANEWARRAY, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("new array of reference");
      }

   }

   static class I_ARETURN extends Return{
      I_ARETURN(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ARETURN, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("return popped reference");
      }

   }

   static class I_ARRAYLENGTH extends Instruction{
      I_ARRAYLENGTH(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ARRAYLENGTH, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop array push length");
      }
   }

   static class I_ASTORE extends LocalVariableIndex08Store{
      I_ASTORE(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ASTORE, _byteReader, _wide);

      }

   }

   static class I_ASTORE_0 extends LocalVariableConstIndexStore{
      I_ASTORE_0(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ASTORE_0, _byteReader, _wide, 0);

      }

   }

   static class I_ASTORE_1 extends LocalVariableConstIndexStore{
      I_ASTORE_1(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ASTORE_1, _byteReader, _wide, 1);

      }

   }

   static class I_ASTORE_2 extends LocalVariableConstIndexStore{
      I_ASTORE_2(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ASTORE_2, _byteReader, _wide, 2);

      }

   }

   static class I_ASTORE_3 extends LocalVariableConstIndexStore{
      I_ASTORE_3(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ASTORE_3, _byteReader, _wide, 3);

      }

   }

   static class I_ATHROW extends Instruction{
      I_ATHROW(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ATHROW, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop reference and throw");
      }

   }

   static class I_BALOAD extends AccessArrayElement{
      I_BALOAD(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.BALOAD, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("push byte/boolean from arrayref and index");
      }

   }

   static class I_BASTORE extends AssignToArrayElement{
      I_BASTORE(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.BASTORE, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop boolean/byte into arrayref[index]");
      }

   }

   static class I_BIPUSH extends ImmediateConstant<Integer>{

      I_BIPUSH(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.BIPUSH, _byteReader, _wide);
         value = _byteReader.u1();

      }

      @Override String getDescription(){
         return ("push (byte)");
      }

      @Override
      public Integer getValue(){
         int byteValue = super.getValue();
         if(byteValue > 127){
            byteValue = -(256 - byteValue);
         }
         return (byteValue);
      }
   }

   static class I_CALOAD extends AccessArrayElement{
      I_CALOAD(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.CALOAD, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("push char from arrayref and index");
      }

   }

   static class I_CASTORE extends AssignToArrayElement{
      I_CASTORE(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.CASTORE, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop char into arrayref[index]");
      }
   }

   static class I_CHECKCAST extends Index16{
      I_CHECKCAST(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.CHECKCAST, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("peek reference check against the constant accessed 16 bit");
      }

   }

   static class I_D2F extends CastOperator{
      I_D2F(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.D2F, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop double push float");
      }

   }

   static class I_D2I extends CastOperator{
      I_D2I(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.D2I, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop double push int");
      }

   }

   static class I_D2L extends CastOperator{
      I_D2L(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.D2L, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop double push long");
      }

   }

   static class I_DADD extends BinaryOperator{
      I_DADD(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DADD, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("add top two doubles");
      }

   }

   static class I_DALOAD extends AccessArrayElement{
      I_DALOAD(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DALOAD, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("push double from arrayref and index");
      }

   }

   static class I_DASTORE extends AssignToArrayElement{
      I_DASTORE(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DASTORE, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop double into arrayref[index]");
      }

   }

   static class I_DCMPG extends Instruction{
      I_DCMPG(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DCMPG, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("push result of double comparison");
      }

   }

   static class I_DCMPL extends Instruction{
      I_DCMPL(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DCMPL, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("push result of double comparison");
      }

   }

   static abstract class BytecodeEncodedConstant<T> extends Instruction implements Constant<T>{
      private T value;

      BytecodeEncodedConstant(ClassModel.ClassModelMethod _method, ByteCode _byteCode, ByteReader _byteReader, boolean _wide, T _value){
         super(_method, _byteCode, _byteReader, _wide);
         value = _value;

      }

      @Override
      public T getValue(){
         return (value);
      }

   }

   static abstract class ImmediateConstant<T> extends Instruction implements Constant<T>{
      protected T value;

      ImmediateConstant(ClassModel.ClassModelMethod _method, ByteCode _byteCode, ByteReader _byteReader, boolean _wide){
         super(_method, _byteCode, _byteReader, _wide);

      }

      @Override
      public T getValue(){
         return (value);
      }

   }

   static class I_DCONST_0 extends BytecodeEncodedConstant<Double>{
      I_DCONST_0(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DCONST_0, _byteReader, _wide, 0.0);

      }

      @Override String getDescription(){
         return ("push (double) 0.0");
      }

   }

   static class I_DCONST_1 extends BytecodeEncodedConstant<Double>{
      I_DCONST_1(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DCONST_1, _byteReader, _wide, 1.0);

      }

      @Override String getDescription(){
         return ("push (double) 1.0");
      }

   }

   static class I_DDIV extends BinaryOperator{
      I_DDIV(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DDIV, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("div top two doubles");
      }

   }

   static class I_DLOAD extends LocalVariableIndex08Load{
      I_DLOAD(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DLOAD, _byteReader, _wide);

      }

   }

   static class I_DLOAD_0 extends LocalVariableConstIndexLoad{
      I_DLOAD_0(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DLOAD_0, _byteReader, _wide, 0);

      }

   }

   static class I_DLOAD_1 extends LocalVariableConstIndexLoad{
      I_DLOAD_1(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DLOAD_1, _byteReader, _wide, 1);

      }

   }

   static class I_DLOAD_2 extends LocalVariableConstIndexLoad{
      I_DLOAD_2(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DLOAD_2, _byteReader, _wide, 2);

      }

   }

   static class I_DLOAD_3 extends LocalVariableConstIndexLoad{
      I_DLOAD_3(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DLOAD_3, _byteReader, _wide, 3);

      }

   }

   static class I_DMUL extends BinaryOperator{
      I_DMUL(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DMUL, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("mul top two doubles");
      }

   }

   static class I_DNEG extends UnaryOperator{
      I_DNEG(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DNEG, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("neg top double");
      }

   }

   static class I_DREM extends BinaryOperator{
      I_DREM(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DREM, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("rem top two doubles");
      }

   }

   static class I_DRETURN extends Return{
      I_DRETURN(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DRETURN, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("return popped double");
      }

   }

   static class I_DSTORE extends LocalVariableIndex08Store{
      I_DSTORE(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DSTORE, _byteReader, _wide);

      }

   }

   static class I_DSTORE_0 extends LocalVariableConstIndexStore{
      I_DSTORE_0(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DSTORE_0, _byteReader, _wide, 0);

      }

   }

   static class I_DSTORE_1 extends LocalVariableConstIndexStore{
      I_DSTORE_1(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DSTORE_1, _byteReader, _wide, 1);

      }

   }

   static class I_DSTORE_2 extends LocalVariableConstIndexStore{
      I_DSTORE_2(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DSTORE_2, _byteReader, _wide, 2);

      }

   }

   static class I_DSTORE_3 extends LocalVariableConstIndexStore{
      I_DSTORE_3(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DSTORE_3, _byteReader, _wide, 3);

      }

   }

   static class I_DSUB extends BinaryOperator{
      I_DSUB(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DSUB, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("sub top two doubles");
      }

   }

   static abstract class DUP extends Instruction{
      DUP(ClassModel.ClassModelMethod _method, ByteCode _byteCode, ByteReader _byteReader, boolean _wide){
         super(_method, _byteCode, _byteReader, _wide);

      }
   }

   static class I_DUP extends DUP{
      I_DUP(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DUP, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("dup top item");
      }

   }

   static class I_DUP_X1 extends DUP{
      I_DUP_X1(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DUP_X1, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("dup top item 2 items down");
      }

   }

   static class I_DUP_X2 extends DUP{
      I_DUP_X2(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DUP_X2, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("dup top item 3 items down");
      }

   }

   static class I_DUP2 extends DUP{
      I_DUP2(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DUP2, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("dup top 2 items");
      }

   }

   static class I_DUP2_X1 extends DUP{
      I_DUP2_X1(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DUP2_X1, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("dup top 2 items 2 items down");
      }

   }

   static class I_DUP2_X2 extends DUP{
      I_DUP2_X2(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.DUP_X2, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("dup top 2 items 3 items down");
      }

   }

   static class I_F2D extends CastOperator{
      I_F2D(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.F2D, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop float push double");
      }

   }

   static class I_F2I extends CastOperator{
      I_F2I(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.F2I, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop float push int");
      }

   }

   static class I_F2L extends CastOperator{
      I_F2L(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.F2L, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop float push long");
      }

   }

   static class I_FADD extends BinaryOperator{
      I_FADD(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.FADD, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("add top two floats");
      }

   }

   static class I_FALOAD extends AccessArrayElement{
      I_FALOAD(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.FALOAD, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("push float from arrayref and index");
      }

   }

   static class I_FASTORE extends AssignToArrayElement{
      I_FASTORE(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.FASTORE, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop float into arrayref[index]");
      }

   }

   static class I_FCMPG extends BinaryOperator{
      I_FCMPG(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.FCMPG, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("push result of float comparison");
      }

   }

   static class I_FCMPL extends BinaryOperator{
      I_FCMPL(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.FCMPL, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("push result of float comparison");
      }

   }

   static class I_FCONST_0 extends BytecodeEncodedConstant<Float>{
      I_FCONST_0(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.FCONST_0, _byteReader, _wide, 0f);

      }

      @Override String getDescription(){
         return ("push (float) 0.0");
      }

   }

   static class I_FCONST_1 extends BytecodeEncodedConstant<Float>{
      I_FCONST_1(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.FCONST_1, _byteReader, _wide, 1f);

      }

      @Override String getDescription(){
         return ("push (float) 1.0");
      }

   }

   static class I_FCONST_2 extends BytecodeEncodedConstant<Float>{
      I_FCONST_2(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.FCONST_2, _byteReader, _wide, 2f);

      }

      @Override String getDescription(){
         return ("push (float) 2.0");
      }

   }

   static class I_FDIV extends BinaryOperator{
      I_FDIV(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.FDIV, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("div top two floats");
      }

   }

   static class I_FLOAD extends LocalVariableIndex08Load{
      I_FLOAD(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.FLOAD, _byteReader, _wide);

      }

   }

   static class I_FLOAD_0 extends LocalVariableConstIndexLoad{
      I_FLOAD_0(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.FLOAD_0, _byteReader, _wide, 0);

      }

   }

   static class I_FLOAD_1 extends LocalVariableConstIndexLoad{
      I_FLOAD_1(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.FLOAD_1, _byteReader, _wide, 1);

      }

   }

   static class I_FLOAD_2 extends LocalVariableConstIndexLoad{
      I_FLOAD_2(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.FLOAD_2, _byteReader, _wide, 2);

      }

   }

   static class I_FLOAD_3 extends LocalVariableConstIndexLoad{
      I_FLOAD_3(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.FLOAD_3, _byteReader, _wide, 3);

      }

   }

   static class I_FMUL extends BinaryOperator{
      I_FMUL(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.FMUL, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("mul top two floats");
      }

   }

   static class I_FNEG extends UnaryOperator{
      I_FNEG(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.FNEG, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("neg top float");
      }

   }

   static class I_FREM extends BinaryOperator{
      I_FREM(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.FREM, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("rem top two floats");
      }

   }

   static class I_FRETURN extends Return{
      I_FRETURN(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.FRETURN, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("return popped float");
      }

   }

   static class I_FSTORE extends LocalVariableIndex08Store{
      I_FSTORE(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.FSTORE, _byteReader, _wide);

      }

   }

   static class I_FSTORE_0 extends LocalVariableConstIndexStore{
      I_FSTORE_0(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.FSTORE_0, _byteReader, _wide, 0);

      }

   }

   static class I_FSTORE_1 extends LocalVariableConstIndexStore{
      I_FSTORE_1(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.FSTORE_1, _byteReader, _wide, 1);

      }

   }

   static class I_FSTORE_2 extends LocalVariableConstIndexStore{
      I_FSTORE_2(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.FSTORE_2, _byteReader, _wide, 2);

      }

   }

   static class I_FSTORE_3 extends LocalVariableConstIndexStore{
      I_FSTORE_3(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.FSTORE_3, _byteReader, _wide, 3);

      }

   }

   static class I_FSUB extends BinaryOperator{
      I_FSUB(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.FSUB, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("sub top two floats");
      }

   }

   static class I_GETFIELD extends Index16 implements AccessInstanceField{
      I_GETFIELD(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.GETFIELD, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("push value from field referenced by 16 bit constant index");
      }

      @Override
      public int getConstantPoolFieldIndex(){
         return (index);
      }

      @Override
      public FieldEntry getConstantPoolFieldEntry(){
         return (method.getConstantPool().getFieldEntry(getConstantPoolFieldIndex()));
      }

      @Override
      public Instruction getInstance(){
         return (getFirstChild());
      }

      @Override int getStackConsumeCount(){
         return (1);
      }

      @Override int getStackProduceCount(){
         return (1);
      }

      @Override public String getFieldName(){
         return (getConstantPoolFieldEntry().getNameAndTypeEntry().getNameUTF8Entry().getUTF8());
      }

   }

   static class I_GETSTATIC extends Index16 implements AccessField{
      I_GETSTATIC(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.GETSTATIC, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("push  static field value at 16 bit constant index");
      }

      @Override
      public int getConstantPoolFieldIndex(){
         return (index);
      }

      @Override
      public FieldEntry getConstantPoolFieldEntry(){
         return (method.getConstantPool().getFieldEntry(getConstantPoolFieldIndex()));
      }

      @Override int getStackConsumeCount(){
         return (0);
      }

      @Override int getStackProduceCount(){
         return (1);
      }

      @Override public String getFieldName(){
         return (getConstantPoolFieldEntry().getNameAndTypeEntry().getNameUTF8Entry().getUTF8());
      }
   }

   static class I_GOTO extends UnconditionalBranch16{
      I_GOTO(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.GOTO, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("branch ");
      }

   }

   static class I_GOTO_W extends Branch32{
      I_GOTO_W(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.GOTO_W, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("goto wide branch");
      }

   }

   static class I_I2B extends CastOperator{
      I_I2B(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.I2B, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop int push byte");
      }

   }

   static class I_I2C extends CastOperator{
      I_I2C(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.I2C, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop int push char");
      }

   }

   static class I_I2D extends CastOperator{
      I_I2D(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.I2D, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop int push double");
      }

   }

   static class I_I2F extends CastOperator{
      I_I2F(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.I2F, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop int push float");
      }

   }

   static class I_I2L extends CastOperator{
      I_I2L(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.I2L, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop int push long");
      }

   }

   static class I_I2S extends CastOperator{
      I_I2S(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.I2S, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop int push short");
      }

   }

   static class I_IADD extends BinaryOperator{
      I_IADD(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IADD, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("add top two ints");
      }

   }

   static class I_IALOAD extends AccessArrayElement{
      I_IALOAD(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IALOAD, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("push int from arrayref and index");
      }

   }

   static class I_IAND extends BinaryOperator{
      I_IAND(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IAND, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("and top two ints");
      }

   }

   static class I_IASTORE extends AssignToArrayElement{
      I_IASTORE(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IASTORE, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop int into arrayref[index]");
      }

   }

   static class I_ICONST_0 extends BytecodeEncodedConstant<Integer>{
      I_ICONST_0(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ICONST_0, _byteReader, _wide, 0);

      }

      @Override String getDescription(){
         return ("push (int) 0");
      }

   }

   static class I_ICONST_1 extends BytecodeEncodedConstant<Integer>{
      I_ICONST_1(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ICONST_1, _byteReader, _wide, 1);

      }

      String getDescription(){
         return ("push (int) 1");
      }

   }

   static class I_ICONST_2 extends BytecodeEncodedConstant<Integer>{
      I_ICONST_2(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ICONST_2, _byteReader, _wide, 2);

      }

      @Override String getDescription(){
         return ("push (int) 2");
      }

   }

   static class I_ICONST_3 extends BytecodeEncodedConstant<Integer>{
      I_ICONST_3(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ICONST_3, _byteReader, _wide, 3);

      }

      @Override String getDescription(){
         return ("push (int) 3");
      }

   }

   static class I_ICONST_4 extends BytecodeEncodedConstant<Integer>{
      I_ICONST_4(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ICONST_4, _byteReader, _wide, 4);

      }

      @Override String getDescription(){
         return ("push (int) 4");
      }

   }

   static class I_ICONST_5 extends BytecodeEncodedConstant<Integer>{
      I_ICONST_5(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ICONST_5, _byteReader, _wide, 5);

      }

      @Override String getDescription(){
         return ("push (int) 5");
      }

   }

   static class I_ICONST_M1 extends BytecodeEncodedConstant<Integer>{
      I_ICONST_M1(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ICONST_M1, _byteReader, _wide, -1);

      }

      @Override String getDescription(){
         return ("push (int)-1");
      }

   }

   static class I_IDIV extends BinaryOperator{
      I_IDIV(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IDIV, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("div top two ints");
      }

   }

   static class I_IF_ACMPEQ extends If{
      I_IF_ACMPEQ(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IF_ACMPEQ, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("branch if stack top references ==");
      }

   }

   static class I_IF_ACMPNE extends If{
      I_IF_ACMPNE(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IF_ACMPNE, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("branch if stack top references !=");
      }

   }

   static class I_IF_ICMPEQ extends If{
      I_IF_ICMPEQ(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IF_ICMPEQ, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("branch if stack top ints ==");
      }

   }

   static class I_IF_ICMPGE extends If{
      I_IF_ICMPGE(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IF_ICMPGE, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("branch if stack top ints >=");
      }

   }

   static class I_IF_ICMPGT extends If{
      I_IF_ICMPGT(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IF_ICMPGT, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("branch if stack top ints > ");
      }

   }

   static class I_IF_ICMPLE extends If{
      I_IF_ICMPLE(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IF_ICMPLE, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("branch if stack top ints <=");
      }

   }

   static class I_IF_ICMPLT extends If{
      I_IF_ICMPLT(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IF_ICMPLT, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("branch if stack top ints < ");
      }

   }

   static class I_IF_ICMPNE extends If{
      I_IF_ICMPNE(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IF_ICMPNE, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("branch if stack top ints !=");
      }

   }

   static class I_IFEQ extends IfUnary{
      I_IFEQ(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IFEQ, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("branch if stack top int == 0");
      }

   }

   static class I_IFGE extends IfUnary{
      I_IFGE(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IFGE, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("branch if stack top int >= 0");
      }

   }

   static class I_IFGT extends IfUnary{
      I_IFGT(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IFGT, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("branch if stack top int > 0");
      }

   }

   static class I_IFLE extends IfUnary{
      I_IFLE(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IFLE, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("branch if stack top int <= 0");
      }

   }

   static class I_IFLT extends IfUnary{
      I_IFLT(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IFLT, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("branch if stack top int < 0");
      }

   }

   static class I_IFNE extends IfUnary{
      I_IFNE(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IFNE, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("branch if stack top int != 0");
      }

   }

   static class I_IFNONNULL extends ConditionalBranch16{
      I_IFNONNULL(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IFNONNULL, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("branch if non null");
      }

   }

   static class I_IFNULL extends ConditionalBranch16{
      I_IFNULL(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IFNULL, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("branch if null");
      }

   }

   static class I_IINC extends Index08 implements LocalVariableTableIndexAccessor{
      private int delta;

      private boolean wide;

      I_IINC(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IINC, _byteReader, _wide);
         wide = _wide;
         if(wide){
            delta = _byteReader.u2();
         }else{
            delta = _byteReader.u1();
         }

      }

      @Override String getDescription(){
         return ("inc var index 08 bit by byte");
      }

      LocalVariableInfo localVariableInfo;

      @Override
      public final LocalVariableInfo getLocalVariableInfo(){

         return (localVariableInfo);
      }

      public void setLocalVariableInfo(LocalVariableInfo _localVariableInfo){
         localVariableInfo = _localVariableInfo;
      }

      @Override
      public int getLocalVariableTableIndex(){
         return (index);
      }

      int getDelta(){
         return (delta);
      }

      boolean isInc(){
         return getAdjust() > 0;
      }

      int getAdjust(){
         int adjust = delta;
         if(wide){
            if(adjust > 0x7fff){
               adjust = -0x10000 + adjust;
            }
         }else{
            if(adjust > 0x7f){
               adjust = -0x100 + adjust;
            }
         }
         return (adjust);
      }
   }

   static class I_ILOAD extends LocalVariableIndex08Load{
      I_ILOAD(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ILOAD, _byteReader, _wide);

      }

   }

   static class I_ILOAD_0 extends LocalVariableConstIndexLoad{
      I_ILOAD_0(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ILOAD_0, _byteReader, _wide, 0);

      }

   }

   static class I_ILOAD_1 extends LocalVariableConstIndexLoad{
      I_ILOAD_1(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ILOAD_1, _byteReader, _wide, 1);

      }

   }

   static class I_ILOAD_2 extends LocalVariableConstIndexLoad{
      I_ILOAD_2(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ILOAD_2, _byteReader, _wide, 2);

      }

   }

   static class I_ILOAD_3 extends LocalVariableConstIndexLoad{
      I_ILOAD_3(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ILOAD_3, _byteReader, _wide, 3);

      }

   }

   static class I_IMUL extends BinaryOperator{
      I_IMUL(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IMUL, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("mul top two ints");
      }

   }

   static class I_INEG extends UnaryOperator{
      I_INEG(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.INEG, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("neg top int");
      }

   }

   static class I_INSTANCEOF extends Index16{
      I_INSTANCEOF(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.INSTANCEOF, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop reference check against the constant accessed 16 bit push 1 if same");
      }

   }

   static class I_INVOKEINTERFACE extends Index16 implements InterfaceMethodCall {
      private int args;

      I_INVOKEINTERFACE(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.INVOKEINTERFACE, _byteReader, _wide);
         args = _byteReader.u1();
         @SuppressWarnings("unused") int zeroByte = _byteReader.u1();

      }

      @Override
      public int getArgs(){
         return (args);
      }

      @Override String getDescription(){
         return ("pop args and call the interface method referenced by 16 bit constant index");
      }

      @Override
      public int getConstantPoolInterfaceMethodIndex(){
         return (index);
      }

      @Override
      public ConstantPool.InterfaceMethodEntry getConstantPoolInterfaceMethodEntry(){
         return (method.getConstantPool().getInterfaceMethodEntry(getConstantPoolInterfaceMethodIndex()));
      }

      @Override
      public Instruction getArg(int _arg){
         Instruction child = getFirstChild();
         _arg++;
         while(_arg-- != 0){
            child = child.getNextExpr();
         }
         return (child);
      }

      @Override
      public Instruction getInstanceReference(){
         return (getFirstChild());
      }

      @Override int getStackConsumeCount(){
         return (getConstantPoolInterfaceMethodEntry().getStackConsumeCount() + 1); // + 1 to account for instance 'this'

      }

      @Override int getStackProduceCount(){
         return (getConstantPoolInterfaceMethodEntry().getStackProduceCount()); // + 1 to account for instance 'this'
      }
   }

   static class I_INVOKEDYNAMIC extends Index16 implements InterfaceMethodCall {
      private int args;

      I_INVOKEDYNAMIC(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.INVOKEDYNAMIC, _byteReader, _wide);
         args = _byteReader.u1();
         @SuppressWarnings("unused") int zeroByte = _byteReader.u1();

      }

      @Override
      public int getArgs(){
         return (args);
      }

      @Override String getDescription(){
         return ("pop args and call the invoke dynamic method referenced by 16 bit constant index");
      }

      @Override
      public int getConstantPoolInterfaceMethodIndex(){
         return (index);
      }

      @Override
      public ConstantPool.InterfaceMethodEntry getConstantPoolInterfaceMethodEntry(){
         return (method.getConstantPool().getInterfaceMethodEntry(getConstantPoolInterfaceMethodIndex()));
      }

      @Override
      public Instruction getArg(int _arg){
         Instruction child = getFirstChild();
         _arg++;
         while(_arg-- != 0){
            child = child.getNextExpr();
         }
         return (child);
      }

      @Override
      public Instruction getInstanceReference(){
         return (getFirstChild());
      }

      @Override int getStackConsumeCount(){
         return (getConstantPoolInterfaceMethodEntry().getStackConsumeCount() + 1); // + 1 to account for instance 'this'

      }

      @Override int getStackProduceCount(){
         return (getConstantPoolInterfaceMethodEntry().getStackProduceCount()); // + 1 to account for instance 'this'
      }
   }

   static class I_INVOKESPECIAL extends Index16 implements VirtualMethodCall{

      I_INVOKESPECIAL(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.INVOKESPECIAL, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop object reference and args and call the special method referenced by 16 bit constant index");
      }

      @Override
      public int getConstantPoolMethodIndex(){
         return (index);
      }

      @Override
      public ConstantPool.MethodEntry getConstantPoolMethodEntry(){
         return (method.getConstantPool().getMethodEntry(getConstantPoolMethodIndex()));
      }

      @Override
      public Instruction getArg(int _arg){
         Instruction child = getFirstChild();
         _arg++;
         while(_arg-- != 0){
            child = child.getNextExpr();
         }
         return (child);
      }

      @Override
      public Instruction getInstanceReference(){
         return (getFirstChild());
      }

      @Override
      public int getStackConsumeCount(){
         return (getConstantPoolMethodEntry().getStackConsumeCount() + 1); // + 1 to account for instance 'this'

      }

      @Override int getStackProduceCount(){
         return (getConstantPoolMethodEntry().getStackProduceCount());
      }
   }

   static class I_INVOKESTATIC extends Index16 implements MethodCall{
      I_INVOKESTATIC(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.INVOKESTATIC, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop args and call the  static method referenced by 16 bit constant index");
      }

      @Override
      public int getConstantPoolMethodIndex(){
         return (index);
      }

      @Override
      public ConstantPool.MethodEntry getConstantPoolMethodEntry(){
         return (method.getConstantPool().getMethodEntry(getConstantPoolMethodIndex()));
      }

      @Override
      public Instruction getArg(int _arg){
         Instruction child = getFirstChild();

         while(_arg-- != 0){
            child = child.getNextExpr();
         }
         return (child);
      }

      @Override int getStackConsumeCount(){
         return (getConstantPoolMethodEntry().getStackConsumeCount());

      }

      @Override int getStackProduceCount(){
         return (getConstantPoolMethodEntry().getStackProduceCount());
      }
   }

   static class I_INVOKEVIRTUAL extends Index16 implements VirtualMethodCall{
      I_INVOKEVIRTUAL(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.INVOKEVIRTUAL, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop object reference and args and call the method referenced by 16 bit constant index");
      }

      @Override
      public int getConstantPoolMethodIndex(){
         return (index);
      }

      @Override
      public ConstantPool.MethodEntry getConstantPoolMethodEntry(){
         return (method.getConstantPool().getMethodEntry(getConstantPoolMethodIndex()));
      }

      @Override
      public Instruction getArg(int _arg){
         Instruction child = getFirstChild();
         _arg++;
         while(_arg-- != 0){
            child = child.getNextExpr();
         }
         return (child);
      }

      @Override
      public Instruction getInstanceReference(){
         return (getFirstChild());
      }

      @Override int getStackConsumeCount(){
         return (getConstantPoolMethodEntry().getStackConsumeCount() + 1); // + 1 to account for instance 'this'
      }

      @Override int getStackProduceCount(){
         return (getConstantPoolMethodEntry().getStackProduceCount());
      }
   }

   static class I_IOR extends BinaryOperator{
      I_IOR(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IOR, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("or top two ints");
      }

   }

   static class I_IREM extends BinaryOperator{
      I_IREM(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IREM, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("rem top two ints");
      }

   }

   static class I_IRETURN extends Return{
      I_IRETURN(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IRETURN, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("return popped int");
      }

   }

   static class I_ISHL extends BinaryOperator{
      I_ISHL(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ISHL, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("shift left top int");
      }

   }

   static class I_ISHR extends BinaryOperator{
      I_ISHR(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ISHR, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("shift right top int");
      }

   }

   static class I_ISTORE extends LocalVariableIndex08Store{
      I_ISTORE(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ISTORE, _byteReader, _wide);

      }

   }

   static class I_ISTORE_0 extends LocalVariableConstIndexStore{
      I_ISTORE_0(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ISTORE_0, _byteReader, _wide, 0);

      }

   }

   static class I_ISTORE_1 extends LocalVariableConstIndexStore{
      I_ISTORE_1(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ISTORE_1, _byteReader, _wide, 1);

      }

   }

   static class I_ISTORE_2 extends LocalVariableConstIndexStore{
      I_ISTORE_2(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ISTORE_2, _byteReader, _wide, 2);

      }

   }

   static class I_ISTORE_3 extends LocalVariableConstIndexStore{
      I_ISTORE_3(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ISTORE_3, _byteReader, _wide, 3);

      }

   }

   static class I_ISUB extends BinaryOperator{
      I_ISUB(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.ISUB, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("sub top two ints");
      }

   }

   static class I_IUSHR extends BinaryOperator{
      I_IUSHR(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IUSHR, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("shift right top int unsigned");
      }

   }

   static class I_IXOR extends BinaryOperator{
      I_IXOR(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.IXOR, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("xor top two ints");
      }

   }

   static class I_JSR extends UnconditionalBranch16{
      I_JSR(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.JSR, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("jump to subroutine ");
      }

   }

   static class I_JSR_W extends Branch32{
      I_JSR_W(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.JSR_W, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("subroutine");
      }

   }

   static class I_L2D extends CastOperator{
      I_L2D(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.L2D, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop long push double");
      }

   }

   static class I_L2F extends CastOperator{
      I_L2F(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.L2F, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop long push float");
      }

   }

   static class I_L2I extends CastOperator{
      I_L2I(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.L2I, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop long push int");
      }

   }

   static class I_LADD extends BinaryOperator{
      I_LADD(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LADD, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("add top two longs");
      }

   }

   static class I_LALOAD extends AccessArrayElement{
      I_LALOAD(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LALOAD, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("push long from arrayref and index");
      }

   }

   static class I_LAND extends BinaryOperator{
      I_LAND(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LAND, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("and top two longs");
      }

   }

   static class I_LASTORE extends AssignToArrayElement{
      I_LASTORE(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LASTORE, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop long into arrayref[index]");
      }

   }

   static class I_LCMP extends BinaryOperator{
      I_LCMP(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LCMP, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("push result of long comparison");
      }

   }

   static class I_LCONST_0 extends BytecodeEncodedConstant<Long>{
      I_LCONST_0(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LCONST_0, _byteReader, _wide, 0L);

      }

      @Override String getDescription(){
         return ("push (long) 0");
      }

   }

   static class I_LCONST_1 extends BytecodeEncodedConstant<Long>{
      I_LCONST_1(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LCONST_1, _byteReader, _wide, 1L);

      }

      @Override String getDescription(){
         return ("push (long) 1");
      }

   }

   static class I_LDC extends Index08 implements ConstantPoolEntryConstant{
      I_LDC(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LDC, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("push constant at 08 bit index");
      }

      @Override
      public Object getValue(){
         return (method.getConstantPool().getConstantEntry(getConstantPoolIndex()));

      }

      @Override
      public int getConstantPoolIndex(){
         return (index);
      }

      @Override
      public Entry getConstantPoolEntry(){
         return (method.getConstantPool().get(getConstantPoolIndex()));
      }

   }

   static class I_LDC_W extends Index16 implements ConstantPoolEntryConstant{
      I_LDC_W(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LDC_W, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("push constant at 16 bit index");
      }

      @Override
      public int getConstantPoolIndex(){
         return (index);
      }

      @Override
      public Object getValue(){
         return (method.getConstantPool().getConstantEntry(getConstantPoolIndex()));

      }

      @Override
      public Entry getConstantPoolEntry(){
         return (method.getConstantPool().get(getConstantPoolIndex()));
      }

   }

   static class I_LDC2_W extends Index16 implements ConstantPoolEntryConstant{
      I_LDC2_W(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LDC2_W, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("push long/double constant at 16 bit index");
      }

      @Override
      public int getConstantPoolIndex(){
         return (index);
      }

      @Override
      public Entry getConstantPoolEntry(){
         return (method.getConstantPool().get(getConstantPoolIndex()));
      }

      @Override
      public Object getValue(){
         return (method.getConstantPool().getConstantEntry(getConstantPoolIndex()));

      }
   }

   static class I_LDIV extends BinaryOperator{
      I_LDIV(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LDIV, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("div top two longs");
      }

   }

   static class I_LLOAD extends LocalVariableIndex08Load{
      I_LLOAD(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LLOAD, _byteReader, _wide);

      }

   }

   static class I_LLOAD_0 extends LocalVariableConstIndexLoad{
      I_LLOAD_0(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LLOAD_0, _byteReader, _wide, 0);

      }

   }

   static class I_LLOAD_1 extends LocalVariableConstIndexLoad{
      I_LLOAD_1(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LLOAD_1, _byteReader, _wide, 1);

      }

   }

   static class I_LLOAD_2 extends LocalVariableConstIndexLoad{
      I_LLOAD_2(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LLOAD_2, _byteReader, _wide, 2);

      }

   }

   static class I_LLOAD_3 extends LocalVariableConstIndexLoad{
      I_LLOAD_3(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LLOAD_3, _byteReader, _wide, 3);

      }

   }

   static class I_LMUL extends BinaryOperator{
      I_LMUL(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LMUL, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("mul top two longs");
      }

   }

   static class I_LNEG extends UnaryOperator{
      I_LNEG(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LNEG, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("neg top long");
      }

   }

   static class I_LOOKUPSWITCH extends Switch{
      private int[] matches;

      private int npairs;

      I_LOOKUPSWITCH(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LOOKUPSWITCH, _byteReader, _wide);
         int operandStart = _byteReader.getOffset();
         int padLength = ((operandStart % 4) == 0) ? 0 : 4 - (operandStart % 4);
         _byteReader.bytes(padLength);
         offset = _byteReader.u4();
         npairs = _byteReader.u4();
         offsets = new int[npairs];
         matches = new int[npairs];
         for(int i = 0; i < npairs; i++){
            matches[i] = _byteReader.u4();
            offsets[i] = _byteReader.u4();
         }

      }

      @Override String getDescription(){
         return ("help!");
      }

      int[] getMatches(){
         return (matches);
      }

      int getNpairs(){
         return (npairs);
      }

   }

   static class I_LOR extends BinaryOperator{
      I_LOR(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LOR, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("or top two longs");
      }

   }

   static class I_LREM extends BinaryOperator{
      I_LREM(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LREM, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("rem top two longs");
      }

   }

   static class I_LRETURN extends Return{
      I_LRETURN(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LRETURN, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("return popped long");
      }

   }

   static class I_LSHL extends BinaryOperator{
      I_LSHL(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LSHL, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("shift left top long");
      }

   }

   static class I_LSHR extends BinaryOperator{
      I_LSHR(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LSHR, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("shift right top long");
      }

   }

   static class I_LSTORE extends LocalVariableIndex08Store{
      I_LSTORE(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LSTORE, _byteReader, _wide);

      }

   }

   static class I_LSTORE_0 extends LocalVariableConstIndexStore{
      I_LSTORE_0(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LSTORE_0, _byteReader, _wide, 0);

      }

   }

   static class I_LSTORE_1 extends LocalVariableConstIndexStore{
      I_LSTORE_1(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LSTORE_1, _byteReader, _wide, 1);

      }

   }

   static class I_LSTORE_2 extends LocalVariableConstIndexStore{
      I_LSTORE_2(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LSTORE_2, _byteReader, _wide, 2);

      }

   }

   static class I_LSTORE_3 extends LocalVariableConstIndexStore{
      I_LSTORE_3(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LSTORE_3, _byteReader, _wide, 3);

      }

   }

   static class I_LSUB extends BinaryOperator{
      I_LSUB(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LSUB, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("sub top two longs");
      }

   }

   static class I_LUSHR extends BinaryOperator{
      I_LUSHR(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LUSHR, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("shift right top long unsigned");
      }

   }

   static class I_LXOR extends BinaryOperator{
      I_LXOR(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.LXOR, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("xor top two longs");
      }

   }

   static class I_MONITORENTER extends Instruction{
      I_MONITORENTER(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.MONITORENTER, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop reference and inc monitor");
      }

   }

   static class I_MONITOREXIT extends Instruction{
      I_MONITOREXIT(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.MONITOREXIT, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop reference and dec monitor");
      }

   }

   static class I_MULTIANEWARRAY extends Index16 implements New{
      private int dimensions;

      I_MULTIANEWARRAY(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.MULTIANEWARRAY, _byteReader, _wide);
         dimensions = _byteReader.u1();

      }

      @Override String getDescription(){
         return ("create a multi dimension array of refernce types ");
      }

      int getDimensions(){
         return (dimensions);
      }

   }

   static class I_NEW extends Index16 implements New{
      I_NEW(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.NEW, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("new");
      }

   }

   static class I_NEWARRAY extends Instruction implements New{
      private int type;

      I_NEWARRAY(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.NEWARRAY, _byteReader, _wide);
         type = _byteReader.u1();

      }

      @Override String getDescription(){
         return ("new array simple prefix");
      }

      int getType(){
         return (type);
      }

   }

   static class I_NOP extends Instruction{
      I_NOP(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.NOP, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("no op");
      }

   }

   static class I_POP extends Instruction{
      I_POP(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.POP, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop one item");
      }

   }

   static class I_POP2 extends Instruction{
      I_POP2(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.POP2, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop 2 items");
      }

   }

   static class I_PUTFIELD extends Index16 implements AssignToInstanceField{
      I_PUTFIELD(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.PUTFIELD, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop stack value into field referenced by 16 bit constant index");
      }

      @Override
      public int getConstantPoolFieldIndex(){
         return (index);
      }

      @Override
      public FieldEntry getConstantPoolFieldEntry(){
         return (method.getConstantPool().getFieldEntry(getConstantPoolFieldIndex()));
      }

      @Override int getStackConsumeCount(){
         return (2);
      }

      @Override int getStackProduceCount(){
         return (0);
      }

      @Override
      public Instruction getInstance(){
         return (getFirstChild());
      }

      @Override
      public Instruction getValueToAssign(){
         return (getLastChild());
      }

      @Override public String getFieldName(){
         return (getConstantPoolFieldEntry().getNameAndTypeEntry().getNameUTF8Entry().getUTF8());
      }
   }

   static class I_PUTSTATIC extends Index16 implements AssignToField{
      I_PUTSTATIC(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.PUTSTATIC, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop stack value into 16 bit constant index as field");
      }

      @Override
      public int getConstantPoolFieldIndex(){
         return (index);
      }

      @Override
      public FieldEntry getConstantPoolFieldEntry(){
         return (method.getConstantPool().getFieldEntry(getConstantPoolFieldIndex()));
      }

      @Override int getStackConsumeCount(){
         return (1);
      }

      @Override int getStackProduceCount(){
         return (0);
      }

      @Override
      public Instruction getValueToAssign(){
         return (getLastChild());
      }

      @Override public String getFieldName(){
         return (getConstantPoolFieldEntry().getNameAndTypeEntry().getNameUTF8Entry().getUTF8());
      }
   }

   static class I_RET extends Index08 implements AssignToLocalVariable{
      I_RET(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.RET, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("return to pc in local var index 08 bit");
      }

      @Override
      public boolean isDeclaration(){
         return (localVariableInfo.getStart() == getThisPC() + getLength());
      }

      @Override
      public int getLocalVariableTableIndex(){
         return (index);
      }

      LocalVariableInfo localVariableInfo;

      @Override
      public final LocalVariableInfo getLocalVariableInfo(){

         return (localVariableInfo);
      }

      public void setLocalVariableInfo(LocalVariableInfo _localVariableInfo){
         localVariableInfo = _localVariableInfo;
      }
   }

   static class I_RETURN extends Return{
      I_RETURN(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.RETURN, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("return void");
      }

   }

   static class I_SALOAD extends AccessArrayElement{
      I_SALOAD(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.SALOAD, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("push short from arrayref and index");
      }

   }

   static class I_SASTORE extends AssignToArrayElement{
      I_SASTORE(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.SASTORE, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("pop short into arrayref[index]");
      }

   }

   static class I_SIPUSH extends ImmediateConstant<Integer>{
      I_SIPUSH(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.SIPUSH, _byteReader, _wide);
         value = _byteReader.u2();

      }

      @Override String getDescription(){
         return ("push (short)");
      }
   }

   static class I_SWAP extends Instruction{
      I_SWAP(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.SWAP, _byteReader, _wide);

      }

      @Override String getDescription(){
         return ("swap top 2 items");
      }

   }

   static class I_TABLESWITCH extends Switch{
      private int high;

      private int low;

      I_TABLESWITCH(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.TABLESWITCH, _byteReader, _wide);
         int operandStart = _byteReader.getOffset();
         int padLength = ((operandStart % 4) == 0) ? 0 : 4 - (operandStart % 4);
         _byteReader.bytes(padLength);
         offset = _byteReader.u4();
         low = _byteReader.u4();
         high = _byteReader.u4();
         offsets = new int[high - low + 1];
         for(int i = low; i <= high; i++){
            offsets[i - low] = _byteReader.u4();
         }

      }

      @Override String getDescription(){
         return ("help!");
      }

      int getHigh(){
         return (high);
      }

      int getLow(){
         return (low);
      }

   }

   static class I_WIDE extends Instruction{
      private boolean iinc;

      private int increment;

      private int index;

      private int wideopcode;

      I_WIDE(ClassModel.ClassModelMethod _method, ByteReader _byteReader, boolean _wide){
         super(_method, ByteCode.WIDE, _byteReader, _wide);
         wideopcode = _byteReader.u1();
         index = _byteReader.u2();

         if(((wideopcode >= 0x15 && wideopcode <= 0x19) || (wideopcode >= 0x36 && wideopcode <= 0x3a) || (wideopcode == 0xa9))){
            iinc = false;
         }else{
            increment = _byteReader.u2();
            iinc = true;
         }

      }

      @Override String getDescription(){
         return ("help");
      }

      int getIncrement(){
         return (increment);
      }

      int getIndex(){
         return (index);
      }

      int getWideopcode(){
         return (wideopcode);
      }

      boolean isiinc(){
         return (iinc);
      }

   }

   static class I_END extends Instruction{

      protected I_END(ClassModel.ClassModelMethod _method, int _pc){
         super(_method, ByteCode.NONE, _pc);
      }

      @Override String getDescription(){
         return ("END");
      }
   }

   static abstract class Index extends Instruction{
      protected int index;

      Index(ClassModel.ClassModelMethod _method, ByteCode _byteCode, ByteReader _byteReader, boolean _wide){
         super(_method, _byteCode, _byteReader, _wide);

      }

   }

   static abstract class IndexConst extends Index{

      IndexConst(ClassModel.ClassModelMethod _method, ByteCode _byteCode, ByteReader _byteReader, boolean _wide, int _index){
         super(_method, _byteCode, _byteReader, _wide);
         index = _index;

      }

   }

   static abstract class Index08 extends Index{
      Index08(ClassModel.ClassModelMethod _method, ByteCode _byteCode, ByteReader _byteReader, boolean _wide){
         super(_method, _byteCode, _byteReader, _wide);
         if(_wide){
            index = _byteReader.u2();
         }else{
            index = _byteReader.u1();
         }

      }
   }

   static abstract class Index16 extends Index{
      Index16(ClassModel.ClassModelMethod _method, ByteCode _byteCode, ByteReader _byteReader, boolean _wide){
         super(_method, _byteCode, _byteReader, _wide);
         index = _byteReader.u2();

      }

   }

   static abstract class Return extends Instruction{

      Return(ClassModel.ClassModelMethod _method, ByteCode _byteCode, ByteReader _byteReader, boolean _wide){
         super(_method, _byteCode, _byteReader, _wide);

      }

   }

   static abstract class Switch extends Branch{

      Switch(ClassModel.ClassModelMethod _method, ByteCode _code, ByteReader _byteReader, boolean _wide){
         super(_method, _code, _byteReader, _wide);
      }

      protected int[] offsets;

      protected Instruction[] targets;

      Instruction getTarget(int _index){
         return (targets[_index]);
      }

      void setTarget(int _index, Instruction _instruction){
         targets[_index] = _instruction;
      }

      int getAbsolute(int _index){
         return (getThisPC() + offsets[_index]);
      }

      int getOffset(int _index){
         return (offsets[_index]);
      }

      int[] getOffsets(){
         return (offsets);
      }

      int getSize(){
         return (offsets.length);
      }

   }

   interface MethodCall {
      int getConstantPoolMethodIndex();

      MethodEntry getConstantPoolMethodEntry();

      Instruction getArg(int _arg);

   }


   interface VirtualMethodCall extends MethodCall{

      Instruction getInstanceReference();
   }

   interface InterfaceMethodCall {
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

      String getFieldName();

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

      void setLocalVariableInfo(LocalVariableTableEntry.LocalVariableInfo _localVariableInfo);

      LocalVariableTableEntry.LocalVariableInfo getLocalVariableInfo();
   }

   interface AccessLocalVariable extends LocalVariableTableIndexAccessor{

   }

   interface AssignToLocalVariable extends LocalVariableTableIndexAccessor{
      boolean isDeclaration();
   }

   interface Constant<T>{
      T getValue();
   }

   @SuppressWarnings("unchecked") interface ConstantPoolEntryConstant extends Constant{
      int getConstantPoolIndex();

      ConstantPool.Entry getConstantPoolEntry();
   }

   ;

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

      CloneInstruction(ClassModel.ClassModelMethod _method, Instruction _cloning){
         super(_method, ByteCode.CLONE, -1);
         cloning = _cloning;
      }

      @Override String getDescription(){
         return ("CLONE! " + getByteCode());
      }

      @Override int getStackConsumeCount(){
         return (cloning.getStackConsumeCount());
      }

      @Override int getStackProduceCount(){
         return (cloning.getStackProduceCount());
      }

      @Override Instruction getReal(){
         return (cloning);
      }
   }

   static class IncrementInstruction extends Instruction{
      private Instruction fieldOrVariable;

      private boolean isInc;

      private boolean isPre;

      Instruction getFieldOrVariableReference(){
         return fieldOrVariable;
      }

      boolean isPre(){
         return isPre;
      }

      IncrementInstruction(ClassModel.ClassModelMethod _method, Instruction _fieldOrVariable, boolean _isInc, boolean _isPre){
         super(_method, ByteCode.INCREMENT, -1);

         fieldOrVariable = _fieldOrVariable;
         isPre = _isPre;
         isInc = _isInc;
      }

      @Override String getDescription(){
         return ("INCREMENT Local Variable! " + getByteCode());
      }

      boolean isInc(){
         return (isInc);
      }

      @Override Instruction getStartInstruction(){
         return (fieldOrVariable.getStartInstruction());
      }
   }

   static class InlineAssignInstruction extends Instruction{
      AssignToLocalVariable assignToLocalVariable;

      Instruction rhs;

      InlineAssignInstruction(ClassModel.ClassModelMethod _method, AssignToLocalVariable _assignToLocalVariable, Instruction _rhs){
         super(_method, ByteCode.INLINE_ASSIGN, -1);
         assignToLocalVariable = _assignToLocalVariable;
         rhs = _rhs;

      }

      @Override String getDescription(){
         return ("INLINE ASSIGN! " + getByteCode());
      }

      AssignToLocalVariable getAssignToLocalVariable(){
         return (assignToLocalVariable);
      }

      Instruction getRhs(){
         return (rhs);
      }
   }

   static class FieldArrayElementAssign extends Instruction{
      AssignToArrayElement assignToArrayElement;

      Instruction rhs;

      FieldArrayElementAssign(ClassModel.ClassModelMethod _method, AssignToArrayElement _assignToArrayElement, Instruction _rhs){
         super(_method, ByteCode.FIELD_ARRAY_ELEMENT_ASSIGN, -1);
         assignToArrayElement = _assignToArrayElement;
         rhs = _rhs;

      }

      @Override String getDescription(){
         return ("FIELD ARRAY_DIM ELEMENT INCREMENT! " + getByteCode());
      }

      AssignToArrayElement getAssignToArrayElement(){
         return (assignToArrayElement);
      }

      Instruction getRhs(){
         return (rhs);
      }

   }

   static class FieldArrayElementIncrement extends Instruction{
      AssignToArrayElement assignToArrayElement;

      boolean isPre;

      boolean isInc;

      FieldArrayElementIncrement(ClassModel.ClassModelMethod _method, AssignToArrayElement _assignToArrayElement, boolean _isInc, boolean _isPre){
         super(_method, ByteCode.FIELD_ARRAY_ELEMENT_INCREMENT, -1);
         assignToArrayElement = _assignToArrayElement;
         isPre = _isPre;
         isInc = _isInc;

      }

      @Override String getDescription(){
         return ("FIELD ARRAY_DIM ELEMENT INCREMENT! " + getByteCode());
      }

      AssignToArrayElement getAssignToArrayElement(){
         return (assignToArrayElement);
      }

      boolean isPre(){
         return (isPre);
      }

      boolean isInc(){
         return (isInc);
      }
   }

   static class MultiAssignInstruction extends Instruction{
      Instruction from, to, common;

      MultiAssignInstruction(ClassModel.ClassModelMethod _method, Instruction _common, Instruction _from, Instruction _to){
         super(_method, ByteCode.MULTI_ASSIGN, -1);
         common = _common;
         from = _from;
         to = _to;
      }

      @Override String getDescription(){
         return ("MULTIASSIGN! " + getByteCode());
      }

      Instruction getTo(){
         return (to);
      }

      Instruction getFrom(){
         return (from);
      }

      Instruction getCommon(){
         return (common);
      }
   }

   static class FakeGoto extends UnconditionalBranch{

      FakeGoto(ClassModel.ClassModelMethod _method, Instruction _target){
         super(_method, ByteCode.FAKEGOTO, _target);
      }

      @Override String getDescription(){

         return "FAKE goto";
      }

   }
}
