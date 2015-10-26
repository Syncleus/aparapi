package com.amd.aparapi.internal.kernel;

import com.amd.aparapi.*;
import com.amd.aparapi.device.*;
import com.amd.aparapi.internal.util.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * Created by Barney on 24/08/2015.
 */
public class KernelManager {

   private static KernelManager INSTANCE = new KernelManager();
   private LinkedHashMap<Class<? extends Kernel>, KernelPreferences> preferences = new LinkedHashMap<>();
   private LinkedHashMap<Class<? extends Kernel>, KernelProfile> profiles = new LinkedHashMap<>();
   private LinkedHashMap<Class<? extends Kernel>, Kernel> sharedInstances = new LinkedHashMap<>();

   private KernelPreferences defaultPreferences;

   protected KernelManager() {
      defaultPreferences = createDefaultPreferences();
   }

   public static KernelManager instance() {
      return INSTANCE;
   }

   public static void setKernelManager(KernelManager manager) {
      INSTANCE = manager;
   }

   static {
      if (Config.dumpProfilesOnExit) {
         Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
               StringBuilder builder = new StringBuilder(2048);
               instance().reportProfilingSummary(builder);
               System.out.println(builder);
            }
         });
      }
   }

   /** This method returns a shared instance of a given Kernel subclass. The kernelClass needs a no-args constructor, which
    *  need not be public.
    *
    *  <p>Each new Kernel instance requires a new JNIContext, the creation of which is expensive. There is apparently no simple solution by which a cached JNIContext can be reused
    *  for all instances of a given Kernel class, since it is intimately connected with resource aquisition and release. In the absence of a context caching solution, it is often
    *  highly desirable to only ever create one instance of any given Kernel subclass, which this method facilitates.</p>
    *
    *  <p>In order to maintain thread saftey when using a shared instance, it is necessary to synchronize on the returned kernel for the duration of the process of setting up,
    *  executing and extracting the results from that kernel.</p>
    *
    *  <p>This method instantiates a Kernel (per Kernel class) via Reflection, and thus can only be used where the Kernel class has a no-args constructor, which need not be public.
    *  In fact, if a Kernel subclass is designed to be used in conjunction with this method, it is recommended that its <b>only</b> constructor is a <b>private</b> no-args constructor.
    *  </p>
    *
    *  @throws RuntimeException if the class cannot be instantiated
    */
   public static <T extends Kernel> T sharedKernelInstance(Class<T> kernelClass) {
       return instance().getSharedKernelInstance(kernelClass);
   }

   /** Append a report to {@code builder} which contains information, per Kernel subclass, on which device is currently being used for the
    * kernel class, and which (if any) devices failed to execute a given Kernel.
    */
   public void reportDeviceUsage(StringBuilder builder, boolean withProfilingInfo) {
      builder.append("Device Usage by Kernel Subclass");
      if (withProfilingInfo) {
         builder.append(" (showing mean elapsed times in milliseconds)");
      }
      builder.append("\n\n");
      for (Class<? extends Kernel> klass : preferences.keySet()) {
         KernelPreferences preferences = this.preferences.get(klass);
         KernelProfile profile = withProfilingInfo ? profiles.get(klass) : null;
         builder.append(klass.getName()).append(":\n\tusing ").append(preferences.getPreferredDevice(null).getShortDescription());
         List<Device> failedDevices = preferences.getFailedDevices();
         if (failedDevices.size() > 0) {
            builder.append(", failed devices = ");
            for (int i = 0; i < failedDevices.size(); ++i) {
               builder.append(failedDevices.get(i).getShortDescription());
               if (i < failedDevices.size() - 1) {
                  builder.append(" | ");
               }
            }
         }
         if (profile != null) {
            builder.append("\n");
            int row = 0;
            for (KernelDeviceProfile deviceProfile : profile.getDeviceProfiles()) {
               if (row == 0) {
                  builder.append(deviceProfile.getTableHeader()).append("\n");
               }
               builder.append(deviceProfile.getAverageAsTableRow()).append("\n");
               ++row;
            }
         }
         builder.append("\n");
      }
   }

   public void reportProfilingSummary(StringBuilder builder) {
      builder.append("\nProfiles by Kernel Subclass (mean elapsed times in milliseconds)\n\n");
      builder.append(KernelDeviceProfile.getTableHeader()).append("\n");
      for (Class<? extends Kernel> kernelClass : profiles.keySet()) {
         String simpleName = Reflection.getSimpleName(kernelClass);
         String kernelName = "----------------- [[ " + simpleName + " ]] ";
         builder.append(kernelName);
         int dashes = 132 - kernelName.length();
         for (int i = 0; i < dashes; ++i) {
            builder.append('-');
         }
         builder.append("\n");
         KernelProfile kernelProfile = profiles.get(kernelClass);
         for (KernelDeviceProfile deviceProfile : kernelProfile.getDeviceProfiles()) {
            builder.append(deviceProfile.getAverageAsTableRow()).append("\n");
         }
      }
   }


   public KernelPreferences getPreferences(Kernel kernel) {
      synchronized (preferences) {
         KernelPreferences kernelPreferences = preferences.get(kernel.getClass());
         if (kernelPreferences == null) {
            kernelPreferences = new KernelPreferences(this, kernel.getClass());
            preferences.put(kernel.getClass(), kernelPreferences);
         }
         return kernelPreferences;
      }
   }

   public void setPreferredDevices(Kernel _kernel, LinkedHashSet<Device> _devices) {
      KernelPreferences kernelPreferences = getPreferences(_kernel);
      kernelPreferences.setPreferredDevices(_devices);
   }

   public KernelPreferences getDefaultPreferences() {
      return defaultPreferences;
   }

   public void setDefaultPreferredDevices(LinkedHashSet<Device> _devices) {
      defaultPreferences.setPreferredDevices(_devices);
   }

   protected KernelPreferences createDefaultPreferences() {
      KernelPreferences preferences = new KernelPreferences(this, null);
      preferences.setPreferredDevices(createDefaultPreferredDevices());
      return preferences;
   }

   private <T extends Kernel> T getSharedKernelInstance(Class<T> kernelClass) {
      synchronized (sharedInstances) {
         T shared = (T) sharedInstances.get(kernelClass);
         if (shared == null) {
            try {
               Constructor<T> constructor = kernelClass.getConstructor();
               constructor.setAccessible(true);
               shared = constructor.newInstance();
               sharedInstances.put(kernelClass, shared);
            }
            catch (Exception e) {
               throw new RuntimeException(e);
            }
         }
         return shared;
      }
   }

   protected LinkedHashSet<Device> createDefaultPreferredDevices() {
      LinkedHashSet<Device> devices = new LinkedHashSet<>();

      List<OpenCLDevice> accelerators = OpenCLDevice.listDevices(Device.TYPE.ACC);
      List<OpenCLDevice> gpus = OpenCLDevice.listDevices(Device.TYPE.GPU);
      List<OpenCLDevice> cpus = OpenCLDevice.listDevices(Device.TYPE.CPU);

      Collections.sort(accelerators, getDefaultAcceleratorComparator());
      Collections.sort(gpus, getDefaultGPUComparator());

      List<Device.TYPE> preferredDeviceTypes = getPreferredDeviceTypes();

      for (Device.TYPE type : preferredDeviceTypes) {
         switch (type) {
            case UNKNOWN:
               throw new AssertionError("UNKNOWN device type not supported");
            case GPU:
               devices.addAll(gpus);
               break;
            case CPU:
               devices.addAll(cpus);
               break;
            case JTP:
               devices.add(JavaDevice.THREAD_POOL);
               break;
            case SEQ:
               devices.add(JavaDevice.SEQUENTIAL);
               break;
            case ACC:
               devices.addAll(accelerators);
               break;
            case ALT:
               devices.add(JavaDevice.ALTERNATIVE_ALGORITHM);
               break;
         }
      }

      return devices;
   }

   protected List<Device.TYPE> getPreferredDeviceTypes() {
      return Arrays.asList(Device.TYPE.ACC, Device.TYPE.GPU, Device.TYPE.CPU, Device.TYPE.ALT, Device.TYPE.JTP);
   }

   /** NB, returns -ve for the better device. */
   protected Comparator<OpenCLDevice> getDefaultAcceleratorComparator() {
      return new Comparator<OpenCLDevice>() {
         @Override
         public int compare(OpenCLDevice left, OpenCLDevice right) {
            return (right.getMaxComputeUnits() - left.getMaxComputeUnits());
         }
      };
   }

   /** NB, returns -ve for the better device. */
   protected Comparator<OpenCLDevice> getDefaultGPUComparator() {
      return new Comparator<OpenCLDevice>() {
         @Override
         public int compare(OpenCLDevice left, OpenCLDevice right) {
            return selectLhs(left, right) ? -1 : 1;
         }
      };
   }

   public Device bestDevice() {
      return getDefaultPreferences().getPreferredDevice(null);
   }

    protected static boolean selectLhs(OpenCLDevice _deviceLhs, OpenCLDevice _deviceRhs) {
       boolean nvidiaLhs = _deviceLhs.getOpenCLPlatform().getVendor().toLowerCase().contains("nvidia");
       boolean nvidiaRhs = _deviceRhs.getOpenCLPlatform().getVendor().toLowerCase().contains("nvidia");
       if (nvidiaLhs || nvidiaRhs) {
          return selectLhsIfCUDA(_deviceLhs, _deviceRhs);
       }
       return _deviceLhs.getMaxComputeUnits() > _deviceRhs.getMaxComputeUnits();
    }

    /** NVidia/CUDA architecture reports maxComputeUnits in a completely different context, i.e. maxComputeUnits is not same as
     * (is much less than) the number of OpenCL cores available.
     *
     * <p>Therefore when comparing an NVidia device we use different criteria.</p>
     */
    protected static boolean selectLhsIfCUDA(OpenCLDevice _deviceLhs, OpenCLDevice _deviceRhs) {
       if (_deviceLhs.getType() != _deviceRhs.getType()) {
          return selectLhsByType(_deviceLhs.getType(), _deviceRhs.getType());
       }
       return _deviceLhs.getMaxWorkGroupSize() == _deviceRhs.getMaxWorkGroupSize()
               ? _deviceLhs.getGlobalMemSize() > _deviceRhs.getGlobalMemSize()
               : _deviceLhs.getMaxWorkGroupSize() > _deviceRhs.getMaxWorkGroupSize();
    }

   private static boolean selectLhsByType(Device.TYPE lhs, Device.TYPE rhs) {
      return lhs.rank < rhs.rank;
   }

   public KernelProfile getProfile(Class<? extends Kernel> kernelClass) {
      synchronized (profiles) {
         KernelProfile profile = profiles.get(kernelClass);
         if (profile == null) {
            profile = new KernelProfile(kernelClass);
            profiles.put(kernelClass, profile);
         }
         return profile;
      }
   }

   /** New home for deprecated methods of {@link Device}. */
   public static class DeprecatedMethods {

      @Deprecated
      public static Device firstDevice(Device.TYPE _type) {
         List<Device> devices = instance().getDefaultPreferences().getPreferredDevices(null);
         for (Device device : devices) {
            if(device.getType() == _type) {
               return device;
            }
         }
         return null;
      }

      @SuppressWarnings("deprecation")
      @Deprecated
      public static Device bestGPU() {
         return firstDevice(Device.TYPE.GPU);
      }

      @SuppressWarnings("deprecation")
      @Deprecated
      public static Device bestACC() {
         return firstDevice(Device.TYPE.ACC);
      }
   }
}
