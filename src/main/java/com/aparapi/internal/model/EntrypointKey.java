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
