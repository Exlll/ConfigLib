package de.exlll.configlib;

import de.exlll.configlib.ConfigurationElements.FieldElement;
import de.exlll.configlib.ConfigurationElements.RecordComponentElement;

import java.lang.reflect.AnnotatedElement;
import java.util.*;

import static de.exlll.configlib.Validator.requireConfigurationType;
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

    private record State(
            Iterator<? extends ConfigurationElement<?>> iterator,
            Object elementHolder
    ) {}

    /**
     * Extracts {@code CommentNode}s of the given configuration type in a DFS manner.
     * The nodes are returned in the order in which they were found.
     *
     * @param elementHolder the elementHolder from which the nodes are extracted
     * @return the nodes in the order in which they are found
     * @throws IllegalArgumentException if {@code elementHolder} is not a configuration type
     * @throws NullPointerException     if {@code elementHolder} is null
     */
    public Queue<CommentNode> extractCommentNodes(final Object elementHolder) {
        requireConfigurationType(elementHolder.getClass());
        final Queue<CommentNode> result = new ArrayDeque<>();
        final var elementNameStack = new ArrayDeque<>(List.of(""));
        final var stateStack = new ArrayDeque<>(List.of(stateFromObject(elementHolder)));

        State state;
        while (!stateStack.isEmpty()) {
            state = stateStack.removeLast();
            elementNameStack.removeLast();

            while (state.iterator.hasNext()) {
                final var element = state.iterator.next();
                final var elementValue = element.value(state.elementHolder);

                if ((elementValue == null) && !outputNull)
                    continue;

                final var elementName = element.name();
                final var commentNode = createNodeIfCommentPresent(
                        element.element(),
                        elementName,
                        elementNameStack
                );
                commentNode.ifPresent(result::add);

                final var elementType = element.type();
                if ((elementValue != null) && Reflect.isConfigurationType(elementType)) {
                    stateStack.addLast(state);
                    elementNameStack.addLast(nameFormatter.format(elementName));
                    state = stateFromObject(elementValue);
                }
            }
        }

        return result;
    }

    private State stateFromObject(final Object elementHolder) {
        final var type = elementHolder.getClass();
        final var iter = type.isRecord()
                ? recordComponentElements(elementHolder)
                : fieldElements(elementHolder);
        return new State(iter, elementHolder);
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

    private Iterator<FieldElement> fieldElements(Object configuration) {
        return FieldExtractors.CONFIGURATION.extract(configuration.getClass())
                .filter(fieldFilter)
                .map(FieldElement::new)
                .iterator();
    }

    private Iterator<RecordComponentElement> recordComponentElements(Object record) {
        return Arrays.stream(record.getClass().getRecordComponents())
                .map(RecordComponentElement::new)
                .iterator();
    }
}
