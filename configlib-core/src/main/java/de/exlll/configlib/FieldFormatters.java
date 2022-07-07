package de.exlll.configlib;

import java.lang.reflect.Field;

/**
 * This class contains instances of ready-to-use {@code FieldFormatter}s.
 */
public enum FieldFormatters implements FieldFormatter {
    /**
     * A {@code FieldFormatter} that returns the name of the field.
     */
    IDENTITY {
        @Override
        public String format(Field field) {
            return field.getName();
        }
    },
    /**
     * A {@code FieldFormatter} that transforms <i>camelCase</i> to
     * <i>lower_underscore</i>.
     * <p>
     * For example, <i>myPrivateField</i> becomes <i>my_private_field</i>.
     */
    LOWER_UNDERSCORE {
        @Override
        public String format(Field field) {
            String fn = field.getName();
            StringBuilder builder = new StringBuilder(fn.length());
            for (char c : fn.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    char lower = Character.toLowerCase(c);
                    builder.append('_').append(lower);
                } else builder.append(c);
            }
            return builder.toString();
        }
    },
    /**
     * A {@code FieldFormatter} that transforms <i>camelCase</i> to
     * <i>UPPER_UNDERSCORE</i>.
     * <p>
     * For example, <i>myPrivateField</i> becomes <i>MY_PRIVATE_FIELD</i>.
     */
    UPPER_UNDERSCORE {
        @Override
        public String format(Field field) {
            String fn = field.getName();
            StringBuilder builder = new StringBuilder(fn.length());
            for (char c : fn.toCharArray()) {
                if (Character.isLowerCase(c)) {
                    builder.append(Character.toUpperCase(c));
                } else if (Character.isUpperCase(c)) {
                    builder.append('_').append(c);
                } else builder.append(c);
            }
            return builder.toString();
        }
    }
}
