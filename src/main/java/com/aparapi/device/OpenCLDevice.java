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

import com.aparapi.opencl.OpenCL.Kernel;

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

import com.aparapi.Range;
import com.aparapi.internal.opencl.OpenCLArgDescriptor;
import com.aparapi.internal.opencl.OpenCLKernel;
import com.aparapi.internal.opencl.OpenCLPlatform;
import com.aparapi.internal.opencl.OpenCLProgram;
import com.aparapi.opencl.OpenCL;
import com.aparapi.opencl.OpenCL.Arg;
import com.aparapi.opencl.OpenCL.Constant;
import com.aparapi.opencl.OpenCL.GlobalReadOnly;
import com.aparapi.opencl.OpenCL.GlobalReadWrite;
import com.aparapi.opencl.OpenCL.GlobalWriteOnly;
import com.aparapi.opencl.OpenCL.Local;
import com.aparapi.opencl.OpenCL.Resource;
import com.aparapi.opencl.OpenCL.Source;

public class OpenCLDevice extends Device{

   private final OpenCLPlatform platform;

   private final long deviceId;

   private int maxComputeUnits;

   private long localMemSize;

   private long globalMemSize;

   private long maxMemAllocSize;

   private String shortDescription = null;

   private String name = null;

   /**
    * Minimal constructor
    *
    * @param _platform
    * @param _deviceId
    * @param _type
    */
   public OpenCLDevice(OpenCLPlatform _platform, long _deviceId, TYPE _type) {
      platform = _platform;
      deviceId = _deviceId;
      type = _type;
   }

   public OpenCLPlatform getOpenCLPlatform() {
      return platform;
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

   void setMaxWorkItemSize(int _dim, int _value) {
      maxWorkItemSize[_dim] = _value;
   }

   public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public long getDeviceId() {
      return (deviceId);
   }

   @Override
   public String getShortDescription() {
      if (shortDescription == null) {
         String vendor = platform.getName();
         // Hopefully(!) this equates to the recognisable name of the vendor, e.g. "Intel", "NVIDIA", "AMD"
         // Note, it is not necessarily the hardware vendor, e.g. if the AMD CPU driver (i.e. platform) is used for an Intel CPU, this will be "AMD"
         String[] split = vendor.split("[\\s\\(\\)]"); // split on whitespace or on '(' or ')' since Intel use "Intel(R)" here
         shortDescription = split[0] + "<" + getType() + ">";
      }
      return shortDescription;
   }

   public static class OpenCLInvocationHandler<T extends OpenCL<T>> implements InvocationHandler{
      private final Map<String, OpenCLKernel> map;

      private final OpenCLProgram program;
      private boolean disposed = false;
      public OpenCLInvocationHandler(OpenCLProgram _program, Map<String, OpenCLKernel> _map) {
         program = _program;
         map = _map;
         disposed = false;
      }

      @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         if (disposed){
            throw new IllegalStateException("bound interface already disposed");
         }
         if (!isReservedInterfaceMethod(method)) {
            final OpenCLKernel kernel = map.get(method.getName());
            if (kernel != null) {
               kernel.invoke(args);
            }
         } else {
            if (method.getName().equals("put")) {
               System.out.println("put not implemented");

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
               System.out.println("get not implemented");
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
               System.out.println("begin not implemented");
            } else if (method.getName().equals("dispose")) {
              // System.out.println("dispose");
               for (OpenCLKernel k:map.values()){
                   k.dispose();
               }
               program.dispose();
               map.clear();
               disposed=true;
            } else if (method.getName().equals("end")) {
               System.out.println("end not implemented");
            }  else if (method.getName().equals("getProfileInfo")){
               proxy = program.getProfileInfo();
            }
         }
         return proxy;
      }
   }

   public List<OpenCLArgDescriptor> getArgs(Method m) {
      final List<OpenCLArgDescriptor> args = new ArrayList<OpenCLArgDescriptor>();
      final Annotation[][] parameterAnnotations = m.getParameterAnnotations();
      final Class<?>[] parameterTypes = m.getParameterTypes();

      for (int arg = 0; arg < parameterTypes.length; arg++) {
         if (parameterTypes[arg].isAssignableFrom(Range.class)) {

         } else {

            long bits = 0L;
            String name = null;
            for (final Annotation pa : parameterAnnotations[arg]) {
               if (pa instanceof GlobalReadOnly) {
                  name = ((GlobalReadOnly) pa).value();
                  bits |= OpenCLArgDescriptor.ARG_GLOBAL_BIT | OpenCLArgDescriptor.ARG_READONLY_BIT;
               } else if (pa instanceof GlobalWriteOnly) {
                  name = ((GlobalWriteOnly) pa).value();
                  bits |= OpenCLArgDescriptor.ARG_GLOBAL_BIT | OpenCLArgDescriptor.ARG_WRITEONLY_BIT;
               } else if (pa instanceof GlobalReadWrite) {
                  name = ((GlobalReadWrite) pa).value();
                  bits |= OpenCLArgDescriptor.ARG_GLOBAL_BIT | OpenCLArgDescriptor.ARG_READWRITE_BIT;
               } else if (pa instanceof Local) {
                  name = ((Local) pa).value();
                  bits |= OpenCLArgDescriptor.ARG_LOCAL_BIT;
               } else if (pa instanceof Constant) {
                  name = ((Constant) pa).value();
                  bits |= OpenCLArgDescriptor.ARG_CONST_BIT | OpenCLArgDescriptor.ARG_READONLY_BIT;
               } else if (pa instanceof Arg) {
                  name = ((Arg) pa).value();
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
            final OpenCLArgDescriptor kernelArg = new OpenCLArgDescriptor(name, bits);
            args.add(kernelArg);

         }
      }

      return (args);
   }

   private static boolean isReservedInterfaceMethod(Method _methods) {
      return (   _methods.getName().equals("put")
              || _methods.getName().equals("get")
              || _methods.getName().equals("dispose")
              || _methods.getName().equals("begin")
              || _methods.getName().equals("end")
              || _methods.getName().equals("getProfileInfo"));
   }

   private String streamToString(InputStream _inputStream) {
      final StringBuilder sourceBuilder = new StringBuilder();

      if (_inputStream != null) {

         final BufferedReader reader = new BufferedReader(new InputStreamReader(_inputStream));

         try {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
               sourceBuilder.append(line).append("\n");
            }
         } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }

         try {
            _inputStream.close();
         } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
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
      final Map<String, List<OpenCLArgDescriptor>> kernelNameToArgsMap = new HashMap<String, List<OpenCLArgDescriptor>>();

      if (_source == null) {
         final StringBuilder sourceBuilder = new StringBuilder();
         boolean interfaceIsAnnotated = false;
         for (final Annotation a : _interface.getAnnotations()) {
            if (a instanceof Source) {
               final Source source = (Source) a;
               sourceBuilder.append(source.value()).append("\n");
               interfaceIsAnnotated = true;
            } else if (a instanceof Resource) {
               final Resource sourceResource = (Resource) a;
               final InputStream stream = _interface.getClassLoader().getResourceAsStream(sourceResource.value());
               sourceBuilder.append(streamToString(stream));
               interfaceIsAnnotated = true;
            }
         }

         if (interfaceIsAnnotated) {
            // just crawl the methods (non put or get) and create kernels
            for (final Method m : _interface.getDeclaredMethods()) {
               if (!isReservedInterfaceMethod(m)) {
                  final List<OpenCLArgDescriptor> args = getArgs(m);
                  kernelNameToArgsMap.put(m.getName(), args);
               }
            }
         } else {

            for (final Method m : _interface.getDeclaredMethods()) {
               if (!isReservedInterfaceMethod(m)) {
                  for (final Annotation a : m.getAnnotations()) {
                     //  System.out.println("   annotation "+a);
                     // System.out.println("   annotation type " + a.annotationType());
                     if (a instanceof Kernel) {
                        sourceBuilder.append("__kernel void " + m.getName() + "(");
                        final List<OpenCLArgDescriptor> args = getArgs(m);

                        boolean first = true;
                        for (final OpenCLArgDescriptor arg : args) {
                           if (first) {
                              first = false;
                           } else {
                              sourceBuilder.append(",");
                           }
                           sourceBuilder.append("\n   " + arg);
                        }

                        sourceBuilder.append(")");
                        final Kernel kernel = (Kernel) a;
                        sourceBuilder.append(kernel.value());
                        kernelNameToArgsMap.put(m.getName(), args);

                     }
                  }
               }
            }

         }
         _source = sourceBuilder.toString();
      } else {
         for (final Method m : _interface.getDeclaredMethods()) {
            if (!isReservedInterfaceMethod(m)) {
               final List<OpenCLArgDescriptor> args = getArgs(m);
               kernelNameToArgsMap.put(m.getName(), args);
            }
         }
      }

      final OpenCLProgram program = new OpenCLProgram(this, _source).createProgram(this);

      final Map<String, OpenCLKernel> map = new HashMap<String, OpenCLKernel>();
      for (final String name : kernelNameToArgsMap.keySet()) {
         final OpenCLKernel kernel = OpenCLKernel.createKernel(program, name, kernelNameToArgsMap.get(name));
         //final OpenCLKernel kernel = new OpenCLKernel(program, name, kernelNameToArgsMap.get(name));
         if (kernel == null) {
            throw new IllegalStateException("kernel is null");
         }

         map.put(name, kernel);
      }

      final OpenCLInvocationHandler<T> invocationHandler = new OpenCLInvocationHandler<T>(program, map);
      final T instance = (T) Proxy.newProxyInstance(OpenCLDevice.class.getClassLoader(), new Class[] {
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

   /** List OpenCLDevices of a given TYPE, or all OpenCLDevices if type == null. */
   public static List<OpenCLDevice> listDevices(TYPE type) {
      final OpenCLPlatform platform = new OpenCLPlatform(0, null, null, null);
      final ArrayList<OpenCLDevice> results = new ArrayList<>();

      for (final OpenCLPlatform p : platform.getOpenCLPlatforms()) {
         for (final OpenCLDevice device : p.getOpenCLDevices()) {
            if (type == null || device.getType() == type) {
               results.add(device);
            }
         }
      }

      return results;
   }

   public static OpenCLDevice select(DeviceSelector _deviceSelector) {
      OpenCLDevice device = null;
      final OpenCLPlatform platform = new OpenCLPlatform(0, null, null, null);

      //!!! oren change 2.15.15 -> allow choosing a platform when multiple platforms are available
      // Currently aparapi does not offer a way to choose a platform
      //for (final OpenCLPlatform p : platform.getOpenCLPlatforms()) {
      for (final OpenCLPlatform p : platform.getOpenCLPlatformsFilteredByConfig()) {
         for (final OpenCLDevice d : p.getOpenCLDevices()) {
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
      final OpenCLPlatform platform = new OpenCLPlatform(0, null, null, null);

      List<OpenCLPlatform> openCLPlatforms = platform.getOpenCLPlatforms();
      for (final OpenCLPlatform p : openCLPlatforms) {
         List<OpenCLDevice> openCLDevices = p.getOpenCLDevices();
         for (final OpenCLDevice d : openCLDevices) {
            if (device == null) {
               device = d;
            } else {
               device = _deviceComparitor.select(device, d);
            }
         }
      }

      return (device);
   }

   public static OpenCLDevice select(DeviceComparitor _deviceComparitor, Device.TYPE _type) {
      OpenCLDevice device = null;
      final OpenCLPlatform platform = new OpenCLPlatform(0, null, null, null);

      //!!! oren change 2.15.15 -> allow choosing a platform when multiple platforms are available
      // Currently aparapi does not offer a way to choose a platform
      //for (final OpenCLPlatform p : platform.getOpenCLPlatforms()) {
       for (final OpenCLPlatform p : platform.getOpenCLPlatformsFilteredByConfig()) {
         for (final OpenCLDevice d : p.getOpenCLDevices()) {
            if (d.getType() != _type) continue;
            if (device == null) {
               device = d;
            } else {
               device = _deviceComparitor.select(device, d);
            }
         }
      }

      return (device);
   }

   @Override public String toString() {
      final StringBuilder s = new StringBuilder("{");
      boolean first = true;
      for (final int workItemSize : maxWorkItemSize) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         s.append(workItemSize);
      }

      s.append("}");

      return ("Device " + deviceId + "\n  vendor = " + getOpenCLPlatform().getVendor()
            + "\n  type:" + type + "\n  maxComputeUnits=" + maxComputeUnits + "\n  maxWorkItemDimensions="
            + maxWorkItemDimensions + "\n  maxWorkItemSizes=" + s + "\n  maxWorkWorkGroupSize=" + maxWorkGroupSize
            + "\n  globalMemSize=" + globalMemSize + "\n  localMemSize=" + localMemSize);
   }
}
