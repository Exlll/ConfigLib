package de.exlll.configlib;

import de.exlll.configlib.KeyedEntry.MissingKeyedEntry;
import de.exlll.configlib.KeyedEntry.MissingKeyedEntry.Reason;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static de.exlll.configlib.Validator.*;

final class KeyedCollectionImpl implements KeyedCollection {
    private final Object delegate;

    KeyedCollectionImpl(Object delegate) {
        this.delegate = requireListOrMap(delegate, "delegate");
    }

    @Override
    public KeyedEntry get(Key key) {
        return applyOperation(key, DelegateOperation.GET);
    }

    @Override
    public KeyedEntry remove(Key key) {
        return applyOperation(key, DelegateOperation.REMOVE);
    }

    private KeyedEntry applyOperation(Key key, DelegateOperation operation) {
        requireNonNull(key, "key");
        requireNonNull(operation, "operation");

        Object current = delegate;

        for (int i = 0, n = key.numParts(); i < n; i++) {
            final DelegateAccessor accessor = DelegateAccessor.forObject(current);
            final Object partI = key.getPart(i);

            if ((accessor == null) || !accessor.supportsPart(partI)) {
                return MissingKeyedEntry.fromKey(key, i, Reason.WRONG_TYPE);
            }

            if (!accessor.containsPart(partI)) {
                return MissingKeyedEntry.fromKey(key, i, Reason.PART_MISSING);
            }

            if (i == n - 1) {
                final Object result = operation.apply(accessor, partI);
                return new KeyedEntry.ExistingKeyedEntry(key, result);
            }

            current = accessor.get(partI);
        }
        // we shouldn't get here since we always return when i == n-1
        throw new RuntimeException(delegate + " ::: " + key);
    }

    @Override
    public KeyedEntry put(Key key, Object value) {
        requireNonNull(key, "key");
        requireTargetTypeArg(value);

        Object current = delegate;

        for (int i = 0, n = key.numParts(); i < n; i++) {
            final DelegateAccessor accessor = DelegateAccessor.forObject(current);
            final Object partI = key.getPart(i);

            if (i == n - 1) {
                final Object result = accessor.put(partI, value);
                return new KeyedEntry.ExistingKeyedEntry(key, result);
            }

            current = accessor.get(partI);
        }
        return null;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        KeyedCollectionImpl that = (KeyedCollectionImpl) object;
        return Objects.equals(delegate, that.delegate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(delegate);
    }

    private enum DelegateOperation {
        GET {
            @Override
            Object apply(DelegateAccessor accessor, Object part) {
                return accessor.get(part);
            }
        },
        REMOVE {
            @Override
            Object apply(DelegateAccessor accessor, Object part) {
                return accessor.remove(part);
            }
        };

        abstract Object apply(DelegateAccessor accessor, Object part);
    }

    private interface DelegateAccessor {
        boolean supportsPart(Object part);

        boolean containsPart(Object object);

        Object get(Object part);

        Object remove(Object part);

        Object put(Object part, Object value);

        static DelegateAccessor forObject(Object object) {
            // TODO: when can these casts lead to exceptions
            if (object instanceof Map<?, ?> map) {
                @SuppressWarnings("unchecked")
                final var m = (Map<Object, Object>) map;
                return new MapAccessor(m);
            }
            if (object instanceof List<?> list) {
                @SuppressWarnings("unchecked")
                final var l = (List<Object>) list;
                return new ListAccessor(l);
            }
            return null;
        }
    }

    private static final class MapAccessor implements DelegateAccessor {
        private final Map<Object, Object> map;

        private MapAccessor(Map<Object, Object> map) {this.map = map;}

        @Override
        public boolean containsPart(Object object) {
            return map.containsKey(object);
        }

        @Override
        public boolean supportsPart(Object part) {
            return (part == null) || Reflect.isSimpleTargetType(part.getClass());
        }

        @Override
        public Object get(Object part) {
            return map.get(part);
        }

        @Override
        public Object remove(Object part) {
            return map.remove(part);
        }

        @Override
        public Object put(Object part, Object value) {
            return map.put(part, value);
        }
    }

    private static final class ListAccessor implements DelegateAccessor {
        private final List<Object> list;

        private ListAccessor(List<Object> list) {this.list = list;}

        @Override
        public boolean containsPart(Object object) {
            final Key.ListIndex index = (Key.ListIndex) object;
            return index.index() < list.size();
        }

        @Override
        public boolean supportsPart(Object part) {
            return part instanceof Key.ListIndex;
        }

        @Override
        public Object get(Object part) {
            final Key.ListIndex index = (Key.ListIndex) part;
            return list.get(index.index());
        }

        @Override
        public Object remove(Object part) {
            final Key.ListIndex index = (Key.ListIndex) part;
            return list.remove(index.index());
        }

        @Override
        public Object put(Object part, Object value) {
            final Key.ListIndex index = (Key.ListIndex) part;
            return list.set(index.index(), value);
        }
    }
}

