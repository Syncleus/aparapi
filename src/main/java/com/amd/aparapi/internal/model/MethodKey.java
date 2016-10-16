package com.amd.aparapi.internal.model;

final class MethodKey{
   static MethodKey of(String name, String signature) {
      return new MethodKey(name, signature);
   }

   private final String name;

   private final String signature;

   @Override public String toString() {
      return "MethodKey [name=" + getName() + ", signature=" + getSignature() + "]";
   }

   @Override public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
      result = prime * result + ((getSignature() == null) ? 0 : getSignature().hashCode());
      return result;
   }

   @Override public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      MethodKey other = (MethodKey) obj;
      if (getName() == null) {
         if (other.getName() != null)
            return false;
      } else if (!getName().equals(other.getName()))
         return false;
      if (getSignature() == null) {
         if (other.getSignature() != null)
            return false;
      } else if (!getSignature().equals(other.getSignature()))
         return false;
      return true;
   }

   private MethodKey(String name, String signature) {
      this.name = name;
      this.signature = signature;
   }

   public String getName() {
      return name;
   }

   public String getSignature() {
      return signature;
   }
}
