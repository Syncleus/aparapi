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

public class UnrelatedIfElsesWithCommonEndByte {
    /*
    1:   istore_1   (0:iconst_1)
    3:   istore_2   (2:iconst_0)
    5:   istore_3   (4:iconst_1)
    7:   istore  4  (6:iconst_0)
    10:  ifeq    39 (9:iload_1)        ?
    14:  ifeq    23 (13:iload_2)       | ?
    18:  istore  4  (17:iconst_1)      | |
    20:  goto    26                    | | +
    24:  istore  4  (23:  iconst_0)    | v |
    27:  ifeq    36 (26:  iload_3)     |   v ?
    31:  istore  4  (30:  iconst_1)    |     |
    33:  goto    39                    |     | +
    37:  istore  4  (36:  iconst_0)    |     v |
    39:  return                        v       v
    */
    int width = 1024;

    float scale = 1f;

    int maxIterations = 10;

    public void run() {
        boolean a = true;
        boolean b = false;
        boolean c = true;
        @SuppressWarnings("unused") boolean result = false;

        if (a) {
            if (b) {
                result = true;
            } else {
                result = false;
            }

            if (c) {
                result = true;
            } else {
                result = false;
            }
        }

    }

}
/**{OpenCL{
 typedef struct This_s{

 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }

 __kernel void run(
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->passid = passid;
 {
 char a = 1;
 char b = 0;
 char c = 1;
 char result = 0;
 if (a!=0){
 if (b!=0){
 result = 1;
 } else {
 result = 0;
 }
 if (c!=0){
 result = 1;
 } else {
 result = 0;
 }
 }
 return;
 }
 }
 }OpenCL}**/
