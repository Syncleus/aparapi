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

public class If_While_Else_While {
    private static final Logger LOGGER = Logger.getLogger(If_While_Else_While.class);

    public void run() {
        boolean a = true;

        if (a) {
            while (a) {
                a = false;
            }
        } else {
            while (!a) {
                a = true;
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
 if (a!=0){
 for (; a!=0; a = 0){
 }
 } else {
 for (; a==0; a = 1){
 }
 }
 return;
 }
 }
 }OpenCL}**/
