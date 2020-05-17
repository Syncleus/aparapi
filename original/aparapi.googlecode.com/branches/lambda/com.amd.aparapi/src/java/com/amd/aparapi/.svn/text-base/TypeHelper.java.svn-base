package com.amd.aparapi;

import java.lang.reflect.Method;
import java.util.*;


public class TypeHelper{

   static final char VOID = 'V';

   static final char BOOLEAN = 'Z';

   static final char STRING = 's'; // Annotation constantpool entries use this

   static final char ENUM = 'e'; // Annotation constantpool entries use this

   static final char CLASS = 'c'; // Annotation constantpool entries use this

   static final char ANNOTATION = '@'; // Annotation constantpool entries use this

   static final char ARRAY = 'a'; // Annotation constantpool entries use this

   static final char BYTE = 'B';

   static final char CHAR = 'C';

   static final char SHORT = 'S';

   static final char INT = 'I';

   static final char LONG = 'J';

   static final char FLOAT = 'F';

   static final char DOUBLE = 'D';


   private static final char ARRAY_DIM = '[';

   private static final char CLASS_START = 'L';

   private static final char ARG_START = '(';

   private static final char CLASS_END = ';';

   private static final char ARG_END = ')';

   private static final char SLASH = '/';

   private static final char DOT = '.';

   private static final char UNDERSCORE = '_';


   static Map<Character, String> charMap = new HashMap<Character, String>();

   {
      charMap.put(VOID, "void");
      charMap.put(INT, "int");
      charMap.put(DOUBLE, "double");
      charMap.put(FLOAT, "float");
      charMap.put(SHORT, "short");
      charMap.put(CHAR, "char");
      charMap.put(BYTE, "byte");
      charMap.put(LONG, "long");
      charMap.put(BOOLEAN, "boolean");
   }


   static String convert(String _string){
      Stack<String> stringStack = new Stack<String>();
      Stack<String> methodStack = null;
      int length = _string.length();
      char[] chars = _string.toCharArray();
      int i = 0;
      boolean inArray = false;
      boolean inMethod = false;
      boolean inArgs = false;
      int args = 0;
      while(i < length){
         switch(chars[i]){
            case CLASS_START:{
               StringBuilder classNameBuffer = new StringBuilder();
               i++;
               while((i < length) && chars[i] != CLASS_END){
                  if(chars[i] == SLASH){
                     classNameBuffer.append(DOT);
                  }else{
                     classNameBuffer.append(chars[i]);
                  }
                  i++;
               }
               i++; // step over CLASS_END
               String className = classNameBuffer.toString();
               if(inArray){
                  // swap the stack items
                  String popped = stringStack.pop();
                  if(inArgs && args > 0){
                     stringStack.push(", ");
                  }
                  stringStack.push(className);
                  stringStack.push(popped);
                  inArray = false;
               }else{
                  if(inArgs && args > 0){
                     stringStack.push(", ");
                  }
                  stringStack.push(className);
               }
               args++;
            }
            break;
            case ARRAY_DIM:{
               StringBuilder arrayDims = new StringBuilder();
               while((i < length) && chars[i] == ARRAY_DIM){
                  arrayDims.append("[]");
                  i++;
               }
               stringStack.push(arrayDims.toString());
               inArray = true;
            }
            break;
            case VOID:
            case INT:
            case DOUBLE:
            case FLOAT:
            case SHORT:
            case CHAR:
            case BYTE:
            case LONG:
            case BOOLEAN:{
               if(inArray){
                  // swap the stack items
                  String popped = stringStack.pop();
                  if(inArgs && args > 0){
                     stringStack.push(", ");
                  }
                  stringStack.push(charMap.get(chars[i]));
                  stringStack.push(popped);
                  inArray = false;
               }else{
                  if(inArgs && args > 0){
                     stringStack.push(", ");
                  }
                  stringStack.push(charMap.get(chars[i]));
               }
               i++; // step over this
            }
            break;
            case ARG_START:{
               stringStack.push("(");
               i++; // step over this
               inArgs = true;
               args = 0;
            }
            break;
            case ARG_END:{
               inMethod = true;
               inArgs = false;
               stringStack.push(")");
               methodStack = stringStack;
               stringStack = new Stack<String>();
               i++; // step over this
            }
            break;
            default:
               throw new IllegalStateException("invalid prefix!");
         }
      }

      StringBuilder returnValue = new StringBuilder();
      for(String s : stringStack){
         returnValue.append(s);
         returnValue.append(" ");

      }
      if(inMethod){
         for(String s : methodStack){
            returnValue.append(s);
            returnValue.append(" ");
         }
      }
      return (returnValue.toString());
   }

   /**
    * Convert a signature form "Lpackage/Name;" or array form to dot class form.
    * <p/>
    * signatureToDotClassName("Lpackage/Outer$Name;", 0) -> "package.Outer.Name"
    * signatureToDotClassName("[Lpackage/Outer$Name;", 1) -> "package.Outer.Name"
    *
    * @param _signature
    * @param _dims
    * @return
    */
   public static String signatureToDotClassName(String _signature, int _dims){
      String dotClassName = slashClassNameToDotClassName(_signature.substring(1 + _dims, _signature.length() - 1));
      return (dotClassName);
   }

   public static String signatureToMangledClassName(String _signature, int _dims){
      String mangledClassName = slashClassNameToMangledClassName(_signature.substring(1 + _dims, _signature.length() - 1));
      return (mangledClassName);
   }

   /**
    * @param _dotClassName
    * @param _dims
    * @return
    */
   public static String dotClassNameToSignature(String _dotClassName, int _dims){
      StringBuilder sb = new StringBuilder();
      for(int i = 0; i < _dims; i++){
         sb.append(ARRAY_DIM);
      }
      sb.append(CLASS_START).append(dotClassNameToSlashClassName(_dotClassName)).append(CLASS_END);
      return (sb.toString());
   }

   public static String dotClassNameToMangledClassName(String _dotClassName){
      return (_dotClassName.replace(DOT, UNDERSCORE));
   }

   /**
    * @param _dotClassName
    * @return
    */
   public static String dotClassNameToSlashClassName(String _dotClassName){
      return (_dotClassName.replace(DOT, SLASH));
   }

   /**
    * @param _slashClassName
    * @return
    */
   public static String slashClassNameToDotClassName(String _slashClassName){
      return (_slashClassName.replace(SLASH, DOT));
   }

   /**
    * @param _slashClassName
    * @return
    */
   public static String slashClassNameToMangledClassName(String _slashClassName){
      return (_slashClassName.replace(SLASH, UNDERSCORE));
   }

   static class JavaType{

      static final Map<String, JavaType> typeMap = new HashMap<String, JavaType>();

      static{
         typeMap.put(PrimitiveType.v.getJavaSig(), new JavaType(PrimitiveType.v));// boolean
         typeMap.put(PrimitiveType.u1.getJavaSig(), new JavaType(PrimitiveType.u1));// boolean
         typeMap.put(PrimitiveType.s8.getJavaSig(), new JavaType(PrimitiveType.s8));// byte
         typeMap.put(PrimitiveType.s16.getJavaSig(), new JavaType(PrimitiveType.s16));// short
         typeMap.put(PrimitiveType.u16.getJavaSig(), new JavaType(PrimitiveType.u16));// char
         typeMap.put(PrimitiveType.s32.getJavaSig(), new JavaType(PrimitiveType.s32));// int
         typeMap.put(PrimitiveType.f32.getJavaSig(), new JavaType(PrimitiveType.f32));// float
         typeMap.put(PrimitiveType.s64.getJavaSig(), new JavaType(PrimitiveType.s64));// long
         typeMap.put(PrimitiveType.f64.getJavaSig(), new JavaType(PrimitiveType.f64));// double
         typeMap.put(PrimitiveType.ref.getJavaSig(), new JavaType(PrimitiveType.ref));// double
      }


      static String createSignature(Class _clazz){
         String arrayPrefix = "";

         String baseType = _clazz.getName();
         String signature = null;
         if(baseType.startsWith("[")){
            int arrayDimensions = baseType.lastIndexOf('[') + 1;
            arrayPrefix = baseType.substring(0, arrayDimensions);
            baseType = baseType.substring(arrayDimensions);
         }
         for(PrimitiveType p : PrimitiveType.javaPrimitiveTypes){
            if(p.getJavaSig() == null){
               throw new IllegalStateException("no!");
            }
            if(p.getJavaSig().equals(baseType)){
               signature = arrayPrefix + p.getJavaSig();
               break;
            }
         }
         if(signature == null){
            signature = arrayPrefix + CLASS_START + dotClassNameToSlashClassName(_clazz.getName()) + CLASS_END;
         }
         return (signature);
      }

      static String createSignature(PrimitiveType _primitiveType){
         return (_primitiveType.getJavaSig());
      }


      static synchronized JavaType getJavaType(String _signature){
         JavaType type = typeMap.get(_signature);
         if(type == null){
            type = new JavaType(_signature);
            typeMap.put(_signature, type);
         }

         return (type);
      }

      static synchronized JavaType getJavaType(Class _clazz){
         String signature = createSignature(_clazz);
         return (getJavaType(signature));
      }


      private int arrayDimensions = 0;
      private String signature;
      private PrimitiveType type; // I if int  OREF if array (or primitive or object or array) or object
      private PrimitiveType arrayElementType; // none if not an array

      private JavaType(PrimitiveType _primitiveType){
         signature = createSignature(_primitiveType);
         arrayDimensions = 0;
         type = _primitiveType;
         arrayElementType = PrimitiveType.none;

      }

      PrimitiveType getPrimitiveType(){
         return (type);
      }

      String getHSAName(){
          if (isPrimitive()){
              return(type.getHSAName());
          }else{
              return(ref.ref.getHSAName());
          }
      }

      PrimitiveType getArrayElementType(){
         return (arrayElementType);
      }

      private JavaType(String _signature){
         arrayDimensions = _signature.startsWith("[") ? _signature.lastIndexOf('[') + 1 : 0;
         signature = _signature;
         type = PrimitiveType.ref;
         arrayElementType = PrimitiveType.getJavaPrimitiveTypeFor(_signature.substring(arrayDimensions));
      }

      String getSignature(){
         return (signature);
      }

      boolean isVoid(){
         return (arrayDimensions == 0 && type.equals(PrimitiveType.v));
      }

      boolean isInt(){
         return (arrayDimensions == 0 && type.equals(PrimitiveType.s32));
      }

      boolean isLong(){
         return (arrayDimensions == 0 && type.equals(PrimitiveType.s64));
      }

      boolean isShort(){
         return (arrayDimensions == 0 && type.equals(PrimitiveType.s16));
      }

      boolean isBoolean(){
         return (arrayDimensions == 0 && type.equals(PrimitiveType.u1));
      }

      boolean isChar(){
         return (arrayDimensions == 0 && type.equals(PrimitiveType.u16));
      }

      boolean isFloat(){
         return (arrayDimensions == 0 && type.equals(PrimitiveType.f32));
      }

      boolean isDouble(){
         return (arrayDimensions == 0 && type.equals(PrimitiveType.f64));
      }

      boolean isByte(){
         return (arrayDimensions == 0 && type.equals(PrimitiveType.s8));
      }

      boolean isObject(){
         return (arrayDimensions == 0 && !isPrimitive());
      }

      String getObjectClassName(){
         return (TypeHelper.signatureToDotClassName(signature, arrayDimensions));
      }

      String getMangledClassName(){
         return (TypeHelper.signatureToMangledClassName(signature, arrayDimensions));
      }


      final boolean isArray(){
         return (arrayDimensions > 0);
      }

      final int getArrayDimensions(){
         return (arrayDimensions);
      }

      final boolean isArrayOfObjects(int _dim){
         return (isArray() && getArrayDimensions() == _dim && isObject());
      }

      final boolean isPrimitive(){
         return (isInt() || isFloat() || isDouble() || isChar() || isLong() || isShort() || isByte() || isVoid());
      }


      @Override
      public String toString(){

         // StringBuilder sb = new StringBuilder(getJavaNamer());
         // for (int i = 0; i < arrayDimensions; i++) {
         //     sb.append("[]");
         // }
         throw new IllegalStateException("no toString!");
         //return (sb.toString());
      }
   }

   public static class JavaMethodArg{
      JavaType type;

      JavaMethodArg(String _signature, int _start, int _pos, int _argc){
         type = TypeHelper.JavaType.getJavaType(_signature.substring(_start, _pos + 1));
         argc = _argc;
      }

      JavaMethodArg(Class _clazz, int _argc){
         type = TypeHelper.JavaType.getJavaType(_clazz);
         argc = _argc;
      }

      private int argc;

      int getArgc(){
         return (argc);
      }

      public JavaType getJavaType(){
         return (type);
      }
   }


   public static class JavaMethodArgsAndReturnType{
      private static enum SignatureParseState{
         skipping,
         inArgs,
         inClass,
         inArray,
         done;
      }

      ;

      static Map<String, JavaMethodArgsAndReturnType> map = new HashMap<String, JavaMethodArgsAndReturnType>();

      String signature;

      JavaMethodArg[] args;
      JavaType returnType;

      public JavaMethodArg[] getArgs(){
         return (args);
      }

      public JavaType getReturnType(){
         return (returnType);
      }


      public static synchronized JavaMethodArgsAndReturnType getArgsAndReturnType(String _signature){
         JavaMethodArgsAndReturnType returnVal = map.get(_signature);
         if(returnVal == null){
            returnVal = new JavaMethodArgsAndReturnType(_signature);
            map.put(_signature, returnVal);
         }
         return (returnVal);
      }


      private JavaMethodArgsAndReturnType(String _signature){
         signature = _signature;

         SignatureParseState state = SignatureParseState.skipping;
         List<JavaMethodArg> argList = new ArrayList<JavaMethodArg>();
         int start = 0;
         for(int pos = 0; state != SignatureParseState.done; pos++){
            char ch = _signature.charAt(pos);
            switch(ch){
               case ARG_START:
                  state = SignatureParseState.inArgs;
                  break;
               case ARG_END:
                  state = SignatureParseState.done;
                  returnType = TypeHelper.JavaType.getJavaType(_signature.substring(pos + 1));
                  break;
               case ARRAY_DIM:
                  switch(state){
                     case inArgs:
                        state = SignatureParseState.inArray;
                        start = pos;
                        break;

                  }
                  break;
               case CLASS_START:
                  switch(state){
                     case inArgs:
                        start = pos;
                        state = SignatureParseState.inClass;
                        break;
                     case inArray:
                        state = SignatureParseState.inClass;
                        break;
                  }
                  break;
               case CLASS_END:
                  argList.add(new JavaMethodArg(_signature, start, pos, argList.size()));
                  state = SignatureParseState.inArgs;
                  break;
               default:
                  switch(state){
                     case inArgs:
                        start = pos;
                        argList.add(new JavaMethodArg(_signature, start, pos, argList.size()));
                        state = SignatureParseState.inArgs;
                        break;
                     case inArray:
                        argList.add(new JavaMethodArg(_signature, start, pos, argList.size()));
                        state = SignatureParseState.inArgs;
                        break;

                  }
                  break;
            }

         }
         args = argList.toArray(new JavaMethodArg[0]);
      }

      public JavaMethodArgsAndReturnType(Method _method){
         args = new JavaMethodArg[_method.getParameterCount()];
         Class<?> argsAsClasses[] = _method.getParameterTypes();
         for(int i = 0; i < argsAsClasses.length; i++){
            args[i] = new JavaMethodArg(argsAsClasses[i], i);
         }
         returnType = TypeHelper.JavaType.getJavaType(_method.getReturnType());
      }

      public boolean matches(Method _method){
         return (returnType == JavaType.getJavaType(_method.getReturnType()));
      }

   }

}
