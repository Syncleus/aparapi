/**
 * Copyright (c) 2016 - 2018 Syncleus, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.aparapi.codegen.test;

public class OpenCLReservedLocalVariable {

    public void run() {
        int kernel = 1;
        int global = kernel + 1;
        kernel = global;
    }
}
