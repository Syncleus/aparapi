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
package com.aparapi.internal.opencl;

import com.aparapi.ProfileInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.jni.OpenCLJNI;

public class OpenCLProgram extends OpenCLJNI{

   private final long programId;

   private final long queueId;

   private final long contextId;

   private final long profileInfo = 0L;

   private final OpenCLDevice device;

   private final String source;

   /**
    * FIXME Why are these not ConcurrentHashMaps or at least synchronized at a finer grain?
    */
   private final Map<Object, OpenCLMem> instanceToMem = new HashMap<Object, OpenCLMem>();

   private final Map<Long, OpenCLMem> addressToMem = new HashMap<Long, OpenCLMem>();

   /**
    * Minimal constructor
    */
   public OpenCLProgram(OpenCLDevice _device, String _source) {
      programId = 0;
      queueId = 0;
      contextId = 0;
      device = _device;
      source = _source;
   }

   /**
    * Full constructor
    * 
    * @param _programId
    * @param _queueId
    * @param _contextId
    * @param _device
    * @param _source
    */
   public OpenCLProgram(long _programId, long _queueId, long _contextId, OpenCLDevice _device, String _source) {
      programId = _programId;
      queueId = _queueId;
      contextId = _contextId;
      device = _device;
      source = _source;
   }

   public OpenCLProgram createProgram(OpenCLDevice context) {
      return createProgram(context, source);
   }

   public OpenCLDevice getDevice() {
      return device;
   }

   public synchronized OpenCLMem getMem(Object _instance, long _address) {
      OpenCLMem mem = instanceToMem.get(_instance);

      if (mem == null) {
         mem = addressToMem.get(_instance);
         if (mem != null) {
            System.out.println("object has been moved, we need to remap the buffer");
            remap(this, mem, _address);
         }
      }

      return (mem);
   }

   public synchronized void add(Object _instance, long _address, OpenCLMem _mem) {
      instanceToMem.put(_instance, _mem);
      addressToMem.put(_address, _mem);
   }

   public synchronized void remapped(Object _instance, long _address, OpenCLMem _mem, long _oldAddress) {
      addressToMem.remove(_oldAddress);
      addressToMem.put(_address, _mem);
   }

   public void dispose(){
       disposeProgram(this);
   }

   public List<ProfileInfo> getProfileInfo(){
      return(getProfileInfo(this));
   }
}
