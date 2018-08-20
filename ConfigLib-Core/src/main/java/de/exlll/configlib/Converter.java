package de.exlll.configlib;

import de.exlll.configlib.annotation.ElementType;

import java.lang.reflect.Field;

/**
 * Implementations of this interface convert field values to objects that can be
 * stored by a {@link ConfigurationSource}, and vice versa.
 * <p>
 * Implementations must have a no-args constructor.
 *
 * @param <F> the type of the field value
 * @param <T> the type of the converted value
 */
public interface Converter<F, T> {
    /**
     * Converts a field value to an object that can be stored by a
     * {@code ConfigurationSource}.
     * <p>
     * If this method returns null, a {@code ConfigurationException} will be thrown.
     *
     * @param element field value that is converted
     * @param info    information about the current conversion step
     * @return converted field value
     */
    T convertTo(F element, ConversionInfo info);

    /**
     * Executes some action before the field value is converted.
     *
     * @param info information about the current conversion step
     */
    default void preConvertTo(ConversionInfo info) {}

    /**
     * Converts a converted field value back to its original representation.
     * <p>
     * If this method returns null, the default value assigned to the field will
     * be kept.
     *
     * @param element object that should be converted back
     * @param info    information about the current conversion step
     * @return the element's original representation
     */
    F convertFrom(T element, ConversionInfo info);

    /**
     * Executes some action before the converted field value is converted back
     * to its original representation.
     *
     * @param info information about the current conversion step
     */
    default void preConvertFrom(ConversionInfo info) {}

    /**
     * Instances of this class contain information about the currently converted
     * configuration, configuration element, and the conversion step.
     */
    final class ConversionInfo {
        private final Field field;
        private final Object instance;
        private final Object value;
        private final Object mapValue;
        private final Class<?> fieldType;
        private final Class<?> valueType;
        private final Class<?> elementType;
        private final String fieldName;
        private final Configuration.Properties props;
        private final int nestingLevel;
        private int currentNestingLevel;

        private ConversionInfo(Field field, Object instance, Object mapValue,
                               Configuration.Properties props) {
            this.field = field;
            this.instance = instance;
            this.value = Reflect.getValue(field, instance);
            this.mapValue = mapValue;
            this.fieldType = field.getType();
            this.valueType = value.getClass();
            this.fieldName = field.getName();
            this.props = props;
            this.elementType = elementType(field);
            this.nestingLevel = nestingLevel(field);
        }

        private static Class<?> elementType(Field field) {
            if (field.isAnnotationPresent(ElementType.class)) {
                ElementType et = field.getAnnotation(ElementType.class);
                return et.value();
            }
            return null;
        }

        private static int nestingLevel(Field field) {
            if (field.isAnnotationPresent(ElementType.class)) {
                ElementType et = field.getAnnotation(ElementType.class);
                return et.nestingLevel();
            }
            return -1;
        }

        static ConversionInfo of(Field field, Object instance,
                                 Configuration.Properties props) {
            return new ConversionInfo(field, instance, null, props);
        }

        static ConversionInfo of(Field field, Object instance, Object mapValue,
                                 Configuration.Properties props) {
            return new ConversionInfo(field, instance, mapValue, props);
        }

        /**
         * Returns the field all other values belong to.
         *
         * @return current field
         */
        public Field getField() {
            return field;
        }

        /**
         * Returns the field name.
         *
         * @return current field name
         */
        public String getFieldName() {
            return fieldName;
        }

        /**
         * Returns the object the field belongs to, i.e. the instance currently
         * converted.
         *
         * @return object the field belongs to
         */
        public Object getInstance() {
            return instance;
        }

        /**
         * Returns the default value assigned to that field.
         *
         * @return default value assigned to field
         */
        public Object getValue() {
            return value;
        }

        /**
         * When loading, returns the converted field value, otherwise returns null.
         *
         * @return converted field value or null
         */
        public Object getMapValue() {
            return mapValue;
        }

        /**
         * Returns the type of the field.
         *
         * @return field type
         */
        public Class<?> getFieldType() {
            return fieldType;
        }

        /**
         * Returns the type of the default value assigned to the field.
         *
         * @return type default value assigned to field
         */
        public Class<?> getValueType() {
            return valueType;
        }

        /**
         * Returns the {@code Configuration.Properties} instance of the currently
         * converted configuration.
         *
         * @return properties of currently converted configuration
         */
        public Configuration.Properties getProperties() {
            return props;
        }

        /**
         * Returns the value of the {@code ElementType} annotation or null if the
         * field is not annotated with this annotation.
         *
         * @return value of the {@code ElementType} annotation or null
         */
        public Class<?> getElementType() {
            return elementType;
        }

        /**
         * Returns whether the field is annotated with the {@code ElementType}
         * annotation.
         *
         * @return true, if field is annotated with {@code ElementType}.
         */
        public boolean hasElementType() {
            return elementType != null;
        }

        int getNestingLevel() {
            return nestingLevel;
        }

        int getCurrentNestingLevel() {
            return currentNestingLevel;
        }

        void incCurrentNestingLevel() {
            currentNestingLevel++;
        }
    }
}
