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

public class ClassHasStaticMethod {
    private static final Logger LOGGER = Logger.getLogger(ClassHasStaticMethod.class);
    int[] ints = new int[1024];

    static int getIntAndReturnIt(int a) {
        return (int) (((int) 1) - a);
    }

    public void run() {
        int foo = 1;
        for (int i = 0; i < 1024; i++) {
            if (i % 2 == 0) {
                ints[i] = foo;
            } else {
                ints[i] = getIntAndReturnIt(foo);
                ;
            }
        }
    }
}
/**{OpenCL{
 typedef struct This_s{
 __global int *ints;
 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }
 int com_amd_aparapi_test_ClassHasStaticMethod__getIntAndReturnIt(int a){
 return((1 - a));
 }
 __kernel void run(
 __global int *ints,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->ints = ints;
 this->passid = passid;
 {
 int foo = 1;
 for (int i = 0; i<1024; i++){
 if ((i % 2)==0){
 this->ints[i] = foo;
 } else {
 this->ints[i] = com_amd_aparapi_test_ClassHasStaticMethod__getIntAndReturnIt(foo);
 }
 }
 return;
 }
 }
 }OpenCL}**/

