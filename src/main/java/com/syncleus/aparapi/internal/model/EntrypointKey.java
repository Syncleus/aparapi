package com.syncleus.aparapi.internal.model;

final class EntrypointKey{
   public static EntrypointKey of(String entrypointName, String descriptor) {
      return new EntrypointKey(entrypointName, descriptor);
   }

   private String descriptor;

   private String entrypointName;

   private EntrypointKey(String entrypointName, String descriptor) {
      this.entrypointName = entrypointName;
      this.descriptor = descriptor;
   }

   String getDescriptor() {
      return descriptor;
   }

   String getEntrypointName() {
      return entrypointName;
   }

   @Override public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((descriptor == null) ? 0 : descriptor.hashCode());
      result = prime * result + ((entrypointName == null) ? 0 : entrypointName.hashCode());
      return result;
   }

   @Override public String toString() {
      return "EntrypointKey [entrypointName=" + entrypointName + ", descriptor=" + descriptor + "]";
   }

   @Override public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      EntrypointKey other = (EntrypointKey) obj;
      if (descriptor == null) {
         if (other.descriptor != null)
            return false;
      } else if (!descriptor.equals(other.descriptor))
         return false;
      if (entrypointName == null) {
         if (other.entrypointName != null)
            return false;
      } else if (!entrypointName.equals(other.entrypointName))
         return false;
      return true;
   }
}
