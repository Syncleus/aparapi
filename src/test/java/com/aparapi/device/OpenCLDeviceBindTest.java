package com.aparapi.device;

import com.aparapi.Range;
import com.aparapi.internal.opencl.OpenCLArgDescriptor;
import com.aparapi.opencl.OpenCL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.StreamSupport;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static com.aparapi.internal.opencl.OpenCLArgDescriptor.*;

@RunWith(Parameterized.class)
public class OpenCLDeviceBindTest {

    public static final String TEST = "test";

    public OpenCLDeviceBindTest(Method method, List<OpenCLArgDescriptor> expectedDescriptors) {
        this.method = method;
        this.expectedDescriptors = expectedDescriptors;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
//            {methodByName("rangeTest"), new ArrayList<>()},
            {methodByName("globalReadOnlyAnnotationTest"), singletonList(descriptor(ARG_GLOBAL_BIT, ARG_READONLY_BIT))},
            {methodByName("globalWriteOnlyAnnotationTest"), singletonList(descriptor(ARG_GLOBAL_BIT, ARG_WRITEONLY_BIT))},
            {methodByName("globalReadWriteAnnotationTest"), singletonList(descriptor(ARG_GLOBAL_BIT, ARG_READWRITE_BIT))},
            {methodByName("localAnnotationTest"), singletonList(descriptor(ARG_LOCAL_BIT))},
            {methodByName("constantAnnotationTest"), singletonList(descriptor(ARG_CONST_BIT, ARG_READONLY_BIT))},
            {methodByName("argAnnotationTest"), singletonList(descriptor(ARG_ISARG_BIT))},


        });
    }
    private final Method method;
    private final List<OpenCLArgDescriptor> expectedDescriptors;

    @Test
    public void shouldReturnCorrespondingDescriptorsForTests(){
        OpenCLDevice sut = Utils.createDevice(Utils.createPlatform("Intel (R)"), Device.TYPE.CPU);
        assertEquals(expectedDescriptors, sut.getArgs(method));
    }


    private static class MethodsForTests{
    public void rangeTest(Range parameter){}
    public void globalReadOnlyAnnotationTest(@OpenCL.GlobalReadOnly(TEST) String parameter){}
    public void globalWriteOnlyAnnotationTest(@OpenCL.GlobalWriteOnly(TEST) String parameter){}
    public void globalReadWriteAnnotationTest(@OpenCL.GlobalReadWrite(TEST) String parameter){}
        public void localAnnotationTest(@OpenCL.Local(TEST) String parameter){}
        public void constantAnnotationTest(@OpenCL.Constant(TEST) String parameter){}
        public void argAnnotationTest(@OpenCL.Arg(TEST) String parameter){}
        public void floatArrayTest(@OpenCL.Arg(TEST)float[] parameter){}
        public void intArrayTest(@OpenCL.Arg(TEST)int[] parameter){}
        public void doubleArrayTest(@OpenCL.Arg(TEST)double[] parameter){}
        public void byteArrayTest(@OpenCL.Arg(TEST)byte[] parameter){}
        public void shortArrayTest(@OpenCL.Arg(TEST)short[] parameter){}
        public void longArrayTest(@OpenCL.Arg(TEST)long[] parameter){}
        public void floatTest(@OpenCL.Arg(TEST)float parameter){}
        public void intTest(@OpenCL.Arg(TEST)int parameter){}
        public void doubleTest(@OpenCL.Arg(TEST)double parameter){}
        public void byteTest(@OpenCL.Arg(TEST)byte parameter){}
        public void shortTest(@OpenCL.Arg(TEST)short parameter){}
        public void longTest(@OpenCL.Arg(TEST)long parameter){}
    }
   private static Method methodByName(String name){
        return Arrays.stream(MethodsForTests.class.getMethods())
            .filter(m->m.getName().equals(name))
            .findFirst().orElseThrow(()->new RuntimeException("method with name not found "+name));
    }
    private static OpenCLArgDescriptor descriptor(int... bitList){
        long bits = Arrays.stream(bitList).asLongStream().reduce(0L, (a, b) -> a | b);
        return new OpenCLArgDescriptor(TEST, bits);
    }
    private static List<OpenCLArgDescriptor> list(OpenCLArgDescriptor... descriptors){
        return Arrays.asList(descriptors);
    }
}
