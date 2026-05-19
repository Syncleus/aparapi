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
package com.aparapi.codegen.test;

import org.junit.Test;

public class LocalMessagePassingTest extends com.aparapi.codegen.CodeGenJUnitBase {
   private static final String[] expectedOpenCL = {
      "int localExchange(__local int *_mailbox, int _targetLocalId, int _value){\n" +
      " _mailbox[get_local_id(0)] = _value;\n" +
      " barrier(CLK_LOCAL_MEM_FENCE);\n" +
      " int value = _mailbox[_targetLocalId];\n" +
      " barrier(CLK_LOCAL_MEM_FENCE);\n" +
      " return value;\n" +
      " }\n" +
      " int localBroadcast(__local int *_mailbox, int _sourceLocalId, int _value){\n" +
      " if (get_local_id(0)==_sourceLocalId){\n" +
      " _mailbox[0] = _value;\n" +
      " }\n" +
      " barrier(CLK_LOCAL_MEM_FENCE);\n" +
      " int value = _mailbox[0];\n" +
      " barrier(CLK_LOCAL_MEM_FENCE);\n" +
      " return value;\n" +
      " }\n" +
      "\n" +
      " typedef struct This_s{\n" +
      " __global int *values;\n" +
      " __local int *mailbox;\n" +
      " int passid;\n" +
      " }This;\n" +
      " int get_pass_id(This *this){\n" +
      " return this->passid;\n" +
      " }\n" +
      " __kernel void run(\n" +
      " __global int *values, \n" +
      " __local int *mailbox, \n" +
      " int passid\n" +
      " ){\n" +
      " This thisStruct;\n" +
      " This* this=&thisStruct;\n" +
      " this->values = values;\n" +
      " this->mailbox = mailbox;\n" +
      " this->passid = passid;\n" +
      " {\n" +
      " int localId = get_local_id(0);\n" +
      " int value = this->values[get_global_id(0)];\n" +
      " int exchangeValue = localExchange(this->mailbox, ((localId + 1) & 3), value);\n" +
      " int broadcastValue = localBroadcast(this->mailbox, 0, value);\n" +
      " this->values[get_global_id(0)]  = exchangeValue + broadcastValue;\n" +
      " return;\n" +
      " }\n" +
      " }\n" +
      " "
   };
   private static final Class<? extends com.aparapi.internal.exception.AparapiException> expectedException = null;

   @Test
   public void LocalMessagePassingTest() {
      test(com.aparapi.codegen.test.LocalMessagePassing.class, expectedException, expectedOpenCL);
   }

   @Test
   public void LocalMessagePassingTestWorksWithCaching() {
      test(com.aparapi.codegen.test.LocalMessagePassing.class, expectedException, expectedOpenCL);
   }
}
