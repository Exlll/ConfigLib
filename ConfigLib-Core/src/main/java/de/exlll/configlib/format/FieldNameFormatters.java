package de.exlll.configlib.format;

public enum FieldNameFormatters implements FieldNameFormatter {
    /**
     * Represents a {@code FieldNameFormatter} that doesn't actually format the
     * field name but instead returns it.
     */
    IDENTITY {
        @Override
        public String fromFieldName(String fn) {
            return fn;
        }
    },
    /**
     * Represents a {@code FieldNameFormatter} that transforms <i>camelCase</i> to
     * <i>lower_underscore</i>.
     * <p>
     * For example, <i>myPrivateField</i> becomes <i>my_private_field</i>.
     */
    LOWER_UNDERSCORE {
        @Override
        public String fromFieldName(String fn) {
            StringBuilder builder = new StringBuilder(fn.length());
            for (char c : fn.toCharArray()) {
                if (Character.isLowerCase(c)) {
                    builder.append(c);
                } else if (Character.isUpperCase(c)) {
                    c = Character.toLowerCase(c);
                    builder.append('_').append(c);
                }
            }
            return builder.toString();
        }
    }
}
