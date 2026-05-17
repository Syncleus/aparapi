/**
 * Copyright (c) 2016 - 2018 Syncleus, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.aparapi.codegen.test;

import org.junit.Test;

public class OpenCLReservedLocalVariableTest extends com.aparapi.codegen.CodeGenJUnitBase {

    private static final String[] expectedOpenCL = {
        "typedef struct This_s{\n"
        + "\n"
        + " int passid;\n"
        + " }This;\n"
        + " int get_pass_id(This *this){\n"
        + " return this->passid;\n"
        + " }\n"
        + "\n"
        + " __kernel void run(\n"
        + " int passid\n"
        + " ){\n"
        + " This thisStruct;\n"
        + " This* this=&thisStruct;\n"
        + " this->passid = passid;\n"
        + " {\n"
        + " int kernel$ = 1;\n"
        + " int global$ = kernel$ + 1;\n"
        + " kernel$ = global$;\n"
        + " return;\n"
        + " }\n"
        + " }\n"
        + " "};
    private static final Class<? extends com.aparapi.internal.exception.AparapiException> expectedException = null;

    @Test
    public void OpenCLReservedLocalVariableTest() {
        test(com.aparapi.codegen.test.OpenCLReservedLocalVariable.class, expectedException, expectedOpenCL);
    }
}
