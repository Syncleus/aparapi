
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
#include "instruction.h"

int main(int argc, char **argv){
   FILE *classFile = fopen("Main.class", "rb");
   if (classFile == NULL){fputs ("OPen error",stderr); exit (1);}
   fseek(classFile, 0 , SEEK_END);
   long size = ftell(classFile);
   rewind (classFile);

   // allocate memory to contain the whole file:
   char *buffer = new char[size];
   if (buffer == NULL) {fputs ("Memory error",stderr); exit (2);}

   // copy the file into the buffer:
   size_t result = fread (buffer,1,size, classFile);
   fclose (classFile);
   if (result != size) {fputs ("Reading error",stderr); exit (3);}

   fprintf(stdout, "read %ld bytes\n", size);

   ByteBuffer *byteBuffer = new ByteBuffer((byte_t*)buffer, size);

   ClassInfo *classInfo = new ClassInfo(byteBuffer);
   //MethodInfo *methodInfo = classInfo->getMethodInfo((char*)"getCount", (char*)"(FF)I");
   MethodInfo *methodInfo = classInfo->getMethodInfo((char*)"run", (char*)"()V");
   CodeAttribute *codeAttribute = methodInfo->getCodeAttribute();
   ByteBuffer *codeByteBuffer = codeAttribute->getCodeByteBuffer();

   PCStack *pcStack = new PCStack(codeAttribute->getMaxStack());
   Instruction** instructions = new Instruction*[codeByteBuffer->getLen()];
   for (unsigned i=0; i< codeByteBuffer->getLen(); i++){
      instructions[i] = NULL;
   }

   s4_t prevPc=-1;

   while (!codeByteBuffer->empty()){
      Instruction *instruction = new Instruction(classInfo->getConstantPool(), codeByteBuffer, pcStack, prevPc);
      prevPc = instruction->getPC();
      instructions[prevPc] = instruction;
      fprintf(stdout, "|");
      for (int i=0; i<pcStack->getSize(); i++){
         if (i>=pcStack->getIndex()){
            fprintf(stdout, "   |", pcStack->get(i));
         }else{
            fprintf(stdout, "%3d|", pcStack->get(i));
         }
      }
      fprintf(stdout, " : ");
      instruction->write(stdout, classInfo->getConstantPool(), codeAttribute->getLocalVariableTableAttribute());
      fprintf(stdout, "\n");
   }
   int label= 0;
   for (Instruction *instruction = instructions[0]; instruction != NULL; instruction = (instruction->getNextPC()<codeByteBuffer->getLen())?instruction = instructions[instruction->getNextPC()]:NULL){
      int targetPC;
      if ((targetPC = instruction->isBranch())>=0){
         instructions[targetPC]->branchFrom(instruction->getPC());
         if (instructions[targetPC]->getLabel()<0){
            instructions[targetPC]->setLabel(label++);
         }
      }
   }

   for (Instruction *instruction = instructions[0]; instruction != NULL; instruction = (instruction->getNextPC()<codeByteBuffer->getLen())?instruction = instructions[instruction->getNextPC()]:NULL){
      if (instruction != instructions[0] && instruction->getStackBase()==0 && instructions[instruction->getPrevPC()]->getStackBase()>0){
         fprintf(stdout, "-8<-\n");
         instructions[instruction->getPrevPC()]->treeWrite(stdout, instructions, codeByteBuffer->getLen(), 0, classInfo->getConstantPool(), codeAttribute->getLocalVariableTableAttribute(), instruction->getPrevPC());
         fprintf(stdout, "->8-\n");
      }
      fprintf(stdout, " stackBase = %2d :", instruction->getStackBase());
      instruction->write(stdout, classInfo->getConstantPool(), codeAttribute->getLocalVariableTableAttribute());
      fprintf(stdout, "\n");
   }

   for (Instruction *instruction = instructions[0]; instruction != NULL; instruction = (instruction->getNextPC()<codeByteBuffer->getLen())?instruction = instructions[instruction->getNextPC()]:NULL){
      int branch; 
      if ((branch = instruction->getLabel())>=0){
         fprintf(stdout, "L%d:\n", branch);
      }
      instruction->writeRegForm(stdout, classInfo->getConstantPool(), codeAttribute->getMaxLocals(), codeAttribute->getLocalVariableTableAttribute());
      int targetPC;
      if ((targetPC = instruction->isBranch())>=0){
         fprintf(stdout, " L%d", instructions[targetPC]->getLabel());
      }
      fprintf(stdout, "\n");
   }

   for (unsigned i=0; i< codeByteBuffer->getLen(); i++){
      if (instructions[i] != NULL){
         delete instructions[i];
      }
   }
   delete [] instructions;
   delete pcStack;
   delete byteBuffer;
   delete[] buffer; 
   delete classInfo;

   /* the whole file is now loaded in the memory buffer. */

   // terminate

}
