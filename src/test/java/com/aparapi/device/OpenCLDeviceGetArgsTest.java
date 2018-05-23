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
package com.aparapi.device;

import com.aparapi.Range;
import com.aparapi.internal.opencl.OpenCLArgDescriptor;
import com.aparapi.opencl.OpenCL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.aparapi.internal.opencl.OpenCLArgDescriptor.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class OpenCLDeviceGetArgsTest {

    static final String TEST = "test";

    public OpenCLDeviceGetArgsTest(Method method, List<OpenCLArgDescriptor> expectedDescriptors) {
        this.method = method;
        this.expectedDescriptors = expectedDescriptors;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return asList(new Object[][]{
            {methodByName("rangeTest"), new ArrayList<>()},
            {methodByName("globalReadOnlyAnnotationTest"), singletonList(descriptor(ARG_GLOBAL_BIT, ARG_READONLY_BIT))},
            {methodByName("globalWriteOnlyAnnotationTest"), singletonList(descriptor(ARG_GLOBAL_BIT, ARG_WRITEONLY_BIT))},
            {methodByName("globalReadWriteAnnotationTest"), singletonList(descriptor(ARG_GLOBAL_BIT, ARG_READWRITE_BIT))},
            {methodByName("localAnnotationTest"), singletonList(descriptor(ARG_LOCAL_BIT))},
            {methodByName("constantAnnotationTest"), singletonList(descriptor(ARG_CONST_BIT, ARG_READONLY_BIT))},
            {methodByName("argAnnotationTest"), singletonList(descriptor(ARG_ISARG_BIT))},
            {methodByName("floatArrayTest"), singletonList(descriptor(ARG_ISARG_BIT, ARG_FLOAT_BIT, ARG_ARRAY_BIT))},
            {methodByName("intArrayTest"), singletonList(descriptor(ARG_ISARG_BIT, ARG_INT_BIT, ARG_ARRAY_BIT))},
            {methodByName("doubleArrayTest"), singletonList(descriptor(ARG_ISARG_BIT, ARG_DOUBLE_BIT, ARG_ARRAY_BIT))},
            {methodByName("byteArrayTest"), singletonList(descriptor(ARG_ISARG_BIT, ARG_BYTE_BIT, ARG_ARRAY_BIT))},
            {methodByName("shortArrayTest"), singletonList(descriptor(ARG_ISARG_BIT, ARG_SHORT_BIT, ARG_ARRAY_BIT))},
            {methodByName("longArrayTest"), singletonList(descriptor(ARG_ISARG_BIT, ARG_LONG_BIT, ARG_ARRAY_BIT))},
            {methodByName("floatTest"), singletonList(descriptor(ARG_ISARG_BIT, ARG_FLOAT_BIT, ARG_PRIMITIVE_BIT))},
            {methodByName("intTest"), singletonList(descriptor(ARG_ISARG_BIT, ARG_INT_BIT, ARG_PRIMITIVE_BIT))},
            {methodByName("doubleTest"), singletonList(descriptor(ARG_ISARG_BIT, ARG_DOUBLE_BIT, ARG_PRIMITIVE_BIT))},
            {methodByName("byteTest"), singletonList(descriptor(ARG_ISARG_BIT, ARG_BYTE_BIT, ARG_PRIMITIVE_BIT))},
            {methodByName("shortTest"), singletonList(descriptor(ARG_ISARG_BIT, ARG_SHORT_BIT, ARG_PRIMITIVE_BIT))},
            {methodByName("longTest"), singletonList(descriptor(ARG_ISARG_BIT, ARG_LONG_BIT, ARG_PRIMITIVE_BIT))},
            {methodByName("multipleParametersTest"), asList(descriptor(ARG_ISARG_BIT, ARG_LONG_BIT, ARG_PRIMITIVE_BIT), descriptor(ARG_LOCAL_BIT))}
        });
    }

    private final Method method;
    private final List<OpenCLArgDescriptor> expectedDescriptors;

    @Test
    public void shouldReturnCorrespondingDescriptorsForTests() {
        OpenCLDevice sut = Utils.createDevice(Utils.createPlatform("Intel (R)"), Device.TYPE.CPU);
        assertEquals(expectedDescriptors, sut.getArgs(method));
    }

    private static class MethodsForTests {
        public void rangeTest(Range parameter) {
        }

        public void globalReadOnlyAnnotationTest(@OpenCL.GlobalReadOnly(TEST) String parameter) {
        }

        public void globalWriteOnlyAnnotationTest(@OpenCL.GlobalWriteOnly(TEST) String parameter) {
        }

        public void globalReadWriteAnnotationTest(@OpenCL.GlobalReadWrite(TEST) String parameter) {
        }

        public void localAnnotationTest(@OpenCL.Local(TEST) String parameter) {
        }

        public void constantAnnotationTest(@OpenCL.Constant(TEST) String parameter) {
        }

        public void argAnnotationTest(@OpenCL.Arg(TEST) String parameter) {
        }

        public void floatArrayTest(@OpenCL.Arg(TEST) float[] parameter) {
        }

        public void intArrayTest(@OpenCL.Arg(TEST) int[] parameter) {
        }

        public void doubleArrayTest(@OpenCL.Arg(TEST) double[] parameter) {
        }

        public void byteArrayTest(@OpenCL.Arg(TEST) byte[] parameter) {
        }

        public void shortArrayTest(@OpenCL.Arg(TEST) short[] parameter) {
        }

        public void longArrayTest(@OpenCL.Arg(TEST) long[] parameter) {
        }

        public void floatTest(@OpenCL.Arg(TEST) float parameter) {
        }

        public void intTest(@OpenCL.Arg(TEST) int parameter) {
        }

        public void doubleTest(@OpenCL.Arg(TEST) double parameter) {
        }

        public void byteTest(@OpenCL.Arg(TEST) byte parameter) {
        }

        public void shortTest(@OpenCL.Arg(TEST) short parameter) {
        }

        public void longTest(@OpenCL.Arg(TEST) long parameter) {
        }

        public void multipleParametersTest(@OpenCL.Arg(TEST) long parameter1, @OpenCL.Local(TEST) String parameter2) {
        }

        public void noAnnotationForParameterTest(long parameter) {
        }
    }

    private static OpenCLArgDescriptor descriptor(int... bitList) {
        long bits = Arrays.stream(bitList).asLongStream().reduce(0L, (a, b) -> a | b);
        return new OpenCLArgDescriptor(TEST, bits);
    }

    private static Method methodByName(String name) {
        return Utils.methodByName(name, MethodsForTests.class);
    }
}
