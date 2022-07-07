package de.exlll.configlib;

import java.lang.reflect.Field;
import java.util.*;

final class CommentNodeExtractor {
    private final FieldFilter fieldFilter;
    private final FieldFormatter fieldFormatter;
    private final boolean outputNull;

    CommentNodeExtractor(ConfigurationProperties properties) {
        this.fieldFilter = Validator.requireNonNull(properties.getFieldFilter(), "field filter");
        this.fieldFormatter = Validator.requireNonNull(properties.getFieldFormatter(), "field formatter");
        this.outputNull = properties.outputNulls();
    }

    private record State(Iterator<Field> iterator, Object configuration) {}

    /**
     * Extracts {@code CommentNode}s of the given configuration in a DFS manner.
     * The nodes are returned in the order in which they were found.
     *
     * @param configuration the configuration from which the nodes are extracted
     * @return the nodes in the order in which they are found
     * @throws IllegalArgumentException if {@code configuration} is not a configuration
     * @throws NullPointerException     if {@code configuration} is null
     */
    public Queue<CommentNode> extractCommentNodes(final Object configuration) {
        Validator.requireConfiguration(configuration.getClass());
        final Queue<CommentNode> result = new ArrayDeque<>();
        final var fnameStack = new ArrayDeque<>(List.of(""));
        final var stateStack = new ArrayDeque<>(List.of(
                new State(configurationFields(configuration), configuration)
        ));

        State state;
        while (!stateStack.isEmpty()) {
            state = stateStack.removeLast();
            fnameStack.removeLast();

            while (state.iterator.hasNext()) {
                final var field = state.iterator.next();
                final var value = Reflect.getValue(field, state.configuration);

                if ((value == null) && !outputNull)
                    continue;

                final var commentNode = createNodeIfFieldHasComment(field, fnameStack);
                commentNode.ifPresent(result::add);

                if ((value == null) || !Reflect.isConfiguration(field.getType()))
                    continue;

                stateStack.addLast(new State(state.iterator, state.configuration));
                fnameStack.addLast(fieldFormatter.format(field));
                state = new State(configurationFields(value), value);
            }
        }

        return result;
    }

    private Optional<CommentNode> createNodeIfFieldHasComment(
            Field field,
            Deque<String> fileNameStack
    ) {
        if (field.isAnnotationPresent(Comment.class)) {
            final var comments = field.getAnnotation(Comment.class).value();
            final var fieldName = fieldFormatter.format(field);
            final var fieldNames = new ArrayList<>(fileNameStack.stream().toList());
            fieldNames.add(fieldName);
            final var result = new CommentNode(Arrays.asList(comments), fieldNames);
            return Optional.of(result);
        }
        return Optional.empty();
    }

    private Iterator<Field> configurationFields(Object configuration) {
        return FieldExtractors.CONFIGURATION.extract(configuration.getClass())
                .filter(fieldFilter)
                .iterator();
    }
}
