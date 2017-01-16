package de.exlll.configlib;

import com.google.common.jimfs.Jimfs;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Field;
import java.nio.file.FileSystem;
import java.util.*;
import java.util.function.Predicate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class CommentsTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private static final FileSystem fs = Jimfs.newFileSystem();
    private static final Predicate<Field> TRUE = x -> true;

    @Test
    public void factoryRequiresNonNullSupplier() throws Exception {
        exception.expect(NullPointerException.class);
        Comments.from(null);
    }

    @Test
    public void factoryReturnsNotNull() throws Exception {
        Comments comments = Comments.from(new FilteredFieldStreamSupplier(
                getClass(), TRUE));
        assertThat(comments, notNullValue());
    }

    @Test
    public void factoryReturnsCommentsWithClassComments() throws Exception {
        Comments comments = Comments.from(new FilteredFieldStreamSupplier(
                TestClassWithComment.class, TRUE));

        List<String> classComments = Collections.singletonList("This is a class comment.");
        assertThat(comments.getClassComments(), is(classComments));
    }

    @Test
    public void factoryReturnsCommentsWithFilteredFieldComments() throws Exception {
        Comments comments = Comments.from(new FilteredFieldStreamSupplier(
                TestClass.class, TRUE));

        /* Fields which don't have a Comment annotation are filtered out. */
        Map<String, List<String>> commentsByFieldName = new HashMap<>();
        commentsByFieldName.put("field1", Collections.singletonList("Field1"));
        commentsByFieldName.put("field2", Arrays.asList("Field2", "field2"));

        assertThat(comments.getCommentsByFieldNames(), is(commentsByFieldName));
    }

    @Test
    public void getCommentsReturnsEmptyListIfNotCommentsPresent() throws Exception {
        List<String> comments = Comments.getComments(TestClassWithoutComment.class);

        assertThat(comments, is(Collections.emptyList()));
    }

    @Test
    public void getCommentsReturnsCommentsAsList() throws Exception {
        List<String> comments = Comments.getComments(TestClassWithComment.class);

        assertThat(comments, is(Collections.singletonList("This is a class comment.")));
    }


    @Comment("This is a class comment.")
    private static final class TestClassWithComment {
    }

    private static final class TestClassWithoutComment {
    }

    private static final class TestClass {
        @Comment("Field1")
        private int field1;
        @Comment({"Field2", "field2"})
        private int field2;
        private int field3;
    }
}