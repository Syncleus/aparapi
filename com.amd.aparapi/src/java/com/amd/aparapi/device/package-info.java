/**
 * Contains classes representing OpenCL-capable devices, and "virtual" (java) devices which execute kernels using java.
 *
 * <p>Various methods of {@link com.amd.aparapi.device.Device} which selected devices of a particular type have been deprecated,
 * as now the preferred mechanism for device selection is to rely on the {@link com.amd.aparapi.internal.kernel.KernelManager} to
 * select an appropriate device. Where a particular device is required to be used for a certain kernel, for such purposes as
 * debugging or unit testing, this can be achieved by using
 * {@link com.amd.aparapi.internal.kernel.KernelManager#setKernelManager(com.amd.aparapi.internal.kernel.KernelManager)} prior to
 * invoking any Kernel executions, by overriding {@link com.amd.aparapi.Kernel#isAllowDevice(com.amd.aparapi.device.Device)}
  * to veto/approve devices from the available devices for a given Kernel class, or (not recommended) by using
 * {@link com.amd.aparapi.internal.kernel.KernelManager#setPreferredDevices(com.amd.aparapi.Kernel, java.util.LinkedHashSet)} to specify
 * a particular device list for a given Kernel class.
 *
 * <p>In order to determine the Device which will be used to execute a particular Kernel, use {@link com.amd.aparapi.Kernel#getTargetDevice()}.
 * This can also be used immediately after execution to see on which device the kernel actually got executed (in case the execution failed
 * and fell back to another device).
 *
 */
package com.amd.aparapi.device;