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

public class IfElse_Or_And_Or {
    private static final Logger LOGGER = Logger.getLogger(IfElse_Or_And_Or.class);

    public void run() {
        int x = 5;
        int y = 5;

        @SuppressWarnings("unused") boolean pass = false;

        if ((x < 0 || x >= 10) && (y < 0 || y >= 10)) {
            pass = true;
        } else {
            pass = false;
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
 int x = 5;
 int y = 5;
 char pass = 0;
 if ((x<0 || x>=10) && (y<0 || y>=10)){
 pass = 1;
 } else {
 pass = 0;
 }
 return;
 }
 }
 }OpenCL}**/
