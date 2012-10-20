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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amd.aparapi.ClassModel.AttributePool.CodeEntry;
import com.amd.aparapi.ClassModel.ConstantPool.FieldEntry;
import com.amd.aparapi.ClassModel.ConstantPool.MethodEntry;
import com.amd.aparapi.InstructionSet.TypeSpec;

/**
 * Class represents a ClassFile (MyClass.class).
 * 
 * A ClassModel is constructed from an instance of a <code>java.lang.Class</code>.
 * 
 * If the java class mode changes we may need to modify this to accommodate.
 * 
 * @see <a href="http://java.sun.com/docs/books/jvms/second_edition/ClassFileFormat-Java5.pdf">Java 5 Class File Format</a>
 * @see <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html"> Java 7 Class File Format</a>
 * 
 * @author gfrost
 *
 */
class ClassModel{

   interface LocalVariableInfo{

      int getStart();

      boolean isArray();

      int getEnd();

      String getVariableName();

      String getVariableDescriptor();

      int getVariableIndex();

   }

   interface LocalVariableTableEntry<T extends LocalVariableInfo> extends Iterable<T>{
      LocalVariableInfo getVariable(int _pc, int _index);

   }

   static final char SIGC_VOID = 'V';

   static final char SIGC_BOOLEAN = 'Z';

   static final char SIGC_BYTE = 'B';

   static final char SIGC_CHAR = 'C';

   static final char SIGC_SHORT = 'S';

   static final char SIGC_INT = 'I';

   static final char SIGC_LONG = 'J';

   static final char SIGC_FLOAT = 'F';

   static final char SIGC_DOUBLE = 'D';

   static final char SIGC_ARRAY = '[';

   static final char SIGC_CLASS = 'L';

   static final char SIGC_START_METHOD = '(';

   static final char SIGC_END_CLASS = ';';

   static final char SIGC_END_METHOD = ')';

   static final char SIGC_PACKAGE = '/';

   private static Logger logger = Logger.getLogger(Config.getLoggerName());

   private ClassModel superClazz = null;

   /**
    * Create a ClassModel representing a given Class.
    * 
    * The class's classfile must be available from the class's classloader via <code>getClassLoader().getResourceAsStream(name))</code>. 
    * For dynamic languages creating classes on the fly we may need another approach. 
    * 
    * @param _class The class we will extract the model from
    * @throws ClassParseException
    */

   ClassModel(Class<?> _class) throws ClassParseException {

      parse(_class);

      Class<?> mySuper = _class.getSuperclass();
      // Find better way to do this check
      // The java.lang.Object test is for unit test framework to succeed - should 
      // not occur in normal use
      if ((mySuper != null) && (!mySuper.getName().equals(Kernel.class.getName()))
            && (!mySuper.getName().equals("java.lang.Object"))) {
         superClazz = new ClassModel(mySuper);
      }
   }

   ClassModel(InputStream _inputStream) throws ClassParseException {

      parse(_inputStream);

   }

   /**
    * Determine if this is the superclass of some other named class.
    * 
    * @param otherClassName The name of the class to compare against
    * @return true if 'this' a superclass of another named class 
    */
   boolean isSuperClass(String otherClassName) {
      if (getClassWeAreModelling().getName().equals(otherClassName)) {
         return true;
      } else if (superClazz != null) {
         return superClazz.isSuperClass(otherClassName);
      } else {
         return false;
      }
   }

   /**
    * Determine if this is the superclass of some other class.
    * 
    * @param otherClass The class to compare against
    * @return true if 'this' a superclass of another class   
    */
   boolean isSuperClass(Class<?> other) {
      Class<?> s = other.getSuperclass();
      while (s != null) {
         if (this.getClassWeAreModelling() == s || (this.getClassWeAreModelling().getName().equals(s.getName()))) {
            return true;
         }
         s = s.getSuperclass();
      }
      return false;
   }

   /**
    * Getter for superClazz
    * 
    * @return the superClazz ClassModel 
    */
   ClassModel getSuperClazz() {
      return superClazz;
   }

   @Annotations.DocMe void replaceSuperClazz(ClassModel c) {
      if (this.superClazz != null) {
         assert c.isSuperClass(this.getClassWeAreModelling()) == true : "not my super";
         if (this.superClazz.getClassWeAreModelling().getName().equals(c.getClassWeAreModelling().getName())) {
            this.superClazz = c;
         } else {
            this.superClazz.replaceSuperClazz(c);
         }
      }
   }

   /**
    * Convert a given JNI character type (say 'I') to its type name ('int').
    * 
    * @param _typeChar
    * @return either a mapped type name or null if no mapping exists.
    */
   static String typeName(char _typeChar) {
      String returnName = null;
      switch (_typeChar) {
         case SIGC_VOID:
            returnName = "void";
            break;
         case SIGC_INT:
            returnName = "int";
            break;
         case SIGC_DOUBLE:
            returnName = "double";
            break;
         case SIGC_FLOAT:
            returnName = "float";
            break;
         case SIGC_SHORT:
            returnName = "short";
            break;
         case SIGC_CHAR:
            returnName = "char";
            break;
         case SIGC_BYTE:
            returnName = "byte";
            break;
         case SIGC_LONG:
            returnName = "long";
            break;
         case SIGC_BOOLEAN:
            returnName = "boolean";
            break;
      }
      return (returnName);
   }

   static String convert(String _string) {
      return (convert(_string, "", false));
   }

   static String convert(String _string, String _insert) {
      return (convert(_string, _insert, false));
   }

   static String convert(String _string, String _insert, boolean _showFullClassName) {
      Stack<String> stringStack = new Stack<String>();
      Stack<String> methodStack = null;
      int length = _string.length();
      char[] chars = _string.toCharArray();
      int i = 0;
      boolean inArray = false;
      boolean inMethod = false;
      boolean inArgs = false;
      int args = 0;
      while (i < length) {
         switch (chars[i]) {
            case SIGC_CLASS: {
               StringBuilder classNameBuffer = new StringBuilder();
               i++;
               while ((i < length) && chars[i] != SIGC_END_CLASS) {
                  if (chars[i] == SIGC_PACKAGE) {
                     classNameBuffer.append('.');
                  } else {
                     classNameBuffer.append(chars[i]);
                  }
                  i++;
               }
               i++; // step over SIGC_ENDCLASS
               String className = classNameBuffer.toString();
               if (_showFullClassName) {
                  if (className.startsWith("java.lang")) {
                     className = className.substring("java.lang.".length());
                  }
               } else {
                  int lastDot = className.lastIndexOf('.');
                  if (lastDot > 0) {
                     className = className.substring(lastDot + 1);
                  }
               }
               if (inArray) {
                  // swap the stack items
                  String popped = stringStack.pop();
                  if (inArgs && args > 0) {
                     stringStack.push(", ");
                  }
                  stringStack.push(className);
                  stringStack.push(popped);
                  inArray = false;
               } else {
                  if (inArgs && args > 0) {
                     stringStack.push(", ");
                  }
                  stringStack.push(className);
               }
               args++;
            }
               break;
            case SIGC_ARRAY: {
               StringBuilder arrayDims = new StringBuilder();
               while ((i < length) && chars[i] == SIGC_ARRAY) {
                  arrayDims.append("[]");
                  i++;
               }
               stringStack.push(arrayDims.toString());
               inArray = true;
            }
               break;
            case SIGC_VOID:
            case SIGC_INT:
            case SIGC_DOUBLE:
            case SIGC_FLOAT:
            case SIGC_SHORT:
            case SIGC_CHAR:
            case SIGC_BYTE:
            case SIGC_LONG:
            case SIGC_BOOLEAN: {
               if (inArray) {
                  // swap the stack items
                  String popped = stringStack.pop();
                  if (inArgs && args > 0) {
                     stringStack.push(", ");
                  }
                  stringStack.push(typeName(chars[i]));
                  stringStack.push(popped);
                  inArray = false;
               } else {
                  if (inArgs && args > 0) {
                     stringStack.push(", ");
                  }
                  stringStack.push(typeName(chars[i]));
               }
               i++; // step over this
            }
               break;
            case SIGC_START_METHOD: {
               stringStack.push("(");
               i++; // step over this
               inArgs = true;
               args = 0;
            }
               break;
            case SIGC_END_METHOD: {
               inMethod = true;
               inArgs = false;
               stringStack.push(")");
               methodStack = stringStack;
               stringStack = new Stack<String>();
               i++; // step over this
            }
               break;
         }
      }

      StringBuilder returnValue = new StringBuilder();
      for (String s : stringStack) {
         returnValue.append(s);
         returnValue.append(" ");

      }
      if (inMethod) {
         for (String s : methodStack) {
            returnValue.append(s);
            returnValue.append(" ");
         }
      } else {
         returnValue.append(_insert);
      }
      return (returnValue.toString());
   }

   static class MethodDescription{
      private String className;

      private String methodName;

      private String type;

      private String[] args;

      MethodDescription(String _className, String _methodName, String _type, String[] _args) {
         methodName = _methodName;
         className = _className;
         type = _type;
         args = _args;
      }

      String[] getArgs() {
         return (args);
      }

      String getType() {
         return (type);
      }

      String getClassName() {
         return (className);
      }

      String getMethodName() {
         return (methodName);
      }
   }

   static MethodDescription getMethodDescription(String _string) {
      String className = null;
      String methodName = null;
      String descriptor = null;
      MethodDescription methodDescription = null;
      if (_string.startsWith("(")) {
         className = "?";
         methodName = "?";
         descriptor = _string;
      } else {
         int parenIndex = _string.indexOf("(");
         int dotIndex = _string.indexOf(".");
         descriptor = _string.substring(parenIndex);
         className = _string.substring(0, dotIndex);
         methodName = _string.substring(dotIndex + 1, parenIndex);
      }
      Stack<String> stringStack = new Stack<String>();
      Stack<String> methodStack = null;
      int length = descriptor.length();
      char[] chars = new char[descriptor.length()];
      descriptor.getChars(0, descriptor.length(), chars, 0);
      int i = 0;
      boolean inArray = false;
      boolean inMethod = false;
      while (i < length) {
         switch (chars[i]) {
            case SIGC_CLASS: {
               StringBuilder stringBuffer = null;
               if (inArray) {
                  stringBuffer = new StringBuilder(stringStack.pop());
               } else {
                  stringBuffer = new StringBuilder();
               }
               while ((i < length) && chars[i] != SIGC_END_CLASS) {
                  stringBuffer.append(chars[i]);
                  i++;
               }
               stringBuffer.append(chars[i]);
               i++; // step over SIGC_ENDCLASS
               stringStack.push(stringBuffer.toString());
               inArray = false;
            }
               break;
            case SIGC_ARRAY: {
               StringBuilder stringBuffer = new StringBuilder();
               while ((i < length) && chars[i] == SIGC_ARRAY) {
                  stringBuffer.append(chars[i]);
                  i++;
               }
               stringStack.push(stringBuffer.toString());
               inArray = true;
            }
               break;
            case SIGC_VOID:
            case SIGC_INT:
            case SIGC_DOUBLE:
            case SIGC_FLOAT:
            case SIGC_SHORT:
            case SIGC_CHAR:
            case SIGC_BYTE:
            case SIGC_LONG:
            case SIGC_BOOLEAN: {
               StringBuilder stringBuffer = null;
               if (inArray) {
                  stringBuffer = new StringBuilder(stringStack.pop());
               } else {
                  stringBuffer = new StringBuilder();
               }
               stringBuffer.append(chars[i]);
               i++; // step over this
               stringStack.push(stringBuffer.toString());
               inArray = false;
            }
               break;
            case SIGC_START_METHOD: {
               i++; // step over this
            }
               break;
            case SIGC_END_METHOD: {
               inMethod = true;
               inArray = false;
               methodStack = stringStack;
               stringStack = new Stack<String>();
               i++; // step over this
            }
               break;
         }
      }
      if (inMethod) {
         methodDescription = new MethodDescription(className, methodName, stringStack.toArray(new String[0])[0], methodStack
               .toArray(new String[0]));
      } else {
         System.out.println("can't convert to a description");
      }
      return (methodDescription);
   }

   private int magic;

   private int minorVersion;

   private int majorVersion;

   private ConstantPool constantPool;

   private int accessFlags;

   private int thisClassConstantPoolIndex;

   private int superClassConstantPoolIndex;

   private List<ClassModelInterface> interfaces = new ArrayList<ClassModelInterface>();

   private List<ClassModelField> fields = new ArrayList<ClassModelField>();

   private List<ClassModelMethod> methods = new ArrayList<ClassModelMethod>();

   private AttributePool attributePool;

   enum ConstantPoolType {
      EMPTY, //0
      UTF8, //1
      UNICODE, //2
      INTEGER, //3
      FLOAT, //4
      LONG, //5
      DOUBLE, //6
      CLASS, //7
      STRING, //8
      FIELD, //9
      METHOD, //10
      INTERFACEMETHOD, //11
      NAMEANDTYPE, //12
      UNUSED13,
      UNUSED14,
      METHODHANDLE, //15
      METHODTYPE, //16
      UNUSED17,
      INVOKEDYNAMIC
      //18
   };

   enum Access {
      PUBLIC(0x00000001),
      PRIVATE(0x00000002),
      PROTECTED(0x00000004),
      STATIC(0x00000008),
      FINAL(0x00000010),
      ACC_SYNCHRONIZED(0x00000020),
      ACC_VOLATILE(0x00000040),
      BRIDGE(0x00000040),
      TRANSIENT(0x00000080),
      VARARGS(0x00000080),
      NATIVE(0x00000100),
      INTERFACE(0x00000200),
      ABSTRACT(0x00000400),
      SUPER(0x00000020),
      STRICT(0x00000800),
      ANNOTATION(0x00002000),
      ACC_ENUM(0x00004000);
      int bits;

      Access(int _bits) {
         bits = _bits;
      }

      boolean bitIsSet(int _accessFlags) {
         return ((bits & _accessFlags) == bits);
      }

      String convert(int _accessFlags) {
         StringBuffer stringBuffer = new StringBuffer();
         for (Access access : Access.values()) {
            if (access.bitIsSet(_accessFlags)) {
               stringBuffer.append(" " + access.name().toLowerCase());
            }
         }
         return (stringBuffer.toString());
      }
   }

   private static enum SignatureParseState {
      skipping,
      counting,
      inclass,
      inArray,
      done;
   };

   class ConstantPool implements Iterable<ConstantPool.Entry>{

      private List<Entry> entries = new ArrayList<Entry>();

      abstract class Entry{
         private ConstantPoolType constantPoolType;

         private int slot;

         Entry(ByteReader _byteReader, int _slot, ConstantPoolType _constantPoolType) {
            constantPoolType = _constantPoolType;
            slot = _slot;
         }

         ConstantPoolType getConstantPoolType() {
            return (constantPoolType);
         }

         int getSlot() {
            return (slot);
         }

      }

      class ClassEntry extends Entry{

         private int nameIndex;

         ClassEntry(ByteReader _byteReader, int _slot) {
            super(_byteReader, _slot, ConstantPoolType.CLASS);
            nameIndex = _byteReader.u2();
         }

         int getNameIndex() {
            return (nameIndex);
         }

         UTF8Entry getNameUTF8Entry() {
            return (ConstantPool.this.getUTF8Entry(nameIndex));
         }

      }

      class DoubleEntry extends Entry{
         private double doubleValue;

         DoubleEntry(ByteReader _byteReader, int _slot) {
            super(_byteReader, _slot, ConstantPoolType.DOUBLE);
            doubleValue = _byteReader.d8();
         }

         double getDoubleValue() {
            return (doubleValue);
         }

      }

      class EmptyEntry extends Entry{
         EmptyEntry(ByteReader _byteReader, int _slot) {
            super(_byteReader, _slot, ConstantPoolType.EMPTY);
         }

      }

      class FieldEntry extends ReferenceEntry{

         FieldEntry(ByteReader _byteReader, int _slot) {
            super(_byteReader, _slot, ConstantPoolType.FIELD);
         }

      }

      class FloatEntry extends Entry{
         private float floatValue;

         FloatEntry(ByteReader _byteReader, int _slot) {
            super(_byteReader, _slot, ConstantPoolType.FLOAT);
            floatValue = _byteReader.f4();
         }

         float getFloatValue() {
            return (floatValue);
         }

      }

      class IntegerEntry extends Entry{
         private int intValue;

         IntegerEntry(ByteReader _byteReader, int _slot) {
            super(_byteReader, _slot, ConstantPoolType.INTEGER);
            intValue = _byteReader.u4();
         }

         int getIntValue() {
            return (intValue);
         }

      }

      class InterfaceMethodEntry extends MethodReferenceEntry{
         InterfaceMethodEntry(ByteReader _byteReader, int _slot) {
            super(_byteReader, _slot, ConstantPoolType.INTERFACEMETHOD);
         }
      }

      class LongEntry extends Entry{
         private long longValue;

         LongEntry(ByteReader _byteReader, int _slot) {
            super(_byteReader, _slot, ConstantPoolType.LONG);
            longValue = _byteReader.u8();
         }

         long getLongValue() {
            return (longValue);
         }

      }

      class MethodEntry extends MethodReferenceEntry{

         MethodEntry(ByteReader _byteReader, int _slot) {
            super(_byteReader, _slot, ConstantPoolType.METHOD);
         }

         @Override public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(getClassEntry().getNameUTF8Entry().getUTF8());
            sb.append(".");
            sb.append(getNameAndTypeEntry().getNameUTF8Entry().getUTF8());
            sb.append(getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8());
            return (sb.toString());
         }

      }

      class NameAndTypeEntry extends Entry{
         private int descriptorIndex;

         private int nameIndex;

         NameAndTypeEntry(ByteReader _byteReader, int _slot) {
            super(_byteReader, _slot, ConstantPoolType.NAMEANDTYPE);
            nameIndex = _byteReader.u2();
            descriptorIndex = _byteReader.u2();
         }

         int getDescriptorIndex() {
            return (descriptorIndex);
         }

         UTF8Entry getDescriptorUTF8Entry() {
            return (ConstantPool.this.getUTF8Entry(descriptorIndex));
         }

         int getNameIndex() {
            return (nameIndex);
         }

         UTF8Entry getNameUTF8Entry() {
            return (ConstantPool.this.getUTF8Entry(nameIndex));
         }

      }

      class MethodTypeEntry extends Entry{
         private int descriptorIndex;

         MethodTypeEntry(ByteReader _byteReader, int _slot) {
            super(_byteReader, _slot, ConstantPoolType.METHODTYPE);
            descriptorIndex = _byteReader.u2();
         }

         int getDescriptorIndex() {
            return (descriptorIndex);
         }

         UTF8Entry getDescriptorUTF8Entry() {
            return (ConstantPool.this.getUTF8Entry(descriptorIndex));
         }

      }

      class MethodHandleEntry extends Entry{
         // http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.4

         private int referenceKind;

         private int referenceIndex;

         MethodHandleEntry(ByteReader _byteReader, int _slot) {
            super(_byteReader, _slot, ConstantPoolType.METHODHANDLE);
            referenceKind = _byteReader.u1();
            referenceIndex = _byteReader.u2();
         }

         int getReferenceIndex() {
            return (referenceIndex);
         }

         int getReferenceKind() {
            return (referenceKind);
         }

      }

      class InvokeDynamicEntry extends Entry{
         // http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.4
         private int descriptorIndex;

         private int bootstrapMethodAttrIndex;

         private int nameAndTypeIndex;

         InvokeDynamicEntry(ByteReader _byteReader, int _slot) {
            super(_byteReader, _slot, ConstantPoolType.INVOKEDYNAMIC);
            bootstrapMethodAttrIndex = _byteReader.u2();
            nameAndTypeIndex = _byteReader.u2();
         }

         int getDescriptorIndex() {
            return (descriptorIndex);
         }

         UTF8Entry getDescriptorUTF8Entry() {
            return (ConstantPool.this.getUTF8Entry(descriptorIndex));
         }

         int getBootstrapMethodAttrIndex() {
            return (bootstrapMethodAttrIndex);
         }

         int getNameAndTypeIndex() {
            return (nameAndTypeIndex);
         }

      }

      abstract class MethodReferenceEntry extends ReferenceEntry{

         class Arg extends Type{
            Arg(String _signature, int _start, int _pos, int _argc) {
               super(_signature.substring(_start, _pos + 1));
               argc = _argc;
            }

            private int argc;

            int getArgc() {
               return (argc);
            }
         }

         private Arg[] args = null;

         private Type returnType = null;

         @Override public int hashCode() {
            NameAndTypeEntry nameAndTypeEntry = getNameAndTypeEntry();

            return ((nameAndTypeEntry.getNameIndex() * 31 + nameAndTypeEntry.getDescriptorIndex()) * 31 + getClassIndex());
         }

         @Override public boolean equals(Object _other) {
            if (_other == null || !(_other instanceof MethodReferenceEntry)) {
               return (false);
            } else {
               MethodReferenceEntry otherMethodReferenceEntry = (MethodReferenceEntry) _other;
               return (otherMethodReferenceEntry.getNameAndTypeEntry().getNameIndex() == getNameAndTypeEntry().getNameIndex()
                     && otherMethodReferenceEntry.getNameAndTypeEntry().getDescriptorIndex() == getNameAndTypeEntry()
                           .getDescriptorIndex() && otherMethodReferenceEntry.getClassIndex() == getClassIndex());
            }
         }

         MethodReferenceEntry(ByteReader byteReader, int slot, ConstantPoolType constantPoolType) {
            super(byteReader, slot, constantPoolType);

         }

         int getStackProduceCount() {
            return (getReturnType().isVoid() ? 0 : 1);
         }

         Type getReturnType() {
            if (returnType == null) {
               getArgs();
            }
            return (returnType);
         }

         Arg[] getArgs() {
            if (args == null || returnType == null) {
               List<Arg> argList = new ArrayList<Arg>();
               NameAndTypeEntry nameAndTypeEntry = getNameAndTypeEntry();

               String signature = nameAndTypeEntry.getDescriptorUTF8Entry().getUTF8();// "([[IF)V" for a method that takes an int[][], float and returns void.
               // Sadly we need to parse this, we need the # of arguments for the call
               SignatureParseState state = SignatureParseState.skipping;
               int start = 0;

               for (int pos = 0; state != SignatureParseState.done; pos++) {
                  char ch = signature.charAt(pos);
                  switch (ch) {
                     case '(':
                        state = SignatureParseState.counting;
                        break;
                     case ')':
                        state = SignatureParseState.done;
                        returnType = new Type(signature.substring(pos + 1));
                        break;
                     case '[':
                        switch (state) {
                           case counting:
                              state = SignatureParseState.inArray;
                              start = pos;
                              break;

                        }
                        // we don't care about arrays
                        break;
                     case 'L':
                        // beginning of Ljava/lang/String; or something

                        switch (state) {
                           case counting:
                              start = pos;
                              // fallthrough intended!!
                           case inArray:
                              state = SignatureParseState.inclass;
                              break;
                        }
                        break;
                     case ';':
                        // note we will only be in 'inclass' if we were previously counting, so this is safe
                        switch (state) {
                           case inclass:
                              argList.add(new Arg(signature, start, pos, argList.size()));
                              state = SignatureParseState.counting;
                              break;
                        }
                        break;

                     default:
                        // we have IJBZDF so inc counter if we are still counting
                        switch (state) {
                           case counting:
                              start = pos;
                              // fallthrough intended!!
                           case inArray:
                              argList.add(new Arg(signature, start, pos, argList.size()));
                              break;

                        }
                        break;
                  }

               }
               // System.out.println("method "+name+" has signature of "+signature+" which has "+count+" args");

               args = argList.toArray(new Arg[0]);
            }
            return (args);

         }

         int getStackConsumeCount() {
            return (getArgs().length);
         }
      }

      abstract class ReferenceEntry extends Entry{
         protected int referenceClassIndex;

         protected int nameAndTypeIndex;

         protected int argCount = -1;

         ReferenceEntry(ByteReader _byteReader, int _slot, ConstantPoolType _constantPoolType) {
            super(_byteReader, _slot, _constantPoolType);
            referenceClassIndex = _byteReader.u2();
            nameAndTypeIndex = _byteReader.u2();
         }

         ClassEntry getClassEntry() {
            return (ConstantPool.this.getClassEntry(referenceClassIndex));
         }

         int getClassIndex() {
            return (referenceClassIndex);
         }

         NameAndTypeEntry getNameAndTypeEntry() {
            return (ConstantPool.this.getNameAndTypeEntry(nameAndTypeIndex));
         }

         int getNameAndTypeIndex() {
            return (nameAndTypeIndex);
         }

         boolean same(Entry _entry) {
            if (_entry instanceof ReferenceEntry) {
               ReferenceEntry entry = (ReferenceEntry) _entry;
               return ((referenceClassIndex == entry.referenceClassIndex) && (nameAndTypeIndex == entry.nameAndTypeIndex));
            }
            return (false);
         }

         class Type{
            private int arrayDimensions = 0;

            Type(String _type) {
               type = _type;

               while (type.charAt(arrayDimensions) == '[') {
                  arrayDimensions++;
               }
               type = type.substring(arrayDimensions);
            }

            String getType() {
               return (type);
            }

            boolean isVoid() {
               return (type.equals("V"));
            }

            private String type;

            final boolean isArray() {
               return (arrayDimensions > 0);
            }

            final int getArrayDimensions() {
               return (arrayDimensions);
            }

         }

      }

      class StringEntry extends Entry{
         private int utf8Index;

         StringEntry(ByteReader _byteReader, int _slot) {
            super(_byteReader, _slot, ConstantPoolType.STRING);
            utf8Index = _byteReader.u2();
         }

         int getUTF8Index() {
            return (utf8Index);
         }

         UTF8Entry getStringUTF8Entry() {
            return (ConstantPool.this.getUTF8Entry(utf8Index));
         }
      }

      class UTF8Entry extends Entry{
         private String UTF8;

         UTF8Entry(ByteReader _byteReader, int _slot) {
            super(_byteReader, _slot, ConstantPoolType.UTF8);
            UTF8 = _byteReader.utf8();
         }

         String getUTF8() {
            return (UTF8);
         }

      }

      ConstantPool(ByteReader _byteReader) {
         int size = _byteReader.u2();
         add(new EmptyEntry(_byteReader, 0)); // slot 0

         for (int i = 1; i < size; i++) {
            ConstantPoolType constantPoolType = ConstantPoolType.values()[_byteReader.u1()];

            switch (constantPoolType) {
               case UTF8:
                  add(new UTF8Entry(_byteReader, i));
                  break;
               case INTEGER:
                  add(new IntegerEntry(_byteReader, i));
                  break;
               case FLOAT:
                  add(new FloatEntry(_byteReader, i));
                  break;
               case LONG:
                  add(new LongEntry(_byteReader, i));
                  i++;// Longs take two slots in the ConstantPool
                  add(new EmptyEntry(_byteReader, i));
                  break;
               case DOUBLE:
                  add(new DoubleEntry(_byteReader, i));
                  i++; // Doubles take two slots in the ConstantPool
                  add(new EmptyEntry(_byteReader, i));
                  break;
               case CLASS:
                  add(new ClassEntry(_byteReader, i));
                  break;
               case STRING:
                  add(new StringEntry(_byteReader, i));
                  break;
               case FIELD:
                  add(new FieldEntry(_byteReader, i));
                  break;
               case METHOD:
                  add(new MethodEntry(_byteReader, i));
                  break;
               case INTERFACEMETHOD:
                  add(new InterfaceMethodEntry(_byteReader, i));
                  break;
               case NAMEANDTYPE:
                  add(new NameAndTypeEntry(_byteReader, i));
                  break;
               case METHODHANDLE:
                  add(new MethodHandleEntry(_byteReader, i));
                  break;
               case METHODTYPE:
                  add(new MethodTypeEntry(_byteReader, i));
                  break;
               case INVOKEDYNAMIC:
                  add(new InvokeDynamicEntry(_byteReader, i));
                  break;
               default:
                  System.out.printf("slot %04x unexpected Constant constantPoolType = %s\n", i, constantPoolType);

            }

         }
      }

      ClassEntry getClassEntry(int _index) {
         try {
            return ((ClassEntry) entries.get(_index));
         } catch (ClassCastException e) {
            return (null);
         }
      }

      DoubleEntry getDoubleEntry(int _index) {
         try {
            return ((DoubleEntry) entries.get(_index));
         } catch (ClassCastException e) {
            return (null);
         }
      }

      FieldEntry getFieldEntry(int _index) {
         try {
            return ((FieldEntry) entries.get(_index));
         } catch (ClassCastException e) {
            return (null);
         }
      }

      FloatEntry getFloatEntry(int _index) {
         try {
            return ((FloatEntry) entries.get(_index));
         } catch (ClassCastException e) {
            return (null);
         }
      }

      IntegerEntry getIntegerEntry(int _index) {
         try {
            return ((IntegerEntry) entries.get(_index));
         } catch (ClassCastException e) {
            return (null);
         }
      }

      InterfaceMethodEntry getInterfaceMethodEntry(int _index) {
         try {
            return ((InterfaceMethodEntry) entries.get(_index));
         } catch (ClassCastException e) {
            return (null);
         }
      }

      LongEntry getLongEntry(int _index) {
         try {
            return ((LongEntry) entries.get(_index));
         } catch (ClassCastException e) {
            return (null);
         }
      }

      MethodEntry getMethodEntry(int _index) {
         try {
            return ((MethodEntry) entries.get(_index));
         } catch (ClassCastException e) {
            return (null);
         }
      }

      NameAndTypeEntry getNameAndTypeEntry(int _index) {
         try {
            return ((NameAndTypeEntry) entries.get(_index));
         } catch (ClassCastException e) {
            return (null);
         }
      }

      StringEntry getStringEntry(int _index) {
         try {
            return ((StringEntry) entries.get(_index));
         } catch (ClassCastException e) {
            return (null);
         }
      }

      UTF8Entry getUTF8Entry(int _index) {
         try {
            return ((UTF8Entry) entries.get(_index));
         } catch (ClassCastException e) {
            return (null);
         }
      }

      void add(Entry _entry) {
         entries.add(_entry);

      }

      @Override public Iterator<Entry> iterator() {
         return (entries.iterator());
      }

      Entry get(int _index) {
         return (entries.get(_index));
      }

      String getDescription(ConstantPool.Entry _entry) {
         StringBuilder sb = new StringBuilder();
         if (_entry instanceof ConstantPool.EmptyEntry) {
            ;
         } else if (_entry instanceof ConstantPool.DoubleEntry) {
            ConstantPool.DoubleEntry doubleEntry = (ConstantPool.DoubleEntry) _entry;
            sb.append(doubleEntry.getDoubleValue());
         } else if (_entry instanceof ConstantPool.FloatEntry) {
            ConstantPool.FloatEntry floatEntry = (ConstantPool.FloatEntry) _entry;
            sb.append(floatEntry.getFloatValue());
         } else if (_entry instanceof ConstantPool.IntegerEntry) {
            ConstantPool.IntegerEntry integerEntry = (ConstantPool.IntegerEntry) _entry;
            sb.append(integerEntry.getIntValue());
         } else if (_entry instanceof ConstantPool.LongEntry) {
            ConstantPool.LongEntry longEntry = (ConstantPool.LongEntry) _entry;
            sb.append(longEntry.getLongValue());
         } else if (_entry instanceof ConstantPool.UTF8Entry) {
            ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) _entry;
            sb.append(utf8Entry.getUTF8());
         } else if (_entry instanceof ConstantPool.StringEntry) {
            ConstantPool.StringEntry stringEntry = (ConstantPool.StringEntry) _entry;
            ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) get(stringEntry.getUTF8Index());
            sb.append(utf8Entry.getUTF8());
         } else if (_entry instanceof ConstantPool.ClassEntry) {
            ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry) _entry;
            ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) get(classEntry.getNameIndex());
            sb.append(utf8Entry.getUTF8());
         } else if (_entry instanceof ConstantPool.NameAndTypeEntry) {
            ConstantPool.NameAndTypeEntry nameAndTypeEntry = (ConstantPool.NameAndTypeEntry) _entry;
            ConstantPool.UTF8Entry utf8NameEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getNameIndex());
            ConstantPool.UTF8Entry utf8DescriptorEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getDescriptorIndex());
            sb.append(utf8NameEntry.getUTF8() + "." + utf8DescriptorEntry.getUTF8());
         } else if (_entry instanceof ConstantPool.MethodEntry) {
            ConstantPool.MethodEntry methodEntry = (ConstantPool.MethodEntry) _entry;
            ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry) get(methodEntry.getClassIndex());
            ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) get(classEntry.getNameIndex());
            ConstantPool.NameAndTypeEntry nameAndTypeEntry = (ConstantPool.NameAndTypeEntry) get(methodEntry.getNameAndTypeIndex());
            ConstantPool.UTF8Entry utf8NameEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getNameIndex());
            ConstantPool.UTF8Entry utf8DescriptorEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getDescriptorIndex());
            sb.append(convert(utf8DescriptorEntry.getUTF8(), utf8Entry.getUTF8() + "." + utf8NameEntry.getUTF8()));
         } else if (_entry instanceof ConstantPool.InterfaceMethodEntry) {
            ConstantPool.InterfaceMethodEntry interfaceMethodEntry = (ConstantPool.InterfaceMethodEntry) _entry;
            ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry) get(interfaceMethodEntry.getClassIndex());
            ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) get(classEntry.getNameIndex());
            ConstantPool.NameAndTypeEntry nameAndTypeEntry = (ConstantPool.NameAndTypeEntry) get(interfaceMethodEntry
                  .getNameAndTypeIndex());
            ConstantPool.UTF8Entry utf8NameEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getNameIndex());
            ConstantPool.UTF8Entry utf8DescriptorEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getDescriptorIndex());
            sb.append(convert(utf8DescriptorEntry.getUTF8(), utf8Entry.getUTF8() + "." + utf8NameEntry.getUTF8()));
         } else if (_entry instanceof ConstantPool.FieldEntry) {
            ConstantPool.FieldEntry fieldEntry = (ConstantPool.FieldEntry) _entry;
            ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry) get(fieldEntry.getClassIndex());
            ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) get(classEntry.getNameIndex());
            ConstantPool.NameAndTypeEntry nameAndTypeEntry = (ConstantPool.NameAndTypeEntry) get(fieldEntry.getNameAndTypeIndex());
            ConstantPool.UTF8Entry utf8NameEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getNameIndex());
            ConstantPool.UTF8Entry utf8DescriptorEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getDescriptorIndex());
            sb.append(convert(utf8DescriptorEntry.getUTF8(), utf8Entry.getUTF8() + "." + utf8NameEntry.getUTF8()));
         }
         return (sb.toString());
      }

      int[] getConstantPoolReferences(ConstantPool.Entry _entry) {
         int[] references = new int[0];
         if (_entry instanceof ConstantPool.StringEntry) {
            ConstantPool.StringEntry stringEntry = (ConstantPool.StringEntry) _entry;
            references = new int[] {
               stringEntry.getUTF8Index()
            };
         } else if (_entry instanceof ConstantPool.ClassEntry) {
            ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry) _entry;
            references = new int[] {
               classEntry.getNameIndex()
            };
         } else if (_entry instanceof ConstantPool.NameAndTypeEntry) {
            ConstantPool.NameAndTypeEntry nameAndTypeEntry = (ConstantPool.NameAndTypeEntry) _entry;
            references = new int[] {
                  nameAndTypeEntry.getNameIndex(),
                  nameAndTypeEntry.getDescriptorIndex()
            };
         } else if (_entry instanceof ConstantPool.MethodEntry) {
            ConstantPool.MethodEntry methodEntry = (ConstantPool.MethodEntry) _entry;
            ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry) get(methodEntry.getClassIndex());
            @SuppressWarnings("unused") ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) get(classEntry.getNameIndex());
            ConstantPool.NameAndTypeEntry nameAndTypeEntry = (ConstantPool.NameAndTypeEntry) get(methodEntry.getNameAndTypeIndex());
            @SuppressWarnings("unused") ConstantPool.UTF8Entry utf8NameEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry
                  .getNameIndex());
            @SuppressWarnings("unused") ConstantPool.UTF8Entry utf8DescriptorEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry
                  .getDescriptorIndex());
            references = new int[] {
                  methodEntry.getClassIndex(),
                  classEntry.getNameIndex(),
                  nameAndTypeEntry.getNameIndex(),
                  nameAndTypeEntry.getDescriptorIndex()
            };
         } else if (_entry instanceof ConstantPool.InterfaceMethodEntry) {
            ConstantPool.InterfaceMethodEntry interfaceMethodEntry = (ConstantPool.InterfaceMethodEntry) _entry;
            ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry) get(interfaceMethodEntry.getClassIndex());
            @SuppressWarnings("unused") ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) get(classEntry.getNameIndex());
            ConstantPool.NameAndTypeEntry nameAndTypeEntry = (ConstantPool.NameAndTypeEntry) get(interfaceMethodEntry
                  .getNameAndTypeIndex());
            @SuppressWarnings("unused") ConstantPool.UTF8Entry utf8NameEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry
                  .getNameIndex());
            @SuppressWarnings("unused") ConstantPool.UTF8Entry utf8DescriptorEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry
                  .getDescriptorIndex());
            references = new int[] {
                  interfaceMethodEntry.getClassIndex(),
                  classEntry.getNameIndex(),
                  nameAndTypeEntry.getNameIndex(),
                  nameAndTypeEntry.getDescriptorIndex()
            };
         } else if (_entry instanceof ConstantPool.FieldEntry) {
            ConstantPool.FieldEntry fieldEntry = (ConstantPool.FieldEntry) _entry;
            ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry) get(fieldEntry.getClassIndex());
            @SuppressWarnings("unused") ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) get(classEntry.getNameIndex());
            ConstantPool.NameAndTypeEntry nameAndTypeEntry = (ConstantPool.NameAndTypeEntry) get(fieldEntry.getNameAndTypeIndex());
            @SuppressWarnings("unused") ConstantPool.UTF8Entry utf8NameEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry
                  .getNameIndex());
            @SuppressWarnings("unused") ConstantPool.UTF8Entry utf8DescriptorEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry
                  .getDescriptorIndex());
            references = new int[] {
                  fieldEntry.getClassIndex(),
                  classEntry.getNameIndex(),
                  nameAndTypeEntry.getNameIndex(),
                  nameAndTypeEntry.getDescriptorIndex()
            };
         }
         return (references);
      }

      String getType(ConstantPool.Entry _entry) {
         StringBuffer sb = new StringBuffer();
         if (_entry instanceof ConstantPool.EmptyEntry) {
            sb.append("empty");
         } else if (_entry instanceof ConstantPool.DoubleEntry) {
            sb.append("double");
         } else if (_entry instanceof ConstantPool.FloatEntry) {
            sb.append("float");
         } else if (_entry instanceof ConstantPool.IntegerEntry) {
            sb.append("int");
         } else if (_entry instanceof ConstantPool.LongEntry) {
            sb.append("long");
         } else if (_entry instanceof ConstantPool.UTF8Entry) {
            sb.append("utf8");
         } else if (_entry instanceof ConstantPool.StringEntry) {
            sb.append("string");
         } else if (_entry instanceof ConstantPool.ClassEntry) {
            sb.append("class");
         } else if (_entry instanceof ConstantPool.NameAndTypeEntry) {
            sb.append("name/type");
         } else if (_entry instanceof ConstantPool.MethodEntry) {
            sb.append("method");
         } else if (_entry instanceof ConstantPool.InterfaceMethodEntry) {
            sb.append("interface method");
         } else if (_entry instanceof ConstantPool.FieldEntry) {
            sb.append("field");
         }
         return (sb.toString());
      }

      Object getConstantEntry(int _constantPoolIndex) {
         Entry entry = get(_constantPoolIndex);
         Object object = null;
         switch (entry.getConstantPoolType()) {
            case FLOAT:
               object = ((FloatEntry) entry).getFloatValue();
               break;
            case DOUBLE:
               object = ((DoubleEntry) entry).getDoubleValue();
               break;
            case INTEGER:
               object = ((IntegerEntry) entry).getIntValue();
               break;
            case LONG:
               object = ((LongEntry) entry).getLongValue();
               break;
            case STRING:
               object = ((StringEntry) entry).getStringUTF8Entry().getUTF8();
               break;
         }
         return (object);
      }
   }

   class AttributePool{
      private List<AttributePoolEntry> attributePoolEntries = new ArrayList<AttributePoolEntry>();

      class CodeEntry extends AttributePoolEntry{

         class ExceptionPoolEntry{
            private int exceptionClassIndex;

            private int end;

            private int handler;

            private int start;

            ExceptionPoolEntry(ByteReader _byteReader) {
               start = _byteReader.u2();
               end = _byteReader.u2();
               handler = _byteReader.u2();
               exceptionClassIndex = _byteReader.u2();
            }

            ConstantPool.ClassEntry getClassEntry() {
               return (constantPool.getClassEntry(exceptionClassIndex));
            }

            int getClassIndex() {
               return (exceptionClassIndex);
            }

            int getEnd() {
               return (end);
            }

            int getHandler() {
               return (handler);
            }

            int getStart() {
               return (start);
            }

         }

         private List<ExceptionPoolEntry> exceptionPoolEntries = new ArrayList<ExceptionPoolEntry>();

         private AttributePool codeEntryAttributePool;

         private byte[] code;

         private int maxLocals;

         private int maxStack;

         CodeEntry(ByteReader _byteReader, int _nameIndex, int _length) {
            super(_byteReader, _nameIndex, _length);
            maxStack = _byteReader.u2();
            maxLocals = _byteReader.u2();
            int codeLength = _byteReader.u4();
            code = _byteReader.bytes(codeLength);
            int exceptionTableLength = _byteReader.u2();
            for (int i = 0; i < exceptionTableLength; i++) {
               exceptionPoolEntries.add(new ExceptionPoolEntry(_byteReader));
            }
            codeEntryAttributePool = new AttributePool(_byteReader);
         }

         @Override AttributePool getAttributePool() {
            return (codeEntryAttributePool);
         }

         LineNumberTableEntry getLineNumberTableEntry() {
            return (codeEntryAttributePool.getLineNumberTableEntry());
         }

         int getMaxLocals() {
            return (maxLocals);
         }

         int getMaxStack() {
            return (maxStack);
         }

         byte[] getCode() {
            return code;
         }

         List<ExceptionPoolEntry> getExceptionPoolEntries() {
            return exceptionPoolEntries;
         }
      }

      class ConstantValueEntry extends AttributePoolEntry{
         private int index;

         ConstantValueEntry(ByteReader _byteReader, int _nameIndex, int _length) {
            super(_byteReader, _nameIndex, _length);
            index = _byteReader.u2();
         }

         int getIndex() {
            return (index);
         }

      }

      class DeprecatedEntry extends AttributePoolEntry{
         DeprecatedEntry(ByteReader _byteReader, int _nameIndex, int _length) {
            super(_byteReader, _nameIndex, _length);
         }

      }

      abstract class AttributePoolEntry{
         protected int length;

         protected int nameIndex;

         AttributePoolEntry(ByteReader _byteReader, int _nameIndex, int _length) {
            nameIndex = _nameIndex;
            length = _length;
         }

         AttributePool getAttributePool() {
            return (null);
         }

         int getLength() {
            return (length);
         }

         String getName() {
            return (constantPool.getUTF8Entry(nameIndex).getUTF8());
         }

         int getNameIndex() {
            return (nameIndex);
         }

      }

      abstract class PoolEntry<T> extends AttributePoolEntry implements Iterable<T>{
         private List<T> pool = new ArrayList<T>();

         List<T> getPool() {
            return (pool);
         }

         PoolEntry(ByteReader _byteReader, int _nameIndex, int _length) {
            super(_byteReader, _nameIndex, _length);
         }

         @Override public Iterator<T> iterator() {
            return (pool.iterator());
         }

      }

      class ExceptionEntry extends PoolEntry<Integer>{

         ExceptionEntry(ByteReader _byteReader, int _nameIndex, int _length) {
            super(_byteReader, _nameIndex, _length);
            int exceptionTableLength = _byteReader.u2();
            for (int i = 0; i < exceptionTableLength; i++) {
               getPool().add(_byteReader.u2());
            }
         }

      }

      class InnerClassesEntry extends PoolEntry<InnerClassesEntry.InnerClassInfo>{
         class InnerClassInfo{
            private int innerAccess;

            private int innerIndex;

            private int innerNameIndex;

            private int outerIndex;

            InnerClassInfo(ByteReader _byteReader) {
               innerIndex = _byteReader.u2();
               outerIndex = _byteReader.u2();
               innerNameIndex = _byteReader.u2();
               innerAccess = _byteReader.u2();
            }

            int getInnerAccess() {
               return (innerAccess);
            }

            int getInnerIndex() {
               return (innerIndex);
            }

            int getInnerNameIndex() {
               return (innerNameIndex);
            }

            int getOuterIndex() {
               return (outerIndex);
            }

         }

         InnerClassesEntry(ByteReader _byteReader, int _nameIndex, int _length) {
            super(_byteReader, _nameIndex, _length);
            int innerClassesTableLength = _byteReader.u2();
            for (int i = 0; i < innerClassesTableLength; i++) {
               getPool().add(new InnerClassInfo(_byteReader));
            }
         }

      }

      class LineNumberTableEntry extends PoolEntry<LineNumberTableEntry.StartLineNumberPair>{

         class StartLineNumberPair{
            private int lineNumber;

            private int start;

            StartLineNumberPair(ByteReader _byteReader) {
               start = _byteReader.u2();
               lineNumber = _byteReader.u2();
            }

            int getLineNumber() {
               return (lineNumber);
            }

            int getStart() {
               return (start);
            }

         }

         LineNumberTableEntry(ByteReader _byteReader, int _nameIndex, int _length) {
            super(_byteReader, _nameIndex, _length);
            int lineNumberTableLength = _byteReader.u2();
            for (int i = 0; i < lineNumberTableLength; i++) {
               getPool().add(new StartLineNumberPair(_byteReader));
            }
         }

         int getSourceLineNumber(int _start, boolean _exact) {
            Iterator<StartLineNumberPair> i = getPool().iterator();
            if (i.hasNext()) {
               StartLineNumberPair from = i.next();
               while (i.hasNext()) {
                  StartLineNumberPair to = i.next();
                  if (_exact) {
                     if (_start == from.getStart()) {
                        return (from.getLineNumber());
                     }
                  } else if (_start >= from.getStart() && _start < to.getStart()) {
                     return (from.getLineNumber());
                  }
                  from = to;
               }
               if (_exact) {
                  if (_start == from.getStart()) {
                     return (from.getLineNumber());
                  }
               } else if (_start >= from.getStart()) {
                  return (from.getLineNumber());
               }
            }
            return (-1);
         }

      }

      class EnclosingMethodEntry extends AttributePoolEntry{

         EnclosingMethodEntry(ByteReader _byteReader, int _nameIndex, int _length) {
            super(_byteReader, _nameIndex, _length);
            enclosingClassIndex = _byteReader.u2();
            enclosingMethodIndex = _byteReader.u2();
         }

         private int enclosingClassIndex;

         int getClassIndex() {
            return (enclosingClassIndex);
         }

         private int enclosingMethodIndex;

         int getMethodIndex() {
            return (enclosingMethodIndex);
         }

      }

      class SignatureEntry extends AttributePoolEntry{

         SignatureEntry(ByteReader _byteReader, int _nameIndex, int _length) {
            super(_byteReader, _nameIndex, _length);
            signatureIndex = _byteReader.u2();
         }

         private int signatureIndex;

         int getSignatureIndex() {
            return (signatureIndex);
         }

      }

      class RealLocalVariableTableEntry extends PoolEntry<RealLocalVariableTableEntry.RealLocalVariableInfo> implements
            LocalVariableTableEntry<RealLocalVariableTableEntry.RealLocalVariableInfo>{

         class RealLocalVariableInfo implements LocalVariableInfo{
            private int descriptorIndex;

            private int usageLength;

            private int variableNameIndex;

            private int start;

            private int variableIndex;

            RealLocalVariableInfo(ByteReader _byteReader) {
               start = _byteReader.u2();
               usageLength = _byteReader.u2();
               variableNameIndex = _byteReader.u2();
               descriptorIndex = _byteReader.u2();
               variableIndex = _byteReader.u2();
            }

            int getDescriptorIndex() {
               return (descriptorIndex);
            }

            int getLength() {
               return (usageLength);
            }

            int getNameIndex() {
               return (variableNameIndex);
            }

            @Override public int getStart() {
               return (start);
            }

            @Override public int getVariableIndex() {
               return (variableIndex);
            }

            @Override public String getVariableName() {
               return (constantPool.getUTF8Entry(variableNameIndex).getUTF8());
            }

            @Override public String getVariableDescriptor() {
               return (constantPool.getUTF8Entry(descriptorIndex).getUTF8());
            }

            @Override public int getEnd() {
               return (start + usageLength);
            }

            @Override public boolean isArray() {
               return (getVariableDescriptor().startsWith("["));
            }
         }

         RealLocalVariableTableEntry(ByteReader _byteReader, int _nameIndex, int _length) {
            super(_byteReader, _nameIndex, _length);
            int localVariableTableLength = _byteReader.u2();
            for (int i = 0; i < localVariableTableLength; i++) {
               getPool().add(new RealLocalVariableInfo(_byteReader));
            }
         }

         public LocalVariableInfo getVariable(int _pc, int _index) {
            RealLocalVariableInfo returnValue = null;
            // System.out.println("pc = " + _pc + " index = " + _index);
            for (RealLocalVariableInfo localVariableInfo : getPool()) {
               // System.out.println("   start=" + localVariableInfo.getStart() + " length=" + localVariableInfo.getLength()
               // + " varidx=" + localVariableInfo.getVariableIndex());
               if (_pc >= localVariableInfo.getStart() - 1 && _pc <= (localVariableInfo.getStart() + localVariableInfo.getLength())
                     && _index == localVariableInfo.getVariableIndex()) {
                  returnValue = localVariableInfo;
                  break;
               }
            }
            // System.out.println("returning " + returnValue);
            return (returnValue);
         }

         String getVariableName(int _pc, int _index) {
            String returnValue = "unknown";
            RealLocalVariableInfo localVariableInfo = (RealLocalVariableInfo) getVariable(_pc, _index);
            if (localVariableInfo != null) {
               returnValue = convert(constantPool.getUTF8Entry(localVariableInfo.getDescriptorIndex()).getUTF8(), constantPool
                     .getUTF8Entry(localVariableInfo.getNameIndex()).getUTF8());
            }
            // System.out.println("returning " + returnValue);
            return (returnValue);
         }

      }

      class BootstrapMethodsEntry extends AttributePoolEntry{
         // http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.21
         class BootstrapMethod{
            class BootstrapArgument{
               public BootstrapArgument(ByteReader _byteReader) {
                  argument = _byteReader.u2();
               }

               int argument;// u2;
            }

            public BootstrapMethod(ByteReader _byteReader) {
               bootstrapMethodRef = _byteReader.u2();
               numBootstrapArguments = _byteReader.u2();
               bootstrapArguments = new BootstrapArgument[numBootstrapArguments];
               for (int i = 0; i < numBootstrapArguments; i++) {
                  bootstrapArguments[i] = new BootstrapArgument(_byteReader);
               }
            }

            int bootstrapMethodRef; //u2

            int numBootstrapArguments; //u2

            BootstrapArgument bootstrapArguments[];
         }

         BootstrapMethodsEntry(ByteReader _byteReader, int _nameIndex, int _length) {
            super(_byteReader, _nameIndex, _length);
            numBootstrapMethods = _byteReader.u2();
            bootstrapMethods = new BootstrapMethod[numBootstrapMethods];
            for (int i = 0; i < numBootstrapMethods; i++) {
               bootstrapMethods[i] = new BootstrapMethod(_byteReader);
            }
         }

         private int numBootstrapMethods;

         BootstrapMethod bootstrapMethods[];

         int getNumBootstrapMethods() {
            return (numBootstrapMethods);
         }

      }

      class OtherEntry extends AttributePoolEntry{
         private byte[] bytes;

         OtherEntry(ByteReader _byteReader, int _nameIndex, int _length) {
            super(_byteReader, _nameIndex, _length);
            bytes = _byteReader.bytes(_length);
         }

         byte[] getBytes() {
            return (bytes);
         }

         @Override public String toString() {
            return (new String(bytes));
         }

      }

      class StackMapTableEntry extends AttributePoolEntry{
         private byte[] bytes;

         StackMapTableEntry(ByteReader _byteReader, int _nameIndex, int _length) {
            super(_byteReader, _nameIndex, _length);
            bytes = _byteReader.bytes(_length);
         }

         byte[] getBytes() {
            return (bytes);
         }

         @Override public String toString() {
            return (new String(bytes));
         }
      }

      class LocalVariableTypeTableEntry extends AttributePoolEntry{
         private byte[] bytes;

         LocalVariableTypeTableEntry(ByteReader _byteReader, int _nameIndex, int _length) {
            super(_byteReader, _nameIndex, _length);
            bytes = _byteReader.bytes(_length);
         }

         byte[] getBytes() {
            return (bytes);
         }

         @Override public String toString() {
            return (new String(bytes));
         }
      }

      class SourceFileEntry extends AttributePoolEntry{
         private int sourceFileIndex;

         SourceFileEntry(ByteReader _byteReader, int _nameIndex, int _length) {
            super(_byteReader, _nameIndex, _length);
            sourceFileIndex = _byteReader.u2();
         }

         int getSourceFileIndex() {
            return (sourceFileIndex);
         }

         String getSourceFileName() {
            return (constantPool.getUTF8Entry(sourceFileIndex).getUTF8());
         }

      }

      class SyntheticEntry extends AttributePoolEntry{
         SyntheticEntry(ByteReader _byteReader, int _nameIndex, int _length) {
            super(_byteReader, _nameIndex, _length);
         }

      }

      class RuntimeAnnotationsEntry extends PoolEntry<RuntimeAnnotationsEntry.AnnotationInfo>{

         class AnnotationInfo{
            private int typeIndex;

            private int elementValuePairCount;

            class ElementValuePair{
               class Value{
                  Value(int _tag) {
                     tag = _tag;
                  }

                  int tag;

               }

               class PrimitiveValue extends Value{
                  private int typeNameIndex;

                  private int constNameIndex;

                  PrimitiveValue(int _tag, ByteReader _byteReader) {
                     super(_tag);
                     typeNameIndex = _byteReader.u2();
                     constNameIndex = _byteReader.u2();
                  }

                  int getConstNameIndex() {
                     return (constNameIndex);
                  }

                  int getTypeNameIndex() {
                     return (typeNameIndex);
                  }
               }

               class EnumValue extends Value{
                  EnumValue(int _tag, ByteReader _byteReader) {
                     super(_tag);
                  }

               }

               class ArrayValue extends Value{
                  ArrayValue(int _tag, ByteReader _byteReader) {
                     super(_tag);
                  }

               }

               class ClassValue extends Value{
                  ClassValue(int _tag, ByteReader _byteReader) {
                     super(_tag);
                  }

               }

               class AnnotationValue extends Value{
                  AnnotationValue(int _tag, ByteReader _byteReader) {
                     super(_tag);
                  }

               }

               @SuppressWarnings("unused") private int elementNameIndex;

               @SuppressWarnings("unused") private Value value;

               ElementValuePair(ByteReader _byteReader) {
                  elementNameIndex = _byteReader.u2();
                  int tag = _byteReader.u1();

                  switch (tag) {
                     case SIGC_BYTE:
                     case SIGC_CHAR:
                     case SIGC_INT:
                     case SIGC_LONG:
                     case SIGC_DOUBLE:
                     case SIGC_FLOAT:
                     case SIGC_SHORT:
                     case SIGC_BOOLEAN:
                     case 's': // special for String
                        value = new PrimitiveValue(tag, _byteReader);
                        break;
                     case 'e': // special for Enum
                        value = new EnumValue(tag, _byteReader);
                        break;
                     case 'c': // special for class
                        value = new ClassValue(tag, _byteReader);
                        break;
                     case '@': // special for Annotation
                        value = new AnnotationValue(tag, _byteReader);
                        break;
                     case 'a': // special for array
                        value = new ArrayValue(tag, _byteReader);
                        break;
                  }

               }

            }

            ElementValuePair[] elementValuePairs;

            AnnotationInfo(ByteReader _byteReader) {
               typeIndex = _byteReader.u2();
               elementValuePairCount = _byteReader.u2();
               elementValuePairs = new ElementValuePair[elementValuePairCount];
               for (int i = 0; i < elementValuePairCount; i++) {
                  elementValuePairs[i] = new ElementValuePair(_byteReader);
               }
            }

            int getTypeIndex() {
               return (typeIndex);
            }

            String getTypeDescriptor() {
               return (constantPool.getUTF8Entry(typeIndex).getUTF8());
            }
         }

         RuntimeAnnotationsEntry(ByteReader _byteReader, int _nameIndex, int _length) {
            super(_byteReader, _nameIndex, _length);
            int localVariableTableLength = _byteReader.u2();
            for (int i = 0; i < localVariableTableLength; i++) {
               getPool().add(new AnnotationInfo(_byteReader));
            }
         }

      }

      private CodeEntry codeEntry = null;

      private EnclosingMethodEntry enclosingMethodEntry = null;

      private DeprecatedEntry deprecatedEntry = null;

      private ExceptionEntry exceptionEntry = null;

      private LineNumberTableEntry lineNumberTableEntry = null;

      private LocalVariableTableEntry localVariableTableEntry = null;

      private RuntimeAnnotationsEntry runtimeVisibleAnnotationsEntry;

      private RuntimeAnnotationsEntry runtimeInvisibleAnnotationsEntry;

      private SourceFileEntry sourceFileEntry = null;

      private SyntheticEntry syntheticEntry = null;

      private BootstrapMethodsEntry bootstrapMethodsEntry = null;

      private final static String LOCALVARIABLETABLE_TAG = "LocalVariableTable";

      private final static String CONSTANTVALUE_TAG = "ConstantValue";

      private final static String LINENUMBERTABLE_TAG = "LineNumberTable";

      private final static String SOURCEFILE_TAG = "SourceFile";

      private final static String SYNTHETIC_TAG = "Synthetic";

      private final static String EXCEPTIONS_TAG = "Exceptions";

      private final static String INNERCLASSES_TAG = "InnerClasses";

      private final static String DEPRECATED_TAG = "Deprecated";

      private final static String CODE_TAG = "Code";

      private final static String ENCLOSINGMETHOD_TAG = "EnclosingMethod";

      private final static String SIGNATURE_TAG = "Signature";

      private final static String RUNTIMEINVISIBLEANNOTATIONS_TAG = "RuntimeInvisibleAnnotations";

      private final static String RUNTIMEVISIBLEANNOTATIONS_TAG = "RuntimeVisibleAnnotations";

      private final static String BOOTSTRAPMETHODS_TAG = "BootstrapMethods";

      private final static String STACKMAPTABLE_TAG = "StackMapTable";

      private final static String LOCALVARIABLETYPETABLE_TAG = "LocalVariableTypeTable";

      AttributePool(ByteReader _byteReader) {

         int attributeCount = _byteReader.u2();
         AttributePoolEntry entry = null;
         for (int i = 0; i < attributeCount; i++) {
            int attributeNameIndex = _byteReader.u2();
            int length = _byteReader.u4();
            String attributeName = constantPool.getUTF8Entry(attributeNameIndex).getUTF8();
            if (attributeName.equals(LOCALVARIABLETABLE_TAG)) {
               localVariableTableEntry = new RealLocalVariableTableEntry(_byteReader, attributeNameIndex, length);
               entry = (RealLocalVariableTableEntry) localVariableTableEntry;
            } else if (attributeName.equals(CONSTANTVALUE_TAG)) {
               entry = new ConstantValueEntry(_byteReader, attributeNameIndex, length);
            } else if (attributeName.equals(LINENUMBERTABLE_TAG)) {
               lineNumberTableEntry = new LineNumberTableEntry(_byteReader, attributeNameIndex, length);
               entry = lineNumberTableEntry;
            } else if (attributeName.equals(SOURCEFILE_TAG)) {
               sourceFileEntry = new SourceFileEntry(_byteReader, attributeNameIndex, length);
               entry = sourceFileEntry;
            } else if (attributeName.equals(SYNTHETIC_TAG)) {
               syntheticEntry = new SyntheticEntry(_byteReader, attributeNameIndex, length);
               entry = syntheticEntry;
            } else if (attributeName.equals(EXCEPTIONS_TAG)) {
               exceptionEntry = new ExceptionEntry(_byteReader, attributeNameIndex, length);
               entry = exceptionEntry;
            } else if (attributeName.equals(INNERCLASSES_TAG)) {
               entry = new InnerClassesEntry(_byteReader, attributeNameIndex, length);
            } else if (attributeName.equals(DEPRECATED_TAG)) {
               deprecatedEntry = new DeprecatedEntry(_byteReader, attributeNameIndex, length);
               entry = deprecatedEntry;
            } else if (attributeName.equals(CODE_TAG)) {
               codeEntry = new CodeEntry(_byteReader, attributeNameIndex, length);
               entry = codeEntry;
            } else if (attributeName.equals(ENCLOSINGMETHOD_TAG)) {
               enclosingMethodEntry = new EnclosingMethodEntry(_byteReader, attributeNameIndex, length);
               entry = enclosingMethodEntry;
            } else if (attributeName.equals(SIGNATURE_TAG)) {
               entry = new SignatureEntry(_byteReader, attributeNameIndex, length);
            } else if (attributeName.equals(RUNTIMEINVISIBLEANNOTATIONS_TAG)) {
               runtimeInvisibleAnnotationsEntry = new RuntimeAnnotationsEntry(_byteReader, attributeNameIndex, length);
               entry = runtimeInvisibleAnnotationsEntry;
            } else if (attributeName.equals(RUNTIMEVISIBLEANNOTATIONS_TAG)) {
               runtimeVisibleAnnotationsEntry = new RuntimeAnnotationsEntry(_byteReader, attributeNameIndex, length);
               entry = runtimeVisibleAnnotationsEntry;
            } else if (attributeName.equals(BOOTSTRAPMETHODS_TAG)) {
               bootstrapMethodsEntry = new BootstrapMethodsEntry(_byteReader, attributeNameIndex, length);
               entry = bootstrapMethodsEntry;
               // http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.21
            } else if (attributeName.equals(STACKMAPTABLE_TAG)) {
               // http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.4
               entry = new StackMapTableEntry(_byteReader, attributeNameIndex, length);
            } else if (attributeName.equals(LOCALVARIABLETYPETABLE_TAG)) {
               // http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.14
               entry = new LocalVariableTypeTableEntry(_byteReader, attributeNameIndex, length);
            } else {
               logger.warning("Found unexpected Attribute (name = " + attributeName + ")");
               entry = new OtherEntry(_byteReader, attributeNameIndex, length);
               attributePoolEntries.add(entry);
            }
         }

      }

      CodeEntry getCodeEntry() {
         return (codeEntry);
      }

      DeprecatedEntry getDeprecatedEntry() {
         return (deprecatedEntry);
      }

      ExceptionEntry getExceptionEntry() {
         return (exceptionEntry);
      }

      LineNumberTableEntry getLineNumberTableEntry() {
         return (lineNumberTableEntry);
      }

      LocalVariableTableEntry getLocalVariableTableEntry() {
         return (localVariableTableEntry);
      }

      SourceFileEntry getSourceFileEntry() {
         return (sourceFileEntry);
      }

      SyntheticEntry getSyntheticEntry() {
         return (syntheticEntry);
      }

      RuntimeAnnotationsEntry getRuntimeInvisibleAnnotationsEntry() {
         return (runtimeInvisibleAnnotationsEntry);
      }

      RuntimeAnnotationsEntry getRuntimeVisibleAnnotationsEntry() {
         return (runtimeVisibleAnnotationsEntry);
      }

      RuntimeAnnotationsEntry getBootstrap() {
         return (runtimeVisibleAnnotationsEntry);
      }

   }

   static ClassLoader classModelLoader = ClassModel.class.getClassLoader();

   class ClassModelField{
      private int fieldAccessFlags;

      AttributePool fieldAttributePool;

      private int descriptorIndex;

      private int index;

      private int nameIndex;

      ClassModelField(ByteReader _byteReader, int _index) {
         index = _index;
         fieldAccessFlags = _byteReader.u2();
         nameIndex = _byteReader.u2();
         descriptorIndex = _byteReader.u2();
         fieldAttributePool = new AttributePool(_byteReader);
      }

      int getAccessFlags() {
         return (fieldAccessFlags);
      }

      AttributePool getAttributePool() {
         return (fieldAttributePool);
      }

      String getDescriptor() {
         return (getDescriptorUTF8Entry().getUTF8());
      }

      int getDescriptorIndex() {
         return (descriptorIndex);
      }

      ConstantPool.UTF8Entry getDescriptorUTF8Entry() {
         return (constantPool.getUTF8Entry(descriptorIndex));
      }

      int getIndex() {
         return (index);
      }

      String getName() {
         return (getNameUTF8Entry().getUTF8());
      }

      int getNameIndex() {
         return (nameIndex);
      }

      ConstantPool.UTF8Entry getNameUTF8Entry() {
         return (constantPool.getUTF8Entry(nameIndex));
      }

      Class<?> getDeclaringClass() {
         String clazzName = getDescriptor().replaceAll("^L", "").replaceAll("/", ".").replaceAll(";$", "");
         try {
            return (Class.forName(clazzName, true, classModelLoader));
         } catch (ClassNotFoundException e) {
            System.out.println("no class found for " + clazzName);
            e.printStackTrace();
            return null;
         }
      }
   }

   class ClassModelMethod{

      private int methodAccessFlags;

      private AttributePool methodAttributePool;

      private int descriptorIndex;

      private int index;

      private int nameIndex;

      private CodeEntry codeEntry;

      ClassModelMethod(ByteReader _byteReader, int _index) {
         index = _index;
         methodAccessFlags = _byteReader.u2();
         nameIndex = _byteReader.u2();
         descriptorIndex = _byteReader.u2();
         methodAttributePool = new AttributePool(_byteReader);
         codeEntry = methodAttributePool.getCodeEntry();
      }

      int getAccessFlags() {
         return (methodAccessFlags);
      }

      public boolean isStatic() {
         return (Access.STATIC.bitIsSet(methodAccessFlags));
      }

      AttributePool getAttributePool() {
         return (methodAttributePool);
      }

      AttributePool.CodeEntry getCodeEntry() {
         return (methodAttributePool.getCodeEntry());
      }

      String getDescriptor() {
         return (getDescriptorUTF8Entry().getUTF8());
      }

      int getDescriptorIndex() {
         return (descriptorIndex);
      }

      ConstantPool.UTF8Entry getDescriptorUTF8Entry() {
         return (constantPool.getUTF8Entry(descriptorIndex));
      }

      int getIndex() {
         return (index);
      }

      String getName() {
         return (getNameUTF8Entry().getUTF8());
      }

      int getNameIndex() {
         return (nameIndex);
      }

      ConstantPool.UTF8Entry getNameUTF8Entry() {
         return (constantPool.getUTF8Entry(nameIndex));
      }

      ConstantPool getConstantPool() {
         return (constantPool);
      }

      AttributePool.LineNumberTableEntry getLineNumberTableEntry() {
         return (getAttributePool().codeEntry.codeEntryAttributePool.lineNumberTableEntry);
      }

      LocalVariableTableEntry getLocalVariableTableEntry() {
         return (getAttributePool().codeEntry.codeEntryAttributePool.localVariableTableEntry);
      }

      void setLocalVariableTableEntry(LocalVariableTableEntry _localVariableTableEntry) {
         getAttributePool().codeEntry.codeEntryAttributePool.localVariableTableEntry = _localVariableTableEntry;
      }

      LocalVariableInfo getLocalVariable(int _pc, int _index) {
         return (getLocalVariableTableEntry().getVariable(_pc, _index));
      }

      byte[] getCode() {
         return (codeEntry.getCode());
      }

      ClassModel getClassModel() {
         return (ClassModel.this);
      }

   }

   class ClassModelInterface{
      private int interfaceIndex;

      ClassModelInterface(ByteReader _byteReader) {
         interfaceIndex = _byteReader.u2();
      }

      ConstantPool.ClassEntry getClassEntry() {
         return (constantPool.getClassEntry(interfaceIndex));
      }

      int getInterfaceIndex() {
         return (interfaceIndex);
      }

   }

   private Class<?> clazz;

   /**
    * We extract the class's classloader and name and delegate to private parse method.
    * @param _class The class we wish to model
    * @throws ClassParseException
    */
   void parse(Class<?> _class) throws ClassParseException {

      clazz = _class;
      parse(_class.getClassLoader(), _class.getName());
   }

   /**
    * Populate this model by parsing a given classfile from the given classloader.
    * 
    * We create a ByteReader (wrapper around the bytes representing the classfile) and pass it to local inner classes to handle the various sections of the class file. 
    * 
    * @see ByteReader
    * @see <a href="http://java.sun.com/docs/books/jvms/second_edition/ClassFileFormat-Java5.pdf">Java 5 Class File Format</a>
    * @param _classLoader The classloader to access the classfile
    * @param _className The name of the class to load (we convert '.' to '/' and append ".class" so you don't have to).
    * @throws ClassParseException
    */
   private void parse(ClassLoader _classLoader, String _className) throws ClassParseException {
      parse(_classLoader.getResourceAsStream(_className.replace('.', '/') + ".class"));

   }

   void parse(InputStream _inputStream) throws ClassParseException {

      ByteReader byteReader = new ByteReader(_inputStream);
      magic = byteReader.u4();
      minorVersion = byteReader.u2();
      majorVersion = byteReader.u2();
      constantPool = new ConstantPool(byteReader);

      accessFlags = byteReader.u2();
      thisClassConstantPoolIndex = byteReader.u2();
      superClassConstantPoolIndex = byteReader.u2();

      int interfaceCount = byteReader.u2();
      for (int i = 0; i < interfaceCount; i++) {
         ClassModelInterface iface = new ClassModelInterface(byteReader);
         interfaces.add(iface);

      }

      int fieldCount = byteReader.u2();
      for (int i = 0; i < fieldCount; i++) {
         ClassModelField field = new ClassModelField(byteReader, i);
         fields.add(field);

      }

      int methodPoolLength = byteReader.u2();
      for (int i = 0; i < methodPoolLength; i++) {
         ClassModelMethod method = new ClassModelMethod(byteReader, i);
         methods.add(method);

      }

      attributePool = new AttributePool(byteReader);

   }

   int getMagic() {
      return (magic);
   }

   int getMajorVersion() {
      return (majorVersion);
   }

   int getMinorVersion() {
      return (minorVersion);
   }

   int getAccessFlags() {
      return (accessFlags);
   }

   ConstantPool getConstantPool() {
      return (constantPool);
   }

   int getThisClassConstantPoolIndex() {
      return (thisClassConstantPoolIndex);
   }

   int getSuperClassConstantPoolIndex() {
      return (superClassConstantPoolIndex);
   }

   AttributePool getAttributePool() {
      return (attributePool);
   }

   ClassModelField getField(String _name, String _descriptor) {
      for (ClassModelField entry : fields) {
         if (entry.getName().equals(_name) && entry.getDescriptor().equals(_descriptor)) {
            return (entry);
         }
      }
      return superClazz.getField(_name, _descriptor);
   }

   ClassModelField getField(String _name) {
      for (ClassModelField entry : fields) {
         if (entry.getName().equals(_name)) {
            return (entry);
         }
      }
      return superClazz.getField(_name);
   }

   ClassModelMethod getMethod(String _name, String _descriptor) {
      for (ClassModelMethod entry : methods) {
         if (entry.getName().equals(_name) && entry.getDescriptor().equals(_descriptor)) {
            return (entry);
         }
      }
      return superClazz != null ? superClazz.getMethod(_name, _descriptor) : (null);
   }

   List<ClassModelField> getFieldPoolEntries() {
      return (fields);
   }

   /**
    * Look up a ConstantPool MethodEntry and return the corresponding Method.  
    * 
    * @param _methodEntry The ConstantPool MethodEntry we want.
    * @param _isSpecial True if we wish to delegate to super (to support <code>super.foo()</code>)
    * 
    * @return The Method or null if we fail to locate a given method.
    */
   ClassModelMethod getMethod(MethodEntry _methodEntry, boolean _isSpecial) {
      String entryClassNameInDotForm = _methodEntry.getClassEntry().getNameUTF8Entry().getUTF8().replace('/', '.');

      // Shortcut direct calls to supers to allow "foo() { super.foo() }" type stuff to work
      if (_isSpecial && (superClazz != null) && superClazz.isSuperClass(entryClassNameInDotForm)) {
         if (logger.isLoggable(Level.FINE)) {
            logger.fine("going to look in super:" + superClazz.getClassWeAreModelling().getName() + " on behalf of "
                  + entryClassNameInDotForm);
         }
         return superClazz.getMethod(_methodEntry, false);
      }

      for (ClassModelMethod entry : methods) {
         if (entry.getName().equals(_methodEntry.getNameAndTypeEntry().getNameUTF8Entry().getUTF8())
               && entry.getDescriptor().equals(_methodEntry.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8())) {
            if (logger.isLoggable(Level.FINE)) {
               logger.fine("Found " + clazz.getName() + "." + entry.getName() + " " + entry.getDescriptor() + " for "
                     + entryClassNameInDotForm);
            }
            return (entry);
         }
      }

      return superClazz != null ? superClazz.getMethod(_methodEntry, false) : (null);
   }

   /**
    * Create a MethodModel for a given method name and signature.
    * 
    * @param _name
    * @param _signature
    * @return 
    * @throws AparapiException
    */

   MethodModel getMethodModel(String _name, String _signature) throws AparapiException {
      ClassModelMethod method = getMethod(_name, _signature);
      return new MethodModel(method);
   }

   // These fields use for accessor conversion
   private ArrayList<FieldEntry> structMembers = new ArrayList<FieldEntry>();

   private ArrayList<Long> structMemberOffsets = new ArrayList<Long>();

   private ArrayList<TypeSpec> structMemberTypes = new ArrayList<TypeSpec>();

   private int totalStructSize = 0;

   ArrayList<FieldEntry> getStructMembers() {
      return structMembers;
   }

   ArrayList<Long> getStructMemberOffsets() {
      return structMemberOffsets;
   }

   ArrayList<TypeSpec> getStructMemberTypes() {
      return structMemberTypes;
   }

   int getTotalStructSize() {
      return totalStructSize;
   }

   void setTotalStructSize(int x) {
      totalStructSize = x;
   }

   Entrypoint getEntrypoint(String _entrypointName, String _descriptor, Object _k) throws AparapiException {
      MethodModel method = getMethodModel(_entrypointName, _descriptor);
      return (new Entrypoint(this, method, _k));
   }

   Class<?> getClassWeAreModelling() {
      return clazz;
   }

   public Entrypoint getEntrypoint(String _entrypointName, Object _k) throws AparapiException {
      return (getEntrypoint(_entrypointName, "()V", _k));
   }

   public Entrypoint getEntrypoint() throws AparapiException {
      return (getEntrypoint("run", "()V", null));
   }
}
