package com.amd.aparapi;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: gfrost
 * Date: 5/8/13
 * Time: 8:09 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class PrimitiveType{
   static final none none = new none();
   static final v v = new v();
   static final u1 u1 = new u1();
   static final u8 u8 = new u8();
   static final u16 u16 = new u16();
   static final u32 u32 = new u32();
   static final s8 s8 = new s8();
   static final s16 s16 = new s16();
   static final s32 s32 = new s32();
   static final s64 s64 = new s64();

   static final f16 f16 = new f16();
   static final f32 f32 = new f32();
   static final f64 f64 = new f64();
   static final u64 u64 = new u64();
   static final ref ref = new ref();


   static final PrimitiveType javaPrimitiveTypes[] = new PrimitiveType[]{
         v, u1, s8, u16, s16, s32, f32, s64, f64, ref
   };
    /*
   static final Map<String, PrimitiveType> javaSigMap= new HashMap<String, PrimitiveType>();  {
      for (PrimitiveType p:javaPrimitiveTypes){
         javaSigMap.put(p.getJavaSig(),p);
      }
   }
   static final Map<String, PrimitiveType> javaTypeNameMap= new HashMap<String, PrimitiveType>();  {
      for (PrimitiveType p:javaPrimitiveTypes){
         javaTypeNameMap.put(p.getJavaTypeName(),p);
      }
   }
      */

   // protected Class clazz;
   protected int javaSlots;


   int getJavaSlots(){
      return (javaSlots);
   }


   int hsaBits;
   int javaBits;
   String hsaPrefix; // u,f,s
   String javaSig;
   String javaTypeName;
   String openCLTypeName;

   public int getHsaBits(){
      return (hsaBits);
   }

   PrimitiveType(int _hsaBits, int _javaBits, String _javaSig, String _javaTypeName, String _openCLTypeName, String _hsaPrefix, int _javaSlots){
      //    clazz = _clazz;
      javaSlots = _javaSlots;
      javaSig = _javaSig;
      hsaBits = _hsaBits;
      javaBits = _javaBits;
      hsaPrefix = _hsaPrefix;
      javaTypeName = _javaTypeName;
      openCLTypeName = _openCLTypeName;
   }

   public String getHSAName(){
      return (hsaPrefix + hsaBits);
   }

   public int getHsaBytes(){
      return (hsaBits / 8);
   }


   public String getJavaSig(){
      return (javaSig);
   }

   public String getJavaTypeName(){
      return (javaTypeName);
   }

   public String getOpenCLTypeName(){
      return (openCLTypeName);
   }

   public int getJavaBits(){
      return (javaBits);
   }

   public int getJavaBytes(){
      return (javaBits / 8);
   }


   public static PrimitiveType getJavaPrimitiveTypeFor(String _sig){
      for(PrimitiveType t : javaPrimitiveTypes){
         if(t.getJavaSig().equals(_sig)){
            return (t);
         }
      }
      return (ref);
   }
}

;

class u1 extends PrimitiveType{

   u1(){
      super(1, 8, "Z", "boolean", "char", "u", 1);
   }
}

class u8 extends PrimitiveType{
   u8(){
      super(8, 8, null, null, null, "u", -1);
   }

}

class s8 extends PrimitiveType{
   s8(){
      super(8, 8, "B", "byte", "char", "s", 1);
   }
}

class u16 extends PrimitiveType{
   u16(){
      super(16, 16, "C", "char", "unsigned short", "u", 1);
   }
}

class s16 extends PrimitiveType{
   s16(){
      super(16, 16, "S", "short", "short", "s", 1);
   }
}

class f16 extends PrimitiveType{
   f16(){
      super(16, 0, null, null, null, "f", -1);
   }
}

class u32 extends PrimitiveType{
   u32(){
      super(32, 0, null, null, null, "u", -1);
   }
}

class s32 extends PrimitiveType{
   s32(){
      super(32, 32, "I", "int", "int", "s", 1);
   }
}

class f32 extends PrimitiveType{
   f32(){
      super(32, 32, "F", "float", "float", "f", 1);

   }
}

class u64 extends PrimitiveType{
   u64(){
      super(64, 0, null, null, null, "u", -1);
   }
}

class ref extends PrimitiveType{
   ref(){
      super(64, 32, "REF", "REF", "REF", "u", 1);
   }
}


class s64 extends PrimitiveType{
   s64(){
      super(64, 64, "J", "long", "long", "s", 2);
   }
}

class f64 extends PrimitiveType{
   f64(){
      super(64, 64, "D", "double", "double", "f", 2);
   }
}

class none extends PrimitiveType{
   none(){
      super(0, 0, null, null, null, "?", -1);
   }
}

class v extends PrimitiveType{
   v(){
      super(0, 0, "V", "void", "void", "v", -1);
   }
}

