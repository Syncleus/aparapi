/**
 * Copyright (c) 2016 - 2018 Syncleus, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aparapi.internal.kernel;

import com.aparapi.opencl.OpenCL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.internal.util.collections.Sets;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class KernelRunnerCheckPropertiesSetTest {
    private final String methodName;
    private final String capability;

    public KernelRunnerCheckPropertiesSetTest(String methodName, String capability) {
        this.methodName = methodName;
        this.capability = capability;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return asList(new Object[][]{
            {"hasFP64Support", OpenCL.CL_KHR_FP64},
            {"hasSelectFPRoundingModeSupport", OpenCL.CL_KHR_SELECT_FPROUNDING_MODE},
            {"hasGlobalInt32BaseAtomicsSupport", OpenCL.CL_KHR_GLOBAL_INT32_BASE_ATOMICS},
            {"hasGlobalInt32ExtendedAtomicsSupport", OpenCL.CL_KHR_GLOBAL_INT32_EXTENDED_ATOMICS},
            {"hasLocalInt32BaseAtomicsSupport", OpenCL.CL_KHR_LOCAL_INT32_BASE_ATOMICS},
            {"hasLocalInt32ExtendedAtomicsSupport", OpenCL.CL_KHR_LOCAL_INT32_EXTENDED_ATOMICS},
            {"hasInt64BaseAtomicsSupport", OpenCL.CL_KHR_INT64_BASE_ATOMICS},
            {"hasInt64ExtendedAtomicsSupport", OpenCL.CL_KHR_INT64_EXTENDED_ATOMICS},
            {"has3DImageWritesSupport", OpenCL.CL_KHR_3D_IMAGE_WRITES},
            {"hasByteAddressableStoreSupport", OpenCL.CL_KHR_BYTE_ADDRESSABLE_SUPPORT},
            {"hasFP16Support", OpenCL.CL_KHR_FP16},
            {"hasGLSharingSupport", OpenCL.CL_KHR_GL_SHARING}
        });
    }

    @Test(expected = InvocationTargetException.class)
    public void shouldThrowAnExceptionIfCapabilitiesSetIsNull() throws Exception {
        KernelRunner sut = Utils.createKernelRunner();
        invokeMethod(sut);
    }

    @Test
    public void shouldReturnFalseIfCapabilitiesSetIsEmpty() throws Exception {
        KernelRunner sut = Utils.createKernelRunner();
        Utils.setFieldValue(sut, "capabilitiesSet", new HashSet<>());
        assertFalse(invokeMethod(sut));
    }

    @Test
    public void shouldReturnTrueIfCapabilityIsSet() throws Exception {
        KernelRunner sut = Utils.createKernelRunner();
        Utils.setFieldValue(sut, "capabilitiesSet", Sets.newSet(capability));
        assertTrue(invokeMethod(sut));
    }

    private boolean invokeMethod(KernelRunner sut) throws Exception {
        Method method = KernelRunner.class.getDeclaredMethod(methodName);
        return (boolean) method.invoke(sut);
    }
}

