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

public class AccessBooleanArray {
    private static final Logger LOGGER = Logger.getLogger(AccessBooleanArray.class);
    boolean[] ba = new boolean[1024];

    public void run() {
        for (int i = 0; i < 1024; i++) {
            if (i % 2 == 0) {
                ba[i] = true;
            } else {
                ba[i] = false;
            }
        }
    }
}
/**{OpenCL{
 typedef struct This_s{
 __global char  *ba;
 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }

 __kernel void run(
 __global char  *ba,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->ba = ba;
 this->passid = passid;
 {
 for (int i = 0; i<1024; i++){
 if ((i % 2)==0){
 this->ba[i]  = 1;
 } else {
 this->ba[i]  = 0;
 }
 }
 return;
 }
 }
 }OpenCL}**/
