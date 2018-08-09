package de.exlll.configlib;

import de.exlll.configlib.annotation.Comment;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

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
        assertThat(comments.getClassComments(), is(List.of("B")));
        assertThat(comments.getFieldComments().entrySet(), empty());

        comments = Comments.ofClass(C.class);
        assertThat(comments.getClassComments(), is(List.of("C", "D")));
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
        assertThat(comments.getFieldComments(), is(Map.of(
                "b", List.of("b"),
                "c", List.of("c", "d")
        )));
    }
}
