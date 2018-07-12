package org.dhatim.safesql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class SafeSqlBuilder implements SafeSqlizable {

    static class Position {

        private final int sqlPosition;
        private final int paramPosition;

        private Position(int sqlPosition, int paramPosition) {
            this.sqlPosition = sqlPosition;
            this.paramPosition = paramPosition;
        }

    }

    private static final String DEFAULT_SEPARATOR = ", ";

    protected final StringBuilder sql;
    protected final List<Object> parameters;

    public SafeSqlBuilder() {
        this(new StringBuilder(), new ArrayList<>());
    }

    public SafeSqlBuilder(String query) {
        this(new StringBuilder(query), new ArrayList<>());
    }

    public SafeSqlBuilder(SafeSqlBuilder other) {
        this(new StringBuilder(other.sql), new ArrayList<>(other.parameters));
    }

    protected SafeSqlBuilder(StringBuilder stringBuilder, List<Object> parameters) {
        // Without copy buffers
        this.sql = stringBuilder;
        this.parameters = parameters;
    }

    /**
     * include integer parameter in SQL with a placeholder <b>?</b>
     *
     * @param num integer parameter
     * @return a reference of this object
     */
    public SafeSqlBuilder param(int num) {
        appendObject(num);
        return this;
    }

    /**
     * include long parameter in SQL with a placeholder <b>?</b>
     *
     * @param num long parameter
     * @return a reference of this object
     */
    public SafeSqlBuilder param(long num) {
        appendObject(num);
        return this;
    }

    /**
     * include double parameter in SQL with a placeholder <b>?</b>
     *
     * @param num double parameter
     * @return a reference of this object
     */
    public SafeSqlBuilder param(double num) {
        appendObject(num);
        return this;
    }

    /**
     * include boolean parameter in SQL with a placeholder <b>?</b>
     *
     * @param bool boolean parameter
     * @return a reference of this object
     */
    public SafeSqlBuilder param(boolean bool) {
        appendObject(bool);
        return this;
    }

    /**
     * include generic parameter in SQL with a placeholder <b>?</b>
     *
     * @param obj object parameter
     * @return a reference of this object
     */
    public SafeSqlBuilder param(Object obj) {
        appendObject(obj);
        return this;
    }

    /**
     * include multiple parameters in SQL with placeholders <b>?</b>
     *
     * @param parameters list of parameter to include
     * @return a reference of this object
     */
    public SafeSqlBuilder params(Object... parameters) {
        if (parameters.length == 1) {
            param(parameters[0]);
        } else if (parameters.length != 0) {
            for (int i=0; i<parameters.length; i++) {
                if (i > 0) {
                    append(DEFAULT_SEPARATOR);
                }
                param(parameters[i]);
            }
        }
        return this;
    }

    /**
     * include multiple parameters in SQL with placeholders <b>?</b>
     *
     * @param iterable {@link Iterable} of parameter to include
     * @return a reference of this object
     */
    public SafeSqlBuilder params(Iterable<?> iterable) {
        paramsIterator(DEFAULT_SEPARATOR, iterable.iterator());
        return this;
    }

    /**
     * include multiple parameters in SQL with placeholders <b>?</b>
     *
     * @param stream stream of parameter to include
     * @return a reference of this object
     */
    public SafeSqlBuilder params(Stream<?> stream) {
        paramsIterator(DEFAULT_SEPARATOR, stream.iterator());
        return this;
    }

    private void paramsIterator(String delimiter, Iterator<?> iterator) {
        boolean first = true;
        while (iterator.hasNext()) {
            if (first) {
                first = false;
            } else {
                append(delimiter);
            }
            param(iterator.next());
        }
    }

    private void paramsIterator(SafeSql delimiter, Iterator<?> iterator) {
        boolean first = true;
        while (iterator.hasNext()) {
            if (first) {
                first = false;
            } else {
                append(delimiter);
            }
            param(iterator.next());
        }
    }

    @SafeVarargs
    public final <T> SafeSqlBuilder array(String type, T... elements) {
        appendObject(ArraySupport.toString(elements));
        sql.append("::").append(type).append("[]");
        return this;
    }

    public <T> SafeSqlBuilder array(String type, Iterable<T> elements) {
        appendObject(ArraySupport.toString(elements));
        sql.append("::").append(type).append("[]");
        return this;
    }

    /**
     * append a {@link SafeSql} to SQL
     *
     * @param s {@link SafeSql} to append to the final SQL
     * @return a reference of this object
     */
    public SafeSqlBuilder append(SafeSql s) {
        sql.append(s.asSql());
        Collections.addAll(parameters, s.getParameters());
        return this;
    }

    public SafeSqlBuilder append(SafeSqlizable sqlizable) {
        sqlizable.appendTo(this);
        return this;
    }

    public SafeSqlBuilder append(String s) {
        sql.append(s);
        return this;
    }

    public SafeSqlBuilder append(char ch) {
        sql.append(ch);
        return this;
    }

    public SafeSqlBuilder append(int i) {
        sql.append(i);
        return this;
    }

    public SafeSqlBuilder append(long l) {
        sql.append(l);
        return this;
    }

    /**
     * write a string literal by escaping
     *
     * @param s this string as literal string in SQL code
     * @return a reference to this object.
     */
    public SafeSqlBuilder literal(String s) {
        sql.append(SafeSqlUtils.escapeString(s));
        return this;
    }

    /**
     * Appends a formatted sql string using the specified arguments.
     *
     * @param query string query with some <code>{}</code> argument place. The
     * argument can have a number inside to force a argument index (start at 1).
     * The escape sequence is <code>{{.*}}</code>.
     * @param args arguments list
     * @return a reference to this object.
     */
    public SafeSqlBuilder format(String query, Object... args) {
        SafeSqlUtils.formatTo(this, query, args);
        return this;
    }

    public <E> SafeSqlBuilder joined(Iterable<E> iterable, Consumer<SafeSqlBuilder> delimiter, BiConsumer<SafeSqlBuilder, E> element) {
        boolean first = true;
        for (E e : iterable) {
            if (first) {
                first = false;
            } else {
                delimiter.accept(this);
            }
            element.accept(this, e);
        }
        return this;
    }

    public SafeSqlBuilder joined(String delimiter, Iterable<String> iterable) {
        SafeSqlJoiner joiner = new SafeSqlJoiner(SafeSqlUtils.fromConstant(delimiter));
        iterable.forEach(joiner::add);
        joiner.appendTo(this);
        return this;
    }

    public SafeSqlBuilder joined(String delimiter, String prefix, String suffix, Iterable<String> iterable) {
        SafeSqlJoiner joiner = new SafeSqlJoiner(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.fromConstant(prefix), SafeSqlUtils.fromConstant(suffix));
        iterable.forEach(joiner::add);
        joiner.appendTo(this);
        return this;
    }

    public SafeSqlBuilder joined(String delimiter, Stream<String> stream) {
        SafeSqlJoiner joiner = stream.collect(() -> new SafeSqlJoiner(SafeSqlUtils.fromConstant(delimiter)),
                SafeSqlJoiner::add, SafeSqlJoiner::merge);
        joiner.appendTo(this);
        return this;
    }

    public SafeSqlBuilder joined(String delimiter, String prefix, String suffix, Stream<String> stream) {
        SafeSqlJoiner joiner = stream.collect(() -> new SafeSqlJoiner(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.fromConstant(prefix), SafeSqlUtils.fromConstant(suffix)),
                SafeSqlJoiner::add, SafeSqlJoiner::merge);
        joiner.appendTo(this);
        return this;
    }

    public SafeSqlBuilder joinedSafeSqls(SafeSql delimiter, Iterable<SafeSql> iterable) {
        return joinedSafeSqls(delimiter, SafeSqlUtils.EMPTY, SafeSqlUtils.EMPTY, iterable);
    }

    public SafeSqlBuilder joinedSafeSqls(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Iterable<SafeSql> iterable) {
        SafeSqlJoiner joiner = new SafeSqlJoiner(delimiter, prefix, suffix);
        iterable.forEach(joiner::add);
        joiner.appendTo(this);
        return this;
    }

    public SafeSqlBuilder joinedSafeSqls(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Stream<SafeSql> stream) {
        SafeSqlJoiner joiner = stream.collect(() -> new SafeSqlJoiner(delimiter, prefix, suffix), SafeSqlJoiner::add, SafeSqlJoiner::merge);
        joiner.appendTo(this);
        return this;
    }

    public SafeSqlBuilder joinedSafeSqls(SafeSql delimiter, Stream<SafeSql> stream) {
        return joinedSafeSqls(delimiter, SafeSqlUtils.EMPTY, SafeSqlUtils.EMPTY, stream);
    }

    public SafeSqlBuilder joinedSafeSqls(String delimiter, Iterable<SafeSql> iterable) {
        return joinedSafeSqls(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.EMPTY, SafeSqlUtils.EMPTY, iterable);
    }

    public SafeSqlBuilder joinedSafeSqls(String delimiter, Stream<SafeSql> stream) {
        return joinedSafeSqls(delimiter, "", "", stream);
    }

    public SafeSqlBuilder joinedSafeSqls(String delimiter, String prefix, String suffix, Iterable<SafeSql> iterable) {
        return joinedSafeSqls(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.fromConstant(prefix), SafeSqlUtils.fromConstant(suffix), iterable);
    }

    public SafeSqlBuilder joinedSafeSqls(String delimiter, String prefix, String suffix, Stream<SafeSql> stream) {
        return joinedSafeSqls(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.fromConstant(prefix), SafeSqlUtils.fromConstant(suffix), stream);
    }

    public SafeSqlBuilder joinedSqlizables(SafeSql delimiter, Iterable<? extends SafeSqlizable> iterable) {
        return joinedSqlizables(delimiter, SafeSqlUtils.EMPTY, SafeSqlUtils.EMPTY, iterable);
    }

    public SafeSqlBuilder joinedSqlizables(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Iterable<? extends SafeSqlizable> iterable) {
        SafeSqlJoiner joiner = new SafeSqlJoiner(delimiter, prefix, suffix);
        iterable.forEach(joiner::add);
        joiner.appendTo(this);
        return this;
    }

    public SafeSqlBuilder joinedSqlizables(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Stream<? extends SafeSqlizable> stream) {
        SafeSqlJoiner joiner = stream.collect(() -> new SafeSqlJoiner(delimiter, prefix, suffix), SafeSqlJoiner::add, SafeSqlJoiner::merge);
        joiner.appendTo(this);
        return this;
    }

    public SafeSqlBuilder joinedSqlizables(SafeSql delimiter, Stream<? extends SafeSqlizable> stream) {
        return joinedSqlizables(delimiter, SafeSqlUtils.EMPTY, SafeSqlUtils.EMPTY, stream);
    }

    public SafeSqlBuilder joinedSqlizables(String delimiter, Iterable<? extends SafeSqlizable> iterable) {
        return joinedSqlizables(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.EMPTY, SafeSqlUtils.EMPTY, iterable);
    }

    public SafeSqlBuilder joinedSqlizables(String delimiter, Stream<? extends SafeSqlizable> stream) {
        return joinedSqlizables(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.EMPTY, SafeSqlUtils.EMPTY, stream);
    }

    public SafeSqlBuilder joinedSqlizables(String delimiter, String prefix, String suffix, Iterable<? extends SafeSqlizable> iterable) {
        return joinedSqlizables(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.fromConstant(prefix), SafeSqlUtils.fromConstant(suffix), iterable);
    }

    public SafeSqlBuilder joinedSqlizables(String delimiter, String prefix, String suffix, Stream<? extends SafeSqlizable> stream) {
        return joinedSqlizables(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.fromConstant(prefix), SafeSqlUtils.fromConstant(suffix), stream);
    }

    /**
     * Write a byte array as literal in PostgreSQL
     *
     * @param bytes bytes to write as literal
     * @return a reference to this object.
     */
    public SafeSqlBuilder literal(byte[] bytes) {
        sql.append("'\\x");
        ArraySupport.appendHexBytes(sql, bytes);
        sql.append('\'');
        return this;
    }

    public SafeSqlBuilder identifier(String identifier) {
        sql.append(SafeSqlUtils.mayEscapeIdentifier(identifier));
        return this;
    }

    public SafeSqlBuilder identifier(String container, String identifier) {
        if (null == container) {
            return identifier(identifier);
        } else {
            sql.append(SafeSqlUtils.mayEscapeIdentifier(container)).append('.').append(SafeSqlUtils.mayEscapeIdentifier(identifier));
            return this;
        }
    }

    protected final String mayEscapeIdentifier(String identifier) {
        return SafeSqlUtils.mayEscapeIdentifier(identifier);
    }

    /**
     * @deprecated Use {@link #literal(String)} instead.
     */
    @Deprecated
    public SafeSqlBuilder appendStringLiteral(String s) {
        return literal(s);
    }

    /**
     * @deprecated Use {@link #format(String, Object...)} instead.
     */
    @Deprecated
    public SafeSqlBuilder appendFormat(String sql, Object... args) {
        return format(sql, args);
    }

    /**
     * @deprecated Use {@link #joinedSafeSqls(String, Iterable)} instead
     */
    @Deprecated
    public SafeSqlBuilder appendJoined(String delimiter, Collection<? extends SafeSqlizable> collection) {
        return joinedSqlizables(delimiter, collection);
    }

    /**
     * @deprecated Use {@link #joinedSafeSqls(String, String, String, Iterable)} instead
     */
    @Deprecated
    public SafeSqlBuilder appendJoined(String delimiter, String prefix, String suffix, Collection<? extends SafeSqlizable> collection) {
        return joinedSqlizables(delimiter, prefix, suffix, collection);
    }

    /**
     * @deprecated Use {@link #joinedSafeSqls(String, Stream)} instead
     */
    @Deprecated
    public SafeSqlBuilder appendJoined(String delimiter, Stream<? extends SafeSqlizable> stream) {
        return joinedSqlizables(delimiter, stream);
    }

    /**
     * @deprecated Use {@link #joinedSafeSqls(String, String, String, Stream)} instead
     */
    @Deprecated
    public SafeSqlBuilder appendJoined(String delimiter, String prefix, String suffix, Stream<? extends SafeSqlizable> stream) {
        return joinedSqlizables(delimiter, prefix, suffix, stream);
    }

    /**
     * @deprecated Use {@link #joinedSafeSqls(SafeSql, Iterable)} instead
     */
    @Deprecated
    public SafeSqlBuilder appendJoined(SafeSql delimiter, Collection<? extends SafeSqlizable> collection) {
        return joinedSqlizables(delimiter, collection);
    }

    /**
     * @deprecated Use {@link #joinedSafeSqls(SafeSql, SafeSql, SafeSql, Iterable)} instead
     */
    @Deprecated
    public SafeSqlBuilder appendJoined(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Collection<? extends SafeSqlizable> collection) {
        return joinedSqlizables(delimiter, prefix, suffix, collection);
    }

    /**
     * @deprecated Use {@link #joinedSafeSqls(SafeSql, Stream)} instead
     */
    @Deprecated
    public SafeSqlBuilder appendJoined(SafeSql delimiter, Stream<? extends SafeSqlizable> stream) {
        return joinedSqlizables(delimiter, stream);
    }

    /**
     * @deprecated Use {@link #joinedSafeSqls(SafeSql, SafeSql, SafeSql, Stream)} instead
     */
    @Deprecated
    public SafeSqlBuilder appendJoined(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Stream<? extends SafeSqlizable> stream) {
        return joinedSqlizables(delimiter, prefix, suffix, stream);
    }

    /**
     * @deprecated Use {@link #literal(byte[])} instead.
     */
    @Deprecated
    public SafeSqlBuilder appendByteLiteral(byte[] bytes) {
        return literal(bytes);
    }

    /**
     * @deprecated Use {@link #identifier(String)} instead.
     */
    @Deprecated
    public SafeSqlBuilder appendIdentifier(String identifier) {
        return identifier(identifier);
    }

    /**
     * @deprecated Use {@link #identifier(String, String)} instead.
     */
    @Deprecated
    public SafeSqlBuilder appendIdentifier(String container, String identifier) {
        return identifier(container, identifier);
    }

    @Deprecated
    public SafeSqlBuilder params(String delimiter, Collection<?> collection) {
        paramsIterator(delimiter, collection.iterator());
        return this;
    }

    @Deprecated
    public SafeSqlBuilder params(String delimiter, String prefix, String suffix, Collection<?> collection) {
        append(prefix);
        paramsIterator(delimiter, collection.iterator());
        append(suffix);
        return this;
    }

    @Deprecated
    public SafeSqlBuilder params(String delimiter, Stream<?> stream) {
        paramsIterator(delimiter, stream.iterator());
        return this;
    }

    @Deprecated
    public SafeSqlBuilder params(String delimiter, String prefix, String suffix, Stream<?> stream) {
        append(prefix);
        paramsIterator(delimiter, stream.iterator());
        append(suffix);
        return this;
    }

    @Deprecated
    public SafeSqlBuilder params(SafeSql delimiter, Collection<?> collection) {
        paramsIterator(delimiter, collection.iterator());
        return this;
    }

    @Deprecated
    public SafeSqlBuilder params(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Collection<?> collection) {
        append(prefix);
        paramsIterator(delimiter, collection.iterator());
        append(suffix);
        return this;
    }

    @Deprecated
    public SafeSqlBuilder params(SafeSql delimiter, Stream<?> stream) {
        paramsIterator(delimiter, stream.iterator());
        return this;
    }

    @Deprecated
    public SafeSqlBuilder params(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Stream<?> stream) {
        append(prefix);
        paramsIterator(delimiter, stream.iterator());
        append(suffix);
        return this;
    }

    @Override
    public SafeSql toSafeSql() {
        return new SafeSqlImpl(asSql(), getParameters());
    }

    @Override
    public void appendTo(SafeSqlBuilder builder) {
        builder.sql.append(sql);
        builder.parameters.addAll(parameters);
    }

    /**
     * Returns <tt>true</tt> if this builder contains no sql and no parameters.
     *
     * @return <tt>true</tt> if this builder contains no sql and no parameters
     */
    public boolean isEmpty() {
        return sql.length() == 0 && parameters.isEmpty();
    }

    protected String asSql() {
        return sql.toString();
    }

    protected Object[] getParameters() {
        return parameters.toArray();
    }

    private void appendObject(Object o) {
        sql.append('?');
        parameters.add(o);
    }

    Position getLength() {
        return new Position(sql.length(), parameters.size());
    }

    void setLength(Position position) {
        sql.setLength(position.sqlPosition);
        int currentSize = parameters.size();
        if (position.paramPosition < currentSize) {
            parameters.subList(position.paramPosition, currentSize).clear();
        }
    }

    void append(SafeSqlBuilder other, Position after) {
        sql.append(other.sql, after.sqlPosition, other.sql.length());
        int afterLength = after.paramPosition;
        parameters.addAll(other.parameters.subList(afterLength, other.parameters.size() - afterLength));
    }

    static Position getLength(SafeSql sql) {
        return new Position(sql.asSql().length(), sql.getParameters().length);
    }

}
