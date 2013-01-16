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

class ByteBuffer; // forward reference

class ConstantPoolEntry{
  private:
    ConstantPoolType constantPoolType;
    int slot;
  public:
    ConstantPoolEntry(ByteBuffer *_byteBuffer, int _slot, ConstantPoolType _constantPoolType)
      : slot(_slot), constantPoolType(_constantPoolType)
    {
    }

    ConstantPoolType getConstantPoolType() {
      return (constantPoolType);
    }

    int getSlot() {
      return (slot);
    }
};

class EmptyConstantPoolEntry : public ConstantPoolEntry{
  public:
    EmptyConstantPoolEntry(ByteBuffer *_byteBuffer, int _slot)
      :  ConstantPoolEntry(_byteBuffer, _slot, EMPTY)
    {
    }
};

class UTF8ConstantPoolEntry: public ConstantPoolEntry{
  private:
    jstring utf8;
  public:
    UTF8ConstantPoolEntry(ByteBuffer *_byteBuffer, int _slot) 
      : ConstantPoolEntry(_byteBuffer, _slot, UTF8)
    {
      //utf8 = _byteReader.utf8();
    }
    jstring getUTF8() {
      return (utf8);
    }
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

    unsigned int u1(jbyte *ptr){
      int u1 = (unsigned int) (*ptr & 0xff);
 //     fprintf(stderr, "u1 %01x\n", u1);
      return (u1);
    }
    unsigned int u2(jbyte *ptr){
      unsigned int u2 = (u1(ptr)<<8)|u1(ptr+1);
 //     fprintf(stderr, "u2 %02x\n", u2);
      return (u2);
    }
    unsigned int u4(jbyte *ptr){
      unsigned int u4 = (u2(ptr)<<16)|u2(ptr+2);
//      fprintf(stderr, "u4 %04x\n", u4);
      return (u4);
    }
    unsigned long u8(jbyte *ptr){
      unsigned long u8 = (((unsigned long)u4(ptr))<<32)|u4(ptr+4);
//      fprintf(stderr, "u8 %08lx\n", u8);
      return (u8);
    }

    bool isKernel(){
      unsigned int magic= u4(ptr); ptr+=4;
      if (magic == 0xcafebabe){
//        fprintf(stdout, "magic = %04x\n", magic);
        unsigned int minor= u2(ptr); ptr+=2;
        unsigned int major= u2(ptr); ptr+=2;
        unsigned int constantPoolSize = u2(ptr); ptr+=2;
//        fprintf(stdout, "constant pool size = %d\n", constantPoolSize);
        unsigned int constantPoolIdx = 0;
        if (0){
          ConstantPoolEntry **entries=new ConstantPoolEntry *[constantPoolSize];
          while (constantPoolIdx < constantPoolSize){
            ConstantPoolType constantPoolType = (ConstantPoolType)u1(ptr); ptr++;
            switch (constantPoolType){
              case EMPTY: //0
                {
                  entries[constantPoolIdx] = new EmptyConstantPoolEntry(this, constantPoolIdx);
                  constantPoolIdx++;
                }
                break;
              case UTF8: //1
                {
                  entries[constantPoolIdx] = new UTF8ConstantPoolEntry(this, constantPoolIdx);
                  constantPoolIdx++;
                }
                break;
              case UNICODE: //2
                {
                }
                break;
              case INTEGER: //3
                {
                }
                break;
              case FLOAT: //4
                {
                }
                break;
              case LONG: //5
                {
                }
                break;
              case DOUBLE: //6
                {
                }
                break;
              case CLASS: //7
                {
                }
                break;
              case STRING: //8
                {
                }
                break;
              case FIELD: //9
                {
                }
                break;
              case METHOD: //10
                {
                }
                break;
              case INTERFACEMETHOD: //11
                {
                }
                break;
              case NAMEANDTYPE: //12
                {
                }
                break;
              case UNUSED13:
                {
                }
                break;
              case UNUSED14:
                {
                }
                break;
              case METHODHANDLE: //15
                {
                }
                break;
              case METHODTYPE: //16
                {
                }
                break;
              case UNUSED17:
                {
                }
                break;
              case INVOKEDYNAMIC: //18
                {
                }
                break;
            }
          }
        }
      }
      return(true);
    }
};

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
  if (byteBuffer->isKernel()){
    head =  new NameToBytes(head, (char*)name, byteBuffer);
  }
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

