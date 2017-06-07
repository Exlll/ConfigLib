package de.exlll.configlib;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class ConfigMap<K, V> implements Defaultable<Map<K, ?>>, Map<K, V> {
    private final Class<K> keyClass;
    private final Class<V> valueClass;
    private final Map<K, V> map;

    public ConfigMap(Class<K> keyClass, Class<V> valueClass) {
        Objects.requireNonNull(keyClass);
        Objects.requireNonNull(valueClass);
        checkSimpleTypeKey(keyClass);
        if (!Reflect.isSimpleType(valueClass)) {
            Reflect.checkDefaultConstructor(valueClass);
        }
        this.keyClass = keyClass;
        this.valueClass = valueClass;
        this.map = Collections.checkedMap(
                new LinkedHashMap<>(), keyClass, valueClass
        );
    }

    private void checkSimpleTypeKey(Class<?> keyClass) {
        if (!Reflect.isSimpleType(keyClass)) {
            String msg = "Class " + keyClass.getSimpleName() + " is not a simple type.\n" +
                    "Only simple types can be used as keys in a map.";
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return map.get(key);
    }

    @Override
    public V put(K key, V value) {
        return map.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        map.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        map.replaceAll(function);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return map.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return map.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return map.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        return map.replace(key, value);
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return map.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return map.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return map.compute(key, remappingFunction);
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return map.merge(key, value, remappingFunction);
    }

    @Override
    public String toString() {
        return "ConfigMap{" +
                "valueClass=" + valueClass +
                ", map=" + map +
                '}';
    }

    @Override
    public Map<K, ?> toDefault() {
        if (Reflect.isSimpleType(valueClass)) {
            return new LinkedHashMap<>(map);
        }
        Map<K, Object> m = new LinkedHashMap<>();
        for (Entry<K, V> entry : entrySet()) {
            Object mapped = FieldMapper.instanceToMap(entry.getValue());
            m.put(entry.getKey(), mapped);
        }
        return m;
    }

    @Override
    public void fromDefault(Object value) {
        clear();
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
            Object instance = fromDefault(entry.getValue(), valueClass);
            Reflect.checkType(entry.getKey(), keyClass);
            put(keyClass.cast(entry.getKey()), valueClass.cast(instance));
        }
    }

    Map<K, V> getMap() {
        return map;
    }
}
