package de.exlll.configlib;

import de.exlll.configlib.KeyedEntry.MissingKeyedEntry;
import de.exlll.configlib.KeyedEntry.MissingKeyedEntry.Reason;
import org.junit.jupiter.api.Test;

import static de.exlll.configlib.Key.key;
import static de.exlll.configlib.TestUtils.assertThrowsIllegalArgumentException;
import static de.exlll.configlib.TestUtils.assertThrowsNullPointerException;
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
}