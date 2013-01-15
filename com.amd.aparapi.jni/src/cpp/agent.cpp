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

class NameToBytes{
  private:
    const char *name;
    const jint len;
    const unsigned char *bytes;
    const NameToBytes* next;
  public:
    NameToBytes(const NameToBytes *_head, const char *_name, const jint _len, const unsigned char *_bytes) : len(_len) {
      int nameLen = strlen(_name)+1;
      name = new char [nameLen];
      memcpy((void*)name, (void*)_name, nameLen);
      for (char *ptr = (char *)name; *ptr; ptr++){
         if (*ptr == '/'){
            *ptr = '.';
         }
      }

    //  name = (const char*)strdup(_name);
      bytes = new unsigned char[len];
      memcpy((void*)bytes, (void*)_bytes, len);
      next = _head;
    }
    ~NameToBytes(){
      delete name;
      delete bytes;
    }
    const unsigned char *getBytes(){
       return(bytes);
    }
    const char *getName(){
       return(name);
    }
    const jint getLen(){
       return(len);
    }
    const NameToBytes *getNext(){
       return(next);
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
           bytes = jenv->NewByteArray(ptr->getLen());
           //fprintf(stdout, "created byte array size= %d\n", ptr->getLen());
           jenv->SetByteArrayRegion(bytes, (jint)0, (jint)ptr->getLen() , (jbyte*)ptr->getBytes());
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
   head =  new NameToBytes(head, name, class_data_len, class_data);
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

