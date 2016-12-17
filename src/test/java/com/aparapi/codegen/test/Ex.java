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
package com.aparapi.codegen.test;

public class Ex {
    public void run() {
        int total = 0;
        for (int i = 0; i < 100; i++) {
            if (i % 10 == 0 && i % 4 == 0) {
                total++;
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
 int total = 0;
 for (int i = 0; i<100; i++){
 if ((i % 10)==0 && (i % 4)==0){
 total++;
 }
 }
 return;
 }
 }
 }OpenCL}**/
