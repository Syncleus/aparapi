switch (n){
  case I_NOP: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_NONE, OpSpec_NONE}, 
  break;
  case I_ACONST_NULL: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_null, PopSpec_NONE, PushSpec_N, OpSpec_NONE},
  break;
  case I_ICONST_M1: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_m1, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
  break;
  case I_ICONST_0: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_0, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
  break;
  case I_ICONST_1: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_1, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
  break;
  case I_ICONST_2: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_2, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
  break;
  case I_ICONST_3: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_3, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
  break;
  case I_ICONST_4: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_4, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
  break;
  case I_ICONST_5: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_5, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
  break;
  case I_LCONST_0: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_0, PopSpec_NONE, PushSpec_L, OpSpec_NONE},
  break;
  case I_LCONST_1: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_1, PopSpec_NONE, PushSpec_L, OpSpec_NONE},
  break;
  case I_FCONST_0: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_0, PopSpec_NONE, PushSpec_F, OpSpec_NONE},
  break;
  case I_FCONST_1: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_1, PopSpec_NONE, PushSpec_F, OpSpec_NONE},
  break;
  case I_FCONST_2: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_2, PopSpec_NONE, PushSpec_F, OpSpec_NONE},
  break;
  case I_DCONST_0: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_0, PopSpec_NONE, PushSpec_D, OpSpec_NONE},
  break;
  case I_DCONST_1: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE_const_1, PopSpec_NONE, PushSpec_D, OpSpec_NONE},
  break;
  case I_BIPUSH: // LDSpec_NONE, STSpec_NONE, ImmSpec_Bconst, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
  break;
  case I_SIPUSH: // LDSpec_NONE, STSpec_NONE, ImmSpec_Sconst, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
  break;
  case I_LDC: // LDSpec_NONE, STSpec_NONE, ImmSpec_Bcpci, PopSpec_NONE, PushSpec_IorForS, OpSpec_NONE},
  break;
  case I_LDC_W: // LDSpec_NONE, STSpec_NONE, ImmSpec_Scpci, PopSpec_NONE, PushSpec_IorForS, OpSpec_NONE},
  break;
  case I_LDC2_W: // LDSpec_NONE, STSpec_NONE, ImmSpec_Scpci, PopSpec_NONE, PushSpec_LorD, OpSpec_NONE},
  break;
  case I_ILOAD: // LDSpec_I, STSpec_NONE, ImmSpec_Blvti, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
  break;
  case I_LLOAD: // LDSpec_L, STSpec_NONE, ImmSpec_Blvti, PopSpec_NONE, PushSpec_L, OpSpec_NONE},
  break;
  case I_FLOAD: // LDSpec_F, STSpec_NONE, ImmSpec_Blvti, PopSpec_NONE, PushSpec_F, OpSpec_NONE},
  break;
  case I_DLOAD: // LDSpec_F, STSpec_NONE, ImmSpec_Blvti, PopSpec_NONE, PushSpec_D, OpSpec_NONE},
  break;
  case I_ALOAD: // LDSpec_A, STSpec_NONE, ImmSpec_Blvti, PopSpec_NONE, PushSpec_O, OpSpec_NONE},
  break;
  case I_ILOAD_0: // LDSpec_I, STSpec_NONE, ImmSpec_NONE_lvti_0, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
  break;
  case I_ILOAD_1: // LDSpec_I, STSpec_NONE, ImmSpec_NONE_lvti_1, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
  break;
  case I_ILOAD_2: // LDSpec_I, STSpec_NONE, ImmSpec_NONE_lvti_2, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
  break;
  case I_ILOAD_3: // LDSpec_I, STSpec_NONE, ImmSpec_NONE_lvti_3, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
  break;
  case I_LLOAD_0: // LDSpec_L, STSpec_NONE, ImmSpec_NONE_lvti_0, PopSpec_NONE, PushSpec_L, OpSpec_NONE},
  break;
  case I_LLOAD_1: // LDSpec_L, STSpec_NONE, ImmSpec_NONE_lvti_1, PopSpec_NONE, PushSpec_L, OpSpec_NONE},
  break;
  case I_LLOAD_2: // LDSpec_L, STSpec_NONE, ImmSpec_NONE_lvti_2, PopSpec_NONE, PushSpec_L, OpSpec_NONE},
  break;
  case I_LLOAD_3: // LDSpec_L, STSpec_NONE, ImmSpec_NONE_lvti_3, PopSpec_NONE, PushSpec_L, OpSpec_NONE},
  break;
  case I_FLOAD_0: // LDSpec_F, STSpec_NONE, ImmSpec_NONE_lvti_0, PopSpec_NONE, PushSpec_F, OpSpec_NONE},
  break;
  case I_FLOAD_1: // LDSpec_F, STSpec_NONE, ImmSpec_NONE_lvti_1, PopSpec_NONE, PushSpec_F, OpSpec_NONE},
  break;
  case I_FLOAD_2: // LDSpec_F, STSpec_NONE, ImmSpec_NONE_lvti_2, PopSpec_NONE, PushSpec_F, OpSpec_NONE},
  break;
  case I_FLOAD_3: // LDSpec_F, STSpec_NONE, ImmSpec_NONE_lvti_3, PopSpec_NONE, PushSpec_F, OpSpec_NONE},
  break;
  case I_DLOAD_0: // LDSpec_D, STSpec_NONE, ImmSpec_NONE_lvti_0, PopSpec_NONE, PushSpec_D, OpSpec_NONE},
  break;
  case I_DLOAD_1: // LDSpec_D, STSpec_NONE, ImmSpec_NONE_lvti_1, PopSpec_NONE, PushSpec_D, OpSpec_NONE},
  break;
  case I_DLOAD_2: // LDSpec_D, STSpec_NONE, ImmSpec_NONE_lvti_2, PopSpec_NONE, PushSpec_D, OpSpec_NONE},
  break;
  case I_DLOAD_3: // LDSpec_D, STSpec_NONE, ImmSpec_NONE_lvti_3, PopSpec_NONE, PushSpec_D, OpSpec_NONE},
  break;
  case I_ALOAD_0: // LDSpec_A, STSpec_NONE, ImmSpec_NONE_lvti_0, PopSpec_NONE, PushSpec_O, OpSpec_NONE},
  break;
  case I_ALOAD_1: // LDSpec_A, STSpec_NONE, ImmSpec_NONE_lvti_1, PopSpec_NONE, PushSpec_O, OpSpec_NONE},
  break;
  case I_ALOAD_2: // LDSpec_A, STSpec_NONE, ImmSpec_NONE_lvti_2, PopSpec_NONE, PushSpec_O, OpSpec_NONE},
  break;
  case I_ALOAD_3: // LDSpec_A, STSpec_NONE, ImmSpec_NONE_lvti_3, PopSpec_NONE, PushSpec_O, OpSpec_NONE},
  break;
  case I_IALOAD: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_I, OpSpec_NONE},
  break;
  case I_LALOAD: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_L, OpSpec_NONE},
  break;
  case I_FALOAD: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_F, OpSpec_NONE},
  break;
  case I_DALOAD: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_D, OpSpec_NONE},
  break;
  case I_AALOAD: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_A, OpSpec_NONE},
  break;
  case I_BALOAD: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_I, OpSpec_NONE},
  break;
  case I_CALOAD: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_I, OpSpec_NONE},
  break;
  case I_SALOAD: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_I, OpSpec_NONE},
  break;
  case I_ISTORE: // LDSpec_NONE, STSpec_I, ImmSpec_Blvti, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_LSTORE: // LDSpec_NONE, STSpec_L, ImmSpec_Blvti, PopSpec_L, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_FSTORE: // LDSpec_NONE, STSpec_F, ImmSpec_Blvti, PopSpec_F, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_DSTORE: // LDSpec_NONE, STSpec_D, ImmSpec_Blvti, PopSpec_D, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_ASTORE: // LDSpec_NONE, STSpec_A, ImmSpec_Blvti, PopSpec_O, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_ISTORE_0: // LDSpec_NONE, STSpec_I, ImmSpec_NONE_lvti_0, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_ISTORE_1: // LDSpec_NONE, STSpec_I, ImmSpec_NONE_lvti_1, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_ISTORE_2: // LDSpec_NONE, STSpec_I, ImmSpec_NONE_lvti_2, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_ISTORE_3: // LDSpec_NONE, STSpec_I, ImmSpec_NONE_lvti_3, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_LSTORE_0: // LDSpec_NONE, STSpec_L, ImmSpec_NONE_lvti_0, PopSpec_L, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_LSTORE_1: // LDSpec_NONE, STSpec_L, ImmSpec_NONE_lvti_1, PopSpec_L, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_LSTORE_2: // LDSpec_NONE, STSpec_L, ImmSpec_NONE_lvti_2, PopSpec_L, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_LSTORE_3: // LDSpec_NONE, STSpec_L, ImmSpec_NONE_lvti_3, PopSpec_L, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_FSTORE_0: // LDSpec_NONE, STSpec_F, ImmSpec_NONE_lvti_0, PopSpec_F, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_FSTORE_1: // LDSpec_NONE, STSpec_F, ImmSpec_NONE_lvti_1, PopSpec_F, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_FSTORE_2: // LDSpec_NONE, STSpec_F, ImmSpec_NONE_lvti_2, PopSpec_F, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_FSTORE_3: // LDSpec_NONE, STSpec_F, ImmSpec_NONE_lvti_3, PopSpec_F, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_DSTORE_0: // LDSpec_NONE, STSpec_D, ImmSpec_NONE_lvti_0, PopSpec_D, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_DSTORE_1: // LDSpec_NONE, STSpec_D, ImmSpec_NONE_lvti_1, PopSpec_D, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_DSTORE_2: // LDSpec_NONE, STSpec_D, ImmSpec_NONE_lvti_2, PopSpec_D, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_DSTORE_3: // LDSpec_NONE, STSpec_D, ImmSpec_NONE_lvti_3, PopSpec_D, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_ASTORE_0: // LDSpec_NONE, STSpec_A, ImmSpec_NONE_lvti_0, PopSpec_O, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_ASTORE_1: // LDSpec_NONE, STSpec_A, ImmSpec_NONE_lvti_1, PopSpec_O, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_ASTORE_2: // LDSpec_NONE, STSpec_A, ImmSpec_NONE_lvti_2, PopSpec_O, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_ASTORE_3: // LDSpec_NONE, STSpec_A, ImmSpec_NONE_lvti_3, PopSpec_O, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_IASTORE: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AII, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_LASTORE: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AIL, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_FASTORE: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AIF, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_DASTORE: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AID, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_AASTORE: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AIO, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_BASTORE: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AIB, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_CASTORE: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AIC, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_SASTORE: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AIS, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_POP: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_POP2: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_DUP: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_II, OpSpec_NONE},
  break;
  case I_DUP_X1: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_III, OpSpec_NONE},
  break;
  case I_DUP_X2: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_III, PushSpec_IIII, OpSpec_NONE},
  break;
  case I_DUP2: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_IIII, OpSpec_NONE},
  break;
  case I_DUP2_X1: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_III, PushSpec_IIIII, OpSpec_NONE},
  break;
  case I_DUP2_X2: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_IIII, PushSpec_IIIIII, OpSpec_NONE},
  break;
  case I_SWAP: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_II, OpSpec_NONE},
  break;
  case I_IADD: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_Add},
  break;
  case I_LADD: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_Add},
  break;
  case I_FADD: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_F, OpSpec_Add},
  break;
  case I_DADD: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_D, OpSpec_Add},
  break;
  case I_ISUB: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_Sub},
  break;
  case I_LSUB: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_Sub},
  break;
  case I_FSUB: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_F, OpSpec_Sub},
  break;
  case I_DSUB: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_D, OpSpec_Sub},
  break;
  case I_IMUL: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_Mul},
  break;
  case I_LMUL: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_Mul},
  break;
  case I_FMUL: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_F, OpSpec_Mul},
  break;
  case I_DMUL: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_D, OpSpec_Mul},
  break;
  case I_IDIV: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_Div},
  break;
  case I_LDIV: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_Div},
  break;
  case I_FDIV: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_F, OpSpec_Div},
  break;
  case I_DDIV: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_D, OpSpec_Div},
  break;
  case I_IREM: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_Rem},
  break;
  case I_LREM: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_Rem},
  break;
  case I_FREM: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_F, OpSpec_Rem},
  break;
  case I_DREM: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_D, OpSpec_Rem},
  break;
  case I_INEG: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_I, OpSpec_Neg},
  break;
  case I_LNEG: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_L, PushSpec_L, OpSpec_Neg},
  break;
  case I_FNEG: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_F, PushSpec_F, OpSpec_Neg},
  break;
  case I_DNEG: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_D, PushSpec_D, OpSpec_Neg},
  break;
  case I_ISHL: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_LeftShift},
  break;
  case I_LSHL: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LI, PushSpec_L, OpSpec_LeftShift},
  break;
  case I_ISHR: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_LogicalRightShift},
  break;
  case I_LSHR: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LI, PushSpec_L, OpSpec_LogicalRightShift},
  break;
  case I_IUSHR: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_ArithmeticRightShift},
  break;
  case I_LUSHR: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LI, PushSpec_L, OpSpec_ArithmeticRightShift},
  break;
  case I_IAND: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_BitwiseAnd},
  break;
  case I_LAND: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_BitwiseAnd},
  break;
  case I_IOR: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_BitwiseOr},
  break;
  case I_LOR: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_BitwiseOr},
  break;
  case I_IXOR: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_BitwiseXor},
  break;
  case I_LXOR: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_BitwiseXor},
  break;
  case I_IINC: // LDSpec_I, STSpec_I, ImmSpec_BlvtiBconst, PopSpec_NONE, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_I2L: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_L, OpSpec_I2LCast},
  break;
  case I_I2F: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_F, OpSpec_I2FCast},
  break;
  case I_I2D: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_D, OpSpec_I2DCast},
  break;
  case I_L2I: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_L, PushSpec_I, OpSpec_L2ICast},
  break;
  case I_L2F: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_L, PushSpec_F, OpSpec_L2FCast},
  break;
  case I_L2D: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_L, PushSpec_D, OpSpec_L2DCast},
  break;
  case I_F2I: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_F, PushSpec_I, OpSpec_F2ICast},
  break;
  case I_F2L: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_F, PushSpec_L, OpSpec_F2LCast},
  break;
  case I_F2D: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_F, PushSpec_D, OpSpec_F2DCast},
  break;
  case I_D2I: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_D, PushSpec_I, OpSpec_D2ICast},
  break;
  case I_D2L: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_D, PushSpec_L, OpSpec_D2LCast},
  break;
  case I_D2F: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_D, PushSpec_F, OpSpec_D2FCast},
  break;
  case I_I2B: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_I, OpSpec_I2BCast},
  break;
  case I_I2C: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_I, OpSpec_I2CCast},
  break;
  case I_I2S: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_I, OpSpec_I2SCast},
  break;
  case I_LCMP: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_I, OpSpec_Sub},
  break;
  case I_FCMPL: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_I, OpSpec_LessThan},
  break;
  case I_FCMPG: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_I, OpSpec_GreaterThan},
  break;
  case I_DCMPL: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_I, OpSpec_LessThan},
  break;
  case I_DCMPG: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_I, OpSpec_GreaterThan},
  break;
  case I_IFEQ: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, PushSpec_NONE, OpSpec_Equal},
  break;
  case I_IFNE: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, PushSpec_NONE, OpSpec_NotEqual},
  break;
  case I_IFLT: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, PushSpec_NONE, OpSpec_LessThan},
  break;
  case I_IFGE: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, PushSpec_NONE, OpSpec_GreaterThanOrEqual},
  break;
  case I_IFGT: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, PushSpec_NONE, OpSpec_GreaterThan},
  break;
  case I_IFLE: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, PushSpec_NONE, OpSpec_LessThanOrEqual},
  break;
  case I_IF_ICMPEQ: // LDSpec_NONE, STSpec_NONE, ImmSpec_Sconst, PopSpec_II, PushSpec_NONE, OpSpec_Equal},
  break;
  case I_IF_ICMPNE: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_II, PushSpec_NONE, OpSpec_NotEqual},
  break;
  case I_IF_ICMPLT: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_II, PushSpec_NONE, OpSpec_LessThan},
  break;
  case I_IF_ICMPGE: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_II, PushSpec_NONE, OpSpec_GreaterThanOrEqual},
  break;
  case I_IF_ICMPGT: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_II, PushSpec_NONE, OpSpec_GreaterThan},
  break;
  case I_IF_ICMPLE: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_II, PushSpec_NONE, OpSpec_LessThanOrEqual},
  break;
  case I_IF_ACMPEQ: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_OO, PushSpec_NONE, OpSpec_Equal},
  break;
  case I_IF_ACMPNE: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_OO, PushSpec_NONE, OpSpec_NotEqual},
  break;
  case I_GOTO: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_NONE, PushSpec_NONE, OpSpec_NONE },
  break;
  case I_JSR: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_NONE, PushSpec_RA, OpSpec_NONE},
  break;
  case I_RET: // LDSpec_NONE, STSpec_NONE, ImmSpec_Blvti, PopSpec_NONE, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_TABLESWITCH: // LDSpec_NONE, STSpec_NONE, ImmSpec_UNKNOWN, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_LOOKUPSWITCH: // LDSpec_NONE, STSpec_NONE, ImmSpec_UNKNOWN, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_IRETURN: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_LRETURN: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_L, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_FRETURN: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_F, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_DRETURN: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_D, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_ARETURN: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_O, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_RETURN: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_GETSTATIC: // LDSpec_NONE, STSpec_NONE, ImmSpec_Scpfi, PopSpec_NONE, PushSpec_FSIG, OpSpec_NONE},
  break;
  case I_PUTSTATIC: // LDSpec_NONE, STSpec_NONE, ImmSpec_Scpfi, PopSpec_FSIG, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_GETFIELD: // LDSpec_NONE, STSpec_NONE, ImmSpec_Scpfi, PopSpec_O, PushSpec_FSIG, OpSpec_NONE},
  break;
  case I_PUTFIELD: // LDSpec_NONE, STSpec_NONE, ImmSpec_Scpfi, PopSpec_OFSIG, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_INVOKEVIRTUAL: // LDSpec_NONE, STSpec_NONE, ImmSpec_Scpmi, PopSpec_OMSIG, PushSpec_MSIG, OpSpec_NONE},
  break;
  case I_INVOKESPECIAL: // LDSpec_NONE, STSpec_NONE, ImmSpec_Scpmi, PopSpec_OMSIG, PushSpec_MSIG, OpSpec_NONE},
  break;
  case I_INVOKESTATIC: // LDSpec_NONE, STSpec_NONE, ImmSpec_Scpmi, PopSpec_MSIG, PushSpec_MSIG, OpSpec_NONE},
  break;
  case I_INVOKEINTERFACE: // LDSpec_NONE, STSpec_NONE, ImmSpec_ScpmiBB, PopSpec_OMSIG, PushSpec_MSIG, OpSpec_NONE},
  break;
  case I_INVOKEDYNAMIC: // LDSpec_NONE, STSpec_NONE, ImmSpec_ScpmiBB, PopSpec_OMSIG, PushSpec_MSIG, OpSpec_NONE},
  break;
  case I_NEW: // LDSpec_NONE, STSpec_NONE, ImmSpec_Scpci, PopSpec_NONE, PushSpec_O, OpSpec_NONE},
  break;
  case I_NEWARRAY: // LDSpec_NONE, STSpec_NONE, ImmSpec_Bconst, PopSpec_I, PushSpec_A, OpSpec_NONE},
  break;
  case I_ANEWARRAY: // LDSpec_NONE, STSpec_NONE, ImmSpec_Sconst, PopSpec_I, PushSpec_A, OpSpec_NONE},
  break;
  case I_ARRAYLENGTH: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_A, PushSpec_I, OpSpec_NONE},
  break;
  case I_ATHROW: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_O, PushSpec_O, OpSpec_NONE},
  break;
  case I_CHECKCAST: // LDSpec_NONE, STSpec_NONE, ImmSpec_Scpci, PopSpec_O, PushSpec_O, OpSpec_NONE},
  break;
  case I_INSTANCEOF: // LDSpec_NONE, STSpec_NONE, ImmSpec_Scpci, PopSpec_O, PushSpec_I, OpSpec_NONE},
  break;
  case I_MONITORENTER: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_O, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_MONITOREXIT: // LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_O, PushSpec_NONE, OpSpec_NONE},
  break;
  case I_WIDE: // LDSpec_NONE, STSpec_NONE, ImmSpec_UNKNOWN, PopSpec_UNKNOWN, PushSpec_UNKNOWN, OpSpec_NONE},
  break;
  case I_MULTIANEWARRAY: // LDSpec_NONE, STSpec_NONE, ImmSpec_ScpciBdim, PopSpec_UNKNOWN, PushSpec_A, OpSpec_NONE},
  break;
  case I_IFNULL: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_O, PushSpec_NONE, OpSpec_EqualNULL},
  break;
  case I_IFNONNULL: // LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_O, PushSpec_NONE, OpSpec_NotEqualNULL},
  break;
  case I_GOTO_W: // LDSpec_NONE, STSpec_NONE, ImmSpec_Ipc, PopSpec_O, PushSpec_NONE, OpSpec_NotEqualNULL},
  break;
  case I_JSR_W: // LDSpec_NONE, STSpec_NONE, ImmSpec_Ipc, PopSpec_NONE, PushSpec_RA, OpSpec_NONE}
  break;
  }
