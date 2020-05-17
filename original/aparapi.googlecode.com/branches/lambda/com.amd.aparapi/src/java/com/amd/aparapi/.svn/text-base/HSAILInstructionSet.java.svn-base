package com.amd.aparapi;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Stack;

/**
 * Created by user1 on 1/14/14.
 */
public class HSAILInstructionSet {
        abstract static class HSAILInstruction<H extends HSAILInstruction<H>>  {
            static  HSAILRegister[] NONE=new HSAILRegister[0];
            String location;
            Instruction from;
            HSAILRegister[] dests = NONE;
            HSAILOperand[] sources = NONE;


            HSAILInstruction(HSAILStackFrame _hsailStackFrame,Instruction _from, int _destCount, int _sourceCount) {
                from = _from;
                dests = _destCount>0?new HSAILRegister[_destCount]:NONE;
                sources = _sourceCount>0?new HSAILOperand[_sourceCount]:NONE;
                location = _hsailStackFrame.getUniqueLocation(from.getStartPC());
            }


            abstract void render(HSAILRenderer r);

        }

        static class nop extends HSAILInstruction<nop>{
            String endLabel;
            nop(HSAILStackFrame _hsailStackFrame,Instruction _from, String _endLabel) {
               super(_hsailStackFrame, _from, 0,0);
               endLabel = _endLabel;
            }
            @Override
            public void render(HSAILRenderer r) {
                if (endLabel!=null){
                    r.label(endLabel).colon().space();
                }
                r.append("// nop").semicolon();

            }
        }


        abstract static class HSAILInstructionWithDest<H extends HSAILInstructionWithDest<H,Rt,T>, Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstruction<H> {



            HSAILInstructionWithDest(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest) {
                super(_hsailStackFrame, _from, 1, 0);
                dests[0] = _dest;
            }

            Rt getDest() {
                return ((Rt) dests[0]);
            }
        }

        abstract static class HSAILInstructionWithSrc<H extends HSAILInstructionWithSrc<H,Rt,T>, Rt extends HSAILOperand<Rt,T>, T extends PrimitiveType> extends HSAILInstruction<H> {



            HSAILInstructionWithSrc(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _src) {
                super(_hsailStackFrame,_from, 0, 1);
                sources[0] = _src;
            }

            Rt getSrc() {
                return ((Rt) sources[0]);
            }
        }

        abstract static class HSAILInstructionWithSrcSrc<H extends HSAILInstructionWithSrcSrc<H,Rt,T>, Rt extends HSAILOperand<Rt,T>, T extends PrimitiveType> extends HSAILInstruction<H> {


            HSAILInstructionWithSrcSrc(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _src_lhs, Rt _src_rhs) {
                super(_hsailStackFrame,_from, 0, 2);
                sources[0] = _src_lhs;
                sources[1] = _src_rhs;
            }

            Rt getSrcLhs() {
                return ((Rt) sources[0]);
            }

            Rt getSrcRhs() {
                return ((Rt) sources[1]);
            }
        }

        abstract static class HSAILInstructionWithDestSrcSrc<H extends HSAILInstructionWithDestSrcSrc<H,Rd,Rlhs,Rrhs,D,Tlhs, Trhs>, Rd extends HSAILRegister<Rd,D>, Rlhs extends HSAILOperand<Rlhs,Tlhs>, Rrhs extends HSAILOperand<Rrhs,Trhs>,D extends PrimitiveType, Tlhs extends PrimitiveType, Trhs extends PrimitiveType> extends HSAILInstruction<H> {


            HSAILInstructionWithDestSrcSrc(HSAILStackFrame _hsailStackFrame,Instruction _from, Rd _dest, Rlhs _src_lhs, Rrhs _src_rhs) {
                super(_hsailStackFrame,_from, 1, 2);
                dests[0] = _dest;
                sources[0] = _src_lhs;
                sources[1] = _src_rhs;
            }

            Rd getDest() {
                return ((Rd) dests[0]);
            }

            Rlhs getSrcLhs() {
                return ((Rlhs) sources[0]);
            }

            Rrhs getSrcRhs() {
                return ((Rrhs) sources[1]);
            }
        }



        abstract static class HSAILInstructionWithDestSrc<H extends HSAILInstructionWithDestSrc<H,Rd,Rt,D,T>, Rd extends HSAILRegister<Rd,D>, Rt extends HSAILOperand<Rt,T>, D extends PrimitiveType, T extends PrimitiveType> extends HSAILInstruction<H> {

            HSAILInstructionWithDestSrc(HSAILStackFrame _hsailStackFrame,Instruction _from, Rd _dest, Rt _src) {
                super(_hsailStackFrame,_from, 1, 1);
                dests[0] = _dest;
                sources[0] = _src;
            }

            Rd getDest() {
                return ((Rd) dests[0]);
            }

            Rt  getSrc() {
                return ((Rt) sources[0]);
            }
        }

        static  class branch <R extends HSAILRegister<R,s32>> extends HSAILInstructionWithSrc<branch<R>,R, s32> {
            String branchName;
            int pc;
            String targetLabel;



            branch(HSAILStackFrame _hsailStackFrame,Instruction _from, R _src, String _branchName, int _pc) {
                super(_hsailStackFrame,_from, _src);
                branchName = _branchName;
                pc = _pc;
               targetLabel = _hsailStackFrame.getUniqueLocation(pc);
            }




            @Override
            public void render(HSAILRenderer r) {
                r.append(branchName).space().label(targetLabel).semicolon();
            }
        }

        static  class cmp_s32_const_0 <R extends HSAILRegister<R,s32>> extends HSAILInstructionWithSrc<cmp_s32_const_0<R>,R, s32> {
            String type;



            cmp_s32_const_0(HSAILStackFrame _hsailStackFrame,Instruction _from, String _type, R _src) {
                super(_hsailStackFrame, _from, _src);
                type = _type;
            }



            @Override
            public void render(HSAILRenderer r) {
                r.append("cmp_").append(type).append("_b1_").typeName(getSrc()).space().append("$c1").separator().operandName(getSrc()).separator().append("0").semicolon();

            }
        }

        static  class cmp_s32 <R extends HSAILRegister<R,s32>> extends HSAILInstructionWithSrcSrc<cmp_s32<R>,R, s32> {

            String type;



            cmp_s32(HSAILStackFrame _hsailStackFrame,Instruction _from, String _type, R _srcLhs, R _srcRhs) {
                super(_hsailStackFrame,_from, _srcLhs, _srcRhs);
                type = _type;
            }


            @Override
            public void render(HSAILRenderer r) {
                r.append("cmp_").append(type).append("_b1_").typeName(getSrcLhs()).space().append("$c1").separator().operandName(getSrcLhs()).separator().operandName(getSrcRhs()).semicolon();

            }
        }
        static  class cmp_ref <R extends HSAILRegister<R,ref>> extends HSAILInstructionWithSrcSrc<cmp_ref<R>,R, ref> {

            String type;



            cmp_ref(HSAILStackFrame _hsailStackFrame,Instruction _from, String _type, R _srcLhs, R _srcRhs) {
                super(_hsailStackFrame, _from, _srcLhs, _srcRhs);
                type = _type;
            }


            @Override
            public void render(HSAILRenderer r) {
                r.append("cmp_").append(type).append("_b1_").typeName(getSrcLhs()).space().append("$c1").separator().operandName(getSrcLhs()).separator().operandName(getSrcRhs()).semicolon();

            }
        }

        static   class cmp<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstructionWithSrcSrc<cmp<Rt,T>,Rt, T> {

            String type;


            cmp(HSAILStackFrame _hsailStackFrame,Instruction _from, String _type, Rt _srcLhs, Rt _srcRhs) {
                super(_hsailStackFrame,_from, _srcLhs, _srcRhs);
                type = _type;
            }


            @Override
            public void render(HSAILRenderer r) {
                r.append("cmp_").append(type).append("u").append("_b1_").typeName(getSrcLhs()).space().append("$c1").separator().operandName(getSrcLhs()).separator().operandName(getSrcRhs()).semicolon();

            }
        }

        static  class cbr extends HSAILInstruction<cbr> {

            int pc;
            String targetLabel;



            cbr(HSAILStackFrame _hsailStackFrame,Instruction _from, int _pc) {
                super(_hsailStackFrame,_from, 0, 0);
                pc = _pc;
               targetLabel = _hsailStackFrame.getUniqueLocation(pc);
            }



            @Override
            public void render(HSAILRenderer r) {
                r.append("cbr").space().append("$c1").separator().label(targetLabel).semicolon();

            }
        }

        static  class brn extends HSAILInstruction<brn> {
            int pc;
            String targetLabel;



            brn(HSAILStackFrame _hsailStackFrame,Instruction _from, int _pc) {
                super(_hsailStackFrame, _from, 0, 0);
                pc = _pc;
               targetLabel = _hsailStackFrame.getUniqueLocation(pc);
            }


            @Override
            public void render(HSAILRenderer r) {
                r.append("brn").space().label(targetLabel).semicolon();

            }
        }






        static  class nyi extends HSAILInstruction<nyi> {



            nyi(HSAILStackFrame _hsailStackFrame,Instruction _from) {
                super(_hsailStackFrame, _from, 0, 0);
            }



            @Override
            void render(HSAILRenderer r) {

                r.append("NYI ").i(from);

            }
        }

        static  class ld_kernarg<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<ld_kernarg<Rt,T>,Rt, T> {



            ld_kernarg(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest) {
                super(_hsailStackFrame, _from, _dest);
            }


            @Override
            void render(HSAILRenderer r) {
                r.append("ld_kernarg_").typeName(getDest()).space().operandName(getDest()).separator().append("[%_arg").append(getDest().index).append("]").semicolon();
            }
        }

    static  class workitemabsid<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<workitemabsid<Rt,T>,Rt, T> {


        workitemabsid(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest) {
            super(_hsailStackFrame, _from, _dest);
        }


        @Override
        void render(HSAILRenderer r) {
            r.append("workitemabsid_").typeName(getDest()).space().operandName(getDest()).separator().append("0").semicolon();
        }
    }

        static  class ld_arg<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<ld_arg<Rt,T>,Rt, T> {



            ld_arg(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest) {
                super(_hsailStackFrame, _from, _dest);
            }


            @Override
            void render(HSAILRenderer r) {
                r.append("ld_arg_").typeName(getDest()).space().operandName(getDest()).separator().append("[%_arg").append(getDest().index).append("]").semicolon();
            }


        }

        static  abstract class binary_const<H extends binary_const<H, Rt, T, C>, Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType, C extends Number> extends HSAILInstructionWithDestSrc<H, Rt,Rt,T,T> {
            C value;
            String op;



            binary_const(HSAILStackFrame _hsailStackFrame,Instruction _from, String _op, Rt _dest, Rt _src, C _value) {
                super(_hsailStackFrame,_from, _dest, _src);
                value = _value;
                op = _op;
            }

            @Override
            void render(HSAILRenderer r) {
                r.append(op).typeName(getDest()).space().operandName(getDest()).separator().operandName(getSrc()).separator().append(value).semicolon();
            }


        }

        static  class add_const<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType, C extends Number> extends binary_const<add_const<Rt, T, C>, Rt,T, C> {


            add_const(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _src, C _value) {
                super(_hsailStackFrame,_from, "add_", _dest, _src, _value);

            }


        }

        static   class and_const<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType, C extends Number> extends binary_const<and_const<Rt, T,C>, Rt, T, C> {



            and_const(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest,Rt _src, C _value) {
                super(_hsailStackFrame,_from, "and_", _dest, _src, _value);

            }


            @Override
            void render(HSAILRenderer r) {
                r.append(op).append("b64").space().operandName(getDest()).separator().operandName(getSrc()).separator().append(value).semicolon();
            }


        }

        static  class mul_const<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType, C extends Number> extends binary_const< mul_const<Rt, T,C>, Rt, T, C> {


            mul_const(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _src, C _value) {
                super(_hsailStackFrame,_from, "mul_", _dest, _src, _value);

            }



        }

        static class mad<Rd extends HSAILRegister<Rd,ref>, Rt extends HSAILRegister<Rt,ref>> extends HSAILInstructionWithDestSrcSrc<mad<Rd,Rt>, Rd, Rt,Rt, ref, ref, ref> {
            long size;


            mad(HSAILStackFrame _hsailStackFrame,Instruction _from, Rd _dest, Rt _src_lhs, Rt _src_rhs, long _size) {
                super(_hsailStackFrame, _from, _dest, _src_lhs, _src_rhs);
                size = _size;
            }



            @Override void render(HSAILRenderer r) {
                r.append("mad_").typeName(getDest()).space().operandName(getDest()).separator().operandName(getSrcLhs()).separator().append(size).separator().operandName(getSrcRhs()).semicolon();
            }
        }


        static   class cvt<Rt1 extends HSAILRegister<Rt1,T1>, Rt2 extends HSAILRegister<Rt2,T2>,T1 extends PrimitiveType, T2 extends PrimitiveType> extends HSAILInstruction<cvt<Rt1,Rt2,T1,T2>> {



            cvt(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt1 _dest, Rt2 _src) {
                super(_hsailStackFrame,_from, 1, 1);
                dests[0] = _dest;
                sources[0] = _src;
            }

            Rt1 getDest() {
                return ((Rt1) dests[0]);
            }

            Rt2 getSrc() {
                return ((Rt2) sources[0]);
            }

            @Override
            void render(HSAILRenderer r) {
                r.append("cvt_").typeName(getDest()).append("_").typeName(getSrc()).space().operandName(getDest()).separator().operandName(getSrc()).semicolon();
            }


        }


        static  class retvoid extends HSAILInstruction<retvoid> {


            retvoid(HSAILStackFrame _hsailStackFrame,Instruction _from) {
                super(_hsailStackFrame,_from, 0, 0);

            }

            @Override
            void render(HSAILRenderer r) {
                r.append("ret").semicolon();
            }


        }

        static  class ret<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstructionWithSrc<ret<Rt,T>,Rt, T> {

            String endLabel;

            ret(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _src) {
                super(_hsailStackFrame,_from, _src);
                endLabel = _hsailStackFrame.getUniqueName()+"_END";

            }

            @Override
            void render(HSAILRenderer r) {
                r.append("st_arg_").typeName(getSrc()).space().operandName(getSrc()).separator().append("[%_result]").semicolon().nl();
                r.append("ret").semicolon();
            }


        }

        static  class array_store<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstructionWithSrc<array_store<Rt, T>,Rt, T> {
            StackReg_ref mem;



            array_store(HSAILStackFrame _hsailStackFrame,Instruction _from, StackReg_ref _mem, Rt _src) {
                super(_hsailStackFrame,_from, _src);
                mem = _mem;
            }



            @Override
            void render(HSAILRenderer r) {
                // r.append("st_global_").typeName(getSrc()).space().append("[").operandName(mem).append("+").array_len_offset().append("]").separator().operandName(getSrc());
                r.append("st_global_").typeName(getSrc()).space().operandName(getSrc()).separator().append("[").operandName(mem).append("+").array_base_offset().append("]").semicolon();
            }


        }


        static   class array_load<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<array_load<Rt,T>,Rt,T> {
            StackReg_ref mem;



            array_load(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, StackReg_ref _mem) {
                super(_hsailStackFrame,_from, _dest);
                mem = _mem;
            }


            @Override
            void render(HSAILRenderer r) {
                r.append("ld_global_").typeName(getDest()).space().operandName(getDest()).separator().append("[").operandName(mem).append("+").array_base_offset().append("]").semicolon();

            }


        }

        static  class array_len<Rs32 extends HSAILRegister<Rs32,s32>> extends HSAILInstructionWithDest<array_len<Rs32>, Rs32, s32> {
            StackReg_ref mem;



            array_len(HSAILStackFrame _hsailStackFrame,Instruction _from, Rs32 _dest, StackReg_ref _mem) {
                super(_hsailStackFrame,_from, _dest);
                mem = _mem;
            }


            @Override
            void render(HSAILRenderer r) {
                r.append("ld_global_").typeName(getDest()).space().operandName(getDest()).separator().append("[").operandName(mem).append("+").array_len_offset().append("]").semicolon();
            }


        }

        static  class field_load<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstructionWithDest<field_load<Rt,T>, Rt,T> {

            StackReg_ref mem;
            long offset;


            field_load(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, StackReg_ref _mem, long _offset) {
                super(_hsailStackFrame,_from, _dest);
                offset = _offset;
                mem = _mem;
            }



            @Override
            void render(HSAILRenderer r) {
                r.append("ld_global_").typeName(getDest()).space().operandName(getDest()).separator().append("[").operandName(mem).append("+").append(offset).append("]").semicolon();
            }


        }

        static  class static_field_load<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<static_field_load<Rt,T>,Rt, T> {
            long offset;
            StackReg_ref mem;


            static_field_load(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, StackReg_ref _mem, long _offset) {
                super(_hsailStackFrame,_from, _dest);
                offset = _offset;
                mem = _mem;
            }



            @Override
            void render(HSAILRenderer r) {
                r.append("ld_global_").typeName(getDest()).space().operandName(getDest()).separator().append("[").operandName(mem).append("+").append(offset).append("]").semicolon();
            }


        }


        static  class field_store<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithSrc<field_store<Rt,T>,Rt,T> {

            StackReg_ref mem;
            long offset;


            field_store(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _src, StackReg_ref _mem, long _offset) {
                super(_hsailStackFrame,_from, _src);
                offset = _offset;
                mem = _mem;
            }



            @Override
            void render(HSAILRenderer r) {
                r.append("st_global_").typeName(getSrc()).space().operandName(getSrc()).separator().append("[").operandName(mem).append("+").append(offset).append("]").semicolon();
            }


        }


        static final class mov<Rd extends HSAILRegister<Rd,D>,Rt extends HSAILRegister<Rt,T>,D extends PrimitiveType, T extends PrimitiveType> extends HSAILInstructionWithDestSrc<mov<Rd,Rt,D,T>, Rd, Rt,D,T> {


            public mov(HSAILStackFrame _hsailStackFrame,Instruction _from, Rd _dest, Rt _src) {
                super(_hsailStackFrame,_from, _dest, _src);
            }

            @Override
            void render(HSAILRenderer r) {
                r.append("mov_").movTypeName(getDest()).space().operandName(getDest()).separator().operandName(getSrc()).semicolon();

            }


        }

   static final class returnMov<Rd extends HSAILRegister<Rd,D>,Rt extends HSAILRegister<Rt,T>,D extends PrimitiveType, T extends PrimitiveType> extends HSAILInstructionWithDestSrc<returnMov<Rd,Rt,D,T>, Rd, Rt,D,T> {
       String endLabel;


      public returnMov(HSAILStackFrame _hsailStackFrame,Instruction _from, Rd _dest, Rt _src, String _endLabel) {
         super(_hsailStackFrame,_from, _dest, _src);
         endLabel = _endLabel;
      }

      @Override
      void render(HSAILRenderer r) {
         r.append("mov_").movTypeName(getDest()).space().operandName(getDest()).separator().operandName(getSrc()).semicolon();
      }


   }

    static final class returnBranch extends  HSAILInstruction<returnBranch> {
        String endLabel;


        public returnBranch(HSAILStackFrame _hsailStackFrame,Instruction _from, String _endLabel) {
            super(_hsailStackFrame,_from,0,0);
            endLabel = _endLabel;
        }

        @Override
        void render(HSAILRenderer r) {
            r.append("brn").space().label(endLabel).semicolon();
        }


    }

        static  abstract class unary<H extends unary<H,Rt,T>, Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstructionWithDestSrc<H,Rt,Rt, T,T> {
            String op;



            public unary(HSAILStackFrame _hsailStackFrame,Instruction _from, String _op, Rt _destSrc) {
                super(_hsailStackFrame,_from, _destSrc, _destSrc);
                op = _op;
            }

            @Override
            void render(HSAILRenderer r) {
                r.append(op).typeName(getDest()).space().operandName(getDest()).separator().operandName(getDest()).semicolon();
            }

            Rt getDest() {
                return ((Rt) dests[0]);
            }

            Rt getSrc() {
                return ((Rt) sources[0]);
            }


        }

        static  abstract class binary<H extends binary<H,Rt,T>, Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstruction<H> {
            String op;

            public binary(HSAILStackFrame _hsailStackFrame,Instruction _from, String _op, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, 1, 2);
                dests[0] = _dest;
                sources[0] = _lhs;
                sources[1] = _rhs;
                op = _op;
            }

            @Override
            void render(HSAILRenderer r) {
                r.append(op).typeName(getDest()).space().operandName(getDest()).separator().operandName(getLhs()).separator().operandName(getRhs()).semicolon();
            }

            Rt getDest() {
                return ((Rt) dests[0]);
            }

            Rt getRhs() {
                return ((Rt) sources[1]);
            }

            Rt getLhs() {
                return ((Rt) sources[0]);
            }


        }

  /*  abstract class binaryRegConst<T extends JavaType, C> extends HSAILInstruction{
      HSAILRegister<T> dest, lhs;
      C value;
      String op;

      public binaryRegConst(Instruction _from, String _op,  HSAILRegister<T> _dest, HSAILRegister<T> _lhs, C _value){
         super(_from);
         dest = _dest;
         lhs = _lhs;
         value = _value;
         op = _op;
      }
      @Override void renderDefinition(HSAILRenderer r){
         r.append(op).typeName(dest).space().operandName(dest).separator().operandName(lhs).separator().append(value.toString());
      }
   }

     class addConst<T extends JavaType, C> extends binaryRegConst<T, C>{

      public addConst(Instruction _from,   HSAILRegister<T> _dest, HSAILRegister<T> _lhs, C _value_rhs){
         super(_from, "add_", _dest, _lhs, _value_rhs);
      }
   }
   */

        static   class add<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<add<Rt,T>, Rt, T> {


            public add(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "add_", _dest, _lhs, _rhs);
            }


        }

        static   class sub<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<sub<Rt,T>, Rt, T> {


            public sub(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "sub_", _dest, _lhs, _rhs);
            }

        }

        static  class div<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<div<Rt,T>, Rt, T> {


            public div(HSAILStackFrame _hsailStackFrame,Instruction _from,Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "div_", _dest, _lhs, _rhs);
            }

        }

        static  class mul<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<mul<Rt,T>, Rt, T> {


            public mul(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "mul_", _dest, _lhs, _rhs);
            }

        }

        static   class rem<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<rem<Rt,T>, Rt, T> {

            public rem(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "rem_", _dest, _lhs, _rhs);
            }

        }

        static  class neg<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends unary<neg<Rt,T>, Rt, T> {


            public neg(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _destSrc) {
                super(_hsailStackFrame,_from, "neg_", _destSrc);
            }

        }

    static  class nsqrt<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends unary<nsqrt<Rt,T>, Rt, T> {


        public nsqrt(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _destSrc) {
            super(_hsailStackFrame,_from, "nsqrt_", _destSrc);
        }

    }

    static  class ncos<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends unary<ncos<Rt,T>, Rt, T> {


        public ncos(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _destSrc) {
            super(_hsailStackFrame,_from, "ncos_", _destSrc);
        }

    }

    static  class nsin<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends unary<nsin<Rt,T>, Rt, T> {


        public nsin(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _destSrc) {
            super(_hsailStackFrame,_from, "nsin_", _destSrc);
        }

    }



        static  class shl<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<shl<Rt,T>, Rt, T> {

            public shl(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "shl_", _dest, _lhs, _rhs);
            }

        }

        static  class shr<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<shr<Rt,T>, Rt, T> {

            public shr(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "shr_", _dest, _lhs, _rhs);
            }

        }

        static  class ushr<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<ushr<Rt,T>, Rt, T> {

            public ushr(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "ushr_", _dest, _lhs, _rhs);
            }

        }


        static  class and<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<and<Rt,T>, Rt, T> {

            public and(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "and_", _dest, _lhs, _rhs);
            }

            @Override
            void render(HSAILRenderer r) {
                r.append(op).movTypeName(getDest()).space().operandName(getDest()).separator().operandName(getLhs()).separator().operandName(getRhs()).semicolon();
            }

        }

        static  class or<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<or<Rt,T>, Rt, T> {

            public or(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "or_", _dest, _lhs, _rhs);
            }

            @Override
            void render(HSAILRenderer r) {
                r.append(op).movTypeName(getDest()).space().operandName(getDest()).separator().operandName(getLhs()).separator().operandName(getRhs()).semicolon();
            }

        }

        static  class xor<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<xor<Rt,T>, Rt, T> {

            public xor(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "xor_", _dest, _lhs, _rhs);
            }

            @Override
            void render(HSAILRenderer r) {
                r.append(op).movTypeName(getDest()).space().operandName(getDest()).separator().operandName(getLhs()).separator().operandName(getRhs()).semicolon();
            }

        }

        static class mov_const<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType, C extends Number> extends HSAILInstructionWithDest<mov_const<Rt,T,C>,Rt,T> {

            C value;

            public mov_const(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, C _value) {
                super(_hsailStackFrame,_from, _dest);
                value = _value;
            }

            @Override
            void render(HSAILRenderer r) {
                r.append("mov_").movTypeName(getDest()).space().operandName(getDest()).separator().append(value).semicolon();

            }
        }












    static public List<HSAILInstruction> field_store_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        add(_instructions, new field_store<StackReg_f32, f32>(_hsailStackFrame, _i, new StackReg_f32(_hsailStackFrame.stackIdx(_i)+1), new StackReg_ref(_hsailStackFrame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_store_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        add(_instructions, new field_store<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_hsailStackFrame.stackIdx(_i)+1), new StackReg_ref(_hsailStackFrame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }

    static public List<HSAILInstruction> field_store_s16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        add(_instructions, new field_store<StackReg_s16, s16>(_hsailStackFrame, _i, new StackReg_s16(_hsailStackFrame.stackIdx(_i)+1), new StackReg_ref(_hsailStackFrame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_store_u16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        add(_instructions, new field_store<StackReg_u16, u16>(_hsailStackFrame, _i, new StackReg_u16(_hsailStackFrame.stackIdx(_i)+1), new StackReg_ref(_hsailStackFrame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_store_s8(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        add(_instructions, new field_store<StackReg_s8, s8>(_hsailStackFrame, _i, new StackReg_s8(_hsailStackFrame.stackIdx(_i)+1), new StackReg_ref(_hsailStackFrame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_store_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        add(_instructions, new field_store<StackReg_ref, ref>(_hsailStackFrame, _i, new StackReg_ref(_hsailStackFrame.stackIdx(_i)+1), new StackReg_ref(_hsailStackFrame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }

    static public List<HSAILInstruction> field_load_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
       add(_instructions, new field_load<StackReg_ref, ref>(_hsailStackFrame, _i, new StackReg_ref(_hsailStackFrame.stackIdx(_i)), new StackReg_ref(_hsailStackFrame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_load_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        add(_instructions, new field_load<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_ref(_hsailStackFrame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_load_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        add(_instructions, new field_load<StackReg_f32, f32>(_hsailStackFrame, _i, new StackReg_f32(_hsailStackFrame.stackIdx(_i)), new StackReg_ref(_hsailStackFrame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_load_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        add(_instructions, new field_load<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_ref(_hsailStackFrame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_load_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        add(_instructions, new field_load<StackReg_f64, f64>(_hsailStackFrame, _i, new StackReg_f64(_hsailStackFrame.stackIdx(_i)), new StackReg_ref(_hsailStackFrame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_load_s16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        add(_instructions, new field_load<StackReg_s16, s16>(_hsailStackFrame, _i, new StackReg_s16(_hsailStackFrame.stackIdx(_i)), new StackReg_ref(_hsailStackFrame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_load_u16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        add(_instructions, new field_load<StackReg_u16, u16>(_hsailStackFrame, _i, new StackReg_u16(_hsailStackFrame.stackIdx(_i)), new StackReg_ref(_hsailStackFrame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_load_s8(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        add(_instructions, new field_load<StackReg_s8, s8>(_hsailStackFrame, _i, new StackReg_s8(_hsailStackFrame.stackIdx(_i)), new StackReg_ref(_hsailStackFrame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> static_field_load_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        add(_instructions, new static_field_load<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_ref(_hsailStackFrame.stackIdx(_i)), (long) UnsafeWrapper.staticFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> static_field_load_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        add(_instructions, new static_field_load<StackReg_f64, f64>(_hsailStackFrame, _i, new StackReg_f64(_hsailStackFrame.stackIdx(_i)), new StackReg_ref(_hsailStackFrame.stackIdx(_i)), (long) UnsafeWrapper.staticFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> static_field_load_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        add(_instructions, new static_field_load<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_ref(_hsailStackFrame.stackIdx(_i)), (long) UnsafeWrapper.staticFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> static_field_load_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        add(_instructions, new static_field_load<StackReg_f32, f32>(_hsailStackFrame, _i, new StackReg_f32(_hsailStackFrame.stackIdx(_i)), new StackReg_ref(_hsailStackFrame.stackIdx(_i)), (long) UnsafeWrapper.staticFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> static_field_load_s16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        add(_instructions, new static_field_load<StackReg_s16, s16>(_hsailStackFrame, _i, new StackReg_s16(_hsailStackFrame.stackIdx(_i)), new StackReg_ref(_hsailStackFrame.stackIdx(_i)), (long) UnsafeWrapper.staticFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> static_field_load_u16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        add(_instructions, new static_field_load<StackReg_u16, u16>(_hsailStackFrame, _i, new StackReg_u16(_hsailStackFrame.stackIdx(_i)), new StackReg_ref(_hsailStackFrame.stackIdx(_i)), (long) UnsafeWrapper.staticFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> static_field_load_s8(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        add(_instructions, new static_field_load<StackReg_s8, s8>(_hsailStackFrame, _i, new StackReg_s8(_hsailStackFrame.stackIdx(_i)), new StackReg_ref(_hsailStackFrame.stackIdx(_i)), (long) UnsafeWrapper.staticFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> static_field_load_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        add(_instructions, new static_field_load<StackReg_ref, ref>(_hsailStackFrame, _i, new StackReg_ref(_hsailStackFrame.stackIdx(_i)), new StackReg_ref(_hsailStackFrame.stackIdx(_i)), (long) UnsafeWrapper.staticFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ret_void(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new retvoid(_hsailStackFrame, _i));
        return(_instructions);
    }
    static public List<HSAILInstruction> ret_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new ret<StackReg_ref, ref>(_hsailStackFrame, _i, new StackReg_ref(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }

    static public List<HSAILInstruction> ret_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new ret<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }

    static public List<HSAILInstruction> ret_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new ret<StackReg_f32, f32>(_hsailStackFrame, _i, new StackReg_f32(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }

    static public List<HSAILInstruction> ret_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new ret<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }

    static public List<HSAILInstruction> ret_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new ret<StackReg_f64, f64>(_hsailStackFrame, _i, new StackReg_f64(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> branch(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
       add(_instructions, new branch(_hsailStackFrame, _i, new StackReg_s32(_hsailStackFrame.stackIdx(_i)), _i.getByteCode().getName(), _i.asBranch().getAbsolute()));
        return(_instructions);
    }
    static public List<HSAILInstruction> brn(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
       add(_instructions, new brn(_hsailStackFrame, _i, _i.asBranch().getAbsolute()));
        return(_instructions);
    }
    static public List<HSAILInstruction> cbr(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
       add(_instructions, new cbr(_hsailStackFrame, _i, _i.asBranch().getAbsolute()));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_ref_ne(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
       add(_instructions, new cmp_ref(_hsailStackFrame, _i, "ne", new StackReg_ref(_hsailStackFrame.stackIdx(_i)), new StackReg_ref(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_ref_eq(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cmp_ref(_hsailStackFrame, _i, "eq", new StackReg_ref(_hsailStackFrame.stackIdx(_i)), new StackReg_ref(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s32_ne(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cmp_s32(_hsailStackFrame, _i, "ne", new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_s32_eq(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cmp_s32(_hsailStackFrame, _i, "eq", new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_s32_lt(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cmp_s32(_hsailStackFrame, _i, "lt", new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_s32_gt(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cmp_s32(_hsailStackFrame, _i, "gt", new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_s32_ge(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cmp_s32(_hsailStackFrame, _i, "ge", new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_s32_le(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cmp_s32(_hsailStackFrame, _i, "le", new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_s32_le_const_0(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
       add(_instructions, new cmp_s32_const_0(_hsailStackFrame, _i, "le", new StackReg_s32(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_s32_gt_const_0(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cmp_s32_const_0(_hsailStackFrame, _i, "gt", new StackReg_s32(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s32_ge_const_0(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cmp_s32_const_0(_hsailStackFrame, _i, "ge", new StackReg_s32(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s32_lt_const_0(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cmp_s32_const_0(_hsailStackFrame, _i, "lt", new StackReg_s32(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s32_eq_const_0(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cmp_s32_const_0(_hsailStackFrame, _i, "eq", new StackReg_s32(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s32_ne_const_0(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cmp_s32_const_0(_hsailStackFrame, _i, "ne", new StackReg_s32(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_s64_le(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
       Instruction lastInstruction = _i.getPrevPC();
       add(_instructions, new cmp<StackReg_s64, s64>(_hsailStackFrame, lastInstruction, "le", new StackReg_s64(_hsailStackFrame.stackIdx(lastInstruction)), new StackReg_s64(_hsailStackFrame.stackIdx(lastInstruction)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s64_ge(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add(_instructions, new cmp<StackReg_s64, s64>(_hsailStackFrame, lastInstruction, "ge", new StackReg_s64(_hsailStackFrame.stackIdx(lastInstruction)), new StackReg_s64(_hsailStackFrame.stackIdx(lastInstruction)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s64_gt(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add(_instructions, new cmp<StackReg_s64, s64>(_hsailStackFrame, lastInstruction, "gt", new StackReg_s64(_hsailStackFrame.stackIdx(lastInstruction)), new StackReg_s64(_hsailStackFrame.stackIdx(lastInstruction)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s64_lt(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add(_instructions, new cmp<StackReg_s64, s64>(_hsailStackFrame, lastInstruction, "lt", new StackReg_s64(_hsailStackFrame.stackIdx(lastInstruction)), new StackReg_s64(_hsailStackFrame.stackIdx(lastInstruction)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s64_eq(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add(_instructions, new cmp<StackReg_s64, s64>(_hsailStackFrame, lastInstruction, "eq", new StackReg_s64(_hsailStackFrame.stackIdx(lastInstruction)), new StackReg_s64(_hsailStackFrame.stackIdx(lastInstruction)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s64_ne(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add(_instructions, new cmp<StackReg_s64, s64>(_hsailStackFrame, lastInstruction, "ne", new StackReg_s64(_hsailStackFrame.stackIdx(lastInstruction)), new StackReg_s64(_hsailStackFrame.stackIdx(lastInstruction)+1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_f64_le(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add(_instructions, new cmp<StackReg_f64, f64>(_hsailStackFrame, lastInstruction, "le", new StackReg_f64(_hsailStackFrame.stackIdx(lastInstruction)), new StackReg_f64(_hsailStackFrame.stackIdx(lastInstruction)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f64_ge(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add(_instructions, new cmp<StackReg_f64, f64>(_hsailStackFrame, lastInstruction, "ge", new StackReg_f64(_hsailStackFrame.stackIdx(lastInstruction)), new StackReg_f64(_hsailStackFrame.stackIdx(lastInstruction)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f64_lt(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add(_instructions, new cmp<StackReg_f64, f64>(_hsailStackFrame, lastInstruction, "lt", new StackReg_f64(_hsailStackFrame.stackIdx(lastInstruction)), new StackReg_f64(_hsailStackFrame.stackIdx(lastInstruction)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f64_gt(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add(_instructions, new cmp<StackReg_f64, f64>(_hsailStackFrame, lastInstruction, "gt", new StackReg_f64(_hsailStackFrame.stackIdx(lastInstruction)), new StackReg_f64(_hsailStackFrame.stackIdx(lastInstruction)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f64_eq(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add(_instructions, new cmp<StackReg_f64, f64>(_hsailStackFrame, lastInstruction, "eq", new StackReg_f64(_hsailStackFrame.stackIdx(lastInstruction)), new StackReg_f64(_hsailStackFrame.stackIdx(lastInstruction)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f64_ne(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add(_instructions, new cmp<StackReg_f64, f64>(_hsailStackFrame, lastInstruction, "ne", new StackReg_f64(_hsailStackFrame.stackIdx(lastInstruction)), new StackReg_f64(_hsailStackFrame.stackIdx(lastInstruction)+1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_f32_le(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add(_instructions, new cmp<StackReg_f32, f32>(_hsailStackFrame, lastInstruction, "le", new StackReg_f32(_hsailStackFrame.stackIdx(lastInstruction)), new StackReg_f32(_hsailStackFrame.stackIdx(lastInstruction)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f32_ge(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add(_instructions, new cmp<StackReg_f32, f32>(_hsailStackFrame, lastInstruction, "ge", new StackReg_f32(_hsailStackFrame.stackIdx(lastInstruction)), new StackReg_f32(_hsailStackFrame.stackIdx(lastInstruction)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f32_lt(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add(_instructions, new cmp<StackReg_f32, f32>(_hsailStackFrame, lastInstruction, "lt", new StackReg_f32(_hsailStackFrame.stackIdx(lastInstruction)), new StackReg_f32(_hsailStackFrame.stackIdx(lastInstruction)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f32_gt(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add(_instructions, new cmp<StackReg_f32, f32>(_hsailStackFrame, lastInstruction, "gt", new StackReg_f32(_hsailStackFrame.stackIdx(lastInstruction)), new StackReg_f32(_hsailStackFrame.stackIdx(lastInstruction)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f32_eq(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add(_instructions, new cmp<StackReg_f32, f32>(_hsailStackFrame, lastInstruction, "eq", new StackReg_f32(_hsailStackFrame.stackIdx(lastInstruction)), new StackReg_f32(_hsailStackFrame.stackIdx(lastInstruction)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f32_ne(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add(_instructions, new cmp<StackReg_f32, f32>(_hsailStackFrame, lastInstruction, "ne", new StackReg_f32(_hsailStackFrame.stackIdx(lastInstruction)), new StackReg_f32(_hsailStackFrame.stackIdx(lastInstruction)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_s8_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cvt<StackReg_s8, StackReg_s32, s8, s32>(_hsailStackFrame, _i, new StackReg_s8(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_s16_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cvt<StackReg_s16, StackReg_s32, s16, s32>(_hsailStackFrame, _i, new StackReg_s16(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_u16_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cvt<StackReg_u16, StackReg_s32, u16, s32>(_hsailStackFrame, _i, new StackReg_u16(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_f32_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cvt<StackReg_f32, StackReg_s32, f32, s32>(_hsailStackFrame, _i, new StackReg_f32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_s64_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cvt<StackReg_s64, StackReg_s32, s64, s32>(_hsailStackFrame, _i, new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_f64_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cvt<StackReg_f64, StackReg_s32, f64, s32>(_hsailStackFrame, _i, new StackReg_f64(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_ref_s32_1(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cvt<StackReg_ref, StackReg_s32, ref, s32>(_hsailStackFrame, _i, new StackReg_ref(_hsailStackFrame.stackIdx(_i)+1), new StackReg_s32(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_ref_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cvt<StackReg_ref, StackReg_s32, ref, s32>(_hsailStackFrame, _i, new StackReg_ref(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_s32_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cvt<StackReg_s32, StackReg_s64, s32, s64>(_hsailStackFrame, _i, new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s64(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_f32_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cvt<StackReg_f32, StackReg_s64, f32, s64>(_hsailStackFrame, _i, new StackReg_f32(_hsailStackFrame.stackIdx(_i)), new StackReg_s64(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_f64_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cvt<StackReg_f64, StackReg_s64, f64, s64>(_hsailStackFrame, _i, new StackReg_f64(_hsailStackFrame.stackIdx(_i)), new StackReg_s64(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }

    static public List<HSAILInstruction> cvt_s32_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cvt<StackReg_s32, StackReg_f32, s32, f32>(_hsailStackFrame, _i, new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_f32(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_f64_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cvt<StackReg_f64, StackReg_f32, f64, f32>(_hsailStackFrame, _i, new StackReg_f64(_hsailStackFrame.stackIdx(_i)), new StackReg_f32(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_s64_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cvt<StackReg_s64, StackReg_f32, s64, f32>(_hsailStackFrame, _i, new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_f32(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_s32_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cvt<StackReg_s32, StackReg_f64, s32, f64>(_hsailStackFrame, _i, new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_f64(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_f32_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cvt<StackReg_f32, StackReg_f64, f32, f64>(_hsailStackFrame, _i, new StackReg_f32(_hsailStackFrame.stackIdx(_i)), new StackReg_f64(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_s64_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new cvt<StackReg_s64, StackReg_f64, s64, f64>(_hsailStackFrame, _i, new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_f64(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> add_const_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new add_const<VarReg_s32, s32, Integer>(_hsailStackFrame, _i, new VarReg_s32(_i, _hsailStackFrame.stackOffset), new VarReg_s32(_i, _hsailStackFrame.stackOffset), ((InstructionSet.I_IINC) _i).getDelta()));
        return(_instructions);
    }
    static public List<HSAILInstruction> xor_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new xor<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_s64(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> xor_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new xor<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> or_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new or<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_s64(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> or_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new or<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> and_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new and<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_s64(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> and_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new and<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ushr_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new ushr<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_s64(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ushr_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new ushr<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> shr_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new shr<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_s64(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> shr_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new shr<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> shl_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new shl<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_s64(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> shl_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new shl<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> neg_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new neg<StackReg_f64, f64>(_hsailStackFrame, _i, new StackReg_f64(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> neg_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new neg<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> neg_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new neg<StackReg_f32, f32>(_hsailStackFrame, _i, new StackReg_f32(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> neg_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new neg<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> rem_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new rem<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_s64(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> rem_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new rem<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> rem_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new rem<StackReg_f64, f64>(_hsailStackFrame, _i, new StackReg_f64(_hsailStackFrame.stackIdx(_i)), new StackReg_f64(_hsailStackFrame.stackIdx(_i)), new StackReg_f64(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> rem_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new rem<StackReg_f32, f32>(_hsailStackFrame, _i, new StackReg_f32(_hsailStackFrame.stackIdx(_i)), new StackReg_f32(_hsailStackFrame.stackIdx(_i)), new StackReg_f32(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> div_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new div<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_s64(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> div_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new div<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> div_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new div<StackReg_f64, f64>(_hsailStackFrame, _i, new StackReg_f64(_hsailStackFrame.stackIdx(_i)), new StackReg_f64(_hsailStackFrame.stackIdx(_i)), new StackReg_f64(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> div_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new div<StackReg_f32, f32>(_hsailStackFrame, _i, new StackReg_f32(_hsailStackFrame.stackIdx(_i)), new StackReg_f32(_hsailStackFrame.stackIdx(_i)), new StackReg_f32(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mul_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new mul<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_s64(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mul_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new mul<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mul_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new mul<StackReg_f64, f64>(_hsailStackFrame, _i, new StackReg_f64(_hsailStackFrame.stackIdx(_i)), new StackReg_f64(_hsailStackFrame.stackIdx(_i)), new StackReg_f64(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mul_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new mul<StackReg_f32, f32>(_hsailStackFrame, _i, new StackReg_f32(_hsailStackFrame.stackIdx(_i)), new StackReg_f32(_hsailStackFrame.stackIdx(_i)), new StackReg_f32(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> sub_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new sub<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_s64(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> sub_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new sub<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> sub_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new sub<StackReg_f64, f64>(_hsailStackFrame, _i, new StackReg_f64(_hsailStackFrame.stackIdx(_i)), new StackReg_f64(_hsailStackFrame.stackIdx(_i)), new StackReg_f64(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> sub_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new sub<StackReg_f32, f32>(_hsailStackFrame, _i, new StackReg_f32(_hsailStackFrame.stackIdx(_i)), new StackReg_f32(_hsailStackFrame.stackIdx(_i)), new StackReg_f32(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> add_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new add<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_s64(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> add_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new add<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_s32(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> add_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new add<StackReg_f64, f64>(_hsailStackFrame, _i, new StackReg_f64(_hsailStackFrame.stackIdx(_i)), new StackReg_f64(_hsailStackFrame.stackIdx(_i)), new StackReg_f64(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> add_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new add<StackReg_f32, f32>(_hsailStackFrame, _i, new StackReg_f32(_hsailStackFrame.stackIdx(_i)), new StackReg_f32(_hsailStackFrame.stackIdx(_i)), new StackReg_f32(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_store_s16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new array_store<StackReg_s16, s16>(_hsailStackFrame, _i, new StackReg_ref(_hsailStackFrame.stackIdx(_i)+1), new StackReg_s16(_hsailStackFrame.stackIdx(_i)+2)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_store_u16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new array_store<StackReg_u16, u16>(_hsailStackFrame, _i, new StackReg_ref(_hsailStackFrame.stackIdx(_i)+1), new StackReg_u16(_hsailStackFrame.stackIdx(_i)+2)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_store_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new array_store<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_ref(_hsailStackFrame.stackIdx(_i)+1), new StackReg_s32(_hsailStackFrame.stackIdx(_i)+2)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_store_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new array_store<StackReg_f32, f32>(_hsailStackFrame, _i, new StackReg_ref(_hsailStackFrame.stackIdx(_i)+1), new StackReg_f32(_hsailStackFrame.stackIdx(_i)+2)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_store_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new array_store<StackReg_f64, f64>(_hsailStackFrame, _i, new StackReg_ref(_hsailStackFrame.stackIdx(_i)+1), new StackReg_f64(_hsailStackFrame.stackIdx(_i)+2)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_store_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new array_store<StackReg_ref, ref>(_hsailStackFrame, _i, new StackReg_ref(_hsailStackFrame.stackIdx(_i)+1), new StackReg_ref(_hsailStackFrame.stackIdx(_i)+2)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_store_s8(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new array_store<StackReg_s8, s8>(_hsailStackFrame, _i, new StackReg_ref(_hsailStackFrame.stackIdx(_i)+1), new StackReg_s8(_hsailStackFrame.stackIdx(_i)+2)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_store_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new array_store<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_ref(_hsailStackFrame.stackIdx(_i)+1), new StackReg_s64(_hsailStackFrame.stackIdx(_i)+2)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mad(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _size){
       add(_instructions, new mad(_hsailStackFrame, _i, new StackReg_ref(_hsailStackFrame.stackIdx(_i)+1), new StackReg_ref(_hsailStackFrame.stackIdx(_i)+1), new StackReg_ref(_hsailStackFrame.stackIdx(_i)), (long) _size));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_var_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new mov<VarReg_ref, StackReg_ref, ref, ref>(_hsailStackFrame, _i, new VarReg_ref(_i, _hsailStackFrame.stackOffset), new StackReg_ref(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_var_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new mov<VarReg_s32, StackReg_s32, s32, s32>(_hsailStackFrame, _i, new VarReg_s32(_i, _hsailStackFrame.stackOffset), new StackReg_s32(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_var_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new mov<VarReg_f32, StackReg_f32, f32, f32>(_hsailStackFrame, _i, new VarReg_f32(_i, _hsailStackFrame.stackOffset), new StackReg_f32(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_var_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new mov<VarReg_f64, StackReg_f64, f64, f64>(_hsailStackFrame, _i, new VarReg_f64(_i, _hsailStackFrame.stackOffset), new StackReg_f64(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_var_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new mov<VarReg_s64, StackReg_s64, s64, s64>(_hsailStackFrame, _i, new VarReg_s64(_i, _hsailStackFrame.stackOffset), new StackReg_s64(_hsailStackFrame.stackIdx(_i))));
        return(_instructions);
    }

    static public List<HSAILInstruction> array_load_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new array_load<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new StackReg_ref(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_load_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new array_load<StackReg_f32, f32>(_hsailStackFrame, _i, new StackReg_f32(_hsailStackFrame.stackIdx(_i)), new StackReg_ref(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_load_u16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new array_load<StackReg_u16, u16>(_hsailStackFrame, _i, new StackReg_u16(_hsailStackFrame.stackIdx(_i)), new StackReg_ref(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_load_s16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new array_load<StackReg_s16, s16>(_hsailStackFrame, _i, new StackReg_s16(_hsailStackFrame.stackIdx(_i)), new StackReg_ref(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_load_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new array_load<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new StackReg_ref(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_load_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new array_load<StackReg_f64, f64>(_hsailStackFrame, _i, new StackReg_f64(_hsailStackFrame.stackIdx(_i)), new StackReg_ref(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> array_load_s8(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new array_load<StackReg_s8, s8>(_hsailStackFrame, _i, new StackReg_s8(_hsailStackFrame.stackIdx(_i)), new StackReg_ref(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_load_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new array_load<StackReg_ref, ref>(_hsailStackFrame, _i, new StackReg_ref(_hsailStackFrame.stackIdx(_i)), new StackReg_ref(_hsailStackFrame.stackIdx(_i)+1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_f64_var(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new mov<StackReg_f64, VarReg_f64, f64, f64>(_hsailStackFrame, _i, new StackReg_f64(_hsailStackFrame.stackIdx(_i)), new VarReg_f64(_i, _hsailStackFrame.stackOffset)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_f32_var(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new mov<StackReg_f32, VarReg_f32, f32, f32>(_hsailStackFrame, _i, new StackReg_f32(_hsailStackFrame.stackIdx(_i)), new VarReg_f32(_i, _hsailStackFrame.stackOffset)));
        return(_instructions);
    }

    static public List<HSAILInstruction> mov_s64_var(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new mov<StackReg_s64, VarReg_s64, s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_hsailStackFrame.stackIdx(_i)), new VarReg_s64(_i, _hsailStackFrame.stackOffset)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_s32_var(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new mov<StackReg_s32, VarReg_s32, s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_hsailStackFrame.stackIdx(_i)), new VarReg_s32(_i, _hsailStackFrame.stackOffset)));
        return(_instructions);
    }


    static public List<HSAILInstruction> mov_ref_var(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        add(_instructions, new mov<StackReg_ref, VarReg_ref, ref, ref>(_hsailStackFrame, _i, new StackReg_ref(_hsailStackFrame.stackIdx(_i)), new VarReg_ref(_i, _hsailStackFrame.stackOffset)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_s64_const(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, long _value){
        add(_instructions, new mov_const<StackReg_s64, s64, Long>(_hsailStackFrame, _i, new StackReg_s64(_hsailStackFrame.stackIdx(_i)), _value));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_s32_const(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _value){
        add(_instructions, new mov_const<StackReg_s32, s32, Integer>(_hsailStackFrame, _i, new StackReg_s32(_hsailStackFrame.stackIdx(_i)), _value));
        return(_instructions);
    }

    static public List<HSAILInstruction> mov_f64_const(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, double _value){
        add(_instructions, new mov_const<StackReg_f64, f64, Double>(_hsailStackFrame, _i, new StackReg_f64(_hsailStackFrame.stackIdx(_i)), _value));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_f32_const(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, float _value){
        add(_instructions, new mov_const<StackReg_f32, f32, Float>(_hsailStackFrame, _i, new StackReg_f32(_hsailStackFrame.stackIdx(_i)), _value));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_arg_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        add(_instructions, new ld_arg(_hsailStackFrame, _i, new VarReg_ref(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_kernarg_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        add(_instructions, new ld_kernarg(_hsailStackFrame, _i, new VarReg_ref(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_arg_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        add(_instructions, new ld_arg(_hsailStackFrame, _i, new VarReg_s32(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_kernarg_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        add(_instructions, new ld_kernarg(_hsailStackFrame, _i, new VarReg_s32(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_arg_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        add(_instructions, new ld_arg(_hsailStackFrame, _i, new VarReg_f32(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_kernarg_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        add(_instructions, new ld_kernarg(_hsailStackFrame, _i, new VarReg_f32(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_arg_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        add(_instructions, new ld_arg(_hsailStackFrame, _i, new VarReg_f64(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_kernarg_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        add(_instructions, new ld_kernarg(_hsailStackFrame, _i, new VarReg_f64(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_arg_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        add(_instructions, new ld_arg(_hsailStackFrame, _i, new VarReg_s64(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_kernarg_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        add(_instructions, new ld_kernarg(_hsailStackFrame, _i, new VarReg_s64(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> workitemabsid_u32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        add(_instructions, new workitemabsid(_hsailStackFrame, _i, new VarReg_s32(_argNum)));
        return(_instructions);
    }
    static public void addmov(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, PrimitiveType _type, int _from, int _to) {
        if (_type.equals(PrimitiveType.ref) || _type.getHsaBits() == 32) {
            if (_type.equals(PrimitiveType.ref)) {
                add(_instructions, new mov(_hsailStackFrame, _i, new StackReg_ref(_hsailStackFrame.stackIdx(_i)+_to), new StackReg_ref(_hsailStackFrame.stackIdx(_i)+_from)));
            } else if (_type.equals(PrimitiveType.s32)) {
                add(_instructions, new mov(_hsailStackFrame, _i, new StackReg_s32(_hsailStackFrame.stackIdx(_i)+_to), new StackReg_s32(_hsailStackFrame.stackIdx(_i)+_from)));
            } else if (_type.equals(PrimitiveType.f32)) {
                add(_instructions, new mov(_hsailStackFrame, _i, new StackReg_f32(_hsailStackFrame.stackIdx(_i)+_to), new StackReg_f32(_hsailStackFrame.stackIdx(_i)+_from)));

            } else {
                throw new IllegalStateException(" unknown prefix 1 prefix for first of DUP2");
            }

        } else {
            throw new IllegalStateException(" unknown prefix 2 prefix for DUP2");
        }
    }
    static public HSAILRegister getRegOfLastWriteToIndex(List<HSAILInstruction>_instructions,int _index) {

        int idx = _instructions.size();
        while (--idx >= 0) {
            HSAILInstruction i = _instructions.get(idx);
            if (i.dests != null) {
                for (HSAILRegister d : i.dests) {
                    if (d.index == _index) {
                        return (d);
                    }
                }
            }
        }


        return (null);
    }
    static public HSAILRegister addmov(List<HSAILInstruction>_instructions, HSAILStackFrame _hsailStackFrame, Instruction _i, int _from, int _to) {
        HSAILRegister r = getRegOfLastWriteToIndex(_instructions, _i.getPreStackBase() + _i.getMethod().getCodeEntry().getMaxLocals() + _from);
        if (r == null){
            System.out.println("damn!");
        }
        addmov(_instructions, _hsailStackFrame, _i, r.type, _from, _to);
        return (r);
    }

    static boolean compressMovs = false;
    static public void add(List<HSAILInstruction> _instructions, HSAILInstruction _instruction) {
        if (compressMovs){
            // before we add lets see if this is a redundant mov
            for (int srcIndex = 0; srcIndex < _instruction.sources.length; srcIndex++) {
                HSAILOperand source = _instruction.sources[srcIndex];
                if (source instanceof StackReg) {
                    // look up the list of reg instructions for the instruction which assigns to this
                    int i = _instructions.size();
                    while ((--i) >= 0) {
                        if (_instructions.get(i) instanceof mov) {
                            // we have found a move
                            mov candidateForRemoval = (mov) _instructions.get(i);
                            if (candidateForRemoval.from.getBlock() == _instruction.from.getBlock()
                                    && (candidateForRemoval.getDest() instanceof StackReg) && candidateForRemoval.getDest().equals(source)) {
                                // so i may be a candidate if between i and instruction.size() i.dest() is not mutated
                                boolean mutated = false;
                                for (int x = i + 1; !mutated && x < _instructions.size(); x++) {
                                    if (_instructions.get(x).dests.length > 0 && _instructions.get(x).dests[0].equals(candidateForRemoval.getSrc())) {
                                        mutated = true;
                                    }
                                }
                                if (!mutated) {
                                    _instructions.remove(i);
                                    _instruction.sources[srcIndex] = candidateForRemoval.getSrc();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        _instructions.add(_instruction);
    }

   enum ParseState {NONE, COMPARE_F32, COMPARE_F64, COMPARE_S64}
   ;
    



   public static void addInstructions(List<HSAILInstruction> instructions, List<HSAILStackFrame> _frameSet, Stack<HSAILStackFrame> _frames, ClassModel.ClassModelMethod  method){
      HSAILStackFrame hsailStackFrame = _frames.peek();
      ParseState parseState = ParseState.NONE;
      boolean inlining = true;
      boolean needsReturnLabel = false;
      for (Instruction i : method.getInstructions()) {

         switch (i.getByteCode()) {

            case ACONST_NULL:
                add(instructions, new nyi(hsailStackFrame, i));
               break;
            case ICONST_M1:
            case ICONST_0:
            case ICONST_1:
            case ICONST_2:
            case ICONST_3:
            case ICONST_4:
            case ICONST_5:
            case BIPUSH:
            case SIPUSH:
               mov_s32_const(instructions, hsailStackFrame, i,  i.asIntegerConstant().getValue());
               break;
            case LCONST_0:
            case LCONST_1:
               mov_s64_const(instructions, hsailStackFrame, i, i.asLongConstant().getValue());
               break;
            case FCONST_0:
            case FCONST_1:
            case FCONST_2:
               mov_f32_const(instructions, hsailStackFrame, i, i.asFloatConstant().getValue());
               break;
            case DCONST_0:
            case DCONST_1:
               mov_f64_const(instructions, hsailStackFrame, i, i.asDoubleConstant().getValue());

               break;
            // case BIPUSH: moved up
            // case SIPUSH: moved up

            case LDC:
            case LDC_W:
            case LDC2_W: {
               InstructionSet.ConstantPoolEntryConstant cpe = (InstructionSet.ConstantPoolEntryConstant) i;

               ClassModel.ConstantPool.ConstantEntry e = (ClassModel.ConstantPool.ConstantEntry) cpe.getConstantPoolEntry();
               if (e instanceof ClassModel.ConstantPool.DoubleEntry) {
                  mov_f64_const(instructions, hsailStackFrame, i, ((ClassModel.ConstantPool.DoubleEntry) e).getValue());
               } else if (e instanceof ClassModel.ConstantPool.FloatEntry) {
                  mov_f32_const(instructions, hsailStackFrame, i, ((ClassModel.ConstantPool.FloatEntry) e).getValue());
               } else if (e instanceof ClassModel.ConstantPool.IntegerEntry) {
                  mov_s32_const(instructions, hsailStackFrame, i, ((ClassModel.ConstantPool.IntegerEntry) e).getValue());
               } else if (e instanceof ClassModel.ConstantPool.LongEntry) {
                  mov_s64_const(instructions, hsailStackFrame, i, ((ClassModel.ConstantPool.LongEntry) e).getValue());
               }

            }
            break;
            // case LLOAD: moved down
            // case FLOAD: moved down
            // case DLOAD: moved down
            //case ALOAD: moved down
            case ILOAD:
            case ILOAD_0:
            case ILOAD_1:
            case ILOAD_2:
            case ILOAD_3:
               mov_s32_var(instructions, hsailStackFrame, i);

               break;
            case LLOAD:
            case LLOAD_0:
            case LLOAD_1:
            case LLOAD_2:
            case LLOAD_3:
               mov_s64_var(instructions, hsailStackFrame, i);
               break;
            case FLOAD:
            case FLOAD_0:
            case FLOAD_1:
            case FLOAD_2:
            case FLOAD_3:

               mov_f32_var(instructions, hsailStackFrame, i);
               break;
            case DLOAD:
            case DLOAD_0:
            case DLOAD_1:
            case DLOAD_2:
            case DLOAD_3:

               mov_f64_var(instructions, hsailStackFrame, i);
               break;
            case ALOAD:
            case ALOAD_0:
            case ALOAD_1:
            case ALOAD_2:
            case ALOAD_3:
               mov_ref_var(instructions, hsailStackFrame, i);

               break;
            case IALOAD:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.s32.getHsaBytes());
               array_load_s32(instructions, hsailStackFrame, i);
               break;
            case LALOAD:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.s64.getHsaBytes());
               array_load_s64(instructions, hsailStackFrame, i);
               break;
            case FALOAD:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.f32.getHsaBytes());
               array_load_f32(instructions, hsailStackFrame, i);
               break;
            case DALOAD:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.f64.getHsaBytes());
               array_load_f64(instructions, hsailStackFrame, i);
               break;
            case AALOAD:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.ref.getHsaBytes());
               array_load_ref(instructions, hsailStackFrame, i);
               break;
            case BALOAD:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.s8.getHsaBytes());
               array_load_s8(instructions, hsailStackFrame, i);
               break;
            case CALOAD:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.u16.getHsaBytes());
               array_load_u16(instructions, hsailStackFrame, i);
               break;
            case SALOAD:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.s32.getHsaBytes());
               array_load_s16(instructions, hsailStackFrame, i);
               break;
            //case ISTORE: moved down
            // case LSTORE:  moved down
            //case FSTORE: moved down
            //case DSTORE:  moved down
            // case ASTORE: moved down
            case ISTORE:
            case ISTORE_0:
            case ISTORE_1:
            case ISTORE_2:
            case ISTORE_3:
               mov_var_s32(instructions, hsailStackFrame, i);

               break;
            case LSTORE:
            case LSTORE_0:
            case LSTORE_1:
            case LSTORE_2:
            case LSTORE_3:
               mov_var_s64(instructions, hsailStackFrame, i);

               break;
            case FSTORE:
            case FSTORE_0:
            case FSTORE_1:
            case FSTORE_2:
            case FSTORE_3:
               mov_var_f32(instructions, hsailStackFrame, i);
               break;
            case DSTORE:
            case DSTORE_0:
            case DSTORE_1:
            case DSTORE_2:
            case DSTORE_3:
               mov_var_f64(instructions, hsailStackFrame, i);
               break;
            case ASTORE:
            case ASTORE_0:
            case ASTORE_1:
            case ASTORE_2:
            case ASTORE_3:
               mov_var_ref(instructions, hsailStackFrame, i);
               break;
            case IASTORE:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.s32.getHsaBytes());
               array_store_s32(instructions, hsailStackFrame, i);
               break;
            case LASTORE:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.s64.getHsaBytes());
               array_store_s64(instructions, hsailStackFrame, i);
               break;
            case FASTORE:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.f32.getHsaBytes());
               array_store_f32(instructions, hsailStackFrame, i);
               break;
            case DASTORE:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.f64.getHsaBytes());
               array_store_f64(instructions, hsailStackFrame, i);
               break;
            case AASTORE:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.ref.getHsaBytes());
               array_store_ref(instructions, hsailStackFrame, i);
               break;
            case BASTORE:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.s8.getHsaBytes());
               array_store_s8(instructions, hsailStackFrame, i);
               break;
            case CASTORE:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.u16.getHsaBytes());
               array_store_u16(instructions, hsailStackFrame, i);
               break;
            case SASTORE:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.s16.getHsaBytes());
               array_store_s16(instructions, hsailStackFrame, i);
               break;
            case POP:
                add(instructions, new nyi(hsailStackFrame, i));
               break;
            case POP2:
                add(instructions, new nyi(hsailStackFrame, i));
               break;
            case DUP:
               // add(new nyi(i));
               addmov(instructions, hsailStackFrame, i, 0, 1);
               break;
            case DUP_X1:
                add(instructions, new nyi(hsailStackFrame, i));
               break;
            case DUP_X2:

               addmov(instructions, hsailStackFrame, i, 2, 3);
               addmov(instructions, hsailStackFrame, i, 1, 2);
               addmov(instructions, hsailStackFrame, i, 0, 1);
               addmov(instructions, hsailStackFrame, i, 3, 0);

               break;
            case DUP2:
               // DUP2 is problematic. DUP2 either dups top two items or one depending on the 'prefix' of the stack items.
               // To complicate this further HSA large model wants object/mem references to be 64 bits (prefix 2 in Java) whereas
               // in java object/array refs are 32 bits (prefix 1).
               addmov(instructions, hsailStackFrame, i, 0, 2);
               addmov(instructions, hsailStackFrame, i, 1, 3);
               break;
            case DUP2_X1:
                add(instructions, new nyi(hsailStackFrame, i));
               break;
            case DUP2_X2:
                add(instructions, new nyi(hsailStackFrame, i));
               break;
            case SWAP:
                add(instructions, new nyi(hsailStackFrame, i));
               break;
            case IADD:
               add_s32(instructions, hsailStackFrame, i);
               break;
            case LADD:
               add_s64(instructions, hsailStackFrame, i);
               break;
            case FADD:
               add_f32(instructions, hsailStackFrame, i);
               break;
            case DADD:
               add_f64(instructions, hsailStackFrame, i);
               break;
            case ISUB:
               sub_s32(instructions, hsailStackFrame, i);
               break;
            case LSUB:
               sub_s64(instructions, hsailStackFrame, i);
               break;
            case FSUB:
               sub_f32(instructions, hsailStackFrame, i);
               break;
            case DSUB:
               sub_f64(instructions, hsailStackFrame, i);
               break;
            case IMUL:
               mul_s32(instructions, hsailStackFrame, i);
               break;
            case LMUL:
               mul_s64(instructions, hsailStackFrame, i);
               break;
            case FMUL:
               mul_f32(instructions, hsailStackFrame, i);
               break;
            case DMUL:
               mul_f64(instructions, hsailStackFrame, i);
               break;
            case IDIV:
               div_s32(instructions, hsailStackFrame, i);
               break;
            case LDIV:
               div_s64(instructions, hsailStackFrame, i);
               break;
            case FDIV:
               div_f32(instructions, hsailStackFrame, i);
               break;
            case DDIV:
               div_f64(instructions, hsailStackFrame, i);
               break;
            case IREM:
               rem_s32(instructions, hsailStackFrame, i);
               break;
            case LREM:
               rem_s64(instructions, hsailStackFrame, i);
               break;
            case FREM:
               rem_f32(instructions, hsailStackFrame, i);
               break;
            case DREM:
               rem_f64(instructions, hsailStackFrame, i);
               break;
            case INEG:
               neg_s32(instructions, hsailStackFrame, i);
               break;
            case LNEG:
               neg_s64(instructions, hsailStackFrame, i);
               break;
            case FNEG:
               neg_f32(instructions, hsailStackFrame, i);
               break;
            case DNEG:
               neg_f64(instructions, hsailStackFrame, i);
               break;
            case ISHL:
               shl_s32(instructions, hsailStackFrame, i);
               break;
            case LSHL:
               shl_s64(instructions, hsailStackFrame, i);
               break;
            case ISHR:
               shr_s32(instructions, hsailStackFrame, i);
               break;
            case LSHR:
               shr_s64(instructions, hsailStackFrame, i);
               break;
            case IUSHR:
               ushr_s32(instructions, hsailStackFrame, i);
               break;
            case LUSHR:
               ushr_s64(instructions, hsailStackFrame, i);
               break;
            case IAND:
               and_s32(instructions, hsailStackFrame, i);
               break;
            case LAND:
               and_s64(instructions, hsailStackFrame, i);
               break;
            case IOR:
               or_s32(instructions, hsailStackFrame, i);
               break;
            case LOR:
               or_s64(instructions, hsailStackFrame, i);
               break;
            case IXOR:
               xor_s32(instructions, hsailStackFrame, i);
               break;
            case LXOR:
               xor_s64(instructions, hsailStackFrame, i);
               break;
            case IINC:
               add_const_s32(instructions, hsailStackFrame, i);
               break;
            case I2L:
               cvt_s64_s32(instructions, hsailStackFrame, i);
               break;
            case I2F:
               cvt_f32_s32(instructions, hsailStackFrame, i);
               break;
            case I2D:
               cvt_f64_s32(instructions, hsailStackFrame, i);
               break;
            case L2I:
               cvt_s32_s64(instructions, hsailStackFrame, i);
               break;
            case L2F:
               cvt_f32_s64(instructions, hsailStackFrame, i);
               break;
            case L2D:
               cvt_f64_s64(instructions, hsailStackFrame, i);
               break;
            case F2I:
               cvt_s32_f32(instructions, hsailStackFrame, i);
               break;
            case F2L:
               cvt_s64_f32(instructions, hsailStackFrame, i);
               break;
            case F2D:
               cvt_f64_f32(instructions, hsailStackFrame, i);
               break;
            case D2I:
               cvt_s32_f64(instructions, hsailStackFrame, i);
               break;
            case D2L:
               cvt_s64_f64(instructions, hsailStackFrame, i);
               break;
            case D2F:
               cvt_f32_f64(instructions, hsailStackFrame, i);
               break;
            case I2B:
               cvt_s8_s32(instructions, hsailStackFrame, i);
               break;
            case I2C:
               cvt_u16_s32(instructions, hsailStackFrame, i);
               break;
            case I2S:
               cvt_s16_s32(instructions, hsailStackFrame, i);
               break;
            case LCMP:
               parseState = ParseState.COMPARE_S64;
               break;
            case FCMPL:
               parseState = ParseState.COMPARE_F32;
               break;
            case FCMPG:
               parseState = ParseState.COMPARE_F32;
               break;
            case DCMPL:
               parseState = ParseState.COMPARE_F64;
               break;
            case DCMPG:
               parseState = ParseState.COMPARE_F64;
               break;
            case IFEQ:
               if (parseState.equals(ParseState.COMPARE_F32)) {
                  cmp_f32_eq(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_F64)) {
                  cmp_f64_eq(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_S64)) {
                  cmp_s64_eq(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else {
                  cmp_s32_eq_const_0(instructions, hsailStackFrame, i);
               }
               cbr(instructions, hsailStackFrame, i);
               break;
            case IFNE:
               if (parseState.equals(ParseState.COMPARE_F32)) {
                  cmp_f32_ne(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_F64)) {
                  cmp_f64_ne(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_S64)) {
                  cmp_s64_ne(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else {
                  cmp_s32_ne_const_0(instructions, hsailStackFrame, i);
               }
               cbr(instructions, hsailStackFrame, i);
               break;
            case IFLT:
               if (parseState.equals(ParseState.COMPARE_F32)) {
                  cmp_f32_lt(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_F64)) {
                  cmp_f64_lt(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_S64)) {
                  cmp_s64_lt(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else {
                  cmp_s32_lt_const_0(instructions, hsailStackFrame, i);

               }
               cbr(instructions, hsailStackFrame, i);
               break;
            case IFGE:
               if (parseState.equals(ParseState.COMPARE_F32)) {
                  cmp_f32_ge(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_F64)) {
                  cmp_f64_ge(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_S64)) {
                  cmp_s64_ge(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else {
                  cmp_s32_ge_const_0(instructions, hsailStackFrame, i);

               }
               cbr(instructions, hsailStackFrame, i);
               break;
            case IFGT:
               if (parseState.equals(ParseState.COMPARE_F32)) {
                  cmp_f32_gt(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_F64)) {
                  cmp_f64_gt(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_S64)) {
                  cmp_s64_gt(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else {
                  cmp_s32_gt_const_0(instructions, hsailStackFrame, i);

               }
               cbr(instructions, hsailStackFrame, i);
               break;
            case IFLE:
               if (parseState.equals(ParseState.COMPARE_F32)) {
                  cmp_f32_le(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_F64)) {
                  cmp_f64_le(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_S64)) {
                  cmp_s64_le(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else {
                  cmp_s32_le_const_0(instructions, hsailStackFrame, i);


               }
               cbr(instructions, hsailStackFrame, i);
               break;
            case IF_ICMPEQ:

               cmp_s32_eq(instructions, hsailStackFrame, i);
               cbr(instructions, hsailStackFrame, i);

               break;
            case IF_ICMPNE:
               cmp_s32_ne(instructions, hsailStackFrame, i);
               cbr(instructions, hsailStackFrame, i);
               break;
            case IF_ICMPLT:
               cmp_s32_lt(instructions, hsailStackFrame, i);
               cbr(instructions, hsailStackFrame, i);
               break;
            case IF_ICMPGE:
               cmp_s32_ge(instructions, hsailStackFrame, i);
               cbr(instructions, hsailStackFrame, i);
               break;
            case IF_ICMPGT:
               cmp_s32_gt(instructions, hsailStackFrame, i);
               cbr(instructions, hsailStackFrame, i);
               break;
            case IF_ICMPLE:
               cmp_s32_le(instructions, hsailStackFrame, i);
               cbr(instructions, hsailStackFrame, i);
               break;
            case IF_ACMPEQ:
               cmp_ref_eq(instructions, hsailStackFrame, i);
               cbr(instructions, hsailStackFrame, i);
               break;
            case IF_ACMPNE:
               cmp_ref_ne(instructions, hsailStackFrame, i);
               cbr(instructions, hsailStackFrame, i);
               break;
            case GOTO:
               brn(instructions, hsailStackFrame, i);
               break;
            case IFNULL:
               branch(instructions, hsailStackFrame, i);
            case IFNONNULL:
               branch(instructions, hsailStackFrame, i);
            case GOTO_W:
               branch(instructions, hsailStackFrame, i);
               break;
            case JSR:
                add(instructions, new nyi(hsailStackFrame, i));
               break;
            case RET:
                add(instructions, new nyi(hsailStackFrame, i));
               break;
            case TABLESWITCH:
                add(instructions, new nyi(hsailStackFrame, i));
               break;
            case LOOKUPSWITCH:
                add(instructions, new nyi(hsailStackFrame, i));
               break;
            case IRETURN:
             case LRETURN:
             case FRETURN:
             case DRETURN:
             case ARETURN:
               if (inlining && _frames.size()>1){
                  int maxLocals=i.getMethod().getCodeEntry().getMaxLocals(); // hsailStackFrame.stackOffset -maxLocals is the slot for the return value

                  switch(i.getByteCode()){
                      case IRETURN: add(instructions, new mov(hsailStackFrame, i, new StackReg_s32(hsailStackFrame.stackIdx(i) - maxLocals), new StackReg_s32(hsailStackFrame.stackIdx(i))));break;
                      case LRETURN: add(instructions, new mov(hsailStackFrame, i, new StackReg_s64(hsailStackFrame.stackIdx(i) - maxLocals), new StackReg_s64(hsailStackFrame.stackIdx(i))));break;
                      case FRETURN: add(instructions, new mov(hsailStackFrame, i, new StackReg_f32(hsailStackFrame.stackIdx(i) - maxLocals), new StackReg_f32(hsailStackFrame.stackIdx(i))));break;
                      case DRETURN: add(instructions, new mov(hsailStackFrame, i, new StackReg_f64(hsailStackFrame.stackIdx(i) - maxLocals), new StackReg_f64(hsailStackFrame.stackIdx(i))));break;
                      case ARETURN: add(instructions, new mov(hsailStackFrame, i, new StackReg_ref(hsailStackFrame.stackIdx(i) - maxLocals), new StackReg_ref(hsailStackFrame.stackIdx(i))));break;
                  }
                   if (i.isLastInstruction()){
                      if (needsReturnLabel){
                         add(instructions, new nop(hsailStackFrame, i, hsailStackFrame.getUniqueName()));
                      }
                  }else{
                      add(instructions, new returnBranch(hsailStackFrame, i, hsailStackFrame.getUniqueName()));
                      needsReturnLabel=true;
                  }
               }else{
                   switch(i.getByteCode()){
                       case IRETURN:  ret_s32(instructions, hsailStackFrame, i);break;
                       case LRETURN:  ret_s64(instructions, hsailStackFrame, i);break;
                       case FRETURN:  ret_f32(instructions, hsailStackFrame, i);break;
                       case DRETURN:  ret_s64(instructions, hsailStackFrame, i);break;
                       case ARETURN:  ret_ref(instructions, hsailStackFrame, i);break;

                   }

               }
               break;
            case RETURN:
                if (inlining && _frames.size()>1){
                    if (i.getNextPC()!=null){
                        add(instructions, new returnBranch(hsailStackFrame, i, hsailStackFrame.getUniqueName()));
                        needsReturnLabel=true;
                    }else{
                        if (i.isBranchTarget()){
                           add(instructions, new nop(hsailStackFrame, i, null));
                        }else if (needsReturnLabel){
                            add(instructions, new nop(hsailStackFrame, i, hsailStackFrame.getUniqueName()));
                        }
                    }
                }else{
                    ret_void(instructions, hsailStackFrame, i);
                }
               break;
            case GETSTATIC: {
               TypeHelper.JavaType type = i.asFieldAccessor().getConstantPoolFieldEntry().getType();

               try {
                  Class clazz = Class.forName(i.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());

                  Field f = clazz.getDeclaredField(i.asFieldAccessor().getFieldName());

                  if (!type.isPrimitive()) {
                     static_field_load_ref(instructions, hsailStackFrame, i, f);
                  } else if (type.isInt()) {
                     static_field_load_s32(instructions, hsailStackFrame, i, f);
                  } else if (type.isFloat()) {
                     static_field_load_f32(instructions, hsailStackFrame, i, f);
                  } else if (type.isDouble()) {
                     static_field_load_f64(instructions, hsailStackFrame, i, f);
                  } else if (type.isLong()) {
                     static_field_load_s64(instructions, hsailStackFrame, i, f);
                  } else if (type.isChar()) {
                     static_field_load_u16(instructions, hsailStackFrame, i, f);
                  } else if (type.isShort()) {
                     static_field_load_s16(instructions, hsailStackFrame, i, f);
                  } else if (type.isChar()) {
                     static_field_load_s8(instructions, hsailStackFrame, i, f);
                  }
               } catch (ClassNotFoundException e) {
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
               } catch (NoSuchFieldException e) {
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
               }


            }
            break;
            case GETFIELD: {
               // TypeHelper.JavaType type = i.asFieldAccessor().getConstantPoolFieldEntry().getType();

               try {
                  Class clazz = Class.forName(i.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());

                  Field f = clazz.getDeclaredField(i.asFieldAccessor().getFieldName());
                  if (!f.getType().isPrimitive()) {
                     field_load_ref(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(int.class)) {
                     field_load_s32(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(short.class)) {
                     field_load_s16(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(char.class)) {
                     field_load_u16(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(boolean.class)) {
                     field_load_s8(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(float.class)) {
                     field_load_f32(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(double.class)) {
                     field_load_f64(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(long.class)) {
                     field_load_s64(instructions, hsailStackFrame, i, f);
                  } else {
                     throw new IllegalStateException("unexpected get field type");
                  }
               } catch (ClassNotFoundException e) {
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
               } catch (NoSuchFieldException e) {
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
               }


            }
            break;
            case PUTSTATIC:
                add(instructions, new nyi(hsailStackFrame, i));
               break;
            case PUTFIELD: {
               // TypeHelper.JavaType type = i.asFieldAccessor().getConstantPoolFieldEntry().getType();

               try {
                  Class clazz = Class.forName(i.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());

                  Field f = clazz.getDeclaredField(i.asFieldAccessor().getFieldName());
                  if (!f.getType().isPrimitive()) {
                     field_store_ref(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(int.class)) {
                     field_store_s32(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(short.class)) {
                     field_store_s16(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(char.class)) {
                     field_store_u16(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(boolean.class)) {
                     field_store_s8(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(float.class)) {
                     field_store_f32(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(double.class)) {
                      add(instructions, new field_store<StackReg_f64, f64>(hsailStackFrame, i, new StackReg_f64(hsailStackFrame.stackIdx(i)+ 1), new StackReg_ref(hsailStackFrame.stackIdx(i)), (long) UnsafeWrapper.objectFieldOffset(f)));
                  } else if (f.getType().equals(long.class)) {
                      add(instructions, new field_store<StackReg_s64, s64>(hsailStackFrame, i, new StackReg_s64(hsailStackFrame.stackIdx(i)+ 1), new StackReg_ref(hsailStackFrame.stackIdx(i)), (long) UnsafeWrapper.objectFieldOffset(f)));
                  }   else {
                     throw new IllegalStateException("unexpected put field type");
                  }
               } catch (ClassNotFoundException e) {
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
               } catch (NoSuchFieldException e) {
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
               }


            }
            break;
            case INVOKEVIRTUAL:
            case INVOKESPECIAL:
            case INVOKESTATIC:
            case INVOKEINTERFACE:
            case INVOKEDYNAMIC:
            {
               CallInfo callInfo = new CallInfo(i);
               InlineIntrinsicCall call = HSAILIntrinsics.getInlineIntrinsic(callInfo);
               if (call != null){
                  call.add(instructions, hsailStackFrame, i);
               }else{
                  if (inlining){
                     try{

                        Class theClass = Class.forName(callInfo.dotClassName);
                        ClassModel classModel = ClassModel.getClassModel(theClass);
                        ClassModel.ClassModelMethod calledMethod = classModel.getMethod(callInfo.name, callInfo.sig);
                        _frames.push(new HSAILStackFrame(hsailStackFrame,  calledMethod, i.getThisPC(), i.getPreStackBase()+i.getMethod().getCodeEntry().getMaxLocals()+hsailStackFrame.stackOffset));
                        _frameSet.add(_frames.peek());
                        addInstructions(instructions, _frameSet, _frames, calledMethod);
                        _frames.pop();
                     }catch (ClassParseException cpe){

                     }catch (ClassNotFoundException cnf){

                     }

                  }  else {
                    // call(instructions, this, hsailStackFrame, i, callInfo);
                  }


               }
            }
            break;
            case NEW:
                add(instructions, new nyi(hsailStackFrame, i));
               break;
            case NEWARRAY:
                add(instructions, new nyi(hsailStackFrame, i));
               break;
            case ANEWARRAY:
                add(instructions, new nyi(hsailStackFrame, i));
               break;
            case ARRAYLENGTH:
                instructions.add (new array_len(hsailStackFrame,i, new StackReg_s32(hsailStackFrame.stackIdx(i)), new StackReg_ref(hsailStackFrame.stackIdx(i))));
               break;
            case ATHROW:
                add(instructions, new nyi(hsailStackFrame, i));
               break;
            case CHECKCAST:
                add(instructions, new nyi(hsailStackFrame, i));
               break;
            case INSTANCEOF:
                add(instructions, new nyi(hsailStackFrame, i));
               break;
            case MONITORENTER:
                add(instructions, new nyi(hsailStackFrame, i));
               break;
            case MONITOREXIT:
                add(instructions, new nyi(hsailStackFrame, i));
               break;
            case WIDE:
                add(instructions, new nyi(hsailStackFrame, i));
               break;
            case MULTIANEWARRAY:
                add(instructions, new nyi(hsailStackFrame, i));
               break;
            case JSR_W:
                add(instructions, new nyi(hsailStackFrame, i));
               break;

         }
         // lastInstruction = i;


      }

   }
}

