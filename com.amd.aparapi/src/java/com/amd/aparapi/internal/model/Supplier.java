package com.amd.aparapi.internal.model;

/**
 * Substitute of Java8's Supplier<V> interface, used in Java7 backport of caches.
 */
public interface Supplier<V> {
   V get();
}
