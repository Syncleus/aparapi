package com.aparapi.internal.opencl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertEquals;

@RunWith(Parameterized.class)
public class OpenCLArgDescriptorTest {
    public static final String NAME = "test";
    private final String name;
    private final long bits;
    private final String expectedToString;

    public OpenCLArgDescriptorTest(String name, long bits, String expectedToString) {
        this.name = name;
        this.bits = bits;
        this.expectedToString = expectedToString;
    }


    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return asList(new Object[][]{
            {NAME, 1 << 0x008, "__global test"},
            {NAME, 1 << 0x000, "WHATISTHIS?test"},
            {NAME, 1 << 0x001, "WHATISTHIS?short test"},
            {NAME, 1 << 0x002, "WHATISTHIS?int test"},
            {NAME, 1 << 0x003, "WHATISTHIS?float test"},
            {NAME, 1 << 0x009, "__local test"},
            {NAME, 1 << 0x00A, "__constant test"},
            {NAME, 1 << 0x00B, "WHATISTHIS?test /* readonly */"},
            {NAME, 1 << 0x00C, "WHATISTHIS?test /* writeonly */"},
            {NAME, 1 << 0x00D, "WHATISTHIS?test /* readwrite */"},
            {NAME, 1 << 0x00E, "test"},
            {NAME, 0b1000000100, "__local int test"}
        });
    }

    @Test
    public void shouldReturnCorrectToStringValue() {
        OpenCLArgDescriptor sut = new OpenCLArgDescriptor(name, bits);
        assertEquals(expectedToString, sut.toString());
    }
}
