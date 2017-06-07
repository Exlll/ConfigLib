package de.exlll.configlib;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class ConfigSet<T> implements Defaultable<Set<?>>, Set<T> {
    private final Class<T> cls;
    private final Set<T> set;

    public ConfigSet(Class<T> cls) {
        Objects.requireNonNull(cls);
        if (!Reflect.isSimpleType(cls)) {
            Reflect.checkDefaultConstructor(cls);
        }
        this.cls = cls;
        this.set = Collections.checkedSet(new LinkedHashSet<>(), cls);
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return set.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return set.iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        set.forEach(action);
    }

    @Override
    public Object[] toArray() {
        return set.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return set.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return set.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return set.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return set.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return set.addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return set.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return set.removeAll(c);
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        return set.removeIf(filter);
    }

    @Override
    public void clear() {
        set.clear();
    }

    @Override
    public Spliterator<T> spliterator() {
        return set.spliterator();
    }

    @Override
    public Stream<T> stream() {
        return set.stream();
    }

    @Override
    public Stream<T> parallelStream() {
        return set.parallelStream();
    }

    @Override
    public String toString() {
        return "ConfigSet{" +
                "cls=" + cls +
                ", set=" + set +
                '}';
    }

    @Override
    public Set<?> toDefault() {
        if (Reflect.isSimpleType(cls)) {
            return new LinkedHashSet<>(set);
        }
        Set<Object> s = new LinkedHashSet<>();
        for (Object item : set) {
            s.add(FieldMapper.instanceToMap(item));
        }
        return s;
    }

    @Override
    public void fromDefault(Object value) {
        clear();
        for (Object item : (Set<?>) value) {
            Object instance = fromDefault(item, cls);
            add(cls.cast(instance));
        }
    }

    Set<T> getSet() {
        return set;
    }
}
