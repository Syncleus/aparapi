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
package com.aparapi.internal.opencl;

import com.aparapi.Config;
import com.aparapi.internal.jni.OpenCLJNI;
import com.aparapi.natives.NativeLoader;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is intended to be a singleton which determines if OpenCL is available upon startup of Aparapi
 */
public class OpenCLLoader extends OpenCLJNI {

    private static final Logger logger = Logger.getLogger(Config.getLoggerName());

    private static final boolean openCLAvailable;

    static {
        boolean openCLAvail = false;
        if (Config.useAgent) {
            logger.fine("Using agent!");
            openCLAvail = true;
        } else {
            try {
                NativeLoader.load();
                System.out.println("Aparapi JNI loaded successfully.");
                openCLAvail = true;
            } catch (final IOException e) {
                logger.log(Level.SEVERE, "Check your environment. Failed to load aparapi native library "
                        + " or possibly failed to locate opencl native library (opencl.dll/opencl.so)."
                        + " Ensure that OpenCL is in your PATH (windows) or in LD_LIBRARY_PATH (linux).");
            }
        }
        openCLAvailable = openCLAvail;
    }

    private static final OpenCLLoader instance = new OpenCLLoader();


    /**
     * Retrieve a singleton instance of OpenCLLoader
     *
     * @return A singleton instance of OpenCLLoader
     */
    protected static OpenCLLoader getInstance() {
        return instance;
    }

    /**
     * Retrieve the status of whether OpenCL was successfully loaded
     *
     * @return The status of whether OpenCL was successfully loaded
     */
    public static boolean isOpenCLAvailable() {
        return openCLAvailable;
    }
}
