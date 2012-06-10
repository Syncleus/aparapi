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
         if (!isReservedInterfaceMethod(method)) {
            OpenCLKernel kernel = map.get(method.getName());
            if (kernel != null) {
               kernel.invoke(args);
            }
         } else {
            if (method.getName().equals("put")) {

               /*
               for (Object arg : args) {
                  Class<?> argClass = arg.getClass();
                  if (argClass.isArray()) {
                     if (argClass.getComponentType().isPrimitive()) {
                        OpenCLMem mem = program.getMem(arg, 0L);
                        if (mem == null) {
                           throw new IllegalStateException("can't put an array that has never been passed to a kernel " + argClass);

                        }
                        mem.bits |= OpenCLMem.MEM_DIRTY_BIT;
                     } else {
                        throw new IllegalStateException("Only array args (of primitives) expected for put/get, cant deal with "
                              + argClass);

                     }
                  } else {
                     throw new IllegalStateException("Only array args expected for put/get, cant deal with " + argClass);
                  }
               }
               */
            } else if (method.getName().equals("get")) {
               /*
               for (Object arg : args) {
                  Class<?> argClass = arg.getClass();
                  if (argClass.isArray()) {
                     if (argClass.getComponentType().isPrimitive()) {
                        OpenCLMem mem = program.getMem(arg, 0L);
                        if (mem == null) {
                           throw new IllegalStateException("can't get an array that has never been passed to a kernel " + argClass);

                        }
                        OpenCLJNI.getJNI().getMem(program, mem);
                     } else {
                        throw new IllegalStateException("Only array args (of primitives) expected for put/get, cant deal with "
                              + argClass);

                     }
                  } else {
                     throw new IllegalStateException("Only array args expected for put/get, cant deal with " + argClass);
                  }
               }
               */
            } else if (method.getName().equals("begin")) {
            } else if (method.getName().equals("end")) {
            }
         }
         return proxy;
      }
   }

   public List<OpenCLArgDescriptor> getArgs(Method m) {
      List<OpenCLArgDescriptor> args = new ArrayList<OpenCLArgDescriptor>();
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
                  bits |= OpenCLArgDescriptor.ARG_GLOBAL_BIT | OpenCLArgDescriptor.ARG_READONLY_BIT;
               } else if (pa instanceof OpenCL.GlobalWriteOnly) {
                  name = ((OpenCL.GlobalWriteOnly) pa).value();
                  bits |= OpenCLArgDescriptor.ARG_GLOBAL_BIT | OpenCLArgDescriptor.ARG_WRITEONLY_BIT;
               } else if (pa instanceof OpenCL.GlobalReadWrite) {
                  name = ((OpenCL.GlobalReadWrite) pa).value();
                  bits |= OpenCLArgDescriptor.ARG_GLOBAL_BIT | OpenCLArgDescriptor.ARG_READWRITE_BIT;
               } else if (pa instanceof OpenCL.Local) {
                  name = ((OpenCL.Local) pa).value();
                  bits |= OpenCLArgDescriptor.ARG_LOCAL_BIT;
               } else if (pa instanceof OpenCL.Constant) {
                  name = ((OpenCL.Constant) pa).value();
                  bits |= OpenCLArgDescriptor.ARG_CONST_BIT | OpenCLArgDescriptor.ARG_READONLY_BIT;
               } else if (pa instanceof OpenCL.Arg) {
                  name = ((OpenCL.Arg) pa).value();
                  bits |= OpenCLArgDescriptor.ARG_ISARG_BIT;
               }

            }
            if (parameterTypes[arg].isArray()) {
               if (parameterTypes[arg].isAssignableFrom(float[].class)) {
                  bits |= OpenCLArgDescriptor.ARG_FLOAT_BIT | OpenCLArgDescriptor.ARG_ARRAY_BIT;
               } else if (parameterTypes[arg].isAssignableFrom(int[].class)) {
                  bits |= OpenCLArgDescriptor.ARG_INT_BIT | OpenCLArgDescriptor.ARG_ARRAY_BIT;
               } else if (parameterTypes[arg].isAssignableFrom(double[].class)) {
                  bits |= OpenCLArgDescriptor.ARG_DOUBLE_BIT | OpenCLArgDescriptor.ARG_ARRAY_BIT;
               } else if (parameterTypes[arg].isAssignableFrom(byte[].class)) {
                  bits |= OpenCLArgDescriptor.ARG_BYTE_BIT | OpenCLArgDescriptor.ARG_ARRAY_BIT;
               } else if (parameterTypes[arg].isAssignableFrom(short[].class)) {
                  bits |= OpenCLArgDescriptor.ARG_SHORT_BIT | OpenCLArgDescriptor.ARG_ARRAY_BIT;
               } else if (parameterTypes[arg].isAssignableFrom(long[].class)) {
                  bits |= OpenCLArgDescriptor.ARG_LONG_BIT | OpenCLArgDescriptor.ARG_ARRAY_BIT;
               }
            } else if (parameterTypes[arg].isPrimitive()) {
               if (parameterTypes[arg].isAssignableFrom(float.class)) {
                  bits |= OpenCLArgDescriptor.ARG_FLOAT_BIT | OpenCLArgDescriptor.ARG_PRIMITIVE_BIT;
               } else if (parameterTypes[arg].isAssignableFrom(int.class)) {
                  bits |= OpenCLArgDescriptor.ARG_INT_BIT | OpenCLArgDescriptor.ARG_PRIMITIVE_BIT;
               } else if (parameterTypes[arg].isAssignableFrom(double.class)) {
                  bits |= OpenCLArgDescriptor.ARG_DOUBLE_BIT | OpenCLArgDescriptor.ARG_PRIMITIVE_BIT;
               } else if (parameterTypes[arg].isAssignableFrom(byte.class)) {
                  bits |= OpenCLArgDescriptor.ARG_BYTE_BIT | OpenCLArgDescriptor.ARG_PRIMITIVE_BIT;
               } else if (parameterTypes[arg].isAssignableFrom(short.class)) {
                  bits |= OpenCLArgDescriptor.ARG_SHORT_BIT | OpenCLArgDescriptor.ARG_PRIMITIVE_BIT;
               } else if (parameterTypes[arg].isAssignableFrom(long.class)) {
                  bits |= OpenCLArgDescriptor.ARG_LONG_BIT | OpenCLArgDescriptor.ARG_PRIMITIVE_BIT;
               }
            } else {
               System.out.println("OUch!");
            }
            if (name == null) {
               throw new IllegalStateException("no name!");
            }
            OpenCLArgDescriptor kernelArg = new OpenCLArgDescriptor(name, bits);
            args.add(kernelArg);

         }
      }

      return (args);
   }

   private static boolean isReservedInterfaceMethod(Method _methods) {
      return (_methods.getName().equals("put") || _methods.getName().equals("get") || _methods.getName().equals("begin") || _methods
            .getName().equals("begin"));
   }

   private String streamToString(InputStream _inputStream) {
      StringBuilder sourceBuilder = new StringBuilder();

      if (_inputStream != null) {

         BufferedReader reader = new BufferedReader(new InputStreamReader(_inputStream));

         try {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
               sourceBuilder.append(line).append("\n");
            }
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }

      }
      try {
         _inputStream.close();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return (sourceBuilder.toString());
   }

   public <T extends OpenCL<T>> T bind(Class<T> _interface, InputStream _inputStream) {
      return (bind(_interface, streamToString(_inputStream)));
   }

   public <T extends OpenCL<T>> T bind(Class<T> _interface) {
      return (bind(_interface, (String) null));
   }

   public <T extends OpenCL<T>> T bind(Class<T> _interface, String _source) {

      Map<String, List<OpenCLArgDescriptor>> kernelNameToArgsMap = new HashMap<String, List<OpenCLArgDescriptor>>();
      if (_source == null) {
         StringBuilder sourceBuilder = new StringBuilder();
         boolean interfaceIsAnnotated = false;
         for (Annotation a : _interface.getAnnotations()) {
            if (a instanceof OpenCL.Source) {
               OpenCL.Source source = (OpenCL.Source) a;
               sourceBuilder.append(source.value()).append("\n");
               interfaceIsAnnotated = true;
            } else if (a instanceof OpenCL.Resource) {
               OpenCL.Resource sourceResource = (OpenCL.Resource) a;
               InputStream stream = _interface.getClassLoader().getResourceAsStream(sourceResource.value());
               sourceBuilder.append(streamToString(stream));
               interfaceIsAnnotated = true;
            }
         }

         if (interfaceIsAnnotated) {
            // just crawl the methods (non put or get) and create kernels
            for (Method m : _interface.getDeclaredMethods()) {
               if (!isReservedInterfaceMethod(m)) {
                  List<OpenCLArgDescriptor> args = getArgs(m);
                  kernelNameToArgsMap.put(m.getName(), args);
               }
            }
         } else {

            for (Method m : _interface.getDeclaredMethods()) {
               if (!isReservedInterfaceMethod(m)) {
                  for (Annotation a : m.getAnnotations()) {
                     //  System.out.println("   annotation "+a);
                     // System.out.println("   annotation type " + a.annotationType());
                     if (a instanceof OpenCL.Kernel) {
                        sourceBuilder.append("__kernel void " + m.getName() + "(");
                        List<OpenCLArgDescriptor> args = getArgs(m);

                        boolean first = true;
                        for (OpenCLArgDescriptor arg : args) {
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

         }
         _source = sourceBuilder.toString();
      } else {
         for (Method m : _interface.getDeclaredMethods()) {
            if (!isReservedInterfaceMethod(m)) {
               List<OpenCLArgDescriptor> args = getArgs(m);
               kernelNameToArgsMap.put(m.getName(), args);
            }
         }
      }

     // System.out.println("opencl{\n" + _source + "\n}opencl");

      OpenCLProgram program = createProgram(_source);

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

   public interface DeviceSelector{
      OpenCLDevice select(OpenCLDevice _device);
   }

   public interface DeviceComparitor{
      OpenCLDevice select(OpenCLDevice _deviceLhs, OpenCLDevice _deviceRhs);
   }

   public static OpenCLDevice select(DeviceSelector _deviceSelector) {
      OpenCLDevice device = null;
      for (OpenCLPlatform p : OpenCLPlatform.getPlatforms()) {
         for (OpenCLDevice d : p.getDevices()) {
            device = _deviceSelector.select(d);
            if (device != null) {
               break;
            }
         }
         if (device != null) {
            break;
         }
      }
      return (device);
   }

   public static OpenCLDevice select(DeviceComparitor _deviceComparitor) {
      OpenCLDevice device = null;
      for (OpenCLPlatform p : OpenCLPlatform.getPlatforms()) {
         for (OpenCLDevice d : p.getDevices()) {
            if (device == null) {
               device = d;
            } else {
               device = _deviceComparitor.select(device, d);
            }
         }
      }
      return (device);
   }

   public OpenCLProgram createProgram(String source) {
      return (OpenCLJNI.getJNI().createProgram(this, source));
   }

}
