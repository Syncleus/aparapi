#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include <jvmti.h>

#include "jniHelper.h"
#include "classtools.h"
#include "com_amd_aparapi_OpenCLJNI.h"

//jvmtiEnv     *jvmti;
//JavaVM       *jvm;

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
         delete[] name;
         delete[] byteBuffer;
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
JNI_JAVA(jbyteArray, OpenCLJNI, getBytes)
   (JNIEnv *jenv, jobject instance, jstring className){
      jbyteArray bytes = NULL;
      const char *nameChars = jenv->GetStringUTFChars(className, NULL);
      //fprintf(stdout, "inside getBytes(\"%s\")\n", nameChars);
      for (NameToBytes *ptr = head; ptr != NULL; ptr=(NameToBytes *)ptr->getNext()){

         //ClassInfo classInfo(ptr->getByteBuffer());  don't uncomment this.  It cauese segv
         // char *superClassName = classInfo.getSuperClassName();
         // if (!strcmp(superClassName,"com/amd/aparapi/Kernel")){
         //    fprintf(stdout, "%s is a kernel!\n", ptr->getName()); 
         // }
         //fprintf(stdout, "testing \"%s\"==\"%s\"   ", nameChars, ptr->getName());
         //fflush(stdout);
         //fprintf(stdout, "classinfo name  \"%s\"\n", classInfo.getClassName());
         //fflush(stdout);
         if (!strcmp(ptr->getName(), nameChars)){
            //fprintf(stderr, "found bytes for \"%s\"\n", nameChars);
            ByteBuffer *byteBuffer = ptr->getByteBuffer();
            bytes = jenv->NewByteArray(byteBuffer->getLen());
            //fprintf(stdout, "created byte array size= %d\n", ptr->getLen());
            jenv->SetByteArrayRegion(bytes, (jint)0, (jint)byteBuffer->getLen() , (jbyte*)byteBuffer->getBytes());
            break;
         }
      }
      if (bytes == NULL){
         fprintf(stdout, "failed to find bytes for \"%s\"\n", nameChars);
         bytes = jenv->NewByteArray(0);
      }
      jenv->ReleaseStringUTFChars(className, nameChars);
      return (bytes);
   }

void dumpClassAscii( jint class_data_len, const unsigned char* class_data){
   fprintf(stdout, "\n      ");
   for (int i=0; i<128; i++){
      fprintf(stdout, "%x",i/16);
   }
   fprintf(stdout, "\n      ");
   for (int i=0; i<128; i++){
      fprintf(stdout, "%x",i%16);
   }
   for (int i=0; i<class_data_len; i++){
      if (i==0 || (i%128)==0){
         fprintf(stdout, "\n %04x ", i);
      }
      if (   (class_data[i]>='a' && class_data[i]<='z')
          || (class_data[i]>='A' && class_data[i]<='Z')
          || (class_data[i]>='0' && class_data[i]<='9')
          || (class_data[i]=='(')
          || (class_data[i]==')')
          || (class_data[i]=='<')
          || (class_data[i]=='>')
          || (class_data[i]==';')
          || (class_data[i]=='.')
          || (class_data[i]=='/')
          || (class_data[i]=='[')
          || (class_data[i]=='$')
          || (class_data[i]==' ')){
         fprintf(stdout, "%c", class_data[i]);
      }else if ( class_data[i]=='\0'){
         fprintf(stdout, "|");
      }else{
         fprintf(stdout, ".");
      }
   }
   fprintf(stdout, "\n");
}

static void JNICALL cbClassFileLoadHook(jvmtiEnv *jvmti_env, JNIEnv* jni_env,
      jclass class_being_redefined,
      jobject loader,
      const char* name,
      jobject protection_domain,
      jint class_data_len,
      const unsigned char* class_data,
      jint* new_class_data_len,
      unsigned char** new_class_data){
   if (name == NULL){
      //fprintf(stdout, "JVMTI_EVENT_CLASS_FILE_LOAD agent was NULL %d \n", class_data_len);
      //dumpClassAscii(class_data_len, class_data);
      int len=(class_data[11]*256)+class_data[12];
      unsigned char *nameFromClass = new unsigned char[len+1];
      memcpy((void*)nameFromClass, (void*)(class_data+13), (size_t)len);
      nameFromClass[len] = '\0';
      //fprintf(stdout, "JVMTI_EVENT_CLASS_FILE_LOAD_HOOK name was null but agent got name from class %s %d %lx %lx\n", nameFromClass, class_data_len, jvmti_env, jni_env);
      byte_t *buf = new byte_t[class_data_len];
      memcpy((void*)buf, (void*)class_data, (size_t)class_data_len);
      ByteBuffer *byteBuffer = new ByteBuffer(buf, (size_t)class_data_len);
      head = new NameToBytes(head, (char *)nameFromClass, byteBuffer);
      delete[] nameFromClass;
   }else{
      //fprintf(stdout, "JVMTI_EVENT_CLASS_FILE_LOAD_HOOK agent got %s %d %lx %lx\n", name, class_data_len, jvmti_env, jni_env);
      byte_t *buf = new byte_t[class_data_len];

      memcpy((void*)buf, (void*)class_data, (size_t)class_data_len);
      ByteBuffer *byteBuffer = new ByteBuffer(buf, (size_t)class_data_len);
      head = new NameToBytes(head, (char *)name, byteBuffer);
   }

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
   jvmtiEnv     *jvmti;
   JavaVM       *jvm;

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

