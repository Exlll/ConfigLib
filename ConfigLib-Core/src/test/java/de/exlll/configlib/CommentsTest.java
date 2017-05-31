package de.exlll.configlib;

import org.junit.Test;

import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class CommentsTest {
    @Comment({"a", "b"})
    public static final class TestClass {
        @Comment("c")
        private int i;
        @Comment({"d", "e"})
        private int j;
        private int k;
    }

    @Test
    public void getCommentsReturnsComments() throws Exception {
        Comments comments = new Comments(TestClass.class);

        assertThat(comments.getClassComments(), is(Arrays.asList("a", "b")));

        assertThat(comments.getFieldComments().get("i"), is(Collections.singletonList("c")));
        assertThat(comments.getFieldComments().get("j"), is(Arrays.asList("d", "e")));
        assertThat(comments.getFieldComments().get("k"), nullValue());
    }
}