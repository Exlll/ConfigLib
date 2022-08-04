package de.exlll.configlib;

/**
 * This class contains instances of ready-to-use {@code NameFormatter}s.
 */
public enum NameFormatters implements NameFormatter {
    /**
     * A {@code NameFormatter} that simply returns the given name.
     */
    IDENTITY {
        @Override
        public String format(String name) {
            return name;
        }
    },
    /**
     * A {@code NameFormatter} that transforms <i>camelCase</i> to
     * <i>lower_underscore</i>.
     * <p>
     * For example, <i>myPrivateField</i> becomes <i>my_private_field</i>.
     */
    LOWER_UNDERSCORE {
        @Override
        public String format(String name) {
            StringBuilder builder = new StringBuilder(name.length());
            for (char c : name.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    char lower = Character.toLowerCase(c);
                    builder.append('_').append(lower);
                } else builder.append(c);
            }
            return builder.toString();
        }
    },
    /**
     * A {@code NameFormatter} that transforms <i>camelCase</i> to
     * <i>UPPER_UNDERSCORE</i>.
     * <p>
     * For example, <i>myPrivateField</i> becomes <i>MY_PRIVATE_FIELD</i>.
     */
    UPPER_UNDERSCORE {
        @Override
        public String format(String name) {
            StringBuilder builder = new StringBuilder(name.length());
            for (char c : name.toCharArray()) {
                if (Character.isLowerCase(c)) {
                    builder.append(Character.toUpperCase(c));
                } else if (Character.isUpperCase(c)) {
                    builder.append('_').append(c);
                } else builder.append(c);
            }
            return builder.toString();
        }
    },
    /**
     * A {@code NameFormatter} that transforms <i>camelCase</i> to
     * <i>lower-kebab-case</i>.
     * <p>
     * For example, <i>myPrivateField</i> becomes <i>my-private-field</i>.
     */
    LOWER_KEBAB_CASE {
        @Override
        public String format(String name) {
            StringBuilder builder = new StringBuilder(name.length());
            for (int i = 0; i < name.length(); i++) {
                char c = name.charAt(i);
                if (Character.isUpperCase(c)) {
                    builder.append('-');
                    builder.append(Character.toLowerCase(c));
                } else {
                    builder.append(c);
                }
            }
            return builder.toString();
        }
    },
    /**
     * A {@code NameFormatter} that transforms <i>camelCase</i> to
     * <i>UPPER-KEBAB-CASE</i>.
     * <p>
     * For example, <i>myPrivateField</i> becomes <i>MY-PRIVATE-FIELD</i>.
     */
    UPPER_KEBAB_CASE {
        @Override
        public String format(String name) {
            StringBuilder builder = new StringBuilder(name.length());
            for (int i = 0; i < name.length(); i++) {
                char c = name.charAt(i);
                if (Character.isLowerCase(c)) {
                    builder.append(Character.toUpperCase(c));
                } else if (Character.isUpperCase(c)) {
                    builder.append('-');
                    builder.append(c);
                } else {
                    builder.append(c);
                }
            }
            return builder.toString();
        }
    }
}
