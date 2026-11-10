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
package com.aparapi.codegen;

import com.aparapi.Kernel;
import com.aparapi.internal.model.ClassModel;
import com.aparapi.internal.writer.KernelWriter;
import com.aparapi.runtime.LocalMessagePassingTest.RingKernel;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LocalMessagePassingCodeGenTest {
    @Test public void emitsLocalPointersAndBothReceiveFences() throws Exception {
        RingKernel kernel = new RingKernel(4, 4, 3);
        String code = KernelWriter.writeToString(ClassModel.createClassModel(RingKernel.class)
                .getEntrypoint("run", kernel));
        assertTrue(code, code.contains("void aparapi_local_send(__local int *mailbox, int slot, int value)"));
        assertTrue(code, code.contains("int aparapi_local_receive(__local int *mailbox, int slot)"));
        assertTrue(code, code.contains("mailbox[slot] = value;"));
        assertTrue(code, code.contains("barrier(CLK_LOCAL_MEM_FENCE);\n   int value = mailbox[slot];\n   barrier(CLK_LOCAL_MEM_FENCE);"));
        assertTrue(code, code.contains("aparapi_local_send(this->mailbox,"));
        assertTrue(code, code.contains("aparapi_local_receive(this->mailbox,"));
    }

    public static class HelperKernel extends Kernel {
        @Local int[] mailbox = new int[4];
        int[] output = new int[4];
        int exchange(int value) {
            localSend(mailbox, getLocalId(), value);
            return localReceive(mailbox, (getLocalId() + 1) % getLocalSize());
        }
        @Override public void run() { output[getGlobalId()] = exchange(getGlobalId()); }
    }

    @Test public void emitsHelpersForCallsInReachableMethods() throws Exception {
        String code = KernelWriter.writeToString(ClassModel.createClassModel(HelperKernel.class)
                .getEntrypoint("run", new HelperKernel()));
        assertTrue(code, code.contains("void aparapi_local_send(__local int *mailbox"));
        assertTrue(code, code.contains("int aparapi_local_receive(__local int *mailbox"));
        assertTrue(code, code.contains("aparapi_local_send(this->mailbox,"));
        assertTrue(code, code.contains("aparapi_local_receive(this->mailbox,"));
    }

    public static class PlainKernel extends Kernel {
        int[] output = new int[1];
        @Override public void run() { output[0] = 1; }
    }

    @Test public void unrelatedKernelsDoNotEmitMessageHelpers() throws Exception {
        String code = KernelWriter.writeToString(ClassModel.createClassModel(PlainKernel.class)
                .getEntrypoint("run", new PlainKernel()));
        assertFalse(code, code.contains("aparapi_local_"));
    }
}
