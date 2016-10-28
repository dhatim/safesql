package org.dhatim.safesql;

import java.math.BigDecimal;
import java.util.Arrays;
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
    public S param(BigDecimal num) {
        appendObject(num);
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
        paramsIterator(iterable.iterator());
        return myself;
    }

    @Override
    public S params(Stream<?> stream) {
        paramsIterator(stream.iterator());
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
    
    private void paramsIterator(Iterator<?> iterator) {
        boolean first = true;
        while (iterator.hasNext()) {
            if (first) {
                first = false;
            } else {
                append(DEFAULT_SEPARATOR);
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
    public S appendStringLiteral(String s) {
        sqlBuilder.append(SafeSqlUtils.escapeString(s));
        return myself;
    }
    
    @Override
    public S appendFormatted(String sql, Object... args) {
        SafeSqlUtils.formatTo(this, sql, args);
        return myself;
    }
    
    @Override
    public S appendJoined(SafeSql delimiter, Iterable<SafeSql> iterable) {
        return appendJoined(delimiter, SafeSqlUtils.EMPTY, SafeSqlUtils.EMPTY, iterable);
    }
    
    @Override
    public S appendJoined(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Iterable<SafeSql> iterable) {
        SafeSqlJoiner joiner = new SafeSqlJoiner(delimiter, prefix, suffix);
        iterable.forEach(joiner::add);
        joiner.appendTo(this);
        return myself;
    }
    
    @Override
    public S appendJoined(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Stream<SafeSql> stream) {
        SafeSqlJoiner joiner = stream.collect(() -> new SafeSqlJoiner(delimiter, prefix, suffix), SafeSqlJoiner::add, SafeSqlJoiner::merge);
        joiner.appendTo(this);
        return myself;
    }
    
    @Override
    public S appendJoined(SafeSql delimiter, Stream<SafeSql> stream) {
        return appendJoined(delimiter, SafeSqlUtils.EMPTY, SafeSqlUtils.EMPTY, stream);
    }
    
    @Override
    public S appendJoined(String delimiter, Iterable<SafeSql> iterable) {
        return appendJoined(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.EMPTY, SafeSqlUtils.EMPTY, iterable);
    }
    
    @Override
    public S appendJoined(String delimiter, Stream<SafeSql> stream) {
        return appendJoined(delimiter, "", "", stream);
    }
    
    @Override
    public S appendJoined(String delimiter, String prefix, String suffix, Iterable<SafeSql> iterable) {
        return appendJoined(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.fromConstant(prefix), SafeSqlUtils.fromConstant(suffix), iterable);
    }
    
    @Override
    public S appendJoined(String delimiter, String prefix, String suffix, Stream<SafeSql> stream) {
        return appendJoined(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.fromConstant(prefix), SafeSqlUtils.fromConstant(suffix), stream);
    }
    
    @Override
    public S appendJoinedSqlizable(SafeSql delimiter, Iterable<? extends SafeSqlizable> iterable) {
        return appendJoinedSqlizable(delimiter, SafeSqlUtils.EMPTY, SafeSqlUtils.EMPTY, iterable);
    }
    
    @Override
    public S appendJoinedSqlizable(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Iterable<? extends SafeSqlizable> iterable) {
        SafeSqlJoiner joiner = new SafeSqlJoiner(delimiter, prefix, suffix);
        iterable.forEach(joiner::add);
        joiner.appendTo(this);
        return myself;
    }
    
    @Override
    public S appendJoinedSqlizable(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Stream<? extends SafeSqlizable> stream) {
        SafeSqlJoiner joiner = stream.collect(() -> new SafeSqlJoiner(delimiter, prefix, suffix), SafeSqlJoiner::add, SafeSqlJoiner::merge);
        joiner.appendTo(this);
        return myself;
    }
    
    @Override
    public S appendJoinedSqlizable(SafeSql delimiter, Stream<? extends SafeSqlizable> stream) {
        return appendJoinedSqlizable(delimiter, SafeSqlUtils.EMPTY, SafeSqlUtils.EMPTY, stream);
    }
    
    @Override
    public S appendJoinedSqlizable(String delimiter, Iterable<? extends SafeSqlizable> iterable) {
        return appendJoinedSqlizable(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.EMPTY, SafeSqlUtils.EMPTY, iterable);
    }
    
    @Override
    public S appendJoinedSqlizable(String delimiter, Stream<? extends SafeSqlizable> stream) {
        return appendJoinedSqlizable(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.EMPTY, SafeSqlUtils.EMPTY, stream);
    }
    
    @Override
    public S appendJoinedSqlizable(String delimiter, String prefix, String suffix, Iterable<? extends SafeSqlizable> iterable) {
        return appendJoinedSqlizable(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.fromConstant(prefix), SafeSqlUtils.fromConstant(suffix), iterable);
    }
    
    @Override
    public S appendJoinedSqlizable(String delimiter, String prefix, String suffix, Stream<? extends SafeSqlizable> stream) {
        return appendJoinedSqlizable(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.fromConstant(prefix), SafeSqlUtils.fromConstant(suffix), stream);
    }
    
    /**
     * Write a byte array as literal in PostgreSQL
     *
     * @param bytes bytes to write as literal
     * @return a reference to this object.
     */
    @Override
    public S appendBytesLiteral(byte[] bytes) {
        sqlBuilder.append("'\\x");
        for (byte b : bytes) {
            sqlBuilder.append(HEX_CODE[(b >> 4) & 0xF]);
            sqlBuilder.append(HEX_CODE[(b & 0xF)]);
        }
        sqlBuilder.append('\'');
        return myself;
    }

    @Override
    public S appendIdentifier(String identifier) {
        sqlBuilder.append(SafeSqlUtils.mayEscapeIdentifier(identifier));
        return myself;
    }

    @Override
    public S appendIdentifier(String container, String identifier) {
        sqlBuilder.append(SafeSqlUtils.mayEscapeIdentifier(container)).append('.').append(SafeSqlUtils.mayEscapeIdentifier(identifier));
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
        parameters.addAll(Arrays.asList(other.parameters).subList(afterLength, other.parameters.size() - afterLength));
    }

    static Position getLength(SafeSql sql) {
        return new Position(sql.asSql().length(), sql.getParameters().length);
    }

}
