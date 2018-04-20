/**
 * Copyright (c) 2016 - 2018 Syncleus, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aparapi.internal.model;

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
