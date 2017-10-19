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

public class MultiContinue {
    private static final Logger LOGGER = Logger.getLogger(MultiContinue.class);

    public void run() {
        @SuppressWarnings("unused") boolean pass = false;
        for (int i = 0; i < 10; i++) {
            if (i == 5) {
                continue;
            } else {
                if (i == 2) {
                    continue;
                }
                if (i == 1) {
                    continue;
                }
            }
            if (i == 10) {
                continue;
            }
            pass = true;
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
 char pass = 0;
 for (int i = 0; i<10; i++){
 if (i==5){
 } else {
 if (i==2){
 } else {
 if (i==1){
 } else {
 if (i==10){
 } else {
 pass = 1;
 }
 }
 }
 }
 }
 return;
 }
 }
 }OpenCL}**/
