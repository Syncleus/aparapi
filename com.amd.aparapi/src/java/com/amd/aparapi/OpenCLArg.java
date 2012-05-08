/**
 * 
 */
package com.amd.aparapi;

public class OpenCLArg{
   public OpenCLMem memVal;

   private String name;

   public long bits;

   public OpenCLKernel kernel;

   public OpenCLArg(String _name, long _bits) {

      name = _name;
      bits = _bits;
   }

   public String toString() {
      StringBuilder argBuilder = new StringBuilder();
      if ((bits & OpenCLJNI.GLOBAL_BIT) == OpenCLJNI.GLOBAL_BIT) {
         argBuilder.append("__global ");
      } else if ((bits & OpenCLJNI.LOCAL_BIT) == OpenCLJNI.LOCAL_BIT) {
         argBuilder.append("__local ");
      } else if ((bits & OpenCLJNI.CONST_BIT) == OpenCLJNI.CONST_BIT) {
         argBuilder.append("__constant ");
      } else if ((bits & OpenCLJNI.ARG_BIT) == OpenCLJNI.ARG_BIT) {
         // 
      } else {
         argBuilder.append("WHATISTHIS?");
      }

      if ((bits & OpenCLJNI.FLOAT_BIT) == OpenCLJNI.FLOAT_BIT) {
         argBuilder.append("float ");
      } else if ((bits & OpenCLJNI.INT_BIT) == OpenCLJNI.INT_BIT) {
         argBuilder.append("int ");
      } else if ((bits & OpenCLJNI.SHORT_BIT) == OpenCLJNI.SHORT_BIT) {
         argBuilder.append("short ");
      } else if ((bits & OpenCLJNI.DOUBLE_BIT) == OpenCLJNI.DOUBLE_BIT) {
         argBuilder.append("double ");
      } else if ((bits & OpenCLJNI.LONG_BIT) == OpenCLJNI.LONG_BIT) {
         argBuilder.append("long ");
      }

      if ((bits & OpenCLJNI.ARRAY_BIT) == OpenCLJNI.ARRAY_BIT) {
         argBuilder.append("*");
      }
      argBuilder.append(name);
      if ((bits & OpenCLJNI.READONLY_BIT) == OpenCLJNI.READONLY_BIT) {
         argBuilder.append(" /* readonly */");
      } else if ((bits & OpenCLJNI.WRITEONLY_BIT) == OpenCLJNI.WRITEONLY_BIT) {
         argBuilder.append(" /* writeonly */");
      } else if ((bits & OpenCLJNI.READWRITE_BIT) == OpenCLJNI.READWRITE_BIT) {
         argBuilder.append(" /* readwrite */");
      }
      return (argBuilder.toString());
   }

}
