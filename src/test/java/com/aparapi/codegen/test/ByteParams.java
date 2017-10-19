/**
 * Copyright (c) 2016 - 2017 Syncleus, Inc.
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
package com.aparapi.codegen.test;

import org.apache.log4j.Logger;

public class ByteParams {
    private static final Logger LOGGER = Logger.getLogger(ByteParams.class);

    // input for addEmUp2
    byte[] test = new byte[4];

    byte addEmUp2(byte x, byte y) {
        return (byte) ((byte) x + (byte) y);
    }

    public void run() {

        @SuppressWarnings("unused") byte bb = 0;
        byte cc = 7;

        addEmUp2((bb = cc), cc);
    }
}
/**{OpenCL{
 typedef struct This_s{

 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }

 char com_amd_aparapi_test_ByteParams__addEmUp2(This *this, char x, char y){
 return((char )(x + y));
 }
 __kernel void run(
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->passid = passid;
 {
 char bb = 0;
 char cc = 7;
 com_amd_aparapi_test_ByteParams__addEmUp2(this, bb = cc, cc);
 return;
 }
 }
 }OpenCL}**/
