package de.exlll.configlib;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public final class ConfigList<T> implements Defaultable<List<?>>, List<T>, RandomAccess {
    private final Class<T> cls;
    private final List<T> list;

    public ConfigList(Class<T> cls) {
        Objects.requireNonNull(cls);
        if (!Reflect.isSimpleType(cls)) {
            Reflect.checkDefaultConstructor(cls);
        }
        this.cls = cls;
        this.list = Collections.checkedList(new ArrayList<>(), cls);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return list.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public T get(int index) {
        return list.get(index);
    }

    @Override
    public T set(int index, T element) {
        return list.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        list.add(index, element);
    }

    @Override
    public T remove(int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator) {
        list.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super T> c) {
        list.sort(c);
    }

    @Override
    public Spliterator<T> spliterator() {
        return list.spliterator();
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        return list.removeIf(filter);
    }

    @Override
    public Stream<T> stream() {
        return list.stream();
    }

    @Override
    public Stream<T> parallelStream() {
        return list.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        list.forEach(action);
    }

    @Override
    public String toString() {
        return "ConfigList{" +
                "cls=" + cls +
                ", list=" + list +
                '}';
    }

    @Override
    public List<?> toDefault() {
        if (Reflect.isSimpleType(cls)) {
            return new ArrayList<>(list);
        }
        List<Object> l = new ArrayList<>();
        for (Object item : list) {
            l.add(FieldMapper.instanceToMap(item));
        }
        return l;
    }

    @Override
    public void fromDefault(Object value) {
        for (Object item : (List<?>) value) {
            Object instance = fromDefault(item, cls);
            add(cls.cast(instance));
        }
    }

    List<T> getList() {
        return list;
    }
}
