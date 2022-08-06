package de.exlll.configlib;

import de.exlll.configlib.TypeComponent.ConfigurationField;
import de.exlll.configlib.TypeComponent.ConfigurationRecordComponent;

import java.lang.reflect.AnnotatedElement;
import java.util.*;

import static de.exlll.configlib.Validator.requireConfigurationOrRecord;
import static de.exlll.configlib.Validator.requireNonNull;

final class CommentNodeExtractor {
    private final FieldFilter fieldFilter;
    private final NameFormatter nameFormatter;
    private final boolean outputNull;

    CommentNodeExtractor(ConfigurationProperties properties) {
        this.fieldFilter = requireNonNull(properties.getFieldFilter(), "field filter");
        this.nameFormatter = requireNonNull(properties.getNameFormatter(), "name formatter");
        this.outputNull = properties.outputNulls();
    }

    private record State(Iterator<? extends TypeComponent<?>> iterator, Object componentHolder) {}

    /**
     * Extracts {@code CommentNode}s of the given configuration or record in a DFS manner.
     * The nodes are returned in the order in which they were found.
     *
     * @param componentHolder the componentHolder from which the nodes are extracted
     * @return the nodes in the order in which they are found
     * @throws IllegalArgumentException if {@code componentHolder} is not a configuration or record
     * @throws NullPointerException     if {@code componentHolder} is null
     */
    public Queue<CommentNode> extractCommentNodes(final Object componentHolder) {
        requireConfigurationOrRecord(componentHolder.getClass());
        final Queue<CommentNode> result = new ArrayDeque<>();
        final var elementNameStack = new ArrayDeque<>(List.of(""));
        final var stateStack = new ArrayDeque<>(List.of(stateFromObject(componentHolder)));

        State state;
        while (!stateStack.isEmpty()) {
            state = stateStack.removeLast();
            elementNameStack.removeLast();

            while (state.iterator.hasNext()) {
                final var component = state.iterator.next();
                final var componentValue = component.value(state.componentHolder);

                if ((componentValue == null) && !outputNull)
                    continue;

                final var componentName = component.name();
                final var commentNode = createNodeIfCommentPresent(
                        component.component(),
                        componentName,
                        elementNameStack
                );
                commentNode.ifPresent(result::add);

                final var componentType = component.type();
                if ((componentValue != null) &&
                    (Reflect.isConfiguration(componentType) ||
                     componentType.isRecord())) {
                    stateStack.addLast(state);
                    elementNameStack.addLast(nameFormatter.format(componentName));
                    state = stateFromObject(componentValue);
                }
            }
        }

        return result;
    }

    private State stateFromObject(final Object componentHolder) {
        final var type = componentHolder.getClass();
        final var iter = type.isRecord()
                ? configurationRecordComponents(componentHolder)
                : configurationFields(componentHolder);
        return new State(iter, componentHolder);
    }

    private Optional<CommentNode> createNodeIfCommentPresent(
            final AnnotatedElement element,
            final String elementName,
            final Deque<String> elementNameStack
    ) {
        if (element.isAnnotationPresent(Comment.class)) {
            final var comments = Arrays.stream(element.getAnnotation(Comment.class).value())
                    .flatMap(s -> Arrays.stream(s.split("\n", -1)))
                    .toList();
            final var formattedName = nameFormatter.format(elementName);
            final var elementNames = new ArrayList<>(elementNameStack);
            elementNames.add(formattedName);
            final var result = new CommentNode(comments, elementNames);
            return Optional.of(result);
        }
        return Optional.empty();
    }

    private Iterator<ConfigurationField> configurationFields(Object configuration) {
        return FieldExtractors.CONFIGURATION.extract(configuration.getClass())
                .filter(fieldFilter)
                .map(ConfigurationField::new)
                .iterator();
    }

    private Iterator<ConfigurationRecordComponent> configurationRecordComponents(Object record) {
        return Arrays.stream(record.getClass().getRecordComponents())
                .map(ConfigurationRecordComponent::new)
                .iterator();
    }
}
