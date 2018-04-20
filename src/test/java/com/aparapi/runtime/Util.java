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
package com.aparapi.runtime;

import java.util.Arrays;

public class Util {
    static void fill(int[] array, Filler _filler) {
        for (int i = 0; i < array.length; i++) {
            _filler.fill(array, i);
        }
    }

    static boolean same(int[] lhs, int[] rhs, Comparer _comparer) {
        boolean same = lhs != null && rhs != null && lhs.length == rhs.length;
        for (int i = 0; same && i < lhs.length; i++) {
            same = _comparer.same(lhs, rhs, i);
        }
        return (same);
    }

    static void zero(int[] array) {
        Arrays.fill(array, 0);
    }

    static boolean same(int[] lhs, int[] rhs) {
        return (same(lhs, rhs, new Comparer() {

            @Override
            public boolean same(int[] lhs, int[] rhs, int index) {

                return lhs[index] == rhs[index];
            }
        }));
    }

    static boolean same(boolean[] lhs, boolean[] rhs) {
        boolean same = lhs != null && rhs != null && lhs.length == rhs.length;
        for (int i = 0; same && i < lhs.length; i++) {
            same = lhs[i] == rhs[i];
        }
        return (same);
    }

    static void apply(int[] lhs, int[] rhs, Operator _operator) {
        for (int i = 0; i < lhs.length; i++) {
            _operator.apply(lhs, rhs, i);
        }
    }

    interface Filler {
        void fill(int[] array, int index);
    }

    interface Comparer {
        boolean same(int[] lhs, int[] rhs, int index);
    }

    interface Operator {
        void apply(int[] lhs, int[] rhs, int index);
    }

}
