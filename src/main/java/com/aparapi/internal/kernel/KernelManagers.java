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
package com.aparapi.internal.kernel;

import com.aparapi.device.Device;

import java.util.Collections;
import java.util.List;

/**
 * KernelManager instances useful for debugging.
 */
public class KernelManagers {

   public static final KernelManager JTP_ONLY = new KernelManager() {

      private final List<Device.TYPE> types = Collections.singletonList(Device.TYPE.JTP);

      @Override
      protected List<Device.TYPE> getPreferredDeviceTypes() {
         return types;
      }
   };

   public static final KernelManager SEQUENTIAL_ONLY = new KernelManager() {

      private final List<Device.TYPE> types = Collections.singletonList(Device.TYPE.SEQ);

      @Override
      protected List<Device.TYPE> getPreferredDeviceTypes() {
         return types;
      }
   };
}
