package org.safesql.connection;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

class TupleMapAdapter implements Map<String, Object> {

    private class Columns extends AbstractSet<String> {

        @Override
        public Iterator<String> iterator() {
            return new ColumnIt();
        }

        @Override
        public int size() {
            return delegate.size();
        }

    }

    private abstract class AbstractIt<T> implements Iterator<T> {

        private int index = -1;

        protected abstract T get(int index);

        @Override
        public boolean hasNext() {
            return index + 1 < delegate.size();
        }

        @Override
        public T next() {
            index++;
            if (index >= delegate.size()) {
                throw new NoSuchElementException();
            }
            return get(index);
        }

    }

    private class ColumnIt extends AbstractIt<String> {

        @Override
        protected String get(int index) {
            return delegate.getColumnLabel(index);
        }

    }

    private class Values extends AbstractCollection<Object> {

        @Override
        public Iterator<Object> iterator() {
            return new ValueIt();
        }

        @Override
        public int size() {
            return delegate.size();
        }
    }

    private class ValueIt extends AbstractIt<Object> {

        @Override
        protected Object get(int index) {
            return delegate.get(index);
        }

    }

    private class Entries extends AbstractSet<Entry<String, Object>> {

        @Override
        public Iterator<Entry<String, Object>> iterator() {
            return new EntryIt();
        }

        @Override
        public int size() {
            return delegate.size();
        }

    }

    private class EntryIt extends AbstractIt<Entry<String, Object>> {

        @Override
        protected Entry<String, Object> get(int index) {
            String columnLabel = delegate.getColumnLabel(index);
            Object value = delegate.get(index);
            return new AbstractMap.SimpleImmutableEntry(columnLabel, value);
        }

    }

    private final Tuple delegate;

    public TupleMapAdapter(Tuple tuple) {
        this.delegate = tuple;
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.contains(requireString(key));
    }

    @Override
    public boolean containsValue(Object value) {
        return new Values().contains(value);
    }

    @Override
    public Object get(Object key) {
        return delegate.getObject(requireString(key));
    }

    @Override
    public Object put(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> keySet() {
        return new Columns();
    }

    @Override
    public Collection<Object> values() {
        return new Values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return new Entries();
    }

    public static Map<String, Object> create(Tuple tuple) {
        return new TupleMapAdapter(tuple);
    }

    private static String requireString(Object key) {
        if (key instanceof String) {
            return (String) key;
        } else {
            throw new ClassCastException("Key is not a String");
        }
    }

}
