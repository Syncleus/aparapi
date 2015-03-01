
#include <stdio.h>
#include <stdlib.h>

#ifndef __APPLE__
#include <malloc.h>
#endif

#include <sys/types.h>
#ifndef _WIN32
#include <unistd.h>
#endif

#include "classtools.h"

int main(int argc, char **argv){
   FILE *classFile = fopen("ClassModel.class", "rb");
   if (classFile == NULL){fputs ("OPen error",stderr); exit (1);}
   fseek(classFile, 0 , SEEK_END);
   long size = ftell(classFile);
   rewind (classFile);

   // allocate memory to contain the whole file:
   char *buffer = (char*) malloc (sizeof(char)*size);
   if (buffer == NULL) {fputs ("Memory error",stderr); exit (2);}

   // copy the file into the buffer:
   size_t result = fread (buffer,1,size, classFile);
   if (result != size) {fputs ("Reading error",stderr); exit (3);}

   fprintf(stdout, "read %d bytes\n", size);

   ByteBuffer byteBuffer((byte_t*)buffer, size);
   isKernel("ClassModel", &byteBuffer);

   /* the whole file is now loaded in the memory buffer. */

   // terminate
   fclose (classFile);

}
