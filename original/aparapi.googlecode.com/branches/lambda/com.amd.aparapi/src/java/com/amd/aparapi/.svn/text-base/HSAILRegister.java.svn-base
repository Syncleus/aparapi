package com.amd.aparapi;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: gfrost
 * Date: 6/4/13
 * Time: 2:00 PM
 * To change this template use File | Settings | File Templates.
 */

abstract class HSAILOperand<R extends HSAILOperand<R,T>, T extends PrimitiveType>{
   protected T type;
   HSAILOperand(R original){
      type = original.type;
   }
   HSAILOperand(T _type){
      type = _type;
   }
   public abstract R cloneMe();
}

abstract class HSAILConst<R extends HSAILConst<R,T,C>, T extends PrimitiveType, C extends Object> extends HSAILOperand<R,T>{
   protected C value;
   HSAILConst(R original){
      super(original);
      value = original.value;
   }
   HSAILConst(T _type, C _value){
      super(_type);
      value = _value;

   }
   public abstract R cloneMe();
}

class HSAILConst_f32 extends HSAILConst<HSAILConst_f32, f32,Float>{

   HSAILConst_f32(HSAILConst_f32 original){
      super(original);

   }
   HSAILConst_f32(Float _value){
      super(PrimitiveType.f32, _value);
   }
   public HSAILConst_f32 cloneMe(){
      return (new HSAILConst_f32(value));
   }
}

class HSAILConst_s32 extends HSAILConst<HSAILConst_s32, s32,Integer>{

   HSAILConst_s32(HSAILConst_s32 original){
      super(original);

   }
   HSAILConst_s32(Integer _value){
      super(PrimitiveType.s32, _value);
   }
   public HSAILConst_s32 cloneMe(){
      return (new HSAILConst_s32(value));
   }
}
class HSAILConst_f64 extends HSAILConst<HSAILConst_f64, f64,Double>{

   HSAILConst_f64(HSAILConst_f64 original){
      super(original);

   }
   HSAILConst_f64(Double _value){
      super(PrimitiveType.f64, _value);
   }
   public HSAILConst_f64 cloneMe(){
      return (new HSAILConst_f64(value));
   }
}

class HSAILConst_s64 extends HSAILConst<HSAILConst_s64, s64,Long>{

   HSAILConst_s64(HSAILConst_s64 original){
      super(original);

   }
   HSAILConst_s64(Long _value){
      super(PrimitiveType.s64, _value);
   }
   public HSAILConst_s64 cloneMe(){
      return (new HSAILConst_s64(value));
   }
}
public abstract class HSAILRegister<R extends HSAILRegister<R,T>, T extends PrimitiveType> extends HSAILOperand<R,T>{
   int index;
   HSAILRegister(R original){
      super(original);
       index = original.index;
   }
   HSAILRegister(int _index, T _type){
      super(_type);
      index = _index;
   }

   @Override
   public boolean equals(Object _other){
      if(_other instanceof HSAILRegister){
         HSAILRegister otherReg = (HSAILRegister) _other;
         return (type.equals(otherReg.type) && index == otherReg.index);
      }
      return false;
   }

    public abstract R cloneMe();
}




abstract class StackReg<R extends StackReg<R,T>, T extends PrimitiveType> extends HSAILRegister<R,T>{
    StackReg(int _index, T _type){
        super(_index, _type);
    }
    StackReg(Instruction _from, int _offset, T _type){
        super( _from.getPreStackBase() + _from.getMethod().getCodeEntry().getMaxLocals() + _offset, _type);
    }
    StackReg(R original){
        super(original);
    }
}


class StackReg_f64 extends StackReg<StackReg_f64, f64>{
    StackReg_f64(StackReg_f64 original){
        super(original);
    }
    @Override public StackReg_f64 cloneMe(){
        return(new StackReg_f64(this));
    }
 //  StackReg_f64(Instruction _from, int _stackBase, int _offset){
  //    super( _from,  + _stackBase + _offset, PrimitiveType.f64);
 //  }
    StackReg_f64(int _idx){
        super(_idx,  PrimitiveType.f64);
    }
}

class StackReg_f32 extends StackReg<StackReg_f32, f32>{
    StackReg_f32(StackReg_f32 original){
        super(original);
    }
    @Override public StackReg_f32 cloneMe(){
        return(new StackReg_f32(this));
    }
//   StackReg_f32(Instruction _from, int _stackBase, int _offset){
 //     super(_from, _stackBase + _offset, PrimitiveType.f32);
 //  }
    StackReg_f32(int _idx){
        super(_idx, PrimitiveType.f32);
    }
}

class StackReg_s64 extends StackReg<StackReg_s64, s64>{
    StackReg_s64(StackReg_s64 original){
        super(original);
    }
    @Override public StackReg_s64 cloneMe(){
        return(new StackReg_s64(this));
    }
  // StackReg_s64(Instruction _from, int _stackBase,int _offset){
   //   super(_from,  _stackBase + _offset, PrimitiveType.s64);
  // }
    StackReg_s64(int _idx){
        super(_idx, PrimitiveType.s64);
    }
}

class StackReg_u64 extends StackReg<StackReg_u64, u64>{
    StackReg_u64(StackReg_u64 original){
        super(original);
    }
    @Override public StackReg_u64 cloneMe(){
        return(new StackReg_u64(this));
    }
  // StackReg_u64(Instruction _from, int _stackBase,int _offset){
   //   super(_from,  _stackBase+ _offset, PrimitiveType.u64);
 //  }
    StackReg_u64(int _idx){
        super(_idx, PrimitiveType.u64);
    }
}

class StackReg_s32 extends StackReg<StackReg_s32, s32>{
    StackReg_s32(StackReg_s32 original){
        super(original);
    }
    @Override public StackReg_s32 cloneMe(){
        return(new StackReg_s32(this));
    }
//   StackReg_s32(Instruction _from, int _stackBase,int _offset){
   //   super(_from,  _stackBase + _offset, PrimitiveType.s32);
  // }
    StackReg_s32(int _idx){
        super(_idx, PrimitiveType.s32);
    }
}

class StackReg_s16 extends StackReg<StackReg_s16, s16>{
    StackReg_s16(StackReg_s16 original){
        super(original);
    }
    @Override public StackReg_s16 cloneMe(){
        return(new StackReg_s16(this));
    }
//   StackReg_s16(Instruction _from, int _stackBase,int _offset){
  //    super(_from,  _stackBase + _offset, PrimitiveType.s16);
 //  }
    StackReg_s16(int _idx){
        super(_idx, PrimitiveType.s16);
    }
}

class StackReg_u16 extends StackReg<StackReg_u16, u16>{
    StackReg_u16(StackReg_u16 original){
        super(original);
    }
    @Override public StackReg_u16 cloneMe(){
        return(new StackReg_u16(this));
    }
 //  StackReg_u16(Instruction _from, int _stackBase,int _offset){
   //   super(_from,  _stackBase + _offset, PrimitiveType.u16);
  // }
    StackReg_u16(int _idx){
        super(_idx, PrimitiveType.u16);
    }
}

class StackReg_s8 extends StackReg<StackReg_s8, s8>{
    StackReg_s8(StackReg_s8 original){
        super(original);
    }
    @Override public StackReg_s8 cloneMe(){
        return(new StackReg_s8(this));
    }
  // StackReg_s8(Instruction _from, int _stackBase,int _offset){
   //   super(_from,  _stackBase+ _offset, PrimitiveType.s8);
 //  }
    StackReg_s8(int _idx){
        super(_idx, PrimitiveType.s8);
    }
}



class StackReg_ref extends StackReg<StackReg_ref, ref>{
    StackReg_ref(StackReg_ref original){
        super(original);
    }
    @Override public StackReg_ref cloneMe(){
        return(new StackReg_ref(this));
    }
 //  StackReg_ref(Instruction _from, int _stackBase,int _offset){
 //     super(_from,  _stackBase + _offset, PrimitiveType.ref);
 //  }
    StackReg_ref(int _idx){
        super(_idx, PrimitiveType.ref);
    }
}

abstract class VarReg<R extends VarReg<R,T>, T extends PrimitiveType> extends HSAILRegister<R,T>{
    VarReg(int _index, T _type){
        super(_index, _type);
    }
    VarReg(R original){
        super(original);
    }
    VarReg(Instruction _from, int offset, T _type){
        super(_from.asLocalVariableAccessor().getLocalVariableTableIndex()+offset, _type);
    }
}
class VarReg_f64 extends VarReg<VarReg_f64, f64>{
    VarReg_f64(VarReg_f64 original){
        super(original);
    }
    @Override public VarReg_f64 cloneMe(){
        return(new VarReg_f64(this));
    }
   VarReg_f64(Instruction _from, int _stackBase){
      super(_from, _stackBase, PrimitiveType.f64);
   }
    public VarReg_f64(int _index){
        super(_index,  PrimitiveType.f64);
    }
}

class VarReg_s64 extends VarReg<VarReg_s64, s64>{
    VarReg_s64(VarReg_s64 original){
        super(original);
    }
    @Override public VarReg_s64 cloneMe(){
        return(new VarReg_s64(this));
    }
   VarReg_s64(Instruction _from, int _stackBase){
      super(_from, _stackBase, PrimitiveType.s64);
   }
    public VarReg_s64(int _index){
        super(_index, PrimitiveType.s64);
    }
}

class VarReg_u64 extends VarReg<VarReg_u64, u64>{
    VarReg_u64(VarReg_u64 original){
        super(original);
    }
    @Override public VarReg_u64 cloneMe(){
        return(new VarReg_u64(this));
    }
   VarReg_u64(Instruction _from, int _stackBase){
      super(_from, _stackBase, PrimitiveType.u64);
   }
    public VarReg_u64(int _index){
        super(_index, PrimitiveType.u64);
    }
}

class VarReg_ref extends VarReg<VarReg_ref, ref>{
    VarReg_ref(VarReg_ref original){
        super(original);
    }
    @Override public VarReg_ref cloneMe(){
        return(new VarReg_ref(this));
    }
   VarReg_ref(Instruction _from, int _stackBase){
      super(_from, _stackBase, PrimitiveType.ref);
   }

   public VarReg_ref(int _index){
      super(_index, PrimitiveType.ref);
   }
}

class VarReg_s32 extends VarReg<VarReg_s32, s32>{
    VarReg_s32(VarReg_s32 original){
        super(original);
    }
    @Override public VarReg_s32 cloneMe(){
        return(new VarReg_s32(this));
    }
   VarReg_s32(Instruction _from, int _stackBase){
      super(_from, _stackBase, PrimitiveType.s32);
   }

   public VarReg_s32(int _index){
      super(_index, PrimitiveType.s32);
   }
}

class VarReg_f32 extends VarReg<VarReg_f32,f32>{
    VarReg_f32(VarReg_f32 original){
        super(original);
    }
    @Override public VarReg_f32 cloneMe(){
        return(new VarReg_f32(this));
    }
   VarReg_f32(Instruction _from, int _stackBase){
      super(_from, _stackBase, PrimitiveType.f32);
   }

   public VarReg_f32(int _index){
      super(_index, PrimitiveType.f32);
   }
}

