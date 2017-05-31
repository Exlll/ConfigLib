package de.exlll.configlib;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CommentAdderTest {
    @Test
    public void adderAddsComments() throws Exception {
        String originalText = "i: 0\n" +
                "j: 0\n" +
                "k: 0\n";
        String commentedText = "# a\n" +
                "# b\n\n" +
                "# c\n" +
                "i: 0\n" +
                "# d\n" +
                "# e\n" +
                "j: 0\n" +
                "k: 0\n";
        Comments comments = new Comments(CommentsTest.TestClass.class);
        CommentAdder adder = new CommentAdder(comments);
        assertThat(adder.addComments(originalText), is(commentedText));
    }
}