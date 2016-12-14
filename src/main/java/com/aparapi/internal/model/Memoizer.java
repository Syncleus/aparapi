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
package com.aparapi.internal.model;

import java.util.*;
import java.util.concurrent.atomic.*;

interface Optional<E> {
   final class Some<E> implements Optional<E>{
      private final E value;

      static final <E> Optional<E> of(E value) {
         return new Some<>(value);
      }

      private Some(E value) {
         this.value = value;
      }

      @Override public E get() {
         return value;
      }

      @Override public boolean isPresent() {
         return true;
      }
   }

   final class None<E> implements Optional<E>{
      @SuppressWarnings("unchecked") static <E> Optional<E> none() {
         return none;
      }

      @SuppressWarnings("rawtypes") private static final None none = new None();

      private None() {
         // Do nothing
      }

      @Override public E get() {
         throw new NoSuchElementException("No value present");
      }

      @Override public boolean isPresent() {
         return false;
      }
   }

   E get();

   boolean isPresent();
}

public interface Memoizer<T> extends Supplier<T> {
   public final class Impl<T> implements Memoizer<T>{
      private final Supplier<T> supplier;

      private final AtomicReference<Optional<T>> valueRef = new AtomicReference<>(Optional.None.<T> none());

      Impl(Supplier<T> supplier) {
         this.supplier = supplier;
      }

      @Override public T get() {
         Optional<T> value = valueRef.get();
         while (!value.isPresent()) {
            Optional<T> newValue = Optional.Some.of(supplier.get());
            if (valueRef.compareAndSet(value, newValue)) {
               value = newValue;
               break;
            }
            value = valueRef.get();
         }
         return value.get();
      }

      public static <T> Memoizer<T> of(Supplier<T> supplier) {
         return new Impl<>(supplier);
      }
   }

   //    static <T> Memoizer<T> of(Supplier<T> supplier)
   //    {
   //        return new Impl<>(supplier);
   //    }
}
