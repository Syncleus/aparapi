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

public class UnrelatedIfsWithCommonEndByte {
    int width = 1024;

    float scale = 1f;

    int maxIterations = 10;

    public void run() {
        boolean a1 = true;
        boolean a2 = true;
        boolean b = false;
        boolean c = true;
        boolean outer = true;
        @SuppressWarnings("unused") boolean result = false;
        if (outer) {
            if (a1 && !a2) {
                // result = true;
                if (b) {
                    result = true;
                }
                //result = false;
                if (c) {
                    result = true;
                }
                //  result = false;
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
 char a1 = 1;
 char a2 = 1;
 char b = 0;
 char c = 1;
 char outer = 1;
 char result = 0;
 if (outer!=0 && a1!=0 && a2==0){
 if (b!=0){
 result = 1;
 }
 if (c!=0){
 result = 1;
 }
 }
 return;
 }
 }
 }OpenCL}**/
