#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include <jvmti.h>

jvmtiEnv     *jvmti;
JavaVM       *jvm;

static void JNICALL vmInit(jvmtiEnv *_jvmtiEnv, JNIEnv* _jniEnv, jthread thread) {
   fprintf(stdout, "from agent vmInit()\n");
   /*
      if (_jniEnv->ExceptionOccurred()) {
      fprintf(stdout, "Exception raised\n");
      _jniEnv->ExceptionDescribe();
      _jniEnv->ExceptionClear();
      }
      jclass classId = _jniEnv->FindClass("C");
      classId = _jniEnv->FindClass("java/lang/String");
      if (_jniEnv->ExceptionOccurred()) {
      fprintf(stdout, "Exception raised\n");
      _jniEnv->ExceptionDescribe();
      _jniEnv->ExceptionClear();
      }
    */
}

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
   INVOKEDYNAMIC //18
};

class ByteBuffer{
   private:
      jint len;
      jbyte *bytes;
      jbyte *ptr;
   public:
      ByteBuffer(jbyte *_bytes, jint _len)
         : len(_len), bytes(new jbyte[len]), ptr(bytes){
            memcpy((void*)bytes, (void*)_bytes, len);
         }
      ~ByteBuffer(){
         delete bytes;
      }
      jbyte *getBytes(){
         return(bytes);
      }
      jint getLen(){
         return(len);
      }
   private:
      unsigned int u1(jbyte *ptr){
         unsigned int u1 = (unsigned int) (*ptr & 0xff);
         //     fprintf(stderr, "u1 %01x\n", u1);
         return (u1);
      }
   public:
      unsigned int u1(){
         unsigned int value = u1(ptr); ptr+=1;
         return (value);
      }
   private:
      unsigned int u2(jbyte *ptr){
         unsigned int u2 = (u1(ptr)<<8)|u1(ptr+1);
         //     fprintf(stderr, "u2 %02x\n", u2);
         return (u2);
      }
   public:
      unsigned int u2(){
         unsigned int value = u2(ptr); ptr+=2;
         return (value);
      }
   private:
      unsigned int u4(jbyte *ptr){
         unsigned int u4 = (u2(ptr)<<16)|u2(ptr+2);
         //      fprintf(stderr, "u4 %04x\n", u4);
         return (u4);
      }
   public:
      unsigned int u4(){
         unsigned int value = u4(ptr); ptr+=4;
         return (value);
      }
      union u4s4f4_u {
         unsigned int u4;
         unsigned int s4;
         float f4;
      };
   private:
      signed int s4(jbyte *ptr){
         u4s4f4_u u4s4f4;
         u4s4f4.u4 = u4(ptr);
         //      fprintf(stderr, "u4 %04x\n", u4);
         return (u4s4f4.s4);
      }
      float f4(jbyte *ptr){
         u4s4f4_u u4s4f4;
         u4s4f4.u4 = u4(ptr);
         //      fprintf(stderr, "u4 %04x\n", u4);
         return (u4s4f4.f4);
      }
   public:
      float f4(){
         float value = f4(ptr); ptr+=4;
         return (value);
      }
      signed int s4(){
         signed int value = s4(ptr); ptr+=4;
         return (value);
      }
   private:
      unsigned long u8(jbyte *ptr){
         unsigned long u8 = (((unsigned long)u4(ptr))<<32)|u4(ptr+4);
         //      fprintf(stderr, "u8 %08lx\n", u8);
         return (u8);
      }
   public:
      unsigned long u8(){
         unsigned long value = u8(ptr); ptr+=8;
         return (value);
      }
   private:
      union u8s8f8_u {
         unsigned long u8;
         signed long s8;
         double d8;
      };
      signed long s8(jbyte *ptr){
         u8s8f8_u u8s8f8;
         u8s8f8.u8 = u8(ptr);
         //      fprintf(stderr, "u8 %08x\n", u8);
         return (u8s8f8.s8);
      }
      double d8(jbyte *ptr){
         u8s8f8_u u8s8f8;
         u8s8f8.u8 = u8(ptr);
         //      fprintf(stderr, "u8 %08x\n", u8);
         return (u8s8f8.d8);
      }
   public:
      double d8(){
         double value = d8(ptr); ptr+=8;
         return (value);
      }
      signed long s8(){
         signed long value = s8(ptr); ptr+=8;
         return (value);
      }

   public:
      jbyte *getBytes(int _len){
         jbyte *buf = new jbyte[_len+1];
         memcpy((void*)buf, (void*)ptr, _len);
         buf[len]='\0';
         ptr+=_len;
         return(buf); 
      }

      bool isKernel();
};

class ConstantPoolEntry{
   private:
      ConstantPoolType constantPoolType;
      unsigned int slot;
   public:
      ConstantPoolEntry(ByteBuffer *_byteBuffer, unsigned int _slot, ConstantPoolType _constantPoolType)
         : slot(_slot), constantPoolType(_constantPoolType) {
         }

      ConstantPoolType getConstantPoolType() {
         return (constantPoolType);
      }

      unsigned int getSlot() {
         return (slot);
      }
};

class EmptyConstantPoolEntry : public ConstantPoolEntry{
   public:
      EmptyConstantPoolEntry(ByteBuffer *_byteBuffer, unsigned int _slot)
         :  ConstantPoolEntry(_byteBuffer, _slot, EMPTY) {
         }
};

class UTF8ConstantPoolEntry: public ConstantPoolEntry{
   private:
      jint len; 
      jbyte *utf8Bytes;
   public:
      UTF8ConstantPoolEntry(ByteBuffer *_byteBuffer, unsigned int _slot) 
         : ConstantPoolEntry(_byteBuffer, _slot, UTF8) {
            len = _byteBuffer->u2();
            if (len == 0){
               utf8Bytes = NULL;
            }else{
               utf8Bytes = _byteBuffer->getBytes(len);
            }
         }
      jint getLen() {
         return(len);
      }
      jbyte *getUTF8Bytes() {
         return(utf8Bytes);
      }
      void write(FILE *file){
         fprintf(file, "len %d \"", len);
         if (len != 0 && utf8Bytes != NULL){
            for (unsigned int i=0; i<len; i++){
               fprintf(file, "%c", utf8Bytes[i]);
            }
         }
         fprintf(file, "\"");
      }
};

class IntegerConstantPoolEntry : public ConstantPoolEntry{
   private:
      signed int value;
   public:
      IntegerConstantPoolEntry(ByteBuffer *_byteBuffer, unsigned int _slot)
         :  ConstantPoolEntry(_byteBuffer, _slot, INTEGER) {
            value = _byteBuffer->s4();
         }
      signed int getValue(){
         return(value);
      }
};

class FloatConstantPoolEntry : public ConstantPoolEntry{
   private:
      float value;
   public:
      FloatConstantPoolEntry(ByteBuffer *_byteBuffer, unsigned int _slot)
         :  ConstantPoolEntry(_byteBuffer, _slot, FLOAT) {
            value = _byteBuffer->f4();
         }
      float getValue(){
         return(value);
      }
};

class DoubleConstantPoolEntry : public ConstantPoolEntry{
   private:
      double value;
   public:
      DoubleConstantPoolEntry(ByteBuffer *_byteBuffer, unsigned int _slot)
         :  ConstantPoolEntry(_byteBuffer, _slot, DOUBLE) {
            value = _byteBuffer->d8();
         }
      double getValue(){
         return(value);
      }
};

class LongConstantPoolEntry : public ConstantPoolEntry{
   private:
      signed long value;
   public:
      LongConstantPoolEntry(ByteBuffer *_byteBuffer, unsigned int _slot)
         :  ConstantPoolEntry(_byteBuffer, _slot, LONG) {
            value = _byteBuffer->s8();
         }
      signed long getValue(){
         return(value);
      }
};

class ClassConstantPoolEntry : public ConstantPoolEntry{
   private:
      unsigned int nameIndex;
   public:
      ClassConstantPoolEntry(ByteBuffer *_byteBuffer, unsigned int _slot)
         :  ConstantPoolEntry(_byteBuffer, _slot, CLASS) {
            nameIndex = _byteBuffer->u2();
         }
      unsigned int getNameIndex(){
         return(nameIndex);
      }
};


class ReferenceConstantPoolEntry : public ConstantPoolEntry{
   protected:
      unsigned int referenceClassIndex;

      unsigned int nameAndTypeIndex;

   public:
      ReferenceConstantPoolEntry(ByteBuffer *_byteBuffer, unsigned int _slot, ConstantPoolType _constantPoolType)
         :  ConstantPoolEntry(_byteBuffer, _slot, _constantPoolType) {
            referenceClassIndex = _byteBuffer->u2();
            nameAndTypeIndex = _byteBuffer->u2();
         }
      unsigned int getReferenceClassIndex(){
         return(referenceClassIndex);
      }
      unsigned int getNameAndTypeIndex(){
         return(nameAndTypeIndex);
      }
};

class FieldConstantPoolEntry : public ReferenceConstantPoolEntry{
   public:
      FieldConstantPoolEntry(ByteBuffer *_byteBuffer, unsigned int _slot)
         :  ReferenceConstantPoolEntry(_byteBuffer, _slot, FIELD) {
         }
};
class MethodReferenceConstantPoolEntry : public ReferenceConstantPoolEntry{
   public:
      MethodReferenceConstantPoolEntry(ByteBuffer *_byteBuffer, unsigned int _slot, ConstantPoolType _constantPoolType)
         :  ReferenceConstantPoolEntry(_byteBuffer, _slot, _constantPoolType) {
         }
};

class MethodConstantPoolEntry : public MethodReferenceConstantPoolEntry{
   public:
      MethodConstantPoolEntry(ByteBuffer *_byteBuffer, unsigned int _slot)
         :  MethodReferenceConstantPoolEntry(_byteBuffer, _slot, METHOD) {
         }
};

class InterfaceMethodConstantPoolEntry : public MethodReferenceConstantPoolEntry{
   public:
      InterfaceMethodConstantPoolEntry(ByteBuffer *_byteBuffer, unsigned int _slot)
         :  MethodReferenceConstantPoolEntry(_byteBuffer, _slot, INTERFACEMETHOD) {
         }
};

class NameAndTypeConstantPoolEntry : public ConstantPoolEntry{
   protected:
      unsigned int descriptorIndex;

      unsigned int nameIndex;

   public:
      NameAndTypeConstantPoolEntry(ByteBuffer *_byteBuffer, unsigned int _slot)
         :  ConstantPoolEntry(_byteBuffer, _slot, NAMEANDTYPE) {
            descriptorIndex = _byteBuffer->u2();
            nameIndex = _byteBuffer->u2();
         }
      unsigned int getDescriptorIndex(){
         return(descriptorIndex);
      }
      unsigned int getNameIndex(){
         return(nameIndex);
      }
};


class MethodTypeConstantPoolEntry : public ConstantPoolEntry{
   protected:
      unsigned int descriptorIndex;

   public:
      MethodTypeConstantPoolEntry(ByteBuffer *_byteBuffer, unsigned int _slot)
         :  ConstantPoolEntry(_byteBuffer, _slot, METHODTYPE) {
            descriptorIndex = _byteBuffer->u2();
         }
      unsigned int getDescriptorIndex(){
         return(descriptorIndex);
      }
};

class MethodHandleConstantPoolEntry : public ConstantPoolEntry{
   protected:
      unsigned int referenceKind;
      unsigned int referenceIndex;

   public:
      MethodHandleConstantPoolEntry(ByteBuffer *_byteBuffer, unsigned int _slot)
         :  ConstantPoolEntry(_byteBuffer, _slot, METHODHANDLE) {
            referenceKind = _byteBuffer->u1();
            referenceIndex = _byteBuffer->u2();
         }
      unsigned int getReferenceKind(){
         return(referenceKind);
      }
      unsigned int getReferenceIndex(){
         return(referenceIndex);
      }
};

class StringConstantPoolEntry : public ConstantPoolEntry{
   protected:
      unsigned int utf8Index;

   public:
      StringConstantPoolEntry(ByteBuffer *_byteBuffer, unsigned int _slot)
         :  ConstantPoolEntry(_byteBuffer, _slot, STRING) {
            utf8Index = _byteBuffer->u2();
         }
      unsigned int getUtf8Index(){
         return(utf8Index);
      }
};

class InvokeDynamicConstantPoolEntry : public ConstantPoolEntry{
   protected:
      unsigned int bootstrapMethodAttrIndex;
      unsigned int nameAndTypeIndex;

   public:
      InvokeDynamicConstantPoolEntry(ByteBuffer *_byteBuffer, unsigned int _slot)
         :  ConstantPoolEntry(_byteBuffer, _slot, INVOKEDYNAMIC) {
            bootstrapMethodAttrIndex = _byteBuffer->u2();
            nameAndTypeIndex = _byteBuffer->u2();
         }
      unsigned int getBootStrapMethodAttrIndex(){
         return(bootstrapMethodAttrIndex);
      }
      unsigned int getNameAndTypeIndex(){
         return(nameAndTypeIndex);
      }
};

#define SHOW

bool ByteBuffer::isKernel(){
   unsigned int magic= u4();
   if (magic == 0xcafebabe){
      //fprintf(stdout, "magic = %04x\n", magic);
      unsigned int minor= u2();
      unsigned int major= u2();
      unsigned int constantPoolSize = u2();
      fprintf(stdout, "constant pool size = %d\n", constantPoolSize);
      unsigned int slot = 0;
      ConstantPoolEntry **entries=new ConstantPoolEntry *[constantPoolSize+2];
      entries[slot] = new EmptyConstantPoolEntry(this, slot);
      slot=1;

      while (slot < constantPoolSize){
         ConstantPoolType constantPoolType = (ConstantPoolType)u1();
         switch (constantPoolType){
            case EMPTY: //0
               {
                  entries[slot] = new EmptyConstantPoolEntry(this, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d EMPTY\n", slot);
#endif
                  slot++;
               }
               break;
            case UTF8: //1
               {
                  entries[slot] = new UTF8ConstantPoolEntry(this, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d UTF8 ", slot);
                  ((UTF8ConstantPoolEntry*)entries[slot])->write(stdout);
                  fprintf(stdout, "\n");
#endif
                  slot++;
               }
               break;
            case UNICODE: //2
               {
                  fprintf(stdout, "ERROR found UNICODE in slot %d\n",slot);
                  exit(1);
               }
               break;
            case INTEGER: //3
               {
                  entries[slot] = new IntegerConstantPoolEntry(this, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d INTEGER\n", slot);
#endif
                  slot++;
               }
               break;
            case FLOAT: //4
               {
                  entries[slot] = new FloatConstantPoolEntry(this, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d FLOAT\n", slot);
#endif
                  slot++;
               }
               break;
            case LONG: //5
               {
                  entries[slot] = new LongConstantPoolEntry(this, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d LONG\n", slot);
#endif
                  slot+=2;
               }
               break;
            case DOUBLE: //6
               {
                  entries[slot] = new DoubleConstantPoolEntry(this, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d DOUBLE\n", slot);
#endif
                  slot+=2;
               }
               break;
            case CLASS: //7
               {
                  entries[slot] = new ClassConstantPoolEntry(this, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d CLASS\n", slot);
#endif
                  slot+=1;
               }
               break;
            case STRING: //8
               {
                  entries[slot] = new StringConstantPoolEntry(this, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d STRING\n", slot);
#endif
                  slot+=1;
               }
               break;
            case FIELD: //9
               {
                  entries[slot] = new FieldConstantPoolEntry(this, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d FIELD\n", slot);
#endif
                  slot+=1;
               }
               break;
            case METHOD: //10
               {
                  entries[slot] = new MethodConstantPoolEntry(this, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d METHOD\n", slot);
#endif
                  slot+=1;
               }
               break;
            case INTERFACEMETHOD: //11
               {
                  entries[slot] = new InterfaceMethodConstantPoolEntry(this, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d INTERFACEMETHOD\n", slot);
#endif
                  slot+=1;
               }
               break;
            case NAMEANDTYPE: //12
               {
                  entries[slot] = new NameAndTypeConstantPoolEntry(this, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d NAMEANDTYPE\n", slot);
#endif
                  slot+=1;
               }
               break;
            case UNUSED13:
               {
                  fprintf(stdout, "ERROR found UNUSED13 in slot %d\n",slot);
                  exit (1);
               }
               break;
            case UNUSED14:
               {
                  fprintf(stdout, "ERROR found UNUSED13 in slot %d\n",slot);
                  exit (1);
               }
               break;
            case METHODHANDLE: //15
               {
                  entries[slot] = new MethodHandleConstantPoolEntry(this, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d METHODHANDLE\n", slot);
#endif
                  slot+=1;
               }
               break;
            case METHODTYPE: //16
               {
                  entries[slot] = new MethodTypeConstantPoolEntry(this, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d METHODTYPE", slot);
#endif
                  slot+=1;
               }
               break;
            case UNUSED17:
               {
                  fprintf(stdout, "ERROR found UNUSED17 in slot %d\n",slot);
                  exit (1);
               }
               break;
            case INVOKEDYNAMIC: //18
               {
                  entries[slot] = new InvokeDynamicConstantPoolEntry(this, slot);
#ifdef SHOW
                  SHOW fprintf(stdout, "slot %d INVOKEDYNAMIC\n", slot);
#endif
                  slot+=1;
               }
               break;
            default: 
               {
                  fprintf(stdout, "ERROR found UNKNOWN! %02x/%0d in slot %d\n", constantPoolType, constantPoolType, slot );
                  exit (1);
               }
               break;
         }
      }

      // we have the constant pool 

      //unsigned int accessFlags = u2();
      //fprintf(stdout, "access flags %04x ", accessFlags);
      //unsigned int thisClassConstantPoolIndex = u2();
      //fprintf(stdout, "class constant pool index = %04x ", thisClassConstantPoolIndex);
      //ClassConstantPoolEntry *thisClassConstantPoolEntry = (ClassConstantPoolEntry*)entries[thisClassConstantPoolIndex];
      //fprintf(stdout, "class name constant pool index = %04x ", thisClassConstantPoolEntry->getNameIndex());
      //UTF8ConstantPoolEntry *thisClassUTF8ConstantPoolEntry = (UTF8ConstantPoolEntry*)entries[thisClassConstantPoolEntry->getNameIndex()];
      //fprintf(stdout, " class is \"");
      //thisClassUTF8ConstantPoolEntry->write(stdout);
      //fprintf(stdout, "\" and");
      //unsigned int superClassConstantPoolIndex = u2();
      //ClassConstantPoolEntry *superClassConstantPoolEntry = (ClassConstantPoolEntry*)entries[superClassConstantPoolIndex];
      //UTF8ConstantPoolEntry *superClassUTF8ConstantPoolEntry = (UTF8ConstantPoolEntry*)entries[superClassConstantPoolEntry->getNameIndex()];

      //fprintf(stdout, " super is \"");
      //superClassUTF8ConstantPoolEntry->write(stdout);
      //fprintf(stdout, "\"");
      fprintf(stdout, "\n");

   }
   return(true);
}

class NameToBytes{
   private:
      char *name;
      ByteBuffer *byteBuffer;
      NameToBytes* next;
   public:
      NameToBytes(NameToBytes *_head, char *_name, ByteBuffer *_byteBuffer)
         : byteBuffer(_byteBuffer) {
            int nameLen = strlen(_name)+1;
            name = new char [nameLen];
            memcpy((void*)name, (void*)_name, nameLen);
            for (char *ptr = (char *)name; *ptr; ptr++){
               if (*ptr == '/'){
                  *ptr = '.';
               }
            }
            next = _head;
         }
      ~NameToBytes(){
         delete name;
      }
      char *getName(){
         return(name);
      }
      NameToBytes *getNext(){
         return(next);
      }
      ByteBuffer *getByteBuffer(){
         return(byteBuffer);
      }
};

NameToBytes *head = NULL;

/*
 * Class:     com_amd_aparapi_OpenCLJNI
 * Method:    getClassBytes
 * Signature: (Ljava/lang/String;)V
 */
#ifdef __cplusplus
extern "C" {
#endif
   JNIEXPORT jbyteArray JNICALL Java_com_amd_aparapi_OpenCLJNI_getBytes (JNIEnv *jenv, jobject instance, jstring className){
      jbyteArray bytes = NULL;
      const char *nameChars = jenv->GetStringUTFChars(className, NULL);
      fprintf(stdout, "inside getBytes(\"%s\")\n", nameChars);
      for (NameToBytes *ptr = head; ptr != NULL; ptr=(NameToBytes *)ptr->getNext()){
         //if (ptr->getByteBuffer()->isKernel()){
         //   fprintf(stdout, "is a kernel!\n"); 
         //}
         //fprintf(stdout, "testing \"%s\"==\"%s\"\n", nameChars, ptr->getName());
         if (!strcmp(ptr->getName(), nameChars)){
            fprintf(stdout, "found bytes for \"%s\"\n", nameChars);
            ByteBuffer *byteBuffer = ptr->getByteBuffer();
            bytes = jenv->NewByteArray(byteBuffer->getLen());
            //fprintf(stdout, "created byte array size= %d\n", ptr->getLen());
            jenv->SetByteArrayRegion(bytes, (jint)0, byteBuffer->getLen() , byteBuffer->getBytes());
            break;
         }
      }
      if (bytes == NULL){
         fprintf(stdout, "failed to find bytes for \"%s\"\n", nameChars);
      }
      jenv->ReleaseStringUTFChars(className, nameChars);
      return (bytes);
   }
#ifdef __cplusplus
}
#endif





static void JNICALL cbClassFileLoadHook(jvmtiEnv *jvmti_env, JNIEnv* jni_env,
      jclass class_being_redefined,
      jobject loader,
      const char* name,
      jobject protection_domain,
      jint class_data_len,
      const unsigned char* class_data,
      jint* new_class_data_len,
      unsigned char** new_class_data){
   //   fprintf(stdout, "from agent classFileLoadHook(%s)\n", name);
   ByteBuffer *byteBuffer = new ByteBuffer((jbyte *)class_data, (jint)class_data_len);
   head = new NameToBytes(head, (char *)name, byteBuffer);
   //fprintf(stdout, "class \"%s\"  ", name); 
}


JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM *vm, char *options, void *reserved) {
   jint                returnValue = 0; // OK
   jint                rc;
   jvmtiError          err;
   jvmtiCapabilities   capabilities;
   jvmtiEventCallbacks callbacks;
   fprintf(stdout, "Agent_Onload()\n");

   // Get a handle on the JVM.
   jvm = vm;

   /* Get JVMTI environment */
   rc = vm->GetEnv((void **)&jvmti, JVMTI_VERSION);
   if (rc != JNI_OK) {
      fprintf(stderr, "ERROR: Unable to create jvmtiEnv, GetEnv failed, error=%d\n", rc);
      returnValue = -1;
   }else{
      /* Get/Add JVMTI capabilities */ 
      if ((err = jvmti->GetCapabilities(&capabilities) ) != JVMTI_ERROR_NONE) {
         fprintf(stderr, "ERROR: GetCapabilities failed, error=%d\n", err);
         returnValue = -1;
      }else{
         capabilities.can_tag_objects = 1;
         capabilities.can_generate_all_class_hook_events = 1;
         if ((err = jvmti->AddCapabilities(&capabilities))!= JVMTI_ERROR_NONE) {
            fprintf(stderr, "ERROR: AddCapabilities failed, error=%d\n", err);
            returnValue = -1;
         }else{
            /* Set callbacks and enable event notifications */
            memset(&callbacks, 0, sizeof(callbacks));
            callbacks.VMInit = &vmInit;
            callbacks.ClassFileLoadHook = &cbClassFileLoadHook;

            jvmti->SetEventCallbacks(&callbacks, sizeof(callbacks));
            jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_VM_INIT, NULL);
            jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_CLASS_FILE_LOAD_HOOK, NULL);
         }
      }
   }
   return returnValue;
}

/* Agent_OnUnload() is called last */
JNIEXPORT void JNICALL Agent_OnUnload(JavaVM *vm) {
   fprintf(stdout, "Agent_OnUnload()\n");
}

