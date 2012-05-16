package com.amd.aparapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenCLProgram{
   private long programId;

   private long queueId;

   private long contextId;

   private OpenCLDevice device;

   private String source;

   private String log;

   OpenCLProgram(long _programId, long _queueId, long _contextId, OpenCLDevice _device, String _source, String _log) {
      programId = _programId;
      queueId = _queueId;
      contextId = _contextId;
      device = _device;
      source = _source;
      log = _log;
   }

   public OpenCLDevice getDevice() {
      return device;
   }

   public OpenCLKernel createKernel(String _kernelName, List<OpenCLArgDescriptor> args) {
      return (OpenCLJNI.getJNI().createKernel(this, _kernelName, args));
   }

   private Map<Object, OpenCLMem> instanceToMem = new HashMap<Object, OpenCLMem>();

   private Map<Long, OpenCLMem> addressToMem = new HashMap<Long, OpenCLMem>();

   public synchronized OpenCLMem getMem(Object _instance, long _address) {
      OpenCLMem mem = instanceToMem.get(_instance);
      if (mem == null) {
         mem = addressToMem.get(_instance);
         if (mem != null) {
            System.out.println("object has been moved, we need to remap the buffer");
            OpenCLJNI.getJNI().remap(this, mem, _address);
         }
      }
      return (mem);
   }

   public synchronized void add(OpenCLMem _mem) {

      instanceToMem.put(_mem.instance, _mem);
      addressToMem.put(_mem.address, _mem);
   }

   public synchronized void remaped(OpenCLMem _mem, long _oldAddress) {
      addressToMem.remove(_oldAddress);
      addressToMem.put(_mem.address, _mem);
   }

}
