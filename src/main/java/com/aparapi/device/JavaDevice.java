/**
 * Copyright (c) 2016 - 2017 Syncleus, Inc.
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
package com.aparapi.device;

public class JavaDevice extends Device {

   public static final JavaDevice THREAD_POOL = new JavaDevice(TYPE.JTP, "Java Thread Pool", -3);
   public static final JavaDevice ALTERNATIVE_ALGORITHM = new JavaDevice(TYPE.ALT, "Java Alternative Algorithm", -2);
   public static final JavaDevice SEQUENTIAL = new JavaDevice(TYPE.SEQ, "Java Sequential", -1);

   private final String name;

   private JavaDevice(TYPE _type, String _name, long deviceId) {
      super(deviceId);
      setType(_type);
      this.name = _name;
   }

   @Override
   public String getShortDescription() {
      return name;
   }

   @Override
   public String toString() {
      return getShortDescription();
   }
}
