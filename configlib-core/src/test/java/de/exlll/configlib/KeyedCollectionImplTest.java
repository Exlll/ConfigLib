package de.exlll.configlib;

import de.exlll.configlib.KeyedEntry.MissingKeyedEntry;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static de.exlll.configlib.Key.key;
import static de.exlll.configlib.Key.listIdx;
import static de.exlll.configlib.KeyedEntry.MissingKeyedEntry.*;
import static de.exlll.configlib.TestUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class KeyedCollectionImplTest {

    @Test
    void ctorRequiresValidArguments() {
        assertThrowsNullPointerException(
                () -> new KeyedCollectionImpl(null),
                "delegate"
        );
        assertThrowsIllegalArgumentException(
                () -> new KeyedCollectionImpl("hello"),
                "The delegate must be a list or map but the given object " +
                "'hello' is of type java.lang.String."
        );
    }

    public static class KeyedCollectionPutTest {
        @Test
        void putRequiresNonNullKey() {
            final var collection = newKeyedCollectionMap();
            assertThrowsNullPointerException(
                    () -> collection.put(null, null),
                    "key"
            );
        }

        @Test
        void putRequiresValidValue() {
            final var collection = newKeyedCollectionMap();
            final var object = new Object();
            assertThrowsIllegalArgumentException(
                    () -> collection.put(key("a", "b"), object),
                    "Value '" + object + "' must be null or of a valid target type " +
                    "but its type is java.lang.Object."
            );
        }


        /// *
        //*  aaa: 10
        //*  bbb:
        //*    ccc: 20
        //*    ddd: 30
        //*  eee:
        //*    - 40
        //*    - 50
        //*  fff:
        //*    - ggg: 60
        //*      hhh: 70
        //*    - iii:
        //*        jjj: 80
        //*    - kkk:
        //*        - 90
        //*/
        @Test
        void putOverwritesExistingInMap() {
            final Map<Object, Object> map = asMap(
                    "aaa", 10L,
                    "bbb", asMap("ccc", 20L, "ddd", 30L),
                    "eee", asList(40L, 50L),
                    "fff", asList(
                            asMap("ggg", 60L, "hhh", 70L),
                            asMap("iii", asMap("jjj", 80L)),
                            asMap("kkk", asList(90L)))
            );

            assertPutOverwritesExistingInMap(
                    key("aaa"),
                    3L,
                    key("aaa"),
                    10L,
                    asMap(
                            "aaa", 3L,
                            "bbb", asMap("ccc", 20L, "ddd", 30L),
                            "eee", asList(40L, 50L),
                            "fff", asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    asMap("iii", asMap("jjj", 80L)),
                                    asMap("kkk", asList(90L)))
                    )
            );

            assertPutOverwritesExistingInMap(
                    key("bbb"),
                    3L,
                    key("bbb"),
                    asMap("ccc", 20L, "ddd", 30L),
                    asMap(
                            "aaa", 10L,
                            "bbb", 3L,
                            "eee", asList(40L, 50L),
                            "fff", asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    asMap("iii", asMap("jjj", 80L)),
                                    asMap("kkk", asList(90L)))
                    )
            );
            assertPutOverwritesExistingInMap(
                    key("bbb", "ccc"),
                    3L,
                    key("bbb", "ccc"),
                    20L,
                    asMap(
                            "aaa", 10L,
                            "bbb", asMap("ccc", 3L, "ddd", 30L),
                            "eee", asList(40L, 50L),
                            "fff", asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    asMap("iii", asMap("jjj", 80L)),
                                    asMap("kkk", asList(90L)))
                    )
            );
            assertPutOverwritesExistingInMap(
                    key("bbb", "ddd"),
                    3L,
                    key("bbb", "ddd"),
                    30L,
                    asMap(
                            "aaa", 10L,
                            "bbb", asMap("ccc", 20L, "ddd", 3L),
                            "eee", asList(40L, 50L),
                            "fff", asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    asMap("iii", asMap("jjj", 80L)),
                                    asMap("kkk", asList(90L)))
                    )
            );

            assertPutOverwritesExistingInMap(
                    key("eee"),
                    3L,
                    key("eee"),
                    asList(40L, 50L),
                    asMap(
                            "aaa", 10L,
                            "bbb", asMap("ccc", 20L, "ddd", 30L),
                            "eee", 3L,
                            "fff", asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    asMap("iii", asMap("jjj", 80L)),
                                    asMap("kkk", asList(90L)))
                    )
            );
            assertPutOverwritesExistingInMap(
                    key("eee", listIdx(0)),
                    3L,
                    key("eee", listIdx(0)),
                    40L,
                    asMap(
                            "aaa", 10L,
                            "bbb", asMap("ccc", 20L, "ddd", 30L),
                            "eee", asList(3L, 50L),
                            "fff", asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    asMap("iii", asMap("jjj", 80L)),
                                    asMap("kkk", asList(90L)))
                    )
            );
            assertPutOverwritesExistingInMap(
                    key("eee", listIdx(1)),
                    3L,
                    key("eee", listIdx(1)),
                    50L,
                    asMap(
                            "aaa", 10L,
                            "bbb", asMap("ccc", 20L, "ddd", 30L),
                            "eee", asList(40L, 3L),
                            "fff", asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    asMap("iii", asMap("jjj", 80L)),
                                    asMap("kkk", asList(90L)))
                    )
            );

            assertPutOverwritesExistingInMap(
                    key("fff"),
                    3L,
                    key("fff"),
                    asList(
                            asMap("ggg", 60L, "hhh", 70L),
                            asMap("iii", asMap("jjj", 80L)),
                            asMap("kkk", asList(90L))
                    ),
                    asMap(
                            "aaa", 10L,
                            "bbb", asMap("ccc", 20L, "ddd", 30L),
                            "eee", asList(40L, 50L),
                            "fff", 3L
                    )
            );
            assertPutOverwritesExistingInMap(
                    key("fff", listIdx(0)),
                    3L,
                    key("fff", listIdx(0)),
                    asMap("ggg", 60L, "hhh", 70L),
                    asMap(
                            "aaa", 10L,
                            "bbb", asMap("ccc", 20L, "ddd", 30L),
                            "eee", asList(40L, 50L),
                            "fff", asList(
                                    3L,
                                    asMap("iii", asMap("jjj", 80L)),
                                    asMap("kkk", asList(90L)))
                    )
            );
            assertPutOverwritesExistingInMap(
                    key("fff", listIdx(1)),
                    3L,
                    key("fff", listIdx(1)),
                    asMap("iii", asMap("jjj", 80L)),
                    asMap(
                            "aaa", 10L,
                            "bbb", asMap("ccc", 20L, "ddd", 30L),
                            "eee", asList(40L, 50L),
                            "fff", asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    3L,
                                    asMap("kkk", asList(90L)))
                    )
            );
            assertPutOverwritesExistingInMap(
                    key("fff", listIdx(2)),
                    3L,
                    key("fff", listIdx(2)),
                    asMap("kkk", asList(90L)),
                    asMap(
                            "aaa", 10L,
                            "bbb", asMap("ccc", 20L, "ddd", 30L),
                            "eee", asList(40L, 50L),
                            "fff", asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    asMap("iii", asMap("jjj", 80L)),
                                    3L)
                    )
            );

            assertPutOverwritesExistingInMap(
                    key("fff", listIdx(0), "ggg"),
                    3L,
                    key("fff", listIdx(0), "ggg"),
                    60L,
                    asMap(
                            "aaa", 10L,
                            "bbb", asMap("ccc", 20L, "ddd", 30L),
                            "eee", asList(40L, 50L),
                            "fff", asList(
                                    asMap("ggg", 3L, "hhh", 70L),
                                    asMap("iii", asMap("jjj", 80L)),
                                    asMap("kkk", asList(90L)))
                    )
            );
            assertPutOverwritesExistingInMap(
                    key("fff", listIdx(0), "hhh"),
                    3L,
                    key("fff", listIdx(0), "hhh"),
                    70L,
                    asMap(
                            "aaa", 10L,
                            "bbb", asMap("ccc", 20L, "ddd", 30L),
                            "eee", asList(40L, 50L),
                            "fff", asList(
                                    asMap("ggg", 60L, "hhh", 3L),
                                    asMap("iii", asMap("jjj", 80L)),
                                    asMap("kkk", asList(90L)))
                    )
            );

            assertPutOverwritesExistingInMap(
                    key("fff", listIdx(1), "iii"),
                    3L,
                    key("fff", listIdx(1), "iii"),
                    asMap("jjj", 80L),
                    asMap(
                            "aaa", 10L,
                            "bbb", asMap("ccc", 20L, "ddd", 30L),
                            "eee", asList(40L, 50L),
                            "fff", asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    asMap("iii", 3L),
                                    asMap("kkk", asList(90L)))
                    )
            );
            assertPutOverwritesExistingInMap(
                    key("fff", listIdx(1), "iii", "jjj"),
                    3L,
                    key("fff", listIdx(1), "iii", "jjj"),
                    80L,
                    asMap(
                            "aaa", 10L,
                            "bbb", asMap("ccc", 20L, "ddd", 30L),
                            "eee", asList(40L, 50L),
                            "fff", asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    asMap("iii", asMap("jjj", 3L)),
                                    asMap("kkk", asList(90L)))
                    )
            );

            assertPutOverwritesExistingInMap(
                    key("fff", listIdx(2), "kkk"),
                    3L,
                    key("fff", listIdx(2), "kkk"),
                    asList(90L),
                    asMap(
                            "aaa", 10L,
                            "bbb", asMap("ccc", 20L, "ddd", 30L),
                            "eee", asList(40L, 50L),
                            "fff", asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    asMap("iii", asMap("jjj", 80L)),
                                    asMap("kkk", 3L))
                    )
            );
            assertPutOverwritesExistingInMap(
                    key("fff", listIdx(2), "kkk", listIdx(0)),
                    3L,
                    key("fff", listIdx(2), "kkk", listIdx(0)),
                    90L,
                    asMap(
                            "aaa", 10L,
                            "bbb", asMap("ccc", 20L, "ddd", 30L),
                            "eee", asList(40L, 50L),
                            "fff", asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    asMap("iii", asMap("jjj", 80L)),
                                    asMap("kkk", asList(3L)))
                    )
            );
        }

        @Disabled
        @Test
        void putOverwritesExistingInSimpleMap() {}

        @Disabled
        @Test
        void putOverwritesExistingInSimpleList() {}

        @Disabled
        @Test
        void putOverwritesExistingInList() {}

        private static void assertPutOverwritesExistingInMap(
                Key key, Object newValue, Key existingKey, Object overwrittenValue, Object newState
        ) {
            assertPutOverwritesExisting(
                    key, newValue, existingKey, overwrittenValue, newState,
                    KeyedCollectionImplTest::newKeyedCollectionMap
            );
        }

        private static void assertPutOverwritesExistingInSimpleMap(
                Key key, Object newValue, Key existingKey, Object overwrittenValue, Object newState
        ) {
            assertPutOverwritesExisting(
                    key, newValue, existingKey, overwrittenValue, newState,
                    KeyedCollectionImplTest::newKeyedCollectionSimpleMap
            );
        }

        private static void assertPutOverwritesExistingInList(
                Key key, Object newValue, Key existingKey, Object overwrittenValue, Object newState
        ) {
            assertPutOverwritesExisting(
                    key, newValue, existingKey, overwrittenValue, newState,
                    KeyedCollectionImplTest::newKeyedCollectionList
            );
        }

        private static void assertPutOverwritesExistingInSimpleList(
                Key key, Object newValue, Key existingKey, Object overwrittenValue, Object newState
        ) {
            assertPutOverwritesExisting(
                    key, newValue, existingKey, overwrittenValue, newState,
                    KeyedCollectionImplTest::newKeyedCollectionSimpleList
            );
        }

        private static void assertPutOverwritesExisting(
                Key key,
                Object newValue,
                Key existingKey,
                Object overwrittenValue,
                Object newState,
                Supplier<KeyedCollection> supplier
        ) {
            final var collection = supplier.get();
            final var existingEntry = (KeyedEntry.ExistingKeyedEntry) collection.put(key, newValue);
            assertThat(existingEntry.key(), is(existingKey));
            assertThat(existingEntry.value(), is(overwrittenValue));
            assertThat(collection, is(new KeyedCollectionImpl(newState)));
        }

        private static void assertPutInsertsMissingIntoMap(
                Key key, Object value, Key existing, Key missing, Object newState
        ) {
            assertPutInsertsMissing(
                    key, value, existing, missing, newState,
                    KeyedCollectionImplTest::newKeyedCollectionMap
            );
        }

        private static void assertPutInsertsMissingIntoList(
                Key key, Object value, Key existing, Key missing, Object newState
        ) {
            assertPutInsertsMissing(
                    key, value, existing, missing, newState,
                    KeyedCollectionImplTest::newKeyedCollectionList
            );
        }

        private static void assertPutInsertsMissing(
                Key key,
                Object value,
                Key existing,
                Key missing,
                Object newState,
                Supplier<KeyedCollection> supplier
        ) {
            final var collection = supplier.get();
            final var missingEntry = (MissingKeyedEntry) collection.put(key, value);
            assertThat(missingEntry.key(), is(key));
            assertThat(missingEntry.existing(), is(existing));
            assertThat(missingEntry.missing(), is(missing));
            assertThat(missingEntry.reason(), is(Reason.PART_MISSING));
            assertThat(collection, is(newState));
        }
    }

    public static class KeyedCollectionRemoveTest {

        @Test
        void removeExistingKeyedEntriesFromSimpleMap() {
            assertRemoveExistingFromSimpleMap(key((Object) null), null, asMap(
                    true, true,
                    1L, 2L,
                    3.0, 4.0,
                    "5", "6"
            ));
            assertRemoveExistingFromSimpleMap(key(true), true, asMap(
                    null, null,
                    1L, 2L,
                    3.0, 4.0,
                    "5", "6"
            ));
            assertRemoveExistingFromSimpleMap(key(1), 2L, asMap(
                    null, null,
                    true, true,
                    3.0, 4.0,
                    "5", "6"
            ));
            assertRemoveExistingFromSimpleMap(key(3.0), 4.0, asMap(
                    null, null,
                    true, true,
                    1L, 2L,
                    "5", "6"
            ));
            assertRemoveExistingFromSimpleMap(key("5"), "6", asMap(
                    null, null,
                    true, true,
                    1L, 2L,
                    3.0, 4.0
            ));
        }

        @Test
        void removeExistingKeyedEntriesFromSimpleList() {
            assertRemoveExistingFromSimpleList(key(listIdx(0)), null, asList(true, 1L, 2.0, "3"));
            assertRemoveExistingFromSimpleList(key(listIdx(1)), true, asList(null, 1L, 2.0, "3"));
            assertRemoveExistingFromSimpleList(key(listIdx(2)), 1L, asList(null, true, 2.0, "3"));
            assertRemoveExistingFromSimpleList(key(listIdx(3)), 2.0, asList(null, true, 1L, "3"));
            assertRemoveExistingFromSimpleList(key(listIdx(4)), "3", asList(null, true, 1L, 2.0));
        }

        @Test
        void removeExistingKeyedEntriesFromMap() {
            assertRemoveExistingFromMap(key("aaa"), 10L, asMap(
                    "bbb", asMap("ccc", 20L, "ddd", 30L),
                    "eee", asList(40L, 50L),
                    "fff", asList(
                            asMap("ggg", 60L, "hhh", 70L),
                            asMap("iii", asMap("jjj", 80L)),
                            asMap("kkk", asList(90L))
                    )
            ));

            assertRemoveExistingFromMap(
                    key("bbb"),
                    asMap("ccc", 20L, "ddd", 30L),
                    asMap(
                            "aaa", 10L,
                            "eee", asList(40L, 50L),
                            "fff", asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    asMap("iii", asMap("jjj", 80L)),
                                    asMap("kkk", asList(90L))
                            )
                    ));
            assertRemoveExistingFromMap(key("bbb", "ccc"), 20L, asMap(
                    "aaa", 10L,
                    "bbb", asMap("ddd", 30L),
                    "eee", asList(40L, 50L),
                    "fff", asList(
                            asMap("ggg", 60L, "hhh", 70L),
                            asMap("iii", asMap("jjj", 80L)),
                            asMap("kkk", asList(90L))
                    )
            ));
            assertRemoveExistingFromMap(key("bbb", "ddd"), 30L, asMap(
                    "aaa", 10L,
                    "bbb", asMap("ccc", 20L),
                    "eee", asList(40L, 50L),
                    "fff", asList(
                            asMap("ggg", 60L, "hhh", 70L),
                            asMap("iii", asMap("jjj", 80L)),
                            asMap("kkk", asList(90L))
                    )
            ));

            assertRemoveExistingFromMap(key("eee"), asList(40L, 50L), asMap(
                    "aaa", 10L,
                    "bbb", asMap("ccc", 20L, "ddd", 30L),
                    "fff", asList(
                            asMap("ggg", 60L, "hhh", 70L),
                            asMap("iii", asMap("jjj", 80L)),
                            asMap("kkk", asList(90L))
                    )
            ));
            assertRemoveExistingFromMap(key("eee", listIdx(0)), 40L, asMap(
                    "aaa", 10L,
                    "bbb", asMap("ccc", 20L, "ddd", 30L),
                    "eee", asList(50L),
                    "fff", asList(
                            asMap("ggg", 60L, "hhh", 70L),
                            asMap("iii", asMap("jjj", 80L)),
                            asMap("kkk", asList(90L))
                    )
            ));
            assertRemoveExistingFromMap(key("eee", listIdx(1)), 50L, asMap(
                    "aaa", 10L,
                    "bbb", asMap("ccc", 20L, "ddd", 30L),
                    "eee", asList(40L),
                    "fff", asList(
                            asMap("ggg", 60L, "hhh", 70L),
                            asMap("iii", asMap("jjj", 80L)),
                            asMap("kkk", asList(90L))
                    )
            ));

            assertRemoveExistingFromMap(key("fff"), asList(
                    asMap("ggg", 60L, "hhh", 70L),
                    asMap("iii", asMap("jjj", 80L)),
                    asMap("kkk", asList(90L))
            ), asMap(
                    "aaa", 10L,
                    "bbb", asMap("ccc", 20L, "ddd", 30L),
                    "eee", asList(40L, 50L)
            ));
            assertRemoveExistingFromMap(
                    key("fff", listIdx(0)),
                    asMap("ggg", 60L, "hhh", 70L),
                    asMap(
                            "aaa", 10L,
                            "bbb", asMap("ccc", 20L, "ddd", 30L),
                            "eee", asList(40L, 50L),
                            "fff", asList(
                                    asMap("iii", asMap("jjj", 80L)),
                                    asMap("kkk", asList(90L))
                            )
                    ));
            assertRemoveExistingFromMap(
                    key("fff", listIdx(1)),
                    asMap("iii", asMap("jjj", 80L)),
                    asMap(
                            "aaa", 10L,
                            "bbb", asMap("ccc", 20L, "ddd", 30L),
                            "eee", asList(40L, 50L),
                            "fff", asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    asMap("kkk", asList(90L))
                            )
                    ));
            assertRemoveExistingFromMap(
                    key("fff", listIdx(2)),
                    asMap("kkk", asList(90L)),
                    asMap(
                            "aaa", 10L,
                            "bbb", asMap("ccc", 20L, "ddd", 30L),
                            "eee", asList(40L, 50L),
                            "fff", asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    asMap("iii", asMap("jjj", 80L))
                            )
                    ));

            assertRemoveExistingFromMap(key("fff", listIdx(0), "ggg"), 60L, asMap(
                    "aaa", 10L,
                    "bbb", asMap("ccc", 20L, "ddd", 30L),
                    "eee", asList(40L, 50L),
                    "fff", asList(
                            asMap("hhh", 70L),
                            asMap("iii", asMap("jjj", 80L)),
                            asMap("kkk", asList(90L))
                    )
            ));
            assertRemoveExistingFromMap(key("fff", listIdx(0), "hhh"), 70L, asMap(
                    "aaa", 10L,
                    "bbb", asMap("ccc", 20L, "ddd", 30L),
                    "eee", asList(40L, 50L),
                    "fff", asList(
                            asMap("ggg", 60L),
                            asMap("iii", asMap("jjj", 80L)),
                            asMap("kkk", asList(90L))
                    )
            ));

            assertRemoveExistingFromMap(
                    key("fff", listIdx(1), "iii"),
                    asMap("jjj", 80L),
                    asMap(
                            "aaa", 10L,
                            "bbb", asMap("ccc", 20L, "ddd", 30L),
                            "eee", asList(40L, 50L),
                            "fff", asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    asMap(),
                                    asMap("kkk", asList(90L))
                            )
                    ));
            assertRemoveExistingFromMap(
                    key("fff", listIdx(1), "iii", "jjj"),
                    80L,
                    asMap(
                            "aaa", 10L,
                            "bbb", asMap("ccc", 20L, "ddd", 30L),
                            "eee", asList(40L, 50L),
                            "fff", asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    asMap("iii", asMap()),
                                    asMap("kkk", asList(90L))
                            )
                    ));

            assertRemoveExistingFromMap(
                    key("fff", listIdx(2), "kkk"),
                    asList(90L),
                    asMap(
                            "aaa", 10L,
                            "bbb", asMap("ccc", 20L, "ddd", 30L),
                            "eee", asList(40L, 50L),
                            "fff", asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    asMap("iii", asMap("jjj", 80L)),
                                    asMap()
                            )
                    ));
            assertRemoveExistingFromMap(
                    key("fff", listIdx(2), "kkk", listIdx(0)),
                    90L,
                    asMap(
                            "aaa", 10L,
                            "bbb", asMap("ccc", 20L, "ddd", 30L),
                            "eee", asList(40L, 50L),
                            "fff", asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    asMap("iii", asMap("jjj", 80L)),
                                    asMap("kkk", asList())
                            )
                    ));
        }

        @Test
        void removeExistingKeyedEntriesFromList() {
            assertRemoveExistingFromList(key(listIdx(0)), 10L, asList(
                    asMap("ccc", 20L, "ddd", 30L),
                    asList(40L, 50L),
                    asList(
                            asMap("ggg", 60L, "hhh", 70L),
                            asMap("iii", asMap("jjj", 80L)),
                            asMap("kkk", asList(90L))
                    )
            ));

            assertRemoveExistingFromList(
                    key(listIdx(1)),
                    asMap("ccc", 20L, "ddd", 30L),
                    asList(
                            10L,
                            asList(40L, 50L),
                            asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    asMap("iii", asMap("jjj", 80L)),
                                    asMap("kkk", asList(90L))
                            )
                    ));
            assertRemoveExistingFromList(key(listIdx(1), "ccc"), 20L, asList(
                    10L,
                    asMap("ddd", 30L),
                    asList(40L, 50L),
                    asList(
                            asMap("ggg", 60L, "hhh", 70L),
                            asMap("iii", asMap("jjj", 80L)),
                            asMap("kkk", asList(90L))
                    )
            ));
            assertRemoveExistingFromList(key(listIdx(1), "ddd"), 30L, asList(
                    10L,
                    asMap("ccc", 20L),
                    asList(40L, 50L),
                    asList(
                            asMap("ggg", 60L, "hhh", 70L),
                            asMap("iii", asMap("jjj", 80L)),
                            asMap("kkk", asList(90L))
                    )
            ));

            assertRemoveExistingFromList(key(listIdx(2)), asList(40L, 50L), asList(
                    10L,
                    asMap("ccc", 20L, "ddd", 30L),
                    asList(
                            asMap("ggg", 60L, "hhh", 70L),
                            asMap("iii", asMap("jjj", 80L)),
                            asMap("kkk", asList(90L))
                    )
            ));
            assertRemoveExistingFromList(key(listIdx(2), listIdx(0)), 40L, asList(
                    10L,
                    asMap("ccc", 20L, "ddd", 30L),
                    asList(50L),
                    asList(
                            asMap("ggg", 60L, "hhh", 70L),
                            asMap("iii", asMap("jjj", 80L)),
                            asMap("kkk", asList(90L))
                    )
            ));
            assertRemoveExistingFromList(key(listIdx(2), listIdx(1)), 50L, asList(
                    10L,
                    asMap("ccc", 20L, "ddd", 30L),
                    asList(40L),
                    asList(
                            asMap("ggg", 60L, "hhh", 70L),
                            asMap("iii", asMap("jjj", 80L)),
                            asMap("kkk", asList(90L))
                    )
            ));

            assertRemoveExistingFromList(key(listIdx(3)), asList(
                    asMap("ggg", 60L, "hhh", 70L),
                    asMap("iii", asMap("jjj", 80L)),
                    asMap("kkk", asList(90L))
            ), asList(
                    10L,
                    asMap("ccc", 20L, "ddd", 30L),
                    asList(40L, 50L)
            ));
            assertRemoveExistingFromList(
                    key(listIdx(3), listIdx(0)),
                    asMap("ggg", 60L, "hhh", 70L),
                    asList(
                            10L,
                            asMap("ccc", 20L, "ddd", 30L),
                            asList(40L, 50L),
                            asList(
                                    asMap("iii", asMap("jjj", 80L)),
                                    asMap("kkk", asList(90L))
                            )
                    ));
            assertRemoveExistingFromList(
                    key(listIdx(3), listIdx(1)),
                    asMap("iii", asMap("jjj", 80L)),
                    asList(
                            10L,
                            asMap("ccc", 20L, "ddd", 30L),
                            asList(40L, 50L),
                            asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    asMap("kkk", asList(90L))
                            )
                    ));
            assertRemoveExistingFromList(
                    key(listIdx(3), listIdx(2)),
                    asMap("kkk", asList(90L)),
                    asList(
                            10L,
                            asMap("ccc", 20L, "ddd", 30L),
                            asList(40L, 50L),
                            asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    asMap("iii", asMap("jjj", 80L))
                            )
                    ));

            assertRemoveExistingFromList(
                    key(listIdx(3), listIdx(0), "ggg"),
                    60L,
                    asList(
                            10L,
                            asMap("ccc", 20L, "ddd", 30L),
                            asList(40L, 50L),
                            asList(
                                    asMap("hhh", 70L),
                                    asMap("iii", asMap("jjj", 80L)),
                                    asMap("kkk", asList(90L))
                            )
                    ));
            assertRemoveExistingFromList(
                    key(listIdx(3), listIdx(0), "hhh"),
                    70L,
                    asList(
                            10L,
                            asMap("ccc", 20L, "ddd", 30L),
                            asList(40L, 50L),
                            asList(
                                    asMap("ggg", 60L),
                                    asMap("iii", asMap("jjj", 80L)),
                                    asMap("kkk", asList(90L))
                            )
                    ));

            assertRemoveExistingFromList(
                    key(listIdx(3), listIdx(1), "iii"),
                    asMap("jjj", 80L),
                    asList(
                            10L,
                            asMap("ccc", 20L, "ddd", 30L),
                            asList(40L, 50L),
                            asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    asMap(),
                                    asMap("kkk", asList(90L))
                            )
                    ));
            assertRemoveExistingFromList(
                    key(listIdx(3), listIdx(1), "iii", "jjj"),
                    80L,
                    asList(
                            10L,
                            asMap("ccc", 20L, "ddd", 30L),
                            asList(40L, 50L),
                            asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    asMap("iii", asMap()),
                                    asMap("kkk", asList(90L))
                            )
                    ));

            assertRemoveExistingFromList(
                    key(listIdx(3), listIdx(2), "kkk"),
                    asList(90L),
                    asList(
                            10L,
                            asMap("ccc", 20L, "ddd", 30L),
                            asList(40L, 50L),
                            asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    asMap("iii", asMap("jjj", 80L)),
                                    asMap()
                            )
                    ));
            assertRemoveExistingFromList(
                    key(listIdx(3), listIdx(2), "kkk", listIdx(0)),
                    90L,
                    asList(
                            10L,
                            asMap("ccc", 20L, "ddd", 30L),
                            asList(40L, 50L),
                            asList(
                                    asMap("ggg", 60L, "hhh", 70L),
                                    asMap("iii", asMap("jjj", 80L)),
                                    asMap("kkk", asList())
                            )
                    ));
        }

        @Test
        void removeMissingKeyedEntriesFromMapBecauseValueIsSimple() {
            assertRemoveMissingFromMap(
                    key("aaa", "mmm"),
                    key("aaa"),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromMap(
                    key("bbb", "ccc", "mmm", "nnn"),
                    key("bbb", "ccc"),
                    key("mmm", "nnn"),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromMap(
                    key("bbb", "ddd", "mmm", "ooo"),
                    key("bbb", "ddd"),
                    key("mmm", "ooo"),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromMap(
                    key("eee", listIdx(0), "mmm", 2.0),
                    key("eee", listIdx(0)),
                    key("mmm", 2.0),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromMap(
                    key("eee", listIdx(1), "mmm"),
                    key("eee", listIdx(1)),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromMap(
                    key("fff", listIdx(0), "ggg", "mmm"),
                    key("fff", listIdx(0), "ggg"),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromMap(
                    key("fff", listIdx(0), "hhh", "mmm"),
                    key("fff", listIdx(0), "hhh"),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromMap(
                    key("fff", listIdx(1), "iii", "jjj", "mmm"),
                    key("fff", listIdx(1), "iii", "jjj"),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromMap(
                    key("fff", listIdx(2), "kkk", listIdx(0), "mmm"),
                    key("fff", listIdx(2), "kkk", listIdx(0)),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );

            assertRemoveMissingFromMap(
                    key("aaa", listIdx(10)),
                    key("aaa"),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromMap(
                    key("bbb", "ccc", listIdx(10), listIdx(11)),
                    key("bbb", "ccc"),
                    key(listIdx(10), listIdx(11)),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromMap(
                    key("bbb", "ddd", listIdx(10), "ooo"),
                    key("bbb", "ddd"),
                    key(listIdx(10), "ooo"),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromMap(
                    key("eee", listIdx(0), listIdx(10), 2.0),
                    key("eee", listIdx(0)),
                    key(listIdx(10), 2.0),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromMap(
                    key("eee", listIdx(1), listIdx(10)),
                    key("eee", listIdx(1)),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromMap(
                    key("fff", listIdx(0), "ggg", listIdx(10)),
                    key("fff", listIdx(0), "ggg"),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromMap(
                    key("fff", listIdx(0), "hhh", listIdx(10)),
                    key("fff", listIdx(0), "hhh"),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromMap(
                    key("fff", listIdx(1), "iii", "jjj", listIdx(10)),
                    key("fff", listIdx(1), "iii", "jjj"),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromMap(
                    key("fff", listIdx(2), "kkk", listIdx(0), listIdx(10)),
                    key("fff", listIdx(2), "kkk", listIdx(0)),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
        }

        @Test
        void removeMissingKeyedEntriesFromListBecauseValueIsSimple() {
            assertRemoveMissingFromList(
                    key(listIdx(0), "mmm"),
                    key(listIdx(0)),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromList(
                    key(listIdx(1), "ccc", "mmm"),
                    key(listIdx(1), "ccc"),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromList(
                    key(listIdx(1), "ddd", "mmm"),
                    key(listIdx(1), "ddd"),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromList(
                    key(listIdx(2), listIdx(0), "mmm"),
                    key(listIdx(2), listIdx(0)),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromList(
                    key(listIdx(2), listIdx(1), "mmm"),
                    key(listIdx(2), listIdx(1)),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromList(
                    key(listIdx(3), listIdx(0), "ggg", "mmm"),
                    key(listIdx(3), listIdx(0), "ggg"),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromList(
                    key(listIdx(3), listIdx(0), "hhh", "mmm"),
                    key(listIdx(3), listIdx(0), "hhh"),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromList(
                    key(listIdx(3), listIdx(1), "iii", "jjj", "mmm"),
                    key(listIdx(3), listIdx(1), "iii", "jjj"),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromList(
                    key(listIdx(3), listIdx(2), "kkk", listIdx(0), "mmm"),
                    key(listIdx(3), listIdx(2), "kkk", listIdx(0)),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );

            assertRemoveMissingFromList(
                    key(listIdx(0), listIdx(10)),
                    key(listIdx(0)),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromList(
                    key(listIdx(1), "ccc", listIdx(10)),
                    key(listIdx(1), "ccc"),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromList(
                    key(listIdx(1), "ddd", listIdx(10)),
                    key(listIdx(1), "ddd"),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromList(
                    key(listIdx(2), listIdx(0), listIdx(10)),
                    key(listIdx(2), listIdx(0)),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromList(
                    key(listIdx(2), listIdx(1), listIdx(10)),
                    key(listIdx(2), listIdx(1)),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromList(
                    key(listIdx(3), listIdx(0), "ggg", listIdx(10)),
                    key(listIdx(3), listIdx(0), "ggg"),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromList(
                    key(listIdx(3), listIdx(0), "hhh", listIdx(10)),
                    key(listIdx(3), listIdx(0), "hhh"),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromList(
                    key(listIdx(3), listIdx(1), "iii", "jjj", listIdx(10)),
                    key(listIdx(3), listIdx(1), "iii", "jjj"),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromList(
                    key(listIdx(3), listIdx(2), "kkk", listIdx(0), listIdx(10)),
                    key(listIdx(3), listIdx(2), "kkk", listIdx(0)),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
        }

        @Test
        void removeMissingKeyedEntriesFromMapBecauseValueIsListAndNotMap() {
            assertRemoveMissingFromMap(
                    key("eee", "mmm"),
                    key("eee"),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromMap(
                    key("fff", 2.0, 1L),
                    key("fff"),
                    key(2.0, 1L),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromMap(
                    key("fff", listIdx(2), "kkk", false, "ooo"),
                    key("fff", listIdx(2), "kkk"),
                    key(false, "ooo"),
                    Reason.WRONG_TYPE
            );
        }

        @Test
        void removeMissingKeyedEntriesFromMapBecauseValueIsMapAndNotList() {
            assertRemoveMissingFromMap(
                    key("bbb", listIdx(0)),
                    key("bbb"),
                    key(listIdx(0)),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromMap(
                    key("fff", listIdx(0), listIdx(1), true),
                    key("fff", listIdx(0)),
                    key(listIdx(1), true),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromMap(
                    key("fff", listIdx(1), listIdx(2), "ooo"),
                    key("fff", listIdx(1)),
                    key(listIdx(2), "ooo"),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromMap(
                    key("fff", listIdx(2), listIdx(3), 4.0),
                    key("fff", listIdx(2)),
                    key(listIdx(3), 4.0),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromMap(
                    key("fff", listIdx(1), "iii", listIdx(4), 5L, 6L),
                    key("fff", listIdx(1), "iii"),
                    key(listIdx(4), 5L, 6L),
                    Reason.WRONG_TYPE
            );
        }

        @Test
        void removeMissingKeyedEntriesFromListBecauseValueIsListAndNotMap() {
            assertRemoveMissingFromList(
                    key(listIdx(2), "mmm"),
                    key(listIdx(2)),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromList(
                    key(listIdx(3), 2.0, 1L),
                    key(listIdx(3)),
                    key(2.0, 1L),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromList(
                    key(listIdx(3), listIdx(2), "kkk", false, "ooo"),
                    key(listIdx(3), listIdx(2), "kkk"),
                    key(false, "ooo"),
                    Reason.WRONG_TYPE
            );
        }

        @Test
        void removeMissingKeyedEntriesFromListBecauseValueIsMapAndNotList() {
            assertRemoveMissingFromList(
                    key(listIdx(1), listIdx(0)),
                    key(listIdx(1)),
                    key(listIdx(0)),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromList(
                    key(listIdx(2), listIdx(0), listIdx(1), true),
                    key(listIdx(2), listIdx(0)),
                    key(listIdx(1), true),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromList(
                    key(listIdx(2), listIdx(1), listIdx(2), "ooo"),
                    key(listIdx(2), listIdx(1)),
                    key(listIdx(2), "ooo"),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromList(
                    key(listIdx(2), listIdx(1), listIdx(3), 4.0),
                    key(listIdx(2), listIdx(1)),
                    key(listIdx(3), 4.0),
                    Reason.WRONG_TYPE
            );
            assertRemoveMissingFromList(
                    key(listIdx(3), listIdx(1), "iii", listIdx(4), 5L, 6L),
                    key(listIdx(3), listIdx(1), "iii"),
                    key(listIdx(4), 5L, 6L),
                    Reason.WRONG_TYPE
            );
        }

        @Test
        void removeMissingKeyedEntriesFromMapBecauseMapIsMissingKey() {
            assertRemoveMissingFromMap(
                    key("bbb", "mmm"),
                    key("bbb"),
                    key("mmm"),
                    Reason.PART_MISSING
            );
            assertRemoveMissingFromMap(
                    key("fff", listIdx(0), 1L),
                    key("fff", listIdx(0)),
                    key(1L),
                    Reason.PART_MISSING
            );
            assertRemoveMissingFromMap(
                    key("fff", listIdx(1), 2.0),
                    key("fff", listIdx(1)),
                    key(2.0),
                    Reason.PART_MISSING
            );
            assertRemoveMissingFromMap(
                    key("fff", listIdx(2), false, "mmm"),
                    key("fff", listIdx(2)),
                    key(false, "mmm"),
                    Reason.PART_MISSING
            );
            assertRemoveMissingFromMap(
                    key("fff", listIdx(1), "iii", null, "mmm"),
                    key("fff", listIdx(1), "iii"),
                    key(null, "mmm"),
                    Reason.PART_MISSING
            );
            assertRemoveMissingFromMap(key("ggg"), null, key("ggg"), Reason.PART_MISSING);
        }

        @Test
        void removeMissingKeyedEntriesFromMapBecauseTopLevelEntryHasWrongType() {
            assertRemoveMissingFromMap(key(listIdx(0)), null, key(listIdx(0)), Reason.WRONG_TYPE);
            assertRemoveMissingFromList(key("ggg"), null, key("ggg"), Reason.WRONG_TYPE);
        }

        @Test
        void removeMissingKeyedEntriesFromMapBecauseListIsMissingIndex() {
            assertRemoveMissingFromMap(
                    key("eee", listIdx(2)),
                    key("eee"),
                    key(listIdx(2)),
                    Reason.PART_MISSING
            );
            assertRemoveMissingFromMap(
                    key("fff", listIdx(10)),
                    key("fff"),
                    key(listIdx(10)),
                    Reason.PART_MISSING
            );
            assertRemoveMissingFromMap(
                    key("fff", listIdx(2), "kkk", listIdx(Integer.MAX_VALUE), "mmm"),
                    key("fff", listIdx(2), "kkk"),
                    key(listIdx(Integer.MAX_VALUE), "mmm"),
                    Reason.PART_MISSING
            );
            assertRemoveMissingFromList(key(listIdx(100)), null, key(listIdx(100)), Reason.PART_MISSING);
        }


        private static void assertRemoveExistingFromMap(Key key, Object removedValue, Object newState) {
            assertRemoveExistingFrom(key, removedValue, newState, KeyedCollectionImplTest::newKeyedCollectionMap);
        }

        private static void assertRemoveExistingFromSimpleMap(Key key, Object removedValue, Object newState) {
            assertRemoveExistingFrom(key, removedValue, newState, KeyedCollectionImplTest::newKeyedCollectionSimpleMap);
        }

        private static void assertRemoveExistingFromList(Key key, Object removedValue, Object newState) {
            assertRemoveExistingFrom(key, removedValue, newState, KeyedCollectionImplTest::newKeyedCollectionList);
        }

        private static void assertRemoveExistingFromSimpleList(Key key, Object removedValue, Object newState) {
            assertRemoveExistingFrom(key, removedValue, newState, KeyedCollectionImplTest::newKeyedCollectionSimpleList);
        }

        private static void assertRemoveExistingFrom(
                Key key,
                Object removedValue,
                Object newState,
                Supplier<KeyedCollection> supplier
        ) {
            final var collection = supplier.get();
            final var existingEntry = (KeyedEntry.ExistingKeyedEntry) collection.remove(key);
            assertThat(existingEntry.key(), is(key));
            assertThat(existingEntry.value(), is(removedValue));
            assertThat(collection, is(new KeyedCollectionImpl(newState)));
        }

        private static void assertRemoveMissingFromMap(Key key, Key existing, Key missing, Reason reason) {
            assertRemoveMissingFrom(key, existing, missing, reason, KeyedCollectionImplTest::newKeyedCollectionMap);
        }

        private static void assertRemoveMissingFromList(Key key, Key existing, Key missing, Reason reason) {
            assertRemoveMissingFrom(key, existing, missing, reason, KeyedCollectionImplTest::newKeyedCollectionList);
        }

        private static void assertRemoveMissingFrom(
                Key key,
                Key existing,
                Key missing,
                Reason reason,
                Supplier<KeyedCollection> supplier
        ) {
            final var collection = supplier.get();
            final var missingEntry = (MissingKeyedEntry) collection.remove(key);
            assertThat(missingEntry.key(), is(key));
            assertThat(missingEntry.existing(), is(existing));
            assertThat(missingEntry.missing(), is(missing));
            assertThat(missingEntry.reason(), is(reason));
            // collection equals initial collection state, i.e. nothing was removed
            assertThat(collection, is(supplier.get()));
        }
    }

    public static class KeyedCollectionGetTest {

        @Test
        void getExistingKeyedEntriesFromSimpleMap() {
            assertGetExistingFromSimpleMap(key((Object) null), null);
            assertGetExistingFromSimpleMap(key(true), true);
            assertGetExistingFromSimpleMap(key(1), 2L);
            assertGetExistingFromSimpleMap(key(3.0), 4.0);
            assertGetExistingFromSimpleMap(key("5"), "6");
        }

        @Test
        void getExistingKeyedEntriesFromSimpleList() {
            assertGetExistingFromSimpleList(key(listIdx(0)), null);
            assertGetExistingFromSimpleList(key(listIdx(1)), true);
            assertGetExistingFromSimpleList(key(listIdx(2)), 1L);
            assertGetExistingFromSimpleList(key(listIdx(3)), 2.0);
            assertGetExistingFromSimpleList(key(listIdx(4)), "3");
        }

        @Test
        void getExistingKeyedEntriesFromMap() {
            assertGetExistingFromMap(key("aaa"), 10L);

            assertGetExistingFromMap(key("bbb"), asMap("ccc", 20L, "ddd", 30L));
            assertGetExistingFromMap(key("bbb", "ccc"), 20L);
            assertGetExistingFromMap(key("bbb", "ddd"), 30L);

            assertGetExistingFromMap(key("eee"), asList(40L, 50L));
            assertGetExistingFromMap(key("eee", listIdx(0)), 40L);
            assertGetExistingFromMap(key("eee", listIdx(1)), 50L);

            assertGetExistingFromMap(key("fff"), asList(
                    asMap("ggg", 60L, "hhh", 70L),
                    asMap("iii", asMap("jjj", 80L)),
                    asMap("kkk", asList(90L))
            ));
            assertGetExistingFromMap(key("fff", listIdx(0)), asMap("ggg", 60L, "hhh", 70L));
            assertGetExistingFromMap(key("fff", listIdx(1)), asMap("iii", asMap("jjj", 80L)));
            assertGetExistingFromMap(key("fff", listIdx(2)), asMap("kkk", asList(90L)));

            assertGetExistingFromMap(key("fff", listIdx(0), "ggg"), 60L);
            assertGetExistingFromMap(key("fff", listIdx(0), "hhh"), 70L);

            assertGetExistingFromMap(key("fff", listIdx(1), "iii"), asMap("jjj", 80L));
            assertGetExistingFromMap(key("fff", listIdx(1), "iii", "jjj"), 80L);

            assertGetExistingFromMap(key("fff", listIdx(2), "kkk"), asList(90L));
            assertGetExistingFromMap(key("fff", listIdx(2), "kkk", listIdx(0)), 90L);
        }

        @Test
        void getExistingKeyedEntriesFromList() {
            assertGetExistingFromList(key(listIdx(0)), 10L);

            assertGetExistingFromList(key(listIdx(1)), asMap("ccc", 20L, "ddd", 30L));
            assertGetExistingFromList(key(listIdx(1), "ccc"), 20L);
            assertGetExistingFromList(key(listIdx(1), "ddd"), 30L);

            assertGetExistingFromList(key(listIdx(2)), asList(40L, 50L));
            assertGetExistingFromList(key(listIdx(2), listIdx(0)), 40L);
            assertGetExistingFromList(key(listIdx(2), listIdx(1)), 50L);

            assertGetExistingFromList(key(listIdx(3)), asList(
                    asMap("ggg", 60L, "hhh", 70L),
                    asMap("iii", asMap("jjj", 80L)),
                    asMap("kkk", asList(90L))
            ));
            assertGetExistingFromList(key(listIdx(3), listIdx(0)), asMap("ggg", 60L, "hhh", 70L));
            assertGetExistingFromList(key(listIdx(3), listIdx(1)), asMap("iii", asMap("jjj", 80L)));
            assertGetExistingFromList(key(listIdx(3), listIdx(2)), asMap("kkk", asList(90L)));

            assertGetExistingFromList(key(listIdx(3), listIdx(0), "ggg"), 60L);
            assertGetExistingFromList(key(listIdx(3), listIdx(0), "hhh"), 70L);

            assertGetExistingFromList(key(listIdx(3), listIdx(1), "iii"), asMap("jjj", 80L));
            assertGetExistingFromList(key(listIdx(3), listIdx(1), "iii", "jjj"), 80L);

            assertGetExistingFromList(key(listIdx(3), listIdx(2), "kkk"), asList(90L));
            assertGetExistingFromList(key(listIdx(3), listIdx(2), "kkk", listIdx(0)), 90L);
        }

        @Test
        void getMissingKeyedEntriesFromMapBecauseValueIsSimple() {
            assertGetMissingFromMap(
                    key("aaa", "mmm"),
                    key("aaa"),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromMap(
                    key("bbb", "ccc", "mmm", "nnn"),
                    key("bbb", "ccc"),
                    key("mmm", "nnn"),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromMap(
                    key("bbb", "ddd", "mmm", "ooo"),
                    key("bbb", "ddd"),
                    key("mmm", "ooo"),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromMap(
                    key("eee", listIdx(0), "mmm", 2.0),
                    key("eee", listIdx(0)),
                    key("mmm", 2.0),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromMap(
                    key("eee", listIdx(1), "mmm"),
                    key("eee", listIdx(1)),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromMap(
                    key("fff", listIdx(0), "ggg", "mmm"),
                    key("fff", listIdx(0), "ggg"),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromMap(
                    key("fff", listIdx(0), "hhh", "mmm"),
                    key("fff", listIdx(0), "hhh"),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromMap(
                    key("fff", listIdx(1), "iii", "jjj", "mmm"),
                    key("fff", listIdx(1), "iii", "jjj"),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromMap(
                    key("fff", listIdx(2), "kkk", listIdx(0), "mmm"),
                    key("fff", listIdx(2), "kkk", listIdx(0)),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );

            assertGetMissingFromMap(
                    key("aaa", listIdx(10)),
                    key("aaa"),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromMap(
                    key("bbb", "ccc", listIdx(10), listIdx(11)),
                    key("bbb", "ccc"),
                    key(listIdx(10), listIdx(11)),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromMap(
                    key("bbb", "ddd", listIdx(10), "ooo"),
                    key("bbb", "ddd"),
                    key(listIdx(10), "ooo"),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromMap(
                    key("eee", listIdx(0), listIdx(10), 2.0),
                    key("eee", listIdx(0)),
                    key(listIdx(10), 2.0),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromMap(
                    key("eee", listIdx(1), listIdx(10)),
                    key("eee", listIdx(1)),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromMap(
                    key("fff", listIdx(0), "ggg", listIdx(10)),
                    key("fff", listIdx(0), "ggg"),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromMap(
                    key("fff", listIdx(0), "hhh", listIdx(10)),
                    key("fff", listIdx(0), "hhh"),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromMap(
                    key("fff", listIdx(1), "iii", "jjj", listIdx(10)),
                    key("fff", listIdx(1), "iii", "jjj"),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromMap(
                    key("fff", listIdx(2), "kkk", listIdx(0), listIdx(10)),
                    key("fff", listIdx(2), "kkk", listIdx(0)),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
        }

        @Test
        void getMissingKeyedEntriesFromListBecauseValueIsSimple() {
            assertGetMissingFromList(
                    key(listIdx(0), "mmm"),
                    key(listIdx(0)),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromList(
                    key(listIdx(1), "ccc", "mmm"),
                    key(listIdx(1), "ccc"),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromList(
                    key(listIdx(1), "ddd", "mmm"),
                    key(listIdx(1), "ddd"),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromList(
                    key(listIdx(2), listIdx(0), "mmm"),
                    key(listIdx(2), listIdx(0)),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromList(
                    key(listIdx(2), listIdx(1), "mmm"),
                    key(listIdx(2), listIdx(1)),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromList(
                    key(listIdx(3), listIdx(0), "ggg", "mmm"),
                    key(listIdx(3), listIdx(0), "ggg"),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromList(
                    key(listIdx(3), listIdx(0), "hhh", "mmm"),
                    key(listIdx(3), listIdx(0), "hhh"),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromList(
                    key(listIdx(3), listIdx(1), "iii", "jjj", "mmm"),
                    key(listIdx(3), listIdx(1), "iii", "jjj"),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromList(
                    key(listIdx(3), listIdx(2), "kkk", listIdx(0), "mmm"),
                    key(listIdx(3), listIdx(2), "kkk", listIdx(0)),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );

            assertGetMissingFromList(
                    key(listIdx(0), listIdx(10)),
                    key(listIdx(0)),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromList(
                    key(listIdx(1), "ccc", listIdx(10)),
                    key(listIdx(1), "ccc"),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromList(
                    key(listIdx(1), "ddd", listIdx(10)),
                    key(listIdx(1), "ddd"),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromList(
                    key(listIdx(2), listIdx(0), listIdx(10)),
                    key(listIdx(2), listIdx(0)),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromList(
                    key(listIdx(2), listIdx(1), listIdx(10)),
                    key(listIdx(2), listIdx(1)),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromList(
                    key(listIdx(3), listIdx(0), "ggg", listIdx(10)),
                    key(listIdx(3), listIdx(0), "ggg"),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromList(
                    key(listIdx(3), listIdx(0), "hhh", listIdx(10)),
                    key(listIdx(3), listIdx(0), "hhh"),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromList(
                    key(listIdx(3), listIdx(1), "iii", "jjj", listIdx(10)),
                    key(listIdx(3), listIdx(1), "iii", "jjj"),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromList(
                    key(listIdx(3), listIdx(2), "kkk", listIdx(0), listIdx(10)),
                    key(listIdx(3), listIdx(2), "kkk", listIdx(0)),
                    key(listIdx(10)),
                    Reason.WRONG_TYPE
            );
        }

        @Test
        void getMissingKeyedEntriesFromMapBecauseValueIsListAndNotMap() {
            assertGetMissingFromMap(
                    key("eee", "mmm"),
                    key("eee"),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromMap(
                    key("fff", 2.0, 1L),
                    key("fff"),
                    key(2.0, 1L),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromMap(
                    key("fff", listIdx(2), "kkk", false, "ooo"),
                    key("fff", listIdx(2), "kkk"),
                    key(false, "ooo"),
                    Reason.WRONG_TYPE
            );
        }

        @Test
        void getMissingKeyedEntriesFromMapBecauseValueIsMapAndNotList() {
            assertGetMissingFromMap(
                    key("bbb", listIdx(0)),
                    key("bbb"),
                    key(listIdx(0)),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromMap(
                    key("fff", listIdx(0), listIdx(1), true),
                    key("fff", listIdx(0)),
                    key(listIdx(1), true),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromMap(
                    key("fff", listIdx(1), listIdx(2), "ooo"),
                    key("fff", listIdx(1)),
                    key(listIdx(2), "ooo"),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromMap(
                    key("fff", listIdx(2), listIdx(3), 4.0),
                    key("fff", listIdx(2)),
                    key(listIdx(3), 4.0),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromMap(
                    key("fff", listIdx(1), "iii", listIdx(4), 5L, 6L),
                    key("fff", listIdx(1), "iii"),
                    key(listIdx(4), 5L, 6L),
                    Reason.WRONG_TYPE
            );
        }

        @Test
        void getMissingKeyedEntriesFromListBecauseValueIsListAndNotMap() {
            assertGetMissingFromList(
                    key(listIdx(2), "mmm"),
                    key(listIdx(2)),
                    key("mmm"),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromList(
                    key(listIdx(3), 2.0, 1L),
                    key(listIdx(3)),
                    key(2.0, 1L),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromList(
                    key(listIdx(3), listIdx(2), "kkk", false, "ooo"),
                    key(listIdx(3), listIdx(2), "kkk"),
                    key(false, "ooo"),
                    Reason.WRONG_TYPE
            );
        }

        @Test
        void getMissingKeyedEntriesFromListBecauseValueIsMapAndNotList() {
            assertGetMissingFromList(
                    key(listIdx(1), listIdx(0)),
                    key(listIdx(1)),
                    key(listIdx(0)),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromList(
                    key(listIdx(2), listIdx(0), listIdx(1), true),
                    key(listIdx(2), listIdx(0)),
                    key(listIdx(1), true),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromList(
                    key(listIdx(2), listIdx(1), listIdx(2), "ooo"),
                    key(listIdx(2), listIdx(1)),
                    key(listIdx(2), "ooo"),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromList(
                    key(listIdx(2), listIdx(1), listIdx(3), 4.0),
                    key(listIdx(2), listIdx(1)),
                    key(listIdx(3), 4.0),
                    Reason.WRONG_TYPE
            );
            assertGetMissingFromList(
                    key(listIdx(3), listIdx(1), "iii", listIdx(4), 5L, 6L),
                    key(listIdx(3), listIdx(1), "iii"),
                    key(listIdx(4), 5L, 6L),
                    Reason.WRONG_TYPE
            );
        }

        @Test
        void getMissingKeyedEntriesFromMapBecauseMapIsMissingKey() {
            assertGetMissingFromMap(
                    key("bbb", "mmm"),
                    key("bbb"),
                    key("mmm"),
                    Reason.PART_MISSING
            );
            assertGetMissingFromMap(
                    key("fff", listIdx(0), 1L),
                    key("fff", listIdx(0)),
                    key(1L),
                    Reason.PART_MISSING
            );
            assertGetMissingFromMap(
                    key("fff", listIdx(1), 2.0),
                    key("fff", listIdx(1)),
                    key(2.0),
                    Reason.PART_MISSING
            );
            assertGetMissingFromMap(
                    key("fff", listIdx(2), false, "mmm"),
                    key("fff", listIdx(2)),
                    key(false, "mmm"),
                    Reason.PART_MISSING
            );
            assertGetMissingFromMap(
                    key("fff", listIdx(1), "iii", null, "mmm"),
                    key("fff", listIdx(1), "iii"),
                    key(null, "mmm"),
                    Reason.PART_MISSING
            );
            assertGetMissingFromMap(key("ggg"), null, key("ggg"), Reason.PART_MISSING);
        }

        @Test
        void getMissingKeyedEntriesFromMapBecauseTopLevelEntryHasWrongType() {
            assertGetMissingFromMap(key(listIdx(0)), null, key(listIdx(0)), Reason.WRONG_TYPE);
            assertGetMissingFromList(key("ggg"), null, key("ggg"), Reason.WRONG_TYPE);
        }

        @Test
        void getMissingKeyedEntriesFromMapBecauseListIsMissingIndex() {
            assertGetMissingFromMap(
                    key("eee", listIdx(2)),
                    key("eee"),
                    key(listIdx(2)),
                    Reason.PART_MISSING
            );
            assertGetMissingFromMap(
                    key("fff", listIdx(10)),
                    key("fff"),
                    key(listIdx(10)),
                    Reason.PART_MISSING
            );
            assertGetMissingFromMap(
                    key("fff", listIdx(2), "kkk", listIdx(Integer.MAX_VALUE), "mmm"),
                    key("fff", listIdx(2), "kkk"),
                    key(listIdx(Integer.MAX_VALUE), "mmm"),
                    Reason.PART_MISSING
            );
            assertGetMissingFromList(key(listIdx(100)), null, key(listIdx(100)), Reason.PART_MISSING);
        }

        private static void assertGetExistingFromMap(Key key, Object expectedValue) {
            assertGetExistingFrom(key, expectedValue, KeyedCollectionImplTest::newKeyedCollectionMap);
        }

        private static void assertGetExistingFromSimpleMap(Key key, Object expectedValue) {
            assertGetExistingFrom(key, expectedValue, KeyedCollectionImplTest::newKeyedCollectionSimpleMap);
        }

        private static void assertGetExistingFromList(Key key, Object expectedValue) {
            assertGetExistingFrom(key, expectedValue, KeyedCollectionImplTest::newKeyedCollectionList);
        }

        private static void assertGetExistingFromSimpleList(Key key, Object expectedValue) {
            assertGetExistingFrom(key, expectedValue, KeyedCollectionImplTest::newKeyedCollectionSimpleList);
        }

        private static void assertGetMissingFromMap(Key key, Key existing, Key missing, Reason reason) {
            assertGetMissingFrom(key, existing, missing, reason, KeyedCollectionImplTest::newKeyedCollectionMap);
        }

        private static void assertGetMissingFromList(Key key, Key existing, Key missing, Reason reason) {
            assertGetMissingFrom(key, existing, missing, reason, KeyedCollectionImplTest::newKeyedCollectionList);
        }

        private static void assertGetExistingFrom(Key key, Object expectedValue, Supplier<KeyedCollection> supplier) {
            final var collection = supplier.get();
            final var existingEntry = (KeyedEntry.ExistingKeyedEntry) collection.get(key);
            assertThat(existingEntry.key(), is(key));
            assertThat(existingEntry.value(), is(expectedValue));
        }

        private static void assertGetMissingFrom(
                Key key,
                Key existing,
                Key missing,
                Reason reason,
                Supplier<KeyedCollection> supplier
        ) {
            final var collection = supplier.get();
            final var missingEntry = (MissingKeyedEntry) collection.get(key);
            assertThat(missingEntry.key(), is(key));
            assertThat(missingEntry.existing(), is(existing));
            assertThat(missingEntry.missing(), is(missing));
            assertThat(missingEntry.reason(), is(reason));
        }
    }

    private static KeyedCollection newKeyedCollectionMap() {
        /*
         *  aaa: 10
         *  bbb:
         *    ccc: 20
         *    ddd: 30
         *  eee:
         *    - 40
         *    - 50
         *  fff:
         *    - ggg: 60
         *      hhh: 70
         *    - iii:
         *        jjj: 80
         *    - kkk:
         *        - 90
         */
        final Map<Object, Object> map = asMap(
                "aaa", 10L,
                "bbb", asMap(
                        "ccc", 20L,
                        "ddd", 30L
                ),
                "eee", asList(40L, 50L),
                "fff", asList(
                        asMap(
                                "ggg", 60L,
                                "hhh", 70L
                        ),
                        asMap(
                                "iii", asMap(
                                        "jjj", 80L
                                )
                        ),
                        asMap("kkk", asList(90L))
                )
        );
        return new KeyedCollectionImpl(map);
    }

    private static KeyedCollection newKeyedCollectionSimpleMap() {
        final Map<Object, Object> map = asMap(
                null, null,
                true, true,
                1L, 2L,
                3.0, 4.0,
                "5", "6"
        );
        return new KeyedCollectionImpl(map);
    }

    private static KeyedCollection newKeyedCollectionList() {
        /*
         * - 10
         * - ccc: 20
         *   ddd: 30
         * - - 40
         *   - 50
         * - - ggg: 60
         *     hhh: 70
         *   - iii:
         *       jjj: 80
         *   - kkk:
         *       - 90
         */
        final List<Object> list = asList(
                10L,
                asMap(
                        "ccc", 20L,
                        "ddd", 30L
                ),
                asList(40L, 50L),
                asList(
                        asMap(
                                "ggg", 60L,
                                "hhh", 70L
                        ),
                        asMap(
                                "iii", asMap(
                                        "jjj", 80L
                                )
                        ),
                        asMap("kkk", asList(90L))
                )
        );
        return new KeyedCollectionImpl(list);
    }

    private static KeyedCollection newKeyedCollectionSimpleList() {
        final List<Object> list = asList(
                null,
                true,
                1L,
                2.0,
                "3"
        );
        return new KeyedCollectionImpl(list);
    }
}