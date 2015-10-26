package com.amd.aparapi.internal.model;

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
