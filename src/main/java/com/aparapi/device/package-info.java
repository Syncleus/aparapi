/**
 * Copyright (c) 2016 - 2018 Syncleus, Inc.
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
/**
 * Contains classes representing OpenCL-capable devices, and "virtual" (java) devices which execute kernels using java.
 *
 * <p>Various methods of {@link com.aparapi.device.Device} which selected devices of a particular type have been deprecated,
 * as now the preferred mechanism for device selection is to rely on the {@link com.aparapi.internal.kernel.KernelManager} to
 * select an appropriate device. Where a particular device is required to be used for a certain kernel, for such purposes as
 * debugging or unit testing, this can be achieved by using
 * {@link com.aparapi.internal.kernel.KernelManager#setKernelManager(com.aparapi.internal.kernel.KernelManager)} prior to
 * invoking any Kernel executions, by overriding {@link com.aparapi.Kernel#isAllowDevice(com.aparapi.device.Device)}
  * to veto/approve devices from the available devices for a given Kernel class, or (not recommended) by using
 * {@link com.aparapi.internal.kernel.KernelManager#setPreferredDevices(com.aparapi.Kernel, java.util.LinkedHashSet)} to specify
 * a particular device list for a given Kernel class.
 *
 * <p>In order to determine the Device which will be used to execute a particular Kernel, use {@link com.aparapi.Kernel#getTargetDevice()}.
 * This can also be used immediately after execution to see on which device the kernel actually got executed (in case the execution failed
 * and fell back to another device).
 *
 */
package com.aparapi.device;