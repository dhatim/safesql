package org.dhatim.safesql;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public abstract class AbstractSafeSqlBuilder<S extends AbstractSafeSqlBuilder<S>> implements SafeSqlizable, SafeSqlAppendable {

    static class Position {

        private final int sqlPosition;
        private final int paramPosition;

        private Position(int sqlPosition, int paramPosition) {
            this.sqlPosition = sqlPosition;
            this.paramPosition = paramPosition;
        }

    }

    private static final String DEFAULT_SEPARATOR = ", ";
    private static final char[] HEX_CODE = "0123456789ABCDEF".toCharArray();

    protected final S myself;

    protected final StringBuilder sqlBuilder;
    protected final List<Object> parameters;

    public AbstractSafeSqlBuilder(Class<S> selfType, StringBuilder stringBuilder, List<Object> parameters) {
        this.myself = selfType.cast(this);
        this.sqlBuilder = stringBuilder;
        this.parameters = parameters;
    }

    public abstract S copy();

    @Override
    public S param(int num) {
        appendObject(num);
        return myself;
    }

    @Override
    public S param(long num) {
        appendObject(num);
        return myself;
    }

    @Override
    public S param(double num) {
        appendObject(num);
        return myself;
    }

    @Override
    public S param(boolean bool) {
        appendObject(bool);
        return myself;
    }

    @Override
    public S param(Object obj) {
        appendObject(obj);
        return myself;
    }

    @Override
    public S params(Object param1, Object param2) {
        appendObject(param1);
        append(DEFAULT_SEPARATOR);
        appendObject(param2);
        return myself;
    }

    @Override
    public S params(Object param1, Object param2, Object param3) {
        appendObject(param1);
        append(DEFAULT_SEPARATOR);
        appendObject(param2);
        append(DEFAULT_SEPARATOR);
        appendObject(param3);
        return myself;
    }

    @Override
    public S params(Object param1, Object param2, Object param3, Object... others) {
        params(param1, param2, param3);
        append(DEFAULT_SEPARATOR);
        paramsArray(others);
        return myself;
    }

    @Override
    public S params(Object... parameters) {
        switch (parameters.length) {
            case 0:
                break; // Do nothing
            case 1:
                param(parameters[0]);
                break;
            case 2:
                params(parameters[0], parameters[1]);
                break;
            case 3:
                params(parameters[0], parameters[1], parameters[2]);
                break;
            default:
                paramsArray(parameters);
        }
        return myself;
    }

    @Override
    public S params(Iterable<?> iterable) {
        paramsIterator(DEFAULT_SEPARATOR, iterable.iterator());
        return myself;
    }

    @Override
    public S params(Stream<?> stream) {
        paramsIterator(DEFAULT_SEPARATOR, stream.iterator());
        return myself;
    }

    private void paramsArray(Object... objects) {
        for (int i=0; i<objects.length; i++) {
            if (i > 0) {
                append(DEFAULT_SEPARATOR);
            }
            param(objects[i]);
        }
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

    @Override
    public S append(SafeSql sql) {
        sqlBuilder.append(sql.asSql());
        parameters.addAll(Arrays.asList(sql.getParameters()));
        return myself;
    }

    @Override
    public S append(SafeSqlizable sqlizable) {
        sqlizable.appendTo(this);
        return myself;
    }

    @Override
    public S append(String s) {
        sqlBuilder.append(s);
        return myself;
    }

    @Override
    public S append(char ch) {
        sqlBuilder.append(ch);
        return myself;
    }

    @Override
    public S append(int i) {
        sqlBuilder.append(i);
        return myself;
    }

    /**
     * write a string literal by escaping
     *
     * @param s Append this string as literal string in SQL code
     * @return a reference to this object.
     */
    @Override
    public S literal(String s) {
        sqlBuilder.append(SafeSqlUtils.escapeString(s));
        return myself;
    }

    @Override
    public S format(String sql, Object... args) {
        SafeSqlUtils.formatTo(this, sql, args);
        return myself;
    }

    @Override
    public SafeSqlAppendable join(String delimiter, Iterable<String> iterable) {
        SafeSqlJoiner joiner = new SafeSqlJoiner(SafeSqlUtils.fromConstant(delimiter));
        iterable.forEach(joiner::add);
        joiner.appendTo(this);
        return myself;
    }

    @Override
    public SafeSqlAppendable join(String delimiter, String prefix, String suffix, Iterable<String> iterable) {
        SafeSqlJoiner joiner = new SafeSqlJoiner(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.fromConstant(prefix), SafeSqlUtils.fromConstant(suffix));
        iterable.forEach(joiner::add);
        joiner.appendTo(this);
        return myself;
    }

    @Override
    public SafeSqlAppendable join(String delimiter, Stream<String> stream) {
        SafeSqlJoiner joiner = stream.collect(() -> new SafeSqlJoiner(SafeSqlUtils.fromConstant(delimiter)),
                SafeSqlJoiner::add, SafeSqlJoiner::merge);
        joiner.appendTo(this);
        return myself;
    }

    @Override
    public SafeSqlAppendable join(String delimiter, String prefix, String suffix, Stream<String> stream) {
        SafeSqlJoiner joiner = stream.collect(() -> new SafeSqlJoiner(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.fromConstant(prefix), SafeSqlUtils.fromConstant(suffix)),
                SafeSqlJoiner::add, SafeSqlJoiner::merge);
        joiner.appendTo(this);
        return myself;
    }

    @Override
    public S joinSafeSqls(SafeSql delimiter, Iterable<SafeSql> iterable) {
        return joinSafeSqls(delimiter, SafeSqlUtils.EMPTY, SafeSqlUtils.EMPTY, iterable);
    }

    @Override
    public S joinSafeSqls(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Iterable<SafeSql> iterable) {
        SafeSqlJoiner joiner = new SafeSqlJoiner(delimiter, prefix, suffix);
        iterable.forEach(joiner::add);
        joiner.appendTo(this);
        return myself;
    }

    @Override
    public S joinSafeSqls(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Stream<SafeSql> stream) {
        SafeSqlJoiner joiner = stream.collect(() -> new SafeSqlJoiner(delimiter, prefix, suffix), SafeSqlJoiner::add, SafeSqlJoiner::merge);
        joiner.appendTo(this);
        return myself;
    }

    @Override
    public S joinSafeSqls(SafeSql delimiter, Stream<SafeSql> stream) {
        return joinSafeSqls(delimiter, SafeSqlUtils.EMPTY, SafeSqlUtils.EMPTY, stream);
    }

    @Override
    public S joinSafeSqls(String delimiter, Iterable<SafeSql> iterable) {
        return joinSafeSqls(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.EMPTY, SafeSqlUtils.EMPTY, iterable);
    }

    @Override
    public S joinSafeSqls(String delimiter, Stream<SafeSql> stream) {
        return joinSafeSqls(delimiter, "", "", stream);
    }

    @Override
    public S joinSafeSqls(String delimiter, String prefix, String suffix, Iterable<SafeSql> iterable) {
        return joinSafeSqls(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.fromConstant(prefix), SafeSqlUtils.fromConstant(suffix), iterable);
    }

    @Override
    public S joinSafeSqls(String delimiter, String prefix, String suffix, Stream<SafeSql> stream) {
        return joinSafeSqls(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.fromConstant(prefix), SafeSqlUtils.fromConstant(suffix), stream);
    }

    @Override
    public S joinSqlizables(SafeSql delimiter, Iterable<? extends SafeSqlizable> iterable) {
        return joinSqlizables(delimiter, SafeSqlUtils.EMPTY, SafeSqlUtils.EMPTY, iterable);
    }

    @Override
    public S joinSqlizables(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Iterable<? extends SafeSqlizable> iterable) {
        SafeSqlJoiner joiner = new SafeSqlJoiner(delimiter, prefix, suffix);
        iterable.forEach(joiner::add);
        joiner.appendTo(this);
        return myself;
    }

    @Override
    public S joinSqlizables(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Stream<? extends SafeSqlizable> stream) {
        SafeSqlJoiner joiner = stream.collect(() -> new SafeSqlJoiner(delimiter, prefix, suffix), SafeSqlJoiner::add, SafeSqlJoiner::merge);
        joiner.appendTo(this);
        return myself;
    }

    @Override
    public S joinSqlizables(SafeSql delimiter, Stream<? extends SafeSqlizable> stream) {
        return joinSqlizables(delimiter, SafeSqlUtils.EMPTY, SafeSqlUtils.EMPTY, stream);
    }

    @Override
    public S joinSqlizables(String delimiter, Iterable<? extends SafeSqlizable> iterable) {
        return joinSqlizables(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.EMPTY, SafeSqlUtils.EMPTY, iterable);
    }

    @Override
    public S joinSqlizables(String delimiter, Stream<? extends SafeSqlizable> stream) {
        return joinSqlizables(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.EMPTY, SafeSqlUtils.EMPTY, stream);
    }

    @Override
    public S joinSqlizables(String delimiter, String prefix, String suffix, Iterable<? extends SafeSqlizable> iterable) {
        return joinSqlizables(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.fromConstant(prefix), SafeSqlUtils.fromConstant(suffix), iterable);
    }

    @Override
    public S joinSqlizables(String delimiter, String prefix, String suffix, Stream<? extends SafeSqlizable> stream) {
        return joinSqlizables(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.fromConstant(prefix), SafeSqlUtils.fromConstant(suffix), stream);
    }

    /**
     * Write a byte array as literal in PostgreSQL
     *
     * @param bytes bytes to write as literal
     * @return a reference to this object.
     */
    @Override
    public S literal(byte[] bytes) {
        sqlBuilder.append("'\\x");
        for (byte b : bytes) {
            sqlBuilder.append(HEX_CODE[(b >> 4) & 0xF]);
            sqlBuilder.append(HEX_CODE[(b & 0xF)]);
        }
        sqlBuilder.append('\'');
        return myself;
    }

    @Override
    public S identifier(String identifier) {
        sqlBuilder.append(SafeSqlUtils.mayEscapeIdentifier(identifier));
        return myself;
    }

    @Override
    public S identifier(String container, String identifier) {
        sqlBuilder.append(SafeSqlUtils.mayEscapeIdentifier(container)).append('.').append(SafeSqlUtils.mayEscapeIdentifier(identifier));
        return myself;
    }

    protected final String mayEscapeIdentifier(String identifier) {
        return SafeSqlUtils.mayEscapeIdentifier(identifier);
    }

    /**
     * @deprecated Use {@link #literal(String)} instead.
     */
    @Deprecated
    public S appendStringLiteral(String s) {
        return literal(s);
    }

    /**
     * @deprecated Use {@link #format(String, Object...)} instead.
     */
    @Deprecated
    public S appendFormat(String sql, Object... args) {
        return format(sql, args);
    }

    /**
     * @deprecated Use {@link #joinSafeSqls(String, Iterable)} instead
     */
    @Deprecated
    public S appendJoined(String delimiter, Collection<? extends SafeSqlizable> collection) {
        return joinSqlizables(delimiter, collection);
    }

    /**
     * @deprecated Use {@link #joinSafeSqls(String, String, String, Iterable)} instead
     */
    @Deprecated
    public S appendJoined(String delimiter, String prefix, String suffix, Collection<? extends SafeSqlizable> collection) {
        return joinSqlizables(delimiter, prefix, suffix, collection);
    }

    /**
     * @deprecated Use {@link #joinSafeSqls(String, Stream)} instead
     */
    @Deprecated
    public S appendJoined(String delimiter, Stream<? extends SafeSqlizable> stream) {
        return joinSqlizables(delimiter, stream);
    }

    /**
     * @deprecated Use {@link #joinSafeSqls(String, String, String, Stream)} instead
     */
    @Deprecated
    public S appendJoined(String delimiter, String prefix, String suffix, Stream<? extends SafeSqlizable> stream) {
        return joinSqlizables(delimiter, prefix, suffix, stream);
    }

    /**
     * @deprecated Use {@link #joinSafeSqls(SafeSql, Iterable)} instead
     */
    @Deprecated
    public S appendJoined(SafeSql delimiter, Collection<? extends SafeSqlizable> collection) {
        return joinSqlizables(delimiter, collection);
    }

    /**
     * @deprecated Use {@link #joinSafeSqls(SafeSql, SafeSql, SafeSql, Iterable)} instead
     */
    @Deprecated
    public S appendJoined(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Collection<? extends SafeSqlizable> collection) {
        return joinSqlizables(delimiter, prefix, suffix, collection);
    }

    /**
     * @deprecated Use {@link #joinSafeSqls(SafeSql, Stream)} instead
     */
    @Deprecated
    public S appendJoined(SafeSql delimiter, Stream<? extends SafeSqlizable> stream) {
        return joinSqlizables(delimiter, stream);
    }

    /**
     * @deprecated Use {@link #joinSafeSqls(SafeSql, SafeSql, SafeSql, Stream)} instead
     */
    @Deprecated
    public S appendJoined(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Stream<? extends SafeSqlizable> stream) {
        return joinSqlizables(delimiter, prefix, suffix, stream);
    }

    /**
     * @deprecated Use {@link #literal(byte[])} instead.
     */
    @Deprecated
    public S appendByteLiteral(byte[] bytes) {
        return literal(bytes);
    }

    /**
     * @deprecated Use {@link #identifier(String)} instead.
     */
    @Deprecated
    public S appendIdentifier(String identifier) {
        return identifier(identifier);
    }

    /**
     * @deprecated Use {@link #identifier(String, String)} instead.
     */
    @Deprecated
    public S appendIdentifier(String container, String identifier) {
        return identifier(container, identifier);
    }

    @Deprecated
    public S params(String delimiter, Collection<?> collection) {
        paramsIterator(delimiter, collection.iterator());
        return myself;
    }

    @Deprecated
    public S params(String delimiter, String prefix, String suffix, Collection<?> collection) {
        append(prefix);
        paramsIterator(delimiter, collection.iterator());
        append(suffix);
        return myself;
    }

    @Deprecated
    public S params(String delimiter, Stream<?> stream) {
        paramsIterator(delimiter, stream.iterator());
        return myself;
    }

    @Deprecated
    public S params(String delimiter, String prefix, String suffix, Stream<?> stream) {
        append(prefix);
        paramsIterator(delimiter, stream.iterator());
        append(suffix);
        return myself;
    }

    @Deprecated
    public S params(SafeSql delimiter, Collection<?> collection) {
        paramsIterator(delimiter, collection.iterator());
        return myself;
    }

    @Deprecated
    public S params(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Collection<?> collection) {
        append(prefix);
        paramsIterator(delimiter, collection.iterator());
        append(suffix);
        return myself;
    }

    @Deprecated
    public S params(SafeSql delimiter, Stream<?> stream) {
        paramsIterator(delimiter, stream.iterator());
        return myself;
    }

    @Deprecated
    public S params(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Stream<?> stream) {
        append(prefix);
        paramsIterator(delimiter, stream.iterator());
        append(suffix);
        return myself;
    }

    @Override
    public SafeSql toSafeSql() {
        return new SafeSqlImpl(sqlBuilder.toString(), parameters.toArray());
    }

    @Override
    public void appendTo(SafeSqlAppendable builder) {
        if (builder instanceof AbstractSafeSqlBuilder<?>) {
            ((AbstractSafeSqlBuilder<?>) builder).parameters.addAll(parameters);
            ((AbstractSafeSqlBuilder<?>) builder).sqlBuilder.append(sqlBuilder);
        } else {
            builder.append(toSafeSql());
        }
    }

    private void appendObject(Object o) {
        sqlBuilder.append('?');
        parameters.add(o);
    }

    Position getLength() {
        return new Position(sqlBuilder.length(), parameters.size());
    }

    void setLength(Position position) {
        sqlBuilder.setLength(position.sqlPosition);
        int currentSize = parameters.size();
        if (position.paramPosition < currentSize) {
            parameters.subList(position.paramPosition, currentSize).clear();
        }
    }

    void append(SafeSqlBuilder other, Position after) {
        sqlBuilder.append(other.sqlBuilder, after.sqlPosition, other.sqlBuilder.length());
        int afterLength = after.paramPosition;
        parameters.addAll(other.parameters.subList(afterLength, other.parameters.size() - afterLength));
    }

    static Position getLength(SafeSql sql) {
        return new Position(sql.asSql().length(), sql.getParameters().length);
    }

}
