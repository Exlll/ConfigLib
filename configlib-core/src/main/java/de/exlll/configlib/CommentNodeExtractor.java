package de.exlll.configlib;

import de.exlll.configlib.TypeComponent.ConfigurationField;
import de.exlll.configlib.TypeComponent.ConfigurationRecordComponent;

import java.lang.reflect.AnnotatedElement;
import java.util.*;

import static de.exlll.configlib.Validator.requireConfigurationOrRecord;

final class CommentNodeExtractor {
    private final FieldFilter fieldFilter;
    private final NameFormatter nameFormatter;
    private final boolean outputNull;

    CommentNodeExtractor(ConfigurationProperties properties) {
        this.fieldFilter = Validator.requireNonNull(properties.getFieldFilter(), "field filter");
        this.nameFormatter = Validator.requireNonNull(properties.getNameFormatter(), "name formatter");
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
                final var componentValue = component.componentValue(state.componentHolder);

                if ((componentValue == null) && !outputNull)
                    continue;

                // pattern switch not yet supported in Java 17
                final AnnotatedElement element = (component instanceof ConfigurationField cf)
                        ? cf.component()
                        : (component instanceof ConfigurationRecordComponent crc)
                        ? crc.component()
                        : null;

                // The element cannot be null because we require a configuration or record
                // at the beginning of this method.
                assert element != null;

                final var componentName = component.componentName();
                final var commentNode = createNodeIfCommentPresent(
                        element,
                        componentName,
                        elementNameStack
                );
                commentNode.ifPresent(result::add);

                if ((componentValue == null) ||
                    (!Reflect.isConfiguration(component.componentType()) &&
                     !component.componentType().isRecord()))
                    continue;

                stateStack.addLast(new State(state.iterator, state.componentHolder));
                elementNameStack.addLast(nameFormatter.format(componentName));
                state = stateFromObject(componentValue);
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
            final var comments = Arrays.stream(element.getAnnotation(Comment.class).value()).flatMap(s -> Arrays.stream(s.split("\n"))).toList();
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
