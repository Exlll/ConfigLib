package de.exlll.configlib;

import de.exlll.configlib.KeyedEntry.MissingKeyedEntry;
import de.exlll.configlib.KeyedEntry.MissingKeyedEntry.Reason;
import org.junit.jupiter.api.Test;

import static de.exlll.configlib.Key.key;
import static de.exlll.configlib.TestUtils.assertThrowsIllegalArgumentException;
import static de.exlll.configlib.TestUtils.assertThrowsNullPointerException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;

class KeyedEntryTest {
    @Test
    void ctorExistingKeyedEntryRequiresNonNullKey() {
        assertThrowsNullPointerException(
                () -> new KeyedEntry.ExistingKeyedEntry(null, null),
                "key"
        );
    }

    @Test
    void ctorMissingKeyedEntryRequiresNonNullKeyAndMissingKey() {
        assertThrowsNullPointerException(
                () -> new MissingKeyedEntry(null, null, null, null),
                "key used to access this entry"
        );
        assertThrowsNullPointerException(
                () -> new MissingKeyedEntry(key(""), null, null, null),
                "key of missing parts"
        );
        assertThrowsNullPointerException(
                () -> new MissingKeyedEntry(key(""), null, key(""), null),
                "reason"
        );

        // this is ok
        new MissingKeyedEntry(key(""), null, key(""), Reason.PART_MISSING);
    }

    @Test
    void fullKeyConsistsOfExistingAndMissingKey() {
        final Key existingKey = Key.key(1, true);
        final Key missingKey = Key.key("a", 2.0);

        // this is ok
        new MissingKeyedEntry(Key.key(1, true, "a", 2.0), existingKey, missingKey, Reason.PART_MISSING);

        assertThrowsIllegalArgumentException(
                () -> new MissingKeyedEntry(Key.key(1, true, "b", 2.0), existingKey, missingKey, Reason.PART_MISSING),
                "Full key: Key[1, true, b, 2.0]; Existing key: Key[1, true]; Missing key: Key[a, 2.0]"
        );
        assertThrowsIllegalArgumentException(
                () -> new MissingKeyedEntry(Key.key("b", 2.0), null, missingKey, Reason.PART_MISSING),
                "Full key: Key[b, 2.0]; Existing key: null; Missing key: Key[a, 2.0]"
        );
    }


    @Test
    void missingKeyedEntryFromKeyRequiresNonNullArgs() {
        assertThrowsNullPointerException(
                () -> MissingKeyedEntry.fromKey(null, 0, Reason.PART_MISSING),
                "key"
        );
        assertThrowsNullPointerException(
                () -> MissingKeyedEntry.fromKey(Key.key("aaa"), 0, null),
                "reason"
        );
    }

    @Test
    void missingKeyedEntryFromKeyUsesReason() {
        final Key key = Key.key(1, "2", 3.0, null, false);

        var entry1 = MissingKeyedEntry.fromKey(key, 0, Reason.PART_MISSING);
        assertThat(entry1.reason(), is(Reason.PART_MISSING));

        var entry2 = MissingKeyedEntry.fromKey(key, 0, Reason.PART_WRONG_TYPE);
        assertThat(entry2.reason(), is(Reason.PART_WRONG_TYPE));
    }

    @Test
    void missingKeyedEntryFromKeySplitsAtIndex() {
        final Key key = Key.key(1, "2", 3.0, null, false);

        {
            var entry = MissingKeyedEntry.fromKey(key, 0, Reason.PART_MISSING);
            assertThat(entry.key(), is(key));
            assertThat(entry.existing(), nullValue());
            assertThat(entry.missing(), is(Key.key(1, "2", 3.0, null, false)));
        }

        {
            var entry = MissingKeyedEntry.fromKey(key, 1, Reason.PART_MISSING);
            assertThat(entry.key(), is(key));
            assertThat(entry.existing(), is(Key.key(1)));
            assertThat(entry.missing(), is(Key.key("2", 3.0, null, false)));
        }

        {
            var entry = MissingKeyedEntry.fromKey(key, 2, Reason.PART_MISSING);
            assertThat(entry.key(), is(key));
            assertThat(entry.existing(), is(Key.key(1, "2")));
            assertThat(entry.missing(), is(Key.key(3.0, null, false)));
        }


        {
            var entry = MissingKeyedEntry.fromKey(key, 3, Reason.PART_MISSING);
            assertThat(entry.key(), is(key));
            assertThat(entry.existing(), is(Key.key(1, "2", 3.0)));
            assertThat(entry.missing(), is(Key.key(null, false)));
        }

        {
            var entry = MissingKeyedEntry.fromKey(key, 4, Reason.PART_MISSING);
            assertThat(entry.key(), is(key));
            assertThat(entry.existing(), is(Key.key(1, "2", 3.0, null)));
            assertThat(entry.missing(), is(Key.key(false)));
        }

    }

    @Test
    void missingKeyedEntryFromKeyRequiresValidSplitKey() {
        final Key key = Key.key(1, "2", 3.0, null, false);

        assertThrowsIllegalArgumentException(
                () -> MissingKeyedEntry.fromKey(key, -1, Reason.PART_MISSING),
                "The split index must not be negative but is -1"
        );
        assertThrowsIllegalArgumentException(
                () -> MissingKeyedEntry.fromKey(key, -10, Reason.PART_MISSING),
                "The split index must not be negative but is -10"
        );

        assertThrowsIllegalArgumentException(
                () -> MissingKeyedEntry.fromKey(key, 5, Reason.PART_MISSING),
                "To create a MissingKeyedEntry the list of missing parts must contain " +
                "at least one entry. Therefore, the split index must not be equal or " +
                "greater than the number of parts in the given key but it is 5."
        );

        assertThrowsIllegalArgumentException(
                () -> MissingKeyedEntry.fromKey(key, 500, Reason.PART_MISSING),
                "To create a MissingKeyedEntry the list of missing parts must contain " +
                "at least one entry. Therefore, the split index must not be equal or " +
                "greater than the number of parts in the given key but it is 500."
        );
    }
}