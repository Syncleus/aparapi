#include "OpenCLArgDescriptor.h"
#include "JavaArgs.h"

void OpenCLArgDescriptor::describeBits(JNIEnv *jenv, jlong bits){
   fprintf(stderr, " %lx ", (unsigned long)bits);
   if (argisset(bits, READONLY)){
      fprintf(stderr, "readonly ");
   }
   if (argisset(bits, WRITEONLY)){
      fprintf(stderr, "writeonly ");
   }
   if (argisset(bits, READWRITE)){
      fprintf(stderr, "readwrite ");
   }
   if (argisset(bits, ARRAY)){
      fprintf(stderr, "array ");
   }
   if (argisset(bits, PRIMITIVE)){
      fprintf(stderr, "primitive ");
   }
   if (argisset(bits, FLOAT)){
      fprintf(stderr, "float ");
   }
   if (argisset(bits, BYTE)){
      fprintf(stderr, "byte ");
   }
   if (argisset(bits, SHORT)){
      fprintf(stderr, "short ");
   }
   if (argisset(bits, LONG)){
      fprintf(stderr, "long ");
   }
   if (argisset(bits, DOUBLE)){
      fprintf(stderr, "double ");
   }
   if (argisset(bits, INT)){
      fprintf(stderr, "int ");
   }
   if (argisset(bits, GLOBAL)){
      fprintf(stderr, "global ");
   }
   if (argisset(bits, LOCAL)){
      fprintf(stderr, "local ");
   }
   if (argisset(bits, ISARG)){
      fprintf(stderr, "isarg ");
   }
}


