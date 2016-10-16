package com.syncleus.aparapi.internal.model;

import java.lang.ref.*;
import java.util.concurrent.*;

//import java.util.function.Supplier;

public final class ValueCache<K, V, T extends Throwable> {
   //    @FunctionalInterface
   public interface ThrowingValueComputer<K, V, T extends Throwable> {
      V compute(K key) throws T;
   }

   //    @FunctionalInterface
   public interface ValueComputer<K, V> extends ThrowingValueComputer<K, V, RuntimeException> {
      // Marker interface
   }

   public static <K, V, T extends Throwable> ValueCache<K, V, T> on(ThrowingValueComputer<K, V, T> computer) {
      return new ValueCache<K, V, T>(computer);
   }

   private final ConcurrentMap<K, SoftReference<V>> map = new ConcurrentHashMap<>();

   private final ThrowingValueComputer<K, V, T> computer;

   private ValueCache(ThrowingValueComputer<K, V, T> computer) {
      this.computer = computer;
   }

   public V computeIfAbsent(K key) throws T {
      Reference<V> reference = map.get(key);
      V value = reference == null ? null : reference.get();
      if (value == null) {
         value = computer.compute(key);
         map.put(key, new SoftReference<>(value));
      }
      return value;
   }

   public void invalidate() {
      map.clear();
   }
}
