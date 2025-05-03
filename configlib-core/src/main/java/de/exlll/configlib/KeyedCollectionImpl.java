package de.exlll.configlib;

import de.exlll.configlib.KeyedEntry.MissingKeyedEntry;
import de.exlll.configlib.KeyedEntry.MissingKeyedEntry.Reason;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static de.exlll.configlib.Validator.requireListOrMap;

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
        Object current = delegate;

        for (int i = 0, n = key.numParts(); i < n; i++) {
            final Object partI = key.getPart(i);
            final DelegateAccessor accessor = DelegateAccessor.forObject(current);

            if ((accessor == null) || !accessor.supportsPart(partI)) {
                return MissingKeyedEntry.fromKey(key, i, Reason.PART_WRONG_TYPE);
            }

            if (!accessor.containsPart(partI)) {
                return MissingKeyedEntry.fromKey(key, i, Reason.PART_MISSING);
            }

            if (i == n - 1) {
                final Object result = switch (operation) {
                    case GET -> accessor.getPart(partI);
                    case REMOVE -> accessor.removePart(partI);
                };
                return new KeyedEntry.ExistingKeyedEntry(key, result);
            }

            current = accessor.getPart(partI);
        }
        // we shouldn't get here since we always return when i == n-1
        throw new RuntimeException(delegate + " ::: " + key);
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

    private enum DelegateOperation {GET, REMOVE}

    private interface DelegateAccessor {
        boolean supportsPart(Object part);

        boolean containsPart(Object object);

        Object getPart(Object part);

        Object removePart(Object part);

        static DelegateAccessor forObject(Object object) {
            if (object instanceof Map<?, ?> map)
                return new MapAccessor(map);
            if (object instanceof List<?> list)
                return new ListAccessor(list);
            return null;
        }
    }

    private static final class MapAccessor implements DelegateAccessor {
        private final Map<?, ?> map;

        private MapAccessor(Map<?, ?> map) {this.map = map;}

        @Override
        public boolean containsPart(Object object) {
            return map.containsKey(object);
        }

        @Override
        public boolean supportsPart(Object part) {
            return (part == null) || Reflect.isSimpleTargetType(part.getClass());
        }

        @Override
        public Object getPart(Object part) {
            return map.get(part);
        }

        @Override
        public Object removePart(Object part) {
            return map.remove(part);
        }
    }

    private static final class ListAccessor implements DelegateAccessor {
        private final List<?> list;

        private ListAccessor(List<?> list) {this.list = list;}

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
        public Object getPart(Object part) {
            final Key.ListIndex index = (Key.ListIndex) part;
            return list.get(index.index());
        }

        @Override
        public Object removePart(Object part) {
            final Key.ListIndex index = (Key.ListIndex) part;
            return list.remove(index.index());
        }
    }
}

