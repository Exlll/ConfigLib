package de.exlll.configlib;

import de.exlll.configlib.Serializers.*;

import java.io.File;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

final class SerializerSelector {
    private static final Map<Class<?>, Serializer<?, ?>> DEFAULT_SERIALIZERS = Map.ofEntries(
            Map.entry(boolean.class, new BooleanSerializer()),
            Map.entry(Boolean.class, new BooleanSerializer()),
            Map.entry(byte.class, new NumberSerializer(byte.class)),
            Map.entry(Byte.class, new NumberSerializer(Byte.class)),
            Map.entry(short.class, new NumberSerializer(short.class)),
            Map.entry(Short.class, new NumberSerializer(Short.class)),
            Map.entry(int.class, new NumberSerializer(int.class)),
            Map.entry(Integer.class, new NumberSerializer(Integer.class)),
            Map.entry(long.class, new NumberSerializer(long.class)),
            Map.entry(Long.class, new NumberSerializer(Long.class)),
            Map.entry(float.class, new NumberSerializer(float.class)),
            Map.entry(Float.class, new NumberSerializer(Float.class)),
            Map.entry(double.class, new NumberSerializer(double.class)),
            Map.entry(Double.class, new NumberSerializer(Double.class)),
            Map.entry(char.class, new CharacterSerializer()),
            Map.entry(Character.class, new CharacterSerializer()),
            Map.entry(String.class, new StringSerializer()),
            Map.entry(BigInteger.class, new BigIntegerSerializer()),
            Map.entry(BigDecimal.class, new BigDecimalSerializer()),
            Map.entry(LocalDate.class, new LocalDateSerializer()),
            Map.entry(LocalTime.class, new LocalTimeSerializer()),
            Map.entry(LocalDateTime.class, new LocalDateTimeSerializer()),
            Map.entry(Instant.class, new InstantSerializer()),
            Map.entry(UUID.class, new UuidSerializer()),
            Map.entry(File.class, new FileSerializer()),
            Map.entry(Path.class, new PathSerializer()),
            Map.entry(URL.class, new UrlSerializer()),
            Map.entry(URI.class, new UriSerializer())
    );
    private final ConfigurationProperties properties;

    public SerializerSelector(ConfigurationProperties properties) {
        this.properties = properties;
    }

    public Serializer<?, ?> select(Type type) {
        final Serializer<?, ?> custom = selectCustomSerializer(type);
        if (custom != null)
            return custom;
        if (type instanceof Class<?> cls) {
            return selectForClass(cls);
        } else if (type instanceof ParameterizedType pType) {
            return selectForParameterizedType(pType);
        } else if (type instanceof WildcardType) {
            String msg = baseExceptionMessage(type) + "Wildcard types cannot be serialized.";
            throw new ConfigurationException(msg);
        } else if (type instanceof GenericArrayType) {
            String msg = baseExceptionMessage(type) + "Generic array types cannot be serialized.";
            throw new ConfigurationException(msg);
        } else if (type instanceof TypeVariable<?>) {
            String msg = baseExceptionMessage(type) + "Type variables cannot be serialized.";
            throw new ConfigurationException(msg);
        }
        // should not happen as we covered all possible types
        throw new ConfigurationException(baseExceptionMessage(type));
    }

    private Serializer<?, ?> selectCustomSerializer(Type type) {
        if (type instanceof Class<?> cls) {
            if (properties.getSerializers().containsKey(cls))
                return properties.getSerializers().get(cls);
        }
        for (var entry : properties.getSerializersByCondition().entrySet()) {
            if (entry.getKey().test(type))
                return entry.getValue();
        }
        return null;
    }

    private Serializer<?, ?> selectForClass(Class<?> cls) {
        if (DEFAULT_SERIALIZERS.containsKey(cls))
            return DEFAULT_SERIALIZERS.get(cls);
        if (Reflect.isEnumType(cls)) {
            // The following cast won't fail because we just checked that it's an enum.
            @SuppressWarnings("unchecked")
            var enumCls = (Class<? extends Enum<?>>) cls;
            return new Serializers.EnumSerializer(enumCls);
        }
        if (Reflect.isArrayType(cls))
            return selectForArray(cls.getComponentType());
        if (Reflect.isConfiguration(cls))
            return new ConfigurationSerializer<>(cls, properties);

        String msg = "Missing serializer for type " + cls + ".\n" +
                     "Either annotate the type with @Configuration or provide a custom " +
                     "serializer by adding it to the properties.";
        throw new ConfigurationException(msg);
    }

    private Serializer<?, ?> selectForArray(Class<?> elementType) {
        if (elementType == boolean.class) {
            return new PrimitiveBooleanArraySerializer();
        } else if (elementType == char.class) {
            return new PrimitiveCharacterArraySerializer();
        } else if (elementType == byte.class) {
            return new PrimitiveByteArraySerializer();
        } else if (elementType == short.class) {
            return new PrimitiveShortArraySerializer();
        } else if (elementType == int.class) {
            return new PrimitiveIntegerArraySerializer();
        } else if (elementType == long.class) {
            return new PrimitiveLongArraySerializer();
        } else if (elementType == float.class) {
            return new PrimitiveFloatArraySerializer();
        } else if (elementType == double.class) {
            return new PrimitiveDoubleArraySerializer();
        }
        var elementSerializer = select(elementType);
        var inputNulls = properties.inputNulls();
        var outputNulls = properties.outputNulls();
        return new ArraySerializer<>(elementType, elementSerializer, outputNulls, inputNulls);
    }

    private Serializer<?, ?> selectForParameterizedType(ParameterizedType type) {
        // the raw type returned by Java is always a class
        final var rawType = (Class<?>) type.getRawType();
        final var typeArgs = type.getActualTypeArguments();
        final var inputNulls = properties.inputNulls();
        final var outputNulls = properties.outputNulls();

        if (Reflect.isListType(rawType)) {
            var elementSerializer = select(typeArgs[0]);
            return new ListSerializer<>(elementSerializer, outputNulls, inputNulls);
        } else if (Reflect.isSetType(rawType)) {
            var elementSerializer = select(typeArgs[0]);
            return properties.serializeSetsAsLists()
                    ? new SetAsListSerializer<>(elementSerializer, outputNulls, inputNulls)
                    : new SetSerializer<>(elementSerializer, outputNulls, inputNulls);
        } else if (Reflect.isMapType(rawType)) {
            if ((typeArgs[0] instanceof Class<?> cls) &&
                (DEFAULT_SERIALIZERS.containsKey(cls) ||
                 Reflect.isEnumType(cls))) {
                var keySerializer = select(typeArgs[0]);
                var valSerializer = select(typeArgs[1]);
                return new MapSerializer<>(keySerializer, valSerializer, outputNulls, inputNulls);
            }
            String msg = baseExceptionMessage(type) +
                         "Map keys can only be of simple or enum type.";
            throw new ConfigurationException(msg);
        }

        String msg = baseExceptionMessage(type) +
                     "Parameterized types other than lists, sets, and maps cannot be serialized.";
        throw new ConfigurationException(msg);
    }

    private String baseExceptionMessage(Type type) {
        return "Cannot select serializer for type '" + type + "'.\n";
    }
}
