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

public class While_If_IfElseElse {
    public void run() {

        int a = 0;
        int b = 0;
        int c = 0;
        int d = 0;
        int e = 0;
        int f = 0;
        int g = 0;
        int h = 0;
        int i = 0;
        int j = 0;
        int k = 0;
        int l = 0;
        int m = 0;
        int n = 0;
        int o = 0;
        int p = 0;
        int q = 0;
        int r = 0;

        while (a == a) {
            b = b;
            if (c == c) {
                d = d;
                if (e == e && f == f) {
                    g = g;
                }
            }
            if (h == h && i == i) {
                if (j == j) {
                    k = k;

                }
                if (l == l) {
                    if (m == m) {
                        n = n;
                    } else if (o == o) {
                        p = p;
                    } else {
                        q = q;
                    }
                    r = r;
                }
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
 int a = 0;
 int b = 0;
 int c = 0;
 int d = 0;
 int e = 0;
 int f = 0;
 int g = 0;
 int h = 0;
 int i = 0;
 int j = 0;
 int k = 0;
 int l = 0;
 int m = 0;
 int n = 0;
 int o = 0;
 int p = 0;
 int q = 0;
 int r = 0;
 for (; a==a; ){
 b = b;
 if (c==c){
 d = d;
 if (e==e && f==f){
 g = g;
 }
 }

 if (h==h && i==i){
 if (j==j){
 k = k;
 }
 if (l==l){
 if (m==m){
 n = n;
 } else {
 if (o==o){
 p = p;
 } else {
 q = q;
 }
 }
 r = r;
 }
 }
 }
 return;
 }
 }
 }OpenCL}**/
