

#define CLASSTOOLS_CPP
#include "classtools.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

u1_t ByteBuffer::u1(byte_t *ptr){
   u1_t u1 = (u1_t) (*ptr & 0xff);
   return (u1);
}
u2_t ByteBuffer::u2(byte_t *ptr){
   u2_t u2 = (u1(ptr)<<8)|u1(ptr+1);
   return (u2);
}
s2_t ByteBuffer::s2(byte_t *ptr){
   u2_t u2 = (u1(ptr)<<8)|u1(ptr+1);
   return ((s2_t)u2);
}
u4_t ByteBuffer::u4(byte_t *ptr){
   u4_t u4 = (u2(ptr)<<16)|u2(ptr+2);
   return (u4);
}
s4_t ByteBuffer::s4(byte_t *ptr){
   u4s4f4_u u4s4f4;
   u4s4f4.u4 = u4(ptr);
   return (u4s4f4.s4);
}
f4_t ByteBuffer::f4(byte_t *ptr){
   u4s4f4_u u4s4f4;
   u4s4f4.u4 = u4(ptr);
   return (u4s4f4.f4);
}
u8_t ByteBuffer::u8(byte_t *ptr){
   u8_t u8 = (((u8_t)u4(ptr))<<32)|u4(ptr+4);
   return (u8);
}
s8_t ByteBuffer::s8(byte_t *ptr){
   u8s8f8_u u8s8f8;
   u8s8f8.u8 = u8(ptr);
   return (u8s8f8.s8);
}
f8_t ByteBuffer::f8(byte_t *ptr){
   u8s8f8_u u8s8f8;
   u8s8f8.u8 = u8(ptr);
   return (u8s8f8.f8);
}
ByteBuffer::ByteBuffer(byte_t *_bytes, size_t _len)
   : len(_len), bytes(_bytes), ptr(_bytes){
   }
ByteBuffer::~ByteBuffer(){
}
byte_t *ByteBuffer::getBytes(){
   return(bytes);
}
size_t ByteBuffer::getLen(){
   return(len);
}
u1_t ByteBuffer::u1(){
   u1_t value = u1(ptr); ptr+=1;
   return (value);
}
u2_t ByteBuffer::u2(){
   u2_t value = u2(ptr); ptr+=2;
   return (value);
}
s2_t ByteBuffer::s2(){
   s2_t value = s2(ptr); ptr+=2;
   return (value);
}
u4_t ByteBuffer::u4(){
   u4_t value = u4(ptr); ptr+=4;
   return (value);
}
f4_t ByteBuffer::f4(){
   f4_t  value = f4(ptr); ptr+=4;
   return (value);
}
s4_t ByteBuffer::s4(){
   s4_t value = s4(ptr); ptr+=4;
   return (value);
}
u8_t ByteBuffer::u8(){
   u8_t value = u8(ptr); ptr+=8;
   return (value);
}
f8_t ByteBuffer::f8(){
   f8_t value = f8(ptr); ptr+=8;
   return (value);
}
s8_t ByteBuffer::s8(){
   s8_t value = s8(ptr); ptr+=8;
   return (value);
}

char *ByteBuffer::createUTF8(int _len){
   char *buf = NULL;
   if (_len > 0){
      buf = new char[_len+1];
      memcpy((void*)buf, (void*)ptr, _len);
      buf[_len]='\0';
   }
   ptr+=_len;
   return(buf); 
}

ByteBuffer *ByteBuffer::createByteBuffer(int _len){
   byte_t *buf = ptr;
   ptr+=_len;
   ByteBuffer *byteBuffer = new ByteBuffer(buf, _len);
   return byteBuffer;
}
size_t  ByteBuffer::getOffset(){
   return((size_t)(ptr-bytes));
}
bool  ByteBuffer::empty(){
   return(getOffset()>=len);
}

ConstantPoolEntry::ConstantPoolEntry(ByteBuffer *_byteBuffer, ConstantPoolType _constantPoolType)
   : constantPoolType(_constantPoolType) {
   }
ConstantPoolEntry::~ConstantPoolEntry(){
}

ConstantPoolType ConstantPoolEntry::getConstantPoolType() {
   return (constantPoolType);
}

EmptyConstantPoolEntry::EmptyConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  ConstantPoolEntry(_byteBuffer, EMPTY) {
   }
EmptyConstantPoolEntry::~EmptyConstantPoolEntry(){
}

UTF8ConstantPoolEntry::UTF8ConstantPoolEntry(ByteBuffer *_byteBuffer) 
   : ConstantPoolEntry(_byteBuffer, UTF8), utf8(NULL) {
      len = (size_t)_byteBuffer->u2();
      utf8 = _byteBuffer->createUTF8(len);
   }
UTF8ConstantPoolEntry::~UTF8ConstantPoolEntry(){
   if (utf8){
      delete[] utf8;
   }
}
size_t UTF8ConstantPoolEntry::getLen() {
   return(len);
}
char *UTF8ConstantPoolEntry::getUTF8() {
   return(utf8);
}
void UTF8ConstantPoolEntry::write(FILE *file){
   fprintf(file, "len %d \"", (int)len);
   if (len != 0 && utf8 != NULL){
      for (unsigned int i=0; i<len; i++){
         fprintf(file, "%c", utf8[i]);
      }
   }
   fprintf(file, "\"");
}

IntegerConstantPoolEntry::IntegerConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  ConstantPoolEntry(_byteBuffer, INTEGER) {
      value = _byteBuffer->s4();
   }
IntegerConstantPoolEntry::~IntegerConstantPoolEntry(){
}

s4_t IntegerConstantPoolEntry::getValue(){
   return(value);
}

FloatConstantPoolEntry::FloatConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  ConstantPoolEntry(_byteBuffer, FLOAT) {
      value = _byteBuffer->f4();
   }
FloatConstantPoolEntry::~FloatConstantPoolEntry(){
}

f4_t FloatConstantPoolEntry::getValue(){
   return(value);
}

DoubleConstantPoolEntry::DoubleConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  ConstantPoolEntry(_byteBuffer, DOUBLE) {
      value = _byteBuffer->f8();
   }
DoubleConstantPoolEntry::~DoubleConstantPoolEntry(){
}
f8_t DoubleConstantPoolEntry::getValue(){
   return(value);
}

LongConstantPoolEntry::LongConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  ConstantPoolEntry(_byteBuffer, LONG) {
      value = _byteBuffer->s8();
   }
LongConstantPoolEntry::~LongConstantPoolEntry(){
}
s8_t LongConstantPoolEntry::getValue(){
   return(value);
}

ClassConstantPoolEntry::ClassConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  ConstantPoolEntry(_byteBuffer, CLASS) {
      nameIndex = _byteBuffer->u2();
   }
ClassConstantPoolEntry::~ClassConstantPoolEntry(){
}
u2_t ClassConstantPoolEntry::getNameIndex(){
   return(nameIndex);
}


ReferenceConstantPoolEntry::ReferenceConstantPoolEntry(ByteBuffer *_byteBuffer, ConstantPoolType _constantPoolType)
   :  ConstantPoolEntry(_byteBuffer, _constantPoolType) {
      referenceClassIndex = _byteBuffer->u2();
      nameAndTypeIndex = _byteBuffer->u2();
   }
ReferenceConstantPoolEntry::~ReferenceConstantPoolEntry(){
}
u2_t ReferenceConstantPoolEntry::getReferenceClassIndex(){
   return(referenceClassIndex);
}
u2_t ReferenceConstantPoolEntry::getNameAndTypeIndex(){
   return(nameAndTypeIndex);
}

FieldConstantPoolEntry::FieldConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  ReferenceConstantPoolEntry(_byteBuffer, FIELD) {
   }
FieldConstantPoolEntry::~FieldConstantPoolEntry(){
}

MethodReferenceConstantPoolEntry::MethodReferenceConstantPoolEntry(ByteBuffer *_byteBuffer, ConstantPoolType _constantPoolType)
   :  ReferenceConstantPoolEntry(_byteBuffer, _constantPoolType) {
   }

MethodReferenceConstantPoolEntry::~MethodReferenceConstantPoolEntry(){
}

MethodConstantPoolEntry::MethodConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  MethodReferenceConstantPoolEntry(_byteBuffer, METHOD) {
   }
MethodConstantPoolEntry::~MethodConstantPoolEntry(){
}
u4_t MethodConstantPoolEntry::getArgCount(ConstantPoolEntry** constantPool){
   NameAndTypeConstantPoolEntry* nameAndType = (NameAndTypeConstantPoolEntry*)constantPool[nameAndTypeIndex];
   u2_t descriptorIndex = nameAndType->getDescriptorIndex();
   UTF8ConstantPoolEntry* utf8 = (UTF8ConstantPoolEntry*)constantPool[descriptorIndex];
   int argc=0;
   char *ptr = utf8->getUTF8();
   if (*ptr=='('){
      ptr++;
      while (*ptr!=')'){
         if (*ptr == '['){
            int dims=0;
            while (*ptr=='['){
               dims++;
               ptr++;
            }
         }
         if (*ptr=='L'){
            while (*ptr!=';'){
               ptr++;
            }
         }
         argc++;
         ptr++;
      }
   }
   //u2_t nameIndex = nameAndType->getNameIndex();
   //UTF8ConstantPoolEntry* utf8Name = (UTF8ConstantPoolEntry*)constantPool[nameIndex];
   //fprintf(stdout, "arg count of %s%s %d\n", utf8Name->getUTF8(), utf8->getUTF8(), argc);
   return(argc);
}


u4_t MethodConstantPoolEntry::getRetCount(ConstantPoolEntry** constantPool){
   NameAndTypeConstantPoolEntry* nameAndType = (NameAndTypeConstantPoolEntry*)constantPool[nameAndTypeIndex];
   u2_t descriptorIndex = nameAndType->getDescriptorIndex();
   UTF8ConstantPoolEntry* utf8 = (UTF8ConstantPoolEntry*)constantPool[descriptorIndex];
   int retc=0;
   char *ptr = utf8->getUTF8();
   if (*ptr=='('){
      ptr++;
      while (*ptr!=')'){
         ptr++;
      }
      ptr++;
      if (*ptr!='V'){
         retc++;
      }
   }
   //u2_t nameIndex = nameAndType->getNameIndex();
   //UTF8ConstantPoolEntry* utf8Name = (UTF8ConstantPoolEntry*)constantPool[nameIndex];
   //fprintf(stdout, "ret count of %s%s %d\n", utf8Name->getUTF8(), utf8->getUTF8(), retc);
   return(retc);
}

InterfaceMethodConstantPoolEntry::InterfaceMethodConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  MethodReferenceConstantPoolEntry(_byteBuffer, INTERFACEMETHOD) {
   }

InterfaceMethodConstantPoolEntry::~InterfaceMethodConstantPoolEntry(){
}
NameAndTypeConstantPoolEntry::NameAndTypeConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  ConstantPoolEntry(_byteBuffer, NAMEANDTYPE) {
      nameIndex = _byteBuffer->u2();
      descriptorIndex = _byteBuffer->u2();
   }
NameAndTypeConstantPoolEntry::~NameAndTypeConstantPoolEntry(){
}
u2_t NameAndTypeConstantPoolEntry::getDescriptorIndex(){
   return(descriptorIndex);
}
u2_t NameAndTypeConstantPoolEntry::getNameIndex(){
   return(nameIndex);
}


MethodTypeConstantPoolEntry::MethodTypeConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  ConstantPoolEntry(_byteBuffer, METHODTYPE) {
      descriptorIndex = _byteBuffer->u2();
   }
MethodTypeConstantPoolEntry::~MethodTypeConstantPoolEntry(){
}
u2_t MethodTypeConstantPoolEntry::getDescriptorIndex(){
   return(descriptorIndex);
}

MethodHandleConstantPoolEntry::MethodHandleConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  ConstantPoolEntry(_byteBuffer, METHODHANDLE) {
      referenceKind = _byteBuffer->u1();
      referenceIndex = _byteBuffer->u2();
   }
MethodHandleConstantPoolEntry::~MethodHandleConstantPoolEntry(){
}
u1_t MethodHandleConstantPoolEntry::getReferenceKind(){
   return(referenceKind);
}
u2_t MethodHandleConstantPoolEntry::getReferenceIndex(){
   return(referenceIndex);
}

StringConstantPoolEntry::StringConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  ConstantPoolEntry(_byteBuffer, STRING) {
      utf8Index = _byteBuffer->u2();
   }
StringConstantPoolEntry::~StringConstantPoolEntry(){
}
u2_t StringConstantPoolEntry::getUtf8Index(){
   return(utf8Index);
}

InvokeDynamicConstantPoolEntry::InvokeDynamicConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  ConstantPoolEntry(_byteBuffer, INVOKEDYNAMIC) {
      bootstrapMethodAttrIndex = _byteBuffer->u2();
      nameAndTypeIndex = _byteBuffer->u2();
   }
InvokeDynamicConstantPoolEntry::~InvokeDynamicConstantPoolEntry(){
}
u2_t InvokeDynamicConstantPoolEntry::getBootStrapMethodAttrIndex(){
   return(bootstrapMethodAttrIndex);
}
u2_t InvokeDynamicConstantPoolEntry::getNameAndTypeIndex(){
   return(nameAndTypeIndex);
}

LineNumberTableAttribute::LineNumberTableEntry::LineNumberTableEntry(ByteBuffer *_byteBuffer, ConstantPoolEntry **_constantPool){
   start_pc = _byteBuffer->u2();
   line_number = _byteBuffer->u2();
}
LineNumberTableAttribute::LineNumberTableEntry::~LineNumberTableEntry(){
}

   LineNumberTableAttribute::LineNumberTableAttribute(ByteBuffer *_byteBuffer, ConstantPoolEntry **_constantPool)
: lineNumberTable(NULL)
{
   line_number_table_length = _byteBuffer->u2();
   if (line_number_table_length){
      lineNumberTable = new LineNumberTableEntry *[line_number_table_length];
      for (u2_t i =0; i< line_number_table_length; i++){
         lineNumberTable[i] = new LineNumberTableEntry(_byteBuffer, _constantPool);
      }
   }
}
LineNumberTableAttribute::~LineNumberTableAttribute(){
   for (u2_t i =0; i< line_number_table_length; i++){
      delete lineNumberTable[i];
   }
   if (lineNumberTable){
      delete[] lineNumberTable;
   }
}

LocalVariableTableAttribute::LocalVariableTableEntry::LocalVariableTableEntry(ByteBuffer *_byteBuffer, ConstantPoolEntry **_constantPool){
   start_pc = _byteBuffer->u2();
   length = _byteBuffer->u2();
   name_index = _byteBuffer->u2();
   descriptor_index = _byteBuffer->u2();
   index = _byteBuffer->u2();
}
LocalVariableTableAttribute::LocalVariableTableEntry::~LocalVariableTableEntry(){
}
bool LocalVariableTableAttribute::LocalVariableTableEntry::isMatch(u2_t _pc, u2_t _index){
    return(_pc>=start_pc && _pc<(start_pc+length) && _index == index);
}
u2_t LocalVariableTableAttribute::LocalVariableTableEntry::getNameIndex(){
    return(name_index);
}
u2_t LocalVariableTableAttribute::LocalVariableTableEntry::getDescriptorIndex(){
    return(descriptor_index);
}

   LocalVariableTableAttribute::LocalVariableTableAttribute(ByteBuffer *_byteBuffer, ConstantPoolEntry **_constantPool)
: localVariableTable(NULL)
{
   local_variable_table_length = _byteBuffer->u2();
   if (local_variable_table_length){
      localVariableTable = new LocalVariableTableEntry *[local_variable_table_length];
      for (u2_t i =0; i< local_variable_table_length; i++){
         localVariableTable[i] = new LocalVariableTableEntry(_byteBuffer, _constantPool);
      }
   }
}
LocalVariableTableAttribute::~LocalVariableTableAttribute(){
   for (u2_t i =0; i< local_variable_table_length; i++){
      delete localVariableTable[i];
   }
   if (localVariableTable){
      delete[] localVariableTable;
   }
}
char* LocalVariableTableAttribute::getLocalVariableName(u4_t _pc, u2_t _slot, ConstantPoolEntry **_constantPool){
   for (int i = 0 ; i< local_variable_table_length; i++){
       LocalVariableTableEntry* entry = localVariableTable[i];
       if (entry->isMatch(_pc,_slot)){
          return((UTF8ConstantPoolEntry*)(_constantPool[entry->getNameIndex()]))->getUTF8();
       }
   }
   return((char *)"?");
   
}

CodeAttribute::ExceptionTableEntry::ExceptionTableEntry(ByteBuffer *_byteBuffer, ConstantPoolEntry **_constantPool){
   start_pc = _byteBuffer->u2();
   end_pc = _byteBuffer->u2();
   handler_pc = _byteBuffer->u2();
   catch_type = _byteBuffer->u2();
}
CodeAttribute::ExceptionTableEntry::~ExceptionTableEntry(){
}

   CodeAttribute::CodeAttribute(ByteBuffer *_byteBuffer, ConstantPoolEntry **_constantPool)
: attributes(NULL), exceptionTable(NULL), codeByteBuffer(NULL)
{
   max_stack = _byteBuffer->u2();
   max_locals = _byteBuffer->u2();
   u4_t code_length = _byteBuffer->u4();
   if (code_length){
      codeByteBuffer = _byteBuffer->createByteBuffer(code_length);
   }
   exception_table_length = _byteBuffer->u2();
   if (exception_table_length){
      exceptionTable = new ExceptionTableEntry *[exception_table_length];
      for (u2_t i =0; i< exception_table_length; i++){
         exceptionTable[i] = new ExceptionTableEntry(_byteBuffer, _constantPool);
      }
   }
   attributes_count = _byteBuffer->u2();
   if (attributes_count){
      attributes = new AttributeInfo *[attributes_count];
      for (u2_t i=0; i< attributes_count; i++){
         attributes[i] = new AttributeInfo(_byteBuffer, _constantPool);
            switch(attributes[i]->getAttributeType()){
               case LineNumberTable:
                  lineNumberTableAttribute = attributes[i]->getLineNumberTableAttribute();
                  break;
               case LocalVariableTable:
                  localVariableTableAttribute = attributes[i]->getLocalVariableTableAttribute();
                  break;
            }
      }
   }
}
LineNumberTableAttribute* CodeAttribute::getLineNumberTableAttribute(){
   return(lineNumberTableAttribute);
}
LocalVariableTableAttribute* CodeAttribute::getLocalVariableTableAttribute(){
   return(localVariableTableAttribute);
}
CodeAttribute::~CodeAttribute(){
   for (u2_t i =0; i< exception_table_length; i++){
      delete exceptionTable[i] ;
   }
   if (exceptionTable){
      delete[] exceptionTable;
   }
   for (u2_t i=0; i< attributes_count; i++){
      delete attributes[i];
   }
   if (attributes){
      delete[] attributes;
   }
   if (codeByteBuffer){
      delete codeByteBuffer;
   }
}

ByteBuffer *CodeAttribute::getCodeByteBuffer(){
   return(codeByteBuffer);
}

u2_t CodeAttribute::getMaxStack(){
   return(max_stack);
}

u2_t CodeAttribute::getMaxLocals(){
   return(max_locals);
}

   AttributeInfo::AttributeInfo(ByteBuffer *_byteBuffer, ConstantPoolEntry **_constantPool)
: attribute_type(UNKNOWN), codeAttribute(NULL), infoByteBuffer(NULL)
{
   attribute_name_index = _byteBuffer->u2();
   UTF8ConstantPoolEntry *attributeName = (UTF8ConstantPoolEntry*)_constantPool[getAttributeNameIndex()];
   char *attributeNameChars = attributeName->getUTF8();
   u4_t attribute_length = _byteBuffer->u4();
   infoByteBuffer = _byteBuffer->createByteBuffer(attribute_length);
   if (!strcmp(attributeNameChars, "Code")){
      codeAttribute = new CodeAttribute(infoByteBuffer, _constantPool);
      attribute_type  = Code;
   } else if (!strcmp(attributeNameChars, "LineNumberTable")){
      lineNumberTableAttribute = new LineNumberTableAttribute(infoByteBuffer, _constantPool);
      attribute_type  = LineNumberTable;
   } else if (!strcmp(attributeNameChars, "LocalVariableTable")){
      localVariableTableAttribute = new LocalVariableTableAttribute(infoByteBuffer, _constantPool);
      attribute_type  = LocalVariableTable;
   } 
}
AttributeInfo::~AttributeInfo(){
   if (infoByteBuffer){
      delete infoByteBuffer;
   }
   switch (attribute_type){
      case Code:
         if (codeAttribute){
            delete codeAttribute;
         }
         break;
      case LineNumberTable:
         if (lineNumberTableAttribute){
            delete lineNumberTableAttribute;
         }
         break;
      case LocalVariableTable:
         if (localVariableTableAttribute){
            delete localVariableTableAttribute;
         }
         break;
   }

}
u2_t AttributeInfo::getAttributeNameIndex(){
   return(attribute_name_index);
}
AttributeType AttributeInfo::getAttributeType(){
   return(attribute_type);
}

CodeAttribute *AttributeInfo::getCodeAttribute(){
   return(codeAttribute);
}

LineNumberTableAttribute *AttributeInfo::getLineNumberTableAttribute(){
   return(lineNumberTableAttribute);
}

LocalVariableTableAttribute *AttributeInfo::getLocalVariableTableAttribute(){
   return(localVariableTableAttribute);
}


   FieldInfo::FieldInfo(ByteBuffer *_byteBuffer, ConstantPoolEntry **_constantPool)
: attributes(NULL)
{
   access_flags = _byteBuffer->u2();
   name_index = _byteBuffer->u2();
   descriptor_index = _byteBuffer->u2();
   attributes_count = _byteBuffer->u2();
   if (attributes_count){
      attributes = new AttributeInfo *[attributes_count];
      for (u2_t i=0; i< attributes_count; i++){
         attributes[i] = new AttributeInfo(_byteBuffer, _constantPool);
      }
   }
}
FieldInfo::~FieldInfo(){
   for (u2_t i=0; i< attributes_count; i++){
      delete attributes[i];
   }
   if (attributes){
      delete[] attributes;
   }
}
u2_t FieldInfo::getNameIndex(){
   return(name_index);
}
u2_t FieldInfo::getDescriptorIndex(){
   return(descriptor_index);
}

MethodInfo::MethodInfo(ByteBuffer *_byteBuffer, ConstantPoolEntry **_constantPool)
   : attributes(NULL) {
      access_flags = _byteBuffer->u2();
      name_index = _byteBuffer->u2();
      descriptor_index = _byteBuffer->u2();
      attributes_count = _byteBuffer->u2();
      if (attributes_count){
         attributes = new AttributeInfo *[attributes_count];
         for (u2_t i=0; i< attributes_count; i++){
            attributes[i] = new AttributeInfo(_byteBuffer, _constantPool);
            switch(attributes[i]->getAttributeType()){
               case Code:
                  codeAttribute = attributes[i]->getCodeAttribute();
                  break;
            }
         }
      }
   }
MethodInfo::~MethodInfo(){
   for (u2_t i=0; i< attributes_count; i++){
      delete attributes[i];
   }
   if (attributes){
      delete[] attributes;
   }

}
u2_t MethodInfo::getNameIndex(){
   return(name_index);
}
u2_t MethodInfo::getDescriptorIndex(){
   return(descriptor_index);
}
CodeAttribute* MethodInfo::getCodeAttribute(){
   return(codeAttribute);
}



   ClassInfo::ClassInfo(ByteBuffer *_byteBuffer)
: constantPool(NULL), interfaces(NULL), fields(NULL), methods(NULL), attributes(NULL)
{
   magic= _byteBuffer->u4();
   if (magic == 0xcafebabe){
      minor= _byteBuffer->u2();
      major= _byteBuffer->u2();
      constantPoolSize = _byteBuffer->u2();
      constantPool=new ConstantPoolEntry *[constantPoolSize];
      u4_t slot = 0;
      for (u4_t i=0; i<constantPoolSize; i++){
         constantPool[i]=NULL;
      }
      constantPool[slot++] = new EmptyConstantPoolEntry(_byteBuffer);

      while (slot < constantPoolSize){
         ConstantPoolType constantPoolType = (ConstantPoolType)_byteBuffer->u1();
         switch (constantPoolType){
            case UTF8: //1
               constantPool[slot++] = new UTF8ConstantPoolEntry(_byteBuffer);
               break;
            case INTEGER: //3
               constantPool[slot++] = new IntegerConstantPoolEntry(_byteBuffer);
               break;
            case FLOAT: //4
               constantPool[slot++] = new FloatConstantPoolEntry(_byteBuffer);
               break;
            case LONG: //5
               constantPool[slot++] = new LongConstantPoolEntry(_byteBuffer);
               constantPool[slot++] = new EmptyConstantPoolEntry(_byteBuffer);
               break;
            case DOUBLE: //6
               constantPool[slot++] = new DoubleConstantPoolEntry(_byteBuffer);
               constantPool[slot++] = new EmptyConstantPoolEntry(_byteBuffer);
               break;
            case CLASS: //7
               constantPool[slot++] = new ClassConstantPoolEntry(_byteBuffer);
               break;
            case STRING: //8
               constantPool[slot++] = new StringConstantPoolEntry(_byteBuffer);
               break;
            case FIELD: //9
               constantPool[slot++] = new FieldConstantPoolEntry(_byteBuffer);
               break;
            case METHOD: //10
               constantPool[slot++] = new MethodConstantPoolEntry(_byteBuffer);
               break;
            case INTERFACEMETHOD: //11
               constantPool[slot++] = new InterfaceMethodConstantPoolEntry(_byteBuffer);
               break;
            case NAMEANDTYPE: //12
               constantPool[slot++] = new NameAndTypeConstantPoolEntry(_byteBuffer);
               break;
            case METHODHANDLE: //15
               constantPool[slot++] = new MethodHandleConstantPoolEntry(_byteBuffer);
               break;
            case METHODTYPE: //16
               constantPool[slot++] = new MethodTypeConstantPoolEntry(_byteBuffer);
               break;
            case INVOKEDYNAMIC: //18
               constantPool[slot++] = new InvokeDynamicConstantPoolEntry(_byteBuffer);
               break;
            default: 
               fprintf(stdout, "ERROR found UNKNOWN! %02x/%0d in slot %d\n", constantPoolType, constantPoolType, slot );
               exit (1);
         }
      }

      // we have the constant pool 

      accessFlags = _byteBuffer->u2();
      thisClassConstantPoolIndex = _byteBuffer->u2();
      superClassConstantPoolIndex = _byteBuffer->u2();
      ClassConstantPoolEntry *superClassConstantPoolEntry = (ClassConstantPoolEntry*)constantPool[superClassConstantPoolIndex];
      UTF8ConstantPoolEntry *superClassUTF8ConstantPoolEntry = (UTF8ConstantPoolEntry*)constantPool[superClassConstantPoolEntry->getNameIndex()];

      interfaceCount = _byteBuffer->u2();
      if (interfaceCount){
         interfaces  = new u2_t[interfaceCount];
         for (u2_t i=0; i< interfaceCount; i++){
            interfaces[i] = _byteBuffer->u2();
         }
      }
      fieldCount = _byteBuffer->u2();
      if (fieldCount){
         fields  = new FieldInfo*[fieldCount];
         for (u2_t i=0; i< fieldCount; i++){
            fields[i] = new FieldInfo(_byteBuffer, constantPool);
         }
      }
      methodCount = _byteBuffer->u2();
      if (methodCount){
         methods  = new MethodInfo*[methodCount];
         for (u2_t i=0; i< methodCount; i++){
            methods[i] = new MethodInfo(_byteBuffer, constantPool);
         }
      }
      attributeCount = _byteBuffer->u2();
      if (attributeCount){
         attributes = new AttributeInfo *[attributeCount];
         for (u2_t i=0; i< attributeCount; i++){
            attributes[i] = new AttributeInfo(_byteBuffer, constantPool);
         }
      }
   }
}
ClassInfo::~ClassInfo(){
   if (attributes){
      for (u2_t i=0; i< attributeCount; i++){
         if (attributes[i]){
            delete attributes[i];
         }
      }
      delete[] attributes;
   }
   if (interfaces){
      delete[] interfaces;
   }
   if (fields){
      for (u2_t i=0; i< fieldCount; i++){
         if (fields[i]){
            delete fields[i];
         }
      }
      delete[] fields;
   }
   if (methods){
      for (u2_t i=0; i< methodCount; i++){
         if (methods[i]){
            delete methods[i];
         }
      }
      delete[] methods;
   }
   for (u4_t i = 0; i<constantPoolSize; i++){ // <= intentional!
      if (constantPool[i]){
         delete constantPool[i];
      }
   }
   if (constantPool){
      delete[] constantPool;
   }
}
// com/amd/aparapi/Main$Kernel.run()V == "run", "()V"
MethodInfo *ClassInfo::getMethodInfo(char *_methodName, char *_methodDescriptor){
   MethodInfo *returnMethodInfo = NULL;
   for (u2_t i=0; returnMethodInfo == NULL && i< methodCount; i++){
      MethodInfo* methodInfo = methods[i];
      char * name=(char*)((UTF8ConstantPoolEntry*)constantPool[methodInfo->getNameIndex()])->getUTF8();
      char * descriptor=(char*)((UTF8ConstantPoolEntry*)constantPool[methodInfo->getDescriptorIndex()])->getUTF8();
      if (!strcmp(_methodName, name) && !strcmp(_methodDescriptor, descriptor)){
         returnMethodInfo = methodInfo;
      }
   }
   return(returnMethodInfo);
}

char *ClassInfo::getClassName(){
   ClassConstantPoolEntry *thisClassConstantPoolEntry = (ClassConstantPoolEntry*)constantPool[thisClassConstantPoolIndex];
   UTF8ConstantPoolEntry *thisClassUTF8ConstantPoolEntry = (UTF8ConstantPoolEntry*)constantPool[thisClassConstantPoolEntry->getNameIndex()];
   return((char*)thisClassUTF8ConstantPoolEntry->getUTF8());
}

char *ClassInfo::getSuperClassName(){
   ClassConstantPoolEntry *superClassConstantPoolEntry = (ClassConstantPoolEntry*)constantPool[superClassConstantPoolIndex];
   UTF8ConstantPoolEntry *superClassUTF8ConstantPoolEntry = (UTF8ConstantPoolEntry*)constantPool[superClassConstantPoolEntry->getNameIndex()];
   return((char*)superClassUTF8ConstantPoolEntry->getUTF8());
}

ConstantPoolEntry **ClassInfo::getConstantPool(){
   return(constantPool);
}




