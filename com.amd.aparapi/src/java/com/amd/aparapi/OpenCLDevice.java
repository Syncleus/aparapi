package com.amd.aparapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenCLDevice extends Device{

   private OpenCLPlatform platform;

   private long deviceId;

   int maxComputeUnits;

   private long localMemSize;

   private long globalMemSize;

   private long maxMemAllocSize;

   OpenCLDevice(OpenCLPlatform _platform, long _deviceId, TYPE _type) {
      platform = _platform;
      deviceId = _deviceId;
      type = _type;

   }

   public int getMaxComputeUnits() {
      return maxComputeUnits;
   }

   public void setMaxComputeUnits(int _maxComputeUnits) {
      maxComputeUnits = _maxComputeUnits;
   }

   public long getLocalMemSize() {
      return localMemSize;
   }

   public void setLocalMemSize(long _localMemSize) {
      localMemSize = _localMemSize;
   }

   public long getMaxMemAllocSize() {
      return maxMemAllocSize;
   }

   public void setMaxMemAllocSize(long _maxMemAllocSize) {
      maxMemAllocSize = _maxMemAllocSize;
   }

   public long getGlobalMemSize() {
      return globalMemSize;
   }

   public void setGlobalMemSize(long _globalMemSize) {
      globalMemSize = _globalMemSize;
   }

   public String toString() {
      StringBuilder s = new StringBuilder("{");
      boolean first = true;
      for (int workItemSize : maxWorkItemSize) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }
         s.append(workItemSize);
      }
      s.append("}");
      return ("Device " + deviceId + "\n  type:" + type + "\n  maxComputeUnits=" + maxComputeUnits + "\n  maxWorkItemDimensions="
            + maxWorkItemDimensions + "\n  maxWorkItemSizes=" + s + "\n  maxWorkWorkGroupSize=" + maxWorkGroupSize
            + "\n  globalMemSize=" + globalMemSize + "\n  localMemSize=" + localMemSize);
   }

   void setMaxWorkItemSize(int _dim, int _value) {
      maxWorkItemSize[_dim] = _value;
   }

   public long getDeviceId() {
      return (deviceId);
   }

   public OpenCLPlatform getPlatform() {
      return (platform);
   }

   public static class OpenCLInvocationHandler<T extends OpenCL<T>> implements InvocationHandler{
      private Map<String, OpenCLKernel> map;

      private OpenCLProgram program;

      public OpenCLInvocationHandler(OpenCLProgram _program, Map<String, OpenCLKernel> _map) {
         program = _program;
         map = _map;
      }

      @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         OpenCLKernel kernel = map.get(method.getName());
         if (kernel != null) {
            // we have a kernel entrypoint bound
            kernel.invoke(args);
         } else if (method.getName().equals("put") || method.getName().equals("get")) {
            for (Object arg : args) {
               Class<?> argClass = arg.getClass();
               if (argClass.isArray()) {
                  if (argClass.getComponentType().isPrimitive()) {
                     OpenCLMem mem = program.getMem(arg, 0L);
                     if (mem == null) {
                        throw new IllegalStateException("can't put/get an array that has never been passed to a kernel " + argClass);

                     }
                     if (method.getName().equals("put")) {
                        mem.bits |= OpenCLJNI.MEM_DIRTY_BIT;
                     } else {
                        OpenCLJNI.getJNI().getMem(program, mem);
                     }

                  } else {
                     throw new IllegalStateException("Only array args (of primitives) expected for put/get, cant deal with "
                           + argClass);

                  }
               } else {
                  throw new IllegalStateException("Only array args expected for put/get, cant deal with " + argClass);
               }
            }
         } else {
            throw new IllegalStateException("How did we get here with method " + method.getName());

         }
         return proxy;
      }

   }

   public List<OpenCLArg> getArgs(Method m) {
      List<OpenCLArg> args = new ArrayList<OpenCLArg>();
      Annotation[][] parameterAnnotations = m.getParameterAnnotations();
      Class<?>[] parameterTypes = m.getParameterTypes();

      for (int arg = 0; arg < parameterTypes.length; arg++) {
         if (parameterTypes[arg].isAssignableFrom(Range.class)) {

         } else {

            long bits = 0L;
            String name = null;
            for (Annotation pa : parameterAnnotations[arg]) {
               if (pa instanceof OpenCL.GlobalReadOnly) {
                  name = ((OpenCL.GlobalReadOnly) pa).value();
                  bits |= OpenCLJNI.GLOBAL_BIT | OpenCLJNI.READONLY_BIT;
               } else if (pa instanceof OpenCL.GlobalWriteOnly) {
                  name = ((OpenCL.GlobalWriteOnly) pa).value();
                  bits |= OpenCLJNI.GLOBAL_BIT | OpenCLJNI.WRITEONLY_BIT;
               } else if (pa instanceof OpenCL.GlobalReadWrite) {
                  name = ((OpenCL.GlobalReadWrite) pa).value();
                  bits |= OpenCLJNI.GLOBAL_BIT | OpenCLJNI.READWRITE_BIT;
               } else if (pa instanceof OpenCL.Local) {
                  name = ((OpenCL.Local) pa).value();
                  bits |= OpenCLJNI.LOCAL_BIT;
               } else if (pa instanceof OpenCL.Constant) {
                  name = ((OpenCL.Constant) pa).value();
                  bits |= OpenCLJNI.CONST_BIT | OpenCLJNI.READONLY_BIT;
               } else if (pa instanceof OpenCL.Arg) {
                  name = ((OpenCL.Arg) pa).value();
                  bits |= OpenCLJNI.ARG_BIT;
               }

            }
            if (parameterTypes[arg].isArray()) {
               if (parameterTypes[arg].isAssignableFrom(float[].class)) {
                  bits |= OpenCLJNI.FLOAT_BIT | OpenCLJNI.ARRAY_BIT;
               } else if (parameterTypes[arg].isAssignableFrom(int[].class)) {
                  bits |= OpenCLJNI.INT_BIT | OpenCLJNI.ARRAY_BIT;
               } else if (parameterTypes[arg].isAssignableFrom(double[].class)) {
                  bits |= OpenCLJNI.DOUBLE_BIT | OpenCLJNI.ARRAY_BIT;
               } else if (parameterTypes[arg].isAssignableFrom(byte[].class)) {
                  bits |= OpenCLJNI.BYTE_BIT | OpenCLJNI.ARRAY_BIT;
               } else if (parameterTypes[arg].isAssignableFrom(short[].class)) {
                  bits |= OpenCLJNI.SHORT_BIT | OpenCLJNI.ARRAY_BIT;
               } else if (parameterTypes[arg].isAssignableFrom(long[].class)) {
                  bits |= OpenCLJNI.LONG_BIT | OpenCLJNI.ARRAY_BIT;
               }
            } else if (parameterTypes[arg].isPrimitive()) {
               if (parameterTypes[arg].isAssignableFrom(float.class)) {
                  bits |= OpenCLJNI.FLOAT_BIT | OpenCLJNI.PRIMITIVE_BIT;
               } else if (parameterTypes[arg].isAssignableFrom(int.class)) {
                  bits |= OpenCLJNI.INT_BIT | OpenCLJNI.PRIMITIVE_BIT;
               } else if (parameterTypes[arg].isAssignableFrom(double.class)) {
                  bits |= OpenCLJNI.DOUBLE_BIT | OpenCLJNI.PRIMITIVE_BIT;
               } else if (parameterTypes[arg].isAssignableFrom(byte.class)) {
                  bits |= OpenCLJNI.BYTE_BIT | OpenCLJNI.PRIMITIVE_BIT;
               } else if (parameterTypes[arg].isAssignableFrom(short.class)) {
                  bits |= OpenCLJNI.SHORT_BIT | OpenCLJNI.PRIMITIVE_BIT;
               } else if (parameterTypes[arg].isAssignableFrom(long.class)) {
                  bits |= OpenCLJNI.LONG_BIT | OpenCLJNI.PRIMITIVE_BIT;
               }
            } else {
               System.out.println("OUch!");
            }
            if (name == null) {
               throw new IllegalStateException("no name!");
            }
            OpenCLArg kernelArg = new OpenCLArg(name, bits);
            args.add(kernelArg);

         }
      }

      return (args);
   }

   public <T extends OpenCL<T>> T create(Class<T> _interface) {

      StringBuilder sourceBuilder = new StringBuilder();
      Map<String, List<OpenCLArg>> kernelNameToArgsMap = new HashMap<String, List<OpenCLArg>>();
      boolean interfaceIsAnnotated = false;
      for (Annotation a : _interface.getAnnotations()) {
         if (a instanceof OpenCL.Source) {
            OpenCL.Source source = (OpenCL.Source) a;
            sourceBuilder.append(source.value()).append("\n");
            interfaceIsAnnotated = true;
         } else if (a instanceof OpenCL.Resource) {
            OpenCL.Resource sourceResource = (OpenCL.Resource) a;
            InputStream stream = _interface.getClassLoader().getResourceAsStream(sourceResource.value());
            if (stream != null) {

               BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

               try {
                  for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                     sourceBuilder.append(line).append("\n");
                  }
               } catch (IOException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }

            }
            interfaceIsAnnotated = true;
         }
      }

      if (interfaceIsAnnotated) {
         // just crawl the methods (non put or get) and create kernels
         for (Method m : _interface.getDeclaredMethods()) {
            if ((!m.getName().equals("put") && !m.getName().equals("get"))) {

               List<OpenCLArg> args = getArgs(m);

               kernelNameToArgsMap.put(m.getName(), args);
            }
         }
      } else {
         for (Method m : _interface.getDeclaredMethods()) {

            for (Annotation a : m.getAnnotations()) {
               //  System.out.println("   annotation "+a);
               // System.out.println("   annotation type " + a.annotationType());
               if (a instanceof OpenCL.Kernel && (!m.getName().equals("put") && !m.getName().equals("get"))) {
                  sourceBuilder.append("__kernel void " + m.getName() + "(");
                  List<OpenCLArg> args = getArgs(m);

                  boolean first = true;
                  for (OpenCLArg arg : args) {
                     if (first) {
                        first = false;
                     } else {
                        sourceBuilder.append(",");
                     }
                     sourceBuilder.append("\n   " + arg);
                  }

                  sourceBuilder.append(")");
                  OpenCL.Kernel kernel = (OpenCL.Kernel) a;
                  sourceBuilder.append(kernel.value());
                  kernelNameToArgsMap.put(m.getName(), args);
               }
            }
         }
      }

      String source = sourceBuilder.toString();
      System.out.println("opencl{\n" + source + "\n}opencl");

      OpenCLProgram program = createProgram(source);

      Map<String, OpenCLKernel> map = new HashMap<String, OpenCLKernel>();
      for (String name : kernelNameToArgsMap.keySet()) {
         OpenCLKernel kernel = program.createKernel(name, kernelNameToArgsMap.get(name));
         if (kernel == null) {
            throw new IllegalStateException("kernel is null");
         }
         map.put(name, kernel);
      }

      OpenCLInvocationHandler<T> invocationHandler = new OpenCLInvocationHandler<T>(program, map);
      T instance = (T) Proxy.newProxyInstance(OpenCLDevice.class.getClassLoader(), new Class[] {
            _interface,
            OpenCL.class
      }, invocationHandler);
      return instance;

   }

   interface DeviceFilter{
      boolean match(OpenCLDevice _device);
   }

   interface DeviceComparitor{
      OpenCLDevice best(OpenCLDevice _deviceLhs, OpenCLDevice _deviceRhs);
   }

   public static OpenCLDevice selectFirst(DeviceFilter _deviceFilter) {
      OpenCLDevice device = null;
      for (OpenCLPlatform p : OpenCLPlatform.getPlatforms()) {
         for (OpenCLDevice d : p.getDevices()) {
            if (_deviceFilter.match(d)) {
               device = d;
               break;
            }
         }
         if (device != null) {
            break;
         }
      }
      return (device);
   }

   public static OpenCLDevice selectBest(DeviceComparitor _deviceComparitor) {
      OpenCLDevice device = null;
      for (OpenCLPlatform p : OpenCLPlatform.getPlatforms()) {
         for (OpenCLDevice d : p.getDevices()) {
            if (device == null) {
               device = d;
            } else {
               device = _deviceComparitor.best(device, d);
            }
         }
      }
      return (device);
   }

   public OpenCLProgram createProgram(String source) {
      return (OpenCLJNI.getJNI().createProgram(this, source));
   }

}
