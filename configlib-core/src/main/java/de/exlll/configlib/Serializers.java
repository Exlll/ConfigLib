package de.exlll.configlib;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

final class Serializers {
    private Serializers() {}

    static final class BooleanSerializer implements Serializer<Boolean, Boolean> {
        @Override
        public Boolean serialize(Boolean element) {
            return element;
        }

        @Override
        public Boolean deserialize(Boolean element) {
            return element;
        }
    }

    /**
     * Converts a primitive number or its wrapper type to another number type according to the
     * following rules:
     * <ul>
     * <li>
     *     The {@code serialize} method converts all four integer types to {@code Long}, and
     *     both floating point types to {@code Double}.
     * </li>
     * <li>
     *     The {@code deserialize} method converts a number from any supported type to the
     *     requested type if the value of the number fits in the range.
     * </li>
     * </ul>
     */
    static final class NumberSerializer implements Serializer<Number, Number> {
        private final Class<? extends Number> cls;

        public NumberSerializer(Class<? extends Number> cls) {
            this.cls = Validator.requireNonNull(cls, "number class");
            Validator.requirePrimitiveOrWrapperNumberType(cls);
        }

        @Override
        public Number serialize(Number element) {
            if (Reflect.isIntegerType(cls))
                return element.longValue();
            if (Reflect.isFloatingPointType(cls))
                return element.doubleValue();
            String msg = "Invalid element '" + element + "' with type " + element.getClass();
            throw new ConfigurationException(msg); // should not happen, types checked in ctor
        }

        @Override
        public Number deserialize(Number element) {
            if (Reflect.isIntegerType(element.getClass())) {
                return deserializeFromIntegerType(element);
            }
            if (Reflect.isFloatingPointType(element.getClass())) {
                return deserializeFromFloatingPointType(element);
            }
            String clsName = element.getClass().getSimpleName();
            String msg = "Cannot deserialize element '" + element + "' of type " + clsName + ".\n" +
                         "This serializer only supports primitive number types and their wrapper types.";
            throw new ConfigurationException(msg);
        }

        private Number deserializeFromFloatingPointType(Number element) {
            double value = element.doubleValue();

            if (cls == float.class || cls == Float.class) {
                if (Double.isNaN(value))
                    return Float.NaN;
                if (value == Double.POSITIVE_INFINITY)
                    return Float.POSITIVE_INFINITY;
                if (value == Double.NEGATIVE_INFINITY)
                    return Float.NEGATIVE_INFINITY;
                if (value != 0.0)
                    requireFloatingPointInRange(value);
                return element.floatValue();
            }

            return value;
        }

        private Number deserializeFromIntegerType(Number element) {
            long value = element.longValue();

            if (cls == byte.class || cls == Byte.class) {
                requireIntegerInRange(value, Byte.MIN_VALUE, Byte.MAX_VALUE);
                return element.byteValue();
            }

            if (cls == short.class || cls == Short.class) {
                requireIntegerInRange(value, Short.MIN_VALUE, Short.MAX_VALUE);
                return element.shortValue();
            }

            if (cls == int.class || cls == Integer.class) {
                requireIntegerInRange(value, Integer.MIN_VALUE, Integer.MAX_VALUE);
                return element.intValue();
            }

            return value;
        }

        private void requireIntegerInRange(long value, long low, long high) {
            if (value < low || value > high) {
                String msg = baseExceptionMessage(value) + "It does not fit into the range " +
                             "of valid values [" + low + ", " + high + "].";
                throw new ConfigurationException(msg);
            }
        }

        private void requireFloatingPointInRange(double value) {
            final String clsName = cls.getSimpleName();
            if ((value > -Float.MIN_VALUE) && (value < Float.MIN_VALUE)) {
                String msg = baseExceptionMessage(value) + "It is smaller than the smallest " +
                             "possible " + clsName + " value.";
                throw new ConfigurationException(msg);
            }
            if ((value < -Float.MAX_VALUE) || (value > Float.MAX_VALUE)) {
                String msg = baseExceptionMessage(value) + "It is larger than the largest " +
                             "possible " + clsName + " value.";
                throw new ConfigurationException(msg);
            }
        }

        private <T extends Number> String baseExceptionMessage(T value) {
            String clsName = cls.getSimpleName();
            return "Number " + value + " cannot be converted to type " + clsName + ". ";
        }

        public Class<? extends Number> getNumberClass() {
            return cls;
        }
    }

    static final class StringSerializer implements Serializer<String, String> {
        @Override
        public String serialize(String element) {
            return element;
        }

        @Override
        public String deserialize(String element) {
            return element;
        }
    }

    static final class CharacterSerializer implements Serializer<Character, String> {
        @Override
        public String serialize(Character element) {
            return element.toString();
        }

        @Override
        public Character deserialize(String element) {
            int length = element.length();

            if (length == 0) {
                String msg = "An empty string cannot be converted to a character.";
                throw new ConfigurationException(msg);
            }
            if (length > 1) {
                String msg = "String '" + element + "' is too long to be converted " +
                             "to a character.";
                throw new ConfigurationException(msg);
            }

            return element.charAt(0);
        }
    }

    static final class BigIntegerSerializer implements Serializer<BigInteger, String> {
        @Override
        public String serialize(BigInteger element) {
            return element.toString();
        }

        @Override
        public BigInteger deserialize(String element) {
            return new BigInteger(element);
        }
    }

    static final class BigDecimalSerializer implements Serializer<BigDecimal, String> {
        @Override
        public String serialize(BigDecimal element) {
            return element.toString();
        }

        @Override
        public BigDecimal deserialize(String element) {
            return new BigDecimal(element);
        }
    }

    static final class LocalTimeSerializer implements Serializer<LocalTime, String> {
        @Override
        public String serialize(LocalTime element) {
            return element.truncatedTo(ChronoUnit.SECONDS).toString();
        }

        @Override
        public LocalTime deserialize(String element) {
            return LocalTime.parse(element);
        }
    }

    static final class LocalDateSerializer implements Serializer<LocalDate, String> {
        @Override
        public String serialize(LocalDate element) {
            return element.toString();
        }

        @Override
        public LocalDate deserialize(String element) {
            return LocalDate.parse(element);
        }
    }

    static final class LocalDateTimeSerializer implements Serializer<LocalDateTime, String> {
        @Override
        public String serialize(LocalDateTime element) {
            return element.truncatedTo(ChronoUnit.SECONDS).toString();
        }

        @Override
        public LocalDateTime deserialize(String element) {
            return LocalDateTime.parse(element);
        }
    }

    static final class UuidSerializer implements Serializer<UUID, String> {
        @Override
        public String serialize(UUID element) {
            return element.toString();
        }

        @Override
        public UUID deserialize(String element) {
            return UUID.fromString(element);
        }
    }

    static final class EnumSerializer implements Serializer<Enum<?>, String> {
        private final Class<? extends Enum<?>> cls;

        public EnumSerializer(Class<? extends Enum<?>> cls) {
            this.cls = Validator.requireNonNull(cls, "enum class");
        }

        @Override
        public String serialize(Enum<?> element) {
            return element.name();
        }

        @Override
        public Enum<?> deserialize(String element) {
            for (Enum<?> constant : cls.getEnumConstants()) {
                if (constant.name().equals(element)) {
                    return constant;
                }
            }
            String msg = createExceptionMessage(element);
            throw new ConfigurationException(msg);
        }

        private String createExceptionMessage(String element) {
            String enums = Arrays.stream(cls.getEnumConstants())
                    .map(Enum::name)
                    .collect(Collectors.joining(", ", "[", "]"));
            return "Enum class " + cls.getSimpleName() + " does not contain enum '" +
                   element + "'. Valid values are: " + enums;
        }

        public Class<? extends Enum<?>> getEnumCls() {
            return cls;
        }
    }

    static class CollectionSerializer<S, T, L extends Collection<S>, R extends Collection<T>>
            implements Serializer<L, R> {
        private final Serializer<S, T> serializer;
        private final boolean outputNulls;
        private final boolean inputNulls;
        private final Supplier<L> lSupplier;
        private final Supplier<R> rSupplier;

        public CollectionSerializer(
                Serializer<S, T> serializer,
                boolean outputNulls,
                boolean inputNulls,
                Supplier<L> lSupplier,
                Supplier<R> rSupplier
        ) {
            this.serializer = Validator.requireNonNull(serializer, "element serializer");
            this.outputNulls = outputNulls;
            this.inputNulls = inputNulls;
            this.lSupplier = lSupplier;
            this.rSupplier = rSupplier;
        }

        @Override
        public final R serialize(L element) {
            final Stream<T> stream = outputNulls
                    ? element.stream().map(s -> s == null ? null : serializer.serialize(s))
                    : element.stream().filter(Objects::nonNull).map(serializer::serialize);
            return stream.collect(Collectors.toCollection(rSupplier));
        }

        @Override
        public final L deserialize(R element) {
            final Stream<S> stream = inputNulls
                    ? element.stream().map(t -> t == null ? null : serializer.deserialize(t))
                    : element.stream().filter(Objects::nonNull).map(serializer::deserialize);
            return stream.collect(Collectors.toCollection(lSupplier));
        }

        public final Serializer<S, T> getElementSerializer() {
            return serializer;
        }
    }

    static final class ListSerializer<S, T> extends CollectionSerializer<S, T, List<S>, List<T>> {
        public ListSerializer(Serializer<S, T> serializer, boolean outputNulls, boolean inputNulls) {
            super(serializer, outputNulls, inputNulls, ArrayList::new, ArrayList::new);
        }
    }

    static final class SetSerializer<S, T> extends CollectionSerializer<S, T, Set<S>, Set<T>> {
        public SetSerializer(Serializer<S, T> serializer, boolean outputNulls, boolean inputNulls) {
            super(serializer, outputNulls, inputNulls, HashSet::new, LinkedHashSet::new);
        }
    }

    static final class SetAsListSerializer<S, T> extends CollectionSerializer<S, T, Set<S>, List<T>> {
        public SetAsListSerializer(Serializer<S, T> serializer, boolean outputNulls, boolean inputNulls) {
            super(serializer, outputNulls, inputNulls, HashSet::new, ArrayList::new);
        }
    }

    static final class MapSerializer<S1, T1, S2, T2> implements Serializer<Map<S1, S2>, Map<T1, T2>> {
        private final Serializer<S1, T1> keySerializer;
        private final Serializer<S2, T2> valSerializer;
        private final boolean outputNulls;
        private final boolean inputNulls;

        public MapSerializer(
                Serializer<S1, T1> keySerializer,
                Serializer<S2, T2> valSerializer,
                boolean outputNulls,
                boolean inputNulls
        ) {
            this.keySerializer = Validator.requireNonNull(keySerializer, "key serializer");
            this.valSerializer = Validator.requireNonNull(valSerializer, "value serializer");
            this.outputNulls = outputNulls;
            this.inputNulls = inputNulls;
        }

        @Override
        public Map<T1, T2> serialize(Map<S1, S2> element) {
            // cannot work with Collectors.toMap as is doesn't allow null values
            final Map<T1, T2> result = new LinkedHashMap<>();
            for (final Map.Entry<S1, S2> entry : element.entrySet()) {
                if (!outputNulls && isEntryNull(entry))
                    continue;
                var s1key = entry.getKey();
                var s2val = entry.getValue();
                var t1key = (s1key == null) ? null : keySerializer.serialize(s1key);
                var t2val = (s2val == null) ? null : valSerializer.serialize(s2val);
                result.put(t1key, t2val);
            }
            return result;
        }

        @Override
        public Map<S1, S2> deserialize(Map<T1, T2> element) {
            // cannot work with Collectors.toMap as is doesn't allow null values
            final Map<S1, S2> result = new LinkedHashMap<>();
            for (final Map.Entry<T1, T2> entry : element.entrySet()) {
                if (!inputNulls && isEntryNull(entry))
                    continue;
                var t1key = entry.getKey();
                var t2val = entry.getValue();
                var s1key = (t1key == null) ? null : keySerializer.deserialize(t1key);
                var s2val = (t2val == null) ? null : valSerializer.deserialize(t2val);
                result.put(s1key, s2val);
            }
            return result;
        }

        private static boolean isEntryNull(Map.Entry<?, ?> entry) {
            return (entry == null) || (entry.getKey() == null) || (entry.getValue() == null);
        }

        public Serializer<S1, T1> getKeySerializer() {
            return keySerializer;
        }

        public Serializer<S2, T2> getValueSerializer() {
            return valSerializer;
        }
    }

    static final class ArraySerializer<T1, T2> implements Serializer<T1[], List<T2>> {
        private final Class<?> componentType;
        private final Serializer<T1, T2> serializer;
        private final boolean outputNulls;
        private final boolean inputNulls;

        public ArraySerializer(
                Class<?> componentType,
                Serializer<T1, T2> serializer,
                boolean outputNulls,
                boolean inputNulls
        ) {
            this.componentType = Validator.requireNonNull(componentType, "component type");
            this.serializer = Validator.requireNonNull(serializer, "element serializer");
            this.outputNulls = outputNulls;
            this.inputNulls = inputNulls;
        }

        @Override
        public List<T2> serialize(T1[] element) {
            final Stream<T2> stream = outputNulls
                    ? Arrays.stream(element).map(s -> s == null ? null : serializer.serialize(s))
                    : Arrays.stream(element).filter(Objects::nonNull).map(serializer::serialize);
            return stream.toList();
        }

        @Override
        public T1[] deserialize(List<T2> element) {
            final Stream<T1> stream = inputNulls
                    ? element.stream().map(t -> t == null ? null : serializer.deserialize(t))
                    : element.stream().filter(Objects::nonNull).map(serializer::deserialize);
            // The following cast won't fail because we choose the elementSerializer based
            // on the componentType.
            @SuppressWarnings("unchecked")
            IntFunction<T1[]> f = value -> (T1[]) Reflect.newArray(componentType, value);
            return stream.toArray(f);
        }

        public Class<?> getComponentType() {
            return componentType;
        }

        public Serializer<T1, T2> getElementSerializer() {
            return serializer;
        }
    }

    static final class PrimitiveBooleanArraySerializer implements Serializer<Object, List<Boolean>> {
        @Override
        public List<Boolean> serialize(Object element) {
            final boolean[] array = (boolean[]) element;
            return IntStream.range(0, array.length).mapToObj(i -> array[i]).toList();
        }

        @Override
        public Object deserialize(List<Boolean> element) {
            final boolean[] array = new boolean[element.size()];
            for (int i = 0; i < element.size(); i++)
                array[i] = Validator.requireNonNullArrayElement(element.get(i), "boolean", i);
            return array;
        }
    }

    static final class PrimitiveCharacterArraySerializer
            implements Serializer<Object, List<String>> {
        private static final CharacterSerializer serializer = new CharacterSerializer();

        @Override
        public List<String> serialize(Object element) {
            final char[] array = (char[]) element;
            return IntStream.range(0, array.length)
                    .mapToObj(i -> serializer.serialize(array[i]))
                    .toList();
        }

        @Override
        public Object deserialize(List<String> element) {
            final char[] array = new char[element.size()];
            for (int i = 0; i < element.size(); i++) {
                String character = Validator.requireNonNullArrayElement(element.get(i), "char", i);
                array[i] = serializer.deserialize(character);
            }
            return array;
        }
    }

    static final class PrimitiveByteArraySerializer
            implements Serializer<Object, List<Number>> {
        private static final NumberSerializer serializer = new NumberSerializer(byte.class);

        @Override
        public List<Number> serialize(Object element) {
            final byte[] array = (byte[]) element;
            return IntStream.range(0, array.length).
                    mapToObj(i -> serializer.serialize(array[i]))
                    .toList();
        }

        @Override
        public Object deserialize(List<Number> element) {
            final byte[] array = new byte[element.size()];
            for (int i = 0; i < element.size(); i++) {
                Number number = Validator.requireNonNullArrayElement(element.get(i), "byte", i);
                array[i] = (byte) serializer.deserialize(number);
            }
            return array;
        }
    }

    static final class PrimitiveShortArraySerializer
            implements Serializer<Object, List<Number>> {
        private static final NumberSerializer serializer = new NumberSerializer(short.class);

        @Override
        public List<Number> serialize(Object element) {
            final short[] array = (short[]) element;
            return IntStream.range(0, array.length)
                    .mapToObj(i -> serializer.serialize(array[i]))
                    .toList();
        }

        @Override
        public Object deserialize(List<Number> element) {
            final short[] array = new short[element.size()];
            for (int i = 0; i < element.size(); i++) {
                Number number = Validator.requireNonNullArrayElement(element.get(i), "short", i);
                array[i] = (short) serializer.deserialize(number);
            }
            return array;
        }
    }

    static final class PrimitiveIntegerArraySerializer
            implements Serializer<Object, List<Number>> {
        private static final NumberSerializer serializer = new NumberSerializer(int.class);

        @Override
        public List<Number> serialize(Object element) {
            final int[] array = (int[]) element;
            return Arrays.stream(array).mapToObj(serializer::serialize).toList();
        }

        @Override
        public Object deserialize(List<Number> element) {
            final int[] array = new int[element.size()];
            for (int i = 0; i < element.size(); i++) {
                Number number = Validator.requireNonNullArrayElement(element.get(i), "int", i);
                array[i] = (int) serializer.deserialize(number);
            }
            return array;
        }
    }

    static final class PrimitiveLongArraySerializer
            implements Serializer<Object, List<Number>> {
        private static final NumberSerializer serializer = new NumberSerializer(long.class);

        @Override
        public List<Number> serialize(Object element) {
            final long[] array = (long[]) element;
            return Arrays.stream(array).mapToObj(serializer::serialize).toList();
        }

        @Override
        public Object deserialize(List<Number> element) {
            final long[] array = new long[element.size()];
            for (int i = 0; i < element.size(); i++) {
                Number number = Validator.requireNonNullArrayElement(element.get(i), "long", i);
                array[i] = (long) serializer.deserialize(number);
            }
            return array;
        }
    }

    static final class PrimitiveFloatArraySerializer
            implements Serializer<Object, List<Number>> {
        private static final NumberSerializer serializer = new NumberSerializer(float.class);

        @Override
        public List<Number> serialize(Object element) {
            final float[] array = (float[]) element;
            return IntStream.range(0, array.length)
                    .mapToObj(i -> serializer.serialize(array[i]))
                    .toList();
        }

        @Override
        public Object deserialize(List<Number> element) {
            final float[] array = new float[element.size()];
            for (int i = 0; i < element.size(); i++) {
                Number number = Validator.requireNonNullArrayElement(element.get(i), "float", i);
                array[i] = (float) serializer.deserialize(number);
            }
            return array;
        }
    }

    static final class PrimitiveDoubleArraySerializer
            implements Serializer<Object, List<Number>> {
        private static final NumberSerializer serializer = new NumberSerializer(double.class);

        @Override
        public List<Number> serialize(Object element) {
            final double[] array = (double[]) element;
            return Arrays.stream(array).mapToObj(serializer::serialize).toList();
        }

        @Override
        public Object deserialize(List<Number> element) {
            final double[] array = new double[element.size()];
            for (int i = 0; i < element.size(); i++) {
                Number number = Validator.requireNonNullArrayElement(element.get(i), "double", i);
                array[i] = (double) serializer.deserialize(number);
            }
            return array;
        }
    }
}
