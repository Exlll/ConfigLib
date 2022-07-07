package de.exlll.configlib;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import static de.exlll.configlib.TestUtils.assertThrowsConfigurationException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings({"unused", "FieldMayBeFinal"})
class CommentNodeExtractorTest {
    private static final ConfigurationProperties PROPERTIES = ConfigurationProperties.newBuilder()
            .outputNulls(true)
            .build();

    private final CommentNodeExtractor EXTRACTOR = new CommentNodeExtractor(PROPERTIES);

    @Test
    void requiresConfiguration() {
        assertThrowsConfigurationException(
                () -> EXTRACTOR.extractCommentNodes(new Object()),
                "Class 'Object' must be a configuration."
        );
    }

    @Test
    void extractSingleComment1() {
        @Configuration
        class A {
            @Comment("Hello")
            int i;
        }

        Queue<CommentNode> nodes = EXTRACTOR.extractCommentNodes(new A());
        assertEquals(cn(List.of("Hello"), "i"), nodes.poll());
        assertTrue(nodes.isEmpty());
    }

    @Test
    void extractSingleComment2() {
        @Configuration
        class A {
            @Comment({"Hello", "World"})
            int i;
        }

        Queue<CommentNode> nodes = EXTRACTOR.extractCommentNodes(new A());
        assertEquals(cn(List.of("Hello", "World"), "i"), nodes.poll());
        assertTrue(nodes.isEmpty());
    }

    @Test
    void extractMultipleComments() {
        @Configuration
        class A {
            @Comment("Hello")
            int i;
            int j;
            @Comment("World")
            int k;
        }

        Queue<CommentNode> nodes = EXTRACTOR.extractCommentNodes(new A());
        assertEquals(cn(List.of("Hello"), "i"), nodes.poll());
        assertEquals(cn(List.of("World"), "k"), nodes.poll());
        assertTrue(nodes.isEmpty());
    }

    @Test
    void extractNestedComment1() {
        @Configuration
        class A {
            @Comment("Hello")
            int i;
        }
        @Configuration
        class B {
            A a = new A();
        }
        Queue<CommentNode> nodes = EXTRACTOR.extractCommentNodes(new B());
        assertEquals(cn(List.of("Hello"), "a", "i"), nodes.poll());
        assertTrue(nodes.isEmpty());
    }

    @Test
    void extractNestedComment2() {
        @Configuration
        class A {
            @Comment("Hello")
            int i;
            int j;
            @Comment("World")
            int k;
        }
        @Configuration
        class B {
            A a = new A();
        }
        Queue<CommentNode> nodes = EXTRACTOR.extractCommentNodes(new B());
        assertEquals(cn(List.of("Hello"), "a", "i"), nodes.poll());
        assertEquals(cn(List.of("World"), "a", "k"), nodes.poll());
        assertTrue(nodes.isEmpty());
    }

    @Test
    void extractNestedComment3() {
        @Configuration
        class A {
            @Comment("Hello")
            int i;
            int j;
            @Comment("World")
            int k;
        }
        @Configuration
        class B {
            @Comment("Hello")
            A a1 = new A();
            @Comment("World")
            A a2 = new A();
        }
        @Configuration
        class C {
            @Comment("Hello")
            B b = new B();
            @Comment("World")
            A a = new A();
        }
        @Configuration
        class D {
            C c = new C();
        }
        Queue<CommentNode> nodes = EXTRACTOR.extractCommentNodes(new D());
        assertEquals(cn(List.of("Hello"), "c", "b"), nodes.poll());
        assertEquals(cn(List.of("Hello"), "c", "b", "a1"), nodes.poll());
        assertEquals(cn(List.of("Hello"), "c", "b", "a1", "i"), nodes.poll());
        assertEquals(cn(List.of("World"), "c", "b", "a1", "k"), nodes.poll());
        assertEquals(cn(List.of("World"), "c", "b", "a2"), nodes.poll());
        assertEquals(cn(List.of("Hello"), "c", "b", "a2", "i"), nodes.poll());
        assertEquals(cn(List.of("World"), "c", "b", "a2", "k"), nodes.poll());
        assertEquals(cn(List.of("World"), "c", "a"), nodes.poll());
        assertEquals(cn(List.of("Hello"), "c", "a", "i"), nodes.poll());
        assertEquals(cn(List.of("World"), "c", "a", "k"), nodes.poll());
        assertTrue(nodes.isEmpty());
    }

    @Test
    void extractEmptyClass() {
        @Configuration
        class A {}
        Queue<CommentNode> nodes = EXTRACTOR.extractCommentNodes(new A());
        assertTrue(nodes.isEmpty());
    }

    @Test
    void extractNestedOnlyIfNotNull() {
        @Configuration
        class A {
            @Comment("Hello")
            int i;
        }
        @Configuration
        class B {
            A a = null;
        }
        @Configuration
        class C {
            @Comment("Hello")
            A a = null;
        }
        Queue<CommentNode> nodes1 = EXTRACTOR.extractCommentNodes(new B());
        assertTrue(nodes1.isEmpty());

        Queue<CommentNode> nodes2 = EXTRACTOR.extractCommentNodes(new C());
        assertEquals(cn(List.of("Hello"), "a"), nodes2.poll());
        assertTrue(nodes2.isEmpty());
    }

    @Test
    void extractIgnoresCommentIfFieldNullAndOutputNull1() {
        @Configuration
        class A {
            @Comment("Hello")
            int i;
        }
        @Configuration
        class B {
            A a = null;
        }
        @Configuration
        class C {
            @Comment("Hello")
            A a = null;
        }
        ConfigurationProperties properties = ConfigurationProperties.newBuilder()
                .outputNulls(false)
                .build();
        CommentNodeExtractor extractor = new CommentNodeExtractor(properties);

        Queue<CommentNode> nodes1 = extractor.extractCommentNodes(new B());
        assertTrue(nodes1.isEmpty());

        Queue<CommentNode> nodes2 = extractor.extractCommentNodes(new C());
        assertTrue(nodes2.isEmpty());
    }

    @Test
    void extractIgnoresCommentIfFieldNullAndOutputNull2() {
        @Configuration
        class A {
            @Comment("Hello")
            String s1 = null;
            @Comment("World")
            String s2 = "";
        }
        ConfigurationProperties properties = ConfigurationProperties.newBuilder()
                .outputNulls(false)
                .build();
        CommentNodeExtractor extractor = new CommentNodeExtractor(properties);
        Queue<CommentNode> nodes = extractor.extractCommentNodes(new A());
        assertEquals(cn(List.of("World"), "s2"), nodes.poll());
        assertTrue(nodes.isEmpty());
    }

    @Test
    void extractAppliesFormatterAndFilter() {
        @Configuration
        class A {
            @Comment("Hello")
            int i;
            int j;
            @Comment("World")
            int k;
        }
        @Configuration
        class B {
            @Comment("Hello")
            A a1 = new A();
            @Comment("World")
            A a2 = new A();
        }
        @Configuration
        class C {
            @Comment("Hello")
            B b = new B();
            @Comment("World")
            A a = new A();
        }

        ConfigurationProperties properties = ConfigurationProperties.newBuilder()
                .setFieldFilter(field -> {
                    String name = field.getName();
                    return !name.equals("a1") && !name.equals("a");
                })
                .setFieldFormatter(FieldFormatters.UPPER_UNDERSCORE)
                .build();

        CommentNodeExtractor extractor = new CommentNodeExtractor(properties);
        Queue<CommentNode> nodes = extractor.extractCommentNodes(new C());
        assertEquals(cn(List.of("Hello"), "B"), nodes.poll());
        assertEquals(cn(List.of("World"), "B", "A2"), nodes.poll());
        assertEquals(cn(List.of("Hello"), "B", "A2", "I"), nodes.poll());
        assertEquals(cn(List.of("World"), "B", "A2", "K"), nodes.poll());
        assertTrue(nodes.isEmpty());
    }

    @Test
    void extractIgnoresCommentsForConfigurationsInCollections() {
        @Configuration
        class A {
            @Comment("Yes")
            int i;
        }
        @Configuration
        class B {
            Set<A> setA;
            List<A> listA;
            Map<Integer, A> mapIntegerA;
        }
        Queue<CommentNode> nodes = EXTRACTOR.extractCommentNodes(new B());
        assertTrue(nodes.isEmpty());
    }

    @Test
    void extractCommentsFromParentClasses() {
        @Configuration
        class A1 {
            @Comment({"int", "i"})
            int i;
        }
        @Configuration
        class A2 extends A1 {
            @Comment({"int", "j"})
            int j;
        }
        @Configuration
        class B1 {
            @Comment({"A2", "a2"})
            A2 a2 = new A2();
        }
        @Configuration
        class B2 extends B1 {
            @Comment({"A1", "a1"})
            A1 a1 = new A1();
        }
        Queue<CommentNode> nodes = EXTRACTOR.extractCommentNodes(new B2());
        assertEquals(cn(List.of("A2", "a2"), "a2"), nodes.poll());
        assertEquals(cn(List.of("int", "i"), "a2", "i"), nodes.poll());
        assertEquals(cn(List.of("int", "j"), "a2", "j"), nodes.poll());
        assertEquals(cn(List.of("A1", "a1"), "a1"), nodes.poll());
        assertEquals(cn(List.of("int", "i"), "a1", "i"), nodes.poll());
        assertTrue(nodes.isEmpty());
    }

    private static CommentNode cn(List<String> comments, String... fieldNames) {
        return new CommentNode(comments, List.of(fieldNames));
    }
}