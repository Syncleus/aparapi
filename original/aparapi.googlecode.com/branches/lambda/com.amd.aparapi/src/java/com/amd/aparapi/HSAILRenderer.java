package com.amd.aparapi;

/**
 * Created with IntelliJ IDEA.
 * User: gfrost
 * Date: 4/27/13
 * Time: 9:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class HSAILRenderer extends TextRenderer<HSAILRenderer>{


   public HSAILRenderer label(String location){
      return (append(String.format("@L%s", location)));
   }

   public HSAILRenderer array_base_offset(){
      return (append(UnsafeWrapper.arrayBaseOffset(int[].class)));
   }
    public HSAILRenderer array_len_offset(){
        return (append((UnsafeWrapper.arrayBaseOffset(int[].class)-8)));
    }
   public HSAILRenderer separator(){
      return (commaSpace());
   }

   public HSAILRenderer typeName(HSAILOperand _reg){
      return (this.append(_reg.type.getHSAName()));
   }

   public HSAILRenderer movTypeName(HSAILOperand _reg){
      return (this.append("b" + _reg.type.getHsaBits()));
   }


    public HSAILRenderer typeName(TypeHelper.JavaType _javaType){
        if (_javaType.isDouble()) {
            this.append("f64");
        } else if (_javaType.isFloat()) {
            this.append("f32");
        } else if (_javaType.isInt()) {
            this.append("s32");
        } else if (_javaType.isLong()) {
            this.append("s64");
        } else if (_javaType.isObject()) {
            this.append("b64");
        }
       return(this);
    }

    public HSAILRenderer regPrefix(PrimitiveType _type){
        switch(_type.getHsaBits()){
            case 8:
                append("$s");
                break;
            case 16:
                append("$s");
                break;
            case 32:
                append("$s");
                break;
            case 64:
                append("$d");
                break;
            default:
                append("$?");
                break;
        }
        return (this);
    }
    public HSAILRenderer regPrefix(TypeHelper.JavaType _javaType){
        this.regPrefix(_javaType.getPrimitiveType());

        return (this);
    }

    public HSAILRenderer regNum(HSAILRegister _reg){


       //old return (this.append(_reg.index + _HSAIL_stackFrame.baseOffset));
        return (this.append(_reg.index));
    }



   public HSAILRenderer operandName(HSAILOperand _operand){
      if (_operand instanceof HSAILConst){
          return(this.append(((HSAILConst) _operand).value.toString()));
      }else{
         this.regPrefix(_operand.type);
         return(this.regNum((HSAILRegister)_operand));
      }
   }
   public HSAILRenderer i(Instruction from){

      mark().append(from.getByteCode().getName()).relpad(8);//InstructionHelper.getLabel(i, false, false, false);

      if(from.isBranch()){
         append(" " + from.asBranch().getAbsolute());
      }else if(from.isFieldAccessor()){
         append(" " + from.asFieldAccessor().getConstantPoolFieldEntry().getType().getSignature());
         append(" " + from.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());
         append(" " + from.asFieldAccessor().getConstantPoolFieldEntry().getName());
      }else if(from.isLocalVariableAccessor()){
         append(" var#" + from.asLocalVariableAccessor().getLocalVariableInfo().getSlot());

         ClassModel.AttributePool.LocalVariableTableEntry.LocalVariableInfo lvi = from.asLocalVariableAccessor().getLocalVariableInfo();
         append("(" + lvi.getVariableName());
         if(lvi.isArg()){

            ClassModel.AttributePool.LocalVariableTableEntry.ArgLocalVariableInfo alvi = lvi.asArgLocalVariableInfo();
            append(" " + alvi.getRealType().getSignature());
         }else{
            InstructionSet.TypeSpec typeSpec = from.asLocalVariableAccessor().getLocalVariableInfo().getTypeSpec();
            append(" ");

            if(typeSpec.getPrimitiveType().equals(PrimitiveType.ref)){
               append(typeSpec.getPrimitiveType().getJavaTypeName());
            }else{
               append("ref type");
            }

         }
         append(")");

      }else if(from.isMethodCall()){
         append(" " + from.asMethodCall().getConstantPoolMethodEntry().getArgsAndReturnType().getReturnType().getSignature());
         append(" " + from.asMethodCall().getConstantPoolMethodEntry().getClassEntry().getDotClassName());
         append("." + from.asMethodCall().getConstantPoolMethodEntry().getName());
      }else if(from.isConstant()){
         append("." + from.asConstant().getValue());
      }
      return (this);
   }

    public HSAILRenderer kernarg(PrimitiveType _t, int _argNum){
        return(append("kernarg_").append(_t.getHSAName()).space().append("%_arg").append(_argNum));
    }




}
