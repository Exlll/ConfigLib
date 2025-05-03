package de.exlll.configlib;

import org.junit.jupiter.api.Test;

import java.util.List;

import static de.exlll.configlib.Key.key;
import static de.exlll.configlib.Key.listIdx;
import static de.exlll.configlib.TestUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class KeyTest {

    @Test
    void ctorRequiresNonNullNonEmptyList() {
        assertThrowsNullPointerException(
                () -> new Key(null),
                "list of parts"
        );
        assertThrowsIllegalArgumentException(
                () -> new Key(asList()),
                "The list of parts must not be empty."
        );
    }

    @Test
    void ctorRequiresOnlyNullAndValidTargetTypes() {
        // the following keys are all valid
        new Key(asList(null, (Object) null));
        new Key(asList(true, false));
        new Key(asList(3L, 4L));
        new Key(asList(5.0, 6.0));
        new Key(asList("", ""));
        new Key(asList(listIdx(0), listIdx(100)));

        assertThrowsIllegalArgumentException(
                () -> new Key(asList((byte) 1)),
                "Part '1' at index 0 must be of a simple target type but its type is 'class java.lang.Byte'"
        );
        assertThrowsIllegalArgumentException(
                () -> new Key(asList(1L, (byte) 2)),
                "Part '2' at index 1 must be of a simple target type but its type is 'class java.lang.Byte'"
        );
        assertThrowsIllegalArgumentException(
                () -> new Key(asList(1L, 2L, (short) 3)),
                "Part '3' at index 2 must be of a simple target type but its type is 'class java.lang.Short'"
        );
        assertThrowsIllegalArgumentException(
                () -> new Key(asList(1L, 2L, 3L, 4)),
                "Part '4' at index 3 must be of a simple target type but its type is 'class java.lang.Integer'"
        );
        assertThrowsIllegalArgumentException(
                () -> new Key(asList(1L, 2L, 3L, 4L, (float) 5)),
                "Part '5.0' at index 4 must be of a simple target type but its type is 'class java.lang.Float'"
        );
        final Object object = new Object();
        assertThrowsIllegalArgumentException(
                () -> new Key(asList(object)),
                "Part '%s' at index 0 must be of a simple target type but its type is 'class java.lang.Object'"
                        .formatted(object.toString())
        );
    }

    @Test
    void ctorMakesListUnmodifiable() {
        Key key = new Key(asList(1L, "2", 3.0, true));
        assertThrowsException(
                UnsupportedOperationException.class,
                () -> key.getAllParts().add(""),
                null
        );
    }

    @Test
    void ctorCopiesList() {
        Key key = new Key(asList(1L, "2", 3.0, true, listIdx(4)));

        List<Object> allParts = key.getAllParts();
        assertThat(allParts.size(), is(5));

        assertThat(allParts.get(0), is(1L));
        assertThat(allParts.get(1), is("2"));
        assertThat(allParts.get(2), is(3.0));
        assertThat(allParts.get(3), is(true));
        assertThat(allParts.get(4), is(listIdx(4)));

        assertThat(allParts.get(0), is(key.getPart(0)));
        assertThat(allParts.get(1), is(key.getPart(1)));
        assertThat(allParts.get(2), is(key.getPart(2)));
        assertThat(allParts.get(3), is(key.getPart(3)));
        assertThat(allParts.get(4), is(key.getPart(4)));
    }

    @Test
    void keyFromArrayRequiresNonNullOtherArray() {
        assertThrowsNullPointerException(
                () -> Key.key("a", null),
                "array of other parts"
        );
        assertThrowsNullPointerException(
                () -> Key.key("a", (Object[]) null),
                "array of other parts"
        );
        Key.key("a", (Object) null); // that's okay
    }

    @Test
    void keyFromArrayConvertsIntegerPartsToLongs() {
        Key key = Key.key((int) 0, (Integer) 1, 2);
        assertThat(key.getPart(0), instanceOf(Long.class));
        assertThat(key.getPart(1), instanceOf(Long.class));
        assertThat(key.getPart(2), instanceOf(Long.class));
    }

    @Test
    void keyFromListRequiresNonNullParts() {
        assertThrowsNullPointerException(
                () -> Key.key(null),
                "list of parts"
        );
        Key.key(asList((Object) null)); // that's okay
    }

    @Test
    void keyFromListConvertsIntegerPartsToLongs() {
        Key key = Key.key(asList((int) 0, (Integer) 1, 2));
        assertThat(key.getPart(0), instanceOf(Long.class));
        assertThat(key.getPart(1), instanceOf(Long.class));
        assertThat(key.getPart(2), instanceOf(Long.class));
    }

    @Test
    void listIndexMustBeAtLeastZero() {
        // these are ok
        new Key.ListIndex(0);
        new Key.ListIndex(1);
        new Key.ListIndex(100);
        new Key.ListIndex(Integer.MAX_VALUE);

        assertThrowsIllegalArgumentException(
                () -> new Key.ListIndex(-1),
                "List indices must be at least zero but the number you provided is -1."
        );
        assertThrowsIllegalArgumentException(
                () -> new Key.ListIndex(-100),
                "List indices must be at least zero but the number you provided is -100."
        );
        assertThrowsIllegalArgumentException(
                () -> new Key.ListIndex(Integer.MIN_VALUE),
                "List indices must be at least zero but the number you provided is " +
                Integer.MIN_VALUE + "."
        );
    }

    @Test
    void numParts() {
        assertThat(key(1).numParts(), is(1));
        assertThat(key(1, "2").numParts(), is(2));
        assertThat(key(1, "2", 3.0).numParts(), is(3));
        assertThat(key(1, "2", 3.0, true).numParts(), is(4));
    }

    @Test
    void lastPart() {
        assertThat(key(1).getLastPart(), is(1L));
        assertThat(key(1, "2").getLastPart(), is("2"));
        assertThat(key(1, "2", 3.0).getLastPart(), is(3.0));
        assertThat(key(1, "2", 3.0, true).getLastPart(), is(true));
    }
}