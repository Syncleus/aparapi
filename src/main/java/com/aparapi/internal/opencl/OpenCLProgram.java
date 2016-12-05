package com.amd.aparapi.internal.opencl;

import java.util.HashMap;
import java.util.Map;

import com.amd.aparapi.device.OpenCLDevice;
import com.amd.aparapi.internal.jni.OpenCLJNI;

public class OpenCLProgram extends OpenCLJNI{

   private final long programId;

   private final long queueId;

   private final long contextId;

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
}
