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

import com.amd.aparapi.ClassModel.AttributePool.CodeEntry;
import com.amd.aparapi.ClassModel.ConstantPool.FieldEntry;
import com.amd.aparapi.ClassModel.ConstantPool.MethodEntry;
import com.amd.aparapi.InstructionSet.Branch;
import com.amd.aparapi.InstructionSet.TypeSpec;
import com.amd.aparapi.TypeHelper.JavaType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class represents a ClassFile (MyClass.class).
 * <p/>
 * OREF ClassModel is constructed from an instance of a <code>java.lang.Class</code>.
 * <p/>
 * If the java class mode changes we may need to modify this to accommodate.
 *
 * @author gfrost
 * @see <a href="http://java.sun.com/docs/books/jvms/second_edition/ClassFileFormat-Java5.pdf">Java 5 Class File Format</a>
 * @see <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html"> Java 7 Class File Format</a>
 */
public class ClassModel{


   private static Logger logger = Logger.getLogger(Config.getLoggerName());

   private ClassModel superClazzModel = null;

   private Class<?> clazz;

   static Map<String, ClassModel> map = new LinkedHashMap<String, ClassModel>();

   public static void flush(){
      map.clear();
   }

   public static synchronized ClassModel getClassModel(Class<?> _clazz) throws ClassParseException{
      String className = _clazz.getName();
      ClassModel classModel = map.get(_clazz.getName());
      if(classModel == null){
         classModel = new ClassModel(_clazz);
         map.put(_clazz.getName(), classModel);
      }
      return (classModel);
   }


   private ClassModel(Class<?> _clazz) throws ClassParseException{
      String name = _clazz.getName();
      int index = name.indexOf('/');
      if (index >0){
          name = name.substring(0,index);
      }
      byte[] _bytes = OpenCLJNI.getJNI().getBytes(name);
      clazz = _clazz;
      parse(new ByteArrayInputStream(_bytes));
   }

   /**
    * Determine if this is the superclass of some other named class.
    *
    * @param otherClassName The name of the class to compare against
    * @return true if 'this' a superclass of another named class
    */
   boolean isSuperClass(String otherClassName){
      if(getDotClassName().equals(otherClassName)){
         return true;
      }else if(superClazzModel != null){
         return superClazzModel.isSuperClass(otherClassName);
      }else{
         return false;
      }
   }

   /**
    * Determine if this is the superclass of some other class.
    *
    * @param _otherClassModel The classModel to compare against
    * @return true if 'this' a superclass of another class
    */
   boolean isSuperClass(ClassModel _otherClassModel){
      ClassModel s = _otherClassModel.getSuperClazzModel();
      while(s != null){
         if(getDotClassName().equals(s.getDotClassName())){
            return true;
         }
         s = s.getSuperClazzModel();
      }
      return false;
   }

   /**
    * Getter for superClazz
    *
    * @return the superClazz ClassModel
    */
   ClassModel getSuperClazzModel(){
      if(superClazzModel == null){
         if(getSuperClassConstantPoolIndex() != 0){
            try{
               superClazzModel = getClassModel(Class.forName(getSuperDotClassName()));
            }catch(ClassNotFoundException cnf){

            }catch(ClassParseException cpe){

            }
         }
      }
      return superClazzModel;
   }

   /**
    * Dont think we need this.
    *
    * @param c
    */
   @Annotations.DocMe void replaceSuperClazz(ClassModel c){
      if(this.superClazzModel != null){
         //  assert c.isSuperClass(this.getClassWeAreModelling()) == true : "not my super";
         if(this.superClazzModel.getDotClassName().equals(c.getDotClassName())){
            this.superClazzModel = c;
         }else{
            this.superClazzModel.replaceSuperClazz(c);
         }
      }
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

   enum ConstantPoolType{
      EMPTY(0, 1, "empty"), //0
      UTF8(1, 1, "utf8"), //1
      UNICODE(2, 1, "unicode"), //2
      INTEGER(3, 1, "int"), //3
      FLOAT(4, 1, "float"), //4
      LONG(5, 2, "long"), //5
      DOUBLE(6, 2, "double"), //6
      CLASS(7, 1, "class"), //7
      STRING(8, 1, "string"), //8
      FIELD(9, 1, "field"), //9
      METHOD(10, 1, "method"), //10
      INTERFACEMETHOD(11, 1, "interface_method"), //11
      NAMEANDTYPE(12, 1, "name and prefix"), //12
      UNUSED13(13, 1, "unused13"),
      UNUSED14(14, 1, "unused14"),
      METHODHANDLE(15, 1, "method_handle"), //15
      METHODTYPE(16, 1, "method_type"), //16
      UNUSED17(17, 1, "unused17"), //17
      INVOKEDYNAMIC(18, 1, "invoke_dynamic");//18

      int index;
      int slots;
      String name;
      ConstantPoolType[] types;

      ConstantPoolType(int _index, int _slots, String _name, ConstantPoolType... _types){
         index = _index;
         slots = _slots;
         name = _name;
         types = _types;
      }
   }

   ;

   enum Access{
      PUBLIC(0x00000001, "public"),
      PRIVATE(0x00000002, "private"),
      PROTECTED(0x00000004, "protected"),
      STATIC(0x00000008, "static"),
      FINAL(0x00000010, "final"),
      ACC_SYNCHRONIZED(0x00000020, "synchronized"),
      ACC_VOLATILE(0x00000040, "volatile"),
      BRIDGE(0x00000040, "bridge"),
      TRANSIENT(0x00000080, "transient"),
      VARARGS(0x00000080, "varargs"),
      NATIVE(0x00000100, "native"),
      INTERFACE(0x00000200, "interface"),
      ABSTRACT(0x00000400, "abstract"),
      SUPER(0x00000020, "super"),
      STRICT(0x00000800, "strict"),
      ANNOTATION(0x00002000, "annotation"),
      ACC_ENUM(0x00004000, "enum");
      int bits;
      String name;

      Access(int _bits, String _name){
         bits = _bits;
         name = _name;
      }

      boolean bitIsSet(int _accessFlags){
         return ((bits & _accessFlags) == bits);
      }

      String convert(int _accessFlags){
         StringBuffer stringBuffer = new StringBuffer();
         for(Access access : Access.values()){
            if(access.bitIsSet(_accessFlags)){
               stringBuffer.append(" " + access.name);
            }
         }
         return (stringBuffer.toString());
      }
   }


   class ConstantPool implements Iterable<ConstantPool.Entry>{

      private List<Entry> entries = new ArrayList<Entry>();

      abstract class Entry{
         private ConstantPoolType constantPoolType;

         private int slot;

         Entry(ByteReader _byteReader, int _slot, ConstantPoolType _constantPoolType){
            constantPoolType = _constantPoolType;
            slot = _slot;
         }

         ConstantPoolType getConstantPoolType(){
            return (constantPoolType);
         }

         int getSlot(){
            return (slot);
         }

         public boolean isEmptyEntry(){
            return (this instanceof EmptyEntry);
         }

         public EmptyEntry asEmptyEntry(){
            return ((EmptyEntry) this);
         }

         public boolean isDoubleEntry(){
            return (this instanceof DoubleEntry);
         }

         public DoubleEntry asDoubleEntry(){
            return ((DoubleEntry) this);
         }

         public boolean isLongEntry(){
            return (this instanceof LongEntry);
         }

         public LongEntry asLongEntry(){
            return ((LongEntry) this);
         }

         public boolean isClassEntry(){
            return (this instanceof ClassEntry);
         }

         public ClassEntry asClassEntry(){
            return ((ClassEntry) this);
         }

         public boolean isFloatEntry(){
            return (this instanceof FloatEntry);
         }

         public FloatEntry asFloatEntry(){
            return ((FloatEntry) this);
         }

         public boolean isIntegerEntry(){
            return (this instanceof IntegerEntry);
         }

         public IntegerEntry asIntegerEntry(){
            return ((IntegerEntry) this);
         }

         public boolean isStringEntry(){
            return (this instanceof StringEntry);
         }

         public StringEntry asStringEntry(){
            return ((StringEntry) this);
         }

         public boolean isUTF8Entry(){
            return (this instanceof UTF8Entry);
         }

         public UTF8Entry asUTF8Entry(){
            return ((UTF8Entry) this);
         }

         public boolean isNameAndTypeEntry(){
            return (this instanceof NameAndTypeEntry);
         }

         public NameAndTypeEntry asNameAndTypeEntry(){
            return ((NameAndTypeEntry) this);
         }

         public boolean isMethodEntry(){
            return (this instanceof MethodEntry);
         }

         public MethodEntry asMethodEntry(){
            return ((MethodEntry) this);
         }

         public boolean isInterfaceMethodEntry(){
            return (this instanceof InterfaceMethodEntry);
         }

         public InterfaceMethodEntry asInterfaceMethodEntry(){
            return ((InterfaceMethodEntry) this);
         }

         public boolean isFieldEntry(){
            return (this instanceof FieldEntry);
         }

         public FieldEntry asFieldEntry(){
            return ((FieldEntry) this);
         }

      }

      class ClassEntry extends Entry{

         private int nameIndex;

         ClassEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.CLASS);
            nameIndex = _byteReader.u2();
         }

         int getNameIndex(){
            return (nameIndex);
         }

         UTF8Entry getNameUTF8Entry(){
            return (ConstantPool.this.getUTF8Entry(nameIndex));
         }

         String getClassName(){
            return (getNameUTF8Entry().getUTF8());
         }

         String getDotClassName(){
            return (TypeHelper.slashClassNameToDotClassName(getNameUTF8Entry().getUTF8()));
         }

         String getMangledClassName(){
            return (TypeHelper.slashClassNameToMangledClassName(getNameUTF8Entry().getUTF8()));
         }

         JavaType getType(){
            String sig = getNameUTF8Entry().getUTF8();
            return (JavaType.getJavaType("L" + sig + ";"));
         }
      }

      class DoubleEntry extends ConstantEntry<Double>{


         DoubleEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.DOUBLE);
            value = _byteReader.d8();
         }


      }

      class EmptyEntry extends Entry{
         EmptyEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.EMPTY);
         }

      }

      class FieldEntry extends ReferenceEntry{

         FieldEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.FIELD);
         }

         final JavaType getType(){
            return (JavaType.getJavaType(getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8()));
         }


      }

      abstract class ConstantEntry<T> extends Entry{
         protected T value;

         ConstantEntry(ByteReader _byteReader, int _slot, ConstantPoolType _type){
            super(_byteReader, _slot, _type);
         }

         T getValue(){
            return value;
         }

      }

      class FloatEntry extends ConstantEntry<Float>{


         FloatEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.FLOAT);
            value = _byteReader.f4();
         }


      }

      class IntegerEntry extends ConstantEntry<Integer>{

         IntegerEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.INTEGER);
            value = _byteReader.u4();
         }


      }

      class InterfaceMethodEntry extends MethodReferenceEntry{
         InterfaceMethodEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.INTERFACEMETHOD);
         }
      }

      class LongEntry extends ConstantEntry<Long>{


         LongEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.LONG);
            value = _byteReader.u8();
         }


      }

      class MethodEntry extends MethodReferenceEntry{

         MethodEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.METHOD);
         }

         @Override
         public String toString(){
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

         NameAndTypeEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.NAMEANDTYPE);
            nameIndex = _byteReader.u2();
            descriptorIndex = _byteReader.u2();
         }

         int getDescriptorIndex(){
            return (descriptorIndex);
         }

         UTF8Entry getDescriptorUTF8Entry(){
            return (ConstantPool.this.getUTF8Entry(descriptorIndex));
         }

         int getNameIndex(){
            return (nameIndex);
         }

         UTF8Entry getNameUTF8Entry(){
            return (ConstantPool.this.getUTF8Entry(nameIndex));
         }

         public String getName(){
            return (getNameUTF8Entry().getUTF8());
         }

         public String getDescriptor(){
            return (getDescriptorUTF8Entry().getUTF8());
         }
      }

      class MethodTypeEntry extends Entry{
         private int descriptorIndex;

         MethodTypeEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.METHODTYPE);
            descriptorIndex = _byteReader.u2();
         }

         int getDescriptorIndex(){
            return (descriptorIndex);
         }

         UTF8Entry getDescriptorUTF8Entry(){
            return (ConstantPool.this.getUTF8Entry(descriptorIndex));
         }


      }

      class MethodHandleEntry extends Entry{
         // http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.4

         private int referenceKind;

         private int referenceIndex;

         MethodHandleEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.METHODHANDLE);
            referenceKind = _byteReader.u1();
            referenceIndex = _byteReader.u2();
         }

         int getReferenceIndex(){
            return (referenceIndex);
         }

         int getReferenceKind(){
            return (referenceKind);
         }

      }

      class InvokeDynamicEntry extends Entry{
         // http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.4

         private int bootstrapMethodAttrIndex;

         private int nameAndTypeIndex;

         InvokeDynamicEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.INVOKEDYNAMIC);
            bootstrapMethodAttrIndex = _byteReader.u2();
            nameAndTypeIndex = _byteReader.u2();
         }

         int getBootstrapMethodAttrIndex(){
            return (bootstrapMethodAttrIndex);
         }

         int getNameAndTypeIndex(){
            return (nameAndTypeIndex);
         }

      }

      abstract class MethodReferenceEntry extends ReferenceEntry{


         @Override
         public int hashCode(){
            NameAndTypeEntry nameAndTypeEntry = getNameAndTypeEntry();

            return ((nameAndTypeEntry.getNameIndex() * 31 + nameAndTypeEntry.getDescriptorIndex()) * 31 + getClassIndex());
         }

         @Override
         public boolean equals(Object _other){
            if(_other == null || !(_other instanceof MethodReferenceEntry)){
               return (false);
            }else{
               MethodReferenceEntry otherMethodReferenceEntry = (MethodReferenceEntry) _other;
               return (otherMethodReferenceEntry.getNameAndTypeEntry().getNameIndex() == getNameAndTypeEntry().getNameIndex()
                     && otherMethodReferenceEntry.getNameAndTypeEntry().getDescriptorIndex() == getNameAndTypeEntry()
                     .getDescriptorIndex() && otherMethodReferenceEntry.getClassIndex() == getClassIndex());
            }
         }

         MethodReferenceEntry(ByteReader byteReader, int slot, ConstantPoolType constantPoolType){
            super(byteReader, slot, constantPoolType);


         }

         int getStackProduceCount(){
            return (getArgsAndReturnType().getReturnType().isVoid() ? 0 : 1);
         }

         // TypeHelper.JavaMethodArgsAndReturnType argsAndReturnType;

         TypeHelper.JavaMethodArgsAndReturnType getArgsAndReturnType(){
            //  if(argsAndReturnType == null){
            NameAndTypeEntry nameAndTypeEntry = getNameAndTypeEntry();

            String signature = nameAndTypeEntry.getDescriptorUTF8Entry().getUTF8();// "([[IF)V" for a method that takes an int[][], float and returns void.
            return (TypeHelper.JavaMethodArgsAndReturnType.getArgsAndReturnType(signature));

         }


         int getStackConsumeCount(){
            return (getArgsAndReturnType().getArgs().length);
         }
      }

      abstract class ReferenceEntry extends Entry{
         protected int referenceClassIndex;

         protected int nameAndTypeIndex;

         // protected int argCount = -1;

         ReferenceEntry(ByteReader _byteReader, int _slot, ConstantPoolType _constantPoolType){
            super(_byteReader, _slot, _constantPoolType);
            referenceClassIndex = _byteReader.u2();
            nameAndTypeIndex = _byteReader.u2();
         }

         ClassEntry getClassEntry(){
            return (ConstantPool.this.getClassEntry(referenceClassIndex));
         }


         String getName(){
            return (getNameAndTypeEntry().getName());
         }

         int getClassIndex(){
            return (referenceClassIndex);
         }

         NameAndTypeEntry getNameAndTypeEntry(){
            return (ConstantPool.this.getNameAndTypeEntry(nameAndTypeIndex));
         }


         int getNameAndTypeIndex(){
            return (nameAndTypeIndex);
         }

         boolean same(Entry _entry){
            if(_entry instanceof ReferenceEntry){
               ReferenceEntry entry = (ReferenceEntry) _entry;
               return ((referenceClassIndex == entry.referenceClassIndex) && (nameAndTypeIndex == entry.nameAndTypeIndex));
            }
            return (false);
         }


      }

      class StringEntry extends ConstantEntry<String>{
         private int utf8Index;

         StringEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.STRING);
            utf8Index = _byteReader.u2();
         }

         int getUTF8Index(){
            return (utf8Index);
         }

         UTF8Entry getStringUTF8Entry(){
            return (ConstantPool.this.getUTF8Entry(utf8Index));
         }

         String getValue(){
            if(value == null){
               value = getStringUTF8Entry().getUTF8();
            }
            return (super.getValue());
         }


      }

      class UTF8Entry extends Entry{
         private String UTF8;

         UTF8Entry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.UTF8);
            UTF8 = _byteReader.utf8();
         }

         String getUTF8(){
            return (UTF8);
         }

      }

      ConstantPool(ByteReader _byteReader){
         int size = _byteReader.u2();
         add(new EmptyEntry(_byteReader, 0)); // slot 0

         for(int i = 1; i < size; i++){
            ConstantPoolType constantPoolType = ConstantPoolType.values()[_byteReader.u1()];

            switch(constantPoolType){
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

      ClassEntry getClassEntry(int _index){
         try{
            return ((ClassEntry) entries.get(_index));
         }catch(ClassCastException e){
            return (null);
         }
      }

      DoubleEntry getDoubleEntry(int _index){
         try{
            return ((DoubleEntry) entries.get(_index));
         }catch(ClassCastException e){
            return (null);
         }
      }

      FieldEntry getFieldEntry(int _index){
         try{
            return ((FieldEntry) entries.get(_index));
         }catch(ClassCastException e){
            return (null);
         }
      }

      FloatEntry getFloatEntry(int _index){
         try{
            return ((FloatEntry) entries.get(_index));
         }catch(ClassCastException e){
            return (null);
         }
      }

      IntegerEntry getIntegerEntry(int _index){
         try{
            return ((IntegerEntry) entries.get(_index));
         }catch(ClassCastException e){
            return (null);
         }
      }

      InterfaceMethodEntry getInterfaceMethodEntry(int _index){
         try{
            return ((InterfaceMethodEntry) entries.get(_index));
         }catch(ClassCastException e){
            return (null);
         }
      }

      LongEntry getLongEntry(int _index){
         try{
            return ((LongEntry) entries.get(_index));
         }catch(ClassCastException e){
            return (null);
         }
      }

      MethodEntry getMethodEntry(int _index){
         try{
            return ((MethodEntry) entries.get(_index));
         }catch(ClassCastException e){
            return (null);
         }
      }

      NameAndTypeEntry getNameAndTypeEntry(int _index){
         try{
            return ((NameAndTypeEntry) entries.get(_index));
         }catch(ClassCastException e){
            return (null);
         }
      }

      StringEntry getStringEntry(int _index){
         try{
            return ((StringEntry) entries.get(_index));
         }catch(ClassCastException e){
            return (null);
         }
      }

      UTF8Entry getUTF8Entry(int _index){
         try{
            return ((UTF8Entry) entries.get(_index));
         }catch(ClassCastException e){
            return (null);
         }
      }

      void add(Entry _entry){
         entries.add(_entry);

      }

      @Override
      public Iterator<Entry> iterator(){
         return (entries.iterator());
      }

      Entry get(int _index){
         return (entries.get(_index));
      }

      String getDescription(ConstantPool.Entry _entry){
         StringBuilder sb = new StringBuilder();
         if(_entry.isEmptyEntry()){
            sb.append("<empty>");
         }else if(_entry.isDoubleEntry()){
            sb.append(_entry.asDoubleEntry().getValue());
         }else if(_entry.isFloatEntry()){
            sb.append(_entry.asFloatEntry().getValue());
         }else if(_entry.isIntegerEntry()){
            sb.append(_entry.asIntegerEntry().getValue());
         }else if(_entry.isLongEntry()){
            sb.append(_entry.asLongEntry().getValue());
         }else if(_entry.isUTF8Entry()){
            sb.append(_entry.asUTF8Entry().getUTF8());
         }else if(_entry.isStringEntry()){
            sb.append(_entry.asStringEntry().getValue());
         }else if(_entry.isClassEntry()){
            sb.append(_entry.asClassEntry().getClassName());
         }else if(_entry.isNameAndTypeEntry()){
            sb.append(_entry.asNameAndTypeEntry().getName() + "." + _entry.asNameAndTypeEntry().getDescriptor());
         }else if(_entry.isMethodEntry()){
            sb.append(TypeHelper.convert(_entry.asMethodEntry().getNameAndTypeEntry().getDescriptor()));
            sb.append(_entry.asMethodEntry().getClassEntry().getClassName() + "." + _entry.asMethodEntry().getName());
         }else if(_entry.isInterfaceMethodEntry()){
            sb.append(TypeHelper.convert(_entry.asInterfaceMethodEntry().getNameAndTypeEntry().getDescriptor()));
            sb.append(_entry.asInterfaceMethodEntry().getClassEntry().getClassName() + "." + _entry.asInterfaceMethodEntry().getName());
         }else if(_entry.isFieldEntry()){
            sb.append(TypeHelper.convert(_entry.asFieldEntry().getNameAndTypeEntry().getDescriptor()));
            sb.append(_entry.asFieldEntry().getClassEntry().getClassName() + "." + _entry.asFieldEntry().getNameAndTypeEntry().getName());
         }
         return (sb.toString());
      }


      <T> T getConstantEntry(int _constantPoolIndex){
         Entry entry = get(_constantPoolIndex);
         T object = null;
         switch(entry.getConstantPoolType()){
            case FLOAT:
               object = (T) entry.asFloatEntry().getValue();
               break;
            case DOUBLE:
               object = (T) entry.asDoubleEntry().getValue();
               break;
            case INTEGER:
               object = (T) entry.asIntegerEntry().getValue();
               break;
            case LONG:
               object = (T) entry.asLongEntry().getValue();
               break;
            case STRING:
               object = (T) entry.asLongEntry().getValue();
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

            ExceptionPoolEntry(ByteReader _byteReader){
               start = _byteReader.u2();
               end = _byteReader.u2();
               handler = _byteReader.u2();
               exceptionClassIndex = _byteReader.u2();
            }

            ConstantPool.ClassEntry getClassEntry(){
               return (constantPool.getClassEntry(exceptionClassIndex));
            }

            int getClassIndex(){
               return (exceptionClassIndex);
            }

            int getEnd(){
               return (end);
            }

            int getHandler(){
               return (handler);
            }

            int getStart(){
               return (start);
            }

         }

         private List<ExceptionPoolEntry> exceptionPoolEntries = new ArrayList<ExceptionPoolEntry>();

         private AttributePool codeEntryAttributePool;

         private byte[] code;

         private int maxLocals;

         private int maxStack;

         CodeEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            maxStack = _byteReader.u2();
            maxLocals = _byteReader.u2();
            int codeLength = _byteReader.u4();
            code = _byteReader.bytes(codeLength);
            int exceptionTableLength = _byteReader.u2();
            for(int i = 0; i < exceptionTableLength; i++){
               exceptionPoolEntries.add(new ExceptionPoolEntry(_byteReader));
            }
            codeEntryAttributePool = new AttributePool(_byteReader);
         }

         @Override AttributePool getAttributePool(){
            return (codeEntryAttributePool);
         }

         LineNumberTableEntry getLineNumberTableEntry(){
            return (codeEntryAttributePool.getLineNumberTableEntry());
         }

         int getMaxLocals(){
            return (maxLocals);
         }

         int getMaxStack(){
            return (maxStack);
         }

         byte[] getCode(){
            return code;
         }

         List<ExceptionPoolEntry> getExceptionPoolEntries(){
            return exceptionPoolEntries;
         }
      }

      class ConstantValueEntry extends AttributePoolEntry{
         private int index;

         ConstantValueEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            index = _byteReader.u2();
         }

         int getIndex(){
            return (index);
         }

      }

      class DeprecatedEntry extends AttributePoolEntry{
         DeprecatedEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
         }

      }

      abstract class AttributePoolEntry{
         protected int length;

         protected int nameIndex;

         AttributePoolEntry(ByteReader _byteReader, int _nameIndex, int _length){
            nameIndex = _nameIndex;
            length = _length;
         }

         AttributePool getAttributePool(){
            return (null);
         }

         int getLength(){
            return (length);
         }

         String getName(){
            return (constantPool.getUTF8Entry(nameIndex).getUTF8());
         }

         int getNameIndex(){
            return (nameIndex);
         }

      }

      abstract class PoolEntry<T> extends AttributePoolEntry implements Iterable<T>{
         private List<T> pool = new ArrayList<T>();

         List<T> getPool(){
            return (pool);
         }

         PoolEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
         }

         @Override
         public Iterator<T> iterator(){
            return (pool.iterator());
         }

      }

      class ExceptionEntry extends PoolEntry<Integer>{

         ExceptionEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            int exceptionTableLength = _byteReader.u2();
            for(int i = 0; i < exceptionTableLength; i++){
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

            InnerClassInfo(ByteReader _byteReader){
               innerIndex = _byteReader.u2();
               outerIndex = _byteReader.u2();
               innerNameIndex = _byteReader.u2();
               innerAccess = _byteReader.u2();
            }

            int getInnerAccess(){
               return (innerAccess);
            }

            int getInnerIndex(){
               return (innerIndex);
            }

            int getInnerNameIndex(){
               return (innerNameIndex);
            }

            int getOuterIndex(){
               return (outerIndex);
            }

         }

         InnerClassesEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            int innerClassesTableLength = _byteReader.u2();
            for(int i = 0; i < innerClassesTableLength; i++){
               getPool().add(new InnerClassInfo(_byteReader));
            }
         }

      }

      class LineNumberTableEntry extends PoolEntry<LineNumberTableEntry.StartLineNumberPair>{

         class StartLineNumberPair{
            private int lineNumber;

            private int start;

            StartLineNumberPair(ByteReader _byteReader){
               start = _byteReader.u2();
               lineNumber = _byteReader.u2();
            }

            int getLineNumber(){
               return (lineNumber);
            }

            int getStart(){
               return (start);
            }

         }

         LineNumberTableEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            int lineNumberTableLength = _byteReader.u2();
            for(int i = 0; i < lineNumberTableLength; i++){
               getPool().add(new StartLineNumberPair(_byteReader));
            }
         }

         int getSourceLineNumber(int _start, boolean _exact){
            Iterator<StartLineNumberPair> i = getPool().iterator();
            if(i.hasNext()){
               StartLineNumberPair from = i.next();
               while(i.hasNext()){
                  StartLineNumberPair to = i.next();
                  if(_exact){
                     if(_start == from.getStart()){
                        return (from.getLineNumber());
                     }
                  }else if(_start >= from.getStart() && _start < to.getStart()){
                     return (from.getLineNumber());
                  }
                  from = to;
               }
               if(_exact){
                  if(_start == from.getStart()){
                     return (from.getLineNumber());
                  }
               }else if(_start >= from.getStart()){
                  return (from.getLineNumber());
               }
            }
            return (-1);
         }

      }

      class EnclosingMethodEntry extends AttributePoolEntry{

         EnclosingMethodEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            enclosingClassIndex = _byteReader.u2();
            enclosingMethodIndex = _byteReader.u2();
         }

         private int enclosingClassIndex;

         int getClassIndex(){
            return (enclosingClassIndex);
         }

         private int enclosingMethodIndex;

         int getMethodIndex(){
            return (enclosingMethodIndex);
         }

      }

      class SignatureEntry extends AttributePoolEntry{

         SignatureEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            signatureIndex = _byteReader.u2();
         }

         private int signatureIndex;

         int getSignatureIndex(){
            return (signatureIndex);
         }

      }

      public class LocalVariableTableEntry implements Iterable<LocalVariableTableEntry.LocalVariableInfo>{

         class LocalVariableInfo{

            int startPc = 0;

            int endPc = 0;

            String name = null;

            final String descriptor;

            final TypeSpec typeSpec;

            int slot;

            LocalVariableInfo(InstructionSet.StoreSpec _storeSpec, int _slot, int _startPc){
               slot = _slot;
               startPc = _startPc;
               name = _storeSpec.toString().toLowerCase() + "_" + _slot;
               descriptor = _storeSpec.toString();
               typeSpec = _storeSpec.getTypeSpec();
            }

            public boolean isArg(){
               return (this instanceof ArgLocalVariableInfo);
            }

            LocalVariableInfo(){
               name = "NONE";
               descriptor = null;
               typeSpec = TypeSpec.NONE;
            }

            public boolean equals(Object object){
               return (object instanceof LocalVariableInfo && ((object == this) || ((LocalVariableInfo) object).name.equals(name)));
            }

            public String toString(){
               return (name + "[" + startPc + "-" + endPc + "]");
            }

            public int getStart(){
               return startPc;
            }


            public int getEnd(){
               return endPc;
            }

            public int getLength(){
               return endPc - startPc;
            }

            public String getVariableName(){
               return (name);
            }

            public String getVariableDescriptor(){
               return (descriptor);
            }

            public int getSlot(){
               return (slot);
            }

            public ArgLocalVariableInfo asArgLocalVariableInfo(){
               return ((ArgLocalVariableInfo) this);
            }

            public TypeSpec getTypeSpec(){
               return (typeSpec);
            }
         }

         class ArgLocalVariableInfo extends LocalVariableInfo{
            TypeHelper.JavaType realType;

            ArgLocalVariableInfo(InstructionSet.StoreSpec _storeSpec, int _slot, int _startPc, JavaType _realType){
               super(_storeSpec, _slot, _startPc);
               realType = _realType;
            }

            public JavaType getRealType(){
               return (realType);
            }

            public boolean isArray(){
               return realType.isArray();
            }

         }

         List<LocalVariableInfo> list = new ArrayList<LocalVariableInfo>();
         List<ArgLocalVariableInfo> argsList = new ArrayList<ArgLocalVariableInfo>();

         public LocalVariableTableEntry(Map<Integer, Instruction> _pcMap, ClassModelMethod _method){
            int numberOfSlots = _method.getCodeEntry().getMaxLocals();

            // MethodDescription description = TypeHelper.getMethodDescription(_method.getDescriptor());

            TypeHelper.JavaMethodArgsAndReturnType argsAndReturnType = _method.getArgsAndReturnType();
            TypeHelper.JavaMethodArg[] args = argsAndReturnType.getArgs();

            int thisOffset = _method.isStatic() ? 0 : 1;

            LocalVariableInfo[] vars = new LocalVariableInfo[numberOfSlots + thisOffset];
            //   InstructionSet.StoreSpec[] argsAsStoreSpecs = new InstructionSet.StoreSpec[args.length + thisOffset];
            if(_method.isVirtual()){
               //argsAsStoreSpecs[0] = InstructionSet.StoreSpec.OREF;
               ArgLocalVariableInfo arg = new ArgLocalVariableInfo(InstructionSet.StoreSpec.OREF, 0, 0, ClassModel.this.getClassType());
               vars[0] = arg;
               list.add(vars[0]);
               argsList.add(arg);

            }

            int currSlotIndex = thisOffset;
            for(int i = 0; i < args.length; i++){
               InstructionSet.StoreSpec storeSpec = InstructionSet.StoreSpec.valueOf(args[i].getJavaType().getPrimitiveType());

               ArgLocalVariableInfo arg = new ArgLocalVariableInfo(storeSpec, currSlotIndex, 0, args[i].getJavaType());
               vars[i + thisOffset] = arg;
               currSlotIndex += storeSpec.getTypeSpec().getPrimitiveType().getJavaSlots(); // 1 for most 2 for Long/Double
               list.add(arg);
               argsList.add(arg);
            }
            for(int i = args.length + thisOffset; i < numberOfSlots + thisOffset; i++){
               vars[i] = new LocalVariableInfo();
            }

            int pc = 0;
            Instruction instruction = null;
            for(Instruction i : _pcMap.values()){
               instruction = i;
               pc = i.getThisPC();
               InstructionSet.StoreSpec storeSpec = i.getByteCode().getStore();
               if(storeSpec != InstructionSet.StoreSpec.NONE){
                  int slotIndex = ((InstructionSet.LocalVariableTableIndexAccessor) i).getLocalVariableTableIndex();
                  LocalVariableInfo prevVar = vars[slotIndex];
                  LocalVariableInfo var = new LocalVariableInfo(storeSpec, slotIndex, pc + i.getLength()); // will get collected pretty soon if this is not the same as the previous in this slot
                  if(!prevVar.equals(var)){
                     prevVar.endPc = pc;
                     vars[slotIndex] = var;
                     list.add(vars[slotIndex]);
                  }
               }else if(i.isForwardBranchTarget()){  // Is there an earlier branch branching here   ?
                  // If so we need to descope all vars declared between the brancher and here
                  // this stops
                  // if (){
                  //    int var1=0;
                  // }
                  // int var2=0;
                  // Turning into OpenCL
                  // if (){
                  //    int var=0;
                  // }
                  // var=0; // <- there is no var in scope for this

                  for(Branch b : instruction.getForwardBranches()){
                     for(int slot = 0; slot < numberOfSlots + thisOffset; slot++){
                        if(vars[slot].endPc == 0 && b.getThisPC() < vars[slot].startPc){
                           vars[slot].endPc = pc;
                           // System.out.println("var "+vars[slot].getVariableName()+" is descoped!");
                           vars[slot] = new LocalVariableInfo();
                        }
                     }
                  }
               }
            }
            for(int i = 0; i < numberOfSlots + thisOffset; i++){
               vars[i].endPc = pc + instruction.getLength();
            }
            Collections.sort(list, new Comparator<LocalVariableInfo>(){
               @Override
               public int compare(LocalVariableInfo o1, LocalVariableInfo o2){
                  return o1.getStart() - o2.getStart();
               }
            });


         }

         public LocalVariableInfo getVariable(int _pc, int _slot){
            LocalVariableInfo returnValue = null;
            //  System.out.println("pc = " + _pc + " index = " + _index);
            for(LocalVariableInfo localVariableInfo : list){
               // System.out.println("   start=" + localVariableInfo.getStart() + " length=" + localVariableInfo.getLength()
               // + " varidx=" + localVariableInfo.getVariableIndex());
               if(_pc >= localVariableInfo.getStart() - 1 && _pc <= (localVariableInfo.getStart() + localVariableInfo.getLength())
                     && _slot == localVariableInfo.getSlot()){
                  returnValue = (LocalVariableInfo) localVariableInfo;
                  break;
               }
            }
            return (returnValue);
         }

         String getVariableName(int _pc, int _index){
            String returnValue = "unknown";
            LocalVariableInfo localVariableInfo = (LocalVariableInfo) getVariable(_pc, _index);
            if(localVariableInfo != null){
               returnValue = ((LocalVariableInfo) localVariableInfo).name;
            }
            // System.out.println("returning " + returnValue);
            return (returnValue);
         }

         @Override
         public Iterator<LocalVariableInfo> iterator(){
            return list.iterator();
         }

         public List<ArgLocalVariableInfo> getArgs(){
            return (argsList);
         }

      }
        /*

      class RealLocalVariableTableEntry extends PoolEntry<LocalVariableInfo> implements
            LocalVariableTableEntry<RealLocalVariableTableEntry, RealLocalVariableTableEntry.Var>{

         class Var implements LocalVariableInfo{
            private int descriptorIndex;

            private int usageLength;

            private int variableNameIndex;

            private int startPc;

            private int slot;

            private JavaType type;

            String variableDescriptor;

            String variableName;

            Var(ByteReader _byteReader){
               startPc = _byteReader.u2();
               usageLength = _byteReader.u2();
               variableNameIndex = _byteReader.u2();
               descriptorIndex = _byteReader.u2();
               variableName = constantPool.getUTF8Entry(variableNameIndex).getUTF8();
               variableDescriptor = constantPool.getUTF8Entry(descriptorIndex).getUTF8();
               type = TypeHelper.getJavaType(variableDescriptor);

               slot = _byteReader.u2();
            }




            int getDescriptorIndex(){
               return (descriptorIndex);
            }

            public int getLength(){
               return (usageLength);
            }

            int getNameIndex(){
               return (variableNameIndex);
            }
            @Override
            public boolean isArg(){
               return (startPc==0);
            }
            @Override
            public int getStart(){
               return (startPc);
            }

            @Override
            public int getSlot(){
               return (slot);
            }

            @Override
            public String getVariableName(){
               return (variableName);
            }

            @Override
            public String getVariableDescriptor(){
               return (variableDescriptor);
            }

            @Override
            public TypeHelper.JavaType getType(){
               return (type);
            }

            @Override
            public TypeHelper.JavaType getRealType(){
               return (type);
            }

            @Override
            public int getEnd(){
               return (startPc + usageLength);
            }

            @Override
            public boolean isArray(){
               return (getVariableDescriptor().startsWith("["));
            }

            @Override
            public boolean isObject(){
               return (getVariableDescriptor().startsWith("L"));
            }
         }

         RealLocalVariableTableEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            int localVariableTableLength = _byteReader.u2();
            for(int i = 0; i < localVariableTableLength; i++){
               getPool().add(new Var(_byteReader));
            }


         }

         public Var getVariable(int _pc, int _slot){
            Var returnValue = null;
            // System.out.println("pc = " + _pc + " index = " + _index);
            for(LocalVariableInfo localVariableInfo : getPool()){
               // System.out.println("   start=" + localVariableInfo.getStart() + " length=" + localVariableInfo.getLength()
               // + " varidx=" + localVariableInfo.getVariableIndex());
               if(_pc >= localVariableInfo.getStart() - 1 && _pc <= (localVariableInfo.getStart() + localVariableInfo.getLength())
                     && _slot == localVariableInfo.getSlot()){
                  returnValue = (Var) localVariableInfo;
                  break;
               }
            }
            // System.out.println("returning " + returnValue);
            return (returnValue);
         }

         String getVariableName(int _pc, int _index){
            String returnValue = "unknown";
            Var localVariableInfo = getVariable(_pc, _index);
            if(localVariableInfo != null){
               returnValue = TypeHelper.convert(constantPool.getUTF8Entry(localVariableInfo.getDescriptorIndex()).getUTF8(), constantPool
                     .getUTF8Entry(localVariableInfo.getNameIndex()).getUTF8());
            }
            // System.out.println("returning " + returnValue);
            return (returnValue);
         }

      }
      */

      class BootstrapMethodsEntry extends AttributePoolEntry{
         // http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.21
         class BootstrapMethod{
            class BootstrapArgument{
               public BootstrapArgument(ByteReader _byteReader){
                  argument = _byteReader.u2();
               }

               int argument;// u2;
            }

            public BootstrapMethod(ByteReader _byteReader){
               bootstrapMethodRef = _byteReader.u2();
               numBootstrapArguments = _byteReader.u2();
               bootstrapArguments = new BootstrapArgument[numBootstrapArguments];
               for(int i = 0; i < numBootstrapArguments; i++){
                  bootstrapArguments[i] = new BootstrapArgument(_byteReader);
               }
            }

            int bootstrapMethodRef; //u2

            int numBootstrapArguments; //u2

            BootstrapArgument bootstrapArguments[];
         }

         BootstrapMethodsEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            numBootstrapMethods = _byteReader.u2();
            bootstrapMethods = new BootstrapMethod[numBootstrapMethods];
            for(int i = 0; i < numBootstrapMethods; i++){
               bootstrapMethods[i] = new BootstrapMethod(_byteReader);
            }
         }

         private int numBootstrapMethods;

         BootstrapMethod bootstrapMethods[];

         int getNumBootstrapMethods(){
            return (numBootstrapMethods);
         }

      }

      class OtherEntry extends AttributePoolEntry{
         private byte[] bytes;

         OtherEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            bytes = _byteReader.bytes(_length);
         }

         byte[] getBytes(){
            return (bytes);
         }

         @Override
         public String toString(){
            return (new String(bytes));
         }

      }

      class StackMapTableEntry extends AttributePoolEntry{
         private byte[] bytes;

         StackMapTableEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            bytes = _byteReader.bytes(_length);
         }

         byte[] getBytes(){
            return (bytes);
         }

         @Override
         public String toString(){
            return (new String(bytes));
         }
      }

      class LocalVariableTypeTableEntry extends AttributePoolEntry{
         private byte[] bytes;

         LocalVariableTypeTableEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            bytes = _byteReader.bytes(_length);
         }

         byte[] getBytes(){
            return (bytes);
         }

         @Override
         public String toString(){
            return (new String(bytes));
         }
      }

      class SourceFileEntry extends AttributePoolEntry{
         private int sourceFileIndex;

         SourceFileEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            sourceFileIndex = _byteReader.u2();
         }

         int getSourceFileIndex(){
            return (sourceFileIndex);
         }

         String getSourceFileName(){
            return (constantPool.getUTF8Entry(sourceFileIndex).getUTF8());
         }

      }

      class SyntheticEntry extends AttributePoolEntry{
         SyntheticEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
         }

      }

      class RuntimeAnnotationsEntry extends PoolEntry<RuntimeAnnotationsEntry.AnnotationInfo>{

         class AnnotationInfo{
            private int typeIndex;

            private int elementValuePairCount;

            class ElementValuePair{
               class Value{
                  Value(int _tag){
                     tag = _tag;
                  }

                  int tag;

               }

               class PrimitiveValue extends Value{
                  private int typeNameIndex;

                  //  private int constNameIndex;

                  PrimitiveValue(int _tag, ByteReader _byteReader){
                     super(_tag);
                     typeNameIndex = _byteReader.u2();
                     // constNameIndex = _byteReader.u2();   GRF?
                  }

                  //   int getConstNameIndex(){
                  //    return (constNameIndex);
                  //   }

                  int getTypeNameIndex(){
                     return (typeNameIndex);
                  }
               }

               class EnumValue extends Value{
                  EnumValue(int _tag, ByteReader _byteReader){
                     super(_tag);
                  }

               }

               class ArrayValue extends Value{
                  ArrayValue(int _tag, ByteReader _byteReader){
                     super(_tag);
                  }

               }

               class ClassValue extends Value{
                  ClassValue(int _tag, ByteReader _byteReader){
                     super(_tag);
                  }

               }

               class AnnotationValue extends Value{
                  AnnotationValue(int _tag, ByteReader _byteReader){
                     super(_tag);
                  }

               }

               @SuppressWarnings("unused")
               private int elementNameIndex;

               @SuppressWarnings("unused")
               private Value value;

               ElementValuePair(ByteReader _byteReader){
                  elementNameIndex = _byteReader.u2();
                  int tag = _byteReader.u1();

                  switch(tag){
                     case TypeHelper.BYTE:
                     case TypeHelper.CHAR:
                     case TypeHelper.INT:
                     case TypeHelper.LONG:
                     case TypeHelper.DOUBLE:
                     case TypeHelper.FLOAT:
                     case TypeHelper.SHORT:
                     case TypeHelper.BOOLEAN:
                     case TypeHelper.STRING: // special for String
                        value = new PrimitiveValue(tag, _byteReader);
                        break;
                     case TypeHelper.ENUM: // special for Enum
                        value = new EnumValue(tag, _byteReader);
                        break;
                     case TypeHelper.CLASS: // special for class
                        value = new ClassValue(tag, _byteReader);
                        break;
                     case TypeHelper.ANNOTATION: // special for Annotation
                        value = new AnnotationValue(tag, _byteReader);
                        break;
                     case TypeHelper.ARRAY: // special for array
                        value = new ArrayValue(tag, _byteReader);
                        break;
                  }

               }

            }

            ElementValuePair[] elementValuePairs;

            AnnotationInfo(ByteReader _byteReader){
               typeIndex = _byteReader.u2();
               elementValuePairCount = _byteReader.u2();
               elementValuePairs = new ElementValuePair[elementValuePairCount];
               for(int i = 0; i < elementValuePairCount; i++){
                  elementValuePairs[i] = new ElementValuePair(_byteReader);
               }
            }

            int getTypeIndex(){
               return (typeIndex);
            }

            String getTypeDescriptor(){
               return (constantPool.getUTF8Entry(typeIndex).getUTF8());
            }
         }

         RuntimeAnnotationsEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            int localVariableTableLength = _byteReader.u2();
            for(int i = 0; i < localVariableTableLength; i++){
               getPool().add(new AnnotationInfo(_byteReader));
            }
         }

      }

      private CodeEntry codeEntry = null;

      private EnclosingMethodEntry enclosingMethodEntry = null;

      private DeprecatedEntry deprecatedEntry = null;

      private ExceptionEntry exceptionEntry = null;

      private LineNumberTableEntry lineNumberTableEntry = null;

      // private RealLocalVariableTableEntry realLocalVariableTableEntry = null;

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

      AttributePool(ByteReader _byteReader){

         int attributeCount = _byteReader.u2();
         AttributePoolEntry entry = null;
         for(int i = 0; i < attributeCount; i++){
            int attributeNameIndex = _byteReader.u2();
            int length = _byteReader.u4();
            ConstantPool.UTF8Entry utf8Entry = constantPool.getUTF8Entry(attributeNameIndex);
            if(utf8Entry == null){
               logger.warning("Found unexpected Attribute (name = NULL) attributeNameIndex = " + attributeNameIndex);
               entry = new OtherEntry(_byteReader, attributeNameIndex, length);
               attributePoolEntries.add(entry);
            }else{
               String attributeName = utf8Entry.getUTF8();
               // System.out.println("Got " + attributeName);
               if(attributeName.equals(LOCALVARIABLETABLE_TAG)){
                  entry = new OtherEntry(_byteReader, attributeNameIndex, length);
                  // realLocalVariableTableEntry = new RealLocalVariableTableEntry(_byteReader, attributeNameIndex, length);
                  // entry = (RealLocalVariableTableEntry) realLocalVariableTableEntry;
               }else if(attributeName.equals(CONSTANTVALUE_TAG)){
                  entry = new ConstantValueEntry(_byteReader, attributeNameIndex, length);
               }else if(attributeName.equals(LINENUMBERTABLE_TAG)){
                  lineNumberTableEntry = new LineNumberTableEntry(_byteReader, attributeNameIndex, length);
                  entry = lineNumberTableEntry;
               }else if(attributeName.equals(SOURCEFILE_TAG)){
                  sourceFileEntry = new SourceFileEntry(_byteReader, attributeNameIndex, length);
                  entry = sourceFileEntry;
               }else if(attributeName.equals(SYNTHETIC_TAG)){
                  syntheticEntry = new SyntheticEntry(_byteReader, attributeNameIndex, length);
                  entry = syntheticEntry;
               }else if(attributeName.equals(EXCEPTIONS_TAG)){
                  exceptionEntry = new ExceptionEntry(_byteReader, attributeNameIndex, length);
                  entry = exceptionEntry;
               }else if(attributeName.equals(INNERCLASSES_TAG)){
                  entry = new InnerClassesEntry(_byteReader, attributeNameIndex, length);
               }else if(attributeName.equals(DEPRECATED_TAG)){
                  deprecatedEntry = new DeprecatedEntry(_byteReader, attributeNameIndex, length);
                  entry = deprecatedEntry;
               }else if(attributeName.equals(CODE_TAG)){
                  codeEntry = new CodeEntry(_byteReader, attributeNameIndex, length);
                  entry = codeEntry;
               }else if(attributeName.equals(ENCLOSINGMETHOD_TAG)){
                  enclosingMethodEntry = new EnclosingMethodEntry(_byteReader, attributeNameIndex, length);
                  entry = enclosingMethodEntry;
               }else if(attributeName.equals(SIGNATURE_TAG)){
                  entry = new SignatureEntry(_byteReader, attributeNameIndex, length);
               }else if(attributeName.equals(RUNTIMEINVISIBLEANNOTATIONS_TAG)){
                  runtimeInvisibleAnnotationsEntry = new RuntimeAnnotationsEntry(_byteReader, attributeNameIndex, length);
                  entry = runtimeInvisibleAnnotationsEntry;
               }else if(attributeName.equals(RUNTIMEVISIBLEANNOTATIONS_TAG)){
                  runtimeVisibleAnnotationsEntry = new RuntimeAnnotationsEntry(_byteReader, attributeNameIndex, length);
                  entry = runtimeVisibleAnnotationsEntry;
               }else if(attributeName.equals(BOOTSTRAPMETHODS_TAG)){
                  bootstrapMethodsEntry = new BootstrapMethodsEntry(_byteReader, attributeNameIndex, length);
                  entry = bootstrapMethodsEntry;
                  // http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.21
               }else if(attributeName.equals(STACKMAPTABLE_TAG)){
                  // http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.4
                  entry = new StackMapTableEntry(_byteReader, attributeNameIndex, length);
               }else if(attributeName.equals(LOCALVARIABLETYPETABLE_TAG)){
                  // http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.14
                  entry = new LocalVariableTypeTableEntry(_byteReader, attributeNameIndex, length);
               }else{
                  logger.warning("Found unexpected Attribute (name = " + attributeName + ")");
                  entry = new OtherEntry(_byteReader, attributeNameIndex, length);
                  attributePoolEntries.add(entry);
               }
            }

         }

      }

      CodeEntry getCodeEntry(){
         return (codeEntry);
      }

      DeprecatedEntry getDeprecatedEntry(){
         return (deprecatedEntry);
      }

      ExceptionEntry getExceptionEntry(){
         return (exceptionEntry);
      }

      LineNumberTableEntry getLineNumberTableEntry(){
         return (lineNumberTableEntry);
      }

      //  LocalVariableTableEntry getRealLocalVariableTableEntry(){
      //     return (realLocalVariableTableEntry);
      //  }
      LocalVariableTableEntry getLocalVariableTableEntry(){
         return (localVariableTableEntry);
      }

      SourceFileEntry getSourceFileEntry(){
         return (sourceFileEntry);
      }

      SyntheticEntry getSyntheticEntry(){
         return (syntheticEntry);
      }

      RuntimeAnnotationsEntry getRuntimeInvisibleAnnotationsEntry(){
         return (runtimeInvisibleAnnotationsEntry);
      }

      RuntimeAnnotationsEntry getRuntimeVisibleAnnotationsEntry(){
         return (runtimeVisibleAnnotationsEntry);
      }

      RuntimeAnnotationsEntry getBootstrap(){
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

      ClassModelField(ByteReader _byteReader, int _index){
         index = _index;
         fieldAccessFlags = _byteReader.u2();
         nameIndex = _byteReader.u2();
         descriptorIndex = _byteReader.u2();
         fieldAttributePool = new AttributePool(_byteReader);
      }

      int getAccessFlags(){
         return (fieldAccessFlags);
      }

      AttributePool getAttributePool(){
         return (fieldAttributePool);
      }

      String getDescriptor(){
         return (getDescriptorUTF8Entry().getUTF8());
      }

      int getDescriptorIndex(){
         return (descriptorIndex);
      }

      ConstantPool.UTF8Entry getDescriptorUTF8Entry(){
         return (constantPool.getUTF8Entry(descriptorIndex));
      }

      int getIndex(){
         return (index);
      }

      String getName(){
         return (getNameUTF8Entry().getUTF8());
      }

      int getNameIndex(){
         return (nameIndex);
      }

      ConstantPool.UTF8Entry getNameUTF8Entry(){
         return (constantPool.getUTF8Entry(nameIndex));
      }

      TypeHelper.JavaType type;

      JavaType getType(){
         if(type == null){
            type = TypeHelper.JavaType.getJavaType(getDescriptor());
         }
         return (type);
      }


   }

   public class ClassModelMethod{

      private int methodAccessFlags;

      private AttributePool methodAttributePool;

      private int descriptorIndex;

      private int index;

      private int nameIndex;

      private CodeEntry codeEntry;

      ClassModelMethod(ByteReader _byteReader, int _index){
         index = _index;
         methodAccessFlags = _byteReader.u2();
         nameIndex = _byteReader.u2();
         descriptorIndex = _byteReader.u2();
         methodAttributePool = new AttributePool(_byteReader);
         codeEntry = methodAttributePool.getCodeEntry();
      }

      int getAccessFlags(){
         return (methodAccessFlags);
      }

      public boolean isStatic(){
         return (Access.STATIC.bitIsSet(methodAccessFlags));
      }

       public boolean isNonStatic(){
           return (!isStatic());
       }

      public boolean isVirtual(){
         return (!Access.STATIC.bitIsSet(methodAccessFlags));
      }

      AttributePool getAttributePool(){
         return (methodAttributePool);
      }

      AttributePool.CodeEntry getCodeEntry(){
         return (methodAttributePool.getCodeEntry());
      }

      String getDescriptor(){
         return (getDescriptorUTF8Entry().getUTF8());
      }

      int getDescriptorIndex(){
         return (descriptorIndex);
      }

      ConstantPool.UTF8Entry getDescriptorUTF8Entry(){
         return (constantPool.getUTF8Entry(descriptorIndex));
      }

      TypeHelper.JavaMethodArgsAndReturnType argsAndReturnType;

      TypeHelper.JavaMethodArgsAndReturnType getArgsAndReturnType(){
         if(argsAndReturnType == null){
            argsAndReturnType = TypeHelper.JavaMethodArgsAndReturnType.getArgsAndReturnType(getDescriptor());
         }
         return (argsAndReturnType);
      }

      int getIndex(){
         return (index);
      }

      String getName(){
         return (getNameUTF8Entry().getUTF8());
      }

      int getNameIndex(){
         return (nameIndex);
      }

      ConstantPool.UTF8Entry getNameUTF8Entry(){
         return (constantPool.getUTF8Entry(nameIndex));
      }

      ConstantPool getConstantPool(){
         return (constantPool);
      }

      AttributePool.LineNumberTableEntry getLineNumberTableEntry(){
         return (getAttributePool().codeEntry.codeEntryAttributePool.lineNumberTableEntry);
      }
      // AttributePool.RealLocalVariableTableEntry getRealLocalVariableTableEntry(){
      //       return (getAttributePool().codeEntry.codeEntryAttributePool.realLocalVariableTableEntry);

      // }
      AttributePool.LocalVariableTableEntry getLocalVariableTableEntry(){
         return (getAttributePool().codeEntry.codeEntryAttributePool.localVariableTableEntry);

      }
      //  LocalVariableTableEntry getPreferredLocalVariableTableEntry(){
      //    if (Config.enableUseRealLocalVariableTableIfAvailable){
      //        return ((LocalVariableTableEntry)getRealLocalVariableTableEntry());
      //     }else{
      //       return ((LocalVariableTableEntry)getLocalVariableTableEntry());
      //     }
      // }

      void setLocalVariableTableEntry(AttributePool.LocalVariableTableEntry _localVariableTableEntry){
         getAttributePool().codeEntry.codeEntryAttributePool.localVariableTableEntry = _localVariableTableEntry;
      }

      AttributePool.LocalVariableTableEntry.LocalVariableInfo getLocalVariable(int _pc, int _index){
         return (getLocalVariableTableEntry().getVariable(_pc, _index));
      }

      byte[] getCode(){
         return (codeEntry.getCode());
      }

      ClassModel getClassModel(){
         return (ClassModel.this);
      }

      public String toString(){
         return getClassModel().getDotClassName() + "." + getName() + " " + getDescriptor();
      }

      Map<Integer, Instruction> pcMap;
      Set<InstructionSet.Branch> branches;

      Set<InstructionSet.Branch> getBranches(){
         getInstructionMap(); // remember it is lazy
         return (branches);
      }

      Set<Instruction> branchTargets;

      Set<Instruction> getBranchTargets(){
         getInstructionMap(); // remember it is lazy
         return (branchTargets);
      }

      Set<InstructionSet.MethodCall> methodCalls;

      public Set<InstructionSet.MethodCall> getMethodCalls(){
         getInstructionMap(); // remember it is lazy
         return (methodCalls);
      }

      Set<InstructionSet.FieldReference> accessedFields;

      public Set<InstructionSet.FieldReference> getFieldAccesses(){
         getInstructionMap(); // remember it is lazy

         return (accessedFields);
      }

      Set<InstructionSet.LocalVariableTableIndexAccessor> accessedLocalVariables;

      public Set<InstructionSet.LocalVariableTableIndexAccessor> getLocalVariableAccesses(){
         getInstructionMap(); // remember it is lazy

         return (accessedLocalVariables);
      }

      class ConsumedInstructionTypeStack{

         Instruction.InstructionType[] instructionTypes;
         int index = 0;

         ConsumedInstructionTypeStack(int _length){
            instructionTypes = new Instruction.InstructionType[_length];


         }

         Instruction.InstructionType pop(){
            Instruction.InstructionType retValue = null;
            if(index > 0){
               retValue = instructionTypes[--index];
               instructionTypes[index] = null;
            }
            return (retValue);
         }

         Instruction.InstructionType peek(){
            Instruction.InstructionType retValue = null;
            if(index > 0){
               retValue = instructionTypes[index - 1];
            }
            return (retValue);

         }

         void push(Instruction _i, PrimitiveType _primitiveType){
            if((index + 1) < instructionTypes.length){
               instructionTypes[index++] = new Instruction.InstructionType(_i, _primitiveType);
            }
         }

         Instruction.InstructionType get(int _index){
            Instruction.InstructionType retValue = null;
            if(_index < instructionTypes.length){
               retValue = instructionTypes[_index];
            }
            return (retValue);

         }

         int getSize(){
            return (instructionTypes.length);
         }

         int getIndex(){
            return (index);
         }


      }


      /**
       * Create a linked list of instructions (from pcHead to pcTail).
       * <p/>
       * Returns a map of int (pc) to Instruction which to allow us to quickly get from a bytecode offset to the appropriate instruction.
       * <p/>
       * Note that not all int values from 0 to code.length values will map to a valid instruction, if pcMap.get(n) == null then this implies
       * that 'n' is not the start of an instruction
       * <p/>
       * So either pcMap.get(i)== null or pcMap.get(i).getThisPC()==i
       *
       * @return Map<Integer, Instruction> the returned pc to Instruction map
       */
      Map<Integer, Instruction> getInstructionMap(){
         // We build this lazily
         if(pcMap == null){


            Instruction pcHead = null;
            Instruction pcTail = null;
            pcMap = new LinkedHashMap<Integer, Instruction>();
            branches = new LinkedHashSet<InstructionSet.Branch>();
            branchTargets = new LinkedHashSet<Instruction>();
            methodCalls = new LinkedHashSet<InstructionSet.MethodCall>();
            accessedFields = new LinkedHashSet<InstructionSet.FieldReference>();
            accessedLocalVariables = new LinkedHashSet<InstructionSet.LocalVariableTableIndexAccessor>();
            byte[] code = getCode();

            // We create a byteReader for reading the bytes from the code array
            ByteReader codeReader = new ByteReader(code);
            while(codeReader.hasMore()){
               // Create an instruction from code reader's current position
               int pc = codeReader.getOffset();
               Instruction instruction = InstructionSet.ByteCode.create(this, codeReader);


               if(instruction instanceof InstructionSet.Branch){
                  branches.add(instruction.asBranch());
               }
               if(instruction.isMethodCall()){
                  methodCalls.add(instruction.asMethodCall());
               }
               if(instruction.isFieldAccessor()){
                  accessedFields.add(instruction.asFieldAccessor());
               }

               if(instruction.isLocalVariableAccessor()){
                  accessedLocalVariables.add(instruction.asLocalVariableAccessor());
               }
               pcMap.put(pc, instruction);

               // list maintenance, make this the pcHead if pcHead is null
               if(pcHead == null){
                  pcHead = instruction;
               }

               // extend the list of instructions here we make the new instruction point to previous tail
               instruction.setPrevPC(pcTail);
               // if tail exists (not the first instruction in the list) link it to the new instruction
               if(pcTail != null){
                  pcTail.setNextPC(instruction);
               }
               // now move the tail along
               pcTail = instruction;

            }

            // Here we connect the branch nodes to the instruction that they branch to.
            //
            // Each branch node contains a 'target' field intended to reference the node that the branch targets. Each instruction also contain four separate lists of branch nodes that reference it.
            // These lists hold forwardConditional, forwardUnconditional, reverseConditional and reverseUnconditional branches that reference it.
            //
            // So assuming that we had a branch node at pc offset 100 which represented 'goto 200'.
            //
            // Following this loop the branch node at pc offset 100 will have a 'target' field which actually references the instruction at pc offset 200, and the instruction at pc offset 200 will
            // have the branch node (at 100) added to it's forwardUnconditional list.
            //
            // @see InstructionSet.Branch#getTarget()

            for(InstructionSet.Branch branch : branches){
               Instruction targetInstruction = pcMap.get(branch.getAbsolute());
               branchTargets.add(targetInstruction);
               branch.setTarget(targetInstruction);
            }

            // We need to remove some javac optimizations
            // Javac optimizes some branches to avoid goto->goto, branch->goto etc.


            for(InstructionSet.Branch branch : branches){
               if(branch.isReverse()){
                  Instruction target = branch.getTarget();
                  LinkedList<InstructionSet.Branch> list = target.getReverseUnconditionalBranches();
                  if((list != null) && (list.size() > 0) && (list.get(list.size() - 1) != branch)){
                     InstructionSet.Branch unconditional = list.get(list.size() - 1).asBranch();
                     branch.retarget(unconditional);

                  }
               }
            }

            int block = 0;
            int depth = 0;
            for(Instruction i : pcMap.values()){
               if(i.isBranch() || i.isBranchTarget()){
                  block++;
               }
               if(i.isForwardBranch()){
                  depth++;
               }
               if(i.isReverseBranch()){
                  depth--;
               }
               depth -= i.getForwardBranches().size();
               depth += i.getReverseBranches().size();


               i.setBlock(block);
               i.setDepth(depth);


            }

             // need to treat ternary else goto's special.


            ConsumedInstructionTypeStack consumedInstructionTypeStack = new ConsumedInstructionTypeStack(codeEntry.getMaxStack() + 1);

            for(Instruction i : pcMap.values()){
               i.setPostStackBase(consumedInstructionTypeStack.getIndex());
               Instruction.InstructionType[] consumedInstructionTypes = new Instruction.InstructionType[i.getStackConsumeCount()];
               for(int ci = 0; ci < consumedInstructionTypes.length; ci++){
                  consumedInstructionTypes[ci] = consumedInstructionTypeStack.pop();
               }
               i.setConsumedInstructionTypes(consumedInstructionTypes);
               InstructionSet.PushSpec push = i.getByteCode().getPush();

               if(i.isMethodCall()){
                  TypeHelper.JavaMethodArgsAndReturnType calledArgsAndReturnType = i.asMethodCall().getConstantPoolMethodEntry().getArgsAndReturnType();
                  if ( !calledArgsAndReturnType.getReturnType().isVoid()) {
                     consumedInstructionTypeStack.push(i, calledArgsAndReturnType.getReturnType().getPrimitiveType());
                  }
               }else if(i.isFieldAccessor() && i instanceof InstructionSet.AccessField){   // don't do this for assigns to fields!
                  JavaType assignedFieldType = i.asFieldAccessor().getConstantPoolFieldEntry().getType();
                  consumedInstructionTypeStack.push(i, assignedFieldType.getPrimitiveType());

               }else{
                  TypeSpec[] typeSpecs = push.getTypes();
                  int prodCount = i.getStackProduceCount();

                  for(int pi = 0; pi < prodCount; pi++){
                     TypeSpec typeSpec = typeSpecs[pi];
                     consumedInstructionTypeStack.push(i, typeSpec.getPrimitiveType());

                  }
               }
                boolean oldDealWithTernary = false;
               if (oldDealWithTernary){ /*
               // So Ternary operators have to be dealt with.
               // If this is a forward conditional target whose stackbase is now greater than or equal to the branch
               // then the block between produces stack.  So must be 'then' part of ternary
               if(i.isForwardConditionalBranchTarget()){
                  int maxStackBase = 0;
                 // System.out.print("Current postStackBase = " + i.getPostStackBase() + " branchers are ");
                  for(Branch b : i.getForwardBranches()){

                    // System.out.print(b.getPostStackBase() + " ");
                     maxStackBase = Math.max(maxStackBase, b.getPostStackBase());
                  }
                 // System.out.println(" max = " + maxStackBase);
                  if(maxStackBase <= i.getPostStackBase()){
                     // System.out.println("this is first expression in else of ternary");
                     // we pop the stack and mark the instruction targetted by the prev goto.  Which is the end of
                     // the ternary

                     consumedInstructionTypeStack.pop();
                     if(i.getPrevPC().isBranch()){
                        Branch unconditional = i.getPrevPC().asBranch();
                        unconditional.setEndOfTernary(true);
                        unconditional.getTarget().setEndOfTernary(true);
                        for(Branch b : i.getForwardBranches()){
                           b.setEndOfTernary(true);
                        }
                     }else{
                        throw new IllegalStateException("never!");
                     }
                  }
               }else if(i.isEndOfTernary()){
                  // the ternary created top of stack.  So we pop the top (currently referencing the else instruction
                  // producer) and we treat the whole if{}else{} as a producer
                  if(i.isForwardUnconditionalBranchTarget()){
                     Branch fub = i.getForwardUnconditionalBranches().iterator().next(); // we assume this is the earliest
                     if(fub.getNextPC().isForwardConditionalBranchTarget()){
                        Instruction ins = fub.getNextPC().getForwardConditionalBranches().iterator().next();
                        consumedInstructionTypeStack.pop();
                        //  consumedInstructionTypeStack.push(pc);
                        Instruction.InstructionType instructionType = i.getConsumedInstructionTypes()[0] = new Instruction.InstructionType(ins, null);
                     }else{
                        throw new IllegalStateException("never!");
                     }

                  }else{
                     throw new IllegalStateException("never!");
                  }
               }
               */}

               if (!oldDealWithTernary){
                   // is this the first instruction in a ternary else block
                   if (i.isBranch() && i.asBranch().isUnconditional() && i.asBranch().isForward()) {
                       // We now no it is the first in an else block. If this is a normal else the 'then' block will not have left an unbalanced stack.
                       // So check if the stack base of the first instruction of then is equal to this!

                       Instruction elseGoto = i;
                       Instruction firstInElseBlock = i.getNextPC();
                      // Instruction last = elseGoto.getPrevPC();

                           LinkedList<Branch> listOfBranches = firstInElseBlock.getForwardBranches();
                           Branch lastBranchElseBlock = listOfBranches.getLast();
                           Instruction firstInThenBlock = lastBranchElseBlock.getNextPC();
                           System.out.println("firstInThenBlock "+ firstInThenBlock.getPreStackBase()+", "+firstInThenBlock.getPostStackBase()) ;
                         //  System.out.println("last "+ last.getPreStackBase()+", "+last.getPostStackBase()) ;
                           System.out.println("elseGoto "+ elseGoto.getPreStackBase()+", "+elseGoto.getPostStackBase()) ;
                           if (elseGoto.getPostStackBase()>firstInThenBlock.getPostStackBase()){
                               System.out.println("@"+i.getStartPC()+" ternary!");

                               consumedInstructionTypeStack.pop(); // We throw one away!
                           }else{
                               System.out.println("@"+i.getStartPC()+" not ternary!");
                           }

                   }

               }

            }


          /*  AttributePool.RealLocalVariableTableEntry realLocalVariableTableEntry =  getRealLocalVariableTableEntry();

             if(realLocalVariableTableEntry != null && Config.enableShowRealLocalVariableTable){
                 Table table = new Table("|  %3d","|  %3d",  "|   %3d", "|  %2d", "|%4s", "| %8s|");
                 table.header("|Start","|  End", "|Length", "|Slot", "|Name", "|Signature|");
                 AttributePool.RealLocalVariableTableEntry real = realLocalVariableTableEntry;
                 for(LocalVariableInfo var : real){
                     table.data(var.getStart());
                     table.data(var.getEnd());
                     table.data(var.getLength());
                     table.data(var.getSlot());
                     table.data(var.getVariableName());
                     table.data(var.getVariableDescriptor());
                 }
                 System.out.println("REAL!\n"+table);
             }
             */


            AttributePool.LocalVariableTableEntry localVariableTableEntry = attributePool.new LocalVariableTableEntry(pcMap, this);

            setLocalVariableTableEntry(localVariableTableEntry);
            if(Config.enableShowLocalVariableTable){
               Table table = new Table("|  %3d", "|  %3d", "|   %3d", "|  %2d", "|%4s", "| %8s|");
               table.header("|Start", "|  End", "|Length", "|Slot", "|Name", "|Signature|");
               for(AttributePool.LocalVariableTableEntry.LocalVariableInfo var : localVariableTableEntry){
                  table.data(var.getStart());
                  table.data(var.getEnd());
                  table.data(var.getLength());
                  table.data(var.getSlot());
                  table.data(var.getVariableName());
                  table.data(var.getVariableDescriptor());
               }

               System.out.println("\n" + table);
            }


            //  LocalVariableTableEntry localVariableTableEntry = getPreferredLocalVariableTableEntry();

            for(InstructionSet.LocalVariableTableIndexAccessor instruction : accessedLocalVariables){
               int pc = ((Instruction) instruction).getThisPC();
               int len = ((Instruction) instruction).getLength();
               int varIndex = instruction.getLocalVariableTableIndex();
               AttributePool.LocalVariableTableEntry.LocalVariableInfo var = localVariableTableEntry.getVariable(pc + len, varIndex);
               if(var == null){
                  System.out.println("Screwed!");
               }
               instruction.setLocalVariableInfo(var);
            }


         }
         return (pcMap);
      }


      public Collection<Instruction> getInstructions(){
         return (getInstructionMap().values());
      }

      public int getInstructionCount(){
         return getInstructionMap().size();
      }


   }

   class ClassModelInterface{
      private int interfaceIndex;

      ClassModelInterface(ByteReader _byteReader){
         interfaceIndex = _byteReader.u2();
      }

      ConstantPool.ClassEntry getClassEntry(){
         return (constantPool.getClassEntry(interfaceIndex));
      }

      int getInterfaceIndex(){
         return (interfaceIndex);
      }

   }

   //private Class<?> clazz;


   void parse(InputStream _inputStream) throws ClassParseException{

      ByteReader byteReader = new ByteReader(_inputStream);
      magic = byteReader.u4();
      minorVersion = byteReader.u2();
      majorVersion = byteReader.u2();
      constantPool = new ConstantPool(byteReader);

      accessFlags = byteReader.u2();
      thisClassConstantPoolIndex = byteReader.u2();
      superClassConstantPoolIndex = byteReader.u2();

      int interfaceCount = byteReader.u2();
      for(int i = 0; i < interfaceCount; i++){
         ClassModelInterface iface = new ClassModelInterface(byteReader);
         interfaces.add(iface);

      }

      int fieldCount = byteReader.u2();
      for(int i = 0; i < fieldCount; i++){
         ClassModelField field = new ClassModelField(byteReader, i);
         fields.add(field);

      }

      int methodPoolLength = byteReader.u2();
      for(int i = 0; i < methodPoolLength; i++){
         ClassModelMethod method = new ClassModelMethod(byteReader, i);
         methods.add(method);

      }

      attributePool = new AttributePool(byteReader);

   }

   int getMagic(){
      return (magic);
   }

   int getMajorVersion(){
      return (majorVersion);
   }

   int getMinorVersion(){
      return (minorVersion);
   }

   int getAccessFlags(){
      return (accessFlags);
   }

   ConstantPool getConstantPool(){
      return (constantPool);
   }

   int getThisClassConstantPoolIndex(){
      return (thisClassConstantPoolIndex);
   }

   int getSuperClassConstantPoolIndex(){
      return (superClassConstantPoolIndex);
   }

   AttributePool getAttributePool(){
      return (attributePool);
   }

   ClassModelField getField(String _name, String _descriptor){
      for(ClassModelField entry : fields){
         if(entry.getName().equals(_name) && entry.getDescriptor().equals(_descriptor)){
            return (entry);
         }
      }
      return superClazzModel.getField(_name, _descriptor);
   }

   ClassModelField getField(String _name){
      for(ClassModelField entry : fields){
         if(entry.getName().equals(_name)){
            return (entry);
         }
      }
      return superClazzModel.getField(_name);
   }

   public ClassModelMethod getMethod(String _name, String _descriptor){
      for(ClassModelMethod entry : methods){
         if(entry.getName().equals(_name) && entry.getDescriptor().equals(_descriptor)){
            return (entry);
         }
      }
      return superClazzModel != null ? superClazzModel.getMethod(_name, _descriptor) : (null);
   }

   public List<ClassModelMethod> getMethods(){
     return(methods);
   }

   List<ClassModelField> getFieldPoolEntries(){
      return (fields);
   }

   /**
    * Look up a ConstantPool MethodEntry and return the corresponding Method.
    *
    * @param _methodEntry The ConstantPool MethodEntry we want.
    * @param _isSpecial   True if we wish to delegate to super (to support <code>super.foo()</code>)
    * @return The Method or null if we fail to locate a given method.
    */
   ClassModelMethod getMethod(MethodEntry _methodEntry, boolean _isSpecial){
      String entryClassNameInDotForm = _methodEntry.getClassEntry().getDotClassName();

      // Shortcut direct calls to supers to allow "foo() { super.foo() }" prefix stuff to work
      if(_isSpecial && (superClazzModel != null) && superClazzModel.isSuperClass(entryClassNameInDotForm)){
         if(logger.isLoggable(Level.FINE)){
            logger.fine("going to look in super:" + superClazzModel.getDotClassName() + " on behalf of "
                  + entryClassNameInDotForm);
         }
         return superClazzModel.getMethod(_methodEntry, false);
      }

      for(ClassModelMethod entry : methods){
         if(entry.getName().equals(_methodEntry.getNameAndTypeEntry().getName())
               && entry.getDescriptor().equals(_methodEntry.getNameAndTypeEntry().getDescriptor())){
            if(logger.isLoggable(Level.FINE)){
               logger.fine("Found " + getDotClassName()
                     + "." + entry.getName() + " " + entry.getDescriptor() + " for "
                     + entryClassNameInDotForm);
            }
            return (entry);
         }
      }

      return superClazzModel != null ? superClazzModel.getMethod(_methodEntry, false) : (null);
   }

   /**
    * Create a MethodModel for a given method name and signature.
    *
    * @param _name
    * @param _signature
    * @return
    * @throws AparapiException
    */

   public MethodModel getMethodModel(String _name, String _signature) throws AparapiException{
      ClassModelMethod method = getMethod(_name, _signature);
      return MethodModel.getMethodModel(method);
   }

   // These fields use for accessor conversion
   private ArrayList<FieldEntry> structMembers = new ArrayList<FieldEntry>();

   private ArrayList<Long> structMemberOffsets = new ArrayList<Long>();

   private ArrayList<TypeSpec> structMemberTypes = new ArrayList<TypeSpec>();

   private int totalStructSize = 0;

   ArrayList<FieldEntry> getStructMembers(){
      return structMembers;
   }

   ArrayList<Long> getStructMemberOffsets(){
      return structMemberOffsets;
   }

   ArrayList<TypeSpec> getStructMemberTypes(){
      return structMemberTypes;
   }

   int getTotalStructSize(){
      return totalStructSize;
   }

   void setTotalStructSize(int x){
      totalStructSize = x;
   }

   Entrypoint getLambdaEntrypoint(String _entrypointName, String _descriptor, Object _k) throws AparapiException{
      MethodModel method = getMethodModel(_entrypointName, _descriptor);
      return (Entrypoint.getEntryPoint(this, method, _k, true));
   }

   public Entrypoint getKernelEntrypoint(String _entrypointName, String _descriptor, Object _k) throws AparapiException{
      MethodModel method = getMethodModel(_entrypointName, _descriptor);
      return (Entrypoint.getEntryPoint(this, method, _k, false));
   }

   Class<?> getClassWeAreModelling(){
      return clazz;
   }

   public Entrypoint getKernelEntrypoint(String _entrypointName, Object _k) throws AparapiException{
      return (getKernelEntrypoint(_entrypointName, "()V", _k));
   }

   public Entrypoint getLambdaEntrypoint(String _entrypointName, Object _k) throws AparapiException{
      return (getLambdaEntrypoint(_entrypointName, "()V", _k));
   }

   public Entrypoint getKernelEntrypoint() throws AparapiException{
      return (getKernelEntrypoint("run", "()V", null));
   }

   public Entrypoint getLambdaEntrypoint() throws AparapiException{
      return (getLambdaEntrypoint("run", "()V", null));
   }

   public String getClassName(){
      ConstantPool.ClassEntry thisClassEntry = constantPool.getClassEntry(getThisClassConstantPoolIndex());
      return (thisClassEntry.getClassName());
   }

   public JavaType getClassType(){
      ConstantPool.ClassEntry thisClassEntry = constantPool.getClassEntry(getThisClassConstantPoolIndex());
      return (thisClassEntry.getType());
   }

   public String getDotClassName(){
      ConstantPool.ClassEntry thisClassEntry = constantPool.getClassEntry(getThisClassConstantPoolIndex());
      return (thisClassEntry.getDotClassName());
   }

   public String getMangledClassName(){

      ConstantPool.ClassEntry thisClassEntry = constantPool.getClassEntry(getThisClassConstantPoolIndex());
      return (thisClassEntry.getMangledClassName());
   }

   public String getSuperClassName(){
      ConstantPool.ClassEntry superClassEntry = constantPool.getClassEntry(getSuperClassConstantPoolIndex());
      return (superClassEntry.getClassName());
   }

   public String getSuperDotClassName(){
      int superClassConstantPoolIndex = getSuperClassConstantPoolIndex();
      ConstantPool.ClassEntry superClassEntry = constantPool.getClassEntry(superClassConstantPoolIndex);
      if(superClassEntry == null){
         superClassEntry = superClassEntry;
      }
      return (superClassEntry.getDotClassName());
   }

}
