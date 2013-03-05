#ifndef RANGE_H
#define RANGE_H

#include "Common.h"
#include "JNIHelper.h"

class Range{
   public:
      static jclass rangeClazz;
      static jfieldID globalSize_0_FieldID;
      static jfieldID globalSize_1_FieldID;
      static jfieldID globalSize_2_FieldID;
      static jfieldID localSize_0_FieldID;
      static jfieldID localSize_1_FieldID;
      static jfieldID localSize_2_FieldID;
      static jfieldID dimsFieldID;
      static jfieldID localIsDerivedFieldID; 
      jobject range;
      cl_int dims;
      size_t *offsets;
      size_t *globalDims;
      size_t *localDims;
      jboolean localIsDerived;
      Range(JNIEnv *jenv, jobject range);
      ~Range();
};

#endif // RANGE_H
