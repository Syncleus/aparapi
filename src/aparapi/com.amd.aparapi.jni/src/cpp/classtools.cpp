

#define CLASSTOOLS_CPP
#include "classtools.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define SHOW

u1_t ByteBuffer::u1(byte_t *ptr){
   u1_t u1 = (u1_t) (*ptr & 0xff);
   //     fprintf(stderr, "u1 %01x\n", u1);
   return (u1);
}
u2_t ByteBuffer::u2(byte_t *ptr){
   u2_t u2 = (u1(ptr)<<8)|u1(ptr+1);
   //     fprintf(stderr, "u2 %02x\n", u2);
   return (u2);
}
u4_t ByteBuffer::u4(byte_t *ptr){
   u4_t u4 = (u2(ptr)<<16)|u2(ptr+2);
   //      fprintf(stderr, "u4 %04x\n", u4);
   return (u4);
}
s4_t ByteBuffer::s4(byte_t *ptr){
   u4s4f4_u u4s4f4;
   u4s4f4.u4 = u4(ptr);
   //      fprintf(stderr, "u4 %04x\n", u4);
   return (u4s4f4.s4);
}
f4_t ByteBuffer::f4(byte_t *ptr){
   u4s4f4_u u4s4f4;
   u4s4f4.u4 = u4(ptr);
   //      fprintf(stderr, "u4 %04x\n", u4);
   return (u4s4f4.f4);
}
u8_t ByteBuffer::u8(byte_t *ptr){
   u8_t u8 = (((u8_t)u4(ptr))<<32)|u4(ptr+4);
   //      fprintf(stderr, "u8 %08lx\n", u8);
   return (u8);
}
s8_t ByteBuffer::s8(byte_t *ptr){
   u8s8f8_u u8s8f8;
   u8s8f8.u8 = u8(ptr);
   //      fprintf(stderr, "u8 %08x\n", u8);
   return (u8s8f8.s8);
}
f8_t ByteBuffer::f8(byte_t *ptr){
   u8s8f8_u u8s8f8;
   u8s8f8.u8 = u8(ptr);
   //      fprintf(stderr, "u8 %08x\n", u8);
   return (u8s8f8.f8);
}
ByteBuffer::ByteBuffer(byte_t *_bytes, size_t _len)
   : len(_len), bytes(new byte_t[len]), ptr(bytes){
      memcpy((void*)bytes, (void*)_bytes, len);
   }
ByteBuffer::~ByteBuffer(){
   delete bytes;
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

byte_t *ByteBuffer::getBytes(int _len){
   byte_t *buf = NULL;
   if (_len > 0){
      buf = new byte_t[_len];
   }
   memcpy((void*)buf, (void*)ptr, _len);
   ptr+=_len;
   return(buf); 
}


ConstantPoolEntry::ConstantPoolEntry(ByteBuffer *_byteBuffer, u4_t _slot, ConstantPoolType _constantPoolType)
   : slot(_slot), constantPoolType(_constantPoolType) {
   }

ConstantPoolType ConstantPoolEntry::getConstantPoolType() {
   return (constantPoolType);
}

u4_t ConstantPoolEntry::getSlot() {
   return (slot);
}

EmptyConstantPoolEntry::EmptyConstantPoolEntry(ByteBuffer *_byteBuffer, u4_t _slot)
   :  ConstantPoolEntry(_byteBuffer, _slot, EMPTY) {
   }

UTF8ConstantPoolEntry::UTF8ConstantPoolEntry(ByteBuffer *_byteBuffer, u4_t _slot) 
   : ConstantPoolEntry(_byteBuffer, _slot, UTF8) {
      len = (size_t)_byteBuffer->u2();
      utf8Bytes = _byteBuffer->getBytes(len);
   }
size_t UTF8ConstantPoolEntry::getLen() {
   return(len);
}
byte_t *UTF8ConstantPoolEntry::getUTF8Bytes() {
   return(utf8Bytes);
}
void UTF8ConstantPoolEntry::write(FILE *file){
   fprintf(file, "len %d \"", (int)len);
   if (len != 0 && utf8Bytes != NULL){
      for (unsigned int i=0; i<len; i++){
         fprintf(file, "%c", utf8Bytes[i]);
      }
   }
   fprintf(file, "\"");
}

IntegerConstantPoolEntry::IntegerConstantPoolEntry(ByteBuffer *_byteBuffer, u4_t _slot)
   :  ConstantPoolEntry(_byteBuffer, _slot, INTEGER) {
      value = _byteBuffer->s4();
   }
s4_t IntegerConstantPoolEntry::getValue(){
   return(value);
}

FloatConstantPoolEntry::FloatConstantPoolEntry(ByteBuffer *_byteBuffer, u4_t  _slot)
   :  ConstantPoolEntry(_byteBuffer, _slot, FLOAT) {
      value = _byteBuffer->f4();
   }
f4_t FloatConstantPoolEntry::getValue(){
   return(value);
}

DoubleConstantPoolEntry::DoubleConstantPoolEntry(ByteBuffer *_byteBuffer, u4_t _slot)
   :  ConstantPoolEntry(_byteBuffer, _slot, DOUBLE) {
      value = _byteBuffer->f8();
   }
f8_t DoubleConstantPoolEntry::getValue(){
   return(value);
}

LongConstantPoolEntry::LongConstantPoolEntry(ByteBuffer *_byteBuffer, u4_t _slot)
   :  ConstantPoolEntry(_byteBuffer, _slot, LONG) {
      value = _byteBuffer->s8();
   }
s8_t LongConstantPoolEntry::getValue(){
   return(value);
}

ClassConstantPoolEntry::ClassConstantPoolEntry(ByteBuffer *_byteBuffer, u4_t _slot)
   :  ConstantPoolEntry(_byteBuffer, _slot, CLASS) {
      nameIndex = _byteBuffer->u2();
   }
u2_t ClassConstantPoolEntry::getNameIndex(){
   return(nameIndex);
}


ReferenceConstantPoolEntry::ReferenceConstantPoolEntry(ByteBuffer *_byteBuffer, u4_t _slot, ConstantPoolType _constantPoolType)
   :  ConstantPoolEntry(_byteBuffer, _slot, _constantPoolType) {
      referenceClassIndex = _byteBuffer->u2();
      nameAndTypeIndex = _byteBuffer->u2();
   }
u2_t ReferenceConstantPoolEntry::getReferenceClassIndex(){
   return(referenceClassIndex);
}
u2_t ReferenceConstantPoolEntry::getNameAndTypeIndex(){
   return(nameAndTypeIndex);
}

FieldConstantPoolEntry::FieldConstantPoolEntry(ByteBuffer *_byteBuffer, u4_t _slot)
   :  ReferenceConstantPoolEntry(_byteBuffer, _slot, FIELD) {
   }

MethodReferenceConstantPoolEntry::MethodReferenceConstantPoolEntry(ByteBuffer *_byteBuffer, u4_t _slot, ConstantPoolType _constantPoolType)
   :  ReferenceConstantPoolEntry(_byteBuffer, _slot, _constantPoolType) {
   }

MethodConstantPoolEntry::MethodConstantPoolEntry(ByteBuffer *_byteBuffer, u4_t _slot)
   :  MethodReferenceConstantPoolEntry(_byteBuffer, _slot, METHOD) {
   }

InterfaceMethodConstantPoolEntry::InterfaceMethodConstantPoolEntry(ByteBuffer *_byteBuffer, u4_t _slot)
   :  MethodReferenceConstantPoolEntry(_byteBuffer, _slot, INTERFACEMETHOD) {
   }

NameAndTypeConstantPoolEntry::NameAndTypeConstantPoolEntry(ByteBuffer *_byteBuffer, u4_t _slot)
   :  ConstantPoolEntry(_byteBuffer, _slot, NAMEANDTYPE) {
      descriptorIndex = _byteBuffer->u2();
      nameIndex = _byteBuffer->u2();
   }
u2_t NameAndTypeConstantPoolEntry::getDescriptorIndex(){
   return(descriptorIndex);
}
u2_t NameAndTypeConstantPoolEntry::getNameIndex(){
   return(nameIndex);
}


MethodTypeConstantPoolEntry::MethodTypeConstantPoolEntry(ByteBuffer *_byteBuffer, u4_t _slot)
   :  ConstantPoolEntry(_byteBuffer, _slot, METHODTYPE) {
      descriptorIndex = _byteBuffer->u2();
   }
u2_t MethodTypeConstantPoolEntry::getDescriptorIndex(){
   return(descriptorIndex);
}

MethodHandleConstantPoolEntry::MethodHandleConstantPoolEntry(ByteBuffer *_byteBuffer, u4_t _slot)
   :  ConstantPoolEntry(_byteBuffer, _slot, METHODHANDLE) {
      referenceKind = _byteBuffer->u1();
      referenceIndex = _byteBuffer->u2();
   }
u1_t MethodHandleConstantPoolEntry::getReferenceKind(){
   return(referenceKind);
}
u2_t MethodHandleConstantPoolEntry::getReferenceIndex(){
   return(referenceIndex);
}

StringConstantPoolEntry::StringConstantPoolEntry(ByteBuffer *_byteBuffer, u4_t _slot)
   :  ConstantPoolEntry(_byteBuffer, _slot, STRING) {
      utf8Index = _byteBuffer->u2();
   }
u2_t StringConstantPoolEntry::getUtf8Index(){
   return(utf8Index);
}

InvokeDynamicConstantPoolEntry::InvokeDynamicConstantPoolEntry(ByteBuffer *_byteBuffer, u4_t _slot)
   :  ConstantPoolEntry(_byteBuffer, _slot, INVOKEDYNAMIC) {
      bootstrapMethodAttrIndex = _byteBuffer->u2();
      nameAndTypeIndex = _byteBuffer->u2();
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

LineNumberTableAttribute::LineNumberTableAttribute(ByteBuffer *_byteBuffer, ConstantPoolEntry **_constantPool){
   line_number_table_length = _byteBuffer->u2();
   lineNumberTable = new LineNumberTableEntry *[line_number_table_length];
#ifdef SHOW
   fprintf(stdout, "%d line numbers", line_number_table_length);
#endif
   for (u2_t i =0; i< line_number_table_length; i++){
      lineNumberTable[i] = new LineNumberTableEntry(_byteBuffer, _constantPool);
   }
}

LocalVariableTableAttribute::LocalVariableTableEntry::LocalVariableTableEntry(ByteBuffer *_byteBuffer, ConstantPoolEntry **_constantPool){
   start_pc = _byteBuffer->u2();
   length = _byteBuffer->u2();
   name_index = _byteBuffer->u2();
   descriptor_index = _byteBuffer->u2();
   index = _byteBuffer->u2();
}

LocalVariableTableAttribute::LocalVariableTableAttribute(ByteBuffer *_byteBuffer, ConstantPoolEntry **_constantPool){
   local_variable_table_length = _byteBuffer->u2();
   localVariableTable = new LocalVariableTableEntry *[local_variable_table_length];
#ifdef SHOW
   fprintf(stdout, "%d local variables", local_variable_table_length);
#endif
   for (u2_t i =0; i< local_variable_table_length; i++){
      localVariableTable[i] = new LocalVariableTableEntry(_byteBuffer, _constantPool);
   }
}

CodeAttribute::ExceptionTableEntry::ExceptionTableEntry(ByteBuffer *_byteBuffer, ConstantPoolEntry **_constantPool){
   start_pc = _byteBuffer->u2();
   end_pc = _byteBuffer->u2();
   handler_pc = _byteBuffer->u2();
   catch_type = _byteBuffer->u2();
}

CodeAttribute::CodeAttribute(ByteBuffer *_byteBuffer, ConstantPoolEntry **_constantPool){
   max_stack = _byteBuffer->u2();
   max_locals = _byteBuffer->u2();
   code_length = _byteBuffer->u4();
   code = _byteBuffer->getBytes(code_length);
#ifdef SHOW
   fprintf(stdout, "MaxStack %d, MaxLocals %d, CodeLength %d", max_stack, max_locals, code_length);
#endif
   exception_table_length = _byteBuffer->u2();
   exceptionTable = new ExceptionTableEntry *[exception_table_length];
   for (u2_t i =0; i< exception_table_length; i++){
      exceptionTable[i] = new ExceptionTableEntry(_byteBuffer, _constantPool);
   }
   attributes_count = _byteBuffer->u2();
   attributes = new AttributeInfo *[attributes_count];
   for (u2_t i=0; i< attributes_count; i++){
      attributes[i] = new AttributeInfo(_byteBuffer, _constantPool);
   }
}

AttributeInfo::AttributeInfo(ByteBuffer *_byteBuffer, ConstantPoolEntry **_constantPool){
   attribute_name_index = _byteBuffer->u2();
   UTF8ConstantPoolEntry *attributeName = (UTF8ConstantPoolEntry*)_constantPool[getAttributeNameIndex()];
   char *attributeNameChars = (char *)attributeName->getUTF8Bytes();
#ifdef SHOW
   fprintf(stdout, " [ATTR=\"%s\"{", attributeNameChars);
#endif
   attribute_length = _byteBuffer->u4();
   info = _byteBuffer->getBytes(attribute_length);
   if (!strcmp(attributeNameChars, "Code")){
      ByteBuffer *codeByteBuffer = new ByteBuffer(info, attribute_length);
      codeAttribute = new CodeAttribute(codeByteBuffer, _constantPool);
      attribute_type  = Code;
   } else if (!strcmp(attributeNameChars, "LineNumberTable")){
      ByteBuffer *lineNumberTableByteBuffer = new ByteBuffer(info, attribute_length);
      lineNumberTableAttribute = new LineNumberTableAttribute(lineNumberTableByteBuffer, _constantPool);
      attribute_type  = LineNumberTable;
   } else if (!strcmp(attributeNameChars, "LocalVariableTable")){
      ByteBuffer *localVariableTableByteBuffer = new ByteBuffer(info, attribute_length);
      localVariableTableAttribute = new LocalVariableTableAttribute(localVariableTableByteBuffer, _constantPool);
      attribute_type  = LocalVariableTable;
   }
#ifdef SHOW
   fprintf(stdout, " }] ", attributeName->getUTF8Bytes());
#endif
}
u2_t AttributeInfo::getAttributeNameIndex(){
   return(attribute_name_index);
}
AttributeType AttributeInfo::getAttributeType(){
   return(attribute_type);
}

FieldInfo::FieldInfo(ByteBuffer *_byteBuffer, ConstantPoolEntry **_constantPool){
   access_flags = _byteBuffer->u2();
   name_index = _byteBuffer->u2();
   descriptor_index = _byteBuffer->u2();
   attributes_count = _byteBuffer->u2();
   attributes = new AttributeInfo *[attributes_count];
   for (u2_t i=0; i< attributes_count; i++){
      attributes[i] = new AttributeInfo(_byteBuffer, _constantPool);
   }
#ifdef SHOW
   UTF8ConstantPoolEntry *fieldName = (UTF8ConstantPoolEntry*)_constantPool[getNameIndex()];
   fprintf(stdout, " field \"%s\"", fieldName->getUTF8Bytes());
   UTF8ConstantPoolEntry *fieldDescriptor = (UTF8ConstantPoolEntry*)_constantPool[getDescriptorIndex()];
   fprintf(stdout, " \"%s\"\n", fieldDescriptor->getUTF8Bytes());
#endif
}
u2_t FieldInfo::getNameIndex(){
   return(name_index);
}
u2_t FieldInfo::getDescriptorIndex(){
   return(descriptor_index);
}

MethodInfo::MethodInfo(ByteBuffer *_byteBuffer, ConstantPoolEntry **_constantPool){
   access_flags = _byteBuffer->u2();
   name_index = _byteBuffer->u2();
   descriptor_index = _byteBuffer->u2();
   attributes_count = _byteBuffer->u2();
   attributes = new AttributeInfo *[attributes_count];
   for (u2_t i=0; i< attributes_count; i++){
      attributes[i] = new AttributeInfo(_byteBuffer, _constantPool);
   }
#ifdef SHOW
   UTF8ConstantPoolEntry *methodName = (UTF8ConstantPoolEntry*)_constantPool[getNameIndex()];
   fprintf(stdout, " method \"%s\"", methodName->getUTF8Bytes());
   UTF8ConstantPoolEntry *methodDescriptor = (UTF8ConstantPoolEntry*)_constantPool[getDescriptorIndex()];
   fprintf(stdout, " \"%s\"\n", methodDescriptor->getUTF8Bytes());
#endif
}
u2_t MethodInfo::getNameIndex(){
   return(name_index);
}
u2_t MethodInfo::getDescriptorIndex(){
   return(descriptor_index);
}

bool isKernel(char *_className, ByteBuffer *_byteBuffer){
   bool isAKernel = false;
   unsigned int magic= _byteBuffer->u4();
   if (magic == 0xcafebabe){
#ifdef SHOW
      fprintf(stdout, "class name \"%s\"\n", _className);
#endif
      //fprintf(stdout, "magic = %04x\n", magic);
      u2_t minor= _byteBuffer->u2();
      u2_t major= _byteBuffer->u2();
      u2_t constantPoolSize = _byteBuffer->u2();
#ifdef SHOW
      fprintf(stdout, "constant pool size = %d\n", constantPoolSize);
#endif
      u4_t slot = 0;
      ConstantPoolEntry **constantPool=new ConstantPoolEntry *[constantPoolSize+1];
      constantPool[slot] = new EmptyConstantPoolEntry(_byteBuffer, slot);
      slot=1;

      while (slot < constantPoolSize){
         ConstantPoolType constantPoolType = (ConstantPoolType)_byteBuffer->u1();
         switch (constantPoolType){
            case UTF8: //1
                  constantPool[slot] = new UTF8ConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d UTF8 \"%s\"\n", slot, ((UTF8ConstantPoolEntry*)constantPool[slot])->getUTF8Bytes());
#endif
                  slot++;
               break;
            case INTEGER: //3
                  constantPool[slot] = new IntegerConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d INTEGER\n", slot);
#endif
                  slot++;
               break;
            case FLOAT: //4
                  constantPool[slot] = new FloatConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d FLOAT\n", slot);
#endif
                  slot++;
               break;
            case LONG: //5
                  constantPool[slot] = new LongConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d LONG\n", slot);
#endif
                  slot+=2;
               break;
            case DOUBLE: //6
                  constantPool[slot] = new DoubleConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d DOUBLE\n", slot);
#endif
                  slot+=2;
               break;
            case CLASS: //7
                  constantPool[slot] = new ClassConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d CLASS\n", slot);
#endif
                  slot+=1;
               break;
            case STRING: //8
                  constantPool[slot] = new StringConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d STRING\n", slot);
#endif
                  slot+=1;
               break;
            case FIELD: //9
                  constantPool[slot] = new FieldConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d FIELD\n", slot);
#endif
                  slot+=1;
               break;
            case METHOD: //10
                  constantPool[slot] = new MethodConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d METHOD\n", slot);
#endif
                  slot+=1;
               break;
            case INTERFACEMETHOD: //11
                  constantPool[slot] = new InterfaceMethodConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d INTERFACEMETHOD\n", slot);
#endif
                  slot+=1;
               break;
            case NAMEANDTYPE: //12
                  constantPool[slot] = new NameAndTypeConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d NAMEANDTYPE\n", slot);
#endif
                  slot+=1;
               break;
            case METHODHANDLE: //15
                  constantPool[slot] = new MethodHandleConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d METHODHANDLE\n", slot);
#endif
                  slot+=1;
               break;
            case METHODTYPE: //16
                  constantPool[slot] = new MethodTypeConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d METHODTYPE", slot);
#endif
                  slot+=1;
               break;
            case INVOKEDYNAMIC: //18
                  constantPool[slot] = new InvokeDynamicConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  SHOW fprintf(stdout, "slot %d INVOKEDYNAMIC\n", slot);
#endif
                  slot+=1;
               break;
            default: 
                  fprintf(stdout, "ERROR found UNKNOWN! %02x/%0d in slot %d\n", constantPoolType, constantPoolType, slot );
                  exit (1);
         }
      }

      // we have the constant pool 

      u2_t accessFlags = _byteBuffer->u2();
#ifdef SHOW
      fprintf(stdout, "access flags %04x\n", accessFlags);
#endif
      u2_t thisClassConstantPoolIndex = _byteBuffer->u2();
#ifdef SHOW
      fprintf(stdout, "this class constant pool index = %04x\n", thisClassConstantPoolIndex);
      ClassConstantPoolEntry *thisClassConstantPoolEntry = (ClassConstantPoolEntry*)constantPool[thisClassConstantPoolIndex];
      fprintf(stdout, "this class name constant pool index = %04x\n", thisClassConstantPoolEntry->getNameIndex());
      UTF8ConstantPoolEntry *thisClassUTF8ConstantPoolEntry = (UTF8ConstantPoolEntry*)constantPool[thisClassConstantPoolEntry->getNameIndex()];
      fprintf(stdout, "UTF8 at this class name index is \"%s\"\n", thisClassUTF8ConstantPoolEntry->getUTF8Bytes());
#endif
      u2_t superClassConstantPoolIndex = _byteBuffer->u2();
      ClassConstantPoolEntry *superClassConstantPoolEntry = (ClassConstantPoolEntry*)constantPool[superClassConstantPoolIndex];
      UTF8ConstantPoolEntry *superClassUTF8ConstantPoolEntry = (UTF8ConstantPoolEntry*)constantPool[superClassConstantPoolEntry->getNameIndex()];

      isAKernel= !strcmp((char *)(superClassUTF8ConstantPoolEntry->getUTF8Bytes()),"com/amd/aparapi/Kernel");

#ifdef SHOW
      fprintf(stdout, "Class name at super index is \"%s\"\n", superClassUTF8ConstantPoolEntry->getUTF8Bytes());
#endif
      u2_t interface_count = _byteBuffer->u2();
#ifdef SHOW
      fprintf(stdout, "This class implements %d interfaces\n", interface_count);
#endif
      u2_t *interfaces  = new u2_t[interface_count];
      for (u2_t i=0; i< interface_count; i++){
         interfaces[i] = _byteBuffer->u2();
      }
      u2_t field_count = _byteBuffer->u2();
#ifdef SHOW
      fprintf(stdout, "This class has  %d fields\n", field_count);
#endif
      FieldInfo **fields  = new FieldInfo*[field_count];
      for (u2_t i=0; i< field_count; i++){
         fields[i] = new FieldInfo(_byteBuffer, constantPool);
      }
      u2_t method_count = _byteBuffer->u2();
#ifdef SHOW
      fprintf(stdout, "This class has  %d methods\n", method_count);
#endif
      MethodInfo **methods  = new MethodInfo*[method_count];
      for (u2_t i=0; i< method_count; i++){
         methods[i] = new MethodInfo(_byteBuffer, constantPool);
      }
      u2_t attributes_count = _byteBuffer->u2();
#ifdef SHOW
      fprintf(stdout, "This class has  %d attributes\n", attributes_count);
#endif
      AttributeInfo **attributes = new AttributeInfo *[attributes_count];
      for (u2_t i=0; i< attributes_count; i++){
         attributes[i] = new AttributeInfo(_byteBuffer, constantPool);
      }
#ifdef SHOW
      fprintf(stdout, "\n");
#endif
   }
   return(isAKernel);
}

