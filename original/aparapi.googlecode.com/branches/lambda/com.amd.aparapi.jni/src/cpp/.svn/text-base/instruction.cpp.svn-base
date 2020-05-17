#define INSTRUCTION_CPP
#include "instruction.h"

#include <string.h>


PCStack::PCStack(unsigned _size)
   : index(0), size(_size+1), values(new int[_size+1]) {
      for (int i=0; i<size; i++){
         values[i]=-1;
      }
   }
PCStack::~PCStack(){
   if (size){
      delete[] values;
   }
}

int PCStack::pop(){
   int retValue = -1;
   if (index>0){
      retValue = values[--index];
      values[index] = -1;
   }
   return(retValue);
}
int PCStack::peek(){
   int retValue = -1;
   if (index>0){
      retValue = values[index-1];
   }
   return(retValue);
}
void PCStack::push(int _value){
   if ((index+1)<size){
      values[index++]=_value;
   }
}
int PCStack::get(unsigned _index){
   int retValue = -1;
   if (_index < size){
      retValue=values[_index];
   }
   return(retValue);
}
unsigned PCStack::getSize(){
   return(size);
}
unsigned PCStack::getIndex(){
   return(index);
}

u4_t Instruction::getPC(){
   return(pc);
}
ByteCode *Instruction::getByteCode(){
   return(byteCode);
}

int Instruction::isBranch(){
   switch (byteCode->bytecode){
      case I_IFEQ: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, PushSpec_NONE, OpSpec_Equal
      case I_IFNE: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, PushSpec_NONE, OpSpec_NotEqual
      case I_IFLT: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, PushSpec_NONE, OpSpec_LessThan
      case I_IFGE: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, PushSpec_NONE, OpSpec_GreaterThanOrEqual
      case I_IFGT: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, PushSpec_NONE, OpSpec_GreaterThan
      case I_IFLE: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, PushSpec_NONE, OpSpec_LessThanOrEqual
      case I_IF_ICMPEQ: // LDSpec_NONE, STSpec_NONE, ImmSpec_Sconst, PopSpec_II, PushSpec_NONE, OpSpec_Equal
      case I_IF_ICMPNE: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_II, PushSpec_NONE, OpSpec_NotEqual
      case I_IF_ICMPLT: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_II, PushSpec_NONE, OpSpec_LessThan
      case I_IF_ICMPGE: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_II, PushSpec_NONE, OpSpec_GreaterThanOrEqual
      case I_IF_ICMPGT: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_II, PushSpec_NONE, OpSpec_GreaterThan
      case I_IF_ICMPLE: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_II, PushSpec_NONE, OpSpec_LessThanOrEqual
      case I_IF_ACMPEQ: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_OO, PushSpec_NONE, OpSpec_Equal
      case I_IF_ACMPNE: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_OO, PushSpec_NONE, OpSpec_NotEqual
      case I_GOTO: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_NONE, PushSpec_NONE, OpSpec_NONE 
      case I_JSR: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_NONE, PushSpec_RA, OpSpec_NONE
         return(immSpec_Spc.pc);
      default:
         return(-1);
   }
}

int Instruction::getLabel(){
   return (label);
}

void Instruction::setLabel(int _label){
   label = _label;
}

void Instruction::branchFrom(int _pc){
  int* newTargets = new int[targetCount+1];
  if (targetCount>0){
     memcpy(newTargets, targets, targetCount*sizeof(int));
     delete [] targets;
  }
  newTargets[targetCount++] = _pc;
  targets = newTargets;
}

Instruction::Instruction(ConstantPoolEntry** _constantPool, ByteBuffer *_codeByteBuffer, PCStack *_pcStack, s4_t _prevPc){
   label = -1;
   targetCount = 0;
   targets = NULL;
   prevPc = _prevPc;
   stackBase = _pcStack->getIndex();
   pc = _codeByteBuffer->getOffset();
   byte_t byte= _codeByteBuffer->u1();
   byteCode = &bytecode[byte];
   bool wide = false;
   if (byteCode->bytecode == I_WIDE){
      wide = true;
      byte_t byte= _codeByteBuffer->u1();
      byteCode = &bytecode[byte];
   }
   switch(byteCode->immSpec){
      case ImmSpec_NONE:
         break;
      case ImmSpec_NONE_const_null:
         break;
      case ImmSpec_NONE_const_m1:
         break;
      case ImmSpec_NONE_const_0:
         break;
      case ImmSpec_NONE_const_1:
         break;
      case ImmSpec_NONE_const_2:
         break;
      case ImmSpec_NONE_const_3:
         break;
      case ImmSpec_NONE_const_4:
         break;
      case ImmSpec_NONE_const_5:
         break;
      case ImmSpec_NONE_lvti_0:
         break;
      case ImmSpec_NONE_lvti_1:
         break;
      case ImmSpec_NONE_lvti_2:
         break;
      case ImmSpec_NONE_lvti_3:
         break;
      case ImmSpec_Blvti:
         if (wide){
            immSpec_Blvti.lvti=_codeByteBuffer->u2();
         }else{
            immSpec_Blvti.lvti=_codeByteBuffer->u1();
         }
         break;
      case ImmSpec_Bcpci:
         immSpec_Bcpci.cpci=_codeByteBuffer->u1();
         break;
      case ImmSpec_Scpci:
         immSpec_Scpci.cpci= _codeByteBuffer->u2();
         break;
      case ImmSpec_Bconst:
         immSpec_Bconst.value= _codeByteBuffer->u1();
         break;
      case ImmSpec_Sconst:
         immSpec_Sconst.value= _codeByteBuffer->u2();
         break;
      case ImmSpec_Spc:
         immSpec_Spc.pc= _codeByteBuffer->s2() +pc;
         break;
      case ImmSpec_Scpfi:
         immSpec_Scpfi.cpfi= _codeByteBuffer->u2();
         break;
      case ImmSpec_ScpmiBB:
         immSpec_ScpmiBB.cpmi= _codeByteBuffer->u2();
         immSpec_ScpmiBB.b1= _codeByteBuffer->u1();
         immSpec_ScpmiBB.b2= _codeByteBuffer->u1();
         break;
      case ImmSpec_BlvtiBconst:
         if (wide){
            immSpec_BlvtiBconst.lvti= _codeByteBuffer->u2();
            immSpec_BlvtiBconst.value= _codeByteBuffer->s2();
         }else{
            immSpec_BlvtiBconst.lvti= _codeByteBuffer->u1();
            immSpec_BlvtiBconst.value= _codeByteBuffer->u1(); //s1()? TODO:
         }
         break;
      case ImmSpec_Scpmi:
         immSpec_Scpmi.cpmi= _codeByteBuffer->u2();
         break;
      case ImmSpec_ScpciBdim:
         immSpec_ScpciBdim.cpci= _codeByteBuffer->u2();
         immSpec_ScpciBdim.dim= _codeByteBuffer->u1();
         break;
      case ImmSpec_Ipc:
         immSpec_Ipc.pc= _codeByteBuffer->s4()+pc;
         break;
      case ImmSpec_UNKNOWN:
         break;

   }

   nextPc = _codeByteBuffer->getOffset();
   length = nextPc-pc;

   switch(byteCode->popSpec){
      case PopSpec_NONE:
         break;
      case PopSpec_A:
         popSpec_A.a = _pcStack->pop();
         break;
      case PopSpec_AI:
         popSpec_AI.i = _pcStack->pop();
         popSpec_AI.a = _pcStack->pop();
         break;
      case PopSpec_AII:
         popSpec_AII.i2 = _pcStack->pop();
         popSpec_AII.i1 = _pcStack->pop();
         popSpec_AII.a = _pcStack->pop();
         break;
      case PopSpec_AIL:
         popSpec_AIL.l = _pcStack->pop();
         popSpec_AIL.i = _pcStack->pop();
         popSpec_AIL.a = _pcStack->pop();
         break;
      case PopSpec_AIF:
         popSpec_AIF.f = _pcStack->pop();
         popSpec_AIF.i = _pcStack->pop();
         popSpec_AIF.a = _pcStack->pop();
         break;
      case PopSpec_AID:
         popSpec_AID.d = _pcStack->pop();
         popSpec_AID.i = _pcStack->pop();
         popSpec_AID.a = _pcStack->pop();
         break;
      case PopSpec_AIO:
         popSpec_AIO.o = _pcStack->pop();
         popSpec_AIO.i = _pcStack->pop();
         popSpec_AIO.a = _pcStack->pop();
         break;
      case PopSpec_AIB:
         popSpec_AIB.b = _pcStack->pop();
         popSpec_AIB.i = _pcStack->pop();
         popSpec_AIB.a = _pcStack->pop();
         break;
      case PopSpec_AIC:
         popSpec_AIC.c = _pcStack->pop();
         popSpec_AIC.i = _pcStack->pop();
         popSpec_AIC.a = _pcStack->pop();
         break;
      case PopSpec_AIS:
         popSpec_AIS.s = _pcStack->pop();
         popSpec_AIS.i = _pcStack->pop();
         popSpec_AIS.a = _pcStack->pop();
         break;
      case PopSpec_II :
         popSpec_II.i2 = _pcStack->pop();
         popSpec_II.i1 = _pcStack->pop();
         break;
      case PopSpec_III:
         popSpec_III.i3 = _pcStack->pop();
         popSpec_III.i2 = _pcStack->pop();
         popSpec_III.i1 = _pcStack->pop();
         break;
      case PopSpec_IIII:
         popSpec_IIII.i4 = _pcStack->pop();
         popSpec_IIII.i3 = _pcStack->pop();
         popSpec_IIII.i2 = _pcStack->pop();
         popSpec_IIII.i1 = _pcStack->pop();
         break;
      case PopSpec_L:
         popSpec_L.l = _pcStack->pop();
         break;
      case PopSpec_LI:
         popSpec_LI.i = _pcStack->pop();
         popSpec_LI.l = _pcStack->pop();
         break;
      case PopSpec_LL:
         popSpec_LL.l2 = _pcStack->pop();
         popSpec_LL.l1 = _pcStack->pop();
         break;
      case PopSpec_F:
         popSpec_F.f = _pcStack->pop();
         break;
      case PopSpec_FF:
         popSpec_FF.f2 = _pcStack->pop();
         popSpec_FF.f1 = _pcStack->pop();
         break;
      case PopSpec_OO:
         popSpec_OO.o2 = _pcStack->pop();
         popSpec_OO.o1 = _pcStack->pop();
         break;
      case PopSpec_RA:
         popSpec_RA.a = _pcStack->pop();
         popSpec_RA.r = _pcStack->pop();
         break;
      case PopSpec_O:
         popSpec_O.o = _pcStack->pop();
         break;
      case PopSpec_I:
         popSpec_I.i = _pcStack->pop();
         break;
      case PopSpec_D:
         popSpec_D.d = _pcStack->pop();
         break;
      case PopSpec_DD:
         popSpec_DD.d2 = _pcStack->pop();
         popSpec_DD.d1 = _pcStack->pop();
         break;
      case PopSpec_OFSIG:
         popSpec_OFSIG.v = _pcStack->pop();
         popSpec_OFSIG.o = _pcStack->pop();
         break;
      case PopSpec_FSIG:
         popSpec_FSIG.v = _pcStack->pop();
         break;
      case PopSpec_UNKNOWN:
         break;
      case PopSpec_MSIG:
         {
            MethodConstantPoolEntry* method = (MethodConstantPoolEntry*)_constantPool[immSpec_Scpmi.cpmi];
            popSpec_MSIG.argc = method->getArgCount(_constantPool);
            if (popSpec_MSIG.argc>0){
               popSpec_MSIG.args = new u4_t[popSpec_MSIG.argc];
               for (int i=popSpec_MSIG.argc-1; i>=0;  i--){
                  popSpec_MSIG.args[i] = _pcStack->pop();
               }
            }else{
               popSpec_MSIG.args = NULL;
            }
         }
         break;
      case PopSpec_OMSIG:
         {
            MethodConstantPoolEntry* method = (MethodConstantPoolEntry*)_constantPool[immSpec_Scpmi.cpmi];
            popSpec_OMSIG.argc = method->getArgCount(_constantPool);
            if ( popSpec_OMSIG.argc>0){
               popSpec_OMSIG.args = new u4_t[popSpec_OMSIG.argc];
               for (int i=popSpec_OMSIG.argc-1; i>=0; i--){
                  popSpec_OMSIG.args[i] = _pcStack->pop();
               }
            }else{
               popSpec_OMSIG.args = NULL;

            }
            popSpec_OMSIG.o = _pcStack->pop();
         }
         break;
   }


   switch(byteCode->pushSpec){
      case PushSpec_NONE:
         break;
      case PushSpec_N:
         _pcStack->push(pc);
         break;
      case PushSpec_I:
         _pcStack->push(pc);
         break;
      case PushSpec_L:
         _pcStack->push(pc);
         break;
      case PushSpec_F:
         _pcStack->push(pc);
         break;
      case PushSpec_D:
         _pcStack->push(pc);
         break;
      case PushSpec_O:
         _pcStack->push(pc);
         break;
      case PushSpec_A:
         _pcStack->push(pc);
         break;
      case PushSpec_RA:
         _pcStack->push(pc);
         _pcStack->push(pc);
         break;
      case PushSpec_IorForS:
         _pcStack->push(pc);
         break;
      case PushSpec_LorD:
         _pcStack->push(pc);
         break;
      case PushSpec_II:
         _pcStack->push(pc);
         _pcStack->push(pc);
         break;
      case PushSpec_III:
         _pcStack->push(pc);
         _pcStack->push(pc);
         _pcStack->push(pc);
         break;
      case PushSpec_IIII:
         _pcStack->push(pc);
         _pcStack->push(pc);
         _pcStack->push(pc);
         _pcStack->push(pc);
         break;
      case PushSpec_IIIII:
         _pcStack->push(pc);
         _pcStack->push(pc);
         _pcStack->push(pc);
         _pcStack->push(pc);
         _pcStack->push(pc);
         break;
      case PushSpec_IIIIII:
         _pcStack->push(pc);
         _pcStack->push(pc);
         _pcStack->push(pc);
         _pcStack->push(pc);
         _pcStack->push(pc);
         _pcStack->push(pc);
         break;
      case PushSpec_FSIG:
         {
            FieldConstantPoolEntry* field = (FieldConstantPoolEntry*)_constantPool[immSpec_Scpmi.cpmi];
            _pcStack->push(pc);
         }

         break;
      case PushSpec_MSIG:
         {
            MethodConstantPoolEntry* method = (MethodConstantPoolEntry*)_constantPool[immSpec_Scpmi.cpmi];
            int retc = method->getRetCount(_constantPool);
            if (retc>0){
               _pcStack->push(pc);
            } else {
            }
         }

         break;
      case PushSpec_UNKNOWN:
         break;
   }


}

Instruction::~Instruction(){
   switch(byteCode->pushSpec){
      case PopSpec_MSIG:
         if (popSpec_MSIG.argc>0){
            delete [] popSpec_MSIG.args;
         }
         break;
      case PopSpec_OMSIG:
         if ( popSpec_OMSIG.argc>0){
            delete [] popSpec_OMSIG.args;
         }
         break;
   }
   if (targetCount>0 && targets != NULL){
      delete [] targets;
   }
}

void Instruction::write(FILE *_file, ConstantPoolEntry **_constantPool, LocalVariableTableAttribute *_localVariableTableAttribute){
   fprintf(_file, "%4d %-14s ", pc, (char*)byteCode->name);
   fprintf(_file, "%4d ", stackBase);
   int popCount = getPopCount(_constantPool);
   fprintf(_file, "%4d ", popCount);
   int pushCount = getPushCount(_constantPool);
   fprintf(_file, "%4d ", pushCount);
   fprintf(_file, "%4d ", pushCount-popCount);

   switch(byteCode->immSpec){
      case ImmSpec_NONE:
         break;
      case ImmSpec_NONE_const_null:
         fprintf(_file, " NULL");
         break;
      case ImmSpec_NONE_const_m1:
         fprintf(_file, " -1");
         break;
      case ImmSpec_NONE_const_0:
         fprintf(_file, " 0");
         break;
      case ImmSpec_NONE_const_1:
         fprintf(_file, " 1");
         break;
      case ImmSpec_NONE_const_2:
         fprintf(_file, " 2");
         break;
      case ImmSpec_NONE_const_3:
         fprintf(_file, " 3");
         break;
      case ImmSpec_NONE_const_4:
         fprintf(_file, " 4");
         break;
      case ImmSpec_NONE_const_5:
         fprintf(_file, " 5");
         break;
      case ImmSpec_NONE_lvti_0:
         if (_localVariableTableAttribute !=  NULL){
            char *varName = _localVariableTableAttribute->getLocalVariableName(pc +length, 0, _constantPool);
            fprintf(_file, " %s", varName);
         }
         break;
      case ImmSpec_NONE_lvti_1:
         if (_localVariableTableAttribute !=  NULL){
            char *varName = _localVariableTableAttribute->getLocalVariableName(pc +length, 1, _constantPool);
            fprintf(_file, " %s", varName);
         }
         break;
      case ImmSpec_NONE_lvti_2:
         if (_localVariableTableAttribute !=  NULL){
            char *varName = _localVariableTableAttribute->getLocalVariableName(pc +length, 2, _constantPool);
            fprintf(_file, " %s", varName);
         }
         break;
      case ImmSpec_NONE_lvti_3:
         if (_localVariableTableAttribute !=  NULL){
            char *varName = _localVariableTableAttribute->getLocalVariableName(pc +length, 3, _constantPool);
            fprintf(_file, " %s", varName);
         }
         break;
      case ImmSpec_Blvti:
         if (_localVariableTableAttribute !=  NULL){
            char *varName = _localVariableTableAttribute->getLocalVariableName(pc +length, immSpec_Blvti.lvti, _constantPool);
            fprintf(_file, " %s", varName);
         }
         break;
      case ImmSpec_Bcpci:
      case ImmSpec_Scpci:
         {
            int cpi = 0;
            if (byteCode->immSpec == ImmSpec_Bcpci){
               cpi = immSpec_Bcpci.cpci;
            }else{
               cpi = immSpec_Scpci.cpci;
            }
            ConstantPoolEntry* constantPoolEntry = _constantPool[cpi];
            switch (constantPoolEntry->getConstantPoolType()){
               case FLOAT:
                  fprintf(_file, " FLOAT %f", ((FloatConstantPoolEntry*)constantPoolEntry)->getValue());
                  break;
               case INTEGER:
                  fprintf(_file, " INTEGER %d", ((IntegerConstantPoolEntry*)constantPoolEntry)->getValue());
                  break;
               case DOUBLE:
                  fprintf(_file, " DOUBLE %lf", ((DoubleConstantPoolEntry*)constantPoolEntry)->getValue());
                  break;
               case LONG:
                  fprintf(_file, " LONG %ld", ((LongConstantPoolEntry*)constantPoolEntry)->getValue());
                  break;
               default:
                  fprintf(_file, " constant pool #%d", immSpec_Bcpci.cpci);
                  break;
            }
            break;
         }
      case ImmSpec_Bconst:
         fprintf(_file, " byte %d", immSpec_Bconst.value);
         break;
      case ImmSpec_Sconst:
         fprintf(_file, " short %d", immSpec_Sconst.value);
         break;
      case ImmSpec_Spc:
         fprintf(_file, " %d", immSpec_Spc.pc);
         break;
      case ImmSpec_Scpfi:
         break;
      case ImmSpec_ScpmiBB:
         break;
      case ImmSpec_BlvtiBconst:
         if (_localVariableTableAttribute !=  NULL){
            char *varName = _localVariableTableAttribute->getLocalVariableName(pc +length, immSpec_BlvtiBconst.lvti, _constantPool);
            fprintf(_file, " %s", varName);
         }
         fprintf(_file, " %d", immSpec_BlvtiBconst.value);
         break;
      case ImmSpec_Scpmi:
         break;
      case ImmSpec_ScpciBdim:
         break;
      case ImmSpec_Ipc:
         fprintf(_file, " %d", immSpec_Ipc.pc);
         break;
      case ImmSpec_UNKNOWN:
         break;

   }

   switch(byteCode->popSpec){
      case PopSpec_NONE:
         fprintf(_file, " <-- NONE");
         break;
      case PopSpec_A:
         fprintf(_file, " <-- pop ((array)%d)", popSpec_A.a);
         break;
      case PopSpec_AI:
         fprintf(_file, " <-- pop ((array)%d, (int)%d)", popSpec_AI.a, popSpec_AI.i);
         break;
      case PopSpec_AII:
         fprintf(_file, " <-- pop ((array)%d, (int)%d, (int)%d)", popSpec_AII.a, popSpec_AII.i1, popSpec_AII.i2);
         break;
      case PopSpec_AIL:
         fprintf(_file, " <-- pop ((array)%d, (int)%d, (long)%d)", popSpec_AIL.a, popSpec_AIL.i, popSpec_AIL.l);
         break;
      case PopSpec_AIF:
         fprintf(_file, " <-- pop ((array)%d, (int)%d, (float)%d)", popSpec_AIF.a, popSpec_AIF.i, popSpec_AIF.f);
         break;
      case PopSpec_AID:
         fprintf(_file, " <-- pop ((array)%d, (int)%d, (double)%d)", popSpec_AID.a, popSpec_AID.i, popSpec_AID.d);
         break;
      case PopSpec_AIO:
         fprintf(_file, " <-- pop ((array)%d, (int)%d, (object)%d)", popSpec_AIO.a, popSpec_AIO.i, popSpec_AIO.o);
         break;
      case PopSpec_AIB:
         fprintf(_file, " <-- pop ((array)%d, (int)%d, (byte)%d)", popSpec_AIB.a, popSpec_AIB.i, popSpec_AIB.b);
         break;
      case PopSpec_AIC:
         fprintf(_file, " <-- pop ((array)%d, (int)%d, (char)%d)", popSpec_AIC.a, popSpec_AIC.i, popSpec_AIC.c);
         break;
      case PopSpec_AIS:
         fprintf(_file, " <-- pop ((array)%d, (int)%d, (short)%d)", popSpec_AIS.a, popSpec_AIS.i, popSpec_AIS.s);
      case PopSpec_II :
         fprintf(_file, " <-- ((int)%d, (int)%d)", popSpec_II.i1, popSpec_II.i2);
         break;
      case PopSpec_III:
         fprintf(_file, " <-- ((int)%d, (int)%d, (int)%d)", popSpec_III.i1, popSpec_III.i2, popSpec_III.i3);
         break;
      case PopSpec_IIII:
         fprintf(_file, " <-- ((int)%d, (int)%d, (int)%d, (int)%d)", popSpec_IIII.i1, popSpec_IIII.i2, popSpec_IIII.i3, popSpec_IIII.i4);
         break;
      case PopSpec_L:
         fprintf(_file, " <-- ((long)%d", popSpec_L.l);
         break;
      case PopSpec_LI:
         fprintf(_file, " <-- ((long)%d, (int)%d)", popSpec_LI.l, popSpec_LI.i);
         break;
      case PopSpec_LL:
         fprintf(_file, " <-- ((long)%d, (long)%d)", popSpec_LL.l1, popSpec_LL.l2);
         break;
      case PopSpec_F:
         fprintf(_file, " <-- ((float)%d)", popSpec_F.f);
         break;
      case PopSpec_FF:
         fprintf(_file, " <-- ((float)%d, (float)%d)", popSpec_FF.f1, popSpec_FF.f2);
         break;
      case PopSpec_OO:
         fprintf(_file, " <-- ((object)%d, (object)%d)", popSpec_OO.o1, popSpec_OO.o2);
         break;
      case PopSpec_RA:
         fprintf(_file, " <-- ((R)%d, (A)%d)", popSpec_RA.r, popSpec_RA.a);
         break;
      case PopSpec_O:
         fprintf(_file, " <-- ((object)%d)", popSpec_O.o);
         break;
      case PopSpec_I:
         fprintf(_file, " <-- ((int)%d)", popSpec_I.i);
         break;
      case PopSpec_D:
         fprintf(_file, " <-- ((double)%d)", popSpec_D.d);
         break;
      case PopSpec_DD:
         fprintf(_file, " <-- ((double)%d, (double)%d)", popSpec_DD.d1, popSpec_DD.d2);
         break;
      case PopSpec_OFSIG:
         break;
      case PopSpec_FSIG:
         break;
      case PopSpec_UNKNOWN:
         break;
      case PopSpec_MSIG:
         {
            fprintf(_file, " <-- ");
            if (popSpec_MSIG.argc==0){
               fprintf(_file, "NONE");
            }else{
               fprintf(_file, "(");
               for (int i=0; i<popSpec_MSIG.argc; i++){
                  if (i>0){
                     fprintf(_file, " ,");
                  }
                  fprintf(_file, "%d ", popSpec_MSIG.args[i]);
               }
               fprintf(_file, ")");
            }

         }
         break;
      case PopSpec_OMSIG:
         {
            fprintf(_file, " <- (%d", popSpec_OMSIG.o);
            if (popSpec_OMSIG.argc>0){
               for (int i=0; i<popSpec_OMSIG.argc; i++){
                  fprintf(_file, ", %d", popSpec_OMSIG.args[i]);
               }
            }
            fprintf(_file, ")");
         }
         break;
   }
}


void Instruction::treeWrite(FILE *_file, Instruction **_instructions, int _codeLength, int _depth, ConstantPoolEntry **_constantPool, LocalVariableTableAttribute *_localVariableTableAttribute, int _rootPc){
   for (int i=0; i<_depth; i++){
      fprintf(_file, "   ");
   }
   fprintf(_file, "%4d %-10s", pc, (char*)byteCode->name);

   switch(byteCode->immSpec){
      case ImmSpec_NONE:
         break;
      case ImmSpec_NONE_const_null:
         fprintf(_file, " NULL");
         break;
      case ImmSpec_NONE_const_m1:
         fprintf(_file, " -1");
         break;
      case ImmSpec_NONE_const_0:
         fprintf(_file, " 0");
         break;
      case ImmSpec_NONE_const_1:
         fprintf(_file, " 1");
         break;
      case ImmSpec_NONE_const_2:
         fprintf(_file, " 2");
         break;
      case ImmSpec_NONE_const_3:
         fprintf(_file, " 3");
         break;
      case ImmSpec_NONE_const_4:
         fprintf(_file, " 4");
         break;
      case ImmSpec_NONE_const_5:
         fprintf(_file, " 5");
         break;
      case ImmSpec_NONE_lvti_0:
         if (_localVariableTableAttribute !=  NULL){
            char *varName = _localVariableTableAttribute->getLocalVariableName(pc +length, 0, _constantPool);
            fprintf(_file, " %s", varName);
         }
         break;
      case ImmSpec_NONE_lvti_1:
         if (_localVariableTableAttribute !=  NULL){
            char *varName = _localVariableTableAttribute->getLocalVariableName(pc +length, 1, _constantPool);
            fprintf(_file, " %s", varName);
         }
         break;
      case ImmSpec_NONE_lvti_2:
         if (_localVariableTableAttribute !=  NULL){
            char *varName = _localVariableTableAttribute->getLocalVariableName(pc +length, 2, _constantPool);
            fprintf(_file, " %s", varName);
         }
         break;
      case ImmSpec_NONE_lvti_3:
         if (_localVariableTableAttribute !=  NULL){
            char *varName = _localVariableTableAttribute->getLocalVariableName(pc +length, 3, _constantPool);
            fprintf(_file, " %s", varName);
         }
         break;
      case ImmSpec_Blvti:
         if (_localVariableTableAttribute !=  NULL){
            char *varName = _localVariableTableAttribute->getLocalVariableName(pc +length, immSpec_Blvti.lvti, _constantPool);
            fprintf(_file, " %s", varName);
         }
         break;
      case ImmSpec_Bcpci:
      case ImmSpec_Scpci:
         {
            int cpi = 0;
            if (byteCode->immSpec == ImmSpec_Bcpci){
               cpi = immSpec_Bcpci.cpci;
            }else{
               cpi = immSpec_Scpci.cpci;
            }
            ConstantPoolEntry* constantPoolEntry = _constantPool[cpi];
            switch (constantPoolEntry->getConstantPoolType()){
               case FLOAT:
                  fprintf(_file, " %f", ((FloatConstantPoolEntry*)constantPoolEntry)->getValue());
                  break;
               case INTEGER:
                  fprintf(_file, " %d", ((IntegerConstantPoolEntry*)constantPoolEntry)->getValue());
                  break;
               case DOUBLE:
                  fprintf(_file, " %lf", ((DoubleConstantPoolEntry*)constantPoolEntry)->getValue());
                  break;
               case LONG:
                  fprintf(_file, " %ld", ((LongConstantPoolEntry*)constantPoolEntry)->getValue());
                  break;
               default:
                  fprintf(_file, " constant pool #%d", immSpec_Bcpci.cpci);
                  break;
            }
            break;
         }
      case ImmSpec_Bconst:
         fprintf(_file, " %d", immSpec_Bconst.value);
         break;
      case ImmSpec_Sconst:
         fprintf(_file, " %d", immSpec_Sconst.value);
         break;
      case ImmSpec_Spc:
         fprintf(_file, " %d", immSpec_Spc.pc);
         break;
      case ImmSpec_Scpfi:
         {
            FieldConstantPoolEntry* fieldConstantPoolEntry = (FieldConstantPoolEntry*)_constantPool[immSpec_Scpfi.cpfi];
            NameAndTypeConstantPoolEntry* nameAndTypeConstantPoolEntry = (NameAndTypeConstantPoolEntry*)_constantPool[fieldConstantPoolEntry->getNameAndTypeIndex()];
            UTF8ConstantPoolEntry* nameConstantPoolEntry = (UTF8ConstantPoolEntry*)_constantPool[nameAndTypeConstantPoolEntry->getNameIndex()];

            fprintf(_file, " %s", nameConstantPoolEntry->getUTF8());
            break;
         }
      case ImmSpec_ScpmiBB:
         break;
      case ImmSpec_BlvtiBconst:
         if (_localVariableTableAttribute !=  NULL){
            char *varName = _localVariableTableAttribute->getLocalVariableName(pc +length, immSpec_BlvtiBconst.lvti, _constantPool);
            fprintf(_file, " %s", varName);
         }
         fprintf(_file, " %d", immSpec_BlvtiBconst.value);
         break;
      case ImmSpec_Scpmi:
         {
            MethodConstantPoolEntry* methodConstantPoolEntry = (MethodConstantPoolEntry*)_constantPool[immSpec_Scpmi.cpmi];
            NameAndTypeConstantPoolEntry* nameAndTypeConstantPoolEntry = (NameAndTypeConstantPoolEntry*)_constantPool[methodConstantPoolEntry->getNameAndTypeIndex()];
            UTF8ConstantPoolEntry* nameConstantPoolEntry = (UTF8ConstantPoolEntry*)_constantPool[nameAndTypeConstantPoolEntry->getNameIndex()];
            fprintf(_file, " %s", nameConstantPoolEntry->getUTF8());
            break;
         }
      case ImmSpec_ScpciBdim:
         break;
      case ImmSpec_Ipc:
         fprintf(_file, " %d", immSpec_Ipc.pc);
         break;
      case ImmSpec_UNKNOWN:
         break;

   }


   //write(_file, _constantPool, _localVariableTableAttribute);
   fprintf(_file, "\n");
   switch(byteCode->popSpec){
      case PopSpec_NONE:
         break;
      case PopSpec_A:
         _instructions[popSpec_A.a]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_AI:
         _instructions[popSpec_AI.a]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AI.i]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_AII:
         _instructions[popSpec_AII.a]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AII.i1]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AII.i2]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_AIL:
         _instructions[popSpec_AIL.a]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AIL.i]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AIL.l]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_AIF:
         _instructions[popSpec_AIF.a]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AIF.i]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AIF.f]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_AID:
         _instructions[popSpec_AID.a]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AID.i]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AID.d]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_AIO:
         _instructions[popSpec_AIO.a]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AIO.i]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AIO.o]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_AIB:
         _instructions[popSpec_AIB.a]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AIB.i]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AIB.b]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_AIC:
         _instructions[popSpec_AIC.a]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AIC.i]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AIC.c]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_AIS:
         _instructions[popSpec_AIS.a]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AIS.i]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AIS.s]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_II :
         _instructions[popSpec_II.i1]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_II.i2]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_III:
         _instructions[popSpec_III.i1]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_III.i2]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_III.i3]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_IIII:
         _instructions[popSpec_IIII.i1]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_IIII.i2]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_IIII.i3]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_IIII.i4]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_L:
         _instructions[popSpec_L.l]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_LI:
         _instructions[popSpec_LI.l]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_LI.i]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_LL:
         _instructions[popSpec_LL.l1]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_LL.l2]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_F:
         _instructions[popSpec_F.f]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_FF:
         _instructions[popSpec_FF.f1]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_FF.f2]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_OO:
         _instructions[popSpec_OO.o1]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_OO.o2]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_RA:
         _instructions[popSpec_RA.r]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_RA.a]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_O:
         _instructions[popSpec_O.o]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_I:
         _instructions[popSpec_I.i]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_D:
         _instructions[popSpec_D.d]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_DD:
         _instructions[popSpec_DD.d1]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_DD.d1]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_OFSIG:
         break;
      case PopSpec_FSIG:
         break;
      case PopSpec_UNKNOWN:
         break;
      case PopSpec_MSIG:
         if (popSpec_MSIG.argc!=0){
            for (int i=0; i<popSpec_MSIG.argc; i++){
               _instructions[popSpec_MSIG.args[i]]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
            }
         }
         break;
      case PopSpec_OMSIG:
         _instructions[popSpec_OMSIG.o]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         if (popSpec_OMSIG.argc>0){
            for (int i=0; i<popSpec_OMSIG.argc; i++){
               _instructions[popSpec_OMSIG.args[i]]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
            }
         }
         break;
   }
   if (nextPc < _rootPc){
      Instruction *nextInstruction =  _instructions[nextPc];
      if (nextInstruction->getByteCode()->pushSpec == PushSpec_NONE && nextInstruction->getByteCode()->popSpec == PopSpec_NONE){
         for (int i=0; i<_depth; i++){
            fprintf(_file, "   ");
         }
         fprintf(_file, " (");
         nextInstruction->treeWrite(_file, _instructions, _codeLength, _depth, _constantPool, _localVariableTableAttribute, _rootPc);
         for (int i=0; i<_depth; i++){
            fprintf(_file, "   ");
         }
         fprintf(_file, " )");
      }
   }
}

s4_t Instruction::getPrevPC(){
   return(prevPc);
}

u4_t Instruction::getNextPC(){
   return(nextPc);
}

u2_t Instruction::getStackBase(){
   return(stackBase);
}

void Decoder::list(u1_t* buf, u4_t len){
   fprintf(stdout, "inside list\n");
}

int Instruction::getPopCount(ConstantPoolEntry **_constantPool){
   int count = 0;
   switch(byteCode->popSpec){
      case PopSpec_NONE:
      case PopSpec_UNKNOWN:
         count = 0;
         break;
      case PopSpec_A:
      case PopSpec_L:
      case PopSpec_F:
      case PopSpec_O:
      case PopSpec_I:
      case PopSpec_D:
         count = 1;
         break;
      case PopSpec_AI:
      case PopSpec_II :
      case PopSpec_LI:
      case PopSpec_LL:
      case PopSpec_FF:
      case PopSpec_OO:
      case PopSpec_OFSIG:
      case PopSpec_FSIG:
      case PopSpec_RA:
      case PopSpec_DD:
         count = 2;
         break;
      case PopSpec_AII:
      case PopSpec_AIL:
      case PopSpec_AIF:
      case PopSpec_AID:
      case PopSpec_AIO:
      case PopSpec_AIB:
      case PopSpec_AIC:
      case PopSpec_AIS:
      case PopSpec_III:
         count = 3;
         break;
      case PopSpec_IIII:
         count = 4;
         break;
      case PopSpec_MSIG:
         count = popSpec_MSIG.argc;
         break;
      case PopSpec_OMSIG:
         count = popSpec_MSIG.argc+1;
         break;
   }
   return(count);
}

int Instruction::getPushCount(ConstantPoolEntry **_constantPool){
   int count=0;
   switch(byteCode->pushSpec){
      case PushSpec_UNKNOWN:
      case PushSpec_NONE:
         count = 0;
         break;
      case PushSpec_N:
      case PushSpec_I:
      case PushSpec_L:
      case PushSpec_F:
      case PushSpec_D:
      case PushSpec_O:
      case PushSpec_A:
      case PushSpec_IorForS:
      case PushSpec_LorD:
      case PushSpec_FSIG:
         count = 1;
         break;
      case PushSpec_RA:
      case PushSpec_II:
         count = 2;
         break;
      case PushSpec_III:
         count = 3;
         break;
      case PushSpec_IIII:
         count = 4;
         break;
      case PushSpec_IIIII:
         count = 5;
         break;
      case PushSpec_IIIIII:
         count = 6;
         break;
      case PushSpec_MSIG:
         {
            MethodConstantPoolEntry* method = (MethodConstantPoolEntry*)_constantPool[immSpec_Scpmi.cpmi];
            count = method->getRetCount(_constantPool);
         }
         break;
   }
   return(count);

}

void getNameAndType(MethodConstantPoolEntry *_method, ConstantPoolEntry **_constantPool, char **_name, char **_type){
   NameAndTypeConstantPoolEntry* nameAndType = (NameAndTypeConstantPoolEntry*)_constantPool[_method->getNameAndTypeIndex()];
   UTF8ConstantPoolEntry* name = (UTF8ConstantPoolEntry*)_constantPool[nameAndType->getNameIndex()];
   UTF8ConstantPoolEntry* type = (UTF8ConstantPoolEntry*)_constantPool[nameAndType->getDescriptorIndex()];
   *_name = name->getUTF8();
   *_type = type->getUTF8();
}

void Instruction::writeRegForm(FILE *_file, ConstantPoolEntry **_constantPool, int _maxLocals, LocalVariableTableAttribute *_localVariableTableAttribute){
         fprintf(_file, "   ");

   int popBase = stackBase - getPopCount(_constantPool) + _maxLocals;
   int pushBase = popBase;


   switch (byteCode->bytecode){
      case I_NOP: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_NONE, OpSpec_NONE 
         fprintf(_file, "nop");
         break;
      case I_ACONST_NULL: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_null, PopSpec_NONE, PushSpec_N, OpSpec_NONE
         fprintf(_file, "mov obj_%d <- NULL",pushBase);
         break;
      case I_ICONST_M1: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_m1, PopSpec_NONE, PushSpec_I, OpSpec_NONE
         fprintf(_file, "mov i32_%d <- -1",pushBase);
         break;
      case I_ICONST_0: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_0, PopSpec_NONE, PushSpec_I, OpSpec_NONE
         fprintf(_file, "mov i32_%d <- 0",pushBase);
         break;
      case I_ICONST_1: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_1, PopSpec_NONE, PushSpec_I, OpSpec_NONE
         fprintf(_file, "mov i32_%d <- 1",pushBase);
         break;
      case I_ICONST_2: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_2, PopSpec_NONE, PushSpec_I, OpSpec_NONE
         fprintf(_file, "mov i32_%d <- 2",pushBase);
         break;
      case I_ICONST_3: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_3, PopSpec_NONE, PushSpec_I, OpSpec_NONE
         fprintf(_file, "mov 3 i32_%d <- 3",pushBase);
         break;
      case I_ICONST_4: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_4, PopSpec_NONE, PushSpec_I, OpSpec_NONE
         fprintf(_file, "mov i32_%d <- 4",pushBase);
         break;
      case I_ICONST_5: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_5, PopSpec_NONE, PushSpec_I, OpSpec_NONE
         fprintf(_file, "mov i32_%d <- 5",pushBase);
         break;
      case I_LCONST_0: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_0, PopSpec_NONE, PushSpec_L, OpSpec_NONE
         fprintf(_file, "mov i64_%d <- 0",pushBase);
         break;
      case I_LCONST_1: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_1, PopSpec_NONE, PushSpec_L, OpSpec_NONE
         fprintf(_file, "mov i64_%d <- 1",pushBase);
         break;
      case I_FCONST_0: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_0, PopSpec_NONE, PushSpec_F, OpSpec_NONE
         fprintf(_file, "mov f32_%d <- 0.0",pushBase);
         break;
      case I_FCONST_1: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_1, PopSpec_NONE, PushSpec_F, OpSpec_NONE
         fprintf(_file, "mov f32_%d <- 1.0",pushBase);
         break;
      case I_FCONST_2: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_2, PopSpec_NONE, PushSpec_F, OpSpec_NONE
         fprintf(_file, "mov f32_%d <- 2.0",pushBase);
         break;
      case I_DCONST_0: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_0, PopSpec_NONE, PushSpec_D, OpSpec_NONE
         fprintf(_file, "mov f64_%d <- 0.0",pushBase);
         break;
      case I_DCONST_1: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_1, PopSpec_NONE, PushSpec_D, OpSpec_NONE
         fprintf(_file, "mov f64_%d <- 1.0",pushBase);
         break;
      case I_BIPUSH: // LDSpec_NONE, STSpec_NONE, ImmSpec_Bconst, PopSpec_NONE, PushSpec_I, OpSpec_NONE
         fprintf(_file, "mov i32_%d <- %d",pushBase, immSpec_Bconst.value);
         break;
      case I_SIPUSH: // LDSpec_NONE, STSpec_NONE, ImmSpec_Sconst, PopSpec_NONE, PushSpec_I, OpSpec_NONE
         fprintf(_file, "mov i32_%d <- %d",pushBase, immSpec_Sconst.value);
         break;
      case I_LDC: // LDSpec_NONE, STSpec_NONE, ImmSpec_Bcpci, PopSpec_NONE, PushSpec_IorForS, OpSpec_NONE
         fprintf(_file, "LDC !!!!");
         break;
      case I_LDC_W: // LDSpec_NONE, STSpec_NONE, ImmSpec_Scpci, PopSpec_NONE, PushSpec_IorForS, OpSpec_NONE
         fprintf(_file, "LDCW !!!");
         break;
      case I_LDC2_W: // LDSpec_NONE, STSpec_NONE, ImmSpec_Scpci, PopSpec_NONE, PushSpec_LorD, OpSpec_NONE
         fprintf(_file, "LDC2W !!!");
         break;
      case I_ILOAD: // LDSpec_I, STSpec_NONE, ImmSpec_Blvti, PopSpec_NONE, PushSpec_I, OpSpec_NONE
         fprintf(_file, "mov i32_%d <- i32_%d",pushBase, immSpec_Blvti.lvti);
         break;
      case I_LLOAD: // LDSpec_L, STSpec_NONE, ImmSpec_Blvti, PopSpec_NONE, PushSpec_L, OpSpec_NONE
         fprintf(_file, "mov i64_%d <- i64_%d",pushBase, immSpec_Blvti.lvti);
         break;
      case I_FLOAD: // LDSpec_F, STSpec_NONE, ImmSpec_Blvti, PopSpec_NONE, PushSpec_F, OpSpec_NONE
         fprintf(_file, "mov f32_%d <- f32_%d",pushBase, immSpec_Blvti.lvti);
         break;
      case I_DLOAD: // LDSpec_F, STSpec_NONE, ImmSpec_Blvti, PopSpec_NONE, PushSpec_D, OpSpec_NONE
         fprintf(_file, "mov f64_%d <- f64_%d",pushBase, immSpec_Blvti.lvti);
         break;
      case I_ALOAD: // LDSpec_A, STSpec_NONE, ImmSpec_Blvti, PopSpec_NONE, PushSpec_O, OpSpec_NONE
         fprintf(_file, "mov obj_%d <- obj_%d",pushBase, immSpec_Blvti.lvti);
         break;
      case I_ILOAD_0: // LDSpec_I, STSpec_NONE, ImmSpec_NONE_lvti_0, PopSpec_NONE, PushSpec_I, OpSpec_NONE
         fprintf(_file, "mov i32_%d <- i32_0",  pushBase);
         break;
      case I_ILOAD_1: // LDSpec_I, STSpec_NONE, ImmSpec_NONE_lvti_1, PopSpec_NONE, PushSpec_I, OpSpec_NONE
         fprintf(_file, "mov i32_%d <- i32_1",  pushBase);
         break;
      case I_ILOAD_2: // LDSpec_I, STSpec_NONE, ImmSpec_NONE_lvti_2, PopSpec_NONE, PushSpec_I, OpSpec_NONE
         fprintf(_file, "mov i32_%d <- i32_2",  pushBase);
         break;
      case I_ILOAD_3: // LDSpec_I, STSpec_NONE, ImmSpec_NONE_lvti_3, PopSpec_NONE, PushSpec_I, OpSpec_NONE
         fprintf(_file, "mov i32_%d <- i32_3",  pushBase);
         break;
      case I_LLOAD_0: // LDSpec_L, STSpec_NONE, ImmSpec_NONE_lvti_0, PopSpec_NONE, PushSpec_L, OpSpec_NONE
         fprintf(_file, "mov i64_%d <- i64_0",  pushBase);
         break;
      case I_LLOAD_1: // LDSpec_L, STSpec_NONE, ImmSpec_NONE_lvti_1, PopSpec_NONE, PushSpec_L, OpSpec_NONE
         fprintf(_file, "mov i64_%d <- i64_1",  pushBase);
         break;
      case I_LLOAD_2: // LDSpec_L, STSpec_NONE, ImmSpec_NONE_lvti_2, PopSpec_NONE, PushSpec_L, OpSpec_NONE
         fprintf(_file, "mov i64_%d <- i64_2",  pushBase);
         break;
      case I_LLOAD_3: // LDSpec_L, STSpec_NONE, ImmSpec_NONE_lvti_3, PopSpec_NONE, PushSpec_L, OpSpec_NONE
         fprintf(_file, "mov i64_%d <- i64_3",  pushBase);
         break;
      case I_FLOAD_0: // LDSpec_F, STSpec_NONE, ImmSpec_NONE_lvti_0, PopSpec_NONE, PushSpec_F, OpSpec_NONE
         fprintf(_file, "mov f32_%d <- f32_0",  pushBase);
         break;
      case I_FLOAD_1: // LDSpec_F, STSpec_NONE, ImmSpec_NONE_lvti_1, PopSpec_NONE, PushSpec_F, OpSpec_NONE
         fprintf(_file, "mov f32_%d <- f32_1",  pushBase);
         break;
      case I_FLOAD_2: // LDSpec_F, STSpec_NONE, ImmSpec_NONE_lvti_2, PopSpec_NONE, PushSpec_F, OpSpec_NONE
         fprintf(_file, "mov f32_%d <- f32_2",  pushBase);
         break;
      case I_FLOAD_3: // LDSpec_F, STSpec_NONE, ImmSpec_NONE_lvti_3, PopSpec_NONE, PushSpec_F, OpSpec_NONE
         fprintf(_file, "mov f32_%d <- f32_3",  pushBase);
         break;
      case I_DLOAD_0: // LDSpec_D, STSpec_NONE, ImmSpec_NONE_lvti_0, PopSpec_NONE, PushSpec_D, OpSpec_NONE
         fprintf(_file, "mov f64_%d <- f64_0",  pushBase);
         break;
      case I_DLOAD_1: // LDSpec_D, STSpec_NONE, ImmSpec_NONE_lvti_1, PopSpec_NONE, PushSpec_D, OpSpec_NONE
         fprintf(_file, "mov f64_%d <- f64_1",  pushBase);
         break;
      case I_DLOAD_2: // LDSpec_D, STSpec_NONE, ImmSpec_NONE_lvti_2, PopSpec_NONE, PushSpec_D, OpSpec_NONE
         fprintf(_file, "mov f64_%d <- f64_2",  pushBase);
         break;
      case I_DLOAD_3: // LDSpec_D, STSpec_NONE, ImmSpec_NONE_lvti_3, PopSpec_NONE, PushSpec_D, OpSpec_NONE
         fprintf(_file, "mov f64_%d <- f64_3",  pushBase);
         break;
      case I_ALOAD_0: // LDSpec_A, STSpec_NONE, ImmSpec_NONE_lvti_0, PopSpec_NONE, PushSpec_O, OpSpec_NONE
         fprintf(_file, "mov obj_%d <- obj_0",  pushBase);
         break;
      case I_ALOAD_1: // LDSpec_A, STSpec_NONE, ImmSpec_NONE_lvti_1, PopSpec_NONE, PushSpec_O, OpSpec_NONE
         fprintf(_file, "mov obj_%d <- obj_1",  pushBase);
         break;
      case I_ALOAD_2: // LDSpec_A, STSpec_NONE, ImmSpec_NONE_lvti_2, PopSpec_NONE, PushSpec_O, OpSpec_NONE
         fprintf(_file, "mov obj_%d <- obj_2",  pushBase);
         break;
      case I_ALOAD_3: // LDSpec_A, STSpec_NONE, ImmSpec_NONE_lvti_3, PopSpec_NONE, PushSpec_O, OpSpec_NONE
         fprintf(_file, "mov obj_%d <- obj_3",  pushBase);
         break;
      case I_IALOAD: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_I, OpSpec_NONE
         fprintf(_file, "mov i32_%d <- obj_%d[i32_%d] ",  pushBase, popBase+1, popBase);
         break;
      case I_LALOAD: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_L, OpSpec_NONE
         fprintf(_file, "mov i64_%d <- obj_%d[i32_%d]",  pushBase, popBase+1, popBase);
         break;
      case I_FALOAD: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_F, OpSpec_NONE
         fprintf(_file, "mov f32_%d <- obj_%d[i32_%d]",  pushBase, popBase+1, popBase);
         break;
      case I_DALOAD: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_D, OpSpec_NONE
         fprintf(_file, "mov f64_%d <- obj_%d[i32_%d]",  pushBase, popBase+1, popBase);
         break;
      case I_AALOAD: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_A, OpSpec_NONE
         fprintf(_file, "mov obj_%d <- obj_%d[i32_%d]",  pushBase, popBase+1, popBase);
         break;
      case I_BALOAD: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_I, OpSpec_NONE
         fprintf(_file, "mov i8_%d <- obj_%d[i32_%d]",  pushBase, popBase+1, popBase);
         break;
      case I_CALOAD: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_I, OpSpec_NONE
         fprintf(_file, "mov u16_%d <- obj_%d[i32_%d]",  pushBase, popBase+1, popBase);
         break;
      case I_SALOAD: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_I, OpSpec_NONE
         fprintf(_file, "mov i16_%d <- obj_%d[i32_%d]",  pushBase, popBase+1, popBase);
         break;
      case I_ISTORE: // LDSpec_NONE, STSpec_I, ImmSpec_Blvti, PopSpec_I, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov i32_%d <- i32_%d",  immSpec_Blvti.lvti, popBase);
         break;
      case I_LSTORE: // LDSpec_NONE, STSpec_L, ImmSpec_Blvti, PopSpec_L, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov i64_%d <- i64_%d",  immSpec_Blvti.lvti, popBase);
         break;
      case I_FSTORE: // LDSpec_NONE, STSpec_F, ImmSpec_Blvti, PopSpec_F, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov f32_%d <- f32_%d",  immSpec_Blvti.lvti, popBase);
         break;
      case I_DSTORE: // LDSpec_NONE, STSpec_D, ImmSpec_Blvti, PopSpec_D, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov f64_%d <- f64_%d",  immSpec_Blvti.lvti, popBase);
         break;
      case I_ASTORE: // LDSpec_NONE, STSpec_A, ImmSpec_Blvti, PopSpec_O, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov obj_%d <- obj_%d",  immSpec_Blvti.lvti, popBase);
         break;
      case I_ISTORE_0: // LDSpec_NONE, STSpec_I, ImmSpec_NONE_lvti_0, PopSpec_I, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov i32_0 <- i32_%d", popBase  );
         break;
      case I_ISTORE_1: // LDSpec_NONE, STSpec_I, ImmSpec_NONE_lvti_1, PopSpec_I, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov i32_1 <- i32_%d",  popBase);
         break;
      case I_ISTORE_2: // LDSpec_NONE, STSpec_I, ImmSpec_NONE_lvti_2, PopSpec_I, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov i32_2 <- i32_%d",  popBase);
         break;
      case I_ISTORE_3: // LDSpec_NONE, STSpec_I, ImmSpec_NONE_lvti_3, PopSpec_I, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov i32_3 <- i32_%d", popBase);
         break;
      case I_LSTORE_0: // LDSpec_NONE, STSpec_L, ImmSpec_NONE_lvti_0, PopSpec_L, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov i64_0 <- i64_%d",  popBase);
         break;
      case I_LSTORE_1: // LDSpec_NONE, STSpec_L, ImmSpec_NONE_lvti_1, PopSpec_L, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov i64_1 <- i64_5d",  popBase);
         break;
      case I_LSTORE_2: // LDSpec_NONE, STSpec_L, ImmSpec_NONE_lvti_2, PopSpec_L, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov i64_2 <- i64_%d",  popBase);
         break;
      case I_LSTORE_3: // LDSpec_NONE, STSpec_L, ImmSpec_NONE_lvti_3, PopSpec_L, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov i64_3 <- i64_%d",  popBase);
         break;
      case I_FSTORE_0: // LDSpec_NONE, STSpec_F, ImmSpec_NONE_lvti_0, PopSpec_F, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov f32_0 <- f32_%d",  popBase);
         break;
      case I_FSTORE_1: // LDSpec_NONE, STSpec_F, ImmSpec_NONE_lvti_1, PopSpec_F, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov f32_1 <- f32_%d",  popBase);
         break;
      case I_FSTORE_2: // LDSpec_NONE, STSpec_F, ImmSpec_NONE_lvti_2, PopSpec_F, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov f32_2 <- f32_%d",  popBase);
         break;
      case I_FSTORE_3: // LDSpec_NONE, STSpec_F, ImmSpec_NONE_lvti_3, PopSpec_F, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov f32_3 <- f32_%d",  popBase);
         break;
      case I_DSTORE_0: // LDSpec_NONE, STSpec_D, ImmSpec_NONE_lvti_0, PopSpec_D, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov f64_0 <- f64_%d",  popBase);
         break;
      case I_DSTORE_1: // LDSpec_NONE, STSpec_D, ImmSpec_NONE_lvti_1, PopSpec_D, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov f64_1 <- f64_%d",  popBase);
         break;
      case I_DSTORE_2: // LDSpec_NONE, STSpec_D, ImmSpec_NONE_lvti_2, PopSpec_D, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov f64_2 <- f64_%d",  popBase);
         break;
      case I_DSTORE_3: // LDSpec_NONE, STSpec_D, ImmSpec_NONE_lvti_3, PopSpec_D, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov f64_3 <- f64_%d",  popBase);
         break;
      case I_ASTORE_0: // LDSpec_NONE, STSpec_A, ImmSpec_NONE_lvti_0, PopSpec_O, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov obj_0 <- obj_%d",  popBase);
         break;
      case I_ASTORE_1: // LDSpec_NONE, STSpec_A, ImmSpec_NONE_lvti_1, PopSpec_O, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov obj_1 <- obj_%d",  popBase);
         break;
      case I_ASTORE_2: // LDSpec_NONE, STSpec_A, ImmSpec_NONE_lvti_2, PopSpec_O, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov obj_2 <- obj_%d",  popBase);
         break;
      case I_ASTORE_3: // LDSpec_NONE, STSpec_A, ImmSpec_NONE_lvti_3, PopSpec_O, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov obj_3 <- obj_%d",  popBase);
         break;
      case I_IASTORE: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AII, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov obj_%d[i32_%d] <- mov i32_%d",  popBase, popBase+1, popBase+2);
         break;
      case I_LASTORE: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AIL, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov obj_%d[i32_%d] <- i64_%d",  popBase, popBase+1, popBase+2);
         break;
      case I_FASTORE: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AIF, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov obj_%d[i32_%d] <- f32_%d",  popBase, popBase+1, popBase+2);
         break;
      case I_DASTORE: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AID, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov obj_%d[i32_%d] <- f64_%d ",  popBase, popBase+1, popBase+2);
         break;
      case I_AASTORE: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AIO, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov obj_%d[i32_%d] <- obj_%d",  popBase, popBase+1, popBase+2);
         break;
      case I_BASTORE: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AIB, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov obj_%d[i32_%d] <- i8_%d",  popBase, popBase+1, popBase+2);
         break;
      case I_CASTORE: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AIC, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov obj_%d[i32_%d] <- u16_%d",  popBase, popBase+1, popBase+2);
         break;
      case I_SASTORE: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AIS, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov obj_%d[i32_%d] <- i16_%d",  popBase, popBase+1, popBase+2);
         break;
      case I_POP: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov i32_%d -> i32_%d ; // Pop artifact essentially a NOP in register form",  popBase, popBase, popBase);
         break;
      case I_POP2: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "mov i32_%d -> i32_%d; mov i32_%d -> i32_%d // Pop artifact essentially a NOP in register form",  popBase, popBase, popBase+1, popBase+1);
         break;
      case I_DUP: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_II, OpSpec_NONE
         fprintf(_file, "mov i32_%d -> i32_%d; mov i32_%d -> i32_%d // DUP artifact ",  popBase, pushBase, popBase, popBase+1);
         break;
      case I_DUP_X1: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_III, OpSpec_NONE
         break;
      case I_DUP_X2: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_III, PushSpec_IIII, OpSpec_NONE
         break;
      case I_DUP2: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_IIII, OpSpec_NONE
         break;
      case I_DUP2_X1: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_III, PushSpec_IIIII, OpSpec_NONE
         break;
      case I_DUP2_X2: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_IIII, PushSpec_IIIIII, OpSpec_NONE
         break;
      case I_SWAP: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_II, OpSpec_NONE
         fprintf(_file, "mov i32_%d -> i32_%d; mov i32_%d -> i32_%d; mov i32_%d -> mov i32_%d // SWAP artifact ",
               popBase, pushBase+2, popBase+1, pushBase, popBase+2, pushBase+1 );
         break;
      case I_IADD: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_Add
         fprintf(_file, "add i32_%d <- i32_%d, i32_%d ",  pushBase, popBase+1, popBase);
         break;
      case I_LADD: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_Add
         fprintf(_file, "add i64_%d <-  i64_%d , i64_%d",  pushBase, popBase+1, popBase);
         break;
      case I_FADD: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_F, OpSpec_Add
         fprintf(_file, "add f32_%d <- f32_%d , f32_%d",  pushBase, popBase+1, popBase);
         break;
      case I_DADD: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_D, OpSpec_Add
         fprintf(_file, "add f64_%d <- f64_%d , f64_%d",  pushBase, popBase+1, popBase);
         break;
      case I_ISUB: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_Sub
         fprintf(_file, "sub i32_%d <-  i32_%d , i32_%d",  pushBase, popBase+1, popBase);
         break;
      case I_LSUB: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_Sub
         fprintf(_file, "sub i64_%d <- i64_%d , i64_%d",  pushBase, popBase+1, popBase);
         break;
      case I_FSUB: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_F, OpSpec_Sub
         fprintf(_file, "sub f32_%d <- f32_%d , f32_%d",  pushBase, popBase+1, popBase);
         break;
      case I_DSUB: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_D, OpSpec_Sub
         fprintf(_file, "sub f64_%d <-  f64_%d, f64_%d",  pushBase, popBase+1, popBase);
         break;
      case I_IMUL: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_Mul
         fprintf(_file, "mul i32_%d <-  i32_%d, i32_%d",  pushBase, popBase+1, popBase);
         break;
      case I_LMUL: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_Mul
         fprintf(_file, "mul i64_%d <- i64_%d , i64_%d",  pushBase, popBase+1, popBase);
         break;
      case I_FMUL: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_F, OpSpec_Mul
         fprintf(_file, "mul f32_%d, <- f32_%d, f32_%d",  pushBase, popBase+1, popBase);
         break;
      case I_DMUL: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_D, OpSpec_Mul
         fprintf(_file, "mul f64_%d, <- f64_%d, f64_%d",  pushBase, popBase+1, popBase);
         break;
      case I_IDIV: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_Div
         fprintf(_file, "div i32_%d <-  i32_%d, i32_%d",  pushBase, popBase+1, popBase);
         break;
      case I_LDIV: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_Div
         fprintf(_file, "div i64_%d <- i64_%d , i64_%d",  pushBase, popBase+1, popBase);
         break;
      case I_FDIV: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_F, OpSpec_Div
         fprintf(_file, "div f32_%d, <- f32_%d, f32_%d",  pushBase, popBase+1, popBase);
         break;
      case I_DDIV: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_D, OpSpec_Div
         fprintf(_file, "div f64_%d, <- f64_%d, f64_%d",  pushBase, popBase+1, popBase);
         break;
      case I_IREM: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_Rem
         fprintf(_file, "rem i32_%d <-  i32_%d, i32_%d",  pushBase, popBase+1, popBase);
         break;
      case I_LREM: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_Rem
         fprintf(_file, "rem i64_%d <- i64_%d , i64_%d",  pushBase, popBase+1, popBase);
         break;
      case I_FREM: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_F, OpSpec_Rem
         fprintf(_file, "rem f32_%d, <- f32_%d, f32_%d",  pushBase, popBase+1, popBase);
         break;
      case I_DREM: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_D, OpSpec_Rem
         fprintf(_file, "rem f64_%d, <- f64_%d, f64_%d",  pushBase, popBase+1, popBase);
         break;
      case I_INEG: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_I, OpSpec_Neg
         fprintf(_file, "neg i32_%d, <- i32_%d",  pushBase, popBase);
         break;
      case I_LNEG: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_L, PushSpec_L, OpSpec_Neg
         fprintf(_file, "neg i64_%d, <- i64_%d",  pushBase, popBase);
         break;
      case I_FNEG: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_F, PushSpec_F, OpSpec_Neg
         fprintf(_file, "neg f32_%d, <- f32_%d",  pushBase, popBase);
         break;
      case I_DNEG: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_D, PushSpec_D, OpSpec_Neg
         fprintf(_file, "neg f64_%d, <- f64_%d",  pushBase, popBase);
         break;
      case I_ISHL: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_LeftShift
         break;
      case I_LSHL: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LI, PushSpec_L, OpSpec_LeftShift
         break;
      case I_ISHR: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_LogicalRightShift
         break;
      case I_LSHR: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LI, PushSpec_L, OpSpec_LogicalRightShift
         break;
      case I_IUSHR: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_ArithmeticRightShift
         break;
      case I_LUSHR: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LI, PushSpec_L, OpSpec_ArithmeticRightShift
         break;
      case I_IAND: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_BitwiseAnd
         break;
      case I_LAND: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_BitwiseAnd
         break;
      case I_IOR: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_BitwiseOr
         break;
      case I_LOR: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_BitwiseOr
         break;
      case I_IXOR: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_BitwiseXor
         break;
      case I_LXOR: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_BitwiseXor
         break;
      case I_IINC: // LDSpec_I, STSpec_I, ImmSpec_BlvtiBconst, PopSpec_NONE, PushSpec_NONE, OpSpec_NONE
         break;
      case I_I2L: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_L, OpSpec_I2LCast
         break;
      case I_I2F: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_F, OpSpec_I2FCast
         break;
      case I_I2D: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_D, OpSpec_I2DCast
         break;
      case I_L2I: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_L, PushSpec_I, OpSpec_L2ICast
         break;
      case I_L2F: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_L, PushSpec_F, OpSpec_L2FCast
         break;
      case I_L2D: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_L, PushSpec_D, OpSpec_L2DCast
         break;
      case I_F2I: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_F, PushSpec_I, OpSpec_F2ICast
         break;
      case I_F2L: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_F, PushSpec_L, OpSpec_F2LCast
         break;
      case I_F2D: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_F, PushSpec_D, OpSpec_F2DCast
         break;
      case I_D2I: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_D, PushSpec_I, OpSpec_D2ICast
         break;
      case I_D2L: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_D, PushSpec_L, OpSpec_D2LCast
         break;
      case I_D2F: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_D, PushSpec_F, OpSpec_D2FCast
         break;
      case I_I2B: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_I, OpSpec_I2BCast
         break;
      case I_I2C: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_I, OpSpec_I2CCast
         break;
      case I_I2S: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_I, OpSpec_I2SCast
         break;
      case I_LCMP: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_I, OpSpec_Sub
         break;
      case I_FCMPL: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_I, OpSpec_LessThan
         break;
      case I_FCMPG: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_I, OpSpec_GreaterThan
         break;
      case I_DCMPL: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_I, OpSpec_LessThan
         break;
      case I_DCMPG: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_I, OpSpec_GreaterThan
         break;
      case I_IFEQ: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, PushSpec_NONE, OpSpec_Equal
         fprintf(_file, "if_eq i32_%d",  popBase);//, immSpec_Spc.pc);
         break;
      case I_IFNE: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, PushSpec_NONE, OpSpec_NotEqual
         fprintf(_file, "if_ne i32_%d",  popBase);//, immSpec_Spc.pc);
         break;
      case I_IFLT: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, PushSpec_NONE, OpSpec_LessThan
         fprintf(_file, "if_lt i32_%d",  popBase);//, immSpec_Spc.pc);
         break;
      case I_IFGE: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, PushSpec_NONE, OpSpec_GreaterThanOrEqual
         fprintf(_file, "if_ge i32_%d",  popBase);//, immSpec_Spc.pc);
         break;
      case I_IFGT: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, PushSpec_NONE, OpSpec_GreaterThan
         fprintf(_file, "if_gt i32_%d",  popBase);//, immSpec_Spc.pc);
         break;
      case I_IFLE: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, PushSpec_NONE, OpSpec_LessThanOrEqual
         fprintf(_file, "if_le i32_%d",  popBase);//, immSpec_Spc.pc);
         break;
      case I_IF_ICMPEQ: // LDSpec_NONE, STSpec_NONE, ImmSpec_Sconst, PopSpec_II, PushSpec_NONE, OpSpec_Equal
         break;
      case I_IF_ICMPNE: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_II, PushSpec_NONE, OpSpec_NotEqual
         break;
      case I_IF_ICMPLT: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_II, PushSpec_NONE, OpSpec_LessThan
         break;
      case I_IF_ICMPGE: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_II, PushSpec_NONE, OpSpec_GreaterThanOrEqual
         fprintf(_file, "if_icmpge f32_%d, f32_%d",  popBase, popBase+1);//, immSpec_Spc.pc);
         break;
      case I_IF_ICMPGT: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_II, PushSpec_NONE, OpSpec_GreaterThan
         break;
      case I_IF_ICMPLE: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_II, PushSpec_NONE, OpSpec_LessThanOrEqual
         break;
      case I_IF_ACMPEQ: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_OO, PushSpec_NONE, OpSpec_Equal
         break;
      case I_IF_ACMPNE: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_OO, PushSpec_NONE, OpSpec_NotEqual
         break;
      case I_GOTO: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_NONE, PushSpec_NONE, OpSpec_NONE 
         fprintf(_file, "goto");//,  immSpec_Spc.pc);
         break;
      case I_JSR: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_NONE, PushSpec_RA, OpSpec_NONE
         break;
      case I_RET: // LDSpec_NONE, STSpec_NONE, ImmSpec_Blvti, PopSpec_NONE, PushSpec_NONE, OpSpec_NONE
         break;
      case I_TABLESWITCH: // LDSpec_NONE, STSpec_NONE, ImmSpec_UNKNOWN, PopSpec_I, PushSpec_NONE, OpSpec_NONE
         break;
      case I_LOOKUPSWITCH: // LDSpec_NONE, STSpec_NONE, ImmSpec_UNKNOWN, PopSpec_I, PushSpec_NONE, OpSpec_NONE
         break;
      case I_IRETURN: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "return i32_%d", popBase);
         break;
      case I_LRETURN: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_L, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "return i64_%d", popBase);
         break;
      case I_FRETURN: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_F, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "return f32_%d", popBase);
         break;
      case I_DRETURN: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_D, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "return f64_%d", popBase);
         break;
      case I_ARETURN: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_O, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "return obj_%d", popBase);
         break;
      case I_RETURN: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_NONE, OpSpec_NONE
         fprintf(_file, "return");
         break;
      case I_GETSTATIC: // LDSpec_NONE, STSpec_NONE, ImmSpec_Scpfi, PopSpec_NONE, PushSpec_FSIG, OpSpec_NONE
         break;
      case I_PUTSTATIC: // LDSpec_NONE, STSpec_NONE, ImmSpec_Scpfi, PopSpec_FSIG, PushSpec_NONE, OpSpec_NONE
         break;
      case I_GETFIELD: // LDSpec_NONE, STSpec_NONE, ImmSpec_Scpfi, PopSpec_O, PushSpec_FSIG, OpSpec_NONE
         {
            FieldConstantPoolEntry* field = (FieldConstantPoolEntry*)_constantPool[immSpec_Scpfi.cpfi];
            NameAndTypeConstantPoolEntry* nameAndType = (NameAndTypeConstantPoolEntry*)_constantPool[field->getNameAndTypeIndex()];
            UTF8ConstantPoolEntry* name = (UTF8ConstantPoolEntry*)_constantPool[nameAndType->getNameIndex()];
            UTF8ConstantPoolEntry* type = (UTF8ConstantPoolEntry*)_constantPool[nameAndType->getDescriptorIndex()];
            fprintf(_file, "mov_field ");
            char *ptr = type->getUTF8();
            switch (*ptr){
               case 'I': fprintf(_file, "i32_%d", pushBase);break;
               case 'H': fprintf(_file, "i16_%d", pushBase);break;
               case 'C': fprintf(_file, "u16_%d", pushBase);break;
               case 'B': fprintf(_file, "i8_%d", pushBase);break;
               case 'F': fprintf(_file, "f32_%d", pushBase);break;
               case 'D': fprintf(_file, "f64_%d", pushBase);break;
               case 'Z': fprintf(_file, "bit_%d", pushBase);break;
               case 'J': fprintf(_file, "i64_%d", pushBase);break;
               case 'L': fprintf(_file, "obj_%d", pushBase);break;
               case '[': fprintf(_file, "obj_%d", pushBase);break;
               default: fprintf(_file, "arg_%d", pushBase);break;
            }
            fprintf(_file, " <- obj_%d.%s", popBase, name->getUTF8() );
            break;
         }
      case I_PUTFIELD: // LDSpec_NONE, STSpec_NONE, ImmSpec_Scpfi, PopSpec_OFSIG, PushSpec_NONE, OpSpec_NONE
         break;
      case I_INVOKEVIRTUAL: // LDSpec_NONE, STSpec_NONE, ImmSpec_Scpmi, PopSpec_OMSIG, PushSpec_MSIG, OpSpec_NONE
         {
            MethodConstantPoolEntry* method = (MethodConstantPoolEntry*)_constantPool[immSpec_Scpmi.cpmi];
            char *name, *type;
            getNameAndType(method, _constantPool, &name, &type);
            fprintf(_file, "call_v ");
            int retc = method->getRetCount(_constantPool);
            if (retc>0){
               char *ptr = type;
               while (ptr && *ptr && *ptr != ')'){
                  ptr++;
               }
               ptr++;
               switch (*ptr){
                  case 'I': fprintf(_file, "i32_%d", pushBase);break;
                  case 'H': fprintf(_file, "i16_%d", pushBase);break;
                  case 'C': fprintf(_file, "u16_%d", pushBase);break;
                  case 'B': fprintf(_file, "i8_%d", pushBase);break;
                  case 'F': fprintf(_file, "f32_%d", pushBase);break;
                  case 'D': fprintf(_file, "f64_%d", pushBase);break;
                  case 'Z': fprintf(_file, "bit_%d", pushBase);break;
                  case 'J': fprintf(_file, "i64_%d", pushBase);break;
                  case 'L': fprintf(_file, "obj_%d", pushBase);break;
                  case '[': fprintf(_file, "obj_%d", pushBase);break;
                  default: fprintf(_file, "arg_%d", pushBase);break;
               }
               fprintf(_file, " <- ");
            }
            fprintf(_file, "obj_%d.%s(",popBase, name);
            int argc = method->getArgCount(_constantPool);
            if (argc>0){
               for (int i=0; i<argc; i++){
                  if (i>0){
                     fprintf(_file, ", ");
                  }
                  fprintf(_file, "arg_%d",popBase+i+1);
               }
            }
            fprintf(_file, ")");
         }
         break;
      case I_INVOKESPECIAL: // LDSpec_NONE, STSpec_NONE, ImmSpec_Scpmi, PopSpec_OMSIG, PushSpec_MSIG, OpSpec_NONE
         break;
      case I_INVOKESTATIC: // LDSpec_NONE, STSpec_NONE, ImmSpec_Scpmi, PopSpec_MSIG, PushSpec_MSIG, OpSpec_NONE
         break;
      case I_INVOKEINTERFACE: // LDSpec_NONE, STSpec_NONE, ImmSpec_ScpmiBB, PopSpec_OMSIG, PushSpec_MSIG, OpSpec_NONE
         break;
      case I_INVOKEDYNAMIC: // LDSpec_NONE, STSpec_NONE, ImmSpec_ScpmiBB, PopSpec_OMSIG, PushSpec_MSIG, OpSpec_NONE
         break;
      case I_NEW: // LDSpec_NONE, STSpec_NONE, ImmSpec_Scpci, PopSpec_NONE, PushSpec_O, OpSpec_NONE
         break;
      case I_NEWARRAY: // LDSpec_NONE, STSpec_NONE, ImmSpec_Bconst, PopSpec_I, PushSpec_A, OpSpec_NONE
         break;
      case I_ANEWARRAY: // LDSpec_NONE, STSpec_NONE, ImmSpec_Sconst, PopSpec_I, PushSpec_A, OpSpec_NONE
         break;
      case I_ARRAYLENGTH: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_A, PushSpec_I, OpSpec_NONE
         break;
      case I_ATHROW: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_O, PushSpec_O, OpSpec_NONE
         break;
      case I_CHECKCAST: // LDSpec_NONE, STSpec_NONE, ImmSpec_Scpci, PopSpec_O, PushSpec_O, OpSpec_NONE
         break;
      case I_INSTANCEOF: // LDSpec_NONE, STSpec_NONE, ImmSpec_Scpci, PopSpec_O, PushSpec_I, OpSpec_NONE
         break;
      case I_MONITORENTER: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_O, PushSpec_NONE, OpSpec_NONE
         break;
      case I_MONITOREXIT: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_O, PushSpec_NONE, OpSpec_NONE
         break;
      case I_WIDE: // LDSpec_NONE, STSpec_NONE, ImmSpec_UNKNOWN, PopSpec_UNKNOWN, PushSpec_UNKNOWN, OpSpec_NONE
         break;
      case I_MULTIANEWARRAY: // LDSpec_NONE, STSpec_NONE, ImmSpec_ScpciBdim, PopSpec_UNKNOWN, PushSpec_A, OpSpec_NONE
         break;
      case I_IFNULL: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_O, PushSpec_NONE, OpSpec_EqualNULL
         break;
      case I_IFNONNULL: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_O, PushSpec_NONE, OpSpec_NotEqualNULL
         break;
      case I_GOTO_W: // LDSpec_NONE, STSpec_NONE, ImmSpec_Ipc, PopSpec_O, PushSpec_NONE, OpSpec_NotEqualNULL
         break;
      case I_JSR_W: // LDSpec_NONE, STSpec_NONE, ImmSpec_Ipc, PopSpec_NONE, PushSpec_RA, OpSpec_NONE
         break;
   }
}
