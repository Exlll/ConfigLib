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
                "Class 'Object' must be a configuration or record."
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
    void extractSingleComment1Record() {
        record R(@Comment("Hello") int i) {}
        Queue<CommentNode> nodes = EXTRACTOR.extractCommentNodes(new R(10));
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
    void extractSingleComment2Record() {
        record R(@Comment({"Hello", "World"}) int i) {}

        Queue<CommentNode> nodes = EXTRACTOR.extractCommentNodes(new R(10));
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
    void extractMultipleCommentsRecord() {
        record R(@Comment("Hello") int i, int j, @Comment("World") int k) {}

        Queue<CommentNode> nodes = EXTRACTOR.extractCommentNodes(new R(1, 2, 3));
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
    void extractNestedComment1Record() {
        record R1(@Comment("Hello") int i) {}
        record R2(R1 r1) {}
        Queue<CommentNode> nodes = EXTRACTOR.extractCommentNodes(new R2(new R1(1)));
        assertEquals(cn(List.of("Hello"), "r1", "i"), nodes.poll());
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
    void extractNestedComment2Record() {
        record R1(@Comment("Hello") int i, int j, @Comment("World") int k) {}
        record R2(R1 r1) {}
        Queue<CommentNode> nodes = EXTRACTOR.extractCommentNodes(new R2(new R1(1, 2, 3)));
        assertEquals(cn(List.of("Hello"), "r1", "i"), nodes.poll());
        assertEquals(cn(List.of("World"), "r1", "k"), nodes.poll());
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
    void extractNestedComment3Record() {
        record R1(@Comment("Hello") int i, int j, @Comment("World") int k) {}
        record R2(@Comment("Hello") R1 r11, @Comment("World") R1 r12) {}
        record R3(@Comment("Hello") R2 r21, @Comment("World") R1 r11) {}
        record R4(R3 r3) {}
        R1 r1 = new R1(1, 2, 3);
        R2 r2 = new R2(r1, r1);
        R3 r3 = new R3(r2, r1);
        Queue<CommentNode> nodes = EXTRACTOR.extractCommentNodes(new R4(r3));
        assertEquals(cn(List.of("Hello"), "r3", "r21"), nodes.poll());
        assertEquals(cn(List.of("Hello"), "r3", "r21", "r11"), nodes.poll());
        assertEquals(cn(List.of("Hello"), "r3", "r21", "r11", "i"), nodes.poll());
        assertEquals(cn(List.of("World"), "r3", "r21", "r11", "k"), nodes.poll());
        assertEquals(cn(List.of("World"), "r3", "r21", "r12"), nodes.poll());
        assertEquals(cn(List.of("Hello"), "r3", "r21", "r12", "i"), nodes.poll());
        assertEquals(cn(List.of("World"), "r3", "r21", "r12", "k"), nodes.poll());
        assertEquals(cn(List.of("World"), "r3", "r11"), nodes.poll());
        assertEquals(cn(List.of("Hello"), "r3", "r11", "i"), nodes.poll());
        assertEquals(cn(List.of("World"), "r3", "r11", "k"), nodes.poll());
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
    void extractEmptyRecord() {
        record R() {}
        Queue<CommentNode> nodes = EXTRACTOR.extractCommentNodes(new R());
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
            @Comment("World")
            A a = null;
        }
        Queue<CommentNode> nodes1 = EXTRACTOR.extractCommentNodes(new B());
        assertTrue(nodes1.isEmpty());

        Queue<CommentNode> nodes2 = EXTRACTOR.extractCommentNodes(new C());
        assertEquals(cn(List.of("World"), "a"), nodes2.poll());
        assertTrue(nodes2.isEmpty());
    }

    @Test
    void extractNestedOnlyIfNotNullRecord() {
        record R1(@Comment("Hello") int i) {}
        record R2(R1 r1) {}
        record R3(@Comment("World") R1 r1) {}

        Queue<CommentNode> nodes1 = EXTRACTOR.extractCommentNodes(new R2(null));
        assertTrue(nodes1.isEmpty());

        Queue<CommentNode> nodes2 = EXTRACTOR.extractCommentNodes(new R3(null));
        assertEquals(cn(List.of("World"), "r1"), nodes2.poll());
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
            @Comment("World")
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
    void extractIgnoresCommentIfComponentNullAndOutputNull1() {
        record R1(@Comment("Hello") int i) {}
        record R2(R1 r1) {}
        record R3(@Comment("World") R1 r1) {}
        ConfigurationProperties properties = ConfigurationProperties.newBuilder()
                .outputNulls(false)
                .build();
        CommentNodeExtractor extractor = new CommentNodeExtractor(properties);

        Queue<CommentNode> nodes1 = extractor.extractCommentNodes(new R2(null));
        assertTrue(nodes1.isEmpty());

        Queue<CommentNode> nodes2 = extractor.extractCommentNodes(new R3(null));
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
    void extractIgnoresCommentIfComponentNullAndOutputNull2() {
        record R(@Comment("Hello") String s1, @Comment("World") String s2) {}
        ConfigurationProperties properties = ConfigurationProperties.newBuilder()
                .outputNulls(false)
                .build();
        CommentNodeExtractor extractor = new CommentNodeExtractor(properties);
        Queue<CommentNode> nodes = extractor.extractCommentNodes(new R(null, ""));
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
                .setNameFormatter(NameFormatters.UPPER_UNDERSCORE)
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
    void extractAppliesFormatterButNotFilterRecord() {
        record R1(@Comment("Hello") int i, int j, @Comment("World") int k) {}
        record R2(@Comment("Hello") R1 r11, @Comment("World") R1 r12) {}
        record R3(@Comment("Hello") R2 r2, @Comment("World") R1 r1) {}

        R1 r1 = new R1(1, 2, 3);
        R2 r2 = new R2(r1, r1);

        ConfigurationProperties properties = ConfigurationProperties.newBuilder()
                .setFieldFilter(field -> {
                    String name = field.getName();
                    return !name.equals("r11") && !name.equals("r1");
                })
                .setNameFormatter(NameFormatters.UPPER_UNDERSCORE)
                .build();

        CommentNodeExtractor extractor = new CommentNodeExtractor(properties);
        Queue<CommentNode> nodes = extractor.extractCommentNodes(new R3(r2, r1));

        assertEquals(cn(List.of("Hello"), "R2"), nodes.poll());
        assertEquals(cn(List.of("Hello"), "R2", "R11"), nodes.poll());
        assertEquals(cn(List.of("Hello"), "R2", "R11", "I"), nodes.poll());
        assertEquals(cn(List.of("World"), "R2", "R11", "K"), nodes.poll());
        assertEquals(cn(List.of("World"), "R2", "R12"), nodes.poll());
        assertEquals(cn(List.of("Hello"), "R2", "R12", "I"), nodes.poll());
        assertEquals(cn(List.of("World"), "R2", "R12", "K"), nodes.poll());
        assertEquals(cn(List.of("World"), "R1"), nodes.poll());
        assertEquals(cn(List.of("Hello"), "R1", "I"), nodes.poll());
        assertEquals(cn(List.of("World"), "R1", "K"), nodes.poll());
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
    void extractIgnoresCommentsForRecordsInCollections() {
        record R(@Comment("Yes") int i) {}
        @Configuration
        class B {
            Set<R> setR = Set.of(new R(1));
            List<R> listR = List.of(new R(2));
            Map<Integer, R> mapIntegerR = Map.of(1, new R(3));
        }
        record C(Set<R> setR, List<R> listR, Map<Integer, R> mapIntegerR) {}
        C c = new C(Set.of(new R(1)), List.of(new R(2)), Map.of(1, new R(3)));
        Queue<CommentNode> nodes = EXTRACTOR.extractCommentNodes(new B());
        assertTrue(nodes.isEmpty());
        nodes = EXTRACTOR.extractCommentNodes(c);
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

    @Test
    void extractCommentsForRecordInClass() {
        record R(@Comment("Hello") int i, int j, @Comment("World") int k) {}
        @Configuration
        class A {
            @Comment({"A", "B"})
            R r = new R(1, 2, 3);
        }
        Queue<CommentNode> nodes = EXTRACTOR.extractCommentNodes(new A());
        assertEquals(cn(List.of("A", "B"), "r"), nodes.poll());
        assertEquals(cn(List.of("Hello"), "r", "i"), nodes.poll());
        assertEquals(cn(List.of("World"), "r", "k"), nodes.poll());
        assertTrue(nodes.isEmpty());
    }

    @Test
    void extractCommentsForClassInRecord() {
        @Configuration
        class A {
            @Comment("Hello")
            int i;
            int j;
            @Comment("World")
            int k;
        }
        record R(@Comment({"A", "B"}) A a) {}
        Queue<CommentNode> nodes = EXTRACTOR.extractCommentNodes(new R(new A()));
        assertEquals(cn(List.of("A", "B"), "a"), nodes.poll());
        assertEquals(cn(List.of("Hello"), "a", "i"), nodes.poll());
        assertEquals(cn(List.of("World"), "a", "k"), nodes.poll());
        assertTrue(nodes.isEmpty());
    }

    private static CommentNode cn(List<String> comments, String... fieldNames) {
        return new CommentNode(comments, List.of(fieldNames));
    }
}