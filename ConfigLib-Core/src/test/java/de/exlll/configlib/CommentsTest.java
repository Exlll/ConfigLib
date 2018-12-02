package de.exlll.configlib;

import de.exlll.configlib.annotation.Comment;
import de.exlll.configlib.annotation.NestedComment;
import org.junit.jupiter.api.Test;

import static de.exlll.configlib.util.CollectionFactory.listOf;
import static de.exlll.configlib.util.CollectionFactory.mapOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CommentsTest {

    @Test
    void classCommentsAdded() {
        class A {}

        @Comment("B")
        class B {}

        @Comment({"C", "D"})
        class C {}

        Comments comments = Comments.ofClass(A.class);
        assertThat(comments.getClassComments(), empty());
        assertThat(comments.getFieldComments().entrySet(), empty());

        comments = Comments.ofClass(B.class);
        assertThat(comments.getClassComments(), is(listOf("B")));
        assertThat(comments.getFieldComments().entrySet(), empty());

        comments = Comments.ofClass(C.class);
        assertThat(comments.getClassComments(), is(listOf("C", "D")));
        assertThat(comments.getFieldComments().entrySet(), empty());
    }

    @Test
    void fieldCommentsAdded() {
        class A {
            int a;
            @Comment("b")
            int b;
            @Comment({"c", "d"})
            int c;
        }

        Comments comments = Comments.ofClass(A.class);
        assertThat(comments.getClassComments(), empty());
        assertThat(comments.getFieldComments(), is(mapOf(
                "b", listOf("b"),
                "c", listOf("c", "d")
        )));
    }

    @Test
    void nestedFieldAdded() {
        class A {

            @Comment("comment-a")
            @NestedComment
            B a;

            @Comment("comment-a2")
            int a2;

            class B {

                @Comment("comment-b")
                @NestedComment
                C b;

                @Comment("comment-b2")
                int b2;

                class C {

                    @Comment("comment-c")
                    int c;

                    @Comment("comment-d")
                    int d;

                }
            }
        }

        Comments comments = Comments.ofClass(A.class);
        for (String key : comments.getFieldComments().keySet()) {
            System.out.println(key + "  -  " + key.split("\\.")[key.split("\\.").length - 1] + comments.getFieldComments().get(key));
        }
    }
}
