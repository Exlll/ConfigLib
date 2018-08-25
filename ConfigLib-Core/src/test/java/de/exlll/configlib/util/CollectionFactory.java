package de.exlll.configlib.util;

import java.util.*;

public final class CollectionFactory {

    private CollectionFactory() { throw new AssertionError(); }

    @SafeVarargs
    public static <T> List<T> listOf(T... values) {
        return Arrays.asList(values);
    }

    @SafeVarargs
    public static <T> Set<T> setOf(T... values) {
        return new LinkedHashSet<>(Arrays.asList(values));
    }

    public static <K, V> Map<K, V> mapOf() {
        return new LinkedHashMap<>();
    }

    public static <K, V> Map<K, V> mapOf(K k, V v) {
        HashMap<K, V> map = new LinkedHashMap<>();
        map.put(k, v);
        return map;
    }

    public static <K, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2) {
        Map<K, V> map = mapOf(k1, v1);
        map.put(k2, v2);
        return map;
    }

    public static <K, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2, K k3, V v3) {
        Map<K, V> map = mapOf(k1, v1, k2, v2);
        map.put(k3, v3);
        return map;
    }

    public static <K, V> Map<K, V> mapOf(
            K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4
    ) {
        Map<K, V> map = mapOf(k1, v1, k2, v2, k3, v3);
        map.put(k4, v4);
        return map;
    }

    public static <K, V> Map<K, V> mapOf(
            K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5
    ) {
        Map<K, V> map = mapOf(k1, v1, k2, v2, k3, v3, k4, v4);
        map.put(k5, v5);
        return map;
    }

    public static <K, V> Map.Entry<K, V> mapEntry(K k, V v) {
        return new MapEntry<>(k, v);
    }

    private static final class MapEntry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private V value;

        public MapEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }
}
